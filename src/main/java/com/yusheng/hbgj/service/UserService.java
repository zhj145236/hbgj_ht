package com.yusheng.hbgj.service;

import com.yusheng.hbgj.dto.UserDto;
import com.yusheng.hbgj.entity.User;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserService {

	User saveUser(UserDto userDto);
	
	User updateUser(UserDto userDto);

	String passwordEncoder(String credentials, String salt);

	User getUser(String username);

	void changePassword(String username, String oldPassword, String newPassword);

	List<User> getAllUser();


    /**
     * openid 存在表的数量，<=1
     * @param openid
     * @return
     */
	int wxCountByOpenid(String openid);


    /***
     * 通过openid 得到主键id
     * @param openid
     * @return
     */
    Long getUserId(String openid);


}
