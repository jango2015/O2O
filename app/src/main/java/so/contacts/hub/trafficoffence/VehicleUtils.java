package so.contacts.hub.trafficoffence;

import java.io.IOException;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import so.contacts.hub.core.Config;
import so.contacts.hub.msgcenter.report.MsgReportParameter;
import so.contacts.hub.net.IResponse;
import so.contacts.hub.net.PTHTTP;
import so.contacts.hub.net.SimpleRequestData;
import so.contacts.hub.trafficoffence.bean.PeccancyResult;
import so.contacts.hub.trafficoffence.bean.RequestData;
import so.contacts.hub.util.LogUtil;
import android.app.Notification;
import android.content.Intent;

import com.google.gson.Gson;
import com.mdroid.core.http.IgnitedHttpResponse;
import com.yulong.android.contacts.discover.R;

public class VehicleUtils {
    private static final String TAG = VehicleUtils.class.getSimpleName();
    
    public static final String VEHICLE_ACTION_CODE = "18000";
    
    /**删除车牌号*/
    public static boolean doDeleteCar(final String vehicleIds) {
        
        String del_car_url = MsgReportParameter.DEL_MUL_CARINFO_URL;
        IgnitedHttpResponse reportResponse = null;// 上报服务器数据
        SimpleRequestData reqData = new SimpleRequestData();
        try {
            LogUtil.d(TAG, "doDeleteCar  ：" + vehicleIds);
            reportResponse = Config.getApiHttp().post(del_car_url + "?" + vehicleIds).send();
            String report = reportResponse.getResponseBodyAsString();
            LogUtil.d(TAG, "report=" + report);
            
            JSONObject obj = new JSONObject(report);
            String ret_code = obj.getString("ret_code");
            if ("0000".equals(ret_code)) {// TODO 删除失败;
                return true;
            }
        } catch (ConnectException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    public static void doSearchVehicle(String paramStr) {
        LogUtil.i(TAG, "doSearchVehicle paramStr=" + paramStr);
        
        try {
            final PeccancyResult result = new Gson().fromJson(paramStr,
                    PeccancyResult.class);
            if (result == null) {
                return;
            }
            
            final String query = result.getApi_url();
            final String params = result.getParams();
            
            JSONObject jsonStr = new JSONObject(params);
            RequestData reqData = new RequestData();
            reqData.setParam(getMaps(jsonStr));
            PTHTTP.getInstance().asynPost(query, reqData, new IResponse() {
                @Override
                public void onSuccess(String content) {
                    LogUtil.d(TAG, "content: " + content);
                    // 返回查询微车url,返回content为json,将此字段返回给服务器解析,服务器解析完毕,再推送消息下来;
                    String url = Config.TRAFFIC_OFENCE.QUERY_USER_CAR_URL;
                    Map<String, String> paramsMap = new HashMap<String, String>();
                    paramsMap.put("json", content);
                    paramsMap.put("car_id", String.valueOf(result.getCar_id()));
                    
                    RequestData reqData = new RequestData();
                    reqData.setParam(paramsMap);
                    
                    PTHTTP.getInstance().asynPost(url, reqData, new IResponse() {
                        @Override
                        public void onSuccess(String content) {
                            LogUtil.d(TAG, "second return=" + content);
                        }

                        @Override
                        public void onFail(int errorCode) {
                            LogUtil.d(TAG, "error=" + errorCode);
                        }
                    });
                }

                @Override
                public void onFail(int errorCode) {
                    LogUtil.d(TAG, "onFail: ");
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    
    public static Map<String, String> getMaps(JSONObject jsonStr) throws JSONException {
        Map<String, String> paramsMap = new HashMap<String, String>();
        Iterator<String> it = jsonStr.keys();
        while (it.hasNext()) {
            String key = it.next();
            String value = jsonStr.getString(key);
            LogUtil.d(TAG, "key = " + key + " ,value= " + value);
            paramsMap.put(key, value);
        }
        return paramsMap;
    }
    
}
