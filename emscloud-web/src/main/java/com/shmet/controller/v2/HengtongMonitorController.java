package com.shmet.controller.v2;

import com.shmet.aop.AccessLimit;
import com.shmet.entity.dto.ResultSearch;
import com.shmet.handler.v2.HengtongMonitorService;
import com.shmet.vo.req.HengtongMonitorReq;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/hentong/monitor")
public class HengtongMonitorController {

  private final HengtongMonitorService hengtongMonitorService;

  @AccessLimit(seconds = 3, maxCounts = 5)
  @PostMapping("/pagequery")
  public ResultSearch pageQuery(@RequestBody HengtongMonitorReq req) {
    return hengtongMonitorService.pageQuery(req);
  }

  @AccessLimit(seconds = 3, maxCounts = 5)
  @GetMapping("/gg")
  public String gg() {
    return "GG GG GG";
  }
}
