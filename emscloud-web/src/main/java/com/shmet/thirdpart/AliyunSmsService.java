package com.shmet.thirdpart;

import cn.hutool.core.util.StrUtil;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;
import com.aliyun.teaopenapi.models.Config;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author
 */
@Service
public class AliyunSmsService {

  public static final Logger logger = LoggerFactory.getLogger(AliyunSmsService.class);

  @Autowired
  private AliyunSmsProp aliyunSmsProp;

  @Value("${registerTemplateCode}")
  private String registerTemplateCode;

  @Autowired
  private StringRedisTemplate stringRedisTemplate;

  public com.aliyun.dysmsapi20170525.Client createClient() throws Exception {
    Config config = new Config()
        // 您的AccessKey ID
        .setAccessKeyId(aliyunSmsProp.getAccessKeyId())
        // 您的AccessKey Secret
        .setAccessKeySecret(aliyunSmsProp.getAccessKeySecret());
    // 访问的域名
    config.endpoint = aliyunSmsProp.getEndpoint();
    return new com.aliyun.dysmsapi20170525.Client(config);
  }

  public SendSmsResponseBody sendRegisterSms(String phone) throws Exception {
    com.aliyun.dysmsapi20170525.Client client = createClient();
    String code = RandomStringUtils.random(4, "123456789");
    SendSmsRequest sendSmsRequest = new SendSmsRequest()
        .setPhoneNumbers(phone)
        .setSignName(aliyunSmsProp.getSignName())
        .setTemplateCode(registerTemplateCode)
        .setTemplateParam("{\"code\":" + code + "}");
    SendSmsResponse sendSmsResponse = client.sendSms(sendSmsRequest);
    if (sendSmsResponse != null && sendSmsResponse.getBody() != null) {
      if (StringUtils.equals(sendSmsResponse.getBody().code, "OK")) {
        //将验证码保存Redis
        stringRedisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);
      }
      return sendSmsResponse.getBody();
    }
    return null;
  }

  public boolean validateSmsCode(String phone, String code) {
    return StrUtil.equals(stringRedisTemplate.opsForValue().get(phone), code);
  }
}
