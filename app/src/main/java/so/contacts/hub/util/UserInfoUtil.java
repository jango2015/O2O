package so.contacts.hub.util;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import so.contacts.hub.account.IAccChangeListener;
import so.contacts.hub.core.Config;
import so.contacts.hub.db.YellowPageDB;
import so.contacts.hub.http.bean.DeleteHabitDataRequest;
import so.contacts.hub.http.bean.DeleteHabitDataResponse;
import so.contacts.hub.http.bean.GetHabitRequest;
import so.contacts.hub.http.bean.ReportHabitRequest;
import so.contacts.hub.http.bean.ReportHabitResponse;
import so.contacts.hub.ui.yellowpage.bean.HabitDataItem;
import so.contacts.hub.yellow.data.Voucher;
import so.contacts.hub.yellow.data.Voucher.VoucherScope;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.mdroid.core.http.IgnitedHttpResponse;

/**
 * 用户信息资料 工具类
 * 用户信息：用户相关的订单、活动信息、优惠券信息
 */
public class UserInfoUtil implements IAccChangeListener{
    private final static String TAG = "UserInfoUtil";
    
    private static UserInfoUtil mUserInfoUtil;
    
    private YellowPageDB mYellowPageDB = null;
    
    /** 用户代金券信息 */
    private List<Voucher> mUserVoucherList = null;
    
    public static UserInfoUtil getInstace(){
        if(mUserInfoUtil == null){
        	synchronized (UserInfoUtil.class) {
				if( mUserInfoUtil == null ){
					mUserInfoUtil = new UserInfoUtil();
				}
			}
        }
        return mUserInfoUtil;
    }
    
    private UserInfoUtil(){
        mYellowPageDB = ContactsAppUtils.getInstance().getDatabaseHelper().getYellowPageDBHelper(); 
    }
    
    /**
     * 获取用户优惠券信息
     */
    public List<Voucher> getUserVoucherList(){
    	return mUserVoucherList;
    }
    
    /**
     * 清空用户优惠券信息
     */
    public void cleanUserVoucherList(){
    	if( mUserVoucherList != null ){
    		mUserVoucherList.clear();
    	}
    }
    
    /**
     * 删除用户优惠券信息
     */
    public void delUserVoucherList(long voucherId){
    	if( mUserVoucherList != null ){
    		int size = mUserVoucherList.size();
    		Voucher voucher = null;
    		for(int i = 0; i < size; i++){
    			Voucher voucherTemp = mUserVoucherList.get(i);
    			if( voucherId == voucherTemp.id ){
    				voucher = voucherTemp;
    				break;
    			}
    		}
    		if( voucher != null ){
    			mUserVoucherList.remove(voucher);
    		}
    	}
    }
    
    /**
     * 删除用户习惯数据
     */
    public static void DeleteHabitData(long id){
        
        DeleteHabitDataResponse responseData = null;
        
        final DeleteHabitDataRequest requestData = new DeleteHabitDataRequest(id);
        IgnitedHttpResponse httpResponse;
        try {
            httpResponse = Config.getApiHttp().post(Config.SERVER, requestData.getData()).send();
            String content = httpResponse.getResponseBodyAsString();
            responseData = requestData.getObject(content);
            if (responseData != null) {
                if (responseData.isSuccess()) {
                    LogUtil.i(TAG, "DeleteHabitData ok");
                } else {
                    LogUtil.i(TAG, "DeleteHabitData fail, errcode="+responseData.ret_code);
                    responseData = null;
                }
            }
        } catch (ConnectException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
    /**
     * 插入或者更新信息
     */
    public void saveHabitData(Context context, HabitDataItem item){
        item.setIsupload(Integer.valueOf(HabitDataItem.LOCAL));
        mYellowPageDB.updateOrInsertHabitData(item);
        if(NetUtil.isWifi(context)){
            uploadHabitDataAsyn();
        }
    }
    
    /**
     * 插入或者更新信息 !不上传
     */
    public void saveHabitDataNotUpload(Context context, HabitDataItem item){
        item.setIsupload(Integer.valueOf(HabitDataItem.NOT_UPlOAD));
        mYellowPageDB.updateOrInsertHabitData(item);
    }
    
    /**
     * 删除用户习惯信息
     */
    public void delHabitData(Context context, HabitDataItem item){
        mYellowPageDB.delHabitData(item);
        HabitDataItem dbItem = ContactsAppUtils.getInstance().getDatabaseHelper().getYellowPageDBHelper().queryUniqueHabitData(item);
        if (dbItem != null && dbItem.getServiceId() != 0) {
            if (NetUtil.isWifi(context)) {
                // del network
                DeleteHabitData(dbItem.getServiceId());
            }
        }
    }
    
    /**
     * 删除用户习惯信息
     */
    public void delHabitDataAsyn(Context context, HabitDataItem item){
        mYellowPageDB.delHabitData(item);
        DelHabitTask task = new DelHabitTask();
        task.execute(context,item);
    }
    
    /**
     * 立即更新信息
     */
    public void saveHabitDataNow(HabitDataItem item){
        item.setIsupload(Integer.valueOf(HabitDataItem.LOCAL));
    	mYellowPageDB.updateOrInsertHabitData(item);
    	uploadHabitDataAsyn();
    }
    
    /**
     * 根据sourceType拿到 习惯信息
     * @param sourceType 来源类型，一般是类名
     */
    public List<HabitDataItem> getHabitDataBySoureType(String sourceType){
        return mYellowPageDB.queryHabitDataBySourceType(sourceType);
    }
    
    /**
     * 获取本地Habit数据库版本号 
     */
    public int getHabitDataVersion(){
    	return mYellowPageDB.queryDataVersion(YellowPageDB.YELLOW_DATA_VERSION_HABIT);
    }
    
    public void cleanHabitData(){
        //暂用线程解决 
        new Thread(){
            @Override
            public void run() {
                // TODO Auto-generated method stub
                mYellowPageDB.cleanHabitData();
                super.run();
            }
        }.start();
    }
    
    /**
     * 根据sourceType content_type 拿到 习惯信息
     *  sourceType 来源类型，一般是类名 
     *  content_type 如电话 MOBILE 邮件 EMAIL
     */
    public List<HabitDataItem> getHabitDataByContentType(String sourceType,String contentType,boolean isAES){
        return mYellowPageDB.queryContentTypeHabitData(sourceType,contentType,isAES);
    }
    
//    /** 
//     * ！！！content_type 是模糊搜索
//     * 根据sourceType content_type 拿到 习惯信息
//     *  sourceType 来源类型，一般是类名 
//     *  content_type 如电话 MOBILE 邮件 EMAIL
//     */
//    public List<HabitDataItem> getHabitDataByFuzzyContentType(String sourceType,String contentType,boolean isAES){
//        return mYellowPageDB.queryFuzzyContentTypeHabitData(sourceType,contentType,isAES);
//    }
    /**
     * 获取用户优惠券信息
     * 
     * @param escope 优惠券应用场景的枚举对象
     */
    public List<Voucher> getAvailableVouchers(VoucherScope escope) {
        ArrayList<Voucher> availableVouchers = new ArrayList<Voucher>();
        if (null == mUserVoucherList || mUserVoucherList.isEmpty()) {
            return availableVouchers;
        }
        for (int i = 0, len = mUserVoucherList.size(); i < len; i++) {
            Voucher voucher = mUserVoucherList.get(i);
            if (voucher != null && escope.value().equalsIgnoreCase(voucher.scope)
                    && voucher.resource_consume == 0 && voucher.status == 0) {
                availableVouchers.add(voucher);
            }
        }
        return availableVouchers;
    }
    /**
     * 更新用户可用优惠券信息
     */
    public void updateUserCouponData(){
    	//LogUtil.d(TAG, "updateUserCouponData token: " + Config.getPTUser().pt_token + " ,UID: " + Config.getPTUser().pt_uid);
        saveUserVoucherList(YellowPageDataUtils.queryCouponLists());
    }
    
    
    /**
     * 设置用户优惠券信息
     */
    public void saveUserVoucherList(List<Voucher> userVoucherList) {
        LogUtil.i(TAG, "saveUserVoucherList start.");
        if (userVoucherList != null) {
            LogUtil.i(TAG, "saveUserVoucherList vouchers: " + userVoucherList.size());
            if (mUserVoucherList == null) {
                mUserVoucherList = new ArrayList<Voucher>();
            } else {
                mUserVoucherList.clear();
            }
            for (Voucher voucher : userVoucherList) {
                if (voucher == null || TextUtils.isEmpty(voucher.scope)) {
                    continue;
                }
                LogUtil.d(TAG, voucher.toString());
                mUserVoucherList.add(voucher);
            }
        } else {
            LogUtil.i(TAG, "saveUserVoucherList vouchers is null.");
        }
    }
    
    private class UploadHabitTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            uploadHabitData();
            return null;
        }
    }
    
    private class UpdateHabitTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
        	updateHabitData();
            return null;
        }
    }
    
    private class DelHabitTask extends AsyncTask<Object, Void, Void> {
        @Override
        protected Void doInBackground(Object... params) {
            delHabitData((Context)params[0],(HabitDataItem)params[1]);
            return null;
        }
    }
    
    public void uploadHabitData(){
        List<HabitDataItem> dataItem = mYellowPageDB.queryAllLocalHabitData();
        if(dataItem != null){
            Log.d(TAG, "uploadHabitData dataItem size = " + dataItem.size());
        }
        if(dataItem.size() == 0){
            return ;
        }
        ReportHabitRequest requestData = new ReportHabitRequest(dataItem);
        IgnitedHttpResponse httpResponse;
        try {
            requestData.getData();
            httpResponse = Config.getApiHttp().post(Config.SERVER, requestData.getData()).send();
            String content;
            content = httpResponse.getResponseBodyAsString();
            ReportHabitResponse responseData = requestData.getObject(content);
            if (responseData != null) {
                if (responseData.isSuccess()) {
                    int version = responseData.habit_version;
                    LogUtil.i(TAG, "uploadHabitData success version: " + version);
                    if(version == 0){
                        return ;
                    }
//                    if(mYellowPageDB.queryHabitDataVersion()>version){
//                        return null;
//                    }
                    mYellowPageDB.insertDataVersion(YellowPageDB.YELLOW_DATA_VERSION_HABIT, version);
                    List<HabitDataItem> list = responseData.habit_data_list;
                    //putao_lhq modify start
                    /*if(list == null && list.size() == 0){
                        return ;
                    }*/
                    if(list == null || list.size() == 0){
                        return ;
                    }
                    //putao_lhq modify end
                    for (int i = 0; i < list.size(); i++) {
                        mYellowPageDB.updateOrInsertHabitData(list.get(i));
                    }
                    //成功后将所有数据标为已上传
                    mYellowPageDB.markAllHabitDataUpload();
                    LogUtil.i(TAG, "upload success");
                } else {
                    LogUtil.i(TAG, "upload onFail");
                }
            }
        } catch (ConnectException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ;
    }
    
    public void updateHabitData(){
        GetHabitRequest requestData = new GetHabitRequest();
        IgnitedHttpResponse httpResponse;
        try {
            httpResponse = Config.getApiHttp().post(Config.SERVER, requestData.getData()).send();
            String content;
            content = httpResponse.getResponseBodyAsString();
            Log.d(TAG, "updateHabitData content" + content);
            ReportHabitResponse responseData = requestData.getObject(content);
            if (responseData != null) {
                if (responseData.isSuccess()) {
                    int version = responseData.habit_version;
                    LogUtil.i(TAG, "updateHabitData success version: " + version);
                    if(version == 0){
                        return ;
                    }
                    mYellowPageDB.insertDataVersion(YellowPageDB.YELLOW_DATA_VERSION_HABIT, version);
                    List<HabitDataItem> list = responseData.habit_data_list;
                    //putao_lhq modify start
                    /*if(list == null && list.size() == 0){
                        return ;
                    }*/
                    if(list == null || list.size() == 0){
                        return ;
                    }
                    Log.d("ljq", "updateHabitData list.size  "+ list.size());
                    //putao_lhq modify end
                    for (int i = 0; i < list.size(); i++) {
                        //将下载的数据标为已经上传
                        HabitDataItem item = list.get(i);
                        item.setIsupload(Integer.valueOf(HabitDataItem.UPlOAD));
                        mYellowPageDB.updateOrInsertHabitData(item);
                    }
                } else {
                    LogUtil.i(TAG, "get onFail");
                }
            }
        } catch (ConnectException e) {
            e.printStackTrace();
            LogUtil.e(TAG, e.getMessage());
        }catch (IOException e) {
            e.printStackTrace();
            LogUtil.e(TAG, e.getMessage());
        }
        return ;
    }
    
    /**
     * 上传 未上传过的用户信息 并将最新的版本号和信息录入数据库
     * @param pt_token 用户令牌
     */
    public void uploadHabitDataAsyn(){
        UploadHabitTask task = new UploadHabitTask();
        task.execute();
    }
    
    /**
     * 取得 最新的版本号和信息并录入数据库
     * @param pt_token 用户令牌
     */
    public void updateHabitDataAsyn(){
    	UpdateHabitTask task = new UpdateHabitTask();
        task.execute();
    }

	@Override
	public void onLogin() {
		LogUtil.d(TAG, "do account login");
		updateHabitData();
		updateUserCouponData();
	}

	@Override
	public void onLogout() {
		LogUtil.d(TAG, "do account logout");
		cleanHabitData();
		cleanUserVoucherList();
	}

	@Override
	public void onChange() {
		// TODO 账号改变时应做的处理
		LogUtil.d(TAG, "do account changed");
		updateHabitData();
		updateUserCouponData();
	}
    
    
    
    
}
