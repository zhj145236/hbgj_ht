package com.yusheng.hbgj.constants;

/**
 * 用户相关常量
 * 
 * @author Jinwei
 *
 */
public interface UserConstants {

	/** 加密次数 */
	int HASH_ITERATIONS = 3;

    String USER_LOGIN_HISTORY = "user:login_history:";

    String USER_LOGOUT_HISTORY = "user:logout_history:";

    String USER_ROLE_NAME = "user:role_name:";


	String USER_ROLE = "user:role:";

	String USER_PERMISSION = "user:permission:";


	String LOGIN_TOKEN = "user:token:";

	String WEB_SESSION_KEY="user:websession:";


}
