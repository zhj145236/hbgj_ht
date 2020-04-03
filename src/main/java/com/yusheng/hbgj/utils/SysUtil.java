package com.yusheng.hbgj.utils;

import com.yusheng.hbgj.config.InitConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @author 金伟 on 2019/6/19 0019.
 * @date 2020/1/20 21:07
 * @desc 系统工具类
 */
public class SysUtil {


    @Autowired
    private static JdbcTemplate jdbcTemplate;


    /***
     * 非空判断
     */
    public static boolean paramsIsNull(Object... obj) {

        for (int i = 0; i < obj.length; i++) {

            if (obj[i] == null) {
                return true;
            } else if (obj[i] instanceof String && StringUtils.isEmpty((String) obj[i])) {
                return true;

            }
        }
        return false;

    }


    /***
     * 加载最新配置信息
     */
    public static void reloadConfig() {

        InitConfig.globalConfig.clear();
        List<Map<String, Object>> maps = jdbcTemplate.queryForList("select  k,v  from sys_config");
        maps.forEach((map) -> {
            InitConfig.globalConfig.put((String) map.get("k"), (String) map.get("v"));
        });

    }
}
