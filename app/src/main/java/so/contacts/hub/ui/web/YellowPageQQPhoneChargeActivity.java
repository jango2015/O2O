package so.contacts.hub.ui.web;

import so.contacts.hub.util.MobclickAgentUtil;

import so.contacts.hub.util.LogUtil;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebView;

public class YellowPageQQPhoneChargeActivity extends YellowPageH5Activity {
	private static final String TAG = "YellowPageQQPhoneChargeActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.d(TAG, "url=" + mUrl);
	}
	
	@Override
	public PutaoWebClientProxy getPutaoWebClientProxy(Context context, Handler h) {
		return new MyPutaoWebClientProxy(context, h);
	}
	
	public class MyPutaoWebClientProxy extends PutaoWebClientProxy {
		public MyPutaoWebClientProxy(Context ctx, Handler h) {
			super(ctx, h);
		}

		@Override
		public void putao_onPageStarted(WebView view, String url, Bitmap favicon) {
			if (mFirstLoadHomePage && url.equals(mUrl) && !isFinishing()) {
				mFirstLoadHomePage = false;
				mHandler.sendEmptyMessage(MSG_SHOW_DIALOG_ACTION);
				mHandler.sendEmptyMessageDelayed(MSG_DISMISS_TIMEDOUT_ACTION, TIMEDOUT);	
				updateProgressBar(initProgress);//add ljq 2014 11 07 开始读取时制造假进度
			}
		}
		
		@Override
		public void putao_onReceivedTitle(WebView view, String title) {
			LogUtil.d(TAG, "putao_onReceivedTitle title=" + title + " url= " + view.getUrl());

			String loadjs = onloadJs_def();
			mWebView.loadUrl("javascript:" + loadjs);
		}

		@Override
		public void putao_onProgressChanged(WebView view, int newProgress) {			
			updateProgressBar(newProgress);
			if(newProgress == 100) {
				mHandler.sendEmptyMessage(MSG_DISMISS_DIALOG_ACTION);
			}			
		}
	}

	private String onloadJs_def() {
		String jscode = "try { " + "var nav=document.getElementById('nav');"
				+ "if(nav) { nav.style.display = 'none';}" +

				"var header='header';"
				+ "var allElements = document.getElementsByTagName('div');"
				+ "for(var i=allElements.length-1; i>=0; i--)" + "{ "
				+ "if (allElements[i].className == header) {"
				+ "allElements[i].style.display = 'none';" + "}" + "}" +

				"} catch (e) {alert('加载失败. '+e.name+':'+e.message); }";

		return jscode;
	}

	private String onloadJs_finished() {
		return onloadJs_def();
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
