package so.contacts.hub.account;

import android.content.Context;


/**
 * 三方账号统一接口
 * @author putao_lhq
 *
 */
interface IThirdAccount {

	/**
	 * 获取三方账号的唯一标识
	 * @param silent TODO
	 * @return
	 */
	public String getUid(boolean silent);
	
	/**
	 * 获取三方账号账户信息
	 * @return
	 */
	public AccountInfo getAccountInfo();
	
	/**
	 * 是否支持单点登录
	 * @return
	 */
	public boolean isSSOEnable();
	
	/**
	 * 获取账号来源
	 * @return
	 */
	public int getSource();
	
	/**
	 * 获取账户类型
	 * @return
	 */
	public int getType();
	
	/**
	 * 判断三方账号是否登录
	 * @return
	 */
	public boolean isLogin();
	
	/**
	 * 显示登录界面
	 */
	public void showLoginUI(Context context, final IAccCallback cb);
	
}
