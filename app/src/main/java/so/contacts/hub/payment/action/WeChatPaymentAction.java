
package so.contacts.hub.payment.action;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.common.PayConfig;
import so.contacts.hub.core.Config;
import so.contacts.hub.payment.PaymentCallback;
import so.contacts.hub.payment.core.PaymentDesc;
import so.contacts.hub.payment.data.ResultCode;
import so.contacts.hub.util.ContactsHubUtils;
import so.contacts.hub.util.LogUtil;
import android.app.Activity;

import com.mdroid.core.util.SystemUtil;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * 微信支付
 * @author Steve Xu 徐远同
 *
 */
public class WeChatPaymentAction extends AbstractPaymentAction {

    private static final String TAG = WeChatPaymentAction.class.getSimpleName();

    public WeChatPaymentAction(Activity ctx) {
        super(ctx);
    }

    @Override
    protected void doPayment(Map<String, String> map, PaymentCallback cb) throws Exception {
        checkParams(map, "appId");
        checkParams(map, "partnerId");
        checkParams(map, "prepayId");
        checkParams(map, "nonceStr");
        checkParams(map, "timeStamp");
        checkParams(map, "packageValue");
        checkParams(map, "sign");
        String appId = map.get("appId");
        Activity context = actRef.get();
        PayConfig.WX_PAY_APPID = appId;
        IWXAPI api = WXAPIFactory.createWXAPI(context, appId);
        PayReq req = new PayReq();
        req.appId = appId;
        req.partnerId = map.get("partnerId");
        req.prepayId = map.get("prepayId");
        req.nonceStr = map.get("nonceStr");
        req.timeStamp = map.get("timeStamp");
        req.packageValue = map.get("packageValue");
        req.sign = map.get("sign");
        req.extData = map.get("extData");
        api.registerApp(appId);
        boolean result = api.sendReq(req);
        CallbackHandleMsg chm = new CallbackHandleMsg();
        chm.actionType = getActionType();
        if (result) {
            chm.resultCode = ResultCode.OrderStatus.Success;
        } else {
            chm.resultCode = ResultCode.OrderStatus.Failed;
        }
        chm.extras = queryOrderMap;

        callbackInUIThread(chm);
    }

    private static void checkParams(Map<String, String> map, String key) {
        if (!map.containsKey(key)) {
            throw new IllegalArgumentException("wechat " + key + "  not found!");
        }
    }

    @Override
    public int getActionType() {
        return PaymentDesc.ID_WE_CHAT;
    }

    
    @Override
	protected String cutomizePostBody(String postBody) {
    	//add by hyl 2015-1-4 添加微信支付渠道标识
    	return postBody + "&pay_channel_no=" + ContactsHubUtils.getWxChannelNo(ContactsApp.getContext());
	}

	@Override
    public String getPutaoCreateOrderUrl() {
        return Config.PAY.WECHAT_CREATE_ORDER_URL;
    }

    @Override
    protected Map<String, String> convertToParamMap(JSONObject paymentBean,
            Map<String, String> queryMap) throws Exception {
        Map<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("appId", paymentBean.getString("app_id"));
        hashMap.put("partnerId", paymentBean.getString("partner_id"));
        hashMap.put("prepayId", paymentBean.getString("prepay_id"));
        hashMap.put("nonceStr", paymentBean.getString("nonce_str"));
        hashMap.put("timeStamp", paymentBean.getString("timestamp"));
        hashMap.put("packageValue", paymentBean.getString("package_value"));
        hashMap.put("sign", paymentBean.getString("sign"));
        LogUtil.d(TAG, "convert param map: json = " + paymentBean + ", map = " + hashMap);
        return hashMap;
    }
}
