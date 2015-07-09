package so.contacts.hub.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.yulong.android.contacts.discover.R;

public class ProgressDialog extends Dialog {
	private View view;
	private CharSequence mMessage;

	public ProgressDialog(Context context) {
		this(context, R.style.putao_ProgressDialog); // ProgressDialog Dialog
	}
	
	//add ljq start 2015/01/20 预留构造器 boolean 参数暂时无用
    public ProgressDialog(Context context, boolean b) {
        this(context, R.style.putao_ProgressDialog); // ProgressDialog Dialog
    }
    //add ljq end 2015/01/20 预留构造器
    
	public ProgressDialog(Context context, int theme) {
		super(context, theme);
	}

	ProgressDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Window dialogWindow = getWindow();
		// Display d = dialogWindow.getWindowManager().getDefaultDisplay(); //
		// 获取屏幕宽、高用
		WindowManager.LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
		// p.height = (int) (d.getHeight() * 0.6); // 高度设置为屏幕的0.6
		// p.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.8
		dialogWindow.setAttributes(p);
		super.onCreate(savedInstanceState);

		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.putao_progress_dialog, null);

		if (!TextUtils.isEmpty(mMessage)) {
			TextView textView = (TextView) view.findViewById(R.id.msg);
			textView.setText(mMessage);
		}
		setContentView(view, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
	}

	public static ProgressDialog show(Context context, CharSequence msg) {
		return show(context, msg, false);
	}

	public static ProgressDialog show(Context context, int id) {
		return show(context, context.getResources().getString(id), false);
	}

	public static ProgressDialog show(Context context, CharSequence msg,
			boolean cancelable) {
		ProgressDialog dialog = new ProgressDialog(context);
		dialog.setMessage(msg);
		dialog.setCancelable(cancelable);
		dialog.setCanceledOnTouchOutside(cancelable);
		dialog.show();
		return dialog;
	}

	public void setMessage(CharSequence msg) {
		mMessage = msg;
		if (view != null && !TextUtils.isEmpty(mMessage)) {
			TextView textView = (TextView) view.findViewById(R.id.msg);
			textView.setText(mMessage);
		}
	}
	
}
