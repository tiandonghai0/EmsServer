package com.shmet.dao.mysql.mapper;

import java.util.List;
import org.beetl.sql.core.mapper.BaseMapper;
import com.shmet.entity.mysql.gen.TAccone;

public interface TAcconeMapperDao extends BaseMapper<TAccone> {
	List<TAccone> selectByCondition(TAccone accone);
}
