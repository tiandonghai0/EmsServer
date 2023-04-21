package com.shmet.utils;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public final class RequestContextUtils {
  /**
   * 读取请求参数转换JSONObject对象
   */
  public static JSONObject readJson(HttpServletRequest request) {
    JSONObject jsonObject = new JSONObject();
    if (request == null) {
      return jsonObject;
    }
    Map<String, String[]> map = request.getParameterMap();

    for (Map.Entry<String, String[]> entry : map.entrySet()) {
      String key = entry.getKey().replaceAll("[\\W]", "");
      if (key.equals("_")) continue;
      if (entry.getValue().length > 1) {
        jsonObject.put(key, entry.getValue());
      } else {
        jsonObject.put(key, entry.getValue()[0]);
      }
    }
    return jsonObject;
  }
}
