package com.shmet.handler.v2;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.shmet.dao.DeviceMapper;
import com.shmet.entity.mysql.gen.Device;
import com.shmet.util.NumberUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.StringOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Year;
import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * @author
 * 安康华银节能减排 service
 */
@Service
public class EnergySaveEmessionReduceService {

  @Autowired
  private MongoTemplate mongoTemplate;

  @Resource
  private DeviceMapper deviceMapper;

  //计算图表上方数据
  public Map<String, Object> calcEchartTopDatas(String date, Long subprojectId, int dateType) {
    List<Long> deviceNos = getDeviceNos(subprojectId);
    AggregationResults<Document> aggRes = null;
    Map<String, Object> map = new LinkedHashMap<>();
    //按月
    if (dateType == 1) {
      long month;
      if (StringUtils.isNotBlank(date)) {
        month = Long.parseLong(StringUtils.replace(date, "-", ""));
      } else {
        String smonth = StringUtils.replace(StringUtils.substringBeforeLast(LocalDate.now().toString(), "-"), "-", "");
        month = Long.parseLong(smonth);
      }
      Aggregation agg = Aggregation.newAggregation(
          Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("yearMonth").is(month)),
          Aggregation.project("yearMonth")
              .and("$statistics.EPE.diffsum").as("epediffsum"),
          Aggregation.group("yearMonth").sum("epediffsum").as("epesum")
      );
      aggRes = mongoTemplate.aggregate(agg, "deviceMonthStatistics", Document.class);
    }
    if (dateType == 2) {
      long startmonth, endmonth;
      if (StringUtils.isNotBlank(date)) {
        startmonth = Long.parseLong(date + "01");
        endmonth = Long.parseLong(date + "12");
      } else {
        startmonth = Long.parseLong(Year.now().getValue() + "01");
        endmonth = Long.parseLong(Year.now().getValue() + "12");
      }
      Aggregation agg = Aggregation.newAggregation(
          Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("yearMonth").gte(startmonth).lte(endmonth)),
          Aggregation.project()
              .and(StringOperators.Substr.valueOf("yearMonth").substring(0, 4)).as("year")
              .and("$statistics.EPE.diffsum").as("epediffsum"),
          Aggregation.group("year").sum("epediffsum").as("epesum")
      );
      aggRes = mongoTemplate.aggregate(agg, "deviceMonthStatistics", Document.class);
    }

    if (aggRes != null) {
      for (Document doc : aggRes.getMappedResults()) {
        String epesum = String.valueOf(doc.get("epesum") == null ? 0d : doc.get("epesum"));
        BigDecimal bigEpeSum = new BigDecimal(new DecimalFormat("#############.##").format(Double.parseDouble(epesum)));
        BigDecimal coal = bigEpeSum.multiply(new BigDecimal(400));
        BigDecimal water = bigEpeSum.multiply(new BigDecimal(4));
        BigDecimal co2 = bigEpeSum.multiply(new BigDecimal(997));
        BigDecimal so2 = bigEpeSum.multiply(new BigDecimal(30));

        map.put("epesum", bigEpeSum);
        map.put("coal", coal);
        map.put("water", water);
        map.put("co2", co2);
        map.put("so2", so2);
      }
    }
    return map;
  }

  //计算图表下方数据
  //按月 展示每天的 清洁能源发电量 节省标准煤 节省水 节省CO2减排量 节省SO2减排量
  //按年 展示每月的 清洁能源发电量 节省标准煤 节省水 节省CO2减排量 节省SO2减排量
  public List<EnergySaveEmessionReduceVo> calcEchartDatas(String date, Long subprojectId, int dateType) {
    List<Long> deviceNos = getDeviceNos(subprojectId);
    Pair<Long, Long> pair = dateToPair(date, dateType);
    AggregationResults<Document> aggRes = null;
    List<EnergySaveEmessionReduceVo> vos = new ArrayList<>();
    //按月
    if (dateType == 1) {
      Aggregation agg = Aggregation.newAggregation(
          Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("day").gte(pair.getLeft()).lte(pair.getRight())),
          Aggregation.project("day")
              .and("$statistics.EPE.diffsum").as("epediffsum"),
          Aggregation.group("day").sum("epediffsum").as("epesum"),
          Aggregation.sort(Sort.Direction.ASC, "_id")
      );
      aggRes = mongoTemplate.aggregate(agg, "deviceDayStatistics", Document.class);
    }
    if (dateType == 2) {
      Aggregation agg = Aggregation.newAggregation(
          Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("yearMonth").gte(pair.getLeft()).lte(pair.getRight())),
          Aggregation.project("yearMonth")
              .and("$statistics.EPE.diffsum").as("epediffsum"),
          Aggregation.group("yearMonth").sum("epediffsum").as("epesum"),
          Aggregation.sort(Sort.Direction.ASC, "_id")
      );
      aggRes = mongoTemplate.aggregate(agg, "deviceMonthStatistics", Document.class);
    }
    if (aggRes != null) {
      for (Document doc : aggRes.getMappedResults()) {
        Object obj = doc.get("_id");
        EnergySaveEmessionReduceVo vo = new EnergySaveEmessionReduceVo();
        if (obj != null) {
          String time = String.valueOf(obj);
          double epesum = NumberUtil.convert2(Double.parseDouble(String.valueOf(doc.get("epesum") == null ? 0d : doc.get("epesum"))));
          BigDecimal ccoal = new BigDecimal(new DecimalFormat("###############.##").format(epesum * 400));
          //Double cwater = epesum * 4;
          BigDecimal cco2 = new BigDecimal(new DecimalFormat("###############.##").format(epesum * 997));
          Double cso2 = epesum * 30;

          vo.setTime(time);
          vo.setEpe(NumberUtil.convert2(epesum));
          vo.setCoal(ccoal);
          vo.setCo2(cco2);
          vo.setSo2(NumberUtil.convert2(cso2));

          vos.add(vo);
        }
      }
    }

    return vos;
  }

  private List<Long> getDeviceNos(Long subprojectId) {
    return deviceMapper.selectList(new QueryWrapper<Device>()
        .in("device_model_type", Lists.newArrayList(2, 3))
        .eq("sub_project_id", subprojectId))
        .stream().map(Device::getDeviceNo).collect(toList());
  }

  private Pair<Long, Long> dateToPair(String date, int dateType) {
    //按月
    if (dateType == 1) {
      if (StringUtils.isNotBlank(date)) {
        long start = Long.parseLong(StringUtils.replace(date, "-", "") + "01");
        long end = Long.parseLong(StringUtils.replace(date, "-", "") + 31);
        return Pair.of(start, end);
      } else {
        String datestr = LocalDate.now().toString();
        String sdate = StringUtils.substringBeforeLast(datestr, "-");
        long start = Long.parseLong(StringUtils.replace(sdate, "-", "") + "01");
        long end = Long.parseLong(StringUtils.replace(sdate, "-", "") + 31);
        return Pair.of(start, end);
      }
    }
    //按年
    if (dateType == 2) {
      if (StringUtils.isNotBlank(date)) {
        long start = Long.parseLong(date + "01");
        long end = Long.parseLong(date + "12");
        return Pair.of(start, end);
      } else {
        int year = Year.now().getValue();
        long start = Long.parseLong(year + "01");
        long end = Long.parseLong(year + "12");
        return Pair.of(start, end);
      }
    }

    return Pair.of(-1L, -1L);
  }

  static class EnergySaveEmessionReduceVo {
    private String time;
    private Double epe;
    //节省煤
    private BigDecimal coal;
    //节省水
    //private Double water;
    //节省二氧化碳
    private BigDecimal co2;
    //节省二氧化硫
    private Double so2;

    public String getTime() {
      return time;
    }

    public void setTime(String time) {
      this.time = time;
    }

    public Double getEpe() {
      return epe;
    }

    public void setEpe(Double epe) {
      this.epe = epe;
    }

    public BigDecimal getCoal() {
      return coal;
    }

    public void setCoal(BigDecimal coal) {
      this.coal = coal;
    }

    public BigDecimal getCo2() {
      return co2;
    }

    public void setCo2(BigDecimal co2) {
      this.co2 = co2;
    }

    public Double getSo2() {
      return so2;
    }

    public void setSo2(Double so2) {
      this.so2 = so2;
    }
  }

}
