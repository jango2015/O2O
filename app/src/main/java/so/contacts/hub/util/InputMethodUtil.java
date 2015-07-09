package so.contacts.hub.util;

import java.util.List;
import java.util.Map;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class InputMethodUtil {
	
	public static void hideInputMethod(Context context) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
	}

	public static boolean hideInputMethod(View view) {
		InputMethodManager imm = (InputMethodManager) view.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		return imm.hideSoftInputFromWindow(view.getApplicationWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

	public static void showInputMethod(View view) {
		InputMethodManager imm = (InputMethodManager) view.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		// imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
	}
	
	public static void backHome(Context context) {
//		 PackageManager pm = context.getPackageManager();
//		 ResolveInfo homeInfo = pm.resolveActivity(new Intent(
//		 Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME), 0);
//		 ActivityInfo ai = homeInfo.activityInfo;
		 Intent startIntent = new Intent(Intent.ACTION_MAIN);
		 //该参数设置错误，应该设置为Intent.CATEGORY_HOME,避免当未设置默认桌面时，出现全部程序列表选择的BUG
		 startIntent.addCategory(Intent.CATEGORY_HOME);
//		 startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//		 startIntent.setComponent(new ComponentName(ai.packageName,
//		 ai.name));
		 context.startActivity(startIntent);
		 
	}

	/*********
	 * 获取activity栈上一个activity的名字
	 * @param context
	 * @return
	 */
	private static String getLastActivityName(Context context){
        final ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> runningTaskInfos = am.getRunningTasks(2);
        if (null != runningTaskInfos && 2 <= runningTaskInfos.size()) {
            return runningTaskInfos.get(1).topActivity.getClassName();
        }
        return null;
	}
	
}
