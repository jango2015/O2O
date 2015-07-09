
package so.contacts.hub.thirdparty.cinema.ui;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.remind.BaseRemindActivity;
import so.contacts.hub.thirdparty.cinema.CinemaConstants;
import so.contacts.hub.thirdparty.cinema.bean.CinemaRoomInfo;
import so.contacts.hub.thirdparty.cinema.bean.CinemaRoomInfo.SeatRow;
import so.contacts.hub.thirdparty.cinema.bean.DetailMovieOrder;
import so.contacts.hub.thirdparty.cinema.bean.Seat;
import so.contacts.hub.thirdparty.cinema.bean.SeatInfo;
import so.contacts.hub.thirdparty.cinema.tool.CinemaApiUtil;
import so.contacts.hub.thirdparty.cinema.tool.CinemaApiUtilHelper;
import so.contacts.hub.thirdparty.cinema.utils.GewaApiReqMethod;
import so.contacts.hub.thirdparty.cinema.widget.OnSeatClickListener;
import so.contacts.hub.thirdparty.cinema.widget.SelectSeatThumView;
import so.contacts.hub.thirdparty.cinema.widget.SelectSeatView;
import so.contacts.hub.util.CalendarUtil;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.NetUtil;
import so.contacts.hub.util.UMengEventIds;
import so.contacts.hub.widget.CommonDialog;
import so.contacts.hub.widget.CommonDialogFactory;
import so.contacts.hub.widget.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yulong.android.contacts.discover.R;


/**
 * 我的订单页
 * 
 * @author zj 2014-12-18 14:30:33
 */
public class CinemaSelectSeatActivity extends BaseRemindActivity implements OnClickListener,OnSeatClickListener {
    public static final int TYPE_OPEN = 1;

    public static final int TYPE_COMING = 2;
    
    public static final String TAG = "CinemaSelectSeatActivity";

    private int type = TYPE_OPEN;

    private static final int DISMISS_THUMBNAIL = 1;

    private SelectSeatView select_seat;

    private SelectSeatThumView select_seat_small;

    private ArrayList<SeatInfo> seatInfoList;

    private LinearLayout film_seat;

    private TextView film_confirm;
    
    private CommonDialog commonDialog;

    private HashMap<String, TextView> selectedSeats;
    
    private LinearLayout mNoDataLayout;
    
    private LinearLayout mMainLayout;
    
    private static final String error_info_1 = "更新影院排期座位图状态错误";
    
    private static final String error_info_2 = "本场次已关闭订票" ;
    
    private String mCinemaAddress;
    
    private String mCityName;
    

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case DISMISS_THUMBNAIL:
                    select_seat_small.setVisibility(View.INVISIBLE);
                    break;

                default:
                    break;
            }

        };
    };

    private long mpid;

    private CinemaRoomInfo seatInfo;

    private SeatInfoTask seatInfoTask;

    private String movie_photo_url;

    private ProgressDialog mProgressDialog;

    private String url;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.putao_cinema_select_seat_activity);
        findViewById(R.id.back_layout).setOnClickListener(this);//add by hyl 2015-1-5
        
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.putao_yellow_page_loading));
        
        
        
        mMainLayout = (LinearLayout)findViewById(R.id.main_layout);
        mNoDataLayout = (LinearLayout)findViewById(R.id.network_exception_layout);
        mNoDataLayout.setOnClickListener(this);
        parseIntent();
        mProgressDialog.show();
        if (mpid != -1) {
            if(NetUtil.isNetworkAvailable(this)){
                mMainLayout.setVisibility(View.VISIBLE);
                mNoDataLayout.setVisibility(View.GONE);
                initData();
            }else{
                mProgressDialog.dismiss();
                mMainLayout.setVisibility(View.GONE);
                mNoDataLayout.setVisibility(View.VISIBLE);
            }
        } else {
            // 数据异常
            mProgressDialog.dismiss();
            mMainLayout.setVisibility(View.GONE);
            mNoDataLayout.setVisibility(View.VISIBLE);
        }
       
        

    }

    private void parseIntent() {
        if (getIntent() != null) {
            Intent intent = getIntent();
            mpid = intent.getLongExtra(CinemaConstants.MPID, -1);
            String cinemaName = intent.getStringExtra(CinemaConstants.CINEMA_NAME);
            movie_photo_url = intent.getStringExtra(CinemaConstants.MOVIE_PHOTO_URL);
            setTitle(cinemaName);
            mCinemaAddress = intent.getStringExtra(CinemaConstants.CINEMA_ADDRESS);
            mCityName = intent.getStringExtra(CinemaConstants.CINEMA_CITY);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initData() {
        seatInfoTask = new SeatInfoTask();
        url = CinemaApiUtil.getOPISeatInfoUrl(mpid);
        seatInfoTask.execute(url);
        selectedSeats = new HashMap<String, TextView>();

    }

    private void initSelectSeatView() {
        // 初始化页面内容
        setTitle(seatInfo.cinemaname);

        film_seat = (LinearLayout)findViewById(R.id.film_seat);
        film_confirm = (TextView)findViewById(R.id.film_confirm);
        film_confirm.setOnClickListener(this);

        TextView film_name = (TextView)findViewById(R.id.film_name);
        film_name.setText(seatInfo.moviename);

        TextView film_type = (TextView)findViewById(R.id.film_type);
        if (!TextUtils.isEmpty(seatInfo.edition)) {
            film_type.setVisibility(View.VISIBLE);
            film_type.setText(seatInfo.edition);
        }

        TextView film_time = (TextView)findViewById(R.id.film_time);
        Timestamp time = Timestamp.valueOf(seatInfo.playtime);
        String date = CalendarUtil.getDateStrFromLong(time.getTime(),
                getString(R.string.putao_select_seat_date_format_pattern));
        film_time.setText(date);

        TextView screen = (TextView)findViewById(R.id.screen);
        screen.setText(getString(R.string.putao_select_seat_room_screen, seatInfo.roomname));

        refreshHint();
        // 初始化选座控件
        select_seat = (SelectSeatView)this.findViewById(R.id.select_seat);
        select_seat_small = (SelectSeatThumView)this.findViewById(R.id.select_seat_small);
        select_seat.setMaxSeats(seatInfo.maxseat);

        select_seat.init(seatInfoList.get(0).getSeatList().size(), seatInfoList.size(),
                seatInfoList, select_seat_small, 5);
        select_seat.setOnSeatClickListener(this);
        
        //初始化选座界面中线在最中间
        SelectSeatView.i(select_seat, (SelectSeatView.s(select_seat) - select_seat.getMeasuredWidth())/2);
        SelectSeatView.a(select_seat, ((float)(select_seat.getMeasuredWidth() - SelectSeatView.s(select_seat))/2));
    }

    protected void removeSeat(String key) {
        film_seat.removeView(selectedSeats.get(key));
        selectedSeats.remove(key);
        if (selectedSeats.size() == 0 && seatInfo != null) {
            TextView tv = new TextView(getApplicationContext());
//            tv.setBackgroundResource(R.drawable.putao_seat_xuanzuo);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimensionPixelSize(R.dimen.putao_text_tertiary_size));
            tv.setTextColor(getResources().getColor(R.color.putao_text_color_second));
            tv.setText(getString(R.string.putao_select_seat_no_seat, seatInfo.maxseat));
            tv.setGravity(Gravity.CENTER);

            film_seat.addView(tv);
        }
    }

    protected void selectSeat(String key, String desc) {
        if (selectedSeats.size() == 0) {
            film_seat.removeAllViews();
        }
        TextView tv = new TextView(getApplicationContext());
        tv.setBackgroundResource(R.drawable.putao_seat_xuanzuo);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimensionPixelSize(R.dimen.putao_text_tertiary_size));
        tv.setTextColor(getResources().getColor(R.color.putao_text_color_importance));
        tv.setText(desc);
        tv.setGravity(Gravity.CENTER);

        film_seat.addView(tv);

        selectedSeats.put(key, tv);
    }

    protected void refreshHint() {
    	if(film_confirm != null && seatInfo != null){
    		film_confirm.setVisibility(View.VISIBLE);
    		/* 
    		 * 不需要再计算服务费(seatInfo.servicefee),gewaprice已经包含了服务费
    		 * modified by hyl 2015-1-8 start
    		 * old code:
            film_confirm.setText(getString(R.string.putao_select_seat_confirm, selectedSeats.size()
    		 * (seatInfo.gewaprice + seatInfo.servicefee)));
    		 */
    		film_confirm.setText(getString(R.string.putao_select_seat_confirm, selectedSeats.size()
    				* seatInfo.gewaprice));
    		//modified by hyl 2015-1-8 end
    	}
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
	        case R.id.back_layout://add by hyl 2015-1-5
	        	finish();
	        	break;
            case R.id.film_confirm:// 跳转确认生成订单页
                if (selectedSeats.size() > 0) {
                    jumpToCreateOrder();

                    // add xcx 2014-12-30 start 统计埋点
                    if (type == TYPE_OPEN) {
                        MobclickAgentUtil.onEvent(
                                ContactsApp.getInstance().getApplicationContext(),
                                UMengEventIds.DISCOVER_YELLOWPAGE_MOVIE_RELEASED_SEAT_CONFIRM);
                    } else {
                        MobclickAgentUtil.onEvent(
                                ContactsApp.getInstance().getApplicationContext(),
                                UMengEventIds.DISCOVER_YELLOWPAGE_MOVIE_UPCOMING_SEAT_CONFIRM);
                    }
                    MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
                            UMengEventIds.DISCOVER_YELLOWPAGE_MOVIE_ALL_SEAT_CONFIRM);
                    // add xcx 2014-12-30 end 统计埋点
                }
                break;
                
            case R.id.network_exception_layout:
                if(NetUtil.isNetworkAvailable(this)){
                    initData();
                    mMainLayout.setVisibility(View.VISIBLE);
                    mNoDataLayout.setVisibility(View.GONE);
                }else{
                }
            default:

                break;
        }

    }

    private void jumpToCreateOrder() {
        Intent intent = new Intent(this, MovieOrderDetailsActivity.class);
        intent.putExtra(CinemaConstants.ENTRY_TYPE, CinemaConstants.GENERATE_ORDER);
        DetailMovieOrder orderDetail = new DetailMovieOrder();
        orderDetail.setOrder_title(getString(R.string.putao_order_title, seatInfo.cinemaname));
       
        /*
         * 不需要再计算服务费(seatInfo.servicefee),gewaprice已经包含了服务费
         * modified by hyl 2015-1-8 start
         * old code:
           orderDetail.setAmount(selectedSeats.size() * (seatInfo.gewaprice + seatInfo.servicefee) * 100);
         */
        orderDetail.setAmount(selectedSeats.size() * (seatInfo.gewaprice ) * 100);
        //modified by hyl 2015-1-8 end
        
        orderDetail.setMovie_name(seatInfo.moviename);
        orderDetail.setCinema_name(seatInfo.cinemaname);
        orderDetail.setRoom_name(seatInfo.roomname);
        orderDetail.setPlay_time(seatInfo.playtime);
        orderDetail.setMp_id(seatInfo.mpid);
        StringBuilder sb = new StringBuilder();
        StringBuilder seatBuilder = new StringBuilder();
        for (Entry<String, TextView> entry : selectedSeats.entrySet()) {
            sb.append(entry.getValue().getText()).append(",");
            seatBuilder.append(entry.getKey()).append(",");
        }
        orderDetail.setSeat(sb.substring(0, sb.length() - 1));

        intent.putExtra(CinemaConstants.MOVIE_ORDER_DETAIL, orderDetail);
        intent.putExtra(CinemaConstants.MOVIE_ORDER_SEAT,
                seatBuilder.substring(0, seatBuilder.length() - 1));
        intent.putExtra(CinemaConstants.MOVIE_ORDER_LANGUAGE, seatInfo.language);
        intent.putExtra(CinemaConstants.MOVIE_ORDER_EDITION, seatInfo.edition);
        intent.putExtra(CinemaConstants.MOVIE_PHOTO_URL, movie_photo_url);
        intent.putExtra(CinemaConstants.CINEMA_ADDRESS, mCinemaAddress);
        intent.putExtra(CinemaConstants.CINEMA_CITY, mCityName);
        intent.putExtra("type", type);
        startActivityForResult(intent,0);
    }
    
    
    

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        // TODO Auto-generated method stub
        super.onActivityResult(arg0, arg1, arg2);
        if(arg0 == 0 && arg2 != null){
            boolean isRefresh = arg2.getBooleanExtra(MovieOrderDetailsActivity.ISREFRESH_UI_KEY,false);
            if(isRefresh){
                //执行刷新操作
                selectedSeats.clear();
                film_seat.removeAllViews();
                
                TextView tv = new TextView(getApplicationContext());
                //              tv.setBackgroundResource(R.drawable.putao_seat_xuanzuo);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        getResources().getDimensionPixelSize(R.dimen.putao_text_tertiary_size));
                tv.setTextColor(getResources().getColor(R.color.putao_text_color_second));
                tv.setText(getString(R.string.putao_select_seat_no_seat, seatInfo.maxseat));
                tv.setGravity(Gravity.CENTER);
                film_seat.addView(tv);
                
                refreshHint();
                if (mProgressDialog!=null) {
                    mProgressDialog.show();
                }
                if (!TextUtils.isEmpty(url)) {
                    seatInfoTask = new SeatInfoTask();
                    seatInfoTask.execute(url);
                }
            }
        }
    }

    private ArrayList<SeatInfo> setSeatInfo(List<SeatRow> seatList) {
        ArrayList<SeatInfo> list_seatInfos = new ArrayList<SeatInfo>();
        for (SeatRow row : seatList) {//
            String[] columns = row.columns.split(",");
            SeatInfo mSeatInfo = new SeatInfo();
            ArrayList<Seat> mSeatList = new ArrayList<Seat>();
            for (int j = 0; j < columns.length; j++) {
                Seat mSeat = new Seat();
                mSeat.setN(columns[j]);
                if ("ZL".equals(columns[j]) || "O".equalsIgnoreCase(columns[j])) {
                    mSeat.setCondition(0);// 0代表走廊
                } else if ("LK".equals(columns[j]) || "W".equals(columns[j])
                        || "S".equals(columns[j])) {
                    mSeat.setCondition(2);// 2代表锁定,不可选
                } else {
                    mSeat.setCondition(1);// 2代表可选
                }
                mSeat.setDamagedFlg("");// 暂不处理情侣座
                mSeat.setLoveInd("0");
                mSeatList.add(mSeat);
            }
            mSeatInfo.setDesc(row.rowid);// 有些影院是abcd
            mSeatInfo.setRow(row.rownum);
            mSeatInfo.setSeatList(mSeatList);
            list_seatInfos.add(mSeatInfo);
        }

        return list_seatInfos;
        // "O".equalsIgnoreCase "ZL"-------走廊或者物体
        // ("LK".equals(this.mStrColumns[i][j])) ||
        // ("W".equals(this.mStrColumns[i][j])) ||
        // ("S".equals(this.mStrColumns[i][j])都是不可用状态
        /*
         * if ("lover".equals(this.mStrColumns[i][j]))-------情侣座 switch
         * (this.mLoverSeats[i][j]) { case 0: default: break; case -2:
         * this.mColumns[i][j] = 9; break; case 1: this.mColumns[i][j] = 4;
         * break; case 2: this.mColumns[i][j] = 5; break; case -1:
         * this.mColumns[i][j] = 8; break; }
         */

    }

    class SeatInfoTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            seatInfo = (CinemaRoomInfo)CinemaApiUtilHelper.doHttpGetObjFromUrl(params[0],
                    GewaApiReqMethod.OPI_SEAT_INFO);
            LogUtil.d(TAG, seatInfo == null ? "seatInfo=null" : "seatInfo:success");
            if (null != seatInfo && null != seatInfo.seatList ) {
                seatInfoList = setSeatInfo(seatInfo.seatList);
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {
            if (null != seatInfo) {
                String msg = seatInfo.error_msg.trim();
                if(TextUtils.isEmpty(seatInfo.error_msg)){
                    initSelectSeatView();
                }else{
                    showInvalidTipDialog(msg);
                }
            }else{
                //这里可以补充超时处理
            }
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if(seatInfoTask != null){
            seatInfoTask.cancel(true);
        }
            
    }

    @Override
    public String getServiceNameByUrl() {
        return null;
    }

    @Override
    public String getServiceName() {
        return CinemaSelectSeatActivity.class.getName();
    }

    @Override
    public boolean needMatchExpandParam() {
        return false;
    }

    @Override
    protected boolean needReset() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public Integer remindCode() {
        return null;
    }
    
    /**
     * 弹出无效选座提示
     */
    private void showInvalidTipDialog(String msg ){
        commonDialog = CommonDialogFactory.getOkCommonDialog(this);
        commonDialog.setTitle(R.string.putao_movie_error_tips);
//        commonDialog.setMessage(R.string.putao_movie_seat_error); 旧的提示文本
        commonDialog.setMessage(msg);//错误信息文本 
        commonDialog.setOkButtonClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                commonDialog.dismiss();
                finish();
            }
        });
        commonDialog.show();
    }

    @Override
    public boolean choose(int column_num, int row_num, boolean paramBoolean) {
        String desc = seatInfoList.get(row_num).getDesc() + "排"
                + (seatInfoList.get(row_num).getSeat(column_num).getN()) + "座";
        selectSeat(seatInfoList.get(row_num).getDesc() + ":"
                + seatInfoList.get(row_num).getSeat(column_num).getN(), desc);
        refreshHint();
        return false;
    }

    @Override
    public boolean cancel(int column_num, int row_num, boolean paramBoolean) {
        removeSeat(seatInfoList.get(row_num).getDesc() + ":"
                + seatInfoList.get(row_num).getSeat(column_num).getN());
        refreshHint();
        return false;
    }

    @Override
    public void viewTouched() {
        select_seat_small.setVisibility(View.VISIBLE);
        if (mHandler.hasMessages(DISMISS_THUMBNAIL)) {
            mHandler.removeMessages(DISMISS_THUMBNAIL);
        }
        mHandler.sendEmptyMessageDelayed(DISMISS_THUMBNAIL, 600);
    };

    @Override
    public void selectSeatMax() {
        Toast.makeText(getApplicationContext(),
                getString(R.string.putao_select_seat_max_seats, seatInfo.maxseat),
                Toast.LENGTH_LONG).show();
    }
    
}
