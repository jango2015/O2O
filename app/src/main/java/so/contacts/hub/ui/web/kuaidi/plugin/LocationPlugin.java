package so.contacts.hub.ui.web.kuaidi.plugin;

import org.json.JSONException;
import org.json.JSONObject;

import so.contacts.hub.ui.web.config.Const;
import so.contacts.hub.ui.web.kuaidi.ResponseEvent;
import so.contacts.hub.ui.web.kuaidi.Session;
import so.contacts.hub.ui.web.kuaidi.WebPlugin;
import so.contacts.hub.util.LogUtil;
import so.putao.findplug.LBSServiceGaode;
import so.putao.findplug.LBSServiceGaode.LBSServiceListener;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class LocationPlugin extends WebPlugin implements Session, LBSServiceListener {

	private static final String TAG = "LocationPlugin";

    private LocationManager locationManagerProxy;
    
	private Context mContext = null;
	
    private static final int WHAT_STARTLOCATION = 1;

    private static final int WHAT_LOCATION_TIMEOUT = 2;

    /**
     * 当前定位SDK所属的坐标系： android原生 WGS84 , 百度定位 BD09 , 高德定位 GCJ02
     */
//    private static final String CURRENT_CORDINATES = "WGS84";
    private static final String CURRENT_CORDINATES = "GCJ02";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case WHAT_STARTLOCATION:
                {
                    // 开启定位
                	LogUtil.d(TAG, "开启定位");
                	LBSServiceGaode.activate(mContext, LocationPlugin.this);
//                    locationManagerProxy
//                            .requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
//                                    2000, 10, locationListener);
                    break;
                }
                case WHAT_LOCATION_TIMEOUT:
                {
                	LogUtil.d(TAG, "定位超时");
                    ResponseEvent event = new ResponseEvent();
                    event.setCode(Const.CODE_TIMEOUT);
                    publishEvent(event);

                    break;
                }
            }
        };
    };

    @Override
    public void onCreate(Context context) {
        Log.i(TAG, "LocationPlugin onCreate ");
        mContext = context;
        locationManagerProxy = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public ResponseEvent execute(JSONObject params) {
        mHandler.sendEmptyMessage(WHAT_STARTLOCATION);
        mHandler.sendEmptyMessageDelayed(WHAT_LOCATION_TIMEOUT, 15 * 1000);
        return super.execute(params);
    }

   private LocationListener locationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onLocationChanged(Location location) {

            /**
             * 定位数据回调，拼装成jsonObject传递给JS，同时关闭定位。
             */
    		LogUtil.d(TAG, "定位完成 latitude:"+location.getLatitude()+" longitude:"+location.getLongitude()+" coordinate:"+CURRENT_CORDINATES);
        	
            ResponseEvent event = new ResponseEvent();
            JSONObject result = new JSONObject();
            try {
                result.put("latitude", location.getLatitude());
                result.put("longitude", location.getLongitude());
                result.put("coordinate", CURRENT_CORDINATES);// 此处必须传递当前定位的坐标系标准
            } catch (JSONException e) {
                e.printStackTrace();
            }
            event.setData(result);
            mHandler.removeMessages(WHAT_LOCATION_TIMEOUT);
            publishEvent(event);
            removeLocationUpdates();
        }
    };

    private void removeLocationUpdates() {
//        locationManagerProxy.removeUpdates(locationListener);
    	LBSServiceGaode.deactivate();
    }

    @Override
    public void onDestory() {
        removeLocationUpdates();
    }

	@Override
	public void onLocationChanged(String city, double latitude,
			double longitude, long time) {

        /**
         * 定位数据回调，拼装成jsonObject传递给JS，同时关闭定位。
         */
		LogUtil.d(TAG, "定位完成 city:"+city+" latitude:"+latitude+" longitude:"+longitude+" time:"+time);
        ResponseEvent event = new ResponseEvent();
        JSONObject result = new JSONObject();
        try {
            result.put("latitude", latitude);
            result.put("longitude", longitude);
            result.put("coordinate", CURRENT_CORDINATES);// 此处必须传递当前定位的坐标系标准
        } catch (JSONException e) {
            e.printStackTrace();
        }
        event.setData(result);
        mHandler.removeMessages(WHAT_LOCATION_TIMEOUT);
        publishEvent(event);
        removeLocationUpdates();
    }

    @Override
    public void onLocationFailed() {
    	LogUtil.d(TAG, "定位失败");
        ResponseEvent event = new ResponseEvent();
        event.setCode(Const.CODE_TIMEOUT);
        publishEvent(event);
        removeLocationUpdates();        
    }

}
