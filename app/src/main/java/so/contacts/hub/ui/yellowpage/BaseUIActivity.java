package so.contacts.hub.ui.yellowpage;

import so.contacts.hub.ad.AdCode;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.yulong.android.contacts.discover.R;
/**
 * 
 * @author putao_lhq
 *
 */
public abstract class BaseUIActivity extends FragmentActivity{

    private FrameLayout mContainer;
    
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        /**
         *add code
         *modify by putao_lhq
         *coolui6.0
         *-->start */
         if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
             // 设置托盘透明
             getWindow().addFlags(
                     WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
         } else {
         } /*<--end*/
    }
    
    @Override
    public void setContentView(int layoutResID) {
        if (layoutResID == R.layout.putao_base_ui_layout || 
                layoutResID == R.layout.putao_base_ui_with_ad_layout || 
                needReset()) {
            super.setContentView(layoutResID);
        } else {
            if (getAdId() != null) {
                setContentView(R.layout.putao_base_ui_with_ad_layout);
            } else {
                setContentView(R.layout.putao_base_ui_layout);
            }
            initView();
            mContainer = (FrameLayout)findViewById(R.id.container);
            View view = LayoutInflater.from(this).inflate(layoutResID, null);
            mContainer.addView(view);
        }
    }
    
    @Override
    public void setContentView(View view) {
        if (needReset()) {
            super.setContentView(view);
            return;
        }
        setContentView(R.layout.putao_base_ui_layout);
        initView();
        mContainer = (FrameLayout)findViewById(R.id.container);
        mContainer.addView(view);
    }
    
    @Override
    public void setContentView(View view, LayoutParams params) {
        if (needReset()) {
            super.setContentView(view, params);
            return;
        }
        setContentView(R.layout.putao_base_ui_layout);
        initView();
        mContainer = (FrameLayout)findViewById(R.id.container);
        mContainer.addView(view, params);
    }
    
    private void initView() {
        findViewById(R.id.back_layout).setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    
    /**
     * 如果子类不用提供的好的布局方式，则重写此方法，返回true
     * @return
     */
    protected boolean needReset() {
        return false;
    }
    
    /**
     * 设置标题
     * @param title
     */
    protected void setTitle(String title) {
        ((TextView)findViewById(R.id.title)).setText(title);
    }
    
    /**
     * 设置标题
     * @param title
     */
    @Override
    public void setTitle(int resId) {
        ((TextView)findViewById(R.id.title)).setText(resId);
    }
    
    /**
     * 獲取廣告ID，該ID由業務提供
     * 具體定義參考{@link AdCode}
     * @return
     */
    public Integer getAdId(){
        return null;
    }
}
