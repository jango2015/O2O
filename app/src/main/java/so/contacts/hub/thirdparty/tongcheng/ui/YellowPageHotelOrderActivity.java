package so.contacts.hub.thirdparty.tongcheng.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.ad.AdCode;
import so.contacts.hub.remind.BaseRemindActivity;
import so.contacts.hub.thirdparty.tongcheng.bean.TCRequestData;
import so.contacts.hub.thirdparty.tongcheng.bean.TC_HotelSameOrderBean;
import so.contacts.hub.thirdparty.tongcheng.bean.TC_Request_SubmitHotelOrder;
import so.contacts.hub.thirdparty.tongcheng.bean.TC_Response_SubmitHotelOrder;
import so.contacts.hub.thirdparty.tongcheng.util.TC_Common;
import so.contacts.hub.thirdparty.tongcheng.util.TC_Hotel_OrderState_Util;
import so.contacts.hub.thirdparty.tongcheng.util.TC_Http;
import so.contacts.hub.thirdparty.tongcheng.util.TC_Request_DataFactory;
import so.contacts.hub.util.CalendarUtil;
import so.contacts.hub.util.ContactsHubUtils;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.NetUtil;
import so.contacts.hub.util.TelAreaUtil;
import so.contacts.hub.util.UMengEventIds;
import so.contacts.hub.util.Utils;
import so.contacts.hub.widget.CommonDialog;
import so.contacts.hub.widget.CommonDialogFactory;
import so.contacts.hub.widget.ProgressDialog;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Contacts.People.Phones;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.yulong.android.contacts.discover.R;

public class YellowPageHotelOrderActivity extends BaseRemindActivity implements OnClickListener {
	
	private static final String TAG = "YellowPageHotelOrderActivity";
	
	/** view start. */
	private TextView mHotelNameTView = null;
	private TextView mHotelDaysTView = null;
	private TextView mComeDateTView = null;
	private TextView mLeaveDateTView = null;
	
	private LinearLayout mRoomNameLayout = null;
	private TextView mRoomNameTView = null;
	private TextView mRoomsTView = null;
	
	private LinearLayout mArriveDateLayout = null;
	private TextView mArriveDateTView = null;

	private EditText mComeNameEDit = null;
	private EditText mMobileEDit = null;
	private TextView mAmountTView = null;
	
	private ImageView mComeNameTipIView = null;
    private ImageView mClearInput = null;
	
	private Button mSubmitBtn = null;
	/** view end. */

	/** data start. */
	private String mCityName = null;
	private String mHotelId = null;
	private String mHotelName = null;
	private String mHotelImg = null;
	private String mHotelAddress = null;
	private String mRoomName = null;
	private String mRoomTypedId = null;
	private String mPolicyId = null;
	private String mRoomAdviceAmount = null;
	private int mGuaranteeType = 0; 	//担保类型(0-无担保；1-担保冻结；2-担保预付；3-代收代付)(用来判断是否需要支付再提交订单)
	private int mDanBaoType = 0;		//担保政策类型
	private int mOverTime = 0;			//超时点钟(值范围[0,23]，超时时显示非必有字段)
	private String mAvgAmount = null;
	private String mComeDate = null;
	private String mLeaveDate = null;
	private long mStayDays = 1;
	private int mSelectRoomIndex = 0;
	private int mSelectArriveIndex = 0;
	private String mComeName = "";
	private String mMobile = "";
	private double mTotalAmount = 0;
	/** data end. */
	
	/** tag data start. */
	private int mSubmitOrderType = TC_Hotel_OrderState_Util.SUBMIT_ORDER_TYPE_NONE;
	
	private CommonDialog mCommonDialog = null; 			// 弹出的选择框
	
	private ProgressDialog mProgressDialog = null;		// 等待框
	
	private CommonDialog mPricesDialog =null; //弹出价格明细的显示框;
	private ListView mPriceListViews =null;
	private ImageView imgView_priceTips = null;
	
	

	private static final int MSG_SHOW_DIALOG_ACTION = 0x2001;
	private static final int MSG_DISMISS_DIALOG_ACTION = 0x2002;
	private static final int MSG_SHOW_SELECT_CONTACT_PHONE_ACTION = 0x2003;
	
    // result code which get contacts phone num
    public static final int REQUEST_CONTACT_INFO = 0x1001;
    
    private static final int VALID_PHONENUM_SIZE = 11;
	
	/**
	 * [1]: 选择房间数量
	 * [2]: 选择到店时间
	 */
	private int mDialogType = 0;
	
	private static final int DIALOG_TYPE_ROOMS = 1;
	private static final int DIALOG_TYPE_ARRIVE_DATE = 2;
	
	private String[] mHotelRoomsList = null;			// 酒店房间数量
	private String[] mHotelArriveDateList = null; 		// 到达时间 列表
	private String[] mHotelArriveDatDatalList = null; 	// 实际请求 到达时间 列表
	
	private SubmitHotelOrderTask mSubmitDataTask = null;
	/** tag data end. */
	
	private InputMethodManager mInputManager = null;
	
	/** hotel order info tag start.
	 *  保存上一次成功预定酒店的入住人姓名
	 */
	private SharedPreferences mSharedPreferences = null;

	private static final String LAST_ORDER_INFO_FILE = "hotel_order_info";
	private static final String LAST_ORDER_INFO_NAME_TAG = "HOTEL_ORDER_NAME";
	private static final String LAST_ORDER_INFO_MOBILE_TAG = "HOTEL_ORDER_MOBILE";
	/** hotel order info tag end. */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.putao_yellow_page_hotelorder);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		
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
		mHotelName = intent.getStringExtra("HotelName");
		mHotelImg = intent.getStringExtra("HotelImg");
		mHotelAddress = intent.getStringExtra("HotelAddress");
		mRoomName = intent.getStringExtra("HotelRoomName");
		mRoomTypedId = intent.getStringExtra("RoomTypeId");
		mPolicyId = intent.getStringExtra("PolicyId");
		mRoomAdviceAmount = intent.getStringExtra("DaysAmountPrice");
		mAvgAmount = intent.getStringExtra("AvgAmount");
		mGuaranteeType  = intent.getIntExtra("GuaranteeType", 0);
		mDanBaoType = intent.getIntExtra("DanbaoType", 0);
		mOverTime = intent.getIntExtra("OverTime", 0);
		mComeDate = intent.getStringExtra("ComeDate");
		mLeaveDate = intent.getStringExtra("LeaveDate");
		mStayDays = CalendarUtil.getGapBetweenTwoDay(mComeDate, mLeaveDate);
	}
	
	
	private void initView(){
        if( TextUtils.isEmpty(mTitleContent) ){
            mTitleContent = getResources().getString(R.string.putao_hotelorder_write);
        }
        ((TextView) findViewById(R.id.title)).setText(mTitleContent);
        findViewById(R.id.back_layout).setOnClickListener(this);
        imgView_priceTips =(ImageView) findViewById(R.id.imgView_priceTips);
        imgView_priceTips.setOnClickListener(this);
        
        mHotelNameTView = (TextView) findViewById(R.id.hotelorder_name);
        mHotelDaysTView = (TextView) findViewById(R.id.hotelorder_days);
        mComeDateTView = (TextView) findViewById(R.id.hotelorder_comedate);
        mLeaveDateTView = (TextView) findViewById(R.id.hotelorder_leavedate);

        mRoomNameLayout = (LinearLayout) findViewById(R.id.hotelorder_roomname_layout);
        mRoomNameTView = (TextView) findViewById(R.id.hotelorder_roomname);
        mRoomsTView = (TextView) findViewById(R.id.hotelorder_romms);
        mRoomNameLayout.setOnClickListener(this);
        
        mArriveDateLayout = (LinearLayout) findViewById(R.id.hotelorder_arrive_date_layout);
        mArriveDateTView = (TextView) findViewById(R.id.hotelorder_arrive_date);
        mArriveDateLayout.setOnClickListener(this);
        
        mComeNameEDit = (EditText) findViewById(R.id.hotelorder_come_name);
        mComeNameTipIView = (ImageView) findViewById(R.id.hotelorder_come_name_tip);
        mComeNameTipIView.setOnClickListener(this);
        mClearInput = (ImageView)findViewById(R.id.clear_search);
        mClearInput.setOnClickListener(this);
        mMobileEDit = (EditText) findViewById(R.id.hotelorder_mobile);
        mMobileEDit.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                LogUtil.d(TAG, "onTextChanged");
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                LogUtil.d(TAG, "onTextChanged");
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                if (TextUtils.isEmpty(arg0)) {
                    mClearInput.setImageResource(R.drawable.putao_icon_contacts);
                } else {
                    mClearInput.setImageResource(R.drawable.putao_icon_list_cancel);
                }
            }
        });
        
        
        mAmountTView = (TextView) findViewById(R.id.hotelorder_amount);
        mAmountTView.setOnClickListener(this);
        mSubmitBtn = (Button) findViewById(R.id.hotelorder_submit);
        mSubmitBtn.setOnClickListener(this);
	}
	
	private void initData(){
		mHotelRoomsList = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};
		initCurrentArriveDate();
		initOrderStateData();
		
        if( mSharedPreferences == null ){
        	mSharedPreferences = getSharedPreferences(LAST_ORDER_INFO_FILE,
        			Context.MODE_MULTI_PROCESS);
        }
        mComeName = mSharedPreferences.getString(LAST_ORDER_INFO_NAME_TAG, "");
        if( !TextUtils.isEmpty(mComeName) ){
        	mComeNameEDit.setText(mComeName);
        	mComeNameEDit.setSelection(mComeName.length());
        }
        mMobile = mSharedPreferences.getString(LAST_ORDER_INFO_MOBILE_TAG, "");
        if( !TextUtils.isEmpty(mMobile) ){
        	mMobileEDit.setText(mMobile);
        	mMobileEDit.setSelection(mMobile.length());
        }
        
		mHotelNameTView.setText(mHotelName);
		mHotelDaysTView.setText(getString(R.string.putao_hotelorder_days, mStayDays));
		mComeDateTView.setText(getString(R.string.putao_hotel_in_date, mComeDate));
		mLeaveDateTView.setText(getString(R.string.putao_hotel_out_date, mLeaveDate));

		mRoomNameTView.setText(mRoomName);
		mRoomsTView.setText(getString(R.string.putao_hotelorder_rooms, Integer.valueOf(mHotelRoomsList[mSelectRoomIndex])));
		mArriveDateTView.setText(mHotelArriveDateList[mSelectArriveIndex]);
		
		if( !TextUtils.isEmpty(mRoomAdviceAmount) ){
			try{
				if( mRoomAdviceAmount.contains(";") ){
					String[] amountList = mRoomAdviceAmount.split(";");
					if( amountList != null && amountList.length > 0 ){
						int priceLen = amountList.length;
						for(int i = 0; i < priceLen; i++){
							String dayAmount = amountList[i];
							if( TextUtils.isEmpty(dayAmount) ){
								// 显示的价格为空，则用平均价格(防止异常)
								dayAmount = mAvgAmount;
							}
							mTotalAmount += Double.valueOf(dayAmount);
						}
					}
				}else{
					mTotalAmount += Double.valueOf(mRoomAdviceAmount);
				}
			}catch(Exception e){
			}
		}
		mAmountTView.setText(getString(R.string.putao_hoteldetail_money, mTotalAmount * Integer.valueOf(mHotelRoomsList[mSelectRoomIndex])));
		
        mCommonDialog = CommonDialogFactory.getListCommonDialog(this);
		mCommonDialog.setListViewItemClickListener(mOnDialogItemClickListener);
		
		mPricesDialog = CommonDialogFactory.getListCommonDialog(this);
	}
	
	private OnItemClickListener mOnDialogItemClickListener = new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long arg3) {
			// TODO Auto-generated method stub
			if (mDialogType == DIALOG_TYPE_ROOMS) {
				if( position < mHotelRoomsList.length ){
					mSelectRoomIndex = position;
					mRoomsTView.setText(getString(R.string.putao_hotelorder_rooms, Integer.valueOf(mHotelRoomsList[mSelectRoomIndex])));
					mAmountTView.setText(getString(R.string.putao_hoteldetail_money, mTotalAmount * Integer.valueOf(mHotelRoomsList[mSelectRoomIndex])));
				}
			}else if (mDialogType == DIALOG_TYPE_ARRIVE_DATE) {
				if( position < mHotelArriveDatDatalList.length ){
					mSelectArriveIndex = position;
					mArriveDateTView.setText(mHotelArriveDateList[mSelectArriveIndex]);
					initOrderStateData();
				}
			}
			if (mCommonDialog != null) {
				mCommonDialog.dismiss();
			}
		}
	};
	
	/**
	 * 显示入住房间数量
	 */
	private void showStayRoomsLayout(){
		if( mCommonDialog != null ){
			mCommonDialog.setTitle(getResources().getString(R.string.putao_hotelorder_rooms_hint));
			mCommonDialog.setSingleChoiceListViewDatas(mHotelRoomsList);
			mCommonDialog.getListView().setItemChecked(mSelectRoomIndex, true);
		}
		mDialogType = DIALOG_TYPE_ROOMS;
		mCommonDialog.show();
	}
	
	/**
	 * 显示到店时间
	 */
	private void showArriveDateLayout(){
		if( mCommonDialog != null ){
			mCommonDialog.setTitle(getResources().getString(R.string.putao_hotelorder_arrive_date_hint));
			mCommonDialog.setSingleChoiceListViewDatas(mHotelArriveDateList);
			mCommonDialog.getListView().setItemChecked(mSelectArriveIndex, true);
		}
		mDialogType = DIALOG_TYPE_ARRIVE_DATE;
		mCommonDialog.show();
	}
	
	private class SubmitHotelOrderTask extends AsyncTask<Void, Void, TC_Response_SubmitHotelOrder> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			mHandler.sendEmptyMessage(MSG_SHOW_DIALOG_ACTION);
		}
		
		@Override
		protected TC_Response_SubmitHotelOrder doInBackground(Void... arg0) {
			TC_Request_SubmitHotelOrder submitOrderRequestBody = new TC_Request_SubmitHotelOrder();
			
			submitOrderRequestBody.setHotelId(mHotelId);
			submitOrderRequestBody.setRoomTypeId(mRoomTypedId);
			submitOrderRequestBody.setPolicyId(mPolicyId);
			submitOrderRequestBody.setDaysAmountPrice(mRoomAdviceAmount.replace(";", ","));
			submitOrderRequestBody.setComeDate(mComeDate);
			submitOrderRequestBody.setLeaveDate(mLeaveDate);
			submitOrderRequestBody.setRooms(Integer.valueOf(mHotelRoomsList[mSelectRoomIndex]));
			submitOrderRequestBody.setArriveTime(mHotelArriveDatDatalList[mSelectArriveIndex]);
			submitOrderRequestBody.setTotalAmountPrice(String.valueOf(mTotalAmount * Integer.valueOf(mHotelRoomsList[mSelectRoomIndex]))); //new DecimalFormat("0").format()
			submitOrderRequestBody.setContactName(mComeName);
			submitOrderRequestBody.setContactMobile(mMobile);
			submitOrderRequestBody.setGuestName(mComeName);
			submitOrderRequestBody.setGuestMobile(mMobile);
			submitOrderRequestBody.setOrderIP("192.168.1.108");
			submitOrderRequestBody.setIsReturnOrderInfo(1);
			
			String requestBody = submitOrderRequestBody.getBody();
			String requestHead = TC_Request_DataFactory.getRequestHead("SubmitHotelOrder");
			String url = TC_Common.TC_URL_SEARCH_ORDER;
			TCRequestData requestData = new TCRequestData(requestHead, requestBody);
			Object object = TC_Http.getDataByPost(url, requestData.getReqeustData(), TC_Response_SubmitHotelOrder.class);
			if( object == null ){
				return null;
			}
			return (TC_Response_SubmitHotelOrder) object;
		}
		
		protected void onPostExecute(TC_Response_SubmitHotelOrder result) {
			mHandler.sendEmptyMessage(MSG_DISMISS_DIALOG_ACTION);
			if( result == null ){
				return;
			}
			//提交过订单就 保存最近一次的入住人姓名
			mSharedPreferences.edit().putString(LAST_ORDER_INFO_NAME_TAG, mComeName).commit();
			mSharedPreferences.edit().putString(LAST_ORDER_INFO_MOBILE_TAG, mMobile).commit();
			
			String resultType = result.getRspType();
			String rspDesc = result.getRspDesc();
			String serialId = "";
			double amount = 0;
			if( "0".equals(resultType) ){
				// 成功订单
				if( result.getHotelorder() != null ){
					serialId = result.getHotelorder().getSerialId();
					amount = result.getHotelorder().getAmount();
					
					//add xcx 2014-12-30 start 统计埋点
					MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
							UMengEventIds.DISCOVER_YELLOWPAGE_HOTEL_ORDER_SUBMIT_SUCCESS);
					//add xcx 2014-12-30 end 统计埋点
				}
			}else if( "1".equals(resultType) ){
				// 重复订单
				if( result.getSameOrderInfoList() != null && result.getSameOrderInfoList().size() > 0 ){
					List<TC_HotelSameOrderBean> sameOrderInfoList = result.getSameOrderInfoList();
					if( sameOrderInfoList != null && sameOrderInfoList.size() > 0){
						serialId = sameOrderInfoList.get(0).getTcOrder();
					}
				}
			}
			if( TextUtils.isEmpty(serialId) ){
				//订单编号为空，则也提示异常（注：resultType=1时，serialId也有可能为空）
				LogUtil.i(TAG, "getHortelOrder resultType: " + resultType + " , error.");
				Toast.makeText(YellowPageHotelOrderActivity.this, rspDesc, Toast.LENGTH_SHORT).show();

				//add xcx 2014-12-30 start 统计埋点
                MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
                        UMengEventIds.DISCOVER_YELLOWPAGE_HOTEL_ORDER_SUBMIT_FAIL);
                //add xcx 2014-12-30 end 统计埋点
				return;
			}
			
			LogUtil.i(TAG, "getHortelOrder resultType: " + resultType + " ,serialId: " + serialId);
			Intent intent = new Intent(YellowPageHotelOrderActivity.this, YellowPageHotelOrderResultActivity.class);
			intent.putExtra("CityName", mCityName);
			intent.putExtra("Order_Status", resultType);
			intent.putExtra("Order_SerialId", serialId);
			intent.putExtra("Order_HotelName", mHotelName);
		    intent.putExtra("HotelAddress", mHotelAddress);
			intent.putExtra("HotelImg", mHotelImg);
			intent.putExtra("Order_Hotel_InDate", mComeDate);
			intent.putExtra("Order_Hotel_OutDate", mLeaveDate);
			intent.putExtra("Order_Hotel_People", mComeName);
			intent.putExtra("Order_Order_ResultInfo", rspDesc);
			intent.putExtra("Order_TotalAmount", amount);
			
			startActivity(intent);
		};
	}
	
	/**
	 * 提交酒店订单
	 */
	private void submitHotelOrder(){
		if( !NetUtil.isNetworkAvailable(this) ){ //网络异常
			Utils.showToast(this, R.string.putao_no_net, false);
			return;
		}
		mComeName = mComeNameEDit.getText().toString();
		if( TextUtils.isEmpty(mComeName) ){
			Toast.makeText(this, R.string.putao_hotelorder_submit_no_comename_hint, Toast.LENGTH_SHORT).show();
			return;
		}
		mMobile = mMobileEDit.getText().toString();
		if( TextUtils.isEmpty(mMobile) ){
			Toast.makeText(this, R.string.putao_hotelorder_submit_no_mobile_hint, Toast.LENGTH_SHORT).show();
			return;
		}
		if ( !TelAreaUtil.getInstance().isValidMobile(mMobile) ) {
			Toast.makeText(this, R.string.putao_hotelorder_submit_no_validmobile_hint, Toast.LENGTH_SHORT).show();
			return;
		}
		
		//担保类型(0-无担保；1-担保冻结；2-担保预付；3-代收代付)(用来判断是否需要支付再提交订单)
		LogUtil.i(TAG, "submitHotelOrder guaranteeType = " + mGuaranteeType);
        if( mSubmitOrderType == TC_Hotel_OrderState_Util.SUBMIT_ORDER_TYPE_NONE ){
        	//提交订单(0-无担保)
        	if ((mSubmitDataTask != null && mSubmitDataTask.getStatus() != AsyncTask.Status.RUNNING)
					|| mSubmitDataTask == null) {
				mSubmitDataTask = new SubmitHotelOrderTask();
				mSubmitDataTask.execute();// 开始刷新数据
			}
        }else{
        	//信用卡预付,跳转到支付页面(1-担保冻结；2-担保预付；3-代收代付)
        	Intent intent = new Intent(this, YellowPageHotelPayActivity.class);
			intent.putExtra("CityName", mCityName);
			intent.putExtra("HotelId", mHotelId);
			intent.putExtra("HotelName", mHotelName);
			intent.putExtra("HotelImg", mHotelImg);
		    intent.putExtra("HotelAddress", mHotelAddress);
			intent.putExtra("RoomTypedId", mRoomTypedId);
			intent.putExtra("PolicyId", mPolicyId);
			intent.putExtra("DaysAmountPrice", mRoomAdviceAmount.replace(";", ","));
			intent.putExtra("ComeDate", mComeDate);
			intent.putExtra("LeaveDate", mLeaveDate);
			intent.putExtra("Rooms", Integer.valueOf(mHotelRoomsList[mSelectRoomIndex]));
			intent.putExtra("ArriveTime", mHotelArriveDatDatalList[mSelectArriveIndex]);
			intent.putExtra("TotalAmountPrice", String.valueOf(mTotalAmount * Integer.valueOf(mHotelRoomsList[mSelectRoomIndex])));
			intent.putExtra("ContactName", mComeName);
			intent.putExtra("ContactMobile", mMobile);
			intent.putExtra("OrderIP", "192.168.1.108");
			intent.putExtra("IsReturnOrderInfo", 1);
			startActivity(intent);
        }
	}
	
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch(what){
			case MSG_SHOW_DIALOG_ACTION:
				if (mProgressDialog == null) {
					mProgressDialog = new ProgressDialog(YellowPageHotelOrderActivity.this);
					mProgressDialog.setMessage(getString(R.string.putao_hotelorderdetail_submiting_order));
				}
				mProgressDialog.show();
				break;
			case MSG_DISMISS_DIALOG_ACTION:
				if (mProgressDialog != null && mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
				break;
            case MSG_SHOW_SELECT_CONTACT_PHONE_ACTION:
                Bundle bundle = msg.getData();
                if (bundle == null) {
                    return;
                }
                String phoneNumTemp = bundle.getString("PhoneNum");
                String contactName = bundle.getString("ContactName");
                if (TextUtils.isEmpty(phoneNumTemp)) {
                    return;
                }
                String phoneNum = ContactsHubUtils.formatIPNumber(phoneNumTemp,
                        YellowPageHotelOrderActivity.this);
                LogUtil.i(TAG, "select contact phonenum: " + phoneNum);
                int selection = phoneNum.length();
                if (selection > VALID_PHONENUM_SIZE) {
                    selection = VALID_PHONENUM_SIZE;
                    phoneNum = phoneNum.substring(0, VALID_PHONENUM_SIZE);
                }
                mMobileEDit.setText(phoneNum);
                mMobileEDit.setSelection(selection);
                if (TextUtils.isEmpty(phoneNum) || phoneNum.length() < VALID_PHONENUM_SIZE
                        || !TelAreaUtil.getInstance().isValidMobile(phoneNum)) {
                    // 如果号码为空或号码长度小于11（为不完整号码）  或者 检测是否是合法的号码
//                    showPriceData(PRICE_STATE_PRICERANGE);
//                    showPhoneNumData(PHONE_STATE_ERROR, "");
                }
                break;	
				
				default:
					break;
			
			}
		}
	};
	
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		int viewId = view.getId();
		if (viewId == R.id.back_layout) {
			finish();
		} else if( viewId == R.id.hotelorder_roomname_layout ){
			// 房间数
			showStayRoomsLayout();
			//add xcx 2014-12-30 start 统计埋点
            MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
                    UMengEventIds.DISCOVER_YELLOWPAGE_HOTEL_ORDER_ROOM_AMOUNT_SELECT);
            //add xcx 2014-12-30 end 统计埋点
		} else if( viewId == R.id.hotelorder_arrive_date_layout ){
			// 到店时间
			showArriveDateLayout();
            //add xcx 2014-12-30 start 统计埋点
            MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
                    UMengEventIds.DISCOVER_YELLOWPAGE_HOTEL_ORDER_LATEST_IN_DATE);
            //add xcx 2014-12-30 end 统计埋点
		} else if( viewId == R.id.hotelorder_submit ){
			submitHotelOrder();
			//add xcx 2014-12-30 start 统计埋点
            MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
                    UMengEventIds.DISCOVER_YELLOWPAGE_HOTEL_ORDER_SUBMIT);
            //add xcx 2014-12-30 end 统计埋点
		} else if( viewId == R.id.hotelorder_come_name_tip) {
		    showNameTip();
		} else if (viewId == R.id.clear_search) {
            if (TextUtils.isEmpty(mMobileEDit.getText().toString())) {
                showInputManager(false);
                try {
                    MobclickAgentUtil.onEvent(this, UMengEventIds.DISCOVER_YELLOWPAGE_HOTEL_SELECT_CONTACT);
                    Intent intent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
                    intent.setType("vnd.android.cursor.dir/phone");
                    intent.setType("vnd.android.cursor.dir/phone_v2");
                    startActivityForResult(intent, REQUEST_CONTACT_INFO);
                } catch (Exception e) {

                }
            } else {
                mMobileEDit.setText("");
            }
        } else if(viewId == R.id.imgView_priceTips||viewId == R.id.hotelorder_amount){
        	showPriceDialog();
        }
		
	}
	
	
	/**
	 * 显示价格明细 add by ls2015-01-21
	 */
	@SuppressLint("SimpleDateFormat")
	private void showPriceDialog() {
		Calendar come = CalendarUtil.convertToCalendar(mComeDate);
		int tempDay =come.get(Calendar.DATE);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				CalendarUtil.DATE_FORMATTER);
		ArrayList<String> days = new ArrayList<String>();
		for (int i = 0; i <mStayDays; i++) {
			Calendar temp = Calendar.getInstance();
			temp.set(Calendar.DATE, tempDay+i);
			days.add(simpleDateFormat.format(temp.getTime()));
		}
		String[] amountList =null;
		if (!TextUtils.isEmpty(mRoomAdviceAmount)) {
			if (mRoomAdviceAmount.contains(";")) {
				amountList = mRoomAdviceAmount.split(";");
			}else{
				amountList = new String[]{mRoomAdviceAmount};
			}
		}
		
		LogUtil.d(TAG, "days="+days.size()+"amountList="+amountList.length);

		mPriceListViews = mPricesDialog.getListView();
//		mPricesDialog.setListViewDatas(amountList);
		PriceAdapter pAdapter = new PriceAdapter(days,amountList);
		mPriceListViews.setAdapter(pAdapter);
		mPriceListViews.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (mPricesDialog != null && mPricesDialog.isShowing()) {
					mPricesDialog.dismiss();
				}
			}
		});
		mPricesDialog.setTitle(getString(R.string.putao_hotelorder_prices_detail_dialog_title));
		mPricesDialog.show();

	}
	
	/**add by ls end*/

	private void showNameTip() {
		showInputManager(false);
	    LayoutInflater inflater = (LayoutInflater)              
                this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);     
        final View vPopWindow=inflater.inflate(R.layout.putao_hotelorder_come_name_tip, null, false);  
        final PopupWindow popWindow =  new PopupWindow(vPopWindow, LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT,true);
        Button okButton = (Button)vPopWindow.findViewById(R.id.hotelorder_come_name_tip_button);
        okButton.setOnClickListener(new View.OnClickListener() {
             
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                popWindow.dismiss();
            }
        });
         
        popWindow.showAtLocation(this.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
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
	
    private void showInputManager(boolean isNeedShow) {
        if (mInputManager == null) {
            mInputManager = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        if (isNeedShow) {
            mInputManager.showSoftInput(mMobileEDit, InputMethodManager.SHOW_IMPLICIT);
        } else {
            if( mInputManager.isActive() ){
                mInputManager.hideSoftInputFromWindow(mMobileEDit.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // TODO Auto-generated method stub
        if (intent == null) {
            return;
        }
        if (REQUEST_CONTACT_INFO == requestCode) {
            Uri uri = intent.getData();
            ContentResolver contentResolver = this.getContentResolver();
            Cursor cursor = contentResolver.query(uri, null, null, null, null);
            if (cursor == null) {
                return;
            }
            String phoneNum = "";
            String contactName = "";
            boolean isFailed = false;
            try {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(Phones.NUMBER);
                if (columnIndex == -1) {
                    // 小米 联系人读取方式
                    columnIndex = cursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                }
                phoneNum = cursor.getString(columnIndex);

                int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                if (nameIndex != -1) {
                    contactName = cursor.getString(nameIndex);
                }
            } catch (Exception e) {
                phoneNum = "";
                isFailed = true;
                LogUtil.d(TAG, "onActivityResult Exception...");
            } finally {
                try {
                    cursor.close();
                } catch (Exception e) {
                }
            }
            if (TextUtils.isEmpty(phoneNum)) {
                if (isFailed) {
                    Utils.showToast(this, R.string.putao_charge_getcontact_hint_error, false);
                } else {
                    Utils.showToast(this, R.string.putao_charge_getcontact_hint_empty, false);
                }
            } else {
                Message msg = mHandler.obtainMessage();
                msg.what = MSG_SHOW_SELECT_CONTACT_PHONE_ACTION;
                Bundle bundle = new Bundle();
                bundle.putString("PhoneNum", phoneNum);
                bundle.putString("ContactName", contactName);
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
        }
    }
    
    /**
	 * 根据当前时间过滤掉已过期的时间
	 */
	private void initCurrentArriveDate(){
		String[] hotelArriveDateList = getResources().getStringArray(R.array.putao_hotelorder_arrivedate);
		String[] hotelArriveDatDatalList = getResources().getStringArray(R.array.putao_hotelorder_arrivedate_data);
		int len = hotelArriveDateList.length;
		Calendar calendar = Calendar.getInstance();
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		
		String todayDate = CalendarUtil.getNowDateStr();
		long stayDays = CalendarUtil.getGapBetweenTwoDay(todayDate, mComeDate);
		if( stayDays > 0 ){
			//说明入住时间不是今天，则酒店最晚到店时间就不受当天日期的限制
			hour = 0;
		}
		if( hour < 18 ){
			mHotelArriveDateList = hotelArriveDateList;
			mHotelArriveDatDatalList = hotelArriveDatDatalList;
		}else if( hour < 20 ){
			mHotelArriveDateList = new String[len-1];
			mHotelArriveDatDatalList = new String[len-1];
			for(int i = 0; i < len-1; i++){
				mHotelArriveDateList[i] = hotelArriveDateList[i+1];
				mHotelArriveDatDatalList[i] = hotelArriveDatDatalList[i+1];
			}
		}else if( hour < 22 ){
			mHotelArriveDateList = new String[len-2];
			mHotelArriveDatDatalList = new String[len-2];
			for(int i = 0; i < len-2; i++){
				mHotelArriveDateList[i] = hotelArriveDateList[i+2];
				mHotelArriveDatDatalList[i] = hotelArriveDatDatalList[i+2];
			}
		}else if( hour < 24 ){
			mHotelArriveDateList = new String[len-3];
			mHotelArriveDatDatalList = new String[len-3];
			for(int i = 0; i < len-3; i++){
				mHotelArriveDateList[i] = hotelArriveDateList[i+3];
				mHotelArriveDatDatalList[i] = hotelArriveDatDatalList[i+3];
			}
		}
	}
	
	/**
	 * 判断是否需要担保、预付等状态
	 */
	private void initOrderStateData(){
		int userSelectArriveTime = 18; //用户选择的最晚到店时间(小时)
		String arriveData = mHotelArriveDatDatalList[mSelectArriveIndex]; //1900-01-01 18:00
		String[] hourStrList = arriveData.split(":| ");
		if( hourStrList.length > 1 ){
			String hourStr = hourStrList[1];
			userSelectArriveTime = Integer.valueOf(hourStr);
			if( userSelectArriveTime <= 5 ){
				//第二天凌晨
				userSelectArriveTime += 24;
			}
		}
		
        mSubmitOrderType = TC_Hotel_OrderState_Util.checkOrderStateData(mGuaranteeType, mDanBaoType, mOverTime, userSelectArriveTime);
        if( mSubmitOrderType == TC_Hotel_OrderState_Util.SUBMIT_ORDER_TYPE_NONE ){
        	//提交订单(0-无担保)
        	mSubmitBtn.setText(R.string.putao_hotelorder_submit);
        }else if( mSubmitOrderType == TC_Hotel_OrderState_Util.SUBMIT_ORDER_TYPE_DANBAO ){
        	//担保(1-担保冻结；2-担保预付)
        	mSubmitBtn.setText(R.string.putao_hotelorder_prepare_danbao);
        }else{
        	//信用卡预付(3-代收代付)
        	mSubmitBtn.setText(R.string.putao_hotelorder_prepare_pay);
        }
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(mSubmitDataTask != null){
			mSubmitDataTask.cancel(true);
			mSubmitDataTask = null;
    	}
	}

    @Override
    public Integer getAdId() {
        return AdCode.ADCODE_YellowPageHotelOrderActivity;
    }
    
    /**
     * add by ls 2015-01-21 显示价格明细
     * @author Administrator
     */
    public class PriceAdapter extends BaseAdapter {
    	
    	private ArrayList<String> staydays;
    	private String [] daysPrices;
    	private int roomsNumber ;
    	public PriceAdapter(ArrayList<String> staydays,String [] daysPrices){
    		this.staydays =staydays;
    		this.daysPrices=daysPrices;
    		this.roomsNumber = Integer.valueOf(mHotelRoomsList[mSelectRoomIndex]);// 房间间数
    	}

		@Override
		public int getCount() {
			if(staydays!=null&&daysPrices!=null&&daysPrices.length>0&&staydays.size()>0){
				return staydays.size();
			}
			return 0;
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
			// TODO Auto-generated method stub
			DialogHolder holder =null;
			if(convertView==null){
				holder = new DialogHolder();
				convertView = View.inflate(YellowPageHotelOrderActivity.this, R.layout.putao_yellow_page_hotelorder_pricedetail_item, null);
				holder.txt_stayDay =(TextView) convertView.findViewById(R.id.txt_stay);
				holder.txt_price = (TextView) convertView.findViewById(R.id.txt_prices);
				convertView.setTag(holder);
			}else{
				holder =(DialogHolder) convertView.getTag();
			}
			if(daysPrices.length==1){
				holder.txt_price.setText(getString(R.string.putao_hotelorder_prices_detail, daysPrices[0],Integer.valueOf(mHotelRoomsList[mSelectRoomIndex])));
			}else if(daysPrices.length>1){
				holder.txt_price.setText(getString(R.string.putao_hotelorder_prices_detail, daysPrices[position],Integer.valueOf(mHotelRoomsList[mSelectRoomIndex])));
			}
			holder.txt_stayDay.setText(staydays.get(position));
			
			return convertView;
		}
    }
    
    public class DialogHolder{
    	public TextView txt_stayDay;
    	public TextView txt_price;
    }
    /**add by ls end*/
    
    
}
