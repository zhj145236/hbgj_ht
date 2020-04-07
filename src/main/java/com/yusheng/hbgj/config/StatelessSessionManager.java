package com.yusheng.hbgj.config;

import com.yusheng.hbgj.filter.RestfulFilter;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.Serializable;

/**
 * @author 金伟 on 2019/6/19 0019.
 * @date 2020-04-07 16:00
 * @desc
 */
public class StatelessSessionManager extends DefaultWebSessionManager {
    /**
     * 这个是服务端要返回给客户端，
     */
    public final static String TOKEN_NAME = "TOKEN";
    /**
     * 这个是客户端请求给服务端带的header
     */
    public final static String HEADER_TOKEN_NAME = "token";


    private static final Logger log = LoggerFactory.getLogger("adminLogger");

    private static final String REFERENCED_SESSION_ID_SOURCE = "Stateless request";

    /**
     * 重写getSessionId,分析请求头中的指定参数，做用户凭证sessionId
     */
    @Override
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {

        // String id = WebUtils.toHttp(request).getHeader(HEADER_TOKEN_NAME);

        String id = RestfulFilter.getToken(request);

        System.out.println("id："+id);

        if (StringUtils.isEmpty(id)) {
            //如果没有携带id参数则按照父类的方式在cookie进行获取
            System.out.println("super：" + super.getSessionId(request, response));
            return super.getSessionId(request, response);
        } else {

            //如果请求头中有 authToken 则其值为sessionId
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, REFERENCED_SESSION_ID_SOURCE);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, id);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);

            return id;
        }
    }
}
