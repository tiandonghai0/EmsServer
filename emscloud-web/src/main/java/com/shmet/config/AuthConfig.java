package com.shmet.config;

import com.shmet.interceptor.AccessLimitInterceptor;
import com.shmet.interceptor.AuthInterceptor;
import com.shmet.props.AccessLimitProp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author
 * 添加拦截器校验
 */
@Configuration
public class AuthConfig implements WebMvcConfigurer {

  @Autowired
  private AccessLimitProp accessLimitProp;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(authInterceptor())
//        .addPathPatterns("/api/listElecGridStatic"/**, "/api/listHistoryData",
//            "/api/listAlarmData", "/api/incomeHourStatis", "/v2/**", "/api/ehang/**"**/);
            .addPathPatterns("/**");
    registry.addInterceptor(accessLimitInterceptor())
        .addPathPatterns(accessLimitProp.getUrls());
  }

  @Bean
  @ConditionalOnMissingBean(AuthInterceptor.class)
  public AuthInterceptor authInterceptor() {
    return new AuthInterceptor();
  }

  @Bean
  @ConditionalOnMissingBean(AccessLimitInterceptor.class)
  public AccessLimitInterceptor accessLimitInterceptor() {
    return new AccessLimitInterceptor();
  }
}
