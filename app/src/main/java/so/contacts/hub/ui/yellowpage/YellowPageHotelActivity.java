package so.contacts.hub.ui.yellowpage;

import com.yulong.android.contacts.discover.R;

import so.contacts.hub.ad.AdCode;
import so.contacts.hub.city.CityListDB;
import so.contacts.hub.remind.BaseRemindActivity;
import android.content.Context;
import android.content.SharedPreferences;
import so.contacts.hub.thirdparty.tongcheng.ui.YellowPageHotelListActivity;
import so.contacts.hub.util.ContactsAppUtils;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.NetUtil;
import so.contacts.hub.util.UMengEventIds;
import so.contacts.hub.util.Utils;
import so.putao.findplug.LBSServiceGaode;
import so.putao.findplug.LBSServiceGaode.LBSServiceListener;
import android.widget.EditText;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import so.contacts.hub.widget.CommonDialog;
import so.contacts.hub.widget.CommonDialogFactory;
import so.contacts.hub.ui.yellowpage.YellowPageCalendarActivity;
import so.contacts.hub.ui.yellowpage.YellowPageCitySelectActivity;
import so.contacts.hub.ui.yellowpage.bean.CalendarBean;
import so.contacts.hub.util.CalendarUtil;
import android.text.TextUtils;
import android.os.Message;
import android.os.Handler;
import android.content.Intent;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import so.contacts.hub.util.MobclickAgentUtil;
import android.os.Bundle;

public class YellowPageHotelActivity extends BaseRemindActivity implements
		OnClickListener, LBSServiceListener, OnKeyListener {

	private static final String TAG = "YellowPageHotelActivity";
	
	private LinearLayout mCityLayout = null;

	private TextView mCityTView = null;

	private LinearLayout mInDateLayout = null;

	private TextView mInDateTView = null;
	
	private TextView mInDateInfoTView = null;

	private LinearLayout mOutDateLayout = null;

	private TextView mOutDateTView = null;

	private TextView mOutDateInfoTView = null;

	private LinearLayout mPriceLayout = null;

	private TextView mPriceTView = null;

	private LinearLayout mStarLayout = null;

	private TextView mStarTView = null;

	private EditText mHotwordEditText = null;
	
	private RelativeLayout mSearchLayout = null;
	
	// 价格列表
	private String[] mHotelPriceList = null;

	// 星级列表
	private String[] mHotelStarList = null;
	
	// 选择城市
	private String mSelectCity = "";
	
	// 选择城市ID
	private String mSelectCityId = "";
	
	// 定位城市
	private String mLocationCity = "";
	
	// 定位的维度
	private double mLatitude = 0;
	
	// 定位的经度
	private double mLongitude = 0;
	
	// 选择的价格
	private String mSelectPrice = "";
	
	// 选择的酒店星级
	private String mSelectStar = "";
	
	// 选择的酒店星级等级
	private int mSelectStarPos = 0;
	
	// 选择的实际价格
	private int mSelectPricelPos = 0;
	
	/**
	 * [1]: 选择价格
	 * [2]: 选择酒店星级
	 */
	private int mDialogType = 0;
	
	private static final int DIALOG_TYPE_PRICE = 1;
	
	private static final int DIALOG_TYPE_STAR = 2;
	
	// 弹出的选择框
	private CommonDialog mCommonDialog = null;
	
	// 入住日期
	private CalendarBean mInCalendarModel = null;
	
	// 离店日期
	private CalendarBean mOutCalendarModel = null;
	
	// 星期中文列表： 周日，周一...
	private String[] mWeekTagList = null;
	
	private static final int MSG_UPDATE_CITY_ACTION = 0x2001;

	private static final int MSG_UPDATE_DATE_IN_ACTION = 0x2002;
	
	private static final int MSG_UPDATE_DATE_OUT_ACTION = 0x2003;

	private static final int MSG_REUPDATE_DATE_OUT_ACTION = 0x2004;
	
	// 选择城市列表 返回Code
	private static final int REQUEST_CODE_CITY = 100;
	
	// 入住时间选择 返回Code
	private static final int REQUEST_CODE_DATE_IN = 101;
	
	// 离店时间选择 返回Code
	private static final int REQUEST_CODE_DATE_OUT = 102;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.putao_hotel_layout);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		
		initView();
		initData();
		doLocationActivate();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgentUtil.onResume(this);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgentUtil.onPause(this);
	}

	private void initView() {
		if( TextUtils.isEmpty(mTitleContent) ){
        	mTitleContent = getResources().getString(R.string.putao_query_telcharge_hint_head);
        }
        ((TextView)findViewById(R.id.title)).setText(mTitleContent);
		findViewById(R.id.back_layout).setOnClickListener(this);

		mCityLayout = (LinearLayout) findViewById(R.id.hotel_city_layout);
		mInDateLayout = (LinearLayout) findViewById(R.id.hotel_in_layout);
		mOutDateLayout = (LinearLayout) findViewById(R.id.hotel_out_layout);
		mPriceLayout = (LinearLayout) findViewById(R.id.hotel_price_layout);
		mStarLayout = (LinearLayout) findViewById(R.id.hotel_star_layout);

		mCityTView = (TextView) findViewById(R.id.hotel_city);
		mInDateTView = (TextView) findViewById(R.id.hotel_in_date);
		mInDateInfoTView = (TextView) findViewById(R.id.hotel_in_date_info);
		mOutDateTView = (TextView) findViewById(R.id.hotel_out_date);
		mOutDateInfoTView = (TextView) findViewById(R.id.hotel_out_date_info);
		mPriceTView = (TextView) findViewById(R.id.hotel_price);
		mStarTView = (TextView) findViewById(R.id.hotel_star);
		mHotwordEditText = (EditText) findViewById(R.id.hotel_hotword);
		mSearchLayout = (RelativeLayout) findViewById(R.id.hotel_query_layout);

		mCityLayout.setOnClickListener(this);
		mInDateLayout.setOnClickListener(this);
		mOutDateLayout.setOnClickListener(this);
		mPriceLayout.setOnClickListener(this);
		mStarLayout.setOnClickListener(this);
		mSearchLayout.setOnClickListener(this);
		
		mCommonDialog = CommonDialogFactory.getListCommonDialog(this);
		mCommonDialog.setListViewItemClickListener(mOnDialogItemClickListener);
		// add by putao_lhq 2014年11月5日 for 
		mHotwordEditText.setOnKeyListener(this);
	}
	
	private void initData(){
		mHotelPriceList = getResources().getStringArray(R.array.putao_hotel_price);
		mHotelStarList = getResources().getStringArray(R.array.putao_hotel_star);
		
		// 初始化位置信息
		SharedPreferences preferences = getSharedPreferences("location", Context.MODE_MULTI_PROCESS);
		try{
			String latitude = preferences.getString("latitude","");
			String longitude = preferences.getString("longitude","");
			mLocationCity = preferences.getString("city","");
			mSelectCity = mLocationCity;
			if( !TextUtils.isEmpty(mSelectCity) ){
				mHandler.sendEmptyMessage(MSG_UPDATE_CITY_ACTION);
			} 
			if( !TextUtils.isEmpty(latitude) && !TextUtils.isEmpty(longitude) ){
				mLatitude = Double.parseDouble(latitude);
				mLongitude = Double.parseDouble(longitude);
			}
		}catch(Exception e){
		}
		
		mWeekTagList = getResources().getStringArray(R.array.putao_week_list);
		
		// 初始化今天的信息
		String todayDate = CalendarUtil.getNowDateStr();
		if( !TextUtils.isEmpty(todayDate) ){
			mInCalendarModel = getCalendarData(todayDate, CalendarUtil.getDayOfWeek());
			showDateInContext(mInCalendarModel);
		}
		
		// 初始化明天的信息
		String tomorrowDate = CalendarUtil.getTomorrowDateStr();
		if( !TextUtils.isEmpty(tomorrowDate) ){
			mOutCalendarModel = getCalendarData(tomorrowDate, CalendarUtil.getWeekNumByDate(tomorrowDate));
			showDateOutContext(mOutCalendarModel);
		}
	}
	
	/**
	 * 生成日历项实例
	 * @param date 如："2014-09-22"
	 */
	private CalendarBean getCalendarData(String date, int week){
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
		if( week >= 0 && week < CalendarUtil.WEEK_NUM ){
			calendarBean.setWeekInfo(mWeekTagList[week]);
		}
		return calendarBean;
	}
	
	/**
	 * 定位
	 */
	private void doLocationActivate(){
		if (NetUtil.isNetworkAvailable(this)) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					LBSServiceGaode.process_activate(YellowPageHotelActivity.this,
							YellowPageHotelActivity.this);
				}
			}).start();
		}
	}
	
	/**
	 * 显示入住时间
	 */
	private void showDateInContext(CalendarBean calendarModel){
		if( calendarModel == null ){
			return;
		}
		mInCalendarModel = calendarModel;
		int inDateYear = mInCalendarModel.getYear();
		int inDateMonth = mInCalendarModel.getMonth();
		int inDateDay = mInCalendarModel.getDay();
		if( inDateYear == CalendarUtil.getYear() && inDateMonth == CalendarUtil.getMonth() && inDateDay == CalendarUtil.getDayIndexOfMonth()){
			// 是今天
			mInDateInfoTView.setVisibility(View.VISIBLE);
			mInDateInfoTView.setText(getResources().getString(R.string.putao_calendar_date_today));
		}else{
			// 不是今天，则显示星期
			String weekInfo = mInCalendarModel.getWeekInfo();
			if( TextUtils.isEmpty(weekInfo) ){
				mInDateInfoTView.setVisibility(View.VISIBLE);
			}else{
				mInDateInfoTView.setText(weekInfo);
				mInDateInfoTView.setVisibility(View.VISIBLE);
			}
		}
		
		mInDateTView.setText(String.format(getResources().getString(R.string.putao_calendar_showdate_month), 
				CalendarUtil.getFormatTwoDecimal(inDateMonth), CalendarUtil.getFormatTwoDecimal(inDateDay)));
	}

	/**
	 * 显示离店时间
	 */
	private void showDateOutContext(CalendarBean calendarModel){
		if( calendarModel == null ){
			return;
		}
		
		boolean isTomorrow = false;
		long day = CalendarUtil.getGapBetweenTwoDay(CalendarUtil.getNowDateStr(), calendarModel.toString());
		if( day == 1 ){
			isTomorrow = true;
		}
		
		mOutCalendarModel = calendarModel;
		int outDateMonth = mOutCalendarModel.getMonth();
		int outDateDay = mOutCalendarModel.getDay();
		if( isTomorrow ){
			// 是明天
			mOutDateInfoTView.setVisibility(View.VISIBLE);
			mOutDateInfoTView.setText(getResources().getString(R.string.putao_calendar_date_tomorrow));
		}else{
			// 不是明天，则显示星期
			String weekInfo = mOutCalendarModel.getWeekInfo();
			if( TextUtils.isEmpty(weekInfo) ){
				mOutDateInfoTView.setVisibility(View.VISIBLE);
			}else{
				mOutDateInfoTView.setText(weekInfo);
				mOutDateInfoTView.setVisibility(View.VISIBLE);
			}
		}
		mOutDateTView.setText(String.format(getResources().getString(R.string.putao_calendar_showdate_month), 
				CalendarUtil.getFormatTwoDecimal(outDateMonth), CalendarUtil.getFormatTwoDecimal(outDateDay)));
	}
	
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch(msg.what){
			case MSG_UPDATE_CITY_ACTION:
				// 更新城市
				if( !TextUtils.isEmpty(mSelectCity) ){
					mCityTView.setText(mSelectCity);
				}
				break;
			case MSG_UPDATE_DATE_IN_ACTION:
				// 入住日期
				CalendarBean inModel = (CalendarBean) msg.obj;
				if( inModel != null ){
					showDateInContext(inModel);
				}
				break;
			case MSG_UPDATE_DATE_OUT_ACTION:
				// 离店日期
				CalendarBean outModel = (CalendarBean) msg.obj;
				showDateOutContext(outModel);
				break;
			case MSG_REUPDATE_DATE_OUT_ACTION:
				// 清除了之前的离店日期，根据入住日期生成明天的日期
				mOutCalendarModel = null;
				CalendarBean reOutModel = (CalendarBean) msg.obj;
				if( reOutModel == null ){
					mOutDateTView.setText("");
					mOutDateInfoTView.setText("");
				}else{
					String tomorrowDate = CalendarUtil.getAppointTomorrowDate(reOutModel.getYear(), reOutModel.getMonth(), reOutModel.getDay());
					mOutCalendarModel = getCalendarData(tomorrowDate, CalendarUtil.getWeekNumByDate(tomorrowDate));
					showDateOutContext(mOutCalendarModel);
				}
				break;
			default:
				break;
			}
		};
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if( data == null ){
			return;
		}
		if( resultCode != RESULT_OK ){
			return;
		}
		if( requestCode == REQUEST_CODE_CITY ){
			// 城市选择
			mSelectCity = data.getStringExtra("cityName");
			mSelectCityId = data.getStringExtra("cityId");
			mHandler.sendEmptyMessage(MSG_UPDATE_CITY_ACTION);
		}else if( requestCode == REQUEST_CODE_DATE_IN ){
			// 入住日期 选择
			Object obj = null;
			try{
				obj = data.getSerializableExtra("SelectCalendar");
			}catch(Exception e){
				obj = null;
			}
			if( obj == null ){
				return;
			}
			// 更新入住时间
			CalendarBean calendarModel = (CalendarBean)obj ;
			Message msg = mHandler.obtainMessage();
			msg.what = MSG_UPDATE_DATE_IN_ACTION;
			msg.obj = calendarModel;
			mHandler.sendMessage(msg);
			
			boolean needClearOutCalendar = data.getBooleanExtra("NeedClearOutCalendar", false);
			if( needClearOutCalendar ){
				// 更新离店时间
				msg = mHandler.obtainMessage();
				msg.what = MSG_REUPDATE_DATE_OUT_ACTION;
				msg.obj = calendarModel;
				mHandler.sendMessage(msg);
			}
		}else if( requestCode == REQUEST_CODE_DATE_OUT ){
			// 离店日期 选择
			Object obj = null;
			try{
				obj = data.getSerializableExtra("SelectCalendar");
			}catch(Exception e){
				obj = null;
			}
			if( obj == null ){
				return;
			}
			CalendarBean calendarModel = (CalendarBean)obj ;
			Message msg = mHandler.obtainMessage();
			msg.what = MSG_UPDATE_DATE_OUT_ACTION;
			msg.obj = calendarModel;
			mHandler.sendMessage(msg);
		}
	}
	
	/**
	 * 显示价格列表
	 */
	private void showPriceLayout(){
		if( mCommonDialog != null ){
			mCommonDialog.setTitle(getResources().getString(R.string.putao_hotel_title_price));
			/**
			 * modify by putao_lhq
			 * old code:
			mCommonDialog.setListViewDatas(mHotelPriceList);*/
			mCommonDialog.setSingleChoiceListViewDatas(mHotelPriceList);
			mCommonDialog.getListView().setItemChecked(mSelectPricelPos, true);
			/*@end*/
		}
		mDialogType = DIALOG_TYPE_PRICE;
		mCommonDialog.show();
	}

	/**
	 * 显示星级
	 */
	private void showStarLayout(){
		if( mCommonDialog != null ){
			mCommonDialog.setTitle(getResources().getString(R.string.putao_hotel_title_star));
			/**
			 * modify by putao_lhq
			 * @start
			 * mCommonDialog.setListViewDatas(mHotelStarList);*/
			mCommonDialog.setSingleChoiceListViewDatas(mHotelStarList);
			mCommonDialog.getListView().setItemChecked(mSelectStarPos, true);
			/*@end*/
		}
		mDialogType = DIALOG_TYPE_STAR;
		mCommonDialog.show();
	}
	
	/**
	 * 开始搜索
	 */
	private void doSearchHotel(){
		if( !NetUtil.isNetworkAvailable(this) ){ //网络异常
			Utils.showToast(this, R.string.putao_no_net, false);
			return;
		}
		String inDate = "";
		if( mInCalendarModel != null ){
			inDate = mInCalendarModel.getFormatStr();
		}
		String outDate = "";
		if( mOutCalendarModel != null ){
			outDate = mOutCalendarModel.getFormatStr();
		}
		String hotword = mHotwordEditText.getText().toString();
		if( !TextUtils.isEmpty(hotword) ){
			MobclickAgentUtil.onEvent(this, UMengEventIds.DISCOVER_YELLOWPAGE_HOTEL_SEARCH_KEYWORD);
		}
		
		if( TextUtils.isEmpty(mSelectCityId) ){
			mSelectCityId = ContactsAppUtils.getInstance().getDatabaseHelper().getCityListDB().getCityIdByName(mSelectCity, CityListDB.CITY_SOURCE_TYPE_TONGCHENG);
		}
		
		Intent intent = new Intent(this, YellowPageHotelListActivity.class);
		intent.putExtra("CityId", mSelectCityId); 
		intent.putExtra("CityName", mSelectCity); 
		intent.putExtra("ComeDate", inDate);
		intent.putExtra("LeaveDate", outDate);
		intent.putExtra("ClintIp", "192.168.1.108");
		if( !TextUtils.isEmpty(mLocationCity) && mLocationCity.equals(mSelectCity) ){
			// 选择的城市 与 定位城市 相同，才需要定位
			intent.putExtra("Latitude", mLatitude);
			intent.putExtra("Longitude", mLongitude);
		}
		intent.putExtra("PriceRangeIndex", mSelectPricelPos);
		intent.putExtra("StarIndex", mSelectStarPos);
		intent.putExtra("HotWord", mHotwordEditText.getText().toString());
		startActivity(intent);
	}
	
	private OnItemClickListener mOnDialogItemClickListener = new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long arg3) {
			// TODO Auto-generated method stub
			if( mDialogType == DIALOG_TYPE_PRICE ){
				if( position <= mHotelPriceList.length - 1 ){
					mSelectPrice = mHotelPriceList[position];
					mPriceTView.setText(mSelectPrice);
					mSelectPricelPos = position;
				}
			}else if( mDialogType == DIALOG_TYPE_STAR ){
				if( position <= mHotelStarList.length - 1 ){
					mSelectStar = mHotelStarList[position];
					mStarTView.setText(mSelectStar);
					mSelectStarPos = position;
				}
			}
			if( mCommonDialog != null ){
				mCommonDialog.dismiss();
			}
		}
		
	};

	
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		Intent intent = null;
		int id = view.getId();
		if (id == R.id.back_layout) {
			finish();
		} else if (id == R.id.hotel_city_layout) {
			MobclickAgentUtil.onEvent(this, UMengEventIds.DISCOVER_YELLOWPAGE_HOTEL_CITY_CHOOSE);
			intent = new Intent(YellowPageHotelActivity.this, YellowPageCitySelectActivity.class);
			intent.putExtra("source_type", CityListDB.CITY_SOURCE_TYPE_TONGCHENG);
			startActivityForResult(intent, REQUEST_CODE_CITY);
		} else if (id == R.id.hotel_in_layout) {
			MobclickAgentUtil.onEvent(this, UMengEventIds.DISCOVER_YELLOWPAGE_HOTEL_DATE_IN_CLICK);
			intent = new Intent(YellowPageHotelActivity.this, YellowPageCalendarActivity.class);
			intent.putExtra("DateType", CalendarBean.MODEL_SELECT_IN); // 入住
			intent.putExtra("InCalendarData", mInCalendarModel);
			intent.putExtra("OutCalendarData", mOutCalendarModel);
			startActivityForResult(intent, REQUEST_CODE_DATE_IN);
		} else if (id == R.id.hotel_out_layout) {
			MobclickAgentUtil.onEvent(this, UMengEventIds.DISCOVER_YELLOWPAGE_HOTEL_DATE_OUT_CLICK);
			intent = new Intent(YellowPageHotelActivity.this, YellowPageCalendarActivity.class);
			intent.putExtra("DateType", CalendarBean.MODEL_SELECT_OUT); // 离店
			intent.putExtra("InCalendarData", mInCalendarModel);
			intent.putExtra("OutCalendarData", mOutCalendarModel);
			startActivityForResult(intent, REQUEST_CODE_DATE_OUT);
		} else if (id == R.id.hotel_price_layout) {
			MobclickAgentUtil.onEvent(this, UMengEventIds.DISCOVER_YELLOWPAGE_HOTEL_PRICE_CHOOS);
			showPriceLayout();
		} else if (id == R.id.hotel_star_layout) {
			MobclickAgentUtil.onEvent(this, UMengEventIds.DISCOVER_YELLOWPAGE_HOTEL_STARS_CHOOSE);
			showStarLayout();
		} else if (id == R.id.hotel_query_layout) {
			MobclickAgentUtil.onEvent(this, UMengEventIds.DISCOVER_YELLOWPAGE_HOTEL_SEARCH);
			doSearchHotel();
		} else {
		}
	}

	@Override
	public void onLocationChanged(String city, double latitude,
			double longitude, long time) {
		// TODO Auto-generated method stub
		LogUtil.d(TAG, "location city: " + city + " ,latitude: " + latitude + " ,longitude: " + longitude + " ,time:  " + time);
		if( !TextUtils.isEmpty(city) ){
			mSelectCity = city;
			mLocationCity = city;
			mHandler.sendEmptyMessage(MSG_UPDATE_CITY_ACTION);
		}
		mLatitude = latitude;
		mLongitude = longitude;
		LBSServiceGaode.deactivate();
	}

	@Override
	public void onLocationFailed() {
		// TODO Auto-generated method stub
		LogUtil.d(TAG, "location failed.");
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		LBSServiceGaode.deactivate();
	}

	@Override
	public String getServiceNameByUrl() {
		// TODO Auto-generated method stub
		return this.getClass().getName();
	}

	@Override
	public Integer remindCode() {
		// TODO Auto-generated method stub
		return mRemindCode;
	}

	@Override
	public String getServiceName() {
		return this.getClass().getName();
	}

	@Override
	public boolean needMatchExpandParam() {
		// TODO Auto-generated method stub
		return false;
	}

	// add by putao_lhq 2014年11月5日
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
			((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(YellowPageHotelActivity.this
							.getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
			doSearchHotel();
		}
		return false;
	}

	@Override
	public Integer getAdId() {
	    return AdCode.ADCODE_YellowPageHotelActivity;
	}
}
