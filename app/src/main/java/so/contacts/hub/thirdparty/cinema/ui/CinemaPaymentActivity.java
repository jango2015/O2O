
package so.contacts.hub.thirdparty.cinema.ui;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.http.OrderEntity;
import so.contacts.hub.payment.GetOrderParam;
import so.contacts.hub.payment.PaymentCallback;
import so.contacts.hub.payment.PaymentViewGroup;
import so.contacts.hub.payment.core.PaymentDesc;
import so.contacts.hub.payment.data.ResultCode.OrderStatus;
import so.contacts.hub.remind.BaseRemindActivity;
import so.contacts.hub.thirdparty.cinema.CinemaConstants;
import so.contacts.hub.thirdparty.cinema.bean.DetailMovieOrder;
import so.contacts.hub.util.CalendarUtil;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.UMengEventIds;
import so.contacts.hub.util.UserInfoUtil;
import so.contacts.hub.widget.CommonDialog;
import so.contacts.hub.widget.CommonDialogFactory;
import so.contacts.hub.yellow.data.Voucher;
import so.contacts.hub.yellow.data.Voucher.VoucherScope;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.yulong.android.contacts.discover.R;

/**
 * 电影票订单支付页面
 * 
 * @author zj 2014-12-22 14:42:22
 */
public class CinemaPaymentActivity extends BaseRemindActivity implements OnClickListener,PaymentCallback{
    public static final int TYPE_OPEN = 1;

    public static final int TYPE_COMING = 2;

    private int type = TYPE_OPEN;
    private static final int SHOW_LIMIT_TIME=1;
    
    private static final int SHOW_COUPON_DATA=2;//add by hyl 2015-1-23
    private Thread initCouponDataThread = null;//add by hyl 2015-1-23
    
    private PaymentViewGroup payment;
    private DetailMovieOrder orderDetail;
    /*
     * modify by putao_lhq at 2015年1月19
     * delete code:
    private String mCinemaAddress;
    private String mCityName;*/
    private CommonDialog commonDialog;
    
    //是否成功支付过
    private boolean isPaySucess = false;
    
    private Handler mHandler=new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case SHOW_LIMIT_TIME:
                    setLimitTime();
                    break;
                case SHOW_COUPON_DATA://add by hyl 2015-1-23 start
                	showCouponData();
                	break;//add by hyl 2015-1-23 end
                default:
                    break;
            }
            
            
        };  
    };

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.putao_cinema_payment_activity);
        
        //add ljq start 2015-01-14 因为1.002版本微信支付状态不准确 只能用此全局变量表示微信支付状态
        ContactsApp.isCinemaWeChatPaySuccess = false;
        //add ljq end 2015-01-14 
        
        
        if (getIntent()!=null) {
            orderDetail = (DetailMovieOrder)getIntent().getSerializableExtra(CinemaConstants.MOVIE_ORDER_DETAIL);
            type = getIntent().getIntExtra("type", TYPE_OPEN);
            /*
             * modify by putao_lhq at 2015年1月19日
             * delete code:
            mCinemaAddress =  getIntent().getStringExtra(CinemaConstants.CINEMA_ADDRESS);
            mCityName = getIntent().getStringExtra(CinemaConstants.CINEMA_CITY);*/
        };
        if (orderDetail!=null) {
            initView();
        }else{
        	finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView() {
//        ((TextView)findViewById(R.id.title)).setText(R.string.putao_pay_order);
    	setTitle(R.string.putao_pay_order);

        findViewById(R.id.confirm).setOnClickListener(this);
        
        TextView tv_filmorder_title=(TextView)findViewById(R.id.tv_filmorder_title);
        
        /*
         * Bug修改（2543），不使用订单信息中的标题 自己根据电影名称拼接 （电影票-一代宗师3D(2)张）
         * modified by hyl 2015-1-8 start
         * old code：
         *  tv_filmorder_title.setText(orderDetail.getOrder_title());
         */
        String payTitle = getString(R.string.putao_movie_pay_title, orderDetail.getMovie_name(),orderDetail.quantity);
        tv_filmorder_title.setText(payTitle);
        //modified by hyl 2015-1-8 end
        
        TextView order_price=(TextView)findViewById(R.id.order_price);
        order_price.setText(getString(R.string.putao_payment_money, orderDetail.getAmount()/100+""));
        
        setLimitTime();
        
        //支付控件
        payment = (PaymentViewGroup)findViewById(R.id.payment);
        payment.setPaymentCallback(this);
        payment.selectPayAction(PaymentDesc.ID_WE_CHAT);
		
		//add by hyl 2015-1-23 start 从葡萄生活同步 电影票优惠券相关
        initRealPay() ;
        mDiscountText = (TextView)findViewById(R.id.cpay_discount_text);
        mDiscountText.setOnClickListener(this);
        
        if(TextUtils.isEmpty(orderDetail.getCoupon_ids())){//判断订单上是否已包含优惠券信息，已有代表已使用过优惠券，不再显示优惠券信息
        	initCouponData();
        }
		//add by hyl 2015-1-23 end 
    }
	
    /**
     * 选择不同的支付渠道
     */
    private void selectPayChannel(String price) {
        for (int i = 0, max = PaymentDesc.ALL_PAY_ACTS.length; i < max; i++) {
            payment.setAmountText(PaymentDesc.ALL_PAY_ACTS[i],
                    String.format(getString(R.string.putao_pay_charge_price), price));
        }
    }

    private void setLimitTime() {
        TextView limit_time=(TextView)findViewById(R.id.limit_time);
        long millis=Timestamp.valueOf(orderDetail.getValid_time()).getTime()-System.currentTimeMillis();
        String time=CalendarUtil.getDateStrFromLong(millis, getString(R.string.putao_cinema_payment_limit_time_format_pattern));
        String s=getString(R.string.putao_cinema_payment_limit_time,time);
        Spannable sp = new SpannableString(s);
        ForegroundColorSpan color=new ForegroundColorSpan(getResources().getColor(R.color.putao_red));
        sp.setSpan(color, 0, 1,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        color=new ForegroundColorSpan(getResources().getColor(R.color.putao_red));
        sp.setSpan(color, s.indexOf(time), s.indexOf(time)+time.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        
        limit_time.setText(sp);
        
        mHandler.sendEmptyMessageDelayed(SHOW_LIMIT_TIME, 1000);
    }
    
    /**
     * 初始化优惠券信息
     * add by hyl 2015-1-23 从葡萄生活同步
     */
    private void initCouponData() {
		LogUtil.d("PutaoAccount", "initCouponData"+System.currentTimeMillis());
		initCouponDataThread = new Thread(new Runnable() {
			@Override
			public void run() {
				LogUtil.d("PutaoAccount", "initCouponData initCouponDataThread"+System.currentTimeMillis());
				UserInfoUtil.getInstace().updateUserCouponData();
				LogUtil.d("PutaoAccount", "initCouponData updateUserCouponData"+System.currentTimeMillis());
				couponList = UserInfoUtil.getInstace().getAvailableVouchers(VoucherScope.Movie);
				mHandler.sendEmptyMessage(SHOW_COUPON_DATA);
			}
		});
		initCouponDataThread.start();
    }

    /**
     * 初始化优惠券信息
     */
    private void showCouponData() {
        if (couponList != null && couponList.size() > 0) {
            // 注: 默认显示第一张代金券
            mChoosedVoucher = couponList.get(0);
            mDiscountText.setText(getString(R.string.putao_cinema_coupon_use_money,
                    mChoosedVoucher.money));
            mDiscountText.setVisibility(View.VISIBLE);
            initRealPay();
        } else {
            mChoosedVoucher = null;
            mDiscountText.setVisibility(View.GONE);
        }
    }

    /**
     * 选中的券
     */
    private Voucher mChoosedVoucher;

    private List<Voucher> couponList = null; // 用户优电影票惠券信息

    private TextView mDiscountText = null;

    /**
     * 实际支付的金额
     */
    private int mRealPayInCents;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm://确认支付
                pay();
                break;
            case R.id.cpay_discount_text: //add by hyl 2015-1-23 start 从葡萄生活同步 电影票优惠券相关
                if (couponList != null) {
                    final ArrayList<String> datas = new ArrayList<String>();
                    final CommonDialog dialog = CommonDialogFactory.getListCommonDialog(this);
                    datas.add(getString(R.string.putao_tel_charge_coupon_not_use)); // 第一个显示：不使用优惠券
                    for (Voucher voucher : couponList) {
                        datas.add(getString(R.string.putao_cinema_coupon_use_money,
                                voucher.money));
                    }
                    dialog.setTitle(R.string.putao_tel_charge_coupon_use);
                    dialog.setListViewDatas(datas);
                    dialog.setListViewItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                long arg3) {
                            dialog.dismiss();
                            if (position == 0) {
                                mChoosedVoucher = null;
                            } else {
                                mChoosedVoucher = couponList.get(position - 1);
                            }
                            mDiscountText.setText(datas.get(position));
                            initRealPay();
                        }
                    });
                    dialog.show();
                }
                break;//add by hyl 2015-1-23 end 从葡萄生活同步 电影票优惠券相关
            default:
            	break;
        }

    }

    /**
     * 设置实际支付额度
     */
    private void initRealPay() {
        mRealPayInCents = orderDetail.amount;
        if (mChoosedVoucher != null) {
            int choiceVoucherMoney = 0;
            try {
                choiceVoucherMoney = (int)(Float.parseFloat(mChoosedVoucher.money) * 100);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (choiceVoucherMoney > 0) {
                int finalPriceInCent = mRealPayInCents - choiceVoucherMoney;
                if (finalPriceInCent <= 0) {
                    finalPriceInCent = 1;// 最低付费金额1分
                }
                mRealPayInCents = finalPriceInCent;
            }
        }
        selectPayChannel(mRealPayInCents / 100 + "");
    }

    private void pay() {
        if(!isPaySucess){
	        GetOrderParam orderParam=new GetOrderParam();
            if (mChoosedVoucher!=null) {//如果有优惠券把优惠券信息设置进去
                orderParam.addCounponId(mChoosedVoucher.id);
            }
			orderParam.setPriceInCents(mRealPayInCents);
	        //orderParam.setPriceInCents(orderDetail.amount);
	        
			orderParam.setOrderNo(orderDetail.order_no);
	        orderParam.setProductType(OrderEntity.Product.cinema.getProductType());
	        orderParam.setProductId(OrderEntity.Product.cinema.getProductId());
	        orderParam.setSubObjMap(OrderEntity.convertToMap(orderDetail, DetailMovieOrder.class)); 
        
	        //add by hyl 2015-1-8 start 添加影院和电影名称（用于支付结果页显示）
	        String payTitle = getString(R.string.putao_movie_pay_title, orderDetail.getMovie_name(),orderDetail.quantity);
	        orderParam.putUIPair("movie_name",payTitle);
	        //orderDetail.getCinema_name()+"-"+orderDetail.getMovie_name()
	//		+getString(R.string.putao_movie_quantity, orderDetail.quantity)
	        //add by hyl 2015-1-8 end
        
	      //add by xcx 2015-01-15 start 支付结果统计
	        addUmengEvent(orderParam);
	      //add by xcx 2015-01-15 start 支付结果统计
	        payment.startPayment(orderParam);       
		}else{
            showInvalidTipDialog();
        }
        //modity by ljq 2015-01-14 end 支付成功后不能再进行支付 
    }

    /**
     * 有盟统计 
     * @author xcx
     * @param orderParam
     */
    private void addUmengEvent(GetOrderParam orderParam){
        StringBuffer uMengSuccessIds=new StringBuffer();
        StringBuffer uMengFailIds=new StringBuffer();
        if (type == TYPE_OPEN) {
            uMengSuccessIds.append(UMengEventIds.DISCOVER_YELLOWPAGE_MOVIE_RELEASED_SEAT_PAY_SUCCESS);
            uMengFailIds.append(UMengEventIds.DISCOVER_YELLOWPAGE_MOVIE_RELEASED_SEAT_PAY_FAIL);
        }else{
            uMengSuccessIds.append(UMengEventIds.DISCOVER_YELLOWPAGE_MOVIE_UPCOMING_SEAT_PAY_SUCCESS);
            uMengFailIds.append(UMengEventIds.DISCOVER_YELLOWPAGE_MOVIE_UPCOMING_SEAT_PAY_FAIL);
        }
        uMengSuccessIds.append(",");
        uMengSuccessIds.append(UMengEventIds.DISCOVER_YELLOWPAGE_MOVIE_ALL_SEAT_PAY_SUCCESS);
        
        uMengFailIds.append(",");
        uMengFailIds.append(UMengEventIds.DISCOVER_YELLOWPAGE_MOVIE_AKK_SEAT_PAY_FAIL);
        
        orderParam.putUIPair(UMengEventIds.EXTRA_UMENG_EVENT_IDS_SUCCESS,uMengSuccessIds.toString());
        orderParam.putUIPair(UMengEventIds.EXTRA_UMENG_EVENT_IDS_FAIL,uMengFailIds.toString());
    }
    @Override
    public String getServiceNameByUrl() {
        return null;
    }

    @Override
    public String getServiceName() {
        return CinemaPaymentActivity.class.getName();
    }

    @Override
    public boolean needMatchExpandParam() {
        return false;
    }

    @Override
    public Integer remindCode() {
        return null;
    }

    @Override
    public void onPaymentFeedback(int actionType, Throwable t, int resultCode,
            Map<String, String> extras) {
        if(actionType == PaymentDesc.ID_WE_CHAT){
            if(ContactsApp.isCinemaWeChatPaySuccess){
                onPaymentFeedbackSuccess();
            }else{
                onPaymentFeedbackFailed();
            }
        }else{
            switch (resultCode) {
                case OrderStatus.Success://返回订单详情页
                    onPaymentFeedbackSuccess();
                    break;
                case OrderStatus.Failed:
                    onPaymentFeedbackFailed();
                    break;
                default:
                    break;
            }
        }
    }
    
    private void onPaymentFeedbackSuccess(){
        if (mChoosedVoucher!=null) {
            UserInfoUtil.getInstace().delUserVoucherList(mChoosedVoucher.id);
            showCouponData();
        }
        isPaySucess = true;
//        // add xcx 2014-12-30 start 统计埋点
//        if (type == TYPE_OPEN) {
//            MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
//                    UMengEventIds.DISCOVER_YELLOWPAGE_MOVIE_RELEASED_SEAT_PAY_SUCCESS);
//        } else {
//            MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
//                    UMengEventIds.DISCOVER_YELLOWPAGE_MOVIE_UPCOMING_SEAT_PAY_SUCCESS);
//        }
//        MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
//                UMengEventIds.DISCOVER_YELLOWPAGE_MOVIE_ALL_SEAT_PAY_SUCCESS);
//        // add xcx 2014-12-30 end 统计埋点
    }
    
    private void onPaymentFeedbackFailed(){
//        // add xcx 2014-12-30 start 统计埋点
//        if (type == TYPE_OPEN) {
//            MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
//                    UMengEventIds.DISCOVER_YELLOWPAGE_MOVIE_RELEASED_SEAT_PAY_FAIL);
//        } else {
//            MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
//                    UMengEventIds.DISCOVER_YELLOWPAGE_MOVIE_UPCOMING_SEAT_PAY_FAIL);
//        }
//        MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
//                UMengEventIds.DISCOVER_YELLOWPAGE_MOVIE_AKK_SEAT_PAY_FAIL);
//        // add xcx 2014-12-30 end 统计埋点
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(initCouponDataThread != null){
        	initCouponDataThread.interrupt();
        	initCouponDataThread = null;
        }
    }
    
    /**
     * 弹出无效支付提示
     */
    private void showInvalidTipDialog(){
        commonDialog = CommonDialogFactory.getOkCommonDialog(this);
        commonDialog.setTitle(R.string.putao_movie_error_tips);
        commonDialog.setMessage(R.string.putao_movie_order_has_been_successful);
        commonDialog.setOkButtonClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                commonDialog.dismiss();
            }
        });
        commonDialog.show();
    }

}
