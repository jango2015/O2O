package so.contacts.hub.thirdparty.tongcheng.util;

import so.contacts.hub.thirdparty.tongcheng.bean.TCRequestData;
import so.contacts.hub.thirdparty.tongcheng.bean.TC_Request_HotelRoomsWithGuaranteePolicy;
import so.contacts.hub.thirdparty.tongcheng.bean.TC_Response_HotelRoomsWithPolicy;
import so.contacts.hub.thirdparty.tongcheng.ui.YellowPageHotelDetailActivity.IQueryRoomPolicyDataCallback;
import android.os.AsyncTask;

public class QueryRoomPolicyDataTask extends AsyncTask<Void, Void, TC_Response_HotelRoomsWithPolicy> {

	private IQueryRoomPolicyDataCallback mIQueryRoomPolicyDataCallback = null;
	
	private String mHotelId;
	private String mComeDate;
	private String mLeaveDate;
	
	private String mRoomTypeId;
	private String mPolicyId;
	private String mComeTime;
	private int mIsGp;
	private int mIsSubmitOrder;
	private int mRooms;
	private int mGuestCome;
	
	private boolean needPolicy = false;
	
	public QueryRoomPolicyDataTask(String hotelId, String comeDate, String leaveDate,
			IQueryRoomPolicyDataCallback iQueryRoomPolicyDataCallback){
		mHotelId = hotelId;
		mComeDate = comeDate;
		mLeaveDate = leaveDate;
		mIQueryRoomPolicyDataCallback = iQueryRoomPolicyDataCallback;
	}
	
	public QueryRoomPolicyDataTask(String hotelId, String comeDate, String leaveDate,
			String roomTypeId, String policyId, String comeTime,
			IQueryRoomPolicyDataCallback iQueryRoomPolicyDataCallback){
		mHotelId = hotelId;
		mComeDate = comeDate;
		mLeaveDate = leaveDate;
		mRoomTypeId = roomTypeId;
		mPolicyId = policyId;
		mComeTime = comeTime;
		mIsGp = 1;
		mIsSubmitOrder = 1;
		mRooms = 1;
		mGuestCome = 1;
		
		needPolicy = true;
		mIQueryRoomPolicyDataCallback = iQueryRoomPolicyDataCallback;
	}
	
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		if( mIQueryRoomPolicyDataCallback != null ){
			mIQueryRoomPolicyDataCallback.onPreExecute();
		}
	}
	
	@Override
	protected TC_Response_HotelRoomsWithPolicy doInBackground(Void... arg0) {
		// TODO Auto-generated method stub
		TC_Request_HotelRoomsWithGuaranteePolicy roomsWithPolicyRequestBody = new TC_Request_HotelRoomsWithGuaranteePolicy();
		roomsWithPolicyRequestBody.setHotelId(mHotelId);
		roomsWithPolicyRequestBody.setComeDate(mComeDate);
		roomsWithPolicyRequestBody.setLeaveDate(mLeaveDate);
		if( needPolicy ){
			roomsWithPolicyRequestBody.setRoomTypeId(mRoomTypeId);
			roomsWithPolicyRequestBody.setPricePolicyId(mPolicyId);
			roomsWithPolicyRequestBody.setComeTime(mComeTime);
			roomsWithPolicyRequestBody.setIsGP(mIsGp);
			roomsWithPolicyRequestBody.setIsSubmitOrder(mIsSubmitOrder);
			roomsWithPolicyRequestBody.setRooms(mRooms);
			roomsWithPolicyRequestBody.setGuestCome(mGuestCome);
		}
		String requestBody = roomsWithPolicyRequestBody.getBody();
		String requestHead = TC_Request_DataFactory.getRequestHead("GetHotelRoomsWithGuaranteePolicy");
		String url = TC_Common.TC_URL_SEARCH_HOTEL;
		TCRequestData requestData = new TCRequestData(requestHead, requestBody);
		Object object = TC_Http.getDataByPost(url, requestData.getReqeustData(), TC_Response_HotelRoomsWithPolicy.class);
		if( object == null ){
			return null;
		}
		return (TC_Response_HotelRoomsWithPolicy) object;
	}
	
	@Override
	protected void onPostExecute(TC_Response_HotelRoomsWithPolicy result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if( mIQueryRoomPolicyDataCallback != null ){
			mIQueryRoomPolicyDataCallback.onPostExecute(result);
		}
	}
	
}
