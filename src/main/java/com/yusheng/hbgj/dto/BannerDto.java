package com.yusheng.hbgj.dto;

import java.util.Date;

/**
 * @author 金伟 on 2019/6/19 0019.
 * @date 2020-04-16 15:03
 * @desc
 */
public class BannerDto {

    private  Long id;
    private String mainImg;
    private String content;



    public String getMainImg() {
        return mainImg;
    }

    public void setMainImg(String mainImg) {
        this.mainImg = mainImg;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
