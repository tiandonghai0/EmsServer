package com.shmet.quartz;

import java.util.Date;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.ClasspathResourceLoader;
import org.beetl.ext.spring.BeetlGroupUtilConfiguration;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import com.shmet.DateTimeUtils;
import com.shmet.bean.MailDataBean;
import com.shmet.bean.RealDataItem;
import com.shmet.dao.redis.MailRedisDao;
import com.shmet.dao.redis.RealDataRedisDao;

@Component
@EnableScheduling
@DisallowConcurrentExecution
public class AlarmJob implements Job {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    RealDataRedisDao redisCacheDao;

    @Autowired
    MailRedisDao mailRedisDao;

    private @Value("${email.from}")
    String from;

    @Value("${email.template.thresholdalarm}")
    private String templateFile;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    @Qualifier("beetlConfig")
    private BeetlGroupUtilConfiguration beetlConfig;

    //去掉新奥邮件发送
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

    }


    public void executePro(JobExecutionContext context) throws JobExecutionException {
        Long deviceNo = 201800020010018L;
        String tagCode = "WL";
        try {
            Date curDate = new Date();
            RealDataItem realDataItem = (RealDataItem) redisCacheDao.getRealData(deviceNo, tagCode);
            if (realDataItem != null) {
                double curVal = Double.parseDouble(realDataItem.getData().toString());
                if (curVal < 1.4) {
                    boolean needSend = false;
                    MailDataBean mailData = (MailDataBean) mailRedisDao.getMailCache(201800020010018L, tagCode);
                    if (mailData != null) {
                        long datediffsecond = DateTimeUtils.differFromSecond(mailData.getSendDate(), curDate);
                        if (datediffsecond > 3600) {
                            needSend = true;
                        }
                    } else {
                        needSend = true;
                    }
                    if (needSend) {
                        GroupTemplate tmp = beetlConfig.getGroupTemplate();
                        Template t = tmp.getTemplate(templateFile, new ClasspathResourceLoader());
                        t.binding("title", "金叶子项目混合水箱低水位报警");
                        t.binding("contactName", "金叶子项目管理员");
                        t.binding("projectName", "金叶子项目");
                        t.binding("deviceId", 18);
                        t.binding("deviceName", "混合水箱");
                        t.binding("tagName", "水位");
                        t.binding("thresholdValue", "1.4米");
                        t.binding("value", curVal + "米");
                        t.binding("timestamp", realDataItem.getDateTime());
                        String content = t.render();

                        MimeMessage mimeMessage = mailSender.createMimeMessage();
                        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
                        messageHelper.setFrom(from);
                        messageHelper.setTo("191144254@qq.com");
                        messageHelper.setCc("76466280@qq.com");
                        messageHelper.setSubject("金叶子项目混合水箱低水位报警");
                        // true 表示启动HTML格式的邮件
                        messageHelper.setText(content, true);
                        mailSender.send(mimeMessage);

                        logger.warn("发送超限报警邮件成功，发送至：191144254@qq.com; 邮件内容： " + content);
                        MailDataBean mailData2 = new MailDataBean();
                        mailData2.setSendDate(curDate);
                        mailRedisDao.saveMailCache(deviceNo, tagCode, mailData2);
                    }
                }

            }
        } catch (MessagingException e) {
            logger.debug("邮件发送发生错误", e);
        }
    }
}
