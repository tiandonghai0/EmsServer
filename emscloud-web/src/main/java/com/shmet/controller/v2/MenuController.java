package com.shmet.controller.v2;

import com.alibaba.fastjson.JSONObject;
import com.shmet.aop.UserLoginToken;
import com.shmet.entity.dto.Result;
import com.shmet.entity.mysql.gen.Menu;
import com.shmet.handler.v2.MenuService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author
 */
@RestController
@RequestMapping("/v2")
public class MenuController {

  @Resource
  private MenuService menuService;

  @GetMapping("/list/with/tree")
  @UserLoginToken
  public Result listWithTree(@RequestParam String account) {
    return Result.getSuccessResultInfo(menuService.listwithtree(account));
  }

  @PostMapping("/add/menu")
  @UserLoginToken
  public Result addMenu(@RequestBody Menu menu) {
    return Result.getSuccessResultInfo(menuService.addMenu(menu));
  }

  /**
   * 获取app岸电底部菜单
   * @return
   */
  @PostMapping("/menu/getAppAdMenu")
  @UserLoginToken
  public Result getAppAdMenu(){
    JSONObject menu=new JSONObject();
    menu.put("name","系统概览");
    menu.put("icon","setting");
    menu.put("url","/index");

    JSONObject menu2=new JSONObject();
    menu2.put("name","实时监测");
    menu2.put("icon","monitor");
    menu2.put("url","/monitor");

    JSONObject menu3=new JSONObject();
    menu3.put("name","运行数据");
    menu3.put("icon","run");
    menu3.put("url","/run_data");

    JSONObject menu4=new JSONObject();
    menu4.put("name","故障报警");
    menu4.put("icon","warning");
    menu4.put("url","/warning");
    List<JSONObject> list=new ArrayList<>();
    list.add(menu);
    list.add(menu2);
    list.add(menu3);
    list.add(menu4);
    return Result.getSuccessResultInfo(list);
  }
}
