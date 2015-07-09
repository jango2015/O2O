package so.contacts.hub.push;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import so.contacts.hub.account.PutaoAccount;
import so.contacts.hub.core.Config;
import so.contacts.hub.core.PTUser;
import so.contacts.hub.util.ContactsHubUtils;
import so.contacts.hub.util.LogUtil;
import so.putao.findplug.LBSServiceGaode;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.mdroid.core.util.SystemUtil;

/**
 * Push工具类
 */
public class PushUtil {

	private static final String TAG = "PushUtil";

	/**
	 * 设置极光alias和tag. 
	 * 极光别名和标签规则:
        "" （空字符串）表示取消之前的设置。
        每次调用设置有效的别名，覆盖之前的设置。
        有效的别名组成：字母（区分大小写）、数字、下划线、汉字。
        限制：alias 命名长度限制为 40 字节。（判断长度需采用UTF-8编码）
	 */
	public static void setAliasTags(Context context, TagAliasCallback callback){
        String ptUid = "";
        PTUser ptUser = PutaoAccount.getInstance().getPtUser();
        if( ptUser != null ){
            ptUid = ptUser.getPt_uid().replace("-", "_");
        }
       
        String channelNo = SystemUtil.getChannelNo(context);        
        String appversion = "CoolPad_"+String.valueOf(SystemUtil.getAppVersion(context));
        if(!PushUtil.isValidTagAndAlias(appversion)) {
        	appversion = appversion.replace(".", "_");
        }
        
        String band = SystemUtil.getMachine();
        if(!PushUtil.isValidTagAndAlias(band)) {
            band = band.replace(" ", "_");
            band = band.replace("-","_");//add by hyl 2014-12-23 
        }
        
        Set<String> tags = new java.util.HashSet<String>();
        if(!TextUtils.isEmpty(appversion) && PushUtil.isValidTagAndAlias(appversion)) {
        	tags.add(appversion);
        }
        if(!TextUtils.isEmpty(band) && PushUtil.isValidTagAndAlias(band)) {
        	tags.add(band);
        }
        String city = LBSServiceGaode.getCity2();
        if(!TextUtils.isEmpty(city) && PushUtil.isValidTagAndAlias(city)) {
        	tags.add(city);
        } else if(!TextUtils.isEmpty((city = LBSServiceGaode.getLocCity()))) {
            tags.add(city);
        }
        
        if(!TextUtils.isEmpty(channelNo) && PushUtil.isValidTagAndAlias(channelNo)) {
        	tags.add(channelNo);
        }
        
        tags = JPushInterface.filterValidTags(tags);
        
        LogUtil.i(TAG, "setAlias ptUid="+ptUid);
        LogUtil.i(TAG, "setTags appversion="+appversion);
        LogUtil.i(TAG, "setTags channelNo="+channelNo);
        LogUtil.i(TAG, "setTags band="+band);
        LogUtil.i(TAG, "setTags city="+city);
        
        JPushInterface.setAliasAndTags(context, ptUid, tags, callback);
	}

    // 校验Tag Alias 只能是数字,英文字母和中文
    public static boolean isValidTagAndAlias(String s) {
    	/*
    	 * tags and alias 标签格式问题，不能有中划线 '-'
    	 * modified by hyl 2014-12-23 start
    	 * old code:
    	 * Pattern p = Pattern.compile("^[\u4E00-\u9FA50-9a-zA-Z_-]{0,}$");
    	 */
        Pattern p = Pattern.compile("^[\u4E00-\u9FA50-9a-zA-Z_]{0,}$");
        //modified by hyl 2014-12-23 end
        
        Matcher m = p.matcher(s);
        return m.matches();
    }
	
	/**
	 * 获取应用版本号
	 */
	private static String getVersionName(Context context){
		String version = null;
		if( context != null ){
			try {
				PackageManager packageManager = context.getPackageManager();
				PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
				if( packInfo != null ){
					version = packInfo.versionName;
				}
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				version = null;
			}
		}
		if( version == null ){
			version = "0.0.0";
		}
		return version;
	}

}
