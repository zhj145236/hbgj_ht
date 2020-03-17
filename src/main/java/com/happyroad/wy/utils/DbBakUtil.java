package com.happyroad.wy.utils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @author 金伟 on 2019/6/19 0019.
 * @date 2020/1/20 20:03
 * @desc 数据库备份与还原
 */
public class DbBakUtil {


    /***
     * 指明Mysql的Bin目录
     */
    private static String mysqlBinDir = "D:\\tools\\mysql-5.6.46-winx64\\bin\\";


    /**
     * 备份数据库db
     *
     * @param root
     * @param pwd
     * @param dbName
     * @param backPath mysqldump -hlocalhost -uroot -p123456 db > /home/back.sql
     */

    public static void dbBackUp(String root, String pwd, String dbName, String backPath) throws Exception {

        long startA = System.currentTimeMillis();

        String pathSql = backPath + dbName + "_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".sql";


        File fileSql = new File(pathSql);
        //创建备份sql文件
        if (!fileSql.exists()) {
            fileSql.createNewFile();
        }


        StringBuffer sb = new StringBuffer();
        sb.append(mysqlBinDir);
        sb.append("mysqldump  ");
        sb.append(" -h127.0.0.1");
        sb.append(" -u").append(root);
        sb.append(" -p").append(pwd);

        sb.append(" " + dbName + " > ");

        sb.append(pathSql);

        System.out.println("cmd命令为：" + sb.toString());
        Runtime runtime = Runtime.getRuntime();
        System.out.println("开始备份：" + dbName);
        Process process = runtime.exec("cmd /c" + sb.toString());
        System.out.println("备份成功!执行耗时:" + (System.currentTimeMillis() - startA) + "ms");
    }

    /**
     * 恢复数据库
     *
     * @param root
     * @param pwd
     * @param dbName
     * @param filePath mysql -hlocalhost -uroot -p123456 db < /home/back.sql
     */
    public static void dbRestore(String root, String pwd, String dbName, String filePath) {

        long startA = System.currentTimeMillis();

        StringBuilder sb = new StringBuilder();

        sb.append(mysqlBinDir);

        sb.append("mysql");
        sb.append(" -h127.0.0.1");
        sb.append(" -u" + root);
        sb.append(" -p" + pwd);
        sb.append(" " + dbName + " <");
        sb.append(filePath);
        System.out.println("cmd命令为：" + sb.toString());
        Runtime runtime = Runtime.getRuntime();
        System.out.println("开始还原数据");
        try {
            Process process = runtime.exec("cmd /c" + sb.toString());
            InputStream is = process.getInputStream();
            BufferedReader bf = new BufferedReader(new InputStreamReader(is, "utf8"));
            String line = null;
            while ((line = bf.readLine()) != null) {
                System.out.println(line);
            }
            is.close();
            bf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("还原成功！执行耗时:" + (System.currentTimeMillis() - startA) + "ms");
    }


    public static void main(String[] args) throws Exception {


        DbBakUtil.dbBackUp("root", "json@1109", "jira", "D:/");

        //DbBakUtil.dbRestore("root","json@1109","test","D:/test_20200120_202319.sql");
    }


}
