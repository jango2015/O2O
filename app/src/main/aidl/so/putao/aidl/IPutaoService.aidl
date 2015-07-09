package so.putao.aidl;

import so.putao.aidl.ICallback;
import android.os.Parcel;

interface IPutaoService {  
    boolean userIsBind();

    boolean cancelMark(String number,String mark);
    boolean uploadMark(String number,String mark);
    String getAllUserMark();
    String getUserMark(String number);
    String checkNumberFromLocal(String number);
    String checkNumberFromNet(String number);
    
 	String queryCategoryByParentId(int parent_id);
 	
 	double getLatitude();
 	double getLongitude();
 	String getCity();
 	
 	void plugPause();
 	void plugResume();
 	
 	String getBubbleRemind(int remindCode);
	String getRemind(int remindCode);
	void onRemindClick(int remindCode);	
 	
 	void perceptTel(String tel);
 	String getNextHotword();
 	
 	int getRefreshPlugViewState(); // add by zjh
 	
 	String getOperateAdDataById(int serverCode); // add by zjh
 	void deleteOperateAdData(int serverCode, int pageIndex); // add by zjh
 	
 	void addRemindBySelf(int type, int remindCode, boolean isMyService); // add by cj
 	
 	void addUMengEvent(String umengEventId, int time); // add by zjh modify by putao_lhq
 	
 	String getRequrlOfSignTail();
 	boolean updateExpandParamById(long category_id);// add by lisheng 2014-11-07
 	
 	int getRemindMaxCount(); //add by putao_lhq
}