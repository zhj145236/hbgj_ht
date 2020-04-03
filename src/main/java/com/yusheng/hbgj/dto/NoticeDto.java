package com.yusheng.hbgj.dto;

import com.yusheng.hbgj.entity.Notice;

import java.util.Date;

public class NoticeDto {

    private static final long serialVersionUID = -3842182350180882396L;

    private String id;
    private String title;
    private String content;
    private Boolean hasRead;
    private Date readTime;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public String getContent() {
        return content;
    }


    public void setContent(String content) {
        this.content = content;
    }


    public Boolean getHasRead() {
        return hasRead;
    }

    public void setHasRead(Boolean hasRead) {
        this.hasRead = hasRead;
    }

    public Date getReadTime() {
        return readTime;
    }

    public void setReadTime(Date readTime) {
        this.readTime = readTime;
    }
}
