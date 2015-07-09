package so.contacts.hub.core;

import java.util.List;

import so.contacts.hub.http.bean.RelateUserResponse;
import so.contacts.hub.http.bean.UserRegisterResponse;

import com.google.gson.Gson;

/**
 * 黄页账户
 * @author hyl 2014-9-19
 */
public class PTUser {
    
    public String pt_uid;//[String][not null][用户id]
    public String pt_token;//[String][not null][用户登录标识]
    private String pt_open_token;//[String][提供给三方授权的open token] //putao_lhq add
    public List<RelateUserResponse> relateUsers;//[List<RelateUserResponse][not null][凭证列表]
   
    public PTUser(String json) {
        UserRegisterResponse registerResponse = new Gson().fromJson(json, UserRegisterResponse.class);
        pt_uid = registerResponse.pt_uid;
        pt_token = registerResponse.pt_token;
        relateUsers = registerResponse.relateUsers;
        pt_open_token = registerResponse.open_token;
    }
    
    public String getPt_uid() {
        return pt_uid;
    }

    public void setPt_uid(String pt_uid) {
        this.pt_uid = pt_uid;
    }

    public String getPt_token() {
        return pt_token;
    }

    public void setPt_token(String pt_token) {
        this.pt_token = pt_token;
    }

    public List<RelateUserResponse> getRelateUsers() {
        return relateUsers;
    }
    public void setRelateUsers(List<RelateUserResponse> relateUsers) {
        this.relateUsers = relateUsers;
    }
    
    /**
     * add by putao_lhq
     * @return
     */
    public String getPt_open_token() {
    	return this.pt_open_token;
    }
    
    @Override
    public String toString() {
    	return "pt_uid:" + pt_uid + ", pt_token:" + pt_token + ",open_token="+pt_open_token+", relateUsers: " + relateUsers;
    }
}
