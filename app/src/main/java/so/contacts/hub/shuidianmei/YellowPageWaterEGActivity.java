package so.contacts.hub.shuidianmei;

import java.util.List;

import so.contacts.hub.thirdparty.cinema.ui.YellowPageMovieListFragment;
import so.contacts.hub.ui.yellowpage.oldtag.YellowPageThreeTagActivity;
import so.contacts.hub.ui.yellowpage.tag.YellowPageIndicatorFragmentActivity;
import so.contacts.hub.ui.yellowpage.tag.YellowPageIndicatorFragmentActivity.TabInfo;
import so.contacts.hub.util.MobclickAgentUtil;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yulong.android.contacts.discover.R;

public class YellowPageWaterEGActivity extends YellowPageIndicatorFragmentActivity
{
    public static final int FRAGMENT_WATER = 0;
    public static final int FRAGMENT_ELECTRICITY = 1;
    public static final int FRAGMENT_GAS = 2;
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if( TextUtils.isEmpty(mTitleContent) ){
            mTitleContent = getResources().getString(R.string.putao_water_eg_tag_title);
        }
        ((TextView)findViewById(R.id.title)).setText(mTitleContent);
        ImageView chargeHistory = (ImageView)findViewById(R.id.next_step_img);
        chargeHistory.setVisibility(View.VISIBLE);
        chargeHistory.setImageResource(R.drawable.putao_icon_title_ls);
		findViewById(R.id.next_setp_layout).setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(YellowPageWaterEGActivity.this, YellowPageWaterEGHistoryActivity.class);
                intent.putExtra("title",getString(R.string.putao_water_eg_tag_history));
                startActivity(intent);
            }
        });
		findViewById(R.id.back_layout).setOnClickListener(this);
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
		// TODO Auto-generated method stub
		return this.getClass().getName();
	}

	@Override
	public boolean needMatchExpandParam() {
		// TODO Auto-generated method stub
		return false;
	}

    @Override
    protected int getMainViewResId() {
        // TODO Auto-generated method stub
        return R.layout.putao_weg_yellow_page_water_eg;
    }

    @Override
    protected int supplyTabs(List<TabInfo> tabs) {
        tabs.add(new TabInfo(FRAGMENT_WATER, getString(R.string.putao_water_eg_tag_title_water),
                YellowPageWEGFragment.class));
        tabs.add(new TabInfo(FRAGMENT_GAS, getString(R.string.putao_water_eg_tag_title_electricity),
                YellowPageWEGFragment.class));
        tabs.add(new TabInfo(FRAGMENT_WATER, getString(R.string.putao_water_eg_tag_title_gas),
                YellowPageWEGFragment.class));
        return FRAGMENT_WATER;
    }

    @Override
    protected void onInitFragmentEnd(int index, Fragment fragment) {
        // TODO Auto-generated method stub
        if(index == FRAGMENT_WATER){
            ((YellowPageWEGFragment)fragment).setWEGType(1);
        }else if(index == FRAGMENT_ELECTRICITY){
            ((YellowPageWEGFragment)fragment).setWEGType(2);
        }else if(index == FRAGMENT_GAS){
            ((YellowPageWEGFragment)fragment).setWEGType(3);
        }
    }

    @Override
    protected void onPageSelectedAction(int index, Fragment fragment) {
        // TODO Auto-generated method stub
    }

    private Toast mToast;
    /**
     * 增加公用TOAST 避免重复弹出提示
     */
    public void showToast(String text) {  
        if(mToast == null) {  
            mToast = Toast.makeText(YellowPageWaterEGActivity.this, text, Toast.LENGTH_SHORT);  
        } else {  
            mToast.setText(text);    
            mToast.setDuration(Toast.LENGTH_SHORT);  
        }  
        mToast.show();  
    }
}
