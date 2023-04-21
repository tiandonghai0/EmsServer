package com.shmet.util;

import java.text.DecimalFormat;

/**
 * @author
 */
public final class NumberUtil {

  //保留小数点后两位
  public static double convert2(Double d) {
    DecimalFormat format = new DecimalFormat("#.00");
    return Double.parseDouble(format.format(d));
  }

  /**
   * 判断字符串是否是整数
   */
  public static boolean isInteger(String value) {
    try {
      Integer.parseInt(value);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  public static void main(String[] args) {
    System.out.println(convert2(99.76014676201424));
  }
}
