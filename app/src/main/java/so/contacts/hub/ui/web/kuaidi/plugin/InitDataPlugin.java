
package so.contacts.hub.ui.web.kuaidi.plugin;

import org.json.JSONException;
import org.json.JSONObject;

import so.contacts.hub.ui.web.config.Const;
import so.contacts.hub.ui.web.kuaidi.ResponseEvent;
import so.contacts.hub.ui.web.kuaidi.Session;
import so.contacts.hub.ui.web.kuaidi.WebPlugin;
import so.contacts.hub.ui.web.kuaidi.bean.InitDataBean;

import android.content.Context;

import com.google.gson.Gson;

/**
 * 获取初始化数据的Plugin
 * 当js 通过webBinder call getInitData时，会执行本插件中的相关方法
 *  
 */
public class InitDataPlugin extends WebPlugin implements Session {

    private InitDataBean initDataBean;

    public InitDataPlugin(InitDataBean dataBean) {
        this.initDataBean = dataBean;
    }

    @Override
    public void onCreate(Context context) {

    }

    @Override
    public ResponseEvent execute(JSONObject params) {
        /**
         * 将初始化数据封装为JSON字符串 return
         */
        ResponseEvent event = new ResponseEvent();
        if (initDataBean != null) {
            String jsonStr = new Gson().toJson(initDataBean);
            try {
                JSONObject jsonObject = new JSONObject(jsonStr);
                event.setData(jsonObject);
            } catch (JSONException e) {
                event.setCode(Const.CODE_ERROR_NULL);
                e.printStackTrace();
            }
        } else {
            event.setCode(Const.CODE_ERROR_NULL);
        }
        return event;
    }

    @Override
    public void onDestory() {
        // TODO Auto-generated method stub

    }

}
