package so.contacts.hub.widget;

import com.yulong.android.contacts.discover.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

public class SlashTextView extends TextView {

	private Paint mPaint = null;

	private int mLineColor = Color.TRANSPARENT;
	
	private int mWidth = 0;
	
	private int mHeight = 0;
	
	private static final int VERTICAL_SPACTING = 7;
	
	public SlashTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initData(context);
	}
	
	public SlashTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initData(context);
	}
	
	public SlashTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		initData(context);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		mWidth = w;
		mHeight = h;
	}
	
	private void initData(Context context){
		if( mPaint == null ){
			mPaint = new Paint();
		}
		mLineColor = context.getResources().getColor(R.color.putao_light_black);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		if( mWidth == 0 || mHeight == 0 ){
			return;
		}
		mPaint.setColor(mLineColor);
		canvas.drawLine(0, VERTICAL_SPACTING, mWidth, mHeight - VERTICAL_SPACTING, mPaint);
	}



}
