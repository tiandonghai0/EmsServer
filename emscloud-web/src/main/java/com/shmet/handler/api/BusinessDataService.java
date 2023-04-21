package com.shmet.handler.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.shmet.api.MyHttpClient;
import com.shmet.dao.AlarmUnitsMapper;
import com.shmet.dao.ChargeListDataMapper;
import com.shmet.dao.UserMapper;
import com.shmet.entity.mysql.gen.AlarmUnits;
import com.shmet.entity.mysql.gen.ChargeListData;
import com.shmet.entity.mysql.gen.User;
import com.shmet.enums.AlarmEnum;
import com.shmet.utils.DateUtils;
import com.shmet.utils.GlobalConfig;
import com.shmet.vo.AlarmVo;
import com.shmet.vo.ChargeListDataVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author
 * 从各大项目迁移过来的API(稍有改动) 未来会作废 标注  @Deprecated
 */
@Service
public class BusinessDataService {

  private static final Logger log = LoggerFactory.getLogger(BusinessDataService.class);

  @Resource
  private ChargeListDataMapper chargeListDataMapper;

  @Resource
  private AlarmUnitsMapper alarmUnitsMapper;

  @Resource
  private StringRedisTemplate stringRedisTemplate;

  @Resource
  private UserMapper userMapper;

  private static final String[] dataTitle = new String[]{"", "", "hour", "day", "month"};
  private static final String[] formats = new String[]{"", "", "yyyy-MM-dd", "yyyy-MM", "yyyy"};
  private static final int[] formatTime = new int[]{0, 0, 1, 3, 5};

  public JSONObject listElecStatic(String callDate) {
    JSONObject result = new JSONObject();
    List<String> lineNos = Lists.newArrayList("819", "817");
    DecimalFormat df = new DecimalFormat("#.00");

    for (String lineNo : lineNos) {
      QueryWrapper<ChargeListData> wrapper = new QueryWrapper<ChargeListData>()
          .eq("project_no", "bs001")
          .likeRight("call_date", callDate)
          .eq("line_no", lineNo);
      List<ChargeListData> chargeListDatas = chargeListDataMapper.selectList(wrapper);

      JSONArray dateList = new JSONArray();
      JSONArray fengList = new JSONArray();
      JSONArray ping1List = new JSONArray();
      JSONArray ping2List = new JSONArray();
      JSONArray guList = new JSONArray();
      JSONArray fengCList = new JSONArray();
      JSONArray pingCList = new JSONArray();
      JSONArray guCList = new JSONArray();

      for (ChargeListData vo : chargeListDatas) {
        double fengC;
        double pingC;

        int month = Integer.parseInt(vo.getCallDate().substring(5));
        double fengVal = vo.getFengVal();
        double ping1Val = vo.getPing1Val();
        double ping2Val = vo.getPing2Val();
        double guVal = vo.getGuVal();
        double guPrice = vo.getGuPrice();
        double fengPrice = vo.getFengPrice();
        double pingPrice = vo.getPingPrice();
        double guC = guVal * guPrice;

        if (month < 8 || month > 10) {
          fengC = fengVal * fengPrice;
          pingC = (ping1Val + ping2Val) * pingPrice;
        } else {
          fengC = (fengVal + ping2Val) * fengPrice;
          pingC = ping1Val * pingPrice;
        }

        dateList.add(month);
        fengList.add(fengVal);
        ping1List.add(ping1Val);
        ping2List.add(ping2Val);
        guList.add(guVal);
        fengCList.add(df.format(fengC));
        pingCList.add(df.format(pingC));
        guCList.add(df.format(guC));
      }

      JSONObject rows = new JSONObject();
      rows.put("date", dateList);
      rows.put("feng", fengList);
      rows.put("ping1", ping1List);
      rows.put("ping2", ping2List);
      rows.put("gu", guList);
      rows.put("fengC", fengCList);
      rows.put("pingC", pingCList);
      rows.put("guC", guCList);

      result.put(lineNo, rows);
    }
    return result;
  }

  /**
   * 电量指标数据
   *
   * @param callDate 年份 例如: 2018、2019、2020
   * @return List<ChargeListDataVo>
   */
  public List<ChargeListDataVo> listElecGridStatic(String callDate) {
    List<ChargeListDataVo> list = chargeListDataMapper.listByProjectNoAndCallDate("bs001", callDate);
    DecimalFormat df = new DecimalFormat("#.00");
    //判断线型
    String lineType = "819";
    if (list != null && !list.isEmpty()) {
      for (ChargeListDataVo vo : list) {
        if (lineType.equals(vo.getLineNo())) {
          vo.setLineNo("甲线");
        } else {
          vo.setLineNo("乙线");
        }
        //数据库 日期格式存放的都是yyyy-MM 格式 这里使用 subString(5)  截取 后续 考虑 使用 commons-lang 包下的工具类改善
        int month = Integer.parseInt(vo.getCallDate().substring(5));
        double fengVal, pingVal, fengC, pingC, guC, sum;
        if (month < 8 || month > 10) {
          fengVal = vo.getFengVal();
          pingVal = vo.getPing1Val() + vo.getPing2Val();
        } else {
          fengVal = vo.getPing1Val() + vo.getPing2Val();
          pingVal = vo.getFengVal();
        }

        fengC = fengVal * vo.getFengPrice();
        pingC = pingVal * vo.getPingPrice();
        guC = vo.getGuVal() * vo.getGuPrice();
        sum = fengC + pingC + guC;
        vo.setFengVal(fengVal);
        vo.setPingVal(df.format(pingVal));
        vo.setFengC(df.format(fengC));
        vo.setPingC(df.format(pingC));
        vo.setGuC(df.format(guC));
        vo.setSum(sum);
      }
    }

    return list;
  }

  /**
   * 获取功率曲线数据
   * param param
   *
   * @return JSONObject
   */
  public JSONObject listHistoryData(JSONObject param) {
    //远程调用URL {@link StatisticsController}
    //String url = GlobalConfig.API_ADDR + "statistics/single/real";
    String url = "http://localhost:3335/statistics/single/real";
    int formatParam = 1;
    int pageSize = 1000;
    String[] tagCodeList = param.getString("tagCode").split(",");
    JSONObject jsonObj = new JSONObject();
    jsonObj.put("sortFlag", 2);
    jsonObj.put("pageNum", 0);
    jsonObj.put("pageCount", pageSize * (tagCodeList.length));
    String deviceNo = param.getString("deviceNo");

    Date date1 = DateUtils.formatStringToDate(param.getString("endTime"), formats[2]);
    Date date2 = DateUtils.rollNOfDate2(date1, formatParam, 1);
    //startTime endTime 格式 yyyyMMddHH
    jsonObj.put("endTime", DateUtils.DateToNum_Dim(date2, 2));
    jsonObj.put("startTime", DateUtils.DateToNum_Dim(date1, 2));

    JSONArray data = getJsonArray(url, tagCodeList, jsonObj, deviceNo);

    JSONObject result = new JSONObject();
    ArrayList<String> timeList = new ArrayList<>();
    ArrayList<Double> varList = new ArrayList<>();

    for (int i = 0; i < data.size(); i++) {
      JSONObject con = data.getJSONObject(i);
      timeList.add(con.getString("timestamp").substring(8));
      JSONObject datas = con.getJSONObject("datas");
      String key = datas.getString("k");
      if ("EPI".equals(key) || "EPE".equals(key) || "QTI".equals(key) || "QTO".equals(key)) {
        varList.add(Double.parseDouble(datas.getJSONArray("v").get(1).toString()));
      } else {
        varList.add(datas.getDouble("v"));
      }
    }
    result.put("time", timeList);
    result.put("var", varList);
    return result;
  }

  /**
   * 取告警历史数据
   *
   * @param param jsonParam
   * @return JSONObject
   */
  public JSONObject listAlarmData(JSONObject param) {
    //远程调用URL {@link StatisticsController}
    //String url = GlobalConfig.API_ADDR + "statistics/fault/real";
    String url = "http://127.0.0.1:3335/statistics/fault/real";
    int dateType = param.getIntValue("dateType");
    int formatParam = formatTime[dateType];
    int pageNum = Integer.parseInt(param.getString("page"));
    int pageSize = Integer.parseInt(param.getString("pageSize"));
    int alarmFlag = param.getIntValue("alarmFlag");
    String subProjectId = param.getString("subProjectId");
    JSONObject param2 = new JSONObject();
    param2.put("sortFlag", 1);
    param2.put("pageNum", pageNum - 1);
    param2.put("pageCount", pageSize);

    Date date1 = DateUtils.formatStringToDate(param.getString("endTime"), formats[dateType]);
    Date date2 = DateUtils.rollNOfDate2(date1, formatParam, 1);
    param2.put("endTime", DateUtils.DateToNum_Dim(date2, 2));
    param2.put("startTime", DateUtils.DateToNum_Dim(date1, 2));
    //id 为系统1的
    param2.put("subProjectId", subProjectId);
    JSONArray excludeTagCodeList = new JSONArray();
    excludeTagCodeList.add("ERR1");
    excludeTagCodeList.add("ERR2");
    excludeTagCodeList.add("ERR3");
    excludeTagCodeList.add("ERR4");
    param2.put("excludeTagCodeList", excludeTagCodeList);
    param2.put("alarmFlag", alarmFlag);
    //远程调用 url: http://47.106.229.3:3335/statistics/fault/real
    JSONObject data = MyHttpClient.postStr(url, param2.toJSONString());
    if (data != null) {
      JSONArray jsonArray = data.getJSONArray("result");
      String totalCount = data.getString("totalCount");
      //log.info("远程调用获取的json数组: {} , 总记录数 : {}", jsonArray, totalCount);
      return resolveDeviceToData(jsonArray, pageNum, pageSize, alarmFlag);
    } else {
      return new JSONObject();
    }
  }

  /**
   * 组装数据
   *
   * @param jsonarr  远程调用转换后的json数组
   * @param pageNum  指定页码
   * @param pageSize 每页大小
   * @return {
   * "unit": "SOC过低告警",
   * "id": 1,
   * "deviceNo": "201800010010043",
   * "time": "20200813170902",
   * "content": "一级告警"
   * }
   */
  public JSONObject resolveDeviceToData(JSONArray jsonarr, int pageNum, int pageSize, int alarmFlag) {
    JSONArray listData = new JSONArray();
    //告警/故障事件名
    String names = stringRedisTemplate.opsForValue().get("alarmNames");
    //告警/故障  状态
    String vals = stringRedisTemplate.opsForValue().get("alarmValues");
    JSONObject alarmNames;
    JSONObject alarmValues;
    if (!StringUtils.isNotEmpty(names) || !StringUtils.isNotEmpty(vals)) {
      //log.info("缓存中没有,直接查询数据库");
      //缓存中没有直接查询数据库
      Pair<JSONObject, JSONObject> pair = selectFromDb();
      alarmNames = pair.getLeft();
      alarmValues = pair.getRight();
      //放入缓存
      initAlarmCache(alarmNames, alarmValues);
    } else {
      //log.info("命中缓存");
      alarmNames = JSONObject.parseObject(names);
    }

    for (int i = 0; i < jsonarr.size(); i++) {
      /**
       * row  此json对象如下形式
       * {
       * 			"deviceNo": 202000020010003,
       * 			"timestamp": 20200908151044,
       * 			"datas": {
       * 				"k": "ASW",
       * 				"v": "16777216"
       *       }
       * }
       */
      JSONObject row = jsonarr.getJSONObject(i);
      /**
       * datas:{"k":"ASW","v":"16777216"}
       */
      JSONObject datas = row.getJSONObject("datas");
      String alarmCode = datas.getString("k");
      /**
       * alarmNames 格式如下:
       * {"ASW":"消防告警"}
       *
       * alarmValues 格式如下:
       * {
       *   "ASW": [
       *     "正常",
       *     "警告"
       *   ]
       *  }
       */
      String isExistAlarmCode = alarmNames.getString(alarmCode);
      if (StringUtils.isBlank(isExistAlarmCode)) {
        continue;
      }
      JSONObject temp = new JSONObject();
      temp.put("id", (pageNum - 1) * pageSize + i + 1);
      temp.put("deviceNo", row.getString("deviceNo"));
      /**
       * {
       *   "ASW": "告警",
       *   "CERR1": "故障"
       * }
       *
       * alarmNames.getString(alarmCode)  对应于MySQL中的 t_alarm_units 表中的 alarm_name 字段的值
       */
      temp.put("unit", alarmNames.getString(alarmCode));
      /**
       * datas:{"k":"ASW","v":"16777216"}
       */
      int dataV = datas.getIntValue("v");

      String alarmLevel;
      if (!StringUtils.isBlank(AlarmEnum.matchAlarmCode(dataV))) {
        alarmLevel = AlarmEnum.matchAlarmCode(dataV);
        temp.put("flag", dataV);
      } else {
        alarmLevel = "正常";
        temp.put("flag", 0);
      }

      temp.put("content", alarmLevel);
      temp.put("time", row.getString("timestamp"));
      listData.add(temp);
    }
    return jsonConvertVo(listData, alarmFlag);
  }

  /**
   * 初始化 缓存  设置不过期
   */
  private void initAlarmCache(JSONObject alarmNames, JSONObject alarmValues) {
    stringRedisTemplate.opsForValue().set("alarmNames", JSON.toJSONString(alarmNames));
    stringRedisTemplate.opsForValue().set("alarmValues", JSON.toJSONString(alarmValues));
  }

  /**
   * 缓存中没有直接从数据库进行查询
   *
   * @return Pair<JSONObject, JSONObject>
   */
  private Pair<JSONObject, JSONObject> selectFromDb() {
    QueryWrapper<AlarmUnits> queryWrapper = new QueryWrapper<AlarmUnits>()
        .eq("project_no", "gk");
    List<AlarmUnits> alarmUnits = alarmUnitsMapper.selectList(queryWrapper);
    JSONObject alarmNameObj = new JSONObject();
    JSONObject alarmLevelTitleObj = new JSONObject();
    for (AlarmUnits unit : alarmUnits) {
      String key = unit.getTagCode();
      String alarmName = unit.getAlarmName();
      String[] levelTitles = unit.getLevelTitle().split(",");
      alarmNameObj.put(key, alarmName);
      alarmLevelTitleObj.put(key, levelTitles);
    }
    return Pair.of(alarmNameObj, alarmLevelTitleObj);
  }

  /**
   * 按日统计  投资收益 => 移峰填谷  用到此业务
   * param param 请求体中获取的参数
   *
   * @return JSONObject
   */
  public JSONObject incomeHourStatis(JSONObject param) {
    String url = GlobalConfig.API_ADDR + "statistics/single/";
    int dateType = param.getIntValue("dateType");
    int formatParam = formatTime[dateType];
    url += dataTitle[dateType];
    String[] tagCodeList = (String[]) param.get("tagCode");
    JSONObject param2 = new JSONObject();
    param2.put("sortFlag", 0);
    param2.put("pageNum", 0);
    param2.put("pageCount", 80);
    String deviceNo = param.getString("deviceId");
    Date date1 = DateUtils.formatStringToDate(param.getString("endTime"), formats[dateType]);
    Date date2 = DateUtils.rollNOfDate2(date1, formatParam, 1);
    param2.put("endTime", DateUtils.DateToNum_Dim(date2, dateType));
    param2.put("startTime", DateUtils.DateToNum_Dim(date1, dateType));

    JSONArray data = getJsonArray(url, tagCodeList, param2, deviceNo);

    System.out.println("按日统计：" + data);
    System.out.println("url：" + url);

    JSONObject result = new JSONObject();
    ArrayList<String> timeList = new ArrayList<>();
    ArrayList<Double> chargePList = new ArrayList<>();
    ArrayList<Double> dischargePList = new ArrayList<>();
    ArrayList<Double> chargeNList = new ArrayList<>();
    ArrayList<Double> dischargeNList = new ArrayList<>();
    ArrayList<Double> incomeList = new ArrayList<>();
    double cSum = 0; //充电量
    double dSum = 0; //放电量
    double cPriceSum = 0; //充电电费
    double dPriceSum = 0; //放电电费
    //double priceSum = 0; //收益
    for (int i = 0; i < data.size(); i++) {
      JSONObject con = data.getJSONObject(i);
      if (con.getJSONObject("statistics").getString("k").equals("EPI")) {
        timeList.add(con.getString("time"));
        double cPriceNum = con.getJSONObject("statistics").getJSONObject("v").getDouble("price");
        double cNum = con.getJSONObject("statistics").getJSONObject("v").getDouble("diffsum");
        cSum += cNum;
        chargePList.add(cPriceNum * (-1));
        chargeNList.add(cNum);
      } else if (con.getJSONObject("statistics").getString("k").equals("EPE")) {
        double dPriceNum = con.getJSONObject("statistics").getJSONObject("v").getDouble("price");
        double dNum = con.getJSONObject("statistics").getJSONObject("v").getDouble("diffsum");
        dSum += dNum;
        dischargePList.add(dPriceNum);
        dischargeNList.add(dNum);
      }
    }

    DecimalFormat df = new DecimalFormat("######0.00");
    for (int j = 0, len = timeList.size(); j < len; j++) {
      cPriceSum += chargePList.get(j);
      dPriceSum += dischargePList.get(j);

      double income = dischargePList.get(j) + chargePList.get(j);
      //priceSum += income;
      incomeList.add(Double.parseDouble(df.format(income)));
    }
    for (int k = timeList.size(); k < 24; k++) {
      chargeNList.add(k, 0.0);
      dischargeNList.add(k, 0.0);
      incomeList.add(k, 0.0);
    }

    result.put("cSum", cSum);
    result.put("dSum", dSum);
    double feng1DVal = Double.parseDouble(df.format(dischargeNList.get(8) + dischargeNList.get(9) + dischargeNList.get(10)));
    double feng1CVal = Double.parseDouble(df.format(chargeNList.get(8) + chargeNList.get(9) + chargeNList.get(10)));
    double feng1P = Double.parseDouble(df.format(incomeList.get(8) + incomeList.get(9) + incomeList.get(10)));
    double feng2DVal = Double.parseDouble(df.format(dischargeNList.get(18) + dischargeNList.get(19) + dischargeNList.get(20)));
    double feng2CVal = Double.parseDouble(df.format(chargeNList.get(18) + chargeNList.get(19) + chargeNList.get(20)));
    double feng2P = Double.parseDouble(df.format(incomeList.get(18) + incomeList.get(19) + incomeList.get(20)));
    double ping11DVal = Double.parseDouble(df.format(dischargeNList.get(6) + dischargeNList.get(7)));
    double ping11CVal = Double.parseDouble(df.format(chargeNList.get(6) + chargeNList.get(7)));
    double ping11P = Double.parseDouble(df.format(incomeList.get(6) + incomeList.get(7)));
    double ping12DVal = Double.parseDouble(df.format(dischargeNList.get(11) + dischargeNList.get(12)));
    double ping12CVal = Double.parseDouble(df.format(chargeNList.get(11) + chargeNList.get(12)));
    double ping12P = Double.parseDouble(df.format(incomeList.get(11) + incomeList.get(12)));
    double ping13DVal = Double.parseDouble(df.format(dischargeNList.get(15) + dischargeNList.get(16) + dischargeNList.get(17)));
    double ping13CVal = Double.parseDouble(df.format(chargeNList.get(15) + chargeNList.get(16) + chargeNList.get(17)));
    double ping13P = Double.parseDouble(df.format(incomeList.get(15) + incomeList.get(16) + incomeList.get(17)));
    double ping14DVal = Double.parseDouble(df.format(dischargeNList.get(21)));
    double ping14CVal = Double.parseDouble(df.format(chargeNList.get(21)));
    double ping14P = Double.parseDouble(df.format(incomeList.get(21)));
    double ping2DVal = Double.parseDouble(df.format(dischargeNList.get(13) + dischargeNList.get(14)));
    double ping2CVal = Double.parseDouble(df.format(chargeNList.get(13) + chargeNList.get(14)));
    double ping2P = Double.parseDouble(df.format(incomeList.get(13) + incomeList.get(14)));
    double gu1DVal = Double.parseDouble(df.format(dischargeNList.get(0) + dischargeNList.get(1) + dischargeNList.get(2) + dischargeNList.get(3) + dischargeNList.get(4) + dischargeNList.get(5)));
    double gu1CVal = Double.parseDouble(df.format(chargeNList.get(0) + chargeNList.get(1) + chargeNList.get(2) + chargeNList.get(3) + chargeNList.get(4) + chargeNList.get(5)));
    double gu1P = Double.parseDouble(df.format(incomeList.get(0) + incomeList.get(1) + incomeList.get(2) + incomeList.get(3) + incomeList.get(4) + incomeList.get(5)));
    double gu2DVal = Double.parseDouble(df.format(dischargeNList.get(22) + dischargeNList.get(23)));
    double gu2CVal = Double.parseDouble(df.format(chargeNList.get(22) + chargeNList.get(23)));
    double gu2P = Double.parseDouble(df.format(incomeList.get(22) + incomeList.get(23)));

    result.put("cPriceSum", df.format(cPriceSum));
    result.put("dPriceSum", df.format(dPriceSum));
    result.put("priceSum", df.format(dPriceSum + cPriceSum));
    result.put("date", Arrays.asList("谷时段(0:00~6:00)", "平1时段(6:00~8:00)", "峰时段(8:00~11:00)", "平1时段(11:00~13:00)", "平2时段(13:00~15:00)", "平1(15:00~18:00)", "峰时段(18:00~21:00)", "平1时段(21:00~22:00)", "谷时段(22:00~24:00)"));
    result.put("price", Arrays.asList("0.296", "0.598", "0.987", "0.598", "0.598", "0.598", "0.987", "0.598", "0.296"));
    result.put("income", Arrays.asList(gu1P, ping11P, feng1P, ping12P, ping2P, ping13P, feng2P, ping14P, gu2P));
    result.put("charge", Arrays.asList(gu1CVal, ping11CVal, feng1CVal, ping12CVal, ping2CVal, ping13CVal, feng2CVal, ping14CVal, gu2CVal));
    result.put("discharge", Arrays.asList(gu1DVal, ping11DVal, feng1DVal, ping12DVal, ping2DVal, ping13DVal, feng2DVal, ping14DVal, gu2DVal));
    result.put("dateT", Arrays.asList("峰时段", "平1时段", "平2时段", "谷时段"));
    result.put("priceT", Arrays.asList("0.987", "0.598", "0.598", "0.296"));
    result.put("incomeT", Arrays.asList((feng1P + feng2P), (ping11P + ping12P + ping13P + ping14P), ping2P, (gu1P + gu2P)));
    result.put("chargeT", Arrays.asList((feng1CVal + feng2CVal), (ping11CVal + ping12CVal + ping13CVal + ping14CVal), ping2CVal, (gu1CVal + gu2CVal)));
    result.put("dischargeT", Arrays.asList((feng1DVal + feng2DVal), (ping11DVal + ping12DVal + ping13DVal + ping14DVal), ping2DVal, (gu1DVal + gu2DVal)));

    return result;
  }

  /**
   * 获取jsonArray数组数据
   *
   * @param url         远程url
   * @param tagCodeList tagCodeList
   * @param param       Json对象
   * @param deviceNo    设备编号
   * @return JsonArray
   */
  private JSONArray getJsonArray(String url, String[] tagCodeList, JSONObject param, String deviceNo) {
    JSONArray statisticsRequestItemList = new JSONArray();
    JSONObject device = new JSONObject();
    device.put("deviceNo", deviceNo);
    device.put("tagCodeList", tagCodeList);
    statisticsRequestItemList.add(device);
    param.put("statisticsRequestItemList", statisticsRequestItemList);
    //远程调用
    return Objects.requireNonNull(MyHttpClient.postStr(url, param.toJSONString())).getJSONArray("result");
  }

  /**
   * 根据账户查询User对象
   *
   * @param account 账户
   * @return User
   */
  public User findByAccount(String account) {
    return userMapper.selectOne(new QueryWrapper<User>().eq("account", account));
  }

  /**
   * 将  JsonArray 中的元素转换到对应的vo 上
   */
  private static JSONObject jsonConvertVo(JSONArray jsonArray, int alarmFlag) {
    List<AlarmVo> vos = new ArrayList<>();
    JSONObject res = new JSONObject();
    for (Object o : jsonArray) {
      JSONObject jsonObj = (JSONObject) o;
      AlarmVo vo = new AlarmVo();
      vo.setId(jsonObj.getInteger("id"));
      vo.setUnit(jsonObj.getString("unit"));
      vo.setFlag(jsonObj.getInteger("flag"));
      vo.setDeviceNo(jsonObj.getString("deviceNo"));
      vo.setTime(jsonObj.getString("time"));
      vo.setContent(jsonObj.getString("content"));

      vos.add(vo);
    }

    List<AlarmVo> filterList
        = vos.stream().filter(v -> v.getFlag() == alarmFlag).collect(Collectors.toList());
    //如果查询不到或者默认 都是查询所有 告警数据
    if (CollectionUtils.isEmpty(filterList)) {
      res.put("total", vos.size());
      res.put("list", vos);
    } else {
      res.put("total", filterList.size());
      res.put("list", filterList);
    }
    return res;
  }
}
