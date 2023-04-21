package com.shmet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class OperationController {

  /**
   * 实时监控页面
   */
  @RequestMapping("/monitoring/operation/init")
  public ModelAndView init() {
    return new ModelAndView("/monitoring/operation.btl");
  }

  /**
   * 空调控制页面
   */
  @RequestMapping("/monitoring/air/control")
  public ModelAndView airControl() {
    return new ModelAndView("/monitoring/aircontrol.btl");
  }
}
