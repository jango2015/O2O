package so.contacts.hub.http.bean;

public class TrafficOrderResponseBean {
    private String order_id;

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    @Override
    public String toString() {
        return "PhoneTrafficRequestBean [order_id=" + order_id + "]";
    }
    
    public TrafficOrderResponseBean(String order_id) {
        super();
        this.order_id = order_id;
    }

    public TrafficOrderResponseBean() {
        super();
    }
    
    
    
}
