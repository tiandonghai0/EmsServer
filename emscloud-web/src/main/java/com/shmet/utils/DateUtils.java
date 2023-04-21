package com.shmet.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

  /**
   * 得到当前日期字符串 格式（yyyy-MM-dd）
   */
  public static String getDate() {
    return getDate("yyyy-MM-dd");
  }


  /**
   * 得到当前日期字符串 格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
   */
  public static String getDate(String pattern) {
    return DateFormatUtils.format(new Date(), pattern);
  }

  public static Date rollNOfDate2(Date date, int dateType, int num) {
    Date date2 = DateUtils.rollNOfDate(date, dateType, num);
    Date date3 = new Date();
    switch (dateType) {
      case 1:
      case 3:
      case 5: {//小时
        date3 = DateUtils.rollNOfDate(date2, 6, -1);
        break;
      }
    }
    return date3;
  }

  /**
   * 得到日期字符串 默认格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
   */
  public static String formatDate(Date date, Object... pattern) {
    return DateFormatUtils.format(date, pattern[0].toString());
  }

  public static int DateToNum_Dim(Date date, int dateType) {
    int timeString;
    String dateStr = formatDate(date, "yyyyMMddHHmmss");
    if (dateType == 2) {
      //yyyyMMddHH
      timeString = Integer.parseInt(dateStr.substring(0, 10));
    } else if (dateType == 3) {
      timeString = Integer.parseInt(dateStr.substring(0, 8));
    } else {
      timeString = Integer.parseInt(dateStr.substring(0, 6));
    }

    return timeString;
  }

  /**
   * 根据时间字符串和字符串格式,转换成java.util.Date对象
   *
   * @param dateString 时间字符串
   * @param format     格式
   * @return Date
   */
  public static Date formatStringToDate(String dateString, String format) {
    format = (format != null ? format : "yyyy-MM-dd HH:mm:ss");
    if (dateString == null || dateString.length() == 0) return null;
    SimpleDateFormat dateFormat = new SimpleDateFormat(format);
    Date date = new Date();
    try {
      date = dateFormat.parse(dateString);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return date;
  }

  /**
   * 得到当前时间字符串 格式（HH:mm:ss）
   */
  public static String getTime() {
    return formatDate(new Date(), "HH:mm:ss");
  }

  /**
   * 得到当前年份字符串 格式（yyyy）
   */
  public static String getYear() {
    return formatDate(new Date(), "yyyy");
  }

  /**
   * 得到当前月份字符串 格式（MM）
   */
  public static String getMonth() {

    return formatDate(new Date(), "MM");
  }

  /**
   * 得到当天字符串 格式（dd）
   */
  public static String getDay() {
    return formatDate(new Date(), "dd");
  }

  /**
   * 得到当前星期字符串 格式（E）星期几
   */
  public static String getWeek() {
    return formatDate(new Date(), "E");
  }


  public static Date getDateStart(Date date) {
    if (date == null) {
      return null;
    }
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    try {
      date = sdf.parse(formatDate(date, "yyyy-MM-dd") + " 00:00:00");
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return date;
  }

  public static Date getDateEnd(Date date) {
    if (date == null) {
      return null;
    }
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    try {
      date = sdf.parse(formatDate(date, "yyyy-MM-dd") + " 23:59:59");
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return date;
  }

  //日期推多少，num：向前为负，向后为正
  public static Date rollNOfDate(Date date, int dateType, int num) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    switch (dateType) {
      case 1: {//天
        cal.add(Calendar.DAY_OF_MONTH, num);
        break;
      }
      case 2: {//周
        cal.add(Calendar.DAY_OF_MONTH, 7 * num);
        break;
      }
      case 3: {//月
        cal.add(Calendar.MONTH, num);
        break;
      }
      case 4: {//季度
        cal.add(Calendar.MONTH, 3 * num);
        break;
      }
      case 5: {//年
        cal.add(Calendar.YEAR, num);
        break;
      }
      case 6: {//小时
        cal.add(Calendar.HOUR, num);
        break;
      }
    }
    return cal.getTime();
  }

  /**
   * 获取每天的 00 点 和 23点 数据
   *
   * @return Pair<Long, Long>
   */
  public static Pair<Long, Long> get00And23() {
    LocalDate now = LocalDate.now();
    Long startdate = Long.valueOf(now.toString().replace("-", "") + "00");
    Long enddate = Long.valueOf(now.toString().replace("-", "") + "23");
    return Pair.of(startdate, enddate);
  }

  /**
   * 根据传入的时间date 转换成特定时间格式
   *
   * @param date   年/月/日
   * @param dbTime hour
   * @return String 2020/09/18 08:00:00
   */
  public static String dateConvert(String date, Long dbTime) {
    if (dbTime != null) {
      String str = String.valueOf(dbTime);
      str = str.substring(str.length() - 6);
      String s = str.substring(0, 2)
          + ":" + str.substring(2, 4)
          + ":" + str.substring(4, 6);
      return date.replace("-", "/") + " " + s;
    } else {
      return "";
    }
  }

  /**
   * 将表示long型的时间数字转换为指定格式的时间字符串
   *
   * @param dbTime dbTime
   * @return eg 202011162136 ==> 2020/11/16 16:21:36
   */
  public static String longConvertDateStr(Long dbTime) {
    if (dbTime != null) {
      String str = String.valueOf(dbTime);
      return str.substring(0, 4) + "/" + str.substring(4, 6) + "/" + str.substring(6, 8) + " "
          + str.substring(8, 10) + ":" + str.substring(10, 12) + ":" + str.substring(12);
    } else {
      return "";
    }
  }

  public static String strConvertDateStr(String time, String splitFlag1, String splitFlag2) {
    if (StringUtils.isNotBlank(time)) {
      return time.substring(0, 4) + splitFlag1 + time.substring(4, 6) + splitFlag1 + time.substring(6, 8) + " " +
          time.substring(8, 10) + splitFlag2 + time.substring(10, 12) + splitFlag2 + time.substring(12);
    } else {
      return "";
    }
  }

  public static Pair<Long, Long> convertDateToPair(String date) {
    //如果没有传入日期就默认为当天日期
    if (StringUtils.isBlank(date)) {
      String now = LocalDate.now().toString();
      String newStr = StringUtils.replace(now, "-", "");
      return Pair.of(Long.valueOf(newStr + "00"), Long.valueOf(newStr + "24"));
    } else {
      String newStr = StringUtils.replace(date, "-", "");
      return Pair.of(Long.valueOf(newStr + "00"), Long.valueOf(newStr + "24"));
    }
  }

  //获取当前时间字符串
  public static String getCurrentTimeStr(LocalDateTime time) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    return formatter.format(time);
  }

  public static String getCurrentTimeStr(LocalDateTime time, DateTimeFormatter formatter) {
    return formatter.format(time);
  }

  public static Pair<Long, Long> str2Long(String timeStr) {
    if (StringUtils.isNotBlank(timeStr)) {
      Long left = Long.valueOf(StringUtils.replace(timeStr, "-", "") + "000000");
      Long right = Long.valueOf(StringUtils.replace(timeStr, "-", "") + "240000");
      return Pair.of(left, right);
    }
    return Pair.of(-1L, -1L);
  }

  //比较格式为 yyyy-MM-dd HH:mm:ss 时间字符串
  public static int compareTime(String startTime, String endTime) {
    if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
      LocalDateTime start = LocalDateTime.parse(startTime, formatter);
      LocalDateTime end = LocalDateTime.parse(endTime, formatter);
      return start.compareTo(end);
    }
    return -99;
  }

  //计算日期差
  public static long subDay(LocalDate start, LocalDate end) {
    if (start == null) {
      start = LocalDate.now();
    }
    return start.until(end, ChronoUnit.DAYS);
  }

  public static String formatterStrTime(String time) {
    return time.substring(0, 4) + "-" + time.substring(4, 6) + "-" + time.substring(6, 8)
        + " " + time.substring(8, 10) + ":" + time.substring(10, 12) + ":" + time.substring(12);
  }
}
