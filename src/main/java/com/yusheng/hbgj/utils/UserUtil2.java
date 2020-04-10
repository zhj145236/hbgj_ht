package com.yusheng.hbgj.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yusheng.hbgj.constants.UserConstants;
import com.yusheng.hbgj.constants.NotLoginException;
import com.yusheng.hbgj.entity.Permission;
import com.yusheng.hbgj.entity.Role;
import com.yusheng.hbgj.entity.User;
import com.yusheng.hbgj.service.RedisService;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
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


    @Value("${token.expire.day}")
    private Integer expireDay;


    public static UserUtil2 userUtil;

    @PostConstruct
    public void init() {
        userUtil = this;

    }


    public static User getCurrentUser() {


        // 尝试从微信端端取用户信息
        String token = getToken();

        User user = null;

        if (!StringUtils.isEmpty(token)) {

            user = JSON.parseObject(userUtil.redisService.get(UserConstants.LOGIN_TOKEN + token), User.class);

        }

        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();

        // 尝试从PC端段取用户信息
        if (user == null) {


            HttpSession session = request.getSession();

            if (session != null) {
                user = (User) (request.getSession()).getAttribute(UserConstants.WEB_SESSION_KEY);
            }
        }


        if (user == null) {


            // TODO nginx 可能识别url有误

            String allowUrl1 = "/sys/login/restful";
            String allowUrl2 = "/sys/login";
            String allowUrl3 = "/users/wxAutoLogin";

            String url = request.getRequestURI();

            if (allowUrl1.equals(url) || allowUrl2.equals(url) || allowUrl3.equals(url)) {
                return null;
            } else {
                throw new NotLoginException("无法获取到登录信息，请重新登录");
            }
        }

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
        String loginToken = httpServletRequest.getParameter(UserConstants.LOGIN_TOKEN_HEAD);
        if (StringUtils.isEmpty(loginToken)) {
            loginToken = httpServletRequest.getHeader(UserConstants.LOGIN_TOKEN_HEAD);
        }


        // 还没有取得就从Web Session中取
        if (StringUtils.isEmpty(loginToken)) {


            User user = (User) (request.getSession().getAttribute(UserConstants.WEB_SESSION_KEY));
            if (user != null && !SysUtil.paramsIsNull(user.getUsername(), user.getOriginalPassword())) {

                loginToken = MD5.getMd5(user.getUsername() + user.getOriginalPassword());

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

            permissions.add(JSONObject.parseObject((String) item, Permission.class));

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
            roles.add(JSONObject.parseObject((String) item, Role.class));
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
        if (list.size() > 0) {

            list.forEach((item -> {
                userUtil.redisService.rightPush(prexAndtoken, JSONObject.toJSONString(item));

            }));

            //userUtil.redisService.rightPushAll(prexAndtoken, list);
        }
        //设置30天过期
        userUtil.redisService.expire(prexAndtoken, userUtil.expireDay, TimeUnit.DAYS);

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
        userUtil.redisService.expire(prexAndtoken, userUtil.expireDay, TimeUnit.DAYS);


    }


    /**
     * 为用户加入他的权限
     *
     * @param list 权限对象集合
     */
    public static void setPermission(String prexAddtoken, List<Permission> list) {


        userUtil.redisService.delete(prexAddtoken);

        if (list.size() > 0) {

            list.forEach((item) -> {

                userUtil.redisService.rightPush(prexAddtoken, JSON.toJSONString(item));
            });


            // userUtil.redisService.rightPushAll(prexAddtoken, list);
        }
        //设置30天过期
        userUtil.redisService.expire(prexAddtoken, userUtil.expireDay, TimeUnit.DAYS);


    }

    public static boolean logout(User user) {

        String loginToken = MD5.getMd5(user.getUsername() + user.getOriginalPassword());

        if (StringUtils.isEmpty(loginToken)) {
            return true;
        }
        return logout(null, null, loginToken);


    }

    public static boolean logout(HttpServletRequest request, HttpSession session, String loginToken) {

        String token;
        if (!StringUtils.isEmpty(loginToken)) {
            token = loginToken;
        } else {
            token = UserUtil2.getToken();
        }


        if (StringUtils.isEmpty(token)) {

            return false;
        }

        //Redis中删除token
        userUtil.redisService.delete(UserConstants.LOGIN_TOKEN + token);


        if (request != null) {
            String ip = NetWorkUtil.getIpAddress(request);
            String time = DateUtil.getNowStr0(true, false);

            //记录退出登录的信息
            saveLogoutHistoory(UserConstants.USER_LOGOUT_HISTORY + token, time + "|" + ip);
        }

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

    // 保存登出历史(最多20个)
    private static void saveLogoutHistoory(String a, String b) {
        Long size = userUtil.redisService.listSize(a, 0);

        if (size >= 20) {
            userUtil.redisService.rightPop(a);
        }
        userUtil.redisService.leftPush(a, b);

    }


    public static void setUserSession(HttpSession session, User user) {

        if (session != null) {

            session.setAttribute(UserConstants.WEB_SESSION_KEY, user);
        }
    }


}
