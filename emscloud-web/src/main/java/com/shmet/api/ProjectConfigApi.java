package com.shmet.api;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.shmet.DateTimeUtils;
import com.shmet.dao.mongodb.ChargePolicyCustomDao;
import com.shmet.dao.mongodb.ProjectConfigDao;
import com.shmet.entity.mongo.ChargePolicy;
import com.shmet.entity.mongo.ChargePolicyCustom;
import com.shmet.entity.mongo.ElectricPricePeriod;
import com.shmet.entity.mongo.ProjectConfig;
import com.shmet.entity.mongo.SeasonPeriod;
import com.shmet.entity.mongo.TimePeriod;

@CrossOrigin
@Controller
public class ProjectConfigApi {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    ProjectConfigDao projectConfigDao;

    @Autowired
    ChargePolicyCustomDao chargePolicyCustomDao;

    @ResponseBody
    @RequestMapping("/api/projectconfig/get/one")
    public ProjectConfig getProjectConfig(@RequestParam(value = "subProjectId") long subProjectId) {
        return projectConfigDao.findElectricProjectConfigBySubProjectIdNoCache(subProjectId);
    }

    @ResponseBody
    @RequestMapping("/api/projectconfig/chargepolicy")
    public List<TimePeriod> getChargePolicy(@RequestParam(value = "timestamp") long timestamp, @RequestParam(value = "subProjectId") long subProjectId) {
        List<TimePeriod> rst = null;
        Calendar today = Calendar.getInstance();
        today.setTime(new Date(timestamp));
        int month = today.get(Calendar.MONTH) + 1;
        int dayOfWeek = today.get(Calendar.DAY_OF_WEEK) - 1;


        int day = Integer.parseInt(DateTimeUtils.date2String(today.getTime(), "yyyyMMdd"));

        ProjectConfig projectConfig = projectConfigDao.findElectricProjectConfigBySubProjectIdNoCache(subProjectId);
        if (projectConfig != null && projectConfig.getElectricPricePeriod() != null
                && projectConfig.getElectricPricePeriod().size() > 0) {
            boolean found = false;
            // 首先寻找ChargePolicyCustom表中的充放电配置，如果没有，再查找ProjectConfig表中的通用充放电配置
            ChargePolicyCustom chargePolicyCustom = chargePolicyCustomDao.findBySubProjectId(projectConfig.getSubProjectId(), day);
            if (chargePolicyCustom != null) {
                rst = chargePolicyCustom.getPolicy();
                return rst;
            }
            for (ElectricPricePeriod electricPricePeriod : projectConfig.getElectricPricePeriod()) {
                if (found)
                    break;
                for (SeasonPeriod seasonPeriod : electricPricePeriod.getSeasonPeriod()) {
                    if (found)
                        break;
                    // month是否落在季节配置中
                    if (seasonPeriod.getMonthFrom() <= month
                            && seasonPeriod.getMonthTo() >= month) {
                        // 查看是否落在时间段配置中
                        for (ChargePolicy chargePolicy : electricPricePeriod.getChargePolicy()) {
                            if (dayOfWeek >= chargePolicy.getDayOfWeekFrom() && dayOfWeek <= chargePolicy.getDayOfWeekTo()) {
                                found = true;
                                rst = chargePolicy.getPolicy();
                                break;
                            }
                        }
                    }
                }
            }
        }
        return rst;
    }
}
