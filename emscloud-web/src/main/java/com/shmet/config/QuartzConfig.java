package com.shmet.config;

import java.io.IOException;
import java.util.Properties;

import com.shmet.quartz.*;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class QuartzConfig {

  @Value("${quartz.cronschedule.pushrealdata}")
  private String pushRealDataCronSchedule;

  @Value("${quartz.cronschedule.alarm}")
  private String alarmCronSchedule;

  @Value("${quartz.cronschedule.teld}")
  private String teldCronSchedule;

  @Value("${quartz.cronschedule.activemq}")
  private String activemqCronSchedule;

  @Value("${quartz.cronschedule.baishu_data_provide}")
  private String baishuDataProvideCronSchedule;

  @Value("${quartz.cronschedule.check_fault}")
  private String checkFaultCronSchedule;

  @Value("${quartz.watermeter.task}")
  private String watermeterCronSchedule;

  @Value("${quartz.cronschedule.ehang365}")
  private String ehang365CronSchedule;

  @Value("${quartz.cronschedule.hong_bo}")
  private String hongBoSchedule;

  @Value("${quartz.cronschedule.auto_examine}")
  private String autoExamineSchedule;

  @Autowired
  private JobFactory myJobFactory;

  @Bean
  public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
    SchedulerFactoryBean factory = new SchedulerFactoryBean();
    factory.setOverwriteExistingJobs(true);
    // 延时启动
    factory.setStartupDelay(20);
    // 加载quartz数据源配置
    factory.setQuartzProperties(quartzProperties());
    // 自定义Job Factory，用于Spring注入
    factory.setJobFactory(myJobFactory);
    return factory;
  }

  @Bean
  public Properties quartzProperties() throws IOException {
    PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
    // propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
    propertiesFactoryBean.afterPropertiesSet();
    return propertiesFactoryBean.getObject();
  }

  @Bean
  public Scheduler quartzScheduler() throws IOException, SchedulerException {
    Scheduler scheduler = schedulerFactoryBean().getScheduler();
    startPushRealDataJob(scheduler);
    startAlarmJob(scheduler);
    startTeldJob(scheduler);
    //startActivemqJob(scheduler); Activemq没用了，先注释掉
    startBaishuDataProvideJob(scheduler);
    startCheckFaultJob(scheduler);
    //startWatermeterJob(scheduler);
    startHongboJob(scheduler);
    startAutoExaminejob(scheduler);
    startEhang365Job(scheduler);
    return scheduler;
  }

  private void startPushRealDataJob(Scheduler scheduler) throws SchedulerException {
    JobDetail jobDetail =
        JobBuilder.newJob(PushRealDataJob.class).withIdentity("pushRealDataJob", "group2").build();
    CronScheduleBuilder scheduleBuilder =
        CronScheduleBuilder.cronSchedule(pushRealDataCronSchedule);
    CronTrigger cronTrigger =
        TriggerBuilder.newTrigger().withIdentity("startPushRealDataTrigger", "group2")
            .withSchedule(scheduleBuilder).build();
    scheduler.scheduleJob(jobDetail, cronTrigger);
  }

  private void startAlarmJob(Scheduler scheduler) throws SchedulerException {
    JobDetail jobDetail =
        JobBuilder.newJob(AlarmJob.class).withIdentity("alarmJob", "group1").build();
    CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(alarmCronSchedule);
    CronTrigger cronTrigger =
        TriggerBuilder.newTrigger().withIdentity("startAlarmTrigger", "group1")
            .withSchedule(scheduleBuilder).build();
    scheduler.scheduleJob(jobDetail, cronTrigger);
  }

  private void startTeldJob(Scheduler scheduler) throws SchedulerException {
    JobDetail jobDetail =
        JobBuilder.newJob(TeldJob.class).withIdentity("teldJob", "group1").build();
    CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(teldCronSchedule);
    CronTrigger cronTrigger =
        TriggerBuilder.newTrigger().withIdentity("startTeldTrigger", "group1")
            .withSchedule(scheduleBuilder).build();
    scheduler.scheduleJob(jobDetail, cronTrigger);
  }

  private void startActivemqJob(Scheduler scheduler) throws SchedulerException {
    JobDetail jobDetail =
        JobBuilder.newJob(ActivemqJob.class).withIdentity("activemqJob", "group1").build();
    CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(activemqCronSchedule);
    CronTrigger cronTrigger =
        TriggerBuilder.newTrigger().withIdentity("startActivemqTrigger", "group1")
            .withSchedule(scheduleBuilder).build();
    scheduler.scheduleJob(jobDetail, cronTrigger);
  }

  private void startBaishuDataProvideJob(Scheduler scheduler) throws SchedulerException {
    JobDetail jobDetail =
        JobBuilder.newJob(BaishuDataProvideJob.class).withIdentity("baishuDataProvideJob", "group1").build();
    CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(baishuDataProvideCronSchedule);
    CronTrigger cronTrigger =
        TriggerBuilder.newTrigger().withIdentity("startBaishuDataProvideTrigger", "group1")
            .withSchedule(scheduleBuilder).build();
    scheduler.scheduleJob(jobDetail, cronTrigger);
  }

  private void startCheckFaultJob(Scheduler scheduler) throws SchedulerException {
    JobDetail jobDetail =
        JobBuilder.newJob(CheckFaultJob.class).withIdentity("checkFaultJob", "group1").build();
    CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(checkFaultCronSchedule);
    CronTrigger cronTrigger =
        TriggerBuilder.newTrigger().withIdentity("startCheckFaultTrigger", "group1")
            .withSchedule(scheduleBuilder).build();
    scheduler.scheduleJob(jobDetail, cronTrigger);
  }

//  private void startWatermeterJob(Scheduler scheduler) throws SchedulerException {
//    JobDetail jobDetail =
//        JobBuilder.newJob(WaterMeterJob.class).withIdentity("watermeterJob", "group1").build();
//    CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(watermeterCronSchedule);
//    CronTrigger cronTrigger =
//        TriggerBuilder.newTrigger().withIdentity("watermeterTrigger", "group1")
//            .withSchedule(scheduleBuilder).build();
//    scheduler.scheduleJob(jobDetail, cronTrigger);
//  }

  private void startEhang365Job(Scheduler scheduler) throws SchedulerException {
    JobDetail jobDetail =
        JobBuilder.newJob(Ehang365Job.class).withIdentity("ehang365Job", "group1").build();
    CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(ehang365CronSchedule);
    CronTrigger cronTrigger =
        TriggerBuilder.newTrigger().withIdentity("startEhang365Trigger", "group1")
            .withSchedule(scheduleBuilder).build();
    scheduler.scheduleJob(jobDetail, cronTrigger);
  }

  private void startHongboJob(Scheduler scheduler) throws SchedulerException {
    JobDetail jobDetail =
        JobBuilder.newJob(HongboPushRealDataJob.class).withIdentity("hongBoJob", "group1").build();
    CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(hongBoSchedule);
    CronTrigger cronTrigger =
        TriggerBuilder.newTrigger().withIdentity("hongBoTrigger", "group1")
            .withSchedule(scheduleBuilder).build();
    scheduler.scheduleJob(jobDetail, cronTrigger);
  }

  private void startAutoExaminejob(Scheduler scheduler) throws SchedulerException {
    JobDetail jobDetail =
        JobBuilder.newJob(AutoExamineJob.class).withIdentity("autoExamineJob", "group1").build();
    CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(autoExamineSchedule);
    CronTrigger cronTrigger =
        TriggerBuilder.newTrigger().withIdentity("autoExamineTrigger", "group1")
            .withSchedule(scheduleBuilder).build();
    scheduler.scheduleJob(jobDetail, cronTrigger);
  }

}
