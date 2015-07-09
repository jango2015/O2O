
package so.contacts.hub.util;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.yulong.android.contacts.discover.R;

import so.contacts.hub.ContactsApp;
import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;

public class CalendarUtil {

    /**
     * 格式化日期的格式 年月日
     */
    public static final String DATE_FORMATTER = "yyyy-MM-dd";
    
    /**
     * 格式化日期的格式 月日
     */
    public static final String DATE_FORMATER_BIRTH = "MM-dd";

    /**
     * 格式化日期的格式 eg:xxxx年xx月xx日
     */
    public static final String DATE_PATTERN_CN = "yyyy年MM月dd日";
    
    /**
     * 格式化日期的格式:XX月:XX日
     */
    public static final String DATE_FORMATTER_MONTH_DAY = "MM月dd日";
    
    /**
     * 格式化时间的格式:XX:XX
     */
    public static final String DATE_FORMATTER_TIME = "HH:mm";

    /**
     * 格式化日期的格式 年月日时分秒
     */
    public static final String DATE_FORMATTER_SIX = "yyyy-MM-dd HH:mm:ss";

    /**
     * 格式化日期的格式 的 分隔符
     */
    public static final String DATE_FORMATTER_CN_SIX = "yyyy年MM月dd日 HH:mm:ss";
    
	/**
	 * 格式化日期的格式 的 分隔符
	 */
	public static final String DATE_FORMATTER_GAP = "-";

    /**
     * 格式化日期的格式 分割数量
     */
    public static final int DATE_FORMATTER_NUM = 3;

    /**
     * 星期的数量
     */
    public static final int WEEK_NUM = 7;

    /**
     * 月份的数量
     */
    public static final int MONTH_NUM = 12;

    /**
     * 获取指定日期的明天
     */
    public static String getAppointTomorrowDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);
        calendar.add(calendar.DATE, 1);
        Date date = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMATTER);
        return formatter.format(date);
    }

    /**
     * 获取指定日期
     */
    public static String getAppointDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day - 1);
        calendar.add(calendar.DATE, 1);
        Date date = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMATTER);
        return formatter.format(date);
    }

    /**
     * 格式化时间
     */
    public static String getFormatDate(String dateUrl) {
        if(TextUtils.isEmpty(dateUrl)){
            return "";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMATTER);
        Date date = null;
        try {
            date = dateFormat.parse(dateUrl);
        } catch (ParseException e) {
        }
        if(date != null){
            return dateFormat.format(date);
        }
        SimpleDateFormat datefFormatBirth = new SimpleDateFormat(DATE_FORMATER_BIRTH);
        try {
            date = datefFormatBirth.parse(dateUrl);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(date != null){
            return datefFormatBirth.format(date);
        }
        return "";
    }

    /**
     * 格式化时间
     * 
     * @param dateStr
     * @return
     */
    public static Date getDateFromString(String dateStr) {
        if (TextUtils.isEmpty(dateStr)) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMATTER);
        Date date = null;
        try {
            date = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * @param dateStr
     * @param pattern
     * @return
     */
    public static Date getDateFromString(String dateStr, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Date date = null;
        try {
            date = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 获得日期格式化后的字符串
     * 
     * @param date
     * @param pattern
     * @return
     */
    public static String getDateStrFromDate(Date date, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(date);
    }

    /**
     * @param time
     * @param pattern
     * @return
     */
    public static String getDateStrFromLong(long time, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Date date = new Date(time);
        return dateFormat.format(date);
    }

    /**
     * @param date
     * @return
     */
    public static String getDateStrFromDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMATTER);
        return dateFormat.format(date);
    }

    /**
     * 获取当前日期,格式年月日
     */
    public static String getNowDateStr() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMATTER);
        return dateFormat.format(new Date());
    }

    /**
     * 获取当前日期并格式化为年月日时分秒
     * 
     * @return
     */
    public static String getNowDateStr(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMATTER_SIX);
        return dateFormat.format(date);
    }

    /**
     * 获取第month月第一天日期
     */
    public static String getFirstDayOfMonth(int year, int month) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMATTER);

        Calendar firstDate = Calendar.getInstance();
        firstDate.set(Calendar.DATE, 1);// 设为当前月的1号
        firstDate.set(Calendar.MONTH, month);
        firstDate.set(Calendar.YEAR, year);
        return dateFormat.format(firstDate.getTime());
    }

    /**
     * 获取本月第一天日期
     */
    public static String getFirstDayOfMonth() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMATTER);

        Calendar firstDate = Calendar.getInstance();
        firstDate.set(Calendar.DATE, 1);// 设为当前月的1号
        return dateFormat.format(firstDate.getTime());
    }

    /**
     * 获取本月最后一天日期
     */
    public static String getLastDayOfMonth() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMATTER);

        Calendar lastDate = Calendar.getInstance();
        lastDate.set(Calendar.DATE, 1);// 设为当前月的1号
        lastDate.add(Calendar.MONTH, 1);// 加一个月，变为下月的1号
        lastDate.add(Calendar.DATE, -1);// 减去一天，变为当月最后一天

        return dateFormat.format(lastDate.getTime());
    }

    /**
     * 获取上月最后一天的日期
     */
    public String getLastDayOfPreviousMonth() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMATTER);
        Calendar lastDate = Calendar.getInstance();
        lastDate.add(Calendar.MONTH, -1);// 减一个月
        lastDate.set(Calendar.DATE, 1);// 把日期设置为当月第一天
        lastDate.roll(Calendar.DATE, -1);// 日期回滚一天，也就是本月最后一天
        return dateFormat.format(lastDate.getTime());
    }

    /**
     * 获取上月第一天的日期
     */
    public String getPreviousMonthFirst() {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMATTER);

        Calendar lastDate = Calendar.getInstance();
        lastDate.set(Calendar.DATE, 1);// 设为当前月的1号
        lastDate.add(Calendar.MONTH, -1);// 减一个月，变为下月的1号

        return formatter.format(lastDate.getTime());
    }

    /**
     * 获取明天的日期
     */
    public static String getTomorrowDateStr() {
        Date date = new Date();// 取时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(calendar.DATE, 1);
        date = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMATTER);
        return formatter.format(date);
    }

    /**
     * 获取某个月有多少天
     */
    public static int getDaysOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        int days_of_month = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        return days_of_month;
    }

    /**
     * 获取第month月有多少天
     */
    public static int getDaysOfMonth(int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, month);

        int days_of_month = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        return days_of_month;
    }

    /**
     * 获取当前月有多少天
     */
    public static int getDaysOfMonth() {
        Calendar cal = Calendar.getInstance();
        int days_of_month = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        return days_of_month;
    }

    /***
     * 获得今天在本月的第几天
     */
    public static int getDayIndexOfMonth() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取两个日期之间间隔天数
     * 日期格式为：yyyy-MM-dd
     * 如：2014-01-14 与 2014-01-14相隔0天
     * 如：2014-01-14 与 2014-01-15相隔1天
     */
    public static long getGapBetweenTwoDay(String startDate, String endDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMATTER);
        long day = 0;
        try {
            Date dateStart = dateFormat.parse(startDate);
            Date dateEnd = dateFormat.parse(endDate);
            day = (dateEnd.getTime() - dateStart.getTime()) / (24 * 60 * 60 * 1000);
        } catch (Exception e) {
            day = -1;
        }
        return day;
    }

    /**
     * 获得今天在本周的第几天
     */
    public static int getDayOfWeek() {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 获取当前年份
     */
    public static int getYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    /**
     * 获取当前月份
     */
    public static int getMonth() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    /**
     * 根据日期获取第几号 如：2014-09-18，获取到的是：18
     */
    public static int getDayByDate(String date) {
        if (TextUtils.isEmpty(date)) {
            return -1;
        }
        String[] strList = date.split(DATE_FORMATTER_GAP);
        if (strList == null || strList.length < DATE_FORMATTER_NUM) {
            return -1;
        }
        String dayStr = strList[DATE_FORMATTER_NUM - 1];
        int day = -1;
        try {
            day = Integer.valueOf(dayStr);
        } catch (Exception e) {
            day = -1;
        }
        return day;
    }

    /**
     * 获取date是星期几 注：返回值星期天为0， 星期一为1
     */
    public static int getWeekNumByDate(String dateStr) {
        if (TextUtils.isEmpty(dateStr)) {
            return -1;
        }
        int weekNum = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMATTER);
        try {
            Date date = dateFormat.parse(dateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            weekNum = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        } catch (Exception e) {

        }
        return weekNum;
    }

    /**
     * 格式化数据
     */
    public static String getFormatTwoDecimal(int data) {
        String MONTH_FORMAT = "00";
        DecimalFormat decimalFormat = new DecimalFormat(MONTH_FORMAT);
        return decimalFormat.format(data);
    }

    /**
     * 用于格式化时间显示的工具方法,最近的时间显示为上午/下午/昨天,其他显示为日期
     * 
     * @param time
     * @return
     */
    public static String formatTimeForMessageCenter(long time) {
        Resources resources = ContactsApp.getContext().getResources();
        Calendar today = Calendar.getInstance();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
            switch (calendar.get(Calendar.AM_PM)) {
                case 0:
                	return resources.getString(R.string.morning_date, 
                			getFormatTwoDecimal(calendar.get(Calendar.HOUR)), getFormatTwoDecimal(calendar.get(Calendar.MINUTE)));
                case 1:
                	/**
                	 * 修复bug# 3026 提醒，上午12点，显示为“下午 00:00”，应该修改为 “上午 12:00”
                	 */
                	int hour = calendar.get(Calendar.HOUR_OF_DAY);
                	if(hour == 12){
                		return resources.getString(R.string.middle_date, 
                    			getFormatTwoDecimal(hour), getFormatTwoDecimal(calendar.get(Calendar.MINUTE)));
                	}else{
                		return resources.getString(R.string.afternoon_date, 
                    			getFormatTwoDecimal(calendar.get(Calendar.HOUR)), getFormatTwoDecimal(calendar.get(Calendar.MINUTE)));
                	}
            }
        } else if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                && (calendar.get(Calendar.DAY_OF_YEAR) + 1) == today.get(Calendar.DAY_OF_YEAR)) {
            return resources.getString(R.string.yesterday);
        } else {
        	return resources.getString(R.string.putao_calendar_showdate_month_divider, 
        			getFormatTwoDecimal(calendar.get(Calendar.MONTH) + 1), getFormatTwoDecimal(calendar.get(Calendar.DAY_OF_MONTH)));
        }
        return "";
    }
    
    
    /**
     * 时间字符串转long
     * 
     * @param dateStr
     * @param formatStr
     * @return
     */
    public static long dateStr2long(String dateStr, String formatStr){
        SimpleDateFormat dateFormat = new SimpleDateFormat(formatStr);
        long dateInMillisecond = 0;
        try {
            Date dt2 = dateFormat.parse(dateStr);
            dateInMillisecond = dt2.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateInMillisecond;
    }

    /**
     * 毫秒转换为距离时间
     * 
     * @param dateInMillisecondStr
     * @param context
     * @param detail
     * @return
     */
    public static String formatShowDate(long dateInMillisecond, Context context, boolean detail) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateStr = dateFormat.format(new Date(dateInMillisecond));
        long nowInMillisecond = System.currentTimeMillis();
        String nowdate = dateFormat.format(new Date(nowInMillisecond));
        int dateYear = Integer.parseInt(dateStr.substring(0, 4));
        int dateMonth = Integer.parseInt(dateStr.substring(5, 7));
        int dateDay = Integer.parseInt(dateStr.substring(8, 10));
        int nowYear = Integer.parseInt(nowdate.substring(0, 4));
        int nowMonth = Integer.parseInt(nowdate.substring(5, 7));
        int nowDay = Integer.parseInt(nowdate.substring(8, 10));
        if (nowInMillisecond - dateInMillisecond <= -60 * 1000) {
            return String.format(
                    context.getResources().getString(R.string.putao_date_year_month_day),
                    dateStr.substring(0, 4), dateStr.substring(5, 7), dateStr.substring(8, 10),
                    dateStr.substring(11, 16));
        }
        long timeInterval = nowInMillisecond - dateInMillisecond;
        int timeIntervalDivideLevel = 60 * 1000;
        if (timeInterval < timeIntervalDivideLevel) {
            return context.getResources().getString(R.string.putao_date_just_now);
        }
        timeIntervalDivideLevel *= 60;
        if (timeInterval < timeIntervalDivideLevel) {
            return String.format(context.getResources().getString(R.string.putao_date_minute),
                    timeInterval / (60 * 1000) + "");
        }
        if (dateYear == nowYear && dateMonth == nowMonth && dateDay == nowDay) {
            return String.format(context.getResources().getString(R.string.putao_date_today),
                    dateStr.substring(11, 16));
        }
        if (dateYear == nowYear && dateMonth == nowMonth) {
            // return String.format(
            // context.getResources().getString(R.string.putao_date_yesterday),
            // dateStr.substring(11, 16));
            return String.format(
                    context.getResources().getString(R.string.putao_date_month_day),
                    dateStr.substring(5, 7), dateStr.substring(8, 10), dateStr.substring(11, 16));
        }
        if (dateYear == nowYear) {
            if (detail) {
                return String.format(
                        context.getResources().getString(R.string.putao_date_month_day),
                        dateStr.substring(5, 7), dateStr.substring(8, 10),
                        dateStr.substring(11, 16));
            } else {
                return String.format(
                        context.getResources().getString(R.string.putao_date_month_day_simple),
                        dateStr.substring(5, 7), dateStr.substring(8, 10));
            }
        }
        if (detail) {
            return String.format(
                    context.getResources().getString(R.string.putao_date_year_month_day),
                    dateStr.substring(0, 4), dateStr.substring(5, 7), dateStr.substring(8, 10),
                    dateStr.substring(11, 16));
        } else {
            return String.format(
                    context.getResources().getString(R.string.putao_date_year_month_day_simple),
                    dateStr.substring(0, 4), dateStr.substring(5, 7));
        }
    }
    
    /**
     * 毫秒转换为距离时间
     * 时间为今天或昨天，则显示”今天/昨天 + 时间“
     * 时间为昨天之前，则显示”日期+时间
     * 
     * @param dateInMillisecondStr
     * @param context
     * @return
     */
    public static String showDateAgo(long dateInMillisecond, Context context) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateStr = dateFormat.format(new Date(dateInMillisecond));
        long nowInMillisecond = System.currentTimeMillis();
        String nowdate = dateFormat.format(new Date(nowInMillisecond));
        int dateYear = Integer.parseInt(dateStr.substring(0, 4));
        int dateMonth = Integer.parseInt(dateStr.substring(5, 7));
        int dateDay = Integer.parseInt(dateStr.substring(8, 10));
        int nowYear = Integer.parseInt(nowdate.substring(0, 4));
        int nowMonth = Integer.parseInt(nowdate.substring(5, 7));
        int nowDay = Integer.parseInt(nowdate.substring(8, 10));
        
        if (dateYear == nowYear && dateMonth == nowMonth && dateDay == nowDay) {
            return String.format(context.getResources().getString(R.string.putao_date_today),
                    dateStr.substring(11, 16));
        }
        if (nowInMillisecond / (24* 60 * 60 * 1000) - dateInMillisecond / (24* 60 * 60 * 1000) <= 1 &&
                nowInMillisecond / (24* 60 * 60 * 1000) - dateInMillisecond / (24* 60 * 60 * 1000) > 0 ) {
             return String.format(
             context.getResources().getString(R.string.putao_date_yesterday),
             dateStr.substring(11, 16));
        }
        if (dateYear == nowYear) {
            return dateStr.substring(5, 10) + " " + dateStr.substring(11, 16);
        } else {
            return dateStr;
        }
    }
    
    /**
     * 检查时间是否过了指定时间
     * 
     * @param hour
     * @param minute
     * @return
     */
    public static boolean isAfterTime(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        int totalMinutes = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
        if (totalMinutes >= hour * 60 + minute) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取传入时间的小时数
     * 
     * @param time
     * @return
     */
    public static int getHourOfDay(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 根据月日判断星座
     * 
     * @param month
     * @param day
     * @return int
     */
    public static String getConstellation(int month, int day) {

        final String[] constellationArr = {
                "魔羯座", "水瓶座", "双鱼座", "牡羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座",
                "魔羯座"
        };

        final int[] constellationEdgeDay = {
                20, 18, 20, 20, 20, 21, 22, 22, 22, 22, 21, 21
        };
        if (day <= constellationEdgeDay[month - 1]) {
            month = month - 1;
        }
        if (month >= 0) {
            return constellationArr[month];
        }
        return "";

    }

    /**
     * add by zj 2015-01-07 20:13:36
     * 格式化出longLongAgo距现在的时间,60分钟内已分钟为单位,24小时内以小时为单位,其余用天为单位
     * @param context
     * @param longLongAgo
     * @return
     */
    public static String formatTimeDesc(Context context, long longLongAgo) {
        long time = System.currentTimeMillis() - longLongAgo;
        if (time > 0) {
            int minutes = (int)(time / 60000);
            int hours = (int)(time / 3600000);
            int days = (int)(time / 86400000);
            if (minutes < 59) {
                return context.getString(R.string.minutes,minutes);
            } else if (hours < 23) {
                return context.getString(R.string.hour, hours);
            } else {
                return context.getString(R.string.day, days);
            }
        }
        return null;
    }

    /**
     * 格式化日期：XX月XX日
     * @param time
     * @return
     */
    public static String getFormatDateMonthDay(long time){
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMATTER_MONTH_DAY);
        return dateFormat.format(new Date(time));
    }
    
    /**
     * 格式化时间：XX:XX
     * @param time
     * @return
     */
    public static String getFormatTime(long time){
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMATTER_TIME);
        return dateFormat.format(new Date(time));
    }
    
    /**
     * @param day 将"2015-01-21"转化成Calendar
     * @return Calendar or null
     */
    public static Calendar convertToCalendar(String day) {
    	Calendar c =null;
		try {
			SimpleDateFormat sdf= new SimpleDateFormat(DATE_FORMATTER);
			Date date =sdf.parse(day);
			c = Calendar.getInstance();
			c.setTime(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return c;
    }
}
