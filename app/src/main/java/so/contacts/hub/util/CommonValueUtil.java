package so.contacts.hub.util;

import com.yulong.android.contacts.discover.R;
import android.content.Context;

/**
 * 保存常量值
 */
public class CommonValueUtil {

	private static CommonValueUtil mInstance = null;
	
	public static CommonValueUtil getInstance(){
		if( mInstance == null ){
			synchronized (CommonValueUtil.class) {
				if( mInstance == null ){
					mInstance = new CommonValueUtil();
				}
			}
		}
		return mInstance;
	}
	
	/** 市 */
	private String mCommonCity = "";

	/** 县 */
	private String mCommonCounty = "";
	
	/** 深圳 */
	private String mCommonShenzhen = "";
	
	/** 单号 */
	private String mCommonDanhao = "";

	/** 话费 */
	private String mCommonHuafei = "";
	
	/** 快递 */
	private String mCommonKuaidi = "";
	
	/** 流量 */
	private String mCommonLiuliang = "";
	
	private String[] mCityDirectlyDivisionList = null;
	
	public void initCommonData(Context context){
		mCommonCity = context.getString(R.string.putao_common_city);
		mCommonCounty = context.getString(R.string.putao_common_county);
		mCommonShenzhen = context.getString(R.string.putao_common_shenzhen);

		mCommonDanhao = context.getString(R.string.putao_common_danhan);
		mCommonHuafei = context.getString(R.string.putao_common_huafei);
		mCommonKuaidi = context.getString(R.string.putao_common_kuaidi);
		mCommonLiuliang = context.getString(R.string.putao_common_liuliang);
		
		mCityDirectlyDivisionList = context.getResources().getStringArray(
                R.array.putao_city_directly_division);
	}
	
	public String getCityData(){
		return mCommonCity;
	}
	
	public String getCountyData(){
		return mCommonCounty;
	}
	
	public String getCityShenzhen(){
		return mCommonShenzhen;
	}
	
	public String getCommonDanhao(){
		return mCommonDanhao;
	}
	
	public String getCommonHuafei(){
		return mCommonHuafei;
	}
	
	public String getCommonKuaidi(){
		return mCommonKuaidi;
	}
	

	public String getCommonLiuliang(){
		return mCommonLiuliang;
	}
	
	/**
	 * 过滤content中的后缀 ：“市”、“县”
	 * @param content
	 * @return
	 */
	public String filterDistrict(String content){
		String[] tagList = new String[]{mCommonCity, mCommonCounty};
		int index = 0;
		while( index < tagList.length ){
			String tag = tagList[index++];
			if( content.endsWith(tag) && content.length() > 2){
				// 长度必须大于2， 因为有些名字（如：开县），所以就没必要除去
				content = content.substring(0, content.length() - tag.length());
				break;
			}
		}
		return content;
	}
	
	/**
	 * 判断城市结果中是否有“省直辖县级行政区划”，“自治区直辖县级行政区划”等信息，有返回true，没有返回false
     * @param content
     * @return
	 */
    public boolean isDirectlyDivision(String content) {

        if (mCityDirectlyDivisionList == null || mCityDirectlyDivisionList.length == 0) {
            return false;
        }

        for (int i = 0; i < mCityDirectlyDivisionList.length; i++) {
            if (mCityDirectlyDivisionList[i].equals(content)) {
                return true;
            }
        }
        return false;
    }
}
