package so.contacts.hub.train;

import java.util.HashMap;
import java.util.Map;

import so.contacts.hub.account.PutaoAccount;
import so.contacts.hub.ad.AdCode;
import so.contacts.hub.remind.BaseRemindActivity;
import so.contacts.hub.ui.yellowpage.YellowPageCalendarActivity;
import so.contacts.hub.ui.yellowpage.YellowPageCitySelectActivity;
import so.contacts.hub.ui.yellowpage.YellowPageJumpH5Activity;
import so.contacts.hub.ui.yellowpage.bean.CalendarBean;
import so.contacts.hub.ui.yellowpage.bean.YellowParams;
import so.contacts.hub.util.CalendarUtil;
import so.contacts.hub.util.ContactsAppUtils;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.MyCenterConstant;
import so.contacts.hub.util.NetUtil;
import so.contacts.hub.util.UMengEventIds;
import so.contacts.hub.util.URLUtil;
import so.contacts.hub.util.YellowUtil;
import so.contacts.hub.widget.CommonDialog;
import so.contacts.hub.widget.CommonDialogFactory;
import so.putao.findplug.LBSServiceGaode;
import so.putao.findplug.LBSServiceGaode.LBSServiceListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yulong.android.contacts.discover.R;

/**
 * add by lisheng 2014-11-21 14:13:33 同城火车票首页
 * 
 */

public class YellowPageBookTrainTicketActivity extends BaseRemindActivity
		implements OnClickListener, LBSServiceListener, OnKeyListener {
	private static final int MSG_UPDATE_STARTCITY_ACTION = 0x100;
	private static final int MSG_UPDATE_END_ACTION = 0x101;
	private static final String TAG = YellowPageBookTrainTicketActivity.class.getSimpleName();
	private static final int REQUEST_START_CITY_CODE = 0x102;
	private static final int REQUEST_END_CITY_CODE = 0x103;
	private static final int REQUEST_CODE_TRAIN_DATE = 0x104;
	private static final int MSG_UPDATE_TRAINDATE_CODE = 0x105;
	private OnItemClickListener mOnDialogItemClickListener;
	// 弹出的选择框
	private CommonDialog mCommonDialog = null;
	private String[] mHotStationList = null;
	
	private int DIALOG_FROM =0;//标记从那里弹出dialog
	private GridView gv;//对话框里面的gv

	private LinearLayout head_layout = null;
	private TextView title;
	private TextView next_step_btn;
	protected YellowParams mYellowParams = null;

	private TextView tv_station_start;
	private TextView tv_station_end;
	private TextView tv_triket_start_date;
	private TextView tv_triket_start_dateinfo;
	// 星期中文列表： 周日，周一...
	private String[] mWeekTagList = null;
	private TextView bt_edit_travellerinfo;// 测试用
	private TextView bt_select_traveller;// 测试用
	
	private TextView showMoreStation;

	private RelativeLayout station_start_layout;
	private RelativeLayout station_end_layout;
	private LinearLayout start_date_layout;
	private String stationTitle ="";

	// 出发日期
	private CalendarBean mCalendarModel = null;
	// 出发城市
	private String mStartCity = "";
	// 到达城市
	private String mEndCity = "";
	// 定位城市
	private String mLocationCity = "";
	// 定位的纬度
	private double mLatitude = 0;
	// 定位的经度
	private double mLongitude = 0;

	private String startDate = "";

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_UPDATE_STARTCITY_ACTION:
				tv_station_start.setText(mStartCity);
				break;
			case MSG_UPDATE_END_ACTION:
				tv_station_end.setText(mEndCity);
				break;
			case MSG_UPDATE_TRAINDATE_CODE:
				CalendarBean trainModel = (CalendarBean) msg.obj;
				if (trainModel != null) {
					startDate = trainModel.toString();
					showDateInContext(trainModel);
				}
				break;
			default:
				break;
			}
		};
	};

	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.putao_train_book_ticket_layout);
		initViews();
		initData();
		doLocationActivate();
	}

	private void initViews() {
		title = (TextView) findViewById(R.id.title);

		// 测试
		title.setText(getResources().getString(R.string.putao_traintriket));
		/*
		 * modify by putao_lhq @start
		 * 改为图片形式
		 * old code:
		next_step_btn = (TextView) findViewById(R.id.next_step_btn);
		next_step_btn.setText(getResources().getString(
				R.string.putao_traintriket_orderhistory));
		next_step_btn.setVisibility(View.VISIBLE);*/
		ImageView img = (ImageView)findViewById(R.id.next_step_img);
		img.setImageResource(R.drawable.putao_icon_title_ls);
		img.setVisibility(View.VISIBLE);
		/*@end by putao_lhq*/
		station_start_layout = (RelativeLayout) findViewById(R.id.station_start_layout);
		station_end_layout = (RelativeLayout) findViewById(R.id.station_end_layout);
		start_date_layout = (LinearLayout) findViewById(R.id.start_date_layout);
		tv_station_start = (TextView) findViewById(R.id.tv_station_start);
		tv_station_end = (TextView) findViewById(R.id.tv_station_end);
		tv_triket_start_date = (TextView) findViewById(R.id.tv_triket_start_date);
		tv_triket_start_dateinfo = (TextView) findViewById(R.id.tv_triket_start_dateinfo);
		

		// 测试用
		bt_edit_travellerinfo = (TextView) findViewById(R.id.bt_add_address);
		bt_select_traveller = (TextView) findViewById(R.id.bt_select_often_address);
		bt_edit_travellerinfo.setOnClickListener(this);
		bt_select_traveller.setOnClickListener(this);
		// 测试用
		
		station_start_layout.setOnClickListener(this);
		station_end_layout.setOnClickListener(this);
		start_date_layout.setOnClickListener(this);
		findViewById(R.id.swap).setOnClickListener(this);
		findViewById(R.id.bt_train_query_btn).setOnClickListener(this);
		findViewById(R.id.back_layout).setOnClickListener(this);
		findViewById(R.id.next_setp_layout).setOnClickListener(this);
	}

	private void initData() {
		initListener();
		stationTitle = getResources().getString(R.string.putao_train_title);
		SharedPreferences sp = getSharedPreferences("last_railwaystation", Context.MODE_PRIVATE);
		mEndCity=sp.getString("arrived_station", "");
		tv_station_end.setText(mEndCity);
		
		
		mWeekTagList = getResources().getStringArray(R.array.putao_week_list);
		mHotStationList =getResources().getStringArray(R.array.putao_hot_station_list);
		mCommonDialog = CommonDialogFactory.getGridCommonDialog(this);
		mCommonDialog.setGridViewItemClickListener(mOnDialogItemClickListener);
		showMoreStation =mCommonDialog.getMoreStation();
		showMoreStation.setOnClickListener(this);

		// 初始化出发地点
		SharedPreferences preferences = getSharedPreferences("location",
				Context.MODE_MULTI_PROCESS);
		try {
			String latitude = preferences.getString("latitude", "");
			String longitude = preferences.getString("longitude", "");
			mLocationCity = preferences.getString("city", "");
			LogUtil.i("YellowPageBookTrainTicketActivity", "mLocationCity="+mLocationCity);
			if (!TextUtils.isEmpty(mStartCity)) {
				mStartCity = mLocationCity.substring(0, mLocationCity.length() - 1);
				mHandler.sendEmptyMessage(MSG_UPDATE_STARTCITY_ACTION);
			}
			if (!TextUtils.isEmpty(latitude) && !TextUtils.isEmpty(longitude)) {
				mLatitude = Double.parseDouble(latitude);
				mLongitude = Double.parseDouble(longitude);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 初始化出发日期,默认为当前日期的第二天;
		String tommorrowDate = CalendarUtil.getTomorrowDateStr();
		if (!TextUtils.isEmpty(tommorrowDate)) {
			mCalendarModel = getCalendarData(tommorrowDate,
					CalendarUtil.getDayOfWeek());
			showDateInContext(mCalendarModel);
		}
		startDate =tommorrowDate;

	}

	private void initListener() {
		mOnDialogItemClickListener = new OnItemClickListener() {
			Drawable drawable = getApplication().getResources().getDrawable(R.drawable.putao_click_btn_bg);
			@SuppressLint("NewApi")
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long arg3) {
				view.setBackground(drawable);
				((TextView)view).setTextColor(0xffffff);
				if(1==DIALOG_FROM){
					mStartCity = ((TextView)view).getText().toString();
					tv_station_start.setText(mStartCity);
				}else if(2==DIALOG_FROM){
					mEndCity = ((TextView)view).getText().toString();
					tv_station_end.setText(mEndCity);
					setLastStation(mEndCity);
				}
				if (mCommonDialog != null) {
					mCommonDialog.dismiss();
				}
				
			}

		};
	}

	private void showDateInContext(CalendarBean calendarModel) {

		if (calendarModel == null) {
			return;
		}
		int inDateYear = calendarModel.getYear();
		int inDateMonth = calendarModel.getMonth();
		int inDateDay = calendarModel.getDay();
		if (inDateYear == CalendarUtil.getYear()
				&& inDateMonth == CalendarUtil.getMonth()
				&& inDateDay == CalendarUtil.getDayIndexOfMonth()+1) {
			// 是明天 modify 2014-12-15 by lisheng
			tv_triket_start_dateinfo.setVisibility(View.VISIBLE);
			tv_triket_start_dateinfo.setText(getResources().getString(
					R.string.putao_calendar_date_tomorrow));
		} else {
			// 不是，则显示星期
			String weekInfo = calendarModel.getWeekInfo();
			if (TextUtils.isEmpty(weekInfo)) {
				tv_triket_start_dateinfo.setVisibility(View.VISIBLE);
			} else {
				tv_triket_start_dateinfo.setText(weekInfo);
				tv_triket_start_dateinfo.setVisibility(View.VISIBLE);
			}
		}

		tv_triket_start_date.setText(String.format(
				getResources()
						.getString(R.string.putao_calendar_showdate_month),
				CalendarUtil.getFormatTwoDecimal(inDateMonth), CalendarUtil
						.getFormatTwoDecimal(inDateDay)));
	}

	/**
	 * 生成日历项实例
	 * 
	 * @param date
	 *            如："2014-09-22"
	 */
	private CalendarBean getCalendarData(String date, int week) {
		String[] strList = date.split(CalendarUtil.DATE_FORMATTER_GAP);
		if (strList == null) {
			return null;
		}
		CalendarBean calendarBean = new CalendarBean();
		if (strList.length == CalendarUtil.DATE_FORMATTER_NUM) {
			try {
				calendarBean.setYear(Integer.parseInt(strList[0]));
				calendarBean.setMonth(Integer.parseInt(strList[1]));
				calendarBean.setDay(Integer.parseInt(strList[2]));
			} catch (Exception e) {
			}
		}
		if (week >= 0 && week < CalendarUtil.WEEK_NUM) {
			calendarBean.setWeekInfo(mWeekTagList[week]);
		}
		return calendarBean;
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgentUtil.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgentUtil.onResume(this);
	}

	private void doLocationActivate() {
		if (NetUtil.isNetworkAvailable(this)) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					LBSServiceGaode.process_activate(
							YellowPageBookTrainTicketActivity.this,
							YellowPageBookTrainTicketActivity.this);
				}
			}).start();
		}
	}

	@Override
	public void onLocationChanged(String city, double latitude,
			double longitude, long time) {
		LogUtil.d(TAG, "location city: " + city + " ,latitude: " + latitude
				+ " ,longitude: " + longitude + " ,time:  " + time);
		if (!TextUtils.isEmpty(city)) {
			mStartCity = city;
			mLocationCity = city;
			mHandler.sendEmptyMessage(MSG_UPDATE_STARTCITY_ACTION);
		}
		mLatitude = latitude;
		mLongitude = longitude;
		LBSServiceGaode.deactivate();
	}

	@Override
	public void onLocationFailed() {
		LogUtil.d(TAG, "location failed.");
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		int id = v.getId();
		if (id == R.id.bt_train_query_btn) {
			MobclickAgentUtil.onEvent(this, UMengEventIds.DISCOVER_YELLOWPAGE_TONGCHENG_TRAIN_QUERY);
			doQuery(intent);
		} else if (id == R.id.station_start_layout) {
			DIALOG_FROM =1;
			MobclickAgentUtil.onEvent(this, UMengEventIds.DISCOVER_YELLOWPAGE_TONGCHENG_SELECT_DEPARTSTATION);
			showHotStationLayout();
		} else if (id == R.id.station_end_layout) {
			DIALOG_FROM =2;
			MobclickAgentUtil.onEvent(this, UMengEventIds.DISCOVER_YELLOWPAGE_TONGCHENG_SELECT_ARRIVEDSTATION);
			showHotStationLayout();
		} else if (id == R.id.start_date_layout) {
			MobclickAgentUtil.onEvent(this, UMengEventIds.DISCOVER_YELLOWPAGE_TONGCHENG_SELECT_DATE);
			intent = new Intent(YellowPageBookTrainTicketActivity.this,
					YellowPageCalendarActivity.class);
			intent.putExtra("DateType", CalendarBean.MODEL_TRAIN_START); //
			intent.putExtra("trainCalendarData", mCalendarModel);
			startActivityForResult(intent, REQUEST_CODE_TRAIN_DATE);
			

		} else if (id == R.id.swap) {
			if (TextUtils.isEmpty(mEndCity)) {
				Toast.makeText(this,getResources().getString(R.string.putao_traintriket_noarrivecity), 0).show();
				return;
			}
			if (mEndCity.equals(mStartCity)) {
				Toast.makeText(this,getResources().getString(R.string.putao_traintriket_same_err), 0).show();
				return;
			}
			MobclickAgentUtil.onEvent(this, UMengEventIds.DISCOVER_YELLOWPAGE_TONGCHENG_SWAP_STATION);
			mStartCity=tv_station_start.getText().toString();
			String temp = mStartCity;
			mStartCity = mEndCity;
			mEndCity = temp;
			tv_station_start.setText(mStartCity);
			tv_station_end.setText(mEndCity);
		}

		// 以下测试用:
		else if (id == R.id.bt_add_address) {
			intent = new Intent(this, YellowPagePostAddressActivity.class);
			startActivity(intent);
		} else if (id == R.id.bt_select_often_address) {
			 intent = new Intent(this,YellowPageTravellerAddressSelectActivity.class);
			 startActivity(intent);
		}//以上测试用:
		
		else if(id==R.id.more_station){
			if(1==DIALOG_FROM){
				intent = new Intent(YellowPageBookTrainTicketActivity.this,
						YellowPageCitySelectActivity.class);
//				intent.putExtra("train", TAG);
				intent.putExtra(YellowPageCitySelectActivity.SHOW_MODE_KEY, YellowPageCitySelectActivity.SHOW_MODE_NOHOT);
				intent.putExtra("title", stationTitle);
				intent.putExtra(YellowPageCitySelectActivity.FROM_ACTIVITY_KEY, TAG);
				startActivityForResult(intent, REQUEST_START_CITY_CODE);
			}else if(2==DIALOG_FROM){
				intent = new Intent(YellowPageBookTrainTicketActivity.this,
						YellowPageCitySelectActivity.class);
//				intent.putExtra("train", TAG);
				intent.putExtra(YellowPageCitySelectActivity.SHOW_MODE_KEY, YellowPageCitySelectActivity.SHOW_MODE_NOHOT);
				intent.putExtra("title", stationTitle);
				intent.putExtra(YellowPageCitySelectActivity.FROM_ACTIVITY_KEY, TAG);
				startActivityForResult(intent, REQUEST_END_CITY_CODE);
			}
			if(mCommonDialog!=null&&mCommonDialog.isShowing()){
				mCommonDialog.dismiss();
			}
		}else if(id==R.id.back_layout){
			finish();
		}else if(id==R.id.next_setp_layout){
			String targetActivity = MyCenterConstant.MY_NODE_TONGCHENG_TRAIN;
			String url =TongChengConfig.YELLOW_PAGE_TONGCHENG_ORDERQUERY;
//			mYellowParams = null;
//			if (mYellowParams == null)
//				mYellowParams = new YellowParams();
//			try {
//				intent = new Intent(YellowPageBookTrainTicketActivity.this,
//						Class.forName(targetActivity));
//				mYellowParams.setTitle(getString(R.string.putao_traintriket_orderhistory));
//				String open_token = PutaoAccount.getInstance().getOpenToken();
//				if (!TextUtils.isEmpty(open_token)) {
//					url = url + "&open_token=" + open_token+"&"+TongChengConfig.PUTAO_TONGCHENG_REFID;
//				}
//				mYellowParams.setUrl(url);
////				mYellowParams.setUrl("http://121.41.60.51:7899/_plugin/head/train.html");
//				intent.putExtra(YellowUtil.TargetIntentParams, mYellowParams);
//				startActivity(intent);
//			} catch (ClassNotFoundException e) {
//				e.printStackTrace();
//			}
//			
			intent = new Intent(this, YellowPageJumpH5Activity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			String open_token = PutaoAccount.getInstance().getOpenToken();
			if (!TextUtils.isEmpty(open_token)) {
			    Map<String, String> paramMap = new HashMap<String, String>();
			    paramMap.put(TongChengConfig.PUTAO_TONGCHENG_TRAIN_REFID, TongChengConfig.PUTAO_TONGCHENG_TRAIN_REFVAL);
			    paramMap.put("open_token", open_token);
			    
				url = URLUtil.addParamForUrl(url, paramMap);
			}
			intent.putExtra("targetActivityName", targetActivity);
			intent.putExtra("url",url);
			intent.putExtra("title", getResources().getString(R.string.putao_traintriket));
//			intent.putExtra(name, value);
			mYellowParams = new YellowParams();
			mYellowParams.setUrl(url);
//			mYellowParams.setUrl("http://121.41.60.51:7899/_plugin/head/train.html");
			mYellowParams.setTitle(getResources().getString(R.string.putao_traintriket));
//				mYellowParams.setUrl("http://121.41.60.51:9200/_plugin/head/train.html");
			intent.putExtra(YellowUtil.TargetIntentParams, mYellowParams);
			startActivity(intent);
		}

	}

	/**查询火车票*/
	private void doQuery(Intent intent) {
		mStartCity =tv_station_start.getText().toString().trim();
		mEndCity =tv_station_end.getText().toString().trim();
		if (TextUtils.isEmpty(mEndCity)) {
			Toast.makeText(this,getResources().getString(R.string.putao_traintriket_noarrivecity), 0).show();
			return;
		}
		if (mEndCity.equals(mStartCity)) {
			Toast.makeText(this,getResources().getString(R.string.putao_traintriket_same_err), 0).show();
			return;
		}
		YellowPageTrainDB db = ContactsAppUtils.getInstance()
				.getDatabaseHelper().getTrainDBHelper();
		
		String stationStart = db.getStationQuanPin(mStartCity);
		String stationEnd = db.getStationQuanPin(mEndCity);
		LogUtil.i(TAG, stationStart+"_"+stationEnd);
		if (TextUtils.isEmpty(stationStart)) {
			Toast.makeText(this,getResources().getString(R.string.putao_traintriket_nostation), 0).show();
			return;
		}
		String open_token = PutaoAccount.getInstance().getOpenToken();
		LogUtil.i(TAG, "open_token="+open_token);
		if (TextUtils.isEmpty(open_token)) {
			Toast.makeText(this,getResources().getString(R.string.putao_traintriket_serverbusy), 0).show();
			return ;
		}
		StringBuilder sb = new StringBuilder(TongChengConfig.YELLOW_PAGE_TONGCHENG_TICKETQUERY);
		sb.append("-")
				.append(stationStart)
				.append("-")
				.append(stationEnd)
				.append(".html?Time=")
				.append(startDate)
				.append(TongChengConfig.YELLOW_PAGE_TONGCHENG_REQUEST_PARAM);
		
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put(TongChengConfig.PUTAO_TONGCHENG_TRAIN_REFID, TongChengConfig.PUTAO_TONGCHENG_TRAIN_REFVAL);
        paramMap.put("open_token", open_token);
        
        String url = URLUtil.addParamForUrl(sb.toString(), paramMap);
		
//		intent = new Intent(this, YellowPageTongchengTrainActivity.class);
		intent = new Intent(this, YellowPageJumpH5Activity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		intent.putExtra("targetActivityName", YellowPageTongchengTrainActivity.class.getName());
//		intent.putExtra("url", url);
//		intent.putExtra("title", getResources().getString(R.string.putao_traintriket));
//		intent.putExtra(name, value);
		mYellowParams = new YellowParams();
		mYellowParams.setUrl(url);
//		mYellowParams.setUrl("http://121.41.60.51:7899/_plugin/head/train.html");
		mYellowParams.setTitle(getResources().getString(R.string.putao_traintriket));
//			mYellowParams.setUrl("http://121.41.60.51:9200/_plugin/head/train.html");
		
		LogUtil.v(TAG, "url="+url);
		intent.putExtra(YellowUtil.TargetIntentParams, mYellowParams);
		startActivity(intent);
	}

	
	private void showHotStationLayout() {
		
		if (mCommonDialog != null) {
			mCommonDialog.setTitle(getResources().getString(R.string.putao_traintriket_title));
			gv = mCommonDialog.getGridView();
			gv.setAdapter(new MyDialogAdapter());
		}
		mCommonDialog.show();
	}

	// 测试用

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

		super.onActivityResult(requestCode, resultCode, data);

		if (data == null) {
			return;
		}
		if (resultCode != RESULT_OK) {
			return;
		}
		if (requestCode == REQUEST_START_CITY_CODE) {
			// 城市选择
			mStartCity = data.getStringExtra("cityName");
			mHandler.sendEmptyMessage(MSG_UPDATE_STARTCITY_ACTION);
		} else if (requestCode == REQUEST_END_CITY_CODE) {
			mEndCity = data.getStringExtra("cityName");
			mHandler.sendEmptyMessage(MSG_UPDATE_END_ACTION);
			setLastStation(mEndCity);
		} else if (requestCode == REQUEST_CODE_TRAIN_DATE) {
			// 火车票日期选择
			Object obj = null;
			try {
				obj = data.getSerializableExtra("SelectCalendar");
			} catch (Exception e) {
				obj = null;
			}
			if (obj == null) {
				return;
			}
			// 更新
			CalendarBean calendarModel = (CalendarBean) obj;
			mCalendarModel = calendarModel;
			Message msg = mHandler.obtainMessage();
			msg.what = MSG_UPDATE_TRAINDATE_CODE;
			msg.obj = calendarModel;
			mHandler.sendMessage(msg);
		}

	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		return false;
	}

	@Override
	public String getServiceNameByUrl() {
		return null;
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

	@Override
	public Integer remindCode() {
		return mRemindCode;
	}

	public class MyDialogAdapter  extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mHotStationList.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView==null){
				convertView =View.inflate(YellowPageBookTrainTicketActivity.this, R.layout.putao_train_common_dialog_gridview_item, null);
			}
			TextView tv = (TextView) convertView.findViewById(R.id.hot_railway);
			tv.setText(mHotStationList[position]);
			return convertView;
		}
		
	}
	
	private void setLastStation(String select){
		SharedPreferences sp = getSharedPreferences("last_railwaystation", Context.MODE_PRIVATE);
		Editor editor =sp.edit();
		editor.putString("arrived_station", select);
		editor.commit();
	}

	@Override
	public Integer getAdId() {
	    return AdCode.ADCODE_YellowPageBookTrainTicketActivity;
	}
	
}
