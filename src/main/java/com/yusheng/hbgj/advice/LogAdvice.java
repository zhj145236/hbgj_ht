package com.yusheng.hbgj.advice;

import com.yusheng.hbgj.dto.ResponseInfo;
import com.yusheng.hbgj.entity.SysLogs;
import com.yusheng.hbgj.annotation.LogAnnotation;
import com.yusheng.hbgj.entity.User;
import com.yusheng.hbgj.service.SysLogService;
import com.yusheng.hbgj.utils.NetWorkUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 统一日志处理
 *
 * @author Jinwei
 * <p>
 * 2017年8月19日
 */
@Aspect
@Component
public class LogAdvice {

    @Autowired
    private SysLogService logService;

    @Around(value = "@annotation(com.yusheng.hbgj.annotation.LogAnnotation)")
    public Object logSave(ProceedingJoinPoint joinPoint) throws Throwable {

        SysLogs sysLogs = new SysLogs();

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        String module = null;
        LogAnnotation logAnnotation = methodSignature.getMethod().getDeclaredAnnotation(LogAnnotation.class);
        module = logAnnotation.module();

        if (StringUtils.isEmpty(module)) {

            ApiOperation apiOperation = methodSignature.getMethod().getDeclaredAnnotation(ApiOperation.class);
            if (apiOperation != null) {
                module = apiOperation.value();
            } else {
                module = "没有指定日志module";
            }
        }
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        sysLogs.setIp(NetWorkUtil.getIpAddress(request));


        sysLogs.setParam(getParam(request));

        sysLogs.setModule(module);

        try {
            Object object = joinPoint.proceed();

            sysLogs.setFlag(true);
            if(object instanceof ResponseInfo && ((ResponseInfo)object).getData()!=null && ((ResponseInfo)object).getData().get("user")!=null) {

               User user= (User) ((ResponseInfo)object).getData().get("user");

                logService.saveRestfulLogin(user,sysLogs);
            }else{
                logService.save(sysLogs);
            }


            return object;
        } catch (Exception e) {
            sysLogs.setFlag(false);
            sysLogs.setRemark(e.getMessage());
            logService.save(sysLogs);
            throw e;
        }

    }


    private String getParam(HttpServletRequest request) {

        StringBuffer sb = new StringBuffer();
        Map map = request.getParameterMap();
        Set keSet = map.entrySet();
        for (Iterator itr = keSet.iterator(); itr.hasNext(); ) {
            Map.Entry me = (Map.Entry) itr.next();
            Object ok = me.getKey();
            Object ov = me.getValue();
            String[] value = new String[1];
            if (ov instanceof String[]) {
                value = (String[]) ov;
            } else {
                value[0] = ov.toString();
            }

            for (int k = 0; k < value.length; k++) {

                sb.append(ok).append("=").append(value[k]).append("&");
            }
        }
        return sb.toString();

    }




}
