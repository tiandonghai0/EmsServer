package com.shmet.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author
 */
@Configuration
@MapperScan(basePackages = {"com.shmet.dao.api.**"})
public class MyBatisPlusConfig {
  //旧的 mp 分页插件配置
  @Bean
  public PaginationInterceptor mybatisPlusInterceptor() {
    return new PaginationInterceptor();
  }
}
