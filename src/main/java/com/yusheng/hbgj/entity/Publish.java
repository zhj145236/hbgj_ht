package com.yusheng.hbgj.entity;

import java.util.Date;

public class Publish extends BaseEntity<Long> {

	private String openid;
    private Long userId;

    private String sex;
	private String tel;
	private String nickName;
	private String headPic;
	private String publishContent;
	private  String reply;

    /***
     * 用户已读消息的时间
     */
	private  Date  userReadTime;

	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getHeadPic() {
		return headPic;
	}
	public void setHeadPic(String headPic) {
		this.headPic = headPic;
	}
	public String getPublishContent() {
		return publishContent;
	}
	public void setPublishContent(String publishContent) {
		this.publishContent = publishContent;
	}


    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getUserReadTime() {
        return userReadTime;
    }

    public void setUserReadTime(Date userReadTime) {
        this.userReadTime = userReadTime;
    }

}
