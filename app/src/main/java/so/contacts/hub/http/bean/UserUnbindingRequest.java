package so.contacts.hub.http.bean;

import so.contacts.hub.core.Config;
/**
 * 解除绑定接口
 * @author hyl 2014-9-19
 */
public class UserUnbindingRequest extends BaseRequestData<UserUnbindingResponse> {

    public String accName;//[String][not null][帐户名]
    public int  accSource;//[int][not null][帐户来源]
    public int    accType;//[int][not null][帐户类型]

    
    public UserUnbindingRequest(String accName,int accSource,int accType) {
        super("130004");
        this.accName = accName;
        this.accSource = accSource;
        this.accType = accType;
    }

    @Override
    protected UserUnbindingResponse getNewInstance() {
        return new UserUnbindingResponse();
    }

    @Override
    protected UserUnbindingResponse fromJson(String json) throws Throwable {
        return Config.mGson.fromJson(json, UserUnbindingResponse.class);
    }
}
