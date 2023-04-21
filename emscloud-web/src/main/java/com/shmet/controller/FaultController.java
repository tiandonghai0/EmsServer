package com.shmet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class FaultController {
  /**
   * 实时监控页面
   */
  @RequestMapping("/monitoring/fault/init")
  public ModelAndView init() {
    return new ModelAndView("/monitoring/fault.btl");
  }
}
