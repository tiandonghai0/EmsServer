package com.shmet.controller.v2;

import com.shmet.ArithUtil;
import com.shmet.entity.dto.Result;
import com.shmet.util.NumberUtil;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author
 */
@RestController
@RequestMapping("/nanrui")
public class NanRuiController {

  private static final Map<Integer, ShipChargeInfoReq> map = new ConcurrentHashMap<>();

  static {
    map.put(1, new ShipChargeInfoReq("1771a15ad5f01cf8d683044488f9ed87", "1771d9cb184dafefc01aaa94636bb2d1", "NR001", "2021-01-25"));
    map.put(2, new ShipChargeInfoReq("1771a15ad5f01cf8d683044488f9ed87", "1771d9cb182bd011be870454126b5f8b", "NR002", "2021-01-25"));
    map.put(3, new ShipChargeInfoReq("1771a15ad5f01cf8d683044488f9ed87", "1771d9cb182bd011be870454126b58kl", "NR003", "2021-01-25"));
    map.put(4, new ShipChargeInfoReq("1771a15ad5f01cf8d683044488f9ed87", "1771d9cb185ad35a2fe7c984f418d715", "NR004", "2021-01-25"));
    map.put(5, new ShipChargeInfoReq("1771a15ad5f01cf8d683044488f9ed87", "1771d9cb184dafefc01aaa94636bb2d1", "NR001", "2021-01-26"));
    map.put(6, new ShipChargeInfoReq("1771a15ad5f01cf8d683044488f9ed87", "1771d9cb182bd011be870454126b5f8b", "NR002", "2021-01-26"));
    map.put(7, new ShipChargeInfoReq("1771a15ad5f01cf8d683044488f9ed87", "1771d9cb182bd011be870454126b58kl", "NR003", "2021-01-26"));
    map.put(8, new ShipChargeInfoReq("1771a15ad5f01cf8d683044488f9ed87", "1771d9cb185ad35a2fe7c984f418d715", "NR004", "2021-01-26"));
  }

  public boolean matchMap(ShipChargeInfoReq shipChargeInfoReq) {
    for (Map.Entry<Integer, ShipChargeInfoReq> entry : map.entrySet()) {
      if (entry.getValue().equals(shipChargeInfoReq)) {
        return true;
      }
    }
    return false;
  }

  @PostMapping("/ems/shipChargeInfo/receive")
  public Result pushShipChargeInfoData(@RequestBody ShipChargeInfoReq shipChargeInfoReq) {
    if (shipChargeInfoReq == null ||
        StringUtils.isBlank(shipChargeInfoReq.getPortId()) ||
        StringUtils.isBlank(shipChargeInfoReq.getBerthId()) ||
        StringUtils.isBlank(shipChargeInfoReq.getShipCode()) ||
        StringUtils.isBlank(shipChargeInfoReq.getChargedate())
    ) {
      return Result.getErrorResultInfo(1001, "非法参数异常,请检查传入参数");
    }
    if (!isValidDate(shipChargeInfoReq.getChargedate())) {
      return Result.getErrorResultInfo(1001, "传入日期格式有误");
    }
    if (!matchMap(shipChargeInfoReq)) {
      return Result.getErrorResultInfo(1002, "失败,查询连船记录不存在");
    }
    ShipChargeInfo shipChargeInfo = new ShipChargeInfo();
    LocalDateTime now = LocalDateTime.now();
    BeanUtils.copyProperties(shipChargeInfoReq, shipChargeInfo);
    shipChargeInfo.setShipName("远洋 21-5");
    //时间随机
    shipChargeInfo.setBerthTime(localDateTimeToStr(now));
    shipChargeInfo.setDepartureTime(localDateTimeToStr(now.plusHours(6)));
    shipChargeInfo.setStartTime(localDateTimeToStr(now.plusMinutes(20)));
    shipChargeInfo.setEndTime(localDateTimeToStr(now.plusHours(5)));
    //用电量随机
    shipChargeInfo.setStartInletElectricity(randomNum(1000, 1500));
    shipChargeInfo.setEndInletElectricity(String.valueOf(
        ArithUtil.add(Double.parseDouble(shipChargeInfo.getStartInletElectricity()), Double.parseDouble(randomNum(100, 500)))
    ));
    shipChargeInfo.setStartExportElectricity(randomNum(1000, 1500));
    shipChargeInfo.setEndExportElectricity(String.valueOf(
        ArithUtil.add(Double.parseDouble(shipChargeInfo.getStartExportElectricity()), Double.parseDouble(randomNum(100, 500)))
    ));
    //最大 电流 功率
    shipChargeInfo.setMaxCurrent(randomNum(30, 80));
    shipChargeInfo.setMaxPower(randomNum(50, 100));
    //进出线 电量差值
    shipChargeInfo.setSupplyElectricity(randomNum(200, 220));
    shipChargeInfo.setUseElectricity(randomNum(130, 200));
    //峰平谷尖峰时段电量
    shipChargeInfo.setPeakElectricity("0");
    shipChargeInfo.setValleyElectricity("0");
    shipChargeInfo.setFlatElectricity("0");
    shipChargeInfo.setSharpElectricity("0");
    shipChargeInfo.setSupplyDuration("16579534");

    shipChargeInfo.setAuxiliaryPower("58.2");
    shipChargeInfo.setRecvVoltage("6.6");
    shipChargeInfo.setRecvFrequency("60");

    return Result.getSuccessResultInfo("1000", shipChargeInfo, "成功");
  }

  static class ShipChargeInfoReq {
    private String portId;
    private String berthId;
    private String shipCode;
    private String chargedate;

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      ShipChargeInfoReq that = (ShipChargeInfoReq) o;
      return Objects.equals(portId, that.portId) &&
          Objects.equals(berthId, that.berthId) &&
          Objects.equals(shipCode, that.shipCode) &&
          Objects.equals(chargedate, that.chargedate);
    }

    @Override
    public int hashCode() {
      return Objects.hash(portId, berthId, shipCode, chargedate);
    }

    public ShipChargeInfoReq() {
    }

    public ShipChargeInfoReq(String portId, String berthId, String shipCode, String chargedate) {
      this.portId = portId;
      this.berthId = berthId;
      this.shipCode = shipCode;
      this.chargedate = chargedate;
    }

    public String getPortId() {
      return portId;
    }

    public void setPortId(String portId) {
      this.portId = portId;
    }

    public String getBerthId() {
      return berthId;
    }

    public void setBerthId(String berthId) {
      this.berthId = berthId;
    }

    public String getShipCode() {
      return shipCode;
    }

    public void setShipCode(String shipCode) {
      this.shipCode = shipCode;
    }

    public String getChargedate() {
      return chargedate;
    }

    public void setChargedate(String date) {
      this.chargedate = date;
    }
  }

  static class ShipChargeInfo {
    //港口ID
    private String portId;
    //泊位ID
    private String berthId;
    //船舶编号
    private String shipCode;
    //连船日期
    private String chargedate;
    //船舶名称
    private String shipName;
    //船舶停靠泊位时间
    private String berthTime;
    //船舶离开泊位时间
    private String departureTime;
    //船舶供电开始时间
    private String startTime;
    //船舶供电结束时间
    private String endTime;
    //开始时进线电量
    private String startInletElectricity;
    //开始时出线电量
    private String startExportElectricity;
    //结束时进线电量
    private String endInletElectricity;
    //结束时出线电量
    private String endExportElectricity;
    //最大电流
    private String MaxCurrent;
    //最大功率
    private String maxPower;
    //进线电量差值(供电量) kWh
    private String supplyElectricity;
    //出线电量差值(用电量) 即 总充电量 kWh
    private String useElectricity;
    //峰时段电量
    private String peakElectricity;
    //平时段电量
    private String flatElectricity;
    //谷时段电量
    private String valleyElectricity;
    //尖峰时段电量
    private String sharpElectricity;
    //实际向船舶送电总时长 s
    private String supplyDuration;
    //设备额定功率 kW
    private String auxiliaryPower;
    //受电电压 kV
    private String recvVoltage;
    //受电频率 Hz
    private String recvFrequency;

    public String getPortId() {
      return portId;
    }

    public void setPortId(String portId) {
      this.portId = portId;
    }

    public String getBerthId() {
      return berthId;
    }

    public void setBerthId(String berthId) {
      this.berthId = berthId;
    }

    public String getShipCode() {
      return shipCode;
    }

    public void setShipCode(String shipCode) {
      this.shipCode = shipCode;
    }

    public String getChargedate() {
      return chargedate;
    }

    public void setChargedate(String date) {
      this.chargedate = date;
    }

    public String getShipName() {
      return shipName;
    }

    public void setShipName(String shipName) {
      this.shipName = shipName;
    }

    public String getBerthTime() {
      return berthTime;
    }

    public void setBerthTime(String berthTime) {
      this.berthTime = berthTime;
    }

    public String getDepartureTime() {
      return departureTime;
    }

    public void setDepartureTime(String departureTime) {
      this.departureTime = departureTime;
    }

    public String getStartTime() {
      return startTime;
    }

    public void setStartTime(String startTime) {
      this.startTime = startTime;
    }

    public String getEndTime() {
      return endTime;
    }

    public void setEndTime(String endTime) {
      this.endTime = endTime;
    }

    public String getStartInletElectricity() {
      return startInletElectricity;
    }

    public void setStartInletElectricity(String startInletElectricity) {
      this.startInletElectricity = startInletElectricity;
    }

    public String getStartExportElectricity() {
      return startExportElectricity;
    }

    public void setStartExportElectricity(String startExportElectricity) {
      this.startExportElectricity = startExportElectricity;
    }

    public String getEndInletElectricity() {
      return endInletElectricity;
    }

    public void setEndInletElectricity(String endInletElectricity) {
      this.endInletElectricity = endInletElectricity;
    }

    public String getEndExportElectricity() {
      return endExportElectricity;
    }

    public void setEndExportElectricity(String endExportElectricity) {
      this.endExportElectricity = endExportElectricity;
    }

    public String getMaxCurrent() {
      return MaxCurrent;
    }

    public void setMaxCurrent(String maxCurrent) {
      MaxCurrent = maxCurrent;
    }

    public String getMaxPower() {
      return maxPower;
    }

    public void setMaxPower(String maxPower) {
      this.maxPower = maxPower;
    }

    public String getSupplyElectricity() {
      return supplyElectricity;
    }

    public void setSupplyElectricity(String supplyElectricity) {
      this.supplyElectricity = supplyElectricity;
    }

    public String getUseElectricity() {
      return useElectricity;
    }

    public void setUseElectricity(String useElectricity) {
      this.useElectricity = useElectricity;
    }

    public String getPeakElectricity() {
      return peakElectricity;
    }

    public void setPeakElectricity(String peakElectricity) {
      this.peakElectricity = peakElectricity;
    }

    public String getFlatElectricity() {
      return flatElectricity;
    }

    public void setFlatElectricity(String flatElectricity) {
      this.flatElectricity = flatElectricity;
    }

    public String getValleyElectricity() {
      return valleyElectricity;
    }

    public void setValleyElectricity(String valleyElectricity) {
      this.valleyElectricity = valleyElectricity;
    }

    public String getSharpElectricity() {
      return sharpElectricity;
    }

    public void setSharpElectricity(String sharpElectricity) {
      this.sharpElectricity = sharpElectricity;
    }

    public String getSupplyDuration() {
      return supplyDuration;
    }

    public void setSupplyDuration(String supplyDuration) {
      this.supplyDuration = supplyDuration;
    }

    public String getAuxiliaryPower() {
      return auxiliaryPower;
    }

    public void setAuxiliaryPower(String auxiliaryPower) {
      this.auxiliaryPower = auxiliaryPower;
    }

    public String getRecvVoltage() {
      return recvVoltage;
    }

    public void setRecvVoltage(String recvVoltage) {
      this.recvVoltage = recvVoltage;
    }

    public String getRecvFrequency() {
      return recvFrequency;
    }

    public void setRecvFrequency(String recvFrequency) {
      this.recvFrequency = recvFrequency;
    }
  }

  private String localDateTimeToStr(LocalDateTime time) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    return formatter.format(time);
  }

  public String randomNum(Integer start, Integer end) {
    double v = RandomUtils.nextDouble(start, end);
    return String.valueOf(NumberUtil.convert2(v));
  }

  private static boolean isValidDate(String str) {
    boolean convertSuccess = true;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    try {
      format.setLenient(false);
      format.parse(str);
    } catch (ParseException e) {
      convertSuccess = false;
    }
    return convertSuccess;
  }
}

