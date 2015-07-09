
package so.contacts.hub.msgcenter.bean;

import java.io.Serializable;

/**
 * 用于订单中心显示数据填充的类
 * 
 * @author zj 2015-01-05 15:43:21
 */
public class PTOrderItemBean implements Serializable {

    private static final long serialVersionUID = 1L;

    public String title; // 标题,例:充值50元 18588469321

    public long time; // 消息更新时间

    public double price; // 价格

    public String status; // 服务状态 例:已付款
    
    public boolean isImportant;//消息是否需要突出显示

    @Override
    public String toString() {
        return "PTOrderItemBean [title=" + title + ", time=" + time + ", price=" + price
                + ", status=" + status + "]";
    }

}
