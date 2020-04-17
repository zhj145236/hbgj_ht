package com.yusheng.hbgj.any;

import com.yusheng.hbgj.utils.MathBaseUtil;

/**
 * @author 金伟 on 2019/6/19 0019.
 * @date 2020/1/18 19:18
 * @desc 随便测试
 */
public class T1 {


    public static void main(String[] args) {

        System.out.println("1111111111111..........");
        System.out.println("1111111111111..........");
        System.out.println("1111111111111..........");
        System.out.println("1111111111111..........");

        Runnable runnable=new Runnable() {

            @Override
            public void run() {

                System.out.println("i am new Runnable(){xxx},启动了");

            }
        };
        Thread thread=new Thread(runnable);
        thread.start();

        System.out.println("2222222222222..........");
        System.out.println("2222222222222..........");
        System.out.println("2222222222222..........");
    }


}
