package so.contacts.hub.account;

/**
 * 账号登录回调接口
 * @author putao_lhq
 *
 */
public interface IAccCallback {

	/**
	 * 账号已存在
	 */
	public static final int LOGIN_FAILED_CODE_HAS_BIND = 1001;
	
	/**
	 * 服务端异常
	 */
	public static final int LOGIN_FAILED_CODE_SERVER_EXCEPTION = 1002;
	
	/**
	 * 网络连接异常
	 */
	public static final int LOGIN_FAILED_CODE_CONNECTION_EXCEPTION = 1003; 
	
	/**
	 * 登录成功
	 */
	public void onSuccess();
	
	/**
	 * 登录失败
	 * @param failed_code
	 */
	public void onFail(int failed_code);
	
	/**
     * 登录取消
     * @param 
     */
    public void onCancel();
}
