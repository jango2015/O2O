package so.contacts.hub.ui.yellowpage;

import java.util.List;

import so.contacts.hub.ui.yellowpage.tag.YellowPageIndicatorFragmentActivity;
import so.contacts.hub.util.MobclickAgentUtil;
import so.putao.findplug.LBSServiceGaode;
import so.putao.findplug.LBSServiceGaode.LBSServiceListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.widget.TextView;
import com.baidu.mapapi.model.LatLng;
import com.yulong.android.contacts.discover.R;

/**
 * 公交
 * @author putao_lhq
 *
 */
public class YellowPageBusQActivity extends YellowPageIndicatorFragmentActivity implements LBSServiceListener{

    public static final int FRAGMENT_CHANGE = 0;
    public static final int FRAGMENT_QUERY = 1;
    
    
	private BusChangeFragment mBusChangeFragment;
	private BusQueryFragment mBusQueryFragment;
	private double mLatitude;
	private double mLongitude;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				LBSServiceGaode.activate(YellowPageBusQActivity.this, 
						YellowPageBusQActivity.this);
				
			}
		}).start();
		if( TextUtils.isEmpty(mTitleContent) ){
            mTitleContent = getResources().getString(R.string.putao_server_head_title_bus);
        }
        ((TextView)findViewById(R.id.title)).setText(mTitleContent);
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
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onLocationChanged(String city, double latitude,
			double longitude, long time) {
		LBSServiceGaode.deactivate();
		mLatitude = latitude;
		mLongitude = longitude;
		mBusChangeFragment.onLocationChanged(city, latitude, longitude, time);
		mBusQueryFragment.mCurCity = city;
	}

	@Override
	public void onLocationFailed() {
		LBSServiceGaode.deactivate();
		mBusChangeFragment.onLocationFailed();
	}

	public LatLng getCurLatLng() {
		return new LatLng(mLatitude, mLongitude);
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
    protected int getMainViewResId() {
        // TODO Auto-generated method stub
        return R.layout.putao_yellow_page_bus;
    }

    @Override
    protected int supplyTabs(List<TabInfo> tabs) {
        tabs.add(new TabInfo(FRAGMENT_CHANGE, getString(R.string.putao_bus_change_text),
                BusChangeFragment.class));
        tabs.add(new TabInfo(FRAGMENT_QUERY, getString(R.string.putao_bus_query),
                BusQueryFragment.class));
        return 0;
    }

    @Override
    protected void onInitFragmentEnd(int index, Fragment fragment) {
        if(index == FRAGMENT_CHANGE){
            mBusChangeFragment = (BusChangeFragment)fragment;
        }else if(index == FRAGMENT_QUERY){
            mBusQueryFragment = (BusQueryFragment)fragment;
        }
        
    }

    @Override
    protected void onPageSelectedAction(int index, Fragment fragment) {
        // TODO Auto-generated method stub
        
    }
}
