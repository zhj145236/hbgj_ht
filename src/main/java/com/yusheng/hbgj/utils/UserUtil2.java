package com.yusheng.hbgj.utils;

import com.alibaba.fastjson.JSON;
import com.yusheng.hbgj.constants.UserConstants;
import com.yusheng.hbgj.constants.NotLoginException;
import com.yusheng.hbgj.entity.Permission;
import com.yusheng.hbgj.entity.Role;
import com.yusheng.hbgj.entity.User;
import com.yusheng.hbgj.service.RedisService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class UserUtil2 {


    @Autowired
    private RedisService redisService;


    public static UserUtil2 userUtil;

    @PostConstruct
    public void init() {
        userUtil = this;

    }


    public static User getCurrentUser() {


        // 从微信端端取
        String token = getToken();

         System.out.println("token--->" + token);

        User user = null;

        if (!StringUtils.isBlank(token)) {


            user = JSON.parseObject(userUtil.redisService.get(UserConstants.LOGIN_TOKEN + token), User.class);

        }

        // 从PC端段取
        if (user == null) {

            HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
            HttpSession session = request.getSession();

            if (session != null) {
                user = (User) (request.getSession()).getAttribute(UserConstants.WEB_SESSION_KEY);
            }
        }


        if (user == null) {

            //throw new NotLoginException("无法获取到登录信息，请重新登录");
        }

        //System.out.println("用户信息--》" + user.getNickname() + "<<<<<<<<<<<");

        return user;
    }


    /**
     * 根据参数或者header获取login-token
     *
     * @param
     * @return
     */
    public static String getToken() {

        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();

        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
        String loginToken = httpServletRequest.getParameter(UserConstants.LOGIN_TOKEN);
        if (StringUtils.isBlank(loginToken)) {
            loginToken = httpServletRequest.getHeader(UserConstants.LOGIN_TOKEN);
        }


        // 还没有取得就从Web Session中取
        if (StringUtils.isBlank(loginToken)) {


            User user = (User) (request.getSession().getAttribute(UserConstants.WEB_SESSION_KEY));
            if (user != null && !SysUtil.paramsIsNull(user.getUsername(), user.getPassword())) {

                loginToken = MD5.getMd5(user.getUsername() + user.getPassword());
            }

        }




        return loginToken;
    }


    /***
     * 获取用户的权限所有权限
     * @return
     */
    public static List<Permission> getCurrentPermissions() {


        String token = getToken();


        List<Object> list = userUtil.redisService.listRange(UserConstants.USER_PERMISSION + token, 0, -1);

        List<Permission> permissions = new ArrayList<>();

        list.forEach((item) -> {
            permissions.add((Permission) item);
        });

        return permissions;
    }


    /***
     * 获取用户的所有角色
     * @return
     */
    public static List<Role> getRole() {

        String token = getToken();

        List<Object> list = userUtil.redisService.listRange(UserConstants.USER_ROLE + token, 0, -1);

        List<Role> roles = new ArrayList<>();

        list.forEach((item) -> {
            roles.add((Role) item);
        });

        return roles;
    }


    /**
     * 为用户加入他的角色
     *
     * @param list 权限对象集合
     */
    public static void setRole(String prexAndtoken, List<Role> list) {


        userUtil.redisService.delete(prexAndtoken);
        userUtil.redisService.rightPushAll(prexAndtoken, list);

        //设置30天过期
        userUtil.redisService.expire(prexAndtoken, 30, TimeUnit.DAYS);

        //getSession().setAttribute(UserConstants.USER_PERMISSIONS, list);


    }

    /**
     * 为用户加入他的角色 (名称字符串 集合)
     *
     * @param set 权限对象集合
     */
    public static void setRoleName(String prexAndtoken, Set<String> set) {

        userUtil.redisService.delete(prexAndtoken);

        set.forEach((item) -> {
            userUtil.redisService.add(prexAndtoken, item);
        });


        //设置30天过期
        userUtil.redisService.expire(prexAndtoken, 30, TimeUnit.DAYS);


    }


    /**
     * 为用户加入他的权限
     *
     * @param list 权限对象集合
     */
    public static void setPermission(String prexAddtoken, List<Permission> list) {


        userUtil.redisService.delete(prexAddtoken);
        userUtil.redisService.rightPushAll(prexAddtoken, list);
        //设置30天过期
        userUtil.redisService.expire(prexAddtoken, 30, TimeUnit.DAYS);

        //getSession().setAttribute(UserConstants.USER_PERMISSIONS, list);


    }

    public static boolean logout(HttpServletRequest request, HttpSession session) {


        String token = UserUtil2.getToken();
        if (StringUtils.isBlank(token)) {

            System.out.println("token无法获取到");
            return false;
        }
        //Redis中删除token
        userUtil.redisService.delete(UserConstants.LOGIN_TOKEN + token);


        String ip = NetWorkUtil.getIpAddress(request);
        String time = DateUtil.getNowStr0(true, false);


        //记录退出登录的信息
        userUtil.redisService.leftPush(UserConstants.USER_LOGOUT_HISTORY + token, time + "|" + ip);


        //Redis中清除删除权限
        userUtil.redisService.delete(UserConstants.USER_PERMISSION + token);

        //Redis清除角色(对象)
        userUtil.redisService.delete(UserConstants.USER_ROLE + token);


        //Redis清除角色(字符串)
        userUtil.redisService.delete(UserConstants.USER_ROLE_NAME + token);


        if (session != null) {
            session.invalidate();
        }


        return true;


    }


    public static void setUserSession(HttpSession session, User user) {

        if (session != null) {

            //String token = MD5.getMd5(user.getUsername() + user.getPassword());

            session.setAttribute(UserConstants.WEB_SESSION_KEY, user);
        }
    }


}
