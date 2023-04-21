package com.shmet;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateTimeUtils {
    static private final Logger logger = LoggerFactory.getLogger(DateTimeUtils.class);
    public static final String FORMAT_YYYYMMDD = "yyyyMMdd";
    public static final String FORMAT_YYYYMMDDHH = "yyyyMMddHH";
    public static final String FORMAT_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static final String FORMAT_yyyy_MM_dd = "yyyy-MM-dd";
    public static final String FORMAT_yyyy_MM = "yyyy-MM";
    public static final String FORMAT_yyyy = "yyyy";
    public static final String FORMAT_yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";

    private static final DateTimeFormatter formatter=DateTimeFormatter.ofPattern(FORMAT_yyyy_MM_dd_HH_mm_ss);
    private static final DateTimeFormatter formatter2=DateTimeFormatter.ofPattern(FORMAT_YYYYMMDDHHMMSS);

    public static String getCurrentTime(){
      return formatter.format(LocalDateTime.now());
    }

    /**
     * FORMAT_YYYYMMDDHHMMSS
     * @return
     */
    public static Long getCurrentLongTime(){
        return Long.parseLong(formatter2.format(LocalDateTime.now()));
    }

    public static Long getTimeLongYYYYMMDD(){
        Calendar calendar = Calendar.getInstance();
        return Long.parseLong(new SimpleDateFormat(FORMAT_YYYYMMDD).format(calendar.getTime()));
        //return Long.parseLong(DateTimeFormatter.ofPattern(FORMAT_YYYYMMDD).format(LocalDateTime.now()));
    }

    static public Date parseDate(String dateString, String formatStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
        Date date = null;
        try {
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            logger.error(Consts.MSG_CODE_E000002, e);
        }
        return date;
    }

    static public String date2String(Date date, String formatStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
        String dateString = null;
        dateString = sdf.format(date);
        return dateString;
    }

    static public Date dateCalc(Date date, int type, int subtrahend) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        gc.add(type, subtrahend);
        return gc.getTime();
    }

    // 计算两个时间差值（秒数）
    public static long differFromSecond(Date from, Date to) {
        long minutes = (to.getTime() - from.getTime()) / 1000;
        return minutes;
    }

    // 计算两个时间差值（秒数）
    public static long differFromSecond(String from, String to, String format) {
        return differFromSecond(parseDate(from, format), parseDate(to, format));
    }

    static public String dateCalc(String dateString, int type, int subtrahend, String formatStr) {
        Date date = dateCalc(parseDate(dateString, formatStr), type, subtrahend);
        return date2String(date, formatStr);
    }

    static public String getLastDayOfMonth(String dateString, String formatStr) {
        Date date = parseDate(dateString, formatStr);
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        gc.add(Calendar.MONTH, 1);// 加一个月
        gc.set(Calendar.DATE, 1);// 把日期设置为当月第一天
        gc.roll(Calendar.DATE, -1);// 日期回滚一天，也就是本月最后一天
        return date2String(date, formatStr);
    }

    static public String dateLong2FString (Long timestamp, String format) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(timestamp);
      return new SimpleDateFormat(format).format(calendar.getTime());
    }

    // long缩写时间转为字符带格式
    static public String dataTimeLongToString(Long timestamp){
        String time=timestamp.toString();
        return time.substring(0,4)+"-"+time.substring(4,6)+"-"+time.substring(6,8)+" "+time.substring(8,10)+":"+time.substring(10,12)+":"+time.substring(12);
    }
    // 带格式时间转long
    static public Long dataTimeToLong(Date date){
        String time=date2String(date,FORMAT_YYYYMMDDHHMMSS);
        return Long.parseLong(time.replace(" ","").replace("-","").replace(":","").replace("/",""));
    }
    //给当前时间加小时
     static public Date getDateAddHour(Date date,int hour){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // 24小时制
        cal.add(Calendar.HOUR, hour);
        return cal.getTime();
    }

    public static Pair<Long, Long> getCurrentDateHourPair(String date) {
        if (StrUtil.isBlank(date)) {
            Long startTime = Long.parseLong(DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now()) + "000000");
            Long endime = Long.parseLong(DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now()));
            return Pair.of(startTime, endime);
        } else {
            String newDateStr = StrUtil.replace(date, "-", "");
            Long startTime = Long.parseLong(newDateStr + "00");
            Long endime = Long.parseLong(newDateStr + "24");
            return Pair.of(startTime, endime);
        }
    }

    static public void main(String args[]) {
        System.out.println(dateCalc("2018083100", GregorianCalendar.MONTH, -1, FORMAT_YYYYMMDDHH));
    }
}
