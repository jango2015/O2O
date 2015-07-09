
package so.contacts.hub.gamecenter.utils;

import java.util.Set;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.core.Config;
import android.content.SharedPreferences;

/**
 * @author qinyn 获取preference 工具
 */
public class SharedPreferenceUtils {
    // public static final String DATA_USER = "DATA_USER";
    // public static final int TYPE_BOOL = 0;
    // public static final int TYPE_INT = 1;
    // public static final int TYPE_STRING = 2;
    // public static final int TYPE_ARRAYLIST = 3;
    //
    // public static final String DATA_SERVER = "DATA_SERVER";
    //

    @SuppressWarnings("unchecked")
    public static <T> T getPreference(String pName, String key, T defaultValue) {
        SharedPreferences sp = ContactsApp.getInstance().getSharedPreferences(pName, 0);
        if (sp.contains(key)) {
            if (defaultValue instanceof String) {
                return (T)sp.getString(key, (String)defaultValue);
            } else if (defaultValue instanceof Long) {
                return (T)Long.valueOf(sp.getLong(key, (Long)defaultValue));
            } else if (defaultValue instanceof Integer) {
                return (T)Integer.valueOf(sp.getInt(key, (Integer)defaultValue));
            } else if (defaultValue instanceof Float) {
                return (T)Float.valueOf(sp.getFloat(key, (Float)defaultValue));
            } else if (defaultValue instanceof Boolean) {
                return (T)Boolean.valueOf(sp.getBoolean(key, (Boolean)defaultValue));
            } else {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getPreference(String pName, String key, Class<T> cls) {
        if (cls.equals(String.class)) {
            return (T)getPreference(pName, key, "");
        } else if (cls.equals(Long.class)) {
            return (T)getPreference(pName, key, 0L);
        } else if (cls.equals(Integer.class)) {
            return (T)getPreference(pName, key, 0);
        } else if (cls.equals(Float.class)) {
            return (T)getPreference(pName, key, 0.0f);
        } else if (cls.equals(Boolean.class)) {
            return (T)getPreference(pName, key, false);
        }
        return null;
    }

    public static <T> void setPreference(String pName, String key, T value) {
        SharedPreferences sp = ContactsApp.getInstance().getSharedPreferences(pName, 0);
        
        SharedPreferences.Editor editor = sp.edit();
        if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean)value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer)value);
        } else if (value instanceof String) {
            editor.putString(key, (String)value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long)value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float)value);
        }
        editor.commit();
    }
    
    public static <T> void setPreference(String pName, String key, T value, int mode) {
      SharedPreferences sp = ContactsApp.getInstance().getSharedPreferences(pName, mode);
      
      SharedPreferences.Editor editor = sp.edit();
      if (value instanceof Boolean) {
          editor.putBoolean(key, (Boolean)value);
      } else if (value instanceof Integer) {
          editor.putInt(key, (Integer)value);
      } else if (value instanceof String) {
          editor.putString(key, (String)value);
      } else if (value instanceof Long) {
          editor.putLong(key, (Long)value);
      } else if (value instanceof Float) {
          editor.putFloat(key, (Float)value);
      }
      editor.commit();
  }
    
    
    public static void setPreferenceStringArray(String pName, String key,
            Set<String> value) {
        SharedPreferences sp = ContactsApp.getInstance().getSharedPreferences(pName, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putStringSet(key, value);
        editor.commit();
    }
    
    public static Set<String> getPreferenceStringArray(String pName, String key, Set<String> defaultValue) {
        SharedPreferences sp = ContactsApp.getInstance().getSharedPreferences(pName, 0);
        if (sp.contains(key)) {
            return sp.getStringSet(key, defaultValue);
        } else {
            return defaultValue;
        }
    }
    
}
