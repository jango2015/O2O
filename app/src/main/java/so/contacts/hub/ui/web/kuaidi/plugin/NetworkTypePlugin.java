
package so.contacts.hub.ui.web.kuaidi.plugin;

import org.json.JSONObject;

import so.contacts.hub.ui.web.config.Const;
import so.contacts.hub.ui.web.kuaidi.ResponseEvent;
import so.contacts.hub.ui.web.kuaidi.WebPlugin;
import so.contacts.hub.util.LogUtil;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;

public class NetworkTypePlugin extends WebPlugin {
	private static final String TAG = "NetworkTypePlugin";
    private Context mContext;

    @Override
    public void onCreate(Context context) {
        this.mContext = context;
    }

    @Override
    public void onDestory() {

    }

    @Override
    public ResponseEvent execute(JSONObject params) {

        ResponseEvent event = new ResponseEvent();

        try {
            ConnectivityManager conMan = (ConnectivityManager) mContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            State wifiState = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
            if (State.CONNECTED == wifiState) {
                event.setData("wifi");
                return event;
            }

            // mobile 3G Data Network
            State mobileState = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();

            if (mobileState == State.CONNECTED) {
                event.setData("mobile");
                return event;
            }
            event.setData("fail");

        } catch (Exception e) {
            event.setCode(Const.CODE_ERROR_NULL);
        }
        
        return event;
    }
}
