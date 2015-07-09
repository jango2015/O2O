
package so.contacts.hub.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpEntity;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.db.DatabaseHelper;
import so.contacts.hub.http.Http;
import so.contacts.hub.thirdparty.cinema.bean.CinemaDetail;
import so.contacts.hub.thirdparty.cinema.bean.CinemaMovieDetail;
import so.contacts.hub.thirdparty.cinema.utils.GewaApiReqMethod;
import so.contacts.hub.thirdparty.cinema.xmlparse.CinemaXMLParser;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.widget.ProgressDialog;
import so.putao.findplug.LBSServiceGaode;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.google.gson.Gson;
import com.mdroid.core.http.IgnitedHttpResponse;
import com.mdroid.core.util.SystemUtil;
import com.yulong.android.contacts.discover.R;

public class Config {
    private static final String TAG = Config.class.getSimpleName();

	// 信鸽调试开关，发布时设置为false
	public static boolean XGDebug = false;
	// 极光调试开关, 发布时设置为false
	public static boolean JPDebug = false;
	
	// 日志输出到logcat开关，发布时设置为false
	public static boolean LOGCAT_DEBUG = true;
	

    public static boolean UMENG_DEBUG = false;

    /*
     * 控制 权限 提醒 开关 add by hyl 2014-6-13
     */
    public static final boolean IS_SHOW_PERMISSION_ALERT = true;

    public static final String TEST_SERVER = "http://192.168.1.63:8080/PT_SERVER/interface.s";

    public static final String NEW_TEST_SERVER = "http://pay.putao.so";

    public static final String SERVER = "http://android1.putao.so/PT_SERVER/interface.s";

    // 订单上报接口
    public static final String ORDER_UPLOAD_URL = "http://pay.putao.so";

    // "http://android1.putao.so/PT_SERVER/interface.s";
    public static final String ACTION_HTTP_REQUEST = "so.putao.community.httprequest";
    
    public static final String YELLOW_PAGE_FEEDBACK_URL = "http://feedback.putao.so/feedback/suggest";//用户反馈url
    
    public interface ORDER {
        public static final String CANCEL_URL = "http://pay.putao.so/pay/order/cancel";        
    };
    
    /**
     * 支付相关URL配置
     * @author change
     *
     */
    public interface PAY {
        public static final String ALIPAY_CREATE_ORDER_URL = "http://pay.putao.so/pay/order/alipay";
        
        public static final String WECHAT_CREATE_ORDER_URL = "http://pay.putao.so/pay/order/wx";
        
    };
    
    /**
     * 充值相关URL配置
     * @author change
     *
     */
    public interface CHARGE {
        /** 后台接口 start [ android1.putao.so - 192.168.1.73:8080  - 42.120.51.89:8080 ]*/
        // 充值询价host地址
        public static final String Serv_Get_Mobile_Price_Url = "http://android1.putao.so/PT_SERVER/query_mobile_price_new.s";
        
        // 充值生成订单地址
        public static final String SERV_GET_ORDER_URL_ALIPAY = "http://android1.putao.so/PT_SERVER/create_order_no.s";//支付宝
        public static final String SERV_GET_ORDER_URL_WECHAT = "http://android1.putao.so/PT_SERVER/create_order_no_wx.s";//微信支付
        
        public static final String Serv_Get_Traffic_Info_Url = "http://android1.putao.so/PT_SERVER/AskPhoneFlowRequest.s";
        public static final String Serv_Get_Traffic_Price_Url = "http://android1.putao.so/PT_SERVER/create_order_no.s";
        public static final String Serv_Get_Ttaffic_Order_Url = "http://android1.putao.so/PT_SERVER/CreatePhoneTrafficOrder.s";
        
        //add by ffh for test
        public static final String Serv_get_traffic_product_url ="http://biz.putao.so/traffic/query_traffic_product";

        //add by xcx 
        public static final String Serv_get_charge_telephone_product_url ="http://biz.putao.so/phonefee/query_phone_fee_product";
    //  public static final String Serv_get_charge_telephone_product_url ="http://192.168.1.59:8080/biz.war/phonefee/query_phone_fee_product";
        
        // add by cj
        public static final String QUERY_ORDER_URL = "http://pay.putao.so/pay/order/detail";
        
    };

    /**
     * 火车票相关URL配置
     * @author change
     *
     */
    public interface TC_TRAIN {
        public static final String QUERY_TRAIN_TICKET = "http://192.168.1.124:8080/biz.war/tongcheng/query_train_ticket";
        
        public static final String HOST_TRAIN = "http://m.ly.com/pub/train"; 
        
    };
    
    /**
     * 酒店相关URL配置
     * @author change
     *
     */
    public interface TC_HOTEL {
        
        // 酒店搜索URL
        public static String TC_URL_SEARCH_HOTEL = "http://tcopenapi.17usoft.com/handlers/hotel/QueryHandler.ashx";
        
        // 订单搜索URL
        public static String TC_URL_SEARCH_ORDER = "http://tcopenapi.17usoft.com/handlers/hotel/orderhandler.ashx";
        
        // 区域查找URL
        public static String TC_URL_SEARCH_DIVISION = "http://tcopenapi.17usoft.com/Handlers/General/AdministrativeDivisionsHandler.ashx";

        //add xcx 2014-12-26 酒店搜索替换为同程
        
        // 酒店订单详情打车跳转到快滴页面
        public static final String TC_KUAIDIACTIVITY = "so.contacts.hub.ui.web.YellowPageKuaidiActivity";
        
        // 订单详情打车URL
        public static String TC_URL_KUAIDI = "http://api.kuaidadi.com:9898/taxi/h5/index.htm?source=putaoxinxi&key=yruwioqpkdlmncvfw2ejd&order2pay=true&orderHis=true&home400=false";
    };

    
    /**
     * 消息上报相关URL配置
     * @author change
     *
     */
    public interface MSG_REPORT {
        public static final String REPORT_URL = "http://pay.putao.so/pay/order/adapter";
        
        public static final String TRAIN_REPORT_URL = "http://pay.putao.so/pay/order/adapter?product_type=9&info={";
    //  public static final String PECCANCY_REPORT_URL = "http://42.121.98.207:9280/msgremind/insert_illeagl_car";
        public static final String PECCANCY_REPORT_URL = "http://biz.putao.so/msgremind/insert_user_car";//本地测试;
        public static final String QUERY_CARINFO_URL = "http://biz.putao.so/msgremind/query_user_car";//本地测试;
        public static final String DEL_CARINFO_URL = "http://biz.putao.so/msgremind/delete_user_car";//本地测试;
        public static final String DEL_MUL_CARINFO_URL = "http://biz.putao.so/msgremind/delete_user_cars";//本地测试;
        public static final String UPDATE_CARINFO_URL = "http://biz.putao.so/msgremind/update_user_car";//本地测试;

        public static final String EXPRESS_REPORT = "http://biz.putao.so/express/report?express_order_no=";// 快递查询成功上报

        public static final String ORDER_LIST = "http://pay.putao.so/pay/order/list";// 获取订单列表

        public static final String NOT_ORDER_LIST = "http://biz.putao.so/daylife/order/list";// 获取订单列表
        
    };
    
    /**
     * 搜索相关URL配置
     * @author change
     *
     */
    public interface SEARCH {
        public static final String SEARCH_SOLTION_URL = "http://search.putao.so/yellowpage/_search?query=";  
    };
    
    /**
     * 水电煤相关URL配置
     * @author change
     *
     */
    public interface WEG {
        public static final String SERV_GET_WEG_ORDER_URL = "http://wec.pay.putao.so/PT_SERVER/query_order_wec.s?";
        // 充值询价host地址
        public static final String SERV_GET_WEG_PRICE_URL = "http://wec.pay.putao.so/PT_SERVER/query_wec_bills.s?";
        //微信订单接口
        public static final String SERV_GET_ORDER_URL_WECHAT = "http://wec.pay.putao.so/PT_SERVER/create_order_no_wx_wec.s";//微信支付
        public static final String SERV_GET_ORDER_URL_ALIPAY = "http://wec.pay.putao.so/PT_SERVER/create_order_no_wec.s";
        
    };
    

    /**
     * 电影相关URL配置
     * @author change
     *
     */
    public interface MOVIE {
        public static final String TEST_HOST_DOMAIN = "http://test.gewala.net/openapi2/router/rest";

        public static final String DEVELOP_HOST_DOMAIN = "http://openapi.gewara.com/router/rest";
        
    };
    
    /**
     * 艺龙相关URL配置
     * @author change
     *
     */
    public interface ELONG {
        public static final String serverHost = "http://android1.putao.so/PT_SERVER/el_face.s";
        
        public static final String SearchHotelCity = "http://m.elong.com/Hotel/SearchHotelCityByEnName?enname=";
        
        public static final String BaseUrl = "http://m.elong.com/Hotel/Detail?ref=putao&hotelid=";

    }
    
    /**
     * 违章相关URL配置
     * @author change
     *
     */
    public interface TRAFFIC_OFENCE {
        public static final String QUERY_USER_CAR_URL = "http://biz.putao.so/msgremind/query_user_car_illegal_info";
        
        
    }


    private static long HEART_BEAT_DELAY = 6 * 60 * 60 * 1000; // 心跳周期
    // private static long HEART_BEAT_DELAY = 60 * 1000; // 心跳周期

    // 默认CATEGORY小图标
    public final static String DEFAULT_CATEGORY_IMAGE_SMALL = "icon_quick_replace_s";

    // 默认CATEGORY大图标
    public final static String DEFAULT_CATEGORY_IMAGE = "icon_quick_replace";

    // 默认CATEGORY大图标按下状态
    public final static String DEFAULT_CATEGORY_IMAGE_DEEP = "icon_quick_replace_d";

    public static final String PREFERENCES = "preferences";

    public static final String KEY = "233&*Adc^%$$per";

    public static int STATE = 1;// 当前程序状态: 1:前台 0:后台

    private static Http mHttp;

    public static Gson mGson = new Gson();

    private static DatabaseHelper mDatabaseHelper;

    /**
     * 声明黄页账户对象,黄页配置 add by hyl 2014-9-19
     */
    // private static PTUser ptUser = null;
    public static final String YELLOW_USER = "pt_preferences";

    // add by hyl 2014-9-19

    /** 回调接口 **/
    public interface CallBack {
        void onSuccess(String o);

        void onFail(String msg);

        void onFinish(Object obj);
    }
    
    // 取得http对象
    public static Http getApiHttp() {
        if (mHttp == null) {
            mHttp = new Http(ContactsApp.getInstance());
        } else {
            mHttp.initDefaultHeaderToken();
        }
        return mHttp;
    }

    public static String getUserAgent() {
        return "Android/mDroid";
    }

    /**
     * 启动应用分组获取动态线程数至少有三个，一般有6,7个，因此增开到10个线程数，执行其他任务，防止线程堵车，请求等待过长时间
     */
    private static final int CORE_POOL_SIZE = 10;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "MDroid thread #" + mCount.getAndIncrement());
        }
    };

    private static ExecutorService mExecutorService;

    public static synchronized ExecutorService getExecutor() {
        if (mExecutorService == null) {
            mExecutorService = Executors.newFixedThreadPool(CORE_POOL_SIZE, sThreadFactory);
        }
        return mExecutorService;
    }

    public static DatabaseHelper getDatabaseHelper() {
        if (mDatabaseHelper == null) {
            mDatabaseHelper = DatabaseHelper.getInstance(ContactsApp.getInstance());
        }
        return mDatabaseHelper;
    }

    /**
     * 设置心跳间隔时间
     * 
     * @param beat_time 心跳间隔时间
     */
    public static void setHeartBeat(long beat_time) {
        if (beat_time != 0) {
            SharedPreferences preferences = ContactsApp.getInstance().getSharedPreferences(
                    ConstantsParameter.CONTACTS_SETTING/*
                                                        * modify by putao_lhq
                                                        * PREFERENCES
                                                        */, Context.MODE_MULTI_PROCESS);

            Config.setHeartBeatDelayConfig(preferences, beat_time);
        }
    }

    /**
     * 重新设置心跳间隔时间
     * 
     * @param preferences
     * @param active_s
     */
    public static void setHeartBeatDelayConfig(SharedPreferences preferences, long active_s) {
        if (null != preferences) {
            if (0 != active_s) {
                preferences.edit().putLong(ConstantsParameter.HEART_DELAY_CONFIG, active_s)
                        .commit();
            }
        }
    }

    public static void setHeartBeatDelay(SharedPreferences preferences) {
        if (null != preferences) {
            long heart_beat_delay_config = preferences.getLong(
                    ConstantsParameter.HEART_DELAY_CONFIG, HEART_BEAT_DELAY);
            long now = System.currentTimeMillis();
            long next_heart_beat_time = heart_beat_delay_config + now;
            if (0 < next_heart_beat_time) {
                preferences.edit()
                        .putLong(ConstantsParameter.NEXT_HEART_BEAT_TIME, next_heart_beat_time)
                        .commit();
            }
        }
    }

    public static long getHeartBeatDelayConfig(SharedPreferences preferences) {
        if (null != preferences) {
            return preferences.getLong(ConstantsParameter.HEART_DELAY_CONFIG, HEART_BEAT_DELAY);
        } else {
            return HEART_BEAT_DELAY;
        }
    }

    /** 退出登录 **/
    public static void logout() {
    }

    public static void execute(Runnable r) {
        Config.getExecutor().execute(r);
    }

    /** post提交请求 **/
    public static void asynPost(final HttpEntity entity, final CallBack callBack) {
        asynPost(null, null, entity, callBack);
    }

    /** 文件缓存在SD卡的位置 **/
    public String getRootCache() {
        return "/mnt/sdcard/contactshub";
    }

    /** post提交请求 **/
    public static void asynPost(final Activity context, String msg, final HttpEntity entity,
            final CallBack callBack) {

        if (context != null && !SystemUtil.contactNet(context)) {
            callBack.onFail(context.getResources().getString(R.string.putao_no_net));// context.getResources().getString(R.string.putao_no_net));
            return;
        }
        // sendHttpRequestBroadcast();
        ProgressDialog tDialog = null;
        if (context != null) {
            try {
                tDialog = ProgressDialog.show(context, msg);
                tDialog.setCancelable(true);
                tDialog.setCanceledOnTouchOutside(false);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        final ProgressDialog dialog = tDialog;
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                if (dialog != null && dialog.getWindow() != null && dialog.isShowing()) {
                    try {
                        dialog.cancel();
                    } catch (Exception e) {
                        // TODO: handle exception
                    }

                }
                if (callBack != null) {
                    if (msg.what == 0) {
                        callBack.onSuccess((String)msg.obj);
                    } else {
                        callBack.onFail((String)msg.obj);
                    }
                }
            }
        };
        execute(new Runnable() {
            @Override
            public void run() {
                try {
                    IgnitedHttpResponse httpResponse = getApiHttp().post(SERVER, entity).send();
                    String content = httpResponse.getResponseBodyAsString();
                    if (!TextUtils.isEmpty(content)) {
                        Message msg = handler.obtainMessage(0);
                        msg.obj = content;
                        handler.sendMessage(msg);
                    } else {
                        Message msg = handler.obtainMessage(1);
                        msg.obj = ContactsApp.getInstance().getResources()
                                .getString(R.string.putao_net_unuseable);// "网络链接不可用";
                        handler.sendMessage(msg);
                    }
                } catch (ConnectException e) {
                    Message msg = handler.obtainMessage(1);
                    msg.obj = ContactsApp.getInstance().getResources()
                            .getString(R.string.putao_net_unuseable);// "网络链接不可用";
                    handler.sendMessage(msg);

                } catch (IOException e) {

                    Message msg = handler.obtainMessage(2);
                    msg.obj = "Read stram error.";
                    handler.sendMessage(msg);
                }
            }
        });
    }

    /** get提交请求 **/
    public static void asynGet(Context context, String msg, final String url,
            final CallBack callBack) {
        asynGet(context, msg, url, false, callBack);
    }

    /** get提交请求 **/
    public static void asynGet(final String url, final boolean cache, final CallBack callBack) {
        asynGet(null, null, url, cache, callBack);
    }

    /** get提交请求 **/
    public static void asynGet(Context context, String msg, final String url, final boolean cache,
            final CallBack callBack) {
        // ProgressDialog tDialog = null;
        if (context != null) {
            // try {
            // tDialog = ProgressDialog.show(context, msg);
            // } catch (Exception e) {
            // e.printStackTrace();
            // }

        }
        // final ProgressDialog dialog = tDialog;
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // if (dialog != null && dialog.isShowing()) {
                // dialog.dismiss();
                // }
                if (callBack != null) {
                    if (msg.what == 0) {
                        callBack.onSuccess((String)msg.obj);
                    } else {
                        callBack.onFail((String)msg.obj);
                    }
                }
            }
        };
        execute(new Runnable() {
            @Override
            public void run() {
                try {
                    IgnitedHttpResponse httpResponse = getApiHttp().get(url, cache).send();
                    String content = httpResponse.getResponseBodyAsString();
                    Message msg = handler.obtainMessage(0);
                    msg.obj = content;
                    handler.sendMessage(msg);
                } catch (ConnectException e) {
                    Message msg = handler.obtainMessage(1);
                    msg.obj = ContactsApp.getInstance().getResources()
                            .getString(R.string.putao_net_unuseable);// "网络链接不可用";
                    handler.sendMessage(msg);
                } catch (IOException e) {
                    Message msg = handler.obtainMessage(2);
                    msg.obj = "Read stram error.";
                    handler.sendMessage(msg);
                }
            }
        });
    }

    /** post提交请求 **/
    public static void asynPost(Context context, String msg, final String url,
            final HttpEntity entity, final CallBack callBack) {
        // ProgressDialog d = null;
        if (context != null) {
            // d = ProgressDialog.show(context, msg);
        }
        // final ProgressDialog dialog = d;
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // if (dialog != null && dialog.isShowing()) {
                // dialog.dismiss();
                // }
                if (callBack != null) {
                    if (msg.what == 0) {
                        callBack.onSuccess((String)msg.obj);
                    } else {
                        callBack.onFail((String)msg.obj);
                    }
                }
            }
        };
        execute(new Runnable() {
            @Override
            public void run() {
                try {
                    IgnitedHttpResponse httpResponse = getApiHttp().post(url, entity).send();
                    String content = httpResponse.getResponseBodyAsString();
                    Message msg = handler.obtainMessage(0);
                    msg.obj = content;
                    handler.sendMessage(msg);
                } catch (ConnectException e) {
                    Message msg = handler.obtainMessage(1);
                    msg.obj = ContactsApp.getInstance().getResources()
                            .getString(R.string.putao_net_unuseable);// "网络链接不可用";
                    handler.sendMessage(msg);
                } catch (IOException e) {
                    Message msg = handler.obtainMessage(2);
                    msg.obj = "Read stram error.";
                    handler.sendMessage(msg);
                }
            }
        });

    }

    /**
     * 格瓦拉请求接口
     * @author ffh
     * @param url
     * @param method
     * @param handler
     */
    public static void asynGetGewara(final String url,final GewaApiReqMethod method, final Handler handler) {
        execute(new Runnable() {
            @Override
            public void run() {
                try {
                    LogUtil.d(TAG, url);
                    IgnitedHttpResponse httpResponse = getApiHttp().get(url, false).send();
                    String content = httpResponse.getResponseBodyAsString();
                    InputStream in = new ByteArrayInputStream(content.getBytes("UTF-8"));
                    Object obj = CinemaXMLParser.parseXML(in, method);
                    in.close();
                    
                    String date = "";
                    /*
                     * add by hyl 2015-1-5 start
                     */
                    if(method.equals(GewaApiReqMethod.OPEN_CINEMA_LIST_BY_PLAYDATE)){
                        obj = calculateLineDistance(obj);
                    }
                    //add by hyl 2015-1-5 end
                    //add by ljq 2015-1-14 start 地获取日期字符串用于返回
                    if(method.equals(GewaApiReqMethod.OPI_LIST)){
                        date = getRequestDateStr(url);
                    }
                    //add by ljq 2015-1-14 end
                    Message msg = handler.obtainMessage(0);
                    msg.obj = obj;
                    Bundle data = new Bundle();
                    data.putString("method", method.toString());
                    data.putString("date",date);
                    msg.setData(data);
                    handler.sendMessage(msg);
                    LogUtil.d(TAG, "success");
                } catch (ConnectException e) {
                    Message msg = handler.obtainMessage(1);
                    msg.obj = ContactsApp.getInstance().getResources()
                            .getString(R.string.putao_no_net);
                    handler.sendMessage(msg);
                    LogUtil.d(TAG, "fail1");
                } catch (IOException e) {
                    Message msg = handler.obtainMessage(2);
                    msg.obj = ContactsApp.getInstance().getResources()
                            .getString(R.string.putao_server_busy);
                    handler.sendMessage(msg);
                    LogUtil.d(TAG, "fail2");
                } catch (Exception e) {
                    e.printStackTrace();
                    Message msg = handler.obtainMessage(2);
                    msg.obj = ContactsApp.getInstance().getResources()
                            .getString(R.string.putao_no_net);
                    handler.sendMessage(msg);
                    LogUtil.d(TAG, "fail3");
                }
            }
        });
    }
    
    /**
     * 获取格瓦拉请求里的日期字符串  作为返回参数适用  add ljq 2014/01/14
     * example : http://openapi.gewara.com/router/rest?appkey=putao&cinemaid=38087751&format=xml&method=com.gewara.partner.movie.opiList&movieid=159639640&playdate=2015-01-14&sign=A3B516ACC728CF60BA8FF6C7C622B3E1&signmethod=MD5&timestamp=2015-01-14+16%3A17%3A12&v=1.0
     * @return 日期字符串 
     */
    private static String getRequestDateStr(String url){
        String date = "";
        if(!TextUtils.isEmpty(url)){
            if(url.contains("&")){
                String[] temp1 = url.split("&");
                for (int i = 0; i < temp1.length; i++) {
                    if(temp1[i].contains("playdate=")){
                        String[] temp2 =  temp1[i].split("=");
                        if(temp2.length == 2 && !TextUtils.isEmpty(temp2[1])){
                            date = temp2[1];
                        }
                    }
                }
            }
        }
        return date;
    }
    
    public static List<CinemaDetail> calculateLineDistance(Object obj){
    	List<CinemaDetail> cinemaList = (List<CinemaDetail>)obj;
    	if(cinemaList != null){
    		CoordinateConverter converter = new CoordinateConverter();// 坐标转换工具
    		converter.from(CoordinateConverter.CoordType.COMMON);
    		converter.coord(new LatLng(LBSServiceGaode.getLatitude(), LBSServiceGaode.getLongitude()));
    		LatLng latLng = converter.convert();
    		
    		for (CinemaDetail cinemaDetail : cinemaList) {
    	        
    	        double destLongitude = 0, destLatitude = 0;
    	        if (!TextUtils.isEmpty(cinemaDetail.getBpointx()) && !TextUtils.isEmpty(cinemaDetail.getBpointy())) {
    	            destLongitude = Double.parseDouble(cinemaDetail.getBpointx());
    	            destLatitude = Double.parseDouble(cinemaDetail.getBpointy());
    	        }
    	        
    	        if(destLatitude != 0 && destLongitude != 0){
    	        	LatLng cinemaLatlng = new LatLng(destLatitude, destLongitude);
    	        	double distance = com.baidu.mapapi.utils.DistanceUtil.getDistance(latLng, cinemaLatlng);
    	        	cinemaDetail.setDistance(distance);
    	        }
			}
    		
    		List<CinemaDetail> sorts = new ArrayList<CinemaDetail>();
    		sorts.addAll(cinemaList);
    		Collections.sort(sorts,new Comparator<CinemaDetail>() {
				@Override
				public int compare(CinemaDetail arg0, CinemaDetail arg1) {
					if(arg0.getDistance() > arg1.getDistance()){
			            return 1;
			        }else{
			            return -1;
			        }
				}
			});
    		
//    		for (CinemaDetail cinemaDetail : sorts) {
//				LogUtil.d("cinema", "distance:"+cinemaDetail.getDistance());
//			}
    		
    		return sorts;
    	}
    	return cinemaList;
    }
}
