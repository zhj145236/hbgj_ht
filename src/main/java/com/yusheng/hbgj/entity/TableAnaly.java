package com.yusheng.hbgj.entity;

/**
 * @author 金伟 on 2019/6/19 0019.
 * @date 2019/11/17 10:03
 * @desc 表分析
 */
public class TableAnaly   {

    private Integer id;
    private String tName;
    private int tRows;
    private String tComments;
    private int period;


    public String gettName() {
        return tName;
    }

    public void settName(String tName) {
        this.tName = tName;
    }

    public int gettRows() {
        return tRows;
    }

    public void settRows(int tRows) {
        this.tRows = tRows;
    }

    public String gettComments() {
        return tComments;
    }

    public void settComments(String tComments) {
        this.tComments = tComments;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
