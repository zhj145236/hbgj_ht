package com.yusheng.hbgj.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author 金伟 on 2019/6/19 0019.
 * @date 2020/3/23 14:21
 * @desc 日期工具类
 */
public class DateUtil {

    private static final Logger log = LoggerFactory.getLogger("adminLogger");

    /***
     * 日期字符串转 Date类型
     * @param dateStr
     * @return
     */
    public static Date parseDate(String dateStr) {

        if (StringUtils.isEmpty(dateStr)) {
            return null;
        }

        dateStr = dateStr.trim();

        String[] parm = new String[]{"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd", "yyyy年MM月dd日 hh时mm分ss秒", "yyyy年MM月dd日"};

        SimpleDateFormat sdf;

        for (int i = 0; i < parm.length; i++) {


            sdf = new SimpleDateFormat(parm[i]);//解析日期错误的问题有一部分原因是由于format格式没有对应
            sdf.setLenient(false);

            try {
                return sdf.parse(dateStr);
            } catch (ParseException e) {

            }

        }
        log.debug(dateStr + "无法转换成Date");
        return null;


    }

    /*
     * 日期转字符串（描述到天）
     */
    public static String dateFormat(Date date) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }


    /*
     * 日期转字符串（描述到秒）
     */
    public static String dateTimeFormat(Date date) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }


    /*
     * 获取当前时间字符串 OOKK
     */
    public static String getNowStr() {


        return getNowStr0(true, false);

    }

    /*
     * 获取当前时间字符串 OOKK
     */
    public static String getNowStr0(Boolean isUpToSec, Boolean isLocalDesc) {

        if (isUpToSec) {
            if (isLocalDesc) {
                return new SimpleDateFormat("yyyy年MM年dd日HH时mm分ss秒").format(new Date());
            } else {
                return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            }

        } else {
            if (isLocalDesc) {
                return new SimpleDateFormat("yyyy年MM年dd日").format(new Date());
            } else {
                return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            }
        }
    }

    /*
     * 日期转Unix时间戳 OOKK
     */
    public static Long dateConvertSec(Date date) {

        return date == null ? null : date.getTime();
    }


    /*
     * Date日期加X天数 OOKK
     */
    public static Date addDay(Date date, Integer days) {
        if ((date == null) || (days == null) || (days == 0)) {
            return date;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(5, days);

        return calendar.getTime();
    }


    /*
     * Date日期加X月数 OOKK
     */
    public static Date addMonth(Date date, Integer months) {
        if ((date == null) || (months == null) || (months == 0)) {
            return date;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(2, months);

        return calendar.getTime();
    }


    /*
     *  计算2个Date日期间相差的天数 OOKK
     */
    public static int daysBetween(Date date1, Date date2) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date1);
        long time1 = cal.getTimeInMillis();
        cal.setTime(date2);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / 86400000L;
        return Integer.parseInt(String.valueOf(between_days));
    }


    /*
     *  返回XXXX年XX月有几天 OOKK
     */
    public static int getDaysOfMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /*
     * 返回指定Date所在月有几天  OOKK
     */
    public static int getDaysOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public static void main(String[] args) {


        // int daysOfMonth = getDaysOfMonth(parseDate("2020-02-10"));


        System.out.println(parseDate("2019-12-24"));
        System.out.println(parseDate("2019/12/24"));
        System.out.println(parseDate("2019-12-24 17:45:25"));
        System.out.println(parseDate("2019/12/24 17:45:25"));
        System.out.println(parseDate("2019年12月24日 17时45分25秒"));
        System.out.println(parseDate("2019年12月24日"));


    }


}
