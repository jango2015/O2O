package so.contacts.hub.ui.yellowpage;

import so.contacts.hub.active.ActiveUtils;
import so.contacts.hub.active.bean.ActiveEggBean;
import so.contacts.hub.charge.ChargeConst;
import so.contacts.hub.charge.ChargeUtils;
import so.contacts.hub.http.bean.OrderInfo;
import so.contacts.hub.http.bean.TelOrderInfo;
import so.contacts.hub.remind.BaseRemindActivity;
import so.contacts.hub.shuidianmei.WEGUtil;
import so.contacts.hub.shuidianmei.bean.WEGOrderInfo;
import so.contacts.hub.ui.yellowpage.bean.ChargeHistoryItem;
import so.contacts.hub.ui.yellowpage.bean.YellowParams;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.UMengEventIds;
import so.contacts.hub.util.WebViewDialogUtils;
import so.contacts.hub.util.YellowUtil;
import so.contacts.hub.widget.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import so.contacts.hub.util.MobclickAgentUtil;

import com.yulong.android.contacts.discover.R;

public class YellowPageChargeResultActivity extends BaseRemindActivity implements OnClickListener {
	private static final String TAG = "YellowPageChargeResultActivity";
	//话费充值
	public static final int CONTENT_TEL = 1;
	//水电煤
	public static final int CONTENT_WEG = 2;
	
	//支付宝
    public static final int TYPE_AIRPLAY = 1;
    //微信
    public static final int TYPE_WEIXIN = 2;
	
	public static final String CONTENT_KEY = "rechage_content";
	
	// layout view
	private LinearLayout mChargeResultLayout = null;
	
	private ImageView mResultImgView = null;

	private TextView mResultTView = null;

	private TextView mResultHintTView = null;

	private TextView mResultChargePhoneTVIew = null;

	private TextView mResultPhoneAddrTVIew = null;

	private TextView mResultSerialTVIew = null;

	private TextView mResultPriceTVIew = null;

	private TextView mResultSuccessPriceTVIew = null;
	
	private LinearLayout mChargeStatusLayout = null;
	
	//水电煤
	private TextView mResultUnitTVIew = null;
	
	private TextView mResultAccountTVIew = null;
	
	// layout data
	protected String mTitle = "";
	
	protected TelOrderInfo mTelOrderInfo = null;  // 订单的支付状态
		
	private int mOrderChargeStatus = 0;   // 0-充值状态pending, 1-充值成功, 2-充值处理中, 3-充值失败 , 4-网络异常，5-服务器超时,  10-支付失败
	
	private ProgressDialog mProgressDialog = null;
	
	private Thread mQryStatusTask = null;
	
	//充值内容 如 电话费 水电煤
	protected int mRechargeContent = CONTENT_TEL ;
	//充值类型 如 支付宝 微信
	protected int mRechargeType = TYPE_AIRPLAY;
	
	//是否初始化界面 现在如果是由微信界面打开则在onCreate不加载界面 
	protected boolean isInit = true;
	
	//水电煤
	protected WEGOrderInfo mWEGOrderInfo = null;  // 订单的支付状态
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

        if (isInit) {
            Intent intent = getIntent();

            if (intent != null) {
                //如果不传值 则默认为 话费结果界面
                int type = intent.getIntExtra(CONTENT_KEY,CONTENT_TEL);
                mRechargeContent = type;
            }
            if (mRechargeContent == CONTENT_TEL) {
                setContentView(R.layout.putao_yellow_page_charge_result);
            } else if (mRechargeContent == CONTENT_WEG) {
                setContentView(R.layout.putao_weg_yellow_page_weg_result);
            }

            initView();
            initLayoutData();
        }
	}
	
	protected void initView(){
        if (mRechargeContent == CONTENT_TEL) {
            initTelView();
        } else if (mRechargeContent == CONTENT_WEG) {
            initWEGView();
        }
	}

    private void initTelView() {
        findViewById(R.id.back_layout).setOnClickListener(this);
        mChargeResultLayout = (LinearLayout)findViewById(R.id.charge_result_layout);

        mChargeStatusLayout = (LinearLayout)findViewById(R.id.charge_stuats_layout);
        mResultHintTView = (TextView)findViewById(R.id.charge_result_hint);

        mResultImgView = (ImageView)findViewById(R.id.charge_result_img);
        mResultTView = (TextView)findViewById(R.id.charge_result_text);
        mResultChargePhoneTVIew = (TextView)findViewById(R.id.charge_result_phone);
        mResultPhoneAddrTVIew = (TextView)findViewById(R.id.charge_result_addr);
        mResultSerialTVIew = (TextView)findViewById(R.id.charge_result_serialnum);
        mResultPriceTVIew = (TextView)findViewById(R.id.charge_result_money);
        mResultSuccessPriceTVIew = (TextView)findViewById(R.id.charge_result_success_money);

        TextView question = (TextView)this.findViewById(R.id.question);
        question.setOnClickListener(this);
        question.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        mProgressDialog = new ProgressDialog(this, R.style.putao_ChargeProgressDialog);
        mProgressDialog.setCancelable(false);
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
            mProgressDialog.setMessage(getString(R.string.putao_charge_qry_status_ing));
        }
    }
	
    private void initWEGView() {
        findViewById(R.id.back_layout).setOnClickListener(this);
        mChargeResultLayout = (LinearLayout)findViewById(R.id.charge_result_layout);

        mChargeStatusLayout = (LinearLayout)findViewById(R.id.charge_stuats_layout);
        mResultHintTView = (TextView)findViewById(R.id.charge_result_hint);

        mResultImgView = (ImageView)findViewById(R.id.charge_result_img);
        mResultTView = (TextView)findViewById(R.id.charge_result_text);
        mResultSerialTVIew = (TextView)findViewById(R.id.charge_result_serialnum);
        mResultPriceTVIew = (TextView)findViewById(R.id.charge_result_money);
        mResultSuccessPriceTVIew = (TextView)findViewById(R.id.charge_result_success_money);
        
        mResultUnitTVIew = (TextView)findViewById(R.id.charge_result_unit);
        mResultAccountTVIew = (TextView)findViewById(R.id.charge_result_account);

        mProgressDialog = new ProgressDialog(this, R.style.putao_ChargeProgressDialog);
        mProgressDialog.setCancelable(false);
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
            mProgressDialog.setMessage(getString(R.string.putao_charge_qry_status_ing));
        }
    }
	
	/**
	 * 初始化界面数据
	 */
	protected void initLayoutData() {
        Intent intent = getIntent();
		if (intent == null) {
			finish();
			return;
		}
		mTitle = intent.getStringExtra("title");
		if(mRechargeContent == CONTENT_TEL){
	        mTelOrderInfo = (TelOrderInfo) intent.getSerializableExtra("OrderInfo");
	        if( mTelOrderInfo == null ){
	            finish();
	            return;
	        }  
		}else if(mRechargeContent == CONTENT_WEG){
		    mWEGOrderInfo = (WEGOrderInfo)intent.getSerializableExtra("OrderInfo");
            if (mWEGOrderInfo == null) {
                finish();
                return;
            }
		}
		
		mHandler.sendEmptyMessage(MSG_SHOW_DATA_ACTION);
	}

	/**
	 * 加载界面数据
	 */
	private void loadLayoutWithData(){
		if( TextUtils.isEmpty(mTitle) ){
		      if(mRechargeContent == CONTENT_TEL){
		          ((TextView) findViewById(R.id.title)).setText(R.string.putao_charge_tag_title_charge);
		        }else if(mRechargeContent == CONTENT_WEG){
		            
		        }
		}else{
			((TextView) findViewById(R.id.title)).setText(mTitle);
		}
		
        if (mRechargeContent == CONTENT_TEL) {
            if (mTelOrderInfo != null
                    && mTelOrderInfo.resultStatus != ChargeConst.ServRtnStatus_Coupon_Error) {
                // 查询充值状态
                if (mQryStatusTask == null) {
                    mQryStatusTask = new QueryChargeStatusTask(mTelOrderInfo.out_trade_no);
                    mQryStatusTask.start();
                }
            } else {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
                mOrderChargeStatus = ChargeConst.ServRtnStatus_Coupon_Error;
            }
        }else if(mRechargeContent == CONTENT_WEG){
            if (mWEGOrderInfo != null
                    && mWEGOrderInfo.resultStatus != ChargeConst.ServRtnStatus_Coupon_Error) {
                // 查询充值状态
                if (mQryStatusTask == null) {
                    mQryStatusTask = new QueryChargeStatusTask(mWEGOrderInfo.out_trade_no);
                    mQryStatusTask.start();
                }
            } else {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
                mOrderChargeStatus = ChargeConst.ServRtnStatus_Coupon_Error;
            }
        }

		updateResultData();
	}
	
	private void updateResultData() {
	    OrderInfo info = null ;
	    if(mRechargeContent == CONTENT_TEL){
	        if( mTelOrderInfo == null ){
	            // 订单信息为空
	            return;
	        }else{
	            info = mTelOrderInfo;
	        }
	    }else if(mRechargeContent == CONTENT_WEG){
	        if( mWEGOrderInfo == null ){
                // 订单信息为空
                return;
                
            }else{
                info = mWEGOrderInfo;
            }
	    }
	    
		boolean isSuccessed = (info.resultStatus == ChargeConst.AlipayRtnOrderStatus_Success) && info.success;
		int resultStatus = info.resultStatus;
		String serialNum = info.out_trade_no;
		String price = info.total_fee;
		String markPrice = info.mark_price;
		LogUtil.i(TAG, "updateResultData orderInfo=" + info.toString());
		LogUtil.i(TAG, "updateResultData order_no="+serialNum+" payStatus="+isSuccessed+" chargeStatus="+mOrderChargeStatus);
		
		// 只要交易序列号存在，就必须去查询
		if (!TextUtils.isEmpty(info.out_trade_no)) {
			
			if(mOrderChargeStatus == 0) {  // 后台没有同步完成
				mChargeStatusLayout.setVisibility(View.GONE);
				mResultHintTView.setVisibility(View.GONE);
			} else {  // 后台返回状态
				mChargeStatusLayout.setVisibility(View.VISIBLE);
				mResultHintTView.setVisibility(View.VISIBLE);
				
				// 产品价值只有查询状态返回后才显示，支付失败暂不显示
				if(!TextUtils.isEmpty(price)){
				    mResultSuccessPriceTVIew.setText(String.format(getResources().getString(R.string.putao_charge_chy_data), price));
				}else{
				    mResultSuccessPriceTVIew.setText(String.format(getResources().getString(R.string.putao_charge_chy_data), markPrice));
				}
			}
            if (mRechargeContent == CONTENT_TEL) {
                switch (mOrderChargeStatus) {
                    case ChargeConst.ChargeRtnStatus_Pay_failed:
                        // 支付失败
                        mResultImgView.setImageResource(R.drawable.putao_icon_logo_failed);
                        mResultTView.setText(R.string.putao_charge_deal_failed);
                        switch (mRechargeType) {
                            case TYPE_AIRPLAY:
                                mResultHintTView.setText(R.string.putao_charge_deal_failed_hint);
                                break;
                            case TYPE_WEIXIN:
                                mResultTView.setText(R.string.putao_charge_deal_failed_hint_weixin);
                                break;
                            default:
                                break;
                        }
                        break;
                    case ChargeConst.ChargeRtnStatus_Ok:
                        // 支付成功
                        mResultImgView.setImageResource(R.drawable.putao_icon_exp_checkbox_p);
                        mResultTView.setText(R.string.putao_charge_deal_success);
                        mResultHintTView.setText(String.format(
                                getResources().getString(R.string.putao_charge_tips_for_success),
                                markPrice));

                        break;
                    case ChargeConst.ChargeRtnStatus_Failed:
                        // 支付失败
                        mResultImgView.setImageResource(R.drawable.putao_icon_logo_failed);
                        mResultTView.setText(R.string.putao_charge_deal_failed);
                        mResultHintTView.setText(String.format(
                                getResources().getString(R.string.putao_charge_tips_for_failed),
                                price));

                        break;
                    case ChargeConst.ChargeRtnStatus_Pending:
                        // 正在处理中...
                        mResultImgView.setImageResource(R.drawable.putao_icon_exp_checkbox_p);
                        mResultTView.setText(R.string.putao_charge_deal_success);
                        mResultHintTView.setText(R.string.putao_charge_tips_for_pending);

                        break;
                    case ChargeConst.ChargeRtnStatus_Neterror:
                        // 网络异常
                        mResultImgView.setImageResource(R.drawable.putao_icon_exp_checkbox_p);
                        mResultTView.setText(R.string.putao_charge_deal_success);
                        mResultHintTView.setText(R.string.putao_charge_tips_for_pending);
                        break;
                    case ChargeConst.ChargeRtnStatus_Timeout:
                        // 服务器超时
                        mResultImgView.setImageResource(R.drawable.putao_icon_exp_checkbox_p);
                        mResultTView.setText(R.string.putao_charge_deal_success);
                        mResultHintTView.setText(R.string.putao_charge_tips_for_pending);

                        break;
                    case ChargeConst.ChargeRtnStatus_Serv_busy:
                        // 服务器繁忙
                        mResultImgView.setImageResource(R.drawable.putao_icon_exp_checkbox_p);
                        mResultTView.setText(R.string.putao_charge_deal_success);
                        mResultHintTView.setText(R.string.putao_charge_tips_for_pending);
                        break;
                    default:
                        break;
                }
            } else if (mRechargeContent == CONTENT_WEG) {
                switch (mOrderChargeStatus) {
                    case ChargeConst.ChargeRtnStatus_Pay_failed:
                        // 支付失败
                        mResultImgView.setImageResource(R.drawable.putao_icon_logo_failed);
                        mResultTView.setText(R.string.putao_water_eg_tag_deal_failed);
                        switch (mRechargeType) {
                            case TYPE_AIRPLAY:
                                mResultHintTView.setText(R.string.putao_water_eg_tag_deal_failed_hint);
                                break;
                            case TYPE_WEIXIN:
                                mResultHintTView.setText(R.string.putao_water_eg_tag_deal_failed_hint_weixin);
                                break;
                            default:
                                break;
                        }
                        break;
                    case ChargeConst.ChargeRtnStatus_Ok:
                        // 支付成功
                        mResultImgView.setImageResource(R.drawable.putao_icon_exp_checkbox_p);
                        mResultTView.setText(R.string.putao_water_eg_tag_deal_success);
                        mResultHintTView.setText(String.format(
                                getResources().getString(
                                        R.string.putao_water_eg_tag_deal_success_hint),WEGUtil.getRechargeStringByType(this, mWEGOrderInfo.weg_type)));
                        break;
                    case ChargeConst.ChargeRtnStatus_Failed:
                        // 支付失败
                        mResultImgView.setImageResource(R.drawable.putao_icon_logo_failed);
                        mResultTView.setText(R.string.putao_water_eg_tag_deal_failed);
                        mResultHintTView.setText(String.format(
                                getResources().getString(
                                        R.string.putao_charge_tips_for_failed), price));

                        break;
                    case ChargeConst.ChargeRtnStatus_Pending:
                        // 正在处理中...
                        mResultImgView.setImageResource(R.drawable.putao_icon_exp_checkbox_p);
                        mResultTView.setText(R.string.putao_water_eg_tag_deal_success);
                        mResultHintTView.setText(R.string.putao_water_eg_tag_deal_failed_tips_for_pending);

                        break;
                    case ChargeConst.ChargeRtnStatus_Neterror:
                        // 网络异常
                        mResultImgView.setImageResource(R.drawable.putao_icon_exp_checkbox_p);
                        mResultTView.setText(R.string.putao_water_eg_tag_deal_success);
                        mResultHintTView.setText(R.string.putao_water_eg_tag_deal_failed_tips_for_pending);
                        break;
                    case ChargeConst.ChargeRtnStatus_Timeout:
                        // 服务器超时
                        mResultImgView.setImageResource(R.drawable.putao_icon_exp_checkbox_p);
                        mResultTView.setText(R.string.putao_water_eg_tag_deal_success);
                        mResultHintTView.setText(R.string.putao_water_eg_tag_deal_failed_tips_for_pending);

                        break;
                    case ChargeConst.ChargeRtnStatus_Serv_busy:
                        // 服务器繁忙
                        mResultImgView.setImageResource(R.drawable.putao_icon_exp_checkbox_p);
                        mResultTView.setText(R.string.putao_water_eg_tag_deal_success);
                        mResultHintTView.setText(R.string.putao_water_eg_tag_deal_failed_tips_for_pending);
                        break;
                    default:
                        break;
                }
            }
        } else {
			if( mOrderChargeStatus == ChargeConst.ServRtnStatus_Coupon_Error ){
				// 优惠券已过期，服务端不会生成订单号
				mResultImgView.setImageResource(R.drawable.putao_icon_logo_failed);
				mResultTView.setText(R.string.putao_charge_deal_failed);
				mResultHintTView.setText(R.string.putao_charge_deal_failed_coupon_timeout);
				mResultSuccessPriceTVIew.setText(String.format(getResources().getString(R.string.putao_charge_chy_data), price));
			}else{
				//此处一般情况是进不来的，如果进来是异常情况，需处理
				LogUtil.i(TAG, "OrderInfo serialNum: " + mTelOrderInfo.out_trade_no + " exception... ");
				
				mResultImgView.setImageResource(R.drawable.putao_icon_logo_failed);
				mResultTView.setText(getResources().getString(R.string.putao_charge_deal_failed));
				if (resultStatus == ChargeConst.AlipayRtnOrderStatus_Canceled) {
					// 中途取消
					mResultHintTView.setText(getResources().getString(R.string.putao_charge_deal_failed_cancel));
					MobclickAgentUtil.onEvent(this, UMengEventIds.DISCOVER_YELLOWPAGE_CHARGE_CANCEL);
				}
				/* else {
				// 支付失败
				mResultHintTView.setText(getResources().getString(R.string.putao_charge_deal_failed_hint));
				MobclickAgentUtil.onEvent(this, UMengEventIds.DISCOVER_YELLOWPAGE_CHARGE_FAIL);				
			   }*/
			}
		}
		if(mRechargeContent == CONTENT_TEL){
		    ChargeHistoryItem historyItem = new ChargeHistoryItem(this, mTelOrderInfo.mobile);
		    mResultChargePhoneTVIew.setText(historyItem.getPhoneNum());
		    mResultPhoneAddrTVIew.setText(historyItem.getProvinceAndOperator());
		}else if(mRechargeContent == CONTENT_WEG){
		    mResultUnitTVIew.setText(mWEGOrderInfo.company);
		    
		    mResultAccountTVIew.setText(mWEGOrderInfo.accountNum);
		}
		
		if(TextUtils.isEmpty(serialNum)) {
		    mResultSerialTVIew.setText(getResources().getString(R.string.putao_charge_no_trade_num));
		} else {
		    mResultSerialTVIew.setText(serialNum);
		}
		mResultPriceTVIew.setText(String.format(getResources().getString(R.string.putao_yellow_page_detail_customsprice), markPrice));
		
		mChargeResultLayout.setVisibility(View.VISIBLE);
		
		/**
		 * 增加对彩蛋的判断，在expand_param中传递状态码
		 */
		ActiveEggBean egg = getValidEgg();
		if(egg != null) {
		    String expand_param = egg.expand_param;
		    if(TextUtils.isEmpty(expand_param) || String.valueOf(mOrderChargeStatus).equals(expand_param)) {
		        LogUtil.i(TAG, "oh yeah, find one egg: "+egg.toString());
		        WebViewDialogUtils.startWebDialog(this, ActiveUtils.getRequrlOfSign(egg));
		    }
		}
	}
	
	protected static final int MSG_SHOW_DATA_ACTION = 0x2001;
	
	protected static final int MSG_UPDATE_DATA_ACTION = 0x2002;
	
	protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	int what = msg.what;
        	switch(what){
        	case MSG_SHOW_DATA_ACTION:
        		loadLayoutWithData();
        		break;
        	case MSG_UPDATE_DATA_ACTION:
                if(mProgressDialog != null)  {
                	mProgressDialog.dismiss();
                }
        		updateResultData();
        		break;
        	}
        }
	};
	
    /**
     * 查询充值状态任务
     * 1-充值成功, 2-充值处理中, 3-充值失败 , 4-网络异常，5-超时, 6-服务器繁忙
     */
	private class QueryChargeStatusTask extends Thread {
		private String order_no = "";
		
		public QueryChargeStatusTask(String order_no) {
			this.order_no = order_no;
		}
		
		@Override
		public void run() {
			long start = System.currentTimeMillis();
			LogUtil.i(TAG, "QueryChargeStatusTask enter order_no="+order_no);
			
			int retry_times = 1 * 1000;
			int timeout = 30 * 1000;
			try {
			    if(mRechargeContent == CONTENT_TEL){
			        mOrderChargeStatus = ChargeUtils.qryChargeStatus(order_no, true, retry_times, timeout);
			    }else if(mRechargeContent == CONTENT_WEG){
			        mOrderChargeStatus = WEGUtil.qryChargeStatus(order_no, true, retry_times, timeout);
			    }
			} catch (Exception e) {
				e.printStackTrace();
			}
            
            LogUtil.i(TAG, "QueryChargeStatusTask done, chargeStatus="+mOrderChargeStatus+" takeTimes="+(System.currentTimeMillis()-start));
            
            mHandler.sendEmptyMessage(MSG_UPDATE_DATA_ACTION);
		}
	};
	
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgentUtil.onResume(this);
		if ( mProgressDialog != null && !mProgressDialog.isShowing() && mOrderChargeStatus==0) {
			mProgressDialog.setMessage(getString(R.string.putao_charge_qry_status_ing));
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgentUtil.onPause(this);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		setResult(RESULT_OK);
		finish();
	}
	
	@Override
	public void onClick(View view) {
		int id = view.getId();
		if (id == R.id.back_layout) {
			onBackPressed();
		} else if (id == R.id.question) {
			Intent intent = new Intent(this, ChargeQuestionActivity.class);
			YellowParams params = new YellowParams();
			params.setTitle(this.getResources().getString(R.string.putao_charge_question));
			params.setUrl(this.getResources().getString(R.string.putao_charge_question_url));
			intent.putExtra(YellowUtil.TargetIntentParams, params);
			this.startActivity(intent);
		}
	}

    @Override
    public String getServiceNameByUrl() {
        return null;
    }

    @Override
    public Integer remindCode() {
        return -1;
    }

	@Override
	public String getServiceName() {
		return this.getClass().getName();
	}

	@Override
	public boolean needMatchExpandParam() {
	    //modity by ljq  2014/12/6
	    if(mRechargeContent == CONTENT_TEL){
	        return true;
	    }else if(mRechargeContent == CONTENT_WEG){
	        return false;
	    }else{
	        return false;
	    }
	}

}
