package so.contacts.hub.http.bean;

import java.util.List;


public class PostUserInfoResponse extends BaseResponseData {
    public List<UserPhoneCardDomain> upcd_list;//[List<UserPhoneCardDomain>][null able][目前数据库中的名片号码列表]
}
