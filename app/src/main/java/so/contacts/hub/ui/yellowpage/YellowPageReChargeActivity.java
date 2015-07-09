package so.contacts.hub.ui.yellowpage;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.util.UMengEventIds;

import java.util.List;

import so.contacts.hub.shuidianmei.YellowPageWEGFragment;
import so.contacts.hub.ui.yellowpage.oldtag.YellowPageTagActivity;
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

import com.yulong.android.contacts.discover.R;

public class YellowPageReChargeActivity extends YellowPageIndicatorFragmentActivity
{
    
    public static final int FRAGMENT_CHARGETELEPHONE = 0;
    public static final int FRAGMENT_TRAFFICTELEPHONE = 1;
    
	//putao_lhq add for 充话费API start
	public String phoneNumber;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		phoneNumber = getIntent().getStringExtra("phone_num");
        ((TextView)findViewById(R.id.title)).setText(getResources().getString(R.string.putao_recharge_title));
		
		/*
		 * 查看话费充值历史
		 * add by hyl 2014-10-10 start
		 * delete code
		 * modify by putao_lhq
		 * coolui 6.0
		 */
		/*TextView chargeHistory = (TextView)findViewById(R.id.next_step_btn);
		chargeHistory.setVisibility(View.VISIBLE);
		chargeHistory.setText(R.string.putao_charge_history);
		findViewById(R.id.next_setp_layout).setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(YellowPageReChargeActivity.this, YellowPageChargeHistoryActivity.class);
                startActivity(intent);
            }
        });*/
        //modify by xcx 2015-01-07 start 隐藏历史订单入口
//        ImageView chargeHistory = (ImageView)findViewById(R.id.next_step_img);
//        chargeHistory.setVisibility(View.VISIBLE);
//        chargeHistory.setImageResource(R.drawable.putao_icon_title_ls);
//        findViewById(R.id.next_setp_layout).setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(YellowPageReChargeActivity.this, YellowPageChargeHistoryActivity.class);
//                startActivity(intent);
//            }
//        });
       //modify by xcx 2015-01-07 end 隐藏历史订单入口
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
        return R.layout.putao_yellow_page_recharge;
    }

    @Override
    protected int supplyTabs(List<TabInfo> tabs) {
        tabs.add(new TabInfo(FRAGMENT_CHARGETELEPHONE, getString(R.string.putao_charge_tag_title_charge),
                YellowPageChargeTelephoneFragmentNew.class));
        tabs.add(new TabInfo(FRAGMENT_TRAFFICTELEPHONE, getString(R.string.putao_charge_tag_title_tiffic),
                YellowPageTrafficTelephoneFragment.class));
        return FRAGMENT_CHARGETELEPHONE;
    }

    @Override
    protected void onInitFragmentEnd(int index, Fragment fragment) {
        // TODO Auto-generated method stub
      
    }

    @Override
    public void onPageSelected(int position) {
        // TODO Auto-generated method stub
        if(position==1){
            //add xcx 2014-12-30 start 统计埋点
            MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
                    UMengEventIds.DISCOVER_YELLOWPAGE_PHONE_RECHARGE_FLOW_IN);
            //add xcx 2014-12-30 end 统计埋点
        }
        super.onPageSelected(position);
    }
    @Override
    protected void onPageSelectedAction(int index, Fragment fragment) {
        // TODO Auto-generated method stub
        
    }
}
