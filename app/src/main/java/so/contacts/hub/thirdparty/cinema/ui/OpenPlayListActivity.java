
package so.contacts.hub.thirdparty.cinema.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.core.Config;
import so.contacts.hub.remind.BaseRemindActivity;
import so.contacts.hub.thirdparty.cinema.CinemaConstants;
import so.contacts.hub.thirdparty.cinema.adapter.OpiListAdapter;
import so.contacts.hub.thirdparty.cinema.bean.CinemaDetail;
import so.contacts.hub.thirdparty.cinema.bean.OpenPlayItem;
import so.contacts.hub.thirdparty.cinema.bean.Playdate;
import so.contacts.hub.thirdparty.cinema.tool.CinemaApiUtil;
import so.contacts.hub.thirdparty.cinema.utils.GewaApiReqMethod;
import so.contacts.hub.util.DateUtil;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.NetUtil;
import so.contacts.hub.util.UMengEventIds;
import so.contacts.hub.util.Utils;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yulong.android.contacts.discover.R;

/**
 * 场次播放列表窗口
 * 
 * @author lixiaohui
 */
public class OpenPlayListActivity extends BaseRemindActivity implements OnClickListener,
        OnItemClickListener {
    public static final int TYPE_OPEN = 1;
    public static final int TYPE_COMING = 2;
    private int type=TYPE_OPEN;
    private long movieId;

    private String movieLength;

    private OpiListAdapter mAdapter;

    private List<OpenPlayItem> mOpiList = new ArrayList<OpenPlayItem>();

    private LinearLayout playDateContainer;
    
    //空白界面里的日期容器
    private LinearLayout playDateContainer_empty;

    private ListView opiListView;

    private CinemaDetail cinemaDetail;

    private String movieName;
    private String moviePhotoUrl;
    
    private LinearLayout emptyContainer ;
    
    private int lastPressViewIndex = -1;
    
    private String mCityName;
    
    /**
     * 场次缓存列表 （需求来自 多次请求格瓦拉时会被封IP10-15分钟） add ljq 2015/01/14 
     */
    private HashMap<String,List<OpenPlayItem>> mCatchScreeningsList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.putao_open_play_list_layout);
        
        mCatchScreeningsList = new HashMap<String, List<OpenPlayItem>>();
        
        movieName = getIntent().getStringExtra("movieName");
        cinemaDetail = (CinemaDetail)getIntent().getSerializableExtra("cinemaDetail");
        movieId = getIntent().getLongExtra("movieid", 0);
        movieLength = getIntent().getStringExtra("length");
        type = getIntent().getIntExtra("type",TYPE_OPEN);
        moviePhotoUrl = getIntent().getStringExtra(CinemaConstants.MOVIE_PHOTO_URL);
        mCityName = getIntent().getStringExtra(CinemaConstants.CINEMA_CITY);
        /*
         * modify by putao_lhq at 2015年1月10日 @start
         * 添加网络异常时，界面提示
         * old code:
        initViews();
        loadPlayDateList();*/
        findViewById(R.id.back_layout).setOnClickListener(this);
        TextView title = (TextView)findViewById(R.id.title);
        title.setText(movieName);
        initViews();
        if (NetUtil.isNetworkAvailable(this)) {
            showEmptyView(false);
            loadPlayDateList();
        } else {
            showEmptyView(true);
            initEmptyView();
        }/* end by putao_lhq */
    }

    private void initViews() {
        int horizontalMargin=getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin);
        /*
         * modify by putao_lhq at 2015年1月10日
         * delete code:
        findViewById(R.id.back_layout).setOnClickListener(this);
        TextView title = (TextView)findViewById(R.id.title);
        title.setText(movieName);*/
        opiListView = (ListView)findViewById(R.id.opi_list);
        opiListView.setOnItemClickListener(this);
        mAdapter = new OpiListAdapter(this, mOpiList, movieLength);
        // 添加头部view
        View ihead = View.inflate(this, R.layout.putao_open_play_list_item_head, null);
        TextView cinemaName = (TextView)ihead.findViewById(R.id.cinema_name);
        cinemaName.setText(cinemaDetail.getCinemaname());
        TextView cinemaRemark = (TextView)ihead.findViewById(R.id.cinema_remark);
        
        /*
         * modified by hyl 2015-1-6 start
         * old code:
         *  cinemaRemark.setText(cinemaDetail.getGeneralmark());
         */
        cinemaRemark.setText(getString(R.string.putao_movie_dtl_rating, cinemaDetail.getGeneralmark()));
        //modified by hyl 2015-1-6 end
        
        TextView cinemaAddress = (TextView)ihead.findViewById(R.id.cinema_address);
        cinemaAddress.setText(cinemaDetail.getAddress());
        playDateContainer = (LinearLayout)ihead.findViewById(R.id.playdate_container);
        
        playDateContainer_empty = (LinearLayout)findViewById(R.id.playdate_container);
        
        //ihead.setPadding(horizontalMargin, Utils.dip2px(this, 16), horizontalMargin, 0);
        opiListView.addHeaderView(ihead, null, false);
        // 添加底部view
        View ibottom = View.inflate(this, R.layout.putao_movie_bottom_layout, null);
        ibottom.setPadding(horizontalMargin, Utils.dip2px(this, 60), horizontalMargin, Utils.dip2px(this, 16));
        opiListView.addFooterView(ibottom, null, false);
        opiListView.setAdapter(mAdapter);
        
        emptyContainer = (LinearLayout)findViewById(R.id.empty_container);
    }

    private void loadPlayDateList() {
        String playDateUrl = CinemaApiUtil.getPlayListUrl(cinemaDetail.getCinemaid(), movieId);
        Config.asynGetGewara(playDateUrl, GewaApiReqMethod.PLAYDATE_LIST, mHandler);
    }

    private void loadOpiList(String playDate ,int index) {
        List<OpenPlayItem> catchlist = null;
        if(mCatchScreeningsList != null){
            catchlist =  mCatchScreeningsList.get(playDate);
        }
        lastPressViewIndex = index;
        if(!NetUtil.isNetworkAvailable(this)){
            showEmptyView(true);
            initEmptyView();
            TextView emptyTip = (TextView)findViewById(R.id.empty_tip_one);
            emptyTip.setText(R.string.putao_netexception_hint);
        }else{
            //modity ljq start 2015-1-14 如果有缓存数据则不继续请求
            if(catchlist != null){
                refreshUI(catchlist);
            }else{
                showLoadingDialog();
                String opiListUrl = CinemaApiUtil.getOpiListUrl(playDate, cinemaDetail.getCinemaid(),
                        movieId);
                Config.asynGetGewara(opiListUrl, GewaApiReqMethod.OPI_LIST, mHandler);
            }
            //modity ljq end 2015-1-14 如果有缓存数据则不继续请求
        }
    }

    /**
     * 初始化购票日期选择的button
     * 
     * @param playDateList
     */
    private void refreshPlayDateViews(List<Playdate> playDateList) {
        int firstVaildIndex = -1;
        
        if (playDateList == null || playDateList.isEmpty()) {
            return;
        }
        final Calendar today = Calendar.getInstance();
        // 时间设置为yyyy-MM-dd 00:00:00:000
        try {
            today.setTime(dateFormat.parse(dateFormat.format(today.getTime())));
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        List<Playdate> tempList = new ArrayList<Playdate>();
        // 过滤列表中今天之前的无意义的（昨天）票
        for (int i = 0, len = playDateList.size(); i < len; i++) {
            final Playdate playdate = playDateList.get(i);
            try {
                Date date = dateFormat.parse(playdate.getPlaydate());
                if (!date.before(today.getTime())) {
                    tempList.add(playdate);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        playDateList.clear();
        playDateList.addAll(tempList);

        // 设置今天button
        Button todayBtn = (Button)playDateContainer.getChildAt(0);
        Button todatBtn_empty = (Button)playDateContainer_empty.getChildAt(0);
        
        todayBtn.setText(getString(R.string.putao_movie_playlist_pd_today,
                dateFormat2.format(today.getTime())));
        todayBtn.setVisibility(View.VISIBLE);
        todayBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                doViewSelected(v);
                loadOpiList(dateFormat.format(today.getTime()),0);
            }
        });
        
        todatBtn_empty.setText(getString(R.string.putao_movie_playlist_pd_today,
                dateFormat2.format(today.getTime())));
        todatBtn_empty.setVisibility(View.VISIBLE);
        todatBtn_empty.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                doViewSelected(v);
                loadOpiList(dateFormat.format(today.getTime()),0);
            }
        });

        boolean isSelectToday = true;
        // 循环限定最大为3个
        for (int i = 0, j = 0, len = playDateList.size(); i < len && i < 3 && j < 3; i++, j++) {
            final Playdate playdate = playDateList.get(i);
            try {
                Date date = dateFormat.parse(playdate.getPlaydate());
                Button button = null;
                Button button_empty = null;
                // 如果第一条数据不是today,move到下一个去view
                if (0 == i && today.getTime().compareTo(date) != 0) {
                    isSelectToday = false;
                    j++;
                }
                final int index = j;
                button = (Button)playDateContainer.getChildAt(j);
                button.setText(dealWithDateStr(today, playdate.getPlaydate()));
                button.setSingleLine();
                button.setVisibility(View.VISIBLE);
                button.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doViewSelected(v);
                        loadOpiList(playdate.getPlaydate(), index);
                        // add xcx 2014-12-30 start 统计埋点
                        if (type == TYPE_OPEN) {
                            MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
                                    UMengEventIds.DISCOVER_YELLOWPAGE_MOVIE_RELEASED_TIME_SWITCHING_DATE);
                        } else {
                            MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
                                    UMengEventIds.DISCOVER_YELLOWPAGE_MOVIE_UPCOMING_TIME_SWITCHING_DATE);
                        }
                        MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
                                UMengEventIds.DISCOVER_YELLOWPAGE_MOVIE_ALL_TIME_SWITCHING_DATE);
                        // add xcx 2014-12-30 end 统计埋点
                    }
                });
				
				button_empty = (Button)playDateContainer_empty.getChildAt(j);
                button_empty.setText(dealWithDateStr(today, playdate.getPlaydate()));
                button_empty.setSingleLine();
                button_empty.setVisibility(View.VISIBLE);
                button_empty.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doViewSelected(v);
                        loadOpiList(playdate.getPlaydate(),index);
                        if (type == TYPE_OPEN) {
                            MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
                                    UMengEventIds.DISCOVER_YELLOWPAGE_MOVIE_RELEASED_TIME_SWITCHING_DATE);
                        } else {
                            MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
                                    UMengEventIds.DISCOVER_YELLOWPAGE_MOVIE_UPCOMING_TIME_SWITCHING_DATE);
                        }
                        MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
                                UMengEventIds.DISCOVER_YELLOWPAGE_MOVIE_ALL_TIME_SWITCHING_DATE);
                    }
                });
				
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        // 处理上映日期非今天的情况
        if (isSelectToday) {
            doViewSelected(todayBtn);
            loadOpiList(dateFormat.format(today.getTime()),0);
        } else {
            if(emptyContainer.getVisibility() == View.VISIBLE){
                
            }else{
                doViewSelected(playDateContainer.getChildAt(1));
                loadOpiList(playDateList.get(0).getPlaydate(),1);
            }
        }
    }

    /**
     * 处理view选中
     * 
     * @param v
     */
    private void doViewSelected(View v) {
        ViewGroup parent = (ViewGroup)v.getParent();
        for (int i = 0, len = parent.getChildCount(); i < len; i++) {
            View child = parent.getChildAt(i);
            if (child == v) {
                child.setSelected(true);
            } else {
                child.setSelected(false);
            }
        }
    }

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private SimpleDateFormat dateFormat2 = new SimpleDateFormat("MM-dd");

    private SimpleDateFormat dateFormat3 = new SimpleDateFormat("EEE MM-dd");

    /**
     * 处理日期，显示为“今天 12-30” “周日01-3 ”等
     * 
     * @param today
     * @param dateStr
     * @return
     */
    private String dealWithDateStr(Calendar today, String dateStr) {
        String dealedStr = null;
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateFormat.parse(dateStr));
            int dayDiff = DateUtil.daysBetween(today.getTime(), calendar.getTime());
            if (dayDiff == 0) {
                dealedStr = getString(R.string.putao_movie_playlist_pd_today,
                        dateFormat2.format(calendar.getTime()));
            } else if (dayDiff == 1) {
                dealedStr = getString(R.string.putao_movie_playlist_pd_tmr,
                        dateFormat2.format(calendar.getTime()));
            } else if (dayDiff == 2) {
                dealedStr = getString(R.string.putao_movie_playlist_pd_atmr,
                        dateFormat2.format(calendar.getTime()));
            } else if (dayDiff > 2 && dayDiff < 6) {
                dealedStr = dateFormat3.format(calendar.getTime());
            } else {
                dealedStr = dateFormat2.format(calendar.getTime());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dealedStr;
    }

    private Handler mHandler = new Handler() {

        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            dismissLoadingDialog();
            LogUtil.d("OpenPlayListActivity", "msg.what = " + msg.what);
            switch (msg.what) {
                case 0:
                    Bundle bundle = msg.peekData();
                    if (bundle != null) {
                        String method = bundle.getString("method");
                        LogUtil.d("OpenPlayListActivity", "method" + method);
                        if (!TextUtils.isEmpty(method) && msg.obj != null) {
                            if (method.equals(GewaApiReqMethod.PLAYDATE_LIST.toString())) {
                                List<Playdate> playDateList = (List<Playdate>)msg.obj;
                                refreshPlayDateViews(playDateList);
                            } else if (method.equals(GewaApiReqMethod.OPI_LIST.toString())) {
                                List<OpenPlayItem> opiList = (List<OpenPlayItem>)msg.obj;
                                //add ljq 2015-01-14 start 根据日期如2015-01-14 为key 缓存列表数据
                                String date = bundle.getString("date");
                                if(!TextUtils.isEmpty(date) && (mCatchScreeningsList != null)){
                                    mCatchScreeningsList.put(date, opiList);
                                }
                                //add ljq 2015-01-14 end 根据日期如2015-01-14 为key 缓存列表数据
                                
                                LogUtil.d("OpenPlayListActivity", "opiList" + opiList.size());
                                /*
                                 * modify by putao_lhq at 2015年1月10日 @start
                                 * fix bug: 2535
                                 * old code:
                                 *
                                if (opiList != null && opiList.size() > 0) {
                                    mOpiList.addAll(opiList);
                                }
                                mAdapter.notifyDataSetChanged();*/
                                refreshUI(opiList);
                                
                            }
                        }
                    }

                    break;
                case 1:
                    Toast.makeText(OpenPlayListActivity.this, msg.obj.toString(),
                            Toast.LENGTH_SHORT).show();
//                    showEmptyView(true);
                    break;
                case 2:
                    Toast.makeText(OpenPlayListActivity.this, msg.obj.toString(),
                            Toast.LENGTH_SHORT).show();
//                    showEmptyView(true);
                    break;
                default:
                    break;
            }
        }

    };
    
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mCatchScreeningsList = null;
    }

    private void refreshUI(List<OpenPlayItem> opiList) {
        mOpiList.clear();
        if (opiList != null && opiList.size() > 0) {
            showEmptyView(false);
            mOpiList.addAll(opiList);
            mAdapter.notifyDataSetChanged();
        } else {
            showEmptyView(true);
            initEmptyView();
            TextView emptyTip = (TextView)findViewById(R.id.empty_tip_one);
            emptyTip.setText(R.string.putao_no_session_data);
        }/* end by putao_lhq */
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_layout:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        OpenPlayItem item = (OpenPlayItem)parent.getAdapter().getItem(position);
        LogUtil.d("OpenPlayListActivity", "mpId : " + item.getMpid());
        if (item != null) {
            Intent intent = new Intent(this, CinemaSelectSeatActivity.class);
            intent.putExtra(CinemaConstants.MPID, item.getMpid());
            intent.putExtra(CinemaConstants.CINEMA_NAME, item.getCinemaname());
            intent.putExtra("type", type);
            intent.putExtra(CinemaConstants.MOVIE_PHOTO_URL, moviePhotoUrl);
            intent.putExtra(CinemaConstants.CINEMA_ADDRESS, cinemaDetail.getAddress());
            intent.putExtra(CinemaConstants.CINEMA_CITY, mCityName);
            startActivity(intent);
        }
     // add xcx 2014-12-30 start 统计埋点
        if (type == TYPE_OPEN) {
            MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
                    UMengEventIds.DISCOVER_YELLOWPAGE_MOVIE_RELEASED_TIME_LIST_ITEM_CLICK);
        } else {
            MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
                    UMengEventIds.DISCOVER_YELLOWPAGE_MOVIE_UPCOMING_TIME_LIST_ITEM_CLICK);
        }
        MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
                UMengEventIds.DISCOVER_YELLOWPAGE_MOVIE_ALL_TIME_LIST_ITEM_CLICK);
        // add xcx 2014-12-30 end 统计埋点
    }
    

    /**
     * add by putao_lhq
     */
    private void initEmptyView() {
        TextView cinemaName = (TextView)findViewById(R.id.cinema_name);
        cinemaName.setText(cinemaDetail.getCinemaname());
        TextView cinemaRemark = (TextView)findViewById(R.id.cinema_remark);
        
        cinemaRemark.setText(getString(R.string.putao_movie_dtl_rating, cinemaDetail.getGeneralmark()));
        
        TextView cinemaAddress = (TextView)findViewById(R.id.cinema_address);
        cinemaAddress.setText(cinemaDetail.getAddress());
//        playDateContainer = (LinearLayout)findViewById(R.id.playdate_container);
        emptyContainer.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                if (NetUtil.isNetworkAvailable(OpenPlayListActivity.this)) {
                    showEmptyView(false);
                    if (opiListView == null) {
                        initViews();
                    }
                    if(mCatchScreeningsList != null && mCatchScreeningsList.size()>0 && playDateContainer_empty != null){
                        playDateContainer_empty.getChildAt(lastPressViewIndex).performClick();
                    }else{
                        loadPlayDateList();
                    }
                }
            }
        });
    }
    
    /**
     * add by putao_lhq
     * @param show
     */
    private void showEmptyView(boolean show) {
        if (show) {
            emptyContainer.setVisibility(View.VISIBLE);
            findViewById(R.id.opi_list).setVisibility(View.GONE);
            if(lastPressViewIndex != -1){
            	doViewSelected(playDateContainer_empty.getChildAt(lastPressViewIndex));
            }
        } else {
            if(lastPressViewIndex != -1){
                doViewSelected(playDateContainer.getChildAt(lastPressViewIndex));
            }
            emptyContainer.setVisibility(View.GONE);
            findViewById(R.id.opi_list).setVisibility(View.VISIBLE);
        }
    }
}
