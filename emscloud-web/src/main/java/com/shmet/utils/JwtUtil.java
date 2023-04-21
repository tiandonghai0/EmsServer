package com.shmet.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.shmet.entity.mysql.gen.AppUser;
import com.shmet.entity.mysql.gen.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class JwtUtil {

  public static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

  //设定12h后过期
  private static final long EXPIRE_TIME = 12 * 60 * 60 * 1000;
  //设定30天后过期
  private static final long APPUSER_EXPIRE_TIME = 30 * 24 * 60 * 60;

  public static String generateToken(User tbUser) {
    //过期时间
    Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
    return JWT.create()
        .withAudience(tbUser.getAccount(), tbUser.getName())
        .withExpiresAt(date)
        .sign(Algorithm.HMAC256(tbUser.getPassword()));
  }

  public static String generateAppUserToken(AppUser appUser) {
    //过期时间
    LocalDateTime localDateTime = LocalDateTime.now().plusSeconds(APPUSER_EXPIRE_TIME);
    Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    logger.info("date : {}", date);
    return JWT.create()
        .withClaim("phone", appUser.getPhone())
        .withExpiresAt(date)
        .sign(Algorithm.HMAC256(appUser.getPassword()));
  }
}