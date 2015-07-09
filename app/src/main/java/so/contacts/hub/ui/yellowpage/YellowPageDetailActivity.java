package so.contacts.hub.ui.yellowpage;

import so.contacts.hub.util.MobclickAgentUtil;

import com.yulong.android.contacts.discover.R;

import so.contacts.hub.ui.web.PutaoWebClientProxy;
import so.contacts.hub.ui.web.YellowPageH5Activity;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.PutaoH5JSBridge;
import so.contacts.hub.widget.CommonDialog;
import so.contacts.hub.widget.CommonDialogFactory;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebView;
import android.widget.TextView;

public class YellowPageDetailActivity extends YellowPageH5Activity implements OnClickListener {
	private static final String TAG = YellowPageDetailActivity.class.getSimpleName();

	protected boolean mPageFinished = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.d(TAG, "url=" + mUrl);
	}

	@Override
	protected void configWebSettings() {
		super.configWebSettings();

        mWebView.addJavascriptInterface(new PutaoH5JSBridge(this), "PutaoH5JSBridge");

		// 设置cookie必须在WebSetting之后，否则会失效
		CookieSyncManager.createInstance(YellowPageDetailActivity.this);		

		CookieManager cm = CookieManager.getInstance();
		if( cm != null ){
			cm.setAcceptCookie(true);
	        String cookie = "";
	        if(mUrl != null && mUrl.length()>0){
	            cookie = cm.getCookie(mUrl);
	        }
			LogUtil.v(TAG, "getCookie url="+mUrl+" cookie="+cookie+" acceptCookie="+cm.acceptCookie());
		}

	}

	@Override
	public PutaoWebClientProxy getPutaoWebClientProxy(Context context, Handler h) {
		return new MyPutaoWebClientProxy(this, mHandler);
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
			mWebView.loadUrl("javascript:" + doJs());//add by putao_lhq for remove footer of dianpin
            if(mPageFinished) {
				LogUtil.d(TAG, "close progress.");	
				mHandler.sendEmptyMessage(MSG_DISMISS_DIALOG_ACTION);
            }
		}	

		@Override
		public void putao_onReceivedTitle(WebView view, String title) {
			LogUtil.d(TAG, "putao_onReceivedTitle title="+title+" progress="+progress+" url= "+view.getUrl());
		
			mHandler.removeMessages(MSG_DISMISS_TIMEDOUT_ACTION);			
			if(mPageFinished && progress==100) {
				LogUtil.d(TAG, "close progress.");	
				mHandler.sendEmptyMessage(MSG_DISMISS_DIALOG_ACTION);
			}
		}

		@Override
		public void putao_onProgressChanged(WebView view, int newProgress) {
			LogUtil.d(TAG, "putao_onProgressChanged progress="+newProgress);
			
			updateProgressBar(newProgress);			
			if(mPageFinished && 100 == newProgress) {
				LogUtil.d(TAG, "close progress.");	
				mHandler.sendEmptyMessage(MSG_DISMISS_DIALOG_ACTION);
			}
		}	
				
		@Override
		public void putao_onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			LogUtil.d(TAG, "putao_onReceivedError errorCode="+errorCode+" desc="+description+" failurl="+failingUrl);
//			view.stopLoading();
//			mHandler.sendEmptyMessage(MSG_RECEIVED_ERROR_ACTION);
		}
		
		@Override
		public void putao_onLoadResource(WebView view, String url) {
			LogUtil.d(TAG, "putao_onLoadResource url="+url);
		}

		@Override
		public boolean putao_onJsAlert(WebView view, String url,
				String message, JsResult result) {
			final CommonDialog alertDialog = CommonDialogFactory.getOkCancelCommonDialog(YellowPageDetailActivity.this);
			alertDialog.setTitle(R.string.putao_point_out);
			TextView msgTv = alertDialog.getMessageTextView();
			msgTv.setText(message);
			alertDialog.setOkButtonClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					alertDialog.dismiss();
				}
			});
			alertDialog.getCancelButton().setVisibility(View.GONE);
	        // 不需要绑定按键事件  
	        // 屏蔽keycode等于84之类的按键  
	        alertDialog.setOnKeyListener(new OnKeyListener() {  
	            public boolean onKey(DialogInterface dialog, int keyCode,KeyEvent event) {  
	                Log.v("onJsAlert", "keyCode==" + keyCode + "event="+ event);  
	                return true;  
	            }
	        });  
	        
	        // 禁止响应按back键的事件  
	        alertDialog.setCancelable(false);
	        alertDialog.show();
	        result.confirm();// 因为没有绑定事件，需要强行confirm,否则页面会变黑显示不了内容。  
	        return true;  
		}

		@Override
		public boolean putao_onJsConfirm(WebView view, String url,
				String message, JsResult result) {
			result.confirm();
			return true;
		}

		@Override
		public boolean putao_onJsPrompt(WebView view, String url,
				String message, String defaultValue, JsPromptResult result) {
			result.confirm(defaultValue);
			return true;
		}
	
	};		
	//putao_lhq add for 去掉点评底部 start
	protected String doJs() {
		String str = "try { "
				+
				"var footer=document.getElementsByClassName('footer appload')[0];"
				+ "if(footer) {  footer.style.display = 'none';  footer.parentNode.removeChild(footer); }"
				+
				"} catch (e) {}";
		return str;
    }
	//putao_lhq add for 去掉点评底部 end
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
	public boolean needMatchExpandParam() {
		return true;
	}
}
