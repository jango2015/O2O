/**
 * @date	: 
 * @author	:
 * @descrip	:
 */
package so.contacts.hub.remind;

import so.contacts.hub.push.bean.PushRemindBean;


public interface IRemindApp {
    public static final String Action = "so.contacts.hub.gamecenter.remind.IRemindApp";
    
    // 模块的打点类型
    public enum RemindType {
        CommPush,
        GameCenter        
    };
    
    /**
     * 接收到一条Remind信息
     */
    public void remindReceived(PushRemindBean r);
    
    public void bubbleReceived(PushRemindBean r);
        
}
