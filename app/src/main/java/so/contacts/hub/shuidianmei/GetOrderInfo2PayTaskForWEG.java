package so.contacts.hub.shuidianmei;

import java.util.List;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.account.PutaoAccount;
import so.contacts.hub.charge.ChargeConst;
import so.contacts.hub.charge.ChargeUtils;
import so.contacts.hub.common.PayConfig;
import so.contacts.hub.core.Config;
import so.contacts.hub.core.PTUser;
import so.contacts.hub.payment.core.PaymentDesc;
import so.contacts.hub.shuidianmei.bean.WEGOrderInfo;
import so.contacts.hub.shuidianmei.bean.WaterElectricityGasBean;
import so.contacts.hub.ui.yellowpage.YellowPageChargeTelephoneFragment;
import so.contacts.hub.util.ContactsAppUtils;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.Utils;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.alipay.android.app.sdk.AliPay;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.yulong.android.contacts.discover.R;

/**
 * 从后台获取订单信息,再组合成alipay符合的字符串,调用sdk支付接口
 * @author change
 */
public class GetOrderInfo2PayTaskForWEG extends AsyncTask<String, Void, Boolean> implements Config.CallBack{
	private static final String TAG = GetOrderInfo2PayTaskForWEG.class.getSimpleName();
	
	private Activity mContext;
	private Handler mHandler;
	
	private String mAlipayResult = "";
	private int mErrorCode = 0;
	private String mErrMsg = "";  // 仅当服务器停机
	private WEGOrderInfo mOrderInfo = new WEGOrderInfo();
	private int mPosition = 0;  // 1-从充值服务主入口进入 2-从充值历史进入 3-从充流量进入
	
	public GetOrderInfo2PayTaskForWEG(final Activity context, Handler handler, int position) {
		mContext = context;
		mHandler = handler;
		mPosition = position;
	}
	
	@Override
	protected Boolean doInBackground(String... args) {
		LogUtil.i(TAG, "doInBackground start.");
		mOrderInfo.timeStr = "";
		mOrderInfo.product_id = args[0];
		mOrderInfo.accountNum = args[1];
		mOrderInfo.mark_price  = args[2];
		mOrderInfo.company = args[3];
		// 订单编号
		String orderNo = args[4];
		
		//add by hyl 2014-10-10 start 添加支付方式参数
		int pay_type = Integer.parseInt(args[5]);
		//add by hyl 2014-10-10 end
		
		//add by hyl 2014-10-19 start 添加代金券id
		String favo_id = null;
		if(args.length == 7){
		    favo_id  = args[6];
		}
		//add by hyl 2014-10-19 end 
		
		if(!TextUtils.isEmpty(mOrderInfo.product_id)){
		    List<WaterElectricityGasBean> beans = ContactsAppUtils.getInstance().getDatabaseHelper().getWaterElectricityGasDB().queryWegDataByProid(mOrderInfo.product_id);
		    if(beans != null && beans.size()>0){
		        mOrderInfo.weg_type = beans.get(0).getWeg_type();
		    }
		}
		
		
		try {
		    mOrderInfo.subject = String.format(mContext.getResources().getString(R.string.putao_water_eg_tag_subject_for_alipay), mOrderInfo.mark_price);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		PTUser ptUser = PutaoAccount.getInstance().getPtUser();
		/*if(ptUser == null || TextUtils.isEmpty(ptUser.getPt_token())){
		    YellowPageDataUtils.login(mContext, this);
		    ptUser = Config.getPTUser();
		}*/

		if(ptUser == null || TextUtils.isEmpty(ptUser.getPt_token())) {
		    mErrorCode = ChargeConst.ServRtnStatus_Internal_Error;
		    return false;
		}
		
		/** 判断是否需要回调到充值页面*/
		boolean needCallback = true;
		
		/*
		 * 由于新增了微信支付方式，所以在这里需要根据传入的支付类型 来判断 使用哪种支付方式的处理
		 * modified by hyl 2014-10-13 start
		 */
		if(pay_type == PaymentDesc.ID_ALIPAY){
		    payByAlipay(mOrderInfo.subject, orderNo, favo_id);
		}else if(pay_type ==  PaymentDesc.ID_WE_CHAT){
			// 微信支付 有自己的回调页面，因此根据是否有异常来判断是否需要回调到自己的页面
			needCallback = payByWeChat(mOrderInfo.product_id, mOrderInfo.accountNum, mOrderInfo.timeStr,mOrderInfo.subject,mOrderInfo.mark_price);
		}
		// modified by hyl 2014-10-13 end

		return needCallback;
	}

	/**
	 * 微信支付 
	 * create by hyl 2014-10-13
	 * @param subject 订单主题描述
	 * @param orderNo 订单号
	 * @return
	 */
	private boolean payByWeChat(String pro_id,String account, String yearmonth,String subject,String price) {
	    String reqOrderUrl = WEGUtil.createReqOrderUrlByWeChat(pro_id, account, yearmonth, subject,price);
        LogUtil.i(TAG, "reqOrderUrl= "+reqOrderUrl);
        
        boolean needCallback = false;
        try {
            WEGOrderInfo orderInfo = WEGUtil.getChargeOrderInfo(reqOrderUrl, mOrderInfo);
            
            String appId = orderInfo.appId;
            String partner_id = orderInfo.partnerId;
            String prepayId = orderInfo.prepayId;
            String nonceStr = orderInfo.nonceStr;
            long timeStamp = orderInfo.timeStamp;
            String packageValue = orderInfo.packageValue;
            String sign = orderInfo.sign;
            
            LogUtil.i(TAG, "payByWeChat appId: " + appId 
            		+ ",partner_id: " + partner_id
            		+ ",prepayId: " + prepayId
            		+ ",nonceStr: " + nonceStr
            		+ ",timeStamp: " + timeStamp
            		+ ",packageValue: " + packageValue
            		+ ",sign: " + sign );
            
            if( TextUtils.isEmpty(prepayId) ){
            	// 为空，生成预支付订单失败
            	LogUtil.i(TAG, "payByWeChat prepayId is null.");
            	mErrorCode = ChargeConst.AlipayRtnOrderStatus_Parameter_Error;
            	needCallback = true;
            }else{
            	// 生成预支付订单成功
            	PayConfig.WX_PAY_APPID = appId;
            	IWXAPI api = WXAPIFactory.createWXAPI(mContext, appId);
            	PayReq req = new PayReq();
            	req.appId = appId;
            	req.partnerId = partner_id;
            	req.prepayId = prepayId;
            	req.nonceStr = nonceStr;
            	req.timeStamp = String.valueOf(timeStamp);
            	req.packageValue = packageValue;
            	req.sign = sign;
            	
            	// 作为传递的订单号
            	req.extData = Config.mGson.toJson(mOrderInfo);
            	// 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
            	api.registerApp(appId);
            	boolean result = api.sendReq(req);
            	if( !result ){
            		LogUtil.i(TAG, "payByWeChat sendReq false.");
            		needCallback = true;
            	}
            	
            	// 打点
            	if(mPosition == 1){
//            		RemindUtils.addMyServiceRemind(RemindConfig.MyOrderChargeHistory, true);
            	}
            }
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage());
            mErrorCode = ChargeConst.ServRtnStatus_Internal_Error;
            parseServRtnMsg(e.getMessage());
            needCallback = true;
        }
        return needCallback;
    }

    /**
	 * 通过支付宝支付
	 * @param subject 标题
	 * @param orderNo 订单号 
	 */
	private boolean payByAlipay(String subject, String orderNo, String favo_id) {
	    String reqOrderUrl = WEGUtil.createReqOrderUrl(subject, mOrderInfo.product_id, mOrderInfo.accountNum, "", mOrderInfo.mark_price, orderNo);
	    
        LogUtil.i(TAG, "reqOrderUrl= " + reqOrderUrl);
        
        String alipayReqOrderUrl = "";
        try {
            /*
             * 将支付宝订单拼接操作（getAlipayOrderInfo） 从 getChargeOrderInfo方法中抽离出来
             * modified by hyl 2014-10-13 start
             * old code:
             * alipayReqOrderUrl = ChargeUtils.getChargeOrderInfo(reqOrderUrl, mOrderInfo);
             */
            WEGOrderInfo orderInfo = WEGUtil.getChargeOrderInfo(reqOrderUrl, mOrderInfo);
            alipayReqOrderUrl = WEGUtil.getAlipayOrderInfo(orderInfo);
            
            LogUtil.d(TAG, "alipayReqOrderUrl=" + alipayReqOrderUrl);

            AliPay alipay = new AliPay(mContext, mHandler);
            //设置为沙箱模式，不设置默认为线上环境
            //alipay.setSandBox(true);

            mAlipayResult = alipay.pay(alipayReqOrderUrl);
            // 打点
            if(mPosition == 1){
//            	RemindUtils.addMyServiceRemind(RemindConfig.MyOrderChargeHistory, true);
            }
            mErrorCode = parseAlipayResult();
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage());
            mErrorCode = ChargeConst.ServRtnStatus_Internal_Error;
            parseServRtnMsg(e.getMessage());
            return false;
        }
        return true;
    }

    @Override
	protected void onPostExecute(Boolean needCallback) {
		LogUtil.i(TAG, "pay result: " + mAlipayResult + " ,needCallback: " + needCallback);
		Message msg = null;
		int what = 0;
		mOrderInfo.resultStatus = mErrorCode;
		switch(mErrorCode) {
			case ChargeConst.ServRtnStatus_Internal_Error:
			case ChargeConst.ServRtnStatus_Sign_Error:
			case ChargeConst.ServRtnStatus_Parameter_Error:
			case ChargeConst.ServRtnStatus_Product_Miss:
			case ChargeConst.ServRtnStatus_Unknow_Error:
				Utils.showToast(mContext, R.string.putao_server_busy, false);
				what = ChargeUtils.MSG_SHOW_CHARGE_EXCEPTION_ACTION;
				mHandler.removeMessages(what);
				msg = mHandler.obtainMessage();
				msg.what = what;
				msg.obj = mOrderInfo;
				
				mHandler.sendMessage(msg);
				break;
			case ChargeConst.ServRtnStatus_Service_Stop:  
				// 服务器维护
				Utils.showToast(mContext, String.format(mContext.getResources().getString(R.string.putao_charge_server_checking), mErrMsg), true);
				what = ChargeUtils.MSG_SHOW_CHARGE_EXCEPTION_ACTION;
				mHandler.removeMessages(what);
				msg = mHandler.obtainMessage();
				msg.what = what;
				msg.obj = mOrderInfo;
				
				mHandler.sendMessage(msg);
				break;
			case ChargeConst.AlipayRtnOrderStatus_NetError:				
			case ChargeConst.AlipayRtnOrderStatus_Canceled:
			case ChargeConst.AlipayRtnOrderStatus_Failed:
			case ChargeConst.AlipayRtnOrderStatus_Processing:
			case ChargeConst.AlipayRtnOrderStatus_Success:
			case ChargeConst.ServRtnStatus_Coupon_Error:
				if( mErrorCode == ChargeConst.ServRtnStatus_Coupon_Error ){
					// 优惠券无效
					Utils.showToast(mContext, R.string.putao_user_tel_charge_coupon_error, false);
				}
				what = ChargeUtils.MSG_SHOW_CHARGE_RESULT_ACTION;
				mHandler.removeMessages(what);
				msg = mHandler.obtainMessage();
				msg.what = what;
				msg.obj = mOrderInfo;
				// 1: 需要回调到结果页面；2：不需要回调到结果页面
				msg.arg1 = needCallback ? 1 : 2;
				
				mHandler.sendMessage(msg);
				break;
			default:
				what = ChargeUtils.MSG_SHOW_CHARGE_EXCEPTION_ACTION;				
				mHandler.removeMessages(what);
				msg = mHandler.obtainMessage();
				msg.what = what;
				mHandler.sendMessage(msg);
				break;
		}
		super.onPostExecute(needCallback);
	}
	
	/**
	 * 解析alipay返回结果,固定返回样式: resultStatus={6001};memo={操作已经取消。};result={}
	 * @param result
	 * @return 充值状态
	 */
	private int parseAlipayResult() {
		if(TextUtils.isEmpty(mAlipayResult)) {
			return ChargeConst.ServRtnStatus_Unknow_Error;  // 未知异常
		}

		int errcode = 0;
		WEGOrderInfo orderRsp = mOrderInfo;
		try {
			String[] result_array = mAlipayResult.split("\\;");
			if(result_array != null && result_array.length == 3) {
				// 返回码
				String resultStatus = result_array[0].substring(result_array[0].indexOf('{')+1, result_array[0].length()-1);
				errcode = Integer.parseInt(resultStatus);
				orderRsp.resultStatus = errcode;
				
				// 信息提示
				String memo = result_array[1].substring(result_array[1].indexOf('{')+1, result_array[1].length()-1);
				
				// 结果参数列表
				String result = result_array[2].substring(result_array[2].indexOf('{')+1, result_array[2].length()-1);
				if(result != null && result.length() > 0) {
					// 解析订单信息
					String orderInfo[] = result.split("\\&");
					if(orderInfo != null && orderInfo.length > 0) {
						for(int i=0; i<orderInfo.length; i++) {
							String[] tmp = orderInfo[i].split("\\=");
							String key = tmp[0];
							String val   = tmp[1].substring(1, tmp[1].length()-1);
							
							if(key.equals("partner")) {
								orderRsp.partner = val;
							} else if(key.equals("seller_id")) {
								orderRsp.seller_id = val;
							} else if(key.equals("out_trade_no")) {
								orderRsp.out_trade_no = val;
							} else if(key.equals("subject")) {
								orderRsp.subject = val;
							} else if(key.equals("body")) {
								orderRsp.body = val;
							} else if(key.equals("total_fee")) {
								orderRsp.total_fee = val;
							} else if(key.equals("notify_url")) {
								orderRsp.notify_url = val;
							} else if(key.equals("service")) {
								orderRsp.service = val;
							} else if(key.equals("payment_type")) {
								orderRsp.payment_type = val;
							} else if(key.equals("success")) {
								orderRsp.success = Boolean.parseBoolean(val);
							} else if(key.equals("it_b_pay")) {
								orderRsp.it_b_pay = val;
							}
						}
					}
				}

				LogUtil.i(TAG, "resultStatus="+resultStatus+" memo="+memo+" \nresult={"+orderRsp.toString()+"}");
			}
		} catch (Exception e) {
			LogUtil.e(TAG, e.getMessage());
			e.printStackTrace();
			errcode = ChargeConst.ServRtnStatus_Parameter_Error;
		}
		return errcode;
	}
	
	/**
	 * 解析出服务其返回错误
	 * @param errmsg
	 */
	private void parseServRtnMsg(String errmsg) {
		if(!TextUtils.isEmpty(errmsg)) {
			if(errmsg.startsWith("-1")) {
				mErrorCode = ChargeConst.ServRtnStatus_Internal_Error;
				
			} else if(errmsg.startsWith("-2")) {
				mErrorCode = ChargeConst.ServRtnStatus_Sign_Error;
				
			} else if(errmsg.startsWith("-3")) {
				mErrorCode = ChargeConst.ServRtnStatus_Coupon_Error;
				
			}else if(errmsg.startsWith("-7")) {
				mErrorCode = ChargeConst.ServRtnStatus_Service_Stop;
				String[] str = errmsg.split("#");
				if(str != null && str.length==2){
					mErrMsg = str[1];
				}
			}
		}
	}

    @Override
    public void onSuccess(String o) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onFail(String msg) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onFinish(Object obj) {
        // TODO Auto-generated method stub
        
    }
	
}
