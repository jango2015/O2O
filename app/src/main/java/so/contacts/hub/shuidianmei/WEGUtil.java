package so.contacts.hub.shuidianmei;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.alipay.android.app.net.ResponseData;
import com.mdroid.core.http.IgnitedHttpResponse;
import com.mdroid.core.util.SystemUtil;
import com.yulong.android.contacts.discover.R;

import android.content.Context;
import android.text.TextUtils;
import so.contacts.hub.ContactsApp;
import so.contacts.hub.account.PutaoAccount;
import so.contacts.hub.charge.ChargeConst;
import so.contacts.hub.core.Config;
import so.contacts.hub.db.YellowPageDB;
import so.contacts.hub.http.bean.GetOrderResultRequest;
import so.contacts.hub.http.bean.GetOrderResultResponse;
import so.contacts.hub.http.bean.TelOrderInfo;
import so.contacts.hub.http.bean.ProductDescBean;
import so.contacts.hub.shuidianmei.bean.WEGOrderInfo;
import so.contacts.hub.shuidianmei.bean.WEGUserBean;
import so.contacts.hub.shuidianmei.bean.WaterElectricityGasBean;
import so.contacts.hub.ui.yellowpage.YellowPageChargeResultActivity;
import so.contacts.hub.ui.yellowpage.bean.HabitDataItem;
import so.contacts.hub.util.ContactsAppUtils;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.MD5;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.UMengEventIds;

public class WEGUtil {
    // "水电煤"- "so.contacts.hub.ui.yellowpage.YellowPageWaterEGActivity"
    public static final String MY_NODE_WEG_RECHARGE= YellowPageWaterEGActivity.class.getName();
    
    // "水电煤"-水电煤充值-用户号
    public static final String HIBAT_CONTENT_TYPE_WEG_ACCOUNT_CODE = "weg_account_code";
    
    /** 【发现-水电煤-点击充值】入口进入次数 */
    public static final String DISCOVER_YELLOWPAGE_WEG_RECHARGE = "discover_yellowpage_WEG_Recharge";
    
    /** 【发现-水电煤-历史】入口进入次数/设备数 */
    public static final String DISCOVER_YELLOWPAGE_WEG_RECHARGE_HISTORY = "discover_yellowpage_weg_recharge_history";
    
    /** 【发现-水电煤-等待付款】点击次数 */
    public static final String DISCOVER_YELLOWPAGE_WEG_WAITING_PAY_BTN = "discover_yellowpage_weg_waiting_pay_btn";
    
    /** 查价没有发现数据 */
    public static final String QUERY_CHARGE_NOT_FIND_DATA = "-5";
    
    
    static int infoNum = 8;
    private static String TAG = WEGUtil.class.getSimpleName();
    
    // 订单超时时间，默认2小时
    public static final String ORDER_TIME_OUT = "120m";
    
    public static final String SERV_GET_WEG_ORDER_URL = Config.WEG.SERV_GET_WEG_ORDER_URL;
    // 充值询价host地址
    public static final String SERV_GET_WEG_PRICE_URL = Config.WEG.SERV_GET_WEG_PRICE_URL;
    //微信订单接口
    public static final String SERV_GET_ORDER_URL_WECHAT = Config.WEG.SERV_GET_ORDER_URL_WECHAT;
    public static final String SERV_GET_ORDER_URL_ALIPAY = Config.WEG.SERV_GET_ORDER_URL_ALIPAY;
    
    public static WEGUserBean qryUserBill(WEGUserBean bean)  throws Exception {
        StringBuffer host_url = new StringBuffer(SERV_GET_WEG_PRICE_URL);
        host_url.append("pro_id=").append(bean.getProid()).append("&account=").append(bean.getAccount()).append("&yearmonth=");
        IgnitedHttpResponse resp = Config.getApiHttp().get(host_url.toString(), false).send();
        String body = resp.getResponseBodyAsString();
        LogUtil.i(TAG, "qryUserBill body="+body);
        if(!body.equals(QUERY_CHARGE_NOT_FIND_DATA)){
            WEGUserBean bill = Config.mGson.fromJson(body,WEGUserBean.class);
            return bill;
        }else{
            return null;
        }
        
    }
    
    /**
     *  查询充值状态
     * @param order_id 订单号
     * @return
     * @throws Exception
     */
    public static GetOrderResultResponse qryChargeStatus(String order_id)  throws IOException, ConnectException, Exception {
        
        String pt_token =  PutaoAccount.getInstance().getPtUser().getPt_token();
        LogUtil.i(TAG, "GetOrderResultResponse order_id="+order_id + " pt_token " + pt_token);
        
        StringBuffer host_url = new StringBuffer(SERV_GET_WEG_ORDER_URL);
        host_url.append("order_id=").append(order_id).append("&pt_token=").append(pt_token);
        
        IgnitedHttpResponse resp = Config.getApiHttp().get(host_url.toString(), false).send();
        String body = resp.getResponseBodyAsString();
        String status = "";
        String worker_status = "";
        GetOrderResultResponse response = null;
        if(!TextUtils.isEmpty(body)){
            JSONObject obj = new JSONObject(body);
            if (!obj.isNull("status")) {
                status = obj.getString("status");
            }
            if (!obj.isNull("worker_status")) {
                worker_status = obj.getString("worker_status");
            }
            LogUtil.i(TAG, "GetOrderResultResponse body=" + body);
            response = new GetOrderResultResponse();
            response.setPayStatus(status);
            response.setTradeStatus(worker_status);
        }else{
            //response = null;
        }
        return response;
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
                MobclickAgentUtil.onEvent(ContactsApp.getInstance(),
                        UMengEventIds.DISCOVER_YELLOWPAGE_CHARGE_PAY_SUCCESS_SERV_TIMEOUT);

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

            if (responseData != null) { // for log print
                // 有结果返回
                LogUtil.i(
                        TAG,
                        "qryChargeStatus orderNo=" + order_no + " payStatus="
                                + responseData.getPayStatus() + " tradeStatus="
                                + responseData.getTradeStatus());
            }

            /**
             * 先判断支付状态再判断充值状态
             */
            if (responseData == null) {
                // 后台查询状态报错，可能是非法订单
                chargeStatus = ChargeConst.ChargeRtnStatus_Failed;
                MobclickAgentUtil.onEvent(ContactsApp.getInstance(),
                        UMengEventIds.DISCOVER_YELLOWPAGE_CHARGE_CANCEL);
                break;

            } else if (!"TRADE_FINISHED".equalsIgnoreCase(responseData.getPayStatus())
                    && !"TRADE_SUCCESS".equalsIgnoreCase(responseData.getPayStatus())) {
                // TRADE_FINISHED 和 TRADE_SUCCESS属于成功，其他属于失败　
                // 支付失败
                chargeStatus = ChargeConst.ChargeRtnStatus_Pay_failed;
                break;

            } else if (TextUtils.isEmpty(responseData.getTradeStatus())) {
                // 需要重试
                // retryCnt++;
                // try {
                // Thread.sleep(retry_wait_time);
                // } catch (Exception e) {
                // }

                // 这里getTradeStatus()因为服务器逻辑是恒为null 所以设为OK
                chargeStatus = ChargeConst.ChargeRtnStatus_Ok;
                break;
            } else {
                // 判断充值状态
                if ("FAILED".equalsIgnoreCase(responseData.getTradeStatus())) {
                    chargeStatus = ChargeConst.ChargeRtnStatus_Failed;
                    MobclickAgentUtil.onEvent(ContactsApp.getInstance(),
                            UMengEventIds.DISCOVER_YELLOWPAGE_CHARGE_FAIL);

                } else if ("TIMEOUT".equalsIgnoreCase(responseData.getTradeStatus())) {
                    chargeStatus = ChargeConst.ChargeRtnStatus_Failed;
                    MobclickAgentUtil.onEvent(ContactsApp.getInstance(),
                            UMengEventIds.DISCOVER_YELLOWPAGE_CHARGE_PAY_SUCCESS_LLIAN_TIMEOUT);

                } else if ("LOCAL_TIMEOUT".equalsIgnoreCase(responseData.getTradeStatus())) {
                    chargeStatus = ChargeConst.ChargeRtnStatus_Failed;
                    MobclickAgentUtil.onEvent(ContactsApp.getInstance(),
                            UMengEventIds.DISCOVER_YELLOWPAGE_CHARGE_PAY_SUCCESS_SERV_TIMEOUT);

                } else if ("LIANLIANSERVICE_EXCEPION".equalsIgnoreCase(responseData
                        .getTradeStatus())) {
                    chargeStatus = ChargeConst.ChargeRtnStatus_Failed;
                    MobclickAgentUtil.onEvent(ContactsApp.getInstance(),
                            UMengEventIds.DISCOVER_YELLOWPAGE_CHARGE_PAY_SUCCESS_LLIAN_TIMEOUT);

                } else if ("PROCESS".equalsIgnoreCase(responseData.getTradeStatus())) {
                    chargeStatus = ChargeConst.ChargeRtnStatus_Pending;
                    MobclickAgentUtil.onEvent(ContactsApp.getInstance(),
                            UMengEventIds.DISCOVER_YELLOWPAGE_CHARGE_SUCCESS);

                } else if ("CANCEL_REQUIRED".equalsIgnoreCase(responseData.getTradeStatus())) {
                    chargeStatus = ChargeConst.ChargeRtnStatus_Pending;
                } else if ("CANCEL_CONFIRMED".equalsIgnoreCase(responseData.getTradeStatus())) {
                    chargeStatus = ChargeConst.ChargeRtnStatus_Failed;
                } else if ("SUCCESS".equalsIgnoreCase(responseData.getTradeStatus())) {
                    chargeStatus = ChargeConst.ChargeRtnStatus_Ok;
                    MobclickAgentUtil.onEvent(ContactsApp.getInstance(),
                            UMengEventIds.DISCOVER_YELLOWPAGE_CHARGE_SUCCESS);

                } else {
                    chargeStatus = ChargeConst.ChargeRtnStatus_Failed;
                    MobclickAgentUtil.onEvent(ContactsApp.getInstance(),
                            UMengEventIds.DISCOVER_YELLOWPAGE_CHARGE_FAIL);

                }
                break;
            }
//            超时以后的逻辑 暂时不需要
//            takesTime += System.currentTimeMillis() - calcStart;
//            if (takesTime >= timedout) {
//                MobclickAgentUtil.onEvent(ContactsApp.getInstance(),
//                        UMengEventIds.DISCOVER_YELLOWPAGE_CHARGE_PAY_SUCCESS_SERV_TIMEOUT);
//                break;
//            }
        }

        LogUtil.i(TAG, "qryChargeStatus orderNo="+order_no+" chargeStatus="+chargeStatus+" retry="+retryCnt+" takesTime="+takesTime);
        return chargeStatus;
    }   
    
    /**
     * 组合拉取订单请求的URL
     * @return
     */
    public static String createReqOrderUrlByWeChat(String pro_id, String account, String yearmonth,String subject,String price) {
        
        long timestemp = System.currentTimeMillis();
        String dev_no = SystemUtil.getDeviceId(ContactsApp.getInstance());
        String security = "kksd%sj*77";
        String local_sign = "";

        StringBuffer sb = new StringBuffer(SERV_GET_ORDER_URL_WECHAT);
        sb.append("?pro_id=");
        sb.append(pro_id);
        sb.append("&account=");
        sb.append(account);
        sb.append("&yearmonth=");
        sb.append(yearmonth);
        sb.append("&dev_no=");
        sb.append(dev_no);
        sb.append("&pt_token=");
        sb.append(PutaoAccount.getInstance().getPtUser().getPt_token());     
        sb.append("&channel_no=");
        sb.append(SystemUtil.getChannelNo(ContactsApp.getInstance().getApplicationContext()));
        sb.append("&timestemp=");
        sb.append(timestemp);
        sb.append("&time_out=");
        sb.append(ORDER_TIME_OUT);
        sb.append("&local_sign=");
        local_sign = MD5.toMD5(timestemp+dev_no+Float.valueOf(price).toString()+security);
        sb.append(local_sign);
        
        return sb.toString();
    }
    
    
    public static WEGOrderInfo getChargeOrderInfo(String host_url, WEGOrderInfo order) throws Exception {
        WEGOrderInfo bean = null;
        String body = "";
        
        try {
            IgnitedHttpResponse resp = Config.getApiHttp().post(host_url).send();
            body = resp.getResponseBodyAsString();
            bean = Config.mGson.fromJson(body, WEGOrderInfo.class);
            
            if(bean != null) {
                LogUtil.i(TAG, "serv rtn charge orderInfo ="+bean.toString());
                // 记录后台生成的流水号
                order.out_trade_no = bean.out_trade_no; 
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

    
    /**
     * 组合拉取订单请求的URL
     * @param mobile      手机号
     * @param total_fee   扣费价格,是50不是48.9
     * @param orderNo     订单编号（如果编号存在，则服务器端删除此编号的信息，可以为空）
     * @return
     */
    public static String createReqOrderUrl(String subject, String pro_id, 
            String account,String yearmonth,String price, String orderNo) {
        
        
        long timestemp = System.currentTimeMillis();
        String dev_no = SystemUtil.getDeviceId(ContactsApp.getInstance());
        String security = "kksd%sj*77";
        String local_sign = "";

        StringBuffer sb = new StringBuffer(SERV_GET_ORDER_URL_ALIPAY);
        sb.append("?subject=");
        sb.append(URLEncoder.encode(subject));
        sb.append("&pro_id=");
        sb.append(pro_id);
        sb.append("&account=");
        sb.append(account);
        sb.append("&yearmonth=");
        sb.append(yearmonth);
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
        sb.append("&local_sign=");
        local_sign = MD5.toMD5(timestemp + dev_no + Float.valueOf(price).toString() + security);
        sb.append(local_sign);
        
        return sb.toString();
    }



    /**
     * 组装从支付宝请求支付的URL
     * @param bean 从后台获取的订单详情
     * @return
     */
    
    public static String getAlipayOrderInfo(WEGOrderInfo bean) {
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
        if(TextUtils.isEmpty(bean.total_fee)){
            sb.append(bean.mark_price);
        }else{
            sb.append(bean.total_fee);
        }
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
    
    public static String getRechargeStringByType(Context context,int type){
        String showStr = "";
        if(type == 1 || type == 2 || type == 3){
            switch (type) {
                case 1:
                    showStr = context.getString(R.string.putao_water_eg_tag_title_water);
                    break;
                case 2:
                    showStr = context.getString(R.string.putao_water_eg_tag_title_electricity);
                    break;
                case 3:
                    showStr = context.getString(R.string.putao_water_eg_tag_title_gas);
                    break;
                default:
                    break;
            }
        }
       return showStr;
    }
    
    //add by ljq start 2014-11-28 
    /**加载水电煤的数据*/
    public static void loadWaterElectricityGasDB() {
        WaterElectricityGasDB db = ContactsAppUtils.getInstance().getDatabaseHelper()
                .getWaterElectricityGasDB();
        InputStream in = null;
        BufferedReader br = null;
        String encoding = "utf-8";
        List<WaterElectricityGasBean> baenList = new ArrayList<WaterElectricityGasBean>();
        try {
            in = ContactsApp.getInstance().getAssets()
                    .open("putao_weg_data.txt");
            br = new BufferedReader(new InputStreamReader(in, encoding));
            String line = null;
            boolean isFirstRead = true;
            while ((line = br.readLine()) != null) {
                if (TextUtils.isEmpty(line)) {
                    continue;
                }
                if (isFirstRead) {
                    isFirstRead = false;
                    continue;
                }
                String elements[] = line.split("\t");
                if(elements == null || elements.length == 0){
                    continue;
                }
                WaterElectricityGasBean bean = new WaterElectricityGasBean();
                bean.setProduct_id(elements[0]);
                bean.setProvince(elements[1]);
                bean.setCity(elements[2]);
                bean.setCompany(elements[3]);
                bean.setWeg_type(Integer.valueOf(elements[4]));
                baenList.add(bean);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
                if (in != null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        db.insertWaterElectricityGasList(baenList);
    }
    //add by ljq end 2014-11-28 
    
}