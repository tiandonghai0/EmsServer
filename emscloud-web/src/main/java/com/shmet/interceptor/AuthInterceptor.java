package com.shmet.interceptor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.shmet.aop.PassToken;
import com.shmet.aop.UserLoginToken;
import com.shmet.entity.mysql.gen.User;
import com.shmet.exception.InvalidTokenException;
import com.shmet.exception.NotTokenException;
import com.shmet.exception.UserNotExistException;
import com.shmet.handler.api.BusinessDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author
 */
public class AuthInterceptor implements HandlerInterceptor {

  @Autowired
  private BusinessDataService businessDataService;

  @Override
  public boolean preHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler) {
    // 从 http 请求头中取出 token
    String token = request.getHeader("token");
    // 如果不是映射到方法直接通过
    if (!(handler instanceof HandlerMethod)) {
      return true;
    }
    HandlerMethod handlerMethod = (HandlerMethod) handler;
    Method method = handlerMethod.getMethod();
    //检查是否有passtoken注释，有则跳过认证
    if (method.isAnnotationPresent(PassToken.class)) {
      PassToken passToken = method.getAnnotation(PassToken.class);
      if (passToken.required()) {
        return true;
      }
    }
    if (method.isAnnotationPresent(UserLoginToken.class)) {
      UserLoginToken userLoginToken = method.getAnnotation(UserLoginToken.class);
      if (userLoginToken.required()) {
        // 执行认证
        if (token == null) {
          throw new NotTokenException("无token，请重新登录");
        }
        // 获取 token 中的 user id
        String account;
        try {
          account = JWT.decode(token).getAudience().get(0);
        } catch (JWTDecodeException j) {
          throw new InvalidTokenException("AuthInterceptor preHandle Jwt exception: " + j.getMessage());
        }
        User user = businessDataService.findByAccount(account);
        if (user == null) {
          throw new UserNotExistException("用户不存在，请重新登录");
        }
        /*
         验证 token 这里不需要捕获异常 直接扔给 GlobaleExceptionhandler 处理
          @see GlobaleExceptionhandler
         */
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(user.getPassword())).build();
        jwtVerifier.verify(token);
        return true;
      }
    }
    //检查有没有需要用户权限的注解
    return true;
  }



}
