package com.happyroad.wy.service;

import com.happyroad.wy.dto.UserDto;
import com.happyroad.wy.entity.User;

public interface UserService {

	User saveUser(UserDto userDto);
	
	User updateUser(UserDto userDto);

	String passwordEncoder(String credentials, String salt);

	User getUser(String username);

	void changePassword(String username, String oldPassword, String newPassword);

}
