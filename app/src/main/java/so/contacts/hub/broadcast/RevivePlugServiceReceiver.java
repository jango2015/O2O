package so.contacts.hub.broadcast;

import so.contacts.hub.service.PlugService;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.YellowPagePlugUtil;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 检查PlugService服务是否存在的广播监听
 * 如果PlugService服务挂了，则重新绑定Service.
 */
public class RevivePlugServiceReceiver extends BroadcastReceiver {

	private static final String TAG = "RevivePlugServiceReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if( context == null || intent == null ){
			return;
		}
		String action = intent.getAction();
		LogUtil.i(TAG, "RevivePlugServiceReceiver onReceive action: " + action);
		if( "so.contacts.hub.check.plugservice".equals(action) ){
			if( !YellowPagePlugUtil.isPlugServiceWorked(context) ){
				LogUtil.i(TAG, "onReceive need start PlugService.");
				Intent serviceIntent = new Intent(context, PlugService.class);
				context.startService(serviceIntent);
			}
		}
	}

}
