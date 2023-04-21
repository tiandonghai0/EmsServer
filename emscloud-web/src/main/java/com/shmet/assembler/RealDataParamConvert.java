package com.shmet.assembler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.shmet.bean.RealDataItem;
import com.shmet.helper.SpringContextUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RealDataParamConvert {

  static private final Logger logger = LoggerFactory.getLogger(RealDataParamConvert.class);

  private static StringRedisTemplate stringRedisTemplate;

  static {
    if (stringRedisTemplate == null) {
      stringRedisTemplate = SpringContextUtil.getBean("stringRedisTemplate");
    }
  }

  static final Map<String, String> map = JSON.parseObject(stringRedisTemplate.opsForValue().get("alarmNames")
      , new TypeReference<Map<String, String>>() {
      });

  public static String readFile(String projectNo, String fileName,String path) {
    // 读取原始json文件并进行操作和输出
    String fileContent = "";
    try {
      //System.out.println("json文件："+path + projectNo + "_" + fileName + ".json");
      File file = new File(path + projectNo + "_" + fileName + ".json");
      BufferedReader br = new BufferedReader(new FileReader(file));
      StringBuilder fileStr = new StringBuilder();
      String s;
      while ((s = br.readLine()) != null)
        fileStr.append(s);
      fileContent = fileStr.toString();
      br.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return fileContent;
  }

  /**
   * 此方法主要是根据 projectNo 和  pageLabel 读取json 参数
   * 然后将 json中的 deviceNos 和 tagCodes 进行聚合 返回 List<RealDataItem>
   *
   * @param pageDataItem example:{projectNo:"gk",pageLabel:"index"} gk_alarm_real.json 3383 项
   * @return List<RealDataItem>
   */
  public static List<RealDataItem> convert(JSONObject pageDataItem,String path) {
    List<RealDataItem> realDataItemList = new ArrayList<>();
    try {
      String projectNo = pageDataItem.getString("projectNo");
      String pageLabel = pageDataItem.getString("pageLabel");
      boolean projectNoIsGK = StringUtils.equals("gk", projectNo);
      boolean pageLableIsAlarmReal = StringUtils.equals("alarm_real", pageLabel);
      //logger.info("projectNo: " + projectNo + " pageLabel: " + pageLabel);
      //根据前端传入的参数 从 json 文件中读取
      String fileContent = readFile(projectNo, pageLabel,path);
      /*
       * 转换为 json数组 大致格式如下:
       * {
       * 		"deviceNos": [
       * 			"202010010010002",
       * 			"202010010010010"
       * 		],
       * 		"tagCodes": [
       * 			"IN_T",
       * 			"CP",
       * 			"HP"
       * 		]
       * },
       * {
       * 		"deviceNos": [
       * 			"202010010010003"
       * 		],
       * 		"tagCodes": [
       * 			"FE1"
       * 		]
       *  }
       *  ...
       */
      JSONArray paramGroups = JSONArray.parseArray(fileContent);
      JSONArray deviceNos = new JSONArray();
      for (int k = 0; k < paramGroups.size(); k++) {
        JSONObject params = paramGroups.getJSONObject(k);

        JSONArray tempDeviceNos = params.getJSONArray("deviceNos");
        deviceNos.fluentAddAll(tempDeviceNos);

        JSONArray tagCodes = params.getJSONArray("tagCodes");

        //tempDeviceNos.size() * tagCodes.size() 多个 RealDataItem 对象
        for (Object tempDeviceNo : tempDeviceNos) {
          String deviceNo = (String) tempDeviceNo;
          for (Object code : tagCodes) {
            String tagCode = (String) code;
            RealDataItem item = new RealDataItem();
            //System.out.println("tagCode:"+tagCode);
           try{
            boolean b = map.containsKey(tagCode);
            //如果为国开事件报警 在进行设置
            if (b && projectNoIsGK && pageLableIsAlarmReal) {
              String tCode = map.get(tagCode);
              item.setUnit(tCode);
            }}catch (Exception ex){}
            item.setDeviceNo(deviceNo);
            item.setTagCode(tagCode);
            realDataItemList.add(item);
          }
        }
      }
      //将设备号保存到参数对象中，用于后台数据排序时使用。
      pageDataItem.put("deviceNos", deviceNos);

    } catch (Exception e) {
      e.printStackTrace();
    }
    return realDataItemList;
  }
}
