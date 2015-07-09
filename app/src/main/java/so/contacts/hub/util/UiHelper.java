
package so.contacts.hub.util;

import java.io.IOException;
import java.net.ConnectException;

import so.contacts.hub.core.Config;
import so.contacts.hub.core.ConstantsParameter;
import so.contacts.hub.gamecenter.receiver.AppDownInstaller;
import so.contacts.hub.http.bean.NewVersionRequestData;
import so.contacts.hub.http.bean.NewVersionResponseData;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.mdroid.core.http.IgnitedHttpResponse;
import com.mdroid.core.util.SystemUtil;
import com.yulong.android.contacts.discover.R;

public class UiHelper {

	public static int widthPixels = 480;
	public static int heightPixels = 800;

	public static final String SECTION_CONTACTS[] = /* { "~", "☆", */{ "☆", "#",
		"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
		"N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
	
	public static final String SECTION_ADD_CONTACTS[] = {"#", "A", "B", "C", "D",
		"E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q",
		"R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    public static int getImageThreshold(Context context) {
        int threshold = context.getResources().getDimensionPixelSize(R.dimen.putao_image_threshold);
        int width = UiHelper.getDisplayMetrics(context).widthPixels;
        return width / 4 > threshold ? width / 4 : threshold;
    }
	
    /**
     * 获取屏幕显示的宽度和高度
     * 
     * @param context 上下文
     * @return 屏幕显示的宽度和高度
     */
    public static DisplayMetrics getDisplayMetrics(Context context) {
        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }

    /**
     * 获取对话框的边距
     * 
     * @return 边距
     */
    public static int getDialogPadding(Context context) {
    	// return getDisplayMetrics(context).widthPixels > 720 ? 40 : 15;
    	return context.getResources().getDimensionPixelSize(getDisplayMetrics(context).widthPixels > 320 ? 
    			R.dimen.putao_dialog_padding_bigscreen : R.dimen.putao_dialog_padding_smallscreen);
    }
    
    public static void checkOrStartCheckUpdate(final Context context) {
        LogUtil.d("PlugService", "plugResume checkOrStartCheckUpdate()");
        if(SystemUtil.isWIFI(context)){
            // 计算是否达到激活检查更新任务
            SharedPreferences setting = context.getSharedPreferences(ConstantsParameter.CONTACTS_SETTING, Context.MODE_MULTI_PROCESS);
            long lastCheckUpdate = setting.getLong("lastCheckUpdate", 0);
            long current = System.currentTimeMillis();
            if (current - lastCheckUpdate >= 1 * 24 * 60 * 60 * 1000) {// 1天时间间隔
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        UiHelper.checkUpdate(context, false);
                    }
                }).start();
            }
        }
    }
    
    private static void checkUpdate(final Context context, final boolean isShowDialog) {
        LogUtil.d("PlugService", "plugResume checkUpdate()");
        // 检查更新
        final NewVersionRequestData requestData = new NewVersionRequestData();
        try {
            IgnitedHttpResponse httpResponse = Config.getApiHttp().post(
                    Config.SERVER, requestData.getData()).send();
            String content = httpResponse.getResponseBodyAsString();
            if (!TextUtils.isEmpty(content)) {
                NewVersionResponseData responseData = requestData.getObject(content);
                // 保存最新检查更新时间
                SharedPreferences setting = context.getSharedPreferences(ConstantsParameter.CONTACTS_SETTING, Context.MODE_MULTI_PROCESS);
                setting.edit().putLong("lastCheckUpdate", System.currentTimeMillis()).commit();

                String localAppVersion = SystemUtil.getAppVersion(context);
                String version = responseData.version;
                LogUtil.d("PlugService", "plugResume checkUpdate"+version);
                if (!TextUtils.isEmpty(version)) {
                    if (!localAppVersion.equals(version)) {
                        LogUtil.d("PlugService", "plugResume checkUpdate downInstallApp");
                        AppDownInstaller.getInstance().downInstallApp(context,
                                responseData.remark, null, responseData.down_url,null);
                    }
                }
            }
        } catch (ConnectException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        
    }
}
