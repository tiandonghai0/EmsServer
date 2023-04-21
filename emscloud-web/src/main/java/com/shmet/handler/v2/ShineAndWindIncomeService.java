package com.shmet.handler.v2;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shmet.ArithUtil;
import com.shmet.dao.DeviceMapper;
import com.shmet.entity.mysql.gen.Device;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.Year;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * @author
 */
@Service
public class ShineAndWindIncomeService {

  @Resource
  private DeviceMapper deviceMapper;

  @Autowired
  private MongoTemplate mongoTemplate;

  //计算 光风电收益中 的 按月/年 分组 中的 各时段数据 目前只针对 安康华银
  //dateType 1 按月 yyyy-MM 2 按年 yyyy
  public Map<String, Double> calcMonthOrYearTimePeriodDatas(String date, Long subprojectId, int dateType, int deviceModelType) {
    long startTime = -1, endTime = -1;
    //按月
    if (dateType == 1) {
      if (StringUtils.isNotBlank(date)) {
        if (StringUtils.equals(date, Year.now().getValue() + "-" + LocalDate.now().getMonthValue())) {
          String sdate = StringUtils.replace(date, "-", "");
          startTime = Long.parseLong(sdate + "0100");
          String s = StringUtils.substringAfterLast(LocalDate.now().minusDays(1).toString(), "-");
          endTime = Long.parseLong(sdate + s + "24");
        } else {
          String sdate = StringUtils.replace(date, "-", "");
          startTime = Long.parseLong(sdate + "0100");
          endTime = Long.parseLong(sdate + "3124");
        }
      } else {
        String tmp = StringUtils.substringBeforeLast(LocalDate.now().toString(), "-");
        String sdate = StringUtils.replace(tmp, "-", "");
        startTime = Long.parseLong(sdate + "0100");
        String s = StringUtils.substringAfterLast(LocalDate.now().minusDays(1).toString(), "-");
        endTime = Long.parseLong(sdate + s + "24");
      }
    }
    //按年
    if (dateType == 2) {
      if (StringUtils.isNotBlank(date)) {
        if (StringUtils.equals(date, String.valueOf(Year.now().getValue()))) {
          startTime = Long.parseLong(date + "010100");
          String s = StringUtils.substringAfter(LocalDate.now().minusDays(1).toString(), "-");
          String sdate = StringUtils.replace(s, "-", "");
          endTime = Long.parseLong(date + sdate + "24");
        } else {
          startTime = Long.parseLong(date + "010100");
          endTime = Long.parseLong(date + "123124");
        }
      } else {
        startTime = Long.parseLong(Year.now().getValue() + "010100");
        String s = StringUtils.substringAfter(LocalDate.now().minusDays(1).toString(), "-");
        String sdate = StringUtils.replace(s, "-", "");
        endTime = Long.parseLong(Year.now().getValue() + sdate + "24");
      }
    }

    List<Long> deviceNos = getDeviceNosBySubprojectId(subprojectId, deviceModelType);
    Aggregation agg = Aggregation.newAggregation(
        Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("hour").gte(startTime).lte(endTime)),
        Aggregation.project().andExclude("_id")
            .and("$statistics.EPE.priceType").as("priceType")
            .and("$statistics.EPE.diffsum").as("epediffsum")
            .and("$statistics.EPI.diffsum").as("epidiffsum"),
        Aggregation.group("priceType")
            .sum("epediffsum").as("epesum")
            .sum("epidiffsum").as("episum")
    );
    AggregationResults<Document> aggRes = mongoTemplate.aggregate(agg, "deviceRealData", Document.class);

    Map<String, Double> map = new LinkedHashMap<>();
    for (Document doc : aggRes.getMappedResults()) {
      Object obj = doc.get("_id");
      String timeType = String.valueOf(obj);
      double epesum = Double.parseDouble(String.valueOf(doc.get("epesum") == null ? 0d : doc.get("epesum")));
      //if (subprojectId == 20202002001L) {
        double episum = Double.parseDouble(String.valueOf(doc.get("episum") == null ? 0d : doc.get("episum")));
        if (StringUtils.equals("null", timeType)) {
          map.put("1", ArithUtil.add(epesum, episum));
        } else if (StringUtils.equals("1", timeType)) {
          Double dd = map.get("1");
          map.put(timeType, ArithUtil.add(dd == null ? 0d : dd, ArithUtil.add(epesum, episum)));
        } else {
          map.put(timeType, ArithUtil.add(epesum, episum));
        }
    }

    return map;
  }

  public List<Long> getDeviceNosBySubprojectId(Long subprojectId, int deviceModelType) {
    return deviceMapper.selectList(
        new QueryWrapper<Device>()
            .eq("device_model_type", deviceModelType)
            .eq("device_model", 1)
            .eq("sub_project_id", subprojectId)
    ).stream().map(Device::getDeviceNo).collect(toList());
  }

}
