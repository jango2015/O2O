package so.contacts.hub.widget;

import com.yulong.android.contacts.discover.R;
import android.content.Context;

/**
 * 公共对话框生产类
 * 
 * @author pengjianbo
 * 
 */
public class CommonDialogFactory {

	/**
	 * 获取List列表对话框
	 * 
	 * @return
	 */
	public static CommonDialog getListCommonDialog(Context context) {
		CommonDialog dialog = new CommonDialog(context, R.style.putao_Dialog,
				R.layout.putao_common_list_dialog);
		return dialog;
	}

	/**
	 * 获取gridview类型对话框 add by lisheng
	 */
	public static CommonDialog getGridCommonDialog(Context context) {
		CommonDialog dialog = new CommonDialog(context, R.style.putao_Dialog,
				R.layout.putao_train_common_gridview_dialog);
		return dialog;
	}
	
	
	/**
	 * 获取【确定|取消】对话框
	 * 
	 * @param context
	 * @return
	 */
	public static CommonDialog getOkCancelCommonDialog(Context context) {
		CommonDialog dialog = new CommonDialog(context, R.style.putao_Dialog,
				R.layout.putao_common_ok_cancel_dialog);
		return dialog;
	}
	
    /**
     * 获取【确定|取消】对话框 带文本输入框
     * 
     * @param context
     * @return
     */
    public static CommonDialog getEditTextCommonDialog(Context context) {
        CommonDialog dialog = new CommonDialog(context, R.style.putao_Dialog,
                R.layout.putao_common_edittext_dialog);
        return dialog;
    }
	
	/**
	 * 获取【确定|取消】对话框
	 * 不用fragment，使用linearlayout代替
	 * @param context
	 * @return
	 */
	public static CommonDialog getOkCancelCommonLinearLayoutDialog(Context context) {
		CommonDialog dialog = new CommonDialog(context, R.style.putao_Dialog,
				R.layout.putao_common_ok_cancel_dialog);
		return dialog;
	}
	
	   /**
     * 获取【确定】对话框
     * 
     * @param context
     * @return
     */
    public static CommonDialog getOkCommonDialog(Context context) {
        CommonDialog dialog = new CommonDialog(context, R.style.putao_Dialog,
                R.layout.putao_common_ok_dialog);
        return dialog;
    }

}
