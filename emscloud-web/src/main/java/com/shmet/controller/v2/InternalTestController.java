package com.shmet.controller.v2;

import com.shmet.entity.dto.Result;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v2")
@RequiredArgsConstructor
@Slf4j
public class InternalTestController {
  private final MongoTemplate mongoTemplate;


  @PostMapping("/internal/testp")
  public Result InternalTestP(@RequestBody @Validated InternalTestReq req) {
    long deviceNo = Long.parseLong(req.getDeviceNo());
    long leftHour = Long.parseLong(StringUtils.replace(req.getDatetime(), "-", "") + "00");
    long rightHour = Long.parseLong(StringUtils.replace(req.getDatetime(), "-", "") + "24");

    AggregationResults<Document> aggRes = mongoTemplate.aggregate(
        Aggregation.newAggregation(
            Aggregation.match(Criteria.where("deviceNo").is(deviceNo).and("hour").gte(leftHour).lte(rightHour)),
            Aggregation.project("deviceNo", "metrics").andExclude("_id"),
            Aggregation.unwind("metrics"),
            Aggregation.project("deviceNo").and("$metrics.datas.p").as("pdatas").and("$metrics.timestamp").as("timestamps"),
            Aggregation.sort(Sort.Direction.ASC, "timestamps"),
            Aggregation.group("deviceNo").push("timestamps").as("times").push("pdatas").as("vars")
        ),
        "deviceRealData",
        Document.class
    );
    Map<String, Object> map = new LinkedHashMap<>();
    for (Document doc : aggRes.getMappedResults()) {
      List<?> vars = (List<?>) doc.get("vars");
      List<Number> times = (List<Number>) doc.get("times");
      if (vars != null && times != null && vars.size() == times.size()) {
        map.put("time", convertToTimes(times));
        map.put("var", vars);
      }
    }
    if (map.size() == 0) {
      map.put("time", new ArrayList<>());
      map.put("var", new ArrayList<>());
    }
    return Result.getSuccessResultInfo(map);
  }

  private List<String> convertToTimes(List<Number> times) {
    return times.stream().map(t -> StringUtils.substring(String.valueOf(t), 8)).collect(Collectors.toList());
  }

  @Getter
  @Setter
  static class InternalTestReq {
    @NotBlank(message = "deviceNo不能为空")
    private String deviceNo;
    @NotBlank(message = "查询时间不能为空")
    @Pattern(regexp = "^[0-9]{4}-[0-9]{2}-[0-9]{2}$", message = "日期格式不正确,格式应为yyyy-MM-dd")
    private String datetime;
  }
}
