package so.contacts.hub.http.bean;

import java.util.List;

/**
 * 设备注册接口 Response
 * @author hyl 2014-9-19
 */
public class DeviceUserRegisterResponse extends BaseResponseData {

    private String pt_uid;//[String][not null][用户id]
    private String pt_token;//[String][not null][用户登录标识]
    private List<RelateUserResponse> relateUsers;//[List<RelateUserResponse][not null][凭证列表]
    
}
