package com.yusheng.hbgj.advice;

import com.yusheng.hbgj.annotation.PermissionTag;
import com.yusheng.hbgj.constants.UnauthorizedException;
import com.yusheng.hbgj.entity.Permission;
import com.yusheng.hbgj.utils.UserUtil2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.List;

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

        int size = permissions.size();
        for (int i = 0; i < size; i++) {


            for (int j = 0; j < permValues.length; j++) {
                String pp = permissions.get(i).getPermission();
                if (pp != null && pp.equals(permValues[j].trim())) {
                    isAllowed = true;
                    break;
                }
            }


        }


        Object object = null;
        if (isAllowed) {
            object = joinPoint.proceed();
        } else {


            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < permValues.length; i++) {

                sb.append(permValues[i].toString());
            }

            log.warn("没有权限查看。。。。。。。。。{}", sb.toString());
            throw new UnauthorizedException("您没有权限操作，请联系管理员，需要权限：" + sb.toString());
        }

        return object;

    }


}
