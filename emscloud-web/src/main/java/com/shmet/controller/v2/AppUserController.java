package com.shmet.controller.v2;

import com.alibaba.fastjson.JSONObject;
import com.shmet.aop.UserLoginToken;
import com.shmet.entity.dto.Result;
import com.shmet.handler.v2.AppUserService;
import com.shmet.vo.req.AppUserLoginReq;
import com.shmet.vo.req.AppUserRegisterReq;
import com.shmet.vo.req.BindAccountReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author tiandonghai
 */
@RestController
@RequestMapping("/v2/appuser")
public class AppUserController {

  @Autowired
  private AppUserService appUserService;

  @PostMapping("/login")
  public Result appUserLogin(@Validated @RequestBody AppUserLoginReq req) {
    return appUserService.appUserLogin(req);
  }

  @PostMapping("/register")
  public Result appUserRegister(@Validated @RequestBody AppUserRegisterReq req) {
    int i = appUserService.registerAppUser(req);
    if (i == -1) {
      return Result.getErrorResultInfo("注册对象不能为空");
    }
    if (i == -2) {
      return Result.getErrorResultInfo("验证码已失效,请重新获取");
    }
    if (i == -3) {
      return Result.getErrorResultInfo("输入的验证码不匹配,请重新输入");
    }
    if (i == -4) {
      return Result.getErrorResultInfo("手机号已注册,请更换手机号");
    }
    if (i == 1) {
      return Result.getSuccessResultInfo("注册成功");
    }
    return Result.getErrorResultInfo("注册失败");
  }

  @PostMapping("/bindaccount")
  public Result bindAccount(@RequestBody BindAccountReq req) {
    boolean b = appUserService.bindAccount(req);
    if (b) {
      return Result.getSuccessResultInfo("绑定用户成功");
    }
    return Result.getErrorResultInfo("绑定用户失败");
  }

  @UserLoginToken
  //注销账号
  @PostMapping("/unUser")
  public  Result unUser(@RequestBody JSONObject obj){
    try {
      if (!obj.containsKey("type")) {
        return Result.getErrorResultInfo("type属性不存在");
      }
      if (!obj.containsKey("accout")) {
        return Result.getErrorResultInfo("accout属性不存在");
      }
      int type = obj.getInteger("type");
      String account = obj.getString("accout");
      if (account == null || account.isEmpty()) {
        return Result.getErrorResultInfo("accout不能为空");
      }
      int deleteResult = appUserService.unUser(type, account);
      return Result.getSuccessResultInfo(deleteResult > 0 ? "注销成功" : "未注销数据");
    }catch (Exception ex){
      return Result.getErrorResultInfo(ex.getMessage());
    }
  }
}
