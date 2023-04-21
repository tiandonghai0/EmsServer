package com.shmet.controller.v2app;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.StrUtil;
import com.shmet.aop.UserLoginToken;
import com.shmet.entity.dto.Result;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ObjectOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v2app")
@RequiredArgsConstructor
@Slf4j
public class CommonQueryMongoHisDataController {

  private final MongoTemplate mongoTemplate;
  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHH");

  @PostMapping("/mongo/his/query")
  @UserLoginToken
  public Result commonQuery(@RequestBody @Validated MongoQueryReq req) {
    Long deviceNo = req.getDeviceNo();
    String date = req.getDate();
    Pair<Long, Long> pairTime = getPairTime(date);
    List<String> tagcodes = req.getTagCodes();
    List<MongoQueryRes> queryResList = new ArrayList<>();

    Aggregation agg = Aggregation.newAggregation(
        Aggregation.match(Criteria.where("deviceNo").is(deviceNo).and("hour").gte(pairTime.getKey()).lte(pairTime.getValue())),
        Aggregation.project("deviceNo", "hour", "metrics").andExclude("_id"),
        Aggregation.unwind("metrics"),
        Aggregation.project("deviceNo", "hour").and("metrics.timestamp").as("timestamp")
            .and(ObjectOperators.ObjectToArray.toArray("$metrics.datas")).as("datas"),
        Aggregation.unwind("datas"),
        Aggregation.project("deviceNo", "hour", "timestamp").and("datas.k").as("tcode").and("datas.v").as("tcodeval"),
        Aggregation.match(Criteria.where("tcode").in(tagcodes)),
        Aggregation.sort(Sort.by("timestamp").ascending()),
        Aggregation.group("deviceNo", "tcode").push("tcodeval").as("tcodevals").push("timestamp").as("timestamps")
    );

    AggregationResults<Document> aggRes = mongoTemplate.aggregate(agg, "deviceRealData", Document.class);
    for (String tagcode : tagcodes) {
      for (Document doc : aggRes.getMappedResults()) {
        String tcode = doc.getString("tcode");
        if (StrUtil.equals(tagcode, tcode)) {
          //Long queryDeviceNo = doc.getLong("deviceNo");
          List<String> timestamps = ((List<Long>) doc.get("timestamps", List.class)).stream().map(String::valueOf).collect(Collectors.toList());
          List<Object> tcodevals = ((List<Object>) doc.get("tcodevals", List.class));
          List<String> vals = convertVals(tcodevals);

          log.info("tcode: {} , times size: {} , vals size : {}", tcode, timestamps.size(), vals.size());
          MongoQueryRes mongoQueryRes = new MongoQueryRes();
          //mongoQueryRes.setDeviceNo(queryDeviceNo);
          mongoQueryRes.setTcode(tcode);
          mongoQueryRes.setTimes(timestamps);
          mongoQueryRes.setVals(vals);
          queryResList.add(mongoQueryRes);

          break;
        }
      }

      if(!queryResList.stream().map(MongoQueryRes::getTcode).collect(Collectors.toList()).contains(tagcode)){
        MongoQueryRes mongoQueryRes = new MongoQueryRes();
        //mongoQueryRes.setDeviceNo(deviceNo);
        mongoQueryRes.setTcode(tagcode);
        mongoQueryRes.setTimes(new ArrayList<>());
        mongoQueryRes.setVals(new ArrayList<>());
        queryResList.add(mongoQueryRes);
      }

    }

    return Result.getSuccessResultInfo(queryResList);
  }

  private List<String> convertVals(List<Object> tcodevals) {

    List<String> tcodeVals = new ArrayList<>(tcodevals.size());

    for (Object tcodeval : tcodevals) {
      if (tcodeval instanceof List<?>) {
        List<?> list = (List<?>) tcodeval;
        tcodevals.add(String.valueOf(list.get(0)));
      } else {
        tcodeVals.add(String.valueOf(tcodeval));
      }
    }
    return tcodeVals;
  }

  private Pair<Long, Long> getPairTime(String date) {
    long begin, end;
    if (StrUtil.isNotBlank(date)) {
      begin = Long.parseLong(StrUtil.replace(date, "-", "") + "00");
      end = Long.parseLong(StrUtil.replace(date, "-", "") + "24");
    } else {
      begin = Long.parseLong(StrUtil.replace(LocalDate.now().toString(), "-", "") + "00");
      String nowTime = formatter.format(LocalDateTime.now());
      end = Long.parseLong(nowTime);
    }
    return Pair.of(begin, end);
  }

  @Getter
  @Setter
  static class MongoQueryReq {
    @NotNull(message = "deviceNo不能是null")
    private Long deviceNo;
    //格式 yyyy-MM-dd 不传默认是当天
    private String date;
    @NotNull(message = "tagcode不能是null")
    private List<String> tagCodes;
  }

  @Getter
  @Setter
  static class MongoQueryRes {
    //private Long deviceNo;
    private String tcode;
    private List<String> times;
    private List<String> vals;
  }

}