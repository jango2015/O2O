
package so.contacts.hub.ui.yellowpage.bean;


import so.contacts.hub.msgcenter.bean.OrderNumber;

import org.json.JSONException;
import org.json.JSONObject;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.gamecenter.utils.SharedPreferenceUtils;
import so.contacts.hub.msgcenter.AbstractMessageBussiness;
import so.contacts.hub.msgcenter.MsgCenterConfig;
import so.contacts.hub.msgcenter.PTMessageCenterSettings;
import so.contacts.hub.msgcenter.PTOrderCenter;
import so.contacts.hub.msgcenter.bean.PTMessageBean;
import so.contacts.hub.msgcenter.bean.PTOrderBean;
import so.contacts.hub.payment.data.ResultCode;
import so.contacts.hub.ui.yellowpage.YellowPageChargeTrafficOrderActivity;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.UMengEventIds;
import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yulong.android.contacts.discover.R;

public class ChargeTrafficMessageBusiness extends AbstractMessageBussiness {
    private static final int ONE_DAY_TIME=24 * 60 * 60 * 1000;
    private static final int TWO_HOUR_TIME=2 * 60 * 60 * 1000;
    private Context context;

    private final String SHARED_PREFS_KEY_TRAFFIC = "message_traffic";
    private static final int TEXT_COLOR_SECOND=R.color.putao_text_color_second;
    private static final int TEXT_COLOR_IMPORTANCE=R.color.putao_text_color_importance;
    public ChargeTrafficMessageBusiness(Context context) {
        super(context);
        super.productType = MsgCenterConfig.Product.traffic.getProductType();
        super.logoId = R.drawable.putao_icon_order_chf;
        super.smallLogoId = R.drawable.putao_icon_order_hf_s;
        super.title = R.string.putao_charge_tag_title_tiffic;
        this.context = context;
        PTOrderCenter.getInstance().register(this);
        umengInsertDataEventId = UMengEventIds.DISCOVER_YELLOWPAGE_MY_MSG_CENTER_FLOW_ITEM_NUM;
    }

    @Override
    public View getOrderView(PTOrderBean bean, View convertView) {
        TrafficOrderBean order = getTrafficOrderBeenFromMessage(bean);
        if (order != null) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.putao_charge_telephone_order_item,
                        null);
                holder = new ViewHolder();
                holder.icon = (ImageView)convertView.findViewById(R.id.logo);
                holder.title = (TextView)convertView.findViewById(R.id.title);
                holder.subject = (TextView)convertView.findViewById(R.id.telephone_subject);
                holder.phoneNum = (TextView)convertView.findViewById(R.id.telephone_num);
                holder.status = (TextView)convertView.findViewById(R.id.payed);
                holder.price = (TextView)convertView.findViewById(R.id.money);
                holder.operator = (ImageView)convertView.findViewById(R.id.operator_logo);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            holder.icon.setImageResource(R.drawable.icon_btn_id_chazhi);
            holder.title.setText(R.string.putao_charge_traffic_subject);
            holder.subject.setText(context.getResources().getString(R.string.putao_charge_traffic_order_content, order.getContent(),order.getOrder_title()));
            holder.phoneNum.setText(order.getMobilenum());
            String result = String.format("%.2f", order.getProd_price() / 100f);
            holder.price.setText(context.getResources().getString(
                    R.string.putao_order_item_showmoney, result));
            if (isOrderOutDate(bean)) {
                bean.setStatus_code(ResultCode.OrderStatus.OutOfDate);
            }
            holder.status.setText(ChargeOrderStatus.getStatusStr(bean));
            if(!TextUtils.isEmpty(order.getContent())){
                if(order.getContent().contains("电信")){
                    holder.operator.setImageResource(R.drawable.icon_btn_id_huafei_a);
                }else if(order.getContent().contains("移动")){
                    holder.operator.setImageResource(R.drawable.icon_btn_id_huafei_b);
                }else{
                    holder.operator.setImageResource(R.drawable.icon_btn_id_huafei_c);
                }
            }
            if(ResultCode.OrderStatus.WaitForPayment==bean.getStatus_code()){
                holder.status.setTextColor(context.getResources().getColor(TEXT_COLOR_IMPORTANCE));
            }else{
                holder.status.setTextColor(context.getResources().getColor(TEXT_COLOR_SECOND));
            }
        } else {
            /*
             * modify by putao_lhq @start
             * 业务数据无效时应返回空
             * old code:
            convertView = View.inflate(context, R.layout.putao_charge_telephone_order_item, null);
            convertView.setVisibility(View.GONE);*/
            return null;/*@end by putao_lhq*/
        }
        return convertView;
    }

    @Override
    public void click(PTMessageBean bean, Activity context) {
        super.click(bean, context);
        if (bean == null) {
            return;
        }
        String expand = bean.getExpand_param();
        if (expand == null) {
            return;
        }
        JSONObject obj = null;
        String pt_order_no = null;
        try {
            obj = new JSONObject(bean.getExpand_param());
            pt_order_no = obj.getString("pt_order_no");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        onClick(pt_order_no, context,false);
        //add 2014-12-31 xcx start 统计埋点
        MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
                UMengEventIds.DISCOVER_YELLOWPAGE_MY_MSG_CENTER_FLOW_ITEM_CLICK);
        //add 2014-12-31 xcx end 统计埋点
    }

    @Override
    public void click(PTOrderBean bean, Activity context) {
        if (bean == null) {
            return;
        }
        onClick(bean.getOrder_no(), context,true);
        
        //add xcx 2014-12-31 start 统计埋点
        MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
                UMengEventIds.DISCOVER_YELLOWPAGE_MY_ORDER_CENTER_FLOW_ITEM_CLICK);
        //add xcx 2014-12-31 end 统计埋点
    }

    private void onClick(String orderNo, Activity context,boolean fromMyOrder) {
        Intent intent = new Intent(context, YellowPageChargeTrafficOrderActivity.class);
        intent.putExtra("orderNo", orderNo);
        intent.putExtra("fromMyOrder", fromMyOrder);
        context.startActivity(intent);
    }

    @Override
    public void handleBusiness(PTMessageBean message) {
        //modify by xcx 2015-01-14 start 流量支付成功和充值成功的通知不在系统通知栏显示
        if (null == message) {
            return;
        }
        if(message.getIs_notify() == 0) {
            return;
        }
        
        OrderNumber orderNumber = getOrderNumber(message);
        if (null == orderNumber) {
            return;
        }
        int status = orderNumber.getPt_order_status();
        if (ResultCode.OrderStatus.Success == status || ResultCode.OrderStatus.Pending == status) {
            return;
        }
        String pt_order_no = orderNumber.getPt_order_no();
        if (TextUtils.isEmpty(pt_order_no)) {
            return;
        }
        Intent intent = new Intent(context, YellowPageChargeTrafficOrderActivity.class);
        intent.putExtra("orderNo", pt_order_no);
        Notification notification = new Notification.Builder(context)
                .setContentTitle(message.getSubject()).setContentText(message.getDigest())
                .setWhen(System.currentTimeMillis()).setSmallIcon(notificationLogoId).build();
        sendNotification(notification, intent);
        //modify by xcx 2015-01-14 end 流量支付成功和充值成功的通知不在系统通知栏显示
    }

    @Override
    public boolean isOrderExpire(PTOrderBean order) { boolean isOderExpire=false;
        switch (order.getStatus_code()) {
            case ResultCode.OrderStatus.Cancel:
            case ResultCode.OrderStatus.WaitForPayment:
            case ResultCode.OrderStatus.Failed:
            case ResultCode.OrderStatus.Success:
            case ResultCode.OrderStatus.Refunded:
            case ResultCode.OrderStatus.OutOfDate: {
                if (System.currentTimeMillis() - order.getM_time() > ONE_DAY_TIME) {
                    isOderExpire = true;
                }
                break;
            }
        }
        return isOderExpire;
    }

    @Override
    public void setEnable(boolean enable) {
        SharedPreferenceUtils.setPreference(PTMessageCenterSettings.SHARED_NAME,
                SHARED_PREFS_KEY_TRAFFIC, enable);
    }

    @Override
    public boolean getEnable() {
        return SharedPreferenceUtils.getPreference(PTMessageCenterSettings.SHARED_NAME,
                SHARED_PREFS_KEY_TRAFFIC, true);
    }

    @Override
    public View getConfigView(Activity context) {
        View view = View.inflate(context,
                R.layout.putao_lottery_remind_setting_layout, null);
        TextView tv = (TextView) view.findViewById(R.id.lottery_setting);
        tv.setText(R.string.putao_traffic_setting_hint);
        return view;
    }

    /**
     * @param bean
     * @return
     */
    private TrafficOrderBean getTrafficOrderBeenFromMessage(PTOrderBean bean) {
        if(null==bean){
            return null;
        }
        String expand_param = bean.getExpand();
        if (!TextUtils.isEmpty(expand_param)) {
            TrafficOrderBean order = null;
            try {
                Gson gson = new Gson();
                order = gson.fromJson(expand_param, TrafficOrderBean.class);
                return order;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return order;
        }
        return null;
    }

    class ViewHolder {
        ImageView icon;

        TextView title;

        TextView subject;

        TextView phoneNum;

        TextView status;

        TextView price;
        
        ImageView operator;
    }
    
    @Override
    public boolean checkMsg(PTMessageBean bean) {
        // TODO Auto-generated method stub
        return super.checkMsg(bean);
    }
    
    @Override
    public boolean checkOrder(PTOrderBean bean) {
        // TODO Auto-generated method stub
        boolean check= super.checkOrder(bean);
        if(check){
            TrafficOrderBean order = getTrafficOrderBeenFromMessage(bean);
            if(null==order){
                check=false;
            }
        }
        return check;
    }
    
    
    /**
     * {@link ResultCode.OrderStatus}
     * @author Administrator
     *
     * // 0 取消
        // 1 待支付
        // 2 支付失败
        // 3 处理中（支付成功 交易进行中）
        // 4 交易成功
        // 5 退款中（支付成功 交易失败 退款中）
        // 6 退款成功
        // 7 过期
        // 8 暂存订单
     */
    public enum ChargeOrderStatus {   
        
        ORDER_CANCEL("取消",0),
        WAIT_BUYER_PAY ("待付款",1),
        PAY_FAIL("待付款",2),
        TRADE_PROCESS("处理中",3),
        TRADE_SUCCESS("充值成功",4),
        REFUND_PROCESS("退款中",5),
        REFUND_SUCCESS("已退款",6),
        OOT_OF_DATE("交易关闭",7); 
        private String strStatus;
        private int intStatus;
        private  ChargeOrderStatus(String strStatus,int intStatus)
        {
            this.strStatus=strStatus;
            this.intStatus=intStatus;
        }
        public String getStatusStr()
        {
            return this.strStatus;
        }
        public int getStatusInt()
        {
            return this.intStatus;
        }
        public static ChargeOrderStatus getStatusBeen(int status)
        {
            for(ChargeOrderStatus been:ChargeOrderStatus.values())
            {
                if(been.intStatus==status)
                    return been;
            }
            return WAIT_BUYER_PAY;
        }
        public static String getStatusStr(PTOrderBean bean)
        {
            int status=bean.getStatus_code();
            if(isOrderOutDate(bean)){
                status=ResultCode.OrderStatus.OutOfDate; 
            }
            return getStatusBeen(status).getStatusStr();
        }
        @Override
        public String toString() {
            // TODO Auto-generated method stub
            return this.strStatus;
        }
    }
    
    /**
     * 判断订单是否过期，待付款状态特殊处理，
     * 待付款状态两小时候状态转为为交易关闭
     * @param bean
     * @return
     */
    public static boolean isOrderOutDate(PTOrderBean bean){
        if(bean.getStatus_code()==ResultCode.OrderStatus.WaitForPayment){
            if (System.currentTimeMillis() - bean.getM_time() > TWO_HOUR_TIME) {
                return true;
            }
        }
        if(bean.getStatus_code()==ResultCode.OrderStatus.OutOfDate){
            return true;
        }
        return false;
    }
}
