package so.contacts.hub.msgcenter;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import so.contacts.hub.msgcenter.MsgCenterConfig.Product;
import so.contacts.hub.msgcenter.bean.OrderNumber;

import java.util.Random;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.msgcenter.bean.PTMessageBean;
import so.contacts.hub.msgcenter.bean.PTOrderBean;
import so.contacts.hub.msgcenter.ui.SettingView;
import so.contacts.hub.msgcenter.ui.SwitchButton;
import so.contacts.hub.payment.data.ResultCode;
import so.contacts.hub.util.CalendarUtil;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.UMengEventIds;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.yulong.android.contacts.discover.R;

/**
 * 该类主要为抽象出一些业务上通用部分的实现方法<br>
 *  需要提醒功能的业务需继承此类
 * @author putao_lhq
 *
 */
public abstract class AbstractMessageBussiness implements IMessageBusiness {
    protected String umengInsertDataEventId="";
    protected int logoId=R.drawable.putao_icon_order_jp;//模拟演示数据
    protected int smallLogoId=R.drawable.putao_icon_order_dy_s;//模拟演示数据
    protected int title=R.string.lottery;
    protected int productType=0;// 服务类别 
    protected String TAG=getClass().getSimpleName();
    protected int notificationLogoId = R.drawable.putao_ic_launcher;
    public AbstractMessageBussiness(Context  context) {
        PTMessageCenter.getInstance().register(this);
    }
    
    public int getProductType() {
        return productType;
    }
   
    public boolean checkOrder(PTOrderBean bean) {
        if(null!=bean&&productType==bean.getProduct_type())
            return true;
        return false;
    }
    
    @Override
    public boolean checkMsg(PTMessageBean bean) {
        if(null!=bean&&productType==bean.getProductType())
            return true;
        return false;
    }
    
    private class ViewHolder{
        ImageView logo;
        TextView subject;
        TextView digest;
        TextView time;
    }
    
    @Override
    public View getNotifyView(PTMessageBean bean, View convertView) {
        View view=null;
        ViewHolder holder=null;
        if (convertView==null) {
            view=View.inflate(ContactsApp.getContext(), R.layout.putao_message_center_list_item, null);
            holder=new ViewHolder();
            holder.logo=(ImageView)view.findViewById(R.id.logo);
            holder.subject=(TextView)view.findViewById(R.id.subject);
            holder.digest=(TextView)view.findViewById(R.id.digest);
            holder.time=(TextView)view.findViewById(R.id.time);
            view.setTag(holder);
        }else {
            view=convertView;
            holder=(ViewHolder)view.getTag();
        }
        
        holder.subject.setText(bean.getSubject());
        holder.digest.setText(bean.getDigest());
        holder.logo.setImageResource(smallLogoId);
        holder.time.setText(CalendarUtil.formatTimeForMessageCenter(bean.getTime()));

        IPTMessageCenter messageCenter = PTMessageCenterFactory.getPTMessageCenter();       
        AbstractMessageBussiness business=messageCenter.getService(bean);
        if (business.isNotifyExpire(bean)) {
            holder.time.setTextColor(ContactsApp.getContext().getResources().getColor(R.color.putao_text_color_second));
        }else {
            holder.time.setTextColor(ContactsApp.getContext().getResources().getColor(R.color.putao_calendar_selected_color));
        }
        
        return view;
    }

    @Override
    public View getSettingView(View convertView,boolean isExpanded,Activity context) {
        SettingView view = null;
        SettingHolder holder=null;
        if (convertView == null) {
            View foreView = View.inflate(ContactsApp.getContext(), R.layout.putao_message_center_settings_list_item, null);
            
            holder=new SettingHolder();
            holder.logo = (ImageView)foreView.findViewById(R.id.logo);
            holder.title = (TextView)foreView.findViewById(R.id.title);
            holder.set_switch = (SwitchButton)foreView.findViewById(R.id.set_switch);
            holder.divider = foreView.findViewById(R.id.divider);

            view = new SettingView(ContactsApp.getContext(), foreView,
                    getConfigView(context), isExpanded);
            view.setTag(holder);
        } else {
            view=(SettingView)convertView;
            view.setHideView(getConfigView(context));
            view.setExpanded(isExpanded);
            holder=(SettingHolder)view.getTag();
        }

        if (isExpanded) {
            holder.divider.findViewById(R.id.divider).setVisibility(View.INVISIBLE);
        }else {
            holder.divider.findViewById(R.id.divider).setVisibility(View.VISIBLE);
        }
        
        holder.logo.setImageResource(logoId);
        holder.title.setText(title);
        holder.set_switch.setOnCheckedChangeListener(null);
        holder.set_switch.setChecked(getEnable());
        holder.set_switch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setEnable(isChecked);
//                System.out.println(isChecked);
                
                if(isChecked){
                    MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(), UMengEventIds.DISCOVER_YELLOWPAGE_MY_MSG_CENTER_SETTING_EVENT_OPEN);  
                }else{
                    MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(), UMengEventIds.DISCOVER_YELLOWPAGE_MY_MSG_CENTER_SETTING_EVENT_CLOSE);
                }
            }
        });
        return view;
    }
    
    private class SettingHolder{
        ImageView logo;
        TextView title;
        SwitchButton set_switch;
        View divider;
    }
    
     /**
      * add by zj 2014-12-18 18:10:21
      * 发送通知
      * @param notification 需要发送的notify,自己设置好标题icon等
      * @param intent 需要跳转的页面
      */
    protected void sendNotification(Notification notification, Intent intent) {
        /**
         * add code by putao_lhq
         * 添加处理逻辑，判断业务是否开启提醒
         * @start
         */
        if (!getEnable()) {
            return;
        }/*@end by putao_lhq*/

        //modify by xcx 2015-01-14 start 优化统计埋点方式
        NotificationManager mNotificationManager = (NotificationManager)ContactsApp.getContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Intent clickIntent = new Intent(ContactsApp.getContext(),
                NotifycationClickIntentService.class); // 点击 Intent
        clickIntent.putExtra("realIntent", intent);
        
        PendingIntent contentIntent = PendingIntent.getService(ContactsApp.getContext(),
                new Random().nextInt(), clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        
        notification.contentIntent = contentIntent;
        notification.flags = Notification.FLAG_AUTO_CANCEL;// 点击后自动消失
        if (PTMessageCenterSettings.getSoundEnable()&&PTMessageCenterSettings.getVibrateEnable()) {
            notification.defaults = Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE;
        }else if (PTMessageCenterSettings.getSoundEnable()){
            notification.defaults=Notification.DEFAULT_SOUND;
        }else if (PTMessageCenterSettings.getVibrateEnable()){
            notification.defaults=Notification.DEFAULT_VIBRATE;
        }else {
            notification.sound=null;
            notification.vibrate=null;
        }
        
        // 用mNotificationManager的notify方法通知用户生成标题栏消息通知
        mNotificationManager.notify(new Random().nextInt(), notification);
        LogUtil.d(TAG,notification.toString());
        MobclickAgentUtil
        .onEvent(
                ContactsApp.getContext(),
                UMengEventIds.DISCOVER_YELLOWPAGE_MY_MSG_CENTER_NOTIFICATION);
       //modify by xcx 2015-01-14 end 优化统计埋点方式
    }

    // add 2014-12-31 xcx start 统计埋点
    @Override
    public void addUMengEvent() {
        // TODO Auto-generated method stub

        MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
                umengInsertDataEventId);
        MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
                UMengEventIds.DISCOVER_YELLOWPAGE_MY_MSG_CENTER_ITEM_NUM);
    }
    // add 2014-12-31 xcx end 统计埋点
    @Override
    public void click(PTMessageBean bean, Activity context) {
        bean.setStatus(1);
        PTMessageCenterFactory.getPTMessageCenter().storeMessage(bean);
    }
    
    @Override
    public boolean isNotifyExpire(PTMessageBean bean) {
        switch (bean.getStatus()) {
            case 0:
                return false;
            case 1:
                return true;
            default:
                break;
        }
        
        return false;
    }
    
    @Override
    public boolean isOrderExpire(PTOrderBean order) {
        if(order.getStatus_code() == ResultCode.OrderStatus.Cancel 
                || order.getStatus_code() == ResultCode.OrderStatus.Failed 
                || order.getStatus_code() == ResultCode.OrderStatus.Refunded
                || order.getStatus_code() == ResultCode.OrderStatus.OutOfDate) {
            return true;
        }
               
        return false;
    }
    public static OrderNumber getOrderNumber(PTMessageBean bean) {
        OrderNumber orderNumber = null;
        JSONObject obj;
        try {
            orderNumber = new OrderNumber();
            obj = new JSONObject(bean.getExpand_param());
            String pt_order_no = obj.optString("pt_order_no");
            orderNumber.setPt_order_no(pt_order_no);
            String pt_order_status = obj.optString("pt_order_status");
            if (!TextUtils.isEmpty(pt_order_status)) {
                orderNumber.setPt_order_status(Integer.parseInt(pt_order_status));
            }
            String order_no = obj.optString("order_no");
            orderNumber.setOrder_no(order_no);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return orderNumber;
    }
    
    /**
     * 判断消息是否已读,关系到打点和显示
     * 
     * @param orderBean
     * @return
     */
    public boolean isReaded(PTOrderBean orderBean) {
        return orderBean.getView_status() == 0 ? false : true;
    }

    /**
     * 获取产品类型枚举对象
     * 
     * @return
     */
    public Product getProduct() {
        return Product.getProduct(productType);
    }
}
