package com.yusheng.hbgj.controller;

import com.alibaba.fastjson.JSONObject;
import com.yusheng.hbgj.annotation.LogAnnotation;
import com.yusheng.hbgj.annotation.PermissionTag;
import com.yusheng.hbgj.constants.NotLoginException;
import com.yusheng.hbgj.constants.UserConstants;
import com.yusheng.hbgj.dao.UserDao;
import com.yusheng.hbgj.dto.UserDto;
import com.yusheng.hbgj.entity.User;
import com.yusheng.hbgj.page.table.PageTableHandler;
import com.yusheng.hbgj.page.table.PageTableRequest;
import com.yusheng.hbgj.page.table.PageTableResponse;
import com.yusheng.hbgj.service.RedisService;
import com.yusheng.hbgj.service.SysLogService;
import com.yusheng.hbgj.service.UserService;
import com.yusheng.hbgj.utils.StrUtil;
import com.yusheng.hbgj.utils.UserUtil2;
import com.yusheng.hbgj.vo.WeiXinVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户相关接口
 *
 * @author Jinwei
 */
@Api(tags = "用户")

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger("adminLogger");

    @Autowired
    private UserService userService;

    @Autowired
    private UserDao userDao;


    @Autowired
    private SysLogService sysLogService;

    @Autowired
    private RedisService redisService;


    @Value("${constants.companyRoleId}")
    private Long companyRoleId;


    @LogAnnotation
    @PostMapping
    @ApiOperation(value = "Web保存用户", notes = "如果没有勾选任何角色，系统就默认设置为厂商角色")
    @PermissionTag("sys:user:add")
    public User saveUser(@RequestBody UserDto userDto) {

        User u = userService.getUser(userDto.getUsername());
        if (u != null) {
            throw new IllegalArgumentException(userDto.getUsername() + "已存在");
        }

        roleDeal(userDto, companyRoleId);

        if (userDto.getCompFlag() == null) {
            userDto.setCompFlag(1);
        }


        return userService.saveUser(userDto);
    }

    @LogAnnotation
    @PutMapping
    @ApiOperation(value = "修改用户")
    @PermissionTag("sys:user:add")
    public User updateUser(@RequestBody UserDto userDto, HttpSession session) {

        return userService.updateUser(userDto, session);

    }

    @LogAnnotation
    @PutMapping(params = "headImgUrl")
    @ApiOperation(value = "修改头像")
    public void updateHeadImgUrl(String headImgUrl, HttpSession session) {
        User user = UserUtil2.getCurrentUser();
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(user, userDto);
        userDto.setHeadImgUrl(headImgUrl);

        userService.updateUser(userDto, session);
        log.debug("{}修改了头像", user.getUsername());
    }


    @GetMapping("/getAllUser")
    @ApiOperation(value = "获取所有用户/厂商")
    public List<User> getAllUser() {

        return userService.getAllUser();
    }


    @LogAnnotation
    @PutMapping("/{username}")
    @ApiOperation(value = "修改密码")
    @PermissionTag("sys:user:password")
    public void changePassword(@PathVariable String username, String oldPassword, String newPassword) {

        userService.changePassword(username, oldPassword, newPassword);

    }


    @PutMapping("/agreeLicence")
    @ApiOperation(value = "厂商同意法律许可")
    @PermissionTag("sys:user:licence")
    public Boolean agreeLicence(@RequestParam String userId) {

        Boolean isSucess = userService.agreeLicence(userId);

        if (isSucess) {
            sysLogService.save(Long.parseLong(userId), "系统", true, "厂商同意了许可");
            return true;
        } else {

            return false;
        }


    }


    @GetMapping
    @ApiOperation(value = "用户列表")
    @PermissionTag("sys:user:query")
    public PageTableResponse listUsers(PageTableRequest request) {
        return new PageTableHandler(new PageTableHandler.CountHandler() {


            @Override
            public int count(PageTableRequest request) {

                // 默认值只显示厂商
                request.getParams().putIfAbsent("compFlag", "1");

                return userDao.count(request.getParams());
            }
        }, new PageTableHandler.ListHandler() {

            @Override
            public List<User> list(PageTableRequest request) {

                // 默认值只显示厂商
                request.getParams().putIfAbsent("compFlag", "1");

                return userDao.list(request.getParams(), request.getOffset(), request.getLimit());

            }
        }).handle(request);
    }

    @ApiOperation(value = "当前登录用户")
    @GetMapping("/current")
    public User currentUser() {

        User tar = UserUtil2.getCurrentUser();

        User user = new User();
        BeanUtils.copyProperties(tar, user);
        user.setOriginalPassword(null);
        user.setPassword(null);
        return user;
    }

    @ApiOperation(value = "根据用户id获取用户")
    @GetMapping("/{id}")
    @PermissionTag("sys:user:query")
    public User user(@PathVariable Long id) {
        return userDao.getById(id);
    }


    @PostMapping("/tryLogin")
    @ApiOperation(value = "用户(游客或者厂商)尝试自动登录")
    public Map tryLogin(@RequestBody WeiXinVo wx) {


        if (StringUtils.isEmpty(wx.getOpenid())) {
            throw new IllegalArgumentException("openid参数丢失");
        }


        String token = redisService.get(UserConstants.OPENID_MAP_TOKEN + wx.getOpenid());

        String userStr;

        // token还在
        if (!StringUtils.isEmpty(token) && (userStr = redisService.get(UserConstants.LOGIN_TOKEN + token)) != null) {


            // 已存在账号并已登录
            Map<String, Object> maps = new HashMap<>();

            User loginUser = JSONObject.parseObject(userStr, User.class);

            loginUser.setPassword(null);
            loginUser.setOriginalPassword(null);
            maps.put("user", loginUser);
            maps.put("role", UserUtil2.getRole(token));
            maps.put("token", token);
            maps.put("message", "之前已经登录，无需再次登录");
            return maps;


        } else {

            throw new NotLoginException("因为您长期没有使用,会话已经过期,请重新登录");

        }


    }


    private void roleDeal(UserDto wx, Long roleId) {

        if (wx.getRoleIds() == null) {
            wx.setRoleIds(new ArrayList<>());
        }
        if (wx.getRoleIds().size() == 0) {
            ArrayList<Long> roles = new ArrayList<>(1);
            roles.add(roleId);
            wx.setRoleIds(roles);
        }
    }


}
