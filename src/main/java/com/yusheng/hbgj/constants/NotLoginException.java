package com.yusheng.hbgj.constants;

/**
 * @author 金伟 on 2019/6/19 0019.
 * @date 2020-04-08 15:20
 * @desc 用户未登录
 */
public class NotLoginException extends  RuntimeException {



    public NotLoginException() {
        super();

    }

    public NotLoginException(String message) {
        super(message);

    }

}
