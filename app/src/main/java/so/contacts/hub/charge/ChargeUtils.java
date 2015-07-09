package so.contacts.hub.charge;

import java.io.UnsupportedEncodingException;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import org.apache.http.HttpEntity;

import so.contacts.hub.http.bean.ChargeTelephoneProductResponseBean;
import so.contacts.hub.http.bean.AskPhoneFeeProductFlowResponse;
import so.contacts.hub.http.bean.AskPhoneFeeProductFlowRequest;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URLEncoder;
import java.util.List;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.account.PutaoAccount;
import so.contacts.hub.core.Config;
import so.contacts.hub.http.bean.AskTrafficProductFlowRequest;
import so.contacts.hub.http.bean.AskTrafficProductFlowResponse;
import so.contacts.hub.http.bean.GetOrderResultRequest;
import so.contacts.hub.http.bean.GetOrderResultResponse;
import so.contacts.hub.http.bean.TelOrderInfo;
import so.contacts.hub.http.bean.ProductDescBean;
import so.contacts.hub.http.bean.TrafficOrderResponseBean;
import so.contacts.hub.http.bean.TrafficProductInfo;
import so.contacts.hub.http.bean.TrafficProductResponseBean;
import so.contacts.hub.net.PTHTTP;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.MD5;
import so.contacts.hub.util.UMengEventIds;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.mdroid.core.http.IgnitedHttpResponse;
import com.mdroid.core.util.SystemUtil;

import so.contacts.hub.util.MobclickAgentUtil;

public class ChargeUtils {
	private static final String TAG = "ChargeUtils";
	
	/** 后台接口 start [ android1.putao.so - 192.168.1.73:8080  - 42.120.51.89:8080 ]*/
	// 充值询价host地址
	public static final String Serv_Get_Mobile_Price_Url = Config.CHARGE.Serv_Get_Mobile_Price_Url;
	
	// 充值生成订单地址
	public static final String SERV_GET_ORDER_URL_ALIPAY = Config.CHARGE.SERV_GET_ORDER_URL_ALIPAY;//阿里支付宝
	public static final String SERV_GET_ORDER_URL_WECHAT = Config.CHARGE.SERV_GET_ORDER_URL_WECHAT;//微信支付
	
	public static final String Serv_Get_Traffic_Info_Url = Config.CHARGE.Serv_Get_Traffic_Info_Url;
	public static final String Serv_Get_Traffic_Price_Url = Config.CHARGE.Serv_Get_Traffic_Price_Url;
	public static final String Serv_Get_Ttaffic_Order_Url = Config.CHARGE.Serv_Get_Ttaffic_Order_Url;
	
	//add by ffh for test
	public static final String Serv_get_traffic_product_url = Config.CHARGE.Serv_get_traffic_product_url;

	//add by xcx 
    public static final String Serv_get_charge_telephone_product_url =Config.CHARGE.Serv_get_charge_telephone_product_url;
//	public static final String Serv_get_charge_telephone_product_url ="http://192.168.1.59:8080/biz.war/phonefee/query_phone_fee_product";
    
    // add by cj
    public static final String QUERY_ORDER_URL = Config.CHARGE.QUERY_ORDER_URL;

	// 查询充值状态
	public static final String Serv_Common_Url = Config.SERVER;	
	// public static final String Serv_Common_Url = Config.TEST_SERVER;
	
	// Handler Message Action 获取话费订单- 网络请求异常(此处是阿里页面返回的结果，因多个Activity可能会使用，所以放在该文件中)
    public static final int MSG_SHOW_CHARGE_EXCEPTION_ACTION = 0x3101;

    // Handler Message Action 获取话费订单- 阿里返回异常(此处是阿里页面返回的结果，因多个Activity可能会使用，所以放在该文件中)
    public static final int MSG_SHOW_CHARGE_ALI_EXCEPTION_ACTION = 0x3102;
    
    // Handler Message Action 获取话费订单- 获取充值结果(此处是支付宝返回的结果，因多个Activity可能会使用，所以放在该文件中)
    public static final int MSG_SHOW_CHARGE_RESULT_ACTION = 0x3103;

    // 订单超时时间，默认2小时
    public static final String ORDER_TIME_OUT = "120m";
    
	/**
	 *  查询充值状态
	 * @param mobile 手机号
	 * @return
	 * @throws Exception
	 */
	public static GetOrderResultResponse qryChargeStatus(String order_no)  throws IOException, ConnectException, Exception {
        LogUtil.i(TAG, "GetOrderResultResponse order_no="+order_no);
        
        GetOrderResultResponse responseData = null;
        
		final GetOrderResultRequest requestData = new GetOrderResultRequest();
		requestData.setOrderId(order_no);
        IgnitedHttpResponse httpResponse;
        
		httpResponse = Config.getApiHttp().post(Serv_Common_Url, requestData.getData()).send();
		String content = httpResponse.getResponseBodyAsString();
		responseData = requestData.getObject(content);
		if (responseData != null) {
			if (responseData.isSuccess()) {
				LogUtil.i(TAG, "qryChargeStatus ok");
			} else {
				LogUtil.i(TAG, "qryChargeStatus fail, errcode="+responseData.ret_code);
				responseData = null;
			}
		}
		
        return responseData;
    }
	
	/**
	 * 阻塞式的查询充值状态
	 * @param order_no
	 * @param retry
	 * @param retry_wait_time
	 * @param timedout
	 * @return 1-充值成功, 2-充值处理中, 3-充值失败 , 4-网络异常，5-超时, 6-服务器繁忙
	 */
	public static int qryChargeStatus(String order_no, boolean retry, int retry_wait_time, int timedout)  {
        LogUtil.i(TAG, "qryChargeStatus order_no="+order_no+" retry="+retry+" retry_wait_time="+retry_wait_time+" timedout="+timedout);
        
        int chargeStatus = ChargeConst.ChargeRtnStatus_Timeout;
        int takesTime = 0;
        int retryCnt = 0;
		GetOrderResultResponse responseData = null;
		while(takesTime<timedout) {
			
			long calcStart = System.currentTimeMillis();
			try {
				responseData = qryChargeStatus(order_no);
			} catch (ConnectException e) {
				LogUtil.e(TAG, "qryChargeStatus " + e.getMessage());
				chargeStatus = ChargeConst.ChargeRtnStatus_Serv_busy;
				MobclickAgentUtil.onEvent(ContactsApp.getInstance(), UMengEventIds.DISCOVER_YELLOWPAGE_CHARGE_PAY_SUCCESS_SERV_TIMEOUT);
				
				break;
			} catch (IOException e) {
				LogUtil.e(TAG, "qryChargeStatus " + e.getMessage());
				chargeStatus = ChargeConst.ChargeRtnStatus_Neterror;
				
				break;
			} catch (Exception e) {
				LogUtil.e(TAG, "qryChargeStatus " + e.getMessage());
				chargeStatus = ChargeConst.ChargeRtnStatus_Neterror;
				
				break;
			}

			if(responseData != null) {  // for log print
				 // 有结果返回
				 LogUtil.i(TAG, "qryChargeStatus orderNo="+order_no+" payStatus="+responseData.getPayStatus()+" tradeStatus="+responseData.getTradeStatus());
			}
			
			/**
			 *   先判断支付状态再判断充值状态
			 */
			if(responseData == null) {
				// 后台查询状态报错，可能是非法订单
				chargeStatus = ChargeConst.ChargeRtnStatus_Failed;
		        MobclickAgentUtil.onEvent(ContactsApp.getInstance(), UMengEventIds.DISCOVER_YELLOWPAGE_CHARGE_CANCEL);
				break;
				
			} else if(!"TRADE_FINISHED".equalsIgnoreCase(responseData.getPayStatus()) && !"TRADE_SUCCESS".equalsIgnoreCase(responseData.getPayStatus())) {
 				 // TRADE_FINISHED 和 TRADE_SUCCESS属于成功，其他属于失败　
				 // 支付失败
				chargeStatus = ChargeConst.ChargeRtnStatus_Pay_failed;
				break;
				
			} else if( TextUtils.isEmpty(responseData.getTradeStatus())) {
				 // 需要重试
				retryCnt++;
				try {
					Thread.sleep(retry_wait_time);
				} catch (Exception e) {
				}

			 } else {
				 // 判断充值状态
				if ("FAILED".equalsIgnoreCase(responseData.getTradeStatus())) {
					chargeStatus = ChargeConst.ChargeRtnStatus_Failed;
					MobclickAgentUtil.onEvent(ContactsApp.getInstance(), UMengEventIds.DISCOVER_YELLOWPAGE_CHARGE_FAIL);
					
				} else if ("TIMEOUT".equalsIgnoreCase(responseData.getTradeStatus())) {
					chargeStatus = ChargeConst.ChargeRtnStatus_Failed;
					MobclickAgentUtil.onEvent(ContactsApp.getInstance(), UMengEventIds.DISCOVER_YELLOWPAGE_CHARGE_PAY_SUCCESS_LLIAN_TIMEOUT);
					
				} else if("LOCAL_TIMEOUT".equalsIgnoreCase(responseData.getTradeStatus())) {
					chargeStatus = ChargeConst.ChargeRtnStatus_Failed;
					MobclickAgentUtil.onEvent(ContactsApp.getInstance(), UMengEventIds.DISCOVER_YELLOWPAGE_CHARGE_PAY_SUCCESS_SERV_TIMEOUT);
					
				} else if("LIANLIANSERVICE_EXCEPION".equalsIgnoreCase(responseData.getTradeStatus())) {
					chargeStatus = ChargeConst.ChargeRtnStatus_Failed;
					MobclickAgentUtil.onEvent(ContactsApp.getInstance(), UMengEventIds.DISCOVER_YELLOWPAGE_CHARGE_PAY_SUCCESS_LLIAN_TIMEOUT);

				} else if ("PROCESS".equalsIgnoreCase(responseData.getTradeStatus())) {
					chargeStatus = ChargeConst.ChargeRtnStatus_Pending;
					MobclickAgentUtil.onEvent(ContactsApp.getInstance(), UMengEventIds.DISCOVER_YELLOWPAGE_CHARGE_SUCCESS);
					
				} else if ("CANCEL_REQUIRED".equalsIgnoreCase(responseData.getTradeStatus())) {
					chargeStatus = ChargeConst.ChargeRtnStatus_Pending;
				} else if ("CANCEL_CONFIRMED".equalsIgnoreCase(responseData.getTradeStatus())) {
					chargeStatus = ChargeConst.ChargeRtnStatus_Failed;
				} else if ("SUCCESS".equalsIgnoreCase(responseData.getTradeStatus())) {
					chargeStatus = ChargeConst.ChargeRtnStatus_Ok;
					MobclickAgentUtil.onEvent(ContactsApp.getInstance(), UMengEventIds.DISCOVER_YELLOWPAGE_CHARGE_SUCCESS);

				} else {
					chargeStatus = ChargeConst.ChargeRtnStatus_Failed;
					MobclickAgentUtil.onEvent(ContactsApp.getInstance(), UMengEventIds.DISCOVER_YELLOWPAGE_CHARGE_FAIL);				 

				}
				 break;
			 }

			 takesTime += System.currentTimeMillis()-calcStart;
			if (takesTime >= timedout) {
				MobclickAgentUtil.onEvent(ContactsApp.getInstance(), UMengEventIds.DISCOVER_YELLOWPAGE_CHARGE_PAY_SUCCESS_SERV_TIMEOUT);
				break;
			}
		}

		LogUtil.i(TAG, "qryChargeStatus orderNo="+order_no+" chargeStatus="+chargeStatus+" retry="+retryCnt+" takesTime="+takesTime);
		return chargeStatus;
	}	
	
	/**
	 *  查询电话充值的详情列表,包括真实价值
	 * @param mobile 手机号
	 * @return
	 * @throws Exception
	 */
	public static List<ProductDescBean> qryMobilePrice(String mobile)  throws Exception {
		StringBuffer host_url = new StringBuffer(Serv_Get_Mobile_Price_Url);
		host_url.append("?mobile=").append(mobile);
		
		IgnitedHttpResponse resp = Config.getApiHttp().get(host_url.toString(), false).send();
		String body = resp.getResponseBodyAsString();
		
		LogUtil.i(TAG, "qryMobilePrice body="+body);
		List<ProductDescBean> list = Config.mGson.fromJson(body, new com.google.gson.reflect.TypeToken<List<ProductDescBean>>() {}.getType());
		return list;
	}
	
	   /**
     *  查询流量充值的详情列表
     * @param mobile 手机号
     * @return
     * @throws Exception
     */
    public static TrafficProductResponseBean qryTrafficPrice(String mobile,String operatorInfo)  throws Exception {
        AskTrafficProductFlowResponse response = null;
        AskTrafficProductFlowRequest request = new AskTrafficProductFlowRequest();
        request.setPhone(mobile);
        request.setAcc_type(operatorInfo);
        String reqData = Config.mGson.toJson(request);
        // modify by xcx 2015-01-16 start 新请求方式多次请求时会有异常，暂时使用老的请求方式
//        String content = PTHTTP.getOldHttp().post(Serv_get_traffic_product_url, reqData);
        StringEntity entity = null;
        try {
            entity = new StringEntity(reqData, HTTP.UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(null==entity){
            return null;
        }
        IgnitedHttpResponse resp = Config.getApiHttp().post(Serv_get_traffic_product_url, entity).send();
        String body = resp.getResponseBodyAsString();
        response = request.getObject(body);
       // modify by xcx 2015-01-16 end 新请求方式多次请求时会有异常，暂时使用老的请求方式
        return response.getData();
    }
    
    
    /**
     *  查询流量充值的详情列表
     * @param mobile 手机号
     * @return
     * @throws Exception
     */
    public static ChargeTelephoneProductResponseBean qryChargeTelephonePrice(String mobile,String operatorInfo)  throws Exception {
        AskPhoneFeeProductFlowResponse response = null;
        AskPhoneFeeProductFlowRequest request = new AskPhoneFeeProductFlowRequest();
        request.setPhone(mobile);
        if(!TextUtils.isEmpty(operatorInfo)){
            operatorInfo=operatorInfo.replace(" ", "");
        }else{
            operatorInfo="";
        }
        
        request.setAcc_type(operatorInfo);
        String reqData = Config.mGson.toJson(request);
        // modify by xcx 2015-01-16 start 新请求方式多次请求时会有异常，暂时使用老的请求方式
//        String content = PTHTTP.getOldHttp().post(Serv_get_charge_telephone_product_url, reqData);
        StringEntity entity = null;
        try {
            entity = new StringEntity(reqData, HTTP.UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(null==entity){
            return null;
        }
        IgnitedHttpResponse resp = Config.getApiHttp().post(Serv_get_charge_telephone_product_url, entity).send();
        String body = resp.getResponseBodyAsString();
        LogUtil.d(TAG, body);
        response = request.getObject(body);
        // modify by xcx 2015-01-16 end 新请求方式多次请求时会有异常，暂时使用老的请求方式
        return response.getData();
    }
    
  /**
  *  查询流量充值的详情列表
  * @param mobile 手机号
  * @return
  * @throws Exception
  */
 public static TrafficOrderResponseBean getPhoneTrafficOrder(String mobile,String product_code)  throws Exception {
     String requestUrl = createPhoneTrafficOrderRequestUrl(mobile, product_code);
//     真是场景 
//     StringBuffer host_url = new StringBuffer(Serv_Get_Traffic_Price_Url);
//     host_url.append("?mobile=").append(mobile);
//     
//     IgnitedHttpResponse resp = Config.getApiHttp().get(host_url.toString(), false).send();
//     String body = resp.getResponseBodyAsString();
     
     //模拟场景
     //
     String body = "{\"order_id\":\"001\"}";
     TrafficOrderResponseBean bean = Config.mGson.fromJson(body, new TrafficOrderResponseBean().getClass());
     return bean;
 }
	
	/**
	 * 同步请求后台发起一个新订单请求,返回组装好的能请求阿里支付包的URL字符串
	 * @param host_url 后台请求URL
	 * @return
	 */
	/*
	 * getChargeOrderInfo返回类型由 String 改为 OrderInfo
	 * modified by hyl 2014-10-13 start
	 * old code:
	 * public static String getChargeOrderInfo(String host_url, OrderInfo order) throws Exception {
	 */
	public static TelOrderInfo getChargeOrderInfo(String host_url, TelOrderInfo order) throws Exception {
		TelOrderInfo bean = null;
		String body = "";
		
		try {
			IgnitedHttpResponse resp = Config.getApiHttp().post(host_url).send();
			body = resp.getResponseBodyAsString();
			bean = Config.mGson.fromJson(body, TelOrderInfo.class);
			
			if(bean != null) {
				LogUtil.i(TAG, "serv rtn charge orderInfo ="+bean.toString());
				// 记录后台生成的流水号
				order.out_trade_no = bean.out_trade_no; 
				
				/*
				 * 不在这里处理支付宝订单拼接操作
				 * modified by hyl 2014-10-13
				 * old code:
				 *  String orderInfoUrl = getAlipayOrderInfo(bean);
                    return orderInfoUrl;
				 */
			} 
			
		} catch (Exception e) {
		    if(e != null){
		        LogUtil.e(TAG, "getChargeOrderInfo out_trade_no="+order.out_trade_no+" body="+body+" err="+e.getMessage());
		        e.printStackTrace();
		    }
			Exception ex = new Exception(body);
			throw ex;
		}
		
		return bean;//modified by hyl 2014-10-13 old code:return "";
	}
//modified by hyl 2014-10-13 end	
	
    public static String getQianNiuOrderInfo(String host_url) throws Exception {
        String body = "";
        
        try {
            IgnitedHttpResponse resp = Config.getApiHttp().post(host_url).send();
            body = resp.getResponseBodyAsString();
            return body;
            
        } catch (Exception e) {
            e.printStackTrace();
            Exception ex = new Exception(body);
            throw ex;
        }
    }	
    
    
    public static String createPhoneTrafficOrderRequestUrl(String mobile, String product_id) {
        StringBuffer sb = new StringBuffer(Serv_Get_Ttaffic_Order_Url);
        sb.append("?mobile=");
        sb.append(mobile);
        sb.append("&product_id=");
        sb.append(product_id);
        return sb.toString();
    }
    
    
    
	
	/**
	 * 组合拉取订单请求的URL
	 * @param mobile      手机号
	 * @param total_fee   扣费价格,是50不是48.9
	 * @param orderNo     订单编号（如果编号存在，则服务器端删除此编号的信息，可以为空）
	 * @return
	 */
	public static String createReqOrderUrl(String subject, String product_id, 
	        String mobile, String total_fee, String orderNo, String favo_id) {
		String body = "Telephone charge by PuTao";
		int payment_type = 1;
		long timestemp = System.currentTimeMillis();
		String dev_no = SystemUtil.getDeviceId(ContactsApp.getInstance());
		String security = "kksd%sj*77";
		String local_sign = "";

		StringBuffer sb = new StringBuffer(SERV_GET_ORDER_URL_ALIPAY);
		sb.append("?subject=");
		sb.append(URLEncoder.encode(subject));
		sb.append("&product_id=");
		sb.append(product_id);
		sb.append("&payment_type=");
		sb.append(payment_type);
		sb.append("&body=");
		sb.append(URLEncoder.encode(body));
		sb.append("&mobile=");
		sb.append(mobile);
		sb.append("&pay=");
		sb.append(total_fee);
		sb.append("&dev_no=");
		sb.append(dev_no);
        sb.append("&pt_token=");
        sb.append(PutaoAccount.getInstance().getPtUser().getPt_token());		
		sb.append("&orderNo=");
		sb.append(orderNo);
		sb.append("&channel_no=");
		sb.append(SystemUtil.getChannelNo(ContactsApp.getInstance().getApplicationContext()));
		sb.append("&timestemp=");
		sb.append(timestemp);
		sb.append("&time_out=");
		sb.append(ORDER_TIME_OUT);
		/*
         * 添加代金券id
         * add by hyl 2014-10-19 start
         */
        if( !TextUtils.isEmpty(favo_id) ){
            sb.append("&favo_id=" + favo_id);
        }
        //add by hyl 2014-10-19 end
        
		sb.append("&local_sign=");
		local_sign = MD5.toMD5(timestemp + dev_no + total_fee + security);
		sb.append(local_sign);
		
		return sb.toString();
	}
	
	/**
     * 组合拉取订单请求的URL
     * @param mobile      手机号
     * @param total_fee   扣费价格,是50不是48.9
     * @param orderNo     订单编号（如果编号存在，则服务器端删除此编号的信息，可以为空）
     * @return
     */
    public static String createReqOrderUrlByWeChat(String subject, String product_id, String mobile, String total_fee, 
    		String orderNo, String favo_id) {
//        String body = "Telephone charge by PuTao";
        long timestemp = System.currentTimeMillis();
        String dev_no = SystemUtil.getDeviceId(ContactsApp.getInstance());
        String security = "kksd%sj*77";
        String local_sign = "";

        StringBuffer sb = new StringBuffer(SERV_GET_ORDER_URL_WECHAT);
//        sb.append("?subject=");
//        sb.append(URLEncoder.encode(subject));
        sb.append("?product_id=");
        sb.append(product_id);
//        sb.append("&payment_type=");
//        sb.append("&body=");
//        sb.append(URLEncoder.encode(body));
        sb.append("&mobile=");
        sb.append(mobile);
        sb.append("&pay=");
        sb.append(total_fee);
        sb.append("&dev_no=");
        sb.append(dev_no);
        sb.append("&pt_token=");
        sb.append(PutaoAccount.getInstance().getPtUser().getPt_token());     
        sb.append("&orderNo=");
        sb.append(orderNo);
        sb.append("&channel_no=");
        sb.append(SystemUtil.getChannelNo(ContactsApp.getInstance().getApplicationContext()));
        sb.append("&timestemp=");
        sb.append(timestemp);
        sb.append("&time_out=");
        sb.append(ORDER_TIME_OUT);
        if( !TextUtils.isEmpty(favo_id) ){
            sb.append("&favo_id=" + favo_id);
        }
        sb.append("&local_sign=");
        local_sign = MD5.toMD5(timestemp+dev_no+total_fee+security);
        sb.append(local_sign);
        return sb.toString();
    }
	
	/**
	 * 组装从支付宝请求支付的URL
	 * @param bean 从后台获取的订单详情
	 * @return
	 */
	
	public static String getAlipayOrderInfo(TelOrderInfo bean) {
		StringBuilder sb = new StringBuilder();
		sb.append("partner=\"");
		sb.append(bean.partner);
		sb.append("\"&out_trade_no=\"");
		sb.append(bean.out_trade_no);
		sb.append("\"&subject=\"");
		sb.append(bean.subject);
		sb.append("\"&body=\"");
		sb.append(bean.body);
		sb.append("\"&total_fee=\"");
		sb.append(bean.total_fee);
		sb.append("\"&notify_url=\"");

		// 网址需要做URL编码
		sb.append(URLEncoder.encode(bean.notify_url));
		sb.append("\"&service=\"");
		sb.append(bean.service);
		sb.append("\"&_input_charset=\"utf-8");
		sb.append("\"&return_url=\"");
		sb.append(URLEncoder.encode("http://m.alipay.com"));		
		sb.append("\"&payment_type=\"");
		sb.append(bean.payment_type);
		sb.append("\"&seller_id=\"");
		sb.append(bean.seller_id);
		sb.append("\"&it_b_pay=\"");
		sb.append(ORDER_TIME_OUT);
		sb.append("\"&sign=\"");
		sb.append(URLEncoder.encode(bean.sign));
		sb.append("\"&sign_type=\"RSA\"");
		
		return sb.toString();
	}

}
