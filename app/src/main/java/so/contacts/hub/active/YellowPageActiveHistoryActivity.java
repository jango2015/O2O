
package so.contacts.hub.active;

import java.util.ArrayList;
import java.util.List;

import so.contacts.hub.account.IAccCallback;
import so.contacts.hub.account.PutaoAccount;
import so.contacts.hub.active.bean.ActiveHistoryBean;
import so.contacts.hub.active.bean.ActiveHistoryRequest;
import so.contacts.hub.active.bean.ActiveHistoryResponse;
import so.contacts.hub.core.Config;
import so.contacts.hub.core.PTUser;
import so.contacts.hub.db.YellowPageDB;
import so.contacts.hub.remind.BaseRemindActivity;
import so.contacts.hub.remind.RemindConfig;
import so.contacts.hub.ui.yellowpage.YellowPageDetailActivity;
import so.contacts.hub.ui.yellowpage.bean.YellowParams;
import so.contacts.hub.util.ContactsAppUtils;
import so.contacts.hub.util.ContactsHubUtils;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.NetUtil;
import so.contacts.hub.util.UMengEventIds;
import so.contacts.hub.util.YellowUtil;
import so.contacts.hub.widget.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mdroid.core.http.IgnitedHttpResponse;
import com.yulong.android.contacts.discover.R;

/**
 * @author ljq
 * @version 2014年10月22日
 */
public class YellowPageActiveHistoryActivity extends BaseRemindActivity implements
        View.OnClickListener, /*CallBack,*/ IAccCallback {

    private static final String TAG = "YellowPageActiveHistoryActivity";

    private ArrayList<ActiveHistoryBean> mActiveList = new ArrayList<ActiveHistoryBean>();

    private ListView mActiveListView;

    private LinearLayout mExceptionTipLayout ;
    
    private TextView mExceptionTextView ;

    private ActiveHistoryListAdapter mAdapter;

    private YellowPageDB db = null;

    // private ArrayList<SimpleRemindView> remindList ;

    private PTUser ptUser = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(TAG, "onCreate");
        setContentView(R.layout.putao_yellow_page_active_history_layout);
        initView();
        initData();
        //请求用户登录状态  若登录则请求数据 否则登录
        if(NetUtil.isNetworkAvailable(this)){
            ptUser = PutaoAccount.getInstance().getPtUser();
            if (null == ptUser || null == ptUser.getPt_token()) {
                /*YellowPageDataUtils.silentLogin(YellowPageActiveHistoryActivity.this,
                        YellowPageActiveHistoryActivity.this);*/
            	PutaoAccount.getInstance().silentLogin(this);
            } else {
                doRequestHistory();
            }
        }else{
            mHandler.sendEmptyMessage(0);
        }
        MobclickAgentUtil.onEvent(this, UMengEventIds.DISCOVER_YELLOWPAGE_AD_MY_ACTIVITIES);
    }

    private void initView() {
        if (TextUtils.isEmpty(mTitleContent)) {
            mTitleContent = getResources().getString(R.string.putao_active_history_title);
        }
        ((TextView)findViewById(R.id.title)).setText(mTitleContent);

        findViewById(R.id.back_layout).setOnClickListener(this);

        mActiveListView = (ListView)this.findViewById(R.id.activities_lv);
        mExceptionTipLayout = (LinearLayout)findViewById(R.id.network_exception_layout);
        mExceptionTextView = (TextView)findViewById(R.id.exception_desc);
        mExceptionTipLayout.setOnClickListener(this);
        
        mActiveListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mActiveList != null && position > -1 && position < mActiveList.size()) {
                    ActiveHistoryBean act = mActiveList.get(position);
                    if (null == act) {
                        LogUtil.v(TAG, "ActiveHistoryBean is null");
                        return;
                    }

                    if (ContactsHubUtils.isURlStr(act.target_url)) {
                        String url = ActiveUtils.getRequrlOfSign(act.target_url);
                        LogUtil.v(TAG, "start web");
                        YellowParams params = new YellowParams();
                        params.setName(act.name);
                        params.setTitle(act.name);
                        params.setUrl(url/*act.target_url*/);// add by putao_lhq 2014年11月10日
                        Intent intent = new Intent(YellowPageActiveHistoryActivity.this,
                                YellowPageDetailActivity.class);
                        intent.putExtra(YellowUtil.TargetIntentParams, params);
                        startActivity(intent);
                    }
                }
            }
        });
        
    }

    /**
     * 初始化数据和列表
     */
    private void initData() {
        LogUtil.v(TAG, "initData");
        db = ContactsAppUtils.getInstance().getDatabaseHelper().getYellowPageDBHelper();
        mActiveList = db.queryAllActiveHistory(ptUser != null ? ptUser.pt_uid : "", false);
        mAdapter = new ActiveHistoryListAdapter(this, mActiveList);
        mActiveListView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgentUtil.onResume(this);
        LogUtil.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        MobclickAgentUtil.onPause(this);
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy");
    }

    @Override
    public void onClick(View arg0) {
        if (arg0.getId() == R.id.back_layout) {
            finish();
        }else if(arg0.getId() == R.id.network_exception_layout){
            if(NetUtil.isNetworkAvailable(this)) {
                mExceptionTipLayout.setVisibility(View.GONE);
                /*ptUser = Config.getPTUser();*/
                if (!PutaoAccount.getInstance().isLogin()/*null == ptUser || null == ptUser.getPt_token()*/) {
                    /*YellowPageDataUtils.silentLogin(YellowPageActiveHistoryActivity.this,
                            YellowPageActiveHistoryActivity.this);*/
                	PutaoAccount.getInstance().silentLogin(this);
                } else {
                    doRequestHistory();           
                }
            }
        }
    }

    /*@Override
    public void onSuccess(String o) {
        LogUtil.i(TAG, "login successful");
        doRequestHistory();
    }*/

    @Override
    public void onFail(int msg) {
        LogUtil.i(TAG, "login failed: " + msg);
        Toast.makeText(this, R.string.putao_server_busy, Toast.LENGTH_SHORT).show();

    }

    /*@Override
    public void onFinish(Object obj) {

    }*/

    @Override
    public Integer remindCode() {
        return RemindConfig.MyActivies;
    }

    @Override
    public String getServiceNameByUrl() {
        // TODO Auto-generated method stub
        return null;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    showData();
                    break;
            }
        }
    };

    /**
     * 请求活动历史 add by putao_lhq
     */
    public void doRequestHistory() {
        LogUtil.v(TAG, "request");
        showLoadingDialog(false);
        Config.execute(new Runnable() {
            @Override
            public void run() {
                final ActiveHistoryRequest request = new ActiveHistoryRequest();
                ActiveHistoryResponse dataResponse = null;
                IgnitedHttpResponse httpResponse;
                try {
                    httpResponse = Config.getApiHttp().post(Config.SERVER, request.getData()).send();
                    String content = httpResponse.getResponseBodyAsString();
                    dataResponse = request.getObject(content);
                    if (dataResponse != null) {
                        doParseResponse(dataResponse);
                    }
                } catch (Exception e) {
                    LogUtil.e(TAG, "doRequestHistory " + e.getMessage());
                    dataResponse = null;
                    dismissLoadingDialog();
                }
            }
        });
    }
    
    private void doParseResponse(ActiveHistoryResponse dataResponse) {
        
        if (dataResponse != null && dataResponse.isSuccess()) {
            //打点更新逻辑暂时用不到 2014-10-21 LJQ Start
            
//            List<ActiveHistoryBean> newHisList = dataResponse.history_list;
//            if (null == newHisList || newHisList.size() == 0) {
//                LogUtil.v(TAG, "active history is null");
//                db.deleteActiveHistoryByUid(ptUser.pt_uid);
//                mActiveList = null;
//                //刷新界面
//                mHandler.sendEmptyMessage(0);
//            } else {
//
//                ArrayList<ActiveHistoryBean> temporaryHisList = db.queryAllActiveHistory(
//                        ptUser != null ? ptUser.pt_uid : "", true);
//                if (temporaryHisList != null && temporaryHisList.size() > 0) {
//                    ListIterator<ActiveHistoryBean> it = newHisList.listIterator();
//                    while (it.hasNext()) {
//                        ActiveHistoryBean newBean = it.next();
//                        ActiveHistoryBean oldBean = indexOfRepeat(temporaryHisList, newBean.activity_id,
//                                newBean.id, ptUser != null ? ptUser.pt_uid : "");
//                        if (oldBean != null) {
//                            newBean.remind = oldBean.remind;
//                        } else {
//                            
//                        }
//                    }
//                }
//                db.deleteActiveHistoryByUid(ptUser.pt_uid);
//                db.insertActiveHisBeanList(newHisList);
//                mActiveList = new ArrayList<ActiveHistoryBean>(newHisList);
//                mHandler.sendEmptyMessage(0);
//            }            
//        } else {
//            LogUtil.v(TAG, "dataResponse is null");
//            db.deleteActiveHistoryByUid(ptUser.pt_uid);
//            mActiveList = null;
//            //刷新界面
//            mHandler.sendEmptyMessage(0);
//            return;
//        }
            //打点更新逻辑暂时用不到 2014-10-21 LJQ end

            List<ActiveHistoryBean> newHisList = dataResponse.history_list;
            
            if (null == newHisList || newHisList.size() == 0) {
                LogUtil.v(TAG, "active history is null");
                db.clearTable(YellowPageDB.ActiveHistoryTable.TABLE_NAME);
                mActiveList = null;
                // 刷新界面
                mHandler.sendEmptyMessage(0);
            } else {
                db.clearTable(YellowPageDB.ActiveHistoryTable.TABLE_NAME);
                db.insertActiveHisBeanList(newHisList);
                mActiveList = new ArrayList<ActiveHistoryBean>(newHisList);
                mHandler.sendEmptyMessage(0);
            }
            dismissLoadingDialog();
        
        }else{
            LogUtil.v(TAG, "dataResponse is null");
            db.clearTable(YellowPageDB.ActiveHistoryTable.TABLE_NAME);
            mActiveList = null;
            dismissLoadingDialog();
            //刷新界面
            mHandler.sendEmptyMessage(0);
            return;
        }
        
    
    }

    /**
     * delete by putao_lhq
     * 注释掉未使用的code
    private ActiveHistoryBean indexOfRepeat(List<ActiveHistoryBean> list, long active_id,
            long my_active_id, String uid) {
        if (list == null || list.size() == 0 || TextUtils.isEmpty(uid))
            return null;

        for (ActiveHistoryBean bean : list) {
            if (bean.activity_id == active_id && bean.id == my_active_id && uid.equals(bean.u_id))
                return bean;
        }

        return null;
    }

    private void updateToHisBean(ActiveHistoryBean addBean, ActiveHistoryBean valueBean,
            boolean updateRemind) {
        addBean.activity_id = valueBean.activity_id;
        addBean.id = valueBean.id;
        addBean.u_id = valueBean.u_id;
        addBean.step_id = valueBean.step_id;
        addBean.participation_time = valueBean.participation_time;
        addBean.description = valueBean.description;
        addBean.status = valueBean.status;
        addBean.target_url = valueBean.target_url;
        addBean.icon_url = valueBean.icon_url;
        addBean.update_time = valueBean.update_time;
        addBean.name = valueBean.name;
        if (updateRemind)
            addBean.remind = valueBean.remind;
    }*/

    /**
     * 根据是否有数据显示调整界面
     */
    private void showData() {
        if (!NetUtil.isNetworkAvailable(this)) {
            mExceptionTextView.setText(R.string.putao_netexception_hint);
            mExceptionTipLayout.setVisibility(View.VISIBLE);
            mActiveListView.setVisibility(View.GONE);
        } else {
            if (mActiveList != null && mActiveList.size() > 0) {
                mAdapter = new ActiveHistoryListAdapter(this, mActiveList);
                mActiveListView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                mExceptionTipLayout.setVisibility(View.GONE);
                mActiveListView.setVisibility(View.VISIBLE);
            } else {
                mExceptionTextView.setText(R.string.putao_active_history_no_data_tip);
                mExceptionTipLayout.setVisibility(View.VISIBLE);
                mActiveListView.setVisibility(View.GONE);
            }
        }

    }

	@Override
	public String getServiceName() {
		// TODO Auto-generated method stub
		return this.getClass().getName();
	}

	@Override
	public boolean needMatchExpandParam() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onSuccess() {
		LogUtil.i(TAG, "login successful");
        doRequestHistory();
	}

    @Override
    public void onCancel() {
        // TODO Auto-generated method stub
        
    }

}
