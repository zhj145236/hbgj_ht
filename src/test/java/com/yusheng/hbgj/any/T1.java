package com.yusheng.hbgj.any;

import com.yusheng.hbgj.utils.MathBaseUtil;

/**
 * @author 金伟 on 2019/6/19 0019.
 * @date 2020/1/18 19:18
 * @desc 随便测试
 */
public class T1 {


    public static void main(String[] args) {

        int expire=25920000;
        //2147483647
        //25920000000

        System.out.println(expire * 1000);
        if( expire != -1 && (expire * 1000) < 2592000000L){

            System.out.println("11111111111");
        }else{
            System.out.println("00000000000000000000");
        }

    }


}
