package so.contacts.hub.http.bean;


public class AskPhoneFeeProductFlowResponse extends BaseResponseData {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    

    private ChargeTelephoneProductResponseBean data;
    
    private String msg;

    public ChargeTelephoneProductResponseBean getData() {
        return data;
    }

    public void setData(ChargeTelephoneProductResponseBean data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
    
}
