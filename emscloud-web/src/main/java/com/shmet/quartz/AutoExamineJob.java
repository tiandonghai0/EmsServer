package com.shmet.quartz;

import com.shmet.handler.v2.AutoExamineService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

/**
 * @author
 */
@Component
@EnableScheduling
@DisallowConcurrentExecution
public class AutoExamineJob implements Job {
  private static final Logger log = LoggerFactory.getLogger(HongboPushRealDataJob.class);

  @Autowired
  private AutoExamineService autoExamineService;

  @Override
  public void execute(JobExecutionContext context) {
    try {
      autoExamineService.examine();
    } catch (Exception e) {
      log.error("宏博推送任务失败", e);
    }
  }
}
