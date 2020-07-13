package com.yusheng.hbgj.service;

import com.yusheng.hbgj.dto.UserDto;
import com.yusheng.hbgj.entity.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

public interface UserService {


    Integer saveOpenid(Long id, String openid);


    User saveUser(UserDto userDto);

    User updateUser(UserDto userDto, HttpSession session);

    String passwordEncoder(String credentials, String salt);

    User getUser(String username);

    void changePassword(String username, String oldPassword, String newPassword);

    List<User> getAllUser();


    void lockAccount(String username);


    /**
     * openid 存在表的数量，< = 1
     *
     * @param openid
     * @return
     */
    int wxCountByOpenid(String openid);


    /***
     * 通过openid得到主键id
     * @param openid
     * @return
     */
    //Long getUserId(String openid);


    /**
     * 用户 登录
     *
     * @param user    用户对象
     * @param request 请求
     * @return
     */
    Map<String, Object> login(User user, HttpServletRequest request, HttpSession session);


    User getInfoByOpenId(String openid,Integer compFlag);


    /**
     * 用户 退出登录
     *
     * @param request 请求
     * @return
     */
    boolean logout(HttpServletRequest request, HttpSession session);


    Boolean agreeLicence(String userId);

    /**
     * 利用openid得到 token
     *
     * @param openid openid
     * @return
     */
    String getTokenByOpenId(String openid);

}
