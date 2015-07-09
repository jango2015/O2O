package so.contacts.hub.ui.yellowpage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.adapter.MyGridViewAdapter;
import so.contacts.hub.adapter.MyListViewAdapter;
import so.contacts.hub.adapter.MyListViewListener;
import so.contacts.hub.core.ConstantsParameter;
import so.contacts.hub.push.bean.PushAdBean;
import so.contacts.hub.remind.RemindConfig;
import so.contacts.hub.remind.YellowPageLiveTitleDataBean;
import so.contacts.hub.service.PlugInterface;
import so.contacts.hub.service.PlugService;
import so.contacts.hub.ui.web.YellowPageH5Activity;
import so.contacts.hub.ui.yellowpage.bean.CategoryBean;
import so.contacts.hub.ui.yellowpage.bean.YellowParams;
import so.contacts.hub.util.ContactsAppUtils;
import so.contacts.hub.util.ContactsHubUtils;
import so.contacts.hub.util.ConvUtil;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.SDKApiUtil;
import so.contacts.hub.util.YellowPagePlugUtil;
import so.contacts.hub.util.YellowUtil;
import so.contacts.hub.widget.AdOperatLayout;
import so.contacts.hub.widget.AdOperatLayout.IUMengCallback;
import so.contacts.hub.widget.MyGridView;
import so.contacts.hub.widget.MyListView;
import so.contacts.hub.yellow.data.RemindBean;
import so.putao.aidl.IPutaoService;
import so.putao.findplug.YelloPageItem;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yulong.android.contacts.discover.R;

public class YellowPagePlug implements PlugInterface, MyListViewListener, IUMengCallback {
    private static final String TAG = "YellowPagePlugin";

    private Context mHostContext = null;

    private Context activityContext = null;

    private Context mPlugContext = null;

    private IPutaoService mService = null;

    /** 插件View 布局对象 start */
    private View mPlugView = null; // 提供给调用者得VIEW视图，第一次创建后缓存起来

    private ProgressBar mProgressBar = null;

    private EditText mEditText = null;

    private TextView mOffenTView = null;

    private TextView mAllTView = null;

    private MyGridView mOffenGridView = null;

    private MyListView mAllListView = null;

    private AdOperatLayout mAdLayoutTop = null;

    private AdOperatLayout mAdLayoutMiddle = null;

    private AdOperatLayout mAdLayoutBottom = null;

    /** 插件View 布局对象 end */

    /** 数据对象  start */
    private List<CategoryBean> categoryBeans = null;

    private List<CategoryBean> mOffenCategoryBeans = null;

    private List<CategoryBean> mAllCategoryBeans = null;

    private MyGridViewAdapter offenGridApt ;
    private Map<Integer, RemindBean> offenRemindMaps = null;
    private Map<Integer,YellowPageLiveTitleDataBean> liveTitleMaps =null;

    private MyListViewAdapter allListApt;
    private Map<Integer, RemindBean> allRemindMaps = null;
    private List<RemindBean> mAllReminds = new ArrayList<RemindBean>(); // add by putao_lhq 2014年11月8日
    /** 数据对象  end */

    private boolean isResume = false;//add by hyl 2014-10-25 增加标志位 标识当前是从resume进入

    private DiscoverAsyncThread mAsyncThread = null;

    private AdLayoutCallback mAdLayoutCallback = null;

    public static final int INIT_VIEW_ACTION = 0x2001;
    public static final int INIT_VIEW_ACTION_BUT_AD = 0x2002;
    private static final int REFRESH_AD_VIEWS = 0x2003;

    private boolean needRefreshData = false;//add by hyl 2014-11-24 增加标志位 标识当前是否需要刷新数据
    private boolean isPlugIn = false;//add by hyl 2014-11-24 增加标志位 标识当前是否停留在黄页界面

    public YellowPagePlug() {
    }

    // client与service建立连接
    private PlugServiceConnection servconn = null;
    private class PlugServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IPutaoService.Stub.asInterface(service);
            LogUtil.i(TAG, "onServiceConnected ComponentName="+name+" mService=" + mService+" id:"+Thread.currentThread().getId());
         
            /*
             * 注释数据刷新逻辑，因为在plugService中initData操作修改为了异步操作，当onServiceConnected调用时，数据有可能还没有初始化好，
             * 所以不在此处执行刷新数据操作（startDiscoverAsyncThread），改为当接收到ACTION_INITDATA_FINISHED广播时再进行处理；
             * modified by hyl 2014-11-24 start
             */
            /*if(mPlugContext != null) {
                mPlugContext.sendBroadcast(new Intent(ConstantsParameter.ACTION_REMIND_UPDATE));
            }
            startDiscoverAsyncThread(DiscoverAsyncThread.REFRESH_DATA_WITH_AD);*/
            //modified by hyl 2014-11-24 end
            
            /*
             * bug修改：设置字体大小后返回 界面不刷新问题修改
             * modified by hyl 2014-11-29 start
             */
            LogUtil.i(TAG, "onServiceConnected isResume="+isResume+" isPlugIn="+isPlugIn+" addr="+YellowPagePlug.this);
            if(isPlugIn && !isResume){
                startDiscoverAsyncThread(DiscoverAsyncThread.REFRESH_DATA_WITH_AD);
            }else{
                needRefreshData = true;
            }
            //modified by hyl 2014-11-29 end
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "onServiceDisconnected ComponentName=" + name);
            if(mHostContext != null) {
                mHostContext.getApplicationContext().unbindService(this);
            }
            mService = null;
        }

    };

    /**
     * 初始化插件
     *
     * @param host
     * @param plug
     * @return
     */
    @Override
    public boolean initPlug(Context hostContext, Context plugContext) {
        LogUtil.i(TAG, "initPlug hostContext="+hostContext+" plugContext="+plugContext+" addr="+this);
        this.mHostContext = hostContext;
        this.mPlugContext = plugContext;


        // Service被杀后会多次调用注册广播导致广播泄露
        // added by cj 2014/12/30 start
        unRegisterYellowPageDataReceiver(hostContext);
        registerYellowPageDataReceiver(hostContext);

        return bindService(hostContext, plugContext);
    }

    private boolean bindService(Context hostContext, Context plugContext){
        Intent intent = new Intent(plugContext, PlugService.class);
        LogUtil.i(TAG, "bindService=" + intent.toString());

        servconn = new PlugServiceConnection();
        return hostContext.getApplicationContext().bindService(intent, servconn,
                Context.BIND_AUTO_CREATE);
    }


    @Override
    public void unitPlug(Context context) {
        // update by hyl
        unRegisterYellowPageDataReceiver(context);

        context.getApplicationContext().unbindService(servconn);

        mService = null;
        LogUtil.i(TAG, "unitPlug");
    }

    @Override
    public IPutaoService getService() {
        return mService;
    }

    /**
     * 初始化插件中所有View
     */
    private void initPlugViewLayout(boolean needShowView){
        if(mPlugView == null){
            LayoutInflater inflater = LayoutInflater.from(mPlugContext);
            LogUtil.e(TAG, "getPlugView: start; needShowView: " + needShowView);
            mPlugView = inflater.inflate(R.layout.putao_yellow_page_plug_home, null, false);
            LogUtil.e(TAG, "getPlugView end:");
        }
        if( needShowView && mProgressBar == null){
            // 需要初始化布局 且之前没有初始化
            mProgressBar = (ProgressBar) mPlugView.findViewById(R.id.progress);
            mProgressBar.setVisibility(View.GONE);
            if(mPlugView.findViewById(R.id.plug_layout) == null){
                ViewStub viewStub = (ViewStub)mPlugView.findViewById(R.id.plug_layout_stub);
                viewStub.inflate();
            }

            mEditText =  (EditText) mPlugView.findViewById(R.id.search_content);
            mOffenGridView = (MyGridView)mPlugView.findViewById(R.id.first_gridView);
            mAllListView = (MyListView)mPlugView.findViewById(R.id.second_gridView);
            mOffenTView = (TextView)mPlugView.findViewById(R.id.first_cateory);
            mAllTView = (TextView)mPlugView.findViewById(R.id.second_cateory);
        }
        LogUtil.v(TAG, "getPlugView:" + mPlugView);
    }

    @Override
    public View getPlugView(Context hostContext) {
        activityContext = hostContext;
        initPlugViewLayout(false);
        return mPlugView;
    }

    private void showHotword(){
        if( mService == null || mEditText == null){
            return;
        }
        // 显示搜索关键字
        String searchHotword = "";
        try {
            searchHotword = mService.getNextHotword();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        LogUtil.d(TAG, "searchHotword: " + searchHotword);
        if( !TextUtils.isEmpty(searchHotword) ){
            mEditText.setHint(searchHotword);
        }
    }

    @Override
    public void onPlugStart() {
        LogUtil.d(TAG, "onPlugStart");
    }

    @Override
    public void onPlugResume() {
        LogUtil.d(TAG, "onPlugResume mService:"+getService()+";mPlugContext:"+mPlugContext+";activityContext:"+activityContext);
        LogUtil.d(TAG, "onPlugResume isPlugServiceWorked:"+YellowPagePlugUtil.isPlugServiceWorked(mPlugContext));

        /**
         * add by zjh 2014-09-29 start
         * 根据获取到的状态来检测是否需要刷新数据
         * (之前通过广播来通知刷新数据，会概率性的导致出现广播未接收的情况)
         */
        if( mService != null ){
            int refreshState = -1;
            try {
                refreshState = mService.getRefreshPlugViewState();
            } catch (RemoteException e) {
                refreshState = -1;
            }
            LogUtil.e(TAG, "onPlugResume refreshState: " + refreshState);
            if( refreshState == 0 ){
                // 刷新首页整个界面
                startDiscoverAsyncThread(DiscoverAsyncThread.REFRESH_DATA_NO_AD);
            }
        	/*
        	 * modified by hyl 2014-10-25 start
        	 * old code:
        	 * // 启动查询任务
                if (mAsyncThread == null || mAsyncThread.getStatus() == Status.FINISHED) {
                    mAsyncThread = new DiscoverAsyncThread();
                    mAsyncThread.execute();
                }
        	 */
            isResume = true;
            //modified by hyl 2014-10-25 end
        }
        /** add by zjh 2014-09-29 end  */

        LogUtil.d(TAG, "onPlugResume end");
    }

    @Override
    public void onPlugPause() {
        LogUtil.d(TAG, "onPlugPause");
        // 取消查询任务
        if (mAsyncThread != null) {
            mAsyncThread.interrupt();
            mAsyncThread = null;
        }
    }


    @Override
    public void onPlugStop() {
        LogUtil.d(TAG, "onPlugStop");
    }

    @Override
    public void onPlugDestory() {
        LogUtil.d(TAG, "onPlugDestory");
    }

    @Override
    public void onPlugIn(){
        isPlugIn = true;//add by hyl 2014-11-24 记录当前处于黄页界面
        LogUtil.i(TAG, "onPlugIn getService():"+getService()+" servconn="+servconn+" addr="+this);

        /**
         * add by zjh 2014-10-14 start
         * 当进入插件页，PlugService被Kill时，重新绑定
         */
        if( mService == null && mPlugContext != null && activityContext != null ){
            boolean result = false;
            if(YellowPagePlugUtil.isPlugServiceWorked(mPlugContext)) {
                result = bindService(mHostContext, mPlugContext);
                LogUtil.w(TAG, "onPlugIn restart bind service result="+result);
            } else {
                result = initPlug(mHostContext, mPlugContext);
                LogUtil.w(TAG, "onPlugIn restart init plug result="+result);
            }
            return;
        }
        /** add by zjh 2014-10-14 end  */

        try {
            if(getService() != null) {
                getService().plugResume();
                /*
                 * 将查询打点信息的逻辑从 onPlugResume方法中 移入到此处，
                 * 避免每次onPlugResume时就调用查询任务，而是等到用户滑动到黄页生活时才进行查询
                 * add by hyl 2014-10-25 start
                 */
                /*
                 * 增加needRefreshData 判断是否需要刷新数据
                 * modified by hyl 2014-11-24 start
                 * old code:
                 * if(isResume){
                    // 启动查询任务
                    startDiscoverAsyncThread(DiscoverAsyncThread.REFRESH_REMIND);
                    isResume = false;
                    }
                 */
                if(isResume || needRefreshData){
                    // 启动查询任务
                    startDiscoverAsyncThread(DiscoverAsyncThread.REFRESH_REMIND);
                    if(needRefreshData){
                        needRefreshData = false;
                    }
                    isResume = false;
                }
                // added by cj 2014/12/03 start
                // 默认给发现页消点
                onRemindClick();
                // added by cj 2014/12/03 end

                //modified by hyl 2014-11-24 end
                //add by hyl 2014-10-25 end

                /**
                 * 如果首页tab产生了点，滑入插件要消点，消点后刷新
                 * add by cj 2014-10-27 start
                 */
                mPlugContext.sendBroadcast(new Intent(ConstantsParameter.ACTION_REMIND_UPDATE));
                // add by cj 2014-10-27 end
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        LogUtil.d(TAG, "onPlugIn end");
    }

    @Override
    public void onPlugOut(){
        LogUtil.d(TAG, "onPlugOut");
        isPlugIn = false;//add by hyl 2014-11-24 记录当前不处于黄页界面
        try {
            if(getService() != null) {
                getService().plugPause();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新黄页数据
     * @param needUpdateAd
     * [true]需要刷新广告栏
     * [false]不需要刷新广告栏
     */
    @SuppressWarnings("unchecked")
    public synchronized void refreshData(final boolean needUpdateAd) {
        LogUtil.i(TAG, "refreshData needUpdateAd: " + needUpdateAd);
        if (getService() == null) {
            LogUtil.w(TAG, "plug service not init,please init service addr="+this);
            return;
        }

        try {
            // 读取分类数据
            String categorys = getService().queryCategoryByParentId(0);
            if (!TextUtils.isEmpty(categorys)) {
                categoryBeans = (List<CategoryBean>)ConvUtil.convertBase64StringToObj(categorys);
            }

            CategoryBean offenbean = null;
            if (categoryBeans != null && categoryBeans.size() > 1) {
                offenbean = categoryBeans.get(1);
            }

            // 读取分类数据 - 常用服务
            String offencategoryStr = "";
            if (offenbean != null) {
                offencategoryStr = getService().queryCategoryByParentId(
                        (int)offenbean.getCategory_id());
            }

            if (!TextUtils.isEmpty(offencategoryStr)) {
                mOffenCategoryBeans = (List<CategoryBean>)ConvUtil
                        .convertBase64StringToObj(offencategoryStr);
                LogUtil.i(TAG, "refreshData mOffenCategoryBeansSize: " + mOffenCategoryBeans.size());
            } else {
                // 无数据
                if (mOffenCategoryBeans != null) {
                    mOffenCategoryBeans.clear();
                }
            }

            // 读取分类数据 - 全部服务
            CategoryBean allbean = null;
            if (categoryBeans != null && categoryBeans.size() > 2) {
                allbean = categoryBeans.get(2);
            }
            String allCategoryStr = "";
            if (allbean != null) {
                allCategoryStr = getService()
                        .queryCategoryByParentId((int)allbean.getCategory_id());
            }
            if (!TextUtils.isEmpty(allCategoryStr)) {
                mAllCategoryBeans = (List<CategoryBean>)ConvUtil
                        .convertBase64StringToObj(allCategoryStr);
                LogUtil.i(TAG, "refreshData mAllCategoryBeansSize: " + mAllCategoryBeans.size());
            } else {
                // 无数据
                if (mAllCategoryBeans != null) {
                    mAllCategoryBeans.clear();
                }
            }

            // 更新所有点的信息
            updateReminds();// add by hyl 2014-10-24

            initLiveTitleMap();//add by lisheng 2014-11-19 22:08:53

            // 更新界面
            if (needUpdateAd) {
                mHandler.sendEmptyMessage(INIT_VIEW_ACTION);
            } else {
                mHandler.sendEmptyMessage(INIT_VIEW_ACTION_BUT_AD);
            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case INIT_VIEW_ACTION:
                    initViews(true);
                    break;
                case INIT_VIEW_ACTION_BUT_AD:
                    initViews(false);
                    break;
                case REFRESH_AD_VIEWS://刷新广告条数据
                    PushAdBean adBean = (PushAdBean)msg.obj;
                    int pageIndex = adBean.getAd_page_index();
                    if( mAdLayoutTop == null ){
                        mAdLayoutTop = (AdOperatLayout) mPlugView.findViewById(R.id.adlayout_top);
                    }
                    if( mAdLayoutMiddle == null ){
                        mAdLayoutMiddle = (AdOperatLayout) mPlugView.findViewById(R.id.adlayout_middle);
                    }
                    if( mAdLayoutBottom == null ){
                        mAdLayoutBottom = (AdOperatLayout) mPlugView.findViewById(R.id.adlayout_bottom);
                    }

                    mAdLayoutCallback = new AdLayoutCallback();
                    if( pageIndex == 1 ){
                        // 首页顶部广告位置
                        mAdLayoutTop.setCallback(mAdLayoutCallback);
                        mAdLayoutTop.setIUMengCallback(YellowPagePlug.this);
                        mAdLayoutTop.setAdImg(false, activityContext, adBean, false, pageIndex, true);
                    }else if( pageIndex == 2 ){
                        // 首页中间广告位置
                        mAdLayoutMiddle.setCallback(mAdLayoutCallback);
                        mAdLayoutMiddle.setIUMengCallback(YellowPagePlug.this);
                        mAdLayoutMiddle.setAdImg(false, activityContext, adBean, false, pageIndex, true);
                    }else if( pageIndex == 3 ){
                        // 首页底部广告位置
                        mAdLayoutBottom.setCallback(mAdLayoutCallback);
                        mAdLayoutBottom.setIUMengCallback(YellowPagePlug.this);
                        mAdLayoutBottom.setAdImg(false, activityContext, adBean, false, pageIndex, true);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * @param needUpdateAd
     * [true]需要刷新广告栏
     * [false]不需要刷新广告栏
     */
    private synchronized void initViews(boolean needUpdateAd) {
        LogUtil.v(TAG, "initViews needUpdateAd: " + needUpdateAd);
        if (mService == null) {
            LogUtil.i(TAG, "initViews mService is null.");
            return;
        }
        initPlugViewLayout(true);

        // 搜索框点击事件处理
        mEditText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mPlugContext, YellowPageSearchNumberActivity.class);
                activityContext.startActivity(intent);
            }
        });

        CategoryBean offenbean = null;
        if(categoryBeans != null && categoryBeans.size() > 1){
            offenbean = categoryBeans.get(1);
        }
        CategoryBean allbean = null;
        if(categoryBeans != null && categoryBeans.size() > 2){
            allbean = categoryBeans.get(2);
        }

        // 常用服务
        if(offenbean != null){//modified by hyl 2014-10-15 增加空判断
            String secondName = offenbean.getShow_name();
            if (!TextUtils.isEmpty(secondName)) {
                secondName = ContactsHubUtils.getShowName(mPlugContext, secondName);
            }
            mOffenTView.setText(secondName);
            
            /*
             * modified by hyl 2014-12-23 start
             * old code:
             *  offenGridApt = new MyGridViewAdapter(mPlugContext);//modify by lisheng end;
            	offenGridApt.setData(mOffenCategoryBeans, liveTitleMaps, offenRemindMaps);// modify by lisheng end 2014-11-19
            	mOffenGridView.setAdapter(offenGridApt);
             */
            if(offenGridApt == null){
                offenGridApt = new MyGridViewAdapter(mPlugContext,mOffenCategoryBeans, liveTitleMaps, offenRemindMaps);
                mOffenGridView.setAdapter(offenGridApt);
            }else{
                offenGridApt.setData(mOffenCategoryBeans, liveTitleMaps, offenRemindMaps);
            }
            //modified by hyl 2014-12-23 end

            // 快捷服务
            mOffenGridView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    CategoryBean bean = mOffenCategoryBeans.get(arg2);
                    onCustomItemClick(bean);
                }
            });
        }

        // 全部服务
        if(allbean != null){ //modified by hyl 2014-10-15 增加空判断
            String firstname = allbean.getShow_name();
            if (!TextUtils.isEmpty(firstname)) {
                firstname = ContactsHubUtils.getShowName(mPlugContext, firstname);
            }
            mAllTView.setText(firstname);
            
            /*
             * modified by hyl 2014-12-23 start
             * old code:
             *  allListApt = new MyListViewAdapter(mPlugContext, this,mAllCategoryBeans, allRemindMaps);
            	allListApt.setData(mAllCategoryBeans, allRemindMaps);
            	mAllListView.setAdapter(allListApt);
             */
            if(allListApt == null){
                allListApt = new MyListViewAdapter(mPlugContext, this,mAllCategoryBeans, allRemindMaps);
                mAllListView.setAdapter(allListApt);
            }else{
                allListApt.setData(mAllCategoryBeans, allRemindMaps);
            }
            //modified by hyl 2014-12-23 end
        }

        // 显示搜索关键字
        showHotword();

        // 添加广告栏
        refreshAdData(needUpdateAd);

    }

    /**
     * 刷新广告数据
     */
    @SuppressWarnings("unchecked")
    private void refreshAdData(final boolean needUpdateAd){
        if( mPlugView == null || mService == null || activityContext == null ){
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                LogUtil.i(TAG, "refreshAdData: " + needUpdateAd);
                int serverCode = YellowPagePlugUtil.HOME_PAGE_AD_UPDATE_ID; // 首页code = 0
                String adListStr = null;
                try {
                    adListStr = mService.getOperateAdDataById(serverCode);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                List<PushAdBean> adList = null;
                if (!TextUtils.isEmpty(adListStr)) {
                    adList = (List<PushAdBean>)ConvUtil.convertBase64StringToObj(adListStr);
                }
                if( adList == null || adList.size() == 0 ){
                    return;
                }
                int size = adList.size();
                LogUtil.v(TAG, "refreshAdData adSize: " + size);
                for(int i = 0; i < size; i++){
                    PushAdBean adBean = adList.get(i);
                    adBean.setNeedRefresh(needUpdateAd);

                    //刷新广告数据
                    Message msg = new Message();
                    msg.what = REFRESH_AD_VIEWS;
                    msg.obj = adBean;
                    mHandler.sendMessage(msg);
                }
            }
        }).start();

    }

    /**
     * 更新打点节点
     * @return
     * @author putao_lhq modify for：</br>
     * #首页打点和气泡同时显示上限可配置：默认6个</br>
     * #若超过上限，则依据时间先后进行处理：</br>
     *（1）打点：就打点自动消除，不恢复</br>
     *（2）气泡：自动隐藏，总数少于上限时恢复显示</br>
     * #显示上限可通过服务器配置</br>
     */
    @SuppressLint("UseSparseArrays")
    private boolean updateReminds() {
        boolean update = false;
        LogUtil.d(TAG, "updateReminds");
        // add by putao_lhq 2014年11月10日 for 打点上限 start
        long startTime = System.currentTimeMillis();
        synchronized (mAllReminds) {
            mAllReminds.clear();
            loadOffenRemind();//加载快捷服务打点数据
            loadAllRemind();//加载常用服务打点数据
            sortRemindByTime();//按照时间先后进行排序
            refactorAllReminds();//移除掉超过显示上限数据
            updateOffenRemind();
            updateAllRemind();
            //LogUtil.d(TAG, "total size is : " + (allRemindMaps.size() + offenRemindMaps.size()));
        }
        long endTime = System.currentTimeMillis();
        LogUtil.d(TAG, "total time is: " + (endTime - startTime));
        // add by putao_lhq 2014年11月10日 for 打点上限 end
        LogUtil.d(TAG, "updateReminds update="+update);
        return update;
    }

    /**
     * #首页打点和气泡同时显示上限可配置：默认6个</br>
     * #若超过上限，则依据时间先后进行处理：</br>
     *（1）打点：就打点自动消除，不恢复</br>
     *（2）气泡：自动隐藏，总数少于上限时恢复显示</br>
     * #显示上限可通过服务器配置</br>
     * @author putao_lhq
     */
    private void refactorAllReminds(){
        if (getService() == null) {
            LogUtil.d(TAG, "refactorAllReminds start, server is null");
            return;
        }
        int max = 6;
        try {
            max = getService().getRemindMaxCount();
        } catch (RemoteException e1) {
            e1.printStackTrace();
        }

        LogUtil.d(TAG, "remind count is: " + mAllReminds.size() + " ,max count is: " + max);
        if (mAllReminds == null || mAllReminds.size() <= max) {
            return;
        }
        for (int i = max; i < mAllReminds.size(); i++) {
            RemindBean bean = mAllReminds.get(i);
            if (bean.getRemindType() != RemindConfig.REMIND_TYPE_TIME_CLEAN) {
                try {
                    if (getService() != null) {
                        getService().onRemindClick(bean.getRemindCode());
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            // modify by putao_lhq 2014年12月3日 start
            //i--;
            mAllReminds.remove(i);
            i--;
            // modify by putao_lhq 2014年12月3日 end
        }
        LogUtil.d(TAG, "refactor remind count is: " + mAllReminds.size());
    }
    /**
     * 根据时间先后顺序对需要打点节点进行排序.
     * deleted by cj 2015/01/24 for 删除该功能，避免酷派要更新putaosdk
     * @author putao_lhq
     */
    private void sortRemindByTime() {/*
		Collections.sort(mAllReminds, new Comparator<RemindBean>() {

			@Override
			public int compare(RemindBean lhs, RemindBean rhs) {
				if (lhs.getInsertTime() > rhs.getInsertTime()) {
					return -1;
				} else if (lhs.getInsertTime() < rhs.getInsertTime()){
					return 1;
				}
				return 0;
			}
		});
		for(int i = 0; i < mAllReminds.size(); i++) {
			LogUtil.d(TAG, "sort result: " + mAllReminds.get(i).toString());
		}
	*/}

    /**
     * 加载常用项需要打点信息
     * @author putao_lhq
     */
    private void loadAllRemind() {
        if(mAllCategoryBeans != null && mAllCategoryBeans.size() > 0) {
            if(allRemindMaps == null) {
                allRemindMaps = new HashMap<Integer, RemindBean>();
            } else {
                allRemindMaps.clear();
            }

            for(CategoryBean bean : mAllCategoryBeans) {
                RemindBean remind = getRemind(bean.getRemind_code());
                if(remind != null && remind.getRemindType()>RemindConfig.REMIND_TYPE_NONE) {
                    mAllReminds.add(remind);
                }
            }
        }
    }

    /**
     * 更新常用项打点
     * @author putao_lhq
     */
    private void updateAllRemind() {
        if(mAllCategoryBeans != null && mAllCategoryBeans.size() > 0) {
            if(allRemindMaps == null) {
                allRemindMaps = new HashMap<Integer, RemindBean>();
            } else {
                allRemindMaps.clear();
            }

            for(CategoryBean bean : mAllCategoryBeans) {
                RemindBean remind = getRemindFromAll(bean.getRemind_code());
                if(remind != null && remind.getRemindType()>RemindConfig.REMIND_TYPE_NONE) {
                    allRemindMaps.put(bean.getRemind_code(), remind);
                }
            }
        }
    }

    /**
     * 加载快捷服务打点信息
     * @author putao_lhq
     */
    private void loadOffenRemind() {
        if(mOffenCategoryBeans != null && mOffenCategoryBeans.size() > 0) {
            if(offenRemindMaps == null) {
                offenRemindMaps = new HashMap<Integer, RemindBean>();
            } else {
                offenRemindMaps.clear();
            }
            for(CategoryBean bean : mOffenCategoryBeans) {
                RemindBean remind = getRemind(bean.getRemind_code());

                if(bean.getCategory_id() == 60 || bean.getRemind_code() == 11) {
                    // 增加”我的“日志，为诊断我的没有点的BUG
                    LogUtil.d(TAG, "My Icon CategoryBean: "+bean.toString());
                    LogUtil.d(TAG, "My Icon RemindBean: "+(remind!=null?remind.toString():""));
                }

                if(remind != null) {
                    //offenRemindMaps.put(bean.getRemind_code(), remind);
                    //update = true;
                    mAllReminds.add(remind);
                }
            }
        }
    }


    //add by lisheng 2014-11-19 21:54:53 start
    /**加载livetitle的数据 */
    private void initLiveTitleMap(){
        if(mOffenCategoryBeans != null && mOffenCategoryBeans.size() > 0) {
            if(liveTitleMaps == null) {
                liveTitleMaps = new HashMap<Integer, YellowPageLiveTitleDataBean>();
            } else {
                liveTitleMaps.clear();
            }
            int delay = 1;
            for (CategoryBean bean : mOffenCategoryBeans) {
                if(!TextUtils.isEmpty(bean.getExpand_param())){
                    YellowPageLiveTitleDataBean ypldb = parse2LiveTitleBean(
                            bean.getExpand_param(), (int) bean.getCategory_id());
                    if (ypldb != null) {
                        if (ypldb.getDismissTime() >= System.currentTimeMillis()) {
                            ypldb.setDelay((delay++) * 2000);
                            liveTitleMaps.put((int) bean.getCategory_id(), ypldb);
                        } else {
                            ContactsAppUtils.getInstance().getDatabaseHelper().getYellowPageDBHelper().updateExpandParamById(bean.getCategory_id());
                        }
                    }
                }
            }
        }

    }
    //add by lisheng end


    /**
     * 更新快捷服务打点
     * @author putao_lhq
     */
    private void updateOffenRemind() {
        if(mOffenCategoryBeans != null && mOffenCategoryBeans.size() > 0) {
            if(offenRemindMaps == null) {
                offenRemindMaps = new HashMap<Integer, RemindBean>();
            } else {
                offenRemindMaps.clear();
            }

            for(CategoryBean bean : mOffenCategoryBeans) {
                RemindBean remind = getRemindFromAll(bean.getRemind_code());

                if(bean.getCategory_id() == 60 || bean.getRemind_code() == 11) {
                    // 增加”我的“日志，为诊断我的没有点的BUG
                    LogUtil.d(TAG, "My Icon CategoryBean: "+bean.toString());
                    LogUtil.d(TAG, "My Icon RemindBean: "+(remind!=null?remind.toString():""));
                }

                if(remind != null) {
                    offenRemindMaps.put(bean.getRemind_code(), remind);
                }
            }
        }
    }

    /**
     * 从保存的所有打点信息中获取对应节点打点信息
     * @param remind_code
     * @return
     * @author putao_lhq
     */
    private RemindBean getRemindFromAll(int remind_code) {
        for (RemindBean bean : mAllReminds) {
//			LogUtil.d(TAG, "getRemindFromAll: " + remind_code
//					+ " ,bean code is: " + bean.getRemindCode());
            if (bean.getRemindCode() == remind_code) {
                return bean;
            }
        }
        return null;
    }

    /**
     * 首页刷新各view使用统一的入口，保持同步和顺序执行
     * added by cj 2014/10/29
     * @param cmd
     * </br>modify by putao_lhq
     */
    private synchronized void startDiscoverAsyncThread(int cmd) {
        // 启动查询任务
        if (mAsyncThread == null || !mAsyncThread.isAlive()) {
            mAsyncThread = new DiscoverAsyncThread(cmd);
            mAsyncThread.start();
        }
    }

    private class DiscoverAsyncThread extends Thread {
        public static final int REFRESH_DATA_NO_AD = 0;
        public static final int REFRESH_DATA_WITH_AD = 1;
        public static final int REFRESH_REMIND = 2;

        private int cmd;
        public DiscoverAsyncThread(int cmd) {
            this.cmd = cmd;
        }

        @Override
        public void run() {
            LogUtil.d(TAG, "DiscoverAsyncThread run cmd="+cmd);
            if(REFRESH_DATA_NO_AD == cmd) {
                refreshData(false);
            } else if(REFRESH_DATA_WITH_AD == cmd) {
                refreshData(true);
            } else if(REFRESH_REMIND == cmd) {
                /**
                 * 不单独刷新打点，避免在刷新点时，编辑图标后没有执行刷新整个界面。
                 * 所以打点也刷新整个界面。
                 * added by cj 2014/11/05 start
                 */
                refreshData(false);
            }
            mAsyncThread = null;
        }
    };

    public void onRemindClick(int remindCode) {
        if (mService != null) {
            try {
                LogUtil.d(TAG, "onRemindClick remindCode="+remindCode);
                mService.onRemindClick(remindCode);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 模拟点击发现页节点
     *
     * @param nodeName
     */
    // @sdk api
    public void onRemindClick() {
        onRemindClick(RemindConfig.DiscoverPageNode);
    }

    /**
     * 获取黄页打点信息
     * @return
     */
    // @sdk api
    public String getDiscoverRemind() {
        String remindInfo = "";
        if (mService != null) {
            try {
                remindInfo = mService.getRemind(RemindConfig.DiscoverPageNode);
                RemindBean remindBean = (RemindBean)ConvUtil.convertBase64StringToObj(remindInfo);
                if(remindBean != null) {
                    LogUtil.i(TAG, "getDiscoverRemind "+remindBean.toString());
                }

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return remindInfo;
    }

    /**
     * 得到指定remindCode打点元数据
     */
    public RemindBean getRemind(Integer remindCode) {
        RemindBean remindBean = null;
        if (mService != null) {
            try {
                String remindInfo = mService.getRemind(remindCode);
                if(TextUtils.isEmpty(remindInfo))
                    return null;
                remindBean = (RemindBean)ConvUtil.convertBase64StringToObj(remindInfo);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return remindBean;
    }

    public boolean cancelMark(String number, String mark) {
        try {
            return getService().cancelMark(number, mark);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 标记号码
     *
     * @param number
     * @param mark
     * @return
     * @throws RemoteException
     */
    public boolean uploadMark(String number, String mark) throws RemoteException {
        return getService().uploadMark(number, mark);
    }

    /**
     * 获取用户标记过的所有号码信息
     *
     * @return
     * @throws RemoteException
     */
    public String getAllUserMark() throws RemoteException {
        String allUserMark = getService().getAllUserMark();
        return allUserMark;
    }

    /**
     * 获取用户标记信息
     *
     * @param number
     * @return
     * @throws RemoteException
     */
    public String getUserMark(String number) throws RemoteException {
        return getService().getUserMark(number);
    }

    /**
     * 从本地查询
     *
     * @param number
     * @return
     * @throws RemoteException
     */
    public String checkNumberFromLocal(String number) throws RemoteException {
        return getService().checkNumberFromLocal(number);
    }

    /**
     * 从网络查询
     *
     * @param number
     * @return
     * @throws RemoteException
     */
    public String checkNumberFromNet(String number) throws RemoteException {
        return getService().checkNumberFromNet(number);
    }

    /**
     * 启动查号
     *
     * @param context
     */
    public void startSearchNumber(Context context) {
        Intent intent = new Intent(mPlugContext, YellowPageSearchNumberActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onCustomItemClick(CategoryBean bean) {
        if( bean == null ){
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
        if (params == null){
            params = new YellowParams();
        }

        try {
            //add ljq 2014_11_10 start 如果是Web型则做进入的优化
            Intent intent = null;
            Class cls = Class.forName(targetActivityName);
            if (YellowPageH5Activity.class.isAssignableFrom(cls)) {
                intent = new Intent(mPlugContext, YellowPageJumpH5Activity.class);
                intent.putExtra("targetActivityName", targetActivityName);
            }else{
                intent = new Intent(mPlugContext, cls);
            }
            //add ljq 2014_11_10 end 如果是Web型则做进入的优化
            params.setCategory_id(bean.getCategory_id());
            params.setCategory_name(bean.getName());
            params.setRemindCode(bean.getRemind_code());
            params.setEntry_type(YellowParams.ENTRY_TYPE_HOME_PAGE);

            //add ljq 2014_11_19 start 加上模拟点击
            onRemindClick(bean.getRemind_code());
            //add ljq 2014_11_19 end 加上模拟点击

            if (TextUtils.isEmpty(params.getTitle())) {
                String showName = ContactsHubUtils.getShowName(mPlugContext, bean.getShow_name());
                params.setTitle(showName);
            }
            intent.putExtra(YellowUtil.TargetIntentParams, params);

            activityContext.startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private MyBroadcastReceiver receiver = null;
    private void registerYellowPageDataReceiver(Context mContext) {
        // 注册黄页数据 和 广告栏 更新广播接收器
        receiver = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter(ConstantsParameter.ACTION_YELLOW_DATA_UPATE_PLUG);
        filter.addAction(ConstantsParameter.ACTION_UPDATE_AD);
        filter.addAction(ConstantsParameter.ACTION_INITDATA_FINISHED);//add by hyl 2014-11-24 数据初始化完毕
        filter.addAction(ConstantsParameter.ACTION_REMIND_UPDATE_PLUG);//add by cj 2014-12-30 合并两个BroadcastReceiver
        mContext.registerReceiver(receiver, filter);
    }

    private void unRegisterYellowPageDataReceiver(Context mContext) {
        if (receiver != null) {
            mContext.unregisterReceiver(receiver);
        }
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            String action = arg1.getAction();
            LogUtil.d(TAG, "action:" + action);
            if (ConstantsParameter.ACTION_YELLOW_DATA_UPATE_PLUG.equals(action)||
                    ConstantsParameter.ACTION_LIVETITLE_UPDATE_PLUG.equals(action)) {//modify by lisheng 增加一个广播的判断;2014-11-07 ;
                startDiscoverAsyncThread(DiscoverAsyncThread.REFRESH_DATA_NO_AD);
            }else if(ConstantsParameter.ACTION_UPDATE_AD.equals(action) ){
                // 刷新首页广告数据 add by zjh 2014-10-10
                startDiscoverAsyncThread(DiscoverAsyncThread.REFRESH_DATA_WITH_AD);
            }else if(ConstantsParameter.ACTION_INITDATA_FINISHED.equals(action)){

                if(mPlugContext != null) {
                    mPlugContext.sendBroadcast(new Intent(ConstantsParameter.ACTION_REMIND_UPDATE));
                }
                /*
                 * 判断当前是否处于黄页界面，处于黄页界面时进行数据刷新,否则使用needRefreshData记录下数据需要刷新
                 * modified by hyl 2014-11-24 start 
                 * old code:
                 * startDiscoverAsyncThread(DiscoverAsyncThread.REFRESH_DATA_WITH_AD);
                 */
                if(isPlugIn){
                    startDiscoverAsyncThread(DiscoverAsyncThread.REFRESH_DATA_WITH_AD);
                }else{
                    needRefreshData = true;
                }
                //modified by hyl 2014-11-24 end
            }else if(ConstantsParameter.ACTION_REMIND_UPDATE_PLUG.equals(action)) {
                // 启动查询任务
                startDiscoverAsyncThread(DiscoverAsyncThread.REFRESH_REMIND);
            }
        }
    }

    //lihq add for plug-1.3 能力API start
    public void startQueryExp(Context context, String exp_num) {
        LogUtil.d(TAG, "startQueryExpress for plug 1.3");
        Intent intent = new Intent(mPlugContext, YellowPageExpressSelectHome.class);
        intent.putExtra("exp_num", exp_num);
        context.startActivity(intent);

    }

    public void startQueryRecharge(Context context, String phoneNumber) {
        LogUtil.d(TAG, "startQueryRecharge for plug 1.3");
        Intent intent = new Intent(mPlugContext, YellowPageReChargeActivity.class);
        intent.putExtra("phone_num", phoneNumber);
        context.startActivity(intent);

    }

    public String parseMMS(String body) {
        return SDKApiUtil.parse(body);
    }

    public void startSearchTel(String tel) {
        if( mService != null ){
            try {
                mService.perceptTel(tel);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public void startShopDetailActivity(Context context, String base64) {
        YelloPageItem itemGaoDe = SDKApiUtil.getYellowPageItem(base64);
        Intent intent = new Intent(mPlugContext, YellowPageShopDetailActivity.class);
        intent.putExtra("CategoryId", 0L);
        Bundle bundle = new Bundle();
        bundle.putSerializable("YelloPageItem", itemGaoDe);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
    //lihq add for plug-1.3 能力API end

    @Override
    public void onEvent(String umengEventId) {
        // TODO Auto-generated method stub
        if( mService != null && !TextUtils.isEmpty(umengEventId) ){
            try {
                mService.addUMengEvent(umengEventId, -1);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private class AdLayoutCallback implements AdOperatLayout.AdLayoutCallback {
        @Override
        public String getReqTailSign() {
            String reqUrlTail = "";
            if(mService != null) {
                try {
                    reqUrlTail = mService.getRequrlOfSignTail();
                } catch (RemoteException e) {
                }
            }
            return reqUrlTail;
        }

        @Override
        public void deleteAdBean(int serverCode, int pageIndex) {
            // TODO Auto-generated method stub
            if( mService != null ){
                try {
                    mService.deleteOperateAdData(serverCode, pageIndex);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

    };

    // add by lisheng start 2014-11-19
    private YellowPageLiveTitleDataBean parse2LiveTitleBean(String expand_param,int code) {
        YellowPageLiveTitleDataBean bean =null;
        JSONObject expandObj = null;
        try {
            expandObj=new JSONObject(expand_param);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(expandObj == null){
            return null;
        }
        bean = new YellowPageLiveTitleDataBean();
        bean.setCode(code);
        try {
            if (expandObj.has("color"))
                bean.setColor((int) Long.parseLong(expandObj.getString("color"), 16));
            if (expandObj.has("text"))
                bean.setText(expandObj.getString("text"));
            if (expandObj.has("imgUrl"))
                bean.setImgUrl(expandObj.getString("imgUrl"));
            if (expandObj.has("dismissTime"))
                bean.setDismissTime(expandObj.getLong("dismissTime"));
            if (expandObj.has("textSize"))
                bean.setTextSize((float) expandObj.getDouble("textSize"));
            if (expandObj.has("keyWordColor"))
                bean.setKeyWordColor((int) Long.parseLong(expandObj.getString("keyWordColor"), 16));
            if (expandObj.has("keyWordStart"))
                bean.setKeyWordStart(expandObj.getInt("keyWordStart"));
            if (expandObj.has("keyWordEnd"))
                bean.setKeyWordEnd(expandObj.getInt("keyWordEnd"));
            if (expandObj.has("keyWordSize"))
                bean.setKeyWordSize((float) expandObj.getDouble("keyWordSize"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }
    //add by lisheng end
}
