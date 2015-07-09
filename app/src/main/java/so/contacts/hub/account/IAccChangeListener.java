package so.contacts.hub.account;

/**
 * 账号变更接口
 * @author putao_lhq
 *
 */
public interface IAccChangeListener {

	/**
	 * 用户登录
	 */
	public void onLogin();
	
	/**
	 * 退出登录
	 */
	public void onLogout();
	
	/**
	 * 变更账号
	 */
	public void onChange();
}
