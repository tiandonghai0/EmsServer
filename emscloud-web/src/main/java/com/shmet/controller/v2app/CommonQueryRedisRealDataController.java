package com.shmet.controller.v2app;

import com.shmet.aop.UserLoginToken;
import com.shmet.bean.RealDataItem;
import com.shmet.entity.dto.Result;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@RestController
@RequestMapping("/v2app")
@RequiredArgsConstructor
public class CommonQueryRedisRealDataController {

  private final RedisTemplate<String, Object> redisTemplate;

  @PostMapping("/redis/real/query")
  @UserLoginToken
  public Result commonQuery(@RequestBody @Validated QueryReq req) {
    List<Long> deviceNos = req.getDeviceNo();
    List<String> tagcodes = req.getTagcode();

    List<QueryRes> queryResList = new ArrayList<>();

    for (Long dno : deviceNos) {
      QueryRes queryRes = new QueryRes();
      queryRes.setDeviceNo(dno);
      LinkedHashMap<String, Object> map = new LinkedHashMap<>();
      String time = null;
      for (String tcode : tagcodes) {
        String key = dno + "." + tcode;
        RealDataItem val = (RealDataItem) redisTemplate.opsForHash().get("RealDataRedisDao.saveRealDataCache", key);
        if (val != null) {
          time = val.getDateTime();
          map.put(tcode, val.getData());
        } else {
          map.put(tcode, null);
        }
      }
      map.put("datetime", time);
      queryRes.setKeyVal(map);

      queryResList.add(queryRes);
    }

    return Result.getSuccessResultInfo(queryResList);
  }

  @Getter
  @Setter
  static class QueryReq {
    @NotNull(message = "deviceNo不能是null")
    private List<Long> deviceNo;
    @NotNull(message = "tagcode不能是null")
    private List<String> tagcode;
  }

  @Getter
  @Setter
  static class QueryRes {
    private Long deviceNo;
    private LinkedHashMap<String, Object> keyVal;
  }

}
