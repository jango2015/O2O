package so.contacts.hub.http.bean;

import so.contacts.hub.core.Config;

/**
 * 退出登录接口
 * @author hyl 2014-9-19
 */
public class UserLogoutRequest extends BaseRequestData<UserLogoutResponse> {

//    public String pt_token;//[String][not null][token]
    
    public UserLogoutRequest(String token) {
        super("130005");
//        this.pt_token = token;
    }

    @Override
    protected UserLogoutResponse getNewInstance() {
        return new UserLogoutResponse();
    }

    @Override
    protected UserLogoutResponse fromJson(String json) throws Throwable {
        return Config.mGson.fromJson(json, UserLogoutResponse.class);
    }
}
