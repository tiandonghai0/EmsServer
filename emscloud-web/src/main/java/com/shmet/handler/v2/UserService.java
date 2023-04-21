package com.shmet.handler.v2;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shmet.utils.EncryptUtil;
import com.shmet.dao.UserMapper;
import com.shmet.entity.mysql.gen.User;
import com.shmet.exception.UserAlreadyExistException;
import com.shmet.vo.UpdatePwdVo;
import com.shmet.vo.req.UserRegisterReq;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author
 */
@Service
public class UserService {

  @Resource
  private UserMapper userMapper;

  @Autowired
  private StringRedisTemplate stringRedisTemplate;

  private User getUserByProjectNoAndAccount(String projectNo, String account) {
    User user = userMapper.selectOne(new QueryWrapper<User>().eq("project_no", projectNo).eq("account", account));
    if (null == user) {
      throw new RuntimeException("根据项目编号和用户名查不到账号信息");
    }
    return user;
  }

  /**
   * 重大操作 使用二级密码进行验证
   *
   * @param projectNo 项目编号 : gk
   * @param account   用户名 t_user account字段
   * @param secondPwd 前端传入的 二级密码进行加密后 与数据库中的进行对比
   */
  public boolean validateSecondPwd(String projectNo, String account, String secondPwd) {
    User user = getUserByProjectNoAndAccount(projectNo, account);
    return StringUtils.equals(user.getSecondaryPassword(), EncryptUtil.md5(secondPwd, true));
  }

  public void modifyPwd(UpdatePwdVo vo) {
    User user = getUserByProjectNoAndAccount(vo.getProjectNo(), vo.getAccount());
    boolean b = StringUtils.equals(user.getSecondaryPassword(), EncryptUtil.md5(vo.getOldSecondPwd(), true));
    String newPwd = EncryptUtil.md5(vo.getNewSecondPwd(), true);
    if (b) {
      userMapper.updateSecondPwd(newPwd, vo.getProjectNo(), vo.getAccount());
    } else {
      throw new RuntimeException("输入的旧的二级密码不正确");
    }
  }

  public int userRegister(UserRegisterReq req) {
    User u = userMapper.selectOne(
        new QueryWrapper<User>().lambda()
            .eq(User::getAccount, req.getAccount())
    );
    String redisCode = stringRedisTemplate.opsForValue().get(req.getAccount());
    if (u != null) {
      throw new UserAlreadyExistException("账户已存在,请更换注册账户名");
    } else if (StringUtils.isBlank(redisCode)) {
      throw new UserAlreadyExistException("验证码已失效");
    } else if (!StringUtils.equals(redisCode, req.getPhoneCode())) {
      throw new UserAlreadyExistException("输入的手机验证码不匹配");
    } else {
      User user = req.buildUser();
      int insert = userMapper.insert(user);
      if (insert == 1) {
        //移除Redis 手机号 key
        stringRedisTemplate.delete(req.getAccount());
        return 1;
      } else {
        return -1;
      }
    }
  }
}
