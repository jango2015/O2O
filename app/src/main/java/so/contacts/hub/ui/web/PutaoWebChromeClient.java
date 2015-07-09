package so.contacts.hub.ui.web;

import so.contacts.hub.util.LogUtil;
import android.content.Context;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class PutaoWebChromeClient extends WebChromeClient {
	private static final String TAG = "PutaoWebChromeClient";
	private Context mContext = null;
	
	private TitleChangedListener mTitleChangedListener = null;
	
	private IProgressChangedListener mIProgressChangedListener = null;
	
	public void setmTitleChangedListener(TitleChangedListener mTitleChangedListener) {
		this.mTitleChangedListener = mTitleChangedListener;
	}
	
	public void setProgressChangedListener(IProgressChangedListener iProgressChangedListener) {
		this.mIProgressChangedListener = iProgressChangedListener;
	}

	public PutaoWebChromeClient(Context context) {
		mContext = context;
	}

	public void onGeolocationPermissionsShowPrompt(String paramString,
			GeolocationPermissions.Callback paramCallback) {
		paramCallback.invoke(paramString, true, false);
		LogUtil.d(TAG, "onGeolocationPermissionsShowPrompt paramString: "+paramString);
	}

    @Override
	public boolean onJsAlert(WebView view, String url, String message,
			JsResult result) {
    	LogUtil.d(TAG, "onJsAlert url:"+url+" message:"+message);
		return super.onJsAlert(view, url, message, result);
	}

	@Override
	public boolean onJsPrompt(WebView view, String url, String message,
			String defaultValue, JsPromptResult result) {
		LogUtil.d(TAG, "onJsPrompt url:"+url+" message:"+message);
		return super.onJsPrompt(view, url, message, defaultValue, result);
	}

	@Override
    public boolean onJsConfirm(WebView view, String url, String message,
            final JsResult result) {
		LogUtil.d(TAG, "onJsConfirm url:"+url+" message:"+message+" result:"+result.toString());
	    return super.onJsConfirm(view, url, message, result);
    }	
	
	/*
	public boolean onJsAlert(WebView paramWebView, String paramString1,
			String paramString2, JsResult paramJsResult) {
		LogUtil.d(TAG, "onJsAlert paramString1: "+paramString1+" paramString2:"+paramString2+" paramJsResult:"+paramJsResult.toString());
		return true;
	}

	public boolean onJsConfirm(WebView paramWebView, String paramString1,
			String paramString2, JsResult paramJsResult) {
		LogUtil.d(TAG, "onJsConfirm paramString1: "+paramString1+" paramString2:"+paramString2+" paramJsResult:"+paramJsResult.toString());
		
		return true;
	}
	
	@Override
	public boolean onJsPrompt(WebView view, String url, String message,
			String defaultValue, JsPromptResult result) {
		LogUtil.d(TAG, "onJsPrompt url:"+url+" message:"+message);
		return super.onJsPrompt(view, url, message, defaultValue, result);
	}
*/	

	public void onProgressChanged(WebView paramWebView, int paramInt) {
//		LogUtil.d(TAG, "onProgressChanged paramInt: "+paramInt);
		if( null != mIProgressChangedListener ){
			mIProgressChangedListener.onProgressChanged(paramWebView, paramInt);
		}
	}

	public void onReceivedTitle(WebView paramWebView, String paramString) {
		LogUtil.d(TAG, "onReceivedTitle paramString: "+paramString);
		if(null != mTitleChangedListener){
			mTitleChangedListener.onTitleChanged(paramWebView, paramString);
		}
	}
	
	public interface TitleChangedListener {
		void onTitleChanged(WebView paramWebView, String title);
	}
	
	public interface IProgressChangedListener {
		void onProgressChanged(WebView paramWebView, int paramInt);
	}
}