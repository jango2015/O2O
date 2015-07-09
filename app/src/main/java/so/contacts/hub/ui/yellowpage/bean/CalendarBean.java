package so.contacts.hub.ui.yellowpage.bean;

import so.contacts.hub.util.CalendarUtil;
import java.io.Serializable;

/**
 * 日历选择项
 *
 */
public class CalendarBean implements Serializable, Comparable{
	
	private static final long serialVersionUID = 1L;
	/**
	 * type 类型
	 */
	public static final int MODEL_TAG_WEEK = 0;  // 星期几
	public static final int MODEL_TAG_PREVIOUS = 1;   //上个月
	public static final int MODEL_TAG_THIS_TIMEOUT = 2;  //本月(过期日期)
	public static final int MODEL_TAG_THIS = 3;  //本月
	public static final int MODEL_TAG_NEXT = 4;  //下个月
	
	private int type;
	
	private int day;
	
	private int month;
	
	private int year;
	
	private String weekInfo;
	
	// 日期附加信息
	private String content;
	
	/**
	 * 选择类型
	 */
	public static final int MODEL_SELECT_DEFAULT = 0; // 未被选择
	public static final int MODEL_SELECT_IN = 1; // 入住
	public static final int MODEL_SELECT_OUT = 2; // 离店

	public static final int MODEL_TRAIN_START = 6; // 火车票日期 add by lisheng ;

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}
	
	public int getMonth(){
		return month;
	}
	
	public void setMonth(int month){
		this.month = month;
	}
	
	public int getYear(){
		return year;
	}
	
	public void setYear(int year){
		this.year = year;
	}
	
	public String getWeekInfo(){
		return weekInfo;
	}
	
	public void setWeekInfo(String weekInfo){
		this.weekInfo = weekInfo;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	/**
	 * 获取格式化字符串
	 */
	public String getFormatStr(){
		//return String.valueOf(year) + CalendarUtil.getFormatTwoDecimal(month) + CalendarUtil.getFormatTwoDecimal(day); // 如：20140919
		return CalendarUtil.getAppointDate(year, month, day);
	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if( obj == null ){
			return false;
		}
		if( !(obj instanceof CalendarBean) ){
			return false;
		}
		CalendarBean calendarModel = (CalendarBean) obj;
		return this.year == calendarModel.getYear() && this.month == calendarModel.getMonth() 
				&& this.day == calendarModel.getDay();
	}

	@Override
	public int compareTo(Object obj) {
		// TODO Auto-generated method stub
		if( obj == null ){
			return -1;
		}
		if( !(obj instanceof CalendarBean) ){
			return -1;
		}
		CalendarBean calendarModel = (CalendarBean) obj;
		int rYear = calendarModel.getYear();
		int rMonth = calendarModel.getMonth();
		int rDay = calendarModel.getDay();
		if( this.year > rYear ){
			return 1;
		}else if( this.year == rYear ){
			if( this.month > rMonth ){
				return 1;
			}else if( this.month == rMonth ){
				return this.day - rDay;
			}else{
				return -1;
			}
		}else{
			return -1;
		}
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return year + "-" + month + "-" + day;
	}

}
