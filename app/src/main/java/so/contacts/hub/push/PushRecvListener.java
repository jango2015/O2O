package so.contacts.hub.push;

import java.util.List;

import so.contacts.hub.active.bean.ActiveEggBean;
import so.contacts.hub.push.bean.PushAdBean;
import so.contacts.hub.push.bean.PushRemindBean;


public interface PushRecvListener {
    
    /**
     * 接收到打点/气泡类型push数据
     * @param list
     */
    public void remindReceived(List<PushRemindBean> bubbleList);
    
    /**
     * 接收到气泡类型push数据
     * @param list
     */
    public void bubbleReceived(List<PushRemindBean> bubbleList);
    
    /**
     * 接收到广告类型push数据
     * @param list
     */    
    public void adReceived(List<PushAdBean> adList);
    
    
    /**
     * 接收到运营控制删除类型push数据
     * @param operateList
     */
    public void opRemoveReceived(List<PushRemindBean> operateList);
    
    /**
     * 接收到活动埋点push数据
     * @param activeList
     */
    public void activeEggReceived(List<ActiveEggBean> activeList);
}
