package com.shmet.aop;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shmet.Consts;
import com.shmet.dao.OperateLogMapper;
import com.shmet.dao.UserMapper;
import com.shmet.entity.mysql.gen.OperateLog;
import com.shmet.entity.mysql.gen.User;
import com.shmet.helper.IpUtil;
import com.shmet.helper.JsonUtils;
import com.shmet.vo.UserVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;

@Slf4j
@Component
@Aspect
public class MySQLOpLogAspect {

  @Resource
  private UserMapper userMapper;

  @Resource
  private OperateLogMapper operateLogMapper;

  @Pointcut("@annotation(com.shmet.aop.OperationLogAnnotation)")
  public void opLogPoinCut() {

  }

  /**
   * 记录操作日志
   *
   * @param joinPoint 方法的执行点
   */
  @Around(value = "opLogPoinCut()")
  public Object saveOperLog(ProceedingJoinPoint joinPoint) throws Throwable {
    // 获取RequestAttributes
    //RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
    // 从获取RequestAttributes中获取HttpServletRequest的信息
    //if (requestAttributes != null) {
    //HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
    HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    try {
      OperateLog olog = new OperateLog();
      // 从切面织入点处通过反射机制获取织入点处的方法
      MethodSignature signature = (MethodSignature) joinPoint.getSignature();
      //获取切入点所在的方法
      Method method = signature.getMethod();
      Object[] args = joinPoint.getArgs();
      //获取操作日志类型
      OperationLogAnnotation annotation = method.getAnnotation(OperationLogAnnotation.class);
      if (annotation != null) {
        olog.setLogType(annotation.logType());
        olog.setContent(annotation.opContent());
      }

      StringBuilder sb = new StringBuilder();
      if (annotation != null && annotation.logType() == 1) {
        UserVo vo = ((UserVo) args[0]);
        String account = vo.getAccount();
        String username = userMapper.selectOne(new QueryWrapper<User>().eq("account", account)).getName();
        String opPlatform = userMapper.getOpPlatform(account);
        olog.setAccount(account);
        olog.setUsername(username);
        olog.setOpPlatform(opPlatform);

        olog.setParam(vo.toString());
      } else {
        if (annotation != null && annotation.logType() != 3) {
          if (args != null && args.length > 0) {
            for (Object arg : args) {
              sb.append(arg).append(",");
            }
          }
        } else {
          if (args != null && args.length > 0) {
            sb.append("subProjectId").append(((JSONObject) args[0]).get("subProjectId"));
          }
        }
        String param = sb.toString();
        olog.setParam(param);
      }

      //操作用户
      //获取token 进行解密
      String token = (String) request.getSession().getAttribute("token");
      if (StringUtils.isBlank(token)) {
        token = request.getHeader("token");
      }
      log.info("token: {}", token);
      if (request.getSession().getAttribute(Consts.SESSION_USERINFO) != null) {
        User user = (User) request.getSession().getAttribute(Consts.SESSION_USERINFO);
        String username = user.getName();
        String opPlatform = userMapper.getOpPlatform(user.getAccount());
        olog.setAccount(user.getAccount());
        olog.setUsername(username);
        olog.setOpPlatform(opPlatform);
      }
      if (StringUtils.isNotBlank(token)) {
        try {
          String account = JWT.decode(token).getAudience().get(0);
          String username = userMapper.selectOne(new QueryWrapper<User>().eq("account", account)).getName();
          String opPlatform = userMapper.getOpPlatform(account);
          olog.setAccount(account);
          olog.setUsername(username);
          olog.setOpPlatform(opPlatform);
        } catch (JWTDecodeException ignored) {
        }
      }
      //操作IP
      //olog.setIp(getIpAdrress(request));
      olog.setIp(IpUtil.getIpAddr(request));
      log.info("olog: {}", JsonUtils.objToString(olog));
      //保存日志
      int insert = operateLogMapper.insert(olog);
      log.info("插入成功??: {}", insert == 1);
    } catch (Exception e) {
      e.printStackTrace();
    }
    //}
    return joinPoint.proceed();
  }

  private static String getIpAdrress(HttpServletRequest request) {
    String Xip = request.getHeader("X-Real-IP");
    String XFor = request.getHeader("X-Forwarded-For");
    if (StringUtils.isNotEmpty(XFor) && !"unKnown".equalsIgnoreCase(XFor)) {
      //多次反向代理后会有多个ip值，第一个ip才是真实ip
      int index = XFor.indexOf(",");
      if (index != -1) {
        return XFor.substring(0, index);
      } else {
        return XFor;
      }
    }
    XFor = Xip;
    if (StringUtils.isNotEmpty(XFor) && !"unKnown".equalsIgnoreCase(XFor)) {
      return XFor;
    }
    if (StringUtils.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
      XFor = request.getHeader("Proxy-Client-IP");
    }
    if (StringUtils.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
      XFor = request.getHeader("WL-Proxy-Client-IP");
    }
    if (StringUtils.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
      XFor = request.getHeader("HTTP_CLIENT_IP");
    }
    if (StringUtils.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
      XFor = request.getHeader("HTTP_X_FORWARDED_FOR");
    }
    if (StringUtils.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
      XFor = request.getRemoteAddr();
    }
    return XFor;
  }
}
