package com.yusheng.hbgj.any;

import com.yusheng.hbgj.utils.MathBaseUtil;

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

        //过滤得到sql 文件大小》10KB


        File f = new File("D:\\download\\");
        if (f.isDirectory()) {

            File[] files = f.listFiles(new FileFilter() {
                @Override
                public boolean accept(File fi) {
                    return fi.getName().endsWith(".sql") && !fi.isDirectory() && (fi.length() > 20 * 1024);
                }
            });

            Arrays.sort(files, new Comparator<File>() {
                public int compare(File f1, File f2) {
                    long diff = f1.lastModified() - f2.lastModified();
                    if (diff > 0)
                        return -1;
                    else if (diff == 0)
                        return 0;
                    else
                        return 1;//如果 if 中修改为 返回-1 同时此处修改为返回 1  排序就会是递减
                }

                public boolean equals(Object obj) {
                    return true;
                }

            });

            for (int i = 0; i < files.length; i++) {

                System.out.println(files[i].getName() + "<<<<<<,," + files[i].length());
            }


        }


    }


}
