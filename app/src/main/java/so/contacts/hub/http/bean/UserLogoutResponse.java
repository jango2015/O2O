package so.contacts.hub.http.bean;

/**
 * 退出登录接口Response
 * @author hyl 2014-9-19
 */
public class UserLogoutResponse extends BaseResponseData {
    public int isisDelete;//[isDelete][int][not null]
                          //[标识用户是否已被删除,0 表帐户下仍有凭证，没有删除用户.1 表帐户下无凭证，已删除主用户，客户端一定要清除客户端token]
}
