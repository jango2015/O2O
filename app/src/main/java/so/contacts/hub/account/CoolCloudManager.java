package so.contacts.hub.account;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.http.bean.RelateUserResponse;
import so.contacts.hub.util.LogUtil;
import android.content.Context;
import android.os.Bundle;

import com.coolcloud.uac.android.api.ErrInfo;
import com.coolcloud.uac.android.api.Request.OnResponseListener;
import com.coolcloud.uac.android.api.Token;
import com.coolcloud.uac.android.api.auth.Account;
import com.coolcloud.uac.android.api.auth.OAuth2.OnAuthListener;
import com.coolcloud.uac.android.api.auth.OAuth2Future;
import com.coolcloud.uac.android.api.internal.Coolcloud2;
import com.coolcloud.uac.android.common.Params;

/**
 * 酷云账号统一管理类
 * 
 * @author putao_lhq
 * @version 2014年9月30日
 */
public class CoolCloudManager implements IThirdAccount{

	public static final String TAG = "PutaoAccount";
	private static final String COOL_ID = "1010032";  //APP ID
	private static final String COOL_KEY = "22f96c498459948b626db4cbab249322"; //APP KEY
	private static CoolCloudManager sInstance;

	private Coolcloud2 coolcloud;
	private Bundle userInfo; //用户信息
	
	private AccountInfo accountInfo;//鉴权账户信息

	private CoolCloudManager() {
		coolcloud = Coolcloud2.createInstance(ContactsApp.getInstance(),
				COOL_ID, COOL_KEY);
	}
	
	public static synchronized CoolCloudManager getInstance() {
		if (null == sInstance) {
			sInstance = new CoolCloudManager();
		}
		return sInstance;
	}
	

	/**
	 * 判断令牌是否有效
	 * @return 有效返回true,此时已授权登录,否则返回false,未授权登录
	 */
	@Override
	public boolean isLogin() {
		Token token = coolcloud.getToken();
		final boolean tokenValid = token.isTokenValid();
		LogUtil.v(TAG, "token valid: " + tokenValid);
		return tokenValid;
	}

	/**
	 * 账户登出,只推出授权令牌,并不退出酷云账号
	 * @param listener
	 */
	public void logout(OnAuthListener listener) {
		coolcloud.logout(ContactsApp.getInstance(), listener);
	}

	/**
	 * 获取openId,该方法只能在线程中执行。
	 * @return
	 */
	private String getOpenId() {
		OAuth2Future<Account> future = coolcloud.getDefaultAccount(ContactsApp.getContext(), null);
		if (future == null) {
			LogUtil.e(TAG, "getOpenId fail");
			return null;
		} 
		Account account = future.getResult();
		if (null == account) {
			LogUtil.e(TAG, "get openid fail, error code: " + future.getError());
			return null;
		}
		return account.getOpenId();
	}
	
	/**
	 * 获取用户资料，必须在线程中进行
	 */
	private void requestUserInfo() {
		coolcloud.request("GET", Params.API_GETUSERINFO, null, new OnResponseListener() {
			
			@Override
			public void onResponse(Object object) {
				if (null == object) {
					LogUtil.e(TAG, "get userinfo error");
					return;
				}
				userInfo = (Bundle) object;
				LogUtil.d(TAG, "find user info: " + userInfo);
				if (userInfo != null) {
					accountInfo = new AccountInfo();
					accountInfo.setName(userInfo.getString(Params.USERNAME));
					accountInfo.setUid(userInfo.getString(Params.UID));
					accountInfo.setNickname(userInfo.getString(Params.NICKNAME));
					accountInfo.setAvatar_url(userInfo.getString(Params.AVATAR_URL));
					accountInfo.setAvatar_hd_url(userInfo.getString(Params.AVATAR_HD_URL));
					// modify by putao_lhq 2014年12月4日 for merge code error start
					//accountInfo.setMobile("mobile");
					accountInfo.setMobile(userInfo.getString("mobile"));
					// modify by putao_lhq 2014年12月4日 for merge code error end
				}
			}
			
			@Override
			public void onError(ErrInfo error) {
				LogUtil.e(TAG, "get userinfo error: " + error);
			}
		});
	}
	
	@Override
	public String getUid(boolean silent) {
		//如果为后台静默登录，并且不支持单点登录，那么不用三方账号进行后台登录
    	if (silent && !isSSOEnable()) {
    		LogUtil.d(TAG, "the third is sso disable, silent login");
    		return null;
    	}
		ExecutorService exec = Executors.newFixedThreadPool(1);
    	Callable<String> call = new Callable<String>() {
			
			@Override
			public String call() throws Exception {
				String openId = getOpenId();
				LogUtil.d(TAG, "getUid-->openId: " + openId);
				return openId;
			}
		};
		Future<String> task = exec.submit(call);
		try {
			String uid = task.get();
			LogUtil.d(TAG, "cool->getUid->uid: " + uid);
			return uid;
		} catch (InterruptedException e) {
			LogUtil.e(TAG, "silent getUid interrupt");
			e.printStackTrace();
		} catch (ExecutionException e) {
			LogUtil.e(TAG, "silent getUid ExecutionException");
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public AccountInfo getAccountInfo() {
		ExecutorService exec = Executors.newFixedThreadPool(1);
    	Callable<String> call = new Callable<String>() {
			
			@Override
			public String call() throws Exception {
				requestUserInfo();
				return null;
			}
		};
		Future<String> task = exec.submit(call);
		try {
			task.get();
		} catch (InterruptedException e) {
			LogUtil.e(TAG, "silent getUid interrupt");
			e.printStackTrace();
		} catch (ExecutionException e) {
			LogUtil.e(TAG, "silent getUid ExecutionException");
			e.printStackTrace();
		}
		return accountInfo;
	}

	@Override
	public int getSource() {
		return RelateUserResponse.SOURCE_FACTORY;
	}

	@Override
	public int getType() {
		return RelateUserResponse.TYPE_FACTORY;
	}

	@Override
	public boolean isSSOEnable() {
		return coolcloud.isSSOEnabled();
	}
	
	@Override
	public void showLoginUI(Context context, final IAccCallback cb) {
		if (cb == null) {
			throw new RuntimeException("cb can't be null");
		}
		coolcloud.login(ContactsApp.getContext(), null, new OnAuthListener() {
            
            @Override
            public void onError(ErrInfo errInfo) {
                LogUtil.d(TAG, "login coolcloud error: " + errInfo.getError() + " ,error msg: " + errInfo.getDetail() + " ," + errInfo.getMessage());
                cb.onFail(IAccCallback.LOGIN_FAILED_CODE_SERVER_EXCEPTION);
            }
            
            @Override
            public void onDone(Object arg0) {
                cb.onSuccess();
            }
            
            @Override
            public void onCancel() {
                cb.onCancel();
            }
        });
	}
}
