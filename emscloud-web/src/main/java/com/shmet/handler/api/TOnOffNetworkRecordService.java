package com.shmet.handler.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shmet.bean.TOnOffNetworkRecord;
import com.shmet.dao.TOnOffNetworkRecordMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ObjectOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author
 */
@Service
public class TOnOffNetworkRecordService {

  public static final Logger logger = LoggerFactory.getLogger(TOnOffNetworkRecordService.class);

  @Resource
  TOnOffNetworkRecordMapper mapper;

  @Autowired
  MongoTemplate mongoTemplate;

  /**
   * 根据传入的时间查询 mysql中的并离网时间记录
   *
   * @param date 前端传入的时间
   * @return List<Triple < Long, Long, Long>>
   */
  public List<Triple<Long, Long, Long>> getMergeLeaveTime(String date, Long subProjectId) {
    Long startdate = Long.valueOf(date.replace("-", "") + "000000");
    Long enddate = Long.valueOf(date.replace("-", "") + "235959");
    QueryWrapper<TOnOffNetworkRecord> queryWrapper = new QueryWrapper<TOnOffNetworkRecord>().eq("f_sub_project_id", subProjectId);
    queryWrapper.and(wrapper -> wrapper.between("f_on_time", startdate, enddate).or().between("f_off_time", startdate, enddate));
    return mapper.selectList(queryWrapper).stream()
        .map(o -> Triple.of(o.getAmmeterDeviceNo(), o.getfOffTime(), o.getfOnTime()))
        .sorted()
        .collect(Collectors.toList());
  }

  /**
   * 获取阶段性离网时间数据 f_off_time 离网时间  f_on_time 并网时间
   * 1.如果当天都没有 离网时间 并网时间 则直接返回(没有并离网数据)
   * 2.如果没有并网时间或者并网时间在当天之后 则需查询出 离网时间 ~ 当天的23:59:59的数据
   * 3.如果没有离网时间或者离网时间在当天之前 则需查询出 当天的00:00:00 ~ 并网时间的数据
   *
   * @return List<DeviceRealData>
   */
  public JSONObject getMergeLeavelDataFromMongo(String date, Long subProjectId) {
    List<Triple<Long, Long, Long>> mergeLeaveTimes = getMergeLeaveTime(date, subProjectId);
    List<Document> result = new ArrayList<>();
    Sort sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "timestamp"));
    Map<Long, Double> resMap = new HashMap<>();
    for (Triple<Long, Long, Long> item : mergeLeaveTimes) {
      Long deviceNo = item.getLeft();
      //leaveTime:离网时间  mergeTime:并网时间
      Long leaveTime = item.getMiddle();
      Long mergeTime = item.getRight();
      //当天的最小最大时间
      long currentMinDate = Long.parseLong(date.replace("-", "") + "000000");
      long currentMaxDate = Long.parseLong(date.replace("-", "") + "235959");
      if (leaveTime == null || leaveTime < currentMinDate) {
        leaveTime = currentMinDate;
      }
      if (mergeTime == null || mergeTime > currentMaxDate) {
        mergeTime = currentMaxDate;
      }
      Aggregation agg = Aggregation.newAggregation(Aggregation.match(Criteria.where("deviceNo")
              .is(deviceNo).and("metrics.timestamp").gte(leaveTime).lte(mergeTime)),
          Aggregation.project().andExclude("_id").andExclude("statistics"),
          Aggregation.unwind("metrics"),
          Aggregation.project("deviceNo").and("metrics.timestamp").as("timestamp")
              .and("metrics.datas").as("datas"),
          Aggregation.match(Criteria.where("timestamp").gte(leaveTime).lte(mergeTime)),
          Aggregation.project("deviceNo").and("timestamp").as("timestamp")
              .and((ObjectOperators.valueOf("datas").toArray())).as("datas"),
          Aggregation.unwind("datas"),
          Aggregation.sort(sort),
          Aggregation.facet(Aggregation.limit(10000)).as("result")
              .and(Aggregation.count().as("count")).as("totalCount"));
      AggregationResults<Document> aggResults = mongoTemplate.aggregate(agg, "deviceRealData", Document.class);
      Document document = aggResults.iterator().next();
      List<Document> tmpList = (List<Document>) document.get("result");
      Double startVal = 0d;
      Double endVal = 0d;
      for (Document value : tmpList) {
        Map<String, Object> docMap = (Map<String, Object>) value.get("datas");
        if (StringUtils.equals("EPE", (String) docMap.get("k"))) {
          startVal = ((List<Double>) docMap.get("v")).get(1);
          break;
        }
      }
      for (int j = tmpList.size() - 1; j > 0; j--) {
        Map<String, Object> docMap = (Map<String, Object>) tmpList.get(j).get("datas");
        if (StringUtils.equals("EPE", (String) docMap.get("k"))) {
          endVal = ((List<Double>) docMap.get("v")).get(1);
          break;
        }
      }
      if (resMap.containsKey(deviceNo)) {
        resMap.put(deviceNo, (endVal - startVal) + resMap.get(deviceNo));
      } else {
        resMap.put(deviceNo, endVal - startVal);
      }
      result.addAll(tmpList);
    }
    logger.info("result size: {}", result.size());
    String jsonString = JSON.toJSONString(result);
    JSONArray jsonArray = JSONObject.parseArray(jsonString);
    JSONObject jsonResult = new JSONObject();
    Set<String> timeSet = new LinkedHashSet<>();
    ArrayList<BigDecimal> varList = new ArrayList<>();
    for (int i = 0; i < jsonArray.size(); i++) {
      JSONObject con = jsonArray.getJSONObject(i);
      JSONObject datas = con.getJSONObject("datas");
      String key = datas.getString("k");
      if (StringUtils.equals("P", key)) {
        timeSet.add(con.getString("timestamp").substring(8));
        //varList.add(datas.getBigDecimal("v"));
      }
    }
    //timelist 重新升序排列
    List<Long> sortTimeList = timeSet.stream().map(Long::parseLong).sorted().collect(Collectors.toList());
    Map<Long, BigDecimal> map = new LinkedHashMap<>();
    for (Long jl : sortTimeList) {
      for (int i = 0; i < jsonArray.size(); i++) {
        JSONObject con = jsonArray.getJSONObject(i);
        Long lg = Long.parseLong(con.getString("timestamp").substring(8));
        if (jl.equals(lg) && StringUtils.equalsIgnoreCase(con.getJSONObject("datas").getString("k"), "P")) {
          BigDecimal bg = con.getJSONObject("datas").getBigDecimal("v");
          map.merge(jl, bg, (a, b) -> b.add(a));
          varList.add(map.get(jl));
        }
      }
    }
    jsonResult.put("time", sortTimeList);
    jsonResult.put("var", varList);
    jsonResult.put("totalCount", varList.size());
    jsonResult.put("leaveNetElectric", resMap);

    return jsonResult;
  }

}
