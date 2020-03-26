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
     * 字符串用...省略
     *
     * @return
     */
    public  static String elide(String str, int maxWord) {

        maxWord = maxWord <= 0 ? 10 : maxWord;

        if (StringUtils.isEmpty(str)) {
            return "";
        } else if (str.length() > maxWord) {

            return str.substring(0, maxWord)+"...";
        } else {
            return str;
        }

    }

    public static void main(String[] args) {

        String aa1 = null;
        String aa0 = "";

        String aa2 = "我很认同您的看法，要是能增加就更好了，是不是";
        String aa3 = "我很认同您";


        System.out.println(elide(aa0,10));
        System.out.println(elide(aa1,10));
        System.out.println(elide(aa2,10));
        System.out.println(elide(aa3,10));


    }

}
