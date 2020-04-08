package com.yusheng.hbgj.dto;

import com.yusheng.hbgj.utils.DateUtil;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.Map;

public class ResponseInfo<T> implements Serializable {

    private static final long serialVersionUID = -4417715614021482064L;

    private String code;
    private String message;
    private String respTime;

    private Map<String, Object> data;


    public ResponseInfo(String code, String message) {
        super();
        this.code = code;
        this.message = message;
        this.respTime = DateUtil.getNowStr();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRespTime() {

        return respTime;
    }

    public void setRespTime(String respTime) {
        this.respTime = respTime;
    }


    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public static ResponseInfo success(Map<String, Object> t) {

        ResponseInfo res = new ResponseInfo(HttpStatus.OK + "", "操作成功");
        res.setData(t);
        return res;


    }

    public static ResponseInfo fail(Map<String, Object> t) {

        ResponseInfo res = new ResponseInfo(HttpStatus.OK + "", "操作失败");
        res.setData(t);
        return res;


    }

    public static ResponseInfo fail(String msg) {

        return new ResponseInfo(HttpStatus.OK + "", msg);


    }

    public static ResponseInfo error(Map<String, Object> t) {

        ResponseInfo res = new ResponseInfo(HttpStatus.INTERNAL_SERVER_ERROR + "", "服务器内部异常");
        res.setData(t);
        return res;


    }


}
