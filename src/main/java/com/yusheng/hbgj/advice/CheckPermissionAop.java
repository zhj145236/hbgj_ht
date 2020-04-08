package com.yusheng.hbgj.advice;

import com.yusheng.hbgj.annotation.LogAnnotation;
import com.yusheng.hbgj.annotation.PermissionTag;
import com.yusheng.hbgj.constants.UnauthorizedException;
import com.yusheng.hbgj.dto.ResponseInfo;
import com.yusheng.hbgj.entity.Permission;
import com.yusheng.hbgj.entity.SysLogs;
import com.yusheng.hbgj.service.SysLogService;
import com.yusheng.hbgj.utils.NetWorkUtil;
import com.yusheng.hbgj.utils.UserUtil2;
import io.swagger.annotations.ApiOperation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
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
public class CheckPermissionAop {


    private static final Logger log = LoggerFactory.getLogger("adminLogger");


    @Around(value = "@annotation(com.yusheng.hbgj.annotation.PermissionTag)")
    public Object logSave(ProceedingJoinPoint joinPoint) throws Throwable {


        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();


        Class<?> classTarget = joinPoint.getTarget().getClass();
        Class<?>[] par = ((MethodSignature) joinPoint.getSignature()).getParameterTypes();
        Method objMethod = classTarget.getMethod(methodName, par);

        PermissionTag permissionTag = objMethod.getAnnotation(PermissionTag.class);
        String[] permValues = permissionTag.value();

        List<Permission> permissions = UserUtil2.getCurrentPermissions();


        boolean isAllowed = false;

        for (int i = 0; i < permissions.size(); i++) {

            for (int j = 0; j < permValues.length; j++) {
                if (permissions.get(i).getPermission().equals(permValues[j].trim())) {
                    isAllowed = true;
                    break;
                }
            }


        }


        Object object = null;
        if (isAllowed) {
            object = joinPoint.proceed();
        } else {


            log.warn("没有权限查看。。。。。。。。。");

            throw new UnauthorizedException("您没有权限操作，请联系管理员");
        }

        return object;

    }


}
