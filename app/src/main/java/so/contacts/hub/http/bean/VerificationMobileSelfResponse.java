package so.contacts.hub.http.bean;

public class VerificationMobileSelfResponse extends BaseResponseData {
	public String token; // [String][not null][令牌]
	public int is_new_user;// [int][not null][是否新用户]

}
