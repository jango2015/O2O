package so.contacts.hub.ui.web;

import java.util.HashMap;
import java.util.Map;

import so.contacts.hub.account.PutaoAccount;
import so.contacts.hub.ui.web.kuaidi.WebBinder;
import so.contacts.hub.ui.web.kuaidi.plugin.LocationPlugin;
import so.contacts.hub.ui.web.kuaidi.plugin.NetworkTypePlugin;
import so.contacts.hub.util.ContactsHubUtils;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.UMengEventIds;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JsResult;
import android.webkit.WebView;
import android.widget.Toast;
import so.contacts.hub.util.MobclickAgentUtil;

import com.yulong.android.contacts.discover.R;

public class YellowPageKuaidiActivity extends YellowPageH5Activity {
	private static final String TAG = YellowPageKuaidiActivity.class.getSimpleName();

	private boolean mPageFinished = false;
	private WebBinder mWebBinder = null;
	private boolean isTest = false;
	
	private String mDialogStr = "Dialog";
	private String mOkStr = "OK";
	private String mCancelStr = "Cancel";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		if(!TextUtils.isEmpty(mUrl) ) {
			mUrl+="&mobile="+ContactsHubUtils.getPhoneNumber(this);
			
			if(isTest) {
				// demo测试界面
//				mUrl="http://t.cn/RvTzwW6";
				mUrl+="&latitude="+String.valueOf(30.2784662)+"&longitude="+String.valueOf(120.1194347)+"&mobile="+ContactsHubUtils.getPhoneNumber(this);

				// 打车测试界面
				mUrl="http://api.kuaidadi.com:9898/taxi/h5/index.htm?source=putaoxinxi&key=yruwioqpkdlmncvfw2ejd&order2pay=true&orderHis=true&home400=false";
//				mUrl="http://test.kuaidadi.com:4196/taxi/h5/index.htm?source=putaoxinxi&key=yruwioqpkdlmncvfw2ejd&order2pay=true&orderHis=true&home400=false";
			}

			
			if(isTest) {
				// demo测试界面
//				mUrl="http://t.cn/RvTzwW6";
				// 打车测试界面
				mUrl="http://test.kuaidadi.com:4196/taxi/h5/index.htm?source=putaoxinxi&key=yruwioqpkdlmncvfw2ejd";
			}
		}
		*/
		LogUtil.d(TAG, "url="+mUrl);
		initData();
	}
	
	private void initData(){
		mDialogStr = getString(R.string.putao_common_dialog);
		mOkStr = getString(R.string.putao_confirm);
		mCancelStr = getString(R.string.putao_cancel);
	}
		
	@Override
	protected void configWebSettings() {
		super.configWebSettings();

		// add by putao_lhq 2014年10月19日 for 添加获取酷云账号中手机号码 start
		String phoneNumber = PutaoAccount.getInstance().getBindMobile();
		if (TextUtils.isEmpty(phoneNumber)) {
			phoneNumber = ContactsHubUtils.getPhoneNumber(this);
		}
		if(!TextUtils.isEmpty(mUrl) ) {
			mUrl+="&mobile="+phoneNumber;
		}
		// add by putao_lhq 2014年10月19日 for 添加获取酷云账号中手机号码 end
		configWebBinder();
		
		// 设置cookie必须在WebSetting之后，否则会失效
		CookieSyncManager.createInstance(YellowPageKuaidiActivity.this);		

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
		return new MyPutaoWebClientProxy(context, h);
	}
	
	public class MyPutaoWebClientProxy extends PutaoWebClientProxy {
		public MyPutaoWebClientProxy(Context ctx, Handler h) {
			super(ctx, h);
		}
		
		@Override
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
			
			mHandler.removeMessages(MSG_DISMISS_TIMEDOUT_ACTION);
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
			LogUtil.d(TAG, "putao_onProgressChanged progress="+newProgress);
			
			updateProgressBar(newProgress);			
			if(!TextUtils.isEmpty(view.getUrl()) && mUrl.equals(view.getUrl())  && mPageFinished && 100 == newProgress) {
                LogUtil.d(TAG, "close progress.");
				mHandler.sendEmptyMessage(MSG_DISMISS_DIALOG_ACTION);
			}
		}			
		
//		public void onProgressChanged(WebView view, int newProgress) {
//			if(newProgress == 0 && mFirstLoadHomePage){
//				mFirstLoadHomePage = false;
//				mHandler.sendEmptyMessage(MSG_SHOW_DIALOG_ACTION);
//				mHandler.sendEmptyMessageDelayed(MSG_DISMISS_TIMEDOUT_ACTION, TIMEDOUT);	
//			}
//			
//			updateProgressBar(newProgress);
//			
//			if(newProgress == 100) {
//				mHandler.sendEmptyMessage(MSG_DISMISS_DIALOG_ACTION);
//			}
//		};

        @Override
		public boolean putao_onJsAlert(WebView view, String url, String message,
				JsResult result) {
        	LogUtil.d(TAG, "putao_onJsAlert url:"+url+" message:"+message);
        	result.confirm();
        	Toast.makeText(YellowPageKuaidiActivity.this, YellowPageKuaidiActivity.this.getResources().getString(R.string.putao_verify_code_error), 1000).show();
        	return true;
		}
		
		@Override
        public boolean putao_onJsConfirm(WebView view, String url, String message,
                final JsResult result) {
			LogUtil.d(TAG, "putao_onJsConfirm url:"+url+" message:"+message+" result:"+result.toString());
			
			if(YellowPageKuaidiActivity.this == null || 
			        YellowPageKuaidiActivity.this.isFinishing())return false;
			
            final AlertDialog.Builder builder = new AlertDialog.Builder(
                    view.getContext());
            builder.setTitle(mDialogStr)
                    .setMessage(message)
                    .setPositiveButton(mOkStr,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    result.confirm();
                                }
                            })
                    .setNeutralButton(mCancelStr,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    result.cancel();
                                }
                            });
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    result.cancel();
                }
            });

            // 屏蔽keycode等于84之类的按键，避免按键后导致对话框消息而页面无法再弹出对话框的问题
            builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode,
                        KeyEvent event) {
                    Log.v("onJsConfirm", "keyCode==" + keyCode + "event="
                            + event);
                    return true;
                }
            });
            // 禁止响应按back键的事件
            // builder.setCancelable(false);
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
            // return super.onJsConfirm(view, url, message, result);
        }
		
	}
	
	private void configWebBinder() {
		mWebBinder = new WebBinder();
		mWebBinder.prepare(this, mWebView);
		
        /**
         * 初始化InitData
         */
//        Module module = new Module(true, false, true);
//        InitDataBean dataBean = new InitDataBean(Configs.SOURCES,
//        		ContactsHubUtils.getPhoneNumber(this), Configs.ORDER_FROM, Configs.ORDER_TO, module);
//        InitDataPlugin initDataPlugin = new InitDataPlugin(dataBean);
//        mWebBinder.addPlugin("getInitData", initDataPlugin);
        
        mWebBinder.addPlugin("getLocation", LocationPlugin.class);
        mWebBinder.addPlugin("getNetworkType", NetworkTypePlugin.class);
        
		mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(mWebBinder, "webBinder");
        
        LogUtil.d(TAG, "configWebBinder ok");
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
//			com.putao.analytics.MobclickAgentUtil.onEventValue(this,
//                    UMengEventIds.DISCOVER_YELLOWPAGE_RESUME_TIME_19, map_value, time);
			MobclickAgentUtil.onEventValue(this,
					UMengEventIds.DISCOVER_YELLOWPAGE_RESUME_TIME_19, map_value, time);
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
