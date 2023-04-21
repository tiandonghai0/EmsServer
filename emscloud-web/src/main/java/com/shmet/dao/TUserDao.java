package com.shmet.dao;

import java.util.List;

import org.beetl.sql.core.mapper.BaseMapper;
import com.shmet.entity.mysql.gen.TUser;

public interface TUserDao extends BaseMapper<TUser> {
    List<TUser> select(TUser user);
}
