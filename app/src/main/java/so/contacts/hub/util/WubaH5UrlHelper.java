package so.contacts.hub.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;


/**
 * 58 页面H5 Url 缓存工具类
 *
 */
public class WubaH5UrlHelper {
	
	private static final String TAG = "WubaH5UrlHelper";

    private static final String URL_CACHE = "cache_url_addr";

    private SharedPreferences mUrlCacheSPref = null;
    
	public WubaH5UrlHelper(Context context){
		mUrlCacheSPref = context.getSharedPreferences(URL_CACHE, android.content.Context.MODE_MULTI_PROCESS );
	}
	
	public String getInfo(int key){
		if( key == -1 ){
			return null;
		}
		return getInfo(String.valueOf(key));
	}
	
	public String getInfo(String key){
		return mUrlCacheSPref.getString(key, null);
	}
	
	/**
	 * 只保留与定位城市相同的 url
	 */
	public void checkAndAddInfo(int key, String dataInfo, String cityCode){
		if( key == -1 || TextUtils.isEmpty(dataInfo) ){
			return;
		}
		LogUtil.i(TAG, "checkAndAddInfo cityCode: " + cityCode + " ,url: " + dataInfo);
		if( !TextUtils.isEmpty(cityCode) ){
			String urlCityCode = getCityCodeByUrl(dataInfo);
			// 如果定位城市(cityCode) 与 58的Url中的城市 不一致，则不保存
			if( !cityCode.equals(urlCityCode) ){
				return;
			}
		}
		addInfo(key, dataInfo);
	}
	
	public void addInfo(int key, String dataInfo){
		if( key == -1 ){
			return;
		}
		addInfo(String.valueOf(key), dataInfo);
	}
	
	public void addInfo(String key, String dataInfo){
		if(TextUtils.isEmpty(key) || TextUtils.isEmpty(dataInfo)){
			return;
		}
		if( !(dataInfo.contains("job.shtml") || dataInfo.contains("jiazheng.shtml") || dataInfo.contains("house.shtml")) ){
			//临时处理：根据关键字过滤 非首页面 url
			return;
		}
		String valueInfo = getUrlInfo(dataInfo);
		if(TextUtils.isEmpty(valueInfo)){
			return;
		}
		mUrlCacheSPref.edit().putString(key, valueInfo).commit();
	}
	
	public void clearCache(int key){
		if( key == -1 ){
			return;
		}
		clearCache(String.valueOf(key));
	}
	
	public void clearCache(String key){
		mUrlCacheSPref.edit().remove(key);
	}
	
	// 传入: http://m.58.com/sz/job.shtml?-15=1&utm_source=link&spm=s-24564483520007-ms-f-txlplus.zhaopin
	// 返回: sz
	public String getCityCodeByUrl(String url){
		if( TextUtils.isEmpty(url) ){
			return "";
		}
		String[] codeList = url.split("/");
		if( codeList == null || codeList.length < 4){
			return "";
		}
		return codeList[3];
	}

	/**
	 * 截取需要保存的url
	 */
	private String getUrlInfo(String urlInfo){
		if( TextUtils.isEmpty(urlInfo) ){
			return null;
		}
		int index = -1;
		if( urlInfo.contains("&needgps") ){
			index = urlInfo.indexOf("&needgps");
			if( index != -1 ){
				urlInfo = urlInfo.substring(0, index);
			}
		}
		if( urlInfo.contains("&lat") ){
			index = urlInfo.indexOf("&lat");
			if( index != -1 ){
				urlInfo = urlInfo.substring(0, index);
			}
		}
		if( urlInfo.contains("&lon") ){
			index = urlInfo.indexOf("&lon");
			if( index != -1 ){
				urlInfo = urlInfo.substring(0, index);
			}
		}
		return urlInfo;
	}

}










