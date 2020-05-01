package com.yusheng.hbgj.controller;

import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author jinwei
 * @date 2020-05-01
 * @desc 微信接口相关
 */

@RestController
@RequestMapping("/weixin")

public class WxController {


    @Value("${constants.appid}")
    private String appid;

    @Value("${constants.appsecret}")
    private String appsecret;


    @Autowired
    private RestTemplate restTemplate;

    private static final Logger log = LoggerFactory.getLogger("adminLogger");


    @PostMapping("/userOpenid")
    @ApiOperation(value = "获取微信用户的openid")
    public Object getOpenid(@RequestParam String js_code) {


        String URL = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appid + "&secret=" + appsecret + "&js_code=" + js_code + "&grant_type=authorization_code";

        String data = restTemplate.getForObject(URL, String.class);

        log.info("获取微信openid信息{}", data);

        return data;


    }


}
