package com.yusheng.hbgj.job;


import com.yusheng.hbgj.dao.TableAnalyDao;
import com.yusheng.hbgj.entity.SysLogs;
import com.yusheng.hbgj.entity.TableAnaly;
import com.yusheng.hbgj.service.SysLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author 金伟 on 2019/6/19 0019.
 * @date 2019/10/18 21:26
 * @desc 每天自动发送系统通知给用户
 */
@Component
public class AutoSendNoticeJob {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private final Logger logger = LoggerFactory.getLogger(AutoSendNoticeJob.class);


    @Autowired
    private SysLogService logService;



    /**
     * 每天8:30执行
     */
   // @Scheduled(cron = "0 30 8 * * ? ")
    @Scheduled(cron = "0 43 17 * * ? ")

    private void task() {


        //记录日志

        logService.save(-1L,"系统",true,"发送通知成功");

        System.out.println("哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈。。。。。");

        logger.warn("每天3:10执行,发送成功，现在时间：{}", dateFormat.format(new Date()));

    }


}
