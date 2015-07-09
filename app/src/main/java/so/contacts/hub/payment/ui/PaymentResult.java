
package so.contacts.hub.payment.ui;

import java.util.Map;

import so.contacts.hub.payment.GetOrderParam;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * 支付结果数据
 * @author Steve Xu 徐远同
 *
 */
public final class PaymentResult implements Parcelable {
    private int originalResultCode = -1;

    private Throwable error;

    private int userResultCode = -1;

    private GetOrderParam orderParam;

    private int actionType = -1;

    private Map<String, String> queryParams;

    @SuppressWarnings("unchecked")
    private PaymentResult(Parcel parcel) {
        originalResultCode = parcel.readInt();
        error = (Throwable)parcel.readSerializable();
        userResultCode = parcel.readInt();
        orderParam = parcel.readParcelable(getClass().getClassLoader());
        actionType = parcel.readInt();
        queryParams = parcel.readHashMap(getClass().getClassLoader());
    }

    public PaymentResult(GetOrderParam orderParam, int actionType) {
        this.orderParam = orderParam;
        this.actionType = actionType;
    }

    public int getOriginalResultCode() {
        return originalResultCode;
    }

    public void setOriginalResultCode(int originalResultCode) {
        this.originalResultCode = originalResultCode;
    }

    public Throwable getError() {
        return error;
    }

    public boolean isError() {
        return null != error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    public int getUserResultCode() {
        return userResultCode;
    }

    public void setUserResultCode(int userResultCode) {
        this.userResultCode = userResultCode;
    }

    public GetOrderParam getOrderParam() {
        return orderParam;
    }

    public void setOrderParam(GetOrderParam orderParam) {
        this.orderParam = orderParam;
    }

    public int getActionType() {
        return actionType;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public int getProductType() {
        if (null != orderParam) {
            return orderParam.getProductType();
        }
        return -1;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(originalResultCode);
        dest.writeSerializable(error);
        dest.writeInt(userResultCode);
        dest.writeParcelable(orderParam, 0);
        dest.writeInt(actionType);
        dest.writeMap(queryParams);
    }

    public static final Creator<PaymentResult> CREATOR = new Creator<PaymentResult>() {
        @Override
        public PaymentResult[] newArray(int size) {
            return new PaymentResult[size];
        }

        @Override
        public PaymentResult createFromParcel(Parcel source) {
            return new PaymentResult(source);
        }
    };
}
