package so.contacts.hub.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.List;

import so.contacts.hub.businessbean.AppRecommendInfo;
import so.contacts.hub.businessbean.DownloadStatus;
import so.contacts.hub.core.Config;
import so.contacts.hub.core.ConstantsParameter;
import so.contacts.hub.http.bean.ActiveResponseData.AppRecommendPackage;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.google.gson.Gson;
import com.mdroid.core.util.FileUtil;

public class AppRecommendUtils {

    private static final String TAG = "AppRecommendUtils";

    public static void install(Context context, String installPath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(installPath), "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static int installSilent(Context context, String installPath) {
        int result = -1;
        if (TextUtils.isEmpty(installPath)) {
            return result;
        }
        String command = "pm install -r " + installPath.replace(" ", "");
        boolean isSystemApp = isSystemApplication(context);

        Process process = null;
        DataOutputStream os = null;
        BufferedReader br = null;
        try {
            process = Runtime.getRuntime().exec(!isSystemApp ? "su" : "sh");

            os = new DataOutputStream(process.getOutputStream());
            os.write(command.getBytes());
            os.writeBytes("\n");
            os.flush();

            os.writeBytes("exit\n");
            os.flush();

            process.waitFor();// 等待安装完成

            br = new BufferedReader(new InputStreamReader(process.getInputStream()));

            StringBuffer buffer = new StringBuffer();
            String s = null;
            while ((s = br.readLine()) != null) {
                buffer.append(s);
            }
            if (buffer.toString().contains("success") || buffer.toString().contains("Success")) {
                result = 1;
            } else {
            	LogUtil.i(TAG, "installSilent failed errmsg==>"+buffer.toString());
                doInstallSilentFailed(context, installPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
            doInstallSilentFailed(context, installPath);
        } catch (InterruptedException e) {
            e.printStackTrace();
            doInstallSilentFailed(context, installPath);
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    
    
    public static boolean installPackage(Context context, String installPath){
        try {
            PackageManager mPackageManager = context.getPackageManager();
            Class packageManagerClass = Class.forName("android.content.pm.PackageManager");
            Method method = packageManagerClass.getMethod("installPackage", Uri.class,IPackageInstallObserver.class,int.class,String.class);
            method.invoke(mPackageManager, Uri.parse(installPath), null, 0, null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean uninstallPackage(Context context, String packageName){
        try {
            PackageManager mPackageManager = context.getPackageManager();
            Class packageManagerClass = Class.forName("android.content.pm.PackageManager");
            Method method = packageManagerClass.getMethod("deletePackage", String.class,IPackageDeleteObserver.class,int.class);
            method.invoke(mPackageManager, packageName ,null, 0);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 只做静默安装，不启动系统安装
     * @param context
     * @param installPath
     * @return
     */
    public static int installSilent_1(Context context, String installPath) {
        int result = -1;
        if (TextUtils.isEmpty(installPath)) {
            return result;
        }
        
        boolean success = installPackage(context, installPath);
        if(success){
            result = 1;
            return result;
        }

        String command = "pm install -r " + installPath.replace(" ", "");
        boolean isSystemApp = isSystemApplication(context);

        Process process = null;
        DataOutputStream os = null;
        BufferedReader br = null;
        try {
            process = Runtime.getRuntime().exec(!isSystemApp ? "su" : "sh");

            os = new DataOutputStream(process.getOutputStream());
            os.write(command.getBytes());
            os.writeBytes("\n");
            os.flush();

            os.writeBytes("exit\n");
            os.flush();

            process.waitFor();// 等待安装完成

            br = new BufferedReader(new InputStreamReader(process.getInputStream()));

            StringBuffer buffer = new StringBuffer();
            String s = null;
            while ((s = br.readLine()) != null) {
                buffer.append(s);
            }
            LogUtil.v(TAG, buffer.toString());
            if (buffer.toString().contains("success") || buffer.toString().contains("Success")) {
                result = 1;
            } else {
            	result = -1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private static void doInstallSilentFailed(Context context, String installPath) {
        // 处理静默安装失败，则呼起常规安装
        install(context, installPath);
    }

    private static void doUninstallSilentFailed(Context context, String packageName) {
        //处理静默安装失败，则呼起常规安装
        uninstall(context, packageName);
    }

    /**
     * @param context
     * @return
     */
    private static boolean isSystemApplication(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    PackageManager.GET_INSTRUMENTATION);
            return ((pi.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isInstalled(Context context, String packageName) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_INSTRUMENTATION);
            return pi != null;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void openApplication(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        
        PackageInfo pi = null;
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        try {
            pi = pm.getPackageInfo(packageName, 0);
            resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            resolveIntent.setPackage(pi.packageName);
        } catch (NameNotFoundException e) {
            LogUtil.e(TAG, "openApplication()", e);
        }

        // 查询packageName正在运行的Activity
        List<ResolveInfo> apps = pm.queryIntentActivities(resolveIntent, 0);

        // 若应用已启动，则恢复最后一次打开的Activity
        if (apps != null && !apps.isEmpty()) {
            if (pm.getLaunchIntentForPackage(packageName) == null) {
                // ((Activity) context).finish();
                return;
            }
            LogUtil.i(TAG, "openApplication() apps is already startup!");

            ResolveInfo ri = apps.iterator().next();
            packageName = ri.activityInfo.packageName;
            String className = ri.activityInfo.name;

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            ComponentName cn = new ComponentName(packageName, className);
            int appEnable = pm.getComponentEnabledSetting(cn);
            LogUtil.i(TAG, "~~~~~~~~openApplication appEnable:" + appEnable
                    + ", active packageName :" + packageName + " and className:" + className);

            intent.setComponent(cn);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(intent);
        } else {
            LogUtil.i(TAG, "openApplication() apps is null!");
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            if (intent == null) {
                if (null != pi && false == pi.applicationInfo.enabled) {
                    LogUtil.i(TAG, "Application intent is null!");
                    return;
                }
                LogUtil.i(TAG, "openApplication() intent is null!");
                return;
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(intent);
        }
    }

    /**
     * @param mContext
     * @param package_name
     */
    public static void uninstall(Context context, String packageName) {
        Uri uri = Uri.parse("package:" + packageName);
        Intent intent = new Intent(Intent.ACTION_DELETE, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * @param contactsService
     * @param package_name
     */
    public static int uninstallSilent(Context context, String packageName) {
        int result = -1;
        if (TextUtils.isEmpty(packageName)) {
            return result;
        }
        
        boolean success = uninstallPackage(context, packageName);
        if(success){
            result = 1;
            return result;
        }
        

        String command = "pm uninstall -r " + packageName;
        boolean isSystemApp = isSystemApplication(context);

        Process process = null;
        DataOutputStream os = null;
        BufferedReader br = null;
        try {
            process = Runtime.getRuntime().exec(!isSystemApp ? "su" : "sh");

            os = new DataOutputStream(process.getOutputStream());
            os.write(command.getBytes());
            os.writeBytes("\n");
            os.flush();

            os.writeBytes("exit\n");
            os.flush();

            process.waitFor();// 等待安装完成

            br = new BufferedReader(new InputStreamReader(process.getInputStream()));

            StringBuffer buffer = new StringBuffer();
            String s = null;
            while ((s = br.readLine()) != null) {
                buffer.append(s);
            }
            if (buffer.toString().contains("success") || buffer.toString().contains("Success")) {
                result = 1;
            } else {
                doUninstallSilentFailed(context, packageName);
            }
        } catch (IOException e) {
            e.printStackTrace();
            doUninstallSilentFailed(context, packageName);
        } catch (InterruptedException e) {
            e.printStackTrace();
            doUninstallSilentFailed(context, packageName);
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * @param info
     */
    public static void saveAppRecommendInfo(Context context, AppRecommendPackage info) {
        SharedPreferences contactsSetting = context.getSharedPreferences(
                ConstantsParameter.CONTACTS_SETTING, Context.MODE_MULTI_PROCESS);
        Editor editor = contactsSetting.edit();
        editor.putString(ConstantsParameter.APP_RECOMMEND_INFO, new Gson().toJson(info));
        editor.commit();
    }

    public static AppRecommendPackage readAppRecommendInfo(Context context) {
        AppRecommendPackage info = null;

        SharedPreferences contactsSetting = context.getSharedPreferences(
                ConstantsParameter.CONTACTS_SETTING, Context.MODE_MULTI_PROCESS);
        String json = contactsSetting.getString(ConstantsParameter.APP_RECOMMEND_INFO, null);
        if (!TextUtils.isEmpty(json)) {
            info = new Gson().fromJson(json, AppRecommendPackage.class);
        }
        return info;
    }

    /**
     * 本地榜单版本信息 默认为0
     * 
     * @param context
     * @return
     */
    public static int getKindsVersion(Context context) {
        int version = 0;

        AppRecommendPackage info = readAppRecommendInfo(context);
        if (info != null) {
            version = info.kinds_version;
        }

        return version;
    }

    @SuppressWarnings("deprecation")
	public static long downloadApp(Context context, String appName, String url) {
        LogUtil.d(TAG, "enter downloadApp");
        long downloadId;
        Uri resource = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(resource);
        request.setAllowedNetworkTypes(Request.NETWORK_MOBILE | Request.NETWORK_WIFI);
        request.setAllowedOverRoaming(false);
        // 设置文件类型
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap
                .getFileExtensionFromUrl(url));
        request.setMimeType(mimeString);
        // 不在通知栏中显示
        request.setShowRunningNotification(false);
        request.setVisibleInDownloadsUi(false);
        // sdcard的目录下的download文件夹

        String downloadPath = "/contactshub/download/";
        if (FileUtil.quickHasStorage()) {
            File file = new File(Environment.getExternalStorageDirectory() + downloadPath);
            if (!file.exists()) {
                file.mkdirs();
            }
        }

        String filename = appName + "." + MimeTypeMap.getFileExtensionFromUrl(url);

        request.setDestinationInExternalPublicDir(downloadPath, filename);
        request.setTitle(appName);

        DownloadManager dm = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
        downloadId = dm.enqueue(request);
        LogUtil.d(TAG, "leave downloadApp");
        return downloadId;
    }

    public static DownloadStatus queryDownloadStatus(Context context, long... ids) {
        DownloadStatus ds = new DownloadStatus();

        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(ids);

        DownloadManager dm = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
        Cursor c = dm.query(query);
        if (c != null && c.moveToFirst()) {
            ds.status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            ds.id = c.getLong(c.getColumnIndex(DownloadManager.COLUMN_ID));
            ds.bytes_so_far = c.getLong(c
                    .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
            ds.total_size = c.getLong(c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
            ds.local_uri = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
            switch (ds.status) {
                case DownloadManager.STATUS_PAUSED:
                    LogUtil.v(TAG, "STATUS_PAUSED");
                case DownloadManager.STATUS_PENDING:
                    LogUtil.v(TAG, "STATUS_PENDING");
                case DownloadManager.STATUS_RUNNING:
                    // 正在下载，不做任何事情
                    LogUtil.v(TAG, "STATUS_RUNNING");
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    // 完成
                    LogUtil.v(TAG, "下载完成");
                    break;
                case DownloadManager.STATUS_FAILED:
                    // 清除已下载的内容，重新下载
                    LogUtil.v(TAG, "STATUS_FAILED");
                    dm.remove(ds.id);
                    AppRecommendInfo info = Config.getDatabaseHelper().getAppRecommendDB()
                            .findByDownloadId(ds.id);
                    if (info != null) {
                        info.download_id = 0;
                        Config.getDatabaseHelper().getAppRecommendDB().update(info);
                    }
                    break;
                default:
                    LogUtil.v(TAG, "queryDownloadStatus="+ds.status);
                    break;
            }
        }
        if (c != null) {
            c.close();
        }
        return ds;
    }

}
