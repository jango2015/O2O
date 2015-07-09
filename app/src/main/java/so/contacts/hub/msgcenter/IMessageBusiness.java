package so.contacts.hub.msgcenter;

import so.contacts.hub.msgcenter.bean.PTMessageBean;
import so.contacts.hub.msgcenter.bean.PTOrderBean;
import android.app.Activity;
import android.view.View;

/**
 * 消息中心业务接口
 * @author putao_lhq
 *
 */
public interface IMessageBusiness {
    
    /**
     * 检查该消息是否为本服务相关的
     * @param bean
     * @return
     */
    public boolean checkMsg(PTMessageBean bean);
    
    /**
     * 检查该订单是否有效订单
     * @param bean
     * @return
     */
    public boolean checkOrder(PTOrderBean bean);
    
   
    /**
     * 
     * @param orderContent 对应json数据
     * @param convertView
     * @return
     */
    public View getOrderView(PTOrderBean bean, View convertView);
    
    /**
     * 
     * @param bean
     * @param convertView
     * @return
     */
    public View getNotifyView(PTMessageBean bean, View convertView);

    /**
     * add by zj 2014-12-18 15:17:28
     * 获取设置提醒设置的view
     * @param convertView
     * @param expandPosion
     * @return
     */
    public View getSettingView(View convertView, boolean isExpanded,Activity context);
    
    /**
     * 提醒中心点击事件,跳转到订单详情页
     * @param bean
     * @param context
     */
    public void click(PTMessageBean bean, Activity context);
    
    /**
     * 订单中心点击事件,跳转到订单详情页
     * @param bean
     * @param context
     */
    public void click(PTOrderBean bean, Activity context);
    
    /**
     * 解析消息
     * @param message TODO
     */
    public void handleBusiness(PTMessageBean message);
   
    /**
     * 该消息是否过期
     * @param bean
     * @return
     */
    public boolean isNotifyExpire(PTMessageBean bean);
    
    /**
     * 该订单是否已过期
     * @param 
     * @return
     */
    public boolean isOrderExpire(PTOrderBean order);
    
    /**
     * 各业务开关控制
     * @param enable
     */
    public void setEnable(boolean enable);
    
    /**
     * 获取各业务是否需要提醒
     * @return
     */
    public boolean  getEnable();
    /**
     * 业务设置具体信息，例如违章，需要设置车辆信息，
     * 如果不需要设置特别信息，只返回提示内容，如果需要设置，则返回
     * 相关设置view，如包含设置车辆信息button的view
     * @return
     */
    public View getConfigView(Activity context);
    
    /**
     * 统计埋点
     * @return
     */
    public void addUMengEvent();
}
