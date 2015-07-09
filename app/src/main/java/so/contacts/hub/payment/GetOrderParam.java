
package so.contacts.hub.payment;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.payment.data.ProductTypeCode;
import so.contacts.hub.util.MD5;
import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import com.mdroid.core.util.SystemUtil;

/**
 * 支付时通用的去后台创建订单用的参数类
 * 
 * @author Steve Xu 徐远同
 */
public class GetOrderParam implements Parcelable {
    /**
     * 设备ID
     */
    private String devNo = "";

    /**
     * 时间戳
     */
    private String timestamp = "";

    /**
     * 本地签名.
     */
    private String localSign = "";

    /**
     * 订单号. 去后台创建前一般都为""
     */
    private String orderNo = "";

    /**
     * Channel编号
     */
    private String channelNo = "";

    /**
     * 优惠券列表
     */
    private List<Long> couponIds;

    /**
     * 支付价格, 单位为分
     */
    private int priceInCents = 0;

    /**
     * 与后台约定的product id, 定义在{@link ProductTypeCode}中
     * 
     * @see ProductTypeCode
     */
    private int productId;

    /**
     * 与后台约定的product type, 定义在{@link ProductTypeCode}中
     * 
     * @see ProductTypeCode
     */
    private int productType;

    /**
     * 业务参数SubObj
     */
    private Map<String, String> subObjMap;

    /**
     * UI用字段
     */
    private Map<String, String> uiMap = new HashMap<String, String>();

    private static final String SECRITY = "kksd%sj*77";

    @SuppressWarnings("unchecked")
    private GetOrderParam(Parcel parcel) {
        devNo = parcel.readString();
        timestamp = parcel.readString();
        localSign = parcel.readString();
        orderNo = parcel.readString();
        channelNo = parcel.readString();
        couponIds = parcel.readArrayList(getClass().getClassLoader());
        productId = parcel.readInt();
        productType = parcel.readInt();
        subObjMap = parcel.readHashMap(getClass().getClassLoader());
        uiMap = parcel.readHashMap(getClass().getClassLoader());
    }

    @SuppressLint("UseSparseArrays")
    public GetOrderParam() {
        couponIds = new ArrayList<Long>(1);
        subObjMap = new HashMap<String, String>();
    }

    /**
     * 获取设备编号. 如果此前没有调用{@link #setDevNo(String)}为设备编号赋值, 则自动赋值
     * 
     * @return
     */
    public String getDevNo() {
        if ("" == devNo) {
            devNo = SystemUtil.getDeviceId(ContactsApp.getInstance());
        }
        return devNo;
    }

    /**
     * 获取支付的价格. <strong>注意: 单位为分!</strong>
     * 
     * @return
     */
    public int getPriceInCents() {
        return priceInCents;
    }

    /**
     * 设置支付的价格. <strong>注意: 单位为分!</strong>
     * 
     * @return
     */
    public GetOrderParam setPriceInCents(int priceInCents) {
        this.priceInCents = priceInCents;
        return this;
    }

    /**
     * 设置设备编号
     * 
     * @return
     */
    public GetOrderParam setDevNo(String devNo) {
        this.devNo = devNo;
        return this;
    }

    /**
     * 获取时间戳. 如果此前没有调用{@link #setTimestamp(String)}为设备编号赋值, 则用
     * <code>System.currentTimeMillis()</code>自动赋值
     * 
     * @return
     */
    public String getTimestamp() {
        if ("" == timestamp) {
            timestamp = String.valueOf(System.currentTimeMillis());
        }
        return timestamp;
    }

    /**
     * 设置时间戳
     * 
     * @return
     */
    public GetOrderParam setTimestamp(String stamp) {
        timestamp = stamp;
        return this;
    }

    /**
     * 获取本地签名. 如果此前没有调用{@link #setLocalSign(String)}为本地签名赋值, 则自动赋值,
     * 算法为:MD5(timestamp + devNo + priceInCents + SECURITY)
     * 
     * @return
     */
    public String getLocalSign() {
        if ("" == localSign) {
            localSign = generateLocalSign();
        }
        return localSign;
    }

    /**
     * 设置本地签名
     * 
     * @return
     */
    public GetOrderParam setLocalSign(String localSign) {
        this.localSign = localSign;
        return this;
    }

    public String getOrderNo() {
        return orderNo;
    }

    /**
     * 设置本地签名
     * 
     * @return
     */
    public GetOrderParam setOrderNo(String orderNo) {
        this.orderNo = orderNo;
        return this;
    }

    /**
     * 获取ChannelNo
     * 
     * @return
     */
    public String getChannelNo() {
        if ("" == channelNo) {
            channelNo = SystemUtil.getChannelNo(ContactsApp.getInstance().getApplicationContext());
        }
        return channelNo;
    }

    /**
     * 返回与后台约定的product type, 定义在{@link ProductTypeCode}中
     * 
     * @see ProductTypeCode
     * @return
     */
    public int getProductType() {
        return productType;
    }

    /**
     * 设置product type, 定义在{@link ProductTypeCode}中
     * 
     * @param productTye
     * @return
     */
    public GetOrderParam setProductType(int productTye) {
        this.productType = productTye;
        return this;
    }

    /**
     * 添加优惠券
     * 
     * @param id 优惠券id
     * @return
     */
    public GetOrderParam addCounponId(long id) {
        couponIds.add(Long.valueOf(id));
        return this;
    }

    /**
     * 存储SubObject参数
     * 
     * @param key
     * @param value
     * @return
     */
    public GetOrderParam putSubObj(String key, String value) {
        subObjMap.put(key, value);
        return this;
    }

    /**
     * 返回与后台约定的product id, 定义在{@link ProductTypeCode}中
     * 
     * @see ProductTypeCode
     * @return
     */
    public int getProductId() {
        return productId;
    }

    /**
     * 设置product id, 定义在{@link ProductTypeCode}中
     * 
     * @param productId
     * @return
     */
    public GetOrderParam setProductId(int productId) {
        this.productId = productId;
        return this;
    }

    /**
     * 获取key对应的SubObj参数
     * 
     * @param key
     * @return
     */
    public String getSubObj(String key) {
        return subObjMap.get(key);
    }

    protected String generateLocalSign() {
        return MD5.toMD5(getTimestamp() + getDevNo() + String.valueOf(priceInCents) + SECRITY);
    }

    /**
     * 将所有字段放在Map中返回
     * 
     * @return
     */
    public HashMap<String, String> toResultUI() {
        HashMap<String, String> jsonMap = new HashMap<String, String>();
        jsonMap.put("dev_no", devNo);
        jsonMap.put("timestamp", timestamp);
        jsonMap.put("local_sign", localSign);
        jsonMap.put("order_no", orderNo);
        jsonMap.put("channel_no", channelNo);
        jsonMap.put("product_type", String.valueOf(productType));
        jsonMap.put("product_id", String.valueOf(productId));
        jsonMap.put("coupon_ids", concactCouponId());
        jsonMap.putAll(subObjMap);
        jsonMap.putAll(uiMap);
        return jsonMap;
    }

    /**
     * 按照与后台约定的格式将所有参数拼接成String
     * 
     * @return
     */
    public String toQueryString() {
        if (0 >= priceInCents) {
            throw new IllegalArgumentException(
                    "pay price is less than 0. forgot call setPriceInCents?");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("dev_no=");
        sb.append(getDevNo());
        sb.append("&timestamp=");
        sb.append(getTimestamp());
        sb.append("&local_sign=");
        sb.append(getLocalSign());
        sb.append("&order_no=");
        sb.append(orderNo);
        sb.append("&channel_no=");
        sb.append(getChannelNo());
        String couponStr = concactCouponId();
        if (null != couponStr) {
            sb.append("&coupon_ids=");
            sb.append(couponStr);
        }
        sb.append("&product_type=");
        sb.append(productType);
        sb.append("&product_id=");
        sb.append(productId);
        sb.append("&pay_price=");
        sb.append(String.valueOf(priceInCents));

        String subObStr = concactSubObjMap();
        if (null != subObStr) {
            sb.append(subObStr);
        }
        return sb.toString();
    }

    private String concactCouponId() {
        if (couponIds.size() == 0) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0, max = couponIds.size(); i < max; i++) {
            sb.append(couponIds.get(i));
            if (i < (max - 1)) {
                sb.append(',');
            }
        }

        return sb.toString();
    }

    /**
     * 在UI表中存入字段. UI Map内的字段用于构建支付结果页{@link PaymentResultActivity}, 不会发送给服务器.
     * 
     * @param key
     * @param value
     * @return
     */
    public GetOrderParam putUIPair(String key, String value) {
        uiMap.put(key, value);
        return this;
    }

    /**
     * 获取UI Map
     * 
     * @return
     */
    public Map<String, String> getUIMap() {
        return uiMap;
    }

    private String concactSubObjMap() {
        if (subObjMap.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append('&');
        int counter = 0, max = subObjMap.size();
        for (Entry<String, String> entry : subObjMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append("sub_obj[\"");
            sb.append(key);
            sb.append("\"]=");
            sb.append(value);
            if (counter++ < (max - 1)) {
                sb.append('&');
            }
        }

        return sb.toString();
    }

    public Map<String, String> getEncodedUIMap() {
        HashMap<String, String> result = new HashMap<String, String>(uiMap.size());
        for (Entry<String, String> entry : uiMap.entrySet()) {
            try {
                result.put(entry.getKey(), URLEncoder.encode(entry.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
            }
        }
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(devNo);
        dest.writeString(timestamp);
        dest.writeString(localSign);
        dest.writeString(orderNo);
        dest.writeString(channelNo);
        dest.writeList(couponIds);
        dest.writeInt(productId);
        dest.writeInt(productType);
        dest.writeMap(subObjMap);
        dest.writeMap(uiMap);
    }

    public static final Creator<GetOrderParam> CREATOR = new Creator<GetOrderParam>() {
        @Override
        public GetOrderParam[] newArray(int size) {
            return new GetOrderParam[size];
        }

        @Override
        public GetOrderParam createFromParcel(Parcel source) {
            return new GetOrderParam(source);
        }
    };

    public void setSubObjMap(Map<String, String> map) {
        this.subObjMap=map;
    }
}
