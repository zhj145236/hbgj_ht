package com.yusheng.hbgj.utils;

import com.yusheng.hbgj.constants.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;


/**
 * @author 金伟 on 2019/6/19 0019.
 * @date 2020/1/20 20:03
 * @desc 数据库备份与还原
 */
@Service
public class MysqlDbService {


    //指明Mysql的Bin目录
    @Value("${constants.mysqlBinDir}")
    private String mysqlBinDir;

    @Value("${spring.datasource.username}")
    private String username;


    @Value("${spring.datasource.password}")
    private String password;

    //指明Mysql的Bin目录
    @Value("${constants.sqlBackDir}")
    private String sqlBackDir;


    private String dbName = "hbgj";

    private static final Logger log = LoggerFactory.getLogger("adminLogger");


    public void dbBackUp() {

        try {
            String filePath = this.dbBackUp(username, password, sqlBackDir);
            log.info("备份数据库文件成功。。{}", filePath);
        } catch (Exception e) {
            log.error("备份文件失败。。{}", sqlBackDir);
        }
    }

    public void dbRestore() {

        try {
            this.dbRestore(sqlBackDir);
            log.info("还原数据库库成功。。{}", sqlBackDir);
        } catch (Exception e) {
            log.error("还原数据库库失败。。{},{}", sqlBackDir, e.getMessage());
        }
    }


    /**
     * 备份数据库db
     *
     * @param root       账号
     * @param pwd        密码
     * @param sqlBackDir 文件放置的目录
     *                   脚本: mysqldump -hlocalhost -uroot -pjson@1109  hbgj > /home/king/kingdata/dbback/xxx.sql
     */
    private String dbBackUp(String root, String pwd, String sqlBackDir) throws Exception {

        long startA = System.currentTimeMillis();

        String pathSql = sqlBackDir + dbName + "_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".sql";


        File fileSql = new File(pathSql);
        //创建备份sql文件
        if (!fileSql.exists()) {
            fileSql.createNewFile();
        }

        StringBuilder sb = new StringBuilder();

        if (!StringUtils.isEmpty(mysqlBinDir)) {
            sb.append(mysqlBinDir);
        }

        sb.append("mysqldump  ");
        sb.append(" -h127.0.0.1");
        sb.append(" -u").append(root);
        sb.append(" -p").append(pwd);

        sb.append(" ").append(dbName).append(" > ");

        sb.append(pathSql);


        Runtime runtime = Runtime.getRuntime();


        if (SysUtil.isOSLinux()) {

            log.info("shell命令为：" + sb.toString());
            runtime.exec(new String[]{"/bin/sh", "-c", sb.toString()}, null, null);

        } else {
            log.info("cmd命令为：" + sb.toString());
            runtime.exec("cmd /c" + sb.toString());
        }


        log.info("备份数据库成功!执行耗时:" + (System.currentTimeMillis() - startA) + "ms");
        return pathSql;


    }

    /**
     * 恢复数据库
     *
     * @param filePath 文件路径
     *                 脚本: mysql -hlocalhost -uroot -pjson@1109 hbgj < /home/king/kingdata/dbback/xxx.sql
     */
    private void dbRestore(String filePath) {

        long startA = System.currentTimeMillis();

        StringBuilder sb = new StringBuilder();

        if (!StringUtils.isEmpty(mysqlBinDir)) {
            sb.append(mysqlBinDir);
        }


        sb.append("mysql");
        sb.append(" -h127.0.0.1");
        sb.append(" -u").append(username);
        sb.append(" -p").append(password);
        sb.append(" ").append(dbName).append(" <");

        String currentSql = this.getCurrentSqlFile(filePath);

        if (StringUtils.isEmpty(currentSql)) {
            log.error("不存在的sql备份文件， 数据库还原失败！{}", filePath);
            throw new BusinessException("不存在的sql备份文件， 数据库还原失败！");
        }

        sb.append(currentSql);


        Runtime runtime = Runtime.getRuntime();

        Process process;

        try {

            if (SysUtil.isOSLinux()) {

                process = runtime.exec(new String[]{"/bin/sh", "-c", sb.toString()}, null, null);

            } else {
                process = runtime.exec("cmd /c" + sb.toString());
            }


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


        log.info("还原成功！执行耗时:" + (System.currentTimeMillis() - startA) + "ms");
    }


    /**
     * 获取最新的sql备份文件
     * @param sqlBackDir 放置sql文件的目录
     * @return 最新的sql文件全路径
     */
    private String getCurrentSqlFile(String sqlBackDir) {
        File f = new File("D:\\download\\");
        if (f.isDirectory()) {

            File[] files = f.listFiles(new FileFilter() {
                @Override
                public boolean accept(File fi) {
                    // 要是sql文件，且大小超过20KB
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

            //按时间排序，第0个就是最新的
            return files[0].getAbsolutePath();

        } else {
            return null;

        }
    }


}