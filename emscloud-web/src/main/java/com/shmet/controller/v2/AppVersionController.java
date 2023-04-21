package com.shmet.controller.v2;

import com.alibaba.fastjson.JSONObject;
import com.shmet.entity.dto.Result;
import com.shmet.entity.mysql.gen.AppVersion;
import com.shmet.handler.v2.AppVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.RequestContext;

import java.util.Date;

/**
 * @author tiandonghai
 */
@RestController
public class AppVersionController {

    @Autowired
    private AppVersionService appVersionService;

    @GetMapping("/v2/query/latestapp/{v}")
    public Result queryLatestApp(@PathVariable(required = false, value = "v") String v) {

        return Result.getSuccessResultInfo(appVersionService.queryLatestApp(v));
    }
    @GetMapping("/v2/query/latestapp")
    public Result queryLatestApp2() {

        return Result.getSuccessResultInfo(appVersionService.queryLatestApp("v1"));
    }
    @GetMapping("/v2/query/getappversion")
    public Result getappversion(@RequestParam String version,@RequestParam String device) {

        return Result.getSuccessResultInfo(appVersionService.getAppVersion(version,device));
    }

    /**
     * app发布
     * @param param
     * @return
     */
    @PostMapping("/v2/app/add")
    public Result addAppVersion(@RequestBody JSONObject param){
        if(!param.containsKey("version")){
            return Result.getErrorResultInfo("缺少version参数！");
        }
        if(!param.containsKey("isUpdate")){
            return Result.getErrorResultInfo("缺少isUpdate参数！");
        }
        if(!param.containsKey("device")){
            return Result.getErrorResultInfo("缺少device参数！");
        }

        AppVersion appVersion=new AppVersion();
        String androidUrl="http://www.jz-energy.cn/source/v2_app/publish/jz_energy_v2_v"+param.getString("version")+".apk";
        appVersion.setAndroidUrl(androidUrl);
        appVersion.setIosUrl("itms-apps://itunes.apple.com/cn/app/id1569789342?mt=8");
        appVersion.setCreatetime(new Date());
        appVersion.setDevice(param.getString("device"));
        appVersion.setVersion(param.getString("version"));
        appVersion.setIsUpdate(param.getInteger("isUpdate"));
        appVersion.setV("v2");

        Integer re= appVersionService.addAppVersion(appVersion);
        if(re>0){
            return  Result.getSuccessResultInfo(appVersion,"操作成功！");
        }else {
            return Result.getErrorResultInfo("操作失败！");
        }
    }
}
