package so.contacts.hub.http.bean;

import java.util.List;

import so.contacts.hub.core.Config;

public class PostUserInfoRequest extends BaseRequestData<PostUserInfoResponse> {
	public String name;// [String][null able][名字]
	public String avatar;// [String][null able][头像]
    public String addr;// [String][null able][地址]
    public String company;// [String][null able][公司]
	public String school;// [String][null able][学校]
	public String job_title;//[String][null able][职位]
    public String website;//[String][null able][网址]
	public String birthday;// [String][null able][生日] 2012-11-11
	public String email;// [String][null able][邮件]
	public String remark;// [String][null able][个人描述,最大长度200]
	public List<String> tags;// [List<String>][null able][个人标签]
	
	public List<UserPhoneCardDomain> upcd_list;//[List<UserPhoneCardDomain>][null able][当前生效名片号码列表] 

    public PostUserInfoRequest() {
        super("10010");
    }
	
//    public PostUserInfoRequest(String name, String school, String company,
//            String birthday, String email, String addr, String avatar,
//            String remark, List<String> tags, List<UserPhoneCardDomain> upcd_list) {
//        super("10010");
//        this.name = name;
//        this.school = school;
//        this.company = company;
//        this.birthday = birthday;
//        this.email = email;
//        this.addr = addr;
//        this.avatar = avatar;
//        this.remark = remark;
//        this.tags = tags;
//    }

	@Override
	protected PostUserInfoResponse fromJson(String json) {
		return Config.mGson.fromJson(json, PostUserInfoResponse.class);
	}

	@Override
	protected PostUserInfoResponse getNewInstance() {
		return new PostUserInfoResponse();
	}

}
