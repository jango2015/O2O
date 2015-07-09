package so.contacts.hub.msgcenter;

import so.contacts.hub.ui.yellowpage.bean.ChargeTrafficMessageBusiness;

import so.contacts.hub.ui.yellowpage.bean.ChargeTelephoneMessageBusiness;

import android.content.Context;
import so.contacts.hub.lottery.LotteryMessageBusiness;
import so.contacts.hub.thirdparty.cinema.bussiness.MovieMessageBussiness;
import so.contacts.hub.thirdparty.express.ExpressMessageBusiness;
import so.contacts.hub.thirdparty.tongcheng.message.HotelMessageBusiness;
import so.contacts.hub.trafficoffence.TrafficOffenceMsgBusiness;
import so.contacts.hub.train.message.TrainMsgBusiness;

/**
 * 消息中心工厂类，负责创建消息中心实例 
 * @author putao_lhq
 */
public class PTMessageCenterFactory {

    public static final String TAG = "MessageCenter";
    
    public static IPTMessageCenter getPTMessageCenter() {
        return PTMessageCenter.getInstance();
    }
    
    public static PTOrderCenter getPTOrderCenter() {
        return PTOrderCenter.getInstance();
    }
    
    /**
     * add by zj 2014-12-22 19:43:17
     * 注册提醒中心业务
     * @param context
     */
    public static void registBussness(Context context){
        LotteryMessageBusiness.getInstance(context);
        HotelMessageBusiness.getInstance(context);
        ExpressMessageBusiness.getInstance(context);
        new MovieMessageBussiness(context);
        TrafficOffenceMsgBusiness.getInstance(context);
        new ChargeTelephoneMessageBusiness(context);
        new ChargeTrafficMessageBusiness(context);
        TrainMsgBusiness.getInstance(context);
    }
    
}
