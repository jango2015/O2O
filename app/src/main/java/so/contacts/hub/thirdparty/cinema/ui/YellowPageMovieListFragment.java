
package so.contacts.hub.thirdparty.cinema.ui;

import java.util.ArrayList;
import java.util.List;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.ad.AdCode;
import so.contacts.hub.core.Config;
import so.contacts.hub.db.MovieDB;
import so.contacts.hub.remind.BaseRemindFragment;
import so.contacts.hub.remind.RemindConfig;
import so.contacts.hub.thirdparty.cinema.CinemaConstants;
import so.contacts.hub.thirdparty.cinema.adapter.MovieListAdapter;
import so.contacts.hub.thirdparty.cinema.bean.CinemaMovieDetail;
import so.contacts.hub.thirdparty.cinema.bean.MovieCity;
import so.contacts.hub.thirdparty.cinema.tool.CinemaApiUtil;
import so.contacts.hub.thirdparty.cinema.utils.GewaApiReqMethod;
import so.contacts.hub.util.ContactsAppUtils;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.NetUtil;
import so.contacts.hub.util.UMengEventIds;
import so.contacts.hub.widget.ProgressDialog;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.loader.DataLoader;
import com.loader.image.ImageLoaderFactory;
import com.yulong.android.contacts.discover.R;

@SuppressLint("ValidFragment")
public class YellowPageMovieListFragment extends BaseRemindFragment implements OnClickListener{

    private static final String TAG = "YellowPageMovieListFragment";
    
    private static final int INIT_DATA = 2001;
    private String cityCode = "440300";

    private ListView mHistoryListView = null;

    private LinearLayout mHistoryNoDataLayout = null;

    private TextView mShowHintTView = null;

    private ProgressDialog mProgressDialog = null;

    private MovieListAdapter mMovieListAdapter = null;

    private List<CinemaMovieDetail> mMovieList = new ArrayList<CinemaMovieDetail>();

    public static final int TYPE_OPEN = 1;

    public static final int TYPE_COMING = 2;

    private View mContentView = null;

    private Context mContext = null;

    private Activity mActivity = null;

    private int mListType = TYPE_OPEN;
    
    private DataLoader mDataLoader = null;//add by hyl 2014-12-31
    
    private String mCityName = "";

    public YellowPageMovieListFragment(int type) {
        mListType = type;
    }

    public YellowPageMovieListFragment() {

    }

    public void setListType(int type) {
        mListType = type;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.putao_yellow_page_movielist, null);
        mContext = this.getActivity().getBaseContext();
        mActivity = this.getActivity();
        mProgressDialog = new ProgressDialog(mActivity,false);
        mProgressDialog.setMessage(getString(R.string.putao_yellow_page_loading));
        return mContentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mDataLoader = new ImageLoaderFactory(getActivity()).getMovieListLoader();//add by hyl 2014-12-31
        
        initView();
//        initData();
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.i(TAG, "onResume");
        MobclickAgentUtil.onResume(mContext);
    }

    public void initData(final String city){
    	mCityName = city;
    	mHandler.post(new Runnable() {
			@Override
			public void run() {
				if(!TextUtils.isEmpty(city)){
					MovieDB mMovieDB = ContactsAppUtils.getInstance().getDatabaseHelper().getMovieDB();
					MovieCity movieCity = mMovieDB.queryMovieCidByCityName(city);
					if(movieCity != null){
						cityCode = movieCity.getCitycode();
					}
				}
            	mHandler.sendEmptyMessage(INIT_DATA);
			}
		});
    }
    
    public void refreshData() {
    	LogUtil.d("cinemae", "loadMovieData refresh:");
       
        /*
         * 这里不需要判断是否登录，登录状态不应该阻塞电影票列表的加载
         * modified by hyl 2015-1-8 start
         * old code:
         *  if (NetUtil.isNetworkAvailable(this.getActivity()) && PutaoAccount.getInstance().isLogin()) {
	            mMovieList.clear();
	            mHistoryNoDataLayout.setVisibility(View.GONE);
	            mHistoryListView.setVisibility(View.VISIBLE);
	            loadData();
	        } else if (!NetUtil.isNetworkAvailable(this.getActivity())) {
	            mHistoryNoDataLayout.setVisibility(View.VISIBLE);
	            mShowHintTView.setText(R.string.putao_netexception_hint);
	            mHistoryListView.setVisibility(View.GONE);
	        } else if (!PutaoAccount.getInstance().isLogin()) {
	            mHistoryListView.setVisibility(View.GONE);
	            mHistoryNoDataLayout.setVisibility(View.VISIBLE);
	            if (!PutaoAccount.getInstance().isLogin()) {
	                Toast.makeText(mContext, R.string.putao_yellow_page_try_login, Toast.LENGTH_SHORT)
	                        .show();
	                PutaoAccount.getInstance().silentLogin(this);
	            }
	        }
         */
    	if (NetUtil.isNetworkAvailable(this.getActivity()) && !TextUtils.isEmpty(cityCode)) {
            mMovieList.clear();
            mHistoryNoDataLayout.setVisibility(View.GONE);
            mHistoryListView.setVisibility(View.VISIBLE);
            loadData();
        } else if (!NetUtil.isNetworkAvailable(this.getActivity())) {
            mHistoryNoDataLayout.setVisibility(View.VISIBLE);
            mShowHintTView.setText(R.string.putao_netexception_hint);
            mHistoryListView.setVisibility(View.GONE);
        } 
    	//modified by hyl 2015-1-8 end
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgentUtil.onPause(mContext);
    }

    private void initView() {
        mHistoryNoDataLayout = (LinearLayout)mContentView
                .findViewById(R.id.network_exception_layout);
        mShowHintTView = (TextView)mContentView.findViewById(R.id.exception_desc);
        mHistoryNoDataLayout.setOnClickListener(this);
        mHistoryListView = (ListView)mContentView.findViewById(R.id.movie_list);
        mHistoryListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                CinemaMovieDetail detail = (CinemaMovieDetail)mMovieListAdapter.getItem(position);
                if (detail != null) {
                    long movieId = detail.getMovieid();
                    if (movieId != 0) {
                        Intent intent = new Intent(mActivity, MovieDetailActivity.class);
                        
                        //add by hyl 2015-1-5 start
                        intent.putExtra("movie_name", detail.getMoviename());//电影名称
                        intent.putExtra("movie_english_name", detail.getEnglishname());//电影英文名称
                        intent.putExtra(CinemaConstants.CINEMA_CITY, mCityName);
                        
                        if (TYPE_COMING == mListType) {//当前类型是即将上映类型
                        	YellowPageMovieListActivity activity = (YellowPageMovieListActivity) mActivity;
                            if(activity.openMovieList.contains(movieId)){//判断该电影是否存在'正在热映'列表中，若包含 则表示可以选座购票，若不包含则无法购票
                            	intent.putExtra("open_buy", true);
                            }
                        }else{
                        	intent.putExtra("open_buy", true);
                        }
                        
                        //add by hyl 2015-1-5 end
                        
                        intent.putExtra("movieid", movieId);
                        intent.putExtra("citycode",cityCode);
                        intent.putExtra("type", mListType);
                        startActivity(intent);
                    }
                }
                // add xcx 2014-12-30 start 统计埋点
                if (TYPE_OPEN == mListType) {
                    MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
                            UMengEventIds.DISCOVER_YELLOWPAGE_MOVIE_RELEASED_LIST_ITEM_CLICK);
                } else {
                    MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
                            UMengEventIds.DISCOVER_YELLOWPAGE_MOVIE_UPCOMING_LIST_ITEM_CLICK);
                }
                MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
                        UMengEventIds.DISCOVER_YELLOWPAGE_MOVIE_ALL_LIST_ITEM_CLICK);
                // add xcx 2014-12-30 end 统计埋点
            }
        });

        /*
         * 增加listView滑动监听，当滑动时 暂停图片加载
         * add by hyl 2014-12-31 start
         */
        mHistoryListView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
				if(arg1 == SCROLL_STATE_IDLE){
					mDataLoader.setPauseWork(false);
				}else {
					mDataLoader.setPauseWork(true);
				}
			}
			
			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
		});
        //add by hyl 2014-12-31 end

        /*
    	 * 增加参数 图片加载器
    	 * modified by hyl 2014-12-31 start
    	 * old code:
    	 * mMovieListAdapter = new MovieListAdapter(mContext, mMovieList);
    	 */
        mMovieListAdapter = new MovieListAdapter(mContext, mMovieList,mDataLoader);
        //modified by hyl 2014-12-31 end
        mHistoryListView.setAdapter(mMovieListAdapter);
        
        LogUtil.d("cinemae", "mHistoryListView setAdapter");
    }

//    private void initData() {
//        if (NetUtil.isNetworkAvailable(mContext) && PutaoAccount.getInstance().isLogin()) {
//            loadData();
//        } else if (!NetUtil.isNetworkAvailable(mContext)) {
//            mHistoryNoDataLayout.setVisibility(View.VISIBLE);
//            mShowHintTView.setText(R.string.putao_netexception_hint);
//            mHistoryListView.setVisibility(View.GONE);
//        } else if (!PutaoAccount.getInstance().isLogin()) {
//            mHistoryListView.setVisibility(View.GONE);
//            mHistoryNoDataLayout.setVisibility(View.VISIBLE);
//            if (!PutaoAccount.getInstance().isLogin()) {
//                Toast.makeText(mContext, R.string.putao_yellow_page_try_login, Toast.LENGTH_SHORT)
//                        .show();
//                PutaoAccount.getInstance().silentLogin(this);
//            }
//        }
//    }

    
    /**
     * 开关进度框
     * @param isShow
     */
    public void showProgressDialog(boolean isShow){
        if (mProgressDialog != null) {
            if (isShow) {
                if (!mProgressDialog.isShowing()) {
                    mProgressDialog.show();
                }
            } else {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }
        }
    }
    
    
    private void loadData() {
        showProgressDialog(true);
        LogUtil.d("cinemae", "loadData mListType:"+mListType);
        if (mListType == TYPE_OPEN) {
            String movieListUrl = CinemaApiUtil.getOpenMovieListUrl(0, "",
            		cityCode, "",
                    CinemaConstants.PIC_SIZE_5[0], CinemaConstants.PIC_SIZE_5[1]);
            Config.asynGetGewara(movieListUrl, GewaApiReqMethod.OPEN_MOVIE_LIST, mHandler);
        } else if (mListType == TYPE_COMING) {
            String movieListUrl = CinemaApiUtil.getFutureMovieListUrl(0, 50, "",
                    CinemaConstants.PIC_SIZE_5[0], CinemaConstants.PIC_SIZE_5[1]);
            Config.asynGetGewara(movieListUrl, GewaApiReqMethod.FUTURE_MOVIE_LIST, mHandler);
        }
    }

    private Handler mHandler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case 0:
                    showProgressDialog(false);
                    List<CinemaMovieDetail> list = (List<CinemaMovieDetail>)msg.obj;
                    mMovieList.addAll(list);
                    
                    LogUtil.d("cinemae", "loadData list size:"+list.size());
                    
                    YellowPageMovieListActivity activity = (YellowPageMovieListActivity) mActivity;
                    /*
                     * modify by putao_lhq at 2015年1月10日 @start
                     * 添加之前需要先清理掉之前的列表
                     * add code:
                     */
                    activity.openMovieList.clear();/* end by putao_lhq */
                    
                    if (mListType == TYPE_OPEN) {
                    	for (CinemaMovieDetail cinemaMovieDetail : list) {
                    		activity.openMovieList.add(cinemaMovieDetail.getMovieid());
                    	}
                    }else if(mListType == TYPE_COMING){
                    	for (CinemaMovieDetail cinemaMovieDetail : list) {
                    		activity.futureMovieList.add(cinemaMovieDetail.getMovieid());
                    	}
                    }
                    
                    /*
                     * 
                     * modified by hyl 2014-12-31 start 
                     * old code:
                     * mMovieListAdapter = new MovieListAdapter(mContext, mMovieList);
                       mHistoryListView.setAdapter(mMovieListAdapter);
                     */
                    if(mMovieListAdapter == null){
                    	mMovieListAdapter = new MovieListAdapter(mContext, mMovieList,mDataLoader);
                        mHistoryListView.setAdapter(mMovieListAdapter);
                    }else{
                    	mMovieListAdapter.setData(mMovieList);
                    }
                    //modified by hyl 2014-12-31 end
                    
                    if (mMovieList.size() == 0) {
                        mHistoryNoDataLayout.setVisibility(View.VISIBLE);
                        mShowHintTView.setText(R.string.putao_moive_list_nodata);
                        mHistoryListView.setVisibility(View.GONE);
                    }
                    break;
                case 1:
                    showProgressDialog(false);
                    if (mMovieList.size() == 0) {
                        mHistoryNoDataLayout.setVisibility(View.VISIBLE);
                        mShowHintTView.setText(R.string.putao_netexception_hint);
                        mHistoryListView.setVisibility(View.GONE);
                    }
                    break;
                case 2:
                    break;
                case INIT_DATA://开始加载数据
                	refreshData();
                	break;
                default:
                    break;
            }
        };
    };

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.network_exception_layout) {//点击网络异常界面时，重新开始刷新数据
        	refreshData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if( mProgressDialog != null ){
        	mProgressDialog.cancel();
        	mProgressDialog = null;
        }
    }

    @Override
    public Integer remindCode() {
        return RemindConfig.MyOrderChargeHistory;
    }

    @Override
    public String getServiceNameByUrl() {
        return null;
    }

    @Override
    public String getServiceName() {
        return this.getClass().getName();
    }

    @Override
    public boolean needMatchExpandParam() {
        return false;
    }


    @Override
    public Integer getAdId() {
        if (mListType == TYPE_OPEN) {
            return AdCode.ADCODE_YellowPageMovieListFragment_TYPE_OPEN;
        } else {
            return AdCode.ADCODE_YellowPageMovieListFragment_TYPE_COMING;
        }
    }
}
