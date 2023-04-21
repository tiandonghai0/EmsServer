package com.shmet.handler.v2;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import com.shmet.bean.PostResponseExBean;
import com.shmet.bean.v2.ConfigEx;
import com.shmet.bean.v2.ResponseDataEx;
import com.shmet.dao.AppUserMapper;
import com.shmet.dao.UserMapper;
import com.shmet.dao.UserRoleRelMapper;
import com.shmet.entity.dto.Result;
import com.shmet.entity.mysql.gen.*;
import com.shmet.exception.UserAlreadyExistException;
import com.shmet.utils.JwtUtil;
import com.shmet.vo.req.AppUserLoginReq;
import com.shmet.vo.req.AppUserRegisterReq;
import com.shmet.vo.req.BindAccountReq;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @author
 */
@Service
public class AppUserService {

  @Resource
  private AppUserMapper appUserMapper;

  @Autowired
  private StringRedisTemplate stringRedisTemplate;

  @Resource
  private UserMapper userMapper;

  @Resource
  private UserRoleRelMapper userRoleRelMapper;

  @Autowired
  private LoginService loginService;

  @Autowired
  private ThreadPoolExecutor executor;

  //登录
  public Result appUserLogin(AppUserLoginReq req) {
    PostResponseExBean rst = new PostResponseExBean();
    String password = DigestUtils.md5Hex(req.getPassword());
    AppUser appUser = appUserMapper.queryAppUserExists(req.getPhone(), password);
    if (appUser != null) {
      String token = JwtUtil.generateAppUserToken(appUser);
      if (appUser.getAccount() != null) {
        User user = userMapper.selectOne(
            new QueryWrapper<User>().eq("account", appUser.getAccount())
        );
        List<ResponseDataEx> datas = new ArrayList<>();
        Result result = Result.getSuccessResultInfo();
        if (user.getProjectNo().equals("jz")) {
          List<Customer> customerList = loginService.getCustomerByVersion("EMS2.0");
          rst.setDatas(customerList);
          rst.setSuccessed(true);
          rst.setToken(token);
          result.setCode("2");
          result.setData(rst);
          return result;
        }
        ResponseDataEx data = new ResponseDataEx();
        String customerNo = user.getProjectNo();
        // 项目号不为空时获取额外信息
        if (!StringUtils.isBlank(customerNo)) {
          Customer customer = loginService.getCustomerByProjectNo(customerNo);
          data.setCustomerNo(customerNo);
          if (customer != null) {
            data.setCustomerId(customer.getCustomerId());
            data.setCustomerlogo(customer.getLogo());
            data.setCity(customer.getCity());
          }
          List<Long> subProjectIds = CompletableFuture.supplyAsync(() -> loginService.getSubProjectIds(customerNo)).join();
          exMethod(datas, data, subProjectIds);
        }
        rst.setToken(token);
        rst.setDatas(datas);
        rst.setSuccessed(null);
        result.setData(rst);
        return result;
      } else {
        HashMap<String, Object> map = Maps.newHashMap();
        map.put("appUser", appUser);
        map.put("token", token);
        return Result.getSuccessResultInfo(map, "没有关联用户");
      }
    } else {
      return Result.getErrorResultInfo("账户不存在");
    }
  }

  private void exMethod(List<ResponseDataEx> datas, ResponseDataEx data, List<Long> subProjectIds) {
    List<ConfigEx> configExes = new ArrayList<>();
    subProjectIds.forEach(i -> {
      ConfigEx configEx = new ConfigEx();
      configEx.setSubprojectId(i);
      List<Device> pcsDevices = CompletableFuture.supplyAsync(() -> loginService.getDevicesBySubProjectId(i, 27), executor).join();
      List<Device> bmsDevices = CompletableFuture.supplyAsync(() -> loginService.getDevicesBySubProjectId(i, 28), executor).join();
      List<Device> airDevices = CompletableFuture.supplyAsync(() -> loginService.getDevicesBySubProjectId(i, 25), executor).join();
      List<ConfigEx.BaseModel> pcsModels = pcsDevices.stream().map(o -> ConfigEx.build(o.getDeviceNo(), o.getDeviceName())).collect(Collectors.toList());
      List<ConfigEx.BaseModel> bmsModels = bmsDevices.stream().map(o -> ConfigEx.build(o.getDeviceNo(), o.getDeviceName())).collect(Collectors.toList());
      List<ConfigEx.BaseModel> airModels = airDevices.stream().map(o -> ConfigEx.build(o.getDeviceNo(), o.getDeviceName())).collect(Collectors.toList());
      configEx.setPcsDeviceNos(pcsModels);
      configEx.setBmsDeviceNos(bmsModels);
      configEx.setAirDeviceNos(airModels);

      configExes.add(configEx);
    });
    data.setConfigs(configExes);

    datas.add(data);
  }

  //注册
  public int registerAppUser(AppUserRegisterReq req) {
    //req null
    if (req == null) {
      return -1;
    }
    String phone = req.getPhone();
    AppUser apUser = appUserMapper.selectOne(
        new LambdaQueryWrapper<AppUser>().eq(AppUser::getPhone, phone)
    );
    if (apUser != null) {
      return -4;
    }
    String redisCode = stringRedisTemplate.opsForValue().get(phone);
    //redis code not exist
    if (StringUtils.isBlank(redisCode)) {
      return -2;
    }
    //phone code not match
    if (!StringUtils.equals(redisCode, req.getPhoneCode())) {
      return -3;
    }
    AppUser appUser = req.buildAppUser();
    int insert = appUserMapper.insert(appUser);
    if (insert == 1) {
      //注册成功 移除 redis code
      stringRedisTemplate.delete(phone);
      return 1;
    } else {
      return -99;
    }
  }


  public boolean bindAccount(BindAccountReq req) {
    User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getAccount, req.getAccount()));
    if (user == null) {
      throw new UserAlreadyExistException("要绑定的账户不存在");
    }
    if (!StringUtils.equals(req.getPassword(), user.getPassword())) {
      throw new UserAlreadyExistException("输入密码与绑定账户密码不匹配");
    }
    AppUser appUser = appUserMapper.selectById(req.getId());
    if (appUser != null) {
      appUser.setAccount(req.getAccount());
      appUserMapper.updateById(appUser);
      return true;
    }
    return false;
  }

  //注销账号
  public int unUser(int type,String account){
    if(type==1){
      userRoleRelMapper.delete(new LambdaQueryWrapper<UserRoleRel>().eq(UserRoleRel::getUserId,account));
     return userMapper.delete(new LambdaQueryWrapper<User>().eq(User::getAccount,account));

    }else  if (type==2){
      return  appUserMapper.delete(new LambdaQueryWrapper<AppUser>().eq(AppUser::getPhone,account));
    }
    return  0;
  }
}
