package com.yusheng.hbgj.constants;

/**
 * @author 金伟 on 2019/6/19 0019.
 * @date 2020-04-08 19:15
 * @desc 没有权限
 */
public class UnauthorizedException extends RuntimeException {


    public UnauthorizedException() {
        super();

    }

    public UnauthorizedException(String message) {
        super(message);

    }

}

