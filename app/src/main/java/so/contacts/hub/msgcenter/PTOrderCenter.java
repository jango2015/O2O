
package so.contacts.hub.msgcenter;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.json.JSONException;
import org.json.JSONObject;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.account.IAccChangeListener;
import so.contacts.hub.core.Config;
import so.contacts.hub.http.bean.ProductDescBean;
import so.contacts.hub.msgcenter.MessageCenterDB.OrderTable;
import so.contacts.hub.msgcenter.bean.OrderNumber;
import so.contacts.hub.msgcenter.bean.PTMessageBean;
import so.contacts.hub.msgcenter.bean.PTOrderBean;
import so.contacts.hub.net.IResponse;
import so.contacts.hub.net.PTHTTP;
import so.contacts.hub.net.SimpleRequestData;
import so.contacts.hub.util.LogUtil;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class PTOrderCenter implements IAccChangeListener {
    private static final String PAGE_SIZE = "20";

    private static final String TAG = "PTOrderCenter";

    private List<AbstractMessageBussiness> mMsgList = new ArrayList<AbstractMessageBussiness>();
    
    private LinkedList<SoftReference<RefreshOrderListener>> mListener = new LinkedList<SoftReference<RefreshOrderListener>>();

    private static PTOrderCenter sInstance;

    private Thread mRefreshThread = null;
    
    private static int gThreadIdx = 0;
    
    private ReentrantLock mThreadLock = new ReentrantLock(false);

    public static synchronized PTOrderCenter getInstance() {
        if (sInstance == null) {
            sInstance = new PTOrderCenter();
        }
        return sInstance;
    }

    private static final int ACTION_REQ_REFRESH_DATA = 110;
    private static final int ACTION_RSP_REFRESH_DATA = 111;
    
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void dispatchMessage(Message msg) {
            switch(msg.what) {
                case ACTION_REQ_REFRESH_DATA:
                    mHandler.removeMessages(ACTION_REQ_REFRESH_DATA);
                    doRequestRefreshOrders((RefreshOrderListener)msg.obj);
                    
                    break;
                case ACTION_RSP_REFRESH_DATA:
                    mHandler.removeMessages(ACTION_RSP_REFRESH_DATA);
                    doResponseRefreshOrders(msg.arg1, (String)msg.obj);
                    
                    break;                
            }
        }
        
    };
    
    /**
     * 给外部提供请求获取最新订单数据
     * @param listener
     */
    public void requestRefreshOrders(RefreshOrderListener listener) {
        Message m = mHandler.obtainMessage();
        m.what = ACTION_REQ_REFRESH_DATA;
        m.obj = listener;
        mHandler.sendMessage(m);
    }
    
    /**
     * 请求刷新订单数据,在主线程中调用
     * @param listener
     */
    private void doRequestRefreshOrders(RefreshOrderListener listener) {
        LogUtil.d(TAG, "doRequestRefreshOrders reqsize="+mListener.size());

        if(listener != null) {
            SoftReference<RefreshOrderListener> weakref = new SoftReference<RefreshOrderListener>(listener);
            mListener.add(weakref);
        }
        
        if(mThreadLock.tryLock()) {
            mRefreshThread = new RefreshOrderThread();
            mRefreshThread.start();
        }
    }
    
    /**
     * 响应刷新订单数据结果,在主线程中调用
     * @param listener
     */
    private void doResponseRefreshOrders(int result, String errmsg) {
        LogUtil.d(TAG, "doResponseRefreshOrders result="+result+" errmsg="+errmsg);
        
        try {
            for(SoftReference<RefreshOrderListener> weakref : mListener) {
                RefreshOrderListener listener = weakref.get();
                if(listener != null) {
                    if(result >= 0) {
                        listener.refreshSuccess(result > 0);
                    } else {
                        listener.refreshFailure(errmsg);
                    }
                }
            }
            
        } finally {
            mListener.clear();
            if(mThreadLock.isLocked()) {
                mThreadLock.unlock();
            }
        }
    }

    /**
     * 所有需要订单的子业务都要向订单中心注册
     * @param interfaceMsg
     */
    public synchronized void register(AbstractMessageBussiness interfaceMsg) {
        if (mMsgList.contains(interfaceMsg)) {
            LogUtil.d(TAG, "the interface " + interfaceMsg + " ,has register");
            return;
        }
        mMsgList.add((AbstractMessageBussiness)interfaceMsg);
    }

    public AbstractMessageBussiness getService(PTOrderBean orderBean) {
        if (orderBean == null)
            return null;

        for (int i = 0; i < mMsgList.size(); i++) {
            AbstractMessageBussiness buss = mMsgList.get(i);
            if (buss != null && buss.getProductType() == orderBean.getProduct_type()) {
                return buss;
            }
        }
        return null;
    }

    /**
     * 查询所有订单数据,按时间倒序
     * 
     * @param startIndex
     * @param sum
     * @return
     */
    public List<PTOrderBean> loadOrders() {
        List<PTOrderBean> orderBeans = Config.getDatabaseHelper().getMessageCenterDB()
                .queryOrders(0, Integer.MAX_VALUE);
        return orderBeans;
    }

    /**
     * 查询数据库订单数据,按时间倒序,不包括无订单业务的数据
     * 
     * @param startIndex
     * @param sum
     * @return
     */
    public List<PTOrderBean> loadOrdersExceptNoOrder() {
        List<PTOrderBean> orderBeans = Config.getDatabaseHelper().getMessageCenterDB()
                .queryOrdersExceptNoOrder(0, Integer.MAX_VALUE);
        return orderBeans;
    }

    /**
     * 根据订单号返回订单数据
     * 
     * @param orderNumber
     * @return
     */
    public PTOrderBean getOrderByOrderNumber(String orderNumber) {
        return Config.getDatabaseHelper().getMessageCenterDB().queryOrderById(orderNumber);
    }
    
    /**
     * 增加刷新线程
     * @author change
     *
     */
    private class RefreshOrderThread extends Thread {
        private boolean isStopped;
        
        public RefreshOrderThread() {
            super("RefreshOrderThread_"+(++gThreadIdx));
            isStopped = false;
        }
        
        @Override
        public void run() {
            LogUtil.d(TAG, Thread.currentThread().getName()+" running...");
            
            //更新本地数据库
            int ret = -1;
            String errmsg = "";
            try {
                // 计算时间戳
                final long orderTimestamp = PTOrderCenter.getInstance().getNewestOrderTimestamp();
                int pageIdx = 1;
                
                ret = syncRefreshOrderData(orderTimestamp, pageIdx);
                LogUtil.d(TAG, "refreshOrderData ret="+ret);
            } catch (Exception e) {
                LogUtil.d(TAG, "RefreshOrderThread exception: "+e.getMessage());
                e.printStackTrace();
                ret = -1;
                errmsg = e.getMessage();
            } finally {
                Message m = mHandler.obtainMessage();
                m.what = ACTION_RSP_REFRESH_DATA;
                m.arg1 = ret;
                m.obj = errmsg;
                mHandler.sendMessage(m);
            }
        }
        
    };    
    
    /**
     * 同步请求更新订单数据
     * @return
     */
    public int syncRefreshOrderData() {
        final long orderTimestamp = PTOrderCenter.getInstance().getNewestOrderTimestamp();
        int pageIdx = 1;
        
        int ret = syncRefreshOrderData(orderTimestamp, pageIdx);
        return ret;
    }

    /**
     * 查询后台,同步本地数据库与后台数据库
     * 
     * @param listener
     */
    private int syncRefreshOrderData(long timeval, int pageIdx) {
        // 联网查询最新数据 TODO
        SimpleRequestData request = new SimpleRequestData();
        request.setParam(MsgCenterConfig.ORDER_TIMESTAMP, String.valueOf(timeval));
        request.setParam(MsgCenterConfig.PRODUCT_TYPE, "0");
        request.setParam(MsgCenterConfig.PAGE_SIZE, PAGE_SIZE);
        request.setParam(MsgCenterConfig.PAGE_NO, String.valueOf(pageIdx));

        LogUtil.d(TAG, "syncRefreshOrderData request order list, timeval="+timeval+" pageIdx=" + pageIdx + " page_size="+ PAGE_SIZE);
        String content = PTHTTP.getInstance().get(MsgCenterConfig.ORDER_LIST, request);
        /*
         * modify by putao_lhq @start
         * add code:
         */
        if (TextUtils.isEmpty(content)) {
            LogUtil.d(TAG, "syncRefreshOrderData-content is null, return 0");
            return -1;
        }/*@end by putao_lhq*/
        
        // 成功
        try {
            boolean isManThread = (Looper.myLooper() == Looper.getMainLooper());
            LogUtil.d(TAG, "isManThread: " + isManThread);

            LogUtil.d(TAG, "onSuccess: " + content);
            JSONObject json = new JSONObject(content);
            String ret_code = json.getString("ret_code");
            if ("0000".equals(ret_code)) {
                String msg = json.getString("msg");
                JSONObject data = json.getJSONObject("data");
                int pages = data.getInt("pages");

                LogUtil.d(TAG, "refreshOrderData onSuccess pages=" + pages);
                LogUtil.i(TAG, msg);

                if (data.getJSONArray("result").length() > 0) {
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<PTOrderBean>>() {
                    }.getType();
                    List<PTOrderBean> orderBeans = gson.fromJson(data.getJSONArray("result")
                            .toString(), listType);
                    
                    if (orderBeans != null) {
                        for (PTOrderBean ptOrderBean : orderBeans) {
                            final AbstractMessageBussiness msgBusi = getService(ptOrderBean);
                            if(msgBusi == null) {
                                LogUtil.e(TAG, "syncRefreshOrderData can not found busi for bean="+ptOrderBean.toString());
                                continue; 
                            }
                            if (msgBusi != null && msgBusi.checkOrder(ptOrderBean)) {
                                storeOrder(ptOrderBean);
                            } else {
                                LogUtil.e(TAG, "syncRefreshOrderData checkOrder failed, bean="+ptOrderBean.toString());
                            }
                        }
                    }
                    if (pages > pageIdx) {
                        pageIdx++;
                        return syncRefreshOrderData(timeval, pageIdx);
                    } else {
                        return 1;
                    }
                } else {
                    if (pages > pageIdx) {
                        pageIdx++;
                        return syncRefreshOrderData(timeval, pageIdx);
                    } else {
                        return 0;
                    }
                }
            } else {
                return -1;
            }
        } catch (JSONException e) {
            LogUtil.e(TAG, "syncRefreshOrderData exception="+e.getMessage());
            e.printStackTrace();
            return -1;
        }
        /*
         * modify by putao_lhq 
         * delete code: return 0;
         */
    }

    /**
     * 获取本地订单最新一条的时间戳
     * 
     * @return
     */
    public long getNewestOrderTimestamp() {
        // 计算时间戳
        List<PTOrderBean> orderBeans = Config.getDatabaseHelper().getMessageCenterDB()
                .queryOrders(0, 1);
        long timeval = 0;
        if (orderBeans != null && orderBeans.size() > 0) {
            timeval = orderBeans.get(0).getM_time();
        }

        return timeval;
    }

    public synchronized void storeOrder(PTOrderBean order) {

        Config.getDatabaseHelper().getMessageCenterDB().insertOrder(order);
    }

    public List<AbstractMessageBussiness> getAllService() {
        return mMsgList;
    }

    public interface RefreshOrderListener {
        public void refreshSuccess(boolean isDbChanged);

        public void refreshFailure(String msg);
    }

    /**
     * 加载所有未过期的订单/业务数据
     * 
     * @return
     */
    public ArrayList<PTOrderBean> loadOrdersNeedRemind() {
        ArrayList<PTOrderBean> orderBeans = new ArrayList<PTOrderBean>();
        List<PTMessageBean> messages = Config.getDatabaseHelper().getMessageCenterDB()
                .queryMessagesNotExpired();
        if (messages != null && messages.size() > 0) {
            OrderNumber orderNumber;
            IPTMessageCenter messageCenter = PTMessageCenterFactory.getPTMessageCenter();
            for (PTMessageBean bean : messages) {
                orderNumber = AbstractMessageBussiness.getOrderNumber(bean);
                if (orderNumber == null || TextUtils.isEmpty(orderNumber.getPt_order_no())) {
                    continue;
                }
                PTOrderBean orderBean = getOrderByOrderNumber(orderNumber.getPt_order_no());
                if (orderBean == null) {
                    continue;
                }
                if (orderBean.getView_status() == 2) {
                    continue;
                }
                if (!orderBeans.contains(orderBean)) {
                    if (messageCenter.getService(bean) == null) {
                        messageCenter.removeMessage(bean);
                        continue;
                    }
                    if (messageCenter.getService(bean).isOrderExpire(orderBean)) {
                        bean.setStatus(1);
                        messageCenter.saveMessage(bean);
                    } else {
//                        orderBean.setMessageBean(bean);
                        orderBeans.add(orderBean);
                    }
                }
            }
        }
        return orderBeans;
    }

    public void updateOrderData(PTOrderBean order) {
        Config.getDatabaseHelper().getMessageCenterDB().updateOrderData(order);
    }
	
	    @Override
    public void onLogin() {

    }

	/**
	 * 获取所有订单总数,只包括订单,不包括非订单
	 * @return
	 */
	public int getOrderSumExceptNoOrder(){
	    return Config.getDatabaseHelper().getMessageCenterDB().getOrderSumExceptNoOrder();
	}
	    
    @Override
    public void onLogout() {
        LogUtil.d(TAG, "order center get account logout");
        Config.getDatabaseHelper().getMessageCenterDB().clearTable(OrderTable.TABLE_NAME);
    }

    @Override
    public void onChange() {
        Config.getDatabaseHelper().getMessageCenterDB().clearTable(OrderTable.TABLE_NAME);

    }
    
    public int getUpdatePointNumber(){
        SharedPreferences sp=ContactsApp.getContext().getSharedPreferences(MsgCenterConfig.SHARED_NAME, Context.MODE_MULTI_PROCESS);
        int oldPoint=sp.getInt(MsgCenterConfig.UPDATE_POINT_NUMBER, 0);
        int newPoint=getOrderSumExceptNoOrder();
        return newPoint-oldPoint;
    }

}
