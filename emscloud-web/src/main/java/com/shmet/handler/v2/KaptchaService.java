package com.shmet.handler.v2;

import com.google.code.kaptcha.Producer;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class KaptchaService {

  private final Producer producer;
  private final StringRedisTemplate stringRedisTemplate;

  public BufferedImage getCaptcha() {
    //生成文字验证码
    String code = producer.createText();

    //5分钟后过期
    stringRedisTemplate.opsForValue().set("IMGCODE_" + code.toUpperCase(), code, 5, TimeUnit.MINUTES);

    return producer.createImage(code);
  }

  public boolean validate(String code) {
    String redisCode = stringRedisTemplate.opsForValue().get("IMGCODE_" + code);
    return StringUtils.isNotBlank(redisCode);
  }

  public String getBase64ByteStr() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ImageIO.write(getCaptcha(), "png", baos);

    String s = Base64.getEncoder().encodeToString(baos.toByteArray());
    s = s.replaceAll("\n", "")
        .replaceAll("\r", "");

    return "data:image/jpg;base64," + s;

  }
}