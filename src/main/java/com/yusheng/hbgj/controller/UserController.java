package com.yusheng.hbgj.controller;

import com.yusheng.hbgj.annotation.LogAnnotation;
import com.yusheng.hbgj.annotation.PermissionTag;
import com.yusheng.hbgj.dao.UserDao;
import com.yusheng.hbgj.dto.UserDto;
import com.yusheng.hbgj.entity.User;
import com.yusheng.hbgj.page.table.PageTableHandler;
import com.yusheng.hbgj.page.table.PageTableRequest;
import com.yusheng.hbgj.page.table.PageTableResponse;
import com.yusheng.hbgj.service.TokenManager;
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
    private TokenManager tokenManager;


    @Value("${constants.companyRoleId}")
    private Long companyRoleId;


    @Value("${constants.visitorRoleId}")
    private Long visitorRoleId;


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

    @GetMapping
    @ApiOperation(value = "用户列表")
    @PermissionTag("sys:user:query")
    public PageTableResponse listUsers(PageTableRequest request) {
        return new PageTableHandler(new PageTableHandler.CountHandler() {

            @Override
            public int count(PageTableRequest request) {
                return userDao.count(request.getParams());
            }
        }, new PageTableHandler.ListHandler() {

            @Override
            public List<User> list(PageTableRequest request) {
                List<User> list = userDao.list(request.getParams(), request.getOffset(), request.getLimit());
                return list;
            }
        }).handle(request);
    }

    @ApiOperation(value = "当前登录用户")
    @GetMapping("/current")
    public User currentUser() {
        return UserUtil2.getCurrentUser();
    }

    @ApiOperation(value = "根据用户id获取用户")
    @GetMapping("/{id}")
    @PermissionTag("sys:user:query")
    public User user(@PathVariable Long id) {
        return userDao.getById(id);
    }


    @LogAnnotation
    @PostMapping("/wxAutoLogin")
    @ApiOperation(value = "微信用户自动登录")
    public Map addWxUser(@RequestBody WeiXinVo wx, HttpServletRequest request, HttpServletResponse response, HttpSession session) {

        if (StringUtils.isEmpty(wx.getOpenid())) {
            throw new IllegalArgumentException("openid参数丢失");
        }

        Map<String, Object> maps = new HashMap<>();

        // 后台已经登录了
        String loginToken = UserUtil2.getToken();


        // 微信用户已经登录，无需再次登录
        if (StringUtils.isEmpty(loginToken)) {

            try {
                User currentUser = UserUtil2.getCurrentUser();
                if (currentUser != null) {
                    maps.put("wxUser", userService.getInfoByOpenId(wx.getOpenid()));

                    maps.put("msg", "之前已经登录，无需再次登录");
                    return maps;
                }

            } catch (Exception e) {

                log.error("用户未登录。。。。。。");

            }

            return maps;


        } else {


            User userObj = userService.getInfoByOpenId(wx.getOpenid());

            if (userObj == null) {


                log.info("微信新用户注册。。。。。");

                UserDto userVo = new UserDto();

                // 默认加上游客角色
                List<Long> roles = new ArrayList<>(1);
                roles.add(visitorRoleId);
                userVo.setRoleIds(roles);


                userVo.setNickname(wx.getNickName());
                userVo.setAddress(wx.getCountry() + wx.getProvince() + wx.getCity());
                userVo.setHeadImgUrl(wx.getAvatarUrl());
                userVo.setOpenid(wx.getOpenid());
                userVo.setSex(wx.getGender());
                userVo.setTelephone(wx.getTel());


                // 无法获取到用户微信名字，所以采用系统随机生成username
                String username = "wx" + StrUtil.random(6);
                while (userService.getUser(username) != null) {
                    log.info(username + "已经被注册，系统重新选号码");
                    username = "wx" + StrUtil.random(6);

                }
                userVo.setUsername(username);
                userVo.setRemark("游客授权微信登录");


                String password = "123456";

                userVo.setPassword(password);
                userVo.setOriginalPassword(password);

                User user = userService.saveUser(userVo);

                // 为新用户在后台静默登录
                Map<String, Object> map = userService.login(user, request, session);

                map.put("msg", "微信新用户登录");

                return map;


            } else {

                log.info("之前已经有注册了,重新新自动登录");

                //userObj.setPassword(userObj.getOriginalPassword()); //密码转换

                Map<String, Object> map = userService.login(userObj, request, session);
                maps.put("wxUser", userService.getInfoByOpenId(wx.getOpenid()));

                maps.put("msg", "之前已经有注册了,系统为其自动登录上");

                return maps;
            }


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
