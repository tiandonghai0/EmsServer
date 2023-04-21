package com.shmet.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shmet.entity.mysql.gen.AppUser;
import org.apache.ibatis.annotations.Param;

/**
 * @author
 */
public interface AppUserMapper extends BaseMapper<AppUser> {
  AppUser queryAppUserExists(@Param("phone") String phone, @Param("password") String password);
}
