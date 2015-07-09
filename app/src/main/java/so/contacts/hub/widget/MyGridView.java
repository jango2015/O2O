package so.contacts.hub.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class MyGridView extends GridView {

	public MyGridView(Context context) {
		super(context);
	}

	public MyGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int spec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, spec);
	}

	// private boolean mClickable = false;
	//
	// private int mMoveNum = 0;

	// @Override
	// public boolean dispatchTouchEvent(MotionEvent ev) {
	// // TODO Auto-generated method stub
	// if(ev.getAction() == MotionEvent.ACTION_DOWN){
	// mClickable = true;
	// mMoveNum = 0;
	// }else if( ev.getAction() == MotionEvent.ACTION_MOVE ){
	// mMoveNum++;
	// if( mMoveNum > 10 ){
	// mClickable = false;
	// return true;
	// }
	// }else if(ev.getAction() == MotionEvent.ACTION_UP){
	// if( !mClickable ){
	// mMoveNum = 0;
	// mClickable = true;
	// this.setPressed(false);
	// return true;
	// }
	// }
	// return super.dispatchTouchEvent(ev);
	// }

	// float preY;
	// @Override
	// public boolean onInterceptTouchEvent(MotionEvent ev) {
	// switch (ev.getAction()) {
	// case MotionEvent.ACTION_DOWN:
	// preY = ev.getY();
	//
	// LogUtil.i("gridview", "onInterceptTouchEvent ACTION_DOWN ");
	// break;
	// case MotionEvent.ACTION_UP:
	// LogUtil.i("gridview", "onInterceptTouchEvent ACTION_UP ");
	// break;
	// case MotionEvent.ACTION_MOVE:
	// LogUtil.i("gridview", "onInterceptTouchEvent ACTION_MOVE ");
	// if((ev.getY() - preY) > 5){
	// return true;
	// }
	// break;
	// default:
	// break;
	// }
	// return super.onInterceptTouchEvent(ev);
	// }
	//
	// @Override
	// public boolean onTouchEvent(MotionEvent ev) {
	// switch (ev.getAction()) {
	// case MotionEvent.ACTION_DOWN:
	// LogUtil.i("gridview", "onTouchEvent ACTION_DOWN ");
	// break;
	// case MotionEvent.ACTION_UP:
	// LogUtil.i("gridview", "onTouchEvent ACTION_UP ");
	// break;
	// case MotionEvent.ACTION_MOVE:
	// LogUtil.i("gridview", "onTouchEvent ACTION_MOVE ");
	// break;
	// default:
	// break;
	// }
	// return super.onTouchEvent(ev);
	// }
}
