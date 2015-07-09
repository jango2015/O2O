
package so.contacts.hub.payment.action;

import so.contacts.hub.payment.core.PaymentDesc;
import android.app.Activity;

public class DefaultPaymentActionFactory {
    public static AbstractPaymentAction createAction(int actionType, Activity ctx) {
        switch (actionType) {
            case PaymentDesc.ID_ALIPAY:
                return new AliPayPaymentAction(ctx);

            case PaymentDesc.ID_WE_CHAT:
                return new WeChatPaymentAction(ctx);

            default:
                return null;
        }
    }
}
