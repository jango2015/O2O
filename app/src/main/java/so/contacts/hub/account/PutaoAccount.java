package so.contacts.hub.account;

import java.util.List;

import com.google.gson.JsonSyntaxException;
import com.yulong.android.contacts.discover.R;

import android.content.Context;
import android.text.TextUtils;
import so.contacts.hub.ContactsApp;
import so.contacts.hub.core.Config;
import so.contacts.hub.core.PTUser;
import so.contacts.hub.http.bean.RelateUserResponse;
import so.contacts.hub.util.ContactsHubUtils;
import so.contacts.hub.util.LogUtil;

/**
 * 葡萄账户对外接口
 * @author putao_lhq
 *
 */
public class PutaoAccount {

	private IPutaoAccount mPutaoAccount;

	public static String TAG = "PutaoAccount";
	
	private static PutaoAccount sInstance;
	
	public static PutaoAccount getInstance() {
		if (sInstance == null) {
		    synchronized(PutaoAccount.class) {
		        if(sInstance == null) {
		            sInstance = new PutaoAccount();
		        }
		    }
		}
		return sInstance;
	}
	
	private PutaoAccount() {
		mPutaoAccount = new PutaoAccountImpl();
	}
	
	/**
	 * 账号登录接口
	 * @param context 上下文
	 * @param accName 登录账号，目前有三种，依托方账号，手机号码， 设备号
	 * @param type 登录方式, 有四种登录方式:</br>
	 * {@link IPutaoAccount#LOGIN_TYPE_BY_SILENT}</br>
	 * {@link IPutaoAccount#LOGIN_TYPE_BY_THIRD}</br>
	 * {@link IPutaoAccount#LOGIN_TYPE_BY_PHONE}</br>
	 * {@link IPutaoAccount#LOGIN_TYPE_BY_DEVICE}</br>
	 * 
	 * @param cb 登录回调接口
	 */
	public void login(Context context, String accName, 
			int type, IAccCallback cb) {
		mPutaoAccount.login(context, accName, type, cb);
	}
	
	/**
	 * 退出登录接口
	 * @param context
	 */
	public void logout(Context context) {
		mPutaoAccount.logout(context);
	}
	
	/**
	 * 获取open Token
	 * @return
	 */
	public String getOpenToken() {
		return mPutaoAccount.getOpenToken();
	}
	
	/**
	 * 后台默认登录接口，执行在线程中处理，回调在主线程！
	 * @param cb
	 */
	public void silentLogin(IAccCallback cb) {
		mPutaoAccount.login(ContactsApp.getInstance(), null, IPutaoAccount.LOGIN_TYPE_BY_SILENT, cb);
	}
	
	/**
	 * 注册账号变更接口
	 * @param l
	 */
	public void addAccChangeListener(IAccChangeListener l) {
		mPutaoAccount.addAccChangeListener(l);
	}
	
    /**
     * 删除账号变更监听器
     * @param l
     */
    public void delAccChangeListener(IAccChangeListener l) {
        mPutaoAccount.delAccChangeListener(l);
    }
	
	/**
	 * 判断账号是否登录
	 * @return
	 */
	public boolean isLogin() {
		return mPutaoAccount.isLogin();
	}
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	public PTUser getPtUser() {
		return mPutaoAccount.getPtUser();
	}
	
	/**
	 * 获取绑定手机号码，目前只是获取酷云绑定手机号
	 * @return
	 */
	public String getBindMobile() {
		PTUser ptUser = getPtUser();
		if (ptUser == null) {
			LogUtil.v(TAG , "getBindMobile fail, ptUser is null");
			return "";
		}
		List<RelateUserResponse> relateUsers = ptUser.getRelateUsers();
		if (relateUsers == null || relateUsers.size() == 0) {
			LogUtil.v(TAG, "getBindMobile fail, relateUser is null");
			return "";
		}
		for (RelateUserResponse relateUser: relateUsers) {
			if (relateUser.accSource != RelateUserResponse.SOURCE_DEVICE &&
					relateUser.accSource != RelateUserResponse.SOURCE_DEVICE) { // BUG:条件重复
				AccountInfo info = null;
				try {
					info = Config.mGson.fromJson(relateUser.accMsg, AccountInfo.class);
				} catch (JsonSyntaxException e) {
					LogUtil.d(TAG, e.getMessage());
					e.printStackTrace();
				}
				if (info != null && !TextUtils.isEmpty(info.getMobile()) && ContactsHubUtils.isTelephoneNumber(info.getMobile())) {
					return info.getMobile();
				}
			}
			if (relateUser.accType == RelateUserResponse.TYPE_PHONE) {
				if(ContactsHubUtils.isTelephoneNumber(relateUser.accName)) {
					return relateUser.accName;
				}
			}
		}
		return "";
	}
	
	/**
	 * 解绑账号
	 * @param accName
	 * @param source
	 * @param type
	 * @param cb
	 */
	public void unbind(String accName, int source, int type, IAccCallback cb) {
		mPutaoAccount.unbind(accName, source, type, cb);
	}
	
	/**
	 * 获取鉴权账户，
	 * @param type 绑定类型:</br>
	 * 平台绑定{@link RelateUserResponse#TYPE_FACTORY}</br>
	 * 手机绑定{@link RelateUserResponse#TYPE_PHONE}</br>
	 * @return
	 */
	public RelateUserResponse getRelateUserResponse(int type) {
		PTUser user = getPtUser();
		if (user == null || user.getRelateUsers() == null || user.getRelateUsers().size() <= 0) {
			return null;
		}
		for (RelateUserResponse relateUser : user.getRelateUsers()) {
			if (relateUser.accType == type) {
				return relateUser;
			}
		}
		return null;
	}
	
	/**
	 * 获取鉴权账号显示名称
	 * @param relateUser
	 * @return
	 */
	public String getDisplayName(RelateUserResponse relateUser) {
		if (null == relateUser) {
			LogUtil.v(TAG, "getDisplayName fail, relateUser is null");
			return null;
		}
		
		AccountInfo info = Config.mGson.fromJson(relateUser.accMsg, AccountInfo.class);
		if (info == null) {
			LogUtil.v(TAG, "getDisplayName fail, relateUser is not find account info");
			return null;
		}
		String displayName = info.getNickname();
		if (TextUtils.isEmpty(displayName)) {
			displayName = info.getMobile();
		}
		LogUtil.v(TAG, "getDisplayName is: " + displayName);
		return displayName;
	}
	
	/**
	 * 获取账号登录错误提示信息
	 * @param context
	 * @param error_code
	 * @return
	 */
	public static String getToastTextError(Context context, int error_code) {
		if (error_code == IAccCallback.LOGIN_FAILED_CODE_CONNECTION_EXCEPTION) {
			return context.getResources().getString(R.string.putao_network_exception);
		} else if (error_code == IAccCallback.LOGIN_FAILED_CODE_HAS_BIND) {
			return context.getResources().getString(R.string.putao_account_exist_cannot_bind);
		} else if (error_code == IAccCallback.LOGIN_FAILED_CODE_SERVER_EXCEPTION) {
			return context.getResources().getString(R.string.putao_server_exception);
		}
		// 增加默认登陆失败的返回 modify by cj 2015-01-04
		return context.getResources().getString(R.string.putao_checking_login_fail);
	}
}
