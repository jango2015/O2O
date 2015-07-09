package so.contacts.hub.trafficoffence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.core.Config;
import so.contacts.hub.msgcenter.AbstractMessageBussiness;
import so.contacts.hub.msgcenter.MsgCenterConfig;
import so.contacts.hub.msgcenter.PTMessageCenterSettings;
import so.contacts.hub.msgcenter.bean.PTMessageBean;
import so.contacts.hub.msgcenter.bean.PTOrderBean;
import so.contacts.hub.net.IResponse;
import so.contacts.hub.net.PTHTTP;
import so.contacts.hub.trafficoffence.bean.PeccancyResult;
import so.contacts.hub.trafficoffence.bean.RequestData;
import so.contacts.hub.trafficoffence.bean.Vehicle;
import so.contacts.hub.ui.yellowpage.bean.YellowParams;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.UMengEventIds;
import so.contacts.hub.util.YellowUtil;
import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.yulong.android.contacts.discover.R;

public class TrafficOffenceMsgBusiness extends AbstractMessageBussiness {

	private Context context;
	private YellowParams mYellowParams = null;
	private static final String CAN_TRAFFIC_OFFENCE_SHOW = "can_traffic_offence_show";
	public static final String SAVED_CARS = "saved_cars";
	private static TrafficOffenceMsgBusiness mInstance = null;
	private static String TAG = TrafficOffenceMsgBusiness.class.getSimpleName();

	private TrafficOffenceMsgBusiness(Context context) {
		super(context);
		this.context = context;
		super.logoId = R.drawable.putao_icon_order_wz;
		super.smallLogoId = R.drawable.putao_icon_order_wz_s;
		super.title = R.string.illegal;
		super.productType = MsgCenterConfig.Product.traffic_offence
				.getProductType();
		umengInsertDataEventId = UMengEventIds.DISCOVER_YELLOWPAGE_MY_MSG_CENTER_CAR_ILLEGAL_ITEM_NUM;
	}

	public static TrafficOffenceMsgBusiness getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new TrafficOffenceMsgBusiness(context);
		}
		return mInstance;
	}
    
	@Override
	public View getOrderView(PTOrderBean bean, View convertView) {
		return null;
	}

	@Override
	public void click(PTMessageBean bean, Activity context) {
		super.click(bean, context);
		if (bean == null) {
			return;
		}
		String exparam = bean.getExpand_param();
		if (exparam == null) {
			return;
		}
		PeccancyResult result = null;
		try {
			result = new Gson().fromJson(exparam, PeccancyResult.class);
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		}
		onClick(result, context);
		// add 2014-12-31 xcx start 统计埋点
		MobclickAgentUtil
				.onEvent(
						ContactsApp.getInstance().getApplicationContext(),
						UMengEventIds.DISCOVER_YELLOWPAGE_MY_MSG_CENTER_CAR_ILLEGA_ITEM_CLICK);
		// add 2014-12-31 xcx end 统计埋点
	}

	

	private void onClick(PeccancyResult result, Activity context) {
		// TODO 违章查询接口;
		if (result == null) {
			return;
		}
		LogUtil.i(TAG, result.toString());
		Intent intent = new Intent(context, TrafficOffenceDetailActivity.class);
//		intent.putExtra("title", "违章查询");
		intent.putExtra(PeccancyResult.class.getSimpleName(), result);
		// TODO 违章查询url 组装;
		mYellowParams = new YellowParams();
//		mYellowParams.setUrl(url);
		mYellowParams.setTitle("违章查询");
		intent.putExtra(YellowUtil.TargetIntentParams, mYellowParams);
		context.startActivity(intent);

	}

	@Override
	public void handleBusiness(PTMessageBean message) {
        LogUtil.i(TAG, "handle message=" + message.toString());
        if (getEnable()) {
            String exparam = message.getExpand_param();
            if (exparam == null) {
                return;
            }
            
            if(message.getIs_notify() == 0) {
                return;
            }

            try {
                final PeccancyResult result = new Gson().fromJson(exparam, PeccancyResult.class);
                if (result == null) {
                    return;
                }

                Intent intent = new Intent(context, TrafficOffenceDetailActivity.class);
                intent.putExtra(PeccancyResult.class.getSimpleName(), result);
                Notification notification = new Notification.Builder(context)
                        .setContentTitle(message.getSubject()).setContentText(message.getDigest())
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.drawable.putao_ic_launcher).build();
                sendNotification(notification, intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}

	@Override
	public boolean isOrderExpire(PTOrderBean orderContent) {
		return false;
	}
	
	@Override
	public void setEnable(boolean enable) {
	    boolean lastEnable = getEnable();

	    //modify by cj 2015/01/12 BUG #2741要增加这个需求
	    //modity by ljq 2015/01/20 暂时注释掉直接进去设置需求
//        final ArrayList<Vehicle> list = getVehicleList();
//	    if (!lastEnable && enable && (list == null || list.size() == 0)) {
//	        Intent intent = new Intent(context,
//                    VehicleInfoSettingActivity.class);
//            intent.putExtra("title", "设置车辆信息");
//            intent.putExtra(YellowUtil.TargetIntentParams,
//                    mYellowParams);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent);
//            
//            MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
//                    UMengEventIds.DISCOVER_YELLOWPAGE_MY_MSG_CENTER_SETTING_CAR_INFO_IN);
//        }
	    //modity by ljq 2015/01/20 暂时注释掉直接进去设置需求
	    
        SharedPreferences sp = ContactsApp.getInstance().getSharedPreferences(
                PTMessageCenterSettings.SHARED_NAME, Context.MODE_MULTI_PROCESS);
        sp.edit().putBoolean(CAN_TRAFFIC_OFFENCE_SHOW, enable).commit();
        
	}

	@Override
	public boolean getEnable() {
		SharedPreferences sp = ContactsApp.getInstance()
				.getSharedPreferences(PTMessageCenterSettings.SHARED_NAME,
						Context.MODE_MULTI_PROCESS);
		return sp.getBoolean(CAN_TRAFFIC_OFFENCE_SHOW, false);
	}

	@Override
	public View getConfigView(final Activity context) {
		View view = View.inflate(context,
				R.layout.putao_traffic_offence_remind_setting_layout, null);
		final ArrayList<Vehicle> list = getVehicleList();
		TextView tv = (TextView)view.findViewById(R.id.offence_setting_hint);
        if (list == null || list.size() == 0) {
            tv.setText(context.getResources()
                    .getString(R.string.traffic_offence_setting_hint_nocar));
        } else {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < list.size(); i++) {
                Vehicle v = list.get(i);
                if (i < list.size() - 1) {
                    sb.append(v.getCar_province() + v.getCar_no()).append(",");
                } else {
                    sb.append(v.getCar_province() + v.getCar_no());
                }
            }
            tv.setText(context.getResources().getString(R.string.traffic_offence_setting_hint)
                    + "(" + sb.toString() + ")");
        }
        Button bt = (Button)view.findViewById(R.id.vehicle_info_setting);
        bt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 待完善
                Intent intent = null;
                YellowParams mYellowParams = new YellowParams();
                mYellowParams.setTitle(context.getResources().getString(
                        R.string.vehicle_info_setting));
                //modity by ljq start 2015/01/06 暂时注释掉直接进去设置需求
//                if (list != null && list.size() > 0) {
                    intent = new Intent(context, VehicleInfoShowActivity.class);
                    intent.putExtra("title", "车辆信息");
                    intent.putExtra(YellowUtil.TargetIntentParams, mYellowParams);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
                    context.startActivity(intent);
//                } 
                //modity by ljq end 2015/01/06 暂时注释掉直接进去设置需求
                //modity by ljq start 2015/01/06 暂时注释掉直接进去设置需求
//                else {
//                    intent = new Intent(context, VehicleInfoSettingActivity.class);
//                    intent.putExtra("title", "设置车辆信息");
//                    intent.putExtra(YellowUtil.TargetIntentParams, mYellowParams);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
//                    context.startActivity(intent);
//                }
                //modity by ljq end 2015/01/06 暂时注释掉直接进去设置需求
                MobclickAgentUtil.onEvent(ContactsApp.getInstance().getApplicationContext(),
                        UMengEventIds.DISCOVER_YELLOWPAGE_MY_MSG_CENTER_SETTING_CAR_INFO_IN);
            }
        });
		return view;
	}

	/** 获取已保存的车辆信息 */
	private ArrayList<Vehicle> getVehicleList() {
		ArrayList<Vehicle> list = null;
		SharedPreferences sp = ContactsApp.getInstance()
				.getSharedPreferences(PTMessageCenterSettings.SHARED_NAME,
						Context.MODE_MULTI_PROCESS);
		String result = sp.getString(SAVED_CARS, null);
		if (result == null) {
			return null;
		} else {
			list = Config.mGson.fromJson(result,
					new TypeToken<ArrayList<Vehicle>>() {
					}.getType());
		}
		return list;
	}

	public Map<String, String> getMaps(JSONObject jsonStr) throws JSONException {
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

	@Override
	public void click(PTOrderBean bean, Activity context) {
		// TODO 违章无订单;
		
	}
}
