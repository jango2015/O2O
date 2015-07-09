package so.contacts.hub.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class MyFrameLayout extends LinearLayout {

    public MyFrameLayout(Context context) {
        super(context);
        setWeightSum(1);
    }

    public MyFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MyFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(){
        
    }
}
