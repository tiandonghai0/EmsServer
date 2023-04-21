package com.shmet.handler.v2;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shmet.ArithUtil;
import com.shmet.dao.DeviceMapper;
import com.shmet.entity.mysql.gen.Device;
import com.shmet.util.NumberUtil;
import com.shmet.utils.DateUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author
 */
@Service
public class DeviceService extends ServiceImpl<DeviceMapper, Device> {

  @Resource
  DeviceMapper deviceMapper;

  @Resource
  MongoTemplate mongoTemplate;

  /**
   * 根据 subProjectId 和 deviceModel 查询所有的 deviceNos
   *
   * @param subProjectId subProjectId
   * @param deviceModel  deviceModel
   * @return deviceNos
   */
  public List<Long> getDeviceNos(Long subProjectId, String deviceModel) {
    QueryWrapper<Device> queryWrapper=new QueryWrapper<>();
    queryWrapper.eq("sub_project_id", subProjectId);
    queryWrapper.eq("device_model", deviceModel);
    if(deviceModel=="1"){
      queryWrapper.eq("device_model_type", "1");
    }
    return deviceMapper.selectList(queryWrapper).stream().map(Device::getDeviceNo).collect(Collectors.toList());
  }

  public List<Long> getDeviceNos(String deviceModel) {
    QueryWrapper<Device> queryWrapper=new QueryWrapper<>();
    queryWrapper.eq("device_model", deviceModel);
    if(deviceModel.equals("1")){
      queryWrapper.eq("device_model_type", "1");
    }
    return deviceMapper.selectList(queryWrapper).stream().map(Device::getDeviceNo).collect(Collectors.toList());
  }

  public List<Long> getDeviceNos(String deviceModel,String deviceModelType) {
    QueryWrapper<Device> queryWrapper=new QueryWrapper<>();
    queryWrapper.eq("device_model", deviceModel);
    queryWrapper.eq("device_model_type", deviceModelType);

    return deviceMapper.selectList(queryWrapper).stream().map(Device::getDeviceNo).collect(Collectors.toList());
  }

  public List<Long> getDeviceNos(Long subProjectId, String deviceModel,String deviceModelType) {
    QueryWrapper<Device> queryWrapper=new QueryWrapper<>();
    queryWrapper.eq("sub_project_id", subProjectId);
    queryWrapper.eq("device_model", deviceModel);
    queryWrapper.eq("device_model_type", deviceModelType);
    return deviceMapper.selectList(queryWrapper).stream().map(Device::getDeviceNo).collect(Collectors.toList());
  }

  /**
   * 根据 subProjectId 和 deviceModel 查询所有的 deviceNos
   *
   * @param subProjectId subProjectId
   * @param deviceModel  deviceModel
   * @return deviceNos
   */
  public List<Integer> getDeviceIds(Long subProjectId, String deviceModel) {
    QueryWrapper<Device> queryWrapper=new QueryWrapper<>();
    queryWrapper.eq("sub_project_id", subProjectId);
    queryWrapper.eq("device_model", deviceModel);

    return deviceMapper.selectList(queryWrapper).stream().map(Device::getDeviceId).collect(Collectors.toList());
  }

  /**
   * 根据 deviceNos 去 mongo 中 查询 当天 00-23 点之间的 DeviceRealData
   *
   * @param deviceNos 设备编号列表
   * @return List<DeviceRealDataBean>
   */
  public JSONArray queryFromMongo(List<Long> deviceNos) {
    Pair<Long, Long> pair = DateUtils.get00And23();
    JSONArray jsonArray = new JSONArray();
    AggregationResults<Document> results = getDocuments(deviceNos, pair);
    for (Document document : results.getMappedResults()) {
      JSONObject jsonObj = new JSONObject();
      Long id = document.getLong("_id");
      Double epe = Double.valueOf(String.format("%.2f", document.getDouble("diffsumEPE")));
      Double epi = Double.valueOf(String.format("%.2f", document.getDouble("diffsumEPI")));

      jsonObj.put("deviceNo", id);
      jsonObj.put("dayEpe", epe);
      jsonObj.put("dayEpi", epi);

      jsonArray.add(jsonObj);
    }

    return jsonArray;
  }

  public Pair<Double, Double> mutiplyElectricSum(List<Long> deviceNos) {
    Pair<Long, Long> pair = DateUtils.get00And23();
    AggregationResults<Document> results = getDocuments(deviceNos, pair);
    Map<String, Double> map = new LinkedHashMap<>();
    for (Document document : results.getMappedResults()) {
      double epe = NumberUtil.convert2(Double.parseDouble(String.valueOf(document.get("diffsumEPE"))));
      double epi = NumberUtil.convert2(Double.parseDouble(String.valueOf(document.get("diffsumEPI"))));
      Double dayEpe = map.get("dayEpe");
      Double dayEpi = map.get("dayEpi");
      if (dayEpe != null) {
        map.put("dayEpe", ArithUtil.add(dayEpe, epe));
      } else {
        map.put("dayEpe", epe);
      }
      if (dayEpi != null) {
        map.put("dayEpi", ArithUtil.add(dayEpi, epi));
      } else {
        map.put("dayEpi", epi);
      }
    }

    return Pair.of(map.get("dayEpe"), map.get("dayEpi"));
  }

  private AggregationResults<Document> getDocuments(List<Long> deviceNos, Pair<Long, Long> pair) {
    Aggregation agg = Aggregation.newAggregation(
        Aggregation.match(Criteria.where("deviceNo").in(deviceNos).and("hour").lte(pair.getRight()).gte(pair.getLeft())),
        Aggregation.group("deviceNo")
            .sum("statistics.EPE.diffsum").as("diffsumEPE")
            .sum("statistics.EPI.diffsum").as("diffsumEPI"));
    return mongoTemplate.aggregate(agg, "deviceRealData", Document.class);
  }

  /**
   * 获取岸电桩设备
   * @return
   */
  public List<Device> getDevicesByAnDianZhuang(){
    LambdaQueryWrapper<Device> queryWrapper = new LambdaQueryWrapper<>();
    //queryWrapper.likeRight(Device::getSubProjectId, 20200009L);
    queryWrapper.and((wrapper)->{
      wrapper.likeRight(Device::getSubProjectId, 20200009L)
              .or().in(Device::getSubProjectId,20210901L,20210902L);
    });
   return deviceMapper.selectList(queryWrapper);
  }

}
