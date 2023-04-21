package com.shmet.controller.v2;

import com.shmet.aop.UserLoginToken;
import com.shmet.entity.dto.Result;
import com.shmet.handler.v2.RealLoadService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/v2")
public class RealLoadController {

  @Autowired
  private RealLoadService realLoadService;

  /**
   * @param date          传入的日期 格式必须是 yyyy-MM-dd 当不传时默认时当天
   * @param subprojectIds subprojectIds
   * @param flag          1 表示储能电表 0 表示市电电表
   * @return Result
   */
  @GetMapping("/realload")
  @UserLoginToken
  public Result getRealLoadData(@RequestParam(required = false) String date,
                                @RequestParam List<Long> subprojectIds,
                                @RequestParam(defaultValue = "0") int flag) {
    return Result.getSuccessResultInfo(realLoadService.realLoadDataFromMongo(date, subprojectIds, flag));
  }

  @GetMapping("/calc/mxin")
  @UserLoginToken
  public Result caclMaxAndMinVal(@RequestParam(required = false) String date,
                                 @RequestParam List<Long> subprojectIds,
                                 @RequestParam(defaultValue = "0") int flag) throws ExecutionException, InterruptedException {
    return Result.getSuccessResultInfo(realLoadService.caclMaxAndMinVal(date, subprojectIds, flag));
  }

  @GetMapping("/storenergy")
  @UserLoginToken
  public Result realLoadStoreEnergyData(@RequestParam(required = false) String date,
                                        @RequestParam List<Long> subprojectIds, @RequestParam(required = false, defaultValue = "1") int flag) {
    return Result.getSuccessResultInfo(realLoadService.realLoadStoreEnergyData(date, subprojectIds, flag));
  }

  @GetMapping("/loadbms")
  @UserLoginToken
  public Result realLoadBmsData(@RequestParam(required = false) String date,
                                @RequestParam List<Long> subprojectIds) {
    return Result.getSuccessResultInfo(realLoadService.realLoadBmsData(date, subprojectIds));
  }

  @GetMapping("/calcsoc")
  @UserLoginToken
  public Result calcsoc(@RequestParam(required = false) String date,
                        @RequestParam List<Long> subprojectIds) {
    return Result.getSuccessResultInfo(realLoadService.calc(date, subprojectIds));
  }

  //华银计算功率P
  @PostMapping("/huayin/calp")
  @UserLoginToken
  public Result calcHuayinP() {
    return Result.getSuccessResultInfo(realLoadService.calcHuayinP());
  }

  //华银 总发电量 收益
  @PostMapping("/huayin/elecIncome")
  @UserLoginToken
  public Result queryHuayinElec() {
    return Result.getSuccessResultInfo(realLoadService.queryHuayinElec());
  }

  @PostMapping("/storereal")
  @UserLoginToken
  public Result queryRealData(@RequestBody StoreRealDataReq req) {
    return Result.getSuccessResultInfo(realLoadService.queryStoreReal(req.getSubprojectIds(), req.getDeviceModel()));
  }

  @Getter
  @Setter
  static class StoreRealDataReq {
    private List<Long> subprojectIds;
    private Integer deviceModel;
  }

}
