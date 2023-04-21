package com.shmet.aop;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shmet.dao.UserMapper;
import com.shmet.entity.mongo.OperationLog;
import com.shmet.entity.mysql.gen.User;
import com.shmet.exception.InvalidTokenException;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * @author
 */
//@Aspect
//@Component
public class OperationLogAspect {

  @Autowired
  private MongoTemplate mongoTemplate;

  @Resource
  private UserMapper userMapper;

  public OperationLogAspect() {
  }

  //@Pointcut("@annotation(com.shmet.aop.OperationLogAnnotation)")
  public void opLogPoinCut() {

  }

  /**
   * 记录操作日志
   *
   * @param joinPoint 方法的执行点
   */
  //@Around(value = "opLogPoinCut()")
  public Object saveOperLog(ProceedingJoinPoint joinPoint) throws Throwable {
    // 获取RequestAttributes
    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
    // 从获取RequestAttributes中获取HttpServletRequest的信息
    if (requestAttributes != null) {
      HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
      try {
        OperationLog operationLog = new OperationLog();
        // 从切面织入点处通过反射机制获取织入点处的方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //获取切入点所在的方法
        Method method = signature.getMethod();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        StringBuilder sb = new StringBuilder();
        if (parameterNames != null && parameterNames.length > 0 && args != null && args.length > 0) {
          for (int i = 0; i < parameterNames.length; i++) {
            sb.append(parameterNames[i]).append(":").append(args[i]).append(",");
          }
        }
        //sb.delete(sb.length() - 1, sb.length() + 1);
        String param = sb.toString();
        operationLog.setContent(param);
        //获取操作日志类型
        OperationLogAnnotation annotation = method.getAnnotation(OperationLogAnnotation.class);
        if (annotation != null) {
          operationLog.setLogType(annotation.logType());
        }
        //操作用户
        if (request != null) {
          String token = request.getHeader("token");
          if (StringUtils.isNotBlank(token)) {
            try {
              String account = JWT.decode(token).getAudience().get(0);
              operationLog.setAccount(account);
              String username = userMapper.selectOne(new QueryWrapper<User>().eq("account", account)).getName();
              String opPlatform = userMapper.getOpPlatform(account);
              operationLog.setUsername(username);
              operationLog.setOperatePlatform(opPlatform);
            } catch (JWTDecodeException j) {
              throw new InvalidTokenException("OperationLogAspect 非法token");
            }
          }
          //操作IP
          operationLog.setClientIp(request.getRemoteAddr());
        }
        //保存日志
        mongoTemplate.save(operationLog);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return joinPoint.proceed();
  }

}
