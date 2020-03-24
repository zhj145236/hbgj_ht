package com.yusheng.hbgj.job;


import com.yusheng.hbgj.dao.NoticeDao;
import com.yusheng.hbgj.dao.TableAnalyDao;
import com.yusheng.hbgj.entity.SysLogs;
import com.yusheng.hbgj.entity.TableAnaly;
import com.yusheng.hbgj.service.SysLogService;
import com.yusheng.hbgj.utils.DateUtil;
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


    private final Logger logger = LoggerFactory.getLogger(AutoSendNoticeJob.class);


    @Autowired
    private SysLogService logService;

    @Autowired
    private NoticeDao noticeDao;


    /**
     * 每天8:30执行
     */
    @Scheduled(cron = "0 30 8 * * ? ")
    private void task() {


        // 自动发布
        noticeDao.autoPublish();

        //记录日志
        logService.save(-1L, "系统", true, "自动将一些草稿通知变成发布状态");

        logger.warn("每天08:30 自动将一些草稿通知变成发布状态，现在时间：{}", DateUtil.getNowStr());

    }


}
