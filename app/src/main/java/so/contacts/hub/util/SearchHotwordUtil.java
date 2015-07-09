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
public class SearchHotwordUtil {

	private static SearchHotwordUtil mInstance = null;
	
	private static String HOTWORD_SEPSIGN = "#";
	
	/** 
	 * 带此标示的字符串不用加 开头词   例 ：    《输入关键字搜索，比如》           不用加
	 */
	private static String SPECIAL_STRING_FLAG = "noPrefix_";
	
	public static SearchHotwordUtil getInstance(){
		if( mInstance == null ){
			synchronized (SearchHotwordUtil.class) {
			    if( mInstance == null ){
			        mInstance = new SearchHotwordUtil();
			    }
			}
		}
		return mInstance;
	}
	
	private String[] mSearchHotwordList = null;
	
	// 输入关键字搜索，比如
	private String mSearchHotwordHead = "";
	
	private int mSearchHotwordsLen = 0;
	
	private int mIndex = 0;
	
	/**
	 * 获取最新的 搜索关键字
	 */
	public String getNextHotword(){
		if(mSearchHotwordList == null){
		    return null;
		}
		mIndex = (mIndex + 1) % mSearchHotwordsLen;
		return getShowWord(mSearchHotwordList, mSearchHotwordHead, mIndex);
	}
	
	/**
	 * 获取显示的 搜索关键字
	 */
	public String getHotword(){
		if(mSearchHotwordList == null){
            return null;
		}
		if( mIndex > mSearchHotwordsLen - 1 ){
			mIndex = 0;
		}
		return getShowWord(mSearchHotwordList, mSearchHotwordHead, mIndex);
	}
	
    /**
     * 从配置中读取热词版本号
     * @param context
     * @return
     */
    public int getHotKeywordsVersion() {
        Context context = ContactsApp.getInstance().getApplicationContext();
        
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences(
                ConstantsParameter.CONTACTS_SETTING, Context.MODE_MULTI_PROCESS);

        return preferences.getInt(ConstantsParameter.HOTKEY_WORDS_VERSION, 0);
    }
    
    /**
     * 配置中热词版本号
     * @param context
     * @return
     */
    public void setHotKeywordsVersion(int version) {
        Context context = ContactsApp.getInstance().getApplicationContext();
        
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences(
                ConstantsParameter.CONTACTS_SETTING, Context.MODE_MULTI_PROCESS);
        Editor editor = preferences.edit();
        editor.putInt(ConstantsParameter.HOTKEY_WORDS_VERSION, version);
        editor.commit();
    }
    
    /**
     * 拿到最终显示的词  因为分加前缀和不加前缀两种情况
     * @param wordList
     * @param headWord
     * @param index
     * @return
     */
    private String getShowWord(String[] wordList,String headWord,int index){
        if(index >= wordList.length || index < 0){
            return "";
        }
        if(wordList[index] == null){
            return "";
        }
        if(wordList[index].startsWith(SPECIAL_STRING_FLAG)){
            return wordList[index].replaceFirst(SPECIAL_STRING_FLAG, "");
        }else{
            return headWord + " " + wordList[index];
        }
    }
    
    /**
     * 从配置中读取热词
     * @param context
     * @return
     */
    public void initHotKeywords(Context context , String text) {
        if( TextUtils.isEmpty(mSearchHotwordHead) ){
            mSearchHotwordHead = context.getResources().getString(R.string.putao_homepage_search_hint);
        }

        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences(
                ConstantsParameter.CONTACTS_SETTING, Context.MODE_MULTI_PROCESS);

        
        //没有分割符说明没有有效值 这时候取本地数据
        if(text == null || !text.contains(HOTWORD_SEPSIGN)){
            String localtext = preferences.getString(ConstantsParameter.HOTKEY_WORDS, "");
          //没有有效值 这时候取本地字符串数组
            if(!localtext.equals("")){
                if( mSearchHotwordList == null ){
                    mSearchHotwordList = localtext.split(HOTWORD_SEPSIGN);
                    mSearchHotwordsLen = mSearchHotwordList.length;
                }
            }else{
                if( mSearchHotwordList == null ){
                    mSearchHotwordList = context.getResources().getStringArray(R.array.putao_serch_hotword);
                    mSearchHotwordsLen = mSearchHotwordList.length;
                }
            }
        }else{

            mSearchHotwordList = text.split(HOTWORD_SEPSIGN);
            mSearchHotwordsLen = mSearchHotwordList.length;
            Editor editor = preferences.edit();
            editor.putString(ConstantsParameter.HOTKEY_WORDS, text);
            editor.commit();
        }
    }
    

    
	
}
