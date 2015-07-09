package so.contacts.hub.net;

import java.util.Map;
import java.util.Map.Entry;

import so.contacts.hub.payment.data.ProductTypeCode;
import so.contacts.hub.util.MD5;

public class PTDemoRequest extends BaseRequestData {

    private static final String SECRITY = "kksd%sj*77";
    /**
     * 订单号. 去后台创建前一般都为""
     */
    private String orderNo = "";
    
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
    
    @Override
    protected void setParams(Map<String, String> param) {
        setParam("product_id", String.valueOf(getProductId()));
        setParam("product_type", String.valueOf(getProductType()));
        setParam("pay_price", String.valueOf(getPriceInCents()));
        concactSubObjMap();
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getProductType() {
        return productType;
    }

    public void setProductType(int productType) {
        this.productType = productType;
    }

    public int getPriceInCents() {
        return priceInCents;
    }

    public void setPriceInCents(int priceInCents) {
        this.priceInCents = priceInCents;
    }

    public Map<String, String> getSubObjMap() {
        return subObjMap;
    }

    public void setSubObjMap(Map<String, String> subObjMap) {
        this.subObjMap = subObjMap;
    }

    private void concactSubObjMap() {
        if (subObjMap == null || subObjMap.isEmpty()) {
            return;
        }

        for (Entry<String, String> entry : subObjMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            StringBuilder sb = new StringBuilder();
            sb.append("sub_obj[\"");
            sb.append(key);
            sb.append("\"]");
            setParam(sb.toString(), value);
        }

    }
    
    @Override
    public String getLocalSign() {
        return generateLocalSign();
    }
    
    protected String generateLocalSign() {
        return MD5.toMD5(getTimestamp() + getDev_no() + String.valueOf(priceInCents) + SECRITY);
    }
}
