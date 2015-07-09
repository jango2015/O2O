package so.contacts.hub.push;

import so.contacts.hub.ui.yellowpage.bean.YellowParams;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.UMengEventIds;
import so.contacts.hub.util.YellowUtil;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

/**
 * Notification 中转 Activity
 * @author heyalin 2014-11-25
 */
public class NotifyActivity extends Activity {
	private static final String TAG = "NotifyActivity";
	private int fromType = -1;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        initIntent();

        if(CoolpadPushUtil.CP_PUSH_TYPE_NOTIFY == fromType) {
	        //酷派回执-通知消息点击事件
	        CoolpadPushUtil.sendClickAction(this, UMengEventIds.NOTIFICATION_COOLPAD_ALREADY_CLICKED);
	        //友盟统计-通知消息点击事件
	        MobclickAgentUtil.onEvent(this, UMengEventIds.NOTIFICATION_COOLPAD_ALREADY_CLICKED);
        }
        
        finish();
    }

    /**
     * 处理intent
     */
    private void initIntent() {
        Intent intent = getIntent();
        Bundle bundle = getIntent().getExtras();
        int pageIndex = bundle.getInt("PageIndex", -1);
        pageIndex = intent.getIntExtra("PageIndex", -1);
        int intentType = intent.getIntExtra("intentType", -1);
        String intentActivity = intent.getStringExtra("intentActivity");
        String url = intent.getStringExtra("url");
        String title = intent.getStringExtra("title");
        String words = intent.getStringExtra("words");
        String category = intent.getStringExtra("category");
        int remindcode = intent.getIntExtra("remindcode", -1);

        fromType = intent.getIntExtra("fromType", -1);
        
        LogUtil.d(TAG, "initIntent\nPageIndex="+pageIndex+"\nintentType="+intentType+
        		"\nintentActivity="+intentActivity+"\nurl="+url+"\ntitle="+title+"\nwords="+words+
        		"\ncategory="+category+"\nremindcode="+remindcode);
        
        Intent notificationIntent = null;
        /*if( pageIndex == -1 ){
            // 跳转到指定页面
            if( intentType > 0 ){
                try {
                    notificationIntent = new Intent(this, Class.forName(intentActivity));
                } catch (ClassNotFoundException e) {
                    notificationIntent = null;
                } 
                if( intentType == 2 ){
                    // 跳转类型为H5类型
                    notificationIntent.putExtra("url", url);
                }
                notificationIntent.putExtra("title", title);
            }
        }else */if( pageIndex == 2 ){
            // 跳转到联系人黄页页面
            notificationIntent = new Intent("android.intent.action.YELLOWPAGE");
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }else if(!TextUtils.isEmpty(intentActivity)){
			// 点击通知,打开activity
			YellowParams params = new YellowParams();
			params.setTitle(title);
			params.setUrl(url);
			params.setEntry_type(YellowParams.ENTRY_TYPE_NOTIFICATION_PAGE);
			params.setWords(words);
			params.setCategory(category);
			params.setRemindCode(remindcode);

			LogUtil.i(TAG, "pt notification go to "+intentActivity);

			try {
				notificationIntent = new Intent(this, Class.forName(intentActivity));
				notificationIntent.putExtra(YellowUtil.TargetIntentParams, params);
				notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				
			} catch(Exception e) {
			    e.printStackTrace();
			}
        }
        startActivity(notificationIntent);//执行通知原有的intent跳转
    }
    
}
