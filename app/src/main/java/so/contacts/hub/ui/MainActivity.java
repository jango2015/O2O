package so.contacts.hub.ui;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import so.contacts.hub.active.ActiveUtils;
import so.contacts.hub.adapter.MyGridViewAdapter;
import so.contacts.hub.adapter.MyListViewAdapter;
import so.contacts.hub.adapter.MyListViewListener;
import so.contacts.hub.core.ConstantsParameter;
import so.contacts.hub.push.bean.PushAdBean;
import so.contacts.hub.remind.RemindConfig;
import so.contacts.hub.remind.RemindManager;
import so.contacts.hub.remind.YellowPageLiveTitleDataBean;
import so.contacts.hub.remind.utils.RemindUtils;
import so.contacts.hub.service.InitDataService;
import so.contacts.hub.ui.web.YellowPageH5Activity;
import so.contacts.hub.ui.yellowpage.YellowPageJumpH5Activity;
import so.contacts.hub.ui.yellowpage.YellowPageSearchNumberActivity;
import so.contacts.hub.ui.yellowpage.bean.CategoryBean;
import so.contacts.hub.ui.yellowpage.bean.YellowParams;
import so.contacts.hub.util.ContactsAppUtils;
import so.contacts.hub.util.ContactsHubUtils;
import so.contacts.hub.util.ConvUtil;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.SearchHotwordUtil;
import so.contacts.hub.util.YellowPagePlugUtil;
import so.contacts.hub.util.YellowUtil;
import so.contacts.hub.widget.AdOperatLayout;
import so.contacts.hub.widget.MyGridView;
import so.contacts.hub.widget.MyListView;
import so.contacts.hub.yellow.data.RemindBean;
import so.putao.aidl.IPutaoService;

import com.google.gson.Gson;
import com.yulong.android.contacts.discover.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity implements MyListViewListener{
    private static final String TAG = "YellowPage";

    private Context mContext = null;

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
    private List<RemindBean> mAllReminds = new ArrayList<RemindBean>();
    /** 数据对象  end */

    private boolean isResume = false;
    private DiscoverAsyncThread mAsyncThread = null;
    private AdLayoutCallback mAdLayoutCallback = null;

    public static final int INIT_VIEW_ACTION = 0x2001;
    public static final int INIT_VIEW_ACTION_BUT_AD = 0x2002;
    private static final int REFRESH_AD_VIEWS = 0x2003;

    private boolean needRefreshData = false;
    private boolean isPlugIn = false;

    private MyBroadcastReceiver receiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.putao_yellow_page_plug_home);
        mContext = this;

        Intent intent = new Intent(this, InitDataService.class);
        startService(intent);

        mProgressBar = (ProgressBar)findViewById(R.id.progress);
        mProgressBar.setVisibility(View.GONE);
        ViewStub viewStub = (ViewStub)findViewById(R.id.plug_layout_stub);
        viewStub.inflate();
        mEditText =  (EditText)findViewById(R.id.search_content);
        mOffenGridView = (MyGridView)findViewById(R.id.first_gridView);
        mAllListView = (MyListView)findViewById(R.id.second_gridView);
        mOffenTView = (TextView)findViewById(R.id.first_cateory);
        mAllTView = (TextView)findViewById(R.id.second_cateory);

        unRegisterYellowPageDataReceiver(mContext);
        registerYellowPageDataReceiver(mContext);

        startDiscoverAsyncThread(DiscoverAsyncThread.REFRESH_DATA_NO_AD);
//		initView();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unRegisterYellowPageDataReceiver(mContext);
    }

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

    private void initView(){
        // 搜索框点击事件处理
        mEditText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, YellowPageSearchNumberActivity.class);
                mContext.startActivity(intent);
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
        if(offenbean != null){
            String secondName = offenbean.getShow_name();
            if (!TextUtils.isEmpty(secondName)) {
                secondName = ContactsHubUtils.getShowName(mContext, secondName);
            }
            mOffenTView.setText(secondName);
            if(offenGridApt == null){
                offenGridApt = new MyGridViewAdapter(mContext,mOffenCategoryBeans, liveTitleMaps, offenRemindMaps);
                mOffenGridView.setAdapter(offenGridApt);
            }else{
                offenGridApt.setData(mOffenCategoryBeans, liveTitleMaps, offenRemindMaps);
            }
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
                firstname = ContactsHubUtils.getShowName(mContext, firstname);
            }
            mAllTView.setText(firstname);
            if(allListApt == null){
                allListApt = new MyListViewAdapter(mContext, this,mAllCategoryBeans, allRemindMaps);
                mAllListView.setAdapter(allListApt);
            }else{
                allListApt.setData(mAllCategoryBeans, allRemindMaps);
            }
        }
        // 显示搜索关键字
        showHotword();

        // 添加广告栏
        refreshAdData(true);
    }
    private void showHotword(){
        if( mEditText == null){
            return;
        }
        // 显示搜索关键字
        String searchHotword = SearchHotwordUtil.getInstance().getNextHotword();
        LogUtil.d(TAG, "searchHotword: " + searchHotword);
        if( !TextUtils.isEmpty(searchHotword) ){
            mEditText.setHint(searchHotword);
        }
    }
    public synchronized void refreshData(final boolean needUpdateAd) {
        LogUtil.i(TAG, "refreshData needUpdateAd: " + needUpdateAd);
        // 读取分类数据
        String categorys = queryCategoryByParentId(0);
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
            offencategoryStr = queryCategoryByParentId(
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
            allCategoryStr = queryCategoryByParentId((int)allbean.getCategory_id());
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
    }
    public String queryCategoryByParentId(int parent_id){
        List<CategoryBean> categoryList = ContactsAppUtils.getInstance().getDatabaseHelper()
                .getYellowPageDBHelper().queryCategoryByParentId(parent_id);
        if (categoryList == null || categoryList.size() == 0)
            return "";
        return ConvUtil.convertObjToBase64String(categoryList);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case INIT_VIEW_ACTION:
                    initView();
                    break;
                case INIT_VIEW_ACTION_BUT_AD:
                    initView();
                    break;
                case REFRESH_AD_VIEWS://刷新广告条数据
                    PushAdBean adBean = (PushAdBean)msg.obj;
                    int pageIndex = adBean.getAd_page_index();
                    if( mAdLayoutTop == null ){
                        mAdLayoutTop = (AdOperatLayout) findViewById(R.id.adlayout_top);
                    }
                    if( mAdLayoutMiddle == null ){
                        mAdLayoutMiddle = (AdOperatLayout) findViewById(R.id.adlayout_middle);
                    }
                    if( mAdLayoutBottom == null ){
                        mAdLayoutBottom = (AdOperatLayout) findViewById(R.id.adlayout_bottom);
                    }

                    mAdLayoutCallback = new AdLayoutCallback();
                    if( pageIndex == 1 ){
                        // 首页顶部广告位置
                        mAdLayoutTop.setCallback(mAdLayoutCallback);
                        mAdLayoutTop.setAdImg(false, mContext, adBean, false, pageIndex, true);
                    }else if( pageIndex == 2 ){
                        // 首页中间广告位置
                        mAdLayoutMiddle.setCallback(mAdLayoutCallback);
                        mAdLayoutMiddle.setAdImg(false, mContext, adBean, false, pageIndex, true);
                    }else if( pageIndex == 3 ){
                        // 首页底部广告位置
                        mAdLayoutBottom.setCallback(mAdLayoutCallback);
                        mAdLayoutBottom.setAdImg(false, mContext, adBean, false, pageIndex, true);
                    }
                    break;
                default:
                    break;
            }
        }
    };
    /**
     * 刷新广告数据
     */
    private void refreshAdData(final boolean needUpdateAd){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                LogUtil.i(TAG, "refreshAdData: " + needUpdateAd);
                int serverCode = YellowPagePlugUtil.HOME_PAGE_AD_UPDATE_ID; // 首页code = 0
                String adListStr = null;
                try {
                    adListStr = getOperateAdDataById(serverCode);
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
    public String getOperateAdDataById(int serverCode) throws RemoteException {
        // TODO Auto-generated method stub
        // 获取广告数据
        List<PushAdBean> adList = ContactsAppUtils.getInstance().getDatabaseHelper()
                .getYellowPageDBHelper().queryAdDataById(serverCode);
        return ConvUtil.convertObjToBase64String(adList);
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
    /**
     * #首页打点和气泡同时显示上限可配置：默认6个</br>
     * #若超过上限，则依据时间先后进行处理：</br>
     *（1）打点：就打点自动消除，不恢复</br>
     *（2）气泡：自动隐藏，总数少于上限时恢复显示</br>
     * #显示上限可通过服务器配置</br>
     * @author putao_lhq
     */
    private void refactorAllReminds(){
        int max = 6;
        max = RemindUtils.getRemindMaxCount();
        LogUtil.d(TAG, "remind count is: " + mAllReminds.size() + " ,max count is: " + max);
        if (mAllReminds == null || mAllReminds.size() <= max) {
            return;
        }
        for (int i = max; i < mAllReminds.size(); i++) {
            RemindBean bean = mAllReminds.get(i);
            if (bean.getRemindType() != RemindConfig.REMIND_TYPE_TIME_CLEAN) {
                RemindManager.onRemindClick(bean.getRemindCode());
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
//				LogUtil.d(TAG, "getRemindFromAll: " + remind_code
//						+ " ,bean code is: " + bean.getRemindCode());
            if (bean.getRemindCode() == remind_code) {
                return bean;
            }
        }
        return null;
    }
    /**
     * 得到指定remindCode打点元数据
     */
    public RemindBean getRemind(Integer remindCode) {
        RemindBean remindBean = null;
        String remindInfo = getremind(remindCode);
        if(TextUtils.isEmpty(remindInfo))
            return null;
        remindBean = (RemindBean)ConvUtil.convertBase64StringToObj(remindInfo);
        return remindBean;
    }
    public String getremind(int remindCode){
        RemindBean bean = RemindUtils.getRemind(remindCode);
        if(bean == null || bean.getRemindType() <= RemindConfig.REMIND_TYPE_NONE) {
            bean = RemindUtils.getBubbleRemind(remindCode);
            if(bean == null || bean.getRemindType() <= RemindConfig.REMIND_TYPE_NONE) {
                return "";
            }
        }
        return ConvUtil.convertObjToBase64String(bean);
    }

    private class AdLayoutCallback implements AdOperatLayout.AdLayoutCallback {
        @Override
        public String getReqTailSign() {
            String reqUrlTail = "";
            reqUrlTail = ActiveUtils.getRequrlOfSignTail();
            return reqUrlTail;
        }

        @Override
        public void deleteAdBean(int serverCode, int pageIndex) {
            // TODO Auto-generated method stub
            // 删除广告数据
            ContactsAppUtils.getInstance().getDatabaseHelper().getYellowPageDBHelper()
                    .deleteAdData(serverCode, pageIndex);
        }

    }

    @Override
    public void onCustomItemClick(CategoryBean bean) {
        // TODO Auto-generated method stub
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
                intent = new Intent(mContext, YellowPageJumpH5Activity.class);
                intent.putExtra("targetActivityName", targetActivityName);
            }else{
                intent = new Intent(mContext, cls);
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
                String showName = ContactsHubUtils.getShowName(mContext, bean.getShow_name());
                params.setTitle(showName);
            }
            intent.putExtra(YellowUtil.TargetIntentParams, params);

            mContext.startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void onRemindClick(int remindCode) {
        RemindManager.onRemindClick(remindCode);
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

    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            String action = arg1.getAction();
            LogUtil.d(TAG, "action:" + action);
            if (ConstantsParameter.ACTION_YELLOW_DATA_UPATE_PLUG.equals(action)||
                    ConstantsParameter.ACTION_LIVETITLE_UPDATE_PLUG.equals(action)) {//modify by lisheng 增加一个广播的判断;2014-11-07 ;
                startDiscoverAsyncThread(DiscoverAsyncThread.REFRESH_DATA_NO_AD);
            }else if(ConstantsParameter.ACTION_UPDATE_AD.equals(action) ){

                startDiscoverAsyncThread(DiscoverAsyncThread.REFRESH_DATA_WITH_AD);
            }else if(ConstantsParameter.ACTION_INITDATA_FINISHED.equals(action)){

                if(mContext != null) {
                    mContext.sendBroadcast(new Intent(ConstantsParameter.ACTION_REMIND_UPDATE));
                }
	                /*
	                 * 判断当前是否处于黄页界面，处于黄页界面时进行数据刷新,否则使用needRefreshData记录下数据需要刷新
	                 * 
	                 * old code:
	                 * startDiscoverAsyncThread(DiscoverAsyncThread.REFRESH_DATA_WITH_AD);
	                 */
                if(isPlugIn){
                    startDiscoverAsyncThread(DiscoverAsyncThread.REFRESH_DATA_WITH_AD);
                }else{
                    needRefreshData = true;
                }
                //
            }else if(ConstantsParameter.ACTION_REMIND_UPDATE_PLUG.equals(action)) {
                // 启动查询任务
                startDiscoverAsyncThread(DiscoverAsyncThread.REFRESH_REMIND);
            }
        }
    }
}

