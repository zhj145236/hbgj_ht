package com.yusheng.hbgj.controller;

import com.alibaba.fastjson.JSONObject;
import com.yusheng.hbgj.annotation.LogAnnotation;
import com.yusheng.hbgj.constants.BusinessException;
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
import org.springframework.beans.factory.annotation.Value;
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


    @Value("${token.expire.webExpireDay}")
    private Integer webExpireDay;


    private static final Logger log = LoggerFactory.getLogger("adminLogger");


    @LogAnnotation
    @ApiOperation(value = "web端登录", notes = "传入账号和密码")
    @PostMapping("/sys/login")
    public void login(String username, String password, HttpServletRequest request, HttpSession session) {

        User user = userService.getUser(username);
        if (user != null) {


            if (user.getPassword().equals(userService.passwordEncoder(password, user.getSalt()))) {

                log.info("登录成功");


                String aa = user.getOriginalPassword();
                String bb = user.getPassword();
                userService.login(user, request, session);

                // Web登录
                user.setOriginalPassword(aa);
                user.setPassword(bb);
                log.info("{},{}", aa, bb);
                session.setAttribute(UserConstants.WEB_SESSION_KEY, user);
                session.setMaxInactiveInterval(webExpireDay * 3600 * 24);  // 秒为单位,这里设置30天


                log.info("登录成功 {},{},{}", user.getId(), user.getUsername(), user.getNickname());


            } else {
                log.info("登录失败，不正确的密码{}", password);

                // TODO 监控并尝试锁住账号 阈值： 10分钟内连续输错5次
                // 你还有 X次机会
                // 请X分钟后再试

                throw new BusinessException("登录失败：密码不正确");
            }


        } else {

            // 不存在的账号
            log.info("不存在的账号{}", username);


            throw new BusinessException("登录失败：不存在的账号【" + username + "】");

        }


    }

    // 连续输入3此密码错误 先锁住账号
    private void dealLogin() {

    }

    @LogAnnotation
    @ApiOperation(value = "Restful方式登录,前后端分离时登录接口", notes = "注意返回的token要放在RequestHeader上,后期调用其他接口都需要token")
    @PostMapping("/sys/login/restful")
    public ResponseInfo restfulLogin(String username, String password, HttpServletRequest request, HttpSession session) {

        User user = userService.getUser(username);

        if (user != null) {


            if (user.getPassword().equals(userService.passwordEncoder(password, user.getSalt()))) {


                // restful 登录
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

    @ApiOperation(value = "获取当前登录用户")
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
