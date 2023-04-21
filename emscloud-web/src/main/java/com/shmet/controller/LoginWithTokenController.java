package com.shmet.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.shmet.Consts;
import com.shmet.aop.OperationLogAnnotation;
import com.shmet.bean.PostResponseExBean;
import com.shmet.bean.v2.ConfigEx;
import com.shmet.bean.v2.ResponseDataEx;
import com.shmet.dao.ProjectMapper;
import com.shmet.dao.SubProjectMapper;
import com.shmet.dao.UserMapper;
import com.shmet.dao.mongodb.ProjectConfigDao;
import com.shmet.entity.dto.Result;
import com.shmet.entity.mongo.ProjectConfig;
import com.shmet.entity.mysql.gen.*;
import com.shmet.handler.api.BusinessDataService;
import com.shmet.handler.v2.LoginService;
import com.shmet.thirdpart.AliyunSmsService;
import com.shmet.utils.JwtUtil;
import com.shmet.vo.UserVo;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @author
 */
@RestController
public class LoginWithTokenController {

  public static final Logger logger = LoggerFactory.getLogger(LoginWithTokenController.class);

  //PCS DeviceModel
  public static final int PCS_DEVICEMODEL = 27;

  //adPCS DeviceModel岸电逆变器
  public static final int AD_PCS_DEVICEMODEL = 52;
  //BMS DeviceModel
  public static final int BMS_HEAP_DEVICEMODEL = 28;
  //AIR DeviceModel
  public static final int AIR_DEVICEMODEL = 46;
  //光伏逆变器 DeviceModel
  public static final int SUNPCS_DEVICEMODEL = 44;
  //储能电表 市电电表 deviceModel
  public static final int DIANBIAO_DEVICEMODEL = 1;

  public  static final int TCTR_DEVICEMODEL=48;

  @Autowired
  BusinessDataService businessDataService;

  @Autowired
  private ThreadPoolExecutor executor;

  @Autowired
  private LoginService loginService;

  @Resource
  private UserMapper userMapper;

  @Resource
  private SubProjectMapper subProjectMapper;

  @Resource
  private ProjectMapper projectMapper;

  @Resource
  private AliyunSmsService aliyunSmsService;

  @Autowired
  ProjectConfigDao projectConfigDao;

  @OperationLogAnnotation(logType = 1, opContent = "登录系统")
  @PostMapping("/login")
  public Result login(@RequestBody UserVo vo, HttpServletRequest request) {
    PostResponseExBean rst = new PostResponseExBean();
    List<ResponseDataEx> datas = new ArrayList<>();
    User user = businessDataService.findByAccount(StringUtils.isEmpty(vo.getAccount())
        ? "" : vo.getAccount());
    if (user == null) {
      return Result.getErrorResultInfo("登录失败,用户不存在");
    } else {
      if (!user.getPassword().equals(vo.getPassword())) {
        return Result.getErrorResultInfo("登录失败,密码错误");
      }

      String customerNo = user.getProjectNo();
      Customer customer = loginService.getCustomerByProjectNo(customerNo);

      Result result = Result.getSuccessResultInfo();
      request.getSession().setAttribute(Consts.SESSION_USERINFO, user);
      String token = JwtUtil.generateToken(user);
      rst.setToken(token);


      if (user.getProjectNo().equals("jz")) {//表示内部人员,选择进入系统
        rst.setSuccessed(true);
        rst.setProjectAdmin(true);
        result.setCode("2");
        result.setData(rst);
        return result;
      }
      ResponseDataEx data = new ResponseDataEx();

      // 项目号不为空时获取额外信息
      if (!StringUtils.isBlank(customerNo)) {

        data.setCustomerNo(customerNo);
        if (customer != null) {
          data.setCustomerId(customer.getCustomerId());
          data.setCustomerlogo(customer.getLogo());
          data.setCustomName(customer.getCustomerName());
          data.setCity(customer.getCity());
          Project project=  projectMapper.selectOne(new QueryWrapper<Project>().eq("customer_id", customer.getCustomerId()));
          if(user.getAccount().equals(customer.getAdminAccount())){//是否项目管理员
            rst.setProjectAdmin(true);
          }
          if(project.getSysType().equals("0")){
            result.setCode("1");
          }else {
            result.setCode("3");
          }
        }
        List<Long> subProjectIds = CompletableFuture.supplyAsync(() -> loginService.getSubProjectIds(customerNo)).join();
        exMethod(datas, data, subProjectIds);
      }
      rst.setDatas(datas);
      rst.setSuccessed(true);
      result.setData(rst);

      return result;
    }

  }

  @PostMapping("/loginbysms")
  public Result loginBySms(@RequestBody LoginSmsReq req, HttpServletRequest request) {
    PostResponseExBean rst = new PostResponseExBean();
    List<ResponseDataEx> datas = new ArrayList<>();
    String phone = req.getPhone();
    String code = req.getCode();
    User user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getAccount, phone));
    if (user == null) {
      return Result.getErrorResultInfo("登录失败,手机号[ " + phone + " ]没有注册");
    } else {
      if (!aliyunSmsService.validateSmsCode(phone, code)) {
        return Result.getErrorResultInfo("登录失败,验证码不匹配");
      }
      String customerNo = user.getProjectNo();
      Customer customer = loginService.getCustomerByProjectNo(customerNo);


      Result result = Result.getSuccessResultInfo();
      request.getSession().setAttribute(Consts.SESSION_USERINFO, user);
      String token = JwtUtil.generateToken(user);
      rst.setToken(token);

      if (user.getProjectNo().equals("jz")) {//表示内部人员,选择进入系统
        List<Customer> customerList = loginService.getCustomerByVersion("EMS2.0");
        rst.setDatas(customerList);
        rst.setSuccessed(true);
        rst.setProjectAdmin(true);
        result.setCode("2");
        result.setData(rst);
        return result;
      }
      ResponseDataEx data = new ResponseDataEx();

      // 项目号不为空时获取额外信息
      if (!StringUtils.isBlank(customerNo)) {

        data.setCustomerNo(customerNo);
        if (customer != null) {
          data.setCustomerId(customer.getCustomerId());
          data.setCustomerlogo(customer.getLogo());
          data.setCustomName(customer.getCustomerName());
          data.setCity(customer.getCity());
          Project project=  projectMapper.selectOne(new QueryWrapper<Project>().eq("customer_id", customer.getCustomerId()));
          if(user.getAccount().equals(customer.getAdminAccount())){//是否项目管理员
            rst.setProjectAdmin(true);
          }
          if(project.getSysType().equals("0")){
            result.setCode("1");
          }else {
            result.setCode("3");
          }
        }
        List<Long> subProjectIds = CompletableFuture.supplyAsync(() -> loginService.getSubProjectIds(customerNo)).join();
        exMethod(datas, data, subProjectIds);
      }
      rst.setDatas(datas);
      rst.setSuccessed(true);
      result.setData(rst);
      return result;
    }

  }

  @PostMapping("/loginSbuProjectInfo")
  public Result login(@RequestBody Customer vo) {
    if (vo == null || vo.getCustomerNo() == null || vo.getCustomerNo().trim().length() == 0) {
      return Result.getErrorResultInfo("参数customerNo不能为空！");

    }
    List<ResponseDataEx> datas = new ArrayList<>();
    ResponseDataEx data = new ResponseDataEx();
    String customerNo = vo.getCustomerNo();
    Customer customer = loginService.getCustomerByProjectNo(customerNo);
    data.setCustomerNo(customerNo);
    if (customer != null) {
      data.setCustomerId(customer.getCustomerId());
      data.setCustomerlogo(customer.getLogo());
      data.setCity(customer.getCity());
      data.setCustomName(customer.getCustomerName());
      Project project= projectMapper.selectOne(new QueryWrapper<Project>().eq("customer_id", customer.getCustomerId()));
      if(project!=null){
        data.setProjectName(project.getProjectName());
      }
    }

    List<Long> subProjectIds = CompletableFuture.supplyAsync(() -> loginService.getSubProjectIds(customerNo)).join();
    exMethod(datas, data, subProjectIds);
    return Result.getSuccessResultInfo(datas);
  }

  @GetMapping("/v2/logout")
  public void logout(HttpServletRequest request, HttpServletResponse response) throws ServletException {
    request.logout();
    request.getSession().invalidate();
    try {
      response.sendRedirect(request.getContextPath() + "/login");
    } catch (IOException e) {
      System.out.println("重定向出错 : " + e);
    }
  }

  private void exMethod(List<ResponseDataEx> datas, ResponseDataEx data, List<Long> subProjectIds) {
    List<ConfigEx> configExes = new ArrayList<>();
    subProjectIds.forEach(i -> {
      ConfigEx configEx = new ConfigEx();
      configEx.setSubprojectId(i);
      Integer showType=0;
      ProjectConfig projectConfig= projectConfigDao.findElectricProjectConfigBySubProjectId(i);
      if(projectConfig!=null&&projectConfig.getConfig().containsKey("showType")){
        showType=Integer.parseInt(projectConfig.getConfig().get("showType").toString());
      }
      configEx.setShowType(showType);
      List<Device> storageElecDevices = loginService.getDevicesBySubProjectIdAndDeviceModelType(i, DIANBIAO_DEVICEMODEL, 1);
      List<Device> cityElecDevices = loginService.getDevicesBySubProjectIdAndDeviceModelType(i, DIANBIAO_DEVICEMODEL, 0);
      List<Device> pcsDevices = CompletableFuture.supplyAsync(() -> loginService.getDevicesBySubProjectId(i, PCS_DEVICEMODEL), executor).join();
      List<Device> adPcsDevices = CompletableFuture.supplyAsync(() -> loginService.getDevicesBySubProjectId(i, AD_PCS_DEVICEMODEL), executor).join();

      List<Device> bmsDevices = CompletableFuture.supplyAsync(() -> loginService.getDevicesBySubProjectId(i, BMS_HEAP_DEVICEMODEL), executor).join();
      List<Device> airDevices = CompletableFuture.supplyAsync(() -> loginService.getDevicesBySubProjectId(i, AIR_DEVICEMODEL), executor).join();
      List<Device> tCtrDevices = CompletableFuture.supplyAsync(() -> loginService.getDevicesBySubProjectId(i, TCTR_DEVICEMODEL), executor).join();
      List<Device> sunPcsDevices = CompletableFuture.supplyAsync(() -> loginService.getDevicesBySubProjectId(i, SUNPCS_DEVICEMODEL), executor).join();
      List<Device> cityElecNameDevices = loginService.getDevicesBySubProjectIdAndDeviceModelType(i, DIANBIAO_DEVICEMODEL, 0);
      List<Long> storageElecModels = storageElecDevices.stream().map(Device::getDeviceNo).collect(Collectors.toList());
      List<Long> cityElecModels = cityElecDevices.stream().map(Device::getDeviceNo).collect(Collectors.toList());
      List<ConfigEx.BaseModel> pcsModels = pcsDevices.stream().map(o -> ConfigEx.build(o.getDeviceNo(), o.getDeviceName())).collect(Collectors.toList());
      List<ConfigEx.BaseModel> adPcsModels = adPcsDevices.stream().map(o -> ConfigEx.build(o.getDeviceNo(), o.getDeviceName())).collect(Collectors.toList());
      List<ConfigEx.BaseModel> tCtrModels = tCtrDevices.stream().map(o -> ConfigEx.build(o.getDeviceNo(), o.getDeviceName())).collect(Collectors.toList());
      List<ConfigEx.BaseModel> bmsModels = bmsDevices.stream().map(o -> ConfigEx.build(o.getDeviceNo(), o.getDeviceName())).collect(Collectors.toList());
      List<ConfigEx.BaseModel> airModels = airDevices.stream().map(o -> ConfigEx.build(o.getDeviceNo(), o.getDeviceName())).collect(Collectors.toList());
      List<ConfigEx.BaseModel> sunPcsModels = sunPcsDevices.stream().map(o -> ConfigEx.build(o.getDeviceNo(), o.getDeviceName())).collect(Collectors.toList());
      List<ConfigEx.BaseModel> cityElecNameModels = cityElecNameDevices.stream().map(o -> ConfigEx.build(o.getDeviceNo(), o.getDeviceName())).collect(Collectors.toList());

      configEx.setStorageElec(storageElecModels.isEmpty() ? null : storageElecModels.get(0));
      configEx.setCityElec(cityElecModels.isEmpty() ? new ArrayList<>() : cityElecModels);
      configEx.setPcsDeviceNos(pcsModels.isEmpty() ? new ArrayList<>() : pcsModels);
      configEx.setTctrDevices(tCtrModels.isEmpty() ? new ArrayList<>() : tCtrModels);
      configEx.setBmsDeviceNos(bmsModels.isEmpty() ? new ArrayList<>() : bmsModels);
      configEx.setAirDeviceNos(airModels.isEmpty() ? new ArrayList<>() : airModels);
      configEx.setSunPcsDeviceNos(sunPcsModels.isEmpty() ? new ArrayList<>() : sunPcsModels);
      configEx.setCityElecName(cityElecNameDevices.isEmpty()?new ArrayList<>():cityElecNameModels);
      configEx.setAdPcsDeviceNos(adPcsModels.isEmpty() ? new ArrayList<>() : adPcsModels);
      configExes.add(configEx);
    });
    data.setConfigs(configExes);

    datas.add(data);
  }

  @Getter
  @Setter
  static class LoginSmsReq {
    @NotBlank(message = "手机号不能为空")
    private String phone;
    @NotBlank(message = "验证码不能为空")
    private String code;
  }
}
