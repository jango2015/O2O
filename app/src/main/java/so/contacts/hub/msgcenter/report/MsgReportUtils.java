package so.contacts.hub.msgcenter.report;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mdroid.core.http.IgnitedHttpResponse;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import so.contacts.hub.core.Config;
import so.contacts.hub.db.DatabaseHelper;
import so.contacts.hub.http.OrderEntity;
import so.contacts.hub.msgcenter.MsgCenterConfig;
import so.contacts.hub.net.PTHTTP;
import so.contacts.hub.net.SimpleRequestData;
import so.contacts.hub.thirdparty.tongcheng.bean.HotelOrderPostBean;
import so.contacts.hub.trafficoffence.bean.Vehicle;
import so.contacts.hub.util.LogUtil;

/**
 * 消息上报工具类
 * @author change 2015/01/22
 *
 */
public class MsgReportUtils {
    private static final String TAG = MsgReportUtils.class.getSimpleName();
    
    public static final int REPORT_STATUS_OK = 0;              // 上报成功
    public static final int REPORT_STATUS_REPEAT = 1;          // 需要重复上报
    public static final int REPORT_STATUS_EXCEPTION = -1;      //上报异常状态 
    
    /**
     * 异步上报消息
     * @param msg
     * @return REPORT_STATUS_OK - 上报成功
     *         REPORT_STATUS_REPEAT - 上报失败,需要重复上报
     *         REPORT_STATUS_EXCEPTION - 上报数据异常
     */
    public static void reportAsync(final Context context, MsgReport report) {
        boolean inserted = DatabaseHelper.getInstance(context).getMsgNotifyDB().insertMsgNotify(report);
        if(inserted) {
            Intent intent = new Intent();
            intent.setAction(MsgReportParameter.ACTION_REPORT);
            context.sendBroadcast(intent);
        }
    }
    
    /**
     * 同步上报火车票
     * @param msg
     * @return REPORT_STATUS_OK - 上报成功
     *         REPORT_STATUS_REPEAT - 上报失败,需要重复上报
     *         REPORT_STATUS_EXCEPTION - 上报数据异常
     */
    public static int doReportTrain(MsgReport msg) {
        if(msg == null || TextUtils.isEmpty(msg.getReportContent())) {
            return REPORT_STATUS_EXCEPTION;
        }
        
        try {
            SimpleRequestData reqData = new SimpleRequestData();
            int productType = OrderEntity.Product.train.getProductType();
            reqData.setParam("product_type", String.valueOf(productType));
            Map<String, String> infoMap = new HashMap<String, String>();
            infoMap.put("order_id", msg.getReportContent());
            Gson gson = new Gson();
            reqData.setParam("info", gson.toJson(infoMap));
            // reqData.setParam("order_id", mReportContent);
            String response = PTHTTP.getInstance().post(Config.MSG_REPORT.REPORT_URL, reqData);

            LogUtil.d(TAG, "doReportTrain response=" + response);
            if (TextUtils.isEmpty(response)) {
                return REPORT_STATUS_REPEAT;
            }
            
            JSONObject obj = new JSONObject(response);
            String ret_code = obj.getString("ret_code");
            if ("0000".equals(ret_code) || "12100".equals(ret_code)) { // 成功或单号重复
                return REPORT_STATUS_OK;
            }

        } catch (JSONException e) {
            LogUtil.e(TAG, e.getMessage());
            e.printStackTrace();
        }
        
        return REPORT_STATUS_REPEAT;
    }
    
    /**
     * 同步上报车牌号
     * @param msg
     * @return REPORT_STATUS_OK - 上报成功
     *         REPORT_STATUS_REPEAT - 上报失败,需要重复上报
     *         REPORT_STATUS_EXCEPTION - 上报数据异常
     */
    public static int doReportVehicle(MsgReport msg) {
        if(msg == null || TextUtils.isEmpty(msg.getReportContent())) {
            return REPORT_STATUS_EXCEPTION;
        }
        
        Vehicle vehicle = null;
        try {
            vehicle = Config.mGson.fromJson(msg.getReportContent(), Vehicle.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return REPORT_STATUS_EXCEPTION;
        } 
            
        if(vehicle == null) {
            return REPORT_STATUS_EXCEPTION;
        }
        
        String report_url = "";
        // 如果数据包含有效ID 说明是更新操作
        if (vehicle.getId() == -1) {
            report_url = MsgReportParameter.PECCANCY_REPORT_URL;
        } else {
            report_url = MsgReportParameter.UPDATE_CARINFO_URL;
        }
        
        IgnitedHttpResponse reportResponse = null;// 上报服务器数据
        SimpleRequestData reqData = new SimpleRequestData();
        String content = null;
        try {
            reportResponse = Config.getApiHttp().post(report_url, getVehicleData(msg.getReportContent())).send();
            String response = reportResponse.getResponseBodyAsString();
            LogUtil.i(TAG, "doReportVehicle response=" + response);
            
            JSONObject obj = new JSONObject(response);
            String ret_code = obj.getString("ret_code");
            if ("0000".equals(ret_code) || "12100".equals(ret_code)) {
                return REPORT_STATUS_OK;
            }
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage());
            e.printStackTrace();
        } 
        
        return REPORT_STATUS_REPEAT;
    }    
    
    /**
     * 同步上报酒店
     * @param msg
     * @return REPORT_STATUS_OK - 上报成功
     *         REPORT_STATUS_REPEAT - 上报失败,需要重复上报
     *         REPORT_STATUS_EXCEPTION - 上报数据异常
     */
    public static int doReportHotel(MsgReport msg) {
        if(msg == null || TextUtils.isEmpty(msg.getReportContent())) {
            return REPORT_STATUS_EXCEPTION;
        }
        
        HotelOrderPostBean hotelBean = null;
        try {
            hotelBean = Config.mGson.fromJson(msg.getReportContent(), HotelOrderPostBean.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return REPORT_STATUS_EXCEPTION;
        } 
            
        if(hotelBean == null) {
            return REPORT_STATUS_EXCEPTION;
        }
        
        if(TextUtils.isEmpty(hotelBean.hotelOrderId) ){
            return REPORT_STATUS_EXCEPTION;
        }
        
        /**
        pay/order/adapter
        {
            info : jsonStr,
            product_type : int
        }
        */
        
        try {
            SimpleRequestData requestData = new SimpleRequestData();
            requestData.setParam("product_type", String.valueOf(MsgCenterConfig.Product.hotel.getProductType()));
            requestData.setParam("info", msg.getReportContent());
        
            LogUtil.i(TAG, "doReportHotel orderId="+hotelBean.hotelOrderId+" HotelName="+hotelBean.hotelName+" HotelImg="+hotelBean.hotelImg);
            String reponse = PTHTTP.getInstance().post(Config.ORDER_UPLOAD_URL + "/pay/order/adapter", requestData);
            LogUtil.i(TAG, "doReportHotel reponse="+reponse);
            
            JSONObject obj = new JSONObject(reponse);
            String ret_code = obj.getString("ret_code");
            if ("0000".equals(ret_code) || "12100".equals(ret_code)) {
                return REPORT_STATUS_OK;
            }
            
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage());
            e.printStackTrace();
        }
        
        return REPORT_STATUS_REPEAT;
    }    
    
    private static HttpEntity getVehicleData(Vehicle vehicle) {
        String cotent = Config.mGson.toJson(vehicle);
        StringEntity entity = null;
        try {
            entity = new StringEntity(cotent, HTTP.UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entity;
    }
    
    private static HttpEntity getVehicleData(String content) {
        StringEntity entity = null;
        try {
            entity = new StringEntity(content, HTTP.UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entity;
    }    
    

    /**
     * 查询是否有需要上报的消息
     * @param context
     * @return
     */
    public static int getUnReportedMsg(final Context context) {
        List<MsgReport> msgList = DatabaseHelper.getInstance(context).getMsgNotifyDB().queryMsgNotifyAll();
        return msgList != null ? msgList.size() : 0 ;
    }
    
}
