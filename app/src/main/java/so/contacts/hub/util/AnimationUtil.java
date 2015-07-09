package so.contacts.hub.util;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;

public class AnimationUtil {

	public static void translateAnim(View view, boolean isVertical, int startPos, int endPos, int duration, final IAnimListener iAnimListener){
		if( view == null ){
			return;
		}
		view.clearAnimation();
		ObjectAnimator objAnim = null;
		if( isVertical ){
			objAnim = ObjectAnimator.ofFloat(view, "translationY", startPos, endPos);
		}else{
			objAnim = ObjectAnimator.ofFloat(view, "translationX", startPos, endPos);
		}
		objAnim.setDuration(duration);
		objAnim.setInterpolator(new AccelerateInterpolator());
		objAnim.addListener(new AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator arg0) {
				// TODO Auto-generated method stub
				if( iAnimListener != null ){
					iAnimListener.onAnimationStart();
				}
			}
			
			@Override
			public void onAnimationRepeat(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animator arg0) {
				// TODO Auto-generated method stub
				if( iAnimListener != null ){
					iAnimListener.onAnimationEnd();
				}
			}
			
			@Override
			public void onAnimationCancel(Animator arg0) {
				// TODO Auto-generated method stub
			}
		});
		objAnim.start();
	}
	
	public static Animation translateAnim(boolean isVertical, int startPos, int endPos, int duration, final IAnimListener iAnimListener){
		TranslateAnimation transAnim = null;
		if( isVertical ){
			transAnim = new TranslateAnimation(0, 0, startPos, endPos);
		}else{
			transAnim = new TranslateAnimation(startPos, endPos, 0, 0);
		}
    	transAnim.setFillAfter(false);
    	transAnim.setDuration(duration);
    	transAnim.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub
				if( iAnimListener != null ){
					iAnimListener.onAnimationStart();
				}
			}
			
			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation arg0) {
				// TODO Auto-generated method stub
				if( iAnimListener != null ){
					iAnimListener.onAnimationEnd();
				}
			}
		});
    	return transAnim;
	}
	
	public interface IAnimListener{
		void onAnimationStart();
		void onAnimationEnd();
	}

}
