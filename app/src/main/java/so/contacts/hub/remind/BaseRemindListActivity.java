/**
 * @date	: 
 * @author	:
 * @descrip	:
 */
package so.contacts.hub.remind;


import so.contacts.hub.active.ActiveInterface;
import so.contacts.hub.active.ActiveUtils;
import so.contacts.hub.active.bean.ActiveEggBean;
import so.contacts.hub.util.ContactsAppUtils;
import so.contacts.hub.util.LogUtil;
import android.app.ListActivity;
import android.os.Bundle;

public abstract class BaseRemindListActivity extends ListActivity implements ActiveInterface{
    private static final String TAG = "BaseRemindListActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        LogUtil.d(TAG, "onCreate remindCode="+remindCode());

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
        return getValidEgg(getServiceNameByUrl());
    }
    
}
