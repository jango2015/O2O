package so.contacts.hub.msgcenter;

import so.contacts.hub.ContactsApp;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * 消息中心设置，涉及到两处：
 * 设置消息中心声音与震动开关
 * @author putao_lhq
 *
 */
public class PTMessageCenterSettings {

    public static final String SHARED_NAME = "message_center";
    private static final String SOUND_NAME = "sound";
    private static final String VIBRATE_NAME = "vibrate";
    
    /**
     * 设置提醒时是否为声音提醒
     * @param enable
     */
    public static  void setSoundEnable(boolean enable) {
        SharedPreferences sp = ContactsApp.getInstance().getSharedPreferences(SHARED_NAME, 
                Context.MODE_MULTI_PROCESS);
        sp.edit().putBoolean(SOUND_NAME, enable).commit();
    }
    
    /**
     * 设置提醒时是否为震动提醒
     * @param enable
     */
    public static void setVibrateEnable(boolean enable) {
        SharedPreferences sp = ContactsApp.getInstance().getSharedPreferences(SHARED_NAME, 
                Context.MODE_MULTI_PROCESS);
        sp.edit().putBoolean(VIBRATE_NAME, enable).commit();
    }
    
    public static boolean getVibrateEnable() {
        SharedPreferences sp = ContactsApp.getInstance().getSharedPreferences(SHARED_NAME, 
                Context.MODE_MULTI_PROCESS);
        return sp.getBoolean(VIBRATE_NAME, true);
    }
    
    public static boolean getSoundEnable() {
        SharedPreferences sp = ContactsApp.getInstance().getSharedPreferences(SHARED_NAME, 
                Context.MODE_MULTI_PROCESS);
        return  sp.getBoolean(SOUND_NAME, true);
    }
}
