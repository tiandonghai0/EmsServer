package com.shmet.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MainController {

  /**
   * 初始页面
   */
  @RequestMapping("/")
  public ModelAndView init(HttpServletRequest request) {
    Map<String, Object> map = new HashMap<>();
    map.put("userinfo", request.getSession(true).getAttribute("SESSION_USERINFO"));
    return new ModelAndView("/main.btl", map);
  }

  /**
   * 主信息页面
   */
  @RequestMapping("/main")
  public ModelAndView main(HttpServletRequest request) {
    Map<String, Object> map = new HashMap<>();
    map.put("userinfo", request.getSession(true).getAttribute("SESSION_USERINFO"));
    return new ModelAndView("/main.btl", map);
  }
}
