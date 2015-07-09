
package so.contacts.hub.ui.yellowpage;

import android.text.TextUtils;
import so.contacts.hub.ui.web.YellowPageH5Activity;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.UMengEventIds;

import so.contacts.hub.remind.BaseRemindActivity;
import so.contacts.hub.ui.yellowpage.bean.YellowParams;
import so.contacts.hub.util.YellowUtil;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.yulong.android.contacts.discover.R;

public class YellowPageJumpH5Activity extends BaseRemindActivity {
    private Intent mIntent = null;
    private static final int DELAYED_TIME = 100;
    
    private Handler mhandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            mhandle.removeMessages(0);
            if(mIntent != null){
                startActivityForResult(mIntent, 0);
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.putao_yellow_page_item_detail);
        try {
            String targetActivityName = getIntent().getStringExtra("targetActivityName");
            if(targetActivityName != null && targetActivityName.length()>0){
                mIntent = new Intent(YellowPageJumpH5Activity.this, Class.forName(targetActivityName));
            }else{
                mIntent = new Intent(YellowPageJumpH5Activity.this, YellowPageDetailActivity.class);
            }
            //add ljq start 2014_11_19 开启新页面时去除动画
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            //add ljq end 2014_11_19 开启新页面时去除动画
            String businessUrl = getIntent().getStringExtra("url");
            if(businessUrl != null && businessUrl.length()>0){
                mIntent.putExtra("url", businessUrl);
            }
            
            String titleContent = "";
            
            String name = getIntent().getStringExtra("title");
            if(name != null && name.length()>0){
                mIntent.putExtra("title", name);
                titleContent = name;
            }

            YellowParams yellowParams = (YellowParams)getIntent().getSerializableExtra(
                    YellowUtil.TargetIntentParams);
            if(yellowParams != null){
                mIntent.putExtra(YellowUtil.TargetIntentParams, yellowParams);
                titleContent = yellowParams.getTitle();
            }
            
            TextView titleView = (TextView)findViewById(R.id.title);
            titleView.setText(titleContent);
            
            mIntent.putExtra("fromJumpH5", true);
            
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            mIntent = null;
        }

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mhandle.sendEmptyMessageDelayed(0,DELAYED_TIME);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        YellowPageJumpH5Activity.this.finish();
    }

    @Override
    public String getServiceNameByUrl() {
        return null;
    }

    @Override
    public String getServiceName() {
        return null;
    }

    @Override
    public boolean needMatchExpandParam() {
        return false;
    }

    @Override
    public Integer remindCode() {
        return null;
    }

    @Override
    protected boolean needReset() {
        return true;
    }
}
