package so.contacts.hub.util;

import java.io.File;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.db.DatabaseHelper;
import android.os.Environment;

public class ContactsAppUtils {
    
    private static ContactsAppUtils appUtils;
    
    private ContactsAppUtils(){};
    
    public static ContactsAppUtils getInstance(){
        if(appUtils == null){
        	synchronized (ContactsAppUtils.class) {
        		if( appUtils == null ){
        			appUtils = new ContactsAppUtils();
        		}
			}
        }
        return appUtils;
    }
    
    private static DatabaseHelper mDatabaseHelper = null;
    
    
    private String check_code = null;// 验证码

    private String manual_check_code = null;// 自助生成的验证码

    private String current_mobile = null;// 用户的手机号码

    public String getCheck_code() {
        return check_code;
    }

    public void setCheck_code(String check_code) {
        this.check_code = check_code;
    }

    public String getManual_check_code() {
        return manual_check_code;
    }

    public void setManual_check_code(String manual_check_code) {
        this.manual_check_code = manual_check_code;
    }

    public String getCurrent_mobile() {
        return current_mobile;
    }

    public void setCurrent_mobile(String current_mobile) {
        this.current_mobile = current_mobile;
    }

    public String getNetImagesCache() {
        String root = getRootCache();
        if (root == null) {
            return null;
        }
        String rootPath = root + "/net/images";
        File rootFile = new File(rootPath);
        if (rootFile.isDirectory() || rootFile.mkdirs()) {
            return rootPath;
        }
        return null;
    }
    
    public String getRootCache() {
        String rootDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            // SD-card available
            rootDir = Environment.getExternalStorageDirectory()
                    .getAbsolutePath()
                    + "/Android/data/"
                    + ContactsApp.getContext().getPackageName();
        }
        return rootDir;
    }
    
    public String getNetCache() {
        String root = getRootCache();
        if (root == null) {
            return null;
        }
        String rootPath = root + "/net";
        File rootFile = new File(rootPath);
        if (rootFile.isDirectory() || rootFile.mkdirs()) {
            return rootPath;
        }
        return null;
    }
    
    public String getModelCache() {
        String root = getRootCache();
        if (root == null) {
            return null;
        }
        String rootPath = root + "/local/model";
        File rootFile = new File(rootPath);
        if (rootFile.isDirectory() || rootFile.mkdirs()) {
            return rootPath;
        }
        return null;
    }
    
    public DatabaseHelper getDatabaseHelper() {
        if (mDatabaseHelper == null) {
            mDatabaseHelper = DatabaseHelper.getInstance(ContactsApp.getContext());
        }
        return mDatabaseHelper;
    }
}
