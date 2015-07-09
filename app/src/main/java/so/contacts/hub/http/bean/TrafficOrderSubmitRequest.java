package so.contacts.hub.http.bean;

public class TrafficOrderSubmitRequest {
    private String order_id;
    
    private String verify_code;

    public TrafficOrderSubmitRequest(String order_id, String verify_code) {
        super();
        this.order_id = order_id;
        this.verify_code = verify_code;
    }

    public TrafficOrderSubmitRequest() {
        super();
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getVerify_code() {
        return verify_code;
    }

    public void setVerify_code(String verify_code) {
        this.verify_code = verify_code;
    }
    
    
}
