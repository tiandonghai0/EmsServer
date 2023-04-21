package com.shmet.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shmet.entity.mysql.gen.ChargeListData;
import com.shmet.vo.ChargeListDataVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author
 */
public interface ChargeListDataMapper extends BaseMapper<ChargeListData> {
  /**
   * 根据projectNo 和 callDate 查询 ChargeListData 列表
   *
   * @param projectNo 项目编号
   * @param callDate  日期
   * @return List<ChargeListDataVo>
   */
  List<ChargeListDataVo> listByProjectNoAndCallDate(@Param("projectNo") String projectNo,
                                                    @Param("callDate") String callDate);
}
