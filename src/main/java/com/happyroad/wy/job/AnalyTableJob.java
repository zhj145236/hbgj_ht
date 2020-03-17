package com.happyroad.wy.job;


import com.happyroad.wy.dao.TableAnalyDao;
import com.happyroad.wy.entity.TableAnaly;
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
 * @desc 采用Spring内置的Schedule实现任务定时执行
 */
@Component
public class AnalyTableJob {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private final Logger logger = LoggerFactory.getLogger(AnalyTableJob.class);


    @Autowired
    private TableAnalyDao tableAnalyDao;

    /**
     * 每天3:10执行
     */
    @Scheduled(cron = "0 10 3 * * ? ")
     //@Scheduled(cron = "0/20 * *  * * ? ")
    private void task1() {


        List<TableAnaly> currentData = tableAnalyDao.getCurrenData();

        if (currentData.size() == 0) {
            logger.warn("无效的表数据！");
            return;
        }

        tableAnalyDao.insertBatch(currentData);
        logger.warn("每天3:10执行,采集数据成功,共有{}表，现在时间：{}", currentData.size(), dateFormat.format(new Date()));

    }


}
