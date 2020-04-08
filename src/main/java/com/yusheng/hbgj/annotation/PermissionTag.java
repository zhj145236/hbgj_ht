package com.yusheng.hbgj.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 金伟 on 2019/6/19 0019.
 * @date 2020-04-08 18:05
 * @desc 自定义权限标记
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PermissionTag {

    String[] value();

    Logical logical() default Logical.AND;
}

