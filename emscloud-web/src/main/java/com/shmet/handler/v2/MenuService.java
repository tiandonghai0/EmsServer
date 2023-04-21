package com.shmet.handler.v2;

import com.shmet.dao.MenuMapper;
import com.shmet.entity.mysql.gen.Menu;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author
 * 前端菜单的业务逻辑类
 */
@Service
public class MenuService {

  @Resource
  private MenuMapper menuMapper;

  /**
   * 根据项目编号返回对应的菜单树
   *
   * @param account 项目编号
   * @return List<Menu>
   */
  public List<Menu> listwithtree(String account) {
    List<Menu> menus = findMenusByProjectNo(account);
    //获取所有父级菜单
    return menus.stream()
        //过滤出全部的一级菜单
        .filter(Menu::isHasSubMenu)
        .sorted(Comparator.comparingInt(Menu::getOrderBy))
        //查询子级菜单
        .peek(m -> m.setSubMenus(getChildrens(m, menus)))
        .collect(Collectors.toList());
  }

  /**
   * 递归查询
   *
   * @param m     父级菜单
   * @param menus 子菜单列表
   * @return List<Menu></>
   */
  private List<Menu> getChildrens(Menu m, List<Menu> menus) {
    return menus.stream()
        .filter(o -> StringUtils.equalsIgnoreCase(m.getId(), o.getPid()))
        .peek(ch -> ch.setSubMenus(getChildrens(ch, menus)))
        .sorted(Comparator.comparingInt(Menu::getOrderBy))
        .collect(Collectors.toList());
  }

  public int addMenu(Menu menu) {
    return menuMapper.insert(menu);
  }

  public List<Menu> findMenusByProjectNo(String account) {
    return menuMapper.findMenusByAccount(account);
  }
}
