package com.yusheng.hbgj.service.impl;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONObject;
import com.yusheng.hbgj.constants.UserConstants;
import com.yusheng.hbgj.dao.PermissionDao;
import com.yusheng.hbgj.dao.RoleDao;
import com.yusheng.hbgj.dao.UserDao;
import com.yusheng.hbgj.dto.RoleEntity;
import com.yusheng.hbgj.dto.Token;
import com.yusheng.hbgj.dto.UserDto;
import com.yusheng.hbgj.entity.Permission;
import com.yusheng.hbgj.entity.Role;
import com.yusheng.hbgj.entity.User;
import com.yusheng.hbgj.service.RedisService;
import com.yusheng.hbgj.service.TokenManager;
import com.yusheng.hbgj.service.UserService;
import com.yusheng.hbgj.utils.*;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger("adminLogger");

    @Autowired
    private UserDao userDao;


    @Autowired
    private RedisService redisService;

    @Value("${token.expire.day}")
    private Integer expireDay;


    @Override
    @Transactional
    public User saveUser(UserDto userDto) {

        User user = userDto;

        user.setOriginalPassword(user.getPassword());

        user.setSalt(DigestUtils.md5Hex(UUID.randomUUID().toString() + System.currentTimeMillis() + UUID.randomUUID().toString()));
        user.setPassword(passwordEncoder(user.getPassword(), user.getSalt()));

        // 标记为厂商
        user.setCompFlag(1);

        user.setStatus(User.Status.VALID);
        userDao.save(user);


        saveUserRoles(user.getId(), userDto.getRoleIds());

        log.debug("新增厂商 {}", user.getUsername());
        return user;
    }

    private void saveUserRoles(Long userId, List<Long> roleIds) {
        if (roleIds != null) {


            userDao.deleteUserRole(userId);


            if (!CollectionUtils.isEmpty(roleIds)) {
                userDao.saveUserRoles(userId, roleIds);
            }

            //TODO 更新Redis

        }
    }

    @Override
    public String passwordEncoder(String credentials, String salt) {
        Object object = new SimpleHash("MD5", credentials, salt, UserConstants.HASH_ITERATIONS);
        return object.toString();
    }

    @Override
    public User getUser(String username) {
        return userDao.getUser(username);
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        User u = userDao.getUser(username);
        if (u == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        if (!u.getPassword().equals(passwordEncoder(oldPassword, u.getSalt()))) {
            throw new IllegalArgumentException("密码错误");
        }

        userDao.changePassword(u.getId(), passwordEncoder(newPassword, u.getSalt()), newPassword);

        log.debug("修改{}的密码", username);
    }

    @Override
    public List<User> getAllUser() {
        return userDao.getAllUser();
    }

    @Override
    public int wxCountByOpenid(String openid) {
        return userDao.wxCountByOpenid(openid);
    }

    @Override
    public Long getUserId(String openid) {

        return userDao.getUserId(openid);

    }


    @Override
    public Map<String, Object> login(User user, HttpServletRequest request, HttpSession session) {


        //利用 账号与明文密码生成唯一token
        String token = MD5.getMd5(user.getUsername() + user.getOriginalPassword());

        log.debug("登录账号{}与密码{}", user.getUsername(), user.getOriginalPassword());

        //保存到Redis中，后期都从这里取登录对象信息
        redisService.set(UserConstants.LOGIN_TOKEN + token, JSONObject.toJSONString(user));


        //登录 有效期 30天
        redisService.expire(UserConstants.LOGIN_TOKEN + token, expireDay, TimeUnit.DAYS);


        String ip = NetWorkUtil.getIpAddress(request);
        String time = DateUtil.getNowStr0(true, false);

        //记录登录时间

        this.saveLoginHistoory(UserConstants.USER_LOGIN_HISTORY + token, time + "|" + ip);


        //记录权限(对象)
        List<Permission> permissionList = SpringUtil.getBean(PermissionDao.class).listByUserId(user.getId());
        UserUtil2.setPermission(UserConstants.USER_PERMISSION + token, permissionList);


        //记录角色(对象)
        List<Role> roles = SpringUtil.getBean(RoleDao.class).listByUserId(user.getId());
        UserUtil2.setRole(UserConstants.USER_ROLE + token, roles);


        //记录角色(字符串)
        Set<String> roleNames = roles.stream().map(Role::getName).collect(Collectors.toSet());
        UserUtil2.setRoleName(UserConstants.USER_ROLE_NAME + token, roleNames);


        Map<String, Object> maps = new HashMap<>();


        user.setOriginalPassword(null);
        user.setPassword(null);

        maps.put("user", user);
        maps.put("token", token);

        ;
        maps.put("role", transRole(roles));


        if (session != null && !StringUtils.isEmpty(session.getId())) {
            maps.put("Cookie", "JSESSIONID=" + session.getId());
        }


        return maps;

    }


    private List<RoleEntity> transRole(List<Role> roles) {

        List<RoleEntity> roleEntityList = new ArrayList<>();

        roles.forEach((item) -> {

            RoleEntity temp = new RoleEntity();

            temp.id = item.getId();
            temp.name = item.getName();
            temp.desc = item.getDescription();


            roleEntityList.add(temp);
        });
        return roleEntityList;
    }


    // 保存登录历史(最多20个)
    private void saveLoginHistoory(String a, String b) {


        Long size = redisService.listSize(a, 0);


        if (size >= 20) {
            redisService.rightPop(a);
        }
        redisService.leftPush(a, b);


    }


    @Override
    public User getInfoByOpenId(String openid) {

        return userDao.getInfoByOpenId(openid);

    }

    @Override
    public boolean logout(HttpServletRequest request, HttpSession session) {

        // 清除会话和权限
        boolean flag = UserUtil2.logout(request, session);

        // 删除无用文件


        return flag;

    }

    @Override
    public Boolean agreeLicence(String userId) {

        int sm=  userDao.agreeLicence(userId);

        return  sm>=1;


    }

    @Override
    @Transactional
    public User updateUser(UserDto userDto, HttpSession session) {
        userDao.update(userDto);
        saveUserRoles(userDto.getId(), userDto.getRoleIds());
        updateUserSession(session, userDto.getId());

        return userDto;
    }

    private void updateUserSession(HttpSession session, Long id) {
        User current = UserUtil2.getCurrentUser();
        if (current.getId().equals(id)) {
            User user = userDao.getById(id);
            UserUtil2.setUserSession(session, user);
        }
    }
}
