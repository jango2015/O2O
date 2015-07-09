package so.contacts.hub.ui.web;

import java.util.HashMap;
import java.util.Map;

import so.contacts.hub.util.MobclickAgentUtil;

import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.UMengEventIds;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebView;

public class YellowPageGuahaoActivity extends YellowPageH5Activity{
	private static final String TAG = YellowPageGuahaoActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		LogUtil.d(TAG, "url="+mUrl);
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
		public void putao_onReceivedTitle(WebView view, String title) {
			LogUtil.d(TAG, "onReceivedTitle url: "+view.getUrl());
			
			mHandler.removeMessages(MSG_DISMISS_TIMEDOUT_ACTION);
			String loadjs= onloadJs_def();				
			mWebView.loadUrl("javascript:" + loadjs);
		}

		@Override
		public void putao_onProgressChanged(WebView view, int newProgress) {
			if(newProgress == 0 && mFirstLoadHomePage){
				mFirstLoadHomePage = false;
				mHandler.sendEmptyMessage(MSG_SHOW_DIALOG_ACTION);
				mHandler.sendEmptyMessageDelayed(MSG_DISMISS_TIMEDOUT_ACTION, TIMEDOUT);	
			}
			
			updateProgressBar(newProgress);
			if (newProgress == 100) {
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
		
	}
	/**
	 * 只取消了首页的footer和navi导航，其他内页把header放开
	 * @return
	 */
	private String onloadJs_def() {
		String jscode = 
				"try { "+
//						"var header = document.getElementsByTagName('header');"+
//						"if(header && header[0]) {"+
//						"  header[0].style.display = 'none';"+					
//					    "  header[0].parentNode.removeChild(header[0]);"+
//						"}"+
					    
//						"var gp-fastorder = document.getElementById('gp-fastorder');"+
//						"if(gp-fastorder) {"+
//						"  gp-fastorder.style = 'padding-top: 3px; padding-bottom: 53px; min-height: 443px;';"+					
//						"}"+
//
//						"var gp-depts = document.getElementById('gp-depts');"+
//						"if(gp-depts) {"+
//						"  gp-depts.style = 'padding-top: 3px; padding-bottom: 53px; min-height: 443px;';"+					
//						"}"+
//						
//						"var gp-experts = document.getElementById('gp-experts');"+
//						"if(gp-experts) {"+
//						"  gp-experts.style = 'padding-top: 3px; padding-bottom: 53px; min-height: 443px;';"+					
//						"}"+
//
//						"var gp-expert = document.getElementById('gp-expert');"+
//						"if(gp-expert) {"+
//						"  gp-expert.style = 'padding-top: 3px; padding-bottom: 53px; min-height: 443px;';"+					
//						"}"+
						
		
						"var footer = document.getElementsByTagName('footer');"+
						"if(footer && footer[0]) {"+
						"  footer[0].style.display = 'none';"+					
					    "  footer[0].parentNode.removeChild(footer[0]);"+
						"}"+
						
						"var fastorder_top='top';"+		
						"var idfooter='ui-footer ui-bar-f1 ui-footer-fixed slideup';"+
						"var navi='navi';"+
						"var navbar='navbar';"+
						"var classElements = [],allElements = document.getElementsByTagName('*');"+ 
						"for (var i=0; i< allElements.length; i++ )"+ 
						  "{ "+
//								"if (allElements[i].className == idfooter || allElements[i].className ==navi || allElements[i].className ==navbar || allElements[i].className==fastorder_top) {"+
								"if (allElements[i].className ==navi) {"+						  
									"	allElements[i].style.visibility = 'hidden';"+
								"}"+
						 "}"+		
			     "} catch (e) {alert('加载失败. '+e.name+':'+e.message); }";
				 
		return jscode;
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
//	        com.putao.analytics.MobclickAgentUtil.onEventValue(this,
//	                    UMengEventIds.DISCOVER_YELLOWPAGE_RESUME_TIME_17, map_value, time);
			MobclickAgentUtil.onEventValue(this,
					UMengEventIds.DISCOVER_YELLOWPAGE_RESUME_TIME_17, map_value, time);
		} catch (Exception e) {
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean needMatchExpandParam() {
		return true;
	}

}
