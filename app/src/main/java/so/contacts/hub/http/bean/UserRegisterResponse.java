package so.contacts.hub.http.bean;

import java.util.List;

/**
 * 鉴权凭证接口 Response
 * @author hyl 2014-9-19
 */
public class UserRegisterResponse extends BaseResponseData {

    public int registerStatus;//[int][not null][帐户切换状态，0表正常，1表不能切换(已绑定凭证用户尝试绑定到另一个用户上)]
    public String pt_uid;//[String][not null][用户id]
    public String pt_token;//[String][not null][用户登录标识]
    public String open_token;//[String][open token] add by putao_lhq
    public List<RelateUserResponse> relateUsers;//[List<RelateUserResponse][not null][凭证列表]
    
}
