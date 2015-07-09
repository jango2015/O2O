
package so.contacts.hub.ui.yellowpage.bean;

import java.io.Serializable;

public class TelephoneOrderBean implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private int prodid;
    private int prod_price;// 价格以分为单位

    private String mobilenum;

    private String face_value;//面值

    private String attribution;//归属地

    private String order_no;//单号

    private int product_type;

    private long m_tiem; // 订单时间戳

    private long time;//创建时间
    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getFace_value() {
        return face_value;
    }

    public void setFace_value(String face_value) {
        this.face_value = face_value;
    }

    public String getAttribution() {
        return attribution;
    }

    public void setAttribution(String attribution) {
        this.attribution = attribution;
    }
    public int getProdid() {
        return prodid;
    }

    public void setProdid(int prodid) {
        this.prodid = prodid;
    }

    public int getProd_price() {
        return prod_price;
    }

    public void setProd_price(int prod_price) {
        this.prod_price = prod_price;
    }

    public String getMobilenum() {
        return mobilenum;
    }

    public void setMobilenum(String mobilenum) {
        this.mobilenum = mobilenum;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public int getProduct_type() {
        return product_type;
    }

    public void setProduct_type(int product_type) {
        this.product_type = product_type;
    }

    public long getM_tiem() {
        return m_tiem;
    }

    public void setM_tiem(long m_tiem) {
        this.m_tiem = m_tiem;
    }

    @Override
    public String toString() {
        return "TrafficOrderBean [prodid=" + prodid + ", prod_price=" + prod_price + ", mobilenum="
                + mobilenum + ", face_value=" + face_value + ", attribution=" + attribution
                + ", order_no=" + order_no + ", product_type=" + product_type + ", m_tiem="
                + m_tiem + "]";
    }

}
