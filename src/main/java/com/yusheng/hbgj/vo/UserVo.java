package com.yusheng.hbgj.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

/**
 * @author 金伟 on 2019/6/19 0019.
 * @date 2020-04-08 14:29
 * @desc 用户实体
 */
public class UserVo {

    private String username;
    private String password;
    @JsonIgnore
    private String salt;
    private String nickname;
    private String headImgUrl;
    private String phone;
    private String telephone;
    private String email;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthday;
    private Integer sex;
    private Integer status;

    private  String openid;
    private String address;
}
