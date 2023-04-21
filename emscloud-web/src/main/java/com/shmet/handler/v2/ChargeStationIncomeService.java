package com.shmet.handler.v2;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shmet.dao.DeviceMapper;
import com.shmet.entity.mysql.gen.Device;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author
 * 投资收益 --> 充电站收益 相关处理
 */
@Service
public class ChargeStationIncomeService {

  private static final Logger log = LoggerFactory.getLogger(ChargeStationIncomeService.class);

  @Autowired
  private MongoTemplate mongoTemplate;

  @Resource
  private DeviceMapper deviceMapper;

  //计算 下方的图表数据
  public List<ChargeStationVo> calcEchartDatas(String date, List<Long> subprojectIds, int dateType, int deviceModelType) {
    String sdate = transferDate(date, dateType);
    List<Long> deviceNos = getDeviceNos(subprojectIds, deviceModelType);

    AggregationResults<Document> aggRes = null;
    List<ChargeStationVo> vos = new ArrayList<>();
    if (dateType == 1) {
      long start = Long.parseLong(sdate + "00");
      long end = Long.parseLong(sdate + "24");
      Aggregation agg = Aggregation.newAggregation(
          Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("hour").gte(start).lte(end)),
          Aggregation.project("hour")
              .and("$statistics.EPE.diffsum").as("epediffsum")
              .and("$statistics.EPE.price").as("epeprice"),
          Aggregation.group("hour")
              .sum("epediffsum").as("epesum")
              .sum("epeprice").as("income"),
          Aggregation.sort(Sort.Direction.ASC, "_id")
      );
      aggRes = mongoTemplate.aggregate(agg, "deviceRealData", Document.class);
    }
    if (dateType == 2) {
      long start = Long.parseLong(sdate + "01");
      long end = Long.parseLong(sdate + "31");
      Aggregation agg = Aggregation.newAggregation(
          Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("day").gte(start).lte(end)),
          Aggregation.project("day")
              .and("$statistics.EPE.diffsum").as("epediffsum")
              .and("$statistics.EPE.price").as("epeprice"),
          Aggregation.group("day")
              .sum("epediffsum").as("epesum")
              .sum("epeprice").as("income"),
          Aggregation.sort(Sort.Direction.ASC, "_id")
      );
      aggRes = mongoTemplate.aggregate(agg, "deviceDayStatistics", Document.class);
    }
    if (dateType == 3) {
      long start = Long.parseLong(sdate + "01");
      long end = Long.parseLong(sdate + "12");
      Aggregation agg = Aggregation.newAggregation(
          Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("yearMonth").gte(start).lte(end)),
          Aggregation.project("yearMonth")
              .and("$statistics.EPE.diffsum").as("epediffsum")
              .and("$statistics.EPE.price").as("epeprice"),
          Aggregation.group("yearMonth")
              .sum("epediffsum").as("epesum")
              .sum("epeprice").as("income"),
          Aggregation.sort(Sort.Direction.ASC, "_id")
      );
      aggRes = mongoTemplate.aggregate(agg, "deviceMonthStatistics", Document.class);
    }

    if (aggRes != null) {
      for (Document doc : aggRes.getMappedResults()) {
        Object obj = doc.get("_id");
        ChargeStationVo vo = new ChargeStationVo();
        if (obj != null) {
          String time = String.valueOf(obj);
          Double epesum = Double.parseDouble(String.valueOf(doc.get("epesum") == null ? 0d : doc.get("epesum")));
          Double income = Double.parseDouble(String.valueOf(doc.get("income") == null ? 0d : doc.get("income")));

          vo.setTime(time);
          vo.setEpesum(epesum);
          vo.setIncome(income);

          vos.add(vo);
        }
      }
    }

    return vos;
  }

  //转换日期 dateType==1 day eg. 2020-12-22 ==> 20201222
  //转换日期 dateType==2 month eg. 2020-12 ==> 202012
  //转换日期 dateType==3 year eg. 2020 ==> 2020
  public String transferDate(String date, int dateType) {
    //按天
    if (dateType == 1) {
      if (StringUtils.isNotBlank(date)) {
        return StringUtils.replace(date, "-", "");
      } else {
        return StringUtils.replace(LocalDate.now().toString(), "-", "");
      }
    }
    //按月
    if (dateType == 2) {
      if (StringUtils.isNotBlank(date)) {
        return StringUtils.replace(date, "-", "");
      } else {
        return Year.now().getValue() + "" + LocalDate.now().getMonthValue();
      }
    }
    //按年
    if (dateType == 3) {
      if (StringUtils.isNotBlank(date)) {
        return date;
      } else {
        return String.valueOf(Year.now().getValue());
      }
    }
    return "-1";
  }

  private List<Long> getDeviceNos(List<Long> subprojectIds, int deviceModelType) {
    return deviceMapper.selectList(new QueryWrapper<Device>()
        .eq("device_model_type", deviceModelType)
        .in("sub_project_id", subprojectIds)
    ).stream().map(Device::getDeviceNo).collect(toList());
  }

  private static class ChargeStationVo {
    private String time;
    private Double epesum;
    private Double income;

    public String getTime() {
      return time;
    }

    public void setTime(String time) {
      this.time = time;
    }

    public Double getEpesum() {
      return epesum;
    }

    public void setEpesum(Double epesum) {
      this.epesum = epesum;
    }

    public Double getIncome() {
      return income;
    }

    public void setIncome(Double income) {
      this.income = income;
    }
  }

}
