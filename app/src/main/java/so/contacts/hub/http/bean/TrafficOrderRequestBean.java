package so.contacts.hub.http.bean;

public class TrafficOrderRequestBean {
    private String phone;
    
    private String product_code;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProduct_code() {
        return product_code;
    }

    public void setProduct_code(String product_code) {
        this.product_code = product_code;
    }

    public TrafficOrderRequestBean(String phone, String product_code) {
        super();
        this.phone = phone;
        this.product_code = product_code;
    }

    public TrafficOrderRequestBean() {
        super();
    }

    @Override
    public String toString() {
        return "PhoneTrafficResponseBean [phone=" + phone + ", product_code=" + product_code + "]";
    }
    
}
