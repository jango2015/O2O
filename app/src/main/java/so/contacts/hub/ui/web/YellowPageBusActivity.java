package so.contacts.hub.ui.web;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.UMengEventIds;
import so.putao.findplug.LBSServiceGaode.LBSServiceListener;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.webkit.CookieSyncManager;
import android.webkit.HttpAuthHandler;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebView;
import so.contacts.hub.util.MobclickAgentUtil;

import com.yulong.android.contacts.discover.R;

public class YellowPageBusActivity extends YellowPageH5Activity implements LBSServiceListener {
    private static final String TAG = YellowPageBusActivity.class.getSimpleName();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(TAG, "url="+mUrl);
    }
           
    @Override
    protected void configWebSettings() {
        super.configWebSettings();
    }
    
    @Override
    public PutaoWebClientProxy getPutaoWebClientProxy(Context context, Handler h) {
        return new MyPutaoWebClientProxy(context, h);
    }

    //putao_lhq add for BUG #1476 start
    private boolean overriteUrl = false;
    @Override
    protected void loadUrl() {
    	super.loadUrl();
    	overriteUrl = false;
    }
    //putao_lhq add for BUG #1476 end
    
    public class MyPutaoWebClientProxy extends PutaoWebClientProxy {
        private boolean mPageFinished;

		public MyPutaoWebClientProxy(Context ctx, Handler h) {
            super(ctx, h);
        }
        
        @Override
        public boolean putao_shouldOverrideUrlLoading(WebView view, String url) {
            LogUtil.d(TAG,"putao_shouldOverrideUrlLoading="+url);
            mUrl = url;
            //putao_lhq add for BUG #1364 start
            if (url.startsWith("baidumap://map/direction")) {
            	return false;
            }
            //putao_lhq add for BUG #1364 end
            // add by putao_lhq 2014年10月17日 for BUG #1606 start
            if (null != url && url.startsWith("sms:")) {
					String body;
					try {
						String decodeStr = URLDecoder.decode(url, "utf-8");
						int start = "sms:?body=".length();
						LogUtil.d(TAG, "end: " + start);
						body = decodeStr.substring(start, decodeStr.length());
						if (TextUtils.isEmpty(body)) {
							LogUtil.v(TAG, "body is null");
							return true;
						}
						Intent share = new Intent(android.content.Intent.ACTION_SEND); 
						share.setType("text/plain"); 
						share.putExtra(android.content.Intent.EXTRA_TEXT, 
								body); 
						String title = getResources().
								getString(R.string.putao_text_shared_str_share_to);
						startActivity(Intent.createChooser(share, title)); 
					} catch (Exception e) {
						LogUtil.e(TAG, "Exception: " + e);
						e.printStackTrace();
					}
            	return true;
            }
            // add by putao_lhq 2014年10月17日 for BUG #1606 end
            overriteUrl = true;////putao_lhq add for BUG #1476
            return super.putao_shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void putao_onPageStarted(WebView view, String url, Bitmap favicon) {
            LogUtil.d(TAG, "putao_onPageStarted");
            mPageFinished = false;
			
			if (mFirstLoadHomePage && url.equals(mUrl) && !isFinishing()) {
				mFirstLoadHomePage = false;
				LogUtil.d(TAG, "start progress.");				
				mHandler.sendEmptyMessage(MSG_SHOW_DIALOG_ACTION);
				mHandler.sendEmptyMessageDelayed(MSG_DISMISS_TIMEDOUT_ACTION, TIMEDOUT);
				updateProgressBar(initProgress);//add ljq 2014 11 07 开始读取时制造假进度
			}
            super.putao_onPageStarted(view, url, favicon);
        }

        @Override
        public void putao_onPageFinished(WebView view, String url) {
            LogUtil.d(TAG, "putao_onPageFinished");
            CookieSyncManager.getInstance().sync();
			
			mPageFinished = true;
			
            if(mPageFinished) {
				LogUtil.d(TAG, "close progress.");	
				mHandler.sendEmptyMessage(MSG_DISMISS_DIALOG_ACTION);
            }
            //putao_lhq add for BUG #1476 start
            if (!overriteUrl) {
            	LogUtil.d(TAG, "clear cache reload url" );
            	mWebView.clearCache(true);
            	loadUrl();
            }
            //putao_lhq add for BUG #1476 end
        }

        @Override
        public void putao_onReceivedError(WebView view, int errorCode, String description,
                String failingUrl) {
            // TODO Auto-generated method stub
            LogUtil.d(TAG, "putao_onReceivedError description="+description+" errorCode="+errorCode);
            super.putao_onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public void putao_onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler,
                String host, String realm) {
            LogUtil.d(TAG, "putao_onProgressChanged");
            // TODO Auto-generated method stub
            super.putao_onReceivedHttpAuthRequest(view, handler, host, realm);
        }

        @Override
        public void putao_onReceivedLoginRequest(WebView view, String realm, String account,
                String args) {
            LogUtil.d(TAG, "putao_onReceivedLoginRequest");
            // TODO Auto-generated method stub
            super.putao_onReceivedLoginRequest(view, realm, account, args);
        }

        @Override
        public void putao_onLoadResource(WebView view, String url) {
            // TODO Auto-generated method stub
            LogUtil.d(TAG, "putao_onLoadResource");
            mWebView.loadUrl("javascript:" + doJs());
            super.putao_onLoadResource(view, url);
        }

        @Override
        public boolean putao_onJsAlert(WebView view, String url, String message, JsResult result) {
            // TODO Auto-generated method stub
            LogUtil.d(TAG, "putao_onJsAlert");
            return super.putao_onJsAlert(view, url, message, result);
        }

        @Override
        public boolean putao_onJsPrompt(WebView view, String url, String message,
                String defaultValue, JsPromptResult result) {
            // TODO Auto-generated method stub
            LogUtil.d(TAG, "putao_onJsPrompt");
            return super.putao_onJsPrompt(view, url, message, defaultValue, result);
        }

        @Override
        public boolean putao_onJsConfirm(WebView view, String url, String message, JsResult result) {
            // TODO Auto-generated method stub
            LogUtil.d(TAG, "putao_onJsConfirm");
            return super.putao_onJsConfirm(view, url, message, result);
        }

        @Override
        public void putao_onProgressChanged(WebView view, int newProgress) {
            LogUtil.d(TAG, "putao_onProgressChanged");
            updateProgressBar(newProgress);			
			if(mPageFinished && 100 == newProgress) {
				LogUtil.d(TAG, "close progress.");	
				mHandler.sendEmptyMessage(MSG_DISMISS_DIALOG_ACTION);
			}
        }

        @Override
        public void putao_onReceivedTitle(WebView paramWebView, String paramString) {
            LogUtil.d(TAG, "putao_onReceivedTitle");
            mHandler.removeMessages(MSG_DISMISS_TIMEDOUT_ACTION);			
			if(mPageFinished && progress==100) {
				LogUtil.d(TAG, "close progress.");	
				mHandler.sendEmptyMessage(MSG_DISMISS_DIALOG_ACTION);
			}
        }
        
        
    }

    // 友盟统计：进入时间
    private long startTime = 0L;
    
    @Override
    protected void onResume() {
        MobclickAgentUtil.onResume(this);
        startTime = System.currentTimeMillis();
        super.onResume();
    }

    @Override
    protected void onPause() {
        MobclickAgentUtil.onPause(this);
        try {
            int time = ((int) ((System.currentTimeMillis() - startTime) / 1000));
            Map<String, String> map_value = new HashMap<String, String>();
            map_value.put("type", this.getClass().getName());
//            com.putao.analytics.MobclickAgentUtil.onEventValue(this,
//                    UMengEventIds.DISCOVER_YELLOWPAGE_RESUME_TIME_19, map_value, time);
            MobclickAgentUtil.onEventValue(this,
                    UMengEventIds.DISCOVER_YELLOWPAGE_RESUME_TIME_19, map_value, time);
        } catch (Exception e) {
        }
        super.onPause();
    }
    //putao_lhq modify for BUG #1378 BUG #1532 start
    private String doJs() {
		String str = "try { "
				+
				"var footer=document.getElementById('common-bottombanner-widget-fis');"
				+ "if(footer) {  footer.style.display = 'none';  footer.parentNode.removeChild(footer); }"
				+ "footer = document.getElementsByClassName('btn-group')[0];"
				+ "if(footer) {	 footer.style.display = 'none';  footer.parentNode.removeChild(footer); }"
				+ "var nav_footer = document.getElementById('app-button-footer');"
				+ "if(nav_footer) {  nav_footer.style.display = 'none';  nav_footer.parentNode.removeChild(nav_footer); }"
				+
				"} catch (e) {}";
		return str;
    }
    //putao_lhq modify for BUG #1378  BUG #1532 end
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    
    @Override
    public void onLocationChanged(String city, double latitude, double longitude, long time) {
        //mUrl = "http://api.map.baidu.com/direction?origin=latlng:34.264642646862,108.95108518068|name:我家&destination=大雁塔&mode=driving&region=西安&src=putao|yellowpage";
        //mWebView.loadUrl(mUrl);
        LogUtil.d(TAG, "Location success, city:"+city+" latitude:"+latitude+" longitude:"+longitude);
        //LBSServiceGaode.deactivate();
    }

    @Override
    public void onLocationFailed() {
        LogUtil.d(TAG, "Location failed.");
        //LBSServiceGaode.deactivate();
    }
    //putao_lhq add for BUG #1508 start
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        LogUtil.d(TAG, "cur url: " + mWebView.getUrl());        
    	if ((keyCode == KeyEvent.KEYCODE_BACK) && 
    			(mWebView == null || TextUtils.isEmpty(mWebView.getUrl()) || mWebView.getUrl().endsWith("?third_party=uri_api"))) {   	    
    		if (getApplicationInfo().targetSdkVersion
                    >= Build.VERSION_CODES.ECLAIR) {
                event.startTracking();
            } else {
                onBackPressed();
            }
            return true;
        }
    	return super.onKeyDown(keyCode, event);
    }
    //putao_lhq add for BUG #1508 end

	@Override
	public boolean needMatchExpandParam() {
		return false;
	}
	
	@Override
	public String getServiceName() {
		return this.getClass().getName();
	}
	
	@Override
	public String getServiceNameByUrl() {
		return this.getClass().getName();//url 不固定，以class name为服务名 
	}
}
