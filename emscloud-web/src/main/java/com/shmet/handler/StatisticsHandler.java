package com.shmet.handler;

import java.util.*;

import com.alibaba.fastjson.JSONObject;
import com.shmet.entity.mongo.DeviceRealData;
import com.shmet.entity.mongo.RealMetricsItem;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shmet.ArithUtil;
import com.shmet.Consts;
import com.shmet.bean.StatisticsRequest;
import com.shmet.bean.StatisticsRequestItem;
import com.shmet.bean.StatisticsResponse;
import com.shmet.dao.mongodb.DeviceConfigDao;
import com.shmet.dao.mongodb.DeviceRealDataDao;
import com.shmet.dao.mongodb.DeviceStatisticsDataDao;
import com.shmet.dao.mongodb.DeviceTagConfigDao;
import com.shmet.dao.mongodb.FaultRealDataDao;
import com.shmet.dao.mysql.TDeviceDao;
import com.shmet.entity.mongo.DeviceConfig;
import com.shmet.entity.mongo.DeviceTagConfig;

@Component
public class StatisticsHandler {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  DeviceRealDataDao deviceRealDataDao;

  @Autowired
  FaultRealDataDao faultRealDataDao;

  @Autowired
  DeviceTagConfigDao deviceTagConfigDao;

  @Autowired
  DeviceStatisticsDataDao deviceStatisticsDataDao;

  @Autowired
  TDeviceDao deviceDao;

  @Autowired
  DeviceConfigDao deviceConfigDao;

  public String batchSingleStatistics(StatisticsRequest requestData, int timeType)
      throws JsonProcessingException {
    Map<String, Long> deviceMap = new LinkedHashMap<>();
    Map<String, String> tagCodeMap = new LinkedHashMap<>();
    Map<String, DeviceConfig> deviceConfigMap = new HashMap<>();
    Map<String, DeviceTagConfig> deviceTagConfigMap = new HashMap<>();
    String rst = null;
    List<StatisticsRequestItem> statisticsItems = requestData.getStatisticsRequestItemList();
    if (statisticsItems != null && statisticsItems.size() > 0) {
      //如果存在 循环遍历这里的每一项
      for (StatisticsRequestItem item : statisticsItems) {
        if (!deviceMap.containsKey(item.getDeviceNo())) {
          // 通过 deviceNo 获取deviceConfig
          List<DeviceConfig> deviceConfigList = deviceConfigDao.findDeviceConfigByDeviceNo(Long.valueOf(StringUtils.isBlank(item.getDeviceNo()) ? "-1" : item.getDeviceNo()));
          String deviceModel = null;

          //{deviceNo.tagCode,deviceConfig对象}
          for (DeviceConfig config : deviceConfigList) {
            deviceConfigMap.put(item.getDeviceNo() + config.getTagCode(), config);
            if (deviceModel == null) {
              deviceModel = config.getDeviceModel();
            }
          }

          if (deviceConfigList.size() > 0) {
            // 获取deviceTagConfig
            List<DeviceTagConfig> deviceTagConfigList = deviceTagConfigDao.findDeviceTagConfigByDeviceModel(deviceModel);
            for (DeviceTagConfig config : deviceTagConfigList) {
              deviceTagConfigMap.put(deviceModel + config.getTagCode(), config);
            }
          }

        }
        deviceMap.put(item.getDeviceNo(), Long.valueOf(item.getDeviceNo()));
        for (String tagCode : item.getTagCodeList()) {
          tagCodeMap.put(tagCode, tagCode);
        }
      }
      Collection<Long> deviceNoList = deviceMap.values();
      Collection<String> tagCodeList = tagCodeMap.values();
      int skip = requestData.getPageNum() <= 1 ? 0 : (requestData.getPageNum() - 1) * requestData.getPageCount();
      AggregationResults<Document> rstList = null;
      // 查询统计数据的时间类型-2-小时
      if (timeType == Consts.TIME_TYPE_HOUR) {
        rstList =
            deviceRealDataDao.aggregateHourByDeviceTimeTagForPage(deviceNoList, tagCodeList,
                requestData.getStartTime(), requestData.getEndTime(), skip,
                requestData.getPageCount(), requestData.getSortFlag(), 0);
      } //查询统计数据的时间类型-3-天
      else if (timeType == Consts.TIME_TYPE_DAY) {
        rstList =
            deviceStatisticsDataDao.aggregateDayByDeviceTimeTagForPage(deviceNoList, tagCodeList,
                requestData.getStartTime(), requestData.getEndTime(), skip,
                requestData.getPageCount(), requestData.getSortFlag(), 0);
      } //查询统计数据的时间类型-4-月
      else if (timeType == Consts.TIME_TYPE_MONTH) {
        rstList =
            deviceStatisticsDataDao.aggregateMonthByDeviceTimeTagForPage(deviceNoList, tagCodeList,
                requestData.getStartTime(), requestData.getEndTime(), skip,
                requestData.getPageCount(), requestData.getSortFlag(), 0);
      }//查询统计数据的时间类型-1-实时数据
      else if (timeType == Consts.TIME_TYPE_REAL) {
        rstList =
            deviceRealDataDao.aggregateRealDataByDeviceTimeTagForPage(deviceNoList, tagCodeList,
                requestData.getStartTime(), requestData.getEndTime(), skip,
                requestData.getPageCount(), requestData.getSortFlag(), 0);
      }

      Document resData = rstList.iterator().next();
      ObjectMapper om = new ObjectMapper();

      StatisticsResponse statisticsResponse = new StatisticsResponse();
      List<Document> rstDocList = (List<Document>) resData.get("result");
      List<Document> rstCountList = (List<Document>) resData.get("totalCount");
      if (rstCountList != null && rstCountList.size() > 0) {
        statisticsResponse.setTotalCount((int) rstCountList.get(0).get("count"));
      }
      // 检查是否需要计算
      if (rstDocList != null && rstDocList.size() > 0) {
        for (Document document : rstDocList) {
          Long deviceNo = document.getLong("deviceNo");
          Document item;
          if (timeType == Consts.TIME_TYPE_REAL) {
            item = document.get("datas", Document.class);
          } else {
            item = document.get("statistics", Document.class);
          }
          String tagCode = item.getString("k");
          DeviceConfig deviceConfig = null;
          String key = deviceNo.toString() + tagCode;
          if (deviceConfigMap.containsKey(key)) {
            deviceConfig = deviceConfigMap.get(key);
          }
          if (deviceConfig != null) {
            if (deviceConfig.getConfig().containsKey("MultiplyingFactor")) {
              double multiplyingFactor = (Double) deviceConfig.getConfig().get("MultiplyingFactor");
              Object obj = item.get("v");
              if (obj instanceof Document) {
                DeviceTagConfig deviceTagConfig = deviceTagConfigMap.get(deviceConfig.getDeviceModel() + tagCode);
                if ("diffsum".equalsIgnoreCase(deviceTagConfig.getRealRecordMethod())) {
                  multiplyingDocument((Document) obj, "first", multiplyingFactor);
                  multiplyingDocument((Document) obj, "last", multiplyingFactor);
                  multiplyingDocument((Document) obj, "diffsum", multiplyingFactor);
                } else {
                  multiplyingDocument((Document) obj, "max", multiplyingFactor);
                  multiplyingDocument((Document) obj, "min", multiplyingFactor);
                  multiplyingDocument((Document) obj, "last", multiplyingFactor);
                  multiplyingDocument((Document) obj, "sum", multiplyingFactor);
                }
              } else if (obj instanceof List) {
                List<Double> ls = (List<Double>) obj;
                for (int i = 0; i < ls.size(); i++) {
                  ls.set(i, ArithUtil.mul(ls.get(i).doubleValue(), multiplyingFactor));
                }
              } else if (obj instanceof Double) {
                obj = ArithUtil.mul((Double) obj, multiplyingFactor);
              }
              item.put("v", obj);
            }
          }
        }
      }

      statisticsResponse.setResult(rstDocList);
      rst = om.writeValueAsString(statisticsResponse);
    }
    return rst;
  }

  private void multiplyingDocument(Document doc, String key, double mf) {
    if (doc.containsKey(key)) {
      double val = doc.getDouble(key);
      doc.put(key, ArithUtil.mul(val, mf));
    }
  }

  /**
   * 统计告警数据  Mongo Aggregate 查询
   *
   * @param requestData 请求数据
   * @param timeType    查询实时数据类型
   * @return jsonString
   */
  public String batchFaultStatistics(StatisticsRequest requestData, int timeType)
      throws JsonProcessingException {
    ObjectMapper om = new ObjectMapper();
//    if(requestData.getSubProjectId()==20200004001L){
//      String asString = om.writeValueAsString(new StatisticsResponse());
//      //logger.info("asString content is: {}", asString);
//      return asString;
//    }
    //skip 跳过基本为0
    int skip = requestData.getPageNum() * requestData.getPageCount();
    AggregationResults<Document> rstList = null;
    if (timeType == Consts.TIME_TYPE_REAL) {
      rstList =
          faultRealDataDao.aggregateFaultRealDataByDeviceTimeTagForPage(requestData.getExcludeTagCodeList(),
              requestData.getSubProjectId(),
              requestData.getStartTime(), requestData.getEndTime(), skip,
              requestData.getPageCount(), requestData.getSortFlag(), 2);
    }
    //Document 主要是一个Map {"result":obj},{"totalCount":obj}
    if (rstList != null) {
      Document resData = rstList.iterator().next();
      StatisticsResponse statisticsResponse = new StatisticsResponse();
      List<Document> rstDocList = (List<Document>) resData.get("result");
      List<Document> rstCountList = (List<Document>) resData.get("totalCount");
      if (rstCountList != null && rstCountList.size() > 0) {
        statisticsResponse.setTotalCount((int) rstCountList.get(0).get("count"));
      }
      statisticsResponse.setResult(rstDocList);
      String asString = om.writeValueAsString(statisticsResponse);
      //logger.info("asString content is: {}", asString);
      return asString;
    }
    return "";
  }

  public AggregationResults<Document> getDeviceByDate(String date) {
    Collection<Long> collection = new ArrayList();
    collection.add(202050010010006L);
    collection.add(202050010010007L);
    collection.add(202050010010008L);
    collection.add(202050010010009L);
    collection.add(202050010010010L);
    collection.add(202050010010011L);
    collection.add(202050010010012L);
    collection.add(202050010010013L);
    collection.add(202050010010014L);
    collection.add(202050010010015L);
    collection.add(202050010010016L);
    collection.add(202050010010017L);
    collection.add(202050010010018L);
    collection.add(202050010010019L);

    Long statrDate = Long.parseLong(date + "00");
    Long endDate = Long.parseLong(date + "23");
    AggregationResults<Document> rstList = deviceRealDataDao.aggregateRealDataByDate(collection, statrDate, endDate);

    return rstList;

  }

  public List<JSONObject> getDeviceListByDate(String date) {
    List<Long> collection = new ArrayList();
    collection.add(202050010010006L);
    collection.add(202050010010007L);
    collection.add(202050010010008L);
    collection.add(202050010010009L);
    collection.add(202050010010010L);
    collection.add(202050010010011L);
    collection.add(202050010010012L);
    collection.add(202050010010013L);
    collection.add(202050010010014L);
    collection.add(202050010010015L);
    collection.add(202050010010016L);
    collection.add(202050010010017L);
    collection.add(202050010010018L);
    collection.add(202050010010019L);

    Long statrDate = Long.parseLong(date + "00");
    Long endDate = Long.parseLong(date + "23");

    List<DeviceRealData> list = deviceRealDataDao.findByDateTimeWithOutMetrics2(collection, statrDate.toString(), endDate.toString());
    List<JSONObject> mapList = new ArrayList();
    for (DeviceRealData deviceRealData : list) {
      for (RealMetricsItem realMetricsItem : deviceRealData.getMetrics()) {
        if (realMetricsItem.getDatas().containsKey("S_STATUS")) {
          String str_S_STATUS = realMetricsItem.getDatas().get("S_STATUS").toString();
          String s = Integer.toBinaryString(Integer.parseInt(str_S_STATUS));
          JSONObject o = new JSONObject();
          Long data = realMetricsItem.getTimestamp();
          Integer hour = deviceRealData.getHour();
          Integer deviceId = deviceRealData.getDeviceId();
          System.out.println("s:" + s + ",长度：" + s.length());
          Integer[] bitList = new Integer[32];
          System.out.println("bitList长度：" + bitList.length);
          int y = 0;
          for (int i = 31; i >= 0; i--) {

            bitList[y] = Integer.parseInt(s.substring(i, i + 1));
            y++;
          }

          o.put("deviceId", deviceId.toString());
          o.put("组串编号", String.valueOf(deviceId - 5));
          o.put("hour", hour.toString());
          o.put("time", "'" + data.toString());
          o.put("status", str_S_STATUS);
          o.put("32位解码", "'" + s);
          o.put("bit0", bitList[0].toString());
          o.put("电池簇连接", bitList[0] == 1 ? "连接" : "断开");

          o.put("bit1", bitList[1].toString());
          o.put("电压过低", bitList[1] == 1 ? "报警" : "正常");

          o.put("bit2", bitList[2].toString());
          o.put("电压过高", bitList[2] == 1 ? "报警" : "正常");

          o.put("bit3", bitList[3].toString());
          o.put("过电流", bitList[3] == 1 ? "报警" : "正常");

          o.put("bit6", bitList[6].toString());
          o.put("DCU通信", bitList[6] == 1 ? "故障" : "正常");

          o.put("bit7", bitList[7].toString());
          o.put("BTU通信", bitList[7] == 1 ? "故障" : "正常");

          o.put("bit8", bitList[8].toString());
          o.put("电压过低保护", bitList[8] == 1 ? "保护" : "正常");

          o.put("bit13", bitList[13].toString());
          o.put("保险熔断", bitList[13] == 1 ? "故障" : "正常");

          o.put("bit14", bitList[14].toString());
          o.put("绝缘电阻过低", bitList[14] == 1 ? "故障" : "正常");

          o.put("bit15", bitList[15].toString());
          o.put("BTU检查到故障", bitList[15] == 1 ? "故障" : "正常");

          o.put("bit16", bitList[16].toString());
          o.put("簇合闸允许", bitList[16] == 1 ? "不允许" : "允许");

          o.put("bit18", bitList[18].toString());
          o.put("继电器合闸故障", bitList[18] == 1 ? "故障" : "正常");

          o.put("bit19", bitList[19].toString());
          o.put("电流测量故障", bitList[19] == 1 ? "故障" : "正常");

          o.put("bit31", bitList[31].toString());
          o.put("通讯连接", bitList[31] == 1 ? "连接" : "断开");


          mapList.add(o);
        }
      }
    }

    return mapList;
  }


}
