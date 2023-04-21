package com.shmet.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shmet.entity.mysql.gen.AppVersion;
import org.apache.ibatis.annotations.Param;

/**
 * @author
 */
public interface AppVersionMapper extends BaseMapper<AppVersion> {
    AppVersion queryLatestApp(@Param("v") String v);
}
