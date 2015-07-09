package so.contacts.hub.http.bean;

import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.account.PutaoAccount;
import so.contacts.hub.core.Config;
import so.contacts.hub.core.PTUser;
import so.contacts.hub.util.LogUtil;
import android.content.Context;

import com.google.gson.Gson;
import com.mdroid.core.util.Md5Util;
import com.mdroid.core.util.SystemUtil;
import com.yulong.android.contacts.discover.R;

public abstract class BaseRequestData<T extends BaseResponseData> {
//	public static final Gson mGson = new Gson();
//	public static final NextCodeHandler mNextCodeHandler = new NextCodeHandler();

//    public String pt_uid;//
    public String pt_token;//add by hyl 2014-9-20
    
	public String action_code;// [string][not null][指令码]
	public String token;// [string][null able][用户令牌,云端生成]
	public String mcode;// [string][null able][用户设备ID，云端生成]
	public UaInfo ua;// [UaInfo][null able][Ua系统]
	public long timestamp;// [long][not null][时间戳：毫秒数]
	public VersionInfo version;// [VersionInfo][not null][版本结构]
	public String secret_key;// [string][not null][交互密钥]
	public int active_status;// [int][not null][0:后台运行,1:主程序]
	public String channel_no = "1001";// :[String][not null][渠道号,无渠道号为PUTAO]
	
    public int app_id = 5; // 标识appId,当前通讯录+的appid是2; doov版本id=3 eton=4 coolpad=5
    
	public String device_code; //[String][not null][手机设备码]
	public int local_dual_version;//[int][not null][本地保存双卡信息版本，默认为0]

	public BaseRequestData(String actionCode) {
		Context context = ContactsApp.getInstance();
		PTUser ptUser = PutaoAccount.getInstance().getPtUser();
		if(ptUser != null){
		    pt_token = ptUser.pt_token;//add by hyl 2014-9-20
		}
		
		action_code = actionCode;
		// removed by cj 2015/02/03
//		token = Config.getUser().token;   
//		mcode = Config.getUser().mcode;
		ua = new UaInfo();
		timestamp = System.currentTimeMillis();
		version = new VersionInfo(SystemUtil.getAppVersion(context), 
		                            "",
		                            "");
		secret_key = Md5Util.md5(actionCode + SystemUtil.getAppVersion(context) + timestamp
				+ Config.KEY);
		active_status = Config.STATE;
		channel_no = SystemUtil.getChannelNo(context);
		device_code = SystemUtil.getDeviceId(context);
		// delete by putao_lhq 2014年10月28日 for 去掉默认设备号 start
		/*if(device_code == null)
		    device_code = "1234567890";*/
		// delete by putao_lhq 2014年10月28日 for 去掉默认设备号 end
//		local_dual_version = DualCardUtils.readDualCardMatchVersion(context);
	}

	public HttpEntity getData() {
	    String cotent = Config.mGson.toJson(this);
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

	public T getObject(String json) {
		T data;
		try {
			data = fromJson(json);
		} catch (Throwable e) {
			e.printStackTrace();
			data = getNewInstance();
			data.ret_code = "9999";
			data.error_remark = ContactsApp.getInstance().getResources().getString(R.string.putao_net_unuseable);//"网络链接不可用";// Server error.
		}

		/**
		 * add by ffh 2015-3-3 start
		 */
		if(null == data) {
		    data = getNewInstance();
		    data.ret_code = "9999";
		    data.error_remark = ContactsApp.getInstance().getResources().getString(R.string.putao_net_unuseable);//"网络链接不可用";// Server error.
		}
		// add by ffh 2015-3-3 end

        //真滴所有接口响应做心跳间隔变化处理
		if(data.active_m_s > 0){
		    Config.setHeartBeat(data.active_m_s);
		}
		
		// if (Config.resume_activitys > 0) {
		// // 只有用户在前台界面才提示用户
		// if (data.enforce == 0) {
		// // 不强制升级
		// } else if (data.enforce == 1) {
		// // 强制升级
		// try {
		// final NewVersionRequestData requestData = new
		// NewVersionRequestData();
		// IgnitedHttpResponse httpResponse = Config.getApiHttp()
		// .post(Config.SERVER, requestData.getData()).send();
		// String content = httpResponse.getResponseBodyAsString();
		// NewVersionResponseData responseData = requestData
		// .getObject(content);
		// if (responseData.isSuccess() && Config.resume_activitys > 0) {
		// String localAppVersion = SystemUtil
		// .getAppVersion(BaseApplication.getInstance());
		// String version = responseData.version;
		// if (!TextUtils.isEmpty(version)
		// && !localAppVersion
		// .equals(responseData.version)
		// && responseData.size >= 0) {
		//
		// Intent intent = new Intent();
		// intent.setAction("force_update");
		// intent.putExtra("version", responseData.version);
		// intent.putExtra("size", responseData.size + "M");
		// intent.putExtra("remark", responseData.remark);
		// intent.putExtra("down_url", responseData.down_url);
		// if (Config.resume_activitys > 0) {
		// BaseApplication.getInstance().sendBroadcast(intent);
		// }
		// }
		// }
		// } catch (ConnectException e) {
		// e.printStackTrace();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// }
		// }

		return data;
	}

	protected abstract T getNewInstance();

	/**
	 * 遍历通知集合将其插入数据库中
	 * 
	 * @param 通知集合
	 */
	// public static void setNotifyInfo(List<NotifyInfo> list) {
	// if(list == null || list.size() == 0)
	// {
	// return;
	// }
	// Calendar cal = Calendar.getInstance();
	// long time = cal.getTimeInMillis();
	// Map<String, NativeContact> pool =
	// Config.getContactsRes().getMobileSummaryPool();
	// for (NotifyInfo nfi : list) {
	// if (nfi.getType() == 2) {
	// // 合并联系人
	// Map<Long, String> mobileMap = new HashMap<Long, String>();
	// if (nfi.mobile_summarys != null) {
	// for (String summary : nfi.mobile_summarys) {
	// if (pool.containsKey(summary)) { // 索引对应联系人存在在通讯录
	// NativeContact contact = pool.get(summary);
	// if (!contact.isLinked()
	// && !mobileMap.containsKey(contact.id)) // 不存在设计关系,且一个联系人合并为一个
	// mobileMap.put(contact.id, summary);
	// }
	// }
	// }
	//
	// Config.getUser().setWeibo_tip_number(mobileMap.size());
	// Config.getUser().setWeiboTipCount(mobileMap.size());
	// if (mobileMap.size() != 0) {
	// Config.getDatabaseHelper()
	// .getNotifyInfoDB()
	// .insert(nfi.getType(),
	// MineView.getContact(nfi.getRemark(),
	// nfi.getType()), mobileMap.size(),
	// time);
	// Intent intent = new Intent("update_notify");
	// intent.putExtra("type", nfi.getType());
	// intent.putExtra("remark",
	// MineView.getContact(nfi.getRemark(), nfi.getType()));
	// Config.Instance().getApplication().sendBroadcast(intent);
	// }
	// }
	// }
	// }

	// /**
	// * 遍历Map表将值插入HomeAddress数据表中
	// *
	// * @param 需要遍历的map表
	// */
	// public static void workMapByEntrySetHomeAddress(Map<String, String> map)
	// {
	// Set<Map.Entry<String, String>> set = map.entrySet();
	// for (Iterator<Map.Entry<String, String>> it = set.iterator(); it
	// .hasNext();) {
	// Map.Entry<String, String> entry = it.next();
	// Config.getDatabaseHelper().getAttributionDB()
	// .insert(entry.getKey(), entry.getValue());
	// }
	// }

	protected  T fromJson(String json) throws Throwable{
	    T t = getNewInstance();
	    Gson gson = new Gson();
	    t = (T)gson.fromJson(json, t.getClass());
	    return t;
	}

}
