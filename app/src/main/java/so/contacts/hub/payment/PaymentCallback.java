
package so.contacts.hub.payment;

import java.util.Map;

import so.contacts.hub.payment.core.PaymentDesc;

/**
 * 支付回调接口. 支付过程中调用
 * @author Steve Xu 徐远同
 *
 */
public interface PaymentCallback {
    /**
     * 支付完成后回调该接口
     * @param actionType 支付类型, 表明本次支付采取的支付方式. 对应{@link PaymentDesc PaymentDesc}里ID_开头的静态常量, 如<code>PaymentDesc.ID_ALIPAY</code>
     * @param t 异常. 如果支付过程中出现异常, 会将异常值传入. 无异常该值为<code>null</code>
     * @param resultCode 支付结果码
     * @param extras 支付期间产生的数据. 具体包括: {@link GetOrderParam}里的全部参数(包括所有field和<code>SubObj</code>Map和<code>UI</code>Map里的全部字段); 从后台创建订单时获取到的全部参数字段; 
     */
    public void onPaymentFeedback(int actionType, Throwable t, int resultCode,
            Map<String, String> extras);
}
