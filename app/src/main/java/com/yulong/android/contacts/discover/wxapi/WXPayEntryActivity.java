
package com.yulong.android.contacts.discover.wxapi;

import so.contacts.hub.shuidianmei.YellowPageWEGFragment;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.UMengEventIds;

import so.contacts.hub.msgcenter.MsgCenterConfig;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import so.contacts.hub.common.PayConfig;
import so.contacts.hub.payment.GetOrderParam;
import so.contacts.hub.payment.PaymentViewGroup;
import so.contacts.hub.payment.ResultActivityDelegator;
import so.contacts.hub.payment.ResultActivityDelegatorFactory;
import so.contacts.hub.payment.core.PaymentDesc;
import so.contacts.hub.payment.core.PaymentResultUIBuilder;
import so.contacts.hub.payment.data.ProductTypeCode;
import so.contacts.hub.payment.data.ResultCode;
import so.contacts.hub.payment.ui.PaymentResult;
import so.contacts.hub.remind.BaseRemindActivity;
import so.contacts.hub.ui.yellowpage.YellowPageChargeResultActivity;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.TelAreaUtil;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * 微信支付回调结果页面 [注：该文件包名、类名均不能修改；微信是通过反射获取该文件的]
 * 
 * @author Michael
 */
public class WXPayEntryActivity extends BaseRemindActivity implements IWXAPIEventHandler {

    private static final String TAG = "WXPayEntryActivity";

    private IWXAPI mIWXAPI = null;

    private PaymentResultUIBuilder builder;

    private ResultActivityDelegator delegator;

    private PaymentResult result;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogUtil.i(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        result = PaymentViewGroup.removePaymentResult();
        mIWXAPI = WXAPIFactory.createWXAPI(this, PayConfig.WX_PAY_APPID);
        mIWXAPI.handleIntent(getIntent(), this);
        if (null != result) {
            try {
                delegator = ResultActivityDelegatorFactory.createDelegator(result.getProductType());
                if (null != delegator) {
                    delegator.onCreate(this, savedInstanceState);
                }
            } catch (Exception e) {
                LogUtil.e(TAG, e.getMessage(), e);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mIWXAPI.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    // 临时使用，因为后台1.7没有做新的话费支付接口，后面统一处理。
    private PaymentResult tempFuncForPhoneCharge(String extData) {
        try {
            JSONObject jsonObj = new JSONObject(extData);
            GetOrderParam fakePara = new GetOrderParam();
            int rechargeContent = jsonObj.optInt("recharge_content", -1);
            int productId = 0, productType = 0;
            switch (rechargeContent) {
                case YellowPageChargeResultActivity.CONTENT_TEL:
                    productId = ProductTypeCode.Telephone.ProductId;
                    productType = ProductTypeCode.Telephone.ProductType;
                    break;
            }
            fakePara.setProductId(productId);
            fakePara.setProductType(productType);
            PaymentResult result = new PaymentResult(fakePara, PaymentDesc.ID_WE_CHAT);
            String mobile = jsonObj.optString("mobile", null);
            Map<String, String> mobileMap = toMap(jsonObj);
            if (null != mobile) {
                String phoneAddr = TelAreaUtil.getInstance().searchTel(mobile, this);
                if (phoneAddr == null) {
                    phoneAddr = "";
                }
                String phoneOperator = TelAreaUtil.getInstance().getNetwork(mobile, this);
                if (phoneOperator == null) {
                    phoneOperator = "";
                }

                if (!TextUtils.isEmpty(phoneAddr)) {
                    String[] addrArray = phoneAddr.split(" ");
                    if (addrArray != null && addrArray.length > 1) {
                        phoneAddr = addrArray[0];
                    }
                }

                String mobileui = mobile + "    " + phoneAddr + " " + phoneOperator;
                mobileMap.put("mobile_ui", mobileui);
            }
            result.setQueryParams(mobileMap);
            return result;
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void onResp(BaseResp resp) {
        LogUtil.i(TAG, "onResp()");
        int errCode = resp.errCode;
        if (resp instanceof PayResp) {
            PayResp payResp = (PayResp)resp;
            String orderInfoStr = payResp.extData;
            if (!TextUtils.isEmpty(orderInfoStr) && null == result) {
                result = tempFuncForPhoneCharge(orderInfoStr);
            }
            if (null == result) {
                LogUtil.e(TAG, "fetal! payment result is null!");
                finish();
                return;
            }
            LogUtil.i(TAG, "onResp errCode="+errCode);
            try {
                int resultCode = ResultCode.OrderStatus.Failed;
                switch (errCode) {
                    case ResultCode.WeChat.Success:
                        resultCode = ResultCode.OrderStatus.Success;
                        //add by xcx 2015-01-15 start 支付结果统计
                        Map<String, String> extras = result.getQueryParams();
                        if(extras!=null){
                            String umentIds=extras.get(UMengEventIds.EXTRA_UMENG_EVENT_IDS_SUCCESS);
                            if(!TextUtils.isEmpty(umentIds)){
                                String[] ids=umentIds.split(",");
                                if(ids.length>0){
                                    for(int i=0;i<ids.length;i++){
                                        MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
                                                ids[i]);
                                    }
                                }
                            }
                        }
                       //add by xcx 2015-01-15 end 支付结果统计
                        break;

                    case ResultCode.WeChat.Failed:
                        resultCode = ResultCode.OrderStatus.Failed;
                        //add by xcx 2015-01-15 start 支付结果统计
                        Map<String, String> extras2 = result.getQueryParams();
                        if(extras2!=null){
                            String umentIds=extras2.get(UMengEventIds.EXTRA_UMENG_EVENT_IDS_FAIL);
                            if(!TextUtils.isEmpty(umentIds)){
                                String[] ids=umentIds.split(",");
                                if(ids.length>0){
                                    for(int i=0;i<ids.length;i++){
                                        MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
                                                ids[i]);
                                    }
                                }
                            }
                        }
                        //add by xcx 2015-01-15 end 支付结果统计
                        break;

                    case ResultCode.WeChat.Cancel:
                        resultCode = ResultCode.OrderStatus.Cancel;
                        break;
                }
                result.setUserResultCode(resultCode);
                builder = new PaymentResultUIBuilder(this, result);
                builder.startQueryStatus();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    
    
    
    @SuppressWarnings("unchecked")
    public static Map<String, String> toMap(JSONObject object) throws JSONException {
        Map<String, String> map = new HashMap<String, String>();

        Iterator<String> keysItr = (Iterator<String>)object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if (value instanceof JSONObject) {
                value = toMap((JSONObject)value);
            } else {
                map.put(key, value.toString());
            }
        }
        return map;
    }

    @Override
    protected void onResume() {
        LogUtil.i(TAG, "onResume()");
        super.onResume();
        if (null != delegator) {
            // delegator调用由业务触发, 存在异常风险, 因此全部做异常处理, 保证activity生命周期正常执行完毕.
            try {
                delegator.onResume(this);
            } catch (Exception e) {
                LogUtil.e(TAG, e.getMessage(), e);
            }
        }
    }

    @Override
    protected void onPause() {
        LogUtil.i(TAG, "onPause()");
        super.onPause();
        if (null != delegator) {
            // delegator调用由业务触发, 存在异常风险, 因此全部做异常处理, 保证activity生命周期正常执行完毕.
            try {
                delegator.onPause(this);
            } catch (Exception e) {
                LogUtil.e(TAG, e.getMessage(), e);
            }
        }
    }

    @Override
    protected void onDestroy() {
        LogUtil.i(TAG, "onDestroy()");
        super.onDestroy();
        if (null != delegator) {
            // delegator调用由业务触发, 存在异常风险, 因此全部做异常处理, 保证activity生命周期正常执行完毕.
            try {
                delegator.onDestroy(this);
            } catch (Exception e) {
                LogUtil.e(TAG, e.getMessage(), e);
            }
        }
    }
    
    protected void onStart(){
        LogUtil.i(TAG, "onStart()");
        super.onStart();
        if (null != delegator) {
            // delegator调用由业务触发, 存在异常风险, 因此全部做异常处理, 保证activity生命周期正常执行完毕.
            try {
                delegator.onStart(this);
            } catch (Exception e) {
                LogUtil.e(TAG, e.getMessage(), e);
            }
        }
    }
    
    protected void onStop(){
        LogUtil.i(TAG, "onStop()");
        super.onStop();
        if (null != delegator) {
            // delegator调用由业务触发, 存在异常风险, 因此全部做异常处理, 保证activity生命周期正常执行完毕.
            try {
                delegator.onStop(this);
            } catch (Exception e) {
                LogUtil.e(TAG, e.getMessage(), e);
            }
        }
    }


    @Override
    public String getServiceNameByUrl() {
        return null;
    }

    @Override
    public String getServiceName() {
        return null;
    }

    @Override
    public boolean needMatchExpandParam() {
        return false;
    }

    @Override
    public Integer remindCode() {
        return null;
    }

    @Override
    protected boolean needReset() {
        return false;
    }
}
