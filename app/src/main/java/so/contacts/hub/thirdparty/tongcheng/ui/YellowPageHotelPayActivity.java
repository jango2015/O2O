package so.contacts.hub.thirdparty.tongcheng.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.yulong.android.contacts.discover.R;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import so.contacts.hub.ContactsApp;
import so.contacts.hub.ad.AdCode;
import so.contacts.hub.remind.BaseRemindActivity;
import so.contacts.hub.thirdparty.tongcheng.bean.TCRequestData;
import so.contacts.hub.thirdparty.tongcheng.bean.TC_HotelSameOrderBean;
import so.contacts.hub.thirdparty.tongcheng.bean.TC_Request_SubmitHotelOrder;
import so.contacts.hub.thirdparty.tongcheng.bean.TC_Response_SubmitHotelOrder;
import so.contacts.hub.thirdparty.tongcheng.util.AES;
import so.contacts.hub.thirdparty.tongcheng.util.TC_Common;
import so.contacts.hub.thirdparty.tongcheng.util.TC_Http;
import so.contacts.hub.thirdparty.tongcheng.util.TC_Request_DataFactory;
import so.contacts.hub.thirdparty.tongcheng.util.TC_Tool;
import so.contacts.hub.util.CalendarUtil;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.NetUtil;
import so.contacts.hub.util.TelAreaUtil;
import so.contacts.hub.util.UMengEventIds;
import so.contacts.hub.util.Utils;
import so.contacts.hub.widget.CommonDialog;
import so.contacts.hub.widget.CommonDialogFactory;
import so.contacts.hub.widget.CustomYearAndMonthDialog;
import so.contacts.hub.widget.CustomYearAndMonthDialog.IGetYearAndMonthCallback;
import so.contacts.hub.widget.ProgressDialog;

public class YellowPageHotelPayActivity extends BaseRemindActivity implements OnClickListener {
	
	private static final String TAG = "YellowPageHotelPayActivity";
	
	/** view start */
	private LinearLayout mPayBankLayout = null;
	private TextView mPayBankTView = null;
	private EditText mPayCardNumTView = null;
	private EditText mPaySecurityCodeTView = null;
	private LinearLayout mPayValidityLayout = null;
	private TextView mPayValidityTView = null;
	private EditText mPayNameTView = null;
	private EditText mPayMobileTView = null;
	private LinearLayout mPayCertificateTypeLayout = null;
	private TextView mPayCertificateTypeTView = null;
	private EditText mPayCertificateNumTView = null;
	private TextView mPayMoneyTView = null;
	private Button mSubmitBtn = null;
	/** view end */
	
	/** data start */
	private String mCityName = null;
	private String mHotelId = null;
	private String mHotelName = null;
	private String mHotelImg = null;
	private String mHotelAddress = null;
	private String mRoomTypedId = null;
	private String mPolicyId = null;
	private String mRoomAdviceAmount = null;
	private String mComeDate = null;
	private String mLeaveDate = null;
	private int mRooms = 0;
	private String mArriveTime = null;
	private String mTotalAmountPrice = null;
	private String mContactName = null;
	private String mContactMobile = null;
	private String mOrderIP = null;
	private int mIsReturnOrderInfo = 0;
	
	private int mCredicardBankIndex = -1;
	private String mCredicardNum = null;
	private String mSecurityCode = null; //短信验证码
	private String mCredicardValidity = null; //  "2013-09-01"
	private String mMasterContactName = null;
	private String mMasterContactMobile = null;
	private int mCredicardValidityMonth = -1;
	private int mCredicardValidityYear = -1;
	private int mCertificateTypeIndex = -1;
	private String mCertificateNum = null;
	/** data end */
	
	/** tag data start. */
	private CommonDialog mCommonDialog = null; // 弹出的选择框
	private String[] mCreditcardBankList = null;
	private String[] mCertificateTypeList = null;

	/**
	 * [1]: 选择信用卡银行
	 * [2]: 选择证件类型
	 */
	private int mDialogType = 0;
	
	private static final int DIALOG_TYPE_CREDICARD = 1;
	private static final int DIALOG_TYPE_CERTIFICATETYPE = 2;

	private SubmitHotelOrderTask mSubmitDataTask = null;

	private ProgressDialog mProgressDialog = null;		// 等待框
	private CustomYearAndMonthDialog mDateSelectDialog = null;

	private static final int MSG_SHOW_DIALOG_ACTION = 0x2001;
	private static final int MSG_DISMISS_DIALOG_ACTION = 0x2002;
	private static final int MSG_SHOW_VALIDITY_DATE_ACTION = 0x2003;
	/** tag data end. */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.putao_yellow_page_hotel_pay_layout);
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
		mRoomTypedId = intent.getStringExtra("RoomTypedId");
		mPolicyId = intent.getStringExtra("PolicyId");
		mRoomAdviceAmount = intent.getStringExtra("DaysAmountPrice");
		mComeDate = intent.getStringExtra("ComeDate");
		mLeaveDate = intent.getStringExtra("LeaveDate");
		mRooms = intent.getIntExtra("Rooms", 0);
		mArriveTime = intent.getStringExtra("ArriveTime");
		mTotalAmountPrice = intent.getStringExtra("TotalAmountPrice");
		mContactName = intent.getStringExtra("ContactName");
		mContactMobile = intent.getStringExtra("ContactMobile");
		mOrderIP = intent.getStringExtra("OrderIP");
		mIsReturnOrderInfo = intent.getIntExtra("IsReturnOrderInfo", 1);
	}
	
	private void initView(){
        if( TextUtils.isEmpty(mTitleContent) ){
            mTitleContent = getResources().getString(R.string.putao_hotelpay_title);
        }
        ((TextView) findViewById(R.id.title)).setText(mTitleContent);
        findViewById(R.id.back_layout).setOnClickListener(this);
		
        mPayBankLayout = (LinearLayout) findViewById(R.id.hotel_pay_bank_layout);
        mPayBankTView = (TextView) findViewById(R.id.hotel_pay_bank_name);
        mPayBankLayout.setOnClickListener(this);
        mPayCardNumTView = (EditText) findViewById(R.id.hotel_pay_cardnum);
        mPaySecurityCodeTView = (EditText) findViewById(R.id.hotel_pay_securitycode);
        mPayValidityLayout = (LinearLayout) findViewById(R.id.hotel_pay_validity_layout);
        mPayValidityTView = (TextView) findViewById(R.id.hotel_pay_validity);
        mPayValidityLayout.setOnClickListener(this);
        mPayNameTView = (EditText) findViewById(R.id.hotel_pay_name);
        mPayMobileTView = (EditText) findViewById(R.id.hotel_pay_mobile);
        mPayCertificateTypeLayout = (LinearLayout) findViewById(R.id.hotel_pay_certificate_type_layout);
        mPayCertificateTypeTView = (TextView) findViewById(R.id.hotel_pay_certificate_type);
        mPayCertificateTypeLayout.setOnClickListener(this);
        mPayCertificateNumTView = (EditText) findViewById(R.id.hotel_pay_certificate_num);
        mPayMoneyTView = (TextView) findViewById(R.id.hotel_pay_money);
        mSubmitBtn = (Button) findViewById(R.id.hotel_pay_submit_btn);
        mSubmitBtn.setOnClickListener(this);
		mCommonDialog = CommonDialogFactory.getListCommonDialog(this);
		mCommonDialog.setListViewItemClickListener(mOnDialogItemClickListener);
		
		mDateSelectDialog = new CustomYearAndMonthDialog(this);
		mDateSelectDialog.setIGetYearAndMonthCallback(new IGetYearAndMonthCallback() {
			
			@Override
			public void getYearAndMonthCallback(int year, int month) {
				// TODO Auto-generated method stub
				mCredicardValidityYear = year;
				mCredicardValidityMonth = month;
				mHandler.sendEmptyMessage(MSG_SHOW_VALIDITY_DATE_ACTION);
			}
		});
	}
	
	private void initData(){
		mCreditcardBankList = getResources().getStringArray(R.array.putao_hotelpay_creditcard_bank);
		mCertificateTypeList = getResources().getStringArray(R.array.putao_hotelpay_certificate_type);
		
		mPayNameTView.setText(mContactName);
		mPayMobileTView.setText(mContactMobile);
		mPayMoneyTView.setText(getString(R.string.putao_hotelpay_pay_money, mTotalAmountPrice));
	}
	
	private OnItemClickListener mOnDialogItemClickListener = new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long arg3) {
			// TODO Auto-generated method stub
			if( mDialogType == DIALOG_TYPE_CREDICARD ){
				mCredicardBankIndex = position;
				mPayBankTView.setText(mCreditcardBankList[mCredicardBankIndex]);
				mPayBankTView.setTextColor(Color.BLACK);
			} else if( mDialogType == DIALOG_TYPE_CERTIFICATETYPE ){
				mCertificateTypeIndex = position;
				mPayCertificateTypeTView.setText(mCertificateTypeList[mCertificateTypeIndex]);
				mPayCertificateTypeTView.setTextColor(Color.BLACK);
			}
			if( mCommonDialog != null ){
				mCommonDialog.dismiss();
			}
		}
	};
	
	/**
	 * 选择信用卡有效期
	 */
	private void showCardValidityLayout(){
		if( mCredicardValidityMonth == -1 ){
			Calendar calendar = Calendar.getInstance(Locale.CHINA);
			calendar.setTime(new Date());
			mCredicardValidityYear = calendar.get(Calendar.YEAR);
			mCredicardValidityMonth = calendar.get(Calendar.MONTH);
		}
		
		if( mDateSelectDialog != null ){
			mDateSelectDialog.setYearAndMonth(mCredicardValidityYear, mCredicardValidityMonth);
			mDateSelectDialog.show();
		}
	}
	
	/**
	 * 显示信用卡发卡银行
	 */
	private void showCreditcardBankLayout(){
		if( mCredicardBankIndex == -1 ){
			mCredicardBankIndex = 0; //第一次初始化为第一个
		}
		if( mCommonDialog != null ){
			mCommonDialog.setTitle(getResources().getString(R.string.putao_hotelpay_credicard_selector));
			mCommonDialog.setSingleChoiceListViewDatas(mCreditcardBankList);
			mCommonDialog.getListView().setItemChecked(mCredicardBankIndex, true);
		}
		mDialogType = DIALOG_TYPE_CREDICARD;
		mCommonDialog.show();
	}
	
	/**
	 * 显示证件类型
	 */
	private void showCertificateTypeLayout(){
		if( mCertificateTypeIndex == -1 ){
			mCertificateTypeIndex = 0;
		}
		if( mCommonDialog != null ){
			mCommonDialog.setTitle(getResources().getString(R.string.putao_hotelpay_certificatetype_selector));
			mCommonDialog.setSingleChoiceListViewDatas(mCertificateTypeList);
			mCommonDialog.getListView().setItemChecked(mCertificateTypeIndex, true);
		}
		mDialogType = DIALOG_TYPE_CERTIFICATETYPE;
		mCommonDialog.show();
	}
	
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch(what){
			case MSG_SHOW_DIALOG_ACTION:
				if (mProgressDialog == null) {
					mProgressDialog = new ProgressDialog(YellowPageHotelPayActivity.this);
					mProgressDialog.setMessage(getString(R.string.putao_hotelorderdetail_submiting_order));
				}
				mProgressDialog.show();
				break;
			case MSG_DISMISS_DIALOG_ACTION:
				if (mProgressDialog != null && mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
				break;
			case MSG_SHOW_VALIDITY_DATE_ACTION:
				mPayValidityTView.setText(getAppointDate(mCredicardValidityYear, mCredicardValidityMonth));
				mPayValidityTView.setTextColor(Color.BLACK);
				break;
			default:
				break;
			}
		}
	};
	
	private static String getAppointDate(int year, int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month-1, 0);
		calendar.add(calendar.DATE, 1); 
		return new SimpleDateFormat("MM/yy").format(calendar.getTime());
	}

	/**
	 * 提交酒店订单
	 */
	private void submitHotelOrder(){
		if( !NetUtil.isNetworkAvailable(this) ){ //网络异常
			Utils.showToast(this, R.string.putao_no_net, false);
			return;
		}
		mCredicardNum = mPayCardNumTView.getText().toString();
		mSecurityCode = mPaySecurityCodeTView.getText().toString();
		mCertificateNum = mPayCertificateNumTView.getText().toString();
		mMasterContactName = mPayNameTView.getText().toString();
		mMasterContactMobile = mPayMobileTView.getText().toString();
		if( mCredicardValidityYear != -1 ){
			mCredicardValidity = CalendarUtil.getAppointDate(mCredicardValidityYear, mCredicardValidityMonth, 1);
		}
		if( mCredicardBankIndex == -1 ){
			Toast.makeText(this, R.string.putao_hotelpay_select_cardbank, Toast.LENGTH_SHORT).show();
			return;
		}else if( TextUtils.isEmpty(mCredicardNum) ){
			Toast.makeText(this, R.string.putao_hotelpay_select_cardnum, Toast.LENGTH_SHORT).show();
			return;
		}else if( TextUtils.isEmpty(mSecurityCode) ){
			Toast.makeText(this, R.string.putao_hotelpay_select_securitycode, Toast.LENGTH_SHORT).show();
			return;
		}else if( TextUtils.isEmpty(mCredicardValidity) ){
			Toast.makeText(this, R.string.putao_hotelpay_select_cardvalidity, Toast.LENGTH_SHORT).show();
			return;
		}else if( TextUtils.isEmpty(mMasterContactName) ){
			Toast.makeText(this, R.string.putao_hotelpay_name_hint, Toast.LENGTH_SHORT).show();
			return;
		}else if( TextUtils.isEmpty(mMasterContactMobile) || !TelAreaUtil.getInstance().isValidMobile(mMasterContactMobile) ){
			Toast.makeText(this, R.string.putao_hotelpay_mobile_hint, Toast.LENGTH_SHORT).show();
			return;
		}else if( mCertificateTypeIndex == -1 ){
			Toast.makeText(this, R.string.putao_hotelpay_select_certificatetype, Toast.LENGTH_SHORT).show();
			return;
		}else if( TextUtils.isEmpty(mCertificateNum) ){
			Toast.makeText(this, R.string.putao_hotelpay_select_certificatenum, Toast.LENGTH_SHORT).show();
			return;
		}
		// 提交订单
		if ((mSubmitDataTask != null && mSubmitDataTask.getStatus() != AsyncTask.Status.RUNNING)
				|| mSubmitDataTask == null) {
			mSubmitDataTask = new SubmitHotelOrderTask();
			mSubmitDataTask.execute();// 开始刷新数据
		}
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
			// TODO Auto-generated method stub
			TC_Request_SubmitHotelOrder submitOrderRequestBody = new TC_Request_SubmitHotelOrder();
			String serviceName = "SubmitHotelOrder";
			String reqTime = TC_Tool.getFormatData();
			String digitalSign = TC_Request_DataFactory.getRequestDigitalSign(serviceName, reqTime); //获取数字签名
			String strRequestHead = TC_Request_DataFactory.getRequestHeadBySignAndTime(serviceName, digitalSign, reqTime); //获取请求头
			
			submitOrderRequestBody.setHotelId(mHotelId);
			submitOrderRequestBody.setRoomTypeId(mRoomTypedId);
			submitOrderRequestBody.setPolicyId(mPolicyId);
			submitOrderRequestBody.setDaysAmountPrice(mRoomAdviceAmount);
			submitOrderRequestBody.setComeDate(mComeDate);
			submitOrderRequestBody.setLeaveDate(mLeaveDate);
			submitOrderRequestBody.setRooms(mRooms);
			submitOrderRequestBody.setArriveTime(mArriveTime);
			submitOrderRequestBody.setTotalAmountPrice(mTotalAmountPrice);
			submitOrderRequestBody.setContactName(mContactName);
			submitOrderRequestBody.setContactMobile(mContactMobile);
			submitOrderRequestBody.setGuestName(mContactName);
			submitOrderRequestBody.setGuestMobile(mContactMobile);
			submitOrderRequestBody.setOrderIP(mOrderIP);
			submitOrderRequestBody.setIsReturnOrderInfo(mIsReturnOrderInfo);
			
			// 信用卡信息 start
			submitOrderRequestBody.setCardNumber(AES.Encrypt(mCredicardNum, digitalSign));
			submitOrderRequestBody.setCardType(AES.Encrypt(mCreditcardBankList[mCredicardBankIndex], digitalSign));
			submitOrderRequestBody.setValiCode(AES.Encrypt(mSecurityCode, digitalSign));
			submitOrderRequestBody.setMasterName(AES.Encrypt(mMasterContactName, digitalSign));
			submitOrderRequestBody.setMasterMobileNumber(AES.Encrypt(mMasterContactMobile, digitalSign));
			submitOrderRequestBody.setPeriodDate(AES.Encrypt(mCredicardValidity, digitalSign));
			submitOrderRequestBody.setCertificatesType(AES.Encrypt(mCertificateTypeList[mCertificateTypeIndex], digitalSign));
			submitOrderRequestBody.setCertificatesNumber(AES.Encrypt(mCertificateNum, digitalSign));
			// 信用卡信息 end
			
			String requestBody = submitOrderRequestBody.getBody();
			String url = TC_Common.TC_URL_SEARCH_ORDER;
			TCRequestData requestData = new TCRequestData(strRequestHead, requestBody);
			Object object = TC_Http.getDataByPost(url, requestData.getReqeustData(), TC_Response_SubmitHotelOrder.class);
			if( object == null ){
				return null;
			}
			return (TC_Response_SubmitHotelOrder) object;
		}
		
		@Override
		protected void onPostExecute(TC_Response_SubmitHotelOrder result) {
			// TODO Auto-generated method stub
			mHandler.sendEmptyMessage(MSG_DISMISS_DIALOG_ACTION);
			if( result == null ){
				return;
			}
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
				Toast.makeText(YellowPageHotelPayActivity.this, rspDesc, Toast.LENGTH_SHORT).show();

				//add xcx 2014-12-30 start 统计埋点
                MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
                        UMengEventIds.DISCOVER_YELLOWPAGE_HOTEL_ORDER_SUBMIT_FAIL);
                //add xcx 2014-12-30 end 统计埋点
				return;
			}
			
			LogUtil.i(TAG, "getHortelOrder resultType: " + resultType + " ,serialId: " + serialId);
			Intent intent = new Intent(YellowPageHotelPayActivity.this, YellowPageHotelOrderResultActivity.class);
			intent.putExtra("CityName", mCityName);
			intent.putExtra("Order_Status", resultType);
			intent.putExtra("Order_SerialId", serialId);
			intent.putExtra("Order_HotelName", mHotelName);
		    intent.putExtra("HotelAddress", mHotelAddress);
			intent.putExtra("HotelImg", mHotelImg);
			intent.putExtra("Order_Hotel_InDate", mComeDate);
			intent.putExtra("Order_Hotel_OutDate", mLeaveDate);
			intent.putExtra("Order_Hotel_People", mContactName);
			intent.putExtra("Order_Order_ResultInfo", rspDesc);
			intent.putExtra("Order_TotalAmount", amount);
			
			startActivity(intent);
		}
		
	}
	
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		int viewId = view.getId();
		if (viewId == R.id.back_layout) {
			finish();
		} else if( viewId == R.id.hotel_pay_bank_layout ){ //发卡银行
			showCreditcardBankLayout();
		} else if( viewId == R.id.hotel_pay_validity_layout ){//有效期
			showCardValidityLayout();
		} else if( viewId == R.id.hotel_pay_certificate_type_layout ){//证件类型
			showCertificateTypeLayout();
		} else if( viewId == R.id.hotel_pay_submit_btn ){//支付
			submitHotelOrder();
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if( mSubmitDataTask != null ){
			mSubmitDataTask.cancel(true);
			mSubmitDataTask = null;
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
