package so.contacts.hub.widget;

import java.util.Arrays;

import com.yulong.android.contacts.discover.R;
import so.contacts.hub.util.UiHelper;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class IndexBar extends View {
	private String[] mIndexes;
	private float mItemHeight;
	private float mItemWidth;

	private int mIndexTextSize = UiHelper.widthPixels < 480 ? (UiHelper.widthPixels < 320 ? 8
			: 12)
			: 18;// 18;
	private int mIndexTextColor = 0xFFA6A9AA;
	private Paint mIndexPaint;

	private Paint mIndexBackgroundPaint;

	private int mCurrentIndex;
	private boolean mDisplaySelected;

	private OnIndexChangeListener mChangeListener;

//	private Bitmap mSearchBitmap;

	public IndexBar(Context context) {
		super(context);
		init();
	}

	public IndexBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		mDisplaySelected = false;
		mIndexPaint = new Paint();
		mIndexPaint.setAntiAlias(true);
		mIndexPaint.setFakeBoldText(true);
		mIndexPaint.setTextAlign(Align.CENTER);
		mIndexPaint.setColor(mIndexTextColor);
		mIndexPaint.setTextSize(mIndexTextSize);

		mIndexBackgroundPaint = new Paint();
		mIndexBackgroundPaint.setColor(mIndexTextColor);
		mIndexBackgroundPaint.setStyle(Style.STROKE);
		mIndexBackgroundPaint.setStrokeWidth(3);
//		mSearchBitmap = BitmapFactory.decodeResource(getResources(),
//				R.drawable.icon_search_mini);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec),
				measureHeight(heightMeasureSpec));
	}

	private int measureWidth(int measureSpec) {
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		int desiredWidth = (int) (getPaddingLeft() + getPaddingRight()
				+ mItemWidth + 10);
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

	public IndexBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		final int hight = getMeasuredHeight();
		if (mIndexes != null) {
			mItemHeight = (hight - getPaddingBottom() - getPaddingTop())
					/ mIndexes.length;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		int i = (int) event.getY();
		int idx = (int) (i / mItemHeight);
		if (idx >= mIndexes.length) {
			idx = mIndexes.length - 1;
		} else if (idx < 0) {
			idx = 0;
		}
		if (event.getAction() == MotionEvent.ACTION_DOWN
				|| event.getAction() == MotionEvent.ACTION_MOVE) {
			mCurrentIndex = idx;
			invalidate();
			if (mChangeListener != null) {
				mChangeListener.onChange(idx, mIndexes[idx]);
			}
			setPressed(true);
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			setPressed(false);
		}

		return true;
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		float widthCenter = getMeasuredWidth() / 2;
		Rect bounds = new Rect();
		if (mIndexes != null) {
			for (int i = 0; i < mIndexes.length; i++) {
				if (mCurrentIndex == i && mDisplaySelected) {
					mIndexPaint.getTextBounds(mIndexes[i], 0,
							mIndexes[i].length(), bounds);
					float padding = (mItemHeight - bounds.height()) / 2f;
					canvas.drawRect(new RectF(
							(getWidth() - mItemWidth) / 2f - 5f, mItemHeight
									* i + padding + getPaddingTop(),
							(getWidth() + mItemWidth) / 2 + 5, mItemHeight
									* (i + 1) + padding + getPaddingTop()),
							mIndexBackgroundPaint);
				}
				if (mIndexes[i].equals("~")) {
//					canvas.drawBitmap(mSearchBitmap,
//							(getWidth() - mSearchBitmap.getWidth()) / 2,
//							getPaddingTop(), null);
				} else {
					canvas.drawText(mIndexes[i], widthCenter, mItemHeight
							* (i + 1) + getPaddingTop(), mIndexPaint);
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

	public void setIndexes(String[] indexes) {
		this.mIndexes = indexes;
		if (mIndexes != null) {
			float[] widths = new float[mIndexes.length];
			mIndexPaint.getTextWidths(mIndexes.toString(), 0, mIndexes.length,
					widths);
			Arrays.sort(widths);
			mItemWidth = widths[widths.length - 1];
		}
	}

}