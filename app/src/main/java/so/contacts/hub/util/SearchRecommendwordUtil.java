package so.contacts.hub.util;

import java.util.ArrayList;
import java.util.List;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.core.ConstantsParameter;

import com.yulong.android.contacts.discover.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

/**
 * 显示搜索关键字
 */
public class SearchRecommendwordUtil {

	private static SearchRecommendwordUtil mInstance = null;
	//分隔符
	private static String RECOMMENDWORD_SEPSIGN = "#";
	//推荐词首个单位 
	private static String RECOMMENDWORD_HEAD = "【热词】";
	
	public static SearchRecommendwordUtil getInstance(){
		if( mInstance == null ){
			synchronized (SearchRecommendwordUtil.class) {
			    if( mInstance == null ){
			        mInstance = new SearchRecommendwordUtil();
			    }
			}
		}
		return mInstance;
	}
	
	private String[] mSearchRecommendwordList = null;
	
	private int mSearchRecommendwordCount = 0;
	
	 /**
     * 获取推荐词数组
     */
    public List<String> getRecommendwordList(){
        
        String[] list = null;
        
        list = mSearchRecommendwordList; 
        
        List<String> recommendwordList = new ArrayList<String>();
        if(list == null){
            return recommendwordList;
        }
        recommendwordList.add(RECOMMENDWORD_HEAD);
        for (int i = 0; i < list.length; i++) {
            recommendwordList.add(list[i]);
        }
        return recommendwordList;
    }
	
	   
    /**
     * 从配置中读取推荐词词版本号
     * @param context
     * @return
     */
    public int getRecommendwordsVersion() {
        Context context = ContactsApp.getInstance().getApplicationContext();
        
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences(
                ConstantsParameter.CONTACTS_SETTING, Context.MODE_MULTI_PROCESS);

        return preferences.getInt(ConstantsParameter.RECOMMEND_WORDS_VERSION, 0);
    }
    
    /**
     * 配置中推荐词版本号
     * @param context
     * @return
     */
    public void setRecommendKeywordsVersion(int version) {
        Context context = ContactsApp.getInstance().getApplicationContext();
        
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences(
                ConstantsParameter.CONTACTS_SETTING, Context.MODE_MULTI_PROCESS);
        Editor editor = preferences.edit();
        editor.putInt(ConstantsParameter.RECOMMEND_WORDS_VERSION, version);
        editor.commit();
    }
    
    /**
     * 从配置中读取推荐词
     * @param context
     * @return
     */
    public void initRecommendKeywords(Context context , String text) {

        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences(
                ConstantsParameter.CONTACTS_SETTING, Context.MODE_MULTI_PROCESS);

        
        //没有分割符说明没有有效值 这时候取本地数据
        if(text == null || !text.contains(RECOMMENDWORD_SEPSIGN)){
            String localtext = preferences.getString(ConstantsParameter.RECOMMEND_WORDS, "");
            if(localtext !=null && localtext.length()>0){
                mSearchRecommendwordList = localtext.split(RECOMMENDWORD_SEPSIGN);
                mSearchRecommendwordCount = mSearchRecommendwordList.length;
            }
        }else{

            mSearchRecommendwordList = text.split(RECOMMENDWORD_SEPSIGN);
            mSearchRecommendwordCount = mSearchRecommendwordList.length;
            Editor editor = preferences.edit();
            editor.putString(ConstantsParameter.RECOMMEND_WORDS, text);
            editor.commit();
        }
    }
    

    
	
}
