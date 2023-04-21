package com.shmet.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shmet.DateTimeUtils;
import com.shmet.bean.ConnectorInfoBean;
import com.shmet.bean.EquipmentInfoBean;
import com.shmet.bean.StationInfoBean;
import com.shmet.bean.StationsInfoDataBean;
import com.shmet.dao.redis.RealDataRedisDao;
import com.shmet.helper.crypt.AESCrypt;
import com.shmet.helper.crypt.HMAC_MD5Crypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
public class TeldHandler {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  RestTemplate restTemplate;

  // 运营商标识
  private static final String operatorID = "MA1K3U9K6";
  // 运营商密钥
  private static final String operatorSecret = "WwxtKzIyCusRbEnn";
  // 签名密匙
  private static final String sigKey = "dOMixEqzsTciGoGv";

  private static final AESCrypt aes = new AESCrypt("RIkCzJWyJFMLlUPs", "XsWtNH5z9eEbkv7O");

  private static final String baseurl = "http://hlht.teld.cn:9501/evcs/v20161110/";

  @Autowired
  RealDataRedisDao redisCacheDao;

  /**
   * liuj 2019.12.31
   * 获取token
   */
  public String queryToken() throws Exception {
    String token = (String) redisCacheDao.getTeldToken(operatorID);
    if (token != null) {
      return token;
    }
    Map<String, Object> dataMap = new HashMap<>();
    dataMap.put("OperatorID", operatorID);
    dataMap.put("OperatorSecret", operatorSecret);
    String data = post(dataMap, "query_token", null);
    if (data != null) {
      ObjectMapper om = new ObjectMapper();
      Map<String, String> resDataMap = om.readValue(data, Map.class);
      token = resDataMap.get("AccessToken");
      redisCacheDao.saveTeldToken(operatorID, token);
    }
    return token;
  }

  /**
   * liuj 2019.12.31
   * 查询充电站Data信息
   */
  public String queryStationsInfoData() throws Exception {
    String token = this.queryToken();
    String stationsInfoData = null;

    if (token != null) {
      Map<String, Object> dataMap = new HashMap<>();
      dataMap.put("LastQueryTime", "");
      dataMap.put("PageNo", 1);
      dataMap.put("PageSize", 1);
      stationsInfoData = post(dataMap, "query_stations_info", token);
    }
    return stationsInfoData;
  }

  /**
   * liuj 2019.12.31
   * 查询充电站的详细信息
   */
  public StationInfoBean queryStationsInfoBean() throws Exception {
    // 获取充电站的Data信息
    String stationsInfoData = this.queryStationsInfoData();

    // 创建一个空的充电站信息的类，用于存储，以及返回充电站信息
    StationInfoBean stationInfoBean = new StationInfoBean();

    // 判断当充电站的Data信息不为空时执行下面代码
    if (stationsInfoData != null) {
      ObjectMapper om = new ObjectMapper();
      StationsInfoDataBean stationsInfoDataBean = om.readValue(
          stationsInfoData, StationsInfoDataBean.class);

      List<StationInfoBean> stationInfosBeanList = stationsInfoDataBean
          .getStationInfosBean();

      for (StationInfoBean bean : stationInfosBeanList) {
        stationInfoBean.setEquipmentInfos(bean.getEquipmentInfos());
        stationInfoBean.setStationID(bean.getStationID());
        stationInfoBean.setOperatorID(bean.getOperatorID());
        stationInfoBean.setEquipmentOwnerID(bean.getEquipmentOwnerID());
        stationInfoBean.setStationName(bean.getStationName());
        stationInfoBean.setCountryCode(bean.getCountryCode());
        stationInfoBean.setAreaCode(bean.getAreaCode());
        stationInfoBean.setAddress(bean.getAddress());
        stationInfoBean.setStationTel(bean.getStationTel());
        stationInfoBean.setServiceTel(bean.getServiceTel());
        stationInfoBean.setStationType(bean.getStationType());
        stationInfoBean.setStationStatus(bean.getStationStatus());
        stationInfoBean.setParkNums(bean.getParkNums());
        stationInfoBean.setStationLng(bean.getStationLng());
        stationInfoBean.setStationLat(bean.getStationLat());
        stationInfoBean.setSiteGuide(bean.getSiteGuide());
        stationInfoBean.setConstruction(bean.getConstruction());
        stationInfoBean.setPictures(bean.getPictures());
        stationInfoBean.setMatchCars(bean.getMatchCars());
        stationInfoBean.setParkInfo(bean.getParkInfo());
        stationInfoBean.setBusineHours(bean.getBusineHours());
        stationInfoBean.setElectricityFee(bean.getElectricityFee());
        stationInfoBean.setServiceFee(bean.getServiceFee());
        stationInfoBean.setParkFee(bean.getParkFee());
        stationInfoBean.setPayment(bean.getPayment());
        stationInfoBean.setSupportOrder(bean.getSupportOrder());
        stationInfoBean.setRemark(bean.getRemark());
      }

    }

    return stationInfoBean;

  }

  /**
   * liuj 2019.12.31
   * 查询充电设备信息列表
   */
  public List<EquipmentInfoBean> queryEquipmentInfoBeanList()
      throws Exception {
    // 获取查询充电站的详细信息的对象
    StationInfoBean stationsInfoBean = this.queryStationsInfoBean();
    // 创建一个空的集合用于存储以及返回数据
    List<EquipmentInfoBean> equipmentInfosList = new ArrayList<>();

    if (stationsInfoBean != null) {
      equipmentInfosList = stationsInfoBean.getEquipmentInfos();

    }
    return equipmentInfosList;
  }

  /**
   * liuj 2019.12.31
   * 查询充电设备接口信息列表
   */
  public List<ConnectorInfoBean> queryConnectorInfoBeanList()
      throws Exception {
    List<EquipmentInfoBean> equipmentInfosList = this
        .queryEquipmentInfoBeanList();

    List<ConnectorInfoBean> connectorInfosList = new ArrayList<>();

    if (equipmentInfosList != null) {
      for (EquipmentInfoBean bean : equipmentInfosList) {
        List<ConnectorInfoBean> connectorInfosList2 = bean
            .getConnectorInfos();

        ConnectorInfoBean connectorInfoBean = new ConnectorInfoBean();

        for (ConnectorInfoBean connectorInfoBean2 : connectorInfosList2) {

          connectorInfoBean.setConnectorID(connectorInfoBean2
              .getConnectorID());
          connectorInfoBean.setConnectorName(connectorInfoBean2
              .getConnectorName());
          connectorInfoBean.setConnectorType(connectorInfoBean2
              .getConnectorType());
          connectorInfoBean.setVoltageUpperLimits(connectorInfoBean2
              .getVoltageUpperLimits());
          connectorInfoBean.setVoltageLowerLimits(connectorInfoBean2
              .getVoltageLowerLimits());
          connectorInfoBean.setCurrent(connectorInfoBean2
              .getCurrent());
          connectorInfoBean.setPower(connectorInfoBean2.getPower());
          connectorInfoBean.setParkNo(connectorInfoBean2.getParkNo());
          connectorInfoBean.setNationalStandard(connectorInfoBean2
              .getNationalStandard());
        }

        connectorInfosList.add(connectorInfoBean);
      }

    }

    return connectorInfosList;

  }

  /**
   * liuj 2019.12.31
   * 设备接口状态查询的Data信息 注意：暂时没接口权限，不能使用！！！
   */
  public String queryStationStatusData() throws Exception {
    String token = this.queryToken();
    String stationStatusData = null;

    if (token != null) {
      String stationsInfoData = this.queryStationsInfoData();

      if (stationsInfoData != null) {
        ObjectMapper om = new ObjectMapper();
        StationsInfoDataBean stationsInfoDataBean = om.readValue(
            stationsInfoData, StationsInfoDataBean.class);

        List<StationInfoBean> stationInfosBeanList = stationsInfoDataBean
            .getStationInfosBean();

        StationInfoBean stationInfoBean = new StationInfoBean();

        for (StationInfoBean bean : stationInfosBeanList) {
          stationInfoBean.setStationID(bean.getStationID());
        }

        String stationID = stationInfoBean.getStationID();

        String[] stationIDs = stationID.split(",");

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("StationIDs", stationIDs);

        stationStatusData = this.post(dataMap, "query_station_status",
            token);

      }
    }

    return stationStatusData;

  }

  private String post(Map<String, Object> dataMap, String url, String token) throws Exception {
    // 创建一个ObjectMapper
    ObjectMapper om = new ObjectMapper();
    // 通过writeValueAsString方法转换成json格式的字符串
    String dataString = om.writeValueAsString(dataMap);
    // 进行加密操作
    String secDataString = aes.encrypt(dataString);
    // 获取时间并且格式化
    String dateString = DateTimeUtils.date2String(new Date(), DateTimeUtils.FORMAT_YYYYMMDDHHMMSS);

    String seq = "001";
    // 拼接顺序为运营商标识（OperatorID）、参数内容（Data）、时间戳（TimeStamp）、自增序列（Seq）
    String md5Data = operatorID + secDataString + dateString + seq;

    String hmac_md5_data = HMAC_MD5Crypt.encryptHMAC(md5Data, sigKey);

    HttpHeaders headers = new HttpHeaders();

    headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

    if (token != null) {
      headers.add("Authorization", "Bearer" + token);

      headers.add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36");
    }
    // 创建一个HashMap，并存入值
    Map<String, String> reqMap = new HashMap<>();
    reqMap.put("OperatorID", operatorID);
    reqMap.put("Data", secDataString);
    reqMap.put("TimeStamp", dateString);
    reqMap.put("Seq", seq);
    reqMap.put("Sig", hmac_md5_data);

    // 将reqMap转换成json格式的字符串
    String jsonData = om.writeValueAsString(reqMap);

    HttpEntity<String> formEntity = new HttpEntity<>(jsonData, headers);

    ResponseEntity<String> response = restTemplate.postForEntity(baseurl + url, formEntity, String.class);

    Map<String, Object> res = om.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {
    });
    String rst = null;
    if ((Integer) res.get("Ret") == 0) {
      rst = aes.decrypt(res.get("Data").toString());
    } else {
      logger.warn("特来电请求失败，" + response.getBody());
    }
    return rst;
  }
}
