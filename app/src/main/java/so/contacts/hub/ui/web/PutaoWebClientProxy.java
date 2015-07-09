package so.contacts.hub.ui.web;

import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.UMengEventIds;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.yulong.android.contacts.discover.R;

import so.contacts.hub.util.LogUtil;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.HttpAuthHandler;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class PutaoWebClientProxy {
	private static final String TAG = PutaoWebClientProxy.class.getSimpleName();
	private Context mContext = null;

	private PutaoWebViewClient mWebViewClient = null;
	private PutaoWebChromeClient mWebChromeClient = null;

	private TitleChangedListener mTitleChangedListener = null;
	private IProgressChangedListener mIProgressChangedListener = null;
	
	protected int initProgress =2 ; //初始化假进度的值 必须大于1才有效

	private String mCurrentLoadUrl = null;

	public PutaoWebClientProxy(Context ctx, Handler h) {
		mContext = ctx;
	}

	public PutaoWebClientProxy(Context ctx, Handler h, TitleChangedListener l1,
			IProgressChangedListener l2) {
		mContext = ctx;
		mTitleChangedListener = l1;
		mIProgressChangedListener = l2;
	}

	public WebViewClient getWebViewClient() {
		if (mWebViewClient == null) {
			mWebViewClient = new PutaoWebViewClient();
		}
		return mWebViewClient;
	}

	public WebChromeClient getWebChromeClient() {
		if (mWebChromeClient == null) {
			mWebChromeClient = new PutaoWebChromeClient();
		}
		return mWebChromeClient;
	}

	public String getUrl() {
		return mCurrentLoadUrl;
	}

	public interface TitleChangedListener {
		void onTitleChanged(WebView paramWebView, String title);
	}

	public interface IProgressChangedListener {
		void onProgressChanged(WebView paramWebView, int paramInt);
	}

	private class PutaoWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			return putao_shouldOverrideUrlLoading(view, url);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			putao_onPageStarted(view, url, favicon);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			putao_onPageFinished(view, url);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			putao_onReceivedError(view, errorCode, description, failingUrl);
		}

		@Override
		public void onReceivedHttpAuthRequest(WebView view,
				HttpAuthHandler handler, String host, String realm) {
			super.onReceivedHttpAuthRequest(view, handler, host, realm);
			putao_onReceivedHttpAuthRequest(view, handler, host, realm);
		}

		@Override
		public void onReceivedLoginRequest(WebView view, String realm,
				String account, String args) {
			super.onReceivedLoginRequest(view, realm, account, args);
			putao_onReceivedLoginRequest(view, realm, account, args);
		}

		@Override
		public void onLoadResource(WebView view, String url) {
			super.onLoadResource(view, url);
			putao_onLoadResource(view, url);
		}
	};

	public class PutaoWebChromeClient extends WebChromeClient {
		@Override
		public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
			Log.d(TAG, "onConsoleMessage " + consoleMessage.message());
			return super.onConsoleMessage(consoleMessage);
		}

		@Override
		public void onConsoleMessage(String message, int lineNumber,
				String sourceID) {
			Log.d(TAG, "onConsoleMessage lineNumber=" + lineNumber
					+ " sourceID=" + sourceID + " message=" + message);
			super.onConsoleMessage(message, lineNumber, sourceID);
		}

		public void onGeolocationPermissionsShowPrompt(String paramString,
				GeolocationPermissions.Callback paramCallback) {
			super.onGeolocationPermissionsShowPrompt(paramString, paramCallback);
			LogUtil.d(TAG, "onGeolocationPermissionsShowPrompt paramString="
					+ paramString);
			putao_onGeolocationPermissionsShowPrompt(paramString, paramCallback);
		}

		@Override
		public boolean onJsAlert(WebView view, String url, String message,
				JsResult result) {
			if (putao_onJsAlert(view, url, message, result)) {
				// LogUtil.d(TAG, "onJsAlert message="+message+" true");
				return true;
			} else {
				// LogUtil.d(TAG, "onJsAlert message="+message+" false");
				return super.onJsAlert(view, url, message, result);
			}
		}

		@Override
		public boolean onJsPrompt(WebView view, String url, String message,
				String defaultValue, JsPromptResult result) {
			if (putao_onJsPrompt(view, url, message, defaultValue, result)) {
				// LogUtil.d(TAG, "onJsPrompt message="+message+" true");
				return true;
			} else {
				// LogUtil.d(TAG, "onJsPrompt message="+message+" false");
				return super.onJsPrompt(view, url, message, defaultValue,
						result);
			}
		}

		@Override
		public boolean onJsConfirm(WebView view, String url, String message,
				final JsResult result) {
			if (putao_onJsConfirm(view, url, message, result)) {
				// LogUtil.d(TAG, "onJsConfirm message="+message+" true");
				return true;
			} else {
				// LogUtil.d(TAG, "onJsConfirm message="+message+" false");
				return super.onJsConfirm(view, url, message, result);
			}
		}

		@Override
		public void onProgressChanged(WebView paramWebView, int newProgress) {
			putao_onProgressChanged(paramWebView, newProgress);
		}

		@Override
		public void onReceivedTitle(WebView paramWebView, String paramString) {
			putao_onReceivedTitle(paramWebView, paramString);
		}
	};

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void putao_loadUrl(WebView view, String url) {
		mCurrentLoadUrl = url;
		view.loadUrl(url);
	}

	/**
	 * 过滤拨打电话号码的url 
	 * 1、过滤掉括号（中文括号、英文括号）
	 * 2、过滤掉“/"的后面部分(有多个号码的会以"/"分开)
	 */
	private String filterTelUrl(String url) {
		if (TextUtils.isEmpty(url)) {
			return "";
		}
		String exeUrl = url;
		try {
			exeUrl = URLDecoder.decode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			exeUrl = url;
		}
		StringBuffer filterStr = new StringBuffer();
		int startPos = -1;
		int endPos = -1;
		if ((startPos = exeUrl.indexOf("("))  != -1) {
			// 截取英文括号
			filterStr.append(exeUrl.substring(0, startPos));
			endPos = exeUrl.indexOf(")");
			if (endPos != -1) {
				filterStr.append(exeUrl.substring(endPos + 1, exeUrl.length()));
			}
		} else if((startPos = exeUrl.indexOf(mContext.getResources().getString(R.string.putao_common_chinese_left_kh)))  != -1){
			// 截取中文括号
			filterStr.append(exeUrl.substring(0, startPos));
			endPos = exeUrl.indexOf(mContext.getResources().getString(R.string.putao_common_chinese_right_kh));
			if (endPos != -1) {
				filterStr.append(exeUrl.substring(endPos + 1, exeUrl.length()));
			}
		} else {
			filterStr.append(exeUrl);
		}
		String validNum = filterStr.toString();
		if( (startPos = validNum.indexOf("/"))  != -1 ){
			// 截取“/"的前面部分
			validNum = validNum.substring(0, startPos);
		}
		return validNum;
	}

	public boolean putao_shouldOverrideUrlLoading(WebView view, String url) {
		if (url.indexOf("tel:") == 0) {
			LogUtil.i(TAG, "putao_shouldOverrideUrlLoading filterUrl1: " + url);
			String filterUrl = filterTelUrl(url);
			LogUtil.i(TAG, "putao_shouldOverrideUrlLoading filterUrl2: " + filterUrl);
			Uri uri = Uri.parse(filterUrl);
			Intent intent = new Intent(Intent.ACTION_CALL, uri);
			mContext.startActivity(intent);
			MobclickAgentUtil.onEvent(mContext,
                    UMengEventIds.DISCOVER_YELLOWPAGE_H5_PAGE_CALL_ALL);

		} else if (url.indexOf("sms") == 0) {
			/**
			 * @author change 2014/08/05 webview提交短信请求,标准通用格式 sms:15919813679
			 */
			Uri smsToUri = Uri.parse(url);
			Intent sendIntent = new Intent(Intent.ACTION_VIEW, smsToUri);
			sendIntent.setType("vnd.android-dir/mms-sms");
			mContext.startActivity(sendIntent);

		} else {
			putao_loadUrl(view, url);
		}
		return true;
	}

	public void putao_onPageStarted(WebView view, String url, Bitmap favicon) {
	}

	public void putao_onPageFinished(WebView view, String url) {
	}

	public void putao_onReceivedError(WebView view, int errorCode,
			String description, String failingUrl) {
	}

	public void putao_onReceivedHttpAuthRequest(WebView view,
			HttpAuthHandler handler, String host, String realm) {
	}

	public void putao_onReceivedLoginRequest(WebView view, String realm,
			String account, String args) {
	}

	public void putao_onLoadResource(WebView view, String url) {
	}

	public void putao_onGeolocationPermissionsShowPrompt(String paramString,
			GeolocationPermissions.Callback paramCallback) {
		paramCallback.invoke(paramString, true, false);
	}

	public boolean putao_onJsAlert(WebView view, String url, String message,
			JsResult result) {
		return false;
	}

	public boolean putao_onJsPrompt(WebView view, String url, String message,
			String defaultValue, JsPromptResult result) {
		return false;
	}

	public boolean putao_onJsConfirm(WebView view, String url, String message,
			final JsResult result) {
		return false;
	}

	public void putao_onProgressChanged(WebView view, int newProgress) {
		if (null != mIProgressChangedListener) {
			mIProgressChangedListener.onProgressChanged(view, newProgress);
		}
	}

	public void putao_onReceivedTitle(WebView paramWebView, String paramString) {
		if (null != mTitleChangedListener) {
			mTitleChangedListener.onTitleChanged(paramWebView, paramString);
		}
	}

}
