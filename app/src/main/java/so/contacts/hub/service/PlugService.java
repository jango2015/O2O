
package so.contacts.hub.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.http.HttpEntity;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.account.IAccChangeListener;
import so.contacts.hub.account.PutaoAccount;
import so.contacts.hub.active.ActiveUtils;
import so.contacts.hub.core.Config;
import so.contacts.hub.core.ConstantsParameter;
import so.contacts.hub.db.YellowPageDB;
import so.contacts.hub.http.bean.ActiveRequestData;
import so.contacts.hub.http.bean.ActiveResponseData;
import so.contacts.hub.http.bean.ActiveResponseData.HotKeyWordsConfig;
import so.contacts.hub.http.bean.ActiveResponseData.RecommendSearchWordsConfig;
import so.contacts.hub.http.bean.ActiveResponseData.RemindInfo;
import so.contacts.hub.http.bean.QueryFavoVoucherRequest;
import so.contacts.hub.msgcenter.report.MsgReport;
import so.contacts.hub.msgcenter.report.MsgReportParameter;
import so.contacts.hub.msgcenter.report.MsgReportThread;
import so.contacts.hub.msgcenter.report.MsgReportUtils;
import so.contacts.hub.push.PushParseFactory;
import so.contacts.hub.push.PushUtil;
import so.contacts.hub.push.bean.OpConfig;
import so.contacts.hub.push.bean.PushAdBean;
import so.contacts.hub.remind.BubbleRemindManager;
import so.contacts.hub.remind.RemindConfig;
import so.contacts.hub.remind.RemindManager;
import so.contacts.hub.remind.utils.RemindUtils;
import so.contacts.hub.search.SearchUtils;
import so.contacts.hub.search.bean.UpdateSearchDataRequest;
import so.contacts.hub.shuidianmei.WEGUtil;
import so.contacts.hub.thirdparty.cinema.tool.CinemaApiUtil;
import so.contacts.hub.ui.yellowpage.bean.CategoryBean;
import so.contacts.hub.util.ContactsAppUtils;
import so.contacts.hub.util.ConvUtil;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.NetUtil;
import so.contacts.hub.util.SearchBusinessUtil;
import so.contacts.hub.util.SearchHotwordUtil;
import so.contacts.hub.util.SearchRecommendwordUtil;
import so.contacts.hub.util.UMengEventIds;
import so.contacts.hub.util.UiHelper;
import so.contacts.hub.util.UserInfoUtil;
import so.contacts.hub.util.YellowPageDataUtils;
import so.contacts.hub.util.YellowPagePlugUtil;
import so.contacts.hub.util.YellowUtil;
import so.contacts.hub.yellow.data.PTNumber;
import so.contacts.hub.yellow.data.RemindBean;
import so.putao.aidl.ICallback;
import so.putao.aidl.IPutaoService;
import so.putao.findplug.LBSServiceGaode;
import so.putao.findplug.LBSServiceGaode.LBSServiceListener;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Base64;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.baidu.mapapi.SDKInitializer;
import com.mdroid.core.http.IgnitedHttpResponse;
import com.mdroid.core.util.SystemUtil;
import com.putao.analytics.util.EventUtil;
import com.sogou.hmt.sdk.manager.HMTNumber;
import com.sogou.hmt.sdk.manager.HmtSdkManager;

public class PlugService extends Service implements LBSServiceListener, IAccChangeListener, TagAliasCallback {
    private static final String TAG = "PlugService";

    public static final int HEART_BEAT = 1;
    private static final int MSG_SET_ALIAS = 1001;//add by hyl 2014-12-23
    private static final int MSG_LOAD_MOVIE_CITY = 1002;//add by hyl 2015-1-7
    public static final int MSG_REPORT = 1003;//add by cj 2015-1-22
    
    private static final int LOAD_MOVIE_CITY_INTERVAL_TIME = 24*60*1000;//add by hyl 2015-1-7 24小时间隔
    
    private static final int REPORT_NEXT_AFTER_TIME = 30*60*1000; // 下次上报的间隔时间
    
    public static final String ACTION_HEART_BEAT = "heartbeat";

    private Context mContext = null;

    private PlugServiceImpl mPlugServiceImpl = null;

    private SharedPreferences preferences;
    
    private MsgReportThread mMsgReportThread = null;

    private Handler mMainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HEART_BEAT: {
                    LogUtil.i(TAG, "handleMessage: " + msg.toString());
                    new Thread() {
                        @Override
                        public void run() {
                            doHeartBeat();
                            postHeartbeatDelay(false);
                        }
                    }.start();
                    break;
                }
                case MSG_SET_ALIAS:{//add by hyl 2014-12-23 设置别名和标签
                	PushUtil.setAliasTags(ContactsApp.getContext(), PlugService.this);
                	break;
                }
                case MSG_LOAD_MOVIE_CITY://add by hyl 2015-1-7
                {
                	new Thread(new Runnable() {
						public void run() {
							CinemaApiUtil.initMovieCityDB();
							mMainHandler.sendEmptyMessageDelayed(MSG_LOAD_MOVIE_CITY, LOAD_MOVIE_CITY_INTERVAL_TIME);
						}
					}).start();
                	break;
                }
                case MSG_REPORT:
                {
                    mMainHandler.removeMessages(MSG_REPORT);
                    //有未上报数据,则启动上报
                    if(NetUtil.isNetworkAvailable(PlugService.this)) {
                        if(MsgReportUtils.getUnReportedMsg(PlugService.this) > 0) {
                            if(mMsgReportThread == null || !mMsgReportThread.isAlive() || mMsgReportThread.finished()) {
                                mMsgReportThread = new MsgReportThread(PlugService.this, mMainHandler);
                                mMsgReportThread.start();
                            }
                        }
                    }
                    LogUtil.d(TAG, "do next report after "+(REPORT_NEXT_AFTER_TIME/(60*1000))+"m");
                    // do next report after 30min
                    mMainHandler.sendEmptyMessageDelayed(PlugService.MSG_REPORT, REPORT_NEXT_AFTER_TIME); // 30min
                    break;
                }
                default:
                    break;
            }
        }
    };

    /**
     * 处理服务器心跳
     */
    private synchronized void doHeartBeat() {
        LogUtil.i(TAG, "enter doHeartBeat");
        final ActiveRequestData requestData = new ActiveRequestData();
        IgnitedHttpResponse httpResponse;
        try {
            HttpEntity httpEntity = requestData.getData();
            
            httpResponse = Config.getApiHttp().post(Config.SERVER, httpEntity).send();
            String content = httpResponse.getResponseBodyAsString();
            ActiveResponseData responseData = requestData.getObject(content);
            if (responseData != null) {
                if (responseData.isSuccess() || "1002".equals(responseData.ret_code)) {
                    LogUtil.i(TAG, "doHeartBeat success");
                    //LogUtil.v(TAG, "content="+content);

                    // 设置服务端配置的心跳周期
                    long heartBeatDelay = Config.getHeartBeatDelayConfig(preferences);
                    
                    // 处理alarm间隔时间
                    doPushFlag(responseData.push_m_s);
                    
                    //刷新热词搜索信息
                    doRefreshHotwordAction(responseData.hotkey_words);
                    
                    //刷新推荐词搜索信息
                    doRefreshRecommendwordAction(responseData.recommend_searchWords);
                    
                    // 处理打点控制信息
                    doTabRemindAction(responseData.remind_info);

                    // 根据心跳返回的结果，返回用户优惠券信息
                    UserInfoUtil.getInstace().saveUserVoucherList(responseData.voucher_list);
                    
                    doOpConfig(responseData.op_config);
                    
                    // 根据心跳返回的结果，处理通知等nextCode操作
                    doNextAction(responseData.next_code);
                } else {
                    LogUtil.i(TAG, "doHeartBeat onFail "+responseData.ret_code);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void doRefreshHotwordAction(HotKeyWordsConfig config){
        if(config != null){
            SearchHotwordUtil.getInstance().setHotKeywordsVersion(config.version);
            SearchHotwordUtil.getInstance().initHotKeywords(this, config.key_words);
        }else{
            SearchHotwordUtil.getInstance().initHotKeywords(this,null);
        }

    }
    
    protected void doRefreshRecommendwordAction(RecommendSearchWordsConfig config){
        if(config != null){
            LogUtil.i(TAG, "doRefreshRecommendwordAction "+config.recommend_search_words);
            SearchRecommendwordUtil.getInstance().setRecommendKeywordsVersion(config.version);
            SearchRecommendwordUtil.getInstance().initRecommendKeywords(this, config.recommend_search_words);
        }else{
            LogUtil.i(TAG, "doRefreshRecommendwordAction is null");
            SearchRecommendwordUtil.getInstance().initRecommendKeywords(this,null);
        }

    }
    
    protected void doTabRemindAction(RemindInfo remind_info) {
        if(remind_info != null) {
            LogUtil.i(TAG, "remind_info.tab_remind = "+remind_info.tab_remind + 
            		" remind_max = " + remind_info.max_remind+" use_net_search_strategy = "+remind_info.use_net_search_strategy);
            RemindUtils.setTabRemind(remind_info.tab_remind==0?false:true);
            RemindUtils.setRemindMaxCount(remind_info.max_remind==0?6:remind_info.max_remind);// add by putao_lhq 2014年11月10日
            SearchUtils.setNetSearchStrategy(remind_info.use_net_search_strategy==0?false:true); // add by cj 2014/12/12
        } else {
            LogUtil.i(TAG, "remind_info.tab_remind = null");
            RemindUtils.setTabRemind(false);
            RemindUtils.setRemindMaxCount(6);// add by putao_lhq 2014年11月10日
        }
    }
    
    protected void doOpConfig(OpConfig op_config) {
        if(op_config != null) {
            LogUtil.i(TAG, "doOpConfig version="+op_config.getVersion());
            
            PushParseFactory.parseMsg(this, op_config.getData(), "");
        }
    }

    protected void doPushFlag(int push_m_s) {
    	LogUtil.d(TAG, "doPushFlag push_m_s="+push_m_s);
    	if(push_m_s == 0) {
    		stopPush(ContactsApp.getContext());
    	} else if(push_m_s > 0) {  //  最小5m才算打开
    		resumePush(ContactsApp.getContext());
    	}
    }

    /**
     * 根据心跳带回来的code处于对应的事件
     * 
     * @param nextCodes
     */
    protected void doNextAction(List<String> nextCodes) {
        // 根据心跳返回的结果，处理通知等nextCode操作
        if (nextCodes != null && nextCodes.size() > 0) {
            for (String nextCode : nextCodes) {
                LogUtil.d(TAG, "nextCode=" + nextCode);


                if(ConstantsParameter.YellowPageDataRequestCode.equals(nextCode)){//add by 2014-8-7
                    LogUtil.d(TAG, "doUpdateYellowPageRequest ");
                    //获取黄页数据
                    YellowPageDataUtils.doUpdateYellowPageRequest();
                }else if(ConstantsParameter.HabitDataRequestCode.equals(nextCode)){//add by 2014-09-23 ljq
                    LogUtil.d(TAG, "doUpdateHabitDataRequestCode ");
                    UserInfoUtil.getInstace().updateHabitData();
                } else if (UpdateSearchDataRequest.ACTION_CODE.equals(nextCode)) {
                	SearchUtils.doUpdateSearchDataRequest();//add by putao_lhq
                } else if (QueryFavoVoucherRequest.ACTION_CODE.equals(nextCode)) {
                	// 获取优惠券信息 add by zjh
                	UserInfoUtil.getInstace().updateUserCouponData();
                }
            }
            //心跳后上传习惯数据
            UserInfoUtil.getInstace().uploadHabitData();
        }
    }
    
    public class PlugServiceImpl extends IPutaoService.Stub {

        @Override
        public boolean userIsBind() throws RemoteException {
            return false;
        }

        @Override
        public boolean cancelMark(String number, String mark) throws RemoteException {
            return HmtSdkManager.getInstance().cancelMark(number, mark);
        }

        @Override
        public boolean uploadMark(String number, String mark) throws RemoteException {
            boolean isSuccess = false;
            MobclickAgentUtil.onEvent(PlugService.this, UMengEventIds.DISCOVER_YELLOWPAGE_NUMBER_MARK);
            MobclickAgentUtil.onEvent(PlugService.this,
                    UMengEventIds.DISCOVER_YELLOWPAGE_NUMBER_MARK_SOUGOU);
            try {
                isSuccess = HmtSdkManager.getInstance().uploadMark(number, mark);
                if (isSuccess) {
                    MobclickAgentUtil.onEvent(PlugService.this,
                            UMengEventIds.DISCOVER_YELLOWPAGE_NUMBER_MARK_SOUGOU_SUCCESS);
                } else {
                    MobclickAgentUtil.onEvent(PlugService.this,
                            UMengEventIds.DISCOVER_YELLOWPAGE_NUMBER_MARK_SOUGOU_NO_DATA);
                }
            } catch (Exception e) {
                MobclickAgentUtil.onEvent(PlugService.this,
                        UMengEventIds.DISCOVER_YELLOWPAGE_NUMBER_MARK_SOUGOU_FAIL);
                return false;
            }

            return isSuccess;
        }

        @Override
        public String getAllUserMark() throws RemoteException {
            Map<String, String> map = HmtSdkManager.getInstance().getAllUserMark();
            String allUserMark = ConvUtil.convertObjToBase64String(map);
            return allUserMark;
        }

        @Override
        public String getUserMark(String number) throws RemoteException {
            return HmtSdkManager.getInstance().getUserMark(number);
        }

        @Override
        public String checkNumberFromLocal(String number) throws RemoteException {
            HMTNumber hmtNumber = HmtSdkManager.getInstance().checkNumberFromLocal(number);
            return checkNumber(number, hmtNumber);
        }

        @Override
        public String checkNumberFromNet(String number) throws RemoteException {
            HMTNumber hmtNumber = null;
            MobclickAgentUtil.onEvent(PlugService.this,
                    UMengEventIds.DISCOVER_YELLOWPAGE_NUMBER_IDENTIFICATION);
            MobclickAgentUtil.onEvent(PlugService.this,
                    UMengEventIds.DISCOVER_YELLOWPAGE_NUMBER_IDENTIFICATION_SOUGOU);

            try {
                hmtNumber = HmtSdkManager.getInstance().checkNumberFromNet(number);
                if (null == hmtNumber || "".equals(hmtNumber)) {
                    MobclickAgentUtil.onEvent(PlugService.this,
                            UMengEventIds.DISCOVER_YELLOWPAGE_NUMBER_IDENTIFICATION_SOUGOU_NO_DATA);
                } else {
                    MobclickAgentUtil.onEvent(PlugService.this,
                            UMengEventIds.DISCOVER_YELLOWPAGE_NUMBER_IDENTIFICATION_SOUGOU_SUCCESS);
                }
            } catch (Exception e) {
                MobclickAgentUtil.onEvent(PlugService.this,
                        UMengEventIds.DISCOVER_YELLOWPAGE_NUMBER_IDENTIFICATION_SOUGOU_FAIL);
                return "";
            }

            return checkNumber(number, hmtNumber);
        }

        @Override
        public String queryCategoryByParentId(int parent_id) throws RemoteException {
            List<CategoryBean> categoryList = ContactsAppUtils.getInstance().getDatabaseHelper()
                    .getYellowPageDBHelper().queryCategoryByParentId(parent_id);
            if (categoryList == null || categoryList.size() == 0)
                return "";
            return ConvUtil.convertObjToBase64String(categoryList);
        }

        @Override
        public double getLatitude() throws RemoteException {
            return LBSServiceGaode.getLatitude();
        }

        @Override
        public double getLongitude() throws RemoteException {
            return LBSServiceGaode.getLongitude();
        }

        @Override
        public String getCity() throws RemoteException {
            return LBSServiceGaode.getCity();
        }

        @Override
        public void plugResume() throws RemoteException {
            LogUtil.d(TAG, "plugResume checkOrStartCheckUpdate");

//            com.putao.analytics.MobclickAgentUtil.onPageStart("yellowLive"); //统计页面
            MobclickAgentUtil.onPageStart("yellowLive"); //统计页面
            
            MobclickAgentUtil.onResume(PlugService.this);//add by hyl 2014-9-22
            
            UiHelper.checkOrStartCheckUpdate(mContext);
        }
        
        @Override
        public void plugPause() throws RemoteException {
//            com.putao.analytics.MobclickAgentUtil.onPageEnd("yellowLive"); //统计页面
            MobclickAgentUtil.onPageEnd("yellowLive"); //统计页面
            
            MobclickAgentUtil.onPause(PlugService.this);//add by hyl 2014-9-22
        }

//		@Override
//		public void onEvent(long value) throws RemoteException {
//			int time = (int) (value / 1000);
//			try {
////				LogUtil.d(TAG, "yh stay time = " + time);
////				Map<String, String> map_value = new HashMap<String, String>();
////				map_value.put("type", "PlugService");
//				MobclickAgentUtil.onEventValue(PlugService.this,
//						UMengEventIds.DISCOVER_RESUME_TIME, map_value, time);
//			    
//			    MobclickAgentUtil.onPageEnd("yellowLive"); //统计页面
//			    MobclickAgentUtil.onPause(PlugService.this);//add by hyl 2014-9-22
//			} catch (Exception e) {
//			}
//		}
		
        /**
         * 远程返回打点信息，优先检查打点信息，如果没有再检查气泡
         */
		@Override
		public String getRemind(int remindCode) throws RemoteException {
		    RemindBean bean = RemindUtils.getRemind(remindCode);
		    if(bean == null || bean.getRemindType() <= RemindConfig.REMIND_TYPE_NONE) {
		        bean = RemindUtils.getBubbleRemind(remindCode);
	            if(bean == null || bean.getRemindType() <= RemindConfig.REMIND_TYPE_NONE) {
	                return "";
	            }
		    }
		    
		    return ConvUtil.convertObjToBase64String(bean);
		}
		
        @Override
        public String getBubbleRemind(int remindCode) throws RemoteException {
            RemindBean bean = RemindUtils.getBubbleRemind(remindCode);
            if(bean != null)
                return ConvUtil.convertObjToBase64String(bean);
            else
                return "";
        }

		@Override
		public void onRemindClick(int remindCode) throws RemoteException {
			RemindManager.onRemindClick(remindCode);
		}

		@Override
		public String getNextHotword() throws RemoteException {
			return SearchHotwordUtil.getInstance().getNextHotword();
		}

		@Override
		public void perceptTel(String tel) throws RemoteException {
			SearchBusinessUtil.getInstance(mContext).search(tel);
		}

		@Override
		public int getRefreshPlugViewState() throws RemoteException {
			return YellowPagePlugUtil.getInstance().getRefreshPlugViewState();
		}

		@Override
		public String getOperateAdDataById(int serverCode) throws RemoteException {
			// TODO Auto-generated method stub
			// 获取广告数据
			List<PushAdBean> adList = ContactsAppUtils.getInstance().getDatabaseHelper()
                    .getYellowPageDBHelper().queryAdDataById(serverCode);
			return ConvUtil.convertObjToBase64String(adList);
		}

		@Override
		public void deleteOperateAdData(int serverCode, int pageIndex)
				throws RemoteException {
			// TODO Auto-generated method stub
			// 删除广告数据
		    ContactsAppUtils.getInstance().getDatabaseHelper().getYellowPageDBHelper()
				.deleteAdData(serverCode, pageIndex);
		}

        @Override
        public void addRemindBySelf(int type, int remindCode, boolean isMyService)
                throws RemoteException {
            LogUtil.i(TAG, "addRemindBySelf: type="+type+" remindCode="+remindCode+" isMyService="+isMyService);
            
            if(type == RemindConfig.REMIND_ADD) {  // 增加打点
                if(isMyService) 
                    RemindUtils.addMyServiceRemind(remindCode, true);
                else 
                    RemindUtils.addServiceRemind(remindCode, true);
                
            } else if(type == RemindConfig.REMIND_UPDATE) { // 更新打点
                
            } else if(type == RemindConfig.REMIND_DELETE) { // 删除打点
                
            }
                    
        }

        /**
         * 更改可以传递属性参数
         * by putao_lhq
         */
		@Override
		public void addUMengEvent(String umengEventId, int time) throws RemoteException {
			// TODO Auto-generated method stub
			if( !TextUtils.isEmpty(umengEventId) ){
				if (time <= -1) {
					MobclickAgentUtil.onEvent(PlugService.this, umengEventId);
				} else {
					HashMap<String, String> data = new HashMap<String, String>();
					data.put("__ct__", String.valueOf(time));
					LogUtil.d(TAG, "um plug time enter: " + time);
					MobclickAgentUtil.onEvent(PlugService.this, umengEventId, data);
				}
			}
		}

        @Override
        public String getRequrlOfSignTail() throws RemoteException {
            return ActiveUtils.getRequrlOfSignTail();
        }

		@Override
		public boolean updateExpandParamById(long category_id)
				throws RemoteException {
			boolean result = ContactsAppUtils.getInstance().getDatabaseHelper().getYellowPageDBHelper().updateExpandParamById(category_id);
			YellowPagePlugUtil.getInstance().setRefreshPlugViewState(YellowPagePlugUtil.STATE_REFRESH_ALL_VIEW);
			return result;
		}

		/**
		 * 获取打点最大显示数
		 * @author putao_lhq
		 */
		@Override
		public int getRemindMaxCount() throws RemoteException {
			int count = RemindUtils.getRemindMaxCount();
			LogUtil.d(TAG, "remind max count is: " + count);
			return count;
		}
    };

    public static void ServToPlugCallback(final RemoteCallbackList<ICallback> callbacks,
            int action, String base64) {
        LogUtil.d(TAG, "ServToPlugCallback beginBroadcast");
        // 直接回调host进程
        int n = callbacks.beginBroadcast();
        for (int i = 0; i < n; i++) {
            ICallback icb = callbacks.getBroadcastItem(i);
            try {
                icb.ServiceCallback(action, base64);
            } catch (RemoteException e) {
                e.printStackTrace();
                LogUtil.e(TAG, e.getMessage());
            }
        }
        callbacks.finishBroadcast();

        LogUtil.d(TAG, "ServToPlugCallback endBroadcast");
    }

    public void postHeartbeatDelay(boolean now) {
        Config.setHeartBeatDelay(preferences);

        mMainHandler.removeMessages(HEART_BEAT);
        if (now) {
            mMainHandler.sendEmptyMessageDelayed(HEART_BEAT, 0);
        } else {
            mMainHandler.sendEmptyMessageDelayed(HEART_BEAT,
                    Config.getHeartBeatDelayConfig(preferences));
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        LogUtil.d(TAG, "onBind");
        
        return mPlugServiceImpl;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtil.d(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        LogUtil.d(TAG, "onCreate");
        super.onCreate();
        mPlugServiceImpl = new PlugServiceImpl();
        mContext = this.getApplicationContext();

        preferences = this.getApplicationContext().getSharedPreferences(
                ConstantsParameter.CONTACTS_SETTING, Context.MODE_MULTI_PROCESS);

        registerRemindReceiver();
        
        PutaoAccount.getInstance().addAccChangeListener(this);
                
        initData();
        postHeartbeatDelay(true);
        
        mMainHandler.sendEmptyMessageDelayed(MSG_LOAD_MOVIE_CITY,20*60*1000);//add by hyl 2015-1-7 
    }

    /**
     * 初始化数据-异步操作
     */
    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                EventUtil.markReport(PlugService.this,false);//add by hyl 2014-10-25 每次重新启动 都将日志标识为未上报
               
                //add ljq 2014_11_20 start 将耗时初始化动作从ContactsApp放在这里来做  特别是数据库的初始化和升级
                SDKInitializer.initialize(PlugService.this.getApplicationContext());
                if(!HmtSdkManager.getInstance().isInit()) {
                    HmtSdkManager.getInstance().init(PlugService.this.getApplicationContext());
                }
                
		        // 初始化默认搜索配置
		        if(!SearchUtils.hasData()) {
		            SearchUtils.initDefaultSearchConfig();
		        }
		        
		        // 黄页静态数据不存在
				YellowPageDB db = ContactsAppUtils.getInstance().getDatabaseHelper()
						.getYellowPageDBHelper();
		
				if (db.getCategoryCount(0) == 0) {
					db.getSQLiteDatabase().beginTransaction();
					YellowUtil.loadDefaultCategoryDB();
					YellowUtil.loadDefaultItemDB();
					YellowUtil.loadAllCityList();   		//add by zjh 2014-12-13
					YellowUtil.loadDefaultExpressDB();
					YellowUtil.loadTrainTicketDB();			//add by lisheng 2014-11-24 19:43:53;
					WEGUtil.loadWaterElectricityGasDB();	//add by ljq 2014-11-28;
					db.getSQLiteDatabase().setTransactionSuccessful();
					db.getSQLiteDatabase().endTransaction();
				}
				//add by hyl 2015-1-7  start 初始化电影票数据
				if(!CinemaApiUtil.isExistCityDBData()){
					YellowUtil.loadMovieCityList();
				}
				//add by hyl 2015-1-7 end
				
        		// 初始化默认气泡 for V1.7.xx alinone
        		if (RemindUtils.isLoadDefBubbles()) {
        			RemindUtils.initDefaultBubbleForV17xx();
        			RemindUtils.setDefBubles(false);
        		}else{
        		    if(YellowUtil.isNeedUpdateRechargeName()){
        		        YellowUtil.setNeedUpdateRechargeName(false);
        		        ContactsAppUtils.getInstance().getDatabaseHelper().getYellowPageDBHelper().replaceRechargeName();
        		    }
        		}
                
                RemindManager.getInstance().dumps("Reminds===>");
                BubbleRemindManager.getInstance().dumps("Bubbles===>");
                //add ljq 2014_11_20 end 将耗时初始化动作从ContactsApp放在这里来做 特别是数据库的初始化和升级
                
                //发送广播 通知数据已初始化完成
                sendBroadcast(new Intent(ConstantsParameter.ACTION_INITDATA_FINISHED));
                
                //有未上报数据,则启动上报
                if(MsgReportUtils.getUnReportedMsg(PlugService.this) > 0) {
                    // start report
                    mMainHandler.sendEmptyMessageDelayed(PlugService.MSG_REPORT, 1000);
                }

                // 初始化完全局数据后再登陆,防止在app启动时登陆返回后缺少数据
            	LogUtil.v(TAG, "PTUSER: " + PutaoAccount.getInstance().getPtUser());
            	boolean isLogin = PutaoAccount.getInstance().isLogin();
            	if(!isLogin) {
            		PutaoAccount.getInstance().silentLogin(null);
            	}
            	
                LogUtil.d(TAG, "initPlug plugService initData end ="+System.currentTimeMillis());
            }
        }).start();
    }    

	private void resumePush(Context context) {
		LogUtil.d(TAG, "resumePush connState="+JPushInterface.getConnectionState(context)+" isPushStopped="+JPushInterface.isPushStopped(context));
		//JPushInterface.stopPush(context);
		// 初始化极光push
		if(JPushInterface.isPushStopped(context)) {
			JPushInterface.init(context);
			JPushInterface.resumePush(context);
		}
		
		PushUtil.setAliasTags(ContactsApp.getContext(), this);
	}
    
	private void stopPush(Context context) {
		LogUtil.d(TAG, "stopPush");
		JPushInterface.stopPush(context);
	}
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        LogUtil.d(TAG, "onDestory");
        super.onDestroy();
        unregisterRemindReceiver();
        PutaoAccount.getInstance().delAccChangeListener(this);
        if(mMsgReportThread != null) {
            mMsgReportThread.interrupt();
            mMsgReportThread = null;
        }

        /*
         * 移除信鸽功能 注释该逻辑
         * modified by hyl 2014-12-24 start
         */
//        /**  注销信鸽push */
//        XGPushUtil.unRegisterXGPush(getApplicationContext());
        //modified by hyl 2014-12-24 end
    }

    private String checkNumber(String number, HMTNumber hmtNumber) {
        PTNumber ptNumber = convertToSougouInfo(hmtNumber);
        String productBase64 = "";
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;
        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(ptNumber);
            productBase64 = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
                if (oos != null) {
                    oos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return productBase64;
    }

    private PTNumber convertToSougouInfo(HMTNumber hmtNumber) {
        PTNumber info = new PTNumber();
        if (hmtNumber != null) {
            info.setType(hmtNumber.getType());
            info.setMarkNumber(hmtNumber.getMarkNumber());
            info.setMarkContent(hmtNumber.getMarkContent());
            info.setMarkSource(hmtNumber.getMarkSource());
            info.setIconBitmap(hmtNumber.getIconBitmap());
        }
        return info;
    }

    @Override
    public void onLocationChanged(String city, double latitude, double longitude, long time) {
    }

    @Override
    public void onLocationFailed() {
        
    }
	
    private class RemindBroadcastReceiver extends BroadcastReceiver {
        static final String TAG = "RemindBroadcastReceiver";
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            String action = arg1.getAction();
            if( TextUtils.isEmpty(action) ){
            	return;
            }
            LogUtil.i(TAG, "action="+action);
            if(ConstantsParameter.ACTION_REMOTE_UPDATE_REMIND.equals(action)) {
                // 接收到远程控制增加打点的广播
            	int type = arg1.getIntExtra("Type", 0);
            	int remindCode = arg1.getIntExtra("RemindCode", -1);
            	boolean isMyService = arg1.getBooleanExtra("IsMyService",false);
            	
            	LogUtil.i(TAG, "action:" + action+" type="+type+" remindCode="+remindCode);
            	if(type == RemindConfig.REMIND_ADD) {  // 增加打点
            		if(isMyService) 
            			RemindUtils.addMyServiceRemind(remindCode, true);
            		else 
            			RemindUtils.addServiceRemind(remindCode, true);
            		
            	} else if(type == RemindConfig.REMIND_UPDATE) { // 更新打点
            		
            	} else if(type == RemindConfig.REMIND_DELETE) { // 删除打点
            		
            	}
            } else if(ConstantsParameter.ACTION_REMOTE_UPDATE_ACTIVE.equals(action)){
                // 接收到更新活动的广播
            	String json = arg1.getStringExtra("remote_update_active");
            	if(TextUtils.isEmpty(json))
            		return;
            	
            	PushParseFactory.parseMsg(ContactsApp.getInstance(), json, "");
            } else if(ConstantsParameter.ACTION_REMOTE_DO_HEARTBEAT.equals(action)) {
                // 接收到产生心跳广播
            	postHeartbeatDelay(true);
            } else if(MsgReportParameter.ACTION_REPORT.equals(action)) {
                // 接收到消息上报广播
                mMainHandler.sendEmptyMessage(MSG_REPORT);
            }
        }
    }
	
	private RemindBroadcastReceiver mReceiver = null;
    private void registerRemindReceiver() {
        // 注册跨进程接收添加打点信息的广播
        mReceiver = new RemindBroadcastReceiver();
        IntentFilter filter = new IntentFilter(ConstantsParameter.ACTION_REMOTE_UPDATE_REMIND);
        filter.addAction(ConstantsParameter.ACTION_REMOTE_UPDATE_ACTIVE);
        filter.addAction(ConstantsParameter.ACTION_REMOTE_DO_HEARTBEAT);// add by putao_lhq 2014年11月14日
        filter.addAction(MsgReportParameter.ACTION_REPORT); // add by cj 2015/01/22 for 消息上报
        mContext.registerReceiver(mReceiver, filter);
    }

    private void unregisterRemindReceiver() {
        if (mReceiver != null) {
            mContext.unregisterReceiver(mReceiver);
        }
    }

	@Override
	public void onLogin() {
		LogUtil.d(TAG, "onLogin");
		postHeartbeatDelay(true);
	}

	@Override
	public void onLogout() {
		
	}

	@Override
	public void onChange() {
		
	}
	/**
	 * 设置alias和tags的错误码: 
        6001    无效的设置，tag/alias 不应参数都为 null     
        6002    设置超时    建议重试
        6003    alias 字符串不合法    有效的别名、标签组成：字母（区分大小写）、数字、下划线、汉字。
        6004    alias超长。最多 40个字节    中文 UTF-8 是 3 个字节
        6005    某一个 tag 字符串不合法  有效的别名、标签组成：字母（区分大小写）、数字、下划线、汉字。
        6006    某一个 tag 超长。一个 tag 最多 40个字节  中文 UTF-8 是 3 个字节
        6007    tags 数量超出限制。最多 100个     这是一台设备的限制。一个应用全局的标签数量无限制。
        6008    tag/alias 超出总长度限制。总长度最多 1K 字节   
        6011    10s内设置tag或alias大于3次     短时间内操作过于频繁 
	 */
	@Override
	public void gotResult(int code, String alias, Set<String> tags) {
		/*
		 * 
		 * modified by hyl 2014-12-23 start
		 * old code:
		 * if(!TextUtils.isEmpty(alias)) {
    			LogUtil.i(TAG, "set alias ["+alias+"] code="+code);
	    	} 
	    	
	    	if(tags != null) {
	    		Iterator<String> it = tags.iterator();
	    		while(it.hasNext()) {
	    			LogUtil.i(TAG, "set tags ["+it.next()+"] code="+code);
	    		}
	    	}
		 */
		LogUtil.i(TAG, "code="+code+" aliad:"+alias);
		switch (code) {
			case 0:
				if(!TextUtils.isEmpty(alias)) {
	    			LogUtil.i(TAG, "set alias ["+alias+"] code="+code);
		    	} 
		    	
		    	if(tags != null) {
		    		Iterator<String> it = tags.iterator();
		    		while(it.hasNext()) {
		    			LogUtil.i(TAG, "set tags ["+it.next()+"] code="+code);
		    		}
		    	}
		    	break;
	        case 6002:
	            if (SystemUtil.contactNet(getApplicationContext())) {
	            	mMainHandler.sendMessageDelayed(mMainHandler.obtainMessage(MSG_SET_ALIAS, tags), 1000 * 60);
	            }
	            break;
	        default:
	        	break;
        }
		//modified by hyl 2014-12-23 end
    }
	
}
