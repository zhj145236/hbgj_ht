package com.yusheng.hbgj.utils.validator;

/**
 * @author 金伟 on 2019/6/19 0019.
 * @date 2019/10/12 20:09
 * @desc 实体验证器
 */

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * * 创建人: 金伟
 * * 描述: 校验对象
 * * 创建时间: 2019/6/5 0005 - 下午 7:08
 */
public class ValidatorUtil {

    /**
     * 可以在实体Bean上加的注解有:
     * JSR提供的校验注解：
     *
     * @Null 被注释的元素必须为 null
     * @NotNull 被注释的元素必须不为 null
     * @AssertTrue 被注释的元素必须为 true
     * @AssertFalse 被注释的元素必须为 false
     * @Min(value) 被注释的元素必须是一个数字，其值必须大于等于指定的最小值
     * @Max(value) 被注释的元素必须是一个数字，其值必须小于等于指定的最大值
     * @DecimalMin(value) 被注释的元素必须是一个数字，其值必须大于等于指定的最小值
     * @DecimalMax(value) 被注释的元素必须是一个数字，其值必须小于等于指定的最大值
     * @Size(max=, min=)   被注释的元素的大小必须在指定的范围内
     * @Digits (integer, fraction)     被注释的元素必须是一个数字，其值必须在可接受的范围内
     * @Past 被注释的元素必须是一个过去的日期
     * @Future 被注释的元素必须是一个将来的日期
     * @Pattern(regex=,flag=) 被注释的元素必须符合指定的正则表达式
     * <p>
     * <p>
     * Hibernate Validator提供的校验注解：
     * @NotBlank(message =)   验证字符串非null，且长度必须大于0
     * @Email 被注释的元素必须是电子邮箱地址
     * @Length(min=,max=) 被注释的字符串的大小必须在指定的范围内
     * @NotEmpty 被注释的字符串的必须非空
     * @Range(min=,max=,message=) 被注释的元素必须在合适的范围内
     * @param entity
     * @return
     */

    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();


    public static <T> ValidatorMessage validate(T obj) {

        ValidatorMessage massage = new ValidatorMessage();


        Map<String, StringBuilder> errorMap = null;
        Set<ConstraintViolation<T>> set = validator.validate(obj, Default.class);

        if (set != null && set.size() > 0) {
            massage.setLegal(false);

            errorMap = new HashMap<String, StringBuilder>(32);
            String property = null;
            for (ConstraintViolation<T> cv : set) {

                //这里循环获取错误信息，可以自定义格式
                property = cv.getPropertyPath().toString();
                if (errorMap.get(property) != null) {
                    errorMap.get(property).append(",").append(cv.getMessage());
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append(cv.getMessage());
                    errorMap.put(property, sb);
                }
            }
            massage.setMessage(mapToStr(errorMap));


        } else {
            massage.setLegal(true);

        }
        return massage;
    }

    private static String mapToStr(Map errorMap) {

        StringBuilder sb = new StringBuilder();
        Iterator<Map.Entry<String, StringBuilder>> iterator = errorMap.entrySet().iterator();

        int aaSize= errorMap.size();

        int i=0;

        while (iterator.hasNext()) {
            Map.Entry<String, StringBuilder> entry = iterator.next();
            i++;
            if(i>=aaSize){
                sb.append(entry.getValue());
            }else{
                sb.append(entry.getValue()).append("；");
            }
        }
            return sb.toString();
    }

}