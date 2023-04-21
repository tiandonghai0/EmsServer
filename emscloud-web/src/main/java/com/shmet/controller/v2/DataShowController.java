package com.shmet.controller.v2;

import com.alibaba.fastjson.JSONObject;
import com.shmet.DateTimeUtils;
import com.shmet.aop.UserLoginToken;
import com.shmet.bean.RealDataItem;
import com.shmet.dao.mongodb.ProjectConfigDao;
import com.shmet.dao.redis.RealDataRedisDao;
import com.shmet.entity.dto.Result;
import com.shmet.entity.mongo.ProjectConfig;
import com.shmet.entity.mysql.gen.Device;
import com.shmet.handler.v2.DeviceService;
import com.shmet.handler.v2.FineGritIncomeService;
import com.shmet.util.NumberUtil;
import jodd.typeconverter.Convert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/v2/DataShow")
public class DataShowController {

    @Autowired
    FineGritIncomeService fineGritIncomeService;

    @Autowired
    DeviceService deviceService;

    @Autowired
    ProjectConfigDao projectConfigDao;

    @Autowired
    RealDataRedisDao realDataRedisDao;

    /*1 .  储能放电量 按月统计 最近12月*/

    @UserLoginToken
    @ResponseBody
    @PostMapping("/storedEnergyMonthEpe")
    public Result storedEnergyMonthEpe(){
        Date date=new Date();
//        data:{
//            vals:[100,120,130,140,150],  //放电量  12项返回
//            times:[] //12项
//        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");//设置格式
        List<Long> deviceNos=deviceService.getDeviceNos("1");
        List<Double> listValues=new ArrayList<>();
        List<String> listTimes=new ArrayList<>();
        for (int i=12;i>0;i--){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MONTH,0-i);
            String month= simpleDateFormat.format(calendar.getTime());
            JSONObject jsonObject= fineGritIncomeService.storedEnergyMonthEpe(month,deviceNos,2);
            if(jsonObject.size()>0){
                listValues.add(jsonObject.getDouble("epesum")) ;
                listTimes.add(month);
            }
        }
        Map<String,Object> map=new HashMap<>();
        map.put("vals",listValues);
        map.put("times",listTimes);

        return Result.getSuccessResultInfo(map);
    }


    /*储能日放电量 按天*/
    @ResponseBody
    @PostMapping("/toredEnergyDayEpe")
    @UserLoginToken
    public Result toredEnergyDayEpe(){
        Date date=new Date();
//        data:{
//            vals:[100,120,130,140,150],  //放电量  12项返回
//            times:[] //12项
//        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");//设置格式
        List<Long> deviceNos=deviceService.getDeviceNos("1");
        List<Double> listValues=new ArrayList<>();
        List<String> listTimes=new ArrayList<>();
        for (int i=15;i>0;i--){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DATE,0-i);
            String day= simpleDateFormat.format(calendar.getTime());
            JSONObject jsonObject= fineGritIncomeService.storedEnergyMonthEpe(day,deviceNos,1);
            if(jsonObject.size()>0){
                listValues.add(jsonObject.getDouble("epesum")) ;
                listTimes.add(day.replace("-","").substring(4,8));
            }
        }
        Map<String,Object> map=new HashMap<>();
        map.put("vals",listValues);
        map.put("times",listTimes);

        return Result.getSuccessResultInfo(map);
    }


    /*页面非曲线数据汇总 7个块*/
    @PostMapping("/toredEnergyDataSum")
    @ResponseBody
    @UserLoginToken
    public Result toredEnergyDataSum(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        Map<String,Object> map=new HashMap<>();
//        storageElec:{  /*日充放电量实时数据*/
//                   dayEpi:110,//日充电量
//                   dayEpe:100,//日放电量
//
//                    monthEpi:310,//月充电量
//                    monthEpe:300,//月放电量
//
//                    yearEpi:1110,//年充电量
//                    yearEpe:1000,//年放电量
//        },



        map.put("storageElec",getEpiEpe("1","1",true));
//        pvElec:{  //光伏2
//
//            dayEpi:110,//日发电量
//                    monthEpi:110,//月发电量
//                    totalEpi:110,//总发电量
//        },

        map.put("pvElec",getEpiEpe("1","2",false));
//        adElec:{  //岸电
//            shipEpi:123.00 //船用电量
//        },
        Double shipEpi=0.0d;
        Double TPAE=0.0d;
        List<Device> adNumList= deviceService.getDevicesByAnDianZhuang();
        for (Device device:adNumList
             ) {
            if(device.getDeviceNo().toString().contains("20200009")) {//亨通
                RealDataItem realDataItemCA =
                        (RealDataItem) realDataRedisDao.getRealData(device.getDeviceNo(), "CA");
                if (realDataItemCA != null) {
                    RealDataItem realDataItemTPAE =
                            (RealDataItem) realDataRedisDao.getRealData(device.getDeviceNo(), "TPAE");//总电能
                    if (realDataItemTPAE != null) {
                        TPAE = ((ArrayList<Double>) realDataItemTPAE.getData()).get(0);
                        shipEpi+=Math.abs(TPAE);
                    }
                }}else {
                //国信
                RealDataItem realDataItem =
                        (RealDataItem) realDataRedisDao.getRealData(device.getDeviceNo(), "D_TE");
                if (realDataItem != null) {
                    TPAE = Double.parseDouble(realDataItem.getData().toString());
                    shipEpi+=Math.abs(TPAE);
//                }
                }
            }
        }


        Map<String,Object> adElecMap=new HashMap<>();
        adElecMap.put("shipEpi", NumberUtil.convert2(shipEpi));
        map.put("adElec",adElecMap);
//        chargeDevice:{  //充电桩 4
//            dayEpi:110,//日充电量
//                    monthEpi:110,//充发电量
//                    totalEpi:110,//总充电量
//        },
        map.put("chargeDevice",getEpiEpe("1","4",false));
//        enProData:{     /*环保数据*/
//            carbonSave:2000, /*节约碳排放*/
//                    oilSave:100, /*节约油*/
//                    coalSave:400    /*节约煤*/
//        },
        Map<String,Object> enProDataMap=new HashMap<>();
        enProDataMap.put("carbonSave", NumberUtil.convert2(Convert.toDouble(((Map<String,Object>)map.get("chargeDevice")).get("totalEpi"))*1.363d));
        enProDataMap.put("oilSave",NumberUtil.convert2(shipEpi*0.3));
        enProDataMap.put("coalSave",NumberUtil.convert2(Convert.toDouble(((Map<String,Object>)map.get("pvElec")).get("totalEpi"))*0.325d));//一度电消耗325g煤
        map.put("enProData",enProDataMap);
//        projectNum:{
//            storageNum:11,  //储能套数
//                    adNum:2   //岸电套数。
//        },

        Map<String,Object> projectMap=new HashMap<>();
        Long storageNum= projectConfigDao.findAll();
        projectMap.put("storageNum",storageNum==null?"-":(storageNum.intValue()-39));
        projectMap.put("adNum",adNumList==null?"-":adNumList.size());
        map.put("projectNum",projectMap);
//        income:{  //收益构成
//            totalIncome:11122, //总收益
//                    storageIncome:1002,// 储能收益
//                    pvIncome:33,        //光伏收益
//        }
        Double storaenepisumprice=Convert.toDouble (((Map<String, Object>) map.get("storageElec")).get("episumprice"));
        Double storaenepesumprice=Convert.toDouble (((Map<String, Object>) map.get("storageElec")).get("epesumprice"));
        Double pvstoraenepisumprice= Convert.toDouble (((Map<String, Object>) map.get("pvElec")).get("episumprice"));
        Double storageIncome=NumberUtil.convert2(Math.abs(storaenepesumprice)-Math.abs(storaenepisumprice));
        Map<String,Object> incomeMap=new HashMap<>();
        incomeMap.put("totalIncome",NumberUtil.convert2((storageIncome+pvstoraenepisumprice)));

        incomeMap.put("storageIncome",storageIncome);

        incomeMap.put("pvIncome",pvstoraenepisumprice);
        map.put("income",incomeMap);

        return Result.getSuccessResultInfo(map);
    }

    private Map<String,Object> getEpiEpe(String deviceMdoel,String deviceModelType,Boolean isYear){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        List<Long> deviceNos=deviceService.getDeviceNos(deviceMdoel,deviceModelType);
        //日
        String pvElecDay= DateTimeUtils.date2String(calendar.getTime(),DateTimeUtils.FORMAT_yyyy_MM_dd);
        JSONObject pvElecDataDay= fineGritIncomeService.storedEnergyMonthEpe(pvElecDay,deviceNos,1);
        Map<String,Object> pvElecMap=new HashMap<>();
        if(pvElecDataDay.size()>0){
            pvElecMap.put("dayEpe",pvElecDataDay.getDouble("epesum"));
            pvElecMap.put("dayEpi",pvElecDataDay.getDouble("episum"));

        }
        //月
        String pvEleccMonth= DateTimeUtils.date2String(calendar.getTime(),DateTimeUtils.FORMAT_yyyy_MM);
        JSONObject pvElecDataMonth= fineGritIncomeService.storedEnergyMonthEpe(pvEleccMonth,deviceNos,2);
        if(pvElecDataMonth.size()>0){
            pvElecMap.put("monthEpe",NumberUtil.convert2(pvElecDataMonth.getDouble("epesum")+pvElecDataDay.getDouble("epesum")));
            pvElecMap.put("monthEpi",NumberUtil.convert2(pvElecDataMonth.getDouble("episum")+pvElecDataDay.getDouble("episum")));

        }
        //年
        String storageElecYear= DateTimeUtils.date2String(calendar.getTime(),DateTimeUtils.FORMAT_yyyy);
        JSONObject jsonObjectYear= fineGritIncomeService.storedEnergyMonthEpe(storageElecYear,deviceNos,3);
        if(jsonObjectYear.size()>0){
            pvElecMap.put("yearEpe",NumberUtil.convert2(Math.abs(jsonObjectYear.getDouble("epesum"))+pvElecDataDay.getDouble("epesum")));
            pvElecMap.put("yearEpi",NumberUtil.convert2(Math.abs(jsonObjectYear.getDouble("episum"))+pvElecDataDay.getDouble("epesum")));

        }

        //总

        JSONObject pvElecCount= fineGritIncomeService.storedEnergyMonthEpe("2017",deviceNos,4);
        if(pvElecCount.size()>0){
            pvElecMap.put("totalEpe",Math.abs(pvElecCount.getDouble("epesum")));
            pvElecMap.put("totalEpi",Math.abs(pvElecCount.getDouble("episum")));
            pvElecMap.put("epesumprice",NumberUtil.convert2(pvElecCount.getDouble("epesumprice")+pvElecDataDay.getDouble("epesumprice")));
            pvElecMap.put("episumprice",NumberUtil.convert2(pvElecCount.getDouble("episumprice")+pvElecDataDay.getDouble("episumprice")));
        }
        return pvElecMap;
    }
}
