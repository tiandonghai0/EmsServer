package com.shmet.vo;

import com.shmet.entity.mysql.gen.Device;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HengtongMonitorVo {
  //设备名称
  private String deviceName;
  //设备编号
  private String deviceNo;
  //设备Id
  private String deviceId;
  //emuId
  private String emuId;
  //最后登录时间
  private String loginLastTime;
  //电能 tagCode
  private String elecEnergy;
  //功率 tagCode
  private String p;
  //emu状态
  private String emuStatus;
  //最新数据时间
  private String latestDataTime;

  private Integer deviceStatus;

  public static HengtongMonitorVo buildVo(Device device) {
    HengtongMonitorVo vo = new HengtongMonitorVo();
    vo.setDeviceId(String.valueOf(device.getDeviceId()));
    vo.setDeviceNo(String.valueOf(device.getDeviceNo()));
    vo.setDeviceName(device.getDeviceName());
    vo.setEmuId(String.valueOf(device.getAcconeId()));
    return vo;
  }
}
