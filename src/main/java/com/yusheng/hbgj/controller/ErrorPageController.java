package com.yusheng.hbgj.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 金伟 on 2019/6/19 0019.
 * @date 2020-04-21 14:43
 * @desc 页面错误统一处理
 */
@Controller
public class ErrorPageController implements ErrorController {


    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        //获取statusCode:401,404,500
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == 401) {
            return "/pages/error/401.html";
        } else if (statusCode == 404) {
            return "/pages/error/404.html";
        } else if (statusCode == 403) {
            return "/pages/error/403.html";
        } else {
            return "/pages/error/500.html";
        }
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
