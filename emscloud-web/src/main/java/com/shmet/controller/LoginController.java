package com.shmet.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.shmet.Consts;
import com.shmet.bean.LoginInfo;
import com.shmet.bean.PostResponseBean;
import com.shmet.entity.mysql.gen.TUser;
import com.shmet.helper.IpUtil;
import com.shmet.helper.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import com.shmet.handler.LoginHandler;

@CrossOrigin
@Controller
public class LoginController {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  LoginHandler loginHandler;

  /**
   * 登录页面
   */
  @RequestMapping("/login")
  public ModelAndView login(HttpServletRequest request) {
    String redirect_url = request.getParameter("redirect_url");
    Map<String, Object> map = new HashMap<>();
    map.put("redirect_url", redirect_url);
    return new ModelAndView("login.btl", map);
  }

  @ResponseBody
  @RequestMapping(method = RequestMethod.POST, value = "/oldlogin")
  public PostResponseBean loginProcess(LoginInfo loginInfo, HttpServletRequest request, HttpServletResponse response) {
    PostResponseBean rst = new PostResponseBean();
    List<TUser> user = loginHandler.getUser(loginInfo);
    if (user != null && user.size() > 0) {
      request.getSession(true).setAttribute(Consts.SESSION_USERINFO, user.get(0));
      rst.setSuccessed(true);
    } else {
      rst.setSuccessed(false);
    }
    return rst;
  }

  @ResponseBody
  @RequestMapping(method = RequestMethod.GET, value = "/logout")
  public void logout(HttpServletRequest request, HttpServletResponse response) {
    request.getSession(true).removeAttribute("SESSION_USERINFO");
    request.getSession().invalidate();
    try {
      response.sendRedirect(request.getContextPath() + "/login");
    } catch (IOException e) {
      logger.error("注销重定向出错", e);
    }
  }

  @ResponseBody
  @RequestMapping(value = "/getip")
  public String getIP(HttpServletRequest request, HttpServletResponse response){
    //return JsonUtils.objToString(request);
    return IpUtil.getIpAddr(request);
  }
}
