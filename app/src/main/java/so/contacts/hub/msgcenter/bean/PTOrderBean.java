
package so.contacts.hub.msgcenter.bean;

import so.contacts.hub.msgcenter.MsgCenterConfig.Product;
import so.contacts.hub.payment.data.ResultCode;

import java.io.Serializable;

import so.contacts.hub.msgcenter.MsgCenterConfig.Product;

/**
 * 消息中心数据接口
 * 
 * @author putao_lhq
 */
public class PTOrderBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title; // 订单主题

    private String status; // 订单状态,非订单业务不可使用此字段

    private int status_code; // 订单状态码

    private int price; // 订单价格,单位分

    private long m_time; // 订单时间戳

    private String order_no; // 订单号

    private int product_type; // 业务类型

    private int product_id; // 业务产品id
    
    private int payment_type;//支付方式,详见PaymentDesc
    
    private int view_status;//查看状态,0代表新消息,1代表旧消息,2代表不提醒,对应提醒的删除

    private String expand; // 扩展参数
    
//    private PTMessageBean messageBean;//此字段不保存在数据库中,只在getNotifyView的时候有值
    private int entry;//1表示从提醒进入.此字段不保存在数据库中,只在getNotifyView的时候有值
    private String coupon_ids;//优惠券列表
    public String getCoupon_ids() {
        return coupon_ids;
    }

    public void setCoupon_ids(String coupon_ids) {
        this.coupon_ids = coupon_ids;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getStatus_code() {
        return status_code;
    }

    public void setStatus_code(int status_code) {
        this.status_code = status_code;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public long getM_time() {
        return m_time;
    }

    public void setM_time(long m_time) {
        this.m_time = m_time;
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

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public String getExpand() {
        return expand;
    }

    public void setExpand(String expand) {
        this.expand = expand;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }
    
    public int getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(int payment_type) {
        this.payment_type = payment_type;
    }

    public int getView_status() {
        return view_status;
    }

    public void setView_status(int view_status) {
        this.view_status = view_status;
    }

    @Override
    public String toString() {
        return "PTOrderBean [title=" + title + ", status=" + status + ", status_code="
                + status_code + ", price=" + price + ", m_tiem=" + m_time + ", order_no="
                + order_no + ", product_type=" + product_type + ", product_id=" + product_id
                + ", payment_type=" + payment_type + ", view_status=" + view_status + ", expand="
                + expand + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((expand == null) ? 0 : expand.hashCode());
        result = prime * result + (int)(m_time ^ (m_time >>> 32));
        result = prime * result + ((order_no == null) ? 0 : order_no.hashCode());
        result = prime * result + payment_type;
        result = prime * result + price;
        result = prime * result + product_id;
        result = prime * result + product_type;
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + status_code;
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + view_status;
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
        PTOrderBean other = (PTOrderBean)obj;
        if (expand == null) {
            if (other.expand != null)
                return false;
        } else if (!expand.equals(other.expand))
            return false;
        if (m_time != other.m_time)
            return false;
        if (order_no == null) {
            if (other.order_no != null)
                return false;
        } else if (!order_no.equals(other.order_no))
            return false;
        if (payment_type != other.payment_type)
            return false;
        if (price != other.price)
            return false;
        if (product_id != other.product_id)
            return false;
        if (product_type != other.product_type)
            return false;
        if (status == null) {
            if (other.status != null)
                return false;
        } else if (!status.equals(other.status))
            return false;
        if (status_code != other.status_code)
            return false;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        if (view_status != other.view_status)
            return false;
        return true;
    }

//    public PTMessageBean getMessageBean() {
//        return messageBean;
//    }
//
//    public void setMessageBean(PTMessageBean messageBean) {
//        this.messageBean = messageBean;
//    }

    public int getEntry() {
        return entry;
    }

    public void setEntry(int entry) {
        this.entry = entry;
    }
    
    /**
     * 获取产品类型枚举对象
     * 
     * @return
     */
    public Product getProduct() {
        return Product.getProduct(product_type);
    }
}
