package so.contacts.hub.core;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.businessbean.AuthMobiles;
import so.contacts.hub.http.bean.BaseResponseData.PublicSnsInfo;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.yulong.android.contacts.discover.R;

public class User {
	public static final int WEIBO_COUNT = 10;
	
	public static final int PUBLIC = -1;
	public static final int CONTACT = 0;// 只看通讯录
	public static final int SINA = 1;
	public static final int TENCENT = 2;
	public static final int RENREN = 3;
	public static final int WEIXIN = 4;
	public static final int MY_WEIBO = 5;
	
	public static final int ALL = 101;

	// 用户数据配置项
	static final String NAME = "name";

	static final String SINA_SNS_UID = "sina_sns_uid";
	static final String SINA_SNS_ASSESS_TOKEN = "sina_sns_assess_token";
	static final String SINA_SNS_TOKEN_EXPIRED = "sina_sns_token_expired";
	static final String SINA_SNS_AVATAR = "sina_sns_avatar";
	static final String SINA_SNS_NAME = "sina_sns_name";
    static final String SINA_MATCH_CONFIG = "sina_match_config";

	static final String TENCENT_SNS_UID = "tencent_sns_uid";
	static final String TENCENT_SNS_ASSESS_TOKEN = "tencent_sns_assess_token";
	static final String TENCENT_SNS_TOKEN_EXPIRED = "tencent_sns_token_expired";
	static final String TENCENT_SNS_AVATAR = "tencent_sns_avatar";
	static final String TENCENT_SNS_NAME = "tencent_sns_name";
	static final String TENCENT_OPEN_ID = "tencent_open_id";

	static final String RENREN_SNS_UID = "renren_sns_uid";
	static final String RENREN_SNS_ASSESS_TOKEN = "renren_sns_assess_token";
	static final String RENREN_SNS_TOKEN_EXPIRED = "renren_sns_token_expired";
	static final String RENREN_SNS_AVATAR = "renren_sns_avatar";
	static final String RENREN_SNS_NAME = "renren_sns_name";

	static final String SNS_DEFAULT = "sns_default";
	static final String SNS_DEFAULT_SELECTOR = "sns_default_selector";
	static final String SNS_DEFAULT_AVATAR = "sns_default_avatar";
	static final String SEND_WEIBO_TENCENT = "send_weibo_tencent";
	static final String SEND_WEIBO_SINA = "send_weibo_sina";
	static final String SEND_WEIBO_RENREN = "send_weibo_renren";
	static final String SEND_WEIBO_WEIXIN = "send_weibo_weixin";

	static final String SNS_TYPE = "sns_type";
	static final String TOKEN = "token";
	static final String MCODE = "m_code";
	static final String MOBILE = "mobile";

	static final String VERIFICATION_CODE = "verification_code";

	static final String MOBILE_NO_VERSION = "mobile_no_version";
	static final String U_ID = "u_id";
	static final String COMPANY = "company";
	static final String JOB_TITLE = "job_title";
	static final String BIRTHDAY = "birthday";
	static final String ADDRESS = "address";
	static final String EMAIL = "email";
	static final String SCHOOL = "school";
	static final String REMARK = "remark";
	static final String WEBSITE = "website";
	static final String FRIEND_UPDATE_NUMBER = "friend_update_number";
    static final String NEW_RECOMMEND_RELATIONSHIP = "new_recommend_relationship";
    static final String NEW_MATERIAL_UPDATE = "new_material_update";
    static final String NEW_AT_ME_COUNT = "new_at_me_count";
    static final String NEW_CONTACTS_FEED = "new_contacts_feed";
	static final String PUBLIC_SNS_VERSION = "public_sns_version";
	static final String PUBLIC_SINA_TOKEN = "public_sina_token";
	static final String PUBLIC_TENCENT_TOKEN = "public_tencent_token";

	static final String USERWEIBO = "userweibo";
	static final String USER_SNS_TYPE = "userSnsType";

	static final String SHOWSETTING = "showsetting";
	static final String AVATAR = "avatar";
	static final String MOBILES = "mobiles";
	static final String USER_MOBILES = "user_mobiles";
	static final String PICURL = "picurl";
	static final String IS_ENABLE_CONFIG = "is_enable_config";
	static final String HAS_PASSWORD = "has_password";
	private SharedPreferences preferences;

	public String name;// 名字
	public String avatar;// [头像地址信息]

	public String sina_sns_uid;// 新浪微博id
	public String sina_sns_assess_token;// 新浪微博Token
	public boolean sina_sns_token_expired;// 新浪微博Token是否有效
	public String sina_sns_avatar;// 新浪微博头像
	public String sina_sns_name;// 新浪微博名字
    public String sina_match_config;// 新浪微博匹配配置

	public String tencent_sns_uid;// 腾讯微博id
	public String tencent_sns_assess_token;// 腾讯微博Token
	public boolean tencent_sns_token_expired;// 腾讯新浪微博Token是否有效
	public String tencent_sns_avatar;// 腾讯微博头像
	public String tencent_sns_name;// 腾讯微博名字
	public String tencent_open_id;// 腾讯用户统一标识，可以唯一标识一个用户

	public String renren_sns_uid;// 人人网id
	public String renren_sns_assess_token;// 人人网Token
	public boolean renren_sns_token_expired;// 人人网Token是否有效
	public String renren_sns_avatar;// 人人网头像
	public String renren_sns_name;// 人人网名字

	public int sns_default;
	public int sns_default_selector;
	public String sns_default_avatar;
	public boolean send_weibo_tencent;
	public boolean send_weibo_sina;
	public boolean send_weibo_renren;
	public boolean send_weibo_weixin;

	public String mobile_no_version;// 号码归属地版本
	public String token;// 云端Token
	public String mcode;// 机器码
	public String mobile;// 验证的号码
	public String verification_code;// 验证码

	public long uid;// 云端ID
	private String company;// 公司
	public String job_title;// 职位
	private long birthday;// 生日
	private String address;// 地点
	private String email;// email
	private String school;// 学校
	private String remark;// 描述
	public String website;// 网站 
	public String public_sns_version;// 公用账号版本
	public String public_sina_token;// 公用sina token
	public String public_tencent_token;// 公用tencnent token
	public int friend_update_number = -1;// 好友更新数
	public String new_recommend_relationship;// 最新的推荐关联
	public String new_material_update;// 最新的资料更新
	public int new_at_me_count;// 最新的提及我的数量
	public String new_contacts_feed;// 最新的通讯录动态
	public boolean need_guide; // 是否需要显示引导图
	public int last_guide_version;// 上一次显示引导图的版本 用于升级后显示引导图
	public long lastweibotime; // 上一次查看微博时最新微博的时间
	public String userweibo; // 用户发送的微博 草稿箱
	public int userSnsType;// 用户发送的微博 社交网络类型

	public String showsetting; // 选择要显示的联系人 以json串的形式保存,释意见ShowContactsBean
	/**
	 * 用户验证过的手机号码JSON串*
	 */
	public String mobiles; // 用户验证过的手机号码JSON串
	public String user_mobiles; // 用户号码卡片列表
	public String picurl; // 上次发微博失败图片的URL
	public int is_enable_config;// 启用通讯录匹配[是否启用通讯录匹配标志,0:未处理,1:使用，2：不使用]
	
    public int has_password;//0是没有密码，1是有密码

	public User(SharedPreferences preferences) {
		this.preferences = preferences;
		name = preferences.getString(NAME, "");
		company = preferences.getString(COMPANY, "");
		birthday = preferences.getLong(BIRTHDAY, -1l);

		sina_sns_uid = preferences.getString(SINA_SNS_UID, "");
		sina_sns_assess_token = preferences
				.getString(SINA_SNS_ASSESS_TOKEN, "");
		sina_sns_token_expired = preferences.getBoolean(SINA_SNS_TOKEN_EXPIRED,
				true);
		sina_sns_avatar = preferences.getString(SINA_SNS_AVATAR, "");
		sina_sns_name = preferences.getString(SINA_SNS_NAME, "");
        sina_match_config = preferences.getString(SINA_MATCH_CONFIG, "");

		tencent_sns_uid = preferences.getString(TENCENT_SNS_UID, "");
		tencent_sns_assess_token = preferences.getString(
				TENCENT_SNS_ASSESS_TOKEN, "");
		tencent_sns_token_expired = preferences.getBoolean(
				TENCENT_SNS_TOKEN_EXPIRED, true);
		tencent_sns_avatar = preferences.getString(TENCENT_SNS_AVATAR, "");
		tencent_sns_name = preferences.getString(TENCENT_SNS_NAME, "");
		tencent_open_id = preferences.getString(TENCENT_OPEN_ID, "");

		renren_sns_uid = preferences.getString(RENREN_SNS_UID, "");
		renren_sns_assess_token = preferences.getString(
				RENREN_SNS_ASSESS_TOKEN, "");
		renren_sns_token_expired = preferences.getBoolean(
				RENREN_SNS_TOKEN_EXPIRED, true);
		renren_sns_avatar = preferences.getString(RENREN_SNS_AVATAR, "");
		renren_sns_name = preferences.getString(RENREN_SNS_NAME, "");

		sns_default = preferences.getInt(SNS_DEFAULT, 1);
		send_weibo_tencent = preferences.getBoolean(SEND_WEIBO_TENCENT, true);
		send_weibo_sina = preferences.getBoolean(SEND_WEIBO_SINA, true);
		send_weibo_renren = preferences.getBoolean(SEND_WEIBO_RENREN, true);
		send_weibo_weixin = preferences.getBoolean(SEND_WEIBO_WEIXIN, true);

		mobile_no_version = preferences.getString(MOBILE_NO_VERSION, "");
		token = preferences.getString(TOKEN, "");
		mcode = preferences.getString(MCODE, "");
		mobile = preferences.getString(MOBILE, "");
		verification_code = preferences.getString(VERIFICATION_CODE, "");

		uid = preferences.getLong(U_ID, -1l);
		address = preferences.getString(ADDRESS, "");
		email = preferences.getString(EMAIL, "");
		school = preferences.getString(SCHOOL, "");
		remark = preferences.getString(REMARK, "");
		friend_update_number = preferences.getInt(FRIEND_UPDATE_NUMBER, -1);
        new_recommend_relationship = preferences.getString(NEW_RECOMMEND_RELATIONSHIP, "");
        new_material_update = preferences.getString(NEW_MATERIAL_UPDATE, "");
        new_at_me_count = preferences.getInt(NEW_AT_ME_COUNT, -1);
        new_contacts_feed = preferences.getString(NEW_CONTACTS_FEED, "");
		public_sns_version = preferences.getString(PUBLIC_SNS_VERSION, "");
		public_sina_token = preferences.getString(PUBLIC_SINA_TOKEN, "");
		public_tencent_token = preferences.getString(PUBLIC_TENCENT_TOKEN, "");
		userweibo = preferences.getString(USERWEIBO, "");

		showsetting = preferences.getString(SHOWSETTING, "");
		avatar = preferences.getString(AVATAR, "");
		mobiles = preferences.getString(MOBILES, "");
		user_mobiles = preferences.getString(USER_MOBILES, "");
		picurl = preferences.getString(PICURL, "");
		is_enable_config = preferences.getInt(IS_ENABLE_CONFIG, 0);
		//has_password  safeng add by haocheng 2014-1-3 
		has_password = preferences.getInt(HAS_PASSWORD, 0);
	}
	
	public void save() {
		Editor editor = preferences.edit();
		editor.putString(NAME, name);
		editor.putString(COMPANY, company);
		editor.putLong(BIRTHDAY, birthday);

		editor.putString(SINA_SNS_UID, sina_sns_uid);
		editor.putString(SINA_SNS_ASSESS_TOKEN, sina_sns_assess_token);
		editor.putBoolean(SINA_SNS_TOKEN_EXPIRED, sina_sns_token_expired);
		editor.putString(SINA_SNS_AVATAR, sina_sns_avatar);
		editor.putString(SINA_SNS_NAME, sina_sns_name);
        editor.putString(SINA_MATCH_CONFIG, sina_match_config);

		editor.putString(TENCENT_SNS_UID, tencent_sns_uid);
		editor.putString(TENCENT_SNS_ASSESS_TOKEN, tencent_sns_assess_token);
		editor.putBoolean(TENCENT_SNS_TOKEN_EXPIRED, tencent_sns_token_expired);
		editor.putString(TENCENT_SNS_AVATAR, tencent_sns_avatar);
		editor.putString(TENCENT_SNS_NAME, tencent_sns_name);
		editor.putString(TENCENT_OPEN_ID, tencent_open_id);

		editor.putString(RENREN_SNS_UID, renren_sns_uid);
		editor.putString(RENREN_SNS_ASSESS_TOKEN, renren_sns_assess_token);
		editor.putBoolean(RENREN_SNS_TOKEN_EXPIRED, renren_sns_token_expired);
		editor.putString(RENREN_SNS_AVATAR, renren_sns_avatar);
		editor.putString(RENREN_SNS_NAME, renren_sns_name);

		editor.putInt(SNS_DEFAULT, sns_default);
		editor.putString(SNS_DEFAULT_AVATAR, sns_default_avatar);

		editor.putString(MOBILE_NO_VERSION, mobile_no_version);
		editor.putString(TOKEN, token);
		editor.putString(MCODE, mcode);
		editor.putString(MOBILE, mobile);
		editor.putString(VERIFICATION_CODE, verification_code);

		editor.putLong(U_ID, uid);
		editor.putString(ADDRESS, address);
		editor.putString(EMAIL, email);
		editor.putString(SCHOOL, school);
		editor.putString(REMARK, remark);
		editor.putInt(FRIEND_UPDATE_NUMBER, friend_update_number);
        editor.putString(NEW_RECOMMEND_RELATIONSHIP, new_recommend_relationship);
        editor.putString(NEW_MATERIAL_UPDATE, new_material_update);
        editor.putInt(NEW_AT_ME_COUNT, new_at_me_count);
        editor.putString(NEW_CONTACTS_FEED, new_contacts_feed);
		editor.putString(PUBLIC_SNS_VERSION, public_sns_version);
		editor.putString(PUBLIC_SINA_TOKEN, public_sina_token);
		editor.putString(PUBLIC_TENCENT_TOKEN, public_tencent_token);
		editor.putString(USERWEIBO, userweibo);
		editor.putInt(USER_SNS_TYPE, userSnsType);

		editor.putString(SHOWSETTING, showsetting);
		editor.putString(AVATAR, avatar);
		editor.putString(MOBILES, mobiles);
		editor.putString(USER_MOBILES, user_mobiles);
		editor.putString(PICURL, picurl);
		editor.putInt(IS_ENABLE_CONFIG, is_enable_config);

		//has_password safeng add by haocheng 2014-1-3 
		editor.putInt(HAS_PASSWORD, has_password);
		editor.commit();
	}	
	/**
     * has_password 
     * 2014-1-3 safeng 豪成版本添加 用于判断用户当前是需要设置密码还是修改密码 
     * **/
	
	public boolean isLogin() {
		return false;
	}
	
	public void setToken(String token) {
		this.token = token;
		Editor editor = preferences.edit();
		editor.putString(TOKEN, token);
		editor.commit();
	}
	
	/**
	 * @param user_id
	 */
	public void setUserId(long user_id) {
		uid = user_id;
		preferences.edit().putLong(U_ID, uid).commit();
	}
	
	public void setPublicSns(PublicSnsInfo psi) {/*
		Editor editor = preferences.edit();
		editor.putString(PUBLIC_SNS_VERSION, psi.version);
		for (PublicSnsAccessToken token : psi.psa_list) {

			if (token.sns_id == User.SINA) {
				this.public_sina_token = token.access_token;
				editor.putString(PUBLIC_SINA_TOKEN, this.public_sina_token);
			} else if (token.sns_id == User.TENCENT) {
				this.public_tencent_token = token.access_token;
				editor.putString(PUBLIC_TENCENT_TOKEN,
						this.public_tencent_token);
			}
		}
		editor.commit();

		if (mTencent != null) {
			mTencent.setPublicToken(this.public_tencent_token);
		}

		if (mSina != null) {
			mSina.setPublicToken(this.public_sina_token);
		}
	*/}
	
	public boolean hasAuthMobiles() {
	    return !TextUtils.isEmpty(mobiles);
	}
	
	public AuthMobiles getAuthMobiles() {
	    if (!hasAuthMobiles()) {
	        return new AuthMobiles();
	    }
	    return Config.mGson.fromJson(mobiles, AuthMobiles.class);
	}
	
	public String getSnsDefaultAvatar() {
		if (!TextUtils.isEmpty(sina_sns_avatar)) {
			sns_default_avatar = sina_sns_avatar;
		} else if (!TextUtils.isEmpty(tencent_sns_avatar)) {
			sns_default_avatar = tencent_sns_avatar;
		} else if (!TextUtils.isEmpty(renren_sns_avatar)) {
			sns_default_avatar = renren_sns_avatar;
		} else {
			sns_default_avatar = "";
		}
		return preferences.getString(SNS_DEFAULT_AVATAR, sns_default_avatar);
	}

	public String getToken() {
	       return this.token;
	    }
	
	public String getName() {
        String name = this.name;
        if (TextUtils.isEmpty(name)) {
            name = ContactsApp.getInstance().getResources().getString(R.string.putao_is_me);
        }
        return name;
	}

	public String getAvatar() {
	    String url = null;
        if (!TextUtils.isEmpty(avatar)) {
            url = avatar;
        } else {
            url = getSnsDefaultAvatar();
        }
	    return url;
	}
	
	public boolean isBind() {
		return !TextUtils.isEmpty(sina_sns_assess_token)
				|| !TextUtils.isEmpty(tencent_sns_assess_token)
				|| !TextUtils.isEmpty(renren_sns_assess_token);
	}

	public boolean isBindSina() {
		return !TextUtils.isEmpty(sina_sns_assess_token);
	}

	public boolean isBindTencent() {
		return !TextUtils.isEmpty(tencent_sns_assess_token);
	}

	public boolean isBindRenren() {
		return !TextUtils.isEmpty(renren_sns_assess_token);
	}

	public boolean isBind(final int snsType) {
		switch (snsType) {
		case SINA:
			return !TextUtils.isEmpty(sina_sns_assess_token);
		case TENCENT:
			return !TextUtils.isEmpty(tencent_sns_assess_token);
		case RENREN:
			return !TextUtils.isEmpty(renren_sns_assess_token);
		default:
			return false;
		}
	}

	public boolean isBind(String snsUserId) {
        return !TextUtils.isEmpty(snsUserId) && ((!TextUtils.isEmpty(sina_sns_uid) && snsUserId.equals(sina_sns_uid)) 
                || (!TextUtils.isEmpty(tencent_sns_uid) && snsUserId.equals(tencent_sns_uid)) 
                || (!TextUtils.isEmpty(renren_sns_uid) && snsUserId.equals(renren_sns_uid)));
	}

	public boolean isDefault(final int snsType) {
		return snsType == sns_default;
	}

	public boolean isExpired(final int snsType) {
		boolean expire = false;
		switch (snsType) {
		case SINA:
			expire = !sina_sns_token_expired;
			break;
		case TENCENT:
			expire = !tencent_sns_token_expired;
			break;
		case RENREN:
			expire = !renren_sns_token_expired;
			break;
		}
		return expire;
	}	
}
