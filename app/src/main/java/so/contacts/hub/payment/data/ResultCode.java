
package so.contacts.hub.payment.data;

/**
 * 支付结果代码常量
 * 
 * @author Steve Xu 徐远同
 */
public class ResultCode {
    private ResultCode() {
    }

    public static interface PutaoServerResponse {
        /** 服务器异常 */
        public static final int InternalErr = -1;

        /** 签名异常 */
        public static final int SignErr = -2;

        /** 优惠券无效 */
        public static final int CouponInvalid = -3;

        /** 参数异常 */
        public static final int ParamErr = -4;

        /** 无产品报价 */
        public static final int MissingProduct = -5;

        /** 服务器维护中 */
        public static final int ServiceStopped = -6;

        /** 未知错误 */
        public static final int UnknownErr = -99;

        public static final String ResultCodeSuccess = "0000";

        public static final String CouponNotExists = "12104";

        public static final String CouponExpired = "12105";

    }

    public static interface AliPay {
        /** 支付成功 */
        public static final int Success = 9000;

        /** 正在处理 */
        public static final int Processing = 8000;

        /** 支付失败 */
        public static final int Failed = 4000;

        /** 请求参数错误 */
        public static final int ParamErr = 4001;

        /** 订单取消 */
        public static final int Canceled = 6001;

        /** 网络连接错误 */
        public static final int NetError = 6002;
    }

    public static interface WeChat {
        public static final int Success = 0;

        public static final int Failed = -1;

        public static final int Cancel = -2;
    }

    public static interface OrderStatus {
        // 0 取消
        // 1 待支付
        // 2 支付失败
        // 3 处理中（支付成功 交易进行中）
        // 4 交易成功
        // 5 退款中（支付成功 交易失败 退款中）
        // 6 退款成功
        // 7 过期
        // 8 暂存订单
        public static final int Cancel = 0;

        public static final int WaitForPayment = 1;

        public static final int Failed = 2;

        public static final int Pending = 3;

        public static final int Success = 4;

        public static final int AskForRefund = 5;

        public static final int Refunded = 6;
        
        public static final int OutOfDate = 7;
        
        public static final int TempOrder = 8;
        
        public static final int TimeOut = 0x100;
    }

    public static interface PaymentHistory {
        /** 充值成功 */
        public static final int Success = 1;

        /** 账单已过期，关闭 */
        public static final int Closed = 1;

        /** 等待付款 */
        public static final int WaitForPayment = 3;

        /** 支付失败, 等待退款 */
        public static final int FailedWaitRefund = 4;

        /** 支付失败, 退款成功 */
        public static final int Refunded = 5;

        // /** 等待付款（作为“充值成功”处理） */
        // public static final int WaitForPayment = 6;
        //
        // /** 充值失败（作为“等待付款”处理） */
        // public static final int FailedWaitRefund = 7;
        //
        // /** 支付成功(支付成功、充值在进行) */
        // public static final int Success = 8;
    }

    public static interface OrderFailed {

        public static final int NetError = -0x200;

        public static final int Timeout = -0x201;

        public static final int ServerBusy = -0x202;
    }
}
