package so.contacts.hub.broadcast;

import com.yulong.android.contacts.discover.R;

import so.contacts.hub.common.OperatorsCommans;
import so.contacts.hub.util.Utils;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

/**
 * 发送查询话费短信追踪的广播接收
 * 
 */
public class QueryTeleChargeMsgReceiver extends BroadcastReceiver {
	String TAG = "QueryTeleChargeMsgReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		if (OperatorsCommans.SENT_SMS_ACTION.equals(action)) {
			//短信接收状态 监听
			switch (getResultCode()) {
			case Activity.RESULT_OK:
				Utils.showToast(context, R.string.putao_query_telecharge_success_hint, false);
				break;
			case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
			case SmsManager.RESULT_ERROR_RADIO_OFF:
			case SmsManager.RESULT_ERROR_NULL_PDU:
				Utils.showToast(context, R.string.putao_query_telecharge_failed_hint, false);
				break;
			default:
				break;
			}
		}
	}

}
