package com.shmet.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shmet.entity.mysql.gen.User;
import org.apache.ibatis.annotations.Param;

/**
 * @author
 */
public interface UserMapper extends BaseMapper<User> {
  void updateSecondPwd(@Param("newPwd") String newPwd, @Param("projectNo") String projectNo, @Param("account") String account);

  String getOpPlatform(@Param("account")String account);
}
