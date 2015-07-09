package so.contacts.hub.ui.yellowpage;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import so.contacts.hub.ContactsApp;
import so.contacts.hub.account.IAccCallback;
import so.contacts.hub.account.PutaoAccount;
import so.contacts.hub.adapter.YellowPageChargeHistoryAdapter;
import so.contacts.hub.charge.ChargeConst;
import so.contacts.hub.charge.ChargeUtils;
import so.contacts.hub.charge.GetOrderInfo2PayTask;
import so.contacts.hub.core.Config;
import so.contacts.hub.http.bean.ChargeTelephoneHistoryRequest;
import so.contacts.hub.http.bean.ChargeTelephoneHistoryResponse;
import so.contacts.hub.http.bean.TelOrderInfo;
import so.contacts.hub.http.bean.ProductDescBean;
import so.contacts.hub.remind.BaseRemindActivity;
import so.contacts.hub.remind.RemindConfig;
import so.contacts.hub.ui.yellowpage.bean.ChargeTelephoneHistoryBean;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.NetUtil;
import so.contacts.hub.util.UMengEventIds;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mdroid.core.http.IgnitedHttpResponse;
import com.mdroid.core.util.SystemUtil;

import so.contacts.hub.util.MobclickAgentUtil;

import com.yulong.android.contacts.discover.R;

public class YellowPageChargeHistoryActivity extends BaseRemindActivity implements 
	OnClickListener, OnLoadMoreListener, IAccCallback{

	private static final String TAG = "YellowPageChargeHistoryActivity";
	
	private CustomListView mHistoryListView = null;
	
	private LinearLayout mHistoryNoDataLayout = null;
	
	private TextView mShowHintTView = null;
	
	private ProgressDialog mProgressDialog = null;
	
	private YellowPageChargeHistoryAdapter mHistoryAdapter = null;
	
	private QueryChargeHistoryTask mQueryHistoryTask = null;
	
	private GetOrderInfo2PayTask mPayTask = null;
	
	private List<ChargeTelephoneHistoryBean> mHistoryList = new ArrayList<ChargeTelephoneHistoryBean>();
	
	// 充值历史中 点击“立即付款”时的数据
	private ChargeTelephoneHistoryBean mChargeBean = null;
	
	private static final int MSG_SHOW_DIALOG_ACTION = 0x2001;
	
	private static final int MSG_DISMISS_DIALOG_ACTION = 0x2002;

	private static final int MSG_SHOW_NODATA_ACTION = 0x2003;

	private static final int MSG_SHOW_NODATA_NONET_ACTION = 0x2004;

    public static final int MSG_NETWORK_EXCEPTION_ACTION = 0x2005;

    // 每次请求的数据位20条
    private static final int PAGE_DATA_SIZE = 20;
    
    // 每次请求的页码
    private int mRequestPageNum = -1;
    
    private String mDeviceCode = "";
    
    // 是否正在加载更多数据
    private boolean mLoadMore = false;
    
    //是否有更多数据（上一次请求的数据小于PAGE_DATA_SIZE，则说明没有更多数据）
    private boolean mHasMoreData = true;
    
    // 是否需要重新加载所有数据
    private boolean mNeedReloadData = false;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.putao_yellow_page_chargehistory);
		
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
		
        
		
		
		MobclickAgentUtil.onEvent(this, UMengEventIds.DISCOVER_YELLOWPAGE_MY_ORDER_CHARGE);
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
		
	}

	private void initData() {
		// TODO Auto-generated method stub
		mHistoryAdapter = new YellowPageChargeHistoryAdapter(this);
		mHistoryAdapter.setData(mHistoryList);
		/*
		 * 点击效果修改，改为整条处理，所以将按钮的点击事件注释
		 * modified by hyl 2014-12-23 start
		 */
//		mHistoryAdapter.setOnClickListener(mAdapterClickListener);
		//modified by hyl 2014-12-23 end
		
		mHistoryListView.setAdapter(mHistoryAdapter);
		/*
		 * 处理点击事件
		 * add by hyl 2014-12-23 start
		 */
		mHistoryListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if( !NetUtil.isNetworkAvailable(YellowPageChargeHistoryActivity.this) ){
					mHandler.sendEmptyMessageDelayed(MSG_NETWORK_EXCEPTION_ACTION, 300);
					return;
				}
				if(mHistoryList.size() > (arg2-1)){
					ChargeTelephoneHistoryBean historyBean = mHistoryList.get(arg2-1);
					if( historyBean == null ){
						// 数据为空
						return;
					}
					int chargeState = historyBean.getCharge_state();
					if( chargeState == ChargeConst.ChargeHistoryStatus_Waitcharge ){
						doCharge(historyBean);
					}
				}
			}
		});
		//add by hyl 2014-12-23 start
		
		if( TextUtils.isEmpty(mDeviceCode) ){
			mDeviceCode = SystemUtil.getDeviceId(ContactsApp.getInstance());
		}
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
		if ( (mQueryHistoryTask != null && mQueryHistoryTask.getStatus() != AsyncTask.Status.RUNNING)
				|| mQueryHistoryTask == null ) {
			mQueryHistoryTask = new QueryChargeHistoryTask(mRequestPageNum);
			mQueryHistoryTask.execute();
        }
	}
	
	/**
	 * 立即充值
	 */
	private void doCharge(ChargeTelephoneHistoryBean historyBean){
		if ( (mPayTask != null && mPayTask.getStatus() != AsyncTask.Status.RUNNING)
				|| mPayTask == null ){
			mChargeBean = historyBean;
			mPayTask = new GetOrderInfo2PayTask(this, mHandler,2);
			/*
			 * 增加支付类型参数 （historyBean.payType）
			 * modified by hyl 2014-10-13 start
			 * old code：
			 * mPayTask.execute(historyBean.getProduct_id(), historyBean.getMobile(),
                     historyBean.getPay_price(), historyBean.getRemark_price(), 
                     historyBean.getOrder_id());
			 */
			mPayTask.execute(historyBean.getProduct_id(), historyBean.getMobile(),
					 historyBean.getPay_price(), historyBean.getRemark_price(), 
					 historyBean.getOrder_id(), String.valueOf(historyBean.payType), String.valueOf(historyBean.getFavo_id()));
			//modified by hyl 2014-10-13 end
		}
	}
	
	/**
     * 获取到支付宝支付结果，跳转到结果页
     */
    private void chargeResult(TelOrderInfo orderInfo) {
        if (orderInfo == null) {
            return;
        }
        mNeedReloadData = true;
        //组装ProductDescBean
        ProductDescBean productBean = new ProductDescBean();
        try{
        	productBean.product_id = Long.valueOf(mChargeBean.getProduct_id());
        }catch(Exception e){
        }
        productBean.mark_price = mChargeBean.getRemark_price();
        
        Intent intent = new Intent(this, YellowPageChargeResultActivity.class);
        intent.putExtra("OrderInfo", orderInfo);
        intent.putExtra("ProduceInfo", productBean);
        intent.putExtra("title", mTitleContent);
        startActivity(intent);
        mChargeBean = null;
    }
	
    /**
     * 获取充值历史列表
     */
	private class QueryChargeHistoryTask extends AsyncTask<String, Void, ChargeTelephoneHistoryResponse> {

		private int pageNum = 0;
		
		public QueryChargeHistoryTask(int pageNum){
			this.pageNum = pageNum;
		}
		
        @Override
        protected ChargeTelephoneHistoryResponse doInBackground(String... arg0) {
        	return queryChargeTelHistory(pageNum);
        }

        @Override
        protected void onPostExecute(ChargeTelephoneHistoryResponse result) {
            super.onPostExecute(result);
            if( mLoadMore ){
        		mHistoryListView.onLoadMoreComplete(false);
            	mLoadMore = false;
        	}
        	mHandler.sendEmptyMessage(MSG_DISMISS_DIALOG_ACTION);
        	if( result == null ){
        		// 无数据 
        		mHasMoreData = false;
            	mHandler.sendEmptyMessage(MSG_SHOW_NODATA_NONET_ACTION);
            	LogUtil.i(TAG, "queryChargeTelHistory responsedata is null.");
            	mNeedReloadData = false;
        		return;
        	}
        	if( mNeedReloadData ){
            	mNeedReloadData = false;
            	mHistoryList.clear();
            }
        	List<ChargeTelephoneHistoryBean> historyList = result.order_trace_list;
        	if( historyList == null || historyList.size() <= 0 ){
        		// 无数据
        		mHasMoreData = false;
            	mHandler.sendEmptyMessage(MSG_SHOW_NODATA_ACTION);
            	LogUtil.i(TAG, "queryChargeTelHistory responsedata is empty.");
        		return;
        	}else{
        		// 有数据则添加进去
        		int historyListSize = historyList.size();
        		if( historyListSize < PAGE_DATA_SIZE ){
        			// 服务器没有更多数据
        			mHasMoreData = false;
        		}else{
        			// 服务器可能还有更多数据
        			mHasMoreData = true;
        		}
        		mHistoryList.addAll(historyList);
            	LogUtil.i(TAG, "queryChargeTelHistory responsedata size: " + historyListSize);
        	}
        	mHistoryAdapter.setData(mHistoryList);
        }
    }
	
	/**
	 * 获取充值历史列表
	 */
	private ChargeTelephoneHistoryResponse queryChargeTelHistory(int pageNum) {
		ChargeTelephoneHistoryRequest requestData = new ChargeTelephoneHistoryRequest(mDeviceCode, pageNum, PAGE_DATA_SIZE);
		ChargeTelephoneHistoryResponse responseData = null;
		IgnitedHttpResponse httpResponse = null;
		try{
			httpResponse = Config.getApiHttp().post(Config.SERVER, requestData.getData()).send();
			String content = httpResponse.getResponseBodyAsString();
			responseData = requestData.getObject(content);
			if (responseData != null) {
				if (responseData.isSuccess()) {
					LogUtil.i(TAG, "queryChargeTelHistory ok");
				} else {
					LogUtil.i(TAG, "queryChargeTelHistory fail, errcode=" + responseData.ret_code);
					responseData = null;
				}
			}
		}catch (ConnectException e) {
			responseData = null;
			LogUtil.i(TAG, "queryChargeTelHistory ConnectException...");
        } catch (IOException e) {
			responseData = null;
			LogUtil.i(TAG, "queryChargeTelHistory IOException...");
        }catch (Exception e) {
			responseData = null;
			LogUtil.i(TAG, "queryChargeTelHistory Exception...");
        }
		return responseData;
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
					Utils.showToast(YellowPageChargeHistoryActivity.this, R.string.putao_charge_history_nomoredata, false);
				}
				break;
			case MSG_SHOW_NODATA_NONET_ACTION:
				// 网络异常 导致 请求无数据
				if( mHistoryList.size() == 0 ){
					mHistoryNoDataLayout.setVisibility(View.VISIBLE);
					mShowHintTView.setText(R.string.putao_netexception_hint);
					mHistoryListView.setVisibility(View.GONE);
				}else{
					Utils.showToast(YellowPageChargeHistoryActivity.this, R.string.putao_netexception, false);
				}
				break;
			case MSG_NETWORK_EXCEPTION_ACTION:
				mHandler.removeMessages(MSG_NETWORK_EXCEPTION_ACTION);
				Utils.showToast(YellowPageChargeHistoryActivity.this, R.string.putao_no_net, false);
				break;
			case ChargeUtils.MSG_SHOW_CHARGE_RESULT_ACTION:
                // 获取话费订单- 结果
                TelOrderInfo orderInfo = (TelOrderInfo)msg.obj;
                chargeResult(orderInfo);
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
	
	private OnClickListener mAdapterClickListener = new OnClickListener(){
		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			if( !NetUtil.isNetworkAvailable(YellowPageChargeHistoryActivity.this) ){
				mHandler.sendEmptyMessageDelayed(MSG_NETWORK_EXCEPTION_ACTION, 300);
				return;
			}
			View parent = (View) view.getParent().getParent();
			ChargeTelephoneHistoryBean historyBean = (ChargeTelephoneHistoryBean) parent.getTag();
			if( historyBean == null ){
				// 数据为空
				return;
			}
			doCharge(historyBean);
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
        if(mQueryHistoryTask != null){
            mQueryHistoryTask.cancel(true);
            mQueryHistoryTask = null;
        }
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
        return RemindConfig.MyOrderChargeHistory;
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
}
