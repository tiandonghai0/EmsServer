package com.shmet.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shmet.entity.mongo.ProjectConfig;
import com.shmet.handler.BaishuElectricPricePeriodHandler;

@Controller
public class BaishuElectricPricePeriodController {

  @Autowired
  BaishuElectricPricePeriodHandler baishuElectricPricePeriodHandler;

  /**
   * 跳转到柏树电力价格周期页面
   */
  @RequestMapping("/baishuelectricPricePeriod/init")
  public ModelAndView jumpToBaishuElectricPricePeriod() {
    return new ModelAndView("/baishuelectricpriceperiod/m_index.btl");
  }


  @RequestMapping("baishuelectricPricePeriod/get_baishuElectricPricePeriod")
  @ResponseBody
  public JSONObject findElectricPricePeriod() {
    return baishuElectricPricePeriodHandler.findProjectConfig();

  }

  @RequestMapping("baishuelectricPricePeriod/update_baishuElectricPricePeriod")
  @ResponseBody
  public JSONObject updateProjectConfig(@RequestBody JSONObject jsonObject) {

    String string = jsonObject.toString();

    ObjectMapper om = new ObjectMapper();
    ProjectConfig projectConfig = null;

    try {
      projectConfig = om.readValue(string, ProjectConfig.class);
    } catch (IOException e) {
      e.printStackTrace();
    }

    if (projectConfig != null) {
      boolean b = baishuElectricPricePeriodHandler.updateProjectConfig(projectConfig);

      JSONObject jsonObject1 = new JSONObject();
      jsonObject1.put("status", b);
      return jsonObject1;
    }
    return new JSONObject();
  }

  /**
   * 跳转到季节周期页面
   */
  @RequestMapping("/baishuelectricPricePeriod/init_season")
  public ModelAndView jumpToSeasonPeriod() {
    return new ModelAndView("/baishuelectricpriceperiod/season.btl");
  }


  /**
   * 跳转到收费策略页面
   */
  @RequestMapping("/baishuelectricPricePeriod/init_charge")
  public ModelAndView jumpToChargePolicy() {
    return new ModelAndView("/baishuelectricpriceperiod/charge.btl");
  }

  /**
   * 跳转到收费策略子页面
   */
  @RequestMapping("/baishuelectricPricePeriod/init_charge_sub")
  public ModelAndView jumpToPolicy() {
    return new ModelAndView("/baishuelectricpriceperiod/charge_sub.btl");
  }

}
