
package so.contacts.hub.http.bean;

import java.io.Serializable;

public class PhoneFeeProductInfo implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private int prod_id;// [int][not null]话费产品id

    private String prod_content;// [String][not null]话费产品面额
    
    private String wx_price;// [String][not null]微信支付价格
    
    private String al_price;//[String][null able]支付宝支付价格
    
    private String prod_delaytimes;//[String][null able]充值处理时间(5分钟，24小时，48小时)

    public int getProd_id() {
        return prod_id;
    }

    public void setProd_id(int prod_id) {
        this.prod_id = prod_id;
    }

    public String getProd_content() {
        return prod_content;
    }

    public void setProd_content(String prod_content) {
        this.prod_content = prod_content;
    }

    public String getWx_price() {
        return wx_price;
    }

    public void setWx_price(String wx_price) {
        this.wx_price = wx_price;
    }

    public String getAl_price() {
        return al_price;
    }

    public void setAl_price(String al_price) {
        this.al_price = al_price;
    }

    public String getProd_delaytimes() {
        return prod_delaytimes;
    }

    public void setProd_delaytimes(String prod_delaytimes) {
        this.prod_delaytimes = prod_delaytimes;
    }



}
