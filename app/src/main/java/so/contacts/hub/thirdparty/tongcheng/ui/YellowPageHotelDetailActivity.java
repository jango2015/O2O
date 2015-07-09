package so.contacts.hub.thirdparty.tongcheng.ui;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.util.UMengEventIds;

import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.loader.DataLoader;
import com.loader.image.ImageLoaderFactory;
import com.yulong.android.contacts.discover.R;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import so.contacts.hub.ad.AdCode;
import so.contacts.hub.adapter.HotelRoomInfoAdapter;
import so.contacts.hub.remind.BaseRemindActivity;
import so.contacts.hub.thirdparty.tongcheng.bean.TC_HotelRoomBean;
import so.contacts.hub.thirdparty.tongcheng.bean.TC_Response_HotelRoomsWithPolicy;
import so.contacts.hub.thirdparty.tongcheng.util.QueryRoomPolicyDataTask;
import so.contacts.hub.ui.yellowpage.YellowPageCalendarActivity;
import so.contacts.hub.ui.yellowpage.bean.CalendarBean;
import so.contacts.hub.util.CalendarUtil;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.NetUtil;
import so.contacts.hub.util.Utils;
import so.contacts.hub.widget.MyListView;
import so.contacts.hub.widget.ProgressDialog;

public class YellowPageHotelDetailActivity extends BaseRemindActivity implements OnClickListener, OnItemClickListener {
	
	/** view start. */
	private ImageView mHotelImgView = null;
	private TextView mHotelNameTView = null;
	private TextView mHotelTypeTView = null;
	private TextView mHotelMarkNumTView = null;
	private TextView mHotelOpendateTView = null;
	private TextView mHotelAddressTView = null;
	private RelativeLayout mHotelComeDateLayout = null;
	private TextView mHotelComeDateTView = null;
	private RelativeLayout mHotelLeaveDateLayout = null;
	private TextView mHotelLeaveDateTView = null;
	
	private LinearLayout mRoomInfoLayout = null;
	private ListView mRoomInfoListView = null;
	private RelativeLayout mExpandLayout = null;
	private ImageView mExpandImgView = null;

	private ProgressDialog mProgressDialog = null;
	/** view end. */

	/** data start. */
	private String mCityName = null;
	private String mHotelId = null;
	private String mHotelName = null;
	private String mHotelImg = null;
	private String mHotelAddress = null;
	private String mLongitude = null;
	private String mLatitude = null;
	private double mHotelMarkNum = 0;
	private String mHotelStarRatedName = null;
	private String mComeDate = null;
	private String mLeaveDate = null;
	private String mHotelOpenDate = null;

	private DataLoader mDataLoader = null;
	private HotelRoomInfoAdapter mAdapter = null;
	private List<TC_HotelRoomBean> mHotelRoomList = new ArrayList<TC_HotelRoomBean>();
	private TC_HotelRoomBean mSelectRoomBean = null;
	/** data end. */
	
	/** tagdata start. */
	private boolean mIsExpanded = false; //是否展开(默认为收缩)

	private static final int MAX_SHOW_NUM = 5; // 酒店房型列表 不需要展开时的显示的最大数量
	
	private QueryRoomPolicyDataTask mQueryDataTask = null;
	private QueryRoomPolicyDataTask mQueryRoomPolicyDataTask = null;

    private static final int PAGE_DATA_SIZE = 20; // 每次请求的数据位20条

    private static final int MSG_START_REFRESH_ROOMLIST_DATA_ACTION = 0x2001;
	private static final int MSG_UPDATE_ALL_ROOMLIST_DATA = 0x2002;
	private static final int MSG_UPDATE_DATE_IN_ACTION = 0x2003;
	private static final int MSG_REUPDATE_DATE_OUT_ACTION = 0x2004;
	private static final int MSG_UPDATE_DATE_OUT_ACTION = 0x2005;
	private static final int MSG_SHOW_DIALOG_ACTION = 0x2006;
	private static final int MSG_DISMISS_DIALOG_ACTION = 0x2007;
	
	private CalendarBean mInCalendarModel = null; // 入住日期
	
	private CalendarBean mOutCalendarModel = null; // 离店日期
	
	// 星期中文列表： 周日，周一...
	private String[] mWeekTagList = null;
	
	private static final int REQUEST_CODE_DATE_IN = 101; // 入住时间选择 返回Code
	
	private static final int REQUEST_CODE_DATE_OUT = 102; // 离店时间选择 返回Code
    
	/** tagdata end. */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.putao_yellow_page_hoteldetail);
		
		parseIntent();
		initView();
		initData();
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
	
	private void parseIntent(){
		Intent intent = getIntent();
		if( intent == null ){
			return;
		}
		mCityName = intent.getStringExtra("CityName");
		mHotelId = intent.getStringExtra("HotelId");
		mHotelImg = intent.getStringExtra("HotelImg");
		mHotelName = intent.getStringExtra("HotelName");
		mHotelAddress = intent.getStringExtra("HotelAddress");
		mLongitude = intent.getStringExtra("Longitude");
		mLatitude = intent.getStringExtra("Latitude");
		mHotelMarkNum = intent.getDoubleExtra("HotelMarkNum", 0);
		mHotelStarRatedName = intent.getStringExtra("StarRatedName");
		mComeDate = intent.getStringExtra("ComeDate");
		mLeaveDate = intent.getStringExtra("LeaveDate");		
	}
	
	private void initView(){
        if( TextUtils.isEmpty(mTitleContent) ){
            mTitleContent = getResources().getString(R.string.putao_hoteldetail);
        }
        ((TextView) findViewById(R.id.title)).setText(mTitleContent);
        findViewById(R.id.back_layout).setOnClickListener(this);
        
        mHotelImgView = (ImageView) findViewById(R.id.hoteldetail_img);
        mHotelNameTView = (TextView) findViewById(R.id.hoteldetail_name);
        mHotelTypeTView = (TextView) findViewById(R.id.hoteldetail_hoteltype);
        mHotelMarkNumTView = (TextView) findViewById(R.id.hoteldetail_marknum);
        mHotelOpendateTView = (TextView) findViewById(R.id.hoteldetail_opendate);
        mHotelAddressTView = (TextView) findViewById(R.id.hoteldetail_address);
        mHotelComeDateLayout = (RelativeLayout) findViewById(R.id.hoteldetail_comedate_layout);
        mHotelComeDateTView = (TextView) findViewById(R.id.hoteldetail_comedate);
        mHotelLeaveDateLayout = (RelativeLayout) findViewById(R.id.hoteldetail_leavedate_layout);
        mHotelLeaveDateTView = (TextView) findViewById(R.id.hoteldetail_leavedate);
        mHotelComeDateLayout.setOnClickListener(this);
        mHotelLeaveDateLayout.setOnClickListener(this);
        mHotelAddressTView.setOnClickListener(this);
        
        mRoomInfoLayout = (LinearLayout) findViewById(R.id.hoteldetail_roominfo_list_layout);
        mRoomInfoListView = (ListView) findViewById(R.id.hoteldetail_roominfo_list);
        mRoomInfoListView.setOnItemClickListener(this);
        mExpandLayout = (RelativeLayout) findViewById(R.id.hoteldetail_expand_layout);
        mExpandImgView = (ImageView) findViewById(R.id.hoteldetail_expand_imgview);
        mExpandLayout.setOnClickListener(this);
	}
	
	/**
	 * 去掉年份，将"2014-12-06"转化为"12-06"
	 */
	private void showHotelDate(boolean isComeIn){
		if( isComeIn ){
			if( !TextUtils.isEmpty(mComeDate) ){
				mHotelComeDateTView.setText(getString(R.string.putao_hotel_in_date, mComeDate.substring(5)));
			}
		}else{
			if( !TextUtils.isEmpty(mLeaveDate) ){
				mHotelLeaveDateTView.setText(getString(R.string.putao_hotel_out_date, mLeaveDate.substring(5)));
			}
		}
	}
	
	private void initData(){
		mDataLoader = new ImageLoaderFactory(this).getYellowPageLoader(R.drawable.putao_a0114, 0);
		
		mDataLoader.loadData(mHotelImg, mHotelImgView);
		mHotelNameTView.setText(mHotelName);
		mHotelTypeTView.setText(getString(R.string.putao_hoteldetail_starratedname, mHotelStarRatedName));
		mHotelMarkNumTView.setText(getString(R.string.putao_hoteldetail_marknum, new DecimalFormat("0.0").format(mHotelMarkNum)));
		mHotelAddressTView.setText(mHotelAddress);
		showHotelDate(true);
		showHotelDate(false);
		
		mAdapter = new HotelRoomInfoAdapter(this, mHotelRoomList, mDataLoader, MAX_SHOW_NUM);
		mRoomInfoListView.setAdapter(mAdapter);
		
		mWeekTagList = getResources().getStringArray(R.array.putao_week_list);
		
		// 初始化入住的信息
		if( !TextUtils.isEmpty(mComeDate) ){
			mInCalendarModel = getCalendarData(mComeDate, CalendarUtil.getWeekNumByDate(mComeDate));
		}
		
		// 初始化离店的信息
		if( !TextUtils.isEmpty(mLeaveDate) ){
			mOutCalendarModel = getCalendarData(mLeaveDate, CalendarUtil.getWeekNumByDate(mLeaveDate));
		}
		
		mHandler.sendEmptyMessage(MSG_START_REFRESH_ROOMLIST_DATA_ACTION);
	}
	
	
	
	/**
	 * 清空房型列表信息
	 */
	private void clearHoelRoomListData(){
		if( mHotelRoomList != null ){
			mHotelRoomList.clear();
			if( mAdapter != null ){
				mAdapter.setData(mHotelRoomList);
				mAdapter.setExpanded(mIsExpanded);
			}
		}
		mIsExpanded = false;
		mRoomInfoListView.setVisibility(View.GONE);
		mExpandImgView.setVisibility(View.GONE);
	}
	
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch(what){
			case MSG_START_REFRESH_ROOMLIST_DATA_ACTION:
				// 刷新房型列表信息
				if( NetUtil.isNetworkAvailable(YellowPageHotelDetailActivity.this) ){
					if ( (mQueryDataTask != null && mQueryDataTask.getStatus() != AsyncTask.Status.RUNNING)
							|| mQueryDataTask == null ) {
						clearHoelRoomListData(); // 重新刷新之前，先隐藏
						mRoomInfoLayout.setVisibility(View.GONE);
						mQueryDataTask = new QueryRoomPolicyDataTask(mHotelId, mComeDate, mLeaveDate,
								new IQueryRoomPolicyDataCallback() {

									@Override
									public void onPreExecute() {
										// TODO Auto-generated method stub
										 mHandler.sendEmptyMessage(MSG_SHOW_DIALOG_ACTION);
									}

									@Override
									public void onPostExecute(TC_Response_HotelRoomsWithPolicy result) {
										// TODO Auto-generated method stub
										mHandler.sendEmptyMessage(MSG_DISMISS_DIALOG_ACTION);
							            if( result == null ){
							            	Utils.showToast(YellowPageHotelDetailActivity.this, 
							            			R.string.putao_hoteldetail_no_roomlist_hint, false);
							                return;
							            }
							            mHotelRoomList = result.getHotelroomlist();
							            if( mHotelRoomList == null || mHotelRoomList.size() == 0 ){
							            	Utils.showToast(YellowPageHotelDetailActivity.this, 
							            			R.string.putao_hoteldetail_no_roomlist_hint, false);
							                return;
							            }
							            mRoomInfoLayout.setVisibility(View.VISIBLE);
							            mHandler.sendEmptyMessage(MSG_UPDATE_ALL_ROOMLIST_DATA);
									}
								});
						mQueryDataTask.execute();// 开始刷新数据
					}
				}else{
					Utils.showToast(YellowPageHotelDetailActivity.this, R.string.putao_no_net, false);
				}
				break;
			case MSG_UPDATE_ALL_ROOMLIST_DATA:
				if( mHotelRoomList != null && mHotelRoomList.size() > 0 ){
					mAdapter.setData(mHotelRoomList);
					mRoomInfoListView.setVisibility(View.VISIBLE);
					if( mHotelRoomList.size() > MAX_SHOW_NUM ){
						mExpandImgView.setImageResource(R.drawable.putao_icon_list_down);
						mExpandImgView.setVisibility(View.VISIBLE);
						mExpandLayout.setVisibility(View.VISIBLE);
					}else{
						mExpandLayout.setVisibility(View.GONE);
					}
				}
				break;
			case MSG_UPDATE_DATE_IN_ACTION:
				// 入住日期
				CalendarBean inModel = (CalendarBean) msg.obj;
				if( inModel == null ){
					return;
				}
				mInCalendarModel = inModel;
				String comeDate = mInCalendarModel.getFormatStr();
				if( !mComeDate.equals(comeDate) ){
					// 日期更改，则需要重新刷新酒店房型列表
					mComeDate = comeDate;
					showHotelDate(true);
					mHandler.sendEmptyMessage(MSG_START_REFRESH_ROOMLIST_DATA_ACTION);
				}
				break;
			case MSG_REUPDATE_DATE_OUT_ACTION:
				// 清除了之前的离店日期，根据入住日期生成明天的日期
				mOutCalendarModel = null;
				CalendarBean reOutModel = (CalendarBean) msg.obj;
				if( reOutModel == null ){
					return;
				}
				mInCalendarModel = reOutModel;
				String reComeDate = mInCalendarModel.getFormatStr();
				if( !mComeDate.equals(reComeDate) ){
					mComeDate = reComeDate;
					showHotelDate(true);
				}
				String tomorrowDate = CalendarUtil.getAppointTomorrowDate(reOutModel.getYear(), reOutModel.getMonth(), reOutModel.getDay());
				mOutCalendarModel = getCalendarData(tomorrowDate, CalendarUtil.getWeekNumByDate(tomorrowDate));
				mLeaveDate = mOutCalendarModel.getFormatStr();
				showHotelDate(false);
				mHandler.sendEmptyMessage(MSG_START_REFRESH_ROOMLIST_DATA_ACTION);
				break;
			case MSG_UPDATE_DATE_OUT_ACTION:
				// 离店日期
				CalendarBean outModel = (CalendarBean) msg.obj;
				if( outModel == null ){
					return;
				}
				mOutCalendarModel = outModel;
				String leaveDate = mOutCalendarModel.getFormatStr();
				if( !mLeaveDate.equals(leaveDate) ){
					// 日期更改，则需要重新刷新酒店房型列表
					mLeaveDate = leaveDate;
					showHotelDate(false);
					mHandler.sendEmptyMessage(MSG_START_REFRESH_ROOMLIST_DATA_ACTION);
				}
				break;
			case MSG_SHOW_DIALOG_ACTION:
				if (mProgressDialog == null) {
					mProgressDialog = new ProgressDialog(YellowPageHotelDetailActivity.this);
					mProgressDialog.setMessage(getString(R.string.putao_yellow_page_loading));
				}
				mProgressDialog.show();
				break;
			case MSG_DISMISS_DIALOG_ACTION:
				if (mProgressDialog != null && mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
				break;
			default:
				break;
			}
		};
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if( data == null ){
			return;
		}
		if( requestCode == REQUEST_CODE_DATE_IN ){
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
			boolean needClearOutCalendar = data.getBooleanExtra("NeedClearOutCalendar", false);
			if( needClearOutCalendar ){
				// 更新离店时间
				msg.what = MSG_REUPDATE_DATE_OUT_ACTION;
			}else{
				// 不需要更新离店时间
				msg.what = MSG_UPDATE_DATE_IN_ACTION;
			}
			msg.obj = calendarModel;
			mHandler.sendMessage(msg);
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
	};

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		// TODO Auto-generated method stub
		if( !NetUtil.isNetworkAvailable(this) ){ //网络异常
			Utils.showToast(this, R.string.putao_no_net, false);
			return;
		}
		TC_HotelRoomBean hotelRoomBean = mHotelRoomList.get(position);
		if( hotelRoomBean == null ){
			return;
		}
		int bookingFlag = hotelRoomBean.getBookingFlag();
		if( bookingFlag != 0 ){
			//房间已满
			Toast.makeText(this, R.string.putao_hoteldetail_cannot_book, Toast.LENGTH_SHORT).show();
			return;
		}
		checkRoomPolicy(hotelRoomBean);
	}
	
	private void checkRoomPolicy(TC_HotelRoomBean hotelRoomBean){
		if ( (mQueryRoomPolicyDataTask != null && mQueryRoomPolicyDataTask.getStatus() != AsyncTask.Status.RUNNING)
				|| mQueryRoomPolicyDataTask == null ) {
			mSelectRoomBean = hotelRoomBean;
			
			mQueryRoomPolicyDataTask = new QueryRoomPolicyDataTask(mHotelId, mComeDate, mLeaveDate,
					mSelectRoomBean.getRoomTypeId(), mSelectRoomBean.getPolicyId(), getCurrentArriveTime(),
					new IQueryRoomPolicyDataCallback() {

						@Override
						public void onPreExecute() {
							// TODO Auto-generated method stub
							 mHandler.sendEmptyMessage(MSG_SHOW_DIALOG_ACTION);
						}

						@Override
						public void onPostExecute(TC_Response_HotelRoomsWithPolicy result) {
							// TODO Auto-generated method stub
							mHandler.sendEmptyMessage(MSG_DISMISS_DIALOG_ACTION);
				            if( result == null ){
				                Utils.showToast(YellowPageHotelDetailActivity.this, R.string.putao_hoteldetail_gethotelpolicy_hint, false);
				                return;
				            }
				            List<TC_HotelRoomBean> hotelroomlist = result.getHotelroomlist();
				            if( hotelroomlist == null ){
				                Utils.showToast(YellowPageHotelDetailActivity.this, R.string.putao_hoteldetail_gethotelpolicy_hint, false);
				            	return;
				            }
				            TC_HotelRoomBean roomBean = hotelroomlist.get(0);
				            if( roomBean == null ){
				                Utils.showToast(YellowPageHotelDetailActivity.this, R.string.putao_hoteldetail_gethotelpolicy_hint, false);
				            	return;
				            }
				            Intent intent = new Intent(YellowPageHotelDetailActivity.this, YellowPageHotelOrderActivity.class);
				            intent.putExtra("CityName", mCityName);
				            intent.putExtra("HotelId", mHotelId);
				            intent.putExtra("HotelName", mHotelName);
				            intent.putExtra("HotelImg", roomBean.getPhotoUrl()); //保存酒店房型Url
				            intent.putExtra("HotelAddress", mHotelAddress);
				            intent.putExtra("HotelRoomName", roomBean.getRoomName());
				            intent.putExtra("RoomTypeId", roomBean.getRoomTypeId());
				            intent.putExtra("PolicyId", roomBean.getPolicyId());
				            intent.putExtra("DaysAmountPrice", roomBean.getRoomAdviceAmount());
				            intent.putExtra("AvgAmount", roomBean.getAvgAmount());
				            intent.putExtra("DanbaoType", roomBean.getDanBaoType());
				            intent.putExtra("GuaranteeType", roomBean.getGuaranteeType());
				            intent.putExtra("OverTime", roomBean.getOverTime());
				            intent.putExtra("ComeDate", mComeDate);
				            intent.putExtra("LeaveDate", mLeaveDate);
				            startActivity(intent);

				            // add xcx 2014-12-30 start 统计埋点
				            MobclickAgentUtil.onEvent(ContactsApp.getInstance()
				                    .getApplicationContext(),
				                    UMengEventIds.DISCOVER_YELLOWPAGE_HOTEL_DETAIL_ROOM_SELECT);
				            // add xcx 2014-12-30 end 统计埋点
						}
					});
			
			mQueryRoomPolicyDataTask.execute();
        }
	}
	
	/**
	 * 根据当前时间来设置正确的最晚到店时间
	 * 注：最晚到店时间为
	 */
	private String getCurrentArriveTime(){
		String currentTime = "1900-01-02 05:00"; 
		Calendar calendar = Calendar.getInstance();
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		if( hour < 18 ){
			currentTime = "1900-01-01 18:00";
		}else if( hour < 20){
			currentTime = "1900-01-01 20:00";
		}else if( hour < 22){
			currentTime = "1900-01-01 22:00";
		}else if( hour < 24){
			currentTime = "1900-01-02 05:00";
		}
		return currentTime;
	}
	
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		int viewId = view.getId();
		Intent intent = null;
		if (viewId == R.id.back_layout) {
			finish();
		} else if (viewId == R.id.hoteldetail_address) {
			if( TextUtils.isEmpty(mLongitude) || TextUtils.isEmpty(mLatitude) ){
				return;
			}
			Uri uri = null;
            try {
                // 高德地图intent
                intent = getGaodeMapIntent(Double.valueOf(mLatitude), Double.valueOf(mLongitude), mHotelAddress, "");

                List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent,
                        PackageManager.GET_ACTIVITIES);
                if (list == null || list.size() == 0) {// 高德地图 未安装，选择百度地图
                    // 百度地图intent
                    CoordinateConverter converter = new CoordinateConverter();// 坐标转换工具
                    converter.from(CoordinateConverter.CoordType.COMMON);
                    converter.coord(new LatLng(Double.valueOf(mLatitude), Double.valueOf(mLongitude)));
                    LatLng latLng = converter.convert();

                    intent = getBaiduMapIntent(latLng.latitude, latLng.longitude, mHotelAddress);
                    list = getPackageManager().queryIntentActivities(intent,
                            PackageManager.GET_ACTIVITIES);

                    if (list == null || list.size() == 0) {// 百度地图
                                                           // 未安装，选择百度地图网页版
                        intent = new Intent(Intent.ACTION_VIEW);
                        uri = Uri.parse("http://api.map.baidu.com/marker?" + "location="
                                + latLng.latitude + "," + latLng.longitude + "&title="
                                + mHotelAddress + "&content=" + mHotelAddress + "&output=html");
                        intent.setData(uri);
                    }
                }
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, R.string.putao_yellow_page_no_mapapp, Toast.LENGTH_SHORT).show();
            }
		} else if (viewId == R.id.hoteldetail_comedate_layout) {
			intent = new Intent(this, YellowPageCalendarActivity.class);
			intent.putExtra("DateType", CalendarBean.MODEL_SELECT_IN); // 入住
			intent.putExtra("InCalendarData", mInCalendarModel);
			intent.putExtra("OutCalendarData", mOutCalendarModel);
			startActivityForResult(intent, REQUEST_CODE_DATE_IN);
		} else if (viewId == R.id.hoteldetail_leavedate_layout) {
			intent = new Intent(this, YellowPageCalendarActivity.class);
			intent.putExtra("DateType", CalendarBean.MODEL_SELECT_OUT); // 离店
			intent.putExtra("InCalendarData", mInCalendarModel);
			intent.putExtra("OutCalendarData", mOutCalendarModel);
			startActivityForResult(intent, REQUEST_CODE_DATE_OUT);
		} else if (viewId == R.id.hoteldetail_expand_layout) {
			if( mHotelRoomList != null && mHotelRoomList.size() > MAX_SHOW_NUM ){
				if( mIsExpanded ){
					mIsExpanded = false;
					mExpandImgView.setImageResource(R.drawable.putao_icon_list_down);
				}else{
					mIsExpanded = true;
					mExpandImgView.setImageResource(R.drawable.putao_icon_list_up);
				}
				mAdapter.setExpanded(mIsExpanded);
			}
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
	
	private Intent getBaiduMapIntent(double latitude, double longitude, String address) {
        // Uri uri = Uri.parse("geo:0,0?q=" + address);
        // Intent intent = new Intent(Intent.ACTION_VIEW);
        // intent.setData(uri);
        // intent.setPackage("com.baidu.BaiduMap");// 百度地图

        String url = "intent://map/marker?location="
                + latitude
                + ","
                + longitude
                + "&title="
                + address
                + "&content"
                + address
                + "&src=yourCompanyName|yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end";

        Intent intent = null;
        try {
            intent = Intent.getIntent(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return intent;
    }

	private Intent getGaodeMapIntent(double latitude, double longitude, String address,
            String poiId) {
        // Uri uri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" +
        // address);
        // Intent intent = new Intent(Intent.ACTION_VIEW);
        // intent.setData(uri);
        // intent.setPackage("com.autonavi.minimap");// 高德地图
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory("android.intent.category.DEFAULT");
        Uri uri = Uri.parse("androidamap://viewMap?sourceApplication=appname&poiid=" + poiId
                + "&poiname=" + address + "&lat=" + latitude + "&lon=" + longitude + "&dev=0");
        intent.setData(uri);
        intent.setPackage("com.autonavi.minimap");// 高德地图

        return intent;
    }
	
	public interface IQueryRoomPolicyDataCallback{
		void onPreExecute();
		void onPostExecute(TC_Response_HotelRoomsWithPolicy result);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(mQueryDataTask != null){
			mQueryDataTask.cancel(true);
			mQueryDataTask = null;
    	}
		if(mQueryRoomPolicyDataTask != null){
			mQueryRoomPolicyDataTask.cancel(true);
			mQueryRoomPolicyDataTask = null;
    	}
	}
	
	@Override
	public String getServiceNameByUrl() {
		// TODO Auto-generated method stub
		return this.getClass().getName();
	}

	@Override
	public String getServiceName() {
		// TODO Auto-generated method stub
		return this.getClass().getName();
	}

	@Override
	public boolean needMatchExpandParam() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Integer remindCode() {
		// TODO Auto-generated method stub
		return mRemindCode;
	}
	
	@Override
	public Integer getAdId() {
		// TODO Auto-generated method stub
		return AdCode.ADCODE_NONE;
	}

}
