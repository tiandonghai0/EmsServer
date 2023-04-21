package com.shmet.vo.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HengtongMonitorReq {
  //设备名称
  private String deviceName;
  //设备编号
  private String deviceNo;
  //emuId
  private String emuId;
  private Integer pageNum = 1;
  private Integer pageSize = 10;
}
