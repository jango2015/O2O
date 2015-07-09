
package so.contacts.hub.payment;

import android.app.Activity;
import android.os.Bundle;

/**
 * 骨架类专给子类继承用. 主要为开发者提供便利方式实现需要的方法.
 * @author Steve Xu 徐远同
 *
 */
public class SkeletonResultDelegator implements ResultActivityDelegator {
    @Override
    public void onCreate(Activity act, Bundle savedInstanceState) {
    }

    @Override
    public void onStart(Activity act) {
    }

    @Override
    public void onDestroy(Activity act) {
    }

    @Override
    public void onStop(Activity act) {
    }

    @Override
    public void onPause(Activity act) {
    }

    @Override
    public void onResume(Activity act) {
    }
}
