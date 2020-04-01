package com.yusheng.hbgj.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author 金伟 on 2019/6/19 0019.
 * @date 2020-04-01 11:43
 * @desc Redis连接配置实体类
 */

@Component
@ConfigurationProperties("spring.redis")
public class RedisYml {


    private String host;


    private String port;


    private String password;


    private Integer database;


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getDatabase() {
        return database;
    }

    public void setDatabase(Integer database) {
        this.database = database;
    }
}
