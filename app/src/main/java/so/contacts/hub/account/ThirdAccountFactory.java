package so.contacts.hub.account;

/**
 * 
 * @author putao_lhq
 *
 */
public class ThirdAccountFactory {

	public static int ACCOUNT_COOLPAD = 0;
	public static int ACCOUNT_LENOVO = 1;
	public static int ACCOUNT_ZTE = 2;
	
	public static IThirdAccount create(int acc_type) {
		if (acc_type == ACCOUNT_COOLPAD) {
			return CoolCloudManager.getInstance();
		} else if (acc_type == ACCOUNT_LENOVO) {
			//return LenovoAccount.getInstance();
		} else if (acc_type == ACCOUNT_ZTE) {
			
		}
		return null;
	}
}
