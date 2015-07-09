package so.contacts.hub.adapter;

import so.contacts.hub.ui.yellowpage.bean.CalendarBean;
import com.yulong.android.contacts.discover.R;
import so.contacts.hub.util.CalendarUtil;
import so.contacts.hub.util.LogUtil;
import android.graphics.Color;
import android.widget.TextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.content.Context;
import java.util.List;
import java.util.ArrayList;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class CalendarAdapter extends BaseAdapter {
	
	private int mGrayColor = 0; // 灰色
	
	private int mBgGreenColor = 0; // 背景-选中状态：绿色
	
	private int mBgNormalColor = 0; // 背景-正常状态： 灰色
	
	private String mHotelInTag = ""; // 入住
	
	private String mHotelOutTag = ""; // 离店
	
	private String mTrainTag = "";//出发  add by lisheng ;
	
	private boolean isSelected =false;
	
	/**
	 * 【true】 显示日历
	 * 【false】 显示 星期
	 */
	private boolean mIsCalendar = true;
	
	// 当前时间的日份
	private int mCurrentDayIndex = 0;
	
	// 当前时间的月份
	private int mCurrentMonthIndex = 0;
	
	// 当前选择显示的月份
	private int mSelectMonthIndex = 0;
	
	// 当前时间的年份
	private int mCurrentYearIndex = 0;
	
	// 当前选择显示的年份
	private int mSelectYearIndex = 0;
	
	private LayoutInflater mInflater = null;

	private List<CalendarBean> mCalendarList = new ArrayList<CalendarBean>();
	
	private String [] mWeekTag = new String[CalendarUtil.WEEK_NUM];
	
	// 入住日期
	private CalendarBean mInCalendarModel = null;
	
	// 离店日期
	private CalendarBean mOutCalendarModel = null;
	
	//add by lisheng start2014-12-15 
	private CalendarBean today =null;
	private CalendarBean tomorrow =null;
	private CalendarBean afterTomorrow =null;
	//add by lisheng end
	
	
	
	//火车出发日期
	private CalendarBean mTrainCalendarModel =null;//add by lisheng 

	private String mTrainTodayTag;

	private String mTrainTommorrowTag;

	private String mTrainAfterTommorowTag;
	
	public CalendarAdapter(Context context, boolean isCalendar){
		mIsCalendar = isCalendar;
		mInflater = LayoutInflater.from(context);
		
		mGrayColor = context.getResources().getColor(R.color.putao_express_result_no_data_info);
		/**
		 * modify by putao_lhq @start
		 * old code:
		 mBgGreenColor = context.getResources().getColor(R.color.putao_light_green);
		 */
		mBgGreenColor = context.getResources().getColor(R.color.putao_calendar_selected_color);
		/*@end modify by ptuao_lhq*/
		mBgNormalColor = context.getResources().getColor(R.color.putao_light_gray);
		
		mHotelInTag = context.getResources().getString(R.string.putao_hotel_hotel_in);
		mHotelOutTag = context.getResources().getString(R.string.putao_hotel_hotel_out);
		mTrainTag =context.getResources().getString(R.string.putao_train_start_date);
		
		// 显示今天,明天,后天
		mTrainTodayTag =context.getResources().getString(R.string.putao_train_today_date);
		mTrainTommorrowTag =context.getResources().getString(R.string.putao_train_tommorrow_date);
		mTrainAfterTommorowTag =context.getResources().getString(R.string.putao_train_aftertommorow_date);
		

		mWeekTag = context.getResources().getStringArray(R.array.putao_week_list);
		
		if( mIsCalendar ){
			mCurrentDayIndex = CalendarUtil.getDayIndexOfMonth();
			mCurrentMonthIndex = CalendarUtil.getMonth();
			mSelectMonthIndex = mCurrentMonthIndex;
			mCurrentYearIndex = CalendarUtil.getYear();
			mSelectYearIndex = mCurrentYearIndex;
			initCalendarData();
		}else{
			initCalendarWeekData(context);
		}
	}
	
	/**
	 * 初始化 入住、离店日期
	 */
	public void setInAndOutDate(CalendarBean inCalendarModel, CalendarBean outCalendarModel){
		mInCalendarModel = inCalendarModel;
		mOutCalendarModel = outCalendarModel;
	}
	
	//add by lisheng start 2014-12-15
	/**初始化火车票日期,显示今天,明天,后天*/
	public void setTrainStartDate(CalendarBean mTrainCalendarModel,
			CalendarBean today, CalendarBean tomorrow,
			CalendarBean afterTomorrow) {
		this.mTrainCalendarModel = mTrainCalendarModel;
		this.today = today;
		this.tomorrow = tomorrow;
		this.afterTomorrow = afterTomorrow;
	}
	//add by lisheng end
	
	
	/**
	 * 显示当前时间
	 */
	public void showDate(IUpdateCalendarDate iUpdateDate){
		if( iUpdateDate != null ){
			int updateType = IUpdateCalendarDate.UPDATE_TYPE_UPDATE;
			if( mSelectMonthIndex == mCurrentMonthIndex ){
				// 如果刷新后等于当前月份
				updateType = IUpdateCalendarDate.UPDATE_TYPE_UPDATE_AND_THIS;
			}
			iUpdateDate.updateDate(mSelectYearIndex, mSelectMonthIndex, updateType);
		}
	}
	
	/**
	 * 显示下一个月的日历
	 */
	public void showNextMonth(IUpdateCalendarDate iUpdateDate){
		if( mSelectMonthIndex == 12 ){
			// 最后一个月，则跳到下一年中的第1个月
			mSelectYearIndex++;
			mSelectMonthIndex = 1;
		}else{
			mSelectMonthIndex++;
		}
		initCalendarData();
		notifyDataSetChanged();
		
		if( iUpdateDate != null ){
			iUpdateDate.updateDate(mSelectYearIndex, mSelectMonthIndex, IUpdateCalendarDate.UPDATE_TYPE_UPDATE);
		}
	}
	
	/**
	 * 显示上一个月的日历
	 */
	public void showPreviousMonth(IUpdateCalendarDate iUpdateDate){
		int updateType = IUpdateCalendarDate.UPDATE_TYPE_UPDATE;
		if( mSelectYearIndex == mCurrentYearIndex ){
			// 同一年份
			if( mSelectMonthIndex > mCurrentMonthIndex ){
				mSelectMonthIndex--;
			}else{
				updateType = IUpdateCalendarDate.UPDATE_TYPE_NOT_UPDATE;
			}
		}else if( mSelectYearIndex > mCurrentYearIndex ){
			// 后面年份
			if( mSelectMonthIndex == 1 ){
				// 第一个月, 则跳到上一年中的第12个月
				mSelectYearIndex--;
				mSelectMonthIndex = 12;
			}else{
				mSelectMonthIndex--;
			}
		}else{
			updateType = IUpdateCalendarDate.UPDATE_TYPE_NOT_UPDATE;
		}
		if( updateType < IUpdateCalendarDate.UPDATE_TYPE_NOT_UPDATE ){
			initCalendarData();
			notifyDataSetChanged();
		}
		
		if( iUpdateDate != null ){
			if( mSelectYearIndex == mCurrentYearIndex && mSelectMonthIndex == mCurrentMonthIndex ){
				// 如果刷新后等于当前年当前月份
				updateType = IUpdateCalendarDate.UPDATE_TYPE_UPDATE_AND_THIS;
			}
			iUpdateDate.updateDate(mSelectYearIndex, mSelectMonthIndex, updateType);
		}
	}
	
	/**
	 * 设置入住、离店等
	 */
	public void setCalendarInfo(int selectPos, int type){
		if( selectPos > mCalendarList.size() -1 ){
			return;
		}
		CalendarBean calendarModel = mCalendarList.get(selectPos);
		if( type == CalendarBean.MODEL_SELECT_IN ){
			// 入住
			mInCalendarModel = calendarModel;
			notifyDataSetChanged();
		}else if( type == CalendarBean.MODEL_SELECT_OUT ){
			//离店
			mOutCalendarModel = calendarModel;
			notifyDataSetChanged();
		
		}else if(type ==CalendarBean.MODEL_TRAIN_START ){ //add by lisheng  火车票日期
			mTrainCalendarModel  =calendarModel;
			
			notifyDataSetChanged();
		}
		
		//add by lisheng end;
	}
	
	/**
	 * 清除离店信息
	 */
	public void clearOutCalendarInfo(){
		mOutCalendarModel = null;
	}
	
	/**
	 * 初始化星期
	 */
	private void initCalendarWeekData(Context context){
		mCalendarList.clear();
		
		//添加星期几的标识
		for(int i = 0; i < CalendarUtil.WEEK_NUM; i++){
			CalendarBean calendarModel = new CalendarBean();
			calendarModel.setWeekInfo(mWeekTag[i]);
			calendarModel.setType(CalendarBean.MODEL_TAG_WEEK);
			mCalendarList.add(calendarModel);
		}
	}
	
	/**
	 * 初始化日历
	 */
	private void initCalendarData(){
		mCalendarList.clear();
		
		String firstDateOfMonth = CalendarUtil.getFirstDayOfMonth(mSelectYearIndex, mSelectMonthIndex-1);
		long monthDays = CalendarUtil.getDaysOfMonth(mSelectYearIndex, mSelectMonthIndex-1); 
		
		// 1. 添加上个月的
		int weekDay = CalendarUtil.getWeekNumByDate(firstDateOfMonth);
		if( weekDay > 0 ){
			for(int i = 0; i < weekDay; i++){
				CalendarBean calendarModel = new CalendarBean();
				calendarModel.setDay(-1);
				calendarModel.setType(CalendarBean.MODEL_TAG_PREVIOUS);
				mCalendarList.add(calendarModel);
			}
		}
		
		// 2. 添加本月的
		for(int i = 0; i < monthDays; i++){
			CalendarBean calendarModel = new CalendarBean();
			calendarModel.setDay(i+1);
			calendarModel.setMonth(mSelectMonthIndex);
			calendarModel.setYear(mSelectYearIndex);
			if( mSelectYearIndex <= mCurrentYearIndex && mSelectMonthIndex <= mCurrentMonthIndex 
					&& (i + 1) < mCurrentDayIndex){
				// 本月(过期日期)
				calendarModel.setType(CalendarBean.MODEL_TAG_THIS_TIMEOUT);
			}else{
				// 本月(正常日期)
				calendarModel.setType(CalendarBean.MODEL_TAG_THIS);
			}
			int weekNum = mCalendarList.size() % CalendarUtil.WEEK_NUM;
			calendarModel.setWeekInfo(mWeekTag[weekNum]);
			mCalendarList.add(calendarModel);
		}
		
		// 3. 添加下个月的
		int lastRowIndex = mCalendarList.size() % CalendarUtil.WEEK_NUM;
		if( lastRowIndex != 0 ){
			for(int i = lastRowIndex; i < CalendarUtil.WEEK_NUM; i++ ){
				CalendarBean calendarModel = new CalendarBean();
				calendarModel.setDay(i - lastRowIndex + 1);
				
				int nextMonth = mSelectMonthIndex;
				int nextMonthYear = mSelectYearIndex;
				if( mSelectMonthIndex == 12 ){
					nextMonthYear = mSelectYearIndex + 1;
					nextMonth = 1;
				}else{
					nextMonth++;
				}
				calendarModel.setMonth(nextMonth);
				calendarModel.setYear(nextMonthYear);
				
				calendarModel.setType(CalendarBean.MODEL_TAG_NEXT);
				mCalendarList.add(calendarModel);
			}
		}
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mCalendarList == null ? 0 : mCalendarList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mCalendarList == null ? null : mCalendarList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.putao_calendar_item, null);
		}
		TextView contextTView = (TextView) convertView
				.findViewById(R.id.calendar_item_text);
		TextView tagTView = (TextView) convertView
				.findViewById(R.id.calendar_item_tag);
		tagTView.setText("");
		tagTView.setVisibility(View.GONE);
		CalendarBean calendarModel = mCalendarList.get(position);
		if (calendarModel.getType() == CalendarBean.MODEL_TAG_WEEK) {
			// 星期几
			contextTView.setTextColor(mGrayColor);
			convertView.setBackgroundColor(Color.TRANSPARENT);
			contextTView.setText(calendarModel.getWeekInfo());
		} else {
			int day = calendarModel.getDay();
			if (calendarModel.equals(mInCalendarModel)) {
				// 入住
				tagTView.setVisibility(View.VISIBLE);
				contextTView.setTextColor(Color.WHITE);
				convertView.setBackgroundColor(mBgGreenColor);
				tagTView.setText(mHotelInTag);
			} else if (calendarModel.equals(mOutCalendarModel)) {
				// 离店
				tagTView.setVisibility(View.VISIBLE);
				contextTView.setTextColor(Color.WHITE);
				convertView.setBackgroundColor(mBgGreenColor);
				tagTView.setText(mHotelOutTag);

			} else if (mTrainCalendarModel != null
					&& calendarModel.equals(mTrainCalendarModel)) {
				// add by lisheng start
				contextTView.setTextColor(Color.WHITE);
				convertView.setBackgroundColor(mBgGreenColor);
				// add by lisheng end

			} else {
				if (calendarModel.getType() == CalendarBean.MODEL_TAG_PREVIOUS) {
					// 上个月
					convertView.setBackgroundColor(mBgNormalColor);
					contextTView.setTextColor(Color.BLACK);
				} else if (calendarModel.getType() == CalendarBean.MODEL_TAG_THIS_TIMEOUT) {
					// 本月过期日期
					convertView.setBackgroundColor(mBgNormalColor);
					contextTView.setTextColor(mGrayColor);
				} else if (calendarModel.getType() == CalendarBean.MODEL_TAG_THIS) {
					// 本月正常日期
					convertView.setBackgroundColor(mBgNormalColor);
					contextTView.setTextColor(Color.BLACK);
				} else if (calendarModel.getType() == CalendarBean.MODEL_TAG_NEXT) {
					// 下个月
					convertView.setBackgroundColor(Color.TRANSPARENT);
					contextTView.setTextColor(Color.BLACK);
				}
			}
			if (day == -1) {
				contextTView.setText("");
			} else if (mTrainCalendarModel != null) {
				if (calendarModel.equals(today)) { // add by lisheng 添加火车票显示今天,明天,后天的信息
													
					contextTView.setText(mTrainTodayTag);
					if (mTrainCalendarModel != null
							&& !mTrainCalendarModel.equals(calendarModel)) {
						contextTView.setTextColor(Color.BLACK);
						convertView.setBackgroundColor(Color.TRANSPARENT);
					} else if (mTrainCalendarModel != null
							&& mTrainCalendarModel.equals(calendarModel)) {
						convertView.setBackgroundColor(mBgGreenColor);
						contextTView.setTextColor(Color.WHITE);
					}
				} else if (calendarModel.equals(tomorrow)) {
					contextTView.setText(mTrainTommorrowTag);
					if (mTrainCalendarModel != null
							&& !mTrainCalendarModel.equals(calendarModel)) {
						contextTView.setTextColor(Color.BLACK);
					} else {
						convertView.setBackgroundColor(mBgGreenColor);
						contextTView.setTextColor(Color.WHITE);
					}
				} else if (calendarModel.equals(afterTomorrow)) {
					contextTView.setText(mTrainAfterTommorowTag);// add by lisheng end
				}else{
					contextTView.setText(String.valueOf(day));
				}
			}
			else {
				contextTView.setText(String.valueOf(day));
			}
		}
		convertView.setTag(calendarModel);
		return convertView;
	}
	
	public interface IUpdateCalendarDate{
		
		static final int UPDATE_TYPE_UPDATE = 0;

		static final int UPDATE_TYPE_UPDATE_AND_THIS = 1;

		static final int UPDATE_TYPE_NOT_UPDATE = 2;
		
		/**
		 * updateType 
		 * [0]: 需要刷新数据，不是当前月份
		 * [1]: 需要刷新数据，当前月份
		 * [2]: 不需要刷新数据
		 */
		void updateDate(int year, int month, int updateType);
	}
	
}
