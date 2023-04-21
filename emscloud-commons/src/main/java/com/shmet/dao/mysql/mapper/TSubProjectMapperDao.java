package com.shmet.dao.mysql.mapper;

import java.util.List;
import org.beetl.sql.core.mapper.BaseMapper;
import com.shmet.entity.mysql.gen.TSubProject;

public interface TSubProjectMapperDao extends BaseMapper<TSubProject> {
	List<TSubProject> selectByCondition(TSubProject subProject);
}
