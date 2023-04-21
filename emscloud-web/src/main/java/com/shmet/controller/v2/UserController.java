package com.shmet.controller.v2;

import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;
import com.auth0.jwt.JWT;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.shmet.aop.UserLoginToken;
import com.shmet.dao.UserMapper;
import com.shmet.entity.dto.Result;
import com.shmet.entity.dto.ResultCode;
import com.shmet.entity.mysql.gen.User;
import com.shmet.exception.UserNotExistException;
import com.shmet.handler.v2.KaptchaService;
import com.shmet.handler.v2.UserService;
import com.shmet.thirdpart.AliyunSmsService;
import com.shmet.vo.req.UserRegisterReq;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.RequestContext;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author
 */
@RestController
@RequestMapping("/v2/user")
public class UserController {

  @Resource
  private UserMapper userMapper;

  @Autowired
  public UserService userService;

  @Autowired
  private KaptchaService kaptchaService;

  @Autowired
  public StringRedisTemplate stringRedisTemplate;

  @Autowired
  private AliyunSmsService aliyunSmsService;

  @PostMapping("/register")
  public Result userRegister(@Validated @RequestBody UserRegisterReq req) {
    int i = userService.userRegister(req);
    if (i == 1) {
      return Result.getSuccessResultInfo();
    }
    return Result.getErrorResultInfo("注册失败");
  }

  @GetMapping("/showRgButton")
  public boolean showRegisterButton() {
    return StringUtils.equals(
        stringRedisTemplate.opsForValue().get("ConfigRegisterButton"), "1"
    );
  }

  /**
   * 获取验证码
   */
  @GetMapping("/get/captcha")
  public Result getCaptcha(HttpServletResponse response) throws IOException {
    return new Result(ResultCode.SUCCESS, true, null, kaptchaService.getBase64ByteStr());
  }

  @PostMapping("/sendRgSms")
  public Result sendRegisterSms(@RequestBody String phone) {
    if (StringUtils.isBlank(phone)) {
      return Result.getErrorResultInfo("请输入手机号");
    }
    //这里简单手机号验证
    Pattern p = Pattern.compile("^1[0-9]{10}$");
    if (!p.matcher(phone).matches()) {
      return Result.getErrorResultInfo("手机号格式不正确,请输入正确的手机号");
    }
    try {
      SendSmsResponseBody responseBody = aliyunSmsService.sendRegisterSms(phone);
      if (responseBody != null && StringUtils.equals(responseBody.code, "OK")) {
        return Result.getSuccessResultInfo("短信验证码发送成功");
      } else if (responseBody != null) {
        return Result.getErrorResultInfo("短信验证码发送失败: " + responseBody.getMessage());
      } else {
        return Result.getErrorResultInfo("短信验证码发送失败,未知异常");
      }
    } catch (Exception e) {
      return Result.getErrorResultInfo(e.getMessage());
    }
  }

  @GetMapping("/validate/captcha")
  public Result validateImgCode(@RequestParam String code) {
    String redisCode = stringRedisTemplate.opsForValue().get("IMGCODE_" + code.toUpperCase());
    if (StringUtils.isBlank(code)) {
      return Result.getErrorResultInfo("图形验证码不存在啦,请刷新验证码图片");
    }
    if (!StringUtils.equalsIgnoreCase(code, redisCode)) {
      return Result.getErrorResultInfo("输入的图形验证码不匹配");
    }
    stringRedisTemplate.delete("IMGCODE_" + code.toUpperCase());
    return Result.getSuccessResultInfo(true);
  }

  //修改密码
  @PostMapping("/modifypwd")
  public Result modifyPwd(@Validated @RequestBody ModifyPwdDto dto) {
    LambdaQueryWrapper<User> wrapper = Wrappers.<User>lambdaQuery().eq(User::getAccount, dto.getAccount());
    User user = userMapper.selectOne(wrapper);
    if (!StringUtils.equals(dto.getConfirmPwd(), dto.getNewPwd())) {
      throw new UserNotExistException("新密码与确认密码不匹配");
    }
    if (user == null) {
      throw new UserNotExistException("用户不存在");
    }
    if (!StringUtils.equals(user.getPassword(), dto.getOldPwd())) {
      throw new UserNotExistException("输入原密码不匹配");
    }
    user.setPassword(dto.getNewPwd());
    int res = userMapper.update(user, wrapper);
    if (res == 1) {
      return Result.getSuccessResultInfo("修改密码成功");
    }
    return Result.getErrorResultInfo("修改密码失败");
  }

  //根据项目新增用户
  @PostMapping("/addUserByPro")
  @UserLoginToken
  public Result addUserByPro(@RequestBody User user){
    LambdaQueryWrapper<User> wrapper = Wrappers.<User>lambdaQuery().eq(User::getAccount,user.getAccount());
    List<User> list= userMapper.selectList(wrapper);
    if(list.size()>0){
      return Result.getErrorResultInfo("登录名已存在，请更换");
    }
    int i= userMapper.insert(user);
    if(i>0){
      return Result.getSuccessResultInfo("新增成功");
    }else{
      return Result.getErrorResultInfo("新增失败");
    }

  }

  //获取项目用户列表
  @PostMapping("/getUserListByPro")
  @UserLoginToken
  public Result getUserListByPro(@RequestBody User user){
    LambdaQueryWrapper<User> wrapper = Wrappers.<User>lambdaQuery().eq(User::getProjectNo,user.getProjectNo());
    List<User> list = userMapper.selectList(wrapper);
    return  Result.getSuccessResultInfo(list);

  }

  //删除项目用户
  @PostMapping("/deleteUserByPro")
  public Result deleteUserByPro(@RequestBody User user){
    if(user.getProjectNo().equals("jz")){
      return Result.getErrorResultInfo("内部用户无法被删除！");
    }else if(user.getProjectNo().equals(user.getAccount())){
      return Result.getErrorResultInfo("项目管理员用户无法被删除！");
    }
    LambdaQueryWrapper<User> wrapper = Wrappers.<User>lambdaQuery().eq(User::getAccount,user.getAccount()).eq(User::getProjectNo,user.getProjectNo());
    int i= userMapper.delete(wrapper);
    if(i>0){
      return Result.getSuccessResultInfo("删除成功");
    }else{
      return Result.getErrorResultInfo("删除失败");
    }

  }

  //重置密码项目用户
  @PostMapping("/modifypwdUserByPro")
  public Result modifypwdUserByPro(@RequestBody User user){
    if(user.getProjectNo().equals("jz")){
      return Result.getErrorResultInfo("内部用户无法被修改！");
    }else if(user.getProjectNo().equals(user.getAccount())){
      return Result.getErrorResultInfo("项目管理员用户无法被修改！");
    }
    LambdaQueryWrapper<User> wrapper = Wrappers.<User>lambdaQuery().eq(User::getAccount,user.getAccount()).eq(User::getProjectNo,user.getProjectNo());
    User userOld= userMapper.selectOne(wrapper);
    userOld.setPassword(user.getPassword());
    int i=userMapper.update(userOld,wrapper);
    if(i>0){
      return Result.getSuccessResultInfo("修改成功");
    }else{
      return Result.getErrorResultInfo("修改失败");
    }

  }


  @Getter
  @Setter
  static class ModifyPwdDto {
    @NotBlank(message = "账户不能为空")
    private String account;
    @NotBlank(message = "新密码不能为空")
    private String newPwd;
    @NotBlank(message = "旧密码不能为空")
    private String oldPwd;
    @NotBlank(message = "确认密码不能为空")
    private String confirmPwd;
  }
}
