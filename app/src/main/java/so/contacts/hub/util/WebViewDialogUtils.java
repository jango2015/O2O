package so.contacts.hub.util;

import so.contacts.hub.ui.yellowpage.WebViewDialog;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

/**
 * 
 * @author putao_lhq
 * @version 2014年10月19日
 */
public class WebViewDialogUtils {

	public static void startWebDialog(Activity activity, String url) {
	    if(TextUtils.isEmpty(url))
	        return;
	    
		Intent intent = new Intent(activity, WebViewDialog.class);
		intent.putExtra(WebViewDialog.URL, url);
		activity.startActivity(intent);
	}
}
