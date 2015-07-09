package so.contacts.hub.remind;


import so.contacts.hub.active.ActiveInterface;
import so.contacts.hub.active.ActiveUtils;
import so.contacts.hub.active.bean.ActiveEggBean;
import so.contacts.hub.core.Config;
import so.contacts.hub.core.ConstantsParameter;
import so.contacts.hub.ui.BaseActivity;
import so.contacts.hub.ui.yellowpage.bean.YellowParams;
import so.contacts.hub.util.ContactsAppUtils;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.NetUtil;
import so.contacts.hub.util.UMengEventIds;
import so.contacts.hub.util.WebViewDialogUtils;
import so.contacts.hub.util.YellowUtil;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

public abstract class BaseRemindFragmentActivity extends BaseActivity implements ActiveInterface {
    private static final String TAG = "BaseRemindFragmentActivity";
	protected YellowParams mYellowParams;
	protected String mTitleContent;
	protected int mRemindCode;
    
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        /**
         *add code
         *modify by putao_lhq
         *coolui6.0
         *-->start */
         /*if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
             // 设置托盘透明
             getWindow().addFlags(
                     WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
         } else {
         }*/ /*<--end*/
        // add by putao_lhq 2014年10月23日 for 获取基本信息 start
        Intent intent = getIntent();
        if (intent != null) {
        	mYellowParams = (YellowParams)intent.getSerializableExtra(
                    YellowUtil.TargetIntentParams);
            if (mYellowParams != null) {
            	mTitleContent = mYellowParams.getTitle();
            	mRemindCode = mYellowParams.getRemindCode();
            }else{
            	mTitleContent = intent.getStringExtra("title");
            	mRemindCode = intent.getIntExtra("RemindCode", -1);
            }
        }
        
        /** add by zjh 2014-12-04 增加友盟统计 start */
        if (mYellowParams != null) {
        	int entryType = mYellowParams.getEntry_type();
        	if( entryType == YellowParams.ENTRY_TYPE_HOME_PAGE ){
        		MobclickAgentUtil.onEvent(this, UMengEventIds.DISCOVER_YELLOWPAGE_HEADER + 
        				mYellowParams.getCategory_id());
        	}else if( entryType == YellowParams.ENTRY_TYPE_SEARCH_PAGE ){
        		MobclickAgentUtil.onEvent(this, UMengEventIds.DISCOVER_SEARCH_SERVER_ACCESS + 
        				mYellowParams.getCategory_id());
        	}
        }
        /** add by zjh 2014-12-04 增加友盟统计 end */
        
        // add by putao_lhq 2014年10月22日 for active start
        findActiveEgg();
        // add by putao_lhq 2014年10月22日 for active end

    }

    /**
     * 找彩蛋
     */
	private void findActiveEgg() {
		if (!NetUtil.isNetworkAvailable(this)) {
			LogUtil.v(TAG, "net work is available");
        	return;
		}
		Config.execute(new Runnable() {
			
			@Override
			public void run() {
				ActiveEggBean egg = getValidEgg();
				if (null == egg) {
					LogUtil.v(TAG, "egg is null");
					return;
				}
				String reqUrl = ActiveUtils.getRequrlOfSign(egg);
				if (TextUtils.isEmpty(reqUrl)) {
					LogUtil.v(TAG, "request url is invalid");
					return;
				}
				
				LogUtil.i(TAG, "oh yeah, find one egg: "+egg.toString());
				WebViewDialogUtils.startWebDialog(BaseRemindFragmentActivity.this, reqUrl);
				
			}
		});
	}

    // 子类告诉父类自己的节点名称
    // See: RemindConfig.java
    public abstract Integer remindCode();
    
    /**
     * 返回指定节点打点类型， 0-查看消除 1-点击消除
     * @date
     * @author
     * @description
     * @params
     */
    protected int remindType() {
        return RemindManager.getInstance().remindType(remindCode());
    }
        
    /**
     * 如果指定节点是需要查看打点类型，递归计算出指定节点和子节点的打点数量。
     * @date
     * @author
     * @description
     * @params
     */
    protected int remindCount() {
        return RemindManager.getInstance().remindCount(remindCode());
    }
    
    /**
     * 得到指定节点的logo地址
     * @date
     * @author
     * @description
     * @params
     */
    protected String remindLogo() {
        RemindNode node = RemindManager.getInstance().get(remindCode());
        if(node != null)
            return node.getImgUrl();
        else 
            return "";
    }
    
    @Override
    protected void onResume() {
        super.onResume();
//        LogUtil.d(TAG, "onResume remindCode="+remindCode());
        
    }

    @Override
    protected void onPause() {
        super.onPause();
//        LogUtil.d(TAG, "onPause remindCode="+remindCode());

        RemindManager.onRemindClick(remindCode());
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy");
        RemindManager.getInstance().save();
    }

    // 判断是否有活动彩蛋存在，返回有效的彩蛋
    @Override
    public ActiveEggBean getValidEgg(String trigger_url) {
        ActiveEggBean bean = ContactsAppUtils.getInstance().getDatabaseHelper().getActiveDB().qryEggTigger(trigger_url);
        
        if(ActiveUtils.isEggValid(bean))
            return bean;
        else
            return null;
    }
    
    // 判断是否有活动彩蛋存在，返回有效的彩蛋, 默认mUrl
    @Override
    public ActiveEggBean getValidEgg() {
    	if (needMatchExpandParam() && mYellowParams != null) {
    		Intent intent = getIntent();
    		long expand = intent.getLongExtra(ConstantsParameter.CATEGORY_ITEMID, 0);
    		if (expand <= 0) {
    			LogUtil.d(TAG, "ItemId is null, find from category id");
    			expand = mYellowParams.getCategory_id();
    		}
    		ActiveEggBean validEgg = ActiveUtils.getValidEgg(getServiceName(), String.valueOf(expand));
    		if (null == validEgg) {
    			validEgg = ActiveUtils.getValidEgg(getServiceNameByUrl(), String.valueOf(expand));
    		}
			return validEgg;
    	} else {
    		ActiveEggBean validEgg = getValidEgg(getServiceName());
    		if (null == validEgg) {
    			validEgg = getValidEgg(getServiceNameByUrl());
    		}
    		return validEgg;
    	}
    }
    
    @Override
    protected boolean needReset() {
        return true;
    }
}
