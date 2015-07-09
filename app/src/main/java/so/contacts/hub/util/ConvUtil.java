package so.contacts.hub.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

import android.util.Base64;

public class ConvUtil {
	private static final String TAG = "ConvUtil";
	
    public static Object convertBase64StringToObj(String base64str) {
        if(base64str == null || "".equals(base64str)) 
            return null;

        Object obj = null;
        byte[] base64 = Base64.decode(base64str.getBytes(), Base64.DEFAULT);
        // 封装到字节流
        ByteArrayInputStream bais = new ByteArrayInputStream(base64);
        try {
            ObjectInputStream bis = new ObjectInputStream(bais);
            obj = bis.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            LogUtil.e(TAG, e.getMessage());
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
            LogUtil.e(TAG, e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.e(TAG, e.getMessage());
        }
        return obj;
    }
    
    
    public static String convertObjToBase64String(Object obj) {
        if(obj == null) {
            return null;
        }
        
        String productBase64 = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            productBase64 = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.e(TAG, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, e.getMessage());
        }
        
        return productBase64;
    }
    
    public static String convUri2File(String uri) {
        if(uri == null) return "";
        if(uri.startsWith("/"))
            return uri;
        else if(uri.startsWith("file://")) {
            String localpath = uri.replaceFirst("file://","");
            return localpath;
        }
        return uri;
    }
    
    
}
