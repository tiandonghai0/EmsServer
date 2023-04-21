package com.shmet.handler;

import java.util.Date;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;

import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.ClasspathResourceLoader;
import org.beetl.ext.spring.BeetlGroupUtilConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.shmet.bean.RealDataForCheckFaultBean;
import com.shmet.bean.RealDataItem;
import com.shmet.dao.redis.RealDataRedisDao;

@Service
public class CheckFaultHandler {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Value("${email.from}")
  private String from;

  private @Value("${email.template.checkfault}")
  String templateFile;


  @Autowired
  private JavaMailSender mailSender;

  @Autowired
  RealDataRedisDao redisCacheDao;

  @Resource
  private BeetlGroupUtilConfiguration beetlConfig;

  public RealDataItem getRealDataItem(Long deviceNo, String tagCode) {

    Object realData = redisCacheDao.getRealData(deviceNo, tagCode);

    if (realData != null) {
      return (RealDataItem) realData;
    }
    return null;

  }

  public RealDataForCheckFaultBean getRealDataForCheckFaultBean(Long deviceNo, String tagCode) {

    Object realDataForCheckFault = redisCacheDao.getRealDataForCheckFault(deviceNo, tagCode);

    if (realDataForCheckFault != null) {
      return (RealDataForCheckFaultBean) realDataForCheckFault;
    }
    return null;
  }

  public void mail(Long deviceNo, String tagCode) {
    try {
      GroupTemplate tmp = beetlConfig.getGroupTemplate();
      Template t = tmp.getTemplate(templateFile, new ClasspathResourceLoader());
      t.binding("contactName", "项目管理员");
      if (deviceNo == 201900020010006L || deviceNo == 201900020010007L || deviceNo == 201900020010008L ||
          deviceNo == 201900020010009L) {
        t.binding("title", "新奥故障掉线报警");
        t.binding("projectName", "新奥项目");
      } else if (deviceNo == 201800010010041L || deviceNo == 201800010010043L) {
        t.binding("title", "柏树故障掉线报警");
        t.binding("projectName", "柏树项目");
      }

      if (deviceNo == 201900020010006L || deviceNo == 201800010010041L) {
        t.binding("deviceName", "PCS");
      } else if (deviceNo == 201900020010007L || deviceNo == 201800010010043L) {
        t.binding("deviceName", "电池系统");
      } else if (deviceNo == 201900020010008L || deviceNo == 201900020010009L) {
        t.binding("deviceName", "光伏逆变器");
      }

      switch (tagCode) {
        case "PDC":
          t.binding("tagName", "直流功率");
          break;
        case "D_P":
          t.binding("tagName", "功率");
          break;
        case "TACTP":
          t.binding("tagName", "总有功功率");
          break;
        case "DCPT":
          t.binding("tagName", "总直流功率");
          break;
      }

      t.binding("deviceNo", deviceNo);
      t.binding("tagCode", tagCode);
      String content = t.render();

      MimeMessage mimeMessage = mailSender.createMimeMessage();
      MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
      messageHelper.setFrom(from);
      messageHelper.setTo("tiandh@jz-energy.com");
      messageHelper.setCc("1194835307@qq.com");
      messageHelper.setSubject("故障报警邮件");
      // true 表示启动HTML格式的邮件
      messageHelper.setText(content, true);
      try {
        mailSender.send(mimeMessage);
      } catch (MailException e) {
        logger.error("邮件发送异常: {}", e.getMessage());
      }

      //logger.warn("发送监测报警邮件成功，发送至：liqunzhang@yeah.net; 邮件内容： " + content);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  /**
   * 监测报警
   */
  public void CheckFault(Long deviceNo, String tagCode) {

    RealDataItem realDataItem = this.getRealDataItem(deviceNo, tagCode);
    RealDataForCheckFaultBean realDataForCheckFaultBean = this.getRealDataForCheckFaultBean(deviceNo, tagCode);

    if (realDataItem == null || realDataForCheckFaultBean == null) {

      if (realDataItem != null) {
        RealDataForCheckFaultBean realDataForCheckFaultBean2 = new RealDataForCheckFaultBean();
        realDataForCheckFaultBean2.setRealDataItem(realDataItem);
        Date date = new Date();
        realDataForCheckFaultBean2.setDate(date);
        redisCacheDao.saveRealDataForCheckFault(deviceNo, tagCode, realDataForCheckFaultBean2);
      }

    } else {
      String dateTime = realDataItem.getDateTime();
      RealDataItem realDataItem2 = realDataForCheckFaultBean.getRealDataItem();
      String dateTime2 = realDataItem2.getDateTime();

      if (dateTime.equals(dateTime2)) {
        Date date = new Date();
        long time = date.getTime();
        Date date2 = realDataForCheckFaultBean.getDate();
        long time2 = date2.getTime();
        if ((time - time2) / 1000 > 1800 || (time - time2) / 1000 == 1800) {
          Date mailDate = realDataForCheckFaultBean.getMailDate();
          if (mailDate == null) {
            // 发邮件及更新邮件发送时间
            this.mail(deviceNo, tagCode);

            Date date3 = new Date();
            realDataForCheckFaultBean.setMailDate(date3);
            redisCacheDao.saveRealDataForCheckFault(deviceNo, tagCode, realDataForCheckFaultBean);
          } else {
            Date date4 = new Date();
            long time4 = date4.getTime();
            long time5 = mailDate.getTime();
            if ((time4 - time5) / 1000 > 1800 || (time4 - time5) / 1000 == 1800) {
              // 发邮件及更新邮件发送时间
              this.mail(deviceNo, tagCode);
              realDataForCheckFaultBean.setMailDate(date4);
              redisCacheDao.saveRealDataForCheckFault(deviceNo, tagCode, realDataForCheckFaultBean);
            }
          }
        }
      } else {
        RealDataForCheckFaultBean realDataForCheckFaultBean2 = new RealDataForCheckFaultBean();
        realDataForCheckFaultBean2.setRealDataItem(realDataItem);
        Date date = new Date();
        realDataForCheckFaultBean2.setDate(date);
        redisCacheDao.saveRealDataForCheckFault(deviceNo, tagCode, realDataForCheckFaultBean2);
      }

    }

  }
}
