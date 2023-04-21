package com.shmet.quartz;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shmet.DateTimeUtils;
import com.shmet.bean.Ehang365.EHang365UsageUpload;
import com.shmet.bean.Ehang365.EHang365UsageUploadData;
import com.shmet.bean.Ehang365.EHang365UsageUploadDataPoints;
import com.shmet.bean.RealDataItem;
import com.shmet.dao.DeviceMapper;
import com.shmet.dao.mongodb.DeviceRealDataDao;
import com.shmet.dao.mongodb.ProjectConfigDao;
import com.shmet.dao.mongodb.ShorePowerLogDataDao;
import com.shmet.dao.mysql.TDeviceDao;
import com.shmet.dao.mysql.mapper.TDeviceMapperDao;
import com.shmet.dao.redis.RealDataRedisDao;
import com.shmet.entity.mysql.gen.Device;
import com.shmet.helper.JsonUtils;
import com.shmet.helper.RequestUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import com.shmet.handler.api.BaishuDataProvideHandler;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.annotation.Resource;
import java.util.*;

@Component
@EnableScheduling
@DisallowConcurrentExecution
public class BaishuDataProvideJob implements Job {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    RealDataRedisDao redisCacheDao;

    @Autowired
    ShorePowerLogDataDao shorePowerLogDataDao;

    @Autowired
    DeviceRealDataDao deviceRealDataDao;

    @Autowired
    TDeviceDao tDeviceDao;

    @Autowired
    TDeviceMapperDao tDeviceMapperDao;

    @Autowired
    DeviceMapper deviceMapper;

    @Autowired
    RealDataRedisDao realDataRedisDao;

    @Autowired
    ProjectConfigDao projectConfigDao;

    @Autowired
    private RabbitTemplate rabbitTemplate;
//    @Autowired
//    BaishuDataProvideHandler baishuDataProvideHandler;


    @Override
    public void execute(JobExecutionContext context) {
        //这里改为船E行 定时上报电能接口
        long projectId = 20200009L;

        try {

            Calendar today = Calendar.getInstance();
            List<Device> list = deviceMapper.selectList(new QueryWrapper<Device>().like("sub_project_id", "20200009").eq("hengtong_ispush",1));
            if (list != null && list.size() > 0) {
                logger.info("Ehang365Push start,size：" + list.size());

                for (Device d : list
                ) {
                    //System.out.println("Ehang365Push:"+d.getDeviceNo());
                    RealDataItem realDataItemTPAE = (RealDataItem) realDataRedisDao.getRealData(d.getDeviceNo(), "TPAE");//总电能
                    if (realDataItemTPAE != null) {

                        Date realDate =
                                DateTimeUtils.parseDate(realDataItemTPAE.getDateTime(), DateTimeUtils.FORMAT_YYYYMMDDHHMMSS);
                        long second = DateTimeUtils.differFromSecond(realDate, today.getTime());

                        // 6分钟之内的值均可信
                        if (second > 360) {
                            logger.info("Ehang365Push,second大于360，"+second);
                            continue;
                        }

                        double TPAE = ((ArrayList<Double>) realDataItemTPAE.getData()).get(0);
                        EHang365UsageUpload eHang365UsageUpload = new EHang365UsageUpload();
                        eHang365UsageUpload.setDeviceCode(d.getDeviceNo().toString());

                        List<EHang365UsageUploadData> eHang365UsageUploadDataList = new ArrayList<EHang365UsageUploadData>();
                        EHang365UsageUploadData eHang365UsageUploadData = new EHang365UsageUploadData();
                        eHang365UsageUploadData.setDataStream("Ep");

                        List<EHang365UsageUploadDataPoints> eHang365UsageUploadDataPointsList = new ArrayList<EHang365UsageUploadDataPoints>();
                        EHang365UsageUploadDataPoints eHang365UsageUploadDataPoints = new EHang365UsageUploadDataPoints();
                        eHang365UsageUploadDataPoints.setTime(new Date().getTime());
                        //eHang365UsageUploadDataPoints.setTime(DateTimeUtils.parseDate(DateTimeUtils.dataTimeLongToString(Long.parseLong(realDataItemTPAE.getDateTime())), DateTimeUtils.FORMAT_yyyy_MM_dd_HH_mm_ss).getTime());
                        eHang365UsageUploadDataPoints.setValue(TPAE);
                        eHang365UsageUploadDataPointsList.add(eHang365UsageUploadDataPoints);
                        eHang365UsageUploadData.setDataPoints(eHang365UsageUploadDataPointsList);
                        eHang365UsageUploadDataList.add(eHang365UsageUploadData);
                        eHang365UsageUpload.setData(eHang365UsageUploadDataList);
                        //logger.info("Ehang365Push:" + JsonUtils.objToString(eHang365UsageUpload));
                        String url = "https://andian.asun.cloud/api/usageUploadHT";
                        Map<String, Object> map=new HashMap<>();
                        map.put("data",eHang365UsageUpload.getData());
                        map.put("deviceCode",eHang365UsageUpload.getDeviceCode());
                        MultiValueMap<String, String> headersMap=new LinkedMultiValueMap<String, String>();
                        headersMap.add("api-key","ddf5239bf35c07c49196");
                        JSONObject object = RequestUtils.postJson(url, map,headersMap, JSONObject.class);
                        //System.out.println("Ehang365Push:参数："+JsonUtils.objToString(map)+",返回："+JsonUtils.objToString(object));
                        if(object.getBoolean("success")){
                            //System.out.println("中天push成功："+eHang365UsageUpload.getDeviceCode());
                        }else{
                            //System.out.println("中天push失败："+eHang365UsageUpload.getDeviceCode()+","+object.getString("message"));
                            logger.info("Ehang365Push,中天push失败："+eHang365UsageUpload.getDeviceCode()+","+object.getString("message"));
                        }
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("亨通job", ex);
        }

//        try {
//            logger.info("朗新推送任务开始");
//            // baishuDataProvideHandler.deliverData();
//            baishuDataProvideHandler.getListData();
//            logger.info("朗新推送任务结束");
//        } catch (Exception e) {
//            logger.error("朗新推送任务失败", e);
//        }
    }
}
