package com.shmet.quartz;

import com.shmet.handler.WaterMeterHandler;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@DisallowConcurrentExecution
public class WaterMeterJob implements Job {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    WaterMeterHandler waterMeterHandler;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            //配置是3分钟执行一次
            //logger.info("智能水表预充值与清零任务开始");
            //waterMeterHandler.waterMeterTaskJob();
            //logger.info("智能水表预充值与清零任务结束");
        } catch (Exception e) {
            logger.error("智能水表预充值与清零任务失败：", e);
        }
    }
}
