package so.contacts.hub.push;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.active.bean.ActiveEggBean;
import so.contacts.hub.core.Config;
import so.contacts.hub.core.ConstantsParameter;
import so.contacts.hub.db.ActiveDB;
import so.contacts.hub.db.YellowPageDB;
import so.contacts.hub.http.bean.ActiveRequestData;
import so.contacts.hub.http.bean.QueryFavoVoucherRequest;
import so.contacts.hub.msgcenter.PTMessageCenter;
import so.contacts.hub.msgcenter.PTOrderCenter;
import so.contacts.hub.msgcenter.bean.PTMessageBean;
import so.contacts.hub.push.bean.PushAdBean;
import so.contacts.hub.push.bean.PushRemindBean;
import so.contacts.hub.remind.BubbleRemindManager;
import so.contacts.hub.remind.RemindManager;
import so.contacts.hub.remind.impl.PushRemindImpl;
import so.contacts.hub.remind.utils.RemindUtils;
import so.contacts.hub.trafficoffence.VehicleUtils;
import so.contacts.hub.ui.yellowpage.bean.CategoryBean;
import so.contacts.hub.ui.yellowpage.bean.YellowParams;
import so.contacts.hub.util.ContactsAppUtils;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.UserInfoUtil;
import so.contacts.hub.util.YellowPagePlugUtil;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

public class PushParseFactory {
    private static final String TAG = "PushParseFactory";

    /**
     * 解析消息
     * @param msgType 【消息类型】1：消息-打点数据, 2:气泡数据, 3:Live Title, 4-广告数据，5-运营删除
     * @param msgData
     */
    public synchronized static void parseMsg(Context context, String contentData, String extras){
        LogUtil.d(TAG, "parseMsg data: \n" + contentData);
//        LogUtil.d(TAG, "parseMsg extras: \n" + extras);
        
        if ( TextUtils.isEmpty(contentData) ) {
            return;
        }
        
        int old_version = RemindUtils.getRemindVersion();
        int version = 0;
        int push_type = -1;
        String msg_delete_flag = null;//add by putao_lhq
        JSONArray jsonArray = null;
        try {
            JSONObject obj = new JSONObject(contentData);
            if(!obj.isNull("version")) {
                version = obj.getInt("version");
            }
            // add by cj start
            // push_type=2是酷派push推送格式的字段,现在在极光推送里增加对酷派push推送格式的支持
            if (!obj.isNull("push_type")) {
            	push_type = obj.getInt("push_type");
            }
            // 如果是push_type,那么data不是数组类型,所以getJSONArray报错
            if(!obj.has("push_type")) {
	            if (!obj.isNull("data")){
	                jsonArray = obj.getJSONArray("data");
	            }
            }
            //add by putao_lhq start
            if (!obj.isNull("msg_delete_flag")) {
            	msg_delete_flag = obj.getString("msg_delete_flag");
            }
            
        } catch (JSONException e) {
            LogUtil.e(TAG, e.getMessage());
            e.printStackTrace();
            jsonArray = null;
        }
        // 增加push消息弹出通知,解决极光所支持的通知内容太少的问题
        if(2 == push_type) {
        	CoolpadPushUtil.doPutaoPush(contentData);
        	return;
        }
        
        if( jsonArray == null ){
        	return;
        }
        
        List<PushRemindBean> remindList = null;
        List<PushRemindBean> bubbleList = null;
        List<PushAdBean> adList = null;
        List<PushRemindBean> opRemoveList = null;
        List<ActiveEggBean> activeList = null;
        List<CategoryBean> liveTitleLists = null;//保存livetitle的数据 add by lisheng
        List<PTMessageBean> msgNotifyList = null; 
        
        boolean opConfigReceived = false;  // 是否接收到需要更新版本的配置数据
        boolean otherConfigReceived = false;   // 是否接收到不需要更新版本的配置数据
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json_obj = jsonArray.getJSONObject(i);
                int msgType = json_obj.getInt("msg_type");
                if (msgType == 1) {
                    if (remindList == null) // 1. 打点
                        remindList = new ArrayList<PushRemindBean>();

                    PushRemindBean remind = PushParseFactory.doParseRemind(json_obj);
                    if (remind != null) {
                        remindList.add(remind);
                        opConfigReceived = true;
                    }

                } else if (msgType == 2) { // 2. 气泡
                    if (bubbleList == null)
                        bubbleList = new ArrayList<PushRemindBean>();

                    PushRemindBean bubble = PushParseFactory.doParseBubble(json_obj);
                    if (bubble != null) {
                        bubbleList.add(bubble);
                        opConfigReceived = true;
                    }

                } else if (msgType == 3) { // 3:Live Title
                	//add by lisheng start 2014-11-07 
                	if(liveTitleLists==null){
                		liveTitleLists= new ArrayList<CategoryBean>();
                	}
                	CategoryBean bean = PushParseFactory.doParseLiveTitle(json_obj);
                	if(bean!=null){
                		liveTitleLists.add(bean);
                		opConfigReceived = true;
    					LogUtil.i(TAG,bean.getExpand_param());	
                	}
                	//add by lisheng end

                } else if (msgType == 4) { // 4.广告
                    if (adList == null)
                        adList = new ArrayList<PushAdBean>();

                    PushAdBean adBean = PushParseFactory.doParseAdBean(json_obj);
                    if (adBean != null) {
                        adList.add(adBean);
                        opConfigReceived = true;
                    }

                } else if (msgType == 5) { // 5.运营删除
                    if (opRemoveList == null)
                        opRemoveList = new ArrayList<PushRemindBean>();

                    PushRemindBean bean = PushParseFactory.doParseOPRemove(json_obj);
                    if (bean != null) {
                        opRemoveList.add(bean);
                        opConfigReceived = true;
                    }

                } else if (msgType == 6) { // 6. 活动彩蛋

                    if (activeList == null)
                        activeList = new ArrayList<ActiveEggBean>();

                    List<ActiveEggBean> eggList = PushParseFactory.doParseActivies(json_obj);
                    if (eggList != null && eggList.size() > 0) {
                        activeList.addAll(eggList);
                        otherConfigReceived = true;
                    }

                } else if (msgType == 7) { // 7-应用开关控制
                    if (!json_obj.isNull("tab_remind") && json_obj.has("tab_remind")) {
                    	// 首页tab打点 开关
                        int tab_remind = json_obj.getInt("tab_remind");
                        LogUtil.i(TAG, "recv push tab_remind = " + tab_remind);
                        RemindUtils.setTabRemind(tab_remind == 0 ? false : true);
                        otherConfigReceived = true;
                    }
                } else if (msgType == 8) { // 8-通知客户端更新数据

                    String actionCode = "";
                    if (json_obj.has("action_code")) {
                        actionCode = json_obj.getString("action_code");
                    }
                    if(ActiveRequestData.ACTION_CODE.equals(actionCode)) {
                        ContactsApp.getInstance().sendBroadcast(new Intent(ConstantsParameter.ACTION_REMOTE_DO_HEARTBEAT));
                        otherConfigReceived = true;
                    } else if (QueryFavoVoucherRequest.ACTION_CODE.equals(actionCode)) {
                        // 启动新进程，避免在系统广播里访问网络
                        // added by cj 2014/10/29 start
                        Config.execute(new Runnable(){
                            @Override
                            public void run() {
                                // 需要更新用户优惠券信息
                                UserInfoUtil.getInstace().updateUserCouponData();
                            }
                        });
                        // added by cj 2014/10/29 end
                    } else if(VehicleUtils.VEHICLE_ACTION_CODE.equals(actionCode)) {
                        // 后台通过push action_code来启动违章车辆查询后上报
                        // add by cj 2015/01/23 start
                        String expand_param = "";
                        if (json_obj.has("msg_expand_param")) {
                            expand_param = json_obj.getString("msg_expand_param");
                        }
                        final String params = expand_param;
                        Config.execute(new Runnable(){
                            @Override
                            public void run() {
                                VehicleUtils.doSearchVehicle(params);
                            }
                        });
                        // add by cj 2015/01/23 end
                    }
                    
                } else if(msgType == 9) {  // 9-消息中心通知
                	if(msgNotifyList == null) {
                		msgNotifyList = new ArrayList<PTMessageBean>();
                	}
                	
                	PTMessageBean ptMsgBean = doParseNotifyMessage(json_obj);
                    LogUtil.d(TAG, "doParseNotifyMessage add MsgBean="+ptMsgBean);
                	if(ptMsgBean != null) {
                		msgNotifyList.add(ptMsgBean);
                		otherConfigReceived = true;
                	}
                }

            }
        } catch (JSONException e) {
            LogUtil.e(TAG, e.getMessage());
            e.printStackTrace();
            return;
        }
        
        // 1. 首先检查配置数据是否有更新
        if(otherConfigReceived) {
            if(activeList != null) {
                activeEggReceived(activeList);
            }
            
            if(msgNotifyList != null) {
            	msgNotifyReceived(msgNotifyList);
            }
        }

        // 2. 再检查运营配置有版本数据更新
        if(opConfigReceived) {
        	//delete by putao_lhq 2014-11-28 start
        	/*
            if (old_version >= version || jsonArray == null) {
                LogUtil.i(TAG, "parseMsg can not update version from " + old_version + " to "
                        + version);
                return;
            }
            */
        	if (!TextUtils.isEmpty(msg_delete_flag)) {
        		String[] flag = msg_delete_flag.split(",");
            	if (flag==null) {
            		return;
            	}
            	for (int i = 0 ; i < flag.length; i++) {
            		int index = Integer.parseInt(flag[i]);
            		doDelete(index);
            	}
            	ContactsApp.getInstance().sendBroadcast(new Intent(ConstantsParameter.ACTION_REMIND_UPDATE));
            	Intent intent = new Intent(ConstantsParameter.ACTION_UPDATE_AD);
                ContactsApp.getInstance().sendBroadcast(intent);
        	}
        	//delete by putao_lhq 2014-11-28 end
            if(remindList != null) {
                remindReceived(remindList);
            }

            if(bubbleList != null) {
                bubbleReceived(bubbleList);
            }

            if(adList != null) {
                adReceived(adList);
            }

            if(opRemoveList != null) {
                opRemoveReceived(opRemoveList);
            }
            
            if(liveTitleLists != null){
            	liveTitleReceived(liveTitleLists);
            }// add by lisheng 2014-11-07 14:53:01
            
            // 保存版本号
            LogUtil.i(TAG, "parseMsg remind update to version=" + version + " oldVersion="
                    + RemindUtils.getRemindVersion());
            RemindUtils.setRemindVersion(version);
        }

    }
    
    
  //add by lisheng start 2014-11-07
    private static void liveTitleReceived(List<CategoryBean> liveTitleLists) {
    	  if(liveTitleLists == null || liveTitleLists.size() == 0){
              return;
          }
    	  ContactsAppUtils.getInstance().getDatabaseHelper().getYellowPageDBHelper().insertLiveTitleList(liveTitleLists);
    	  YellowPagePlugUtil.getInstance().setRefreshPlugViewState(YellowPagePlugUtil.STATE_REFRESH_ALL_VIEW);//add 2014-10-31 11:58:18
//          ContactsApp.getInstance().sendBroadcast(new Intent(ConstantsParameter.ACTION_LIVETITLE_UPDATE));
          ContactsApp.getInstance().sendBroadcast(new Intent(ConstantsParameter.ACTION_LIVETITLE_UPDATE_PLUG));
		
	}
    	
	//解析push livetitle 数据
    private static CategoryBean doParseLiveTitle(JSONObject obj) {
    	 if(obj == null){
    		 return null; 
    	 }
    	 CategoryBean cb = null;
    	 try {
			int code = obj.getInt("code");
			 String expand_param = obj.getString("expand_param");
			 cb = new CategoryBean();
			 cb.setCategory_id(code);
			 cb.setExpand_param(expand_param);
		} catch (JSONException e) {
			e.printStackTrace();
		}
         return cb;
	}
    //add by lisheng end.
    
    // 解析push打点信息
    private static PushRemindBean doParseRemind(JSONObject obj) throws JSONException {
        if(obj == null)
            return null;
        PushRemindBean remindBean = null;
        
        int code = obj.getInt("code");
        int type = obj.getInt("type");
        String expand_param = obj.getString("expand_param");
        JSONObject expandObj = new JSONObject(expand_param);

        remindBean = new PushRemindBean();
        remindBean.setCode(code);
        remindBean.setType(type);

        if (expandObj.has("sub_code"))
            remindBean.setSubCode(expandObj.getString("sub_code"));
        if (expandObj.has("style"))
            remindBean.setStyle(expandObj.getInt("style"));
        if (expandObj.has("time"))
            remindBean.setTime(expandObj.getLong("time"));
        if (expandObj.has("text"))
            remindBean.setText(expandObj.getString("text"));
        if (expandObj.has("img_url"))
            remindBean.setImg_url(expandObj.getString("img_url"));
        
        return remindBean;
    }
    
    // 解析push气泡
    private static PushRemindBean doParseBubble(JSONObject obj) throws JSONException{
        if(obj == null)
            return null;
        PushRemindBean remindBean = null;

        int code = obj.getInt("code");
        int type = obj.getInt("type");
        String expand_param = obj.getString("expand_param");
        JSONObject expandObj = new JSONObject(expand_param);

        remindBean = new PushRemindBean();
        remindBean.setCode(code);
        remindBean.setType(type);

        if (expandObj.has("sub_code"))
            remindBean.setSubCode(expandObj.getString("sub_code"));
        if (expandObj.has("style"))
            remindBean.setStyle(expandObj.getInt("style"));
        if (expandObj.has("time"))
            remindBean.setTime(expandObj.getLong("time"));
        if (expandObj.has("text"))
            remindBean.setText(expandObj.getString("text"));
        if (expandObj.has("img_url"))
            remindBean.setImg_url(expandObj.getString("img_url"));

        return remindBean;
    }
    
    // 解析push广告数据
    private static PushAdBean doParseAdBean(JSONObject obj) throws JSONException {
        if(obj == null)
            return null;
        PushAdBean adBean = null;
        int code = obj.getInt("code");
        adBean = new PushAdBean();
        String expand_param = obj.getString("expand_param");
        JSONObject expandObj = new JSONObject(expand_param);
        if(obj.toString().contains("yellowparams")){
            String yellowparams = obj.getString("yellowparams");
            if(yellowparams !=null && yellowparams.length()>0){
                try {
                    List<YellowParams> params = new Gson().fromJson(yellowparams, new TypeToken<List<YellowParams>>(){}.getType());
                    if(params != null){
                        adBean.setAd_params_str(yellowparams);
                    }
                } catch (Exception e) {
                }
            }
        }
        
        adBean.setAd_code(code);
        if (expandObj.has("page_index")) {
            adBean.setAd_page_index(expandObj.getInt("page_index"));
        }
        if (expandObj.has("img_url")) {
            adBean.setAd_img_url(expandObj.getString("img_url"));
        }
        if (expandObj.has("click_type")) {
            adBean.setAd_click_type(expandObj.getString("click_type"));
        }
        if (expandObj.has("click_activity")) {
            adBean.setAd_click_activity(expandObj.getString("click_activity"));
        }
        if (expandObj.has("click_link")) {
            adBean.setAd_click_link(expandObj.getString("click_link"));
        }
        if (expandObj.has("text")) {
            adBean.setAd_text(expandObj.getString("text"));
        }
        if (expandObj.has("start_time")) {
            adBean.setAd_start_time(expandObj.getLong("start_time"));
        }
        if (expandObj.has("end_time")) {
            adBean.setAd_end_time(expandObj.getLong("end_time"));
        }

        return adBean;
    }

    //解析push运营删除
    private static PushRemindBean doParseOPRemove(JSONObject obj) throws JSONException {
        if (obj == null)
            return null;
        
        PushRemindBean bean = null;
        int code = obj.getInt("code");
        int type = obj.getInt("type");

        bean = new PushRemindBean();
        bean.setCode(code);
        bean.setType(type);

        return bean;
    }

    //解析push活动
    private static List<ActiveEggBean> doParseActivies(JSONObject obj) throws JSONException {
        List<ActiveEggBean> activeList = new ArrayList<ActiveEggBean>();

        long active_id = obj.getLong("activity_id");
        String req_url = obj.getString("target_url");
        int type = obj.getInt("type");
        long start_time = obj.getLong("start_time");
        long end_time = obj.getLong("end_time");
        int status = 0;
        if (obj.has("status")) {
        	status = obj.getInt("status");
        } else {
        	LogUtil.d(TAG, "not find status");
        	status = 2;
        }
        
        //活动状态为2表示活动已结束，模拟一个彩蛋去删除数据库
        if (status == 2) {
        	ActiveEggBean bean = new ActiveEggBean();
        	bean.active_id = active_id;
        	bean.status = status;
        	activeList.add(bean);
        	return activeList;
        }
        
        if (!obj.isNull("match_list")) {
            JSONArray eggObjList = obj.getJSONArray("match_list");
            for (int j = 0; j < eggObjList.length(); j++) {
                JSONObject egg_obj = eggObjList.getJSONObject(j);

                ActiveEggBean bean = new ActiveEggBean();
                bean.active_id = active_id;
                bean.request_url = req_url;

                bean.start_time = start_time;
                bean.end_time = end_time;

                int egg_id = egg_obj.getInt("match_id");
                bean.egg_id = egg_id;

                bean.status = status;
                
                if (egg_obj.has("op_match_name") && !egg_obj.getString("op_match_name").equals("")) {
                    bean.trigger = egg_obj.getString("op_match_name");
                    bean.trigger_type = type;
                } else if (egg_obj.has("op_match_url")
                        && !egg_obj.getString("op_match_url").equals("")) {
                    bean.trigger = egg_obj.getString("op_match_url");
                    bean.trigger_type = type;
                }

                if (egg_obj.has("expand_param")) {
                    bean.expand_param = egg_obj.getString("expand_param");
                }

                // add by putao_lhq 2014年10月24日 for active start
                if (egg_obj.has("valid_time")) {
                	bean.valid_time = egg_obj.getLong("valid_time");
                }
                // add by putao_lhq 2014年10月24日 for active end
                activeList.add(bean);
            }
        }
        return activeList;
    }
    
    // 解析push提醒消息
    private static PTMessageBean doParseNotifyMessage(JSONObject obj) throws JSONException {
        if(obj == null)
            return null;
        
        PTMessageBean messageBean = new PTMessageBean();
        
        long msg_id = obj.getLong("msg_id");
        int msg_product_type = obj.getInt("msg_product_type");
        String msg_subject = obj.getString("msg_subject");
        String msg_digest = obj.getString("msg_digest");
        long msg_time = obj.getLong("msg_time");
        int msg_is_notify = 0;
        if(obj.has("is_notify")) {
            msg_is_notify = obj.getInt("is_notify");
        }
        String expand_param = obj.getString("msg_expand_param");
        
        messageBean.setMsgId(msg_id);
        messageBean.setProductType(msg_product_type);
        messageBean.setSubject(msg_subject);
        messageBean.setDigest(msg_digest);
        messageBean.setTime(msg_time);
        messageBean.setIs_notify(msg_is_notify);
        messageBean.setExpand_param(expand_param);
        
        return messageBean;
    }
    

    private static void remindReceived(final List<PushRemindBean> remindList) {
        if(remindList == null || remindList.size() == 0){
            return;
        }

        PushRemindImpl serviceImpl = new PushRemindImpl();
        for (PushRemindBean remind : remindList) {
            serviceImpl.remindReceived(remind);
        }

        ContactsApp.getInstance().sendBroadcast(new Intent(ConstantsParameter.ACTION_REMIND_UPDATE));
        ContactsApp.getInstance().sendBroadcast(new Intent(ConstantsParameter.ACTION_REMIND_UPDATE_PLUG));
    }

    private static void bubbleReceived(final List<PushRemindBean> bubbleList) {
        if(bubbleList == null || bubbleList.size() == 0){
            return;
        }
        
        PushRemindImpl serviceImpl = new PushRemindImpl();
        for (PushRemindBean remind : bubbleList) {
            serviceImpl.bubbleReceived(remind);
        }
        ContactsApp.getInstance().sendBroadcast(new Intent(ConstantsParameter.ACTION_REMIND_UPDATE_PLUG));
    }

    private static void adReceived(final List<PushAdBean> adList) {
        if( adList == null || adList.size() == 0 ){
            return;
        }

        ContactsAppUtils.getInstance().getDatabaseHelper().getYellowPageDBHelper().insertAdList(adList);
        Intent intent = new Intent(ConstantsParameter.ACTION_UPDATE_AD);
        ContactsApp.getInstance().sendBroadcast(intent);
        
//      Config.execute(new Runnable() {
//            @Override
//            public void run() {
//                int downcnt = 0;
//                StringBuffer adUrls = new StringBuffer();
//                for(PushAdBean bean : adList) {
//                  if( bean.getAd_code() != YellowPagePlugUtil.HOME_PAGE_AD_UPDATE_ID ){
//                      continue;
//                  }
//                    LogUtil.d(TAG, "adReceived "+bean.toString());
//                    
//                    if(!TextUtils.isEmpty(bean.getAd_img_url())) {
//                        String urls[] = bean.getAd_img_url().split(",");
//                        
//                        for(String url : urls) {
//                            if(TextUtils.isEmpty(url))
//                                continue;
//                            File imgFile = ImageLoader.downloadBitmap(ContactsApp.getInstance(), url);
//                            if(imgFile != null && imgFile.isFile() && imgFile.exists()) {
//                                downcnt++;
//                                adUrls.append(imgFile.getAbsolutePath()).append(",");
//                                LogUtil.i(TAG, "adReceived down url="+url+" saveTo local=["+imgFile.getAbsolutePath()+"]");
//                            }
//                        }
//                    }
//                }
//                
//                if(downcnt > 0) {
//                    Intent intent = new Intent("update.ad.layout.action");
//                    sendBroadcast(intent);
//                }
//            }
//          
//      });
    }

    private static void opRemoveReceived(final List<PushRemindBean> operateList) {
        if( operateList == null || operateList.size() == 0 ){
            return;
        }

        for (PushRemindBean bean : operateList) {
            RemindUtils.removeBubbuleRemind(bean.getCode()); // 运营删除code就是remind_code
        }

        BubbleRemindManager.getInstance().save();
    }
    
    private static void activeEggReceived(final List<ActiveEggBean> activeList) {
        if( activeList == null || activeList.size() == 0 ){
            return;
        }
        
        LogUtil.i(TAG, "Insert actives ===>");

        SQLiteDatabase db = ContactsAppUtils.getInstance().getDatabaseHelper().getActiveDB().getSQLiteDatabase();
        db.beginTransaction();

        long curActiveId = 0;
        for (ActiveEggBean bean : activeList) {
        	if (curActiveId != bean.active_id) {
        		curActiveId = bean.active_id;
        		ContactsAppUtils.getInstance().getDatabaseHelper().getActiveDB().deleteActive(bean.active_id);
        	}
        }
        
        for (ActiveEggBean bean : activeList) {
        	if (bean.status == 2) {
        		ContactsAppUtils.getInstance().getDatabaseHelper().getActiveDB()
        		.deleteActive(bean.active_id);
        	} else {
        		ContactsAppUtils.getInstance().getDatabaseHelper().getActiveDB().insertActiveEgg(bean);
        	}
            LogUtil.d(TAG, "insert egg: " + bean.toString());
        }

        db.setTransactionSuccessful();
        db.endTransaction();
    }
    
    private static void msgNotifyReceived(final List<PTMessageBean> msgNotifyList) {
    	if(msgNotifyList == null || msgNotifyList.size() == 0) {
    		return;
    	}
    	LogUtil.d(TAG, "msgNotifyReceived size="+msgNotifyList.size());
        Config.execute(new Runnable(){
            @Override
            public void run() {
                PTOrderCenter.getInstance().syncRefreshOrderData();
            	for(PTMessageBean msgBean : msgNotifyList) {
            		PTMessageCenter.getInstance().receive(msgBean);
            	}
            }
        });
    }

    /**
     * 删除所有打点
     * @author putao_lhq
     */
    private static  void doDeleteRemind() {
    	RemindManager.getInstance().cleanAndSave();
    }
    
    /**
     * 删除所有气泡
     * @author putao_lhq
     */
    private static void doDeleteBubbleRemind() {
    	BubbleRemindManager.getInstance().cleanAndSave();
    }
    
    /**
     * 删除所有广告
     * @author putao_lhq
     */
    private static void doDeleteAd() {
        /*
         * 广告数据的删除刷新不能直接删除数据，应该将所有数据的imgUrl置为空，界面刷新时会再执行删除数据操作
         * modified by hyl 2014-11-29 start
         * old code:
         *	 ContactsAppUtils.getInstance().getDatabaseHelper().getYellowPageDBHelper().clearTable(YellowPageDB.AdTable.TABLE_NAME);
         */
        ContactsAppUtils.getInstance().getDatabaseHelper().getYellowPageDBHelper().clearAllAdData();
        //modified by hyl 2014-11-29 end
    }
    
    /**
     * 删除活动
     */
    private static void doDeleteActive() {
    	ContactsAppUtils.getInstance().getDatabaseHelper().getActiveDB().clearTable(ActiveDB.ActiveEggTable.TABLE_NAME);
    }
    /**
     * 执行删除操作
     * @param msg_delete_flag
     */
    private static void doDelete(int msg_delete_flag) {
    	if (msg_delete_flag == 1 ) {
    		doDeleteRemind();
    	} else if (msg_delete_flag == 2) {
    		doDeleteBubbleRemind();
    	} else if (msg_delete_flag == 3) {
    		
    	} else if (msg_delete_flag == 4) {
    		doDeleteAd();
    	} else if (msg_delete_flag == 5) {
    		
    	} else if (msg_delete_flag == 6) {
    		doDeleteActive();
    	}
    }
}
