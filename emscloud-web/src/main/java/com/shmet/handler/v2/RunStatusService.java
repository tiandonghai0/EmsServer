package com.shmet.handler.v2;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shmet.ArithUtil;
import com.shmet.DateTimeUtils;
import com.shmet.bean.RealDataItem;
import com.shmet.dao.SubProjectMapper;
import com.shmet.dao.mongodb.DeviceRealDataDao;
import com.shmet.dao.mongodb.ProjectConfigDao;
import com.shmet.entity.mongo.DeviceRealData;
import com.shmet.entity.mongo.ProjectConfig;
import com.shmet.entity.mongo.RealMetricsItem;
import com.shmet.entity.mysql.gen.SubProject;
import com.shmet.vo.RunStatusVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.*;

@Service
public class RunStatusService {

  private static final Logger logger = LoggerFactory.getLogger(RunStatusService.class);

  public static final String REALDATA_CACHE = "RealDataRedisDao.saveRealDataCache";

  @Autowired
  private StringRedisTemplate stringRedisTemplate;

  @Autowired
  private LoginService loginService;

  @Resource
  private SubProjectMapper subProjectMapper;

  @Autowired
  ProjectConfigDao projectConfigDao;

  @Autowired
  private DeviceService deviceService;

  @Autowired
  private FineGritIncomeService fineGritIncomeService;

  @Autowired
  private DeviceRealDataDao deviceRealDataDao;

  public RunStatusVo getFromRedis(String customerNo) {
    RunStatusVo runStatusVo = new RunStatusVo();
    List<Long> subProjectIds = loginService.getSubProjectIds(customerNo);
    List<Long> deviceNos, storeDeviceNos = null;
    if (StringUtils.equalsIgnoreCase("hy", customerNo)) {
      deviceNos = loginService.getBatteryDeviceNos(subProjectIds, 27);
    } else {
      deviceNos = loginService.getBatteryDeviceNos(subProjectIds, 28);
      storeDeviceNos = loginService.getDeviceNosBySubprojectIdAndDeviceModel(subProjectIds, 1, 1);
    }
    if (CollectionUtils.isNotEmpty(subProjectIds)) {
      Double totalCapacity = subProjectMapper.selectOne(new QueryWrapper<SubProject>().eq("sub_project_id", subProjectIds.get(0))).getTotalCapacity();
      runStatusVo.setBatteryCapacity(totalCapacity);
    }
    logger.info("getFromRedis deviceNos : {}", deviceNos);
    if (CollectionUtils.isNotEmpty(deviceNos)) {
      Double ut = listKeysByPattern(String.valueOf(deviceNos.get(0)), "UT");
      Double u = listKeysByPattern(String.valueOf(deviceNos.get(0)), "U");
      Double status = listKeysByPattern(String.valueOf(deviceNos.get(0)), "CSS");
      runStatusVo.setStatus(status);
      runStatusVo.setVoltage(ut == 0d ? u : ut);
    }
    if (CollectionUtils.isNotEmpty(storeDeviceNos)) {
      runStatusVo.setP(listKeysByPattern(String.valueOf(storeDeviceNos.get(0)), "P"));
    }
    for (Long deviceNo : deviceNos) {
      String dno = String.valueOf(deviceNo);
      Double idc = listKeysByPattern(dno, "IDC");
      Double i = listKeysByPattern(dno, "I");
      runStatusVo.setIa(ArithUtil.add(runStatusVo.getIa(), idc == 0d ? i : idc));
      double socVal = (listKeysByPattern(dno, "SOC") == null ? 0d : listKeysByPattern(dno, "SOC"));
      runStatusVo.setElectricCost(convert(socVal));
      //runStatusVo.setP(ArithUtil.add(runStatusVo.getP(), listKeysByPattern(dno, "AP")));
    }
    return runStatusVo;
  }



  //精确查询 应当不会有慢查询存在
  public Double listKeysByPattern(String deviceNo, String tagCode) {
    String res = (String) stringRedisTemplate.opsForHash().get(REALDATA_CACHE, deviceNo + "." + tagCode);
    if (StringUtils.isNotBlank(res)) {
      RealDataItem item = JSONObject.parseObject(res, new TypeReference<RealDataItem>() {
      });
      if (item != null) {
        if(item.getData() instanceof ArrayList<?>){
          for (Object d: (ArrayList<Object>) item.getData()
               ) {
            return Double.valueOf(String.valueOf(d));

          }
        }else if (item.getData() instanceof JSONArray){

            for (Object d:(JSONArray)item.getData()
                 ) {
              if(d instanceof JSONArray){
                return Double.valueOf(String.valueOf(((JSONArray)d).get(0)));
              }

          }
        }
        else {
          return Double.valueOf((String) item.getData());
        }
      }
    }
    return 0d;
  }

  private Double convert(Double v) {
    DecimalFormat f = new DecimalFormat("#.00");
    return Double.parseDouble(f.format(v));
  }

  public Map<String,Object> getFromRedis2(Long subProjectId) {
    RunStatusVo runStatusVo = new RunStatusVo();
    Map<String,Object> map=new HashMap<>();
    //List<Long> subProjectIds = loginService.getSubProjectIds(subProjectId);
    List<Long> subProjectIds = new ArrayList<Long>();
    subProjectIds.add(subProjectId);
    List<Long> deviceNos, storeDeviceNos = null;
    DecimalFormat df = new DecimalFormat("0.00");
      deviceNos = loginService.getBatteryDeviceNos(subProjectIds, 28);
      //storeDeviceNos = loginService.getDeviceNosBySubprojectIdAndDeviceModel(subProjectIds, 1, 1);

    if (CollectionUtils.isNotEmpty(subProjectIds)) {
      Double totalCapacity = subProjectMapper.selectOne(new QueryWrapper<SubProject>().eq("sub_project_id", subProjectIds.get(0))).getTotalCapacity();
      runStatusVo.setBatteryCapacity(totalCapacity);
    }
    logger.info("getFromRedis deviceNos : {}", deviceNos);
    if (CollectionUtils.isNotEmpty(deviceNos)) {
      Double ut = listKeysByPattern(String.valueOf(deviceNos.get(0)), "UT");
      Double u = listKeysByPattern(String.valueOf(deviceNos.get(0)), "U");
      Double status = listKeysByPattern(String.valueOf(deviceNos.get(0)), "CSS");
      runStatusVo.setStatus(status);
      runStatusVo.setVoltage(ut == 0d ? u : ut);
      map.put("U",u);
    }

      runStatusVo.setP(listKeysByPattern(String.valueOf(subProjectId*10000+1), "P"));

    for (Long deviceNo : deviceNos) {
      String dno = String.valueOf(deviceNo);
      Double idc = listKeysByPattern(dno, "IDC");
      Double i = listKeysByPattern(dno, "I");
      runStatusVo.setIa(ArithUtil.add(runStatusVo.getIa(), idc == 0d ? i : idc));
      double socVal = (listKeysByPattern(dno, "SOC") == null ? 0d : listKeysByPattern(dno, "SOC"));
      runStatusVo.setElectricCost(convert(socVal));
      map.put("I",i);
      map.put("BSS",listKeysByPattern(dno, "BSS"));
      //runStatusVo.setP(ArithUtil.add(runStatusVo.getP(), listKeysByPattern(dno, "AP")));
    }
//    emsStatus:"1",  //放电
//            emsAuto:1,      //手自动模式
//            emsP:"1122",    //储能总功率
//            emsCanCharge:"110",  //可充，可放。
//            emsCanDisCharge:"2",
//
//            dayEpi:110,       //当日充电量
//            dayEpe:10,        //当日放电
//            PvEpe:110,        //光伏发电
    ProjectConfig projectConfig = projectConfigDao.findElectricProjectConfigBySubProjectId(subProjectId);
    map.put("CSS",runStatusVo.getStatus());
    map.put("P",runStatusVo.getP()==null?0.0d:runStatusVo.getP());
    map.put("emsAuto",projectConfig.getEssAMStatus());
    map.put("SOC",runStatusVo.getElectricCost());
    map.put("totalCapacity",runStatusVo.getBatteryCapacity());
//    map.put("emsCanCharge",runStatusVo.getStatus());
//    map.put("emsCanDisCharge",runStatusVo.getStatus());

    //获取储能电表电量
    Long energyDbNo=subProjectId*100000+1;
    List<Long> listenergyDbNo= deviceService.getDeviceNos(subProjectId,"1","1");
    if(listenergyDbNo!=null&&listenergyDbNo.size()>0){
      energyDbNo=listenergyDbNo.get(0);
    }
    Object dayEpe="";
    Object dayEpi="";
    List<DeviceRealData> lastEnergyDbList= deviceRealDataDao.findByDeviceLastHours((energyDbNo),Long.parseLong(DateTimeUtils.getTimeLongYYYYMMDD()+"23"),1);
    List<DeviceRealData> firstEnergyDbList=deviceRealDataDao.findByDeviceFirstHours((energyDbNo),Long.parseLong(DateTimeUtils.getTimeLongYYYYMMDD()+"00"),1);
    if(lastEnergyDbList!=null&&lastEnergyDbList.size()!=0&&firstEnergyDbList!=null&&firstEnergyDbList.size()!=0){
      Double lastEpi=0.0d;
      Double lastEpe=0.0d;
      Double firstEpi=0.0d;
      Double firstEpe=0.0d;

      RealMetricsItem lastRealMetricsItem=lastEnergyDbList.get(0).getMetrics().get(lastEnergyDbList.get(0).getMetrics().size()-1);

      if(lastRealMetricsItem.getDatas().containsKey("EPI")){
        Object lastEpiO=lastRealMetricsItem.getDatas().get("EPI");
        if (lastEpiO instanceof Double) {
          lastEpi=Double.parseDouble(String.valueOf(lastEpiO));
        }else if (lastEpiO instanceof ArrayList<?>){
          for (Object d:(ArrayList<?>)lastEpiO)
          {
            lastEpi=Double.parseDouble(String.valueOf(d));
            break;
          }
        }
      }

      if(lastRealMetricsItem.getDatas().containsKey("EPE")){
        Object lastEpeO=lastRealMetricsItem.getDatas().get("EPE");
        if (lastEpeO instanceof Double) {
          lastEpe=Double.parseDouble(String.valueOf(lastEpeO));
        }else if (lastEpeO instanceof ArrayList<?>){
          for (Object d:(ArrayList<?>)lastEpeO)
          {
            lastEpe=Double.parseDouble(String.valueOf(d));
            break;
          }
        }
      }

      RealMetricsItem firstRealMetricsItem=firstEnergyDbList.get(0).getMetrics().get(0);
      if(firstRealMetricsItem.getDatas().containsKey("EPI")){
        Object lastEpiO=firstRealMetricsItem.getDatas().get("EPI");
        if (lastEpiO instanceof Double) {
          firstEpi=Double.parseDouble(String.valueOf(lastEpiO));
        }else if (lastEpiO instanceof ArrayList<?>){
          for (Object d:(ArrayList<?>)lastEpiO)
          {
            firstEpi=Double.parseDouble(String.valueOf(d));
            break;
          }
        }
      }

      if(firstRealMetricsItem.getDatas().containsKey("EPE")){
        Object lastEpeO=firstRealMetricsItem.getDatas().get("EPE");
        if (lastEpeO instanceof Double) {
          firstEpe=Double.parseDouble(String.valueOf(lastEpeO));
        }else if (lastEpeO instanceof ArrayList<?>){
          for (Object d:(ArrayList<?>)lastEpeO)
          {
            firstEpe=Double.parseDouble(String.valueOf(d));
            break;
          }
        }
      }
      dayEpi=df.format((lastEpi-firstEpi));
      dayEpe=df.format((lastEpe-firstEpe));

    }
    map.put("dayEpi",dayEpi);
    map.put("dayEpe",dayEpe);

    //获取光伏电表电量
    Long pvDbNo=subProjectId*100000+20;
    List<Long> listpvDbNo= deviceService.getDeviceNos(subProjectId,"1","2");
    if(listpvDbNo!=null&&listpvDbNo.size()>0){
      pvDbNo=listpvDbNo.get(0);
    }
    Object pvEpe="";
    Object pvEpi="";
    List<DeviceRealData> lastPvDbList= deviceRealDataDao.findByDeviceLastHours((pvDbNo),Long.parseLong(DateTimeUtils.getTimeLongYYYYMMDD()+"23"),1);
    List<DeviceRealData> firstPvDbList=deviceRealDataDao.findByDeviceFirstHours((pvDbNo),Long.parseLong(DateTimeUtils.getTimeLongYYYYMMDD()+"00"),1);
    if(lastPvDbList!=null&&lastPvDbList.size()!=0&&firstPvDbList!=null&&firstPvDbList.size()!=0){
      Double lastEpe=0.0d;
      Double firstEpe=0.0d;

      Double lastEpi=0.0d;
      Double firstEpi=0.0d;

      RealMetricsItem lastRealMetricsItem=lastPvDbList.get(0).getMetrics().get(lastPvDbList.get(0).getMetrics().size()-1);

      if(lastRealMetricsItem.getDatas().containsKey("EPE")){
        Object lastEpeO=lastRealMetricsItem.getDatas().get("EPE");
        if (lastEpeO instanceof Double) {
          lastEpe=Double.parseDouble(String.valueOf(lastEpeO));
        }else if (lastEpeO instanceof ArrayList<?>){
          for (Object d:(ArrayList<?>)lastEpeO)
          {
            lastEpe=Double.parseDouble(String.valueOf(d));
            break;
          }
        }
      }

      RealMetricsItem firstRealMetricsItem=firstPvDbList.get(0).getMetrics().get(0);

      if(firstRealMetricsItem.getDatas().containsKey("EPE")){
        Object lastEpeO=firstRealMetricsItem.getDatas().get("EPE");
        if (lastEpeO instanceof Double) {
          firstEpe=Double.parseDouble(String.valueOf(lastEpeO));
        }else if (lastEpeO instanceof ArrayList<?>){
          for (Object d:(ArrayList<?>)lastEpeO)
          {
            firstEpe=Double.parseDouble(String.valueOf(d));
            break;
          }
        }
      }

      if(lastRealMetricsItem.getDatas().containsKey("EPI")){
        Object lastEpiO=lastRealMetricsItem.getDatas().get("EPI");
        if (lastEpiO instanceof Double) {
          lastEpi=Double.parseDouble(String.valueOf(lastEpiO));
        }else if (lastEpiO instanceof ArrayList<?>){
          for (Object d:(ArrayList<?>)lastEpiO)
          {
            lastEpi=Double.parseDouble(String.valueOf(d));
            break;
          }
        }
      }


      if(firstRealMetricsItem.getDatas().containsKey("EPI")){
        Object firstEpiO=firstRealMetricsItem.getDatas().get("EPI");
        if (firstEpiO instanceof Double) {
          firstEpi=Double.parseDouble(String.valueOf(firstEpiO));
        }else if (firstEpiO instanceof ArrayList<?>){
          for (Object d:(ArrayList<?>)firstEpiO)
          {
            firstEpi=Double.parseDouble(String.valueOf(d));
            break;
          }
        }
      }

      pvEpe=df.format((lastEpe-firstEpe));
      pvEpi=df.format((lastEpi-firstEpi));
    }
    map.put("pvEpe",pvEpe);
    map.put("pvEpi",pvEpi);
    return map;
  }

  public Map<String,Object> getStatusV2(Long subProjectId) {

    Map<String,Object> resultMap=new HashMap<>();

    //市电电表
    List<Long> listcityDbNo= deviceService.getDeviceNos(subProjectId,"1","0");
    Map<String,Double> cityMap=new HashMap<>();
    if(listcityDbNo!=null&&listcityDbNo.size()>0){
      Long cityDbNo=listcityDbNo.get(0);
      Double cityDbEPE= listKeysByPattern(cityDbNo.toString(),"EPE");
      Double cityDbEPI= listKeysByPattern(cityDbNo.toString(),"EPI");
      Double cityDbP= listKeysByPattern(cityDbNo.toString(),"P");

      cityMap.put("EPE",cityDbEPE);
      cityMap.put("EPI",cityDbEPI);
      cityMap.put("P",cityDbP);
    }

    resultMap.put("meterCity",cityMap);

    //光伏电表
    List<Long> listpvDbNo= deviceService.getDeviceNos(subProjectId,"1","2");
    Map<String,Double> pvMap=new HashMap<>();
    if(listpvDbNo!=null&&listpvDbNo.size()>0){
      Long pvDbNo=listpvDbNo.get(0);
      Double pvDbEPE= listKeysByPattern(pvDbNo.toString(),"EPE");
      Double pvDbEPI= listKeysByPattern(pvDbNo.toString(),"EPI");
      Double pvDbP= listKeysByPattern(pvDbNo.toString(),"P");
      pvMap.put("EPE",pvDbEPE);
      pvMap.put("EPI",pvDbEPI);
      pvMap.put("P",pvDbP);
    }
    resultMap.put("meterPv",pvMap);

    //储能电表
    List<Long> listenergyDbNo= deviceService.getDeviceNos(subProjectId,"1","1");
    Map<String,Double> energyMap=new HashMap<>();
    if(listenergyDbNo!=null&&listenergyDbNo.size()>0){
      Long energyDbNo=listenergyDbNo.get(0);
      Double energyDbEPE= listKeysByPattern(energyDbNo.toString(),"EPE");
      Double energyDbEPI= listKeysByPattern(energyDbNo.toString(),"EPI");
      Double energyDbP= listKeysByPattern(energyDbNo.toString(),"P");
      energyMap.put("EPE",energyDbEPE);
      energyMap.put("EPI",energyDbEPI);
      energyMap.put("P",energyDbP);
    }
    resultMap.put("meterEnergy",energyMap);

    //PCS
    List<Long> listPcs= deviceService.getDeviceNos(subProjectId,"27");
    List<Map<String,Object>> listPcsData=new ArrayList<>();
    if(listPcs!=null&&listPcs.size()>0){

      for (Long deviceNo:listPcs
           ) {
        Integer CWS= listKeysByPattern(deviceNo.toString(),"CWS").intValue();
        Integer CCS= listKeysByPattern(deviceNo.toString(),"CCS").intValue();
        Double P= listKeysByPattern(deviceNo.toString(),"P");
        Map<String,Object> pcsMap=new HashMap<>();
        pcsMap.put("CWS",CWS);
        pcsMap.put("CCS",CCS);
        pcsMap.put("P",P);
        listPcsData.add(pcsMap);
      }

    }
    resultMap.put("pcs",listPcsData);

    //光伏逆变器
    List<Long> listPv= deviceService.getDeviceNos(subProjectId,"44");
    List<Map<String,Object>> listPvData=new ArrayList<>();
    if(listPv!=null&&listPv.size()>0){
      for (Long deviceNo:listPv
      ) {
        Double P= listKeysByPattern(deviceNo.toString(),"P");
        Map<String,Object> pvMap2=new HashMap<>();
        pvMap2.put("P",P);
        listPvData.add(pvMap2);
      }
    }
    resultMap.put("pv",listPvData);

    //bms
    List<Long> listBms= deviceService.getDeviceNos(subProjectId,"28");
    Long bmsNo=subProjectId*100000+41;
    if(listBms!=null&&listBms.size()>0)
    {
      bmsNo=listBms.get(0);
    }
    Integer bmsBSS= listKeysByPattern(bmsNo.toString(),"BSS").intValue();
    Integer bmsCSS= listKeysByPattern(bmsNo.toString(),"CSS").intValue();
    Double bmsSOC= listKeysByPattern(bmsNo.toString(),"SOC");
    Double bmsU= listKeysByPattern(bmsNo.toString(),"U");
    Double bmsI= listKeysByPattern(bmsNo.toString(),"I");
    Map<String,Object> bmsMap=new HashMap<>();
    bmsMap.put("BSS",bmsBSS);
    bmsMap.put("CSS",bmsCSS);
    bmsMap.put("SOC",bmsSOC);
    bmsMap.put("U",bmsU);
    bmsMap.put("I",bmsI);
    resultMap.put("bms",bmsMap);

    //环境控制器
    List<Long> listEc= deviceService.getDeviceNos(subProjectId,"46");
    Map<String,Object> ecMap=new HashMap<>();
    if(listEc!=null&&listEc.size()>0){
      Integer ecFIRE= listKeysByPattern(listEc.get(0).toString(),"FIRE").intValue();
      Integer ecACS= listKeysByPattern(listEc.get(0).toString(),"ACS").intValue();
      ecMap.put("FIRE",ecFIRE);
      ecMap.put("ACS",ecACS);
    }
    resultMap.put("ec",ecMap);

    return resultMap;
  }
}
