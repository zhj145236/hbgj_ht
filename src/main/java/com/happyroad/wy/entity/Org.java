package com.happyroad.wy.entity;

import java.util.List;

public class Org extends BaseEntity<Long> {

    private static final long serialVersionUID = 6180869216498363919L;

    private Long parentId;
    private String orgName;

    private List<Org> child;

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }





    public List<Org> getChild() {
        return child;
    }

    public void setChild(List<Org> child) {
        this.child = child;
    }


}
