package com.yusheng.hbgj.dto;

import java.util.Date;

/**
 * @author 金伟 on 2019/6/19 0019.
 * @date 2020-04-03 17:11
 * @desc 我的发布
 */
public class PublishDto {

    private String id;


    private String openid;

    private String publishContent;
    private String reply;

    private Date replyTime;

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
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

    public Date getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(Date replyTime) {
        this.replyTime = replyTime;
    }
}
