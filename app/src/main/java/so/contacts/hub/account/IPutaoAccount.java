package so.contacts.hub.account;

import so.contacts.hub.core.PTUser;
import android.content.Context;

/**
 * 账号接口
 * @author putao_lhq
 *
 */
public interface IPutaoAccount {

	/**
	 * 后台默认登录
	 */
	public static int LOGIN_TYPE_BY_SILENT = 0;
	/**
	 * 以依托方账号进行登录
	 */
	public static int LOGIN_TYPE_BY_THIRD = 1;
	
	/**
	 * 以手机号码进行登录
	 */
	public static int LOGIN_TYPE_BY_PHONE = 2;
	
	/**
	 * 以设备号进行登录
	 */
	public static int LOGIN_TYPE_BY_DEVICE = 3;
	
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
			int type, IAccCallback cb);
	
	/**
	 * 退出登录接口
	 * @param context 上下文
	 */
	public void logout(Context context);
	
	/**
	 * 获取葡萄账号
	 * @param context
	 * @return 账号已登录返回账号信息，否则为null
	 */
	public PTUser getPtUser();
	
	/**
	 * 解绑鉴权账号
	 * @param accName 账号
	 * @param source  账号来源
	 * @param type	账号类型
	 * @param cb 解绑结果回调
	 */
	public void unbind(String accName, int source, int type, IAccCallback cb);
	
	/**
	 * 是否已登录
	 * @return 登录为true否则为false
	 */
	public boolean isLogin();
	
	/**
	 * 获取OPEN TOKEN
	 * @return
	 */
	public String getOpenToken();
	
	/**
	 * 添加账号变更监听器
	 * @param l
	 */
	public void addAccChangeListener(IAccChangeListener l);
	
    /**
     * 删除账号变更监听器
     * @param l
     */
    public void delAccChangeListener(IAccChangeListener l);
	
}
