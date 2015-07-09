package so.contacts.hub.thirdparty.tongcheng.ui;

import so.contacts.hub.active.ActiveUtils;
import so.contacts.hub.core.Config;
import so.contacts.hub.msgcenter.MsgCenterConfig;
import so.contacts.hub.msgcenter.report.MsgReport;
import so.contacts.hub.msgcenter.report.MsgReportUtils;
import so.contacts.hub.net.IResponse;
import so.contacts.hub.net.PTHTTP;
import so.contacts.hub.net.SimpleRequestData;
import so.contacts.hub.remind.BaseRemindActivity;
import so.contacts.hub.thirdparty.tongcheng.bean.HotelOrderPostBean;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.NetUtil;
import so.contacts.hub.util.Utils;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yulong.android.contacts.discover.R;

public class YellowPageHotelOrderResultActivity extends BaseRemindActivity implements OnClickListener, IResponse{
	
	private static final String TAG = YellowPageHotelOrderResultActivity.class.getSimpleName();
	
	/** view start */
	private ImageView mShowResultImgView = null;
	private TextView mShowResultTView = null;
	private TextView mShowResultHintTView = null;
	
	private LinearLayout mShowResultOrderNumLayout = null;
	private TextView mShowResultOrderNumTView = null;
	private LinearLayout mShowResultHotelNameLayout = null;
	private TextView mShowResultHotelNameTView = null;
	private LinearLayout mShowResultHotelInOutLayout = null;
	private TextView mShowResultHotelInOutTView = null;
	private LinearLayout mShowResultPeopleLayout = null;
	private TextView mShowResultPeopleTView = null;
	
	private LinearLayout mRepeatOrderHintLayout = null;
	private TextView mRepeatOrderHintContentLayout = null;
	private TextView mRepeatOrderResonHintTView = null;
	
	private View mShowResultOrderMoneyDivider = null;
	private TextView mShowResultOrderMoneyTView = null;
	
	private Button mOpenOrderBtn = null;
	
	/** view end */
	
	/** data start */
	private String mCityName = null;
	private String mOrderStatus = null; 			// 0:成功；1重复
	private String mOrderSerialId = null;
	private String mHotelName = null;
	private String mHotelImg = null;
	private String mHotelAddress = null;
	private String mHotelInDate = null;
	private String mHotelOutDate = null;
	private String mHotelPeople = null;
	private double mTotalAmount = 0;
	private String mOrderErrorInfo = null;
	/** data end */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.putao_yellow_page_hotelorderresult);
	
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
		mOrderStatus = intent.getStringExtra("Order_Status");
		mOrderSerialId = intent.getStringExtra("Order_SerialId");
		mHotelName = intent.getStringExtra("Order_HotelName");
        mHotelAddress = intent.getStringExtra("HotelAddress");
		mHotelInDate = intent.getStringExtra("Order_Hotel_InDate");
		mHotelOutDate = intent.getStringExtra("Order_Hotel_OutDate");
		mHotelPeople = intent.getStringExtra("Order_Hotel_People");
		mOrderErrorInfo = intent.getStringExtra("Order_Order_ResultInfo");
		mTotalAmount = intent.getDoubleExtra("Order_TotalAmount", 0);
		mHotelImg = intent.getStringExtra("HotelImg"); //酒店图片
	}
	
	private void initView(){
        findViewById(R.id.back_layout).setOnClickListener(this);
        
        mShowResultImgView = (ImageView) findViewById(R.id.order_result_img);
        mShowResultTView = (TextView) findViewById(R.id.order_result_text);
        mShowResultHintTView = (TextView) findViewById(R.id.order_result_hint);
        
        mShowResultOrderNumLayout = (LinearLayout) findViewById(R.id.order_result_ordernum_layout);
        mShowResultOrderNumTView = (TextView) findViewById(R.id.order_result_ordernum);
        mShowResultHotelNameLayout = (LinearLayout) findViewById(R.id.order_result_hotelname_layout);
        mShowResultHotelNameTView = (TextView) findViewById(R.id.order_result_hotelname);
        mShowResultHotelInOutLayout = (LinearLayout) findViewById(R.id.order_result_inout_layout);
        mShowResultHotelInOutTView = (TextView) findViewById(R.id.order_result_inout);
        mShowResultPeopleLayout = (LinearLayout) findViewById(R.id.order_result_people_layout);
        mShowResultPeopleTView = (TextView) findViewById(R.id.order_result_people);
        
        mRepeatOrderHintLayout = (LinearLayout) findViewById(R.id.order_result_order_reson_hintlayout);
        mRepeatOrderHintContentLayout = (TextView) findViewById(R.id.order_result_order_reson_hint_content);
        mRepeatOrderResonHintTView = (TextView) findViewById(R.id.order_result_order_reson);
        
        mShowResultOrderMoneyDivider = findViewById(R.id.order_result_order_success_money_hint);
        mShowResultOrderMoneyTView = (TextView) findViewById(R.id.order_result_order_success_money);
        
        mOpenOrderBtn = (Button) findViewById(R.id.hotelorder_openorderdetail);
	}
	
	private void initData(){
		if( "0".equals(mOrderStatus) || "1".equals(mOrderStatus) ){
			if( "0".equals(mOrderStatus) ){
				//成功订单 
				((TextView) findViewById(R.id.title)).setText(R.string.putao_hotelorderdetail_title_success);
				mShowResultImgView.setImageResource(R.drawable.putao_icon_transaction_success);
				mShowResultTView.setText(R.string.putao_hotelorderdetail_submit_success);
				mShowResultHintTView.setVisibility(View.VISIBLE);
				mShowResultHintTView.setText(R.string.putao_hotelorderdetail_submit_success_hint);
				
				//成功订单 显示价格
				mShowResultOrderMoneyDivider.setVisibility(View.VISIBLE);
				mRepeatOrderHintLayout.setVisibility(View.GONE);
				mRepeatOrderResonHintTView.setVisibility(View.GONE);
				mShowResultOrderMoneyTView.setVisibility(View.VISIBLE);
				mShowResultOrderMoneyTView.setText(getString(R.string.putao_hotelorderdetail_submit_ordermoney, mTotalAmount));
				
				//订单成功时上报数据
				asyncPostHotelOrderToServer();
				
				// 直接查找彩蛋并弹出页面
				// add by cj 2015/01/23
				ActiveUtils.findValidEggAndStartWebDialog(this,
				        YellowPageHotelOrderResultActivity.class.getName(), null);
								
//				//订单成功时，启动“常显型卡片-附近的”搜索数据
//                SmartNearSearchUtil.addNearBySearchData(mHotelName, mHotelAddress, mCityName, mHotelOutDate,
//                        SmartNearSearchUtil.SMART_NEARBY_NOTIFY_TYPE_HOTEL);
			}else{
				//重复订单
				((TextView) findViewById(R.id.title)).setText(R.string.putao_hotelorderdetail_title_repeat);
				mShowResultImgView.setImageResource(R.drawable.putao_icon_transaction_error);
				mShowResultTView.setText(R.string.putao_hotelorderdetail_submit_repeat);
				mShowResultHintTView.setVisibility(View.GONE);
				
				//重复订单 显示重复理由
				mShowResultOrderMoneyTView.setVisibility(View.GONE);
				mRepeatOrderHintLayout.setVisibility(View.VISIBLE);
				mRepeatOrderHintContentLayout.setText(R.string.putao_hotelorderdetail_submit_repeatreson_hint); // 理由
				mRepeatOrderResonHintTView.setVisibility(View.VISIBLE);
				mRepeatOrderResonHintTView.setText(getString(R.string.putao_hotelorderdetail_submit_repeatreson, 
						mHotelPeople, mHotelInDate, mHotelOutDate)); //内容
				
				if( !TextUtils.isEmpty(mOrderSerialId) ){ //只在重复订单状态下显示查看订单详情内容
					mOpenOrderBtn.setOnClickListener(this);
					mOpenOrderBtn.setVisibility(View.VISIBLE);
		        }
			}
			mShowResultOrderNumTView.setText(mOrderSerialId);
			mShowResultHotelNameTView.setText(mHotelName);
			mShowResultHotelInOutTView.setText(getString(R.string.putao_hotelorderdetail_submit_inout_data, mHotelInDate, mHotelOutDate));
			mShowResultPeopleTView.setText(mHotelPeople);
		} else{
			//订单异常
			((TextView) findViewById(R.id.title)).setText(R.string.putao_hotelorderdetail_title_error);
			mShowResultImgView.setImageResource(R.drawable.putao_icon_transaction_error);
			mShowResultTView.setText(R.string.putao_hotelorderdetail_title_error);
			mShowResultHintTView.setVisibility(View.GONE);			

			findViewById(R.id.result_divider).setVisibility(View.GONE);
			mShowResultOrderNumLayout.setVisibility(View.GONE);
			mShowResultHotelNameLayout.setVisibility(View.GONE);
			mShowResultHotelInOutLayout.setVisibility(View.GONE);
			mShowResultPeopleLayout.setVisibility(View.GONE);
			mOpenOrderBtn.setVisibility(View.GONE);
			
			//订单异常 显示异常原因
			mShowResultOrderMoneyTView.setVisibility(View.GONE);
			mRepeatOrderHintLayout.setVisibility(View.VISIBLE);
			mRepeatOrderResonHintTView.setVisibility(View.VISIBLE);
			mRepeatOrderHintContentLayout.setText(R.string.putao_hotelorderdetail_submit_order_error_hint);
			mRepeatOrderResonHintTView.setText(mOrderErrorInfo);
		}
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		int viewId = view.getId();
		if (viewId == R.id.back_layout) {
			finish();
		}else if( viewId == R.id.hotelorder_openorderdetail ){
			if( !NetUtil.isNetworkAvailable(this) ){ //网络异常
				Utils.showToast(this, R.string.putao_no_net, false);
				return;
			}
			//订单详情
			Intent intent = new Intent(this, YellowPageHotelOrderDetailActivity.class);
			intent.putExtra("Order_SerialId", mOrderSerialId);
			intent.putExtra("Hotel_Img", mHotelImg);
			startActivity(intent);
		}
	}
	
	/**
	 * 酒店订单上报[上报参数为：酒店订单编号]
	 */
	private void asyncPostHotelOrderToServer(){
		if( TextUtils.isEmpty(mOrderSerialId) ){
			LogUtil.i(TAG, "asyncPostHotelOrderToServer hotelOrderId is null.");
			return;
		}
		
		HotelOrderPostBean orderPostBean = new HotelOrderPostBean(mOrderSerialId, mHotelName, mHotelImg);

	    // 异步上报 modify by cj 2015/01/22 
		MsgReport report = new MsgReport();
		report.setType(MsgCenterConfig.Product.hotel.getProductType());
		report.setReportContent(Config.mGson.toJson(orderPostBean));
		
		MsgReportUtils.reportAsync(this, report);
		// modify by cj 2015/01/22 end
		
		/**
		pay/order/adapter
		{
		    info : jsonStr,
		    product_type : int
		}
		*/
		// old code: 
/*		SimpleRequestData requestData = new SimpleRequestData();
		requestData.setParam("product_type", String.valueOf(SimpleRequestData.Product.hotel.getProductType()));
		HotelOrderPostBean orderPostBean = new HotelOrderPostBean(mOrderSerialId, mHotelName, mHotelImg);
		requestData.setParam("info", new Gson().toJson(orderPostBean));
		//requestData.setParam("info", "HotelOrderId=" + mOrderSerialId);
		
	    LogUtil.i(TAG, "asyncPostHotelOrderToServer orderId="+mOrderSerialId+" HotelName="+mHotelName+" HotelImg="+mHotelImg);
		PTHTTP.getInstance().asynPost(Config.ORDER_UPLOAD_URL + "/pay/order/adapter", requestData, this);
*/	
	}

    public void onSuccess(String content) {
        LogUtil.i(TAG, "asyncPostHotelOrderToServer content="+content);
    }

    @Override
    public void onFail(int errorCode) {
        LogUtil.i(TAG, "asyncPostHotelOrderToServer errorCode="+errorCode);
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
		return true;
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
