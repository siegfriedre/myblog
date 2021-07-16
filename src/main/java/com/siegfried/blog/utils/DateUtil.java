package com.siegfried.blog.utils;

import com.google.common.base.Strings;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by zy_zhu on 2021/5/7.
 */
public class DateUtil {
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYY_MM_DD_HH = "yyyy-MM-dd HH";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    //我来写

    /**
     *
     * @param date 传入一个日期
     * @param day 移动的天数
     * @return 移动后的日期
     */
    public static String getStartTime(Date date,int day){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE,day);
        return date2Str(calendar.getTime(), YYYY_MM_DD_HH_MM_SS);
    }

    /**
     *
     * @param date 传入一个日期
     * @return 该日期的String,默认pattern
     */
    public static String getNowTime(Date date ){
        return date2Str(date, YYYY_MM_DD_HH_MM_SS);
    }



    public static Date getSomeDay(Date date,int day){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE,day);
        return calendar.getTime();
    }

    public static Date strToDate(String strDate){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return formatter.parse(strDate);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static String date2Str(Date date,String pattern){
        return !Objects.isNull(date) && !Strings.isNullOrEmpty(pattern) ?
                Optional.of((new SimpleDateFormat(pattern)).format(date)).get() : (String) Optional.empty().get();
    }

    /**
     * 获取日期所在的星期
     * @param date 日期
     * @return 星期
     */
    public static int getDayOfWeek(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK) - 1;
    }

    /**
     * 获取日期所在年份
     *
     * @param date 日期
     * @return 年份
     */
    public static int getDayOfYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    public static String getMinTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return getMinTime(calendar);
    }

    /**
     * 设置最小时间: 零点
     *
     * @param calendar
     */
    private static String getMinTime(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return date2Str(calendar.getTime(), YYYY_MM_DD_HH_MM_SS);
    }

    public static String getMaxTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return getMaxTime(calendar);
    }

    /**
     * 设置最大时间
     *
     * @param calendar
     */
    private static String getMaxTime(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getActualMaximum(Calendar.MILLISECOND));
        return date2Str(calendar.getTime(), YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 获取多少小时前
     * @param hour 小时
     * @return 时间
     */
    public static String getBeforeHourTime(int hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - hour);
        return date2Str(calendar.getTime(), YYYY_MM_DD_HH_MM_SS);
    }
}
