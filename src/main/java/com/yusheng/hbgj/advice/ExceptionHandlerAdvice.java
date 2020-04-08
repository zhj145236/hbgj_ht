package com.yusheng.hbgj.advice;

import com.yusheng.hbgj.constants.BusinessException;
import com.yusheng.hbgj.constants.NotLoginException;
import com.yusheng.hbgj.constants.UnauthorizedException;
import com.yusheng.hbgj.dto.ResponseInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * springmvc异常处理
 *
 * @author Jinwei
 */

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    private static final Logger log = LoggerFactory.getLogger("adminLogger");

    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseInfo badRequestException(IllegalArgumentException exception) {

        log.warn("参数不正确,{}", exception.getMessage());
        return new ResponseInfo(HttpStatus.BAD_REQUEST.value() + "", exception.getMessage());
    }

    @ExceptionHandler({BusinessException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseInfo badRequestException(BusinessException exception) {

        log.warn("业务处理失败,{}", exception.getMessage());
        return new ResponseInfo(HttpStatus.BAD_REQUEST.value() + "", exception.getMessage());
    }


    /***
     * 未登录 或 登录过期
     * @param exception 异常
     * @return
     */
    @ExceptionHandler({NotLoginException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseInfo loginException(Exception exception) {
        log.warn("未登录或登录过期,{}", exception.getMessage());
        return new ResponseInfo(HttpStatus.UNAUTHORIZED.value() + "", exception.getMessage());
    }

    @ExceptionHandler({UnauthorizedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseInfo forbidden(Exception exception) {
        log.warn("系统拒绝处理,请检查角色与权限.{}", exception.getMessage());
        return new ResponseInfo(HttpStatus.FORBIDDEN.value() + "", "系统拒绝处理,请检查角色与权限。" + exception.getMessage());
    }

    @ExceptionHandler({MissingServletRequestParameterException.class, HttpMessageNotReadableException.class,
            UnsatisfiedServletRequestParameterException.class, MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseInfo badRequestException(Exception exception) {

        log.warn("无效的接口请求,{}", exception.getMessage());

        return new ResponseInfo(HttpStatus.BAD_REQUEST.value() + "", "无效的接口请求,请检查接口方式（POST|GET|PUT|DELETE）与参数类型（FORM|JSON）" + exception.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseInfo exception(Throwable throwable) {
        log.error("系统内部错误", throwable);
        return new ResponseInfo(HttpStatus.INTERNAL_SERVER_ERROR.value() + "", throwable.getMessage());

    }

}
