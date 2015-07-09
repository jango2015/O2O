package so.contacts.hub.msgcenter.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * 提醒设置的自定义view
 * @author zj 2014-12-18 14:31:46
 *
 */
public class SettingView extends LinearLayout {
        private View hideView;

        private boolean isExpanded = false;

        public SettingView(Context context, View foreView,
                View hideView, boolean isExpanded) {
            super(context);
            this.setOrientation(VERTICAL);

            //防止item里面的view抢点击事件
            setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
            addView(foreView);

            this.hideView = hideView;
            addView(hideView, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));

            setExpanded(isExpanded);
        }

        /**
         * Convenience method to expand or hide the dialogue
         */
        public void setExpanded(boolean expanded) {
            if (expanded) {
                hideView.setVisibility(VISIBLE);
                isExpanded = true;
            } else {
                hideView.setVisibility(GONE);
                isExpanded = false;
            }
        }

        public void setHideView(View hideView) {
            removeView(this.hideView);
            addView(hideView, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));
            this.hideView = hideView;
        }

        public boolean isExpanded() {
            return isExpanded;
        }

    }