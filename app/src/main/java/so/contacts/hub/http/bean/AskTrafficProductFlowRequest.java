package so.contacts.hub.http.bean;

import so.contacts.hub.core.Config;

public class AskTrafficProductFlowRequest extends BaseRequestData<AskTrafficProductFlowResponse> {

    private String acc_type;//[String][not null]手机号信息  格式:四川移动
    private String phone;//[String][not null] 手机号码

    
    public AskTrafficProductFlowRequest(String actionCode) {
        super(actionCode);
    }
    
    public AskTrafficProductFlowRequest(){
        super("");
    }

    @Override
    protected AskTrafficProductFlowResponse getNewInstance() {
        return new AskTrafficProductFlowResponse();
    }

    @Override
    protected AskTrafficProductFlowResponse fromJson(String json) throws Throwable {
        return Config.mGson.fromJson(json, AskTrafficProductFlowResponse.class);
    }

    public String getAcc_type() {
        return acc_type;
    }

    public void setAcc_type(String acc_type) {
        this.acc_type = acc_type;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    
}
