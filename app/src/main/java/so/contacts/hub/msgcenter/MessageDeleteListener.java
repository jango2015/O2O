
package so.contacts.hub.msgcenter;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.msgcenter.bean.PTOrderBean;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.UMengEventIds;
import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

public class MessageDeleteListener implements OnClickListener {
    private PTOrderBean orderBean;

    private Activity activity;

    private String orderNo;

    public MessageDeleteListener(PTOrderBean order, Activity activity) {
        orderBean = order;
        this.activity = activity;
    }

    public MessageDeleteListener(String orderNo, Activity activity) {
        this.orderNo = orderNo;
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {/*
        if (orderBean != null && activity != null) {
            orderBean.setView_status(2);
            PTOrderCenter.getInstance().updateOrderData(orderBean);
            // 统计XX提醒卡片删除次数
            MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
                    UMengEventIds.CNT_NOTIFY_CARD_CLOSE_ + orderBean.getProduct().toString());
            activity.finish();
        } else if (!TextUtils.isEmpty(orderNo) && activity != null) {
            PTOrderCenter.getInstance().setOrderDeleted(orderNo);
            PTOrderBean bean = new PTOrderCenter().getOrderByOrderNumber(orderNo);
            if (bean != null) {
                // 统计XX提醒卡片删除次数
                MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
                        UMengEventIds.CNT_NOTIFY_CARD_CLOSE_ + bean.getProduct().toString());
            }
            activity.finish();
        }
    */}

}
