package com.shmet.quartz;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import com.shmet.handler.TeldHandler;

@Component
@EnableScheduling
@DisallowConcurrentExecution
public class TeldJob implements Job {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    TeldHandler teldHandler;

    @Override
    public void execute(JobExecutionContext context) {
        try {
            teldHandler.queryStationsInfoData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
