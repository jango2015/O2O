package so.contacts.hub.http.bean;

import com.mdroid.core.util.SystemUtil;
import so.contacts.hub.ContactsApp;
import so.contacts.hub.core.Config;
import so.contacts.hub.core.ConstantsParameter;
import so.contacts.hub.db.YellowPageDB;
import so.contacts.hub.remind.utils.RemindUtils;
import so.contacts.hub.util.ContactsAppUtils;
import so.contacts.hub.util.SearchHotwordUtil;
import so.contacts.hub.util.SearchRecommendwordUtil;
import so.contacts.hub.util.UserInfoUtil;
import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;

public class ActiveRequestData extends BaseRequestData<ActiveResponseData> {
    public static final String ACTION_CODE = "00001";

	public int game_hot_version;  // [int][not null][热点游戏版本信息]
	public int game_center_version; // [int][not null][本地游戏中心ID]
	public int kinds_version;// [int][not null][本地榜单版本信息默认为0]
	public int is_collect; // [int][not null][是否为收集信息心跳,0:普通心跳，1:收集信息心跳]
	public long user_id; // [long][null able][产生心跳用户ID，如果没有登录则报0]
	public LocationInfo location; // [LocationInfo][null able][基站信息，用于标识地理位置信息]
	public int net_status; // [int][not null][网络状况.0:未定义网络状况，1:2G,2:3G,3:WIFI]
	public String mobile; // [String][null able][本机号码]
	public int yellow_data_version;//[int][黄页数据version]
	public int hot_keywords_version;//[int][null able][热词版本]
	public int habit_version;//[int][null able][用户习惯版本]
	public int recommend_searchwords_version;//[int][null able][推荐查询热词版本]
	public int search_config_version;//[int][null able][远程搜索配置版本]
	public int op_config_version;//[int][null able][运营数据版本]
	public GpsLocation gpsLocation;//[GpsLocation][null able][地理位置]
	
	public ActiveRequestData() {
		super("00001");
		
		Context context = ContactsApp.getInstance().getApplicationContext();
		
//		game_hot_version = GameUtils.getGameHotVersion();
//		game_center_version = GameUtils.getGameCenterVersion();
//		kinds_version = AppRecommendUtils.getKindsVersion(context);
		
		//add by hyl 2014-8-7 start
		YellowPageDB db = ContactsAppUtils.getInstance().getDatabaseHelper().getYellowPageDBHelper();
		yellow_data_version = db.queryDataVersion(YellowPageDB.YELLOW_DATA_VERSION_DEFAULT);
		//add by hyl 2014-8-7 end
		
		//add by ljq 2014-9-20 start
		hot_keywords_version = SearchHotwordUtil.getInstance().getHotKeywordsVersion();
		//add by ljq 2014-9-20 end
		
		//add by ljq 2014-9-20 start
		habit_version = UserInfoUtil.getInstace().getHabitDataVersion();
		//add by ljq 2014-9-20 end
		
		//add by ljq 2014-10-10 start
		recommend_searchwords_version = SearchRecommendwordUtil.getInstance().getRecommendwordsVersion()  ;
		//add by ljq 2014-10-10 end
		
		//add by putao_lhq 2014-10-17 start
		search_config_version = db.queryDataVersion(YellowPageDB.YELLOW_DATA_VERSION_SEARCH);
		//add by putao_lhq 2014-10-17 end
		
		//add by cj 2014-10-23 start
		op_config_version = RemindUtils.getRemindVersion();
		//add by cj 2014-10-23 start
		//add ljq 2014_11_26 start 添加用户网络状态和地理位置
		net_status = SystemUtil.getNetStatus(context);

		gpsLocation = new GpsLocation(context);
		//add ljq 2014_11_26 end 添加用户网络状态和地理位置
		// 未登录地理位置信息采集，由于心跳周期比较长，无需计数等待
		if (isActiveCollect(context)) {
			is_collect = 1;
			
//			user_id = Config.getUser().uid;
//			location = getLocationInfo(context);
//			net_status = SystemUtil.getNetStatus(context);
//			mobile = Utils.getPhoneNumber(context);
		} else {
			is_collect = 0;
		}
	}
	
	/**
	 * 是否达到激活采集信息条件
	 * @param context
	 * @return
	 */
	private boolean isActiveCollect(Context context) {
		final String key = "ACTIVE_REQUEST_COUNT";
		final int defaultVal = 0;
		
		SharedPreferences contactsSetting = context.getSharedPreferences(ConstantsParameter.CONTACTS_SETTING, Context.MODE_MULTI_PROCESS);
		int activeRequestCount = contactsSetting.getInt(key, defaultVal);
		if (activeRequestCount == defaultVal) {
			contactsSetting.edit().putInt(key, activeRequestCount + 1).commit();
			return true;// 首次上报
		} else if (activeRequestCount == 20) {// 20次收集信息心跳上报一次
			contactsSetting.edit().putInt(key, 1).commit();// 复位，从1开始
			return true;
		} else {
			contactsSetting.edit().putInt(key, activeRequestCount + 1).commit();
			return false;
		}
	}

	private LocationInfo getLocationInfo(Context context) {
		LocationInfo loc = null;
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		try {
			GsmCellLocation gcl = (GsmCellLocation) tm.getCellLocation();
			if (null != gcl) {//获取基站信息成功
				loc = new LocationInfo();
				loc.cell_id = String.valueOf(gcl.getCid());
				loc.lac = String.valueOf(gcl.getLac());
				if (tm.getSimState() == TelephonyManager.SIM_STATE_READY) {// SIM卡已挂载
					String operator = tm.getNetworkOperator();
					if (TextUtils.isEmpty(operator) || operator.length() < 3) {
						loc.mcc = "460";
						loc.mnc = "00";
					} else if (operator.length() >= 3) {
						loc.mcc = operator.substring(0,3);
						loc.mnc = operator.substring(3,operator.length());
					}
				}
			}
		} catch (Exception e) {
		}
		return loc;
	}
	
	@Override
	protected ActiveResponseData fromJson(String json) {
		return Config.mGson.fromJson(json, ActiveResponseData.class);
	}

	@Override
	protected ActiveResponseData getNewInstance() {
		return new ActiveResponseData();
	}
	
	
    private class GpsLocation {
        String longitude;

        String latitude;

        String city;
        

        @Override
        public String toString() {
            return "GpsLocation [longitude=" + longitude + ", latitude=" + latitude + ", city="
                    + city + "]";
        }

        GpsLocation (Context context){
            SharedPreferences pref = context.getSharedPreferences(
                    ConstantsParameter.YELLOW_PAGE_GPSLOCATION,
                    Context.MODE_MULTI_PROCESS);
            
            this.city = pref.getString(ConstantsParameter.YELLOW_PAGE_GPSLOCATION_CITY,"");
            this.latitude = pref.getString(ConstantsParameter.YELLOW_PAGE_GPSLOCATION_LATITUDE,"");
            this.longitude = pref.getString(ConstantsParameter.YELLOW_PAGE_GPSLOCATION_LONGTITUDE,"");
            

        }
    }
    

}
