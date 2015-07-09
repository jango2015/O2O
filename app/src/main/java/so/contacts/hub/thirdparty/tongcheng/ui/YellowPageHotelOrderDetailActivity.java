package so.contacts.hub.thirdparty.tongcheng.ui;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import so.contacts.hub.core.Config;
import so.contacts.hub.msgcenter.PTMessageCenter;
import so.contacts.hub.msgcenter.PTOrderCenter;
import so.contacts.hub.msgcenter.bean.PTMessageBean;
import so.contacts.hub.msgcenter.bean.PTOrderBean;
import so.contacts.hub.net.PTHTTP;
import so.contacts.hub.net.SimpleRequestData;
import so.contacts.hub.remind.BaseRemindActivity;
import so.contacts.hub.thirdparty.tongcheng.bean.TCRequestData;
import so.contacts.hub.thirdparty.tongcheng.bean.TC_OrderDetailBean;
import so.contacts.hub.thirdparty.tongcheng.bean.TC_Request_CancelOrder;
import so.contacts.hub.thirdparty.tongcheng.bean.TC_Request_OrderDetail;
import so.contacts.hub.thirdparty.tongcheng.bean.TC_Response_CancelOrder;
import so.contacts.hub.thirdparty.tongcheng.bean.TC_Response_OrderDetail;
import so.contacts.hub.thirdparty.tongcheng.util.TC_Common;
import so.contacts.hub.thirdparty.tongcheng.util.TC_Http;
import so.contacts.hub.thirdparty.tongcheng.util.TC_Request_DataFactory;
import so.contacts.hub.ui.yellowpage.YellowPageJumpH5Activity;
import so.contacts.hub.util.CalendarUtil;
import so.contacts.hub.util.ContactsHubUtils;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.NetUtil;
import so.contacts.hub.util.Utils;
import so.contacts.hub.widget.CommonDialog;
import so.contacts.hub.widget.CommonDialogFactory;
import so.contacts.hub.widget.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.TextView;
import com.loader.DataLoader;
import com.loader.image.ImageLoaderFactory;
import com.yulong.android.contacts.discover.R;

public class YellowPageHotelOrderDetailActivity extends BaseRemindActivity implements OnClickListener {

	private static final String TAG = "YellowPageHotelOrderDetailActivity";
	
	private String mOrderSerialId = null;
	private int mOrderState = 0;
	private int mOldOrderState = 0;
	
	/** view start. */
	private TextView mHotelNameTView = null;
	private TextView mHotelRoomNameTView = null;
	private TextView mHotelInTView = null;
	private TextView mHotelOutTView = null;
	private TextView mHotelRetainTimeTView = null;
    private TextView mHotelPriceTView = null;
	private TextView mHotelStateTView = null;
	private TextView mHotelMoneyTView = null;
	private TextView mHotelSerialIdTView = null;
	private TextView mHotelOrderCreateTView = null;
	private TextView mHotelDescriptionTView = null;
	private TextView mHotelAddressTView = null;
    private TextView mHotelLastInDateTView = null;
    private TextView mHotelOrderTypeTView = null;
    private TextView mHotelComeNameTView = null;
	private TextView mHotelMobileTView = null;
	private TextView mHotelKefuTelTView = null;
	private TextView mHotelTaxiTView = null;
	private ImageView mHotelImgView = null;
	private LinearLayout mCancelOrderLayout = null;
	private ImageView mHotelLogoImgView = null;
	
	private LinearLayout mCardLayout = null;
	private LinearLayout mContentLayout = null;
	private LinearLayout mNetworkExceptionLayout = null;
	
	private ProgressDialog mProgressDialog = null;
	/** view end. */
	
	/** data start. */
	private String mHotelImg = null;
	
	private DataLoader mDataLoader = null;
	
	private String mPtOrderNo = null;
	
	private PTOrderBean orderBean = null;
	/** data end. */

	/** tag data start. */
	private QueryDataTask mQueryDataTask = null;
	private CancelOrderTask mCancelOrderTask = null;
	
	private TC_OrderDetailBean mOrderDetail = null;
	private String[] mCancelOrderCaseList = null;
	private int mCancelOrderCase = 0;
	private CommonDialog mCommonDialog = null; 			// 弹出的选择框
	private int smallLogoId = 0;


	private static final int MSG_SHOW_DIALOG_ACTION = 0x2001;
	private static final int MSG_DISMISS_DIALOG_ACTION = 0x2002;
	private static final int MSG_GET_DATA_ERROR_ACTION = 0x2003;
	/** tag data end. */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		smallLogoId = R.drawable.icon_btn_id_jiudian;
		setContentView(R.layout.putao_yellow_page_hotelorderdetail);
	
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
		mOrderSerialId = intent.getStringExtra("Order_SerialId");
		mHotelImg = intent.getStringExtra("Hotel_Img");
		mOldOrderState = intent.getIntExtra("Hotel_Order_State", 0);
		mPtOrderNo = intent.getStringExtra("Putao_Order_No");
		//putao_pxy 2014/01/14 start for BUG #2958 
        if (!TextUtils.isEmpty(mPtOrderNo)) {
            PTOrderBean orderBean = PTOrderCenter.getInstance().getOrderByOrderNumber(mPtOrderNo);
            if (orderBean == null) {
                finish();
            }
        }
        //putao_pxy 2014/01/14 end for BUG #2958 
	}
	
	private void initView(){
		if( TextUtils.isEmpty(mTitleContent) ){
        	mTitleContent = getResources().getString(R.string.putao_hoteldetail);
        }
		View headLayout = findViewById(R.id.head_layout);
		((TextView)headLayout.findViewById(R.id.title)).setText(mTitleContent);
        findViewById(R.id.back_layout).setOnClickListener(this);
        
        mCardLayout = (LinearLayout) findViewById(R.id.card_layout);
        mContentLayout = (LinearLayout) findViewById(R.id.content_layout);
        mNetworkExceptionLayout = (LinearLayout) findViewById(R.id.network_exception_layout);
        mNetworkExceptionLayout.setOnClickListener(this);
        
        mHotelLogoImgView = (ImageView)findViewById(R.id.logo);
        mHotelNameTView = (TextView) mCardLayout.findViewById(R.id.title);
        
        mHotelDescriptionTView = (TextView)findViewById(R.id.orderdetail_name);
        mHotelRoomNameTView = (TextView) findViewById(R.id.orderlist_hotel_roomname);
        mHotelInTView = (TextView) findViewById(R.id.orderlist_hotel_in_date);
        mHotelOutTView = (TextView) findViewById(R.id.orderlist_hotel_out_date);
        mHotelRetainTimeTView = (TextView)findViewById(R.id.payed);
        mHotelPriceTView = (TextView)findViewById(R.id.money);
        mHotelStateTView = (TextView) findViewById(R.id.orderdetail_state);
        mHotelMoneyTView = (TextView) findViewById(R.id.orderdetail_money);
        mHotelSerialIdTView = (TextView) findViewById(R.id.orderdetail_serialid);
        mHotelOrderCreateTView = (TextView) findViewById(R.id.orderdetail_order_createtime);
        mHotelAddressTView = (TextView) findViewById(R.id.orderdetail_address);
        mHotelLastInDateTView = (TextView)findViewById(R.id.orderdetail_lastin_date);
        mHotelOrderTypeTView  = (TextView)findViewById(R.id.orderdetail_order_type);
        mHotelComeNameTView =(TextView)findViewById(R.id.orderdetail_come_name);
        mHotelMobileTView = (TextView) findViewById(R.id.orderdetail_mobile);
        mHotelKefuTelTView = (TextView)findViewById(R.id.orderdetail_kefudianhua);
        mHotelTaxiTView = (TextView)findViewById(R.id.orderdetail_taxi);
        mHotelImgView = (ImageView) findViewById(R.id.orderlist_hotel_img);
		
        mCancelOrderLayout = (LinearLayout) findViewById(R.id.orderdetail_cancelorder);
        mCancelOrderLayout.setOnClickListener(this);
        mHotelMobileTView.setOnClickListener(this);
        mHotelKefuTelTView.setOnClickListener(this);
        mHotelTaxiTView.setOnClickListener(this);
        
	}
	
	private void initData(){
		mDataLoader = new ImageLoaderFactory(this).getYellowPageLoader(R.drawable.putao_a0114, 0);
		mCancelOrderCaseList = getResources().getStringArray(R.array.putao_hotel_cancelorder_case);
		mCommonDialog = CommonDialogFactory.getListCommonDialog(this);
		mCommonDialog.setListViewItemClickListener(mOnDialogItemClickListener);
		
		doLoadDetailData();
	}
	
	/**
	 * 加载网络数据
	 */
	private void doLoadDetailData(){
		if( NetUtil.isNetworkAvailable(this) ){
			mNetworkExceptionLayout.setVisibility(View.GONE);
			mCardLayout.setVisibility(View.VISIBLE);
			mContentLayout.setVisibility(View.VISIBLE);
			
			if ( (mQueryDataTask != null && mQueryDataTask.getStatus() != AsyncTask.Status.RUNNING)
					|| mQueryDataTask == null ) {
				mQueryDataTask = new QueryDataTask();
				mQueryDataTask.execute();
	        }
		}else{
			//网络异常
			mNetworkExceptionLayout.setVisibility(View.VISIBLE);
			mCardLayout.setVisibility(View.GONE);
			mContentLayout.setVisibility(View.GONE);
			Utils.showToast(this, R.string.putao_no_net, false);
		}
	}
	
	private class QueryDataTask extends AsyncTask<Void, Void, TC_Response_OrderDetail> {
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			mHandler.sendEmptyMessage(MSG_SHOW_DIALOG_ACTION);
		}

		@Override
		protected TC_Response_OrderDetail doInBackground(Void... arg0) {
		    LogUtil.i(TAG, "doInBackground is come in.");
			TC_Request_OrderDetail orderDetailRequestBody = new TC_Request_OrderDetail();
			orderDetailRequestBody.setSerialIds(mOrderSerialId);
			orderDetailRequestBody.setIsCtripOrderId(1);
			orderDetailRequestBody.setIsReturnCash(1);
			String requestBody = orderDetailRequestBody.getBody();
			String requestHead = TC_Request_DataFactory.getRequestHead("GetHotelOrderDetail");
			String url = TC_Common.TC_URL_SEARCH_ORDER;
			TCRequestData requestData = new TCRequestData(requestHead, requestBody);
			Object object = TC_Http.getDataByPost(url, requestData.getReqeustData(), TC_Response_OrderDetail.class);
			if( object == null ){
				return null;
			}
			return (TC_Response_OrderDetail) object;
		}
		
		@Override
		protected void onPostExecute(TC_Response_OrderDetail result) {
			// TODO Auto-generated method stub
			mHandler.sendEmptyMessage(MSG_DISMISS_DIALOG_ACTION);
			if( result == null ){
				LogUtil.i(TAG, "onPostExecute result is null.");
				mHandler.sendEmptyMessage(MSG_GET_DATA_ERROR_ACTION);
				return;
			}
			List<TC_OrderDetailBean> orderList = result.getOrderList();
			if( orderList == null || orderList.size() == 0 ){
				LogUtil.i(TAG, "onPostExecute orderList is null or length is 0.");
				mHandler.sendEmptyMessage(MSG_GET_DATA_ERROR_ACTION);
				return;
			}
			mOrderDetail = orderList.get(0);
			if( mOrderDetail == null ){
				LogUtil.i(TAG, "onPostExecute mOrderDetail is null.");
				mHandler.sendEmptyMessage(MSG_GET_DATA_ERROR_ACTION);
				return;
			}
			updateOrderDetail();
		}
	}
	
	private void updateOrderDetail(){
	    mHotelLogoImgView.setImageResource(smallLogoId);
		mHotelNameTView.setText(getString(R.string.putao_hotelorderdetail_name_pfrfix, mOrderDetail.getHotelName()));
		mHotelDescriptionTView.setText(mOrderDetail.getHotelName());
		mHotelRoomNameTView.setText(mOrderDetail.getRoomName());
		
		Calendar calendar = Calendar.getInstance();
		Date inDate = CalendarUtil.getDateFromString(mOrderDetail.getCheckin());
		if( inDate != null ){
			calendar.setTime(inDate);
			mHotelInTView.setText(getString(R.string.putao_calendar_showdate_month, 
					CalendarUtil.getFormatTwoDecimal((calendar.get(Calendar.MONTH) + 1)), 
					CalendarUtil.getFormatTwoDecimal(calendar.get(Calendar.DAY_OF_MONTH))));
		}
		Date outDate = CalendarUtil.getDateFromString(mOrderDetail.getCheckout());
		if( outDate != null ){
			calendar.setTime(outDate);
			mHotelOutTView.setText(getString(R.string.putao_calendar_showdate_month, 
					CalendarUtil.getFormatTwoDecimal((calendar.get(Calendar.MONTH) + 1)),
					CalendarUtil.getFormatTwoDecimal(calendar.get(Calendar.DAY_OF_MONTH))));
		}
  
		String orderStateStr = "";
		long msgTime = System.currentTimeMillis();
		mOrderState = mOrderDetail.getOrderStatus();
    	if( mOrderState == 1 ){
			orderStateStr = getString(R.string.putao_hotelorderdetail_state_new);
		}else if( mOrderState == 2 ){
			orderStateStr = getString(R.string.putao_hotelorderdetail_state_cancel);
		}else if( mOrderState == 3 || mOrderState == 4 || mOrderState == 5){
			orderStateStr = getString(R.string.putao_hotelorderdetail_state_confirm);
		}else if( mOrderState == 6){
			orderStateStr = getString(R.string.putao_hotelorderdetail_state_zancun);
		}
        mHotelStateTView.setText(orderStateStr);//订单状态
        
        if( mOldOrderState != 0 && mOldOrderState == mOrderState ){
        	//一致，则卡片上的状态显示 卡片的状态文案;否则卡片上的状态显示订单的状态
        	List<PTMessageBean> msgBeanList = PTMessageCenter.getInstance().queryMessageByOrderNo(mPtOrderNo);
        	if( msgBeanList != null && msgBeanList.size() > 0 ){
        		PTMessageBean msgBean = msgBeanList.get(0);
        		if( msgBean != null ){
        			String orderStateStrTemp = msgBean.getDigest();
        			if( !TextUtils.isEmpty(orderStateStrTemp) ){
        				orderStateStr = orderStateStrTemp;
        			}
        			long msgTimeTemp = msgBean.getTime();
        			if( msgTimeTemp > 0 ){
        				msgTime = msgTimeTemp;
        			}
        		}
        	}
        }
        mHotelRetainTimeTView.setText(orderStateStr); // 卡片上的状态显示
        mHotelPriceTView.setText(""); //金额
        
		mHotelMoneyTView.setText(getString(R.string.putao_hotelpay_pay_money, mOrderDetail.getOrderAmount()));
		mHotelSerialIdTView.setText(mOrderDetail.getSerialId());
		mHotelOrderCreateTView.setText(mOrderDetail.getCreateDate());
		mHotelAddressTView.setText(mOrderDetail.getHotelAddress());
        String arriveDateStr = mOrderDetail.getCheckin().split(" ")[0];
        StringBuilder arriveTimeStr = new StringBuilder(arriveDateStr).append(" ").append(
                mOrderDetail.getArriveTime());
        mHotelLastInDateTView.setText(arriveTimeStr.toString());
//        mHotelOrderTypeTView.setText(String.valueOf(mOrderDetail.getOrderType()));
        mHotelComeNameTView.setText(mOrderDetail.getGuestName());
		mHotelMobileTView.setText(mOrderDetail.getHotelTel());
		
        if (mHotelImg != null) {
            mDataLoader.loadData(mHotelImg, mHotelImgView);
        } else {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.putao_a0114);
            int corner = getResources().getDimensionPixelSize(R.dimen.putao_image_round_corner);
            mHotelImgView.setImageBitmap(ContactsHubUtils.corner(bitmap, corner, 0));
        }
		
		double guaranteeAmount = mOrderDetail.getGuaranteeAmount();  //担保金额
		if( mOrderState == 1 && guaranteeAmount == 0 ){ //非担保预付 不能取消
			mCancelOrderLayout.setVisibility(View.VISIBLE);
		}else{
			mCancelOrderLayout.setVisibility(View.GONE);
		}
		checkAsyncPostHotelOrderToServer();
	}
	
	/**
	 * 酒店订单状态上报[上报参数为：酒店订单编号 与 订单状态]
	 * 注：后台状态与（请求的）同城状态不一致时，更新订单状态到后台服务器
	 */
	private void checkAsyncPostHotelOrderToServer(){
		if( mOldOrderState != 0 && mOldOrderState != mOrderState ){
			/**
			 * http://192.168.1.73:8080/pay/pay/order/hotel/tongcheng/update_status?status=1&orderNo=tc1000
			 */
			SimpleRequestData requestData = new SimpleRequestData();
			requestData.setParam("status", String.valueOf(mOrderState));
			requestData.setParam("orderNo", mOrderSerialId);
			PTHTTP.getInstance().asynPost(Config.ORDER_UPLOAD_URL + "/pay/order/hotel/tongcheng/update_status", requestData, null);
		}
		
	}
	
	/**
	 * 显示取消订单原因
	 */
	private void showCancelOrderCaseLayout(){
		if( mCommonDialog != null ){
			mCommonDialog.setTitle(getResources().getString(R.string.putao_hotelorderdetail_cancelorder_hint));
			mCommonDialog.setListViewDatas(mCancelOrderCaseList);
		}
		mCommonDialog.show();
	}
	
	private void cancelHotelOrder(){
		if( NetUtil.isNetworkAvailable(this) ){
			if ( (mCancelOrderTask != null && mCancelOrderTask.getStatus() != AsyncTask.Status.RUNNING)
					|| mCancelOrderTask == null ) {
				mCancelOrderTask = new CancelOrderTask();
				mCancelOrderTask.execute();
	        }
		}else{
			//网络异常
			Utils.showToast(this, R.string.putao_no_net, false);
		}
	}
	
	private OnItemClickListener mOnDialogItemClickListener = new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long arg3) {
			if( position < mCancelOrderCaseList.length ){
				mCancelOrderCase = position + 1;
				if (mCommonDialog != null) {
					mCommonDialog.dismiss();
				}
				cancelHotelOrder();
			}
		}
	};
	
	private class CancelOrderTask extends AsyncTask<Void, Void, TC_Response_CancelOrder> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			mHandler.sendEmptyMessage(MSG_SHOW_DIALOG_ACTION);
		}

		@Override
		protected TC_Response_CancelOrder doInBackground(Void... arg0) {
			TC_Request_CancelOrder cancelOrderRequestBody = new TC_Request_CancelOrder();
			cancelOrderRequestBody.setSerialId(mOrderSerialId);
			cancelOrderRequestBody.setCancelReasonCode(mCancelOrderCase);
			String requestBody = cancelOrderRequestBody.getBody();
			String requestHead = TC_Request_DataFactory.getRequestHead("CancelOrder");
			String url = TC_Common.TC_URL_SEARCH_ORDER;
			TCRequestData requestData = new TCRequestData(requestHead, requestBody);
			Object object = TC_Http.getDataByPost(url, requestData.getReqeustData(), TC_Response_CancelOrder.class);
			if( object == null ){
				return null;
			}
			return (TC_Response_CancelOrder) object;
		}

		@Override
		protected void onPostExecute(TC_Response_CancelOrder result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			mHandler.sendEmptyMessage(MSG_DISMISS_DIALOG_ACTION);
			if( result != null && "0".equals(result.getRspType()) ){
				Utils.showToast(YellowPageHotelOrderDetailActivity.this, 
						R.string.putao_hotelorderdetail_cancelorder_result_success, false);
			}else{
				Utils.showToast(YellowPageHotelOrderDetailActivity.this, 
						R.string.putao_hotelorderdetail_cancelorder_result_failed, false);
			}
			finish();
		}
	}
	
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch(what){
			case MSG_SHOW_DIALOG_ACTION:
				if (mProgressDialog == null) {
					mProgressDialog = new ProgressDialog(YellowPageHotelOrderDetailActivity.this);
					mProgressDialog.setMessage(getString(R.string.putao_yellow_page_loading));
				}
				mProgressDialog.show();
				break;
			case MSG_DISMISS_DIALOG_ACTION:
				if (mProgressDialog != null && mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
				break;
			case MSG_GET_DATA_ERROR_ACTION:
				Utils.showToast(YellowPageHotelOrderDetailActivity.this, 
						R.string.putao_hotel_orderdetail_failed_hint, false);
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
		}else if( viewId == R.id.orderdetail_cancelorder ){
			showCancelOrderCaseLayout();
		}else if( viewId == R.id.orderdetail_mobile ){
			String mobile = mOrderDetail.getHotelTel();
			if( !TextUtils.isEmpty(mobile) ){
				ContactsHubUtils.call(YellowPageHotelOrderDetailActivity.this, mobile);
			}
		}else if (viewId == R.id.orderdetail_taxi) {
//		    MobclickAgentUtil.onEvent(this, UMengEventIds.CNT_NOTIFY_CARD_ITEM_HOTEL_TAXI);
            startKuaiDiActivity();
        } else if (viewId == R.id.orderdetail_kefudianhua) {
            String kefuTel = getString(R.string.putao_hotelorderdetail_kefu_telnumber);
            if (!TextUtils.isEmpty(kefuTel)) {
                ContactsHubUtils.call(YellowPageHotelOrderDetailActivity.this, kefuTel);
            }
        } else if (viewId == R.id.network_exception_layout ){
        	doLoadDetailData();
        }
	}
	
    private void startKuaiDiActivity() {
        Intent intent = new Intent(this, YellowPageJumpH5Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        String targetActivity = TC_Common.TC_KUAIDIACTIVITY;
        String url = TC_Common.TC_URL_KUAIDI;
        intent.putExtra("targetActivityName", targetActivity);
        intent.putExtra("url", url);
        intent.putExtra("title", getString(R.string.putao_hotelorderdetail_taxi_hint));
        startActivity(intent);
    }
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if( mQueryDataTask != null ){
			mQueryDataTask.cancel(true);
			mQueryDataTask = null;
    	}
		if( mCancelOrderTask != null ){
			mCancelOrderTask.cancel(true);
			mCancelOrderTask = null;
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
	protected boolean needReset() {
	    return true;
	}
}
