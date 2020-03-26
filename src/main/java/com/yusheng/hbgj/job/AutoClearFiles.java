package com.yusheng.hbgj.job;

import com.yusheng.hbgj.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author 金伟 on 2019/6/19 0019.
 * @date 2020/3/26 11:12
 * @desc 自动清除没有被引用的文件
 */

@Component
public class AutoClearFiles {


    private final Logger logger = LoggerFactory.getLogger(AnalyTableJob.class);


    public void doTask() {


        System.err.println("自动删除文件。。。。，现在时间：" + DateUtil.getNowStr());
        logger.warn("自动删除文件。。。。，现在时间：{}", DateUtil.getNowStr());

    }

}
