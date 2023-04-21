package com.shmet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PredictionController {
  /**
   * 调控页面
   */
  @RequestMapping("/prediction/prediction/init")
  public ModelAndView init() {
    return new ModelAndView("/prediction/prediction.btl");
  }
}
