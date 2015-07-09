package so.contacts.hub.active;

import java.net.URLEncoder;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.account.PutaoAccount;
import so.contacts.hub.active.bean.ActiveEggBean;
import so.contacts.hub.core.Config;
import so.contacts.hub.core.ConstantsParameter;
import so.contacts.hub.core.PTUser;
import so.contacts.hub.remind.BaseRemindActivity;
import so.contacts.hub.util.ContactsAppUtils;
import so.contacts.hub.util.ContactsHubUtils;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.NetUtil;
import so.contacts.hub.util.WebViewDialogUtils;
import so.putao.findplug.LBSServiceGaode;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.mdroid.core.util.Md5Util;
import com.mdroid.core.util.SystemUtil;

public class ActiveUtils {
    static final String TAG = "ActiveUtils";
    
    private static final String encrypt_key = "887~@#***_!";
    
    /**
     * 根据触发点取蛋
     * @param trigger
     * @return
     */
    public static ActiveEggBean getActiveEgg(String trigger) {
        if(TextUtils.isEmpty(trigger)) {
            return null;
        }
        
        return ContactsAppUtils.getInstance().getDatabaseHelper().getActiveDB().qryEggTigger(trigger);
    }
    
    // 检查是否有效蛋
    public static boolean isEggValid(ActiveEggBean egg) {
        if(egg == null){
        	return false;
        }
        long currentTime = System.currentTimeMillis();
        boolean timeValid = currentTime >= egg.start_time &&  currentTime <= egg.end_time;
        LogUtil.d(TAG, "to check the egg is or not valid: " + egg.toString() + " ,time is valid: " + timeValid);
		return egg.active_id>=0 && egg.egg_id>=0 && egg.status!=2 && 
                !TextUtils.isEmpty(egg.request_url) &&  
                timeValid;
    }
    
    // 根据彩蛋组合计算出签名后的url
    public static String getRequrlOfSign(ActiveEggBean egg) {
        PTUser ptUser = PutaoAccount.getInstance().getPtUser();
        if(ptUser == null || TextUtils.isEmpty(ptUser.getPt_token())) {
            return "";
        }
        
        StringBuffer reqUrl = new StringBuffer();
        reqUrl.append(egg.request_url);        
        if(egg.request_url.indexOf("?") < 0){
            reqUrl.append("?");
        } else {
            reqUrl.append("&");
        }
        
        String pt_token = ptUser.getPt_token();
        long time = System.currentTimeMillis();
        String dev_no = SystemUtil.getDeviceId(ContactsApp.getInstance());
        if(dev_no == null)
            dev_no = "";
        
        String sign = Md5Util.md5(pt_token+dev_no+time+encrypt_key);
        reqUrl.append("active_id=").append(egg.active_id)
                .append("&match_op_id=").append(egg.egg_id)
                .append("&pt_token=").append(ptUser.getPt_token())
                .append("&channel_no=").append(SystemUtil.getChannelNo(ContactsApp.getInstance()))
                .append("&brand=").append(SystemUtil.getMachine())
                .append("&mobile=").append(ContactsHubUtils.getPhoneNumber(ContactsApp.getInstance()))
                .append("&dev_no=").append(dev_no)
                .append("&app_version=").append(SystemUtil.getAppVersion(ContactsApp.getInstance()))
                .append("&timestamp=").append(time)
                .append("&lat=").append(LBSServiceGaode.getLatitude())
                .append("&lon=").append(LBSServiceGaode.getLongitude())
                .append("&sign=").append(sign);
        
        LogUtil.d(TAG, "getRequrlOfSign="+reqUrl.toString());
        return reqUrl.toString();
    }
    
    public static String getRequrlOfSign(String url) {
        //PTUser ptUser = Config.getPTUser();
        if(!PutaoAccount.getInstance().isLogin()/*url == null || ptUser == null || TextUtils.isEmpty(ptUser.getPt_token())*/) {
            LogUtil.e(TAG, "getRequrlOfSign return null");
            return "";
        }
        
        StringBuffer reqUrl = new StringBuffer();
        reqUrl.append(url);        
        if(url.indexOf("?") < 0){
            reqUrl.append("?");
        } else {
            reqUrl.append("&");
        }
        
        String pt_token = PutaoAccount.getInstance().getPtUser().getPt_token();/*ptUser.getPt_token();*/
        long time = System.currentTimeMillis();
        String dev_no = SystemUtil.getDeviceId(ContactsApp.getInstance());
        if(dev_no == null)
            dev_no = "";
        
        String sign = Md5Util.md5(pt_token+dev_no+time+encrypt_key);
        reqUrl.append("pt_token=").append(pt_token/*ptUser.getPt_token()*/)
                .append("&channel_no=").append(SystemUtil.getChannelNo(ContactsApp.getInstance()))
                .append("&brand=").append(SystemUtil.getMachine())
                .append("&mobile=").append(ContactsHubUtils.getPhoneNumber(ContactsApp.getInstance()))
                .append("&dev_no=").append(dev_no)
                .append("&app_version=").append(SystemUtil.getAppVersion(ContactsApp.getInstance()))
                .append("&timestamp=").append(time)
                .append("&lat=").append(LBSServiceGaode.getLatitude())
                .append("&lon=").append(LBSServiceGaode.getLongitude())
                .append("&sign=").append(sign);
        
        LogUtil.d(TAG, "getRequrlOfSign="+reqUrl.toString());
        return reqUrl.toString();
    }    
    
    public static String getRequrlOfSignTail() {
        PTUser ptUser = PutaoAccount.getInstance().getPtUser();/*Config.getPTUser();*/
        if(ptUser == null || TextUtils.isEmpty(ptUser.getPt_token())) {
            LogUtil.e(TAG, "getRequrlOfSign return null");
            return "";
        }
        
        String open_token = PutaoAccount.getInstance().getOpenToken();
        if(TextUtils.isEmpty(open_token)) {
            open_token = "";
        }
        
        StringBuffer reqUrl = new StringBuffer();
        
        String pt_token = ptUser.getPt_token();
        long time = System.currentTimeMillis();
        String dev_no = SystemUtil.getDeviceId(ContactsApp.getInstance());
        if(dev_no == null)
            dev_no = "";
        
        String sign = Md5Util.md5(pt_token+dev_no+time+encrypt_key);
        reqUrl.append("pt_token=").append(ptUser.getPt_token())
                .append("&open_token=").append(open_token)
                .append("&channel_no=").append(SystemUtil.getChannelNo(ContactsApp.getInstance()))
                .append("&brand=").append(URLEncoder.encode(SystemUtil.getMachine()))
                .append("&mobile=").append(ContactsHubUtils.getPhoneNumber(ContactsApp.getInstance()))
                .append("&dev_no=").append(dev_no)
                .append("&app_version=").append(SystemUtil.getAppVersion(ContactsApp.getInstance()))
                .append("&timestamp=").append(time)
                .append("&lat=").append(LBSServiceGaode.getLatitude())
                .append("&lon=").append(LBSServiceGaode.getLongitude())
                .append("&sign=").append(sign);
        
        LogUtil.d(TAG, "getRequrlOfSign="+reqUrl.toString());
        return reqUrl.toString();
    }    
    
    // 添加默认的测试用活动
    public static void addDefaultActive() {
        long active_id = 1;
        String req_url = "http://192.168.1.59:9999/Activity_Server/triggerCallback.s";
        
        ActiveEggBean egg = new ActiveEggBean();
        egg.active_id = active_id;
        egg.egg_id = 1;
        egg.request_url = req_url;
//        egg.trigger = "http://lite.m.dianping.com/zOxOda-j7H";
        egg.trigger = "so.contacts.hub.ui.yellowpage.YellowPageH5Activity";
        egg.expand_param = "19";
        egg.trigger_type = 1;
        egg.start_time = System.currentTimeMillis();
        egg.end_time = egg.start_time + 1000*60*60*24L;
        egg.status = 1;
        
        ContactsAppUtils.getInstance().getDatabaseHelper().getActiveDB().insertActiveEgg(egg);
    }
    
    // 判断是否有活动彩蛋存在，返回有效的彩蛋
    public static ActiveEggBean getValidEgg(String trigger) {
        ActiveEggBean bean = ContactsAppUtils.getInstance().getDatabaseHelper().getActiveDB().qryEggTigger(trigger);
        
        if(ActiveUtils.isEggValid(bean))
            return bean;
        else
            return null;
    }
    
    /**
     * 
     * @param trigger
     * @param expand
     * @return
     * @author putao_lhq
     */
    public static ActiveEggBean getValidEgg(String trigger, String expand) {
    	LogUtil.d(TAG, "trigger: " + trigger + " ,expand: " + expand);
    	if (TextUtils.isEmpty(trigger) || TextUtils.isEmpty(expand)) {
    		return null;
    	}
    	ActiveEggBean egg = ContactsAppUtils.getInstance().getDatabaseHelper().getActiveDB().qryEggTigger(trigger, expand);
    	if (egg == null || !isEggValid(egg)) {
    		return null;
    	}
    	return egg;
    }
    
    /**
     * 直接根据触发条件和扩展参数查找彩蛋后打开
     * @param activity
     * @param trigger
     * @param expand
     */
    public static void findValidEggAndStartWebDialog(final Activity activity, final String trigger, final String expand) {
        LogUtil.d(TAG, "findValidEggAndStartWebDialog trigger="+trigger+" expand="+expand);
        if (!NetUtil.isNetworkAvailable(activity)) {
            return;
        }
        
        Config.execute(new Runnable() {
            
            @Override
            public void run() {
                ActiveEggBean egg = null;
                if(TextUtils.isEmpty(expand)) {
                    egg = getValidEgg(trigger);
                } else {
                    egg = getValidEgg(trigger, expand);
                }
                
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
                WebViewDialogUtils.startWebDialog(activity, reqUrl);
            }
        });
    }
    
}
