
package so.contacts.hub.payment.core;

import so.contacts.hub.payment.GetOrderParam;
import so.contacts.hub.payment.PaymentCallback;


/**
 * 支付操作接口, 用于执行具体的支付操作
 * @author Steve Xu 徐远同
 */
public interface PaymentAction {
    /**
     * 开始执行支付操作
     * @param param 构建支付用参数
     * @param callback 支付结果回调
     */
    public void startPayment(GetOrderParam param, PaymentCallback callback);
}
