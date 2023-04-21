package com.shmet.dao.mysql.mapper;

import java.util.List;
import org.beetl.sql.core.mapper.BaseMapper;
import com.shmet.entity.mysql.gen.TDevice;

public interface TDeviceMapperDao extends BaseMapper<TDevice> {
	List<TDevice> selectByCondition(TDevice device);
}
