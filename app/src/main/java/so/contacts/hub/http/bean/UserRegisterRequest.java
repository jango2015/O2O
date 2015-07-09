package so.contacts.hub.http.bean;

import so.contacts.hub.account.AccountInfo;
import so.contacts.hub.core.Config;
import so.contacts.hub.util.LogUtil;

/**
 * 鉴权凭证注册接口
 * @author hyl 2014-9-19
 */
public class UserRegisterRequest extends BaseRequestData<UserRegisterResponse> {

    public String accName;//[String][not null][帐户名]
    public int  accSource;//[int][not null][帐户来源]
    public int    accType;//[int][not null][帐户类型]
    public String accMsg;    //[String][账号信息] //add by putao_lhq
    
    public UserRegisterRequest(String accName,int accSource,int accType, AccountInfo accMsg) {
        super("130002");
        this.accName = accName;
        this.accSource = accSource;
        this.accType = accType;
        if (accMsg == null) {
        	this.accMsg = "";
        } else {
        	this.accMsg = Config.mGson.toJson(accMsg);
        }
        LogUtil.d("putao_lhq", "accmsg: " + this.accMsg);
    }

    @Override
    protected UserRegisterResponse getNewInstance() {
        return new UserRegisterResponse();
    }
    
    @Override
    protected UserRegisterResponse fromJson(String json) throws Throwable {
        return Config.mGson.fromJson(json, UserRegisterResponse.class);
    }

    
}
