package com.shmet.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.shmet.dao.IDeviceDao;
import com.shmet.entity.mysql.gen.TDevice;

import javax.annotation.Resource;

@Service
public class DeviceHandler {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Resource
  private IDeviceDao iDeviceDao;

  /**
   * 分页查询业务
   */
  public Map<String, Object> queryPageDevice(Integer page, Integer rows) {

    // 数据的总行数
    long count = iDeviceDao.allCount();

    if (page == 1) {
      page = 1;
    } else {
      page = (page - 1) * rows + 1;
    }

    // 分页查询的对象集合
    List<TDevice> list = iDeviceDao.all(page, rows);

    Map<String, Object> map = new HashMap<>();
    map.put("total", count);
    map.put("rows", list);

    return map;
  }

  /**
   * 添加设备
   */
  public void addTDecivce(TDevice tdevice) {
    iDeviceDao.insert(tdevice);
  }

  /**
   * 修改设备
   */
  public int updateTDecivce(TDevice tdevice) {
    return iDeviceDao.updateTemplateById(tdevice);
  }

  /**
   * 删除设备
   */
  public int destroyTDevice(Long deviceNo) {
    return iDeviceDao.deleteById(deviceNo);
  }

  /**
   * 查询是否有设备表主键
   */
  public Long checkDeviceNo(TDevice tDevice) {
    return iDeviceDao.templateCount(tDevice);
  }

}
