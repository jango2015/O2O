/**
 * @date	: 
 * @author	:
 * @descrip	:
 */
package so.contacts.hub.gamecenter.receiver;

import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.businessbean.DownloadStatus;
import so.contacts.hub.util.AppRecommendUtils;
import so.contacts.hub.util.ConvUtil;
import so.contacts.hub.util.LogUtil;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

public class AppDownInstaller {
    private static final String TAG = "AppDownInstaller";
    private static AppDownInstaller mInstance = null;
    
    private static List<WrapCaller> mListener;
    
    private AppDownInstaller() {
        mListener = new CopyOnWriteArrayList<WrapCaller>();            
        LogUtil.i(TAG, "AppDownInstaller initialized.");
    }
    
    public static AppDownInstaller getInstance() {
        if(mInstance == null) {
            mInstance = new AppDownInstaller();
        }
        return mInstance;
    }
    
    public interface AppDownInstallListener {
        public void onDownload(long downloadId, DownloadStatus ds);
        public void onInstall(String action, String pkgName);
    }
    
    /**
     * 下载并安装App
     */
    public synchronized long downInstallApp(Context context, String appName, String pkgName, String url, AppDownInstallListener l) {
        long downloadId = -1;
        try {
            downloadId = AppRecommendUtils.downloadApp(context, appName, url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(downloadId >= 0) {
            WrapCaller caller = new WrapCaller();
            if(context instanceof Activity){
                caller.activity = (Activity)context;
            }
            caller.downloadId = downloadId;
            caller.appName = appName;
            caller.pkgName = pkgName;
            caller.url = url;
            caller.listener = l;
            
            mListener.add(caller);
        }
        return downloadId;
    }
        
    public synchronized void unregisterListener(Activity activity) {
        for(WrapCaller caller : mListener) {
            if(caller.activity == activity) {
                mListener.remove(caller);
                break;
            }
        }
    }
    
    public static class AppDownloadReceiver extends BroadcastReceiver {
        private static final String TAG = "AppDownloadReceiver";
        @Override   
        public void onReceive(Context context, Intent intent) {
            if (intent != null && mListener != null && mListener.size()>0) {
                LogUtil.v(TAG,"enter onReceive "+intent.getAction());
            	
                if(DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                    long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                    DownloadStatus ds = AppRecommendUtils.queryDownloadStatus(ContactsApp.getInstance(), downloadId);
                    
                    LogUtil.i(TAG, "download completed id="+downloadId+" uri=" + ds.local_uri);                        

                    /**
                     * 下载完成回调onDownload接口，并启动新线程安装app
                     */
                    WrapCaller findCaller = null;
                    ListIterator<WrapCaller> it = mListener.listIterator();
                    while(it.hasNext()) {
                        WrapCaller caller = it.next();

                        if(caller.downloadId == downloadId) {
                            findCaller = caller;
                            if(caller.activity != null && !caller.activity.isFinishing() && caller.listener != null) {
                                caller.listener.onDownload(downloadId, ds);
                                break;
                            }
                        }
                    }
                    
                    if(findCaller == null) {
                        LogUtil.d(TAG, "findCaller is null");
                    	return;// 当findCaller为空是,需终止升级动作; modify by ls 2015-03-09
                    }
                    
                    boolean openThread = true;
                    LogUtil.d(TAG, "start install openThread="+openThread);       
                    
                    if(openThread) {
                        Thread t = new AppInstallThread(findCaller, ds.local_uri);
                        t.start();
                    } else {
                        String local_file = ConvUtil.convUri2File(ds.local_uri);
                        if (!TextUtils.isEmpty(local_file)) { // 静默安装
                            if (AppRecommendUtils.installSilent_1(ContactsApp.getInstance(), local_file) > 0) {
                                LogUtil.i(TAG, "installSilent done. url=" + local_file);
                            } else { // 呼叫系统默认安装
                                install(findCaller, ds.local_uri);
                                LogUtil.i(TAG, "installNormal start. url=" + ds.local_uri);
                            }
                        } else {
                            LogUtil.i(TAG, "invalid url="+ds.local_uri);
                        }
                    }                    
                    LogUtil.d(TAG, "leave onReceive size="+mListener.size());
                }
            }
        }   
    };    

    /**
     * 安装线程1.静默安装 2.系统安装
     *
     */
    private static class AppInstallThread extends Thread {
        private static final String TAG = "AppInstallThread";
        private String uri;
        private WrapCaller caller;

        public AppInstallThread(WrapCaller caller, String uri) {
            this.uri = uri;
            this.caller = caller;
        }

        @Override
        public void run() {
            LogUtil.d(TAG, "run");
            String local_file = ConvUtil.convUri2File(uri);
            if (!TextUtils.isEmpty(local_file)) { // 静默安装
                if (AppRecommendUtils.installSilent_1(ContactsApp.getInstance(), local_file) > 0) {
                    LogUtil.i(TAG, "installSilent done. url=" + local_file);
                } else { // 呼叫系统默认安装
                    install(caller, uri);
                    LogUtil.i(TAG, "installNormal start. url=" + uri);
                }
            } else {
                LogUtil.i(TAG, "invalid url="+uri);
            }
        }
    }    
    
    public static class AppPackageReceiver extends BroadcastReceiver {
        private static final String TAG = "AppPackageReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && mListener != null && mListener.size() > 0) {
                LogUtil.v(TAG,"enter onReceive "+intent.getAction());
            	
                String pkgName = intent.getDataString();
                ListIterator<WrapCaller> it = mListener.listIterator();
                while(it.hasNext()) {
                    WrapCaller caller = it.next();
                    if(pkgName != null && pkgName.equals(caller.pkgName)) {
                        if(caller.activity != null && !caller.activity.isFinishing() && caller.listener != null) {
                            caller.listener.onInstall(intent.getAction(), pkgName);
                        }
                    }
                }
            }
        }
    };

    private class WrapCaller {
        Activity activity;
        long downloadId;
        String appName;
        String pkgName;
        String url;
        AppDownInstallListener listener;
    }
    
    private static void install(WrapCaller caller, String installPath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        LogUtil.d(TAG, "install path="+installPath+" uri_path="+Uri.parse(installPath));
        intent.setDataAndType(Uri.parse(installPath), "application/vnd.android.package-archive");
        
        if (caller != null && caller.activity != null && !caller.activity.isFinishing() && caller.listener != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            LogUtil.d(TAG, "install intent===> "+intent.toString());
            caller.activity.startActivityForResult(intent, 100);
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);            
            ContactsApp.getInstance().startActivity(intent);
        }
    }
    
    public synchronized void release() {
        mListener.clear();
        LogUtil.i(TAG, "AppDownInstaller released");
    }
}
