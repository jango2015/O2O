
package so.contacts.hub.payment;

import java.util.List;
import java.util.Map;

import so.contacts.hub.payment.core.PaymentDesc;
import so.contacts.hub.payment.data.ResultCode;
import so.contacts.hub.payment.ui.PaymentActionView;
import so.contacts.hub.payment.ui.PaymentResult;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.Utils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.yulong.android.contacts.discover.R;

/**
 * <p>
 * 支付视图组件, 封装了支付界面效果和支付动作(action).
 * </p>
 * <p>
 * 该类是一个容器类, 将{@link so.contacts.hub.payment.core.PaymentDesc PaymentDesc}转换为
 * {@link so.contacts.hub.payment.ui.PaymentActionView PaymentActionView}.
 * </p>
 * <p>
 * 同时, 该类是{@link so.contacts.hub.payment.PaymentCallback PaymentCallback}
 * 的Delegator. 该类实现了{@link so.contacts.hub.payment.PaymentCallback
 * PaymentCallback}, 代理逻辑如下:
 * <ol>
 * <li>将支付业务的结果返回码转换成统一的
 * {@link so.contacts.hub.payment.data.ResultCode.OrderStatus OrderStatus}状态码.
 * 比如, 支付宝的网络异常码为6002, 该类会将其转换为7</li>
 * <li>根据支付业务的返回结果弹出Toast提示信息, 如网络异常的情况会弹出"服务器异常"</li>
 * <li>调用Delegate的PaymentCallback</li>
 * <li>根据支付业务返回结果决定是否需要打开支付结果页面
 * {@link so.contacts.hub.payment.PaymentResultActivity PaymentResultActivity}</li>
 * </ol>
 * 最后, 该类有一个静态接口{@link OnPaymentActionSelectedListener}, 该接口在用户选中支付方式时进行回调.
 * 如业务类关心当前选中的支付方式, 可实现该接口.
 * </p>
 * 
 * @author Steve Xu 徐远同
 */
public class PaymentViewGroup extends LinearLayout implements OnClickListener, PaymentCallback {

    private static final String TAG = PaymentViewGroup.class.getSimpleName();

    // 当前选中的支付描述
    private PaymentDesc currentSelectPay;

    // 被代理的回调接口
    private PaymentCallback callback;

    // 支付方式选中回调接口
    private OnPaymentActionSelectedListener listener;

    // 用来存储PaymentResult的静态变量. 采取ThreadLocal的方式存储是为了保证该结果只能在UI进程里被访问
    private static final ThreadLocal<PaymentResult> RESULT = new ThreadLocal<PaymentResult>();

    public PaymentViewGroup(Context context) {
        super(context);
        initView(PaymentDesc.createDefaultList((Activity)getContext()));
    }

    public PaymentViewGroup(Context context, List<PaymentDesc> descList) {
        super(context);
        initView(descList);
    }

    public PaymentViewGroup(Context context, AttributeSet attr) {
        super(context, attr);
        initView(PaymentDesc.createDefaultList((Activity)getContext()));
    }

    public PaymentViewGroup(Context context, AttributeSet attr, int style) {
        super(context, attr, style);
        initView(PaymentDesc.createDefaultList((Activity)getContext()));
    }

    /*
     * 初始化界面
     */
    private void initView(List<PaymentDesc> list) {
        setOrientation(LinearLayout.VERTICAL);
        // setClickable保证click事件的正常传递
        setClickable(true);
        int height = (int)getResources().getDimension(R.dimen.putao_divider_line_size);
        int lineColor = getResources().getColor(R.color.putao_yellow_page_line_color);
        int margin = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, getContext()
                .getResources().getDisplayMetrics());
        for (int i = 0, max = list.size(); i < max; i++) {
            PaymentDesc pd = list.get(i);
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT);
            params.setMargins(margin, 0, margin, 0);
            View v = new PaymentActionView(getContext(), pd);
            v.setOnClickListener(this);
            addView(v, params);
            if (i < (max - 1)) {
                addView(createDivider(height, lineColor, margin));
            }
        }
        // 如果存在上次保存的支付结果, 说明上次支付可能遇见了异常, 目前采取的方式是移除掉上次结果
        removePaymentResult();
    }

    private View createDivider(int height, int color, int margin) {
        View divider = new View(getContext());
        divider.setBackgroundColor(color);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, height);
        params.setMargins(margin, 0, margin, 0);
        params.gravity = Gravity.CENTER_VERTICAL;
        divider.setLayoutParams(params);
        return divider;
    }

    /**
     * 设置支付类型对应的价格文本
     * 
     * @param actionType 支付类型, 对应{@link PaymentDesc PaymentDesc}里ID_开头的静态常量, 如
     *            <code>PaymentDesc.ID_ALIPAY</code>
     * @param text 需要改变的文本
     */
    public void setAmountText(int actionType, String text) {
        View v = findViewById(actionType);
        if (null != v && v instanceof PaymentActionView) {
            PaymentActionView pav = (PaymentActionView)v;
            pav.setAmountText(text);
        }
    }

    /**
     * 设置支付类型对应的视图可用状态
     * 
     * @param actionType 支付类型, 对应{@link PaymentDesc PaymentDesc}里ID_开头的静态常量, 如
     *            <code>PaymentDesc.ID_ALIPAY</code>
     * @param enable true为可用, false为禁用
     */
    public void enablePayActoin(int actionType, boolean enable) {
        View v = findViewById(actionType);
        if (null != v && v instanceof PaymentActionView) {
            PaymentActionView pav = (PaymentActionView)v;
            pav.enable(enable);
        }
    }

    /**
     * 选中actionType对应的支付视图. 该方法会触发{@link OnPaymentActionSelectedListener }的
     * <code>onActionSelected</code>方法
     * 
     * @param actionType 支付类型, 对应{@link PaymentDesc PaymentDesc}里ID_开头的静态常量, 如
     *            <code>PaymentDesc.ID_ALIPAY</code>
     * @param enable true为可用, false为禁用
     * @see OnPaymentActionSelectedListener
     */
    public void selectPayAction(int actionType) {
        View v = findViewById(actionType);
        if (null != v && v instanceof PaymentActionView) {
            PaymentActionView pav = (PaymentActionView)v;
            currentSelectPay = pav.getPaymentDesc();
            int id = actionType;
            for (int i = 0, max = getChildCount(); i < max; i++) {
                View child = getChildAt(i);
                boolean shouldSelect = (child.getId() == id);
                child.setSelected(shouldSelect);
            }
            OnPaymentActionSelectedListener listener = getOnPaymentActionSelectedListener();
            if (null != listener) {
                try {
                    listener.onActionSelected(currentSelectPay, pav);
                } catch (Exception e) {
                    LogUtil.e(TAG, e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 获取当前选中的支付描述文件
     * 
     * @return
     */
    public PaymentDesc getCurrentSelectPay() {
        return currentSelectPay;
    }

    /**
     * 判断当前是否能进行支付
     * 
     * @return false: 没有支付方式被选中 true: 有选中的支付方式
     */
    public boolean canDoPayment() {
        return currentSelectPay != null;
    }

    @Override
    public void onClick(View v) {
        if (v instanceof PaymentActionView) {
            PaymentActionView pav = (PaymentActionView)v;
            selectPayAction(pav.getId());
        }
    }

    /**
     * 开始支付. 根据选中的支付方式, 将{@link GetOrderParam orderParam}对应的参数传递到服务器, 生成订单,
     * 并完成支付
     * 
     * @param orderParam
     * @return
     */
    public boolean startPayment(GetOrderParam orderParam) {
        if (!canDoPayment()) {
            LogUtil.d(TAG, "can not do payment now");
            return false;
        }

        if (!setPaymentResult(new PaymentResult(orderParam, currentSelectPay.actionType))) {
            LogUtil.d(TAG, "cannot lock payment result. payment is in action.");
            return false;
        }

        currentSelectPay.action.startPayment(orderParam, this);
        return true;
    }

    /**
     * 设置支付回调接口
     * 
     * @param callback
     */
    public void setPaymentCallback(PaymentCallback callback) {
        this.callback = callback;
    }

    /**
     * 设置支付选中监听
     * 
     * @param listener
     */
    public void setOnPaymentActionSelectedListener(OnPaymentActionSelectedListener listener) {
        this.listener = listener;
    }

    /**
     * 获取支付选中监听
     * 
     * @return
     */
    public OnPaymentActionSelectedListener getOnPaymentActionSelectedListener() {
        return listener;
    }

    /**
     * 支付方式选中监听器, 当{@link PaymentViewGroup}里的{@link PaymentActionView}被选中时触发,
     * 用于告知当前被选中的支付方式.
     * 
     * @author Steve Xu 徐远同
     */
    public static interface OnPaymentActionSelectedListener {
        /**
         * 支付方式被选中时触发
         * 
         * @param desc 被选中的支付描述文件
         * @param view 被选中的支付视图
         */
        public void onActionSelected(PaymentDesc desc, PaymentActionView view);
    }

    static boolean setPaymentResult(PaymentResult result) {
        RESULT.set(result);
        return true;
    }

    /**
     * 获取并移除当前的支付结果数据
     * 
     * @return
     */
    public static PaymentResult removePaymentResult() {
        PaymentResult result = RESULT.get();
        if (null != result) {
            RESULT.set(null);
        }
        return result;
    }

    @Override
    public void onPaymentFeedback(int actionType, Throwable t, int resultCode,
            Map<String, String> extras) {
        PaymentResult payResult = RESULT.get();
        if (null == payResult) {
            LogUtil.e(TAG, "fetal err! result is null! should never happened!");
            return;
        }
        // 设置原始状态吗
        payResult.setOriginalResultCode(resultCode);
        // 设置异常
        payResult.setError(t);
        // 设置参数
        payResult.setQueryParams(extras);
        if(extras != null){
            //add by xcx 2015-01-12 start 将原始结果码返回方便业务处理
            extras.put("originalResultCode", String.valueOf(resultCode));
            //add by xcx 2015-01-12 start 将原始结果码返回方便业务处理
        }
        int userResult = ResultCode.OrderStatus.Failed;
        boolean openActivity = true;
        switch (resultCode) {
            case ResultCode.PutaoServerResponse.InternalErr:
            case ResultCode.PutaoServerResponse.SignErr:
            case ResultCode.PutaoServerResponse.ParamErr:
            case ResultCode.PutaoServerResponse.MissingProduct:
            case ResultCode.PutaoServerResponse.UnknownErr:
            case ResultCode.OrderFailed.ServerBusy:
                Utils.showToast(getContext(), R.string.putao_server_busy, false);
                openActivity = false;
                break;

            case ResultCode.OrderFailed.NetError:
                Utils.showToast(getContext(), R.string.putao_no_net, false);
                openActivity = false;
                break;

            case ResultCode.PutaoServerResponse.ServiceStopped:
                String message = t.getMessage();
                String[] str = message.split("#");
                String userMsg = "";
                if (str != null && str.length == 2) {
                    userMsg = str[1];
                }
                userMsg = String.format(
                        getContext().getResources()
                                .getString(R.string.putao_charge_server_checking), userMsg);
                Utils.showToast(getContext(), userMsg, true);
                openActivity = false;
                break;

            case ResultCode.PutaoServerResponse.CouponInvalid:
                Utils.showToast(getContext(), R.string.putao_user_tel_charge_coupon_error, false);
                openActivity = false;
                break;

            case ResultCode.AliPay.Success:
            case ResultCode.OrderStatus.Success:
                userResult = ResultCode.OrderStatus.Success;
                break;

            case ResultCode.AliPay.NetError:
            case ResultCode.AliPay.Canceled:
                userResult = ResultCode.OrderStatus.Failed;
                break;
        }
        payResult.setUserResultCode(userResult);

        openActivity = openActivity && (actionType != PaymentDesc.ID_WE_CHAT);

        if (null != callback) {
            try {
                callback.onPaymentFeedback(actionType, t, userResult, extras);
            } catch (Exception e) {
                LogUtil.e(TAG, "", e);
            }
        }

        if (openActivity) {
            Intent intent = new Intent(getContext(), PaymentResultActivity.class);
            intent.putExtra("result", removePaymentResult());
            getContext().startActivity(intent);
        } else {
            if (actionType != PaymentDesc.ID_WE_CHAT) {
                removePaymentResult();
            }
        }
    }
}
