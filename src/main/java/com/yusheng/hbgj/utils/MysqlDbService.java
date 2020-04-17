package com.yusheng.hbgj.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.text.SimpleDateFormat;
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

        log.info("cmd命令为：" + sb.toString());
        Runtime runtime = Runtime.getRuntime();
        log.info("开始备份：" + dbName);
        Process process = runtime.exec("cmd /c" + sb.toString());
        log.info("备份成功!执行耗时:" + (System.currentTimeMillis() - startA) + "ms");
        return pathSql;


    }

    /**
     * 恢复数据库
     *
     * @param root     账号
     * @param pwd      密码
     * @param filePath 文件路径
     *                 脚本: mysql -hlocalhost -uroot -pjson@1109 hbgj < /home/king/kingdata/dbback/xxx.sql
     */
    private void dbRestore(String root, String pwd, String filePath) {

        long startA = System.currentTimeMillis();

        StringBuilder sb = new StringBuilder();

        sb.append(mysqlBinDir);

        sb.append("mysql");
        sb.append(" -h127.0.0.1");
        sb.append(" -u").append(root);
        sb.append(" -p").append(pwd);
        sb.append(" ").append(dbName).append(" <");
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
        log.info("还原成功！执行耗时:" + (System.currentTimeMillis() - startA) + "ms");
    }


    public static void main(String[] args) throws Exception {


        // DbBakUtil.dbBackUp("root", "json@1109", "jira", "D:/");

        //DbBakUtil.dbRestore("root","json@1109","test","D:/test_20200120_202319.sql");
    }


}
