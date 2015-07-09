
package so.contacts.hub.ui.yellowpage;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import so.contacts.hub.ui.web.PutaoWebClientProxy;
import so.contacts.hub.ui.web.YellowPageH5Activity;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.NetUtil;
import so.contacts.hub.util.PinyinConvUtil;
import so.contacts.hub.util.UMengEventIds;
import so.contacts.hub.util.WubaH5UrlHelper;
import so.putao.findplug.LBSServiceGaode;
import so.putao.findplug.LBSServiceGaode.LBSServiceListener;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.webkit.WebBackForwardList;
import android.webkit.WebView;

import so.contacts.hub.util.MobclickAgentUtil;

/**
 * 58city的子页面需要进度条,与其他h5有所不同,不使用mFirstHomePage标识
 * 
 * @author change
 */
public class YellowPageWubaCityActivity extends YellowPageH5Activity implements LBSServiceListener {
    private static final String TAG = "YellowPageWubaCityActivity";

    private boolean mPageFinished = false;

    private String mLoadUrl = "";

    private int mLastProgress = 0;

    private WubaH5UrlHelper mH5UrlCache = null;

    // 58同城 网页类型：招聘
    private static final int WEB_TYPE_ZHAOPING = 1;

    // 58同城 网页类型：租房
    private static final int WEB_TYPE_ZUFANG = 2;

    // 58同城 网页类型：家政
    private static final int WEB_TYPE_JIAZHENG = 3;

    // 加载首页面时是否重定向了（默认是重定向的，缓存是不需要的）
    private boolean mNeedRedirect = true;
    
    // 获取高德定位城市代码（如深圳：sz)
    private String mGaodeCityCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mH5UrlCache = new WubaH5UrlHelper(this);
        super.onCreate(savedInstanceState);
        LogUtil.d(TAG, "url=" + mUrl);
    }

    @Override
    protected void configWebSettings() {
        super.configWebSettings();
        new Thread(new Runnable() {
            @Override
            public void run() {
                LBSServiceGaode.process_activate(YellowPageWubaCityActivity.this,
                        YellowPageWubaCityActivity.this);
            }
        }).start();
        LogUtil.d(TAG, "start progress.");
        mHandler.sendEmptyMessage(MSG_SHOW_DIALOG_ACTION);
        mHandler.sendEmptyMessageDelayed(MSG_DISMISS_TIMEDOUT_ACTION, TIMEDOUT);
    }

    @Override
    protected void loadUrl() {
		mHandler.sendEmptyMessage(MSG_UPDATE_PROGRESS_ACTION);
    }

    private void configUrl(String city, double latitude, double longitude) {
        if (NetUtil.isNetworkAvailable(this)) {

            // 根据URL获得城市首字母(如：深圳为sz)
            String urlCityCode = "";
            if (mWebType != -1) {
                String url = mH5UrlCache.getInfo(mWebType);
                if (!TextUtils.isEmpty(url)) {
                    mNeedRedirect = false;
                    urlCityCode = mH5UrlCache.getCityCodeByUrl(url);
                    mUrl = url;
                }
            }
            LogUtil.d(TAG, "loadUrl url: " + mUrl + " ,mWebType: " + mWebType);
            // 深圳: &lat=22.534775&lon=113.94408
            // 福州：&lat=26.079062&lon=119.292277
            // 上海：&lat=31.241999&lon=121.409711
            // 北京：&lat=39.928391&lon=116.370529
            
            /*
			 * modify by ls 2015-1-27 start
			 * copyrigth by zjh
			 */
            if( latitude == 0 || longitude == 0 ){
            	//未定位到数据(添加深圳)//modity by ljq 2015/02/06 酷派项目默认定位深圳 old:bj 北京
            	latitude = 22.534775;
            	longitude = 113.94408;
            	mGaodeCityCode = "sz";
            }else{
                //String city = LBSServiceGaode.getCity();
                mGaodeCityCode = PinyinConvUtil.getCityPinyin(city);
            }
            // end 2015-1-27 by ls  
            
            
            if( mUrl.contains("?") ){
            	mUrl += "&lat=" + latitude + "&lon=" + longitude;
            }else{
            	mUrl += "?lat=" + latitude + "&lon=" + longitude;
            }

            //String city = LBSServiceGaode.getCity();
//            mGaodeCityCode = PinyinConvUtil.getCityPinyin(city);
            LogUtil.d(TAG, "loadUrl gaoDeCityCode:" + mGaodeCityCode + " ,urlCityCode: " + urlCityCode + " ,gaodeCity: " + city);
            if (!urlCityCode.equals(mGaodeCityCode)) {
                mNeedRedirect = true;
            }
            mLoadUrl = mUrl;
            
            //modify by lisheng 2014-11-19 14:36:59
//          mWebView.loadUrl(mUrl);
            loadUrl(mUrl);
            //modify by lisheng end
           
        } else {
            mHandler.sendEmptyMessage(MSG_RECEIVED_ERROR_ACTION);
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

        /**
         * TODO:在酷派手机上tel和sms无法唤起app,其他厂商手机可以,暂未解决
         */
        @Override
        public boolean putao_shouldOverrideUrlLoading(WebView view, String url) {
            LogUtil.d(TAG, "putao_shouldOverrideUrlLoading url=" + url);
            if (url.indexOf("tel:") == 0) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_CALL, uri);
                YellowPageWubaCityActivity.this.startActivity(intent);
                MobclickAgentUtil.onEvent(YellowPageWubaCityActivity.this,
                        UMengEventIds.DISCOVER_YELLOWPAGE_H5_PAGE_CALL_ALL);
                return true;// add by putao_lhq for 58 new API.
            } else if (url.indexOf("sms") == 0) {
                /**
                 * @author change 2014/08/05 webview提交短信请求,格式是58同城定义,不具有通用性
                 *         sms:15919813679
                 *         ?body=%E6%9C%89%E5%85%AC%E5%8F%B8%E5%8F
                 *         %AB%E8%BD%A6%E6
                 *         %8B%89%E8%B4%A7%E7%9A%84%E4%BB%8B%E7%BB%8D%E4%BA%BA
                 */
                try {
                    String[] result = url.split("\\?");
                    if (result.length == 2) {
                        String[] phone = result[0].split(":");
                        String[] body = result[1].split("=");

                        Uri smsToUri = Uri.parse(result[0]);
                        String decBody = URLDecoder.decode(body[1], "UTF-8");
                        Intent sendIntent = new Intent(Intent.ACTION_SENDTO, smsToUri);
//                        sendIntent.setType("vnd.android-dir/mms-sms");
//                        sendIntent.putExtra("address", phone[1]);
                        sendIntent.putExtra("sms_body", decBody);

                        YellowPageWubaCityActivity.this.startActivity(sendIntent);
                        return true;// add by putao_lhq for 58 new API.
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                mLoadUrl = url;
                //mWebView.loadUrl(url);// delete by putao_lhq for 58 new API.
            }
            return false;//true;// modify by putao_lhq for 58 new API.
        }

        @Override
        public void putao_onPageStarted(WebView view, String url, Bitmap favicon) {
            LogUtil.d(TAG, "putao_onPageStarted progress=" + progress + " url=" + url);
            mPageFinished = false;

            if (!mUrl.equals(url)) {
                LogUtil.d(TAG, "start progress 2.");
                mHandler.sendEmptyMessage(MSG_SHOW_DIALOG_ACTION);
                mHandler.sendEmptyMessageDelayed(MSG_DISMISS_TIMEDOUT_ACTION, TIMEDOUT);
                updateProgressBar(initProgress);//add ljq 2014 11 07 开始读取时制造假进度
            }
        }

        @Override
        public void putao_onPageFinished(WebView view, String url) {
            LogUtil.d(TAG, "putao_onPageFinished progress=" + progress + " url=" + url);
            mPageFinished = true;

            mHandler.removeMessages(MSG_DISMISS_TIMEDOUT_ACTION);
            if (mLoadUrl.equals(view.getUrl())) {
                LogUtil.d(TAG, "close progress 1.");
                mHandler.sendEmptyMessage(MSG_DISMISS_DIALOG_ACTION);
            }
        }

        @Override
        public void putao_onReceivedError(WebView view, int errorCode, String description,
                String failingUrl) {
            LogUtil.d(TAG, "putao_onReceivedError: " + failingUrl);
            mH5UrlCache.clearCache(mWebType);
            view.stopLoading();
            
            mHandler.removeMessages(MSG_DISMISS_TIMEDOUT_ACTION);
        }

        @Override
        public void putao_onProgressChanged(WebView view, int newProgress) {
        	String loadUrl = view.getUrl();
        	LogUtil.d(TAG, "putao_onProgressChanged progressFirst= " + progress + " ,newProgress= " + newProgress);
            
            //过滤（第一次重定向）: 第一次重定向直接从0调到100
            if (newProgress - mLastProgress == 100) {
            	mLastProgress = 0 ;
                return;
            }
            mLastProgress = newProgress;
             
            //过滤（第二次重定向）: 首页url所指向城市 与 定位城市 不在同一城市的
            String urlCityCode = mH5UrlCache.getCityCodeByUrl(loadUrl);
            if( !TextUtils.isEmpty(mGaodeCityCode) && !mGaodeCityCode.equals(urlCityCode)){
            	// 如果定位城市(cityCode) 与 58的Url中的城市 不一致，则不保存，也不更新进度
            	mLastProgress = 0 ;
            	return;
    		}

        	LogUtil.d(TAG, "putao_onProgressChanged progress= " + progress + " ,newProgress= " + newProgress);
            LogUtil.d(TAG, "putao_onProgressChanged loadUrl= " + loadUrl + " ,mGaodeCityCode: " + mGaodeCityCode);
            
            updateProgressBar(newProgress);
            if (100 == newProgress && mLoadUrl.equals(loadUrl)) {
                LogUtil.d(TAG, "close progress 2.");
                mH5UrlCache.checkAndAddInfo(mWebType, loadUrl, mGaodeCityCode);
                mHandler.sendEmptyMessage(MSG_UPDATE_PROGRESS_DISMISS_AND_INIT_ACTION);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        WebBackForwardList list = mWebView.copyBackForwardList();
        if (mNeedRedirect && list.getCurrentIndex() == 1) {
            mWebView.clearHistory();
        }

        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            /*
             * 当在网页中返回上一个web页面时，先记住上一个页面的url，为了 在进度中判断url是否加载完成来关闭进度．
             */
            int prevIdx = list.getCurrentIndex() - 1;
            if (prevIdx > 0) {
                String orgiUrl = list.getItemAtIndex(prevIdx).getOriginalUrl();
                mLoadUrl = list.getItemAtIndex(prevIdx).getUrl();
                LogUtil.d(TAG, "onKeyDown:back orgiUrl=" + orgiUrl + "\nloadUrl=" + mLoadUrl);
            }
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
        super.onPause();
        if (mYellowParams != null) {
            String uMengTypeId = "";
            if (mYellowParams.getProvider() == WEB_TYPE_ZHAOPING) {
                uMengTypeId = UMengEventIds.DISCOVER_YELLOWPAGE_RESUME_TIME_12;
            } else if (mYellowParams.getProvider() == WEB_TYPE_ZUFANG) {
                uMengTypeId = UMengEventIds.DISCOVER_YELLOWPAGE_RESUME_TIME_13;
            } else if (mYellowParams.getProvider() == WEB_TYPE_JIAZHENG) {
                uMengTypeId = UMengEventIds.DISCOVER_YELLOWPAGE_RESUME_TIME_14;
            }
            if (TextUtils.isEmpty(uMengTypeId)) {
                return;
            }
            try {
                int time = ((int)((System.currentTimeMillis() - startTime) / 1000));
                Map<String, String> map_value = new HashMap<String, String>();
                map_value.put("type", this.getClass().getName());
//                com.putao.analytics.MobclickAgentUtil.onEventValue(this, uMengTypeId, map_value, time);
                MobclickAgentUtil.onEventValue(this, uMengTypeId, map_value, time);
            } catch (Exception e) {
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(final String city, final double latitude, final double longitude, final long time) {
        LogUtil.d(TAG, "LBSServiceGaode deactivate");
        LBSServiceGaode.deactivate();
        // loadUrl();
        
        /**
         * WebView.loadUrl必须在同一个线程中
         * added by cj 2014/11/05 start
         */
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                configUrl(city, latitude, longitude);                
//            }
//        });
        // added by cj 2014/11/05 end
        
        //modify by lisheng start
        configUrl(city, latitude, longitude);     
        //modify by lisheng end
    }

    @Override
    public void onLocationFailed() {
    	/*
		 * modify by ls 2015-1-27 start
		 */
    	LogUtil.d(TAG, "location failed");
    	configUrl("", 0, 0);
    	// end 2015-1-27 by ls  
    }

}
