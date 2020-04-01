package com.yusheng.hbgj.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.yusheng.hbgj.constants.UserConstants;
import com.yusheng.hbgj.dao.UserDao;
import com.yusheng.hbgj.dto.UserDto;
import com.yusheng.hbgj.entity.User;
import com.yusheng.hbgj.service.UserService;
import com.yusheng.hbgj.utils.UserUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger("adminLogger");

    @Autowired
    private UserDao userDao;


    @Override
    @Transactional
    public User saveUser(UserDto userDto) {
        User user = userDto;

        user.setOriginalPassword(user.getPassword());

        user.setSalt(DigestUtils.md5Hex(UUID.randomUUID().toString() + System.currentTimeMillis() + UUID.randomUUID().toString()));
        user.setPassword(passwordEncoder(user.getPassword(), user.getSalt()));


        user.setStatus(User.Status.VALID);
        userDao.save(user);



        saveUserRoles(user.getId(), userDto.getRoleIds());

        log.debug("新增用户{}", user.getUsername());
        return user;
    }

    private void saveUserRoles(Long userId, List<Long> roleIds) {
        if (roleIds != null) {
            userDao.deleteUserRole(userId);
            if (!CollectionUtils.isEmpty(roleIds)) {
                userDao.saveUserRoles(userId, roleIds);
            }
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
    @Transactional
    public User updateUser(UserDto userDto) {
        userDao.update(userDto);
        saveUserRoles(userDto.getId(), userDto.getRoleIds());
        updateUserSession(userDto.getId());

        return userDto;
    }

    private void updateUserSession(Long id) {
        User current = UserUtil.getCurrentUser();
        if (current.getId().equals(id)) {
            User user = userDao.getById(id);
            UserUtil.setUserSession(user);
        }
    }
}
