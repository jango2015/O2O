
package so.contacts.hub.widget;

import java.util.Arrays;
import java.util.Map;

import so.contacts.hub.util.UiHelper;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

public class IndexBarContacts extends View {
    private String[] mIndexes = UiHelper.SECTION_CONTACTS;

    private Map<Integer, String> mPosMap;

    private float mItemHeight;

    private float mItemWidth;

    // private int mIndexTextSize = SDKConfig.widthPixels < 480 ?
    // (SDKConfig.widthPixels < 320 ? 8 : 12) : 18 ;//18;
    private final int mIndexTextColor = 0xFFA6A9AA;

    private final int mIndexHasTextColor = 0xFFA6A9AA;// 0xFF000000;//2013-8-12
                                                      // 全变灰色

    private Paint mIndexPaint;

    private Paint mIndexBackgroundPaint;

    private int mCurrentIndex;

    private boolean mDisplaySelected;

    private OnIndexChangeListener mChangeListener;

//    private Bitmap mSearchBitmap;

    private PopupWindow mPopupWindow;

    private TextView mPopupText;

    public IndexBarContacts(Context context) {
        super(context);
        init();
    }

    public IndexBarContacts(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

        mDisplaySelected = false;
        mIndexPaint = new Paint();
        mIndexPaint.setAntiAlias(true);
        // mIndexPaint.setFakeBoldText(true);
        mIndexPaint.setTextAlign(Align.CENTER);
        mIndexPaint.setColor(mIndexTextColor);
        // mIndexPaint.setTextSize(mIndexTextSize);

        TextView textView = new TextView(getContext());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        // 设置字体大小，兼容不同分辨率
        mIndexPaint.setTextSize(textView.getTextSize());

        mIndexBackgroundPaint = new Paint();
        mIndexBackgroundPaint.setColor(mIndexTextColor);
        mIndexBackgroundPaint.setStyle(Style.STROKE);
        mIndexBackgroundPaint.setStrokeWidth(3);
//        mSearchBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_search_mini);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        int desiredWidth = (int)(getPaddingLeft() + getPaddingRight() + mItemWidth + 10);
        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            return specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            return desiredWidth < specSize ? desiredWidth : specSize;
        } else {
            return desiredWidth;
        }
    }

    private int measureHeight(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        int desiredHeight = 300 + getPaddingTop() + getPaddingBottom();
        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            return specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            return desiredHeight < specSize ? desiredHeight : specSize;
        } else {
            return desiredHeight;
        }
    }

    public IndexBarContacts(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        final int hight = getMeasuredHeight();
        if (mIndexes != null) {
            mItemHeight = (hight - getPaddingBottom() - getPaddingTop()) / mIndexes.length;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mIndexes == null) {
            return true;
        }
        super.onTouchEvent(event);
        int i = (int)event.getY();
        int idx = (int)(i / mItemHeight);
        if (idx >= mIndexes.length) {
            idx = mIndexes.length - 1;
        } else if (idx < 0) {
            idx = 0;
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN
                || event.getAction() == MotionEvent.ACTION_MOVE) {
            mCurrentIndex = idx;
//            showPopup(idx, event.getRawX(), event.getRawY());
            invalidate();
             if (mChangeListener != null) {
             mChangeListener.onChange(idx, mIndexes[idx]);
             }
            setPressed(true);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            setPressed(false);
            dismissPopup();
        }

        return true;
    }

    private void dismissPopup() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }

    private void showPopup(int item, float x, float y) {
        if (mPopupWindow == null) {
            mPopupWindow = new PopupWindow(130, 130);
            mPopupText = new TextView(getContext());
            mPopupText.setTextColor(Color.rgb(0, 130, 251));
            mPopupText.setTextSize(50);
            mPopupText.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
            mPopupWindow.setContentView(mPopupText);
        }
        String text = "";
        if (item == 0) {
            text = "#";
        } else {
            text = Character.toString((char)('A' + item - 1));
        }
        mPopupText.setText(text);
        if (mPopupWindow.isShowing()) {
            mPopupWindow.update((int)x, (int)y, 130, 130);
        } else {
            mPopupWindow.showAtLocation(getRootView(), Gravity.NO_GRAVITY, (int)x, (int)y);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float widthCenter = getMeasuredWidth() / 2;
        Rect bounds = new Rect();
        if (mIndexes != null) {
            for (int i = 0; i < mIndexes.length; i++) {
                if (mPosMap != null && mPosMap.containsValue(mIndexes[i])) {
                    mIndexPaint.setColor(mIndexHasTextColor);
                    mIndexBackgroundPaint.setColor(mIndexHasTextColor);
                } else {
                    mIndexPaint.setColor(mIndexTextColor);
                    mIndexBackgroundPaint.setColor(mIndexTextColor);
                }
                if (mCurrentIndex == i && mDisplaySelected) {
                    mIndexPaint.getTextBounds(mIndexes[i], 0, mIndexes[i].length(), bounds);
                    float padding = (mItemHeight - bounds.height()) / 2f;
                    canvas.drawRect(new RectF((getWidth() - mItemWidth) / 2f - 5f, mItemHeight * i
                            + padding + getPaddingTop(), (getWidth() + mItemWidth) / 2 + 5,
                            mItemHeight * (i + 1) + padding + getPaddingTop()),
                            mIndexBackgroundPaint);
                }

                if (mIndexes[i].equals("~")) {
//                    canvas.drawBitmap(mSearchBitmap, (getWidth() - mSearchBitmap.getWidth()) / 2,
//                            getPaddingTop(), null);
                } else {
                    canvas.drawText(mIndexes[i], widthCenter, mItemHeight * (i + 1)
                            + getPaddingTop(), mIndexPaint);
                }
            }
        }
    }

    public void setSelectIndex(int index) {
        mCurrentIndex = index;
        invalidate();
    }

    public void setOnIndexChangeListener(OnIndexChangeListener changeListener) {
        this.mChangeListener = changeListener;
    }

    public interface OnIndexChangeListener {
        void onChange(int index, String indexChar);
    }

    public String[] getIndexes() {
        return mIndexes;
    }

    public void setIndexes(String[] indexes, Map<Integer, String> posMap) {
        this.mIndexes = indexes;
        if (posMap != null) {
            this.mPosMap = posMap;
        }
        if (mIndexes != null) {
            float[] widths = new float[mIndexes.length];
            mIndexPaint.getTextWidths(mIndexes.toString(), 0, mIndexes.length - 1, widths);
            Arrays.sort(widths);
            mItemWidth = widths[widths.length - 1];
        }
        invalidate();
    }

}
