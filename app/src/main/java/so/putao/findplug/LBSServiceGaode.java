
package so.putao.findplug;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.util.CommonValueUtil;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;

public class LBSServiceGaode {
    
    //add by hyl 2014-8-9 start
    public static final double DEFAULT_LATITUDE = 30.2784662;
    public static final double DEFAULT_LONGITUDE = 120.1194347;
    //add by hyl 2014-8-9 end
    
    public static final int LOCATION_TIMEOUT = 5 * 60 * 1000;//定位超时时间(五分钟)
    
    private static LocationManagerProxy mAMapLocationManager;

    private static volatile AMapLocation mAMapLocation;

    private static volatile AMapLocation mPreAMapLocation;

    private static LBSServiceListener mLBSServiceListener;

    private static AMapLocationListener mAMapLocationListener = new AMapLocationListener() {

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
            
        }

        @Override
        public void onLocationChanged(AMapLocation location) {
            mPreAMapLocation = location;
            mAMapLocation = location;
            
            deactivate(); // 临时解决定位耗电的问题, modify by cj 2015/01/21
            
            if (mLBSServiceListener != null ) {
                /**
                 * 增加定位失败 判断逻辑
                 * update by hyl 2014-8-9
                 * old code :
                 * mLBSServiceListener.onLocationChanged(mAMapLocation.getCity(),
                        mAMapLocation.getLatitude(), mAMapLocation.getLongitude(),
                        mAMapLocation.getTime());
                 */
                if (location != null && location.getAMapException().getErrorCode() == 0) {//定位成功
                    
                    //add by hyl 2014-8-14
                    storeLocationInfo(location);
                    //add by hyl 2014-8-14
                    String city = mAMapLocation.getCity();
                    if (!TextUtils.isEmpty(city) && city.endsWith(CommonValueUtil.getInstance().getCityData())) {
                        city = city.substring(0, city.length() - 1);
                    }
                    
                    mLBSServiceListener.onLocationChanged(city,
                            mAMapLocation.getLatitude(), mAMapLocation.getLongitude(),
                            mAMapLocation.getTime());
                }else{//定位失败
                    mLBSServiceListener.onLocationFailed();
                }
            }
        }
    };

    public static void activate(Context context, LBSServiceListener listener) {
        Log.e("error", "activate()");
        mAMapLocation = null;
        mLBSServiceListener = listener;
        if (mAMapLocationManager == null) {
            mAMapLocationManager = LocationManagerProxy.getInstance(context);
            //此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            //注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用removeUpdates()方法来取消定位请求
            //在定位结束后，在合适的生命周期调用destroy()方法      
            //其中如果间隔时间为-1，则定位只定一次
            //在单次定位情况下，定位无论成功与否，都无需调用removeUpdates()方法移除请求，定位sdk内部会移除
            mAMapLocationManager.requestLocationData(LocationProviderProxy.AMapNetwork,
                    5 * 1000, 10, mAMapLocationListener);
        }
    }
    
    /**
     * 存储定位信息
     * @param location
     */
    protected static void storeLocationInfo(AMapLocation location) {
        SharedPreferences preferences = ContactsApp.getInstance().getSharedPreferences("location", Context.MODE_MULTI_PROCESS);
        Editor editor = preferences.edit();
        editor.putString("latitude", location.getLatitude()+"");
        editor.putString("longitude", location.getLongitude()+"");
        editor.putString("city", location.getCity());
        editor.putLong("last_location_time", System.currentTimeMillis());
        editor.commit();
    }

    /**
     * 定位
     * 先判断上次是否有定位信息记录且未超时(十分钟)，若未超时且有信息则将上次定位信息返回
     * @param context
     * @param listener
     */
    public static void process_activate(Context context, LBSServiceListener listener){
        SharedPreferences preferences = context.getSharedPreferences("location", Context.MODE_MULTI_PROCESS);
        long time = preferences.getLong("last_location_time",0);
        //计算是否超时
        boolean isOutTime = false;
        if(time != 0){
            if((System.currentTimeMillis() - time) > LOCATION_TIMEOUT){
                isOutTime = true;
            }
        }else{
            isOutTime = true;
        }
        
        if(isOutTime){//已超时，重新开启定位
            activate(context, listener);
        }else{//未超时
            String latitude = preferences.getString("latitude","");
            String longitude = preferences.getString("longitude","");
            String city = preferences.getString("city","");
            
            if(!TextUtils.isEmpty(latitude) && !TextUtils.isEmpty(longitude)){
                if (!TextUtils.isEmpty(city) && city.endsWith(CommonValueUtil.getInstance().getCityData())) {
                    city = city.substring(0, city.length() - 1);
                }
                //经纬度信息不为空
                listener.onLocationChanged(city, Double.parseDouble(latitude), Double.parseDouble(longitude), 0);
            }else{
                activate(context, listener);
            }
        }
    }

    public static void deactivate() {
        Log.e("error", "deactivate()");
        if (mAMapLocationManager != null) {
            mAMapLocationManager.removeUpdates(mAMapLocationListener);
            mAMapLocationManager.destory();
        }
        mAMapLocationManager = null;
    }

    public static boolean hasPreInfo() {
        if (mPreAMapLocation != null) {
            return true;
        }
        return false;
    }

    public static String getPreCity() {
        String city = "";

        if (mPreAMapLocation != null) {
            city = mPreAMapLocation.getCity();
        }

        if (TextUtils.isEmpty(city)) {
            return CommonValueUtil.getInstance().getCityShenzhen();
        }
        if (city.endsWith(CommonValueUtil.getInstance().getCityData())) {
            city = city.substring(0, city.length() - 1);
        }
        return city;
    }

    public static double getPreLatitude() {
        if (mPreAMapLocation != null) {
            return mPreAMapLocation.getLatitude();
        }
        return 0;
    }

    public static double getPreLongitude() {
        if (mPreAMapLocation != null) {
            return mPreAMapLocation.getLongitude();
        }
        return 0;
    }

    public static boolean hasInfo() {
        if (mAMapLocation != null || mPreAMapLocation != null) {
            return true;
        }
        return false;
    }

    public static String getCity() {
        if (mAMapLocation == null && mPreAMapLocation == null) {
            return "";
        }
        // String city = mAMapLocation !=
        // null?mAMapLocation.getCity():mPreAMapLocation.getCity();
        String city = "";
        if (mAMapLocation != null) {
            city = mAMapLocation.getCity();
        } else if (mPreAMapLocation != null) {
            city = mPreAMapLocation.getCity();
        }

        if (TextUtils.isEmpty(city)) {
            return CommonValueUtil.getInstance().getCityShenzhen();
        }
        if (city.endsWith(CommonValueUtil.getInstance().getCityData())) {
            city = city.substring(0, city.length() - 1);
        }
        return city;
    }

    /**
     * 获取城市,不用默认值
     * @return
     */
    public static String getCity2() {
        if (mAMapLocation == null && mPreAMapLocation == null) {
            return "";
        }
        // String city = mAMapLocation !=
        // null?mAMapLocation.getCity():mPreAMapLocation.getCity();
        String city = "";
        if (mAMapLocation != null) {
            city = mAMapLocation.getCity();
        } else if (mPreAMapLocation != null) {
            city = mPreAMapLocation.getCity();
        }

        if (TextUtils.isEmpty(city)) {
            return "";
        }
        if (city.endsWith(CommonValueUtil.getInstance().getCityData())) {
            city = city.substring(0, city.length() - 1);
        }
        return city;
    }
    
    /**
     * 获取本地城市,不定位
     * @return
     */
    public static String getLocCity() {
        SharedPreferences preferences = ContactsApp.getInstance().getSharedPreferences("location", Context.MODE_MULTI_PROCESS);
        String city = preferences.getString("city","");
        return city;
    }

    
    
    public static double getLatitude() {
        double latitude = 0;
        if (mAMapLocation != null) {
            latitude = mAMapLocation.getLatitude();
        } else if (mPreAMapLocation != null) {
            latitude = mPreAMapLocation.getLatitude();
        }
        
        if (latitude == 0 ) {
            SharedPreferences preferences = ContactsApp.getInstance().getSharedPreferences(
                    "location", Context.MODE_MULTI_PROCESS);
            String latitudeStr = preferences.getString("latitude", "");
            if (!TextUtils.isEmpty(latitudeStr)) {
                latitude = Double.parseDouble(latitudeStr);
            }
        }
        if (latitude == 0) {
            latitude = DEFAULT_LATITUDE;
        }
       /*  delet by xcx 2015-2-5
         * 读取上一次定位的信息
         * add by hyl 2015-1-5 start
         
        else {
            SharedPreferences preferences = ContactsApp.getInstance().getSharedPreferences("location", Context.MODE_MULTI_PROCESS);
            String latitudeStr = preferences.getString("latitude","");
            if(!TextUtils.isEmpty(latitudeStr)){
                latitude = Double.parseDouble(latitudeStr);
            }
            //add by hyl 2015-1-5 end
        }*/
        
        return latitude;
        // return mAMapLocation != null ? mAMapLocation.getLatitude():
        // mPreAMapLocation.getLatitude();
    }
    
    /**
     * 从本地获取保存的经纬度,不定位
     * @return
     */
    public static double getLocLatitude() {
        double latitude = 0.0f;
        SharedPreferences preferences = ContactsApp.getInstance().getSharedPreferences("location",
                Context.MODE_MULTI_PROCESS);
        String latitudeStr = preferences.getString("latitude", "");
        if (!TextUtils.isEmpty(latitudeStr)) {
            latitude = Double.parseDouble(latitudeStr);
        }
        return latitude;
    }
    

    public static double getLongitude() {
        double longitude = 0;
        if (mAMapLocation != null) {
            longitude = mAMapLocation.getLongitude();
        } else if (mPreAMapLocation != null) {
            longitude = mPreAMapLocation.getLongitude();
        }
        if(longitude==0){
            SharedPreferences preferences = ContactsApp.getInstance().getSharedPreferences("location", Context.MODE_MULTI_PROCESS);
            String longitudeStr = preferences.getString("longitude","");
            if(!TextUtils.isEmpty(longitudeStr)){
                longitude = Double.parseDouble(longitudeStr);
            }
        }
        if(longitude==0){
            longitude=DEFAULT_LONGITUDE;
        }
        
       /* delete by xcx 2015-2-5
         * 读取上一次定位的信息
         * add by hyl 2015-1-5 start
         
        else {
            SharedPreferences preferences = ContactsApp.getInstance().getSharedPreferences("location", Context.MODE_MULTI_PROCESS);
            String longitudeStr = preferences.getString("longitude","");
            if(!TextUtils.isEmpty(longitudeStr)){
                longitude = Double.parseDouble(longitudeStr);
            }
            //add by hyl 2015-1-5 end
        }*/
        
       
        
        return longitude;
//        return mAMapLocation != null ? mAMapLocation.getLongitude() : mPreAMapLocation
//                .getLongitude();
    }

    /**
     * 获取本地保存的经纬度,不定位
     * @return
     */
    public static double getLocLongitude() {
        double longitude = 0.0f;
        SharedPreferences preferences = ContactsApp.getInstance().getSharedPreferences("location",
                Context.MODE_MULTI_PROCESS);
        String longitudeStr = preferences.getString("longitude", "");
        if (!TextUtils.isEmpty(longitudeStr)) {
            longitude = Double.parseDouble(longitudeStr);

        }
        return longitude;
    }
    
    
    public static long getTime() {
        if (mAMapLocation == null && mPreAMapLocation == null) {
            return 0;
        }
        long time = 0;
        if (mAMapLocation != null) {
            time = mAMapLocation.getTime();
        } else if (mPreAMapLocation != null) {
            time = mPreAMapLocation.getTime();
        }
        return time;
        // return mAMapLocation !=
        // null?mAMapLocation.getTime():mPreAMapLocation.getTime();
    }

    public interface LBSServiceListener {
        public void onLocationChanged(String city, double latitude, double longitude, long time);
        
        /*
         * 定位失败 回调函数
         * add by hyl 2014-8-9 start
         */
        public void onLocationFailed();
        //add by hyl 2014-8-9 start
    }
    
    /**
     * 判断定位信息是否已经过时   
     * @param min 距离上次定位的时间   单位（分钟）
     * @return true-过时 false-未过时
     * add by hyl 2014-8-9 17:31
     */
    public static boolean isTimeOut(int min){
        if((System.currentTimeMillis() - getTime()) > min * 60 * 1000){
            return true;
        }
        return false;
    }
    
    /**
     * 判断定位信息是否已经过时（距离上次定位十分钟）
     * @return true-过时 false-未过时
     * add by hyl 2014-8-9 17:31
     */
    public static boolean isTimeOut(){
       return isTimeOut(LOCATION_TIMEOUT);
    }
}
