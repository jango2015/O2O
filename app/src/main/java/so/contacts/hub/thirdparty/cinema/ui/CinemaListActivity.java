
package so.contacts.hub.thirdparty.cinema.ui;

import java.util.ArrayList;
import java.util.List;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.core.Config;
import so.contacts.hub.remind.BaseRemindActivity;
import so.contacts.hub.thirdparty.cinema.CinemaConstants;
import so.contacts.hub.thirdparty.cinema.adapter.CinemaListAdapter;
import so.contacts.hub.thirdparty.cinema.bean.CinemaDetail;
import so.contacts.hub.thirdparty.cinema.tool.CinemaApiUtil;
import so.contacts.hub.thirdparty.cinema.utils.GewaApiReqMethod;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.NetUtil;
import so.contacts.hub.util.UMengEventIds;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yulong.android.contacts.discover.R;

public class CinemaListActivity extends BaseRemindActivity implements OnClickListener,
        OnItemClickListener {
    public static final int TYPE_OPEN = 1;

    public static final int TYPE_COMING = 2;

    private int type = TYPE_OPEN;

    private ListView cinemaListView;

    private TextView title;

    private String movieName;

    private long movieId;

    private String cityCode;

    private String movieLength;
    private String moviePhotoUrl;
    private List<CinemaDetail> mCinemaList = new ArrayList<CinemaDetail>();

    private CinemaListAdapter mAdapter;

    private LinearLayout mNoDataLayout;
    
    /*private TextView mExceptionDescTextView;*/
    
    private String mCityName;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.putao_cinema_list_layout);
        cinemaListView = (ListView)findViewById(R.id.cinema_list);
        title = (TextView)findViewById(R.id.title);
        title.setText(R.string.putao_cinemalist_title);
        findViewById(R.id.back_layout).setOnClickListener(this);
        cinemaListView.setOnItemClickListener(this);
        mAdapter = new CinemaListAdapter(this, mCinemaList);
        cinemaListView.setAdapter(mAdapter);
        
        mNoDataLayout = (LinearLayout)findViewById(R.id.network_exception_layout);
        mNoDataLayout.setOnClickListener(this);
        /*mExceptionDescTextView = (TextView)findViewById(R.id.exception_desc);*/

        if (getIntent() != null) {
            movieId = getIntent().getLongExtra("movieid", 0);
            cityCode = getIntent().getStringExtra("citycode");
            movieLength = getIntent().getStringExtra("length");
            movieName = getIntent().getStringExtra("movieName");
            type = getIntent().getIntExtra("type", TYPE_OPEN);
            moviePhotoUrl = getIntent().getStringExtra(CinemaConstants.MOVIE_PHOTO_URL);
            mCityName = getIntent().getStringExtra(CinemaConstants.CINEMA_CITY);
        }
        if (movieId != 0) {
            loadData();
        }
    }

    private void loadData() {
        showLoadingDialog(false);
        String cinemaListUrl = CinemaApiUtil.getMovieCinemaListUrl(null, movieId, cityCode, "", 0,
                0);
        Config.asynGetGewara(cinemaListUrl, GewaApiReqMethod.OPEN_CINEMA_LIST_BY_PLAYDATE, mHandler);
    }

    private Handler mHandler = new Handler() {

        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            dismissLoadingDialog();
            switch (msg.what) {
                case 0:
                    /*
                     * modify by putao_lhq at 2015年1月10日 @start
                     * old code:
                    List<CinemaDetail> cinemaList = (List<CinemaDetail>)msg.obj;
                    if (cinemaList != null) {
                        mCinemaList.clear();
                        mCinemaList.addAll(cinemaList);
                        mAdapter.setCinemaList(cinemaList);
                        mAdapter.notifyDataSetChanged();
                    }*/
                    if (msg.obj == null) {
                        Toast.makeText(CinemaListActivity.this, 
                                getString(R.string.putao_not_found_cinema_in_this_city), 
                                Toast.LENGTH_SHORT).show();
//                        finish();
                        return;
                    }
                    List<CinemaDetail> cinemaList = (List<CinemaDetail>)msg.obj;
                    if (cinemaList != null && cinemaList.size() > 0) {
                        mCinemaList.clear();
                        mCinemaList.addAll(cinemaList);
                        mAdapter.setCinemaList(cinemaList);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(CinemaListActivity.this, 
                                getString(R.string.putao_not_found_cinema_in_this_city), 
                                Toast.LENGTH_SHORT).show();
//                        finish();
                        return;
                    }/* end by putao_lhq */
                    break;
                case 1:
                    cinemaListView.setVisibility(View.GONE);
                    mNoDataLayout.setVisibility(View.VISIBLE);
                    Toast.makeText(CinemaListActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT)
                            .show();
                    break;
                case 2:
                    cinemaListView.setVisibility(View.GONE);
                    mNoDataLayout.setVisibility(View.VISIBLE);
                    Toast.makeText(CinemaListActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT)
                            .show();
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    protected void onResume() {
        super.onResume();
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
            case R.id.network_exception_layout:
                if(NetUtil.isNetworkAvailable(this)){
                    cinemaListView.setVisibility(View.VISIBLE);
                    mNoDataLayout.setVisibility(View.GONE);
                    loadData();
                }
                
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CinemaDetail cinemaDetail = (CinemaDetail)mAdapter.getItem(position);
        Intent intent = new Intent(this, OpenPlayListActivity.class);
        intent.putExtra("cinemaDetail", cinemaDetail);
        intent.putExtra("movieid", movieId);
        intent.putExtra("length", movieLength);
        intent.putExtra("movieName", movieName);
        intent.putExtra(CinemaConstants.MOVIE_PHOTO_URL, moviePhotoUrl);
        intent.putExtra(CinemaConstants.CINEMA_CITY, mCityName);
        startActivity(intent);
        // add xcx 2014-12-30 start 统计埋点
        if (type == TYPE_OPEN) {
            MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
                    UMengEventIds.DISCOVER_YELLOWPAGE_MOVIE_RELEASED_THEATER_LIST_ITEM_CLICK);
        } else {
            MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
                    UMengEventIds.DISCOVER_YELLOWPAGE_MOVIE_UPCOMING_THEATER_LIST_ITEM_CLICK);
        }
        MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
                UMengEventIds.DISCOVER_YELLOWPAGE_MOVIE_ALL_THEATER_LIST_ITEM_CLICK);
        // add xcx 2014-12-30 end 统计埋点
    }

    @Override
    protected boolean needReset() {
        return true;
    }
}
