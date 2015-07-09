
package so.contacts.hub.thirdparty.cinema.ui;

import java.util.ArrayList;
import java.util.List;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.account.PutaoAccount;
import so.contacts.hub.core.ConstantsParameter;
import so.contacts.hub.ui.yellowpage.YellowPageCitySelectActivity;
import so.contacts.hub.ui.yellowpage.tag.YellowPageIndicatorFragmentActivity;
import so.contacts.hub.util.CommonValueUtil;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.NetUtil;
import so.contacts.hub.util.UMengEventIds;
import so.contacts.hub.widget.CommonDialog;
import so.contacts.hub.widget.CommonDialogFactory;
import so.putao.findplug.LBSServiceGaode;
import so.putao.findplug.LBSServiceGaode.LBSServiceListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.yulong.android.contacts.discover.R;

public class YellowPageMovieListActivity extends YellowPageIndicatorFragmentActivity implements LBSServiceListener{

    public static final int FRAGMENT_PLAYING = 0;

    public static final int FRAGMENT_COMEING = 1;

    protected String lastTimeSelectedCity = "";		//用户上次选择的城市
    
    private TextView mCityText;

    private static final int ACTIVITY_CITY = 1;
    
    /*
     * 添加即将上映影片集合  和 正在热映影片集合
     * add by hyl 2015-1-5 start
     */
    public List<Long> openMovieList = new ArrayList<Long>();
    public List<Long> futureMovieList = new ArrayList<Long>();
    //add by hyl 2015-1-5 end
    
    private final int MSG_LOCATION_SUCCESS_ACTION = 0x2001;
    private final int MSG_NETWORK_EXCEPTION_ACTION = 0x2002;
    private final int MSG_LOCATION_FAILED_ACTION = 0x2003;
    private final int MSG_SHOW_PROGRESSDIALOG = 0x2004;
    private final int MSG_DISMISS_PROGRESSDIALOG = 0x2005;
    // 定位状态：正在定位
    private static final int LOCATION_STATE_LOCATING = 1;
    // 定位状态：定位失败
    private static final int LOCATION_STATE_FAILED = 2;
    // 定位状态：定位成功，显示所在城市
    private static final int LOCATION_STATE_SUCCESS = 3;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        initviews();
        
        //add by hyl 2015-1-14 增加静默登录点 start
        if(!PutaoAccount.getInstance().isLogin()){
        	PutaoAccount.getInstance().silentLogin(null);
        }
        //add by hyl 2015-1-14 增加静默登录点 end
        
        
        // add xcx 2014-12-30 start 统计埋点
        MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
                UMengEventIds.DISCOVER_YELLOWPAGE_MOVIE_IN);
        // add xcx 2014-12-30 end 统计埋点
    }

    private void initviews() {
    	if (TextUtils.isEmpty(mTitleContent)) {
            mTitleContent = getResources().getString(R.string.putao_movie_title_name);
        }
        ((TextView)findViewById(R.id.title)).setText(mTitleContent);
        findViewById(R.id.back_layout).setOnClickListener(this);
        
        mCityText = (TextView)findViewById(R.id.next_step_btn);
        mCityText.setVisibility(View.VISIBLE);
        mCityText.setText(lastTimeSelectedCity);
        mCityText.setCompoundDrawablesWithIntrinsicBounds(
                getResources().getDrawable(R.drawable.putao_icon_marker_white), null, null, null);
        mCityText.setTextColor(getResources().getColor(R.color.putao_white));
        mCityText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        mCityText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(YellowPageMovieListActivity.this,
                        YellowPageCitySelectActivity.class);
                intent.putExtra(YellowPageCitySelectActivity.FROM_ACTIVITY_KEY,
                        YellowPageMovieListActivity.class.getSimpleName() + "_city");
                intent.putExtra(YellowPageCitySelectActivity.SHOW_MODE_KEY,
                        YellowPageCitySelectActivity.SHOW_MODE_NOHOT);
                startActivityForResult(intent, ACTIVITY_CITY);
            }
        });
	}
    
    
    Thread locationThread = null;
    /**
     * 开始请求定位信息
     * modified by hyl 2015-1-10
     */
	public void requestLocationData() {
	    lastTimeSelectedCity = getSelectedCity();//获取用户上次选中的城市
    	showLocationText(LOCATION_STATE_LOCATING, "");
    	if(locationThread != null && locationThread.isAlive()){
    		return;
    	}
    	locationThread = new Thread(new Runnable() {
    			@Override
    			public void run() {
    				LBSServiceGaode.process_activate(YellowPageMovieListActivity.this,YellowPageMovieListActivity.this);
    			}
    		});
    	locationThread.start();
    	//add ljq 2015_01_24 加载城市之前显示进度框
    	if(NetUtil.isNetworkAvailable(this)){
    	    mhandelr.sendEmptyMessageDelayed(MSG_SHOW_PROGRESSDIALOG,50); 
    	}
    }
    
	@Override
    protected void onDestroy() {
        super.onDestroy();
        LBSServiceGaode.deactivate();
        /*
         * modify by putao_lhq at 2015年1月9日 @start
         */
        mhandelr.removeCallbacksAndMessages(null);/* end by putao_lhq */
        mhandelr = null;
    }

    private Handler mhandelr = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SHOW_PROGRESSDIALOG://显示进度框
                    if(getCurFragment() != null){
                        getCurFragment().showProgressDialog(true);
                    }
                    break;
                case MSG_DISMISS_PROGRESSDIALOG://隐藏进度框
                    if(getCurFragment() != null){
                        getCurFragment().showProgressDialog(false);
                    }
                    break;     
                case MSG_LOCATION_SUCCESS_ACTION://定位成功
                	String city = (String)msg.obj;
                	
                	if(!TextUtils.isEmpty(lastTimeSelectedCity) && !city.equals(lastTimeSelectedCity)){//判断当前定位城市与 已选择城市是否为一致，若不一致则弹出对话框提示用户
                		showLocationText(LOCATION_STATE_SUCCESS, lastTimeSelectedCity);
                		showDialog(city);
                	}else if (!TextUtils.isEmpty(city) ) {
            			showLocationText(LOCATION_STATE_SUCCESS, city);
            			loadMovieData(city);
                	}
                	break;
                case MSG_NETWORK_EXCEPTION_ACTION://网络异常
                case MSG_LOCATION_FAILED_ACTION://定位失败
                	showLocationText(LOCATION_STATE_FAILED, "");
                	loadMovieData(lastTimeSelectedCity);
                	break;
                default:
                	break;
            }
        }
	};
	
	/**
	 * 开始加载电影数据
	 */
	public void loadMovieData(String city){
	    /*
         * modify by putao_lhq at 2015年1月9日 @start
         * add code:
         */
	    if (mTabs == null) {
	        return;
	    }/* end by putao_lhq */
        for (int i = 0; i< mTabs.size() ; i++) {
            YellowPageMovieListFragment fragment = (YellowPageMovieListFragment) myAdapter.getItem(i);
            if (fragment != null) {
                fragment.initData(city);
            }
        }
	}
	
	private YellowPageMovieListFragment getCurFragment(){
	    return (YellowPageMovieListFragment)getFragmentById(mCurrentTab).fragment;
	}
	
    private YellowPageMovieListFragment getFragment(int index) {
        return (YellowPageMovieListFragment)getFragmentById(index).fragment;
    }
	
	
	private void showDialog(final String locationCity) {
	    mhandelr.sendEmptyMessage(MSG_DISMISS_PROGRESSDIALOG);
        final CommonDialog dialog = CommonDialogFactory.getOkCancelCommonLinearLayoutDialog(this);
        dialog.getTitleTextView().setText(R.string.putao_yellow_page_position_change);
        String msg = getString(R.string.putao_yellow_page_position_change_msg,
                LBSServiceGaode.getPreCity());
        dialog.getMessageTextView().setText(msg);
        dialog.getCancelButton().setText(R.string.putao_cancel);
        dialog.setCancelButtonClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showLocationText(LOCATION_STATE_SUCCESS, lastTimeSelectedCity);
                dialog.dismiss();
                loadMovieData(lastTimeSelectedCity);
            }
        });

        dialog.getOkButton().setText(R.string.putao_yellow_page_change_position);
        dialog.setOkButtonClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showLocationText(LOCATION_STATE_SUCCESS, LBSServiceGaode.getPreCity());
                saveCity("");
                lastTimeSelectedCity = "";
                dialog.dismiss();
                loadMovieData(locationCity);
            }
        });
        dialog.show();
    }
	
	
	private void showLocationText(int locationState, String city){
        switch(locationState){
        case LOCATION_STATE_LOCATING:
        	mCityText.setText(R.string.putao_location_text);
            break;
        case LOCATION_STATE_FAILED:
        	mCityText.setText(R.string.putao_yellow_page_location_failed);
            break;
        case LOCATION_STATE_SUCCESS:
        	mCityText.setText(city);
            break;
        default:
            break;
        }
    }
    
    @Override
    protected int getMainViewResId() {
        return R.layout.putao_yellow_page_movie_list_activity;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == ACTIVITY_CITY) {
            String cityName = data.getStringExtra("cityName");
            String cityId = data.getStringExtra("cityId");
            if (!TextUtils.isEmpty(cityName) && !TextUtils.isEmpty(cityId)) {
                lastTimeSelectedCity = cityName;
                saveCity(cityName);
                mCityText.setText(cityName);
                YellowPageMovieListFragment fragment_playing = getFragment(FRAGMENT_PLAYING);
                YellowPageMovieListFragment fragment_coming = getFragment(FRAGMENT_COMEING);
                if (fragment_playing != null) {
                    fragment_playing.initData(cityName);
                }
                if (fragment_coming != null) {
                    fragment_coming.initData(cityName);
                }
            }
        }
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
    public String getServiceNameByUrl() {
        return null;
    }

    @Override
    public Integer remindCode() {
        return mRemindCode;
    }

    @Override
    public String getServiceName() {
        return this.getClass().getName();
    }

    @Override
    public boolean needMatchExpandParam() {
        return false;
    }

    @Override
    protected int supplyTabs(List<TabInfo> tabs) {
        tabs.add(new TabInfo(FRAGMENT_PLAYING, getString(R.string.putao_movie_released_list),
                YellowPageMovieListFragment.class));
        tabs.add(new TabInfo(FRAGMENT_COMEING, getString(R.string.putao_movie_upcoming_list),
                YellowPageMovieListFragment.class));
        return 0;
    }

    @Override
    protected void onInitFragmentEnd(int index, Fragment fragment) {
        if (index == FRAGMENT_PLAYING) {
            ((YellowPageMovieListFragment)fragment)
                    .setListType(YellowPageMovieListFragment.TYPE_OPEN);
            MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
                    UMengEventIds.DISCOVER_YELLOWPAGE_MOVIE_RELEASED);
        } else if (index == FRAGMENT_COMEING) {
            ((YellowPageMovieListFragment)fragment)
                    .setListType(YellowPageMovieListFragment.TYPE_COMING);
            
        }
        if(index == 1){
        	requestLocationData();
        }
    }

	@Override
	public void onLocationChanged(String city, double latitude,
			double longitude, long time) {
		if ( !TextUtils.isEmpty(city) ) {
            LBSServiceGaode.deactivate();
            if (city.endsWith(CommonValueUtil.getInstance().getCityData())) {
                city = city.substring(0, city.length() - 1);
            }
        	Message msg = mhandelr.obtainMessage();
        	msg.what = MSG_LOCATION_SUCCESS_ACTION;
        	msg.obj = city;
        	mhandelr.sendMessage(msg);
        } else {
            onLocationFailed();
        }
	}

	@Override
	public void onLocationFailed() {
        LBSServiceGaode.deactivate();        
        
        /**
         * 偶现toast显示crash问题,将toast显示移到handler中显示
         * modify by cj at 2014/10/08 start
         */
        mhandelr.sendEmptyMessage(MSG_LOCATION_FAILED_ACTION);
        // modify by cj at 2014/10/08 end
	}

    @Override
    protected void onPageSelectedAction(int index, Fragment fragment) {
        // TODO Auto-generated method stub
        
    }

    private String getSelectedCity(){
        SharedPreferences pref = this.getSharedPreferences(
                ConstantsParameter.SHARED_PREFS_YELLOW_PAGE,
                Context.MODE_MULTI_PROCESS);
        return pref.getString(ConstantsParameter.YELLOW_PAGE_MOVIE_SELECTED_CITY, "");
    }
    
    private void saveCity(String name) {
        SharedPreferences pref = this.getSharedPreferences(
                ConstantsParameter.SHARED_PREFS_YELLOW_PAGE,
                Context.MODE_MULTI_PROCESS);
        Editor e = pref.edit();
        e.putString(ConstantsParameter.YELLOW_PAGE_MOVIE_SELECTED_CITY, name);
        e.commit();
    }
    
    @Override
    public void onPageSelected(int position) {
        // TODO Auto-generated method stub
       // add xcx 2014-12-30 start 统计埋点
        if (0 == position) {
            MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
                    UMengEventIds.DISCOVER_YELLOWPAGE_MOVIE_RELEASED);
        } else {
            MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
                    UMengEventIds.DISCOVER_YELLOWPAGE_MOVIE_UPCOMING_IN);
        }
        // add xcx 2014-12-30 end 统计埋点
        super.onPageSelected(position);
    }
}
