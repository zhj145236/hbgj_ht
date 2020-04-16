package com.yusheng.hbgj.entity;
import javax.validation.constraints.NotNull;
import java.util.Date;

public class FileInfo extends BaseEntity<String> {

	private static final long serialVersionUID = -5761547882766615438L;

	private String contentType;
	private long size;
	private String path;
	private String url;
	private String  type;

	private  String md5;


    /**
     * 文件资源ID ，绑定业务来源的ID
     */
    private String resourceId;


    /**
     *  文件标签
     */
    private String tag;


    /***
     * 文件上传时间
     */
	private  Date  uploadTime;


    /**
     * 原文件名
     */
    private String fileOriginName;


    /**
     *  图片删除时间(如果有删除：delFlag=1)
     */
    private Date delTime;


	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
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




    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getFileOriginName() {
        return fileOriginName;
    }

    public void setFileOriginName(String fileOriginName) {
        this.fileOriginName = fileOriginName;
    }

    @Override
    public Date getDelTime() {
        return delTime;
    }

    @Override
    public void setDelTime(Date delTime) {
        this.delTime = delTime;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
