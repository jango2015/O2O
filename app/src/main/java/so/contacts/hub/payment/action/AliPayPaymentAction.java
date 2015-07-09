
package so.contacts.hub.payment.action;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import so.contacts.hub.core.Config;
import so.contacts.hub.payment.PaymentCallback;
import so.contacts.hub.payment.core.PaymentDesc;
import so.contacts.hub.payment.data.ResultCode;
import so.contacts.hub.util.LogUtil;
import android.app.Activity;
import android.text.TextUtils;

import com.alipay.android.app.sdk.AliPay;

/**
 * 支付宝
 * @author Steve Xu 徐远同
 *
 */
public class AliPayPaymentAction extends AbstractPaymentAction {

    private static final String TAG = AliPayPaymentAction.class.getSimpleName();

    // 订单超时时间，默认0.5小时
    public static final String ORDER_TIME_OUT = "30m";

    public AliPayPaymentAction(Activity ctx) {
        super(ctx);
    }

    @Override
    protected void doPayment(Map<String, String> map, PaymentCallback cb) throws Exception {
        AliPay alipay = new AliPay(actRef.get(), uiDispatcher);
        String param = createAlipayUrl(map);
        LogUtil.d(TAG, "alipay param=" + param);
        String alipayResult = alipay.pay(param);
        LogUtil.d(TAG, "alipay result=" + alipayResult);
        CallbackHandleMsg msg = parseAlipayResult(alipayResult);
        msg.callback = cb;
        callbackInUIThread(msg);
    }

    protected String cutomizePostBody(String postBody) {
        // 支付宝需要添加超时参数
        return postBody + "&time_out=" + ORDER_TIME_OUT;
    }

    @SuppressWarnings("deprecation")
    private String createAlipayUrl(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("partner=\"");
        sb.append(map.get("partner"));
        sb.append("\"&out_trade_no=\"");
        sb.append(map.get("out_trade_no"));
        sb.append("\"&subject=\"");
        sb.append(map.get("subject"));
        sb.append("\"&body=\"");
        sb.append(map.get("body"));
        sb.append("\"&total_fee=\"");
        sb.append(map.get("total_fee"));
        sb.append("\"&notify_url=\"");
        // 网址需要做URL编码
        sb.append(URLEncoder.encode(map.get("notify_url")));
        sb.append("\"&service=\"");
        sb.append(map.get("service"));
        sb.append("\"&_input_charset=\"utf-8");
        sb.append("\"&return_url=\"" + URLEncoder.encode("http://m.alipay.com"));
        sb.append("\"&payment_type=\"");
        sb.append(map.get("payment_type"));
        sb.append("\"&seller_id=\"");
        sb.append(map.get("seller_id"));
        sb.append("\"&it_b_pay=\"");
        sb.append(ORDER_TIME_OUT);
        sb.append("\"&sign=\"");
        sb.append(URLEncoder.encode(map.get("sign")));
        sb.append("\"&sign_type=\"RSA\"");
        return sb.toString();
    }

    private CallbackHandleMsg parseAlipayResult(String alipayResult) {
        CallbackHandleMsg msg = new CallbackHandleMsg();
        msg.actionType = getActionType();
        msg.extras = queryOrderMap;
        if (TextUtils.isEmpty(alipayResult)) {
            msg.resultCode = ResultCode.AliPay.ParamErr;
            return msg;
        }

        int errcode = 0;
        try {
            String[] result_array = alipayResult.split("\\;");
            if (result_array != null && result_array.length == 3) {
                // 返回码
                String resultStatus = result_array[0].substring(result_array[0].indexOf('{') + 1,
                        result_array[0].length() - 1);
                errcode = Integer.parseInt(resultStatus);
                msg.resultCode = errcode;
                String result = result_array[2].substring(result_array[2].indexOf('{') + 1,
                        result_array[2].length() - 1);
                if (result != null && result.length() > 0) {
                    // 解析订单信息
                    String orderInfo[] = result.split("\\&");
                    if (orderInfo != null && orderInfo.length > 0) {
                        for (int i = 0; i < orderInfo.length; i++) {
                            String[] tmp = orderInfo[i].split("\\=");
                            String key = tmp[0];
                            String val = tmp[1].substring(1, tmp[1].length() - 1);
                            msg.extras.put(key, val);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage(), e);
            msg.resultCode = ResultCode.AliPay.ParamErr;
        }
        return msg;
    }

    @Override
    public int getActionType() {
        return PaymentDesc.ID_ALIPAY;
    }

    @Override
    public String getPutaoCreateOrderUrl() {
        return Config.PAY.ALIPAY_CREATE_ORDER_URL;
    }

    @Override
    protected Map<String, String> convertToParamMap(JSONObject paymentBean,
            Map<String, String> queryMap) throws Exception {
        Map<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("partner", paymentBean.getString("partner"));
        hashMap.put("out_trade_no", paymentBean.getString("out_trade_no"));
        hashMap.put("subject", paymentBean.getString("subject"));
        hashMap.put("body", paymentBean.getString("body"));
        hashMap.put("total_fee", paymentBean.getString("total_fee"));
        hashMap.put("notify_url", paymentBean.getString("notify_url"));
        hashMap.put("service", paymentBean.getString("service"));
        hashMap.put("return_url", "http://m.alipay.com");
        hashMap.put("payment_type", paymentBean.getString("payment_type"));
        hashMap.put("seller_id", paymentBean.getString("seller_id"));
        hashMap.put("sign", paymentBean.getString("sign"));
        return hashMap;
    }
}
