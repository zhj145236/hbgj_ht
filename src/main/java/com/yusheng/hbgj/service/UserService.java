package com.yusheng.hbgj.service;

import com.yusheng.hbgj.dto.UserDto;
import com.yusheng.hbgj.entity.User;

import java.util.List;

public interface UserService {

	User saveUser(UserDto userDto);
	
	User updateUser(UserDto userDto);

	String passwordEncoder(String credentials, String salt);

	User getUser(String username);

	void changePassword(String username, String oldPassword, String newPassword);

	List<User> getAllUser();

}
