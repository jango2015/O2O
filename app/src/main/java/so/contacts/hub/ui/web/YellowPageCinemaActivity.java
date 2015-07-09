package so.contacts.hub.ui.web;

import java.util.HashMap;
import java.util.Map;

import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.UMengEventIds;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * 注意: 函数putao_shouldOverrideUrlLoading在电影猫眼的H5页面的加载中没有被调用,
 *  			并且在调用putao_onPageFinished时,url增加了瞄点,
 * 	       所以处理该页面时,不能比较view.getUrl().equals(mUrl)
 * @author change
 */
public class YellowPageCinemaActivity extends YellowPageH5Activity {
	private static final String TAG = "YellowPageCinemaActivity";
	private boolean mPageFinished = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		LogUtil.d(TAG, "url="+mUrl);
	}
	
	@Override
	protected void configWebSettings() {
		super.configWebSettings();
		mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        
		// 设置cookie必须在WebSetting之后，否则会失效
		CookieSyncManager.createInstance(YellowPageCinemaActivity.this);		

		CookieManager cm = CookieManager.getInstance();
		cm.setAcceptCookie(true);
		String cookie = "";
		if(mUrl != null && mUrl.length()>0){
		    cookie = cm.getCookie(mUrl);
		}
		LogUtil.d(TAG, "getCookie url="+mUrl+" cookie="+cookie+" acceptCookie="+cm.acceptCookie());

	}

	@Override
	public PutaoWebClientProxy getPutaoWebClientProxy(Context context, Handler h) {
		return new MyPutaoWebClientProxy(this, mHandler);
	}
	
	public class MyPutaoWebClientProxy extends PutaoWebClientProxy {
		public MyPutaoWebClientProxy(Context ctx, Handler h) {
			super(ctx, h);
		}
		
		/**
		 * 该函数在电影猫眼的H5页面的加载中没有被调用
		 */
		public boolean putao_shouldOverrideUrlLoading(WebView view, String url) {
			LogUtil.d(TAG,"putao_shouldOverrideUrlLoading="+url);
			mUrl = url;
			return super.putao_shouldOverrideUrlLoading(view, url);
		}		
		
		@Override
		public void putao_onPageStarted(WebView view, String url, Bitmap favicon) {
			LogUtil.d(TAG, "putao_onPageStarted progress="+progress+" url="+url);
			
			mPageFinished = false;
			if (mFirstLoadHomePage && url.equals(mUrl) ) {
				mFirstLoadHomePage = false;
				LogUtil.d(TAG, "start progress.");
				mHandler.sendEmptyMessage(MSG_SHOW_DIALOG_ACTION);
				mHandler.sendEmptyMessageDelayed(MSG_DISMISS_TIMEDOUT_ACTION, TIMEDOUT);
				updateProgressBar(initProgress);//add ljq 2014 11 07 开始读取时制造假进度
			}
		}
		
		@Override
		public void putao_onPageFinished(WebView view, String url) {
			LogUtil.d(TAG, "putao_onPageFinished progress="+progress+" url="+url+" viewUrl="+view.getUrl());
			
			CookieSyncManager.getInstance().sync();
			
			String loadjs= doJs();//onloadJs_maoyan_def();//putao_lhq modify for remove gewara download start			
			mWebView.loadUrl("javascript:" + loadjs);

			mPageFinished = true;
            if(!TextUtils.isEmpty(view.getUrl()) && progress==100) {
				LogUtil.d(TAG, "close progress.");
				mHandler.sendEmptyMessage(MSG_DISMISS_DIALOG_ACTION);
            }
		}	
		//putao_lhq modify for remove gewara download start
		private String doJs() {
			String str = "try { "
					+
					"var footer=document.getElementById('thelist');"
					+ "if(footer) {  footer.style.display = 'none';  footer.parentNode.removeChild(footer);}"
					+ "var inav = document.getElementsByClassName('inav')[0];"
					+ "if(inav) {inav.style.display = 'none'; inav.parentNode.removeChild(inav);}"
					+ "var clubs = document.getElementsByClassName('clubs')[0];" 
					+ "if(clubs) {clubs.style.display = 'none'; clubs.parentNode.removeChild(clubs);}"
					+
					"} catch (e) {}";
			return str;
	    }
		//putao_lhq modify for remove gewara download end
		@Override
		public void putao_onReceivedTitle(WebView view,
				String title) {
			LogUtil.d(TAG, "putao_onReceivedTitle title="+title+" url="+view.getUrl());
			
			mHandler.removeMessages(MSG_DISMISS_TIMEDOUT_ACTION);
			String loadjs= onloadJs_maoyan_def();				
			mWebView.loadUrl("javascript:" + loadjs);
		}

		@Override
		public void putao_onProgressChanged(WebView view, int newProgress) {
			LogUtil.d(TAG, "putao_onProgressChanged progress="+newProgress);
			
			updateProgressBar(newProgress);
			if(!TextUtils.isEmpty(view.getUrl()) && mPageFinished && 100 == newProgress) {
				LogUtil.d(TAG, "close progress.");
				mHandler.sendEmptyMessage(MSG_DISMISS_DIALOG_ACTION);
			}
		}	
				
		@Override
		public void putao_onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
//			view.stopLoading();
//			mHandler.sendEmptyMessage(MSG_RECEIVED_ERROR_ACTION);
		}
	};	
	    
    /**
     * 为猫眼定制js
     * @return
     */
	private String onloadJs_maoyan_def() {
		String jscode =
				"try { "+
						// 广告条
						"var banner=document.getElementById('banner');"+
						"if(banner) {  banner.style.display = 'none';  banner.parentNode.removeChild(banner); }"+
								
		                // 头部
						"var hd=document.getElementById('hd');"+
						"if(hd) {  hd.style.display = 'none';  hd.parentNode.removeChild(hd); }"+

						// 底部
						"var footer=document.getElementById('ft');"+
						"if(footer) {  footer.style.display = 'none';  footer.parentNode.removeChild(footer); }"+
												 
			     "} catch (e) {alert('加载失败. '+e.name+':'+e.message); }";

		return jscode;
	}
    
	/**
	 * 为卖座网定制css
	 * @return
	 */
    private String create_css() {
    	String cscode= 
    			" var styleid='putao_cj';"+
    			" if(!document.getElementById(styleid)) {"+
    			"	  var style = document.createElement('style');"+
    			"	  style.type = 'text/css';"+
    			"	  style.id = styleid;"+
    			"	  var head=document.getElementsByTagName('head');" +
    			"    if(head && head[0]) {" +
    			"    		head[0].appendChild(style);"+
    			"	  		var styles = '.putao_css{width:100%;overflow:hidden;margin-bottom:0px;margin-top:0px;}';"+
    			"	  		style.appendChild(document.createTextNode(styles));"+
    			"    }"+
    			"	}  ";
    	
    		return cscode;
    	}
    	
    /**
     * 为卖座网定制js
     * 猫眼电影，保留该方法，以备后续再接入使用
     * @return
     */
	@SuppressWarnings("unused")
	private String onloadJs_maizuo_def() {
		String jscode =
				create_css() + 

				"try { "+
		                // 公共头
						"var header = document.getElementsByTagName('header');"+
						"if(header && header[0]) {"+
						"  header[0].style.display = 'none';"+					
					    "  header[0].parentNode.removeChild(header[0]);"+
						"}"+
						
						// 广告条
						"var mySwipe=document.getElementById('banner');"+
						"if(mySwipe) {  mySwipe.style.display = 'none';  mySwipe.parentNode.removeChild(mySwipe); }"+
						
						// 广告条下方空白
						"var headspc='container m-content-box m-index-view';"+
						"var descspc='container m-content-box m-movie-view';"+
						"var descspc1='container m-content-box m-index-view hide';"+
						"var seats_spc ='container m-content-box m-seats-view';"+    // 选坐
						
						"var allElements = document.getElementsByTagName('div');"+ 
						"for(var i=0; i<allElements.length; i++)"+
						  "{ "+
								"if (allElements[i].className == headspc) {"+
								    "allElements[i].className = 'putao_css m-index-view';"+
								    "break;"+
								"} else if (allElements[i].className == descspc) {"+
								    "allElements[i].className = 'putao_css m-movie-view';"+
								    "break;"+
							    "} else if (allElements[i].className == descspc1) {"+
								    "allElements[i].className = 'putao_css m-index-view';"+
								    "break;"+
							    "} else if (allElements[i].className == seats_spc) {"+
								    "allElements[i].className = 'putao_css m-seats-view';"+
								    "break;"+
							    "}"+		
						 "}"+

					    // 页脚
						"var footer = document.getElementsByTagName('footer');"+ 
						"if(footer && footer[0]) {"+
						"  footer[0].style.display = 'none';"+
						"  footer[0].parentNode.removeChild(footer[0]);"+
						"}"+
						 
			     "} catch (e) {alert('加载失败. '+e.name+':'+e.message); }";

		return jscode;
	}

	private String onloadJs_fullscreen() {
		return "";
	}

	private long startTime = 0L;
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgentUtil.onResume(this);
		startTime = System.currentTimeMillis();
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgentUtil.onPause(this);
		try {
			int time = ((int) ((System.currentTimeMillis() - startTime) / 1000));
			Map<String, String> map_value = new HashMap<String, String>();
			map_value.put("type", this.getClass().getName());
//	        com.putao.analytics.MobclickAgentUtil.onEventValue(this,
//	                    UMengEventIds.DISCOVER_YELLOWPAGE_RESUME_TIME_56, map_value, time);
			MobclickAgentUtil.onEventValue(this,
					UMengEventIds.DISCOVER_YELLOWPAGE_RESUME_TIME_56, map_value, time);
		} catch (Exception e) {
		}
	}

	@Override
	protected void onDestroy() {
		modifyCookie();//add by putao_lhq
		super.onDestroy();
	}

	@Override
	public boolean needMatchExpandParam() {
		return true;
	}

	/**
	 * cookie同步,删除城市cookie,格瓦拉电影定位不到问题.
	 * @author putao_lhq
	 */
	private void modifyCookie() {
		SQLiteDatabase db = null;
		try {
			if (Build.VERSION.SDK_INT > 18) {//Build.VERSION_CODES.JELLY_BEAN_MR2 = 18
				String path = this.getApplicationContext().getDir("webview", 0)
						.getPath()
						+ "/";
				LogUtil.d(TAG, "database path: " + path);
				db = this.openOrCreateDatabase(path + "Cookies", MODE_PRIVATE,
						null);
			} else {
				db = this.openOrCreateDatabase("webviewCookiesChromium.db",
						MODE_PRIVATE, null);
			}
			int result = db.delete("cookies", "host_key=? " + "AND "
					+ "name=? ", new String[] { "m.gewara.com", "citycode" });
			LogUtil.d(TAG, "result: " + result);
			result = db.delete("cookies", "host_key=? " + "AND " + "name=? ",
					new String[] { "m.gewara.com", "cityname" });
			LogUtil.d(TAG, "result2: " + result);
		} catch (Exception e) {
			LogUtil.e(TAG, "delete cookies db exception: " + e);
		} finally { 
			if(db != null)
				db.close();			
		}
		CookieSyncManager.getInstance().sync();
	}
}
