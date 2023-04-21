package com.shmet.controller.v2;

import com.shmet.utils.DateUtils;
import com.shmet.entity.dto.Result;
import com.shmet.entity.dto.ResultSearch;
import com.shmet.handler.v2.AutoExamineService;
import com.shmet.handler.v2.AdConnrecordinfoService;
import com.shmet.vo.req.AdCreateReq;
import com.shmet.vo.req.AdQueryReq;
import com.shmet.vo.req.AdUpdateReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sgad")
public class AdConnrecordinfoController {

  @Autowired
  private AdConnrecordinfoService adConnrecordinfoService;

  @Autowired
  private AutoExamineService autoExamineService;

  @GetMapping("/{iccid}")
  public Result getsimInfo(@PathVariable String iccid) {
    return Result.getSuccessResultInfo(autoExamineService.getSimInfo(iccid));
  }

  //分页条件查询
  @PostMapping("/pagelist")
  public ResultSearch pageConditionList(@RequestBody AdQueryReq req) {
    return adConnrecordinfoService.pageConditionList(req);
  }

  //录入连船信息
  @PostMapping("/save")
  public Result saveAdConnrecordinfo(@RequestBody AdCreateReq req) {
    return Result.getSuccessResultInfo(adConnrecordinfoService.saveAdConnrecordinfo(req));
  }

  //结束用电
  @PostMapping("/endelec/{id}")
  public Result endElec(@PathVariable Integer id) {
    return Result.getSuccessResultInfo(adConnrecordinfoService.endElec(id));
  }

  //更新
  @PostMapping("/update")
  public Result update(@RequestBody AdUpdateReq req) {
    String berthTime = req.getBerthTime();
    String departureTime = req.getDepartureTime();
    if (DateUtils.compareTime(berthTime, departureTime) > 0) {
      return Result.getErrorResultInfo("靠泊时间不能晚于离泊时间");
    }
    if (DateUtils.compareTime(berthTime, departureTime) == -99) {
      return Result.getErrorResultInfo("靠泊时间或离泊时间为空");
    }
    int update = adConnrecordinfoService.update(req);
    if (update != -1) {
      return Result.getSuccessResultInfo(update);
    } else {
      return Result.getErrorResultInfo("id为空或者未查询到记录");
    }
  }
}
