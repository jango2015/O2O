package so.contacts.hub.ad;

import java.util.List;

import so.contacts.hub.active.ActiveUtils;
import so.contacts.hub.db.YellowPageDB;
import so.contacts.hub.push.bean.PushAdBean;
import so.contacts.hub.ui.BaseActivity;
import so.contacts.hub.ui.BaseFragment;
import so.contacts.hub.util.ContactsAppUtils;
import so.contacts.hub.widget.AdOperatLayout;
import so.contacts.hub.widget.AdOperatLayout.AdLayoutCallback;
import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.yulong.android.contacts.discover.R;

public class AdViewCreator implements AdLayoutCallback {
    private YellowPageDB mDbHelper = null;

    private static volatile AdViewCreator __instance = null;

    private AdViewCreator() {
        mDbHelper = ContactsAppUtils.getInstance().getDatabaseHelper().getYellowPageDBHelper();
    }

    public static AdViewCreator getCreator() {
        if (null == __instance) {
            synchronized (AdViewCreator.class) {
                if (null == __instance) {
                    __instance = new AdViewCreator();
                }
            }
        }

        return __instance;
    }

    public void onDestory(Activity act) {
        if (null == act) {
            return;
        }

        disposeAdView(act, R.id.ad_view_layout_top);
        disposeAdView(act, R.id.ad_view_layout_mid);
        disposeAdView(act, R.id.ad_view_layout_bottom);
    }

    private void disposeAdView(Activity act, int adViewId) {
        View view = act.findViewById(R.id.ad_view_layout_top);
        if (view instanceof AdOperatLayout) {
            AdOperatLayout adView = (AdOperatLayout)view;
            adView.setCallback(null);
            adView.clear();
        }
    }

    public void onDestory(BaseFragment fragment) {
        if (null == fragment) {
            return;
        }

        Activity act = fragment.getActivity();
        if (null != act) {
            onDestory(act);
        }
    }

    public void handleAdView(BaseActivity act) {
        if (null == act) {
            return;
        }
        Integer adId = null;
        Intent intent = act.getIntent();
        if (null != intent) {
            adId = intent.getIntExtra("ad_id", -1);
        }

        if (null == adId || -1 == adId.intValue()) {
            adId = act.getAdId();
        }

        if (null != adId && adId.intValue() > 0) {
            initAdViewImpl(act, adId, null);
        }
    }

    private void initAdViewImpl(Activity act, Integer adId, View view) {
        List<PushAdBean> adList = mDbHelper.queryAdDataById(adId.intValue());
        if (null != adList && !adList.isEmpty()) {
            int idToFind = -1;
            View adLayout = null;
            for (int i = 0, size = adList.size(); i < size; i++) {
                PushAdBean adBean = adList.get(i);
                /**
                 * 显示广告时需要传入pageIndex
                 * modify by zjh 2015-01-23 start
                 */
                int adPageIndex = adBean.getAd_page_index();
                switch (adPageIndex) {
                    case 1:
                        idToFind = R.id.ad_view_layout_top;
                        break;
                    case 2:
                        idToFind = R.id.ad_view_layout_mid;
                        break;
                    case 3:
                        idToFind = R.id.ad_view_layout_bottom;
                        break;
                    default:
                        //注：此处最好不要添加默认如果需要删除广告的，会根据pageIndex进行删除，导致删除不了
//                        idToFind = R.id.ad_view_layout_top;
                        break;
                }
                if (act != null) {
                    adLayout = act.findViewById(idToFind);
                } else if (view != null) {
                    adLayout = view.findViewById(idToFind);
                }
                if (adLayout != null && adLayout instanceof AdOperatLayout) {
                    AdOperatLayout adView = (AdOperatLayout)adLayout;
                    adView.setCallback(this);
                    adView.setAdImg(true, adBean, false, adPageIndex, false);
                }
                /** modify by zjh 2015-01-23 end */
            }
        }
    }

    public void handleAdView(BaseFragment fragment) {
        Activity act = fragment.getActivity();
        if (null == act) {
            return;
        }
        Integer adId = null;
        Intent intent = act.getIntent();
        if (null != intent) {
            adId = intent.getIntExtra("ad_id", -1);
        }

        if (null == adId || -1 == adId.intValue()) {
            adId = fragment.getAdId();
        }

        if (null != adId && adId.intValue() > 0) {
            initAdViewImpl(null, adId, fragment.getView());
        }
    }

    @Override
    public String getReqTailSign() {
        return ActiveUtils.getRequrlOfSignTail();
    }

    @Override
    public void deleteAdBean(int serverCode, int pageIndex) {
        if (mDbHelper != null) {
            mDbHelper.deleteAdData(serverCode, pageIndex);
        }
    }
}

