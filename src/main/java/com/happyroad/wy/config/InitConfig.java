package com.happyroad.wy.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 金伟 on 2019/6/19 0019.
 * @date 2020/1/20 20:58
 * @desc 从数据库加载配置信息，作为全局变量
 */
@Component
@Order(2)   //通过order值的大小来决定启动的顺序
public class InitConfig implements CommandLineRunner {


    @Autowired
    private JdbcTemplate jdbcTemplate;


    public static Map<String, String> globalConfig = new HashMap<>();

    @Override
    public void run(String... args) throws Exception {

        List<Map<String, Object>> maps = jdbcTemplate.queryForList("select  k,v  from sys_config");
        maps.forEach((map) -> {
            globalConfig.put((String) map.get("k"), (String) map.get("v"));
            System.out.println(map.toString());
        });

        System.out.println("配置初始化完毕。。。。。。。。。。。。。");
    }
}
