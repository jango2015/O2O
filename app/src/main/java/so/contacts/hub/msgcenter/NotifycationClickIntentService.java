
package so.contacts.hub.msgcenter;

import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.UMengEventIds;
import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;

/**
 * 通知栏被点击监听桥接
 * 
 * @author lixiaohui
 */
public class NotifycationClickIntentService extends IntentService {

    public NotifycationClickIntentService(String name) {
        super("NotifycationClickIntentService");
    }

    public NotifycationClickIntentService() {
        super("NotifycationClickIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        MobclickAgentUtil.onEvent(this, UMengEventIds.DISCOVER_YELLOWPAGE_MY_MSG_CENTER_NOTIFICATION_IN);
        Intent realIntent = intent.getParcelableExtra("realIntent");
        startActivity(realIntent);
    }

}
