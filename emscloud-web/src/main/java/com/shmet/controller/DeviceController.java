package com.shmet.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.shmet.bean.ResultState;
import com.shmet.entity.mysql.gen.TAccone;
import com.shmet.entity.mysql.gen.TDevice;
import com.shmet.entity.mysql.gen.TSubProject;
import com.shmet.handler.AcconeHandler;
import com.shmet.handler.DeviceHandler;
import com.shmet.handler.SubProjectHandler;

@Controller
public class DeviceController {
  @Autowired
  private DeviceHandler deviceHandler;
  @Autowired
  private AcconeHandler acconeHandler;
  @Autowired
  private SubProjectHandler subProjectHandler;

  /**
   * 跳转到设备查询页面
   */
  @RequestMapping("/device/init")
  public ModelAndView jumpToDevice() {
    return new ModelAndView("/device/device.btl");
  }

  /**
   * 设备分页查询
   */
  @ResponseBody
  @RequestMapping("/device/get_device")
  public Map<String, Object> getTDevice(Integer page, Integer rows) {

    Map<String, Object> map = null;
    try {
      map = deviceHandler.queryPageDevice(page, rows);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return map;

  }

  // 异步获取acconSn
  @ResponseBody
  @RequestMapping("/device/get_t_acconeId")
  public List<Map<String, Object>> getTAcconeId() {
    List<Map<String, Object>> list = null;
    try {
      List<TAccone> acconList = acconeHandler.queryTAccone();
      list = new ArrayList<>();
      for (TAccone tAccone : acconList) {
        Map<String, Object> map = new HashMap<>();
        String acconeId = tAccone.getAcconeId().toString();
        String acconeSn = tAccone.getAcconeSn();
        // map存放键值对
        map.put("acconeId", acconeId);
        map.put("acconeSn", acconeSn);
        // list存放map
        list.add(map);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return list;
  }

  // 异步获取subProjectId
  @ResponseBody
  @RequestMapping("/device/get_sub_project_id")
  public List<Map<String, Object>> getsubProjectId() {
    List<Map<String, Object>> list = null;
    try {
      List<TSubProject> tSubProjectList = subProjectHandler
          .queryTSubProject();
      list = new ArrayList<>();
      for (TSubProject tSubProject : tSubProjectList) {
        Map<String, Object> map = new HashMap<>();

        String subProjectId = tSubProject.getSubProjectId().toString();
        String subProjectName = tSubProject.getSubProjectName();
        // map存放键值对
        map.put("subProjectId", subProjectId);
        map.put("subProjectName", subProjectName);
        // list存放map
        list.add(map);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return list;
  }

  // 添加设备
  @ResponseBody
  @RequestMapping("/device/add_device")
  public ResultState addTDevice(TDevice tdevice) {
    ResultState resultState = new ResultState();
    try {
      // 设置创建时间
      tdevice.setCreateDate(new Date());
      // 设置修改时间
      tdevice.setUpdateDate(tdevice.getCreateDate());
      deviceHandler.addTDecivce(tdevice);
      resultState.setSuccess(true);
      resultState.setMessage("添加成功");
    } catch (Exception e) {
      e.printStackTrace();
      resultState.setSuccess(false);
    }
    return resultState;
  }

  // 修改设备
  @ResponseBody
  @RequestMapping("/device/update_device")
  public ResultState updateTDevice(TDevice tdevice, Long deviceNo) {
    ResultState resultState = new ResultState();
    try {
      // 设置主键
      tdevice.setDeviceNo(deviceNo);
      // 设置创建时间
      tdevice.setCreateDate(null);
      // 设置修改时间
      tdevice.setUpdateDate(new Date());
      int i = deviceHandler.updateTDecivce(tdevice);
      if (i > 0) {
        resultState.setSuccess(true);
        resultState.setMessage("修改成功");
      } else {
        resultState.setSuccess(false);
      }
    } catch (Exception e) {
      e.printStackTrace();
      resultState.setSuccess(false);
    }
    return resultState;
  }

  // 删除设备
  @ResponseBody
  @RequestMapping("/device/destroy_device")
  public ResultState destroyTDevice(Long deviceNo) {
    ResultState resultState = new ResultState();
    try {
      int i = deviceHandler.destroyTDevice(deviceNo);
      if (i > 0) {
        resultState.setSuccess(true);
        resultState.setMessage("删除成功");
      } else {
        resultState.setSuccess(false);
      }
    } catch (Exception e) {
      e.printStackTrace();
      resultState.setSuccess(false);
    }
    return resultState;
  }

  // 验证数据库是否有deviceNo
  @ResponseBody
  @RequestMapping("/device/check_deviceNo")
  public ResultState checkDeviceNo(Long deviceNo) {
    ResultState resultState = new ResultState();
    TDevice tDevice = new TDevice();
    try {
      tDevice.setDeviceNo(deviceNo);
      Long l = deviceHandler.checkDeviceNo(tDevice);
      if (l > 0) {
        resultState.setSuccess(true);
        resultState.setMessage("已设置，不可重复");
      } else {
        resultState.setSuccess(false);
      }
    } catch (Exception e) {
      e.printStackTrace();
      resultState.setSuccess(true);
      resultState.setMessage("程序异常");
    }
    return resultState;
  }

}
