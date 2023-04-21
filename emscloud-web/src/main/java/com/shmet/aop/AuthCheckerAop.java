package com.shmet.aop;

import com.shmet.Consts;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

//@Aspect
//@Component
public class AuthCheckerAop {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Pointcut("execution(public * com.shmet.controller..*.*(..)) && " +
      "!execution(public * com.shmet.controller.LoginController.*(..)) && " +
      "!execution(public * com.shmet.controller.v2..*(..)) && " +
      "!execution(public * com.shmet.controller.LoginWithTokenController.*(..)) && " +
      "!execution(public * com.shmet.controller.OperationController.*(..)) && " +
      "!execution(public * com.shmet.controller.StatisticsController.*(..)) && " +
      "!execution(public * com.shmet.controller.WaterMeterController.*(..))  && " +
      "!execution(public * com.shmet.controller.AcconeController.*(..))  && " +
      "!execution(public * com.shmet.controller.ProjectController.*(..))")
  public void authCheckPoint() {

  }

  @Around("authCheckPoint()")
  public Object authCheckAdvice(ProceedingJoinPoint pjp) throws IOException {
    Object result = null;
    String targetName = pjp.getTarget().getClass().getSimpleName();
    String methodName = pjp.getSignature().getName();
    logger.info("----------------执行方法-----------------");
    logger.info("类名：" + targetName + " 方法名：" + methodName);
    ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (servletRequestAttributes == null) {
      return null;
    }
    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    HttpSession session = request.getSession();
    HttpServletResponse response = servletRequestAttributes.getResponse();

    if (session.getAttribute(Consts.SESSION_USERINFO) != null) {
      try {
        result = pjp.proceed();
      } catch (Throwable e) {
        e.printStackTrace();
      }
      return result;
    } else {
      StringBuffer url = request.getRequestURL();
      String queryurl = request.getQueryString();
      if (null != queryurl) {
        url.append("?").append(queryurl);
      }
      logger.debug("Session已超时，或者登录失败，正在跳转回登录页面");
      assert response != null;
      response.sendRedirect(request.getContextPath() + "/login?redirect_url=" + url);
    }
    return null;
  }
}
