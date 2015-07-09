package so.contacts.hub.shuidianmei;

import so.contacts.hub.util.UMengEventIds;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.account.IAccCallback;
import so.contacts.hub.account.PutaoAccount;
import so.contacts.hub.charge.ChargeUtils;
import so.contacts.hub.msgcenter.MsgCenterConfig;
import so.contacts.hub.msgcenter.PTOrderCenter.RefreshOrderListener;
import so.contacts.hub.msgcenter.bean.PTOrderBean;
import so.contacts.hub.net.IResponse;
import so.contacts.hub.net.PTHTTP;
import so.contacts.hub.net.SimpleRequestData;
import so.contacts.hub.payment.GetOrderParam;
import so.contacts.hub.payment.PaymentCallback;
import so.contacts.hub.payment.PaymentViewGroup;
import so.contacts.hub.payment.core.PaymentDesc;
import so.contacts.hub.payment.data.ProductTypeCode;
import so.contacts.hub.remind.BaseRemindActivity;
import so.contacts.hub.shuidianmei.bean.WEGHistoryBean;
import so.contacts.hub.shuidianmei.bean.WEGOrderInfo;
import so.contacts.hub.shuidianmei.bean.WaterElectricityGasBean;
import so.contacts.hub.ui.yellowpage.YellowPageChargeResultActivity;
import so.contacts.hub.util.ContactsAppUtils;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.NetUtil;
import so.contacts.hub.util.Utils;
import so.contacts.hub.widget.CustomListView;
import so.contacts.hub.widget.CustomListView.OnLoadMoreListener;
import so.contacts.hub.widget.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mdroid.core.util.SystemUtil;
import com.yulong.android.contacts.discover.R;

public class YellowPageWaterEGHistoryActivity extends BaseRemindActivity implements 
	OnClickListener, OnLoadMoreListener, IAccCallback ,PaymentCallback{

	private static final String TAG = YellowPageWaterEGHistoryActivity.class.getName();
	
	private CustomListView mHistoryListView = null;
	
	private LinearLayout mHistoryNoDataLayout = null;
	
	private TextView mShowHintTView = null;
	
	private ProgressDialog mProgressDialog = null;
	
	private YellowPageWEGHistoryAdapter mHistoryAdapter = null;
	
	private List<WEGHistoryBean> mHistoryList = new ArrayList<WEGHistoryBean>();
	
	private static final int MSG_SHOW_DIALOG_ACTION = 0x2001;
	
	private static final int MSG_DISMISS_DIALOG_ACTION = 0x2002;

	private static final int MSG_SHOW_NODATA_ACTION = 0x2003;

	private static final int MSG_SHOW_NODATA_NONET_ACTION = 0x2004;

    public static final int MSG_NETWORK_EXCEPTION_ACTION = 0x2005;

    private static final String PAGE_SIZE = "30";
    
    //水电煤在服务器的产品ID号
    private static final String PRODUCT_TYPE = "2";
    
    //每次请求的页码
    private int mRequestPageNum = -1;
    
    //是否正在加载更多数据
    private boolean mLoadMore = false;
    
    //是否有更多数据（上一次请求的数据小于PAGE_DATA_SIZE，则说明没有更多数据）
    private boolean mHasMoreData = true;
    
    //是否需要重新加载所有数据
    private boolean mNeedReloadData = false;
    
    //支付组件 
    private PaymentViewGroup paymentView;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.putao_weg_yellow_page_weg_history);
		
		initView();
		initData();
		
		if(NetUtil.isNetworkAvailable(this) && PutaoAccount.getInstance().isLogin()){
			doQueryChargeHistoryData(0, true);
		}else if(!NetUtil.isNetworkAvailable(this)) {
			mHistoryNoDataLayout.setVisibility(View.VISIBLE);
	        mShowHintTView.setText(R.string.putao_netexception_hint);
	        mHistoryListView.setVisibility(View.GONE);
		}else if(!PutaoAccount.getInstance().isLogin()){
            mHistoryListView.setVisibility(View.GONE);
            mHistoryNoDataLayout.setVisibility(View.VISIBLE);
            if (!PutaoAccount.getInstance().isLogin()) {
                Toast.makeText(this, R.string.putao_yellow_page_try_login, Toast.LENGTH_SHORT).show();
                PutaoAccount.getInstance().silentLogin(this);
            }
		}
		MobclickAgentUtil.onEvent(this, WEGUtil.DISCOVER_YELLOWPAGE_WEG_RECHARGE_HISTORY);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		LogUtil.i(TAG, "onResume");
		MobclickAgentUtil.onResume(this);
		if( mNeedReloadData ){
			mRequestPageNum = -1;
			mHasMoreData = true;
			doQueryChargeHistoryData(0, true);
		}
	}
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgentUtil.onPause(this);
	}
	
	private void initView() {
		// TODO Auto-generated method stub
		TextView titleTView = (TextView)findViewById(R.id.title);
        if( TextUtils.isEmpty(mTitleContent) ){
            titleTView.setText(R.string.putao_charge_history);
        }else{
            titleTView.setText(mTitleContent);
        }
		findViewById(R.id.back_layout).setOnClickListener(this);
		
		mHistoryNoDataLayout = (LinearLayout) findViewById(R.id.network_exception_layout);
		mShowHintTView = (TextView) findViewById(R.id.exception_desc);
		mHistoryNoDataLayout.setOnClickListener(this);
		mHistoryListView = (CustomListView) findViewById(R.id.chargehistory_list);
		mHistoryListView.setCanLoadMore(true);
		mHistoryListView.setOnLoadListener(this);
		
		mProgressDialog = new ProgressDialog(this);
		if (!mProgressDialog.isShowing()) {
			mProgressDialog.setMessage(getString(R.string.putao_yellow_page_loading));
		}
		
//		/**
//		 * add code by putao_lhq
//		 * @start
//		 */
//		mHistoryListView.setOnItemClickListener(new OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
//                TextView chargeBtn = (TextView)view.findViewById(R.id.historyitem_charge);
//                if (chargeBtn != null && chargeBtn.getVisibility() == View.VISIBLE) {
//                    chargeBtn.callOnClick();
//                }
//            }
//        });/*@end*/
		
		/**
         * add code by putao_ljq
         * @start
         */
		paymentView = (PaymentViewGroup)findViewById(R.id.charge_payment_view);
		paymentView.setPaymentCallback(this);
		
		
		/*@end*/
	}

	private void initData() {
		// TODO Auto-generated method stub
		mHistoryAdapter = new YellowPageWEGHistoryAdapter(this);
		mHistoryAdapter.setData(mHistoryList);
		mHistoryListView.setAdapter(mHistoryAdapter);
		mHistoryListView.setOnItemClickListener(mListOnItemClickListener);
	}
	
	/**
	 * 执行查询充值历史记录
	 * @param pageNum
	 */
	private void doQueryChargeHistoryData(int pageNum, boolean needDialog){
		if( !mHasMoreData ){
			// 没有更多数据 或者 上一次请求数据不超过20条
			mHistoryListView.onLoadMoreComplete(true);
			return;
		}
		if( pageNum == mRequestPageNum && mLoadMore){
			// 当前请求正在执行 
			mLoadMore = false;
			mHistoryListView.onLoadMoreComplete(false);
			return;
		}
		if( needDialog ){
			mHandler.sendEmptyMessage(MSG_SHOW_DIALOG_ACTION);
		}
		LogUtil.i(TAG, "doQueryChargeHistoryData pageNum: " + pageNum);
		mRequestPageNum = pageNum;
		refreshOrderData(new RefreshOrderListener() {
            @Override
            public void refreshSuccess(boolean isDbChanged) {
                // TODO Auto-generated method stub
                if( mLoadMore ){
                    mHistoryListView.onLoadMoreComplete(false);
                    mLoadMore = false;
                }
                mHandler.sendEmptyMessage(MSG_DISMISS_DIALOG_ACTION);
                if( mNeedReloadData ){
                    mNeedReloadData = false;
                }
                if( (mHistoryList == null  || mHistoryList.size() == 0 )&& !NetUtil.isNetworkAvailable(YellowPageWaterEGHistoryActivity.this)){
                    // 无数据 
                    mHasMoreData = false;
                    mHandler.sendEmptyMessage(MSG_SHOW_NODATA_NONET_ACTION);
                    LogUtil.i(TAG, "queryChargeTelHistory responsedata is null.");
                    mNeedReloadData = false;
                    return;
                }
                if( mHistoryList == null || mHistoryList.size() <= 0 ){
                    // 无数据
                    mHasMoreData = false;
                    mHandler.sendEmptyMessage(MSG_SHOW_NODATA_ACTION);
                    LogUtil.i(TAG, "queryChargeTelHistory responsedata is empty.");
                    return;
                }else{
                    // 有数据则添加进去
                    int historyListSize = mHistoryList.size();
                    //现在暂时是一次性取完数据 所以没有更多数据了
//                  if( historyListSize < PAGE_DATA_SIZE ){
//                      // 服务器没有更多数据
//                      mHasMoreData = false;
//                  }else{
//                      // 服务器可能还有更多数据
//                      mHasMoreData = true;
//                  }
                    mHasMoreData = false;
                    LogUtil.i(TAG, "queryChargeTelHistory responsedata size: " + historyListSize);
                }
                mHistoryAdapter.setData(mHistoryList);
            }
            
            @Override
            public void refreshFailure(String msg) {
                if( mLoadMore ){
                    mHistoryListView.onLoadMoreComplete(false);
                    mLoadMore = false;
                }
                mHandler.sendEmptyMessage(MSG_DISMISS_DIALOG_ACTION);
                mHasMoreData = false;
                mHandler.sendEmptyMessage(MSG_SHOW_NODATA_NONET_ACTION);
                LogUtil.i(TAG, "queryChargeTelHistory responsedata is null.");
                mNeedReloadData = false;
            }
        });
		
	}
	
	private Handler mHandler = new Handler(){
		@Override
        public void handleMessage(Message msg) {
			int what = msg.what;
			switch(what){
			case MSG_SHOW_DIALOG_ACTION:
				if( mProgressDialog != null ){
					mProgressDialog.show();
				}
				break;
			case MSG_DISMISS_DIALOG_ACTION:
	            if (mProgressDialog != null && mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
				break;
			case MSG_SHOW_NODATA_ACTION:
				// 网络正常，请求无数据
				if( mHistoryList.size() == 0 ){
					mHistoryNoDataLayout.setVisibility(View.VISIBLE);
					mShowHintTView.setText(R.string.putao_charge_history_nodata);
					mHistoryListView.setVisibility(View.GONE);
				}else{
					Utils.showToast(YellowPageWaterEGHistoryActivity.this, R.string.putao_charge_history_nomoredata, false);
				}
				break;
			case MSG_SHOW_NODATA_NONET_ACTION:
				// 网络异常 导致 请求无数据
				if( mHistoryList.size() == 0 ){
					mHistoryNoDataLayout.setVisibility(View.VISIBLE);
					mShowHintTView.setText(R.string.putao_netexception_hint);
					mHistoryListView.setVisibility(View.GONE);
				}else{
					Utils.showToast(YellowPageWaterEGHistoryActivity.this, R.string.putao_netexception, false);
				}
				break;
			case MSG_NETWORK_EXCEPTION_ACTION:
				mHandler.removeMessages(MSG_NETWORK_EXCEPTION_ACTION);
				Utils.showToast(YellowPageWaterEGHistoryActivity.this, R.string.putao_no_net, false);
				break;
			case ChargeUtils.MSG_SHOW_CHARGE_EXCEPTION_ACTION:
                // 获取话费订单- 网络请求异常
				break;
			case ChargeUtils.MSG_SHOW_CHARGE_ALI_EXCEPTION_ACTION:
                // 获取话费订单- 阿里返回异常
				break;
			default:
				break;
			}
		};
	};
	
	private OnItemClickListener mListOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // TODO Auto-generated method stub
            WEGHistoryBean historyBean = (WEGHistoryBean) view.getTag();
            if( historyBean == null){
                // 数据为空
                return;
            }
            if( !NetUtil.isNetworkAvailable(YellowPageWaterEGHistoryActivity.this) ){
                mHandler.sendEmptyMessageDelayed(MSG_NETWORK_EXCEPTION_ACTION, 300);
                return;
            }
            //处于等待付款阶段
            if((historyBean.status_code == YellowPageWEGHistoryAdapter.PAY_FAIL || historyBean.status_code == YellowPageWEGHistoryAdapter.WAIT_BUYER_PAY)){
                MobclickAgentUtil.onEvent(YellowPageWaterEGHistoryActivity.this, WEGUtil.DISCOVER_YELLOWPAGE_WEG_WAITING_PAY_BTN);
                doChargeWEG(historyBean);
            }
        }
    };

	@Override
	public void onClick(View view) {
		int id = view.getId();
		if (id == R.id.network_exception_layout) {
			if (NetUtil.isNetworkAvailable(this) && PutaoAccount.getInstance().isLogin()) {
			    mHistoryNoDataLayout.setVisibility(View.GONE);
			    mHistoryListView.setVisibility(View.VISIBLE);
			    mRequestPageNum = -1;
			    mHasMoreData = true;
			    doQueryChargeHistoryData(0, true);
			} else {
			    if (NetUtil.isNetworkAvailable(this)){
			        if (!PutaoAccount.getInstance().isLogin()) {
			            Toast.makeText(this, R.string.putao_yellow_page_try_login, Toast.LENGTH_SHORT)
			            .show();
			            PutaoAccount.getInstance().silentLogin(this);
			        }
			    }
			}
		} else if (id == R.id.back_layout) {
			finish();
		} else {
		}
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		// 如果有更多的数据则进行加载
		if( NetUtil.isNetworkAvailable(this) ){
        	mLoadMore = true;
			int pageNum = mRequestPageNum + 1;
			doQueryChargeHistoryData(pageNum, false);
		}else{
			mHandler.sendEmptyMessageDelayed(MSG_NETWORK_EXCEPTION_ACTION, 300);
			mHistoryListView.onLoadMoreComplete(true);
		}
	}

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSuccess() {
        // TODO Auto-generated method stub
        Toast.makeText(this, R.string.putao_yellow_page_try_login_success, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFail(int msg) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Integer remindCode() {
        return mRemindCode;
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
		return false;
	}

    @Override
    public void onCancel() {
        // TODO Auto-generated method stub
        
    }	
    
    
    
    /**
     * 查询后台,同步本地数据库与后台数据库
     * 
     * @param listener
     */
    public synchronized void refreshOrderData(final RefreshOrderListener listener) {
        // 联网查询最新数据 TODO
        SimpleRequestData data = new SimpleRequestData();
        data.setParam(MsgCenterConfig.ORDER_TIMESTAMP, "0");
        data.setParam(MsgCenterConfig.PRODUCT_TYPE,PRODUCT_TYPE);
        data.setParam(MsgCenterConfig.PAGE_SIZE, PAGE_SIZE);
        data.setParam(MsgCenterConfig.PAGE_NO, String.valueOf(1));
        PTHTTP.getInstance().asynGet(MsgCenterConfig.ORDER_LIST, data, new IResponse() {
            @Override
            public void onSuccess(String content) {
                try {
                    LogUtil.d(TAG, "onSuccess: " + content);
                    JSONObject json = new JSONObject(content);
                    String ret_code = json.getString("ret_code");
                    if ("0000".equals(ret_code)) {
                        String msg = json.getString("msg");
                        JSONObject data = json.getJSONObject("data");
                        LogUtil.i(TAG, "onSuccess msg :" + msg);
                        if (data.getJSONArray("result").length()>0) {
                            Gson gson = new Gson();
                            Type listType = new TypeToken<ArrayList<PTOrderBean>>() {}.getType();
                            ArrayList<PTOrderBean> orderBeans = gson.fromJson(data.getJSONArray("result").toString(),listType);
                            mHistoryList.clear();
                            if (orderBeans!=null) {
                                for (PTOrderBean ptOrderBean : orderBeans) {
                                    //把PTOrderBean 转化为 WEGHistoryBean
                                    WEGHistoryBean bean = new WEGHistoryBean();
                                    bean.status_code = ptOrderBean.getStatus_code();
                                    bean.status_des = ptOrderBean.getStatus();
                                    float price = Float.valueOf(ptOrderBean.getPrice())/100f;
                                    bean.sale_price = String.valueOf(price);
                                    bean.mark_price = String.valueOf(price);
                                    bean.order_no = ptOrderBean.getOrder_no();
                                    bean.c_time = getDateStr(ptOrderBean.getM_time());
                                    bean.pay_type = String.valueOf(ptOrderBean.getPayment_type());
                                    String expand = ptOrderBean.getExpand();
                                    JSONObject jsonObject = new JSONObject(expand);
                                    if(jsonObject.has("id")){
                                        bean.id = jsonObject.getString("id");
                                    }
                                    if(jsonObject.has("order_no")){
                                        bean.order_no = jsonObject.getString("order_no");
                                    }
                                    if(jsonObject.has("pro_id")){
                                        bean.pro_id = jsonObject.getString("pro_id");
                                        if(!TextUtils.isEmpty(bean.pro_id)){
                                            ///查找公司信息
                                            List<WaterElectricityGasBean> wegBean = ContactsAppUtils.getInstance().getDatabaseHelper().getWaterElectricityGasDB().queryWegDataByProid(bean.pro_id);
                                            if(wegBean != null ){
                                                bean.company = wegBean.get(0).getCompany();
                                                bean.weg_type = wegBean.get(0).getWeg_type();
                                            }
                                        }
                                    }
                                    if(jsonObject.has("account")){
                                        bean.account = jsonObject.getString("account");
                                    }
                                    if(jsonObject.has("yearmonth")){
                                        bean.yearmonth = jsonObject.getString("yearmonth"); 
                                    }
                                    mHistoryList.add(bean);
                                }
                            }
                        }else {
                            
                        }
                        //这里的传值没有作用
                        listener.refreshSuccess(true);
                    } else {
                        if (listener!=null) {
                            listener.refreshFailure(json.getString("msg"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(int errorCode) {
                listener.refreshFailure(errorCode + "");
            }
        });
    }
    
    public String getDateStr(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(time);
        return dateString;
     }
    
    /**
     *  重新发起交易 
     */
    private void doChargeWEG(WEGHistoryBean bean) {
        if ( !PutaoAccount.getInstance().isLogin() ) {
            Toast.makeText(this, R.string.putao_yellow_page_try_login, Toast.LENGTH_SHORT).show();
            PutaoAccount.getInstance().silentLogin(this);
        } else {
            LogUtil.i(TAG, "doChargeTelephone start.");

            // 获取订单信息和支付
            String pro_id = bean.pro_id;
            String account = bean.account;
            float price = Float.valueOf(bean.sale_price);
            String company = bean.company;
            String order_no = bean.order_no;
            int pay_type = Integer.valueOf(bean.pay_type);

            GetOrderParam param = new GetOrderParam();
            param.setProductId(ProductTypeCode.WaterElectricityGas.ProductId);
            param.setProductType(ProductTypeCode.WaterElectricityGas.ProductType);

            String wegStr =  WEGUtil.getRechargeStringByType(this, bean.weg_type);
            param.putSubObj("pro_id", pro_id);
            param.putSubObj("account", account);
            param.putSubObj("yearmonth", "");
            param.putSubObj("subject", String.format(this.getResources().getString(R.string.putao_water_eg_tag_subject_for_alipay), String.valueOf(price)));
            param.putSubObj("pt_token", PutaoAccount.getInstance().getPtUser().getPt_token());
            param.putUIPair("weg_str", wegStr);
            param.putUIPair("company", company);
            param.putUIPair("total_fee", String.valueOf(price));
            param.putUIPair("weg_type", String.valueOf(bean.weg_type));
            //传入有效的OrderNo就不会发起新的订单
            param.setOrderNo(order_no);
            param.setPriceInCents((int)(price * 100));
            
            if(pay_type == PaymentDesc.ID_ALIPAY){
                paymentView.selectPayAction(PaymentDesc.ID_ALIPAY);
            }else if(pay_type == PaymentDesc.ID_WE_CHAT){
                paymentView.selectPayAction(PaymentDesc.ID_WE_CHAT);
            }
            mNeedReloadData = true;
            addUmengEvent(param,pay_type,bean.weg_type);
            paymentView.startPayment(param);
        }
    }

    /**
     * 有盟统计 
     * @author xcx
     * @param orderParam
     */
    private void addUmengEvent(GetOrderParam orderParam,int payType,int weg_type){
        StringBuffer uMengSuccessIds=new StringBuffer();
        StringBuffer uMengFailIds=new StringBuffer();
        if(weg_type==1){//水
            if (PaymentDesc.ID_ALIPAY==payType) {
                uMengSuccessIds.append(UMengEventIds.DISCOVER_YELLOWPAGE_SHUIDIANMEI_WATER_ALIPAY_SUCCESS);
                uMengFailIds.append(UMengEventIds.DISCOVER_YELLOWPAGE_SHUIDIANMEI_WATER_ALIPAY_FAIL);
            }else{
                uMengSuccessIds.append(UMengEventIds.DISCOVER_YELLOWPAGE_SHUIDIANMEI_WATER_WECHAT_SUCCESS);
                uMengFailIds.append(UMengEventIds.DISCOVER_YELLOWPAGE_SHUIDIANMEI_WATER_WECHAT_FAIL);
            }
           
        }else if(weg_type==2){//电
            if (PaymentDesc.ID_ALIPAY==payType) {
                uMengSuccessIds.append(UMengEventIds.DISCOVER_YELLOWPAGE_SHUIDIANMEI_ELECTRICITY_ALIPAY_SUCCESS);
                uMengFailIds.append(UMengEventIds.DISCOVER_YELLOWPAGE_SHUIDIANMEI_ELECTRICITY_ALIPAY_FAIL);
            }else{
                uMengSuccessIds.append(UMengEventIds.DISCOVER_YELLOWPAGE_SHUIDIANMEI_ELECTRICITY_WECHAT_SUCCESS);
                uMengFailIds.append(UMengEventIds.DISCOVER_YELLOWPAGE_SHUIDIANMEI_ELECTRICITY_WECHAT_FAIL);
            }
           
        }else {//煤 
            if (PaymentDesc.ID_ALIPAY==payType) {
                uMengSuccessIds.append(UMengEventIds.DISCOVER_YELLOWPAGE_SHUIDIANMEI_GAS_ALIPAY_SUCCESS);
                uMengFailIds.append(UMengEventIds.DISCOVER_YELLOWPAGE_SHUIDIANMEI_GAS_ALIPAY_FAIL);
            }else{
                uMengSuccessIds.append(UMengEventIds.DISCOVER_YELLOWPAGE_SHUIDIANMEI_GAS_WECHAT_SUCCESS);
                uMengFailIds.append(UMengEventIds.DISCOVER_YELLOWPAGE_SHUIDIANMEI_GAS_WECHAT_FAIL);
            }
           
        }
        
        uMengSuccessIds.append(",");
        uMengSuccessIds.append(UMengEventIds.DISCOVER_YELLOWPAGE_SHUIDIANMEI_ALL_SUCCESS);
        
        uMengFailIds.append(",");
        uMengFailIds.append(UMengEventIds.DISCOVER_YELLOWPAGE_SHUIDIANMEI_ALL_FAIL);
        orderParam.putUIPair(UMengEventIds.EXTRA_UMENG_EVENT_IDS_SUCCESS,uMengSuccessIds.toString());
        orderParam.putUIPair(UMengEventIds.EXTRA_UMENG_EVENT_IDS_FAIL,uMengFailIds.toString());
    }
    @Override
    public void onPaymentFeedback(int actionType, Throwable t, int resultCode,
            Map<String, String> extras) {
        // TODO Auto-generated method stub
        
    }
}
