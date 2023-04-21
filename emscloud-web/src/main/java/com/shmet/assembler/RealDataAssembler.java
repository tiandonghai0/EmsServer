package com.shmet.assembler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shmet.DateTimeUtils;
import com.shmet.entity.dto.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class RealDataAssembler implements Assembler {
  @Override
  public Object assemble(Object inputData, Object outputMeta) {
    JSONArray sourceList = JSONArray.parseArray(JSONObject.toJSONString(inputData));

    JSONObject result = new JSONObject();

    JSONObject jsonObj = (outputMeta == null ? new JSONObject() : (JSONObject) outputMeta);
    String projectNo = jsonObj.getString("projectNo");
    String pageLabel = jsonObj.getString("pageLabel");
    if (StringUtils.equals("gk", projectNo) && StringUtils.equals("alarm_real", pageLabel)) {
      return convertRowsNewMethod(sourceList);
    } else {
      result.put("success", true);
      JSONObject rowsData = convertRows(sourceList);
      JSONObject data = new JSONObject();
      JSONArray deviceNos = jsonObj.getJSONArray("deviceNos");
      if (deviceNos != null && deviceNos.size() > 0) {
        for (int i = 0, len = deviceNos.size(); i < len; i++) {
          JSONObject row = rowsData.getJSONObject(deviceNos.getString(i));
          if (row != null) {
            row.put("no", row.getString("id").substring(13));
            data.put(row.getString("id"), row);
          }
        }
      }
      result.put("data", data);
      return result;
    }
  }

  public JSONObject convertRows(JSONArray sourceList) {
    JSONObject result = new JSONObject();
    for (int i = 0, len = sourceList.size(); i < len; i++) {
      JSONObject temp = sourceList.getJSONObject(i);
      String deviceNo = temp.getString("deviceNo");
      JSONObject rows = result.getJSONObject(deviceNo);
      if (temp.get("data") == null || temp.get("dateTime") == null) {
        continue;
      }
      if (rows == null) {
        rows = new JSONObject();
        rows.put("id", deviceNo);
        rows.put("callDate", DateTimeUtils.date2String(DateTimeUtils.parseDate(temp.getString("dateTime"), "yyyyMMddHHmmss"), "yyyy-MM-dd HH:mm:ss"));
        result.put(deviceNo, rows);
      }
      String tagCode = temp.getString("tagCode");
      if ("kwhTotal".equals(tagCode)) {
        JSONArray kwhTotalVals = temp.getJSONArray("data");
        rows.put("elecNum", kwhTotalVals.getDoubleValue(2));
      } else if (tagCode.contains("State")) {
        rows.put(temp.getString("tagCode"), temp.getIntValue("data"));
      } else {
        rows.put(temp.getString("tagCode"), temp.getString("data"));
      }
    }
    return result;
  }

  /**
   * 兼容国开事件告警的新的返回值的格式
   *
   * @param sourceList 源数据
   * @return JSONArray
   */
  public Result convertRowsNewMethod(JSONArray sourceList) {
    JSONArray jsonArray = new JSONArray();

    for (int i = 0; i < sourceList.size(); i++) {
      JSONObject result = new JSONObject();
      JSONObject temp = sourceList.getJSONObject(i);
      String deviceNo = temp.getString("deviceNo");
      String tagCode = temp.getString("tagCode");
      String dateTime = temp.getString("dateTime");
      String unit = temp.getString("unit");
      String content = temp.getString("content");

      result.put("unit", unit);
      result.put("id", deviceNo);
      result.put("tagCode", tagCode);
      result.put("deviceNo", deviceNo);
      result.put("time", dateTime);
      result.put("content", content);

      jsonArray.add(result);
    }
    return Result.getSuccessResultInfo(jsonArray, "国开实时数据获取成功");
  }
}
