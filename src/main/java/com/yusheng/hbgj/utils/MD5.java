package com.yusheng.hbgj.utils;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author 金伟 on 2019/6/19 0019.
 * @date 2019/10/19 14:51
 * @desc MD5加密工具类
 */

public class MD5 {

    public static String getMd5(String plainTex) {

        return getMd5(plainTex, 32);

    }

    public static String getMd5(String plainText, int length) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            md.update(plainText.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer();
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }
            if (length == 32) {
                //32位加密
                result = buf.toString();
            } else if (length == 16) {
                //16位加密
                result = buf.toString().substring(8, 24);
            } else if (length == 6) {

                result = buf.toString().substring(0, 6);
            } else {
                return "";
            }
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result;
    }


}

