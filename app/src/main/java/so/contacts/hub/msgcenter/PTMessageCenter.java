
package so.contacts.hub.msgcenter;

import java.util.ArrayList;
import java.util.List;

import so.contacts.hub.account.IAccChangeListener;
import so.contacts.hub.core.Config;
import so.contacts.hub.msgcenter.MessageCenterDB.MessageCenterTable;
import so.contacts.hub.msgcenter.bean.PTMessageBean;
import so.contacts.hub.util.LogUtil;

import com.google.gson.JsonSyntaxException;

/**
 * 消息统一管理中心，接收后台push消息
 * 
 * @author putao_lhq
 */
public class PTMessageCenter implements IPTMessageCenter, IAccChangeListener{

    private static final String TAG = "PTMessageCenter";

    private List<AbstractMessageBussiness> mMsgList = new ArrayList<AbstractMessageBussiness>();

    private static PTMessageCenter sInstance;

    private PTMessageBean newestMsg = null;

    public static synchronized PTMessageCenter getInstance() {
        if (sInstance == null) {
            sInstance = new PTMessageCenter();
        }
        return sInstance;
    }

    @Override
    public synchronized void register(AbstractMessageBussiness interfaceMsg) {
        if (mMsgList.contains(interfaceMsg)) {
            LogUtil.d(TAG, "the interface " + interfaceMsg + " ,has register");
            return;
        }
        mMsgList.add((AbstractMessageBussiness)interfaceMsg);
    }

    /**
     * 消息接收接口
     * 
     * @param message
     */
    public synchronized void receive(PTMessageBean msgBean) {
        if (msgBean != null) {
            LogUtil.d(TAG, msgBean.toString());
            IMessageBusiness msg = getService(msgBean);
            if (msg != null && msg.getEnable()) {
                saveMessage(msgBean);
                dispatch(msgBean);
                setNewestMsg(msgBean);
            }
        }
    }

    /**
     * 消息接收接口
     * 
     * @param message
     */
    public synchronized void receive(String jsonMessage) {
        PTMessageBean msgBean = null;
        try {
            msgBean = Config.mGson.fromJson(jsonMessage, PTMessageBean.class);
        } catch (JsonSyntaxException e) {
            LogUtil.e(TAG, e.getMessage());
            e.printStackTrace();
        }

        receive(msgBean);
    }

    /**
     * 保存接受到的消息到消息数据库
     * 
     * @param message
     */
    public void saveMessage(PTMessageBean msgBean) {
        if(Config.getDatabaseHelper().getMessageCenterDB().insertMessage(msgBean)){
            IMessageBusiness msg = getService(msgBean);
            if(null!=msg){
                msg.addUMengEvent();
            }
        }
    }

    /**
     * 分发消息到注册的各服务
     * 
     * @param message
     */
    private void dispatch(PTMessageBean msgBean) {
        if (msgBean == null) {
            return;
        }
        IMessageBusiness msg = getService(msgBean);
        if (msg != null) {
            msg.handleBusiness(msgBean);
        }
    }

    @Override
    public AbstractMessageBussiness getService(PTMessageBean msgBean) {
        for (int i = 0; i < mMsgList.size(); i++) {
            AbstractMessageBussiness msg = mMsgList.get(i);
            if (msg != null && msg.checkMsg(msgBean)) {
                return msg;
            }
        }
        return null;
    }

    @Override
    public PTMessageBean getNewestMsg() {
        // 获取最新消息方法
        return newestMsg;
    }

    private void setNewestMsg(PTMessageBean msgBean) {
        newestMsg = msgBean;
    }

    @Override
    public List<PTMessageBean> loadMessage() {
        return Config.getDatabaseHelper().getMessageCenterDB().queryMessages();
    }

    /**
     * add by zj 2015-01-07 21:31:04
     * 根据pt订单号来查询消息,时间倒序,第0条数据为最新
     * @param orderNo
     * @return
     */
    public List<PTMessageBean> queryMessageByOrderNo(String orderNo) {
        return Config.getDatabaseHelper().getMessageCenterDB().queryMessageByOrderNo(orderNo);
    }
    
    @Override
    public boolean getEnableSound() {
        return PTMessageCenterSettings.getSoundEnable();
    }

    @Override
    public boolean getEnableVibrate() {
        return PTMessageCenterSettings.getVibrateEnable();
    }

    @Override
    public List<AbstractMessageBussiness> getAllService() {
        return mMsgList;
    }
    
    public void storeMessage(PTMessageBean msgBean){
        Config.getDatabaseHelper().getMessageCenterDB().insertMessage(msgBean);
    }
	
    @Override
    public int removeMessage(PTMessageBean msgBean) {
        return Config.getDatabaseHelper().getMessageCenterDB().removeMessage(msgBean);
    }
	
    @Override
    public void onLogin() {
        LogUtil.d(TAG, "message center get account login");
        
    }

    @Override
    public void onLogout() {
        LogUtil.d(TAG, "message center get account logout");
        Config.getDatabaseHelper().getMessageCenterDB().clearTable(MessageCenterTable.TABLE_NAME);
    }

    @Override
    public void onChange() {
        LogUtil.d(TAG, "message center get account change");
        Config.getDatabaseHelper().getMessageCenterDB().clearTable(MessageCenterTable.TABLE_NAME);
    }
    
}
