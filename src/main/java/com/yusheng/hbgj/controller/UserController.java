package com.yusheng.hbgj.controller;

import java.util.ArrayList;
import java.util.List;

import com.yusheng.hbgj.annotation.LogAnnotation;
import com.yusheng.hbgj.constants.BusinessException;
import com.yusheng.hbgj.dao.UserDao;
import com.yusheng.hbgj.dto.UserDto;
import com.yusheng.hbgj.entity.User;
import com.yusheng.hbgj.page.table.PageTableHandler;
import com.yusheng.hbgj.page.table.PageTableRequest;
import com.yusheng.hbgj.page.table.PageTableResponse;
import com.yusheng.hbgj.service.UserService;
import com.yusheng.hbgj.utils.StrUtil;
import com.yusheng.hbgj.utils.UserUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

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

    @Value("${constants.companyRoleId}")
    private Long companyRoleId;


    @Value("${constants.visitorRoleId}")
    private Long visitorRoleId;


    @LogAnnotation
    @PostMapping
    @ApiOperation(value = "Web保存用户", notes = "如果没有勾选任何角色，系统就默认设置为厂商角色")
    @RequiresPermissions("sys:user:add")
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
    @RequiresPermissions("sys:user:add")
    public User updateUser(@RequestBody UserDto userDto) {
        return userService.updateUser(userDto);
    }

    @LogAnnotation
    @PutMapping(params = "headImgUrl")
    @ApiOperation(value = "修改头像")
    public void updateHeadImgUrl(String headImgUrl) {
        User user = UserUtil.getCurrentUser();
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(user, userDto);
        userDto.setHeadImgUrl(headImgUrl);

        userService.updateUser(userDto);
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
    @RequiresPermissions("sys:user:password")
    public void changePassword(@PathVariable String username, String oldPassword, String newPassword) {
        userService.changePassword(username, oldPassword, newPassword);
    }

    @GetMapping
    @ApiOperation(value = "用户列表")
    @RequiresPermissions("sys:user:query")
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
        return UserUtil.getCurrentUser();
    }

    @ApiOperation(value = "根据用户id获取用户")
    @GetMapping("/{id}")
    @RequiresPermissions("sys:user:query")
    public User user(@PathVariable Long id) {
        return userDao.getById(id);
    }


    @LogAnnotation
    @PostMapping("/addWxUser")
    @ApiOperation(value = "新增微信用户")
    public User addWxUser(@RequestBody UserDto wx) {

        if (StringUtils.isEmpty(wx.getOpenid())) {
            throw new IllegalArgumentException("Openid参数丢失");
        }

        if (userService.wxCountByOpenid(wx.getOpenid()) >= 1) {

            throw new BusinessException("此微信号已经注册过了，请直接登录");

        }


        // 默认加上游客角色
        roleDeal(wx, visitorRoleId);

        //TODO username 即为微信用户名（英文）

        // TODO 如果没登录自动为他登录

        wx.setPassword("123456");
        wx.setOriginalPassword("123456");

        return userService.saveUser(wx);

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
