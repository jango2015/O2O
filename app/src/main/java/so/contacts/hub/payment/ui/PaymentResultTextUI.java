
package so.contacts.hub.payment.ui;

public class PaymentResultTextUI {
    public int valueResId;

    public String paramKey;

    public PaymentResultTextUI() {
    }

    public PaymentResultTextUI(int resId) {
        this(resId, null);
    }

    public PaymentResultTextUI(int resId, String key) {
        this.valueResId = resId;
        this.paramKey = key;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((paramKey == null) ? 0 : paramKey.hashCode());
        result = prime * result + valueResId;
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
        PaymentResultTextUI other = (PaymentResultTextUI)obj;
        if (paramKey == null) {
            if (other.paramKey != null)
                return false;
        } else if (!paramKey.equals(other.paramKey))
            return false;
        if (valueResId != other.valueResId)
            return false;
        return true;
    }
}
