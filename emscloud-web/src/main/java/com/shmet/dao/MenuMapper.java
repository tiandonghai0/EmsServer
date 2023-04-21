package com.shmet.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shmet.entity.mysql.gen.Menu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author
 */
public interface MenuMapper extends BaseMapper<Menu> {
  /**
   * 根据客户编号查询所有的 菜单列表
   *
   * @param account 客户编号
   * @return 菜单列表
   */
  List<Menu> findMenusByAccount(@Param("account") String account);
}
