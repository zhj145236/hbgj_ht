package com.yusheng.hbgj.constants;

/**
 * 用户相关常量
 * 
 * @author Jinwei
 *
 */
public interface UserConstants {


    /**
     * openid与token关联
     */
    String OPENID_MAP_TOKEN ="string:openid_map_token:";




	/** 加密次数 */
	int HASH_ITERATIONS = 3;

    String USER_LOGIN_HISTORY = "user:login_history:";

    String USER_LOGOUT_HISTORY = "user:logout_history:";

    String USER_ROLE_NAME = "user:role_name:";


	String USER_ROLE = "user:role:";

	String USER_PERMISSION = "user:permission:";


    /**
     * token 和 用户基本信息到redis中
     */
	String LOGIN_TOKEN = "user:token:";

    /**
     * 请求头
     */
    String LOGIN_TOKEN_HEAD = "login-token";


	String WEB_SESSION_KEY="user:websession:";


}
