package com.shmet.controller.v2;

import com.shmet.aop.UserLoginToken;
import com.shmet.entity.dto.Result;
import com.shmet.handler.v2.HaiKouShineService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * @author
 */
@RestController
@RequestMapping("/v2/outward")
public class HaiKouShineController {

    @Autowired
    private HaiKouShineService haiKouShineService;

    //根据 deviceNos 查询 Redis 实时缓存中 对应的 所有点位数据进行返回
    @UserLoginToken
    @PostMapping("/realdata/list")
    public Result queryAllRealDatasByDeviceNo(@RequestBody List<Long> deviceNos) {
        Map<String, Map<String, String>> resMap = haiKouShineService.queryAllRealDatasByDeviceNo(deviceNos);
        return Result.getSuccessResultInfo(resMap);
    }

    @PostMapping("/kt")
    @UserLoginToken
    public Result getKtTagcodeValue(@RequestParam Long subprojectId) {
        return Result.getSuccessResultInfo(haiKouShineService.getKtTagcodeValue(subprojectId));
    }

    @PostMapping("/hjkzq")
    @UserLoginToken
    public Result getHjkzq(@RequestBody HjkzqReq req) {
        Long subprojectId = req.getSubprojectId();
        String deviceModel = req.getDeviceModel();
        return Result.getSuccessResultInfo(haiKouShineService.getHjkzq(subprojectId, deviceModel));
    }


    //@OperationLogAnnotation
    @PostMapping("/calc01")
    @UserLoginToken
    public Result calcHaikou01(@RequestBody HaiKouShineService.HaikouQueryCondition01 condition01) {
        return Result.getSuccessResultInfo(haiKouShineService.calcElec(condition01));
    }

    @Getter
    @Setter
    static class HjkzqReq {
        @NotNull(message = "subprojectId不能为空")
        private Long subprojectId;
        private String deviceModel = "46";
    }

}
