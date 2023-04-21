package com.shmet;

import com.shmet.props.AccessLimitProp;
import com.shmet.props.FileLogoProp;
import com.shmet.thirdpart.AliyunSmsProp;
import com.shmet.thirdpart.EmsUuid;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
@EnableTransactionManagement
@MapperScan(basePackages = {"com.shmet.dao","com.shmet.dao.api"})
@EnableConfigurationProperties(value = {AliyunSmsProp.class, AccessLimitProp.class, FileLogoProp.class})
public class Startup {

  public static void main(String[] args) {
   // System.out.println("时间："+DateTimeUtils.dateLong2FString(20210115122236L,"yyyyMMddHHmmss"));
    TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
    new SpringApplicationBuilder(Startup.class).web(WebApplicationType.SERVLET).run(args);
  }

  @PostConstruct
  public void initUUId() {
    EmsUuid.getInstance().init(0, 0);
  }
}
