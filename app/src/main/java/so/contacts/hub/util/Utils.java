package so.contacts.hub.util;

import com.coolcloud.uac.android.common.util.TextUtils;

import android.widget.EditText;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

/**
 * so.contacts.hub.uitl
 * 
 * @author kzl
 * @created at 13-6-7 下午3:37
 */
public class Utils {
    private static final String TAG = "Utils";
    
    public static Handler mhandler = new Handler(Looper.getMainLooper());
    public static void _BitmapRecyle(final Bitmap bm){
        LogUtil.e(TAG, "bm == " + bm);
        new Exception().printStackTrace();
        mhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(bm != null){
                   bm.recycle();
                }
            }
        }, 100);
    }
    
	public static void showToast(Context context, int strResId, boolean isLong){
		Toast.makeText(context, context.getResources().getString(strResId), 
				isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
	}
	
	public static void showToast(Context context, String showText, boolean isLong){
		Toast.makeText(context, showText, 
				isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * dip转为 px
	 */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 *  px 转为 dip
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	
	
	/**
     * 实现号码3-4-4格式
     * @param contents
     * @param edit
     */
	public static String formatPhoneNum(String contents){
	    if(TextUtils.isEmpty(contents)){
	        return "";
	    }
	    String newStr=contents.replace(" ", "");
	    int length = newStr.length();
	    
	    if(length<3){
	        //不用处理
	    }else if(length==3){
	        newStr=newStr+" ";
	    }else if(length<7){
	        newStr=newStr.substring(0, 3)+" "+newStr.substring(3);
	    }else if(length==7){
	        newStr=newStr.substring(0, 3)+" "+newStr.substring(3)+" ";
	    }else{
	        newStr=newStr.substring(0, 3)+" "+newStr.substring(3,7)+" "+newStr.substring(7);
	    }
	    
	    return newStr;
	}
	
	/**
	 * 号码输入编辑框，实现号码3-4-4格式
	 * @param contents
	 * @param edit
	 */
	public static void setEditPhoneNumFormat(String contents,EditText edit){
	    if(TextUtils.isEmpty(contents)){
	        return;
	    }
	    int length = contents.length();
        if(length == 4){
            if(contents.substring(3).equals(" ")){ 
//                edit.setText(contents);
//                edit.setSelection(contents.length());
            }else{
                contents = contents.substring(0, 3) + " " + contents.substring(3);
                edit.setText(contents);
                edit.setSelection(contents.length());
            }
        }
        else if(length == 9){
            if(contents.substring(8).equals(" ")){
//                edit.setText(contents);
//                edit.setSelection(contents.length());
            }else{
                contents = contents.substring(0, 8) + " " + contents.substring(8);
                edit.setText(contents);
                edit.setSelection(contents.length());
            }
        }
	}
}
