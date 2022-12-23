

package io.agilefastgateway.util;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期处理
 * 
 * @author
 * @email
 * @date 2016年12月21日 下午12:53:33
 */
public class DateUtils {
    public final static String FORMAT_DATE_DEFAULT = "yyyy-MM-dd";
	/** 时间格式(yyyy-MM-dd) */
	public final static String DATE_PATTERN = "yyyy-MM-dd";
	/** 时间格式(yyyy-MM-dd HH:mm:ss) */
	public final static String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 日期格式化 日期格式为：yyyy-MM-dd
     * @param date  日期
     * @return  返回yyyy-MM-dd格式日期
     */
	public static String format(Date date) {
        return format(date, DATE_PATTERN);
    }

    /**
     * 日期格式化 日期格式为：yyyy-MM-dd
     * @param date  日期
     * @param pattern  格式，如：DateUtils.DATE_TIME_PATTERN
     * @return  返回yyyy-MM-dd格式日期
     */
    public static String format(Date date, String pattern) {
        if(date != null){
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            return df.format(date);
        }
        return null;
    }

    public final static Date getCurrentDate() {
        return Calendar.getInstance().getTime();
    }

    /**
     * Returns current system date as formatted string value with default format
     * pattern.
     *
     * @return current system date.
     * @see #FORMAT_DATE_DEFAULT
     */
    public final static String getCurrentDateAsString() {
        return getCurrentDateAsString(FORMAT_DATE_DEFAULT);
    }

    /**
     * Returns current system date as formatted string value with given format
     * pattern.
     *
     * @param formatPattern format pattern.
     * @return current system date.
     */
    public final static String getCurrentDateAsString(String formatPattern) {
        Date date = getCurrentDate();
        return format(date, formatPattern);
    }

    /**
     * 将String转换成date
     */
    public static Date toDate(String stringDate) {
        try {
            return new SimpleDateFormat(DATE_TIME_PATTERN).parse(stringDate);
        } catch (ParseException e) {
            System.out.println("字符串转换时间出错！2222");
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 字符串转换成日期
     * @param strDate 日期字符串
     * @param pattern 日期的格式，如：DateUtils.DATE_TIME_PATTERN
     */
    public static Date stringToDate(String strDate, String pattern) {
        if (StringUtils.isBlank(strDate)){
            return null;
        }

        DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
        return fmt.parseLocalDateTime(strDate).toDate();
    }

    /**
     * 根据周数，获取开始日期、结束日期
     * @param week  周期  0本周，-1上周，-2上上周，1下周，2下下周
     * @return  返回date[0]开始日期、date[1]结束日期
     */
    public static Date[] getWeekStartAndEnd(int week) {
        DateTime dateTime = new DateTime();
        LocalDate date = new LocalDate(dateTime.plusWeeks(week));

        date = date.dayOfWeek().withMinimumValue();
        Date beginDate = date.toDate();
        Date endDate = date.plusDays(6).toDate();
        return new Date[]{beginDate, endDate};
    }

    /**
     * 对日期的【秒】进行加/减
     *
     * @param date 日期
     * @param seconds 秒数，负数为减
     * @return 加/减几秒后的日期
     */
    public static Date addDateSeconds(Date date, int seconds) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusSeconds(seconds).toDate();
    }

    /**
     * 对日期的【分钟】进行加/减
     *
     * @param date 日期
     * @param minutes 分钟数，负数为减
     * @return 加/减几分钟后的日期
     */
    public static Date addDateMinutes(Date date, int minutes) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusMinutes(minutes).toDate();
    }

    /**
     * 对日期的【小时】进行加/减
     *
     * @param date 日期
     * @param hours 小时数，负数为减
     * @return 加/减几小时后的日期
     */
    public static Date addDateHours(Date date, int hours) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusHours(hours).toDate();
    }

    /**
     * 对日期的【天】进行加/减
     *
     * @param date 日期
     * @param days 天数，负数为减
     * @return 加/减几天后的日期
     */
    public static Date addDateDays(Date date, int days) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusDays(days).toDate();
    }

    /**
     * 对日期的【周】进行加/减
     *
     * @param date 日期
     * @param weeks 周数，负数为减
     * @return 加/减几周后的日期
     */
    public static Date addDateWeeks(Date date, int weeks) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusWeeks(weeks).toDate();
    }

    /**
     * 对日期的【月】进行加/减
     *
     * @param date 日期
     * @param months 月数，负数为减
     * @return 加/减几月后的日期
     */
    public static Date addDateMonths(Date date, int months) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusMonths(months).toDate();
    }

    /**
     * 对日期的【年】进行加/减
     *
     * @param date 日期
     * @param years 年数，负数为减
     * @return 加/减几年后的日期
     */
    public static Date addDateYears(Date date, int years) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusYears(years).toDate();
    }

    /**
     * 获取当月第一天
     * @return
     */
    public static Date getFirstDayOfMonth()
    {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天
        return c.getTime();
    }

    /**
     * 获取当月最后一天
     * @return
     */
    public static Date getLastDayOfMonth()
    {
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
        return ca.getTime();
    }

    /**
     * 求两时间之间的分钟差值
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return 差值 (单位：分钟)
     */
    public static int getMinuteDifference(Date startDate, Date endDate){
        long start = startDate.getTime();
        long end = endDate.getTime();
        return (int) ((end - start) / (1000 * 60));
    }

    /**
     * 求两时间之间的秒差值
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return 差值 (单位：秒)
     */
    public static int getSecondDifference(Date startDate,Date endDate){
        long start = startDate.getTime();
        long end = endDate.getTime();
        return (int) ((end - start) / (1000));
    }

    /**
     * 获取 1 天 20 小时 26 分 40 秒
     * @param time
     * @return
     */
    public static String exchangeDate(String time){
        Long second = Long.parseLong(time);
        if (second < 60) return Math.round(second) + "秒";
        int days = Math.round(second / (60*60*24));
        int hours = Math.round((second - days*60*60*24)/(60*60));
        int minutes = Math.round((second - days*60*60*24 - hours*60*60)/60);
        int seconds = Math.round(second - days*60*60*24 - hours*60*60 - minutes * 60);
        return (days==0? "": days+" 天 ")+(hours == 0? "" :hours+" 小时 ")+(minutes==0?"":minutes+" 分 ")+(seconds==0?"":seconds+" 秒");
    }
}
