package com.yusheng.hbgj.constants;

/**
 * @author 金伟 on 2019/6/19 0019.
 * @date 2020-04-03 10:41
 * @desc 业务逻辑异常
 */
public class BusinessException extends RuntimeException {


    public BusinessException() {
        super();

    }

    public BusinessException(String message) {
        super(message);

    }

}
