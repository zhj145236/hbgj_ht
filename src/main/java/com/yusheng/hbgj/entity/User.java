package com.yusheng.hbgj.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class User extends BaseEntity<Long> {

    private static final long serialVersionUID = -6525908145032868837L;

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


    /**
     * 是否为厂商 1=是，0=否
     */
    private Integer compFlag;

    /**
     * 厂商同意许可的时间
     */
    private Date agreeLicence;


    /***
     * 微信Openid
     */
    private String openid;


    /**
     * 原始密码
     */
    private String originalPassword;

    private String address;

    /**
     * '合同开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date contractBeginDate;

    /**
     * 合同结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date contractEndDate;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public interface Status {

        int VALID = 1;
        int LOCKED = 2;
    }

    public String getOriginalPassword() {
        return originalPassword;
    }

    public void setOriginalPassword(String originalPassword) {
        this.originalPassword = originalPassword;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public Integer getCompFlag() {
        return compFlag;
    }

    public void setCompFlag(Integer compFlag) {
        this.compFlag = compFlag;
    }

    public Date getAgreeLicence() {
        return agreeLicence;
    }

    public void setAgreeLicence(Date agreeLicence) {
        this.agreeLicence = agreeLicence;
    }

    public Date getContractBeginDate() {
        return contractBeginDate;
    }

    public void setContractBeginDate(Date contractBeginDate) {
        this.contractBeginDate = contractBeginDate;
    }

    public Date getContractEndDate() {
        return contractEndDate;
    }

    public void setContractEndDate(Date contractEndDate) {
        this.contractEndDate = contractEndDate;
    }
}
