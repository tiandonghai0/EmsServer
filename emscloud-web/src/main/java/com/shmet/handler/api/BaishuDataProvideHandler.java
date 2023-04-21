package com.shmet.handler.api;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.shmet.bean.LxBean;
import com.shmet.bean.LxDataListBean;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shmet.bean.RealDataItem;
import com.shmet.dao.BaishuDataProvideDao;
import com.shmet.dao.redis.RealDataRedisDao;

@Component
public class BaishuDataProvideHandler {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  RestTemplate restTemplate;

  @Autowired
  BaishuDataProvideDao baishuDataProvideDao;

  @Autowired
  RealDataRedisDao redisCacheDao;

  // 客户端ID
  private final String client_id = "xJsFO0IMGN0nyHKtAybKd1US";

  // 客户端秘钥
  private final String client_secret = "KybPPqusjuByvX8wIwgqu9tUABuosuVD";

  // 客户端权限
  private final String scope = "basic";

  // redirect地址
  private final String redirect_uri = "127.0.0.1:7000/login";

  // 基础的URL
  private final String baseurl = "http://221.12.172.57:9000/cps-server";

  /**
   * 登录,获取cookie
   */
  public String getCookie() {
    // 用户名
    String username = "dabaishu";
    // 密码
    String password = "123456!-";
    // 登录地址
    String url = baseurl + "/check";
    String cookie = (String) redisCacheDao.getLangxinCookies(username);
    if (cookie != null) {
      return cookie;
    }
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    // 创建一个LinkedMultiValueMap并存人值
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("username", username);
    map.add("password", password);
    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
    ResponseEntity<String> response;
    try {
      response = restTemplate.postForEntity(url, request, String.class);
    } catch (Exception e) {
      logger.warn("获取Cookie失败," + e.getMessage());
      return null;
    }
    String body = response.getBody();
    String cookie1 = response.getHeaders().get("Set-Cookie").get(0);
    redisCacheDao.saveLangxinCookies(username, cookie1);
    return cookie1;
  }

  /**
   * 获取code
   */
  public String getCode() {
    // 调用登录
    String cookie = this.getCookie();
    if (cookie == null) {
      logger.warn("获取Code失败,cookie为null");
      return null;
    }
    String response_type = "code";
    // 获取code地址
    String url = baseurl + "/oauth2.0/authorize";
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    List<String> cookies = new ArrayList<>();
    cookies.add(cookie);
    headers.put(HttpHeaders.COOKIE, cookies);
    // 创建一个LinkedMultiValueMap并存人值
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("client_id", client_id);
    map.add("redirect_uri", redirect_uri);
    map.add("scope", scope);
    map.add("response_type", response_type);
    HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
    HttpClient httpClient = HttpClientBuilder.create()
        .setRedirectStrategy(new LaxRedirectStrategy()).build();
    factory.setHttpClient(httpClient);
    restTemplate.setRequestFactory(factory);
    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(
        map, headers);
    ResponseEntity<String> response;
    try {
      response = restTemplate.postForEntity(url, request, String.class);
    } catch (Exception e1) {
      logger.warn("获取Code失败," + e1.getMessage());
      return null;
    }
    String body = response.getBody();
    ObjectMapper om = new ObjectMapper();
    Map<String, String> readValue;
    try {
      readValue = om.readValue(body, Map.class);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
    if (readValue != null) {
      return readValue.get("code");
    }
    return null;
  }

  /**
   * 获取新的token
   */
  public String getToken() {
    String code = this.getCode();
    if (code == null) {
      logger.warn("获取token失败,code为空");
      return null;
    }
    String grant_type = "authorization_code";
    String url = baseurl + "/oauth2.0/token";
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    // 创建一个LinkedMultiValueMap并存人值
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("client_id", client_id);
    map.add("redirect_uri", redirect_uri);
    map.add("code", code);
    map.add("grant_type", grant_type);
    map.add("client_secret", client_secret);
    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(
        map, headers);

    ResponseEntity<String> response;
    try {
      response = restTemplate.postForEntity(url, request, String.class);
    } catch (RestClientException e) {
      logger.warn("获取token失败," + e.getMessage());
      return null;
    }

    String body = response.getBody();

    ObjectMapper om = new ObjectMapper();
    Map<String, String> readValue;
    try {
      readValue = om.readValue(body, Map.class);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }

    if (readValue != null) {
      String token = readValue.get("access_token");
      redisCacheDao.saveLangxinToken(client_id, token);
      return token;
    }
    return null;

  }

  /**
   * 获取存在Redis缓存里的token
   */
  public String getRedisToken() {
    String token = (String) redisCacheDao.getLangxinToken(client_id);
    if (token != null) {
      return token;
    } else {
      return this.getToken();
    }
  }

  private String post(Object obj, String url, String token) {
    String rst = null;
    try {
      // 创建一个ObjectMapper
      ObjectMapper om = new ObjectMapper();
      // 通过writeValueAsString方法转换成json格式的字符串
      String dataString = om.writeValueAsString(obj);
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

      if (token == null) {
        logger.warn("推送数据失败,token为null");
        return null;
      }
      headers.add("access_token", token);
      HttpEntity<String> request = new HttpEntity<>(dataString, headers);
      ResponseEntity<String> response = restTemplate.postForEntity(baseurl + url, request, String.class);
      HttpStatus statusCode = response.getStatusCode();
      int code = statusCode.value();
      if (code == 200) {
        Map<String, Object> res = om.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {
        });
        if (res != null) {
          boolean containsKey = res.containsKey("error");
          if (containsKey) {
            if ((res.get("error")).equals("invalid_grant")) {
              String token2 = this.getToken();
              return this.post(obj, url, token2);
            } else {
              logger.warn("推送数据失败，" + response.getBody());
            }
          } else {
            boolean containsKey2 = res.containsKey("code");
            if (containsKey2) {
              // 如果返回10000000，则是推送数据成功
              if ((res.get("code")).equals("10000000")) {
                rst = response.getBody();
              } else {
                logger.warn("推送数据失败，" + response.getBody());
              }
            }
          }
        }
      }
    } catch (Exception e) {
      logger.warn("推送数据失败," + e.getMessage());
      return null;
    }
    return rst;
  }

  public void addDevice() {
    String url = "/v1/collection/equiInfochg";

    Map<String, String> map = new HashMap<>();
    map.put("equipName", "测试");
    map.put("instArea", "测试地址");
    map.put("equipState", "00");

    String token = this.getRedisToken();
    if (token != null) {
      this.post(map, url, token);
    }
  }

  public void addMonitoringPoint() {
    String url = "/v1/collection/operMonsInfoChg";
    Map<String, String> map = new HashMap<>();
    map.put("monName", "系统充放电状态");
    map.put("energyType", "99");
    map.put("monState", "02");
    map.put("equipFilezInfoId", "7B3E7331BB2E2A7AB55D7AF1398F47F7");

    String token = this.getRedisToken();
    if (token != null) {
      String post = this.post(map, url, token);
    }
  }

  public void getRealData(List<LxDataListBean> listBeans, String monsNo, String collItemCode, Long deviceNo, String tagCode) {
    RealDataItem realDataItem = baishuDataProvideDao.queryRealData(deviceNo, tagCode);

    if (realDataItem != null) {
      String data = null;

      if (realDataItem.getData() instanceof String || realDataItem.getData() instanceof Double) {
        data = realDataItem.getData().toString();
      } else {
        ArrayList data2 = (ArrayList) realDataItem.getData();
        data = data2.get(0).toString();
      }

      // 操作时间
      Date date = new Date();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

      LxDataListBean lxDataList = new LxDataListBean();

      lxDataList.setMonsNo(monsNo);
      lxDataList.setCollItemCode(collItemCode);
      lxDataList.setCollTime(realDataItem.getDateTime());
      lxDataList.setDataTime(sdf.format(date));
      lxDataList.setDataValue(data);
      listBeans.add(lxDataList);

    } else {
      logger.warn("获取数据失败，没有相应的点位信息");
    }
  }


  public void getListData() throws Exception {
    LxBean lxBean = new LxBean();
    List<LxDataListBean> lxDataListBeansList = new ArrayList<>();

    // 断路器运行状态
    this.getRealData(lxDataListBeansList, "CD1577677153560", "5300", 201800010010042L, "WS");
    // 双向变流器(PCS)
    // 当日充电量
    this.getRealData(lxDataListBeansList, "CD1576123255281", "9010", 201800010010041L, "QDI");

    // 当日放电量
    this.getRealData(lxDataListBeansList, "CD1576132652441", "9010", 201800010010041L, "QDO");
    // 输出有功功率（交流侧）
    this.getRealData(lxDataListBeansList, "CD1576132952876", "B630", 201800010010041L, "OACTP");

    // 直流侧功率
    this.getRealData(lxDataListBeansList, "CD1576133120414", "B630", 201800010010041L, "DCPT");
    // 直流侧电压
    this.getRealData(lxDataListBeansList, "CD1576133360428", "B610", 201800010010041L, "UDC");

    // 直流侧电流
    this.getRealData(lxDataListBeansList, "CD1576133552221", "B620", 201800010010041L, "IDC");
    // 逆变频率
    this.getRealData(lxDataListBeansList, "CD1576133793860", "B220", 201800010010041L, "FINV");

    // 电网频率
    this.getRealData(lxDataListBeansList, "CD1576134050364", "B220", 201800010010041L, "FG");
    // 运行状态
    this.getRealData(lxDataListBeansList, "CD1576134475914", "5300", 201800010010041L, "RS");

    // BMS系统状态
    this.getRealData(lxDataListBeansList, "CD1576134624654", "5700", 201800010010041L, "SSBMS");
    // BMS告警状态
    this.getRealData(lxDataListBeansList, "CD1576134763598", "5800", 201800010010041L, "ALMBMS");

    // 内部温度
    this.getRealData(lxDataListBeansList, "CD1576134952430", "B810", 201800010010041L, "TI");
    // 散热器温度
    this.getRealData(lxDataListBeansList, "CD1576135086740", "B810", 201800010010041L, "TRAD");

    // 交流侧电压（A相）
    this.getRealData(lxDataListBeansList, "CD1576135281658", "B611", 201800010010041L, "OUU");
    // 交流侧电压（B相）
    this.getRealData(lxDataListBeansList, "CD1576135448917", "B612", 201800010010041L, "OUV");

    // 交流侧电压（C相）
    this.getRealData(lxDataListBeansList, "CD1576135605702", "B613", 201800010010041L, "OUW");
    // 交流侧电流（A相）
    this.getRealData(lxDataListBeansList, "CD1576135746205", "B621", 201800010010041L, "OIU");

    // 交流侧电流（B相）
    this.getRealData(lxDataListBeansList, "CD1576135906685", "B622", 201800010010041L, "OIV");
    // 交流侧电流（C相）
    this.getRealData(lxDataListBeansList, "CD1576136012805", "B623", 201800010010041L, "OIW");


    // 磷酸铁锂电池系统
    // 系统充放电状态
    this.getRealData(lxDataListBeansList, "CD1577677575057", "5400", 201800010010043L, "IDC");
    // 总电压
    this.getRealData(lxDataListBeansList, "CD1576136285490", "B610", 201800010010043L, "UT");

    // 功率
    this.getRealData(lxDataListBeansList, "CD1576136433865", "B630", 201800010010043L, "D_P");
    // 直流电流
    this.getRealData(lxDataListBeansList, "CD1576136625487", "B620", 201800010010043L, "IDC");

    // 充电深度
    this.getRealData(lxDataListBeansList, "CD1576136947604", "5500", 201800010010043L, "SOC");
    // 电池健康度
    this.getRealData(lxDataListBeansList, "CD1576137109976", "5600", 201800010010043L, "SOH");

    // 单体最高电压
    this.getRealData(lxDataListBeansList, "CD1576137305840", "B610", 201800010010043L, "USMAX");
    // 单体最低电压
    this.getRealData(lxDataListBeansList, "CD1576137460040", "B610", 201800010010043L, "USMIN");

    // 单体平均电压
    this.getRealData(lxDataListBeansList, "CD1576461068897", "B610", 201800010010043L, "USAVG");
    // 单体最高温度
    this.getRealData(lxDataListBeansList, "CD1576461497493", "B810", 201800010010043L, "TSMAX");

    // 单体最低温度
    this.getRealData(lxDataListBeansList, "CD1576462241587", "B810", 201800010010043L, "TSMIN");
    // 单体平均温度
    this.getRealData(lxDataListBeansList, "CD1576462589806", "B810", 201800010010043L, "TSAVG");

    // 允许最大充电电流
    this.getRealData(lxDataListBeansList, "CD1576463387594", "B620", 201800010010043L, "IEIMAX");
    // 允许最大放电电流
    this.getRealData(lxDataListBeansList, "CD1576464397166", "B620", 201800010010043L, "IEOMAX");

    // 1号电池柜
    // 充电深度
    this.getRealData(lxDataListBeansList, "CD1576466432053", "5500", 201800010010044L, "SOC");
    // 单体最高温度
    this.getRealData(lxDataListBeansList, "CD1576466620836", "B810", 201800010010044L, "TSMAX");

    // 单体最低温度
    this.getRealData(lxDataListBeansList, "CD1576466758814", "B810", 201800010010044L, "TSMIN");
    // 单体平均温度
    this.getRealData(lxDataListBeansList, "CD1576466885184", "B810", 201800010010044L, "TSAVG");

    // 电流
    this.getRealData(lxDataListBeansList, "CD1576467004862", "B620", 201800010010044L, "I");
    // 允许最大充电电流
    this.getRealData(lxDataListBeansList, "CD1576467127439", "B620", 201800010010044L, "IEIMAX");

    // 允许最大放电电流
    this.getRealData(lxDataListBeansList, "CD1576467250300", "B620", 201800010010044L, "IEOMAX");
    // 单体最高电压
    this.getRealData(lxDataListBeansList, "CD1576467364843", "B610", 201800010010044L, "USMAX");

    // 单体最低电压
    this.getRealData(lxDataListBeansList, "CD1576467493465", "B610", 201800010010044L, "USMIN");
    // 单体平均电压
    this.getRealData(lxDataListBeansList, "CD1576477710283", "B610", 201800010010044L, "USAVG");

    // 电池健康度
    this.getRealData(lxDataListBeansList, "CD1576477908798", "5600", 201800010010044L, "SOH");


    // 2号电池柜
    // 充电深度
    this.getRealData(lxDataListBeansList, "CD1576478064527", "5500", 201800010010045L, "SOC");

    // 单体最高温度
    this.getRealData(lxDataListBeansList, "CD1576478210586", "B810", 201800010010045L, "TSMAX");
    // 单体最低温度
    this.getRealData(lxDataListBeansList, "CD1576478364077", "B810", 201800010010045L, "TSMIN");

    // 单体平均温度
    this.getRealData(lxDataListBeansList, "CD1576478519690", "B810", 201800010010045L, "TSAVG");
    // 电流
    this.getRealData(lxDataListBeansList, "CD1576478678305", "B620", 201800010010045L, "I");

    // 允许最大充电电流
    this.getRealData(lxDataListBeansList, "CD1576478871277", "B620", 201800010010045L, "IEIMAX");
    // 允许最大放电电流
    this.getRealData(lxDataListBeansList, "CD1576479024538", "B620", 201800010010045L, "IEOMAX");

    // 单体最高电压
    this.getRealData(lxDataListBeansList, "CD1576479316775", "B610", 201800010010045L, "USMAX");
    // 单体最低电压
    this.getRealData(lxDataListBeansList, "CD1576479443376", "B610", 201800010010045L, "USMIN");

    // 单体平均电压
    this.getRealData(lxDataListBeansList, "CD1576479732663", "B610", 201800010010045L, "USAVG");
    // 电池健康度
    this.getRealData(lxDataListBeansList, "CD1576479860529", "5600", 201800010010045L, "SOH");


    // 3号电池柜
    // 充电深度
    this.getRealData(lxDataListBeansList, "CD1576479992437", "5500", 201800010010046L, "SOC");
    // 单体最高温度
    this.getRealData(lxDataListBeansList, "CD1576480387150", "B810", 201800010010046L, "TSMAX");

    // 单体最低温度
    this.getRealData(lxDataListBeansList, "CD1576480518409", "B810", 201800010010046L, "TSMIN");
    // 单体平均温度
    this.getRealData(lxDataListBeansList, "CD1576480719911", "B810", 201800010010046L, "TSAVG");

    // 电流
    this.getRealData(lxDataListBeansList, "CD1576480925539", "B620", 201800010010046L, "I");
    // 允许最大充电电流
    this.getRealData(lxDataListBeansList, "CD1576481050914", "B620", 201800010010046L, "IEIMAX");

    // 允许最大放电电流
    this.getRealData(lxDataListBeansList, "CD1576481179413", "B620", 201800010010046L, "IEOMAX");
    // 单体最高电压
    this.getRealData(lxDataListBeansList, "CD1576481302289", "B610", 201800010010046L, "USMAX");

    // 单体最低电压
    this.getRealData(lxDataListBeansList, "CD1576481483434", "B610", 201800010010046L, "USMIN");
    // 单体平均电压
    this.getRealData(lxDataListBeansList, "CD1576481598293", "B610", 201800010010046L, "USAVG");

    // 电池健康度
    this.getRealData(lxDataListBeansList, "CD1576481769837", "5600", 201800010010046L, "SOH");


    // 4号电池柜
    // 充电深度
    this.getRealData(lxDataListBeansList, "CD1576482062663", "5500", 201800010010047L, "SOC");

    // 单体最高温度
    this.getRealData(lxDataListBeansList, "CD1576482310648", "B810", 201800010010047L, "TSMAX");
    // 单体最低温度
    this.getRealData(lxDataListBeansList, "CD1576482482308", "B810", 201800010010047L, "TSMIN");

    // 单体平均温度
    this.getRealData(lxDataListBeansList, "CD1576482675394", "B810", 201800010010047L, "TSAVG");
    // 电流
    this.getRealData(lxDataListBeansList, "CD1576482955058", "B620", 201800010010047L, "I");

    // 允许最大充电电流
    this.getRealData(lxDataListBeansList, "CD1576483089611", "B620", 201800010010047L, "IEIMAX");
    // 允许最大放电电流
    this.getRealData(lxDataListBeansList, "CD1576483217613", "B620", 201800010010047L, "IEOMAX");

    // 单体最高电压
    this.getRealData(lxDataListBeansList, "CD1576483395491", "B610", 201800010010047L, "USMAX");
    // 单体最低电压
    this.getRealData(lxDataListBeansList, "CD1576483505183", "B610", 201800010010047L, "USMIN");

    // 单体平均电压
    this.getRealData(lxDataListBeansList, "CD1576483629643", "B610", 201800010010047L, "USAVG");
    // 电池健康度
    this.getRealData(lxDataListBeansList, "CD1576483817880", "5600", 201800010010047L, "SOH");
    lxBean.setDataList(lxDataListBeansList);

    String url = "/v1/collection/measuringRealtimeBatchData";
    String result = post(lxBean, url, getRedisToken());
    //logger.info("结果：");
    logger.info(result);

  }

  public void getRealtimeData(String monsNo, String collItemCode, Long deviceNo, String tagCode) {
    String url = "/v1/collection/measuringRealtimeData";
    RealDataItem realDataItem = baishuDataProvideDao.queryRealData(deviceNo, tagCode);
    if (realDataItem != null) {
      String data;
      if (realDataItem.getData() instanceof String || realDataItem.getData() instanceof Double) {
        data = realDataItem.getData().toString();
      } else {
        ArrayList data2 = (ArrayList) realDataItem.getData();
        data = data2.get(0).toString();
      }
      // 操作时间
      Date date = new Date();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

      Map<String, String> map = new HashMap<>();
      map.put("monsNo", monsNo);
      map.put("collItemCode", collItemCode);
      map.put("collTime", realDataItem.getDateTime());
      map.put("dataTime", sdf.format(date));
      map.put("dataValue", data);

      String token = this.getRedisToken();

      if (token != null) {
        String post = this.post(map, url, token);
      } else {
        logger.warn("推送数据失败,token为空");
      }
    } else {
      logger.warn("获取数据失败，没有相应的点位信息");
    }
  }

  public void deliverData() throws Exception {
    // 断路器运行状态
    this.getRealtimeData("CD1577677153560", "5300", 201800010010042L, "WS");
    // 双向变流器(PCS)
    // 当日充电量
    this.getRealtimeData("CD1576123255281", "9010", 201800010010041L, "QDI");
    Thread.sleep(1000);
    // 当日放电量
    this.getRealtimeData("CD1576132652441", "9010", 201800010010041L, "QDO");
    // 输出有功功率（交流侧）
    this.getRealtimeData("CD1576132952876", "B630", 201800010010041L, "OACTP");
    Thread.sleep(1000);
    // 直流侧功率
    this.getRealtimeData("CD1576133120414", "B630", 201800010010041L, "DCPT");
    // 直流侧电压
    this.getRealtimeData("CD1576133360428", "B610", 201800010010041L, "UDC");
    Thread.sleep(1000);
    // 直流侧电流
    this.getRealtimeData("CD1576133552221", "B620", 201800010010041L, "IDC");
    // 逆变频率
    this.getRealtimeData("CD1576133793860", "B220", 201800010010041L, "FINV");
    Thread.sleep(1000);
    // 电网频率
    this.getRealtimeData("CD1576134050364", "B220", 201800010010041L, "FG");
    // 运行状态
    this.getRealtimeData("CD1576134475914", "5300", 201800010010041L, "RS");
    Thread.sleep(1000);
    // BMS系统状态
    this.getRealtimeData("CD1576134624654", "5700", 201800010010041L, "SSBMS");
    // BMS告警状态
    this.getRealtimeData("CD1576134763598", "5800", 201800010010041L, "ALMBMS");
    Thread.sleep(1000);
    // 内部温度
    this.getRealtimeData("CD1576134952430", "B810", 201800010010041L, "TI");
    // 散热器温度
    this.getRealtimeData("CD1576135086740", "B810", 201800010010041L, "TRAD");
    Thread.sleep(1000);
    // 交流侧电压（A相）
    this.getRealtimeData("CD1576135281658", "B611", 201800010010041L, "OUU");
    // 交流侧电压（B相）
    this.getRealtimeData("CD1576135448917", "B612", 201800010010041L, "OUV");
    Thread.sleep(1000);
    // 交流侧电压（C相）
    this.getRealtimeData("CD1576135605702", "B613", 201800010010041L, "OUW");
    // 交流侧电流（A相）
    this.getRealtimeData("CD1576135746205", "B621", 201800010010041L, "OIU");
    Thread.sleep(1000);
    // 交流侧电流（B相）
    this.getRealtimeData("CD1576135906685", "B622", 201800010010041L, "OIV");
    // 交流侧电流（C相）
    this.getRealtimeData("CD1576136012805", "B623", 201800010010041L, "OIW");
    Thread.sleep(1000);
    // 磷酸铁锂电池系统
    // 系统充放电状态
    this.getRealtimeData("CD1577677575057", "5400", 201800010010043L, "IDC");
    // 总电压
    this.getRealtimeData("CD1576136285490", "B610", 201800010010043L, "UT");
    Thread.sleep(1000);
    // 功率
    this.getRealtimeData("CD1576136433865", "B630", 201800010010043L, "D_P");
    // 直流电流
    this.getRealtimeData("CD1576136625487", "B620", 201800010010043L, "IDC");
    Thread.sleep(1000);
    // 充电深度
    this.getRealtimeData("CD1576136947604", "5500", 201800010010043L, "SOC");
    // 电池健康度
    this.getRealtimeData("CD1576137109976", "5600", 201800010010043L, "SOH");
    Thread.sleep(1000);
    // 单体最高电压
    this.getRealtimeData("CD1576137305840", "B610", 201800010010043L, "USMAX");
    // 单体最低电压
    this.getRealtimeData("CD1576137460040", "B610", 201800010010043L, "USMIN");
    Thread.sleep(1000);
    // 单体平均电压
    this.getRealtimeData("CD1576461068897", "B610", 201800010010043L, "USAVG");
    // 单体最高温度
    this.getRealtimeData("CD1576461497493", "B810", 201800010010043L, "TSMAX");
    Thread.sleep(1000);
    // 单体最低温度
    this.getRealtimeData("CD1576462241587", "B810", 201800010010043L, "TSMIN");
    // 单体平均温度
    this.getRealtimeData("CD1576462589806", "B810", 201800010010043L, "TSAVG");
    Thread.sleep(1000);
    // 允许最大充电电流
    this.getRealtimeData("CD1576463387594", "B620", 201800010010043L, "IEIMAX");
    // 允许最大放电电流
    this.getRealtimeData("CD1576464397166", "B620", 201800010010043L, "IEOMAX");
    Thread.sleep(1000);
    // 1号电池柜
    // 充电深度
    this.getRealtimeData("CD1576466432053", "5500", 201800010010044L, "SOC");
    // 单体最高温度
    this.getRealtimeData("CD1576466620836", "B810", 201800010010044L, "TSMAX");
    Thread.sleep(1000);
    // 单体最低温度
    this.getRealtimeData("CD1576466758814", "B810", 201800010010044L, "TSMIN");
    // 单体平均温度
    this.getRealtimeData("CD1576466885184", "B810", 201800010010044L, "TSAVG");
    Thread.sleep(1000);
    // 电流
    this.getRealtimeData("CD1576467004862", "B620", 201800010010044L, "I");
    // 允许最大充电电流
    this.getRealtimeData("CD1576467127439", "B620", 201800010010044L, "IEIMAX");
    Thread.sleep(1000);
    // 允许最大放电电流
    this.getRealtimeData("CD1576467250300", "B620", 201800010010044L, "IEOMAX");
    // 单体最高电压
    this.getRealtimeData("CD1576467364843", "B610", 201800010010044L, "USMAX");
    Thread.sleep(1000);
    // 单体最低电压
    this.getRealtimeData("CD1576467493465", "B610", 201800010010044L, "USMIN");
    // 单体平均电压
    this.getRealtimeData("CD1576477710283", "B610", 201800010010044L, "USAVG");
    Thread.sleep(1000);
    // 电池健康度
    this.getRealtimeData("CD1576477908798", "5600", 201800010010044L, "SOH");

    // 2号电池柜
    // 充电深度
    this.getRealtimeData("CD1576478064527", "5500", 201800010010045L, "SOC");
    Thread.sleep(1000);
    // 单体最高温度
    this.getRealtimeData("CD1576478210586", "B810", 201800010010045L, "TSMAX");
    // 单体最低温度
    this.getRealtimeData("CD1576478364077", "B810", 201800010010045L, "TSMIN");
    Thread.sleep(1000);
    // 单体平均温度
    this.getRealtimeData("CD1576478519690", "B810", 201800010010045L, "TSAVG");
    // 电流
    this.getRealtimeData("CD1576478678305", "B620", 201800010010045L, "I");
    Thread.sleep(1000);
    // 允许最大充电电流
    this.getRealtimeData("CD1576478871277", "B620", 201800010010045L, "IEIMAX");
    // 允许最大放电电流
    this.getRealtimeData("CD1576479024538", "B620", 201800010010045L, "IEOMAX");
    Thread.sleep(1000);
    // 单体最高电压
    this.getRealtimeData("CD1576479316775", "B610", 201800010010045L, "USMAX");
    // 单体最低电压
    this.getRealtimeData("CD1576479443376", "B610", 201800010010045L, "USMIN");
    Thread.sleep(1000);
    // 单体平均电压
    this.getRealtimeData("CD1576479732663", "B610", 201800010010045L, "USAVG");
    // 电池健康度
    this.getRealtimeData("CD1576479860529", "5600", 201800010010045L, "SOH");

    Thread.sleep(1000);

    // 3号电池柜
    // 充电深度
    this.getRealtimeData("CD1576479992437", "5500", 201800010010046L, "SOC");
    // 单体最高温度
    this.getRealtimeData("CD1576480387150", "B810", 201800010010046L, "TSMAX");
    Thread.sleep(1000);
    // 单体最低温度
    this.getRealtimeData("CD1576480518409", "B810", 201800010010046L, "TSMIN");
    // 单体平均温度
    this.getRealtimeData("CD1576480719911", "B810", 201800010010046L, "TSAVG");
    Thread.sleep(1000);
    // 电流
    this.getRealtimeData("CD1576480925539", "B620", 201800010010046L, "I");
    // 允许最大充电电流
    this.getRealtimeData("CD1576481050914", "B620", 201800010010046L, "IEIMAX");
    Thread.sleep(1000);
    // 允许最大放电电流
    this.getRealtimeData("CD1576481179413", "B620", 201800010010046L, "IEOMAX");
    // 单体最高电压
    this.getRealtimeData("CD1576481302289", "B610", 201800010010046L, "USMAX");
    Thread.sleep(1000);
    // 单体最低电压
    this.getRealtimeData("CD1576481483434", "B610", 201800010010046L, "USMIN");
    // 单体平均电压
    this.getRealtimeData("CD1576481598293", "B610", 201800010010046L, "USAVG");
    Thread.sleep(1000);
    // 电池健康度
    this.getRealtimeData("CD1576481769837", "5600", 201800010010046L, "SOH");

    // 4号电池柜
    // 充电深度
    this.getRealtimeData("CD1576482062663", "5500", 201800010010047L, "SOC");
    Thread.sleep(1000);
    // 单体最高温度
    this.getRealtimeData("CD1576482310648", "B810", 201800010010047L, "TSMAX");
    // 单体最低温度
    this.getRealtimeData("CD1576482482308", "B810", 201800010010047L, "TSMIN");
    Thread.sleep(1000);
    // 单体平均温度
    this.getRealtimeData("CD1576482675394", "B810", 201800010010047L, "TSAVG");
    // 电流
    this.getRealtimeData("CD1576482955058", "B620", 201800010010047L, "I");
    Thread.sleep(1000);
    // 允许最大充电电流
    this.getRealtimeData("CD1576483089611", "B620", 201800010010047L, "IEIMAX");
    // 允许最大放电电流
    this.getRealtimeData("CD1576483217613", "B620", 201800010010047L, "IEOMAX");
    Thread.sleep(1000);
    // 单体最高电压
    this.getRealtimeData("CD1576483395491", "B610", 201800010010047L, "USMAX");
    // 单体最低电压
    this.getRealtimeData("CD1576483505183", "B610", 201800010010047L, "USMIN");
    Thread.sleep(1000);
    // 单体平均电压
    this.getRealtimeData("CD1576483629643", "B610", 201800010010047L, "USAVG");
    // 电池健康度
    this.getRealtimeData("CD1576483817880", "5600", 201800010010047L, "SOH");
  }

}
