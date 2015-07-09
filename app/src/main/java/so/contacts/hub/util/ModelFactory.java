package so.contacts.hub.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import so.contacts.hub.ui.yellowpage.bean.DianpingCustomsInfo;
import so.contacts.hub.ui.yellowpage.bean.DianpingReviewsInfo;

/**
 * 根据字符串解析出对应的对象
 * @author Michael
 *
 */
public class ModelFactory {

	/**
	 * 黄页详情（大众点评）团购信息
	 */
	public static DianpingCustomsInfo getCustomsData(String strData){
		DianpingCustomsInfo dataInfo = new DianpingCustomsInfo();
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(strData);
			String status = jsonObject.getString("status");
			if( !"OK".equals(status) ){
				return null;
			}
			JSONArray jsonArray = jsonObject.getJSONArray("deals");
			if( jsonArray == null || jsonArray.length() <= 0 ){
				return null;
			}
			JSONObject jsonObj = jsonArray.getJSONObject(0);
			dataInfo.setDeal_id(jsonObj.getString("deal_id"));
			dataInfo.setTitle(jsonObj.getString("title"));
			dataInfo.setDescription(jsonObj.getString("description"));
			dataInfo.setCurrent_price(jsonObj.getDouble("current_price"));
			dataInfo.setList_price(jsonObj.getDouble("list_price"));
			dataInfo.setImage_url(jsonObj.getString("image_url"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			dataInfo = null;
		}
		return dataInfo;
	}
	
	/**
	 * 黄页详情（大众点评）评论
	 */
	public static DianpingReviewsInfo getReviewsData(String strData){
		DianpingReviewsInfo dataInfo = new DianpingReviewsInfo();
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(strData);
			String status = jsonObject.getString("status");
			if( !"OK".equals(status) ){
				return null;
			}
			JSONArray jsonArray = jsonObject.getJSONArray("reviews");
			if( jsonArray == null || jsonArray.length() <= 0 ){
				return null;
			}
			JSONObject jsonObj = jsonArray.getJSONObject(0);
			dataInfo.setReview_id(jsonObj.getInt("review_id"));
			dataInfo.setText_excerpt(jsonObj.getString("text_excerpt"));
			dataInfo.setReview_rating(jsonObj.getDouble("review_rating"));
			
			String reviewUrl = "";
			JSONObject additionalInfo = jsonObject.getJSONObject("additional_info");
			if( additionalInfo != null ){
				String url = additionalInfo.getString("more_reviews_url");
				if( !TextUtils.isEmpty(url) ){
					reviewUrl = url;
				}
			}
			if( TextUtils.isEmpty(reviewUrl) ){
				reviewUrl = jsonObj.getString("review_url");
			}
			dataInfo.setReview_url(reviewUrl);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			dataInfo = null;
		}
		return dataInfo;
	}
	
}
