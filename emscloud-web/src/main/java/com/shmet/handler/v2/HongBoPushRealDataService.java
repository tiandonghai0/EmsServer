package com.shmet.handler.v2;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.shmet.bean.RealDataItem;
import com.shmet.dao.DeviceMapper;
import com.shmet.entity.mysql.gen.Device;
import com.shmet.helper.redis.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wisepaas.datahub.java.sdk.EdgeAgent;
import wisepaas.datahub.java.sdk.common.Const;
import wisepaas.datahub.java.sdk.common.EdgeAgentListener;
import wisepaas.datahub.java.sdk.common.Enum;
import wisepaas.datahub.java.sdk.model.edge.DCCSOptions;
import wisepaas.datahub.java.sdk.model.edge.EdgeAgentOptions;
import wisepaas.datahub.java.sdk.model.edge.EdgeConfig;
import wisepaas.datahub.java.sdk.model.edge.EdgeData;
import wisepaas.datahub.java.sdk.model.event.DisconnectedEventArgs;
import wisepaas.datahub.java.sdk.model.event.EdgeAgentConnectedEventArgs;
import wisepaas.datahub.java.sdk.model.event.MessageReceivedEventArgs;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author
 */
@Service
public class HongBoPushRealDataService {

  public static final Logger log = LoggerFactory.getLogger(HongBoPushRealDataService.class);

  private static final Map<String, String> tagcodeMap = new ConcurrentHashMap<>();
  private static final Map<String, String> deviceMap = new ConcurrentHashMap<>();

  public static final List<String> powerIgnoreTagcodes = Lists.newArrayList("EPE", "EPI", "P", "F");

  static {
    deviceMap.put("202000100010001", "PowerMeter1");
    deviceMap.put("202000100010002", "PowerMeter2");
    deviceMap.put("202000100010003", "PCS1");
    deviceMap.put("202000100010005", "PCS2");
    deviceMap.put("202000100010004", "BMS");
    //============================
//    tagcodeMap.put("EPI", "充电量");
//    tagcodeMap.put("EPE", "放电量");
//    tagcodeMap.put("F", "频率");
//    tagcodeMap.put("P", "功率");
//    tagcodeMap.put("BSS", "电堆状态");
//    tagcodeMap.put("BSS2", "运行状态");
//    tagcodeMap.put("S_SOC", "剩余电量");
//    tagcodeMap.put("S_SOH", "健康度");
//    tagcodeMap.put("COS", "运行状态");
  }

  private final CommonService commonService;

  @Resource
  private DeviceMapper deviceMapper;

  private final ObjectMapper objectMapper;

  @Autowired
  private RedisUtil redisUtil;

  private static final String REALDATA_CACHE = "RealDataRedisDao.saveRealDataCache";
  int flag = 0;

  EdgeAgentOptions options = new EdgeAgentOptions();
  EdgeAgentListener agentListener = new EdgeAgentListener() {
    @Override
    public void Connected(EdgeAgent agent, EdgeAgentConnectedEventArgs args) {
      System.out.println("Connected");
    }

    @Override
    public void Disconnected(EdgeAgent agent, DisconnectedEventArgs args) {
      System.out.println("Disconnected");
    }

    @Override
    public void MessageReceived(EdgeAgent agent, MessageReceivedEventArgs e) {
      System.out.println("MessageReceived");
    }
  };

  final EdgeAgent _edgeAgent = new EdgeAgent(options, agentListener);

  public HongBoPushRealDataService(CommonService commonService, ObjectMapper objectMapper) {
    this.commonService = commonService;
    this.objectMapper = objectMapper;
  }

  public List<Long> getHongboDeviceNos() {
    return deviceMapper.selectList(new QueryWrapper<Device>()
        .eq("sub_project_id", "20200010001")
        .in("device_model", Lists.newArrayList(1, 27, 28)))
        .stream().map(Device::getDeviceNo).collect(Collectors.toList());
  }

  //推送实时数据
  public void pushRealData() {
    //执行连接
    doConnect(_edgeAgent);
    //doUploadCfg(_edgeAgent);
    if (flag < 1) {
      //执行上传配置
      doUploadCfg(_edgeAgent);
      flag++;
      if (flag == 100000) {
        flag = 1;
      }
    }
    //doUploadTest(_edgeAgent);
    //执行数据发送
    sendData(_edgeAgent);
    //doSendDataTest(_edgeAgent);
    //删除所有Tag
    //doDeleteAllCfg(_edgeAgent);
  }

  //Iot Hub 连接时调用
  private void doConnect(EdgeAgent agent) {
    try {
      EdgeAgentOptions options = new EdgeAgentOptions();
      // 连线类型 (DCCS, MQTT), 预设是 DCCS
      options.ConnectType = Enum.ConnectType.DCCS;
      // 若连线类型是 DCCS, DCCSOptions 为必填
      options.DCCS = new DCCSOptions("6kyGJfqeDxTBoshI", "https://dccs.energy-aseri.com/");
      options.UseSecure = false;
      options.AutoReconnect = true;
      // Get from portal
      options.NodeId = "bb23e40c-be86-4209-9a07-d5ae3e828bc2";
      // 节点类型 (Gateway, Device), 预设是 Gateway
      options.Type = Const.EdgeType.Gateway;
      // 若 type 为 Device, 则必填
      options.DeviceId = "HBDevice";
      // 预设是 60 seconds
      options.Heartbeat = 60000;
      options.DataRecover = true;

      this._edgeAgent.Options = options;
      agent.Connect();
    } catch (Exception e) {
      //e.printStackTrace();
      log.error("connect exception: {}", e.getMessage());
    }
  }

  //上传配置
  private void doUploadCfg(EdgeAgent agent) {
    try {
      EdgeConfig config = new EdgeConfig();
      config.Node = new EdgeConfig.NodeConfig();

      config.Node.DeviceList = new ArrayList<>();

      long start = System.currentTimeMillis();
      //log.info("弘博推送任务,开始计时: {}", start);
      EdgeData edgeData = prepareData();

      EdgeConfig.DeviceConfig device = new EdgeConfig.DeviceConfig();
      device.Id = "HBDevice666";
      device.Name = "HBDevice666";
      device.Type = "Smart Device";
      device.Description = "HBDevice666";

      device.AnalogTagList = new ArrayList<>();
//      for (EdgeData.Tag tag : edgeData.TagList) {
//        System.out.println(tag.DeviceId + "\t" + tag.TagName + "\t" + tag.Value);
//      }
      for (EdgeData.Tag tag : edgeData.TagList) {
        //TTag
        EdgeConfig.AnalogTagConfig analogTagConfig = new EdgeConfig.AnalogTagConfig();
        analogTagConfig.Name = tag.TagName;
        analogTagConfig.Description = tag.TagName;
        analogTagConfig.ReadOnly = false;
        analogTagConfig.ArraySize = 0;
        device.AnalogTagList.add(analogTagConfig);
      }
      //============================================
//      for (int i = 1; i <= 100; i++) {
//        //TTag
//        EdgeConfig.TextTagConfig textTag = new EdgeConfig.TextTagConfig();
//        textTag.Name = "tTag" + i;
//        textTag.Description = "tTag" + i;
//        textTag.ReadOnly = false;
//        textTag.ArraySize = 0;
//        device.TextTagList.add(textTag);
//      }
      //============================================
      config.Node.DeviceList.add(device);
      agent.UploadConfig(Const.ActionType.Create, config);

      log.info("弘博推送任务,结束计时,耗时: {}", (System.currentTimeMillis() - start) / 1000);

    } catch (Exception e) {
      //e.printStackTrace();
      log.error("upload cfg exception: {}", e.getMessage());
    }
  }

  //********************************************************
  public void doUploadTest(EdgeAgent agent) {
    try {
      EdgeConfig config = new EdgeConfig();
      config.Node = new EdgeConfig.NodeConfig();

      config.Node.DeviceList = new ArrayList<>();

      EdgeConfig.DeviceConfig device = new EdgeConfig.DeviceConfig();

      device.Id = "TestDevice";
      device.Name = "TestDevice";
      device.Type = "Smart Device";
      device.Description = "TestDevice";

      device.TextTagList = new ArrayList<>();
      for (int i = 0; i < 1000; i++) {
        //TTag
        EdgeConfig.TextTagConfig textTag = new EdgeConfig.TextTagConfig();
        textTag.Name = "TTag" + i;
        textTag.Description = "TTag" + i;
        textTag.ReadOnly = false;
        textTag.ArraySize = 0;
        device.TextTagList.add(textTag);
      }
      config.Node.DeviceList.add(device);
      agent.UploadConfig(Const.ActionType.Create, config);

    } catch (Exception e) {
      //e.printStackTrace();
      log.error("send data exception: {}", e.getMessage());
    }
  }
  //********************************************************

  //删除所有tag and cfg
  private void doDeleteAllCfg(EdgeAgent agent) {
    try {
      if (agent == null) {
        return;
      }
      EdgeConfig config = new EdgeConfig();
      config.Node = new EdgeConfig.NodeConfig();
      agent.UploadConfig(Const.ActionType.Delete, config);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  //********************************************************
  private void doSendDataTest(EdgeAgent agent) {
    agent.SendData(prepareDataTest());
  }

  public EdgeData prepareDataTest() {
    Random random = new Random();
    EdgeData data = new EdgeData();
    for (int i = 0; i < 1000; i++) {
      EdgeData.Tag tTag = new EdgeData.Tag();
      tTag.DeviceId = "TestDevice";
      tTag.TagName = "TTag" + i;
      tTag.Value = random.nextInt(100);

      data.TagList.add(tTag);
    }
    data.Timestamp = new Date();
    return data;
  }
  //********************************************************

  //发送数据
  private void sendData(EdgeAgent agent) {
    try {
      EdgeData edgeData = prepareDataAnologTag();
      agent.SendData(edgeData);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  //准备数据
  private EdgeData prepareDataAnologTag() {
    List<Long> deviceNos = getHongboDeviceNos();
    EdgeData data = new EdgeData();
    List<String> requireTagcodes = Lists.newArrayList("EPI", "EPE", "P", "F", "BSS", "BSS2", "S_SOC", "S_SOH", "COS");
    for (Long dno : deviceNos) {
      //List<String> objStrs = commonService.cursorItems(String.valueOf(dno), requireTagcodes);
      List<String> objStrs = new ArrayList<>();
      for (String tcode : requireTagcodes) {
        String s;
        try {
          s = objectMapper.writeValueAsString(redisUtil.hget(REALDATA_CACHE, dno + "." + tcode, -1));
          if (StringUtils.isNotBlank(s) && !StringUtils.equalsIgnoreCase("null", s)) {
            objStrs.add(s);
          }
        } catch (JsonProcessingException e) {
          e.printStackTrace();
        }
      }
      if (objStrs.size() > 0) {
        for (String objStr : objStrs) {
          EdgeData.Tag tag = new EdgeData.Tag();
          try {
            RealDataItem realDataItem = objectMapper.readValue(objStr, RealDataItem.class);
            String deviceNo = realDataItem.getDeviceNo();
            String tagCode = realDataItem.getTagCode();
            Object redisData = realDataItem.getData();

            Object objData;

            if (redisData instanceof List) {
              List<?> list = (List<?>) redisData;
              if (list.get(0) instanceof List) {
                objData = ((List<Number>) list.get(1)).get(0);
              } else {
                objData = list.get(0);
              }
            } else if (redisData instanceof String) {
              if (String.valueOf(redisData).contains(".")) {
                objData = Double.parseDouble(String.valueOf(redisData));
              } else {
                objData = Long.parseLong(String.valueOf(redisData));
              }
            } else {
              objData = redisData;
            }
            if (NumberUtils.isCreatable(String.valueOf(objData))) {
              tag.DeviceId = "HBDevice666";
              tag.TagName = deviceMap.get(deviceNo) + "." + tagCode;
              if (StringUtils.equals(tagCode, "BSS") || StringUtils.equals(tagCode, "BSS2")) {
                long i = Long.parseLong(String.valueOf(objData));
                tag.Value = i % 2;
              } else {
                tag.Value = objData;
              }
            }
          } catch (IOException e) {
            e.printStackTrace();
          }
          if (StringUtils.isNotBlank(String.valueOf(tag.Value)) && NumberUtils.isCreatable(String.valueOf(tag.Value))) {
            data.TagList.add(tag);
          }
        }
      } else {
        for (String ignorePowerTcode : powerIgnoreTagcodes) {
          EdgeData.Tag tag = new EdgeData.Tag();
          tag.DeviceId = "HBDevice666";
          tag.TagName = deviceMap.get(String.valueOf(dno)) + "." + ignorePowerTcode;
          tag.Value = -1;
          data.TagList.add(tag);
        }
      }
    }
    data.Timestamp = new Date();
    return data;
  }

  //准备数据
  private EdgeData prepareData() {
    List<Long> deviceNos = getHongboDeviceNos();
    EdgeData data = new EdgeData();
    List<String> requireTagcodes = Lists.newArrayList("EPI", "EPE", "P", "F", "BSS", "BSS2", "S_SOC", "S_SOH", "COS");
    for (Long dno : deviceNos) {
      List<String> objStrs = new ArrayList<>();
      for (String tcode : requireTagcodes) {
        RealDataItem item = (RealDataItem) redisUtil.hget(REALDATA_CACHE, dno + "." + tcode, -1);
        String s = null;
        try {
          s = objectMapper.writeValueAsString(item);
        } catch (JsonProcessingException e) {
          e.printStackTrace();
        }
        if (StringUtils.isNotBlank(s) && !StringUtils.equalsIgnoreCase("null", s)) {
          objStrs.add(s);
        }
      }
      if (objStrs.size() > 0) {
        for (String objStr : objStrs) {
          EdgeData.Tag tag = new EdgeData.Tag();
          try {
            RealDataItem realDataItem = objectMapper.readValue(objStr, RealDataItem.class);
            String deviceNo = realDataItem.getDeviceNo();
            String tagCode = realDataItem.getTagCode();
            Object redisData = realDataItem.getData();

            String objData;

            if (redisData instanceof List) {
              List<?> list = (List<?>) redisData;
              if (list.get(0) instanceof List) {
                objData = String.valueOf(
                    ((List<Number>) list.get(1)).get(0)
                );
              } else {
                objData = String.valueOf(list.get(0));
              }
            } else {
              objData = String.valueOf(redisData);
            }
            if (NumberUtils.isCreatable(objData)) {
              tag.DeviceId = "HBDevice666";
              //tag.TagName = deviceNo + "." + tagCode;
              tag.TagName = deviceMap.get(deviceNo) + "." + tagCode;
              if (StringUtils.equals(tagCode, "BSS") || StringUtils.equals(tagCode, "BSS2")) {
                int i = Integer.parseInt(objData);
                //log.info("BSS : [{}]", i % 2);
                tag.Value = String.valueOf(i % 2);
              } else {
                tag.Value = objData;
              }
            }
          } catch (IOException e) {
            e.printStackTrace();
          }
          if (StringUtils.isNotBlank(String.valueOf(tag.Value)) && NumberUtils.isCreatable(String.valueOf(tag.Value))) {
            data.TagList.add(tag);
          }
        }
      } else {
        for (String ignorePowerTcode : powerIgnoreTagcodes) {
          EdgeData.Tag tag = new EdgeData.Tag();
          tag.DeviceId = "HBDevice666";
          tag.TagName = deviceMap.get(String.valueOf(dno)) + "." + ignorePowerTcode;
          tag.Value = null;
          data.TagList.add(tag);
        }
      }
    }
    data.Timestamp = new Date();
    return data;
  }
}
