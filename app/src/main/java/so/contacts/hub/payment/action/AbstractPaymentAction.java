
package so.contacts.hub.payment.action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import so.contacts.hub.account.PutaoAccount;
import so.contacts.hub.core.PTUser;
import so.contacts.hub.http.Http;
import so.contacts.hub.payment.GetOrderParam;
import so.contacts.hub.payment.PaymentCallback;
import so.contacts.hub.payment.core.PaymentAction;
import so.contacts.hub.payment.core.PaymentDesc;
import so.contacts.hub.payment.data.ResultCode;
import so.contacts.hub.payment.data.ResultCode.PutaoServerResponse;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.NetUtil;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.mdroid.core.http.IgnitedHttpResponse;

/**
 * 抽象支付动作. 该类执行具体的支付操作. 目前支持支付宝和微信支付. 后继添加新的支付方式应该继承自该类. 封装的支付流程具体如下:
 * <ol>
 * <li>获取葡萄后台创建订单的URL. 不同的支付方式对应的链接不同, 由子类决定</li>
 * <li>将{@link GetOrderParam}传递给葡萄后台, 生成订单信息</li>
 * <li>如订单生成失败, 回调{@link PaymentCallback}; 如订单生成成功, 将订单结果封装成JSON对象,
 * 交给子类将其封装为支付需要的参数Map</li>
 * <li>子类使用参数Map, 执行支付</li>
 * <li>回调{@link PaymentCallback}返回支付结果</li>
 * </ol>
 * 
 * @see PaymentCallback
 * @see PaymentAction
 * @author Steve Xu 徐远同
 */
public abstract class AbstractPaymentAction implements PaymentAction, Runnable {
    private static final String TAG = AbstractPaymentAction.class.getSimpleName();

    /**
     * 执行支付操作的线程池
     */
    protected static ThreadPoolExecutor exe = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());

    /**
     * 静态的Handler, 用于将支付结果发送到UI线程
     */
    protected static Handler uiDispatcher = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            CallbackHandleMsg chm = (CallbackHandleMsg)msg.obj;
            if (null != chm.callback) {

                try {
                    chm.callback.onPaymentFeedback(chm.actionType, chm.error, chm.resultCode,
                            chm.extras);
                } catch (Exception e) {
                    LogUtil.e(TAG, e.getMessage(), e);
                }
            }
        }
    };

    /**
     * 获取支付类型. 由子类返回. 支付类型返回的值应该是{@link PaymentDesc}中定义的ID_开头字段.
     * 
     * @return
     */
    public abstract int getActionType();

    /**
     * 获取葡萄服务器创建该支付类型的URL链接.
     * 
     * @return
     */
    public abstract String getPutaoCreateOrderUrl();

    /**
     * 支付过程中的参数列表
     */
    protected Map<String, String> queryOrderMap = new HashMap<String, String>();

    /**
     * Activity的弱引用
     */
    protected WeakReference<Activity> actRef;

    /**
     * PaymentCallback的弱引用
     */
    protected WeakReference<PaymentCallback> callbackRef;

    /**
     * 与葡萄服务器链接创建订单用的统一数据类型
     */
    protected GetOrderParam orderParam;

    public AbstractPaymentAction(Activity ctx) {
        actRef = new WeakReference<Activity>(ctx);
    }

    public AbstractPaymentAction putServerOrderParam(String key, String value) {
        queryOrderMap.put(key, value);
        return this;
    }

    public AbstractPaymentAction setQueryOrderMap(Map<String, String> map) {
        queryOrderMap = map;
        return this;
    }

    public void startPayment(GetOrderParam param, PaymentCallback callback) {
        // 没有设置callback的情况下支付没有意义
        if (null == callback) {
            LogUtil.e(TAG, "no payment callback found!");
            return;
        }

        callbackRef = new WeakReference<PaymentCallback>(callback);
        orderParam = param;
        // 没有用户, 无法进行支付
        PTUser ptUser = PutaoAccount.getInstance().getPtUser();
        if (null == ptUser) {
            LogUtil.e(TAG, "no pu tao user found!");
            callback.onPaymentFeedback(getActionType(), new IllegalStateException(
                    "putao user not found"), ResultCode.OrderStatus.Failed, null);
            return;
        }

        // 没有网络, 无法进行支付
        if (!NetUtil.checkNet(actRef.get())) {
            LogUtil.e(TAG, "no pu tao user found!");
            callback.onPaymentFeedback(getActionType(), new IllegalStateException(
                    "no internet found!"), ResultCode.OrderFailed.NetError, null);
            return;
        }

        exe.execute(this);
    }

    /**
     * 将葡萄服务器生成的订单JSON对象转换成支付需要用到的参数列表
     * 
     * @param paymentBean
     * @param queryMap
     * @return
     * @throws Exception
     */
    protected abstract Map<String, String> convertToParamMap(JSONObject paymentBean,
            Map<String, String> queryMap) throws Exception;

    /**
     * 去葡萄服务器创建订单
     * 
     * @param url
     * @return
     * @throws JSONException
     * @throws IOException
     * @throws Exception
     */
    protected JSONObject createPutaoOrder(String url) throws JSONException, IOException {
        Context ctx = actRef.get();
        Http http = new Http(ctx);
        http.setDefaultHeader("Content-Type", "application/x-www-form-urlencoded");
        // 将GetOrderParam按约定转换成对应的字符串
        String postBody = orderParam.toQueryString();
        // 给子类一个自定义参数的机会
        postBody = cutomizePostBody(postBody);
        LogUtil.d(TAG, "post body = " + postBody);
        StringEntity entity = null;
        try {
            entity = new StringEntity(postBody, HTTP.UTF_8);
        } catch (UnsupportedEncodingException e) {
            LogUtil.e(TAG, e.getMessage(), e);
        }
        IgnitedHttpResponse resp;
        resp = http.post(url, entity).send();
        String body = resp.getResponseBodyAsString();
        LogUtil.d(TAG, "create order from server, url = " + url + ", response = " + body);
        return new JSONObject(body);
    }

    /**
     * Hook方法. 给子类一个自定义定义创建订单参数的机会.默认返回原值.
     * 
     * @param postBody
     * @return
     */
    protected String cutomizePostBody(String postBody) {
        return postBody;
    }

    /**
     * 执行支付操作
     * 
     * @param map
     * @param cb
     * @throws Exception
     */
    protected abstract void doPayment(Map<String, String> map, PaymentCallback cb) throws Exception;

    /**
     * 将支付消息发送到UI线程
     * 
     * @param msg
     */
    protected void callbackInUIThread(CallbackHandleMsg msg) {
        if (null == msg.callback) {
            msg.callback = callbackRef.get();
        }
        if (null == msg.extras) {
            msg.extras = queryOrderMap;
        }

        msg.extras.putAll(orderParam.toResultUI());

        /*
         * || msg.actionType == PaymentDesc.ID_WE_CHAT 增加判断条件，如果是微信则算出total_fee设置进去
         * modify by ffh 2015-3-3 start
         */
        if (!msg.extras.containsKey("total_fee") || msg.actionType == PaymentDesc.ID_WE_CHAT) {
            msg.extras.put("total_fee",
                    String.format("%.2f", ((orderParam.getPriceInCents() * 1.0) / 100.0)));
        }
        /*
         * end 2015-3-3 by ffh
         */
        
        uiDispatcher.sendMessage(uiDispatcher.obtainMessage(0, msg));
    }

    /**
     * 将入参JSONObject里的值全部放到{@link #queryOrderMap}中
     * 
     * @param json
     */
    private void putJsonToQueryMap(JSONObject json) {
        @SuppressWarnings("unchecked")
        Iterator<String> keyIte = (Iterator<String>)json.keys();
        while (keyIte.hasNext()) {
            String key = keyIte.next();
            try {
                String value = json.getString(key);
                queryOrderMap.put(key, value);
            } catch (JSONException e) {
                LogUtil.e(TAG, e.getMessage(), e);
            }
        }
    }

    @Override
    public void run() {
        PaymentCallback callback = callbackRef.get();
        // 1. 获取葡萄后台创建订单的URL
        String url = getPutaoCreateOrderUrl();
        // 2. 去葡萄后台创建订单
        JSONObject resp = null;
        String resultCode, message;
        try {
            resp = createPutaoOrder(url);
            resultCode = resp.getString("ret_code");
            message = resp.getString("msg");
        } catch (Exception e) {
            CallbackHandleMsg msg = new CallbackHandleMsg();
            msg.error = e;
            msg.actionType = getActionType();
            msg.extras = queryOrderMap;
            msg.resultCode = ResultCode.OrderFailed.ServerBusy;
            msg.callback = callback;
            callbackInUIThread(msg);
            return;
        }

        // 订单创建失败
        boolean isOrderSuccess = ResultCode.PutaoServerResponse.ResultCodeSuccess
                .equals(resultCode) && resp.has("data");
        if (!isOrderSuccess) {
            LogUtil.d(TAG, "create order failed! result code = " + resultCode);
            CallbackHandleMsg msg = new CallbackHandleMsg();
            msg.error = new IllegalArgumentException(message);
            msg.actionType = getActionType();
            msg.extras = queryOrderMap;
            msg.resultCode = parseErrResultCode(resultCode);
            msg.callback = callback;
            callbackInUIThread(msg);
            queryOrderMap.put("isOrderSuccess", "false");
            return;
        }
        queryOrderMap.put("isOrderSuccess", "true");
        try {
            // 3.订单创建成功, 获取订单信息
            JSONObject json = resp.getJSONObject("data");
            // 4. 将订单信息转换为支付方式需要的名值对
            Map<String, String> map = convertToParamMap(json, queryOrderMap);
            LogUtil.d(TAG, "convert payement map=" + map);
            // 将订单信息存在Map里
            putJsonToQueryMap(json);
            // 5. 执行支付操作
            doPayment(map, callback);
        } catch (Exception e) {
            CallbackHandleMsg msg = new CallbackHandleMsg();
            msg.error = e;
            msg.actionType = getActionType();
            msg.extras = queryOrderMap;
            msg.resultCode = parseErrResultCode(e.getMessage());
            msg.callback = callback;
            callbackInUIThread(msg);
        }
    }

    private static int parseErrResultCode(String resultCode) {
        if (!TextUtils.isEmpty(resultCode)) {
            if (PutaoServerResponse.CouponNotExists.equals(resultCode)
                    || PutaoServerResponse.CouponExpired.equals(resultCode)) {
                return PutaoServerResponse.CouponInvalid;
            }
        }
        return ResultCode.OrderFailed.ServerBusy;
    }

    static class CallbackHandleMsg {
        public int actionType;

        public Throwable error;

        public int resultCode;

        public Map<String, String> extras;

        public PaymentCallback callback;
    }
}
