package com.yusheng.hbgj.service;

import com.yusheng.hbgj.dto.Token;
import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * Token管理器<br>
 * 目前提供两种实现<br>
 * 默认基于ehcache，如需更改，使用@Primary注解
 * @see RedisTokenManager
 * 
 * @author Jinwei
 *
 *         2017年8月4日
 */
public interface TokenManager {

	/**
	 * 保存Token
	 * 
	 * @param token
	 * @return
	 */
	Token saveToken(UsernamePasswordToken token);

	/**
	 * 根据token获取凭证
	 * 
	 * @param key
	 * @return
	 */
	UsernamePasswordToken getToken(String key);

	/**
	 * 删除token
	 * 
	 * @param key
	 */
	boolean deleteToken(String key);

}
