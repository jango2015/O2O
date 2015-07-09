package so.contacts.hub.widget;



import com.yulong.android.contacts.discover.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.LinearLayout;

/**
 * Created with zj	2014-09-20 11:53:02
 */
public class SlideView extends LinearLayout implements View.OnClickListener,
		Animation.AnimationListener {

	private Interpolator interpolator;

	private static int sDefaultDuration;

	public interface OnSlideListener {

		public void onClick(SlideView view);

		public void onSlideStart(SlideView view);

		public void onSlideEnd(SlideView view);
	}

	private OnSlideListener mListener;

	private boolean mIsSlided;

	private boolean mIsDefaultAnimated;

	private View foreView;

	private View backView;

	private TranslateAnimation mAnimation;

	private boolean mIsSliding;

	private int mheight;

	private TranslateAnimation mReverseAnimation;

	private TranslateAnimation upToDownAnimation;

	private TranslateAnimation downToUpAnimation;

	private TranslateAnimation leftToRightAnimation;

	private TranslateAnimation rightToLeftAnimation;
	
	private int liveTitleNum;

	private int mwidth;
	
	// 设置slideView的显示
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				toggleSlide();
				handler.sendEmptyMessageDelayed(1, 7500);
				break;
			case 1:
				reverseSlide();
				if (liveTitleNum!=0) {
				    handler.sendEmptyMessageDelayed(0, 15000*liveTitleNum-7500);
                }
				break;
			default:
				break;
			}
		}
	};

	public SlideView(Context context) {
		super(context);
		init(context, null, 0);
	}

	public SlideView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs, 0);
	}

	public SlideView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		init(context, attrs, defStyle);
	}

	private void init(Context context, AttributeSet attrs, int defStyle) {
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.SlideView, defStyle, 0);

		int backViewId = a
				.getResourceId(R.styleable.SlideView_slideBackView, 0);
		int foreViewId = a
				.getResourceId(R.styleable.SlideView_slideForeView, 0);
		int duration = a.getInt(R.styleable.SlideView_slideDuration,
				sDefaultDuration);
		int interpolatorResId = a.getResourceId(
				R.styleable.SlideView_slideInterpolator, 0);
		interpolator = interpolatorResId > 0 ? AnimationUtils.loadInterpolator(
				context, interpolatorResId) : new AccelerateInterpolator();

		if (backViewId == 0) {
			backView = null;
		} else {
			backView = View.inflate(getContext(), backViewId, null);
		}
		if (foreViewId == 0) {
			foreView = null;
		} else {
			foreView = View.inflate(getContext(), foreViewId, null);
		}

		// 设置下滑动画
		upToDownAnimation = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT,
				0f, Animation.RELATIVE_TO_PARENT, 0f,
				Animation.RELATIVE_TO_PARENT, 1f);
		upToDownAnimation.setRepeatCount(0);
		upToDownAnimation.setRepeatMode(Animation.RESTART);
		upToDownAnimation.setFillAfter(true);
		upToDownAnimation.setAnimationListener(this);
		upToDownAnimation.setInterpolator(interpolator);
		upToDownAnimation.setDuration(duration);
		mAnimation = upToDownAnimation;
		// 设置反向动画
		downToUpAnimation = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT,
				0f, Animation.RELATIVE_TO_PARENT, 1f,
				Animation.RELATIVE_TO_PARENT, 0f);
		downToUpAnimation.setRepeatCount(0);
		downToUpAnimation.setRepeatMode(Animation.RESTART);
		downToUpAnimation.setFillAfter(true);
		downToUpAnimation.setAnimationListener(this);
		downToUpAnimation.setInterpolator(interpolator);
		downToUpAnimation.setDuration(duration);
		mReverseAnimation = downToUpAnimation;

		// 设置默认不可点击,避免抢了父控件的点击事件,由父控件来控制动画的播放
		// setOnClickListener(this);

		if (foreView != null) {
			setForeView(foreView);
		}
		if (backView != null) {
			setBackView(backView);
		}
		mIsSliding = false;

		a.recycle();
	}

	/**
	 * 设置背面的View
	 * @param backView
	 */
	public void setBackView(View backView) {
		this.backView = backView;
		int w = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		foreView.measure(w, h);
		mheight = foreView.getMeasuredHeight();
		mwidth = foreView.getMeasuredWidth();

		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(mwidth,
				mheight);
		lp.setMargins(0, -mheight, 0, 0);
		lp.gravity = 17;// 居中
		addView(backView, 0, lp);
	}
	
	/**
	 * 设置背面的View
	 * @param backView
	 * @param delay	设置动画多少毫秒后开始播放
	 */
	public void setBackView(View backView,int delay) {
		this.backView = backView;
		int w = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		foreView.measure(w, h);
		mheight = foreView.getMeasuredHeight();
		mwidth = foreView.getMeasuredWidth();

		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(mwidth,
				mheight);
		lp.setMargins(0, -mheight, 0, 0);
		lp.gravity = 17;// 居中
		addView(backView, 0, lp);
		
		//开启动画
		handler.sendEmptyMessageDelayed(0, delay);
	}

	/**
	 * 设置当前的View
	 * 
	 * @param view
	 */
	public void setForeView(View foreView) {
		this.foreView = foreView;
		addView(foreView);
	}

	public View getForeView(){
		return foreView;
	}
	
	public View getBackView(){
		return backView;
	}
	
	public TranslateAnimation getSlideAnimation() {
		return mAnimation;
	}

	public TranslateAnimation getReverseAnimation() {
		return mReverseAnimation;
	}

	public void setInterpolator(Interpolator interpolator) {
		mAnimation.setInterpolator(interpolator);
		mReverseAnimation.setInterpolator(interpolator);
	}

	public void setDuration(int duration) {
		mAnimation.setDuration(duration);
		mReverseAnimation.setDuration(duration);
	}

	public boolean isSlided() {
		return mIsSlided;
	}

	public boolean isSliding() {
		return mIsSliding;
	}

	public boolean isAnimated() {
		return mIsDefaultAnimated;
	}

	public void setAnimated(boolean animated) {
		mIsDefaultAnimated = animated;
	}

	public void toggleSlide() {
		if (backView == null) {
			return;
		}
		if (!mIsSlided) {
			if (!mIsSliding) {
				backView.startAnimation(mAnimation);
				foreView.startAnimation(mAnimation);
				mIsSlided = !mIsSlided;
			}
		}
	}

	public void reverseSlide() {
		if (backView == null) {
			return;
		}
		if (mIsSlided) {
			if (!mIsSliding) {
				backView.startAnimation(mReverseAnimation);
				foreView.startAnimation(mReverseAnimation);
				mIsSlided = !mIsSlided;
			}
		}
	}

	public void setOnSlideListener(OnSlideListener listener) {
		mListener = listener;
	}

	@Override
	public void onClick(View v) {
		if (mIsSlided) {
			reverseSlide();
		} else {
			toggleSlide();
		}
		if (mListener != null) {
			mListener.onClick(this);
		}
	}

	@Override
	public void onAnimationStart(Animation animation) {
		if (mListener != null) {
			mListener.onSlideStart(this);
		}
		mIsSliding = true;
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		if (mListener != null) {
			mListener.onSlideEnd(this);
		}
		mIsSliding = false;
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
	}

	/**
	 * 得到快速设置的内部类
	 */
	public QuickSettings getQuickSettings() {
		return new QuickSettings();
	}

	/**
	 * 自定义设置的集合类
	 * 
	 * @author Administrator
	 */
	public class QuickSettings {

		/**
		 * 所有按默认设置
		 */
		public void setAllDefault() {
			setInterpolator(0);
			SlideView.this.setDuration(500);
			setDirection(0);
		}

		/**
		 * 设置动画的插入器 
		 * @param type	0 1 2 3 4 5 减速 加速 先减速再加速 回弹 后翻一点 先前翻一点再后翻
		 * @param accelerate	设置加速度,默认为1.0f
		 */
		public void setInterpolator(int type,float accelerate) {
			switch (type) {
			case 0:
				SlideView.this.setInterpolator(new DecelerateInterpolator(accelerate));
				break;
			case 1:
				SlideView.this.setInterpolator(new AccelerateInterpolator(accelerate));
				break;
			case 2:
				SlideView.this
						.setInterpolator(new AccelerateDecelerateInterpolator());
				break;
			case 3:
				SlideView.this.setInterpolator(new BounceInterpolator());
				break;
			case 4:
				SlideView.this.setInterpolator(new OvershootInterpolator(accelerate));
				break;
			case 5:
				SlideView.this
						.setInterpolator(new AnticipateOvershootInterpolator(accelerate));
				break;
			default:
				SlideView.this.setInterpolator(new DecelerateInterpolator(accelerate));
				break;
			}
		}

		/**
		 * 设置动画的插入器 
		 * @param type	0 1 2 3 4 5 减速 加速 先减速再加速 回弹 后翻一点 先前翻一点再后翻
		 */
		public void setInterpolator(int type) {
			switch (type) {
			case 0:
				SlideView.this.setInterpolator(new DecelerateInterpolator());
				break;
			case 1:
				SlideView.this.setInterpolator(new AccelerateInterpolator());
				break;
			case 2:
				SlideView.this
						.setInterpolator(new AccelerateDecelerateInterpolator());
				break;
			case 3:
				SlideView.this.setInterpolator(new BounceInterpolator());
				break;
			case 4:
				SlideView.this.setInterpolator(new OvershootInterpolator());
				break;
			case 5:
				SlideView.this
						.setInterpolator(new AnticipateOvershootInterpolator());
				break;
			default:
				SlideView.this.setInterpolator(new DecelerateInterpolator());
				break;
			}
		}
		
		/**
		 * 动画滑动的事件监听
		 * 
		 * @param onFlipListener
		 */
		public void setOnSlideListener(OnSlideListener onFlipListener) {
			SlideView.this.setOnSlideListener(onFlipListener);
		}

		/**
		 * 设置滑动时长
		 * 
		 * @param duration
		 */
		public void setDuration(int duration) {
			SlideView.this.setDuration(duration);
		}

		/**
		 * 设置隐藏的view
		 * 
		 * @param backView
		 */
		public void setBackView(View backView) {
			SlideView.this.setBackView(backView);
		}

		/**
		 * 设置显示的view
		 * 
		 * @param foreView
		 */
		public void setForeView(View foreView) {
			SlideView.this.setForeView(foreView);
		}

		/**
		 * 方法还没有完善,暂不公开 设置滑动方向 0 1 2 3 从上到下 从下到上 从左到右 从右到左
		 * 如果要使用上下方向,那么layout_height需要设定具体的高度值 如果要使用左右方向,那么layout_width要设定具体的宽度值
		 * 
		 * @param direction
		 */
		public void setDirection(int direction) {
			if (backView == null) {
				return;
			}
			LinearLayout.LayoutParams layoutParams;
			switch (direction) {
			case 0:
				upToDownAnimation = new TranslateAnimation(
						Animation.RELATIVE_TO_PARENT, 0f,
						Animation.RELATIVE_TO_PARENT, 0f,
						Animation.RELATIVE_TO_PARENT, 0f,
						Animation.RELATIVE_TO_PARENT, 1f);
				upToDownAnimation.setRepeatCount(mAnimation.getRepeatCount());
				upToDownAnimation.setRepeatMode(mAnimation.getRepeatMode());
				upToDownAnimation.setFillAfter(mAnimation.getFillAfter());
				upToDownAnimation.setAnimationListener(SlideView.this);
				upToDownAnimation.setInterpolator(mAnimation.getInterpolator());
				upToDownAnimation.setDuration(mAnimation.getDuration());

				downToUpAnimation = new TranslateAnimation(
						Animation.RELATIVE_TO_PARENT, 0f,
						Animation.RELATIVE_TO_PARENT, 0f,
						Animation.RELATIVE_TO_PARENT, 1f,
						Animation.RELATIVE_TO_PARENT, 0f);
				downToUpAnimation.setRepeatCount(mAnimation.getRepeatCount());
				downToUpAnimation.setRepeatMode(mAnimation.getRepeatMode());
				downToUpAnimation.setFillAfter(mAnimation.getFillAfter());
				downToUpAnimation.setAnimationListener(SlideView.this);
				downToUpAnimation.setInterpolator(mAnimation.getInterpolator());
				downToUpAnimation.setDuration(mAnimation.getDuration());

				mAnimation = upToDownAnimation;
				mReverseAnimation = downToUpAnimation;

				layoutParams = (LayoutParams) backView.getLayoutParams();
				layoutParams.setMargins(0, -mheight, 0, 0);
				backView.setLayoutParams(layoutParams);
				break;
			case 1:
				upToDownAnimation = new TranslateAnimation(
						Animation.RELATIVE_TO_PARENT, 0f,
						Animation.RELATIVE_TO_PARENT, 0f,
						Animation.RELATIVE_TO_PARENT, -1f,
						Animation.RELATIVE_TO_PARENT, 0f);
				upToDownAnimation.setRepeatCount(mAnimation.getRepeatCount());
				upToDownAnimation.setRepeatMode(mAnimation.getRepeatMode());
				upToDownAnimation.setFillAfter(mAnimation.getFillAfter());
				upToDownAnimation.setAnimationListener(SlideView.this);
				upToDownAnimation.setInterpolator(mAnimation.getInterpolator());
				upToDownAnimation.setDuration(mAnimation.getDuration());

				downToUpAnimation = new TranslateAnimation(
						Animation.RELATIVE_TO_PARENT, 0f,
						Animation.RELATIVE_TO_PARENT, 0f,
						Animation.RELATIVE_TO_PARENT, 0f,
						Animation.RELATIVE_TO_PARENT, -1f);
				downToUpAnimation.setRepeatCount(mAnimation.getRepeatCount());
				downToUpAnimation.setRepeatMode(mAnimation.getRepeatMode());
				downToUpAnimation.setFillAfter(mAnimation.getFillAfter());
				downToUpAnimation.setAnimationListener(SlideView.this);
				downToUpAnimation.setInterpolator(mAnimation.getInterpolator());
				downToUpAnimation.setDuration(mAnimation.getDuration());

				mAnimation = downToUpAnimation;
				mReverseAnimation = upToDownAnimation;

				layoutParams = (LayoutParams) backView.getLayoutParams();
				layoutParams.setMargins(0, mheight, 0, -mheight * 2);// 这里的设定很有意思哦
				backView.setLayoutParams(layoutParams);
				break;
			case 2:
				leftToRightAnimation = new TranslateAnimation(
						Animation.RELATIVE_TO_PARENT, 0f,
						Animation.RELATIVE_TO_PARENT, 1f,
						Animation.RELATIVE_TO_PARENT, 0f,
						Animation.RELATIVE_TO_PARENT, 0f);
				leftToRightAnimation
						.setRepeatCount(mAnimation.getRepeatCount());
				leftToRightAnimation.setRepeatMode(mAnimation.getRepeatMode());
				leftToRightAnimation.setFillAfter(mAnimation.getFillAfter());
				leftToRightAnimation.setAnimationListener(SlideView.this);
				leftToRightAnimation.setInterpolator(mAnimation
						.getInterpolator());
				leftToRightAnimation.setDuration(mAnimation.getDuration());

				rightToLeftAnimation = new TranslateAnimation(
						Animation.RELATIVE_TO_PARENT, 1f,
						Animation.RELATIVE_TO_PARENT, 0f,
						Animation.RELATIVE_TO_PARENT, 0f,
						Animation.RELATIVE_TO_PARENT, 0f);
				rightToLeftAnimation
						.setRepeatCount(mAnimation.getRepeatCount());
				rightToLeftAnimation.setRepeatMode(mAnimation.getRepeatMode());
				rightToLeftAnimation.setFillAfter(mAnimation.getFillAfter());
				rightToLeftAnimation.setAnimationListener(SlideView.this);
				rightToLeftAnimation.setInterpolator(mAnimation
						.getInterpolator());
				rightToLeftAnimation.setDuration(mAnimation.getDuration());

				mAnimation = leftToRightAnimation;
				mReverseAnimation = rightToLeftAnimation;

				setOrientation(LinearLayout.HORIZONTAL);
				layoutParams = (LayoutParams) backView.getLayoutParams();
				layoutParams.setMargins(-mwidth, 0, 0, 0);

				backView.setLayoutParams(layoutParams);
				break;
			case 3:
				leftToRightAnimation = new TranslateAnimation(
						Animation.RELATIVE_TO_PARENT, 0f,
						Animation.RELATIVE_TO_PARENT, -1f,
						Animation.RELATIVE_TO_PARENT, 0f,
						Animation.RELATIVE_TO_PARENT, 0f);
				leftToRightAnimation
						.setRepeatCount(mAnimation.getRepeatCount());
				leftToRightAnimation.setRepeatMode(mAnimation.getRepeatMode());
				leftToRightAnimation.setFillAfter(mAnimation.getFillAfter());
				leftToRightAnimation.setAnimationListener(SlideView.this);
				leftToRightAnimation.setInterpolator(mAnimation
						.getInterpolator());
				leftToRightAnimation.setDuration(mAnimation.getDuration());

				rightToLeftAnimation = new TranslateAnimation(
						Animation.RELATIVE_TO_PARENT, -1f,
						Animation.RELATIVE_TO_PARENT, 0f,
						Animation.RELATIVE_TO_PARENT, 0f,
						Animation.RELATIVE_TO_PARENT, 0f);
				rightToLeftAnimation
						.setRepeatCount(mAnimation.getRepeatCount());
				rightToLeftAnimation.setRepeatMode(mAnimation.getRepeatMode());
				rightToLeftAnimation.setFillAfter(mAnimation.getFillAfter());
				rightToLeftAnimation.setAnimationListener(SlideView.this);
				rightToLeftAnimation.setInterpolator(mAnimation
						.getInterpolator());
				rightToLeftAnimation.setDuration(mAnimation.getDuration());

				mAnimation = leftToRightAnimation;
				mReverseAnimation = rightToLeftAnimation;

				setOrientation(LinearLayout.HORIZONTAL);
				layoutParams = (LayoutParams) backView.getLayoutParams();
				layoutParams.setMargins(mwidth, 0, -mwidth * 2, 0);

				backView.setLayoutParams(layoutParams);
				break;
			}
		}
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

    /**
     * @return the liveTitleNum
     */
    public int getLiveTitleNum() {
        return liveTitleNum;
    }

    /**
     * @param liveTitleNum the liveTitleNum to set
     */
    public void setLiveTitleNum(int liveTitleNum) {
        this.liveTitleNum = liveTitleNum;
    }
}
