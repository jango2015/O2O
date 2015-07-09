package so.contacts.hub;

import so.contacts.hub.util.ContactsHubUtils;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.VolleyQueue;
import android.app.Application;
import android.content.Context;

/*
                   _ooOoo_
                  o8888888o
                  88" . "88
                  (| -_- |)
                  O\  =  /O
               ____/`---'\____
             .'  \\|     |//  `.
            /  \\|||  :  |||//  \
           /  _||||| -:- |||||-  \
           |   | \\\  -  /// |   |
           | \_|  ''\---/''  |   |
           \  .-\__  `-`  ___/-. /
         ___`. .'  /--.--\  `. . __
      ."" '<  `.___\_<|>_/___.'  >'"".
     | | :  `- \`.;`\ _ /`;.`/ - ` : | |
     \  \ `-.   \_ __\ /__ _/   .-` /  /
======`-.____`-.___\_____/___.-`____.-'======
                   `=---='
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
         佛祖保佑       永无BUG
*/

public class ContactsApp extends Application {
    
    //add ljq start 2015-01-16 因为1.707版本微信支付状态不准确 只能用此全局变量表示微信支付状态
    public static boolean isCinemaWeChatPaySuccess = false;
    //add ljq end

    private static final String TAG = "ContactsApp";
    
    public static Context getInstance() {
        return YellowPageApplicationProxy.getContext();
    }
    
    public static Context getContext(){
        return YellowPageApplicationProxy.getContext();
    }
    
    @Override
    public void onCreate() {
        LogUtil.d(TAG, "initPlug ContactsApp onCreate ="+System.currentTimeMillis()+"process:"+ContactsHubUtils.getCurProcessName(this));
        super.onCreate();
        YellowPageApplicationProxy.setApplicationContext(this);
        if (ContactsHubUtils.isMainProcess(this)) {
            YellowPageApplicationProxy.initYellowApp();
        }
        LogUtil.d(TAG, "initPlug ContactsApp onCreate end="+System.currentTimeMillis());
    }
}
