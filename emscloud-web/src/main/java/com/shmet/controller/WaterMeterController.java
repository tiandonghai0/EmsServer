package com.shmet.controller;

import com.shmet.bean.waterMeter.TWaterMeterTaskInfo;
import com.shmet.bean.waterMeter.WaterMeterBaseInfo;
import com.shmet.bean.waterMeter.WaterMeterTaskInfo;
import com.shmet.entity.dto.Result;
import com.shmet.entity.dto.ResultSearch;
import com.shmet.entity.mysql.gen.TWaterMeter;
import com.shmet.handler.WaterMeterHandler;
import com.shmet.helper.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 鹤鸣洲酒店水表控制器
 */

@Controller
public class WaterMeterController {
  @Autowired
  WaterMeterHandler waterMeterHandler;

  /**
   * 获取水表（房间）编号
   */
  @ResponseBody
  @RequestMapping(value = "/watermeter/getNo", method = RequestMethod.POST)
  public String getNo(@RequestBody TWaterMeter model) {

    Result result = Result.getSuccessResultInfo();
    try {
      List<TWaterMeter> list = waterMeterHandler.getAllWaterMeter(model);
      result.setData(list);
    } catch (Exception e) {

      result.setMessage(e.getMessage());
      result.setSuccess(false);
    }
    return JsonUtils.objToString(result);
  }

  /**
   * 新增预充值
   */
  @ResponseBody
  @RequestMapping(value = "/watermeter/add", method = RequestMethod.POST)
  public String add(@RequestBody WaterMeterTaskInfo model) {
    Result result = Result.getSuccessResultInfo();
    try {
      result = waterMeterHandler.insertWaterMeterTask(model);
    } catch (Exception e) {
      result.setMessage(e.getMessage());
      result.setSuccess(false);
    }
    return JsonUtils.objToString(result);
  }

  /**
   * 获取预充值列表
   */
  @ResponseBody
  @RequestMapping(value = "/watermeter/getTasksList", method = RequestMethod.POST)
  public String getTasksList(@RequestBody TWaterMeterTaskInfo model) {
    ResultSearch result = ResultSearch.getSuccessResultInfo();
    try {
      result = waterMeterHandler.queryWaterMeter(model);
    } catch (Exception e) {
      result.setMessage(e.getMessage());
      result.setSuccess(false);
    }
    return JsonUtils.objToString(result);
  }

  @ResponseBody
  @RequestMapping(value = "/watermeter/cancelTask", method = RequestMethod.POST)
  public String cancelTask(@RequestBody List<Integer> list) {
    Result result = Result.getSuccessResultInfo();

    try {
      result = waterMeterHandler.cancelTask(list);
    } catch (Exception e) {
      result.setSuccess(false);
      result.setMessage(e.getMessage());
    }

    return JsonUtils.objToString(result);
  }

  @ResponseBody
  @RequestMapping(value = "/watermeter/open", method = RequestMethod.POST)
  public String open(@RequestBody WaterMeterBaseInfo model) {
    Result result = Result.getSuccessResultInfo();
    try {
      waterMeterHandler.waterMeterTaskJob();
      result = waterMeterHandler.open(model);
    } catch (Exception e) {
      result.setSuccess(false);
      result.setMessage(e.getMessage());
    }
    return JsonUtils.objToString(result);
  }

}
