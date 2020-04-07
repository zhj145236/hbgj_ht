package com.yusheng.hbgj.vo;

import java.util.PrimitiveIterator;

/**
 * @author 金伟 on 2019/6/19 0019.
 * @date 2020-04-07 17:32
 * @desc 微信实体类
 */
public class WeiXinVo {


   /* avatarUrl: "https://wx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTKUyic7ia2o1IiaDBcLI4xeFpWrbaH2dWr21sSPvPwoEcDviakK4ibbfV9SWnqqSpRytZSMwhVrgGMUleQ/132"
    city: ""
    country: "Albania"
    gender: 1
    language: "zh_CN"
    nickName: "恐低的鸟"
    province: ""*/


    /**
     * 头像
     */
    private String avatarUrl;
    /**
     * 城市
     */
    private String city;
    /**
     * 国家？
     */
    private String country;

    /**
     * 性别 1=男，0=女
     */
    private Integer gender;


    private String language;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 省
     */
    private String province;

    /***
     * openid
     */
    private String openid;

    /**
     * 手机号
     */
    private  String tel;


    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }
}
