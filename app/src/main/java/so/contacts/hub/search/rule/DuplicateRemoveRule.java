package so.contacts.hub.search.rule;

import java.util.ArrayList;
import java.util.List;
import android.text.TextUtils;
import so.contacts.hub.util.DistanceUtil;
import so.contacts.hub.util.LogUtil;
import so.putao.findplug.SourceItemObject;
import so.putao.findplug.YelloPageItem;

/**
 * 重复数据删除
 */
public class DuplicateRemoveRule {
	
	private static final String TAG = "DuplicateRemoveRule";

	/**
	 * 校验重复数据的定位距离的最小距离(米)
	 */
	private static final double MATCH_DISTANCE = 500;
	
	/**
	 * 名字相似度最小值
	 */
	private static final double NAME_SIMILAR_MIN = 0.8;
	
	/**
	 * 重复数据删除
	 * 【将newList添加到orgiList，并且去除重复数据】
	 */
	@SuppressWarnings("rawtypes")
	public static void duplicateRemoved(List<YelloPageItem> orgiList, List<YelloPageItem> newList) {
		if( newList == null || newList.size() == 0 ){
			return;
		}
		long startTime = System.currentTimeMillis();
		int size = newList.size();
		for(int i = 0; i < size; i++){
			YelloPageItem data = newList.get(i);
			if( !isContains(orgiList, data) ) {
				orgiList.add(data);
			}
		}
		LogUtil.i(TAG, "duplicateRemoved time: " + (System.currentTimeMillis() - startTime));
	}
	
	/**
	 * 重复数据删除
	 * 【去除orgiList中重复数据】
	 */
	@SuppressWarnings("rawtypes")
	public static void duplicateRemoved(List<YelloPageItem> orgiList){
		if( orgiList == null || orgiList.size() == 0 ){
			return;
		}
		LogUtil.i(TAG, "orgiList start size: " + orgiList.size());
		long startTime = System.currentTimeMillis();
		List<YelloPageItem> newList = new ArrayList<YelloPageItem>();
		for(int i = 0; i < orgiList.size(); i++){
			YelloPageItem data = orgiList.get(i);
			if( !isContains(newList, data) ) {
				newList.add(data);
			}
		}
		orgiList.clear();
		orgiList.addAll(newList);
		LogUtil.i(TAG, "duplicateRemoved time: " + (System.currentTimeMillis() - startTime) + " ,orgiList end size: " + orgiList.size());
	}
	
	/**
	 * dataList中是否包含有checkData
	 * 【重复数据规则】
	 *  规则一：定位距离 < 500米， 有相同号码 或者 名字形似度>=80%.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static boolean isContains(List<YelloPageItem> dataList, YelloPageItem checkData){
		int listSize = dataList.size();
		if( listSize == 0 ){
			return false;
		}
		List<String> checkNumbers = checkData.getNumbers();
		if( checkNumbers == null || checkNumbers.size() == 0 ){
			// 没有号码，则表示不是重复数据
			return false;
		}
		
		String checkName = checkData.getName();
		SourceItemObject checkItemObject = checkData.getData();
		double checkLong = checkItemObject.getLongitude();
		double checkLat = checkItemObject.getLatitude();
		for(int i = 0; i < listSize; i++){
			YelloPageItem data = dataList.get(i);
			
			//规则一 start
			String itemName = data.getName();
			SourceItemObject itemObject = data.getData();
			double itemLong = itemObject.getLongitude();
			double itemLat = itemObject.getLatitude();
			if( (itemLong != 0 && itemLat != 0) && (checkLong != 0 && checkLat != 0) ){ // 距离不为空，则先比较距离
				double distance = DistanceUtil.lantitudeLongitudeDist(checkLong, checkLat, itemLong, itemLat);
				if( distance < MATCH_DISTANCE ){ // 距离小于500米
					boolean isSimilar = isSimilarCompare(data.getNumbers(), checkData.getNumbers(), itemName, checkName);
					if( isSimilar ){
						return true;
					}
				}
			}else{ // 只要有一个经纬度为空，则只比较号码 和 名称
				boolean isSimilar = isSimilarCompare(data.getNumbers(), checkData.getNumbers(), itemName, checkName);
				if( isSimilar ){
					return true;
				}
			}
			//规则一  end
		}
		return false;
	}
	
	/**
	 * 比较两条数据 是否有相同的号码 或者 名字相似度>=80%
	 */
	private static boolean isSimilarCompare(List<String> numberList1, List<String> numberList2, String name1, String name2){
		List<String> numbers = checkAndGetValidNumbers(numberList1);
		if( numbers != null && numbers.size() > 0 ){
			List<String> checkNumbers = checkAndGetValidNumbers(numberList2);
			checkNumbers.retainAll(numbers); // 获取是否有相同的号码
			if( checkNumbers.size() > 0 ){ // 有相同的号码
				return true;
			}
		}
		if( SimilarUtils.sim(name1, name2) >= NAME_SIMILAR_MIN ){ // 名字相似度>=80%
			return true;
		}
		return false;
	}
	
	/**
	 * 检测并返回checkNumbers中的完整号码
	 * 有些号码中类似于：0755-26546036;0755-26546038
	 */
	private static List<String> checkAndGetValidNumbers(List<String> checkNumbers){
		if( checkNumbers == null ){
			return null;
		}
		List<String> numbers = new ArrayList<String>();
		for(int i = 0; i < checkNumbers.size(); i++){
			String numberStr = checkNumbers.get(i);
			if( TextUtils.isEmpty(numberStr) ){
				continue;
			}
			if( numberStr.contains(";") ){
				String[] splitNumbers = numberStr.split(";");
				for(int j = 0; j < splitNumbers.length; j++){
					numbers.add(splitNumbers[j]);
				}
			}else{
				numbers.add(numberStr);
			}
		}
		return numbers;
	}
	
}


















