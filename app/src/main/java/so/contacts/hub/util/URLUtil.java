package so.contacts.hub.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import so.contacts.hub.account.PutaoAccount;
import android.text.TextUtils;

public class URLUtil {

    /**
     * 给URL后面增加open_token
     * @param url
     * @return
     */
    public static String addTokenForUrl(String url) {
        if(TextUtils.isEmpty(url)) {
           return url; 
        }

        String open_token = PutaoAccount.getInstance().getOpenToken();
        if(TextUtils.isEmpty(open_token)) {
            return url;
        }
        
        return addParamForUrl(url, "open_token", open_token);
    }

    /**
     * 给URL后面增加参数和值
     * @param url
     * @param paramName
     * @param paramVal
     * @return
     */
    public static String addParamForUrl(String url, String paramName, String paramVal) {
        if(TextUtils.isEmpty(url)) {
           return url; 
        }

        StringBuffer sb = new StringBuffer();
        sb.append(url);
        
        final String param = paramName+"=";
        if(url.indexOf(param) < 0) {
            if(url.indexOf("?") >= 0) {
                sb.append("&").append(param).append(paramVal);
            } else {
                sb.append("?").append(param).append(paramVal);
            }
        }
        
        return sb.toString();
    }

    /**
     * 给URL后面增加参数和值
     * @param url
     * @param paramName
     * @param paramVal
     * @return
     */
    public static String addParamForUrl(String url, Map<String, String> paramMap) {
        if(TextUtils.isEmpty(url)) {
           return url; 
        }

        StringBuffer sb = new StringBuffer();
        sb.append(url);
        
        Set<String> keys = paramMap.keySet();
        Iterator<String> it = keys.iterator();
        while(it.hasNext()) {
            String key = it.next();
            String paramVal  = paramMap.get(key);
            if(paramVal == null)
                continue;
            String paramname = key+"=";
            
            if(url.indexOf(paramname) < 0) {
                if(sb.indexOf("?") > 0) {
                    sb.append("&").append(paramname).append(paramVal);
                } else {
                    sb.append("?").append(paramname).append(paramVal);
                }
            }
        }
        return sb.toString();
    }
    
}
