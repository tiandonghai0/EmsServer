package com.shmet.interceptor;

import com.shmet.aop.AccessLimit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

@Component
public class AccessLimitInterceptor implements HandlerInterceptor {

  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  @Override
  public boolean preHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler) throws Exception {
    if (handler instanceof HandlerMethod) {
      HandlerMethod hm = (HandlerMethod) handler;
      final AccessLimit aclAnno = hm.getMethodAnnotation(AccessLimit.class);

      //没有直接放行
      if (aclAnno == null) {
        return true;
      }

      final int seconds = aclAnno.seconds();
      final int maxCounts = aclAnno.maxCounts();

      final String ip = request.getRemoteAddr();
      final String reqUri = request.getRequestURI();

      String key = ip + reqUri;

      Integer count = (Integer) redisTemplate.opsForValue().get(key);

      if (count == null) {
        redisTemplate.opsForValue().set(key, 1);
        redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
        return true;
      }

      if (count <= maxCounts) {
        redisTemplate.opsForValue().increment(key, 1);
      }

      if (count > maxCounts) {
        response.setContentType("text/plain;charset=utf-8");
        response.getWriter().write("请求过于频繁,请稍后再试");
        return false;
      }

    }
    return true;
  }
}