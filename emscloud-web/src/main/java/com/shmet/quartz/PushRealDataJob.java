package com.shmet.quartz;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import com.corundumstudio.socketio.SocketIOServer;
import com.shmet.dao.redis.RealDataRedisDao;
import com.shmet.handler.PushRealDataHandler;

@Component
@EnableScheduling
@DisallowConcurrentExecution
public class PushRealDataJob implements Job {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  PushRealDataHandler pushRealDataHandler;

  @Autowired
  RealDataRedisDao redisCacheDao;

  @Autowired
  SocketIOServer socketIoServer;

  @Override
  public void execute(JobExecutionContext context) {
    pushRealDataHandler.broadcastRealData(redisCacheDao, socketIoServer);
  }
}
