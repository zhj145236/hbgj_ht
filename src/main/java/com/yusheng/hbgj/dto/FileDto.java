package com.yusheng.hbgj.dto;

import java.util.Date;

/**
 * @author 金伟 on 2019/6/19 0019.
 * @date 2020-04-03 15:30
 * @desc 企业检修资料；企业台账；合同；环保文件
 */
public class FileDto {


    private String id;

    private String url;

    private String fileOriginName;

    private Date uploadTime;
    private String type;
    private String tag;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFileOriginName() {
        return fileOriginName;
    }

    public void setFileOriginName(String fileOriginName) {
        this.fileOriginName = fileOriginName;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
