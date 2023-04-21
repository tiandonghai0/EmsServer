package com.shmet.handler.v2;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.shmet.bean.RealDataItem;
import com.shmet.utils.DateUtils;
import com.shmet.vo.Digit22Vo;
import com.shmet.vo.DigitVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author
 */
@Service
public class BmsRealService {
  public static final String REALDATA_CACHE = "RealDataRedisDao.saveRealDataCache";

  @Autowired
  private LoginService loginService;

  @Autowired
  private SubProjectService subProjectService;

  @Autowired
  private CommonService commonService;

  @Autowired
  private RunStatusService runStatusService;

  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  public Map<Long, JSONObject> getBmsDataFromRedis(Long subprojectId) {
    Map<Long, JSONObject> map = new LinkedHashMap<>();
    List<Long> bmsDeviceNos;
    if (subprojectId == 20202002001L) {
      bmsDeviceNos = loginService.getBatteryDeviceNosBySubprojectId(subprojectId, 27);
    } else {
      bmsDeviceNos = loginService.getBatteryDeviceNosBySubprojectId(subprojectId);
    }
    Double totalCapacity = subProjectService.getTotalCapacity(subprojectId);
    //组串数据
    List<Long> digitNos = loginService.getBatteryDeviceNosBySubprojectId(subprojectId, 29);
    List<DigitVo> vos = Lists.newArrayList();

    for (Long dno : bmsDeviceNos) {
      JSONObject jsonObj = new JSONObject();
      jsonObj.put("totalCapacity", totalCapacity == null ? 0d : totalCapacity);
      digitNos.forEach(dio -> {
        Digit22Vo vo = new Digit22Vo(
            getRedisTagcodeData(dio, "S_STATUS") == null ? getRedisTagcodeData(dio, "CON") : getRedisTagcodeData(dio, "S_STATUS"),
            getRedisTagcodeData(dio, "SOC"),
            getRedisTagcodeData(dio, "SOH"),
            getRedisTagcodeData(dio, "USMAX") == null ? getRedisTagcodeData(dio, "HCV") : getRedisTagcodeData(dio, "USMAX"),
            getRedisTagcodeData(dio, "USMIN") == null ? getRedisTagcodeData(dio, "LCV") : getRedisTagcodeData(dio, "USMIN"),
            getRedisTagcodeData(dio, "TSMAX") == null ? getRedisTagcodeData(dio, "HBT") : getRedisTagcodeData(dio, "TSMAX"),
            getRedisTagcodeData(dio, "SOCSMAX") == null ? getRedisTagcodeData(dio, "HSOC") : getRedisTagcodeData(dio, "SOCSMAX"),
            getRedisTagcodeData(dio, "SOHSMAX") == null ? getRedisTagcodeData(dio, "HSOH") : getRedisTagcodeData(dio, "SOHSMAX"),

            getRedisTagcodeData(dio, "SOCSMIN") == null ? getRedisTagcodeData(dio, "LSOC") : getRedisTagcodeData(dio, "SOCSMIN"),
            getRedisTagcodeData(dio, "SOHSMIN") == null ? getRedisTagcodeData(dio, "LSOH") : getRedisTagcodeData(dio, "SOHSMIN"),
            getRedisTagcodeData(dio, "MAX_B") == null ? getRedisTagcodeData(dio, "NOHCV") : getRedisTagcodeData(dio, "MAX_B"),
            getRedisTagcodeData(dio, "MIN_B") == null ? getRedisTagcodeData(dio, "NOLCV") : getRedisTagcodeData(dio, "MIN_B"),
            getRedisTagcodeData(dio, "MIN_SOC_B") == null ? getRedisTagcodeData(dio, "NOLSOC") : getRedisTagcodeData(dio, "MIN_SOC_B"),
            getRedisTagcodeData(dio, "MIN_SOH_B") == null ? getRedisTagcodeData(dio, "NOLSOH") : getRedisTagcodeData(dio, "MIN_SOH_B"),
            getRedisTagcodeData(dio, "MAX_T") == null ? getRedisTagcodeData(dio, "NOHBT") : getRedisTagcodeData(dio, "MAX_T"),

            getRedisTagcodeData(dio, "WSTS"),
            getRedisTagcodeData(dio, "WALM"),
            getRedisTagcodeData(dio, "WFLT"),
            getRedisTagcodeData(dio, "I"),
            getRedisTagcodeData(dio, "U"),
            getRedisTagcodeData(dio, "LBT"),
            getRedisTagcodeData(dio, "NOLBT"),
            getRedisTagcodeData(dio, "NOLSOC")
        );
        vo.setDigitNo(dio);
        vos.add(vo);
      });
      jsonObj.put("digitData", vos);
      Set<String> items = commonService.listKeysByPattern(REALDATA_CACHE, dno + ".*");
      items.forEach(item -> {
        RealDataItem realDataItem = JSON.parseObject(item, RealDataItem.class);
        jsonObj.put(realDataItem.getTagCode(), realDataItem.getData());
        jsonObj.put("calldate", DateUtils.strConvertDateStr(realDataItem.getDateTime(), "-", ":"));
      });

      map.put(dno, jsonObj);
    }
    return map;
  }

  public Double getRedisTagcodeData(Long deviceNo, String tagCode) {
    RealDataItem dataItem = (RealDataItem) redisTemplate.opsForHash().get(REALDATA_CACHE, deviceNo + "." + tagCode);
    if (dataItem != null) {
      Object data = dataItem.getData();
      String sdata = String.valueOf(data);
      if (!StringUtils.equalsIgnoreCase("null", sdata) && NumberUtils.isCreatable(sdata)) {
        return NumberUtils.toDouble(sdata);
      }
    }
    return null;
  }

}
