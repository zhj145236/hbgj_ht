package com.yusheng.hbgj.utils;

import com.sun.management.OperatingSystemMXBean;
import com.yusheng.hbgj.config.InitConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import java.lang.management.ManagementFactory;
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

    public static String getMemInfo() {


        StringBuilder sb = new StringBuilder("\n=================================================\n");

        // 虚拟机级内存情况查询
        long vmFree, vmUse, vmTotal, vmMax;

        int byteToMb = 1024 * 1024;

        Runtime rt = Runtime.getRuntime();
        vmTotal = rt.totalMemory() / byteToMb;
        vmFree = rt.freeMemory() / byteToMb;
        vmMax = rt.maxMemory() / byteToMb;
        vmUse = vmTotal - vmFree;
        sb.append("JVM内存已用的空间为：").append(vmUse).append(" MB").append("\n");
        sb.append("JVM内存的可用空间为：").append(vmFree).append(" MB").append("\n");
        sb.append("JVM总内存空间为：").append(vmTotal).append(" MB").append("\n");
        sb.append("JVM最大内存空间为：").append(vmMax).append(" MB").append("\n");

        // 操作系统级内存情况查询
        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        String os = System.getProperty("os.name");
        long physicalFree = osmxb.getFreePhysicalMemorySize() / byteToMb;
        long physicalTotal = osmxb.getTotalPhysicalMemorySize() / byteToMb;
        long physicalUse = physicalTotal - physicalFree;
        sb.append("操作系统的版本：").append(os).append("\n");
        sb.append("操作系统物理内存空闲空间为：").append(physicalFree).append(" MB").append("\n");
        sb.append("操作系统物理内存已用空间为：").append(physicalUse).append(" MB").append("\n");
        sb.append("操作系统总物理内存：").append(physicalTotal).append(" MB").append("\n");

        // 获得线程总数
        ThreadGroup parentThread;
        int totalThread = 0;
        for (parentThread = Thread.currentThread().getThreadGroup(); parentThread
                .getParent() != null; parentThread = parentThread.getParent()) {
            totalThread = parentThread.activeCount();
        }
        sb.append("获得线程总数:").append(totalThread).append("\n");

        sb.append("启动时间:").append(DateUtil.getNowStr()).append("\n");

        sb.append("=================================================\n");

        return sb.toString();

    }
}
