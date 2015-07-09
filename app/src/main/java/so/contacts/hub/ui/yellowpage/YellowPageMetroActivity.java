package so.contacts.hub.ui.yellowpage;

import so.contacts.hub.gamecenter.utils.SharedPreferenceUtils;
import so.contacts.hub.ui.web.PutaoWebClientProxy;
import so.contacts.hub.ui.web.YellowPageH5Activity;
import so.contacts.hub.ui.yellowpage.bean.SubwayManager;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.NetUtil;
import so.contacts.hub.util.UMengEventIds;
import so.putao.findplug.LBSServiceGaode;
import so.putao.findplug.LBSServiceGaode.LBSServiceListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import so.contacts.hub.util.MobclickAgentUtil;
import com.yulong.android.contacts.discover.R;

/**
 * 葡萄地铁
 * @author putao_lhq
 *
 */
public class YellowPageMetroActivity extends YellowPageH5Activity implements LBSServiceListener {

	private boolean mPageFinished = false;
	private final String DEFAULT_CITY_CODE = "4403";
	private final String TAG = "YellowPageMetro";
	private TextView mSelectCityView;
	private String metroUrl;
	private String locUrl;
	private String curCity;
	
	//add by lisheng start
	private String lastLocUrl =null;
	//add by lisheng end
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSelectCityView = (TextView) findViewById(R.id.next_step_btn);
    	mSelectCityView.setVisibility(View.VISIBLE);
    	mSelectCityView.setText(getResources().getString(R.string.putao_shenzhen));
    	ImageView mNextStep = (ImageView)findViewById(R.id.next_step_img);
		mNextStep.setImageResource(R.drawable.putao_icon_marker_white);
		mNextStep.setVisibility(View.VISIBLE);
    	findViewById(R.id.next_setp_layout).setOnClickListener(this);
    	
        // modify by cj 2014-12-29
        //BUGFIX: 等VIEW初始化完成后再启动定位,否则可能引起NPE错误
    	new Thread(new Runnable() {
    	    @Override
    	    public void run() {
    	        LBSServiceGaode.activate(YellowPageMetroActivity.this,
    	                YellowPageMetroActivity.this);
    	    }
    	}).start();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgentUtil.onResume(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgentUtil.onPause(this);
	}
	@Override
	protected void configWebSettings() {
		super.configWebSettings();
		mWebView.getSettings().setBuiltInZoomControls(true);
		mWebView.getSettings().setDisplayZoomControls(false);
		mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		mWebView.setInitialScale(150);
	}
	
	@Override
	public PutaoWebClientProxy getPutaoWebClientProxy(Context context, Handler h) {
		return new MyPutaoWebClientProxy(this, mHandler);
	}

	//add by lisheng start	
	@Override
	protected void loadUrl() {
		// TODO Auto-generated method stub
		return ;
	}
	//add by lisheng end
	
	
	
	@Override
	public void onLocationChanged(String city, double latitude,
			double longitude, long time) {
		LogUtil.d(TAG, "city: " + city + ",latitude: " + latitude + 
				" ,longitude: " + longitude + " ,code: " + SubwayManager.getCode(city));
		LBSServiceGaode.deactivate();
		if (TextUtils.isEmpty(city)) {
			return;
		}
		
		//add by lisheng start 2014-11-18 如果定位的城市无地铁,提示用户该地无地铁数据;
		if(TextUtils.isEmpty(SubwayManager.getCode(city))){
			super.showWebLoadErr(this.getResources().getString(R.string.putao_nometroexception_hint));
			return;
		}else{
			mSelectCityView.setText(city);
			metroUrl = mUrl + SubwayManager.getCode(city) + "&lnglat=" + longitude
					+ "," + latitude;
			locUrl = metroUrl;
			curCity = city;
			
			//modify by lisheng 2014-11-18 21:39:00
			loadUrl(metroUrl);
			
			
//			mWebView.loadUrl(metroUrl);
			//modify by lisheng end
			
			LogUtil.i(TAG, "定位成功!metroUrl="+metroUrl);
		}
		//add by lisheng end

	}

	@Override
	public void onLocationFailed() {
		metroUrl = mUrl + DEFAULT_CITY_CODE;
		
		//add by lisheng 2014-11-13 20:21:45
		LBSServiceGaode.deactivate();
		LogUtil.i(TAG, "定位失败!lastLocUrl="+lastLocUrl);
		
		if(NetUtil.isNetworkAvailable(YellowPageMetroActivity.this)) {
		    lastLocUrl=getLastLocUrl();
		    if(TextUtils.isEmpty(lastLocUrl)){
		        mWebView.loadUrl(metroUrl);
		    }else{
		        mWebView.loadUrl(lastLocUrl);
		    }		    
		} else {
            super.showWebLoadErr(this.getResources().getString(R.string.putao_netexception_hint));
		}
		//add by lisheng end
	}

	public class MyPutaoWebClientProxy extends PutaoWebClientProxy {
		public MyPutaoWebClientProxy(Context ctx, Handler h) {
			super(ctx, h);
		}
		
		public boolean putao_shouldOverrideUrlLoading(WebView view, String url) {
			LogUtil.d(TAG,"putao_shouldOverrideUrlLoading="+url);
			metroUrl = url;
			return super.putao_shouldOverrideUrlLoading(view, url);
		}		
		
		@Override
		public void putao_onPageStarted(WebView view, String url, Bitmap favicon) {
			LogUtil.d(TAG, "putao_onPageStarted progress="+progress+" url="+url);
			
			mPageFinished = false;
			
			if (mFirstLoadHomePage && url.equals(metroUrl) && !isFinishing()) {
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
		}
		
		@Override
		public void putao_onLoadResource(WebView view, String url) {
			LogUtil.d(TAG, "putao_onLoadResource url="+url);
		}

		@Override
		public boolean putao_onJsAlert(WebView view, String url,
				String message, JsResult result) {
			return false; 
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
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.next_setp_layout) {
			LBSServiceGaode.deactivate();//putao_lhq add for BUG #1507
			Intent intent = new Intent(this, YellowPageSubwayCitySelectActivity.class);
			startActivityForResult(intent, 0);
		} else {
		}
		super.onClick(v);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		if (resultCode == Activity.RESULT_OK) {
			String city = intent.getStringExtra("cityName");
			if (TextUtils.isEmpty(city)) {
				return;
			}
			mSelectCityView.setText(city);
			if (TextUtils.isEmpty(curCity) || TextUtils.isEmpty(locUrl) || 
					!curCity.equals(city)) {
				metroUrl = mUrl + SubwayManager.getCode(city);
			} else {
				metroUrl = locUrl;
			}
			
			//add by lisheng 2014-11-13 20:10:03 end
			if(!NetUtil.isNetworkAvailable(this)){
				super.showWebLoadErr(this.getResources().getString(R.string.putao_netexception_hint));
				return;
			}
			//add by lisheng end
			
			mWebView.loadUrl(metroUrl);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		LBSServiceGaode.deactivate(); 
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getApplicationInfo().targetSdkVersion
                    >= Build.VERSION_CODES.ECLAIR) {
                event.startTracking();
            } else {
                onBackPressed();
            }
            return true;
        }
		return super.onKeyDown(keyCode, event);
	}

	
	//add by lisheng  2014-11-12 21:44:59 start
	@Override
	protected void showWebLoadErr(String showText) {
		lastLocUrl=getLastLocUrl();
		LogUtil.d(TAG, "lastLocUrl="+lastLocUrl);
		if(!TextUtils.isEmpty(lastLocUrl)){
			mWebView.loadUrl(lastLocUrl);
		}else{
			super.showWebLoadErr(showText);
		}
	}
	
	public String getLastLocUrl(){
		/*
		 * 原逻辑存在空指针风险，修改增加判断
		 * modified by hyl 2014-12-25 start
		 * old code:
		 * String lastCity = SharedPreferenceUtils.getPreference("location", "city", "").split("市")[0];
		 */
		String lastCity = null;
		String city = SharedPreferenceUtils.getPreference("location", "city", "");
		try {
			if(!TextUtils.isEmpty(city)){
				lastCity = city.split("市")[0];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(TextUtils.isEmpty(lastCity)){
			return null;
		}
		//modified by hyl 2014-12-25 end
		String code =SubwayManager.getCode(lastCity);
		if(TextUtils.isEmpty(code)){
			return null;
		}
		mSelectCityView = (TextView) findViewById(R.id.next_step_btn);// 修复bug 2356 空指针异常;
		mSelectCityView.setText(lastCity);
		String preLo = SharedPreferenceUtils.getPreference("location", "longitude", "");
		String preLa =	SharedPreferenceUtils.getPreference("location", "latitude", "");
		LogUtil.d(TAG, "location="+preLo+preLa);
		if(TextUtils.isEmpty(preLo)||TextUtils.isEmpty(preLa)){
			return null;
		}
		double preLongt =Double.parseDouble(preLo);
		double preLati =Double.parseDouble(preLa);
		configWebSettings();
		StringBuilder sb = new StringBuilder();
		sb.append(mUrl).append(SubwayManager.getCode(lastCity));
		sb.append("&lnglat=").append(preLongt).append(",").append(preLati);
		LogUtil.d(TAG, "网络故障:"+lastCity+preLo+preLa);
		
		return sb.toString();
	}
	//add by lisheng end
}
