package so.contacts.hub.remind.simple;

import android.text.Spannable;

import android.text.style.ImageSpan;

import android.graphics.drawable.Drawable;

import android.text.SpannableString;

import so.contacts.hub.remind.RemindConfig;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.yellow.data.RemindBean;
import android.R.color;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.loader.DataLoader;
import com.yulong.android.contacts.discover.R;

/**
 * 打点layout
 * @author zjh
 */
public class SimpleRemindView extends TextView {

    private static final String TAG = "SimpleRemindView";

    private int mRemindCode = -1;
    
    private RemindBean mRemindBean = null;
	
	private DataLoader mImgDataLoader = null;
	
	private float mTextSize = 0f;
	
	public SimpleRemindView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupLayout(context);
	}
	
	private void setupLayout(Context context){
		mTextSize = getResources().getDimension(R.dimen.putao_remind_textsize);
		setTextColor(Color.WHITE);
	}
	
	/**
     *  显示节点node的打点图标和数字
     */
    private void refreshView() {
        if (mRemindBean == null || mRemindBean.getRemindType() <= RemindConfig.REMIND_TYPE_NONE) {
            this.setVisibility(View.GONE);
            return;
        }
        setText("");
        setTextSize(mTextSize);
        setBackgroundResource(color.transparent);
//        LogUtil.v(TAG, "refreshView " + mRemindBean.toString());

        String text = mRemindBean.getText();
        int count = mRemindBean.getRemindCount();
        String imgUrl = mRemindBean.getImgUrl();
        int style = mRemindBean.getStyle();
        
        if( TextUtils.isEmpty(imgUrl) ){
        	// 无图片
        	if( TextUtils.isEmpty(text) ){
        		// 无文字
        		if(style > 0) {
        	    	// 根据样式显示
        	    	setVisibility(View.VISIBLE);
        	        if(style == RemindConfig.REMIND_STYLE_HOT) {
        	        	setBackgroundResource(R.drawable.putao_icon_logo_hot);
        	        } else if(style == RemindConfig.REMIND_STYLE_HUI) {
						setBackgroundResource(R.drawable.putao_icon_logo_hui);
        	        } else if(style == RemindConfig.REMIND_STYLE_TUAN) {
        	        	setBackgroundResource(R.drawable.putao_icon_logo_tuan);
        	        } else if(style == RemindConfig.REMIND_STYLE_RECOMMENT) {
                        setBackgroundResource(R.drawable.putao_icon_logo_recomment);
                    } else{
                    	setVisibility(View.GONE);
                    }
        	    } else if( count == 0 ){
        	    	// 显示小点
        		    LogUtil.v(TAG, "refreshView Set small prompt visible.");
        			setVisibility(View.VISIBLE);
        			//add by xcx 2015-01-14 start 解决圆点变椭圆的问题
        			try{
        			    final SpannableString ss = new SpannableString("icon");
                        Drawable d = getResources().getDrawable(R.drawable.putao_icon_prompt_s);
                        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                        ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
                        ss.setSpan(span, 0, "icon".length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                        setText(" ");
                        append(ss);
        			}catch(Exception e){
        			  e.printStackTrace();
        			  setText("");
        			  setTextSize(0);
                      setBackgroundResource(R.drawable.putao_icon_prompt_s);
        			}
        			//add by xcx 2015-01-14 end 解决圆点变椭圆的问题
        		} else {
        			// 显示大点
        		    LogUtil.v(TAG, "refreshView Set big prompt visible.");
        		    setVisibility(View.VISIBLE);
        		    setBackgroundResource(R.drawable.putao_icon_prompt);
        			setText(String.valueOf(count));
        		}
        	}else{
        		// 有文字
        		setVisibility(View.VISIBLE);
        		setText(text);
    	    	setBackgroundResource(R.drawable.putao_bubble_bg_red);
        	}
        }else{
        	// 有图片
        	LogUtil.v(TAG, "refreshView load net img imgUrl: " + imgUrl);
        	if( mImgDataLoader != null ){
        		mImgDataLoader.loadData(imgUrl, this);
        	}
    	    if( !TextUtils.isEmpty(text) ){
    	    	setText(text);
    	    	setVisibility(View.VISIBLE);
    	    }
        }
    }    
    
    /**
     * 设置RemindBean后，会自动刷新打点
     */
    public void setRemind(RemindBean remind) {
        mRemindBean = remind;
        refreshView();
    }

    public RemindBean getRemind() {
        return mRemindBean ;
    }
    
	
	public int getRemindCode() {
        return mRemindCode;
    }
	
    public void setRemindCode(int code) {
        mRemindCode = code;
    }
	
	public void setDataLoader(DataLoader loader) {
		if( mImgDataLoader == null ){
			mImgDataLoader = loader;
		}
    }

}
