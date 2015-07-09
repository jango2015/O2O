
package so.contacts.hub.ui.web;

import java.util.HashMap;
import java.util.Map;
import so.contacts.hub.ContactsApp;
import so.contacts.hub.account.PutaoAccount;
import so.contacts.hub.active.ActiveUtils;
import so.contacts.hub.active.bean.ActiveEggBean;
import so.contacts.hub.core.PTUser;
import so.contacts.hub.remind.RemindConfig;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.UMengEventIds;
import so.contacts.hub.util.WebViewDialogUtils;
import so.putao.findplug.LBSServiceGaode;
import so.putao.findplug.LBSServiceGaode.LBSServiceListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebView;
import so.contacts.hub.util.MobclickAgentUtil;

/**
 * 团购h5页面，解决地理信息切换要确认的问题，默认为自动切换
 * 
 * @author change
 */
public final class YellowPageTuanActivity extends YellowPageH5Activity implements OnClickListener,
        LBSServiceListener {
    private static final String TAG = YellowPageTuanActivity.class.getSimpleName();

    private boolean mPageFinished = false;

    double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(TAG, "url=" + mUrl);
        initDazhongCallback();
    }

    @Override
    protected void configWebSettings() {
        super.configWebSettings();
        
		// 设置cookie必须在WebSetting之后，否则会失效
		CookieSyncManager.createInstance(YellowPageTuanActivity.this);		

		CookieManager cm = CookieManager.getInstance();
		cm.setAcceptCookie(true);
        String cookie = "";
        if(mUrl != null && mUrl.length()>0){
            cookie = cm.getCookie(mUrl);
        }

		LogUtil.d(TAG, "getCookie url="+mUrl+" cookie="+cookie+" acceptCookie="+cm.acceptCookie());        
        LogUtil.d(TAG, "start progress.");
        mHandler.sendEmptyMessage(MSG_SHOW_DIALOG_ACTION);
        mHandler.sendEmptyMessageDelayed(MSG_DISMISS_TIMEDOUT_ACTION, TIMEDOUT);
    }
    
    @Override
    protected void loadUrl() {
        mWebView.loadUrl(mUrl);
		mHandler.sendEmptyMessage(MSG_UPDATE_PROGRESS_ACTION);
    }

    private void configUrl() {
        if (!TextUtils.isEmpty(mUrl)) {
            /*
             * 每次进入定位影响加载效率，改为十分钟超时 才重新定位，不然则取上一次定位信息
             * update by hyl 2014-8-14
             *  old code :
             *  if(latitude == LBSServiceGaode.DEFAULT_LATITUDE || latitude == 0) {
                      LBSServiceGaode.activate(ContactsApp.getInstance(), this);
                  } else {
                       mUrl += "?latitude=" + String.valueOf(latitude) + "&longitude="
                        + String.valueOf(longitude);
                       mWebView.loadUrl(mUrl);
                  }
             */
            LBSServiceGaode.process_activate(ContactsApp.getInstance(), this);
            //update by hyl 2014-8-14
            
            LogUtil.d(TAG, "url=" + mUrl);
        }
    }

    @Override
    public PutaoWebClientProxy getPutaoWebClientProxy(Context context, Handler h) {
        return new MyPutaoWebClientProxy(this, mHandler);
    }
    
    /**
     * （点评）团购 首页切换城市 也需要显示title头
     * 注：（点评）团购 切换城市后 url中会包含group
     * by zjh 2014-8-14
     */
    private boolean needShowTitle(String url){
    	if(TextUtils.isEmpty(url)){
    		return false;
    	}
    	String[] strList = url.split("/");
    	if( strList == null || strList.length < 3 ){
    		return false;
    	}
    	int len = strList.length;
    	if( "group".equals(strList[len-2]) ){
    		// http://lite.m.dianping.com/group/shenzhen
    		return true;
    	}
    	return false;
    }

    public class MyPutaoWebClientProxy extends PutaoWebClientProxy {
        public MyPutaoWebClientProxy(Context ctx, Handler h) {
            super(ctx, h);
        }

        @Override
        public boolean putao_shouldOverrideUrlLoading(WebView view, String url) {
            LogUtil.d(TAG, "putao_shouldOverrideUrlLoading=" + url);
            
//            http://wappaygw.alipay.com/service/rest.htm?format=xml&partner=2088301426069753&req_data=%3Cauth_and_execute_req%3E%3Crequest_token%3E2014082072373ebeab6e04212dea16b3a8b431e1%3C%2Frequest_token%3E%3C%2Fauth_and_execute_req%3E&sec_id=MD5&service=alipay.wap.auth.authAndExecute&sign=7acb2b7ad054761124979e7286accf55&v=2.0&hasheader=0

            
            /*
             * 去掉title栏 update by zjh 2014-8-13 start
             */
            /*
             * bug:点击号码拨号时，在号码上带上了hasheader
             * 增加判断逻辑，当url为拨号 或者 短信时 不进入 处理 hasheader逻辑
             * update by hyl 2014-8-16
             * add : && !(url.startsWith("tel:") || url.startsWith("sms")) 
             * 
             * update by hyl 2014-8-20
             * add : url.startsWith("http://lite.m.dianping.com") //只有属于点评的url才处理 隐藏title
             */
            if (url.startsWith("http://lite.m.dianping.com") && !mHomePageUrl.equals(url)) {
            	if( !url.contains("hasheader") && !needShowTitle(url)){
            		if (url.contains("?")) {
            			url = url + "&hasheader=0";// 隐藏顶部栏 0-隐藏, 1-打开
            		} else {
            			url = url + "?hasheader=0";// 隐藏顶部栏 0-隐藏, 1-打开
            		}
            	}
            } else if(url.startsWith("tel:")) {
                // modified by cj for 修改团购多个电话之间用空格分割的问题,只拨号第一个 2015/01/10
                LogUtil.d(TAG, "putao_shouldOverrideUrlLoading filterUrl0:"+url);
                int spacePos = url.indexOf(" ");
                if(spacePos > 0) {
                    url = url.substring(0, spacePos);
                }
            }
            // update by zjh 2014-8-13 end
            mUrl = url;
            
            CookieSyncManager.getInstance().sync();
            
            return super.putao_shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void putao_onPageStarted(WebView view, String url, Bitmap favicon) {
            LogUtil.d(TAG, "putao_onPageStarted progress=" + progress + " url=" + url);

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
            LogUtil.d(TAG, "putao_onPageFinished progress=" + progress + " url=" + url);

            CookieSyncManager.getInstance().sync();
            
            mPageFinished = true;

            mHandler.removeMessages(MSG_DISMISS_TIMEDOUT_ACTION);
            if (mPageFinished) {
                LogUtil.d(TAG, "close progress.");
                mHandler.sendEmptyMessage(MSG_DISMISS_DIALOG_ACTION);
            }
        }

        @Override
        public void putao_onReceivedTitle(WebView view, String title) {
            LogUtil.d(TAG, "putao_onReceivedTitle title=" + title + " progress=" + progress
                    + " url= " + view.getUrl());

            mHandler.removeMessages(MSG_DISMISS_TIMEDOUT_ACTION);
            
            if (mPageFinished && progress == 100) {
                LogUtil.d(TAG, "close progress.");
                mHandler.sendEmptyMessage(MSG_DISMISS_DIALOG_ACTION);
            }
        }

        @Override
        public void putao_onProgressChanged(WebView view, int newProgress) {
            LogUtil.d(TAG, "putao_onProgressChanged progress=" + newProgress);

            updateProgressBar(newProgress);
            if (mPageFinished && 100 == newProgress) {
                LogUtil.d(TAG, "close progress.");
                mHandler.sendEmptyMessage(MSG_DISMISS_DIALOG_ACTION);
            }
        }

        @Override
        public void putao_onReceivedError(WebView view, int errorCode, String description,
                String failingUrl) {
//            view.stopLoading();
//            mHandler.sendEmptyMessage(MSG_RECEIVED_ERROR_ACTION);
        }

        @Override
        public boolean putao_onJsAlert(WebView view, String url, String message, JsResult result) {
            LogUtil.d(TAG, "putao_onJsAlert message=" + message);
            result.confirm();
            return true;
        }

        @Override
        public boolean putao_onJsPrompt(WebView view, String url, String message,
                String defaultValue, JsPromptResult result) {
            LogUtil.d(TAG, "putao_onJsPrompt message=" + message + " defValue=" + defaultValue);
            result.confirm(defaultValue);
            return true;
        }

        @Override
        public boolean putao_onJsConfirm(WebView view, String url, String message, JsResult result) {
            LogUtil.d(TAG, "putao_onJsPrompt message=" + message);
            result.confirm();
            return true;
        }
    };

    @Override
    public void onLocationChanged(String city, double latitude, double longitude, long time) {
        LBSServiceGaode.deactivate();

        this.latitude = latitude;
        this.longitude = longitude;

        mUrl += "?latitude=" + String.valueOf(latitude) + "&longitude=" + String.valueOf(longitude) +"&uid=12345";
        LogUtil.d(TAG, "onLocationChanged mUrl=" + mUrl);

        CookieSyncManager.getInstance().sync();
        
        /*
         * 添加团购用户标识uid
         * add by hyl 2014-9-22 start
         */
        PTUser ptUser = PutaoAccount.getInstance().getPtUser();
        if(ptUser != null){
            String ptUid = ptUser.getPt_uid();
            if(!TextUtils.isEmpty(ptUid)){
                mUrl = mUrl+"&uid="+ptUid;
            }
        }
        //add by hyl 2014-9-22 end
        
        loadUrl(mUrl);//modify by lisheng 2014-11-19 12:10:13
    }

    @Override
    public void onLocationFailed() {
        LBSServiceGaode.deactivate();

        latitude = 0;
        longitude = 0;

        mUrl += "?latitude=" + String.valueOf(latitude) + "&longitude="+ String.valueOf(longitude);
        LogUtil.d(TAG, "onLocationFailed mUrl=" + mUrl);
        
        /*
         * 添加团购用户标识uid
         * add by hyl 2014-9-22 start
         */
        PTUser ptUser = PutaoAccount.getInstance().getPtUser();
        if(ptUser != null){
            String ptUid = ptUser.getPt_uid();
            if(!TextUtils.isEmpty(ptUid)){
                mUrl = mUrl+"&uid="+ptUid;
            }
        }
        //add by hyl 2014-9-22 end
        
        loadUrl(mUrl);//modify by lisheng 2014-11-19 12:10:55
    }
    
    @Override
    protected void onResume() {
        MobclickAgentUtil.onResume(this);
        startTime = System.currentTimeMillis();
        super.onResume();
    }
    
    // 友盟统计：进入时间
    private long startTime = 0L;

    @Override
    protected void onPause() {
         MobclickAgentUtil.onPause(this);
            try {
                int time = ((int) ((System.currentTimeMillis() - startTime) / 1000));
                Map<String, String> map_value = new HashMap<String, String>();
                map_value.put("type", this.getClass().getName());
//                com.putao.analytics.MobclickAgentUtil.onEventValue(this,
//                        UMengEventIds.DISCOVER_YELLOWPAGE_RESUME_TIME_62 , map_value, time);
                MobclickAgentUtil.onEventValue(this,
                        UMengEventIds.DISCOVER_YELLOWPAGE_RESUME_TIME_62 , map_value, time);
            } catch (Exception e) {
            }
        super.onPause();
    }
    
    private void initDazhongCallback(){
        mWebView.addJavascriptInterface(new Object()  
        {  
            /** 
             *页面标题回调：展示到TextView中 
             */  
             @JavascriptInterface  
             public void setWebTitle(final String title) {  
          
                runOnUiThread(new Runnable() {  
                    @Override  
                    public void run(){  
//                        mTitleView.setText(title);  
                    }  
                });  
             }  
          
            /** 
             *订单数据回调：支付完成通过toast消息提示 
             */  
             @JavascriptInterface  
             public void setOrder(String orderId, String status, String amount, String quantity, String dealGroupId, String uid) {  
                 LogUtil.i(TAG, "orderId：" + orderId + " ,status：" + status + " ,amount：" + amount + " ,quantity：" + quantity 
                		 + " ,dealGroupId：" + dealGroupId + " ,uid：" + uid);

                 if(mService != null) {
                     try {
                         mService.addRemindBySelf(RemindConfig.REMIND_ADD, RemindConfig.MyOrderTuan, true);
                     } catch (RemoteException e){
                         e.printStackTrace();
                     }
                 } else {
                     sendRemindServiceBroadcast(RemindConfig.REMIND_ADD, RemindConfig.MyOrderTuan, true);
                 }
                 ActiveEggBean egg = ActiveUtils.getValidEgg("so.contacts.hub.ui.yellowpage.YellowPageTuanActivity", status);
                 if (egg != null) {
                	 String reqUrl = ActiveUtils.getRequrlOfSign(egg);
                	 if (!TextUtils.isEmpty(reqUrl)) {
                		 WebViewDialogUtils.startWebDialog(YellowPageTuanActivity.this, reqUrl);
                	 }
                 }
             }
        },"DPOpenJSBridge");  

    }
}
