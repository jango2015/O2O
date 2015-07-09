
package so.contacts.hub.payment;

import android.text.TextUtils;

import so.contacts.hub.shuidianmei.YellowPageWEGFragment;

import so.contacts.hub.payment.data.ResultCode;

import so.contacts.hub.payment.core.PaymentDesc;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.msgcenter.MsgCenterConfig;
import so.contacts.hub.util.UMengEventIds;

import java.util.Map;

import so.contacts.hub.payment.core.PaymentResultUIBuilder;
import so.contacts.hub.payment.ui.PaymentResult;
import so.contacts.hub.remind.BaseRemindActivity;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.MobclickAgentUtil;
import android.content.Intent;
import android.os.Bundle;

/**
 * 支付结果页面
 * 
 * @author Steve Xu 徐远同
 */
public class PaymentResultActivity extends BaseRemindActivity {

    private static final String TAG = PaymentResultActivity.class.getName();

    /**
     * 支付结果页Builder
     */
    private PaymentResultUIBuilder builder;

    private ResultActivityDelegator delegator;

    public void onCreate(Bundle saveInstance) {
        super.onCreate(saveInstance);
        // 没有intent对象, 异常情况, 记录并退出
        Intent intent = getIntent();
        if (null == intent) {
            LogUtil.e(TAG, "intent is null");
            finish();
            return;
        }

        // 获取支付结果
        PaymentResult result = (PaymentResult)intent.getParcelableExtra("result");
        if (null == result) {
            LogUtil.e(TAG, "payment result parcel is null");
            finish();
            return;
        }

        int businessType = result.getProductType();
        int actionType = result.getActionType();
        int resultCode = result.getUserResultCode();
        Map<String, String> extras = result.getQueryParams();
        if (-1 == businessType || -1 == actionType || extras == null || -1 == resultCode) {
            LogUtil.e(TAG, "param not found. business type=" + businessType + ", extras = "
                    + extras);
            finish();
            return;
        }

        delegator = ResultActivityDelegatorFactory.createDelegator(businessType);

        // 订单号
        String orderNo = extras.get("out_trade_no");

        if (orderNo == null) {
            LogUtil.e(TAG, "order no not found.");
            finish();
            return;
        }
        
        
        builder = new PaymentResultUIBuilder(this, result);
        builder.buildView();
        
        if (null != delegator) {
            // delegator调用由业务触发, 存在异常风险, 因此全部做异常处理, 保证activity生命周期正常执行完毕.
            try {
                delegator.onCreate(this, saveInstance);
            } catch (Exception e) {
                LogUtil.e(TAG, e.getMessage(), e);
            }
        }
        
        //add by xcx 2015-01-15 start 支付结果统计
        if (ResultCode.AliPay.Success == result.getOriginalResultCode()) {
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
        } else if (ResultCode.AliPay.Failed == result.getOriginalResultCode()
                || ResultCode.AliPay.ParamErr == result.getOriginalResultCode()
                || ResultCode.AliPay.NetError == result.getOriginalResultCode()) {
            String umentIds=extras.get(UMengEventIds.EXTRA_UMENG_EVENT_IDS_FAIL);
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
    }

    public void onResume() {
        super.onResume();
        // onResume时去查询订单状态
        builder.startQueryStatus();
        MobclickAgentUtil.onResume(this);
        if (null != delegator) {
            // delegator调用由业务触发, 存在异常风险, 因此全部做异常处理, 保证activity生命周期正常执行完毕.
            try {
                delegator.onResume(this);
            } catch (Exception e) {
                LogUtil.e(TAG, e.getMessage(), e);
            }
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_OK);
        finish();
    }

    public void onPause() {
        super.onPause();
        MobclickAgentUtil.onPause(this);
        if (null != delegator) {
            // delegator调用由业务触发, 存在异常风险, 因此全部做异常处理, 保证activity生命周期正常执行完毕.
            try {
                delegator.onPause(this);
            } catch (Exception e) {
                LogUtil.e(TAG, e.getMessage(), e);
            }
        }
    }

    public void onStart() {
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

    public void onStop() {
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

    public void onDestroy() {
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
        /*
         * modify by putao_lhq at 2015年1月14日 @start
         * 充值结果界面，只有在充值成功后才会弹出彩蛋界面
         * old code:
         * return false;
         */
        return true;/* end by putao_lhq */
    }

    @Override
    public Integer remindCode() {
        return 0;
    }

}
