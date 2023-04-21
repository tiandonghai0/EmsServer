package com.shmet.quartz;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.shmet.handler.CheckFaultHandler;

@Component
@EnableScheduling
@DisallowConcurrentExecution
public class CheckFaultJob implements Job {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    CheckFaultHandler checkFaultHandler;

    @Override
    public void execute(JobExecutionContext context) {

        try {
            // 新奥 PCS  注释 kyle
            //checkFaultHandler.CheckFault(201900020010006L, "PDC");
            // 新奥 电池系统 注释 kyle
            //checkFaultHandler.CheckFault(201900020010007L, "D_P");
            // 新奥 光伏逆变器 注释 kyle
            //checkFaultHandler.CheckFault(201900020010008L, "TACTP");
            //checkFaultHandler.CheckFault(201900020010009L, "TACTP");

            // 柏树 PCS
            checkFaultHandler.CheckFault(201800010010041L, "DCPT");
            // 柏树 电池系统
            checkFaultHandler.CheckFault(201800010010043L, "D_P");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
