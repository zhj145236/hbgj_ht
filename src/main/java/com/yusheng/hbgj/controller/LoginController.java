package com.yusheng.hbgj.controller;

import com.alibaba.fastjson.JSONObject;
import com.yusheng.hbgj.annotation.LogAnnotation;
import com.yusheng.hbgj.constants.UserConstants;
import com.yusheng.hbgj.dto.ResponseInfo;
import com.yusheng.hbgj.entity.User;
import com.yusheng.hbgj.service.UserService;
import com.yusheng.hbgj.utils.MD5;
import com.yusheng.hbgj.utils.UserUtil2;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
    private UserService userService;


    private static final Logger log = LoggerFactory.getLogger("adminLogger");


    @LogAnnotation
    @ApiOperation(value = "web端登录", notes = "传入账号和密码")
    @PostMapping("/sys/login")
    public void login(String username, String password, HttpServletRequest request, HttpSession session) {

        User user = userService.getUser(username);
        if (user != null) {


            if (user.getPassword().equals(userService.passwordEncoder(password, user.getSalt()))) {

                log.info("登录成功");

                // Web登录
                session.setAttribute(UserConstants.WEB_SESSION_KEY, user);

                // TODO 设置失效时间


                userService.login(user, request, session);

                //利用 账号与密码生成唯一token
                //String token = MD5.getMd5(user.getUsername() + user.getPassword());



                log.info("登录成功 {},{},{}", user.getId(), user.getUsername(), user.getNickname());


            } else {
                log.info("登录失败，不正确的密码{}", password);
            }


        } else {

            // 不存在的账号
            log.info("不存在的账号{}", username);

        }


        //UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(username, password);
        //SecurityUtils.getSubject().login(usernamePasswordToken);
        //SecurityUtils.getSubject().getSession().setTimeout(serverProperties.getServlet().getSession().getTimeout().toMillis());


    }

    @LogAnnotation
    @ApiOperation(value = "Restful方式登录,前后端分离时登录接口", notes = "注意返回的token要放在RequestHeader上,后期调用其他接口都需要token")
    @PostMapping("/sys/login/restful")
    public ResponseInfo restfulLogin(String username, String password, HttpServletRequest request, HttpSession session) {

        User user = userService.getUser(username);

        if (user != null) {


            if (user.getPassword().equals(userService.passwordEncoder(password, user.getSalt()))) {


                //restful 登录
                Map<String, Object> maps = userService.login(user, request, session);

                log.info("登录成功 {},{},{}", user.getId(), user.getUsername(), user.getNickname());

                return ResponseInfo.success(maps);

            } else {
                log.info("登录失败，{}输入了不正确的密码{}", username, password);

                return ResponseInfo.fail("登录失败,原因密码不正确");

            }


        } else {

            log.info("登录失败,账号不存在，{}", username);
            return ResponseInfo.fail("登录失败,账号不存在");


        }


    }

    @ApiOperation(value = "当前登录用户")
    @GetMapping("/sys/login")
    public User getLoginInfo() {

        return UserUtil2.getCurrentUser();
    }


    @ApiOperation(value = "用户退出登录")
    @GetMapping("/sys/logout")
    public boolean logout(HttpServletRequest request, HttpSession session) {

        userService.logout(request, session);


        return true;

    }


}
