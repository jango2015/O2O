package so.contacts.hub.thirdparty.cinema.ui;


import so.contacts.hub.ContactsApp;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.UMengEventIds;

import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import so.contacts.hub.account.PutaoAccount;
import so.contacts.hub.core.Config;
import so.contacts.hub.core.PTUser;
import so.contacts.hub.msgcenter.PTMessageCenter;
import so.contacts.hub.msgcenter.PTOrderCenter;
import so.contacts.hub.msgcenter.PTOrderCenter.RefreshOrderListener;
import so.contacts.hub.msgcenter.bean.OrderNumber;
import so.contacts.hub.msgcenter.bean.PTMessageBean;
import so.contacts.hub.msgcenter.bean.PTOrderBean;
import so.contacts.hub.msgcenter.bean.PTOrderStatus;
import so.contacts.hub.net.IResponse;
import so.contacts.hub.net.NormalOrderRequestData;
import so.contacts.hub.net.PTHTTP;
import so.contacts.hub.net.SimpleRequestData;
import so.contacts.hub.payment.data.ResultCode;
import so.contacts.hub.remind.BaseRemindActivity;
import so.contacts.hub.thirdparty.cinema.CinemaConstants;
import so.contacts.hub.thirdparty.cinema.bean.DetailMovieOrder;
import so.contacts.hub.thirdparty.cinema.bussiness.MovieMessageBussiness;
import so.contacts.hub.thirdparty.cinema.tool.CinemaApiUtil;
import so.contacts.hub.thirdparty.cinema.tool.CinemaApiUtilHelper;
import so.contacts.hub.thirdparty.cinema.utils.CinemaUtils;
import so.contacts.hub.thirdparty.cinema.utils.GewaApiReqMethod;
import so.contacts.hub.ui.web.YellowPageH5Activity;
import so.contacts.hub.ui.yellowpage.YellowPageJumpH5Activity;
import so.contacts.hub.ui.yellowpage.YellowPageLoginActivity;
import so.contacts.hub.ui.yellowpage.bean.CategoryBean;
import so.contacts.hub.ui.yellowpage.bean.HabitDataItem;
import so.contacts.hub.ui.yellowpage.bean.YellowParams;
import so.contacts.hub.util.CalendarUtil;
import so.contacts.hub.util.ContactsAppUtils;
import so.contacts.hub.util.ContactsHubUtils;
import so.contacts.hub.util.MyCenterConstant;
import so.contacts.hub.util.NetUtil;
import so.contacts.hub.util.UserInfoUtil;
import so.contacts.hub.util.YellowUtil;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loader.DataLoader;
import com.loader.image.ImageLoaderFactory;
import com.yulong.android.contacts.discover.R;

/**
 * 电影订单详情界面
 * @author peku
 *
 */
public class MovieOrderDetailsActivity extends BaseRemindActivity implements OnClickListener, RefreshOrderListener{
	
	public final static String FLAG_ORDER_CODE="order_code";  //格瓦拉的订单号
	public final static String FLAG_PTORDER_CODE="pt_order_no";  //后台订单号
	private final static String TICKET_HELP_RESULT="TICKET_HELP";
	
    public static final String ISREFRESH_UI_KEY = "isRefreshUI";

    public static final int TYPE_OPEN = 1;
    public static final int TYPE_COMING = 2;
	public static final int MSG_ORDER_NOT_EXIST = 3;
    public static final int MSG_ORDER_FOUNDED = 4;
    private int type = TYPE_OPEN;
    
	private View layout_main;
	private LinearLayout layout_payandcancel;
	private TextView tv_movie_status;
	private TextView tv_price;
	private TextView tv_tradeno;
	private TextView order_price;
	private TextView tv_createtime;
	private TextView tv_seat;
	private TextView tv_phone;
	private EditText et_phone;
	private TextView tv_reminder;
	private View btn_quickpay;
	private View btn_cancel;
	private DataLoader imageLoader;
	private ImageView movie_icon;
	private TextView cinema_name;
	private TextView play_time;
	private TextView movie_name;
	private LinearLayout layout_aboutmovie;
	
	private View  mLayoutOrderStatus;
	private View mLayoutServerProviderInfo;
	private View mLayoutTaxiAndEntertainment;
	private View mButtonTaxi;
	private View mButtonEntertainment;
	private Button mButtonGenerateOrder;
	private TextView mTextViewOrderStatus;
	private View mEditTextLayout;
	
	private ImageView iv_movie_logo;
	private LinearLayout tr_price;
	private LinearLayout tr_createtime;
	
	private String language;
	private String edition;
	private String tradeno;
	private String pt_tradeno;
	private String movie_photo_url;
	private DetailMovieOrder orderDetail;
    private String orderSeat;
    private boolean isRunning=false;
	private int entryType;
	private String mCinemaAddress;
	private String mCityName;
	
	private int gewalaResult=-1;
	private int serverResult=-1;
	private boolean isCancelFail=false;
	
    private View exception_layout;//add by ljq 2015-1-23
	
	private String servicefee  = "";//add by hyl 2015-1-7 电影票服务费
	private TextView tv_filmorder_seatInfo;
	private boolean isFromMyOrder=false;
	public Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			dismissLoadingDialog();
			switch (msg.what) {
			case 0:
				String method = msg.getData().getString("method");
				
				if (GewaApiReqMethod.TICKET_HELP.toString().equals(method)) {//取票机位置
					Intent intent = new Intent(MovieOrderDetailsActivity.this,
							MovieTicketHelpActivity.class);
					intent.putExtra(TICKET_HELP_RESULT, (String) msg.obj);
					MovieOrderDetailsActivity.this.startActivity(intent);
				}
				/*
				 * 注释无效代码
				 * modified by hyl 2015-1-6 start
				 */
//				else if (GewaApiReqMethod.TICKETORDER_DETAIL.toString()
//						.equals(method)) {
//					orderDetail = (DetailMovieOrder) msg.obj;
//					initData();
//				}
				//modified by hyl 2015-1-6 end
				
				//modified by hyl 2014-1-6
				else if (GewaApiReqMethod.CANCEL_OEDER.toString().equals(method)) {
					
					finish();
					
//					String result = (String) msg.obj;
//					gewalaResult = resultStr2Bool(result);
//					if (gewalaResult == 1) {// 取消订单成功
//						// 请求后台取消订单
//						SimpleRequestData reqData = new SimpleRequestData();
//						reqData.setParam("order_no", tradeno);
//						PTHTTP.getInstance().asynPost(
//								"http://pay.putao.so/pay/order/cancel",
//								reqData, MovieOrderDetailsActivity.this);
//					} else {
//						// 取消失败
//						handleCancelOrder(result);
//					}
				}
				break;
			case MSG_ORDER_NOT_EXIST:
				Toast.makeText(MovieOrderDetailsActivity.this, R.string.order_not_exist, Toast.LENGTH_SHORT).show();
				finish();
				break;
			case MSG_ORDER_FOUNDED:
				initData();
				break;
			default:
				break;
			} 
		};
	};
    private PTOrderBean movieOrder;
  

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.putao_order_filmticket_layout);
		imageLoader = new ImageLoaderFactory(this).getMovieListLoader();
		initView();
		setProgressBarVisible(true);
		parseIntent();
    }
	
	private void initData() {
		if(null != orderDetail){
			if (orderDetail.pt_status == PTOrderStatus.WAIT_BUYER_PAY.getStatusInt() &&!orderDetail.isTimeOut()) {
//				tv_reminder.setVisibility(View.GONE);
				layout_payandcancel.setVisibility(View.VISIBLE);
//				btn_tickethelp.setVisibility(View.VISIBLE); //暂时隐藏取票机入口
			} else {
//				tv_reminder.setVisibility(View.VISIBLE);
				layout_payandcancel.setVisibility(View.GONE);
//				btn_tickethelp.setVisibility(View.GONE);
			}
			switch (entryType) {
			case CinemaConstants.GENERATE_ORDER:
				initDataWithScheme1();
				break;
			case CinemaConstants.ORDER_DETAIL:
				initDataWithScheme2();
			default:
				break;
			}
		}
		setProgressBarVisible(false);
	}


	private void initDataWithScheme1() {
		tv_movie_status.setVisibility(View.GONE);
		tr_price.setVisibility(View.GONE);
		tr_createtime.setVisibility(View.GONE);
		et_phone.setVisibility(View.VISIBLE);
		tv_phone.setVisibility(View.GONE);
		tv_reminder.setVisibility(View.VISIBLE);
//		btn_cancel.setVisibility(View.GONE);
		mButtonGenerateOrder.setVisibility(View.VISIBLE);
//		mButtonGenerateOrder.setText(R.string.order_movie_finishseat);
		//add by jsy 
		mLayoutOrderStatus.setVisibility(View.GONE);
		mLayoutServerProviderInfo.setVisibility(View.GONE);
		mLayoutTaxiAndEntertainment.setVisibility(View.GONE);
		layout_payandcancel.setVisibility(View.GONE);
		
		
		if(null!=orderDetail)
			fillData(orderDetail);
		
	}


	private void initDataWithScheme2() {
		
		et_phone.setVisibility(View.GONE);
		tv_phone.setVisibility(View.VISIBLE);
		tv_movie_status.setVisibility(View.VISIBLE);
		tr_price.setVisibility(View.VISIBLE);
		tr_createtime.setVisibility(View.VISIBLE);
//		btn_cancel.setVisibility(View.VISIBLE);
//		btn_quickpay.setText(R.string.order_movie_quickpay);
		
		//add by jsy 
		tv_reminder.setVisibility(View.GONE);
		mLayoutOrderStatus.setVisibility(View.VISIBLE);
		mLayoutServerProviderInfo.setVisibility(View.VISIBLE);
		
		
		/*
		 * 增加订单状态判断
		 * add by hyl 2014-1-6 start 
		 */
		layout_payandcancel.setVisibility(View.GONE);
		if(orderDetail.pt_status == PTOrderStatus.TRADE_SUCCESS.getStatusInt()){//订单交易成功
			layout_payandcancel.setVisibility(View.GONE);
			mLayoutTaxiAndEntertainment.setVisibility(View.VISIBLE);
		}else if(orderDetail.pt_status == PTOrderStatus.TRADE_PROCESS.getStatusInt()){//处理中
			layout_payandcancel.setVisibility(View.GONE);
			mLayoutTaxiAndEntertainment.setVisibility(View.VISIBLE);
		}else if(orderDetail.pt_status == PTOrderStatus.ORDER_CANCEL.getStatusInt()){//订单取消
			layout_payandcancel.setVisibility(View.GONE);
			mLayoutTaxiAndEntertainment.setVisibility(View.GONE);
		}else if(orderDetail.pt_status == PTOrderStatus.WAIT_BUYER_PAY.getStatusInt()){//订单创建 未支付
			if(orderDetail.isTimeOut()){//订单已过期
				layout_payandcancel.setVisibility(View.GONE);
				mLayoutTaxiAndEntertainment.setVisibility(View.GONE);
			}else{
				layout_payandcancel.setVisibility(View.VISIBLE);
				mLayoutTaxiAndEntertainment.setVisibility(View.GONE);
			}
		}else if(orderDetail.pt_status == PTOrderStatus.PAY_FAIL.getStatusInt()){//支付失败
			btn_cancel.setVisibility(View.GONE);
			btn_quickpay.setVisibility(View.VISIBLE);
			mLayoutTaxiAndEntertainment.setVisibility(View.GONE);
		}else if(orderDetail.pt_status == PTOrderStatus.REFUND_PROCESS.getStatusInt()){//退款中
			btn_cancel.setVisibility(View.GONE);
			btn_quickpay.setVisibility(View.GONE);
			mLayoutTaxiAndEntertainment.setVisibility(View.GONE);
		}else if(orderDetail.pt_status == PTOrderStatus.REFUND_SUCCESS.getStatusInt()){//退款成功
			btn_cancel.setVisibility(View.GONE);
			btn_quickpay.setVisibility(View.GONE);
			mLayoutTaxiAndEntertainment.setVisibility(View.GONE);
		}
		//add by hyl 2014-1-6 end
		
		if(null!=orderDetail){
			fillData(orderDetail);
		}
	}


	/**
	 * @author peku
	 * 设置控件数据
	 * @param filmOrder 电影订单详情的been
	 */
	private void fillData(DetailMovieOrder filmOrder) {
		if( entryType == CinemaConstants.ORDER_DETAIL ){
			tv_tradeno.setText(filmOrder.getTrade_no());
			String createtime = CinemaUtils.getNeedFormatTime(
					CinemaUtils.timeStr2Timestamp(filmOrder.getAdd_time()), "yyyy-MM-dd HH:mm");
			tv_createtime.setText(createtime);
			tv_phone.setText(CinemaUtils.hidePhone(filmOrder.getMobile()));

            String statusStr = filmOrder.showStatus();
            mTextViewOrderStatus.setText(statusStr); // 订单状态描述

            List<PTMessageBean> listBean = PTMessageCenter.getInstance().queryMessageByOrderNo(
                    filmOrder.getOrder_no());

            boolean showMessageData = false;
            if ((listBean != null) && (listBean.get(0) != null)) {
                PTMessageBean messageBean = listBean.get(0);
                OrderNumber orderNumber = MovieMessageBussiness.getOrderNumber(messageBean);
                if(orderNumber!=null){
                    int messageStatus = orderNumber.getPt_order_status();
                    int orderStatus = filmOrder.getPt_status();
                    if(filmOrder.isTimeOut()){
                        orderStatus=ResultCode.OrderStatus.Cancel;
                    }
                    if (messageStatus == orderStatus) {
                        showMessageData = true;
                        tv_movie_status.setText(messageBean.getDigest());
                        tv_price.setText(CalendarUtil.showDateAgo(listBean.get(0).getTime(), this));
                    }
                }
            }
            if (!showMessageData) {
                tv_movie_status.setText(statusStr);
                tv_price.setText(getResources().getString(
                        R.string.putao_order_item_showmoney,
                        getString(R.string.putao_order_item_showmoney,
                                CinemaUtils.getDouble2(filmOrder.getAmount() / 100))));
            }
        }
		
		order_price.setText("￥"+CinemaUtils.getDouble2(filmOrder.getAmount()/100) + servicefee);
		movie_icon.setImageResource(R.drawable.putao_icon_btn_id_dianying);//电影默认图标
		cinema_name.setTextColor(Color.WHITE);
		cinema_name.setText(getResources().getString(R.string.putao_film_ticket) + "-"+filmOrder.getCinema_name());
		
		movie_name.setText(filmOrder.getMovie_name());
		String playTime=CinemaUtils.getNeedFormatTime(CinemaUtils.timeStr2Timestamp(filmOrder.getPlay_time()), "HH:mm MM月dd日");
		play_time.setText(playTime);
		
		/*
		 * 当订单包含多个座位信息时，只显示一张
		 * modified by hyl 2015-1-7 start
		 * old code：
		 * tv_seat.setText(filmOrder.getSeat())
		 */
		String seats = CinemaUtils.formatOrderItemSeat(filmOrder.getSeat());
		String seatInfo = seats;
		if(seats.split(",").length > 1){
			seatInfo = seats.split(",")[0];
		}
		int num = seats.split(",").length == 0?1 : seats.split(",").length ;
		if(num == 1){
		    seatInfo = seatInfo + String.format(getResources().getString(R.string.putao_moive_order_filmticket_count_tip),num);
		}
		tv_seat.setText(seatInfo);	
		//modified by hyl 2015-1-7 end
		
		tv_filmorder_seatInfo.setText(seats);//add by hyl 2015-1-7
		
		if(null!=movie_photo_url)
		{
			Log.e("mr", movie_photo_url);
			imageLoader.loadData(movie_photo_url,iv_movie_logo);
		}
	}
	
	
    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        // TODO Auto-generated method stub
        super.onActivityResult(arg0, arg1, arg2);
        if(arg0 == 0){
            Intent it = new Intent();  
            it.putExtra(ISREFRESH_UI_KEY,true);  
            setResult(Activity.RESULT_OK, it);  
            finish();
        }
    }

	/**
	 * 
	 * @return 订单号
	 */
	private void parseIntent() {
		Intent intent = getIntent();
		if(intent != null){
		    type = intent.getIntExtra("type", TYPE_OPEN);
		    entryType = intent.getIntExtra(CinemaConstants.ENTRY_TYPE, -1);
		    isFromMyOrder= intent.getBooleanExtra("fromMyOrder", false);
		    switch (entryType) {
                case CinemaConstants.GENERATE_ORDER:
                    orderDetail = (DetailMovieOrder)intent.getSerializableExtra(CinemaConstants.MOVIE_ORDER_DETAIL);
                    orderSeat = (String)intent.getSerializableExtra(CinemaConstants.MOVIE_ORDER_SEAT);
                    language = intent.getStringExtra(CinemaConstants.MOVIE_ORDER_LANGUAGE);
                    edition = intent.getStringExtra(CinemaConstants.MOVIE_ORDER_EDITION);
                    movie_photo_url = intent.getStringExtra(CinemaConstants.MOVIE_PHOTO_URL);
                    mCinemaAddress = intent.getStringExtra(CinemaConstants.CINEMA_ADDRESS);
                    mCityName = intent.getStringExtra(CinemaConstants.CINEMA_CITY);
                    /*
                     * 获取电影票服务费，组装服务费显示字符串
                     * add by hyl 2015-1-7 start
                     */
                    int service_fee = intent.getIntExtra(CinemaConstants.MOVIE_ORDER_SERVICEFEE,0);
                    if(service_fee > 0){
                    	servicefee = getString(R.string.order_service_fee, service_fee);
                    }
                    //add by hyl 2015-1-7 end
                    initData();
                    break;
                case CinemaConstants.ORDER_DETAIL:
                	tradeno = intent.getStringExtra(FLAG_ORDER_CODE);
                	pt_tradeno = intent.getStringExtra(FLAG_PTORDER_CODE);
                	movie_photo_url = intent.getStringExtra(CinemaConstants.MOVIE_PHOTO_URL);
                	movieOrder = PTOrderCenter.getInstance().getOrderByOrderNumber(pt_tradeno);
        		    PTOrderCenter.getInstance().requestRefreshOrders(new RefreshOrderListener() {
        				@Override
        				public void refreshSuccess(boolean isDbChanged) {
        					
        					if (movieOrder != null) {
        						orderDetail = new Gson().fromJson(movieOrder.getExpand(), DetailMovieOrder.class);
        						
        						//add by hyl 2015-1-23 start 价格采用PTOrderBean中的价格
        						orderDetail.setAmount(movieOrder.getPrice());
        						
        						/*
        						 * 将订单中的优惠券信息 传入到订单详情bean中
        						 * add by hyl 2015-1-23 start
        						 */
        						if(!TextUtils.isEmpty(movieOrder.getCoupon_ids())){
        							orderDetail.setCoupon_ids(movieOrder.getCoupon_ids());
        						}
        						//add by hyl 2015-1-23 end
        						
        						movie_photo_url = orderDetail.getMovie_photo_url();
        						
        						orderDetail.pt_status = movieOrder.getStatus_code();//add by hyl 2015-1-5
        						mHandler.sendEmptyMessage(MSG_ORDER_FOUNDED);
        						//add ljq start 2015-1-23 加上错误页提示
                                layout_main.setVisibility(View.VISIBLE);
                                exception_layout.setVisibility(View.GONE);
                                //add ljq end 2015-1-23 加上错误页提示
							}else{
								mHandler.sendEmptyMessage(MSG_ORDER_NOT_EXIST);
								//add ljq start 2015-1-23 加上错误页提示
                                layout_main.setVisibility(View.GONE);
                                exception_layout.setVisibility(View.VISIBLE);
                                ((TextView)findViewById(R.id.exception_desc)).setText(R.string.order_not_exist);
                                //add ljq end 2015-1-23 加上错误页提示
							}
        				}
        				
        				@Override
        				public void refreshFailure(String msg) {
        					dismissLoadingDialog();
        					//add ljq start 2015-1-23 加上错误页提示
                            layout_main.setVisibility(View.GONE);
                            exception_layout.setVisibility(View.VISIBLE);
                            //add ljq end 2015-1-23 加上错误页提示
        				}
        			});
        		    
                default:
                    break;
            }
		}
	}
	
	private void initView() {
		
		movie_icon=(ImageView) findViewById(R.id.logo);
		play_time=(TextView) findViewById(R.id.tv_filmtime);
		tv_seat=(TextView) findViewById(R.id.tv_filmseat);
		tv_movie_status=(TextView)findViewById(R.id.payed);
		tv_price=(TextView) findViewById(R.id.money);
		
		tv_price.setVisibility(View.GONE);//add by hyl 2015-1-6 订单详情中隐藏卡片中的价格信息
		
		movie_name=(TextView)findViewById(R.id.tv_moviename);
		layout_payandcancel=(LinearLayout)findViewById(R.id.layout_payandcancel);
		tv_tradeno=(TextView) findViewById(R.id.tv_filmorder_serialnumber);
		tv_createtime=(TextView) findViewById(R.id.tv_filmorder_createtime);
		tv_phone = (TextView)findViewById(R.id.tv_filmorder_phone_no);
		et_phone = (EditText)findViewById(R.id.et_filmorder_phone_no);
		tv_reminder=(TextView)findViewById(R.id.tv_filmorder_reminder);
		btn_quickpay=  findViewById(R.id.btn_quickpay);
		btn_cancel= findViewById(R.id.order_movie_cancel);
		tr_price=(LinearLayout)findViewById(R.id.tr_filmorder_serialnumber);
		tr_createtime = (LinearLayout)findViewById(R.id.tr_filmorder_createtime);
		
		layout_main=findViewById(R.id.layout_main);
		order_price=(TextView)findViewById(R.id.tv_filmorder_price);
		
		layout_aboutmovie=(LinearLayout)findViewById(R.id.putao_view_order_aboutmovie);
		iv_movie_logo=(ImageView)layout_aboutmovie.findViewById(R.id.orderdetail_movie_logo);
		cinema_name=(TextView)layout_aboutmovie.findViewById(R.id.title);
		findViewById(R.id.back_layout).setOnClickListener(this);
		((TextView)findViewById(R.id.title)).setText(R.string.movie_order_message);
		
		tv_filmorder_seatInfo = (TextView)findViewById(R.id.tv_filmorder_seatInfo);
		
		mLayoutOrderStatus = findViewById(R.id.tr_filmorder_status);
		mLayoutServerProviderInfo = findViewById(R.id.server_provider_layout);
		mLayoutTaxiAndEntertainment = findViewById(R.id.layout_taxi_and_entertainment);
		mButtonGenerateOrder = (Button)findViewById(R.id.btn_quickpay_generate_order);
		mTextViewOrderStatus = (TextView)findViewById(R.id.tv_filmorder_status);
		mButtonTaxi = findViewById(R.id.btn_taxi);
		mButtonEntertainment = findViewById(R.id.btn_entertainment);
		mEditTextLayout = findViewById(R.id.edittext_layout);
		
		btn_quickpay.setOnClickListener(this);
		mButtonGenerateOrder.setOnClickListener(this);
		mButtonTaxi.setOnClickListener(this);
		mButtonEntertainment.setOnClickListener(this);
//		btn_tickethelp.setOnClickListener(this);
		btn_cancel.setOnClickListener(this);
		btn_quickpay.setOnClickListener(this);
		mEditTextLayout.setOnClickListener(this);
		//add ljq 2015_1_10 start 尝试自动填写手机号
		String autoPhoneNum = getAutoWritePhoneNum();
		et_phone.setText(autoPhoneNum);
		et_phone.setSelection(autoPhoneNum.length());
		//add ljq 2015_1_10 end 尝试自动填写手机号
		
        //add by ljq 2015-1-23 start 增加异常界面显示
        exception_layout = findViewById(R.id.network_exception_layout);
        exception_layout.setOnClickListener(this);
        //add by ljq 2015-1-23 end
		
	}
		/**
	 * 获取用户订票历史 无 则尝试获取本机号码
	 * @return
	 */
	private String getAutoWritePhoneNum(){
	    String phoneNum = "";
        List<HabitDataItem> dataItems = UserInfoUtil.getInstace().getHabitDataByContentType(MyCenterConstant.MY_NODE_MOVIE_DETAIL, MyCenterConstant.MOVIE_ORDER_PAY_NUM, false);
        if(dataItems != null && dataItems.size()>0){
            phoneNum = dataItems.get(0).getContent_data();
        }else{
            phoneNum = ContactsHubUtils.getPhoneNumber(this);
        }
        return phoneNum;
	}
	

	  @SuppressWarnings("rawtypes")
    public void onCategoryClick(int  categoryId) {
		  CategoryBean bean = ContactsAppUtils.getInstance().getDatabaseHelper()
		            .getYellowPageDBHelper().queryCategoryByCategoryId(categoryId);
	        if (bean == null) {
	            return;
	        }
	        String targetActivityName = bean.getTarget_activity();
	        if (TextUtils.isEmpty(targetActivityName)) {
	            targetActivityName = YellowUtil.DefCategoryActivity;
	        }

	        YellowParams params = null;
	        if (!TextUtils.isEmpty(bean.getTarget_params())) {
	            Gson gson = new Gson();
	            params = gson.fromJson(bean.getTarget_params(), YellowParams.class);
	        }
	        if (params == null) {
	            params = new YellowParams();
	        }

	        try {
	            // add ljq 2014_11_10 start 如果是Web型则做进入的优化
	            Intent intent = null;
	            Class cls = Class.forName(targetActivityName);
	            if (YellowPageH5Activity.class.isAssignableFrom(cls)) {
	                intent = new Intent(this, YellowPageJumpH5Activity.class);
	                intent.putExtra("targetActivityName", targetActivityName);
	            } else {
	                intent = new Intent(this, cls);
	            }
	            // add ljq 2014_11_10 end 如果是Web型则做进入的优化
	            params.setCategory_id(bean.getCategory_id());
	            params.setCategory_name(bean.getName());
	            params.setRemindCode(bean.getRemind_code());
	            params.setEntry_type(YellowParams.ENTRY_TYPE_HOME_PAGE);

	            if (TextUtils.isEmpty(params.getTitle())) {
	                String showName = ContactsHubUtils.getShowName(this, bean.getShow_name());
	                params.setTitle(showName);
	            }
	            intent.putExtra(YellowUtil.TargetIntentParams, params);

	            startActivity(intent);
	        } catch (ClassNotFoundException e) {
	            e.printStackTrace();
	        }
	    }
	    
	public  String getShowName(Context c, String name) {
        String showName = name;
        Locale locale = c.getResources().getConfiguration().locale;
        String[] names = name.split(";");
        for (String temp : names) {
            String[] ts = temp.split(":");
            if (locale.toString().equals(ts[0])) {
                showName = ts[1];
                break;
            }
        }
        return showName;
    }
	/**
	 * 取消订单需请求格瓦拉和后台
	 * @author peku
	 */
	private void cancelOrder() {
		//格瓦拉请求取消订单
//		pb_loading.setVisibility(View.VISIBLE);
		showLoadingDialog(true);
//		String cancelOrderUrl = CinemaApiUtil.getCancelOrderUrl(tradeno,orderDetail.getUkey());
//        Config.asynGetGewara(cancelOrderUrl,GewaApiReqMethod.CANCEL_OEDER,mHandler);
        // 请求后台取消订单
		SimpleRequestData reqData = new SimpleRequestData();
		reqData.setParam("order_no", pt_tradeno);
		PTHTTP.getInstance().asynPost(Config.ORDER.CANCEL_URL,reqData, new IResponse() {
			
			@Override
			public void onSuccess(String content) {
				//{"ret_code":"12109","msg":"订单状态不正确"}
				if(!TextUtils.isEmpty(content)){
					try {
						JSONObject jsonObject = new JSONObject(content);
						String ret_code = jsonObject.getString("ret_code");
						if("0000".equals(ret_code)){// 取消订单成功
							Toast.makeText(MovieOrderDetailsActivity.this,  R.string.putao_hotelorderdetail_cancelorder_result_success, 0).show();
							
							
							
							String cancelOrderUrl = CinemaApiUtil.getCancelOrderUrl(tradeno,orderDetail.getUkey());
					        Config.asynGetGewara(cancelOrderUrl,GewaApiReqMethod.CANCEL_OEDER,mHandler);
						}else{
							String msg = jsonObject.getString("msg");
							Toast.makeText(MovieOrderDetailsActivity.this, msg, 0).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			
			@Override
			public void onFail(int errorCode) {
				Toast.makeText(MovieOrderDetailsActivity.this, R.string.putao_hotelorderdetail_cancelorder_result_failed, 0).show();
			}
		});
        
	}
	
	private void ticketHelp() {
		String ticketHelpUrl = CinemaApiUtil.getTicketHelpUrl(orderDetail.getCinema_id());
//		String ticketHelpUrl = CinemaApiUtil.getTicketHelpUrl(140554367);
//		pb_loading.setVisibility(View.VISIBLE);
		showLoadingDialog(true);
        Config.asynGetGewara(ticketHelpUrl,GewaApiReqMethod.TICKET_HELP,mHandler);
	}
	
//	Intent intent=new Intent(MovieOrderDetailsActivity.this,MovieTicketHelpActivity.class);
//	intent.putExtra(TICKET_HELP_RESULT, (String)obj);
//	MovieOrderDetailsActivity.this.startActivity(intent);
	
	private void quickPay() {
		if(entryType == CinemaConstants.GENERATE_ORDER){
		    Log.d("ljq", "quickPay 1");
			createOrder();
		}else if(entryType == CinemaConstants.ORDER_DETAIL){
		    Log.d("ljq", "quickPay 2");
			if(!orderDetail.isTimeOut()){//订单未过期 进入支付
			    readyForPay(orderDetail);
			}
		}
	}
	
	private void createOrder()
	{
	    OrderInfoTask orderInfoTask = new OrderInfoTask();
        if (!isRunning) {
        	/*
        	 * 提交订单前判断网络状态
        	 * modified by hyl 2015-1-8 start
        	 */
        	if(!NetUtil.isNetworkAvailable(this)){
        		Toast.makeText(this, R.string.putao_no_net, Toast.LENGTH_SHORT).show();
        		return;
        	}else{
        	    
        	}
        	//modified by hyl 2015-1-8 end
//        	pb_loading.setVisibility(View.VISIBLE);
        	showLoadingDialog(true);
            PTUser user=PutaoAccount.getInstance().getPtUser();
            
            /*
             * 判断当账户未登录时，跳转到登录界面让用户先进行登录
             * modified by hyl 2015-1-14 start
             */
            if(user == null){
            	startActivity(new Intent(this,YellowPageLoginActivity.class));
            	return;
            }
            //modified by hyl 2015-1-14 end
            
            String url=CinemaApiUtil.getAddTicketOrderUrl(orderDetail.mp_id, et_phone.getText().toString().trim(), language, edition, orderSeat, user.pt_uid);
            orderInfoTask.execute(url);
        }
	}
    
	class OrderInfoTask extends AsyncTask<String, Void, DetailMovieOrder>{
        
        @Override
        protected DetailMovieOrder doInBackground(String... params) {
            DetailMovieOrder orderDetail=(DetailMovieOrder)CinemaApiUtilHelper.doHttpGetObjFromUrl(params[0], GewaApiReqMethod.TICKETORDER_ADD);
            isRunning=true;
            //传给后台一份订单信息
            if (orderDetail!=null) {
                if (TextUtils.isEmpty(orderDetail.error)&&!TextUtils.isEmpty(orderDetail.trade_no)) {
                    orderDetail.setMovie_photo_url(movie_photo_url);
                    NormalOrderRequestData requestData=new NormalOrderRequestData(null, orderDetail.getAmount(), NormalOrderRequestData.Product.cinema.getProductId(), NormalOrderRequestData.Product.cinema.getProductType(), orderDetail, DetailMovieOrder.class);

                    try {
                        String content = PTHTTP.getInstance().post(Config.NEW_TEST_SERVER+"/pay/order", requestData);
                        if (!TextUtils.isEmpty(content)) {
                            System.out.println(content);

                            JSONObject json=new JSONObject(content);
                            String ret_code=json.getString("ret_code");
                            if ("0000".equals(ret_code)) {
                                String data=json.getString("data");
                                orderDetail.order_no=data;
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return orderDetail;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(DetailMovieOrder orderDetail) {
//        	pb_loading.setVisibility(View.GONE);
        	dismissLoadingDialog();
            //进入确认支付页面
            if (orderDetail!=null) {
                if (!TextUtils.isEmpty(orderDetail.error)){
                    if (orderDetail.error.contains("mobile")) {
                        Toast.makeText(getApplicationContext(), getString(R.string.movie_order_mobile_error), 1).show();
                    }else {
                        Toast.makeText(getApplicationContext(), orderDetail.error, 1).show();
                    }
                }else if (!TextUtils.isEmpty(orderDetail.order_no)) {
                    readyForPay(orderDetail);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.movie_order_failure), 1).show();
                }
            }else {
                Toast.makeText(getApplicationContext(), getString(R.string.movie_order_failure), 1).show();
            }
            isRunning=false;
        }
    }

	/**
	 * 跳转支付界面
	 * @param orderDetail
	 */
    public void readyForPay(DetailMovieOrder orderDetail) {
        //add ljq start 保存用户习惯信息 2015-1-10
        HabitDataItem item = new HabitDataItem();
        item.setSource_type(MyCenterConstant.MY_NODE_MOVIE_DETAIL);
        //加上weg_type来分别是哪个类型的充值
        item.setContent_type(MyCenterConstant.MOVIE_ORDER_PAY_NUM);
        item.setContent_data(orderDetail.getMobile());
        UserInfoUtil.getInstace().saveHabitData(this, item);
        //add ljq end 保存用户习惯信息 2015-1-10
        
        
        Intent intent=new Intent(getApplicationContext(), CinemaPaymentActivity.class);
        intent.putExtra(CinemaConstants.MOVIE_ORDER_DETAIL, orderDetail);
        intent.putExtra("type", type);
        if(entryType == CinemaConstants.GENERATE_ORDER){
        	intent.putExtra(CinemaConstants.CINEMA_ADDRESS, mCinemaAddress);
        	intent.putExtra(CinemaConstants.CINEMA_CITY, mCityName);
        }
        startActivityForResult(intent,0);        
    }
    
    private void setProgressBarVisible(boolean isVisible)
    {
    	if(isVisible)
    	{
//    		pb_loading.setVisibility(View.VISIBLE);
    		layout_main.setVisibility(View.GONE);
    		showLoadingDialog(false);
    	}
    	else
    	{
//    		pb_loading.setVisibility(View.GONE);
    		layout_main.setVisibility(View.VISIBLE);
    		dismissLoadingDialog();
    	}
    }


	public int resultStr2Bool(String result) {
		if ("success".equals(result)) {
			return 1;
		} else {
			return 0;
		}
	}

	public void handleCancelOrder(String result) {
		if (gewalaResult == 1 && serverResult == 1) {
			Toast.makeText(MovieOrderDetailsActivity.this,  R.string.putao_hotelorderdetail_cancelorder_result_success, 0).show();
			String cancelOrderUrl = CinemaApiUtil.getCancelOrderUrl(tradeno,orderDetail.getUkey());
	        Config.asynGetGewara(cancelOrderUrl,GewaApiReqMethod.CANCEL_OEDER,mHandler);
		} else if (gewalaResult == 0 || serverResult == 0) {
			if (!isCancelFail) {
				Toast.makeText(MovieOrderDetailsActivity.this, result, 0)
						.show();
				isCancelFail = true;
			} else
				isCancelFail = false;
		}
//		pb_loading.setVisibility(View.GONE);
		dismissLoadingDialog();
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_layout:
			finish();
			break;
		case R.id.btn_quickpay_generate_order:
		case R.id.btn_quickpay:
			quickPay();

//             add xcx 2014-12-30 start 统计埋点
			if(isFromMyOrder){
			    MobclickAgentUtil.onEvent(
                        ContactsApp.getInstance().getApplicationContext(),
                        UMengEventIds.DISCOVER_YELLOWPAGE_MY_ORDER_CENTER_MOVIE_TICKET_IMMEDIATE_PAY);
			}else{
			    if (type == TYPE_OPEN) {
		               MobclickAgentUtil.onEvent(
		                       ContactsApp.getInstance().getApplicationContext(),
		                       UMengEventIds.DISCOVER_YELLOWPAGE_MOVIE_RELEASED_SEAT_IMMEDIATE_PAY);
		           } else {
		               MobclickAgentUtil.onEvent(
		                       ContactsApp.getInstance().getApplicationContext(),
		                       UMengEventIds.DISCOVER_YELLOWPAGE_MOVIE_UPCOMING_SEAT_IMMEDIATE_PAY);
		           }
			}
           MobclickAgentUtil.onEvent(
                   ContactsApp.getInstance().getApplicationContext(),
                   UMengEventIds.DISCOVER_YELLOWPAGE_MOVIE_ALL_IMMEDIATE_PAY);
           // add xcx 2014-12-30 end 统计埋点
			break;
		case R.id.indexofgetticket:
			ticketHelp();
			break;
		case R.id.order_movie_cancel:
			cancelOrder();
			break;
		case R.id.btn_taxi:
		    //MobclickAgentUtil.onEvent(this, UMengEventIds.CNT_NOTIFY_CARD_ITEM_CINEMA_TAXI);
		    onCategoryClick(19);
			break;
		case R.id.btn_entertainment:
		    //MobclickAgentUtil.onEvent(this, UMengEventIds.CNT_NOTIFY_CARD_ITEM_CINEMA_NEAR);
            onCategoryClick(5);
			break;
		case R.id.edittext_layout:
			//显示键盘
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
			imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS); 
			break;
	    //add ljq start 2015-1-23 加上错误页提示
        case R.id.network_exception_layout:
            if (NetUtil.isNetworkAvailable(this)) {
                exception_layout.setVisibility(View.GONE);
                parseIntent();
            }
            break;
        //add ljq end 2015-1-23 加上错误页提示
		default:
			break;
		}
	}

    @Override
    public void refreshSuccess(boolean isDbChanged) {
        PTOrderBean movieOrder = PTOrderCenter.getInstance().getOrderByOrderNumber(pt_tradeno);
        if (movieOrder != null) {
            orderDetail = new Gson().fromJson(movieOrder.getExpand(), DetailMovieOrder.class);
            movie_photo_url = orderDetail.getMovie_photo_url();
            
            orderDetail.pt_status = movieOrder.getStatus_code();//add by hyl 2015-1-5
            initData();
            
        }else{
            Toast.makeText(MovieOrderDetailsActivity.this, R.string.order_not_exist, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void refreshFailure(String msg) {
        dismissLoadingDialog();
    }
	
	
}
