package so.contacts.hub.search.rule;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import android.text.TextUtils;
import so.contacts.hub.util.LogUtil;
import so.putao.findplug.YelloPageItem;

public class ResultSortedRule {

	private static final String TAG = "ResultSortedRule";
	
	private static final int SORT_POWER_MAX = 100;
	
	/**
	 * 排序规则 字段
	 */
	public static final int SORT_TYPE_NAME = 1;  //名称
	public static final int SORT_TYPE_DISTANCE = 2; //距离
	public static final int SORT_TYPE_RATING = 3; //评星
	public static final int SORT_TYPE_ADDRESS = 4; //地址
	
	
	/**
	 * 对结果按照规则进行排序
	 */
	@SuppressWarnings("rawtypes") 
	public static void resultSorted(List<YelloPageItem> orgiList, final String keyword, final int[] keyTypeList) {
		if( orgiList == null || orgiList.size() <= 1 || TextUtils.isEmpty(keyword) || keyTypeList == null || keyTypeList.length == 0){
			return;
		}
		long startTime = System.currentTimeMillis();
		Collections.sort(orgiList, new Comparator<YelloPageItem>() {
			
            @Override
            public int compare(YelloPageItem leftData, YelloPageItem rightData) {
            	int index = -1;
            	int keyTag = getNextKeyTag(keyTypeList, index++);
            	while( keyTag != -1 ){
            		int sortData = getSortData(leftData, rightData, keyword, keyTag);
            		if( sortData == 0 ){
            			keyTag = getNextKeyTag(keyTypeList, index++);
            		}else{
            			return sortData;
            		}
            	}
            	return 0;
            }
        });
		LogUtil.i(TAG, "resultSorted time: " + (System.currentTimeMillis() - startTime));
	}
	
	/**
	 * 根据keyTag对应的排序规则比较两条数据
	 */
	@SuppressWarnings("rawtypes")
	private static int getSortData(YelloPageItem leftData, YelloPageItem rightData, String keyword, int keyType){
		if( keyType == SORT_TYPE_NAME ){
			return getSortDataByName(leftData, rightData, keyword);
		}else if( keyType == SORT_TYPE_DISTANCE ){
			return getSortDataByDistance(leftData, rightData);
		}else if( keyType == SORT_TYPE_RATING ){
			return getSortDataByRating(leftData, rightData);
		}else if( keyType == SORT_TYPE_ADDRESS ){
			return getSortDataByAddress(leftData, rightData, keyword);
		}
		return 1;
	}
	
	/**
	 * 获取keyTagList中index下一个keyTag
	 */
	private static int getNextKeyTag(int[] keyTypeList, int index){
		int keyTag = -1;
		if( index < keyTypeList.length - 1 ){
			keyTag = keyTypeList[index + 1];
		}
		return keyTag;
	}
	
	/**
	 * 按照名称相似度 从大到小排序
	 */
	@SuppressWarnings("rawtypes")
	private static int getSortDataByName(YelloPageItem leftData, YelloPageItem rightData, String keyword) {
		String lName = leftData.getName();
		String rName = rightData.getName();
		int lSimilarData = (int) (SORT_POWER_MAX * SimilarUtils.sim(lName, keyword));
		int rSimilarData = (int) (SORT_POWER_MAX * SimilarUtils.sim(rName, keyword));
		return rSimilarData - lSimilarData;
	}
	
	/**
	 * 按照距离 从近到远排序
	 */
	@SuppressWarnings("rawtypes")
	private static int getSortDataByDistance(YelloPageItem leftData, YelloPageItem rightData) {
		double lDistance = leftData.getDistance();
		double rDistance = rightData.getDistance();
		return (int) (lDistance - rDistance);
	}
	
	/**
	 * 按照评星从大到小排序
	 */
	@SuppressWarnings("rawtypes")
	private static int getSortDataByRating(YelloPageItem leftData, YelloPageItem rightData) {
		int lRating = (int) (leftData.getAvg_rating() * SORT_POWER_MAX);
		int rRating = (int) (rightData.getAvg_rating() * SORT_POWER_MAX);
		return rRating - lRating;
	}
	
	/**
	 * 按照地址相似度 从大到小排序
	 */
	@SuppressWarnings("rawtypes")
	private static int getSortDataByAddress(YelloPageItem leftData, YelloPageItem rightData, String keyword) {
		String lAddress = leftData.getAddress();
		String rAddress = rightData.getAddress();
		int lSimilarData = (int) (SORT_POWER_MAX * SimilarUtils.sim(lAddress, keyword));
		int rSimilarData = (int) (SORT_POWER_MAX * SimilarUtils.sim(rAddress, keyword));
		return rSimilarData - lSimilarData;
	}
}















