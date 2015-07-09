package so.contacts.hub.util;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import so.contacts.hub.core.Config;
import so.contacts.hub.db.YellowPageDB;
import so.contacts.hub.http.bean.QueryFavoVoucherRequest;
import so.contacts.hub.http.bean.QueryFavoVoucherResponse;
import so.contacts.hub.http.bean.UpdateYellowPageDataRequest;
import so.contacts.hub.http.bean.UpdateYellowPageDataResponse;
import so.contacts.hub.ui.yellowpage.bean.CategoryBean;
import so.contacts.hub.ui.yellowpage.bean.ItemBean;
import so.contacts.hub.yellow.data.Voucher;
import android.text.TextUtils;
import android.util.Log;

import com.mdroid.core.http.IgnitedHttpResponse;

/**
 * 黄页数据管理工具类
 * 
 * @author hyl 2014-8-7
 */
public class YellowPageDataUtils {
    public static final String TAG = "PutaoAccount";
    
	// add by putao_lhq 2014年10月9日 for 酷云账号
	/*public static void login(Context context, final Config.CallBack callBack) { 
		if (!hasUserLoginOutOperate(context) && !CoolCloudManager.getInstance().isBindKuyun()) {
			CoolCloudManager.getInstance().loginKuyun(callBack);
		} else {
			login2(context, callBack);
		}
		
	}*/
	
    /**
     * 免密登录（同步操作） add by hyl 2014-9-22
     * 
     * @param context
     * @param callBack
     */
    /*public static void login2(Context context, final Config.CallBack callBack) {
        String phoneNumber = ContactsHubUtils.getPhoneNumber(context);
        LogUtil.i(TAG, "silentLogin phoneNumber:" + phoneNumber);
        
        
         * modified by hyl 2014-9-28 start
         * 增加判断逻辑：判断用户是否手动退出登录过
         * old code:if (!TextUtils.isEmpty(phoneNumber)){
         * 
         
        if (!TextUtils.isEmpty(phoneNumber) && !hasUserLoginOutOperate(context)) {// 提取手机默认号码成功
        //modified by hyl 2014-9-28 end 
            
            loginYellow(context, phoneNumber, 1, 1, callBack, null);// 手机号码登录
        } else {
            String devicedId = SystemUtil.getDeviceId(context);// 获取设备号
            LogUtil.i(TAG, "silentLogin devicedId:" + devicedId);
            if (!TextUtils.isEmpty(devicedId)) {
                loginByDevicedId(context, callBack);// 设备号登录
            } else {
                if(callBack != null) {
                	String logStr = context.getString(R.string.putao_cannot_find_device_phone);
                    LogUtil.i(TAG, logStr);        
                    callBack.onFail(logStr); //找不到任何设备号和手机号
                }
            }
        }
    }*/

    // add by putao_lhq 2014年10月9日 for 酷云账号
    /*public static void silentLogin(Context context, final Config.CallBack callBack) {
    	if (!hasUserLoginOutOperate(context) && !CoolCloudManager.getInstance().isBindKuyun()) {
    		CoolCloudManager.getInstance().silentLoginKuyun(callBack, -1);
    	} else {
    		silentLogin2(context, callBack);
    	}
    }*/
    /**
     * 免密登录（异步操作） add by hyl 2014-9-19
     * 
     * @param context
     * @param callBack
     */
   /* public static void silentLogin2(Context context, final Config.CallBack callBack) {
        String phoneNumber = ContactsHubUtils.getPhoneNumber(context);
        LogUtil.i(TAG, "silentLogin phoneNumber:" + phoneNumber);
        
         * modified by hyl 2014-9-28 start
         * 增加判断逻辑：判断用户是否手动退出登录过
         * old code:if (!TextUtils.isEmpty(phoneNumber)){
         
        if (!TextUtils.isEmpty(phoneNumber) && !hasUserLoginOutOperate(context)) {// 提取手机默认号码成功
        //modified by hyl 2014-9-28 end 
            loginYellowAsyn(context, null, phoneNumber, 1, 1, callBack, null);// 手机号码登录
        } else {
            String devicedId = SystemUtil.getDeviceId(context);// 获取设备号
            if (!TextUtils.isEmpty(devicedId)) {
                loginByDevicedIdAsyn(context, callBack);// 设备号登录
            }
            //putao_lhq add for 静默登录业务阻塞 start
            else {
            	if (callBack != null) {
            		callBack.onFail("device id is null");
            	}
            }
            //putao_lhq add for 静默登录业务阻塞 end
        }
    }*/

    /**
     * 黄页账户设备号登录 add by hyl 2014-9-19
     * 
     * @param context 上下文
     * @param callBack 回调函数
     */
    /*public static void loginByDevicedIdAsyn(final Context context, final Config.CallBack callBack) {
        LogUtil.i(TAG, "loginByDevicedIdAsyn");
        final DeviceUserRegisterRequest registerRequest = new DeviceUserRegisterRequest();

        Config.asynPost(null, null, registerRequest.getData(), new CallBack() {
            @Override
            public void onSuccess(String json) {
                DeviceUserRegisterResponse registerResponse = registerRequest.getObject(json);
                if (registerResponse.isSuccess()) {
                    LogUtil.i(TAG, "loginByDevicedIdAsyn success");
                    // 保存黄页账户对象json
                    savePtUser(context, json);

                    if (callBack != null) {
                        callBack.onSuccess(json);
                    }
                }
            }

            @Override
            public void onFinish(Object obj) {
                if (callBack != null) {
                    callBack.onFinish(obj);
                }
            }

            @Override
            public void onFail(String msg) {
                if (callBack != null) {
                    callBack.onFail(msg);
                }
            }
        });
    }*/

    /**
     * 黄页账户设备号登录 (同步操作) add by hyl 2014-9-19
     * 
     * @param context 上下文
     * @param callBack 回调函数
     */
    /*public static void loginByDevicedId(final Context context, final Config.CallBack callBack) {
        LogUtil.i(TAG, "loginByDevicedId");
        final DeviceUserRegisterRequest registerRequest = new DeviceUserRegisterRequest();

        IgnitedHttpResponse httpResponse;
        try {
            httpResponse = Config.getApiHttp().post(Config.SERVER, registerRequest.getData())
                    .send();
            String content = httpResponse.getResponseBodyAsString();
            DeviceUserRegisterResponse registerResponse = registerRequest.getObject(content);

            if (registerResponse != null) {
                if (registerResponse.isSuccess()) {
                    LogUtil.i(TAG, "loginByDevicedId success");
                    // 保存黄页账户对象json
                    savePtUser(context, content);
                    if (callBack != null) {
                        callBack.onSuccess(content);
                    }
                }
            }
        } catch (ConnectException e) {
            e.printStackTrace();
            if (callBack != null) {
                callBack.onFail(context.getString(R.string.putao_network_exception)); //网络连接异常
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (callBack != null) {
                callBack.onFail(context.getString(R.string.putao_server_exception)); //服务器异常
            }
        }
    }*/

    /**
     * 黄页账户免密登录 （异步操作，若根据传入accName 找不到已有账户信息 服务会自动生成一个 新账户 返回） add by hyl
     * 2014-9-19
     * 
     * @param context 上下文
     * @param progressMsg 加载提示语
     * @param accName 账户名称
     * @param accSource 账户来源
     * @param accType 账户类型
     * @param callBack 回调函数
     */
    /*public static void loginYellowAsyn(final Context context, String progressMsg, String accName,
            int accSource, int accType, final Config.CallBack callBack, AccountInfo info) {
        LogUtil.i(TAG, "loginYellow start");
        final UserRegisterRequest registerRequest = new UserRegisterRequest(accName, accSource,
                accType, info);

        Activity activity = null;
        if (!TextUtils.isEmpty(progressMsg)) {
            activity = (Activity)context;
        }

        Config.asynPost(activity, progressMsg, registerRequest.getData(), new CallBack() {
            @Override
            public void onSuccess(String json) {
                UserRegisterResponse registerResponse = registerRequest.getObject(json);
                LogUtil.d(CoolCloudManager.TAG, "response code: " + registerResponse.ret_code); // add by putao_lhq
                if (registerResponse.isSuccess()) {
                    if (registerResponse.registerStatus == 0) {// 注册或绑定成功
                        LogUtil.i(TAG, "loginYellow success");
                        // 保存黄页账户对象json
                        savePtUser(context, json);
                        
                        if (callBack != null) {
                            callBack.onSuccess(json);
                        }
                    } else {
                        // 提示用户不可绑定
                        onFail(context.getString(R.string.putao_account_exist_cannot_bind)); // 账户已存在，不能绑定
                    }
                //add by putao_lhq
                } else {
                	LogUtil.d(CoolCloudManager.TAG, "error response code");
                	if (callBack != null) {
                		onFail(context.getString(R.string.putao_server_exception)); //服务器异常
                	}
                }
            }

            @Override
            public void onFinish(Object obj) {
                if (callBack != null) {
                    callBack.onFinish(obj);
                }
            }

            @Override
            public void onFail(String msg) {
                if (callBack != null) {
                    callBack.onFail(msg);
                }
            }
        });
    }*/

    /**
     * 黄页账户免密登录 （同步操作，若根据传入accName 找不到已有账户信息 服务会自动生成一个 新账户 返回） add by hyl
     * 2014-9-22
     * 
     * @param context 上下文
     * @param accName 账户名称
     * @param accSource 账户来源
     * @param accType 账户类型
     * @param callBack 回调函数
     */
    /*public static void loginYellow(final Context context, String accName, int accSource,
            int accType, final Config.CallBack callBack, AccountInfo info) {
        LogUtil.i(TAG, "loginYellow start");
        final UserRegisterRequest registerRequest = new UserRegisterRequest(accName, accSource,
                accType, info);

        IgnitedHttpResponse httpResponse;
        try {
            httpResponse = Config.getApiHttp().post(Config.SERVER, registerRequest.getData())
                    .send();
            String content = httpResponse.getResponseBodyAsString();
            UserRegisterResponse registerResponse = registerRequest.getObject(content);

            if (registerResponse != null) {
                if (registerResponse.isSuccess()) {
                    if (registerResponse.registerStatus == 0) {// 注册或绑定成功
                        LogUtil.i(TAG, "loginYellow success");
                        // 保存黄页账户对象json
                        savePtUser(context, content);
                        if (callBack != null) {
                            callBack.onSuccess(content);
                        }
                    } else {
                        // 提示用户不可绑定
                        if (callBack != null) {
                        	callBack.onFail(context.getString(R.string.putao_account_exist_cannot_bind)); //账户已存在，不能绑定
                        }
                    }
                }
            }
        } catch (ConnectException e) {
            e.printStackTrace();
            if (callBack != null) {
                callBack.onFail(context.getString(R.string.putao_network_exception)); //网络连接异常
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (callBack != null) {
                callBack.onFail(context.getString(R.string.putao_server_exception)); //服务器异常
            }
        }
    }*/

    /**
     * 解绑黄页鉴权账户 add by hyl 2014-9-19
     * 
     * @param context 上下文
     * @param progressMsg 加载提示语
     * @param accName 账户名称
     * @param accSource 账户来源
     * @param accType 账户类型
     * @param callBack 回调函数
     */
    /*public static void unbindAccount(final Context context, String progressMsg, final String accName,
            final int accSource, final int accType, final Config.CallBack callBack) {
        final UserUnbindingRequest registerRequest = new UserUnbindingRequest(accName, accSource,
                accType);

        Activity activity = null;
        if (!TextUtils.isEmpty(progressMsg)) {
            activity = (Activity)context;
        }

        Config.asynPost(activity, progressMsg, registerRequest.getData(), new CallBack() {
            @Override
            public void onSuccess(String json) {
                UserUnbindingResponse registerResponse = registerRequest.getObject(json);
                if (registerResponse.isSuccess()) {
                    
                     * 解除用户绑定的鉴权用户信息
                     * modified by hyl 2014-9-28 start
                     * old code:cleanPtUser(context);
                     
                    unBindPtUserRelateUser(context,accName,accSource,accType);
                    //modified by hyl 2014-9-28 end

                    if (callBack != null) {
                        callBack.onSuccess(json);
                    }
                }
            }

            @Override
            public void onFinish(Object obj) {
                if (callBack != null) {
                    callBack.onFinish(obj);
                }
            }

            @Override
            public void onFail(String msg) {
                if (callBack != null) {
                    callBack.onFail(msg);
                }
            }
        });
    }*/

    /**
     * 解除鉴权用户绑定
     * add by hyl 2014-9-28
     * @param accName  账户名称
     * @param accSource 账户来源
     * @param accType  账户类型
     */
    /*protected static void unBindPtUserRelateUser(Context context,String accName, int accSource, int accType) {
        PTUser ptUser = Config.getPTUser();
        if(ptUser != null && ptUser.relateUsers != null){
            RelateUserResponse unbindItem = null;
            for (RelateUserResponse item : ptUser.relateUsers) {
                if(item.accName.equals(accName) 
                        && item.accSource == accSource 
                        && item.accType == accType){
                    unbindItem = item;
                    break;
                }
            }
            if(unbindItem != null){
                ptUser.relateUsers.remove(unbindItem);
                String json = new Gson().toJson(ptUser);
                savePtUser(context, json);
            }
        }
    }*/

    /**
     * 退出登录
     * add by hyl 2014-9-23
     * @param context
     * @param progressMsg
     * @param token
     * @param callBack
     */
    /*public static void logOut(final Context context, String progressMsg, String token,final Config.CallBack callBack) {
        final UserLogoutRequest registerRequest = new UserLogoutRequest(token);
        Activity activity = null;
        if (!TextUtils.isEmpty(progressMsg)) {
            activity = (Activity)context;
        }
        Config.asynPost(activity, progressMsg, registerRequest.getData(), new CallBack() {
            @Override
            public void onSuccess(String json) {
                UserLogoutResponse registerResponse = registerRequest.getObject(json);
                if (registerResponse.isSuccess()) {
                    // 退出登录成功
                    loginOutPtUser(context);

                    if (callBack != null) {
                        callBack.onSuccess(json);
                    }
                }
            }

            @Override
            public void onFinish(Object obj) {
                if (callBack != null) {
                    callBack.onFinish(obj);
                }
            }

            @Override
            public void onFail(String msg) {
                if (callBack != null) {
                    callBack.onFail(msg);
                }
            }
        });
    }*/
    
    /**
     * 删除黄页用户
     * add by hyl 2014-9-23
     * @param context
     * @param progressMsg
     * @param token
     * @param callBack
     */
    /*public static void ptUserDelete(final Context context, String progressMsg, String token,final Config.CallBack callBack) {
        final UserDeleteRequest registerRequest = new UserDeleteRequest(token);
        Activity activity = null;
        if (!TextUtils.isEmpty(progressMsg)) {
            activity = (Activity)context;
        }
        Config.asynPost(activity, progressMsg, registerRequest.getData(), new CallBack() {
            @Override
            public void onSuccess(String json) {
                UserDeleteResponse registerResponse = registerRequest.getObject(json);
                if (registerResponse.isSuccess()) {
                    // 删除用户成功
                    cleanPtUser(context);

                    if (callBack != null) {
                        callBack.onSuccess(json);
                    }
                }
            }

            @Override
            public void onFinish(Object obj) {
                if (callBack != null) {
                    callBack.onFinish(obj);
                }
            }

            @Override
            public void onFail(String msg) {
                if (callBack != null) {
                    callBack.onFail(msg);
                }
            }
        });
    }*/

    /**
     * 保存黄页账户信息 json add by hyl 2014-9-19
     * 
     * @param context
     * @param json
     */
    /*public static void savePtUser(Context context, String json) {
        Config.clearPTUser();
        SharedPreferences preferences = context.getSharedPreferences(Config.YELLOW_USER,
                Context.MODE_MULTI_PROCESS);//old code:Context.MODE_PRIVATE modify by putao_lhq
        preferences.edit().putString("pt_user", json).commit();
        
        *//**
         * add by zjh 2014-10-17 start
         * 注册信鸽push
         *//*
        XGPushUtil.registerXGPush(ContactsApp.getInstance());
        *//** add by zjh 2014-10-17 end *//*
        
        // 检查当前是否主线程，如果是主线程则启动新线程处理请求
        // add by cj 2014-10-25 start
        if(Looper.myLooper() == Looper.getMainLooper()) {
            Config.execute(new Runnable() {
                @Override
                public void run() {
                    UserInfoUtil.getInstace().updateHabitData();//add by hyl 2014-9-23 获取服务器配置应用数据
                    UserInfoUtil.getInstace().updateUserCouponData(); //add by zjh 2014-10-24 获取用户优惠券信息
                }
            });
        } else {
            UserInfoUtil.getInstace().updateHabitData();//add by hyl 2014-9-23 获取服务器配置应用数据
            UserInfoUtil.getInstace().updateUserCouponData(); //add by zjh 2014-10-24 获取用户优惠券信息
        }
        // add by cj 2014-10-25 end
        
        // add by putao_lhq 2014年11月14日 for 账号登录通知心跳 start
        ContactsApp.getInstance().sendBroadcast(new Intent(ConstantsParameter.ACTION_REMOTE_DO_HEARTBEAT));
        // add by putao_lhq 2014年11月14日 for 账号登录通知心跳 end
    }*/

    /**
     * 退出黄页账户登录 add by hyl 2014-9-20
     * 
     * @param context
     */
    /*public static void loginOutPtUser(Context context) {
        cleanPtUser(context);
        // 记录用户主动退出行为
        markUserLoginOutOperate(context);
        
        *//**
         * 用户推出登录时，清空该用户优惠券信息
         * add by zjh 2014-10-25 start
         *//*
        UserInfoUtil.getInstace().cleanUserVoucherList(); 
        *//** dd by zjh 2014-10-25 end *//*
    }*/

    /**
     * 记录用户主动退出行为 add by hyl 2014-9-20
     * 
     * @param context
     */
    /*public static void markUserLoginOutOperate(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Config.YELLOW_USER,
                Context.MODE_MULTI_PROCESS);
        preferences.edit().putBoolean("is_user_operate", true).commit();
    }*/

    /**
     * 判断 用户 是否 主动退出过登录 add by hyl 2014-9-20
     * 
     * @param context
     * @return
     */
    /*public static boolean hasUserLoginOutOperate(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Config.YELLOW_USER,
                Context.MODE_MULTI_PROCESS);
        return preferences.getBoolean("is_user_operate", false);
    }*/

    /**
     * 清楚黄页账户信息 add by hyl 2014-9-20
     * 
     * @param context
     * @param json
     */
    /*public static void cleanPtUser(Context context) {
        Config.clearPTUser();
        SharedPreferences preferences = context.getSharedPreferences(Config.YELLOW_USER,
                Context.MODE_MULTI_PROCESS);
        preferences.edit().remove("pt_user").commit();
    }*/

    /**
     * 请求服务器 获取 黄页数据
     */
    public static void doUpdateYellowPageRequest() {
        YellowPageDB db = ContactsAppUtils.getInstance().getDatabaseHelper().getYellowPageDBHelper();
        UpdateYellowPageDataResponse dataResponse = null;
        UpdateYellowPageDataRequest dataRequest = new UpdateYellowPageDataRequest(
                db.queryDataVersion(YellowPageDB.YELLOW_DATA_VERSION_DEFAULT));
        IgnitedHttpResponse httpResponse;
        try {
            httpResponse = Config.getApiHttp().post(Config.SERVER, dataRequest.getData()).send();
            String content = httpResponse.getResponseBodyAsString();
            Log.e("PlugService", "content: " + content);
            dataResponse = dataRequest.getObject(content);
            if (dataResponse != null && dataResponse.isSuccess()) {
                List<CategoryBean> categoryList = dataResponse.categoryList;
                List<ItemBean> itemList = dataResponse.itemList;
                
                Log.e("PlugService", "categoryList: " + categoryList.size());
                Log.e("PlugService", "itemList: " + itemList.size());

                LogUtil.d("PlugService", "categoryList: " + categoryList.size());
                LogUtil.d("PlugService", "itemList: " + itemList.size());
                saveModeifyData(categoryList, itemList, dataResponse.data_version);
                // 发送广播 更新界面
                if (categoryList.size() != 0 || itemList.size() != 0) {
                    sendUpdateCategoryBroastCast();
                }
            }
        } catch (ConnectException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 保存数据 用事务包装
     */
    private static void saveModeifyData(List<CategoryBean> categoryBeans , List<ItemBean> itemBeans , int version ){
        if( categoryBeans != null && itemBeans != null){
            YellowPageDB db = ContactsAppUtils.getInstance().getDatabaseHelper().getYellowPageDBHelper();
            
            //检查传入List<CategoryBean>  List<ItemBean> 是否因为json字符串里没有字段值导致该字段有空值或者初始化值
            //如果有检查数据库中是否有即将更新的bean将空值替换为原值 如果没有则初始化值
            //add ljq 2015/02/05 start
            
            boolean hasNullId = false;//是否包含Null ID的 Bean 有则作废此次更新
            for (int i = 0; i < categoryBeans.size(); i++) {
                CategoryBean category = categoryBeans.get(i);
                if(category.getCategory_id() != 0){
                    CategoryBean local_category = db.queryCategoryByCategoryId(category.getCategory_id());
                    if(local_category != null){
                        if (TextUtils.isEmpty(category.getExpand_param())) {
                            category.setExpand_param(local_category.getExpand_param());
                        }
                        if (TextUtils.isEmpty(category.getIcon())) {
                            category.setIcon(local_category.getIcon());
                        }
                        if (TextUtils.isEmpty(category.getIconLogo())) {
                            category.setIconLogo(local_category.getIconLogo());
                        }
                        if (TextUtils.isEmpty(category.getKey_tag())) {
                            category.setKey_tag(local_category.getKey_tag());
                        }
                        if (TextUtils.isEmpty(category.getName())) {
                            category.setName(local_category.getName());
                        }
                        if (TextUtils.isEmpty(category.getPressIcon())) {
                            category.setPressIcon(local_category.getPressIcon());
                        }
                        if (TextUtils.isEmpty(category.getShow_name())) {
                            category.setShow_name(local_category.getShow_name());
                        }
                        if (TextUtils.isEmpty(category.getTagIcon())) {
                            category.setTagIcon(local_category.getTagIcon());
                        }
                        if (TextUtils.isEmpty(category.getTarget_activity())) {
                            category.setTarget_activity(local_category.getTarget_activity());
                        }
                        if (TextUtils.isEmpty(category.getTarget_params())) {
                            category.setTarget_params(local_category.getTarget_params());
                        }
                        if (category.getLastSort() == 0) {
                            category.setLastSort(local_category.getLastSort());
                        }
                        if (category.getParent_id() == 0) {
                            category.setParent_id(local_category.getParent_id());
                        }
                        if (category.getRemind_code() == 0) {
                            category.setRemind_code(local_category.getRemind_code());
                        }
                        if (category.getSearch_sort() == 0) {
                            category.setSearch_sort(local_category.getSearch_sort());
                        }
                        if (category.getSort() == 0) {
                            category.setSort(local_category.getSort());
                        }
                        
                    }else{
                        if (TextUtils.isEmpty(category.getExpand_param())) {
                            category.setExpand_param("");
                        }
                        if (TextUtils.isEmpty(category.getIcon())) {
                            category.setIcon("");
                        }
                        if (TextUtils.isEmpty(category.getIconLogo())) {
                            category.setIconLogo("");
                        }
                        if (TextUtils.isEmpty(category.getKey_tag())) {
                            category.setKey_tag("");
                        }
                        if (TextUtils.isEmpty(category.getName())) {
                            category.setName("");
                        }
                        if (TextUtils.isEmpty(category.getPressIcon())) {
                            category.setPressIcon("");
                        }
                        if (TextUtils.isEmpty(category.getShow_name())) {
                            category.setShow_name("");
                        }
                        if (TextUtils.isEmpty(category.getTagIcon())) {
                            category.setTagIcon("");
                        }
                        if (TextUtils.isEmpty(category.getTarget_activity())) {
                            category.setTarget_activity("");
                        }
                        if (TextUtils.isEmpty(category.getTarget_params())) {
                            category.setTarget_params("");
                        }
                    }
                }else{
                    hasNullId = true;
                }
            }
            
            
            
            for (int i = 0; i < itemBeans.size(); i++) {
                ItemBean itembean = itemBeans.get(i);
                if(itembean.getItem_id() != 0){
                    ItemBean local_itembean = db.queryItemByItemId(itembean.getItem_id());
                    if(local_itembean != null){
                        if (TextUtils.isEmpty(itembean.getExpand_param())) {
                            itembean.setExpand_param(local_itembean.getExpand_param());
                        }
                        if (TextUtils.isEmpty(itembean.getIcon())) {
                            itembean.setIcon(local_itembean.getIcon());
                        }
                        if (TextUtils.isEmpty(itembean.getIconLogo())) {
                            itembean.setIconLogo(local_itembean.getIconLogo());
                        }
                        if (TextUtils.isEmpty(itembean.getKey_tag())) {
                            itembean.setKey_tag(local_itembean.getKey_tag());
                        }
                        if (TextUtils.isEmpty(itembean.getName())) {
                            itembean.setName(local_itembean.getName());
                        }
                        if (TextUtils.isEmpty(itembean.getPressIcon())) {
                            itembean.setPressIcon(local_itembean.getPressIcon());
                        }
                        if (TextUtils.isEmpty(itembean.getShow_name())) {
                            itembean.setShow_name(local_itembean.getShow_name());
                        }
                        if (TextUtils.isEmpty(itembean.getTagIcon())) {
                            itembean.setTagIcon(local_itembean.getTagIcon());
                        }
                        if (TextUtils.isEmpty(itembean.getTarget_activity())) {
                            itembean.setTarget_activity(local_itembean.getTarget_activity());
                        }
                        if (TextUtils.isEmpty(itembean.getTarget_params())) {
                            itembean.setTarget_params(local_itembean.getTarget_params());
                        }
                        if (itembean.getLastSort() == 0) {
                            itembean.setLastSort(local_itembean.getLastSort());
                        }
                        if (itembean.getParent_id() == 0) {
                            itembean.setParent_id(local_itembean.getParent_id());
                        }
                        if (itembean.getRemind_code() == 0) {
                            itembean.setRemind_code(local_itembean.getRemind_code());
                        }
                        if (itembean.getSearch_sort() == 0) {
                            itembean.setSearch_sort(local_itembean.getSearch_sort());
                        }
                        if (itembean.getSort() == 0) {
                            itembean.setSort(local_itembean.getSort());
                        }
                        
                        if (TextUtils.isEmpty(itembean.getDescription())) {
                            itembean.setDescription(local_itembean.getDescription());
                        }
                        
                        if (TextUtils.isEmpty(itembean.getContent())) {
                            itembean.setContent(local_itembean.getContent());
                        }
                        
                        
                    }else{
                        if (TextUtils.isEmpty(itembean.getExpand_param())) {
                            itembean.setExpand_param("");
                        }
                        if (TextUtils.isEmpty(itembean.getIcon())) {
                            itembean.setIcon("");
                        }
                        if (TextUtils.isEmpty(itembean.getIconLogo())) {
                            itembean.setIconLogo("");
                        }
                        if (TextUtils.isEmpty(itembean.getKey_tag())) {
                            itembean.setKey_tag("");
                        }
                        if (TextUtils.isEmpty(itembean.getName())) {
                            itembean.setName("");
                        }
                        if (TextUtils.isEmpty(itembean.getPressIcon())) {
                            itembean.setPressIcon("");
                        }
                        if (TextUtils.isEmpty(itembean.getShow_name())) {
                            itembean.setShow_name("");
                        }
                        if (TextUtils.isEmpty(itembean.getTagIcon())) {
                            itembean.setTagIcon("");
                        }
                        if (TextUtils.isEmpty(itembean.getTarget_activity())) {
                            itembean.setTarget_activity("");
                        }
                        if (TextUtils.isEmpty(itembean.getTarget_params())) {
                            itembean.setTarget_params("");
                        }
                        if (TextUtils.isEmpty(itembean.getDescription())) {
                            itembean.setDescription("");
                        }
                        
                        if (TextUtils.isEmpty(itembean.getContent())) {
                            itembean.setContent("");
                        }
                        
                    }
                }else{
                    hasNullId = true;
                }
            }
            
            
            if(hasNullId){
                return;
            }
            //add ljq 2015/02/05 end
            db.updateCategoryDataNotClean(categoryBeans, itemBeans,version);
        }
    }

    /**
     * 发送广播：黄页数据更新
     */
    public static void sendUpdateCategoryBroastCast() {
        LogUtil.d("PlugService", "sendUpdateCategoryBroastCast");
        // 有数据更新， 更新黄页数据
        YellowPagePlugUtil.getInstance().setRefreshPlugViewState(YellowPagePlugUtil.STATE_REFRESH_ALL_VIEW);
//        Intent intent = new Intent();
//        intent.setAction("com.yulong.android.contacts.yellowpage.data.update");
//        ContactsApp.getInstance().sendBroadcast(intent);
    }
    
    //putao_lhq add start
    /**
     * 判断用户是否已登录
     * @return 登录返回true，否则返回false
     */
    /*public static boolean isLogin() {
        final PTUser user = Config.getPTUser();
        if (user == null || TextUtils.isEmpty(user.getPt_token())) {           
            return false;
        }
        return true;
    }*/
    //putao_lhq add end
    
    /**
     * 查询用户优惠券
     * create by hyl 2014-10-19
     */
    public static List<Voucher> queryCouponLists(){
        final QueryFavoVoucherRequest qVoucherRequest = new QueryFavoVoucherRequest();

        List<Voucher> coupons = new ArrayList<Voucher>();
        IgnitedHttpResponse httpResponse = null;
        try {
            //http://192.168.1.63:8080/PT_SERVER/interface.s
        	long startTime = System.currentTimeMillis();
            httpResponse = Config.getApiHttp().post(Config.SERVER, qVoucherRequest.getData()).send();
            String content = httpResponse.getResponseBodyAsString();
            LogUtil.d(TAG, "queryCouponLists="+content+" time:"+(System.currentTimeMillis() - startTime));
            QueryFavoVoucherResponse registerResponse = qVoucherRequest.getObject(content);
            if(registerResponse.isSuccess()){
            	coupons = registerResponse.voucher_list;
            }
        } catch (ConnectException e) {
            e.printStackTrace();
            coupons = null;
        } catch (IOException e) {
            e.printStackTrace();
            coupons = null;
        }
        return coupons;
    }
}
