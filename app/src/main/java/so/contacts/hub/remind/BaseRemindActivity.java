/**
 * @date	: 
 * @author	: change
 * @descrip	:
 */
package so.contacts.hub.remind;


import so.contacts.hub.active.ActiveInterface;
import so.contacts.hub.active.ActiveUtils;
import so.contacts.hub.active.bean.ActiveEggBean;
import so.contacts.hub.core.Config;
import so.contacts.hub.core.ConstantsParameter;
import so.contacts.hub.ui.BaseActivity;
import so.contacts.hub.util.ContactsAppUtils;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.NetUtil;
import so.contacts.hub.util.WebViewDialogUtils;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

public abstract class BaseRemindActivity extends BaseActivity implements ActiveInterface {
    private static final String TAG = "BaseRemindActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        LogUtil.d(TAG, "onCreate remindCode="+remindCode());
        // add by putao_lhq 2014年10月22日 for active start
        findActiveEgg();
        // add by putao_lhq 2014年10月22日 for active end
    }
    
    // 子类告诉父类自己的节点名称
    // See: RemindConfig.java
//    public abstract Integer remindCode();
    //modify by lxh.理由：非必须提示所有子类要重写该方法的可以写个默认，需要重写的子类在去
    public Integer remindCode() {
        return null;
    }
    
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
        RemindManager.getInstance().dumps();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
//        LogUtil.d(TAG, "onDestroy");
        RemindManager.getInstance().save();
    }
    
    // 判断是否有活动彩蛋存在，返回有效的彩蛋
    @Override
    public ActiveEggBean getValidEgg(String trigger_url) {
        return ActiveUtils.getValidEgg(trigger_url);
    }
    
    /**
     * 判断是否有活动彩蛋存在，返回有效的彩蛋, 默认mUrl.
     * modify by cj 2015/01/23 for 修改彩蛋扩展参数,增加优先检查expand_param参数,然后检查item_id和category_id
     * 
     */
    @Override
    public ActiveEggBean getValidEgg() {
        Intent intent = getIntent();
    	if (needMatchExpandParam() && intent != null) {
    	    String expand = null;
            expand = intent.getStringExtra(ConstantsParameter.EXPAND_PARAM);
            
            if(TextUtils.isEmpty(expand)) {
                if(mYellowParams != null) {
                    long id = intent.getLongExtra(ConstantsParameter.CATEGORY_ITEMID, 0);
                    if (id <= 0) {
                        id = mYellowParams.getCategory_id();
                    }
                    expand = String.valueOf(id);
                }
            }
            LogUtil.d(TAG, "getValidEgg find expand="+expand);
            if(TextUtils.isEmpty(expand)) {
                return null;
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
  
    /**
     * 找彩蛋
     * @author putao_lhq
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
				WebViewDialogUtils.startWebDialog(BaseRemindActivity.this, reqUrl);
				
			}
		});
	}

    @Override
    public String getServiceName() {
        return this.getClass().getName();
    }

    @Override
    public String getServiceNameByUrl() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean needMatchExpandParam() {
        // TODO Auto-generated method stub
        return false;
    }
	
}
