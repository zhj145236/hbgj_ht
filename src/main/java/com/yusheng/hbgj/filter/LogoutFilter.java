package com.yusheng.hbgj.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.yusheng.hbgj.dto.ResponseInfo;
import com.yusheng.hbgj.entity.User;
import com.yusheng.hbgj.utils.SpringUtil;
import com.yusheng.hbgj.utils.UserUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import com.alibaba.fastjson.JSONObject;
import com.yusheng.hbgj.service.SysLogService;
import com.yusheng.hbgj.service.TokenManager;

/**
 * 退出<br>
 * web退出和restful方式退出<br>
 * 后者会删除缓存的token
 *
 * @author Jinwei
 * <p>
 * 2017年8月13日
 */
public class LogoutFilter extends org.apache.shiro.web.filter.authc.LogoutFilter {

    private static final Logger log = LoggerFactory.getLogger("adminLogger");

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        String loginToken = RestfulFilter.getToken(request);
        User user = UserUtil.getCurrentUser();
        if (StringUtils.isBlank(loginToken)) {// 非Restful方式
            boolean flag = super.preHandle(request, response);
            log.debug("{}退出成功", user.getUsername());
            SpringUtil.getBean(SysLogService.class).save(user.getId(), "退出", true, null);

            SecurityUtils.getSubject().logout();

            return flag;
        } else {
            TokenManager tokenManager = SpringUtil.getBean(TokenManager.class);
            boolean flag = tokenManager.deleteToken(loginToken);
            if (flag) {
                RestfulFilter.writeResponse(WebUtils.toHttp(response), HttpStatus.OK.value(), SUCCESS_INFO);
                log.debug("{}退出成功", user.getUsername());
            } else {
                RestfulFilter.writeResponse(WebUtils.toHttp(response), HttpStatus.BAD_REQUEST.value(), ERR_INFO);
            }

            SpringUtil.getBean(SysLogService.class).save(user.getId(), "token方式退出", flag, null);

            SecurityUtils.getSubject().logout();

            return false;
        }
    }

    private static String SUCCESS_INFO = JSONObject.toJSONString(new ResponseInfo(HttpStatus.OK.value() + "", "退出成功"));
    private static String ERR_INFO = JSONObject
            .toJSONString(new ResponseInfo(HttpStatus.BAD_REQUEST.value() + "", "退出失败,token不存在"));

}
