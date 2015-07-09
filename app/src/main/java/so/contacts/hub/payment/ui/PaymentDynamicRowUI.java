
package so.contacts.hub.payment.ui;

/**
 * 支付结果页面 业务参数动态行的界面描述文件
 * @author Steve Xu 徐远同
 *
 */
public class PaymentDynamicRowUI {
    public int keyResId;

    public PaymentResultTextUI value;

    public PaymentDynamicRowUI() {
    }

    public PaymentDynamicRowUI(int keyResId, int valResId, String paramKey) {
        this.keyResId = keyResId;
        this.value = new PaymentResultTextUI(valResId, paramKey);
    }

    public PaymentDynamicRowUI(int keyResId, int valResId) {
        this(keyResId, valResId, null);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + keyResId;
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PaymentDynamicRowUI other = (PaymentDynamicRowUI)obj;
        if (keyResId != other.keyResId)
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }
}
