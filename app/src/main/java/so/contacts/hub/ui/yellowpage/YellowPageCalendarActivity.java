package so.contacts.hub.ui.yellowpage;

import java.util.Calendar;

import android.os.Message;
import android.os.Handler;
import android.widget.Toast;
import so.contacts.hub.ui.BaseActivity;
import so.contacts.hub.ui.yellowpage.bean.CalendarBean;
import android.content.Intent;
import so.contacts.hub.util.CalendarUtil;
import so.contacts.hub.adapter.CalendarAdapter.IUpdateCalendarDate;
import so.contacts.hub.adapter.CalendarAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import so.contacts.hub.widget.MyGridView;

import so.contacts.hub.util.MobclickAgentUtil;
import com.yulong.android.contacts.discover.R;

import android.os.Bundle;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.app.Activity;

public class YellowPageCalendarActivity extends BaseActivity implements OnClickListener, OnItemClickListener, IUpdateCalendarDate {

	private ImageView mLeftImgView = null;

	private ImageView mRightImgView = null;
	
	private TextView mDateTView = null;
	
	private MyGridView mWeekTagGridView = null;
	
	private MyGridView mCalendarGridView = null;
	
	private CalendarAdapter mCalendarAdapter = null;
	
	/**
	 * 入住 或者 离店的标识
	 * [1]: 入住
	 * [2]: 离店
	 * [3]: 火车票
	 */
	private int mDateType = 0;
	
	// 上一次选择的入住日期
	private CalendarBean mLastInCalendarModel = null;
	
	// 上一次选择的离店日期
	private CalendarBean mLastOutCalendarModel = null;
	
	//上一次选择的火车票出发日期;
	private CalendarBean mLastTrainCalendarModel = null;
	
	//add by lisheng 2014-12-15 火车票 ,明天,后天
	private CalendarBean tomorrow =null;
	private CalendarBean afterTomorrow =null;
	
	//初始化今天的日期;
	private CalendarBean today =null;
	
	// 返回到订机票页面时是否需要清除离店日期
	private boolean mNeedClearOutCalendarInfo = false;
	
	private static final int MSG_SHOW_SELECTDATE_HINT_ACTION = 0x2001;
	private static final int MSG_RETURN_ACTION = 0x2003;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//putao_lhq add for COOLUI6.0 start
        /*if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			// 设置托盘透明
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		} */
        //putao_lhq add for COOLUI6.0 end
		setContentView(R.layout.putao_calendar_select_layout);
		
		parseIntent();
		initView();
	}
	
	private void parseIntent(){
		Intent intent = getIntent();
		today = getCalendarData(CalendarUtil.getNowDateStr(), CalendarUtil.getDayOfWeek());
		tomorrow = getCalendarData(CalendarUtil.getTomorrowDateStr(),CalendarUtil.getDayOfWeek());
		afterTomorrow = getCalendarData(CalendarUtil.getAppointTomorrowDate(tomorrow.getYear(), tomorrow.getMonth(), tomorrow.getDay()),CalendarUtil.getDayOfWeek());
		if( intent != null ){
			
			mDateType = intent.getIntExtra("DateType", CalendarBean.MODEL_SELECT_IN);
			
			//modify by lisheng start ;
			if(mDateType==CalendarBean.MODEL_TRAIN_START){
				mLastTrainCalendarModel =(CalendarBean) intent.getSerializableExtra("trainCalendarData");
				
			}else{
				mLastInCalendarModel = (CalendarBean) intent.getSerializableExtra("InCalendarData");
				mLastOutCalendarModel = (CalendarBean) intent.getSerializableExtra("OutCalendarData");
			}
			//modify by lisheng end;
		}
	}
	
	private void initView(){
		((TextView) findViewById(R.id.title)).setText(getResources().getString(
				R.string.putao_calendar_title));

		findViewById(R.id.back_layout).setOnClickListener(this);

		mDateTView = (TextView) findViewById(R.id.calendar_date);
		mLeftImgView = (ImageView) findViewById(R.id.calendar_left);
		mRightImgView = (ImageView) findViewById(R.id.calendar_right);
		mLeftImgView.setOnClickListener(this);
		mRightImgView.setOnClickListener(this);
		
		mWeekTagGridView = (MyGridView) findViewById(R.id.gridView_week);
		mWeekTagGridView.setAdapter(new CalendarAdapter(this, false));
		
		mCalendarGridView = (MyGridView) findViewById(R.id.gridView_calendar);
		mCalendarGridView.setOnItemClickListener(this);
		mCalendarAdapter = new CalendarAdapter(this, true);
		mCalendarAdapter.setInAndOutDate(mLastInCalendarModel, mLastOutCalendarModel);
		
		if(mDateType==CalendarBean.MODEL_TRAIN_START){
			mCalendarAdapter.setTrainStartDate(mLastTrainCalendarModel,today,tomorrow,afterTomorrow);	//modify by lisheng 2014-12-15
		}
		
		mCalendarGridView.setAdapter(mCalendarAdapter);
		mCalendarAdapter.showDate(this);
		
	}
	
	@Override
	public void onClick(View view) {
		int id = view.getId();
		if (id == R.id.back_layout) {
			finish();
		} else if (id == R.id.calendar_left) {
			mCalendarAdapter.showPreviousMonth(this);
		} else if (id == R.id.calendar_right) {
			mCalendarAdapter.showNextMonth(this);
		} else {
		}
	}

	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch(what){
				case MSG_SHOW_SELECTDATE_HINT_ACTION:
					mHandler.removeMessages(MSG_SHOW_SELECTDATE_HINT_ACTION);
					Toast.makeText(YellowPageCalendarActivity.this, R.string.putao_hotel_select_error_hint, Toast.LENGTH_SHORT).show();
					break;
				case MSG_RETURN_ACTION:
					finish();
					break;
				default:
					break;
			}
		};
	};
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
		CalendarBean selectCalendarModel = (CalendarBean) view.getTag();
		if( selectCalendarModel == null ){
			return;
		}
		int type = selectCalendarModel.getType();
		if( type == CalendarBean.MODEL_TAG_THIS ){
			if( mDateType == CalendarBean.MODEL_SELECT_IN ){
				if( mLastOutCalendarModel != null && selectCalendarModel.compareTo(mLastOutCalendarModel) >= 0 ){
					//选择的入住日期 如果大于 离店日期，则清除离店日期
					mLastOutCalendarModel = null;
					mCalendarAdapter.clearOutCalendarInfo();
					mNeedClearOutCalendarInfo = true;
				}
				mLastInCalendarModel = selectCalendarModel;
			}else if( mDateType == CalendarBean.MODEL_SELECT_OUT ){
				if( mLastInCalendarModel != null && selectCalendarModel.compareTo(mLastInCalendarModel) <= 0 ){
					//离店日期 必须大于 入住日期
					mHandler.sendEmptyMessageDelayed(MSG_SHOW_SELECTDATE_HINT_ACTION, 300);
					return;
				}
				mLastOutCalendarModel = selectCalendarModel;
			}
			mCalendarAdapter.setCalendarInfo(position, mDateType);
			Intent intent = new Intent();
			if( mNeedClearOutCalendarInfo ){
				// 返回到订机票页面时清除离店日期
				intent.putExtra("NeedClearOutCalendar", true);
			}
			intent.putExtra("SelectCalendar", selectCalendarModel);
			setResult(RESULT_OK, intent);
			mHandler.sendEmptyMessageDelayed(MSG_RETURN_ACTION, 300);
		}else if( type == CalendarBean.MODEL_TAG_NEXT ){
			mCalendarAdapter.showNextMonth(this);
		}
	}

	@Override
	public void updateDate(int year, int month, int updateType) {
		mDateTView.setText(String.format(getResources().getString(R.string.putao_calendar_showdate), year, 
				CalendarUtil.getFormatTwoDecimal(month)));
		if( updateType == IUpdateCalendarDate.UPDATE_TYPE_UPDATE ){
			mLeftImgView.setVisibility(View.VISIBLE);
		}else if( updateType == IUpdateCalendarDate.UPDATE_TYPE_UPDATE_AND_THIS ){
			mLeftImgView.setVisibility(View.INVISIBLE);
		}else if( updateType == IUpdateCalendarDate.UPDATE_TYPE_NOT_UPDATE ){
			mLeftImgView.setVisibility(View.INVISIBLE);
		}
	}
	
	@Override
	protected void onResume() {
		MobclickAgentUtil.onResume(this);
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		MobclickAgentUtil.onPause(this);
		super.onPause();
	}
	
	//生成日历实例;
	private CalendarBean getCalendarData(String date, int week){
		//TODO
		String[] strList = date.split(CalendarUtil.DATE_FORMATTER_GAP);
		if(strList == null ){
			return null;
		}
		CalendarBean calendarBean = new CalendarBean();
		if( strList.length == CalendarUtil.DATE_FORMATTER_NUM ){
			try{
				calendarBean.setYear(Integer.parseInt(strList[0]));
				calendarBean.setMonth(Integer.parseInt(strList[1]));
				calendarBean.setDay(Integer.parseInt(strList[2]));
			}catch(Exception e){
				
			}
		}
		return calendarBean;
	}
	
	
	/*修复bug 3291,3292 add by ls 2015-03-05 start*/
	@Override
	protected boolean needReset() {
		return true;
	}
	/*add by ls end*/
	
}
