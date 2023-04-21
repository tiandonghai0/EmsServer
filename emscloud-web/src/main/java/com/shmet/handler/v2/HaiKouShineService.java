package com.shmet.handler.v2;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.shmet.bean.RealDataItem;
import com.shmet.dao.DeviceMapper;
import com.shmet.dao.mysql.TDeviceDao;
import com.shmet.entity.mysql.gen.Device;
import com.shmet.entity.mysql.gen.TDevice;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * @author
 */
@Service
public class HaiKouShineService {

    public static final String REALDATA_CACHE = "RealDataRedisDao.saveRealDataCache";
    private static final Logger log = LoggerFactory.getLogger(HaiKouShineService.class);

    private final CommonService commonService;

    private final ObjectMapper objectMapper;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Resource
    private DeviceMapper deviceMapper;

    @Autowired
    TDeviceDao deviceDao;

    private final RedisTemplate<String, Object> redisTemplate;

    public HaiKouShineService(CommonService commonService,
                              ObjectMapper objectMapper,
                              RedisTemplate<String, Object> redisTemplate) {
        this.commonService = commonService;
        this.objectMapper = objectMapper;
        this.redisTemplate = redisTemplate;
    }

    public Map<String, Map<String, String>> queryAllRealDatasByDeviceNo(List<Long> deviceNos) {
        Map<String, Map<String, String>> resMap = new LinkedHashMap<>();
        for (Long dno : deviceNos) {
            Set<String> objStrs = commonService.listKeysByPattern(REALDATA_CACHE, dno + ".*");
            Map<String, String> map = new LinkedHashMap<>();
            for (String item : objStrs) {
                try {
                    RealDataItem realItem = objectMapper.readValue(item, RealDataItem.class);
                    if (realItem != null) {
                        String tagCode = realItem.getTagCode();
                        Object tagcodeVal = realItem.getData();
                        if (tagcodeVal instanceof String) {
                            map.put(tagCode, String.valueOf(tagcodeVal));
                        }else if (tagcodeVal instanceof ArrayList<?>){

                            for (Object o:(ArrayList<?>)tagcodeVal
                                 ) {
                                if(o instanceof Double){
                                    map.put(tagCode,String.valueOf(o));
                                    break;
                                }else if(o instanceof ArrayList<?>){
                                    for (Object o1:(ArrayList<?>)o
                                         ) {
                                        if(o1 instanceof Double){
                                            map.put(tagCode,String.valueOf(o1));
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        map.put("Time", realItem.getDateTime());

                    }
                } catch (IOException e) {
                    log.error("string转为java对象出错");
                }
            }

            TDevice device= deviceDao.findByDeviceNo(dno);
            map.put("Name",device==null?null:device.getDeviceName());
            resMap.put(String.valueOf(dno), map);
        }

        return resMap;
    }

    //查询海口光储充空调点位值
    public List<KtVo> getKtTagcodeValue(Long subprojectId) {
        List<KtVo> res = new ArrayList<>();
        List<Long> deviceNos = getDeviceNos(subprojectId, "25");
        log.info("开始计时: {}", System.currentTimeMillis());
        for (Long dno : deviceNos) {
            KtVo vo = new KtVo();
            vo.setDeviceNo(dno);

            Map<String, String> dataMap = Maps.newLinkedHashMap();

            RealDataItem airOnO = (RealDataItem) redisTemplate.opsForHash().get("RealDataRedisDao.saveRealDataCache", dno + ".AirOn");
            RealDataItem airPwrO = (RealDataItem) redisTemplate.opsForHash().get("RealDataRedisDao.saveRealDataCache", dno + ".AirPwr");
            RealDataItem airTempO = (RealDataItem) redisTemplate.opsForHash().get("RealDataRedisDao.saveRealDataCache", dno + ".AirTemp");
            RealDataItem battAeraTempO = (RealDataItem) redisTemplate.opsForHash().get("RealDataRedisDao.saveRealDataCache", dno + ".BattAeraTemp");

            addToList("AirOn", airOnO, dataMap);
            addToList("AirPwr", airPwrO, dataMap);
            addToList("AirTemp", airTempO, dataMap);
            addToList("BattAeraTemp", battAeraTempO, dataMap);

            vo.setData(dataMap);

            res.add(vo);
        }

        log.info("计时结束: {}", System.currentTimeMillis());
        return res;
    }

    public List<KtVo> getHjkzq(Long subprojectId, String deviceModel) {
        List<KtVo> res = new ArrayList<>();
        List<Long> deviceNos = getDeviceNos(subprojectId, deviceModel);
        deviceNos.forEach(dno -> {
            KtVo vo = new KtVo();
            vo.setDeviceNo(dno);
            Map<String, String> map = getData(dno);
            vo.setData(map);
            res.add(vo);
        });
        return res;
    }

    private Map<String, String> getData(Long dno) {
        Map<String, String> map = new LinkedHashMap<>();
        String sdno = String.valueOf(dno);
        Cursor<Map.Entry<Object, Object>> cursor = redisTemplate.opsForHash().scan("RealDataRedisDao.saveRealDataCache", ScanOptions.scanOptions().count(Integer.MAX_VALUE).match(sdno + ".*").build());
        while (cursor.hasNext()) {
            Map.Entry<Object, Object> entry = cursor.next();
            if (entry.getKey() instanceof String) {
                String ks = (String) entry.getKey();
                String key = StrUtil.split(ks, ".").get(1);
                RealDataItem item = (RealDataItem) entry.getValue();
                Object v = item.getData();
                String vs;
                if (v instanceof Number) {
                    vs = String.valueOf(v);
                } else if (v instanceof List<?>) {
                    List<?> list = (List<?>) v;
                    vs = String.valueOf(list.get(0));
                } else {
                    vs = String.valueOf(v);
                }
                map.put(key, vs);
            }
        }
        return map;
    }

    static class KtVo {
        private Long deviceNo;
        private Map<String, String> data;

        public Long getDeviceNo() {
            return deviceNo;
        }

        public void setDeviceNo(Long deviceNo) {
            this.deviceNo = deviceNo;
        }

        public Map<String, String> getData() {
            return data;
        }

        public void setData(Map<String, String> data) {
            this.data = data;
        }
    }

    private void addToList(String tagcode, RealDataItem item, Map<String, String> map) {
        if (Objects.nonNull(item)) {
            map.put(tagcode, String.valueOf(item.getData()));
        }
    }

    public List<Long> getDeviceNos(Long subProjectId, String deviceModel) {
        return deviceMapper.selectList(new QueryWrapper<Device>()
                        .eq("sub_project_id", subProjectId)
                        .eq("device_model", deviceModel))
                .stream().map(Device::getDeviceNo).collect(Collectors.toList());
    }

    public HaikouElecRes calcElec(HaikouQueryCondition01 condition01) {
        String sdate = commonService.transferDate(condition01.getDate(), condition01.getDateType());
        List<Long> deviceNos = commonService.getDeviceNos(condition01.getSubprojectIds(), condition01.getDeviceModelType(), condition01.getDeviceModel());
        MatchOperation basicMatch = commonService.basicAgg(sdate, condition01.getDateType(), deviceNos);
        Aggregation agg = null;
        if (condition01.getDateType() == 1) {
            agg = Aggregation.newAggregation(
                    basicMatch,
                    Aggregation.project("deviceNo").andExclude("_id")
                            .and("$hour").as("_id")
                            .and("$statistics.EPI.diffsum").as("epidiffsum")
                            .and("$statistics.EPE.diffsum").as("epediffsum"),
                    Aggregation.sort(Sort.by("_id").ascending())
            );
        }
        if (condition01.getDateType() == 2) {
            agg = Aggregation.newAggregation(
                    basicMatch,
                    Aggregation.project("deviceNo").andExclude("_id")
                            .and("$hour").substring(0, 8).as("day")
                            .and("$statistics.EPI.diffsum").as("epidiffsum")
                            .and("$statistics.EPE.diffsum").as("epediffsum"),
                    Aggregation.group("day").sum("epidiffsum").as("epidiffsum").sum("epediffsum").as("epediffsum"),
                    Aggregation.sort(Sort.by("_id").ascending())
            );
        }
        if (condition01.getDateType() == 3) {
            agg = Aggregation.newAggregation(
                    basicMatch,
                    Aggregation.project("deviceNo").andExclude("_id")
                            .and("$hour").substring(0, 6).as("month")
                            .and("$statistics.EPI.diffsum").as("epidiffsum")
                            .and("$statistics.EPE.diffsum").as("epediffsum"),
                    Aggregation.group("month").sum("epidiffsum").as("epidiffsum").sum("epediffsum").as("epediffsum"),
                    Aggregation.sort(Sort.by("_id").ascending())
            );
        }
        if (agg != null) {
            AggregationResults<Document> aggRes = mongoTemplate.aggregate(agg, "deviceRealData", Document.class);
            List<HaikouElec> elecs = aggRes.getMappedResults().stream().map(doc -> {
                String time = String.valueOf(doc.get("_id") != null ? doc.get("_id") : "");
                String epe = String.valueOf(doc.get("epidiffsum") != null ? doc.get("epidiffsum") : "");
                String epi = String.valueOf(doc.get("epediffsum") != null ? doc.get("epediffsum") : "");
                return new HaikouElec(time, epe, epi);
            }).collect(toList());
            double epesum = elecs.stream().mapToDouble(o -> Double.parseDouble(o.getEpe())).sum();
            double episum = elecs.stream().mapToDouble(o -> Double.parseDouble(o.getEpi())).sum();

            return new HaikouElecRes(elecs, epesum, episum);
        }
        return new HaikouElecRes(Lists.newArrayList(), 0d, 0d);
    }

    @Getter
    @Setter
    @ToString
    public static class HaikouQueryCondition01 {
        private String date;
        //默认是1
        private int dateType = 1;
        private List<Long> subprojectIds;
        //默认是0 0 市电 4 充电桩
        private int deviceModelType = 0;
        //默认是1
        private int deviceModel = 1;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    static class HaikouElecRes {
        private List<HaikouElec> elecs;
        private Double epesum;
        private Double episum;

        public HaikouElecRes(List<HaikouElec> elecs, Double epesum, Double episum) {
            this.elecs = elecs;
            this.epesum = epesum;
            this.episum = episum;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    static class HaikouElec {
        private String time;
        private String epe;
        private String epi;

        public HaikouElec(String time, String epe, String epi) {
            this.time = time;
            this.epe = epe;
            this.epi = epi;
        }
    }
}
