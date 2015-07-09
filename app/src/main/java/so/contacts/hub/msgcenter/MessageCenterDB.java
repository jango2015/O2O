
package so.contacts.hub.msgcenter;

import so.contacts.hub.util.MobclickAgentUtil;

import so.contacts.hub.ContactsApp;

import so.contacts.hub.util.UMengEventIds;

import so.contacts.hub.msgcenter.bean.PTMessageBean;
import so.contacts.hub.msgcenter.bean.PTOrderBean;
import so.contacts.hub.remind.utils.RemindUtils;

import java.util.ArrayList;
import java.util.List;

import com.aps.be;

import so.contacts.hub.db.DatabaseHelper;
import so.contacts.hub.util.LogUtil;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.text.TextUtils;

/**
 * 消息中心数据库, 统一数据库，各业务不对该数据库进行操作处理
 * 
 * @author putao_lhq
 */
public class MessageCenterDB {

    private SQLiteDatabase db;

    public MessageCenterDB(DatabaseHelper helper) {
        db = helper.getWritableDatabase();
    }

    public SQLiteDatabase getDB() {
        return db;
    }

    public static class MessageCenterTable implements BaseColumns {
        public static String TABLE_NAME = "message_center";

        // 定义数据中心表结构
        public static String MSG_ID = "msg_id";

        public static String MSG_PRODUCT_TYPE = "product_type";

        public static String MSG_SUBJECT = "msg_subject";

        public static String MSG_DIGEST = "msg_digest";

        public static String MSG_TIME = "msg_time";

        public static String MSG_EXPAND_PARAM = "msg_expand_param";
        
        public static String MSG_STATUS="msg_status";
    }

    /**
     * 订单数据表
     * 
     * @author putao_lhq
     */
    public static class OrderTable implements BaseColumns {
        public static String TABLE_NAME = "pt_order";

        /**
         * 订单号
         */
        public static String ORDER_NO = "order_no";

        /**
         * 订单主题
         */
        public static String ORDER_TITLE = "order_title";

        /**
         * 订单状态
         */
        public static String ORDER_STATUS = "order_status";

        /**
         * 订单状态码
         */
        public static String ORDER_STATUS_CODE = "order_status_code";

        /**
         * 订单金额单位分
         */
        public static String ORDER_PRICE = "order_price";

        /**
         * 支付方式
         */
        public static String ORDER_PAYMENT_TYPE = "order_payment_type";

        /**
         * 订单数据最后变化的时间
         */
        public static String ORDER_M_TIEM = "order_m_tiem";

        /**
         * 业务类型
         */
        public static String ORDER_PRODUCT_TYPE = "order_product_type";

        /**
         * 业务产品id
         */
        public static String ORDER_PRODUCT_ID = "order_product_id";

        /**
         * 查看状态
         */
        public static String ORDER_VIEW_STATUS = "order_view_status";

        /**
         * 订单业务详情扩展字段
         */
        public static String ORDER_EXPAND = "order_expand";

        
        /**
         * 优惠券列表
         */
        public static String ORDER_COUPON_IDS = "order_coupon_ids";
    }

    public static String getCreateMessageTableSQL() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ").append(MessageCenterTable.TABLE_NAME).append(" (");
        sb.append(MessageCenterTable._ID).append(" INTEGER  PRIMARY KEY autoincrement,");
        sb.append(MessageCenterTable.MSG_ID).append(" INTEGER,");
        sb.append(MessageCenterTable.MSG_PRODUCT_TYPE).append(" INTEGER,");
        sb.append(MessageCenterTable.MSG_SUBJECT).append(" TEXT,");
        sb.append(MessageCenterTable.MSG_DIGEST).append(" TEXT,");
        sb.append(MessageCenterTable.MSG_TIME).append(" TEXT,");
        sb.append(MessageCenterTable.MSG_STATUS).append(" INTEGER,");
        sb.append(MessageCenterTable.MSG_EXPAND_PARAM).append(" TEXT");
        sb.append(");");
        return sb.toString();
    }

    public static String getCreateOrderTableSQL() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ").append(OrderTable.TABLE_NAME).append(" (");
        sb.append(OrderTable._ID).append(" INTEGER  PRIMARY KEY autoincrement,");
        //add by xcx 2015-01-12 start 新增优惠券列表字段
        sb.append(OrderTable.ORDER_COUPON_IDS).append(" TEXT,");
        //add by xcx 2015-01-12 end 新增优惠券列表字段
        sb.append(OrderTable.ORDER_EXPAND).append(" TEXT,");
        sb.append(OrderTable.ORDER_M_TIEM).append(" LONG,");
        sb.append(OrderTable.ORDER_NO).append(" TEXT,");
        sb.append(OrderTable.ORDER_PRICE).append(" INTEGER,");
        sb.append(OrderTable.ORDER_PRODUCT_ID).append(" INTEGER,");
        sb.append(OrderTable.ORDER_PRODUCT_TYPE).append(" INTEGER,");
        sb.append(OrderTable.ORDER_PAYMENT_TYPE).append(" INTEGER,");
        sb.append(OrderTable.ORDER_VIEW_STATUS).append(" INTEGER,");
        sb.append(OrderTable.ORDER_STATUS).append(" TEXT,");
        sb.append(OrderTable.ORDER_STATUS_CODE).append(" INTEGER,");
        sb.append(OrderTable.ORDER_TITLE).append(" TEXT");
        sb.append(");");
        return sb.toString();
    }

    /**
     * 清楚表tableName数据
     * 
     * @param tableName
     */
    public void clearTable(String tableName) {
        if (TextUtils.isEmpty(tableName)) {
            return;
        }
        db.delete(tableName, null, null);
    }

    /**
     * 插入消息中心数据
     * 
     * @param bean
     * @return 插入 true,更新 false
     */
    public boolean insertMessage(PTMessageBean bean) {
        if (bean == null) {
            LogUtil.d(PTMessageCenterFactory.TAG, "insertMessage is null");
            return false;
        }

        ContentValues values = getValues(bean);
        if (bean.get_id() != 0) {
            LogUtil.d(PTMessageCenterFactory.TAG, "insertMessage -> " + bean.toString()
                    + " has exist, update data");
            db.update(MessageCenterTable.TABLE_NAME, values,
                    MessageCenterTable._ID + "=" + bean.get_id(), null);
            return false;
        } else {
            db.insert(MessageCenterTable.TABLE_NAME, null, values);
            RemindUtils.addMsgVirtualNode();
            if (bean!=null) {
                // 统计XX提醒卡片出现的次数
            }
            return true;
        }
    }

    /**
     * 插入订单数据
     * 
     * @param msg_id
     * @param order_id
     * @param order_content
     */
    public synchronized void insertOrder(PTOrderBean order) {
        if (order == null) {
            return;
        }

        LogUtil.d(PTMessageCenterFactory.TAG, "insertOrder: " + order.toString());
        if (TextUtils.isEmpty(order.getOrder_no())) {
            return;
        }

        PTOrderBean bean = queryOrderById(order.getOrder_no());
        ContentValues values = new ContentValues();
        values.put(OrderTable.ORDER_EXPAND, order.getExpand());
        values.put(OrderTable.ORDER_M_TIEM, order.getM_time());
        values.put(OrderTable.ORDER_NO, order.getOrder_no());
        values.put(OrderTable.ORDER_PRICE, order.getPrice());
        values.put(OrderTable.ORDER_PRODUCT_ID, order.getProduct_id());
        values.put(OrderTable.ORDER_PRODUCT_TYPE, order.getProduct_type());
        values.put(OrderTable.ORDER_PAYMENT_TYPE, order.getPayment_type());
        // values.put(OrderTable.ORDER_VIEW_STATUS, order.getView_status());
        values.put(OrderTable.ORDER_STATUS, order.getStatus());
        values.put(OrderTable.ORDER_STATUS_CODE, order.getStatus_code());
        values.put(OrderTable.ORDER_TITLE, order.getTitle());
        values.put(OrderTable.ORDER_COUPON_IDS, order.getCoupon_ids());
        if (bean == null) {
            db.insert(OrderTable.TABLE_NAME, null, values);
        } else {
            db.update(OrderTable.TABLE_NAME, values, OrderTable.ORDER_NO + "=?", new String[] {
                order.getOrder_no()
            });
        }
    }

    public void updateOrderData(PTOrderBean order) {
        if (order == null) {
            return;
        }

        LogUtil.d(PTMessageCenterFactory.TAG, "updateOrderData: " + order.toString());
        if (TextUtils.isEmpty(order.getOrder_no())) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(OrderTable.ORDER_EXPAND, order.getExpand());
        values.put(OrderTable.ORDER_M_TIEM, order.getM_time());
        values.put(OrderTable.ORDER_NO, order.getOrder_no());
        values.put(OrderTable.ORDER_PRICE, order.getPrice());
        values.put(OrderTable.ORDER_PRODUCT_ID, order.getProduct_id());
        values.put(OrderTable.ORDER_PRODUCT_TYPE, order.getProduct_type());
        values.put(OrderTable.ORDER_PAYMENT_TYPE, order.getPayment_type());
        values.put(OrderTable.ORDER_VIEW_STATUS, order.getView_status());
        values.put(OrderTable.ORDER_STATUS, order.getStatus());
        values.put(OrderTable.ORDER_STATUS_CODE, order.getStatus_code());
        values.put(OrderTable.ORDER_TITLE, order.getTitle());

        db.update(OrderTable.TABLE_NAME, values, OrderTable.ORDER_NO + "=?", new String[] {
            order.getOrder_no()
        });
    }

    public void setOrderDeleted(String orderNo) {
        if (TextUtils.isEmpty(orderNo)) {
            return;
        }

        LogUtil.d(PTMessageCenterFactory.TAG, "setOrderDeleted: " + orderNo);

        ContentValues values = new ContentValues();
        values.put(OrderTable.ORDER_VIEW_STATUS, 2);

        db.update(OrderTable.TABLE_NAME, values, OrderTable.ORDER_NO + "=?", new String[] {
            orderNo
        });
    }

    private ContentValues getValues(PTMessageBean bean) {
        ContentValues values = new ContentValues();
        // values.put(MessageCenterTable._ID, bean.get_id());
        values.put(MessageCenterTable.MSG_ID, bean.getMsgId());
        values.put(MessageCenterTable.MSG_PRODUCT_TYPE, bean.getProductType());
        values.put(MessageCenterTable.MSG_SUBJECT, bean.getSubject());
        values.put(MessageCenterTable.MSG_DIGEST, bean.getDigest());
        values.put(MessageCenterTable.MSG_EXPAND_PARAM, bean.getExpand_param());
        values.put(MessageCenterTable.MSG_TIME, String.valueOf(bean.getTime()));
        values.put(MessageCenterTable.MSG_STATUS, bean.getStatus());
        return values;
    }

    private void closeCursor(Cursor cursor) {
        if (cursor != null) {
            try {
                cursor.close();
            } catch (Exception e) {
            } finally {
                cursor.close();
            }
        }
    }

    /**
     * 根据消息id来查询消息
     * 
     * @param id
     * @return
     */
    public Cursor queryMessageByMsgId(long id) {
        return db.query(MessageCenterTable.TABLE_NAME, null, MessageCenterTable.MSG_ID + "=" + id,
                null, null, null, null);
    }

    /**
     * 根据pt订单号来查询消息,时间倒序,第0条数据为最新
     * 
     * @param orderNo
     * @return
     */
    public List<PTMessageBean> queryMessageByOrderNo(String orderNo) {
        Cursor cursor = db.query(MessageCenterTable.TABLE_NAME, null,
                MessageCenterTable.MSG_EXPAND_PARAM + " LIKE '%" + orderNo + "%'", null, null,
                null, MessageCenterTable.MSG_TIME + " DESC");

        if (cursor == null) {
            return null;
        }
        if (cursor.getCount() <= 0) {
            closeCursor(cursor);
            return null;
        }
        List<PTMessageBean> messages = new ArrayList<PTMessageBean>();
        while (cursor.moveToNext()) {
            PTMessageBean bean = covert(cursor);
            messages.add(bean);
        }
        closeCursor(cursor);
        return messages;
    }

    /**
     * 根据订单号来查询消息
     * 
     * @param id
     * @return
     */
    public Cursor queryOrderByOrderId(String id) {
        return db.query(OrderTable.TABLE_NAME, null, OrderTable.ORDER_NO + "=" + id, null, null,
                null, null);
    }

    /**
     * 查询消息数据
     * 
     * @return
     */
    public List<PTMessageBean> queryMessages() {
        Cursor cursor = db.query(MessageCenterTable.TABLE_NAME, null, null, null, null, null,
                MessageCenterTable.MSG_TIME + " DESC ");
        if (cursor == null) {
            return null;
        }
        if (cursor.getCount() <= 0) {
            closeCursor(cursor);
            return null;
        }
        List<PTMessageBean> messages = new ArrayList<PTMessageBean>();
        while (cursor.moveToNext()) {
            PTMessageBean bean = covert(cursor);
            messages.add(bean);
        }
        closeCursor(cursor);
        return messages;
    }

    /**
     * 查询所有未过期的消息数据
     * 
     * @return
     */
    public List<PTMessageBean> queryMessagesNotExpired() {
        Cursor cursor = db.query(MessageCenterTable.TABLE_NAME, null, MessageCenterTable.MSG_STATUS
                + "=?", new String[] {
            "0"
        }, null, null, MessageCenterTable.MSG_TIME + " DESC ");
        if (cursor == null) {
            return null;
        }
        if (cursor.getCount() <= 0) {
            closeCursor(cursor);
            return null;
        }
        List<PTMessageBean> messages = new ArrayList<PTMessageBean>();
        while (cursor.moveToNext()) {
            PTMessageBean bean = covert(cursor);
            messages.add(bean);
        }
        closeCursor(cursor);
        return messages;
    }

    /**
     * 查询本地数据库订单总数 add by zj 2014-12-27 11:26:04
     * 
     * @return
     */
    public int queryOrderSum() {
        Cursor cursor = db.rawQuery("select count(1) from " + OrderTable.TABLE_NAME, null);
        if (cursor == null) {
            return 0;
        }
        if (cursor.getCount() <= 0) {
            closeCursor(cursor);
            return 0;
        }
        if (cursor.moveToNext()) {
            return cursor.getInt(0);
        } else {
            return 0;
        }

    }

    public PTOrderBean queryOrderById(String order_id) {
        if (TextUtils.isEmpty(order_id)) {
            return null;
        }
        Cursor cursor = db.query(OrderTable.TABLE_NAME, null, OrderTable.ORDER_NO + "=?",
                new String[] {
                    order_id
                }, null, null, null);
        if (cursor == null) {
            return null;
        }
        if (cursor.getCount() <= 0) {
            closeCursor(cursor);
            return null;
        }
        if (cursor.moveToNext()) {
            PTOrderBean bean = covertOrder(cursor);
            if (bean != null) {
                return bean;
            }
        }
        closeCursor(cursor);
        return null;
    }

    /**
     * 分页查询数据库订单数据,按时间倒序,包括无订单业务的数据 add by zj 2014-12-27 11:37:42
     * 
     * @param startIndex 分页起始坐标
     * @param sum 这一页数据的数量
     * @return
     */
    public List<PTOrderBean> queryOrders(int startIndex, int sum) {
        List<PTOrderBean> orders = new ArrayList<PTOrderBean>();
        Cursor cursor = db.query(OrderTable.TABLE_NAME, null, null, null, null, null,
                OrderTable.ORDER_M_TIEM + " DESC ", startIndex + "," + sum);
        if (cursor == null) {
            return orders;
        }
        if (cursor.getCount() <= 0) {
            closeCursor(cursor);
            return orders;
        }
        while (cursor.moveToNext()) {
            PTOrderBean bean = covertOrder(cursor);
            if (bean != null) {
                orders.add(bean);
            }
        }
        closeCursor(cursor);
        return orders;
    }
    
    /**
     * add by zj 2015-01-14 16:36:15
     * 获取所有订单总数,只包括订单,不包括非订单
     * @return
     */
    public int getOrderSumExceptNoOrder() {
        Cursor cursor = db.query(OrderTable.TABLE_NAME, new String[]{"COUNT(1)"}, OrderTable.ORDER_STATUS
                + " IS NOT NULL ", null, null, null, null);
        int sum=0;
        if (cursor == null) {
            return 0;
        }
        if (cursor.moveToNext()) {
            sum=cursor.getInt(0);
        }
        closeCursor(cursor);
        return sum;
    }

    /**
     * 分页查询数据库订单数据,按时间倒序,不包括无订单业务的数据
     * 
     * @param startIndex 分页起始坐标
     * @param sum 这一页数据的数量
     * @return
     */
    public List<PTOrderBean> queryOrdersExceptNoOrder(int startIndex, int sum) {
        List<PTOrderBean> orders = new ArrayList<PTOrderBean>();
        Cursor cursor = db.query(OrderTable.TABLE_NAME, null, OrderTable.ORDER_STATUS
                + " IS NOT NULL ", null, null, null, OrderTable.ORDER_M_TIEM + " DESC ", startIndex
                + "," + sum);
        if (cursor == null) {
            return orders;
        }
        if (cursor.getCount() <= 0) {
            closeCursor(cursor);
            return orders;
        }
        while (cursor.moveToNext()) {
            PTOrderBean bean = covertOrder(cursor);
            if (bean != null) {
                orders.add(bean);
            }
        }
        closeCursor(cursor);
        return orders;
    }

    /**
     * 分页查询数据库订单数据,按时间倒序,只包括无订单业务的数据
     * 
     * @param startIndex 分页起始坐标
     * @param sum 这一页数据的数量
     * @return
     */
    public List<PTOrderBean> queryNotOrdersData(int startIndex, int sum) {
        List<PTOrderBean> orders = new ArrayList<PTOrderBean>();
        Cursor cursor = db.query(OrderTable.TABLE_NAME, null,
                OrderTable.ORDER_STATUS + " IS NULL ", null, null, null, OrderTable.ORDER_M_TIEM
                        + " DESC ", startIndex + "," + sum);
        if (cursor == null) {
            return orders;
        }
        if (cursor.getCount() <= 0) {
            closeCursor(cursor);
            return orders;
        }
        while (cursor.moveToNext()) {
            PTOrderBean bean = covertOrder(cursor);
            if (bean != null) {
                orders.add(bean);
            }
        }
        closeCursor(cursor);
        return orders;
    }

    private PTMessageBean covert(Cursor msgCursor) {
        PTMessageBean bean = new PTMessageBean();
        if (msgCursor != null && msgCursor.getCount() > 0) {
            bean.set_id(msgCursor.getLong(msgCursor.getColumnIndex(MessageCenterTable._ID)));
            bean.setMsgId(msgCursor.getLong(msgCursor.getColumnIndex(MessageCenterTable.MSG_ID)));
            bean.setProductType(msgCursor.getInt(msgCursor
                    .getColumnIndex(MessageCenterTable.MSG_PRODUCT_TYPE)));
            bean.setDigest(msgCursor.getString(msgCursor
                    .getColumnIndex(MessageCenterTable.MSG_DIGEST)));
            bean.setExpand_param(msgCursor.getString(msgCursor
                    .getColumnIndex(MessageCenterTable.MSG_EXPAND_PARAM)));
            bean.setSubject(msgCursor.getString(msgCursor
                    .getColumnIndex(MessageCenterTable.MSG_SUBJECT)));
            bean.setStatus(msgCursor.getInt(msgCursor.getColumnIndex(MessageCenterTable.MSG_STATUS)));
            String timeStr = msgCursor.getString(msgCursor
                    .getColumnIndex(MessageCenterTable.MSG_TIME));
            if (!TextUtils.isEmpty(timeStr)) {
                bean.setTime(Long.parseLong(timeStr));
            }
        }

        return bean;
    }

    private PTOrderBean covertOrder(Cursor orderCursor) {
        PTOrderBean bean = null;
        if (orderCursor != null && orderCursor.getCount() > 0) {
            bean = new PTOrderBean();

            bean.setExpand(orderCursor.getString(orderCursor
                    .getColumnIndex(OrderTable.ORDER_EXPAND)));
            bean.setM_time(orderCursor.getLong(orderCursor.getColumnIndex(OrderTable.ORDER_M_TIEM)));
            bean.setOrder_no(orderCursor.getString(orderCursor.getColumnIndex(OrderTable.ORDER_NO)));
            bean.setPrice(orderCursor.getInt(orderCursor.getColumnIndex(OrderTable.ORDER_PRICE)));
            bean.setProduct_id(orderCursor.getInt(orderCursor
                    .getColumnIndex(OrderTable.ORDER_PRODUCT_ID)));
            bean.setProduct_type(orderCursor.getInt(orderCursor
                    .getColumnIndex(OrderTable.ORDER_PRODUCT_TYPE)));
            bean.setPayment_type(orderCursor.getInt(orderCursor
                    .getColumnIndex(OrderTable.ORDER_PAYMENT_TYPE)));
            bean.setView_status(orderCursor.getInt(orderCursor
                    .getColumnIndex(OrderTable.ORDER_VIEW_STATUS)));
            bean.setStatus(orderCursor.getString(orderCursor
                    .getColumnIndex(OrderTable.ORDER_STATUS)));
            bean.setStatus_code(orderCursor.getInt(orderCursor
                    .getColumnIndex(OrderTable.ORDER_STATUS_CODE)));
            bean.setTitle(orderCursor.getString(orderCursor.getColumnIndex(OrderTable.ORDER_TITLE)));
            bean.setCoupon_ids(orderCursor.getString(orderCursor.getColumnIndex(OrderTable.ORDER_COUPON_IDS)));
        }
        return bean;
    }

    public int removeMessage(PTMessageBean msgBean) {
        return db.delete(MessageCenterTable.TABLE_NAME, MessageCenterTable._ID + "=?",
                new String[] {
                    msgBean.get_id() + ""
                });
    }
}
