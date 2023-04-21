package com.shmet.controller.v2;

import cn.hutool.core.collection.CollUtil;
import com.shmet.DateTimeUtils;
import com.shmet.aop.UserLoginToken;
import com.shmet.bean.FaultData;
import com.shmet.entity.dto.Result;
import com.shmet.entity.dto.ResultSearch;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/faultdata")
@RequiredArgsConstructor
public class FaultDataController {

  private final MongoTemplate mongoTemplate;
  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

  @PostMapping("/save")
  @UserLoginToken
  public Result saveFaultData(@RequestBody @Validated FaultDataReq req) {
    FaultData faultData = new FaultData();
    BeanUtils.copyProperties(req, faultData);
    return Result.getSuccessResultInfo(mongoTemplate.save(faultData));
  }

  @PostMapping("/existgz")
  @UserLoginToken
  public Result existGz(@RequestBody SubprojectIdForm form) {
    Long subprojectId = form.getSubprojectId();

    Pair<Long, Long> pair = getBefore1Hour();

    //0正常/ 1警告 /2故障
    List<FaultData> faultData = mongoTemplate.find(
        Query.query(Criteria.where("subprojectId").is(subprojectId).and("createTime").gte(pair.getLeft()).lte(pair.getRight()).and("faultType").is(2)),
        FaultData.class,
        "faultData"
    );

    boolean ne = CollUtil.isNotEmpty(faultData);
    if (ne) {
      return Result.getSuccessResultInfo();
    } else {
      return Result.getErrorResultInfo("不存在故障信息");
    }
  }

  private Pair<Long, Long> getBefore1Hour() {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime before1HourTime = now.minusHours(1);
    long l = Long.parseLong(formatter.format(before1HourTime));
    long r = Long.parseLong(formatter.format(now));
    return Pair.of(l, r);
  }

  @PostMapping("/page/list")
  @UserLoginToken
  public ResultSearch pageList(@RequestBody FaultCodePageForm form) {
    String createTime = form.getDate();
    Long subprojectId = form.getSubprojectId();
    Integer pageNum = form.getPageNum();
    Integer pageSize = form.getPageSize();

    Integer type = form.getType();

    ResultSearch pageR = new ResultSearch(pageNum, pageSize);
    Pair<Long, Long> pair;
    if(form.dateType==1){
      String newStr = StringUtils.replace(LocalDate.now().toString(), "-", "");
      Calendar nowTime = Calendar.getInstance();
      nowTime.add(Calendar.MINUTE,-30);
      Long start =DateTimeUtils.dataTimeToLong(nowTime.getTime());
      Long end =DateTimeUtils.getCurrentLongTime();
      pair= Pair.of(start, end);
    }else {
      pair = getPairTime(createTime);
    }
    long skipCount = (long) (pageNum - 1) * pageSize;

    Query query = Query.query(Criteria.where("subprojectId").is(subprojectId).and("createTime").gte(pair.getKey()).lte(pair.getValue()));
    if (type == null) {
      query.addCriteria(Criteria.where("faultType").in(1, 2));
    } else {
      query.addCriteria(Criteria.where("faultType").is(type));
    }
    long total = mongoTemplate.count(query, FaultData.class);
    pageR.setTotalCount(total);
    long l = total / pageSize;
    long lb = total % pageSize;
    pageR.setPageCount(lb == 0 ? l : l + 1);
    Query pageQuery = query.with(Sort.by(Sort.Direction.DESC, "createTime")).skip(skipCount).limit(pageSize);

    List<FaultCodeResponse> responses = mongoTemplate.find(pageQuery, FaultData.class)
        .stream()
        .map(d -> new FaultCodeResponse(d.getDeviceName(), d.getTagcode(), d.getTagcodeName(), d.getDataValue(), d.getCreateTime()))
        .collect(toList());

    pageR.setData(responses);
    pageR.setCode("0");
    pageR.setSuccess(true);

    return pageR;
  }

  //timestr yyyy-MM-dd
  private Pair<Long, Long> getPairTime(String timestr) {
    if (StringUtils.isNotBlank(timestr)) {
      String newStr = timestr.replaceAll("-", "");
      Long start = Long.parseLong(newStr + "000000");
      Long end = Long.parseLong(newStr + "240000");
      return Pair.of(start, end);
    } else {
      String newStr = StringUtils.replace(LocalDate.now().toString(), "-", "");
      Long start = Long.parseLong(newStr + "000000");
      Long end = DateTimeUtils.getCurrentLongTime();
      return Pair.of(start, end);
    }
  }

  @Getter
  @Setter
  static class SubprojectIdForm {
    @NotNull(message = "subprojectId不能为空")
    private Long subprojectId;
  }

  @Getter
  @Setter
  static class FaultCodePageForm {
    private String date;
    @NotNull(message = "subprojectId不能为空")
    private Long subprojectId;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    //不传就是[1,2] 1 警告 2 故障
    private Integer type;
    private Integer dateType=0; //0按天，1最近30分钟，3按时间段
  }

  @Getter
  @Setter
  @NoArgsConstructor
  static class FaultCodeResponse {
    private String deviceName;
    private String tagcode;
    private String tagcodeName;
    private String dataValue;
    private Long createTime;

    public FaultCodeResponse(String deviceName, String tagcode, String tagcodeName, String dataValue, Long createTime) {
      this.deviceName = deviceName;
      this.tagcode = tagcode;
      this.tagcodeName = tagcodeName;
      this.dataValue = dataValue;
      this.createTime = createTime;
    }
  }

  @Getter
  @Setter
  static class FaultDataReq {
    @NotBlank(message = "deviceId不能为空")
    private String deviceId;
    @NotNull(message = "subprojectId不能为空")
    private Long subprojectId;
    private String deviceName;
    @NotNull(message = "modelType不能为空")
    private Integer modelType;
    @NotBlank(message = "tagcode不能为空")
    private String tagcode;
    private String tagcodeName;
    private String dataValue;
    private String content;
    @NotNull(message = "createTime不能为空")
    private Long createTime = DateTimeUtils.getCurrentLongTime();
    private Integer faultType;
  }

}
