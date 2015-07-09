package so.contacts.hub.http.bean;


public class AskTrafficProductFlowResponse extends BaseResponseData {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    

    private TrafficProductResponseBean data;
    
    private String msg;

    public TrafficProductResponseBean getData() {
        return data;
    }

    public void setData(TrafficProductResponseBean data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
    
}
