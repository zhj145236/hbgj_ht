package com.yusheng.hbgj.utils.validator;

/**
 * 创建人: 金伟
 * 描述: 验证反馈的消息
 * 创建时间: 2019/6/5 0005 - 下午 7:56
 **/
public class ValidatorMessage {


    /**
     * 验证是否通过 true:通过
     */
    private boolean isLegal;


    /**
     * 验证不通过的消息提示
     */
    private String message;

    public boolean isLegal() {
        return isLegal;
    }

    public void setLegal(boolean legal) {
        isLegal = legal;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}

    