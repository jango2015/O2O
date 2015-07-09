package so.contacts.hub.msgcenter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import so.contacts.hub.msgcenter.bean.OrderNumber;
import so.contacts.hub.msgcenter.bean.PTMessageBean;

/**
 * 消息中心接口
 * @author putao_lhq
 *
 */
public interface IPTMessageCenter {

    /**
     * 业务注册接口
     * @param interfaceMsg
     */
    public void register(AbstractMessageBussiness interfaceMsg);
    
    /**
     * 消息分发接口
     * @param message
     *//*
    public void dispatch(String message);*/
    
    /**
     * 根据action_type来获取相应的service
     * @param msgBean
     * @return {@link AbstractMessageBussiness}
     */
    public AbstractMessageBussiness getService(PTMessageBean msgBean);
    
    /**
     * add by zj 2014-12-18 14:51:52
     * 获取所有服务 
     * @return
     */
    public List<AbstractMessageBussiness> getAllService();
    
    /**
     * 获取最新一条消息
     * @return
     */
    public PTMessageBean getNewestMsg();
    
    /**
     * 加载当前所有消息
     * @return
     */
    public List<PTMessageBean> loadMessage();
    
    public void storeMessage(PTMessageBean msgBean);
    
    /**
     * 加载订单信息
     * @return
     */
//    public List<PTMessageBean> loadOrders();
    
    /**
     * 存储订单信息
     * @param msg_id
     * @param orderId TODO
     * @param order
     */
//    public void storeOrder(int msg_id, String orderId, String order);
    
    public boolean getEnableSound();
    
    public boolean getEnableVibrate();
    
    public void saveMessage(PTMessageBean msgBean); 
    
    public int removeMessage(PTMessageBean msgBean);
    
}
