package so.contacts.hub.payment;

import android.app.Activity;
import android.os.Bundle;

/**
 * 
 * 支付结果页的代理. 在支付结果页{@link PaymentResultActivity}的每个生命周期中会调用到. 
 * 调用顺序是先调用系统的生命周期方法, 再调用delegator的方法.
 * @author Steve Xu 徐远同
 *
 */
public interface ResultActivityDelegator {
    public void onCreate(Activity act, Bundle savedInstanceState);
    public void onStart(Activity act);
    public void onDestroy(Activity act);
    public void onStop(Activity act);
    public void onPause(Activity act);
    public void onResume(Activity act);
}
