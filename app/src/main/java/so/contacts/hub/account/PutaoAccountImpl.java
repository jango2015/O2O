package so.contacts.hub.account;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.core.Config;
import so.contacts.hub.core.PTUser;
import so.contacts.hub.http.bean.DeviceUserRegisterRequest;
import so.contacts.hub.http.bean.DeviceUserRegisterResponse;
import so.contacts.hub.http.bean.RelateUserResponse;
import so.contacts.hub.http.bean.UserRegisterRequest;
import so.contacts.hub.http.bean.UserRegisterResponse;
import so.contacts.hub.http.bean.UserUnbindingRequest;
import so.contacts.hub.http.bean.UserUnbindingResponse;
import so.contacts.hub.msgcenter.PTMessageCenter;
import so.contacts.hub.msgcenter.PTOrderCenter;
import so.contacts.hub.util.ContactsHubUtils;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.UserInfoUtil;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.mdroid.core.http.IgnitedHttpResponse;
import com.mdroid.core.util.SystemUtil;

/**
 * 账号接口具体实现
 * @author putao_lhq
 * @version 2014-11-21
 */
class PutaoAccountImpl implements IPutaoAccount {

	private static final String TAG = "PutaoAccount";
	private static final String YELLOW_USER = "pt_preferences";
	
	private IAccCallback mCallBack;
	private HandlerThread mAccountThread;
	private AccountHandler mAccountHanlder;
	private IThirdAccount mThirdAccount;
	private PTUser mPtUser;
	private boolean isLogin = false;
	
	private List<IAccChangeListener> mAccChangeListener;
	private int mErrorMsg = -1;
	
	public PutaoAccountImpl() {
		mAccountThread = new HandlerThread("account#", Process.THREAD_PRIORITY_BACKGROUND);
		mAccountThread.start();
		mAccountHanlder = new AccountHandler(mAccountThread.getLooper());
		mAccChangeListener = new ArrayList<IAccChangeListener>();
		mThirdAccount = ThirdAccountFactory.create(ThirdAccountFactory.ACCOUNT_COOLPAD);
		addAccChangeListener(UserInfoUtil.getInstace());
		addAccChangeListener(PTMessageCenter.getInstance());
		addAccChangeListener(PTOrderCenter.getInstance());
	}
	
	@Override
	public void login(Context context, String accName, int type, IAccCallback cb) {
		LogUtil.d(TAG, "accName = " + accName + 
				" ,type = " + type + " ,IAccCallback = " + cb);
		if (isLogin) {
			LogUtil.d(TAG, "login is running...");
			if(cb != null) {
			    cb.onCancel();
			}
			
			return;
		}
		this.mCallBack = cb;
		this.isLogin = true;
		
		switch (type) {
		case LOGIN_TYPE_BY_SILENT:
			mAccountHanlder.sendEmptyMessage(AccountHandler.MSG_LOGIN_BY_SILENT);
			break;

		case LOGIN_TYPE_BY_THIRD:
			if (mThirdAccount == null) {
				return;
			}
			//判断用户是否已经登录，如果登录，则直接获取账号登录到葡萄账号，如果未登录，则拉起三方账号登录界面进行登录。
			if (mThirdAccount.isLogin()) {
				Message msg = mAccountHanlder.obtainMessage(AccountHandler.MSG_LOGIN_BY_THIRD, accName);
				mAccountHanlder.sendMessage(msg);
			} else {
				showLoginUI(context);
			}
			break;

		case LOGIN_TYPE_BY_PHONE:
			Message msg_phone = mAccountHanlder.obtainMessage(AccountHandler.MSG_LOGIN_BY_PHONE, accName);
			mAccountHanlder.sendMessage(msg_phone);
			break;
		
		default:
			break;
		}

	}

	@Override
	public void logout(Context context) {
		//清除本地数据
		cleanPtUser(context);
		mPtUser = null;
		markUserLoginOutOperate(context);
		mAccountHanlder.sendEmptyMessage(AccountHandler.MSG_HANDLE_LOGOUT);
	}

	@Override
	public PTUser getPtUser() {
		SharedPreferences preferences = ContactsApp.getInstance()
                .getSharedPreferences(YELLOW_USER, Context.MODE_MULTI_PROCESS);
        String pt_user = preferences.getString("pt_user", null);
        if(!TextUtils.isEmpty(pt_user)){
            mPtUser = new PTUser(pt_user);
        }
		return mPtUser;
	}

	@Override
	public void unbind(String accName, int source, int type, IAccCallback cb) {
		mCallBack = cb;
		Message msg = mAccountHanlder.obtainMessage(AccountHandler.MSG_UNBIND_ACCOUNT, 
				source, type, accName);
		mAccountHanlder.sendMessage(msg);

	}

	@Override
	public boolean isLogin() {
		loadPtUser();
		if (mPtUser != null && mPtUser.getPt_token() != null) {
			return true;
		}
		return false;
	}

	@Override
	public String getOpenToken() {
		loadPtUser();
		if (mPtUser != null) {
			return mPtUser.getPt_open_token();
		}
		return null;
	}
	
	@Override
	public void addAccChangeListener(IAccChangeListener l) {
		if (mAccChangeListener == null) {
			mAccChangeListener = new ArrayList<IAccChangeListener>();
		}
		if (!mAccChangeListener.contains(l)) {
			mAccChangeListener.add(l);
		}
	}
	
	@Override
    public void delAccChangeListener(IAccChangeListener l) {
	    if(mAccChangeListener != null) {
	        mAccChangeListener.remove(l);
	    }
	}
	
	/*------------------------------------------------------------------------------------*/
	/**
	 * 后台静默登录，同步方法！
	 */
	private synchronized boolean silentLoginSync() {
		if (hasUserLoginOutOperate()) {
			return loginByDevice();
		}
		boolean success = loginByThirdAccount(true);
		if (success) {
			return true;
		}
		String phoneNumber = ContactsHubUtils.getPhoneNumber(ContactsApp.getInstance());
		if (!TextUtils.isEmpty(phoneNumber)) {
			success = loginByPhone(phoneNumber);
		}
		if (success) {
			return true;
		}
		success = loginByDevice();
		return success;
	}
	
	/**
     * 通过三方账号登录葡萄账户
     * @return
     */
    private boolean loginByThirdAccount(boolean silent) {
    	if (mThirdAccount == null) {
    		LogUtil.d(TAG, "mThirdAccount is null");
    		return false;
    	}
    	String third_acc = mThirdAccount.getUid(silent);
    	if (TextUtils.isEmpty(third_acc)) {
    		LogUtil.d(TAG, "can't obtain third account");
    		return false;
    	}
    	AccountInfo accountInfo = mThirdAccount.getAccountInfo();
    	int source = mThirdAccount.getSource();
    	int type = mThirdAccount.getType();
		return loginPutaoAccount(third_acc, source, type, accountInfo);
	}
    
    /**
     * 展示登录界面
     * @param context
     */
    private void showLoginUI(Context context) {
    	mThirdAccount.showLoginUI(context, new IAccCallback() {
			
			@Override
			public void onSuccess() {
				Message msg = mAccountHanlder.obtainMessage(AccountHandler.MSG_LOGIN_BY_THIRD, null);
				mAccountHanlder.sendMessage(msg);
			}
			
			@Override
			public void onFail(int error_code) {
				LogUtil.d(TAG, "showLoginUI->login fail: " + error_code);
				mainHandler.sendEmptyMessage(MSG_LOGIN_FAIL);
			}

            @Override
            public void onCancel() {
                LogUtil.d(TAG, "showLoginUI->login canceled");
                mainHandler.sendEmptyMessage(MSG_LOGIN_CANCEL);
            }
		});
    }
    
    /**
     * 通过手机号码登录葡萄账户
     * @return
     */
	private boolean loginByPhone(String phoneNumber) {
		LogUtil.d(TAG, "phoneNumber: " + phoneNumber);
		if (TextUtils.isEmpty(phoneNumber)) {
			return false;
		}
		return loginPutaoAccount(phoneNumber, RelateUserResponse.SOURCE_PHONE, 
				RelateUserResponse.TYPE_PHONE, null);
	}
	
	/**
     * 判断 用户 是否 主动退出过登录 
     * @return
     */
    private boolean hasUserLoginOutOperate() {
        SharedPreferences preferences = ContactsApp.getInstance().getSharedPreferences(YELLOW_USER,
                Context.MODE_MULTI_PROCESS);
        return preferences.getBoolean(KEY_USER_OPERATE, false);
    }
    
    /**
     * 清楚本地账号信息
     * @param context
     */
    private void cleanPtUser(Context context) {
    	SharedPreferences preference = context.getSharedPreferences(YELLOW_USER, 
    			Context.MODE_MULTI_PROCESS);
    	preference.edit().remove(KEY_PT_USER_SP).commit();
    }
    
    /**
     * 标明为用户主动退出
     * @param context
     */
    private void markUserLoginOutOperate(Context context) {
    	SharedPreferences preferences = context.getSharedPreferences(YELLOW_USER, 
    			Context.MODE_MULTI_PROCESS);
    	/*
    	 * bug修改：标志用户主动退出，不能直接移除（原逻辑会导致永远不会记录用户主动退出，每次都会静默登录）
    	 * modified by hyl 2014-12-25 start
    	 * old code:
    	 * preferences.edit().remove(KEY_USER_OPERATE).commit();
    	 */
    	preferences.edit().putBoolean(KEY_USER_OPERATE, true).commit();
    	// modified by hyl 2014-12-25 end
    }
    /**
	 * 通过设备ID登录葡萄账号
	 * @return
	 */
	private boolean loginByDevice() {
		String devicedId = SystemUtil.getDeviceId(ContactsApp.getInstance());
		LogUtil.d(TAG, "device id is: " + devicedId);
		if (TextUtils.isEmpty(devicedId)) {
			return false;
		}
		final DeviceUserRegisterRequest registerRequest = new DeviceUserRegisterRequest();

        IgnitedHttpResponse httpResponse;
        try {
            httpResponse = Config.getApiHttp().post(Config.SERVER, registerRequest.getData())
                    .send();
            String content = httpResponse.getResponseBodyAsString();
            DeviceUserRegisterResponse registerResponse = registerRequest.getObject(content);

            if (registerResponse != null) {
                if (registerResponse.isSuccess()) {
                    LogUtil.i(TAG, "loginByDevicedId success");
                    // 保存黄页账户对象json
                    savePtUser(content);
                    return true;
                }
            }
        } catch (ConnectException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
	}
	
	/**
	 * 登录葡萄账号. <strong>注：该方法只能在非主线程下执行</strong>
	 * @param accName 绑定的鉴权账户
	 * @param accSource 绑定鉴权账号来源 : 0 表使用设备创建, 1 表使用手机创建, 2 表使用酷派创建
	 * @param accType	绑定鉴权账号类型: 0 表临时用户, 1 表普通绑定用户, 2 表平台绑定用户
	 * @param info 鉴权账户信息 {@link AccountInfo}
	 * @return 登录成功为true，否则为false
	 */
	protected boolean loginPutaoAccount(String accName, 
			int accSource, 
			int accType, 
			AccountInfo info) {
		LogUtil.d(TAG, "loginPutaoAccount: " + accName + " accountInfo: " + info);
		UserRegisterRequest registerRequest = new UserRegisterRequest(accName, accSource,
                accType, info);

        IgnitedHttpResponse httpResponse;
        try {
            httpResponse = Config.getApiHttp().post(Config.SERVER, registerRequest.getData())
                    .send();
            String content = httpResponse.getResponseBodyAsString();
            UserRegisterResponse registerResponse = registerRequest.getObject(content);
			if (registerResponse != null && registerResponse.isSuccess()) {
				if (registerResponse.registerStatus == 0) {
					LogUtil.i(TAG, "loginPutaoAccount success ");
					// 保存黄页账户对象json
					savePtUser(content);
					return true;
				} else {
					mErrorMsg = IAccCallback.LOGIN_FAILED_CODE_HAS_BIND;
				}
			} else {
				mErrorMsg = IAccCallback.LOGIN_FAILED_CODE_SERVER_EXCEPTION;
			}
        } catch (ConnectException e) {
        	LogUtil.d(TAG, "ConnectException: " + e);
        	mErrorMsg = IAccCallback.LOGIN_FAILED_CODE_CONNECTION_EXCEPTION;
            e.printStackTrace();
        } catch (IOException e) {
        	LogUtil.d(TAG, "IOException: " + e);
        	mErrorMsg = IAccCallback.LOGIN_FAILED_CODE_SERVER_EXCEPTION;
            e.printStackTrace();
        }
        LogUtil.i(TAG, "loginPutaoAccount fail ");
        return false;
	}
	
	private boolean unBindRelateUser(String accName, int accSource, int accType) {
		UserUnbindingRequest registerRequest = new UserUnbindingRequest(accName, accSource,
                accType);

        IgnitedHttpResponse httpResponse;
        try {
            httpResponse = Config.getApiHttp().post(Config.SERVER, registerRequest.getData())
                    .send();
            String content = httpResponse.getResponseBodyAsString();
            UserUnbindingResponse registerResponse = registerRequest.getObject(content);
			if (registerResponse != null && registerResponse.isSuccess()
					&& registerResponse.isSuccess()) {
				LogUtil.i(TAG, "unBind success ");
				//savePtUser(content);
				unBindPtUserRelateUser(accName, accSource, accType);
				loadPtUser();
				return true;
			}
        } catch (ConnectException e) {
        	LogUtil.d(TAG, "ConnectException: " + e);
            e.printStackTrace();
        } catch (IOException e) {
        	LogUtil.d(TAG, "IOException: " + e);
            e.printStackTrace();
        }
        LogUtil.i(TAG, "loginPutaoAccount fail ");
        return false;
		
	}
	
	/**
     * 解除鉴权用户绑定
     * add by hyl 2014-9-28
     * @param accName  账户名称
     * @param accSource 账户来源
     * @param accType  账户类型
     */
    private void unBindPtUserRelateUser(String accName, int accSource, int accType) {
        if(mPtUser != null && mPtUser.relateUsers != null){
            RelateUserResponse unbindItem = null;
            for (RelateUserResponse item : mPtUser.relateUsers) {
                if(item.accName.equals(accName) 
                        && item.accSource == accSource 
                        && item.accType == accType){
                    unbindItem = item;
                    break;
                }
            }
            if(unbindItem != null){
            	mPtUser.relateUsers.remove(unbindItem);
                String json = new Gson().toJson(mPtUser);
                savePtUser(json);
            }
        }
    }
    
	/**
	 * 保存账户信息
	 * @param content
	 */
	private void savePtUser(String content) {
		LogUtil.d(TAG, "savePtUser: " + content);
		/*
		 * modify by ptuao_lhq @start
		 * 首先来判断下与之前账号是否一致
		 * add code: 
		 */
		PTUser curUser = getPtUser();
        if (curUser != null && !curUser.getPt_uid().equals(new PTUser(content).pt_uid)) {
		    LogUtil.d(TAG, "putao account has changed");
		    mAccountHanlder.sendEmptyMessage(AccountHandler.MSG_LOGIN_ACC_CHANGED);
		}
        SharedPreferences preferences = ContactsApp.getInstance().getSharedPreferences(YELLOW_USER,
                Context.MODE_MULTI_PROCESS);
        preferences.edit().putString(KEY_PT_USER_SP, content).commit();
	}
	
	/**
	 * 加载葡萄用户信息
	 * @return
	 */
	private void loadPtUser() {
		SharedPreferences preferences = ContactsApp.getInstance()
				.getSharedPreferences(YELLOW_USER,
						Context.MODE_MULTI_PROCESS);
		String pt_user = preferences.getString(KEY_PT_USER_SP, null);
		if (!TextUtils.isEmpty(pt_user)) {
			mPtUser = new PTUser(pt_user);
		}
	}
	
	private static final int MSG_LOGIN_SUCCESS = 100;
	private static final int MSG_LOGIN_FAIL = 101;
	private static final int MSG_UNBIND_SUCCESS = 102;
	private static final int MSG_UNBIND_FAIL = 103;
    private static final int MSG_LOGIN_CANCEL = 104;    

	private Handler mainHandler = new Handler() {
		
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_LOGIN_SUCCESS:
				LogUtil.d(TAG, "handle MSG_LOGIN_SUCCESS start");
				//loadPtUser();
				if (mCallBack != null) {
					mCallBack.onSuccess();
				}
				mAccountHanlder.sendEmptyMessage(AccountHandler.MSG_HANLE_LOGIN_SUCCESS);
				isLogin = false;
				LogUtil.d(TAG, "handle MSG_LOGIN_SUCCESS end");
				break;
			case MSG_LOGIN_FAIL:
				LogUtil.d(TAG, "handle MSG_LOGIN_FAIL start");
				if (mCallBack != null) {
					mCallBack.onFail(mErrorMsg);
				}
				isLogin = false;
				mErrorMsg = -1;
				LogUtil.d(TAG, "handle MSG_LOGIN_FAIL end");
				break;
			case MSG_UNBIND_SUCCESS:
				if (mCallBack != null) {
					mCallBack.onSuccess();
				}
				break;
			case MSG_UNBIND_FAIL:
				if (mCallBack != null) {
					mCallBack.onFail(mErrorMsg);
				}
				mErrorMsg = -1;
				break;
            case MSG_LOGIN_CANCEL:
                if (mCallBack != null) {
                    mCallBack.onCancel();
                }
                isLogin = false;
                mErrorMsg = -1;
                break;				
			default:
				break;
			}
		};
	};
	private String KEY_PT_USER_SP = "pt_user";
	private String KEY_USER_OPERATE = "is_user_operate";
	
	/**
	 * @hide
	 * @author putao_lhq
	 * @version 2014年11月4日
	 */
	class AccountHandler extends Handler {
		
		public static final int MSG_LOGIN_BY_PHONE = 1;
		public static final int MSG_LOGIN_BY_DEVICE = 2;
		public static final int MSG_LOGIN_BY_THIRD = 3;
		public static final int MSG_LOGIN_BY_SILENT = 4; //后台静默登录，登录顺序：1.三方账户 2.本机号码 3.设备号
		public static final int MSG_HANLE_LOGIN_SUCCESS = 5;
		public static final int MSG_UNBIND_ACCOUNT = 6; //解绑账户
		public static final int MSG_HANDLE_LOGOUT = 7; //处理用户退出时相关操作。
		public static final int MSG_LOGIN_ACC_CHANGED = 8; // 账号改变
		public AccountHandler(Looper looper) {
			super(looper);
		}
		
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_LOGIN_BY_SILENT:
				LogUtil.d(TAG, "handle MSG_LOGIN_BY_SILENT start");
				boolean success = silentLoginSync();
				if (success) {
					LogUtil.d(TAG, "handle MSG_LOGIN_BY_SILENT success");
					mainHandler.sendEmptyMessage(MSG_LOGIN_SUCCESS);
				} else {
					mainHandler.sendEmptyMessage(MSG_LOGIN_FAIL);
				}
				LogUtil.d(TAG, "handle MSG_LOGIN_BY_SILENT end");
				break;
			case MSG_LOGIN_BY_PHONE:
				LogUtil.d(TAG, "handle login by phone start");
				String phoneNumber = null;
				if (msg.obj != null) {
					phoneNumber = (String)msg.obj;
				}
				if (loginByPhone(phoneNumber)) {
					mainHandler.sendEmptyMessage(MSG_LOGIN_SUCCESS);
				} else {
					mainHandler.sendEmptyMessage(MSG_LOGIN_FAIL);
				}
				LogUtil.d(TAG, "handle login by phone end");
				break;
			case MSG_LOGIN_BY_THIRD:
				LogUtil.d(TAG, "handle MSG_LOGIN_BY_THIRD start");
				if (loginByThirdAccount(false)) {
					mainHandler.sendEmptyMessage(MSG_LOGIN_SUCCESS);
				} else {
					mainHandler.sendEmptyMessage(MSG_LOGIN_FAIL);
				}
				LogUtil.d(TAG, "handle MSG_LOGIN_BY_THIRD end");
				break;
			case MSG_LOGIN_BY_DEVICE:
				LogUtil.d(TAG, "handle login by device start");
				boolean result = loginByDevice();
				if (result) {
					mainHandler.sendEmptyMessage(MSG_LOGIN_SUCCESS);
				} else {
					mainHandler.sendEmptyMessage(MSG_LOGIN_FAIL);
				}
				LogUtil.d(TAG, "handle login by device end");
				break;
			case MSG_UNBIND_ACCOUNT:
				LogUtil.d(TAG, "handle MSG_UNBIND_PHONE start");
				if (msg.obj == null) {
					return;
				}
				String accName = (String) msg.obj;
				boolean unBindResult = unBindRelateUser(accName, 
						msg.arg1, msg.arg2);
				if (unBindResult) {
					mainHandler.sendEmptyMessage(MSG_UNBIND_SUCCESS);
				} else {
					mainHandler.sendEmptyMessage(MSG_UNBIND_FAIL);
				}
				LogUtil.d(TAG, "handle MSG_UNBIND_PHONE end");
				break;
			case MSG_HANLE_LOGIN_SUCCESS:
				LogUtil.d(TAG, "handle MSG_HANLE_LOGIN_SUCCESS start");
				 /*
		         * 移除信鸽功能 注释该逻辑
		         * modified by hyl 2014-12-24 start
		         */
//				XGPushUtil.registerXGPush(ContactsApp.getInstance());//add by zjh 2014-10-17 注册信鸽push
				//modified by hyl 2014-12-24 end
				
				//UserInfoUtil.getInstace().updateHabitData();//add by hyl 2014-9-23 获取服务器配置应用数据
                //UserInfoUtil.getInstace().updateUserCouponData(); //add by zjh 2014-10-24 获取用户优惠券信息
                if (mAccChangeListener == null || mAccChangeListener.size() <= 0) {
                	return;
                }
                for (int i = 0; i < mAccChangeListener.size(); i++) {
                	IAccChangeListener l = mAccChangeListener.get(i);
                	l.onLogin();
                }
                LogUtil.d(TAG, "handle MSG_HANLE_LOGIN_SUCCESS end");
				break;
			case MSG_HANDLE_LOGOUT:
				LogUtil.d(TAG, "handle MSG_HANDLE_LOGOUT start");
				if (mAccChangeListener == null || mAccChangeListener.size() <= 0) {
                	return;
                }
                for (int i = 0; i < mAccChangeListener.size(); i++) {
                	IAccChangeListener l = mAccChangeListener.get(i);
                	l.onLogout();
                }
				LogUtil.d(TAG, "handle MSG_HANDLE_LOGOUT end");
				break;
			case MSG_LOGIN_ACC_CHANGED:
			    if (mAccChangeListener == null || mAccChangeListener.size() <= 0) {
                    return;
                }
                for (int i = 0; i < mAccChangeListener.size(); i++) {
                    IAccChangeListener l = mAccChangeListener.get(i);
                    l.onChange();
                }
			    break;
			default:
				break;
			}
		}

	}
}
