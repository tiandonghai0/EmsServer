package com.shmet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class OverviewController {

  /**
   * 信息概览页面
   */
  @RequestMapping("/overview/init")
  public ModelAndView init() {
    return new ModelAndView("/overview.btl");
  }
}
