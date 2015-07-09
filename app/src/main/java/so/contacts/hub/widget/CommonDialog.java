package so.contacts.hub.widget;

import java.util.ArrayList;

import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.UiHelper;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.yulong.android.contacts.discover.R;

/**
 * 对话框公共对话框
 * <p/>
 * 
 * 通过CommonDialogFactory获取实力
 * 
 * @author pengjianbo
 * 
 */
public class CommonDialog extends Dialog {

	// common view
	private TextView mTitleTv;

	// R.layout.common_list_dialog
	private ListView mListView;

	// R.layout.common_ok_cancel_dialog
	// 确定、取消
	private TextView mOkBtn, mCancelBtn;
	// 用于显示Message信息
	private TextView mMessageTv;
	// 用于编辑输入信息
    private EditText mInputEt;
	
    //add by lisheng 显示热门车站
    private GridView mGridView;
    private TextView showMoreStation;
    
	// 用户扩卡中
	private FrameLayout mFrameLayout;
	
	private Context context = null;

	protected CommonDialog(Context context, int resLayout) {
		super(context);
		this.context = context;
		init(resLayout);
	}

	protected CommonDialog(Context context, int theme, int resLayout) {
		super(context, theme);
		this.context = context;
		init(resLayout);
	}

	private void init(int resLayout) {
		setContentView(resLayout);

		final DisplayMetrics displayMetrics = UiHelper.getDisplayMetrics(context);
		int screenWidth = displayMetrics.widthPixels;
		//putao_lhq modify for coolui6.0 start
		//int padding = UiHelper.getDialogPadding(context);

		// 设置对话框宽度
		//getWindow().setBackgroundDrawable(null);
		getWindow().setBackgroundDrawableResource(R.color.putao_transparent);
		WindowManager.LayoutParams p = getWindow().getAttributes();
		//p.width = screenWidth - padding * 2;
		p.width = screenWidth;
		getWindow().setAttributes(p);
		getWindow().setGravity(Gravity.BOTTOM); //此处可以设置dialog显示的位置
		//putao_lhq modify for coolui6.0 end
		getWindow().setWindowAnimations(R.style.putao_AnimationDialog); // 添加动画

		// 查找控件
		mTitleTv = (TextView) findViewById(R.id.title_tv);

		if (resLayout == R.layout.putao_common_list_dialog) {
			mListView = (ListView) findViewById(R.id.listview);
		} else if (resLayout == R.layout.putao_common_ok_cancel_dialog) {
			mOkBtn = (TextView) findViewById(R.id.ok_btn);
			mCancelBtn = (TextView) findViewById(R.id.cancel_btn);
			mMessageTv = (TextView) findViewById(R.id.message_tv);
			mFrameLayout = (FrameLayout) findViewById(R.id.framelayout);
			setCancelButtonClickListener(null);
		} else if (resLayout == R.layout.putao_common_edittext_dialog) {
			mOkBtn = (TextView) findViewById(R.id.ok_btn);
			mCancelBtn = (TextView) findViewById(R.id.cancel_btn);
			mInputEt = (EditText) findViewById(R.id.input_et);
			mFrameLayout = (FrameLayout) findViewById(R.id.framelayout);
			setCancelButtonClickListener(null);
		} else if(resLayout==R.layout.putao_train_common_gridview_dialog){//add by lisheng 
			mGridView = (GridView) findViewById(R.id.hot_gridview);
			showMoreStation =(TextView) findViewById(R.id.more_station);
			//add by lisheng 
		} else if(resLayout==R.layout.putao_common_ok_dialog){//add by ljq
            mOkBtn = (TextView)findViewById(R.id.ok_btn);
            mMessageTv = (TextView)findViewById(R.id.message_tv);
            mFrameLayout = (FrameLayout)findViewById(R.id.framelayout);
		}//add by ljq
	}
	
	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		// Dialog所属的Activity没有结束时，则dismiss
		if ( context != null && !((Activity)context).isFinishing() ){
			super.dismiss();
        }
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitleTv.setText(title);
	}

	@Override
	public void setTitle(int titleId) {
		mTitleTv.setText(titleId);
	}

	/**
	 * 设置dialog内容
	 * @param messageId
	 */
	public void setMessage(int messageId){
	    mMessageTv.setText(messageId);
	}
	
	/**
	 * 设置dialog内容
	 * @param message
	 */
	public void setMessage(CharSequence message){
	    mMessageTv.setText(message);
	}
	
	/**
	 * 获取ListView控件
	 * 
	 * @return
	 */
	public ListView getListView() {
		return mListView;
	}

	/**
	 * 获取GridView控件
	 */
	public GridView getGridView(){
		return mGridView;
	}
	
	
	/**
	 * 获取标题TextView控件
	 * 
	 * @return
	 */
	public TextView getTitleTextView() {
		return mTitleTv;
	}

	/**
	 * 获取确定按钮
	 * 
	 * @return
	 */
	public TextView getOkButton() {
		return mOkBtn;
	}

	public CommonDialog setOkButton(String text) {
		mOkBtn.setText(text);
		return this;
	}

	
	public TextView getMoreStation(){
		return showMoreStation;
	}
	
	
	
	/**
	 * 获取取消按钮
	 * 
	 * @return
	 */
	public TextView getCancelButton() {
		return mCancelBtn;
	}

	public CommonDialog setCancelutton(String text) {
		mCancelBtn.setText(text);
		return this;
	}

	/**
	 * 获取显示信息View
	 * 
	 * @return
	 */
	public TextView getMessageTextView() {
		return mMessageTv;
	}
	
	
	/**
     * 获取输入信息View
     * 
     * @return
     */
    public TextView getInputEditView() {
        return mInputEt;
    }

	public void hideBottom() {
		findViewById(R.id.bottom_bar).setVisibility(View.GONE);
		mCancelBtn.setVisibility(View.GONE);
		mOkBtn.setVisibility(View.GONE);
		// findViewById(R.id.spilt_line).setVisibility(View.GONE);
	}

	/**
	 * 用户扩展对话框显示
	 * 
	 * @return
	 */
	public FrameLayout getExpandFrameLayout() {
		return mFrameLayout;
	}

	/**
	 * 设置ListView ItemClick
	 * 
	 * @param itemClickListener
	 */
	public void setListViewItemClickListener(
			OnItemClickListener itemClickListener) {
		if (mListView != null) {
			mListView.setOnItemClickListener(itemClickListener);
		} else {
			throw new NullPointerException("ListView 为空");
		}
		this.dismiss();
	}

	/**
	 * 设置GridView item的点击事件
	 * add by lisheng
	 */
	public void setGridViewItemClickListener(
			OnItemClickListener itemClickListener) {
		if (mGridView != null) {
			mGridView.setOnItemClickListener(itemClickListener);
		} else {
			throw new NullPointerException("mGridView 为空");
		}
		this.dismiss();
	}
	
	
	
	
	/**
	 * 设置确定按钮事件
	 * 
	 * @param clickListener
	 */
	public void setOkButtonClickListener(View.OnClickListener clickListener) {
		if (mOkBtn != null) {
			mOkBtn.setOnClickListener(clickListener);
		} else {
			throw new NullPointerException("没有找到确定按钮");
		}
	}

	/**
	 * 设置取消按钮事件
	 * 
	 * @param clickListener
	 */
	public void setCancelButtonClickListener(View.OnClickListener clickListener) {
		if (mCancelBtn != null) {
			if (clickListener == null) {
				clickListener = new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dismiss();
					}
				};
			}
			mCancelBtn.setOnClickListener(clickListener);
		} else {
			throw new NullPointerException("没有找到取消按钮");
		}
	}

	/**
	 * 设置ListView Adapter
	 * 
	 * @param adapter
	 */
	public void setListAdapter(BaseAdapter adapter) {
		if (adapter != null && mListView != null) {
			mListView.setAdapter(adapter);
		} else {
			throw new NullPointerException("adapter 或 ListView 为空");
		}
	}

	/**
	 * 设置GridView Adatper
	 */
	public void setGridViewAdapter(BaseAdapter adapter){
		if (adapter != null && mGridView != null) {
			mGridView.setAdapter(adapter);
		} else {
			throw new NullPointerException("adapter 或 GridView 为空");
		}
	}
	
	
	/**
	 * 设置ListView控件数据库，调用了该方法不需要再setListAdapter <br/>
	 */
	public void setListViewDatas(String[] data) {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
				R.layout.putao_common_dialog_base_lv_item, data);
		if (mListView != null) {
			mListView.setAdapter(adapter);
		} else {
			throw new NullPointerException("ListView 为空");
		}
	}
	
	/**
	 * add by putao_lhq
	 * @param data
	 */
	public void setSingleChoiceListViewDatas(String[] data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                R.layout.putao_list_item_single_choice, data);
        if (mListView != null) {
            mListView.setItemsCanFocus(false);
            mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            mListView.setAdapter(adapter);
        } else {
            throw new NullPointerException("ListView 为空");
        }
    }
	
	/**
	 * add by putao_lhq
	 * @param data
	 */
	public void setSingleChoiceListViewDatas(ArrayList<String> data){
        if( data == null || data.size() == 0 ){
            return;
        }
        int size = data.size();
        setSingleChoiceListViewDatas((String[]) data.toArray(new String[size]));
    }
	
	public void setListViewDatas(ArrayList<String> data){
		if( data == null || data.size() == 0 ){
			return;
		}
		int size = data.size();
		setListViewDatas((String[]) data.toArray(new String[size]));
	}
	
}
