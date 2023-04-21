package com.shmet.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;

@Configuration
public class SocketIOConfig {

  @Value("${socketio.port}")
  int port;

  @Bean
  public SocketIOServer socketIOServer() {
    /*
     * 创建Socket，并设置监听端口
     */
    com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
    // 设置主机名，默认是0.0.0.0
    config.setHostname("0.0.0.0");
    // 设置监听端口
    config.setPort(port);
    // 协议升级超时时间（毫秒），默认10000。HTTP握手升级为ws协议超时时间
    config.setUpgradeTimeout(10000);
    // Ping消息间隔（毫秒），默认25000。客户端向服务器发送一条心跳消息间隔
    config.setPingInterval(60000);
    // Ping消息超时时间（毫秒），默认60000，这个时间间隔内没有接收到心跳消息就会发送超时事件
    config.setPingTimeout(180000);
    // 这个版本0.9.0不能处理好namespace和query参数的问题。所以为了做认证必须使用全局默认命名空间
    config.setAuthorizationListener(data -> {
      // 可以使用如下代码获取用户密码信息
      // MD5盐
      // 如果认证不通过会返回一个Socket.EVENT_CONNECT_ERROR事件
      return true;
    });

    return new SocketIOServer(config);
  }

  @Bean
  public SpringAnnotationScanner springAnnotationScanner(SocketIOServer socketServer) {
    return new SpringAnnotationScanner(socketServer);
  }

}
