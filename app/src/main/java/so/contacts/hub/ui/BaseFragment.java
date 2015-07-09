
package so.contacts.hub.ui;

import so.contacts.hub.ad.AdViewCreator;
import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment {

    public void onResume() {
        super.onResume();
        AdViewCreator.getCreator().handleAdView(this);
    }

    public void onDestroy(){
        super.onDestroy();
        AdViewCreator.getCreator().onDestory(this);
    }
    
    public Integer getAdId() {
        return null;
    }
}
