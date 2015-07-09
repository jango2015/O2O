package so.contacts.hub.ui.yellowpage;


import so.contacts.hub.ui.yellowpage.bean.ChargeTrafficMessageBusiness.ChargeOrderStatus;
import so.contacts.hub.ui.yellowpage.bean.ChargeTrafficMessageBusiness;
import so.contacts.hub.core.ConstantsParameter;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.msgcenter.PTOrderCenter;
import so.contacts.hub.msgcenter.PTOrderCenter.RefreshOrderListener;
import so.contacts.hub.msgcenter.bean.PTOrderBean;
import so.contacts.hub.net.IResponse;
import so.contacts.hub.net.PTHTTP;
import so.contacts.hub.net.SimpleRequestData;
import so.contacts.hub.payment.GetOrderParam;
import so.contacts.hub.payment.PaymentCallback;
import so.contacts.hub.payment.action.AbstractPaymentAction;
import so.contacts.hub.payment.action.DefaultPaymentActionFactory;
import so.contacts.hub.payment.data.ProductTypeCode;
import so.contacts.hub.payment.data.ResultCode;
import so.contacts.hub.remind.BaseRemindActivity;
import so.contacts.hub.ui.yellowpage.bean.TrafficOrderBean;
import so.contacts.hub.util.CalendarUtil;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.UMengEventIds;
import so.contacts.hub.widget.CommonDialog;
import so.contacts.hub.widget.CommonDialogFactory;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.yulong.android.contacts.discover.R;

public class YellowPageChargeTrafficOrderActivity extends BaseRemindActivity implements OnClickListener,IResponse,RefreshOrderListener,PaymentCallback{
    private static final int TEXT_COLOR_SECOND=R.color.putao_text_color_second;
    private static final int TEXT_COLOR_IMPORTANCE=R.color.putao_text_color_importance;
    private ImageView orderIcon;
    private ImageView operatorLogo;
    private TextView orderTitle;
    private TextView orderContent;
    private TextView orderPhoneNum;
    private TextView orderStatus;
    private TextView orderPrice;
    private TextView orderNo;
    private TextView orderCreateTime;
    private TextView orderProductDes;
    private Button orderCancelBtn;
    private Button orderPayBtn;
    private LinearLayout bottomLayout;
    private View bottomLine;
    private TextView orderPhone;
    private CommonDialog commonDialog;
    private ProgressDialog progressDialog;
    
    private PTOrderBean orderBean;
    private TrafficOrderBean order;
    private String orderNoStr;
    private boolean isFromMyOrder=false;
    private boolean isLoading=false;
    
    private boolean isNeedLoadData=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.putao_charge_telephone_order_detail);
        initViews();
        initData();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        refreshUI();
    }

    private void initViews(){
        orderIcon = (ImageView)findViewById(R.id.order_icon);
        orderTitle = (TextView)findViewById(R.id.order_title);
        orderContent = (TextView)findViewById(R.id.order_content);
        orderPhoneNum = (TextView)findViewById(R.id.order_phone_num);
        orderStatus = (TextView)findViewById(R.id.order_status);
        orderPrice = (TextView)findViewById(R.id.order_price);
        orderNo = (TextView)findViewById(R.id.order_no);
        orderCreateTime = (TextView)findViewById(R.id.order_create_time);
        orderProductDes = (TextView)findViewById(R.id.order_product_des);
        orderCancelBtn = (Button)findViewById(R.id.order_cancel_btn);
        orderPayBtn = (Button)findViewById(R.id.order_pay_btn);
        bottomLayout = (LinearLayout)findViewById(R.id.bottom_layout);
        bottomLine = findViewById(R.id.bottom_line);
        operatorLogo = (ImageView)findViewById(R.id.operator_logo);
        orderPhone= (TextView)findViewById(R.id.order_phone);
        TextView title = (TextView)findViewById(R.id.title);
        title.setText(R.string.putao_hotelorder_detail_hint);
        
        findViewById(R.id.back_layout).setOnClickListener(this);
        orderCancelBtn.setOnClickListener(this);
        orderPayBtn.setOnClickListener(this);
    }
    
    private void initData(){
        Intent intent = getIntent();
        if(intent != null){
            orderNoStr = intent.getStringExtra("orderNo");
            isFromMyOrder=getIntent().getBooleanExtra("fromMyOrder", false);
        }
        loadData();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.putao_yellow_page_loading));
    }
    
    private void refreshUI(){
        orderBean = PTOrderCenter.getInstance().getOrderByOrderNumber(orderNoStr);
        if (null == orderBean) {
            if(isNeedLoadData){
                loadData(); 
            }else if(!isLoading){
                Toast.makeText(YellowPageChargeTrafficOrderActivity.this,
                        R.string.putao_order_cancel_refresh_failure, Toast.LENGTH_SHORT).show();
                finish();
            }
            
            return;
        }
        if(orderBean != null && !TextUtils.isEmpty(orderBean.getExpand())){
            order =  new Gson().fromJson(orderBean.getExpand(), TrafficOrderBean.class);
            if(orderBean.getStatus_code() == ResultCode.OrderStatus.WaitForPayment){
                bottomLayout.setVisibility(View.VISIBLE);
                bottomLine.setVisibility(View.VISIBLE);
            }else{
                bottomLayout.setVisibility(View.GONE);
                bottomLine.setVisibility(View.GONE);
            }
        }
        if(order != null){
            orderIcon.setImageResource(R.drawable.icon_btn_id_chazhi);
            orderTitle.setText(R.string.putao_charge_traffic_subject);
            orderContent.setText(order.getContent());
            orderPhoneNum.setText(order.getMobilenum());
            orderPhone.setText(order.getMobilenum());
            if (ChargeTrafficMessageBusiness.isOrderOutDate(orderBean)) {
                orderBean.setStatus_code(ResultCode.OrderStatus.OutOfDate);
            }
            orderStatus.setText(ChargeOrderStatus.getStatusStr(orderBean));
            String result = String.format("%.2f", order.getProd_price() / 100f);
            orderPrice.setText(getResources().getString(
                    R.string.putao_order_item_showmoney, result));
            orderNo.setText(orderNoStr);
            orderCreateTime.setText(CalendarUtil.getDateStrFromLong(order.getTime(), "yyyy-MM-dd HH:mm"));
            orderProductDes.setText(getString(R.string.putao_charge_traffic_order_content, order.getContent(),order.getOrder_title()));
            if(!TextUtils.isEmpty(order.getContent())){
                if(order.getContent().contains("电信")){
                    operatorLogo.setImageResource(R.drawable.icon_btn_id_huafei_a);
                }else if(order.getContent().contains("移动")){
                    operatorLogo.setImageResource(R.drawable.icon_btn_id_huafei_b);
                }else{
                    operatorLogo.setImageResource(R.drawable.icon_btn_id_huafei_c);
                }
            }
            if(ResultCode.OrderStatus.WaitForPayment==orderBean.getStatus_code()){
                orderStatus.setTextColor(getResources().getColor(TEXT_COLOR_IMPORTANCE));
            }else{
                orderStatus.setTextColor(getResources().getColor(TEXT_COLOR_SECOND));
            }
        }
    }
    private void loadData(){
        if (!isLoading && !isFromMyOrder) {
            isNeedLoadData=false;
            isLoading=true;
            showLoadingDialog();
            PTOrderCenter.getInstance().requestRefreshOrders(this);
        }
    }
    private void showDialog(){
        commonDialog = CommonDialogFactory.getOkCancelCommonDialog(this);
        commonDialog.setTitle(R.string.putao_order_cancel_title);
        commonDialog.setMessage(R.string.putao_order_cancel_message);
        commonDialog.setOkButtonClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                commonDialog.dismiss();
                cancelOrder();
            }
        });
        commonDialog.show();
    }
    
    private void cancelOrder(){
        if(progressDialog != null){
            progressDialog.show();
        }
        SimpleRequestData reqData = new SimpleRequestData();
        reqData.setParam("order_no", orderNoStr);
        PTHTTP.getInstance().asynPost("http://pay.putao.so/pay/order/cancel", reqData,this);
    }
    
    private void payOrder(){
        AbstractPaymentAction paymentAction = DefaultPaymentActionFactory.createAction(orderBean.getPayment_type(), this);
        GetOrderParam param = new GetOrderParam();
        param.setProductId(ProductTypeCode.Flow.ProductId);
        param.setProductType(ProductTypeCode.Flow.ProductType);
        param.setOrderNo(orderNoStr);

        param.putSubObj("prodid", String.valueOf(order.getProdid()));
        param.putSubObj("prod_price", String.valueOf(order.getProd_price()));
        param.putSubObj("mobilenum", order.getMobilenum());
        param.putSubObj("order_title", order.getOrder_title());
        param.putSubObj("content",order.getContent());
        
        
        param.putUIPair("mobile_ui", order.getMobilenum() + "  " + order.getContent());
        param.putUIPair("traffic_value", order.getOrder_title());
        
        param.setPriceInCents(orderBean.getPrice());
        addUmengEvent(param);
        paymentAction.startPayment(param, this);
    }
    /**
     * 有盟统计 
     * @author xcx
     * @param orderParam
     */
    private void addUmengEvent(GetOrderParam orderParam){
        StringBuffer uMengSuccessIds = new StringBuffer();
        StringBuffer uMengFailIds = new StringBuffer();
        uMengSuccessIds.append(UMengEventIds.DISCOVER_YELLOWPAGE_PHONE_RECHARGE_FLOW_SUCCESS);
        uMengFailIds.append(UMengEventIds.DISCOVER_YELLOWPAGE_PHONE_RECHARGE_FLOW_FAIL);
        orderParam.putUIPair(UMengEventIds.EXTRA_UMENG_EVENT_IDS_SUCCESS, uMengSuccessIds.toString());
        orderParam.putUIPair(UMengEventIds.EXTRA_UMENG_EVENT_IDS_FAIL, uMengFailIds.toString());
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_layout:
                finish();
                break;
            case R.id.order_cancel_btn:
                showDialog();
                break;
            case R.id.order_pay_btn:
                payOrder();
                // add xcx 2014-12-31 start 统计埋点
                if (isFromMyOrder) {
                    MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
                            UMengEventIds.DISCOVER_YELLOWPAGE_MY_ORDER_CENTER_FLOW_IMMEDIATE_PAY);
                }
                // add xcx 2014-12-31 end 统计埋点
                break;
            default:
                break;
        }
    }

    @Override
    public void refreshSuccess(boolean isDbChanged) {
        refreshUI();
        dismissLoadingDialog();
        isLoading=false;
        
     // removed by cj 2015/02/03
//        ContactsApp.getInstance().sendBroadcast(new Intent(ConstantsParameter.ACTION_ORDER_UPDATE_DATA));
    }

    @Override
    public void refreshFailure(String msg) {
        Toast.makeText(this, R.string.putao_order_cancel_refresh_failure, Toast.LENGTH_SHORT).show();
        isLoading=false;
        dismissLoadingDialog();
        if(!isFromMyOrder &&null ==orderBean ){
            finish();
        }
    }

    @Override
    public void onSuccess(String content) {
        if(progressDialog != null){
            progressDialog.dismiss();
        }
        try {
            JSONObject json = new JSONObject(content);
            String ret_code=json.getString("ret_code");
            if(ret_code.equals("0000")){
                PTOrderCenter.getInstance().requestRefreshOrders(this);
            }else{
                Toast.makeText(this, R.string.putao_order_cancel_failure, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFail(int errorCode) {
        if(progressDialog != null){
            progressDialog.dismiss();
        }
        Toast.makeText(this, R.string.putao_order_cancel_failure, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentFeedback(int actionType, Throwable t, int resultCode,
            Map<String, String> extras) {
        if(resultCode == ResultCode.AliPay.Success
            || resultCode == ResultCode.OrderStatus.Success){
            PTOrderCenter.getInstance().requestRefreshOrders(this);
        }
    }

    
}
