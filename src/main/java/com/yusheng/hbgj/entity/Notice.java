package com.yusheng.hbgj.entity;

public class Notice extends BaseEntity<Long> {

    private static final long serialVersionUID = -4401913568806243090L;

    private String title;
    private String content;
    private Integer status;

    /**
     * 此公告是否私有？ 私有消息（1=是; 0=否,deafult:0）
     */
    private Integer isPersonal;


    private  String receiveId;

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public interface Status {
        int DRAFT = 0;
        int PUBLISH = 1;
    }

    public interface Personal {
        int YES = 1;
        int NO = 0;
    }


    public Integer getIsPersonal() {
        return isPersonal;
    }

    public void setIsPersonal(Integer isPersonal) {
        this.isPersonal = isPersonal;
    }

    public String getReceiveId() {
        return receiveId;
    }

    public void setReceiveId(String receiveId) {
        this.receiveId = receiveId;
    }
}
