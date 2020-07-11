package com.yusheng.hbgj.any;

import com.ulisesbocchio.jasyptspringboot.encryptor.DefaultLazyEncryptor;
import com.yusheng.hbgj.utils.MathBaseUtil;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.core.env.StandardEnvironment;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;

/**
 * @author 金伟 on 2019/6/19 0019.
 * @date 2020/1/18 19:18
 * @desc 随便测试
 */
public class T1 {


    public static void main(String[] args) {


        // 加密用的密码（盐值）见 有道云笔记中
        System.setProperty("jasypt.encryptor.password", "LoveMe520@Girl");
        StringEncryptor stringEncryptor = new DefaultLazyEncryptor(new StandardEnvironment());


        String mysqlOriginPswd = "json@1109";
        String redisOriginPswd = "hbgj@Redis#2020$";

        //要加密的数据（数据库的用户名或密码）
        String A = stringEncryptor.encrypt(mysqlOriginPswd);
        String B = stringEncryptor.encrypt(redisOriginPswd);
        // 注意：每次生成的密码是不一样的, 但是通过密钥,可以解密成一样的明文.
        System.out.println("Mysql加密:" + A);
        System.out.println("Redis加密:" + B);


        String PASSA = stringEncryptor.decrypt("/ST/BTZ+BtLm/kdWVBMXlfL5rPVl5NgYXwJL3PSqZ7AIei4IEEGbkoFzejPW01I9");
        String PASSB = stringEncryptor.decrypt("XaemAvJovXdExUlVkxC7bcKgpPJQXlW1EH3N+Q24Kgr5i0LjpNRbHJlBa5vF6FvmIn5dc9tLfANrHdhuy4HdMg==");

        System.out.println("密码还原：" + PASSA + "\n" + PASSB);


    }


}
