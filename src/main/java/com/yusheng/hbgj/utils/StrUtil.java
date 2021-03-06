package com.yusheng.hbgj.utils;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;
import org.springframework.util.StringUtils;

/**
 * 字符串转化工具类
 *
 * @author Jinwei
 */
public class StrUtil {

    /**
     * 字符串转为驼峰
     *
     * @param str
     * @return
     */
    public static String str2hump(String str) {
        StringBuffer buffer = new StringBuffer();
        if (str != null && str.length() > 0) {
            if (str.contains("_")) {
                String[] chars = str.split("_");
                int size = chars.length;
                if (size > 0) {
                    List<String> list = Lists.newArrayList();
                    for (String s : chars) {
                        if (s != null && s.trim().length() > 0) {
                            list.add(s);
                        }
                    }

                    size = list.size();
                    if (size > 0) {
                        buffer.append(list.get(0));
                        for (int i = 1; i < size; i++) {
                            String s = list.get(i);
                            buffer.append(s.substring(0, 1).toUpperCase());
                            if (s.length() > 1) {
                                buffer.append(s.substring(1));
                            }
                        }
                    }
                }
            } else {
                buffer.append(str);
            }
        }

        return buffer.toString();
    }

    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * @param length 随机数长度
     * @return 随机数
     */
    public static String random(int length) {


        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {

            sb.append((int) (Math.random() * 10));

        }
        return sb.toString();
    }


    /**
     * 字符串用...省略
     *
     * @return
     */
    public static String elide(String str, int maxWord) {

        maxWord = maxWord <= 0 ? 10 : maxWord;

        if (StringUtils.isEmpty(str)) {
            return "";
        } else if (str.length() > maxWord) {

            return str.substring(0, maxWord) + "...";
        } else {
            return str;
        }

    }



}
