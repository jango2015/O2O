package so.contacts.hub.ui.web;

import so.contacts.hub.util.LogUtil;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebView;
import so.contacts.hub.util.MobclickAgentUtil;

public class YellowPageCtripHotelActivity extends YellowPageH5Activity{
	private static final String TAG = YellowPageCtripHotelActivity.class.getSimpleName();

	private boolean mPageFinished = false;

	private int mLastProgressData = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		LogUtil.d(TAG, "url="+mUrl);
	}
	
	@Override
	public PutaoWebClientProxy getPutaoWebClientProxy(Context context, Handler h) {
		// TODO Auto-generated method stub
		return new MyPutaoWebClientProxy(this, mHandler);
	}
	
	@Override
	protected void configWebSettings() {
		super.configWebSettings();
		
		// 设置cookie必须在WebSetting之后，否则会失效
		CookieSyncManager.createInstance(YellowPageCtripHotelActivity.this);		

		CookieManager cm = CookieManager.getInstance();
		cm.setAcceptCookie(true);
	    String cookie = "";
	    if(mUrl != null && mUrl.length()>0){
	        cookie = cm.getCookie(mUrl);
	    }
		LogUtil.d(TAG, "getCookie url="+mUrl+" cookie="+cookie+" acceptCookie="+cm.acceptCookie());
		
		mHandler.sendEmptyMessage(MSG_UPDATE_PROGRESS_ACTION);
	}
	
	public class MyPutaoWebClientProxy extends PutaoWebClientProxy {
		public MyPutaoWebClientProxy(Context ctx, Handler h) {
			super(ctx, h);
		}
		
		public boolean putao_shouldOverrideUrlLoading(WebView view, String url) {
			LogUtil.d(TAG,"putao_shouldOverrideUrlLoading="+url);
			mUrl = url;
			
			return super.putao_shouldOverrideUrlLoading(view, url);
		}		
		
		@Override
		public void putao_onPageStarted(WebView view, String url, Bitmap favicon) {
			LogUtil.d(TAG, "putao_onPageStarted progress="+progress+" url="+url);
			
			mPageFinished = false;
			
			if (mFirstLoadHomePage && url.equals(mUrl) && !isFinishing()) {
				mFirstLoadHomePage = false;
				LogUtil.d(TAG, "start progress.");				
				mHandler.sendEmptyMessage(MSG_SHOW_DIALOG_ACTION);
				mHandler.sendEmptyMessageDelayed(MSG_DISMISS_TIMEDOUT_ACTION, TIMEDOUT);
				updateProgressBar(initProgress);//add ljq 2014 11 07 开始读取时制造假进度
			}
		}
		
		@Override
		public void putao_onPageFinished(WebView view, String url) {
			LogUtil.d(TAG, "putao_onPageFinished progress="+progress+" url="+url);
			
			CookieSyncManager.getInstance().sync();
			
			mPageFinished = true;
            if(!TextUtils.isEmpty(view.getUrl()) && view.getUrl().equals(mUrl)  && progress==100) {
				LogUtil.d(TAG, "close progress.");	
				mHandler.sendEmptyMessage(MSG_DISMISS_DIALOG_ACTION);
            }
		}	

		@Override
		public void putao_onReceivedTitle(WebView paramWebView,
				String paramString) {
			mHandler.removeMessages(MSG_DISMISS_TIMEDOUT_ACTION);
		}

		@Override
		public void putao_onProgressChanged(WebView view, int newProgress) {
			String loadUrl = view.getUrl();
			LogUtil.d(TAG, "putao_onProgressChanged url= " + loadUrl);
			
			/**
			 * 携程的网页加载情况是：同一Url会多次加载(0 - 100)
			 * 规律是最后一次加载情况比较正常，前几次加载一般递增幅度很大（如：0,100 和 0,5,43,100 )
			 * 因此目前解决方案是：对于相隔间隙增幅超过50的予以过滤。
			 */
			if( newProgress - mLastProgressData > 50 ){
				mLastProgressData = 0;
				return;
			}
			mLastProgressData = newProgress;
			LogUtil.d(TAG, "putao_onProgressChanged progress= " + newProgress);
			
			
			updateProgressBar(newProgress);			
			if(!TextUtils.isEmpty(view.getUrl()) && mUrl.equals(view.getUrl())  && mPageFinished && 100 == newProgress) {
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
		
		@Override
		public boolean putao_onJsAlert(WebView view, String url,
				String message, JsResult result) {
			LogUtil.d(TAG, "putao_onJsAlert message="+message);
			result.confirm();
			return true;
		}

		@Override
		public boolean putao_onJsConfirm(WebView view, String url,
				String message, JsResult result) {
			LogUtil.d(TAG, "putao_onJsConfirm message="+message);
			result.confirm();
			return true;
		}

		@Override
		public boolean putao_onJsPrompt(WebView view, String url,
				String message, String defaultValue, JsPromptResult result) {
			LogUtil.d(TAG, "putao_onJsPrompt message="+message+" defaultValue="+defaultValue);
			result.confirm(defaultValue);
			return true;
		}
		
	};	
	
	private String onloadJs_def() {
		String jscode = 
				"try { "+
						"var header = document.getElementsByTagName('header');"+
						"if(header && header[0]) {"+
						"  header[0].style.display = 'none';"+
					    "  header[0].parentNode.removeChild(header[0]);"+
						"}"+

			     "} catch (e) {alert('加载失败. '+e.name+':'+e.message); }";

		return jscode;
	}
	
	private String jsDef(){
		String jsCode = "var headerView = document.getElementById('headerview');" +
						" var header = headerView.getElementsByTagName('header');" +
						"if(header && header[0]) {"+
						"  header[0].style.display = 'none';"+
					    "  headerView.removeChild(header[0]);"+
						"}"+
				        " headerView.style.display = 'none';";
		return jsCode;
	}
	    
	@Override
	protected void onResume() {
		MobclickAgentUtil.onResume(this);
		super.onResume();
	}

	@Override
	protected void onPause() {
		MobclickAgentUtil.onPause(this);
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
