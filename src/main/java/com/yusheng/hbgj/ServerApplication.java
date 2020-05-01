package com.yusheng.hbgj;

import com.yusheng.hbgj.utils.SysUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * 启动类
 *
 * @author Jinwei
 * <p>
 * 2017年4月18日
 */
@SpringBootApplication
public class ServerApplication {


    private static final Logger log = LoggerFactory.getLogger("adminLogger");

    public static void main(String[] args) {

        long startA = System.currentTimeMillis();

        SpringApplication.run(ServerApplication.class, args);


        log.warn("{}", SysUtil.getMemInfo() + "\n启动耗时：" + ((System.currentTimeMillis() - startA) / 1000) + "s");

    }

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }


}
