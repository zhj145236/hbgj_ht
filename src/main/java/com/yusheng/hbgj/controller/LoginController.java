package com.yusheng.hbgj.controller;

import com.yusheng.hbgj.annotation.LogAnnotation;
import com.yusheng.hbgj.dto.ResponseInfo;
import com.yusheng.hbgj.dto.Token;
import com.yusheng.hbgj.entity.User;
import com.yusheng.hbgj.service.TokenManager;
import com.yusheng.hbgj.service.UserService;
import com.yusheng.hbgj.utils.UserUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录相关接口
 *
 * @author Jinwei
 */
@Api(tags = "登录")
@RestController
@RequestMapping
public class LoginController {

    @Autowired
    private TokenManager tokenManager;
    @Autowired
    private ServerProperties serverProperties;

    @Autowired
    private UserService userService;


    @LogAnnotation
    @ApiOperation(value = "web端登录", notes = "传入账号和密码")
    @PostMapping("/sys/login")
    public void login(String username, String password) {
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(username, password);
        SecurityUtils.getSubject().login(usernamePasswordToken);
        SecurityUtils.getSubject().getSession().setTimeout(serverProperties.getServlet().getSession().getTimeout().toMillis());

    }

    @LogAnnotation
    @ApiOperation(value = "Restful方式登录,前后端分离时登录接口", notes = "注意返回的token要放在RequestHeader上,后期调用其他接口都需要token")
    @PostMapping("/sys/login/restful")
    public ResponseInfo restfulLogin(String username, String password, HttpSession session) {


        Map<String, Object> maps = userService.restfulLogin(username, password, session, tokenManager);


        return ResponseInfo.success(maps);


    }

    @ApiOperation(value = "当前登录用户")
    @GetMapping("/sys/login")
    public User getLoginInfo() {
        return UserUtil.getCurrentUser();
    }

}
