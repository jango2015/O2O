
package so.contacts.hub.ui.web;


import so.contacts.hub.ContactsApp;
import so.contacts.hub.account.IAccCallback;
import so.contacts.hub.account.PutaoAccount;
import so.contacts.hub.active.ActiveInterface;
import so.contacts.hub.active.ActiveUtils;
import so.contacts.hub.active.bean.ActiveEggBean;
import so.contacts.hub.core.Config;
import so.contacts.hub.core.ConstantsParameter;
import so.contacts.hub.core.PTUser;
import so.contacts.hub.remind.RemindConfig;
import so.contacts.hub.remind.utils.RemindUtils;
import so.contacts.hub.ui.web.PutaoWebClientProxy.IProgressChangedListener;
import so.contacts.hub.ui.web.PutaoWebClientProxy.TitleChangedListener;
import so.contacts.hub.ui.yellowpage.bean.YellowParams;
import so.contacts.hub.util.ContactsAppUtils;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.NetUtil;
import so.contacts.hub.util.UMengEventIds;
import so.contacts.hub.util.WebViewDialogUtils;
import so.contacts.hub.util.YellowUtil;
import so.contacts.hub.widget.ProgressDialog;
import so.putao.aidl.IPutaoService;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yulong.android.contacts.discover.R;

/**
 * 所有H5界面的基类,处理了WebSetting设置,进度控制,标题改变以及默认的back事件
 * 需要子类集成,并实现WebChromeClient,WebViewClient处理单独页面回调.
 * 
 * @author change
 */
public abstract class YellowPageH5Activity extends Activity implements OnClickListener,
        TitleChangedListener, IProgressChangedListener, ActiveInterface, IAccCallback {
    private static final String TAG = YellowPageH5Activity.class.getSimpleName();

    public static final int TIMEDOUT = 30 * 1000; // 10s

    public static final int WEBPAGETYPE_DEFAULT = 0;

    private int mPageType = WEBPAGETYPE_DEFAULT;

    protected Context mContext = null;
    
    protected IPutaoService mService = null;

    protected PutaoWebClientProxy mWebProxy = null;

    private LinearLayout mShowLayout = null;

    private ProgressBar mProgressBar = null;

    private RelativeLayout mNetExceptionLayout = null;

    private TextView mException_desc = null;
    
    protected WebView mWebView = null;

    private TextView mTitleView = null;

    protected String mUrl;

    protected String mHomePageUrl = "";

    // 网页类型ID
    protected int mWebType = -1;

    protected ProgressDialog mProgressDialog = null;

    protected int progress = 1;

    protected YellowParams mYellowParams = null;

    private long mStartTime = 0L;

    protected boolean mFirstLoadHomePage = true;

    public static final int MSG_SHOW_DIALOG_ACTION = 0x2001;

    public static final int MSG_DISMISS_DIALOG_ACTION = 0x2002;

    public static final int MSG_DISMISS_TIMEDOUT_ACTION = 0x2003;

    public static final int MSG_RECEIVED_ERROR_ACTION = 0x2004;

    public static final int MSG_UPDATE_PROGRESS_ACTION = 0x2005;

    public static final int MSG_UPDATE_PROGRESS_DISMISS_AND_INIT_ACTION = 0x2006;

    public static final int MSG_ASYNC_LOGIN_ACTION = 0x2007;
    
    public static final int MSG_LOCATED_SUCCESS_ACTION = 0x2008;

    private boolean mWebConfigured = false;
    
    private int mRemindCode = -1;
    
    //是否来自YellowPageJumpH5Activity的跳转
    private boolean isfromJumpH5 = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
         
        mContext = this;
        
        setContentView(R.layout.putao_yellow_page_item_detail);
        findViewById(R.id.back_layout).setOnClickListener(this);
        mTitleView = (TextView)findViewById(R.id.title);
        
        mStartTime = System.currentTimeMillis();
        mShowLayout = (LinearLayout)findViewById(R.id.show_layout);
        mProgressBar = (ProgressBar)findViewById(R.id.progressbar);
        if (mProgressBar != null) {
            mProgressBar.setProgress(progress);
        }
        mWebView = (WebView)findViewById(R.id.yellow_page_detail);
        mNetExceptionLayout = (RelativeLayout)findViewById(R.id.network_exception_layout);
        mException_desc = (TextView)findViewById(R.id.exception_desc);
        mNetExceptionLayout.setOnClickListener(this);
        
        Intent intent = getIntent();
        String title = "";
        if (intent != null) {
            //add ljq start 2014_11_19 加上变量判断是否来自jumpH5跳转
            isfromJumpH5 = intent.getBooleanExtra("fromJumpH5", false);
            //add ljq end 2014_11_19 加上变量判断是否来自jumpH5跳转
            
            mYellowParams = (YellowParams)getIntent().getSerializableExtra(
                    YellowUtil.TargetIntentParams);
            if (mYellowParams != null) {
            	/** add by zjh 2014-12-04 增加友盟统计 start */
            	int entryType = mYellowParams.getEntry_type();
            	if( entryType == YellowParams.ENTRY_TYPE_HOME_PAGE ){
            		MobclickAgentUtil.onEvent(this, UMengEventIds.DISCOVER_YELLOWPAGE_HEADER + 
            				mYellowParams.getCategory_id());
            	}else if( entryType == YellowParams.ENTRY_TYPE_SEARCH_PAGE ){
            		MobclickAgentUtil.onEvent(this, UMengEventIds.DISCOVER_SEARCH_SERVER_ACCESS + 
            				mYellowParams.getCategory_id());
            	}else if(entryType == YellowParams.ENTRY_TYPE_NOTIFICATION_PAGE) {
            		MobclickAgentUtil.onEvent(this, UMengEventIds.DISCOVER_NOTIFICATION_SERVER_ACCESS + 
            				mYellowParams.getCategory_id());
            	}
            	/** add by zjh 2014-12-04 增加友盟统计 end */
            	
                mPageType = mYellowParams.getWebtype();
                mUrl = mYellowParams.getUrl();
                mWebType = mYellowParams.getProvider();
                title = mYellowParams.getTitle();
                mRemindCode = mYellowParams.getRemindCode();

                LogUtil.d(TAG, "onCreate mRemindCode: " + mRemindCode);
                
            } else {
                title = intent.getStringExtra("title");
                mUrl = intent.getStringExtra("url");
                mRemindCode = intent.getIntExtra("RemindCode", -1);
                
                LogUtil.d(TAG, "onCreate mRemindCode: " + mRemindCode);
                
                /*
                 * 移除信鸽功能 注释该逻辑
                 * modified by hyl 2014-12-24 start
                 */
//                /**
//                 * 获取信鸽传输的title/Url参数
//                 */
//                XGPushClickedResult clickResult = XGPushManager.onActivityStarted(this);
//        		if( clickResult != null ){
//        			LogUtil.i(TAG, " get XG intent key-value.");
//        			String customContent = clickResult.getCustomContent();
//        			if( !TextUtils.isEmpty(customContent) ){
//        				String xgTitle = null;
//        				String xgUrl = null;
//        				try{
//        					JSONObject json = new JSONObject(customContent);
//        					xgTitle = json.getString("title");
//        					xgUrl = json.getString("url");
//        				}catch(JSONException e){
//        					xgTitle = null;
//        					xgUrl = null;
//        				}
//        				if( !TextUtils.isEmpty(xgTitle) ){
//        					title = xgTitle;
//        				}
//        				if( !TextUtils.isEmpty(xgUrl) ){
//        					mUrl = xgUrl;
//        				}
//        			}
//        		}
//                LogUtil.i(TAG, "title: " + title + " ,url: " + mUrl);
                // modified by hyl 2014-12-24 end
            }
            mHomePageUrl = mUrl;
        }
        mTitleView.setText(title);
        
        if(mUrl == null || mUrl.equals("")){
            showWebLoadErr(this.getResources().getString(R.string.putao_netexception_hint));
            return;
        }

        // 在父类来统一检测网络，具体配置和加载可在子类实现
        if (NetUtil.isNetworkAvailable(this)) {
            //putao_lhq modify for 静默登录业务阻塞 start
        	configWebSettings();
        	//mHandler.obtainMessage(MSG_ASYNC_LOGIN_ACTION).sendToTarget();
        	if (!PutaoAccount.getInstance().isLogin()) {
        		PutaoAccount.getInstance().silentLogin(this);
        	} else {
        		loadUrl();
        		// 找蛋需要登陆后 modify by cj 2015/01/23
        		findActiveEgg();//add by putao_lhq for active
        	}
            //putao_lhq modify 静默登录业务阻塞 end
        } else {
            showWebLoadErr(this.getResources().getString(R.string.putao_netexception_hint));
        }
    }

    /**
     * 找彩蛋活动
     * @author putao_lhq
     */
	private void findActiveEgg() {
		Config.execute(new Runnable() {
			
			@Override
			public void run() {
				LogUtil.d(TAG, "start find active egg");
				ActiveEggBean egg = getValidEgg();
				if(egg != null) {
					LogUtil.i(TAG, "oh yeah, find one egg: " + egg.toString());
					String requrlOfSign = ActiveUtils.getRequrlOfSign(egg);
					if (TextUtils.isEmpty(requrlOfSign)) {
						LogUtil.d(TAG, "sign request url is fail");
					}
					WebViewDialogUtils.startWebDialog(YellowPageH5Activity.this, requrlOfSign);
				} else {
					LogUtil.d(TAG, "getValidEgg null");    
				}
			}
		});
	}

    protected boolean isFullScreen() {
        return mPageType != WEBPAGETYPE_DEFAULT ? true : false;
    }

    protected void configWebSettings() {
        LogUtil.d(TAG, "configWebSettings");
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setLoadsImagesAutomatically(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setDefaultTextEncodingName("UTF-8");
        mWebView.getSettings().setDomStorageEnabled(true);

        mWebView.getSettings().setGeolocationEnabled(true);
        mWebView.getSettings().setGeolocationDatabasePath(
                this.getApplicationContext().getDir("geocache", 0).getPath());

        mWebView.getSettings().setSaveFormData(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setAppCachePath(
                this.getApplicationContext().getDir("appcache", 0).getPath());

        mWebView.getSettings().setDatabaseEnabled(true);
        mWebView.getSettings().setDatabasePath(
                this.getApplicationContext().getDir("dbcache", 0).getPath());

        mWebView.requestFocus();

        mWebProxy = getPutaoWebClientProxy(this, mHandler);
        if (mWebProxy != null) {
            mWebView.setWebChromeClient(mWebProxy.getWebChromeClient());
            mWebView.setWebViewClient(mWebProxy.getWebViewClient());
        }
        
        mWebConfigured = true;
    }

    public abstract PutaoWebClientProxy getPutaoWebClientProxy(Context context, Handler h);

    protected void loadUrl() {
        mWebView.loadUrl(mUrl);
        
    }
    
    //add by lisheng 2014-11-19 11:52:37
    /**修改子类的LBS定位完成的回调里加载H5页面的方法*/
    protected void loadUrl(String url){
    	Message msg = Message.obtain();
		msg.obj = url;
		msg.what = MSG_LOCATED_SUCCESS_ACTION;
		mHandler.sendMessage(msg);
    }
    //add by lisheng end

    protected void showWebLoadErr(String showText) {
        mNetExceptionLayout.setVisibility(View.VISIBLE);
        mException_desc.setText(showText);
        mNetExceptionLayout.setOnClickListener(this);
        if (mShowLayout != null) {
            mShowLayout.setVisibility(View.INVISIBLE);
        } else {
            mWebView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgentUtil.onResume(this);
    }

    @Override
    protected void onPause() {
        MobclickAgentUtil.onPause(this);
        //Patao lihq 2014-09-03 delete for 灭屏之后加载中断bug start
        //mWebView.stopLoading();
        //Patao lihq 2014-09-03 delete for 灭屏之后加载中断bug end
        super.onPause();
    }

    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!isFinishing()) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MSG_SHOW_DIALOG_ACTION:
                        mHandler.removeMessages(MSG_SHOW_DIALOG_ACTION);
                        LogUtil.i(TAG, "mProgressBar="+mProgressBar);
                        mProgressBar.setVisibility(View.VISIBLE);
                        break;
                    case MSG_DISMISS_DIALOG_ACTION:
                        mHandler.removeMessages(MSG_DISMISS_TIMEDOUT_ACTION);
                        mHandler.removeMessages(MSG_DISMISS_DIALOG_ACTION);
                        mProgressBar.setVisibility(View.GONE);
                        break;
                    case MSG_DISMISS_TIMEDOUT_ACTION:
                        mHandler.removeMessages(MSG_DISMISS_TIMEDOUT_ACTION);
                        showWebLoadErr(YellowPageH5Activity.this.getResources().getString(
                                R.string.putao_netexception_connect_timedout));
                        break;
                    case MSG_RECEIVED_ERROR_ACTION:
                        showWebLoadErr(getResources().getString(R.string.putao_netexception_hint));
                        break;
                    case MSG_UPDATE_PROGRESS_ACTION:
                        mHandler.removeMessages(MSG_UPDATE_PROGRESS_ACTION);
                        if (progress < 60) {
                            // 此处是在加载h5页面比较卡顿时可以模拟前60%的加载进度
                            progress += 2;
//                            LogUtil.v(TAG, "MSG_UPDATE_PROGRESS_CIRCLE_ACTION simulate progess: "
//                                    + progress);
                            mHandler.sendEmptyMessageDelayed(MSG_UPDATE_PROGRESS_ACTION, 100);
                        }
                        mProgressBar.setProgress(progress);
                        break;
                    case MSG_UPDATE_PROGRESS_DISMISS_AND_INIT_ACTION:
                        mProgressBar.setVisibility(View.GONE);
                        progress = 1;
                        mProgressBar.setProgress(progress);
                        break;
                    case MSG_ASYNC_LOGIN_ACTION:
                    	//putao_lhq add start
                    	PTUser user = PutaoAccount.getInstance().getPtUser();
                    	if (null == user || null == user.getPt_token()) {
                    		PutaoAccount.getInstance().silentLogin(YellowPageH5Activity.this);
                    	}
                    	//putao_lhq add end
                        break;
                        
                        //add by lisheng start 2014-11-18 21:50:44
                    case MSG_LOCATED_SUCCESS_ACTION:
                    	mHandler.removeMessages(MSG_LOCATED_SUCCESS_ACTION);
                    	if(msg.obj instanceof String){
                    		String url = (String) msg.obj;
                    		mWebView.loadUrl(url);
                    	}
                    	break;
                        //add by lisheng end
                        
                    default:
                        break;
                }
            }
        }
    };

    protected void updateProgressBar(int progressData) {
        if (progressData < 1) {
            return;
        }
        if (progress < progressData) {
            progress = progressData;
        }
        mHandler.removeMessages(MSG_UPDATE_PROGRESS_ACTION);
        mHandler.sendEmptyMessage(MSG_UPDATE_PROGRESS_ACTION);
    }

    @Override
    public void onProgressChanged(WebView paramWebView, int paramInt) {
        updateProgressBar(paramInt);
    }

    @Override
    public void onTitleChanged(WebView paramWebView, String title) {
        if (null != title && !"".equals(title.trim())) {
            // titleView.setText(title);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if(mWebView.canGoBack()){
                mWebView.goBack();
                return true;
            }else{
                finish();
                if(isfromJumpH5){
                    overridePendingTransition(0, 0);
                }
                return super.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
		if (id == R.id.back_layout) {
			finish();
			if(isfromJumpH5){
			    overridePendingTransition(0, 0);
			}
		} else if (id == R.id.network_exception_layout) {
	        if(mUrl == null || mUrl.equals("")){
	            return;
	        }
			if (NetUtil.isNetworkAvailable(this)) {
				mNetExceptionLayout.setOnClickListener(null);
				mNetExceptionLayout.setVisibility(View.INVISIBLE);
				mShowLayout.setVisibility(View.VISIBLE);
				mWebView.setVisibility(View.VISIBLE);
				
				mHandler.sendEmptyMessageDelayed(MSG_DISMISS_TIMEDOUT_ACTION, TIMEDOUT);
				
				if(!mWebConfigured)
					configWebSettings();
				//putao_lhq modify for 静默登录业务阻塞 start
				if (!PutaoAccount.getInstance().isLogin()) {
					PutaoAccount.getInstance().silentLogin(this);
				} else {
					loadUrl();	
				}
				//putao_lhq modify for 静默登录业务阻塞 end
			} else {
			    showWebLoadErr(this.getResources().getString(R.string.putao_netexception_hint));
			}
		} else {
		}
    }

    @Override
    public void onSuccess() {
        LogUtil.i(TAG, "login successful");
        loadUrl();
    }

    @Override
    public void onFail(int msg) {
        LogUtil.i(TAG, "login failed: " + msg);
        //putao_lhq modify for 静默登录业务阻塞 start
        Toast.makeText(this, R.string.putao_server_busy, Toast.LENGTH_SHORT).show();
        showWebLoadErr(this.getResources().getString(R.string.putao_login_exception_hint));
        mHandler.removeMessages(MSG_DISMISS_TIMEDOUT_ACTION);
        //putao_lhq modify for 静默登录业务阻塞 end
    }

    @Override
    protected void onDestroy() {
        // 杀掉进程解绑会报异常,无需解绑
//        if(servconn != null)
//            ContactsApp.getInstance().unbindService(servconn);

        LogUtil.d("kill_process", "H5 onDestroy");
        mShowLayout.removeView(mWebView);
        mWebView.setVisibility(View.GONE);
        mWebView.setWebChromeClient(null);
        mWebView.setWebViewClient(null);
        mWebView.removeAllViews();
        mWebView.destroy();
        super.onDestroy();
//        System.exit(0);
        Process.killProcess(Process.myPid());
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
	
    //putao_lhq add for plug-1.3 subway start
    public void changeTitle(CharSequence title) {
    	mTitleView.setText(title);
    }
    //putao_lhq add for plug-1.3 subway end
    
    protected void sendRemindServiceBroadcast(int type, int remindCode, boolean isMyService) {
        Intent intent = new Intent(ConstantsParameter.ACTION_REMOTE_UPDATE_REMIND);
        intent.putExtra("Type", type);
        intent.putExtra("RemindCode",remindCode);
        intent.putExtra("IsMyService",isMyService);
        ContactsApp.getInstance().sendBroadcast(intent);
    }
    
    protected void addRemindService(int type, int remindCode, boolean isMyService) {
        LogUtil.i(TAG, " type="+type+" remindCode="+remindCode+" isMyService="+isMyService);
        
        if(type == RemindConfig.REMIND_ADD) {  // 增加打点
            if(isMyService) 
                RemindUtils.addMyServiceRemind(remindCode, true);
            else 
                RemindUtils.addServiceRemind(remindCode, true);
            
        } else if(type == RemindConfig.REMIND_UPDATE) { // 更新打点
            
        } else if(type == RemindConfig.REMIND_DELETE) { // 删除打点
            
        }

    }
    
    // 判断是否有活动彩蛋存在，返回有效的彩蛋, 默认mUrl
    @Override
    public ActiveEggBean getValidEgg() {
    	if (needMatchExpandParam() && mYellowParams != null) {
    		Intent intent = getIntent();
    		long expand = intent.getLongExtra("ItemId", 0);
    		LogUtil.d(TAG, "ItemId: " + expand);
    		if (expand <= 0) {
    			expand = mYellowParams.getCategory_id();
    		}
    		LogUtil.d(TAG, "expand: " + expand);
    		ActiveEggBean validEgg = ActiveUtils.getValidEgg(getServiceName(), String.valueOf(expand));
    		if (null == validEgg) {
    			validEgg = ActiveUtils.getValidEgg(getServiceNameByUrl(), String.valueOf(expand));
    		}
			return validEgg;
    	} else {
    		ActiveEggBean validEgg = getValidEgg(getServiceNameByUrl());
    		return validEgg;
    	}
    }

    @Override
    public String getServiceNameByUrl() {
    	return mUrl;
    }
    
    @Override
    public String getServiceName() {
    	return "so.contacts.hub.ui.yellowpage.YellowPageH5Activity";
    }
    
    @Override
    public ActiveEggBean getValidEgg(String trigger_url) {
        ActiveEggBean bean = ContactsAppUtils.getInstance().getDatabaseHelper().getActiveDB().qryEggTigger(trigger_url);
        
        if(ActiveUtils.isEggValid(bean))
            return bean;
        else
            return null;
    }
    
    @Override
    public boolean needMatchExpandParam() {
    	return true;
    }
    
    @Override    
    public void onCancel() {
        
    }
    
}
