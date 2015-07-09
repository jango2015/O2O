package so.contacts.hub.train;

import java.util.ArrayList;
import java.util.List;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.account.PutaoAccount;
import so.contacts.hub.core.Config;
import so.contacts.hub.core.ConstantsParameter;
import so.contacts.hub.db.YellowPageDB;
import so.contacts.hub.http.bean.RelateUserResponse;
import so.contacts.hub.msgcenter.MsgCenterConfig;
import so.contacts.hub.msgcenter.report.MsgReport;
import so.contacts.hub.msgcenter.report.MsgReportParameter;
import so.contacts.hub.msgcenter.report.MsgReportUtils;
import so.contacts.hub.remind.RemindConfig;
import so.contacts.hub.remind.utils.RemindUtils;
import so.contacts.hub.train.bean.OffenAddress;
import so.contacts.hub.train.bean.OffenTraveler;
import so.contacts.hub.train.bean.TrainJsonWrapper;
import so.contacts.hub.train.bean.UserBaseInfo;
import so.contacts.hub.ui.yellowpage.bean.HabitDataItem;
import so.contacts.hub.util.CalendarUtil;
import so.contacts.hub.util.ContactsHubUtils;
import so.contacts.hub.util.ConvUtil;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.MyCenterConstant;
import so.contacts.hub.util.UserInfoUtil;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;

import com.google.gson.Gson;
import com.yulong.android.contacts.discover.R;

public class PutaoTrainH5JsBridge {
	private static final String TAG = PutaoTrainH5JsBridge.class.getSimpleName();
	
	private Activity mActivity = null;
	
	public PutaoTrainH5JsBridge (Activity activity) {
		this.mActivity = activity;
	}
	
	private boolean isTokenValid(String open_token) {
		if(TextUtils.isEmpty(open_token))
			return false;
		
		if(!open_token.equals(PutaoAccount.getInstance().getOpenToken())) {
			return false;
		}
		
	    return true;
	}
	
	@JavascriptInterface
	public String getUserBaseData(String open_token, int encrypt) {
	    if(!isTokenValid(open_token))
	        return strerr();
	    
	    UserBaseInfo userInfo = new UserBaseInfo();
	    userInfo.setName("");
	    String nickName =  PutaoAccount.getInstance().getDisplayName(PutaoAccount.getInstance().getRelateUserResponse(RelateUserResponse.TYPE_FACTORY));
	    if(!TextUtils.isEmpty(nickName)){
	    	 userInfo.setNickname(nickName);
	    }else{
	    	 userInfo.setNickname("");
	    }
	    userInfo.setSex("");
	    userInfo.setHeadImg("");
	    String mobile = ContactsHubUtils.getPhoneNumber(mActivity); //PutaoAccount.getInstance().getBindMobile();
	    if(!TextUtils.isEmpty(mobile)){
	    	userInfo.setMobile(mobile);
	    }else{
	    	userInfo.setMobile("");
	    }
	    userInfo.setEmail("");
	    userInfo.setCtime("");
	    userInfo.setUtime("");
	    
	    ArrayList<UserBaseInfo> list = new ArrayList<UserBaseInfo>();
	    list.add(userInfo);
	    
	    TrainJsonWrapper<UserBaseInfo> wrapper = new TrainJsonWrapper<UserBaseInfo>();
        wrapper.setCount(list.size());
        wrapper.setRetcode("0");
	    wrapper.setData(list);
	    
	    String jsonstr = toJson(wrapper);
	    LogUtil.i(TAG, "getUserBaseData jsonstr="+jsonstr);
	    
	    return jsonstr;
	}
	
    @JavascriptInterface
	@SuppressWarnings("unchecked")
    public String getOffenTraveler(String open_token, int encrypt) {
    	LogUtil.d(TAG, "getOffenTraveler open_token="+open_token+" encrypt="+encrypt);

        if(!isTokenValid(open_token)){
        	return strerr();
        }
        List<HabitDataItem> habitItemList = UserInfoUtil.getInstace().getHabitDataByContentType(MyCenterConstant.TRAIN_OFFEN_TRAVELER_INFO, 
        		MyCenterConstant.HIBAT_CONTENT_TYPE_OFFEN_TRAVELER_INFO, false);
        if( habitItemList == null || habitItemList.size() == 0 ){
        	return strempty();
        }
        ArrayList<OffenTraveler> list = new ArrayList<OffenTraveler>();
        for(int i = 0; i < habitItemList.size(); i++){
        	String content_data = habitItemList.get(i).getContent_data();
        	if( TextUtils.isEmpty(content_data) ){
        		continue;
        	}
			
        	LogUtil.d(TAG, "content_data="+content_data);
        	
        	OffenTraveler traveler = new Gson().fromJson(content_data, OffenTraveler.class);
			if(traveler != null && !TextUtils.isEmpty(traveler.getLname())){
				list.add(traveler);
			}
        }
        
        TrainJsonWrapper<OffenTraveler> wrapper = new TrainJsonWrapper<OffenTraveler>();
        wrapper.setCount(list.size());
        wrapper.setRetcode("0");
        wrapper.setData(list);
        
        String jsonstr = toJson(wrapper);
        LogUtil.i(TAG, "getOffenTraveler jsonstr="+jsonstr);
        
        return jsonstr;
    }

    @JavascriptInterface
	@SuppressWarnings("unchecked")
    public String getOffenAddress(String open_token, int encrypt) {
        if( !isTokenValid(open_token) ){
        	return strerr();
        }
        
        List<HabitDataItem> habitItemList = UserInfoUtil.getInstace().getHabitDataByContentType(MyCenterConstant.TRAIN_OFFEN_MAIL_ADDRESS_INFO, 
        		MyCenterConstant.HIBAT_CONTENT_TYPE_OFFEN_MAIL_ADDRESS_INFO, false);
        if( habitItemList == null || habitItemList.size() == 0 ){
        	return strempty();
        }
        ArrayList<OffenAddress> list = new ArrayList<OffenAddress>();
        for(int i = 0; i < habitItemList.size(); i++){
        	String content_data = habitItemList.get(i).getContent_data();
        	LogUtil.i(TAG, "content_data="+content_data);
        	if( TextUtils.isEmpty(content_data) ){
        		continue;
        	}
			ArrayList<OffenAddress> addrList = (ArrayList<OffenAddress>) ConvUtil.convertBase64StringToObj(content_data);
			if(addrList == null|| addrList.size() ==0){
	        	return strempty();
	        }
			
			for(OffenAddress t : addrList) {
				LogUtil.i(TAG, "t="+t.toString());
				if(t != null && !TextUtils.isEmpty(t.getName())){
					list.add(t);
				}
			}
        }

        TrainJsonWrapper<OffenAddress> wrapper = new TrainJsonWrapper<OffenAddress>();
        wrapper.setCount(list.size());
        wrapper.setRetcode("0");
        wrapper.setData(list);
        
        String jsonstr = toJson(wrapper);
        LogUtil.i(TAG, "getOffenAddress jsonstr="+jsonstr);
        
        return jsonstr;
    }

    
    @JavascriptInterface
    public int addOftenTraveller(String open_token, String lname, String sex, 
            String ltype, String mobile, String birthday, String cno, String ctype) {
    	LogUtil.d(TAG, "addOftenTraveller open_token="+open_token+" lname="+lname+" sex="+sex+" ltype="+ltype+
    			" mobile="+mobile+" birthday="+birthday+" cno="+cno+" ctype="+ctype);
        if( !isTokenValid(open_token) ){
        	return 1;
        }
        
        //ArrayList<OffenTraveler> mTraveler = new ArrayList<OffenTraveler>();
        
        OffenTraveler traveler = new OffenTraveler();
        traveler.setLname(lname);
        if("undefined".equalsIgnoreCase(sex)) {
        	sex = "1";
        }
        traveler.setSex(sex);
        traveler.setLtype(ltype);
        traveler.setMobil(mobile);
        traveler.setBirthday(birthday);
        traveler.setCno(cno);
        traveler.setCtype(ctype);
        if("0".equals(ctype)) {
        	traveler.setCname(mActivity.getResources().getString(R.string.putao_train_ctype_0));   // （0身份证,1护照，2台胞证 3港澳通行证）
        } else if("1".equals(ctype)) {
        	traveler.setCname(mActivity.getResources().getString(R.string.putao_train_ctype_1));
        } else if("2".equals(ctype)) {
        	traveler.setCname(mActivity.getResources().getString(R.string.putao_train_ctype_2));
        } else if("3".equals(ctype)) {
        	traveler.setCname(mActivity.getResources().getString(R.string.putao_train_ctype_3));
        }
        traveler.setCtime(CalendarUtil.getNowDateStr()); //"2014-10-21 00:00:00"
        
        String data = new Gson().toJson(traveler);
        LogUtil.d(TAG, "addOftenTraveller data="+data);
        
        // 数据立即上传
        HabitDataItem item = new HabitDataItem();
        item.setSource_type(MyCenterConstant.TRAIN_OFFEN_TRAVELER_INFO);
        item.setContent_type(MyCenterConstant.HIBAT_CONTENT_TYPE_OFFEN_TRAVELER_INFO);
        item.setContent_data(data);
        UserInfoUtil.getInstace().saveHabitDataNow(item);
        
        return 0;
    }
    
    
    public static int addOftenAddress(String name, String mobile, 
            String pro, String city, String reg, String address, String zcode) {
    	
    	ArrayList<OffenAddress> mOffenAddressList = new ArrayList<OffenAddress>();
    	
    	OffenAddress offenAddress = new OffenAddress();
    	offenAddress.setName(name);
    	offenAddress.setMobil(mobile);
    	offenAddress.setPro(pro);
    	offenAddress.setCity(city);
    	offenAddress.setReg(reg);
    	offenAddress.setStraddr(address);
    	offenAddress.setZcode(zcode);
    	mOffenAddressList.add(offenAddress);
    	
    	// 数据立即上传
        HabitDataItem item = new HabitDataItem();
        item.setSource_type(MyCenterConstant.TRAIN_OFFEN_MAIL_ADDRESS_INFO);
        item.setContent_type(MyCenterConstant.HIBAT_CONTENT_TYPE_OFFEN_MAIL_ADDRESS_INFO);
        item.setContent_data(ConvUtil.convertObjToBase64String(mOffenAddressList));
        UserInfoUtil.getInstace().saveHabitDataNow(item);
    	
    	return 0;
    }
    
	@JavascriptInterface
	public void callOffenPostAddress(String open_token, int serviceType) {
		LogUtil.d(TAG, "callOffenPostAddress open_token="+open_token+" serviceType="+serviceType);
		String serviceName = YellowPagePostAddressActivity.class.getName();
		startLocService(serviceName);
	}
	
    @JavascriptInterface
    public int  reportOrderStauts(String open_token, final String order_id, 
            final String status, final String account, final String description) {
    	LogUtil.i(TAG, "open_token="+open_token+",order_id="+order_id+",status="+status+",account="+account+",description="+description);
        if(!isTokenValid(open_token))
            return 1;
        
        /**
         * 取消订单不要上报,同程传的order_id和下单传的order_id不一致,导致后台查询不到该订单,引起客户端重复上报
         * modify by cj 2015/01/21 start 
         */
        if(status != null && status.indexOf(mActivity.getResources().getString(R.string.train_cancel_str)) >= 0) {
            return 1;
        }
        // modify by cj 2015/01/21 end

        // 异步上报 modify by cj 2015/01/22 
        MsgReport report = new MsgReport();
        report.setType(MsgCenterConfig.Product.train.getProductType());
        report.setReportContent(order_id);
        
        MsgReportUtils.reportAsync(mActivity, report);
        // modify by cj 2015/01/22 end
        
       	//sendTrainOrderBroadCast(order_id);
        
        return 0;
    }
    
    private void sendTrainOrderBroadCast(String orderId) {
		// TODO Auto-generated method stub
    	  Intent intent = new Intent(MsgReportParameter.ACTION_REPORT);
          intent.putExtra(MsgReportParameter.TYPE, MsgReportParameter.TRAIN);
          intent.putExtra(MsgReportParameter.REPORT_CONTENT,orderId);
          ContactsApp.getInstance().sendBroadcast(intent);
	}

	private void sendRemindServiceBroadcast(int type, int remindCode, boolean isMyService) {
        Intent intent = new Intent(ConstantsParameter.ACTION_REMOTE_UPDATE_REMIND);
        intent.putExtra("Type", type);
        intent.putExtra("RemindCode",remindCode);
        intent.putExtra("IsMyService",isMyService);
        ContactsApp.getInstance().sendBroadcast(intent);
    }

	private String strerr() {
        StringBuffer sb = new StringBuffer();
        sb.append("{\"Retcode\":\"1\"}");
        return sb.toString();
    }
	
	private String strempty() {
        StringBuffer sb = new StringBuffer();
        sb.append("{\"Retcode\":\"0\",\"Count\":\"0\"}");
        return sb.toString();
    }
	
    
    private String toJson(TrainJsonWrapper wrapper) {
        Gson gson = new Gson();
        String jsonstr = gson.toJson(wrapper);
        return jsonstr;
    }
	
	/**
	 * 启动本地服务
	 * @param name
	 */
	private void startLocService(String name) {
		LogUtil.i(TAG, "start local service");
		try {
			Intent intent = new Intent();
			intent.setClassName(mActivity, name);
//			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			this.mActivity.startActivityForResult(intent, 11);
		} catch (Exception e) {
			LogUtil.e(TAG, "start local service exception: " + e);
		}
	}
}
