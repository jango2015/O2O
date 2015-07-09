package so.contacts.hub.ui.yellowpage;

import so.contacts.hub.ui.web.PutaoWebClientProxy;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.PutaoH5JSBridge;
import so.contacts.hub.util.UiHelper;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ProgressBar;
import so.contacts.hub.util.MobclickAgentUtil;

import com.yulong.android.contacts.discover.R;

/**
 * 
 * @author putao_lhq
 * @version 2014年10月19日
 */
public class WebViewDialog extends Activity {

	private static final String TAG = "WebViewDialog";
	public static final String URL = "url_name";
	private WebView mWebView;
	private ProgressBar mProgressBar;
	private String url;
	private MyPutaoWebClientProxy mWebProxy;
	
	private static final int MSG_LOAD_FINISH = 10;
	@SuppressLint("HandlerLeak") 
	private Handler handler = new Handler() {
		
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_LOAD_FINISH:
				if (mWebView != null) {
					showWebView(true);
				} else {
					finish();
				}
				break;

			default:
				break;
			}
		}

	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/**
         *add code
         *modify by putao_lhq
         *coolui6.0
         *-->start */
         if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
             // 设置托盘透明
             getWindow().addFlags(
                     WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
         } else {
         } /*<--end*/
		setContentView(R.layout.putao_webview_dialog);
		LogUtil.d(TAG, "onCreate");
		this.url = getIntent().getStringExtra(URL);
		if (TextUtils.isEmpty(url)) {
			LogUtil.d(TAG, "url is null");
			finish();
			return;
		}
		//initWindow();
		mWebView = (WebView) findViewById(R.id.webView);
		mProgressBar = (ProgressBar) findViewById(R.id.progress);
		configWebSettings();

		mWebProxy = new MyPutaoWebClientProxy(this, null);
		mWebView.setWebChromeClient(mWebProxy.getWebChromeClient());
        mWebView.setWebViewClient(mWebProxy.getWebViewClient());
		mWebView.loadUrl(url/*"file:///android_asset/active_egg.html"*/);
	}
	
	private void showWebView(boolean show) {
		if (show) {
			mWebView.setVisibility(View.VISIBLE);
			mProgressBar.setVisibility(View.GONE);
		} else {
			mWebView.setVisibility(View.GONE);
			mProgressBar.setVisibility(View.VISIBLE);
		}
	}

	@SuppressLint("SetJavaScriptEnabled") 
	protected void configWebSettings() {
        LogUtil.i(TAG, "configWebSettings");
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setLoadsImagesAutomatically(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setDefaultTextEncodingName("UTF-8");
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.requestFocus();
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.addJavascriptInterface(new PutaoH5JSBridge(this), "PutaoH5JSBridge");
    }
	
	private void initWindow(){
		final DisplayMetrics displayMetrics = UiHelper.getDisplayMetrics(this);
		int screenWidth = displayMetrics.widthPixels;
		int screenHeight = displayMetrics.heightPixels;
		//int padding = UiHelper.getDialogPadding(this);//putao_lhq modify for fullscreen
//		getWindow().setBackgroundDrawable(android.R.color.transparent);
		WindowManager.LayoutParams p = getWindow().getAttributes();
		p.width = screenWidth ;//- padding - padding;//putao_lhq modify for fullscreen
		p.height = screenHeight;//(int)(screenHeight * 0.618);//putao_lhq modify for fullscreen
		getWindow().setAttributes(p);
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

	class MyPutaoWebClientProxy extends PutaoWebClientProxy {

		public MyPutaoWebClientProxy(Context ctx, Handler h) {
			super(ctx, h);
		}
		
		@Override
		public boolean putao_shouldOverrideUrlLoading(WebView view, String u) {
			url = u;
			return super.putao_shouldOverrideUrlLoading(view, url);
		}
		
		@Override
		public void putao_onPageFinished(WebView view, String url) {
			if (isWebViewShow()) {
				LogUtil.d(TAG, "webview is show");
				return;
			}
			handler.sendEmptyMessage(MSG_LOAD_FINISH);
		}
	}
	
	private boolean isWebViewShow() {
		if (mWebView == null || mWebView.getVisibility() != View.VISIBLE) {
			return false;
		} else {
			return true;
		}
	}
}
