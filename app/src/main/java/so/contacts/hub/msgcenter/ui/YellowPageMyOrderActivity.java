
package so.contacts.hub.msgcenter.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.core.ConstantsParameter;
import so.contacts.hub.msgcenter.IMessageBusiness;
import so.contacts.hub.msgcenter.PTMessageCenterFactory;
import so.contacts.hub.msgcenter.PTOrderCenter;
import so.contacts.hub.msgcenter.PTOrderCenter.RefreshOrderListener;
import so.contacts.hub.msgcenter.bean.PTOrderBean;
import so.contacts.hub.remind.BaseRemindActivity;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.NetUtil;
import so.contacts.hub.util.UMengEventIds;
import so.contacts.hub.widget.CustomListView;
import so.contacts.hub.widget.CustomListView.OnRefreshListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yulong.android.contacts.discover.R;

/**
 * 我的订单页
 * @author zj 2014-12-18 14:30:33
 *
 */
public class YellowPageMyOrderActivity extends BaseRemindActivity implements OnClickListener, OnRefreshListener, RefreshOrderListener {

    private static final String TAG = YellowPageMyOrderActivity.class.getSimpleName();
    
    /*
    * modify by putao_lhq
    * old code:
    protected PTPullToRefreshListView listView;*/
    protected CustomListView listView;/*@end by putao_lhq*/
    
    protected OrderListAdapter adapter;

    private PTOrderCenter orderCenter;

    private ArrayList<PTOrderBean> newestOrders=new ArrayList<PTOrderBean>();

    /*
     * modify by putao_lhq at 2015年1月10日 @start
     * add code:
     */
    private ArrayList<PTOrderBean> newestTemp = new ArrayList<PTOrderBean>();
    /* end by putao_lhq */
    
    private ArrayList<PTOrderBean> expiredOrders=new ArrayList<PTOrderBean>();
       
    private Handler mHandler = new MyHandler();
    
    public static final int ACTION_REFRESH_DB_CHANGED = 101;
    public static final int ACTION_REFRESH_DB_UNCHANGED = 102;
    public static final int ACTION_REFRESH_FAILED = 103;
    
    private class MyHandler extends Handler {

        @Override
        public void dispatchMessage(Message msg) {
            updateData(msg.what);
        }
    };
    
    private void updateData(int action) {
        LogUtil.d(TAG, "updateData action="+action);
        switch(action) {
            case ACTION_REFRESH_DB_CHANGED:
            	
            	dismissLoadingDialog();//add by ls 2015-01-23;
                /*
                 * modify by putao_lhq at 2015年1月10日 @start
                 */
                if (newestTemp.size()<=0) {
                    findViewById(R.id.my_nodata_layout).setVisibility(View.VISIBLE);
                    newestOrders.clear();
                } else {
                    newestOrders.clear();
                    newestOrders.addAll(newestTemp);
                }/* end by putao_lhq */
                
                adapter.notifyDataSetChanged();
                listView.onRefreshComplete();
                
                break;
            case ACTION_REFRESH_DB_UNCHANGED:
            	dismissLoadingDialog();//add by ls 2015-01-23;
                // 原来有数据则无操作,无数据则显示无数据
                /*
                 * modify by putao_lhq at 2015年1月10日 @start
                 */
                if (newestTemp.size()<=0) {
                    findViewById(R.id.my_nodata_layout).setVisibility(View.VISIBLE);
                    newestOrders.clear();
                    adapter.notifyDataSetChanged();
                }
                /* end by putao_lhq */
                
                listView.onRefreshComplete();
                
                break;
            case ACTION_REFRESH_FAILED:
            	dismissLoadingDialog();//add by ls 2015-02-03;
                listView.onRefreshComplete();
                
                //old code:Toast.makeText(getApplicationContext(), getString(R.string.putao_cinema_pull_to_refresh_failure), 1).show();
                //modity by ljq bug3085 2015/02/03
                if(NetUtil.isNetworkAvailable(this)){
                    Toast.makeText(getApplicationContext(), getString(R.string.putao_cinema_pull_to_refresh_failure), 1).show();
                }else{
                    Toast.makeText(getApplicationContext(), getString(R.string.putao_no_net), 1).show();
                }
                //modity by ljq bug3085 2015/02/03
                if (newestOrders.size()<=0) {
                    findViewById(R.id.my_nodata_layout).setVisibility(View.VISIBLE);
                }
                
                break;
        }
    }
        
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.putao_order_center_refresh_activity);
        
        showLoadingDialog();//add by ls 2015-01-23;

        //显示loading,先显示本地数据,判断网络是否可用,不可用则只访问本地数据(取消loading),
        //可用则拉取服务器最新数据,listview无缝加载
        initData();
        initView();
     // removed by cj 2015/02/03
//        registerUpdateReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        refreshData();
        /*
         * delete by putao_lhq
         * listView.setRefreshing();*/
    }
    
    private void initData() {
        orderCenter = PTMessageCenterFactory.getPTOrderCenter();
        //先加载本地数据
        List<PTOrderBean> orderList =orderCenter.loadOrders();
        if(checkOrder(orderList)) {
            mHandler.sendEmptyMessage(ACTION_REFRESH_DB_CHANGED);
        }

    }
    
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
     // removed by cj 2015/02/03        
//        unregisterUpdateReceiver();
    }
    /**
     * 刷新数据库数据
     * @param orderList
     */
    private void refreshData() {
        findViewById(R.id.my_nodata_layout).setVisibility(View.GONE);
        LogUtil.d(TAG, "refreshData");

        orderCenter.requestRefreshOrders(this);
    }

    private boolean checkOrder(List<PTOrderBean> orderList) {
        newestTemp.clear();
        expiredOrders.clear();

        if (null!=orderList) {
            for (PTOrderBean bean : orderList) {
                IMessageBusiness business = orderCenter.getService(bean);
                if(business == null){//保证每一条数据业务都能正常识别,去掉错误数据
                    LogUtil.w(TAG, "checkOrder can not found busi for "+bean.getProduct_type());
                    continue;
                }
                if (orderCenter.getService(bean).isOrderExpire(bean)) {
                    expiredOrders.add(bean);
                } else {
                    newestTemp.add(bean);
                }
            }
            
            LogUtil.d(TAG, "checkOrder newestOrders="+newestTemp.size()+" expiredOrders="+expiredOrders.size());            
        }
        
        return newestTemp.size() > 0;
    }

    private void initView() {
        findViewById(R.id.next_setp_layout).setOnClickListener(this);

        setTitle(R.string.putao_my_orderlist_name);
        
        TextView tv=(TextView)findViewById(R.id.exception_desc);
        tv.setText(R.string.putao_order_none);
        
        /*
         * modify by putao_lhq @start
         * old code:
        TextView next_step_btn = (TextView)findViewById(R.id.next_step_btn);
        next_step_btn.setText(R.string.order_history);
        next_step_btn.setVisibility(View.VISIBLE);*/
        ImageView img = (ImageView)findViewById(R.id.next_step_img);
        img.setImageResource(R.drawable.putao_icon_title_ls);
        img.setVisibility(View.VISIBLE);/*@end by putao_lhq*/
        
        /*
         * modify by putao_lhq @start
         * 改用CustomListView下拉刷新
         * old code:
         * listView = (PTPullToRefreshListView)findViewById(R.id.list);*/
        listView = (CustomListView)findViewById(R.id.list);
        listView.setOnRefreshListener(this);
        listView.setCanRefresh(true);
        listView.setTipString(getString(R.string.putao_pull_to_refresh));
        listView.setTipDoingString(getString(R.string.putao_do_refresh));
        /*@end by putao_lhq*/
        
        adapter = new OrderListAdapter(newestOrders, orderCenter);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*
                 * modify by putao_lhq at 2015年1月10日 @start
                 * old code:
                 * PTOrderBean bean = newestOrders.get(position);
                 */
                if (position > newestOrders.size()) {
                    return;
                }
                
                /*
                 * bug: 小概率出现position取值为0的情况
                 * add by hyl 2015-1-12 start
                 */
                if(position == 0){
                	position = 1;
                }
                //add by hyl 2015-1-12 end
                
                PTOrderBean bean = newestOrders.get(position-1);
                /* end by putao_lhq */
                
                orderCenter.getService(bean).click(bean, YellowPageMyOrderActivity.this);
                MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
                        UMengEventIds.DISCOVER_YELLOWPAGE_MY_ORDER_CENTER_CLICK_ITEM);
            }
        });
        
        /*
         * modify by putao_lhq
         * 改用android.support.v4.widget.SwipeRefreshLayout 下拉刷新
         * delete code
         * listView.setOnRefreshListener(new OnRefreshListener() {
            
            @Override
            public void onRefresh() {
                refreshData();
            }
        });*/
        
        /**
         * add code by putao_lhq
         * 添加返回事件
         * @start
         */
        findViewById(R.id.back_layout).setOnClickListener(this);
        /**
         * @end by putao_lhq*/
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next_setp_layout:
                Intent intent = new Intent(this, YellowPageMyHistoryOrderActivity.class);
                intent.putExtra(YellowPageMyHistoryOrderActivity.HISTORY_ORDERS, expiredOrders);
                startActivity(intent);
                break;
                /**
                 * add code by putao_lhq
                 * 添加返回事件
                 * @start
                 */
            case R.id.back_layout:
                finish();
                break;/*@end by putao_lhq*/
            default:
                break;
        }

    }

    @Override
    public String getServiceNameByUrl() {
        return null;
    }

    @Override
    public String getServiceName() {
        return YellowPageMyOrderActivity.class.getName();
    }

    @Override
    public boolean needMatchExpandParam() {
        return false;
    }

    @Override
    public Integer remindCode() {
        return null;
    }
    
    @Override
    protected boolean needReset() {
        return true;
    }
    
    
    
    // removed by cj 2015/02/03
/*    UpdateReceiver updateReceiver = null;
    private void registerUpdateReceiver() {
        String action = ConstantsParameter.ACTION_ORDER_UPDATE_DATA;
        IntentFilter filter = new IntentFilter(action);

        updateReceiver = new UpdateReceiver();
        registerReceiver(updateReceiver, filter);
    }

    private void unregisterUpdateReceiver() {
        if (updateReceiver != null) {
            unregisterReceiver(updateReceiver);
        }
    }
    private class UpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            List<PTOrderBean> orderBeans =orderCenter.loadOrders();
            if (orderBeans!=null&&orderBeans.size()>0) {
                checkOrder(orderBeans);
                mHandler.sendEmptyMessage(ACTION_REFRESH_DB_CHANGED);
            }   
        }
    }
*/
    @Override
    public void onRefresh() {
        LogUtil.d(TAG, "start swipe refresh");
        refreshData();
    }

    @Override
    public void refreshSuccess(boolean isDbChanged) {
        List<PTOrderBean> orders = orderCenter.loadOrders();
        boolean hasNewOrders = checkOrder(orders);
        
        if(hasNewOrders) {
            mHandler.sendEmptyMessage(ACTION_REFRESH_DB_CHANGED);
        } else {
            mHandler.sendEmptyMessage(ACTION_REFRESH_DB_UNCHANGED);
        }
    }

    @Override
    public void refreshFailure(String msg) {
        mHandler.sendEmptyMessage(ACTION_REFRESH_FAILED);
    }
}
