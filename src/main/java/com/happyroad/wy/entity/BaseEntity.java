package com.happyroad.wy.entity;

import java.io.Serializable;
import java.util.Date;

public abstract class BaseEntity<ID extends Serializable> implements Serializable {

	private static final long serialVersionUID = 2054813493011812469L;

	private ID id;


    private String createName;
    private String modifyName;
    private Date createDate;
    private Date modifyDate;

    private String delFlag;
    private String remark;
    private Integer sort;


	public ID getId() {
		return id;
	}

	public void setId(ID id) {
		this.id = id;
	}



    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public String getModifyName() {
        return modifyName;
    }

    public void setModifyName(String modifyName) {
        this.modifyName = modifyName;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
