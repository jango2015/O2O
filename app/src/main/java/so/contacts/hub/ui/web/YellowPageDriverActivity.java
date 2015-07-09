package so.contacts.hub.ui.web;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.util.LogUtil;
import so.putao.findplug.LBSServiceGaode;
import so.putao.findplug.LBSServiceGaode.LBSServiceListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.webkit.WebView;

import so.contacts.hub.util.MobclickAgentUtil;

public class YellowPageDriverActivity extends YellowPageH5Activity implements LBSServiceListener{
	private static final String TAG = YellowPageDriverActivity.class.getSimpleName();

	double latitude,longitude;
	private boolean mPageFinished = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void configWebSettings() {
		super.configWebSettings();
		latitude = LBSServiceGaode.getLatitude();
        longitude = LBSServiceGaode.getLongitude();
        LogUtil.d(TAG, "configWebSettings latitude="+latitude+" longitude"+longitude);
		new Thread(new Runnable() {
            @Override
            public void run() {
                configUrl();
            }
        }).start();

        LogUtil.d(TAG, "start progress.");                
        mHandler.sendEmptyMessage(MSG_SHOW_DIALOG_ACTION);
        mHandler.sendEmptyMessageDelayed(MSG_DISMISS_TIMEDOUT_ACTION, TIMEDOUT);	
	}
	
	@Override
	protected void loadUrl() {
	}

	private void configUrl() {
		if(!TextUtils.isEmpty(mUrl)) {
		    
		    /*
		     * 每次进入定位影响加载效率，改为十分钟超时 才重新定位，不然则取上一次定位信息
		     * update by hyl 2014-8-14
		     *  old code :
		     *  if(latitude == LBSServiceGaode.DEFAULT_LATITUDE || latitude == 0) {
                      LBSServiceGaode.activate(ContactsApp.getInstance(), this);
                  } else {
                      mUrl+="&lat="+String.valueOf(latitude)+"&lng="+String.valueOf(longitude);
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

    public class MyPutaoWebClientProxy extends PutaoWebClientProxy {
        public MyPutaoWebClientProxy(Context ctx, Handler h) {
            super(ctx, h);
        }

        @Override
        public void putao_onPageStarted(WebView view, String url, Bitmap favicon) {         
            LogUtil.d(TAG, "putao_onPageStarted progress="+progress+" url="+url);
            mPageFinished = false;
            updateProgressBar(initProgress);//add ljq 2014 11 07 开始读取时制造假进度
        }
        
        @Override
        public void putao_onPageFinished(WebView view, String url) {
            LogUtil.d(TAG, "onPageFinished progress="+progress+" url="+url);
            
            mPageFinished = true;
            
            mHandler.removeMessages(MSG_DISMISS_TIMEDOUT_ACTION);
            if(progress == 100) {
                LogUtil.d(TAG, "close progress1.");
                mHandler.sendEmptyMessage(MSG_DISMISS_DIALOG_ACTION);
            }
        }   

        @Override
        public void putao_onReceivedError(WebView view, int errorCode,
                String description, String failingUrl) {
//            view.stopLoading();
        }

        @Override
        public void putao_onProgressChanged(WebView view, int newProgress) {
            LogUtil.d(TAG, "putao_onProgressChanged progress="+progress);

            updateProgressBar(newProgress);
            
            if(progress == 100 && mPageFinished) {
                LogUtil.d(TAG, "close progress2.");
                mHandler.sendEmptyMessage(MSG_DISMISS_DIALOG_ACTION);
            }
        }       

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
	    LBSServiceGaode.deactivate();
		super.onDestroy();
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onLocationChanged(String city, double latitude, double longitude, long time) {
        LBSServiceGaode.deactivate();
        
        this.latitude = latitude;
        this.longitude = longitude;
        
        mUrl+="&lat="+String.valueOf(latitude)+"&lng="+String.valueOf(longitude);
        LogUtil.d(TAG, "onLocationChanged mUrl="+mUrl);
        
        //modify by lisheng 2014-11-19 
        loadUrl(mUrl);
		//modify by lisheng end
        
    }

	@Override
    public void onLocationFailed() {
        LBSServiceGaode.deactivate();
        
        latitude = 0;
        longitude = 0;
        
        mUrl+="&lat="+String.valueOf(latitude)+"&lng="+String.valueOf(longitude);
        LogUtil.d(TAG, "onLocationFailed mUrl="+mUrl);
        
        
        //modify by lisheng 2014-11-19
     	loadUrl(mUrl);
     	//modify by lisheng end
        
    }
}
