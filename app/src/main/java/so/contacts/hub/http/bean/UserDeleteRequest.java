package so.contacts.hub.http.bean;

import so.contacts.hub.core.Config;

/**
 * 注销用户接口
 * @author hyl 2014-9-19
 */
public class UserDeleteRequest extends BaseRequestData<UserDeleteResponse> {

//    public String pt_token;//[String][not null][token]
    
    public UserDeleteRequest(String token) {
        super("130003");
//        this.pt_token = token;
    }

    @Override
    protected UserDeleteResponse getNewInstance() {
        return new UserDeleteResponse();
    }

    @Override
    protected UserDeleteResponse fromJson(String json) throws Throwable {
        return Config.mGson.fromJson(json, UserDeleteResponse.class);
    }
}
