package com.shmet.controller.v2;

import com.shmet.aop.UserLoginToken;
import com.shmet.entity.dto.Result;
import com.shmet.handler.v2.PhotovoltaicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author
 */
@RestController
@RequestMapping("/v2")
public class PhotovoltaicController {

  @Autowired
  private PhotovoltaicService photovoltaicService;

  @GetMapping("/real/patic")
  @UserLoginToken
  public Result realPhotovoltaicData(@RequestParam List<Long> subprojectIds, @RequestParam int type) {
    return Result.getSuccessResultInfo(photovoltaicService.realPhotovoltaicData(subprojectIds, type));
  }

  @GetMapping("/real/p")
  @UserLoginToken
  public Result calcRealPhotovolP(@RequestParam List<Long> subprojectIds,
                                  @RequestParam(required = false) String Date,
                                  @RequestParam(defaultValue = "2") int flag) {
    return Result.getSuccessResultInfo(photovoltaicService.calcRealPhotovolP(subprojectIds, Date, flag));
  }

  @GetMapping("/real/p2")
  @UserLoginToken
  public Result calcRealPhotovolP2(@RequestParam List<Long> subprojectIds,
                                   @RequestParam(required = false) String Date,
                                   @RequestParam(defaultValue = "2") int flag) {
    return Result.getSuccessResultInfo(photovoltaicService.calcRealPhotovolP2(subprojectIds, Date, flag));
  }

  @GetMapping("/real/epi")
  @UserLoginToken
  public Result caclRealEpi(@RequestParam List<Long> subprojectIds,
                            @RequestParam(required = false) String date,
                            @RequestParam(defaultValue = "2") int type) {
    return Result.getSuccessResultInfo(photovoltaicService.caclRealEpi(subprojectIds, date, type));
  }
}
