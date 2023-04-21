package com.shmet.entity.mysql.gen;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author
 */
@TableName("t_menu")
public class Menu {
  private String id;
  private String pid;
  @TableField(value = "menu_code")
  private String menuCode;
  @TableField(value = "name")
  @JsonProperty(value = "name")
  private String menuName;
  @TableField(value = "router")
  @JsonProperty(value = "router")
  private String url;
  private String icon;
  @TableField(exist = false)
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<Menu> subMenus;
  @TableField(value = "has_sub_menu")
  private boolean hasSubMenu;
  @TableField(value = "order_by")
  private Integer orderBy;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getPid() {
    return pid;
  }

  public void setPid(String pid) {
    this.pid = pid;
  }

  public String getMenuCode() {
    return menuCode;
  }

  public void setMenuCode(String menuCode) {
    this.menuCode = menuCode;
  }

  public String getMenuName() {
    return menuName;
  }

  public void setMenuName(String menuName) {
    this.menuName = menuName;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public List<Menu> getSubMenus() {
    return subMenus;
  }

  public void setSubMenus(List<Menu> subMenus) {
    this.subMenus = subMenus;
  }

  public boolean isHasSubMenu() {
    return hasSubMenu;
  }

  public void setHasSubMenu(boolean hasSubMenu) {
    this.hasSubMenu = hasSubMenu;
  }

  public Integer getOrderBy() {
    return orderBy;
  }

  public void setOrderBy(Integer orderBy) {
    this.orderBy = orderBy;
  }
}
