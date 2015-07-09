package so.contacts.hub.search.rule;

import android.text.TextUtils;
import so.putao.findplug.YelloPageItem;

/**
 * 关键词与搜索数据的相似度匹配
 * @author change
 *
 */
public class MatchSimilarityRule implements IMatchRule {

	/**
	 * 匹配度检查，返回匹配度的证书值，值越大匹配度越高
	 * @param word 关键字
	 * @param item 搜索结果
	 * @return
	 */
	public double match(String word, String matchWord ) {
		return SimilarUtils.sim(word, matchWord);
		/*
		if(TextUtils.isEmpty(word) || item == null || TextUtils.isEmpty(item.getName()))
			return 0;
		
		final String name = item.getName();
		final String addr = item.getAddress();
		int role = 100;
		
		// 全匹配名字 匹配度：100
		if(word.equals(name)) {
			return role;
		}
		
		int wordLen = word.length();
		int nameLen = name.length();
		
		// 名字包含 匹配度：50
		int startPos = name.indexOf(word);
		if(startPos >= 0) {
			float deep = wordLen / nameLen;
			return Math.round(deep);
		}
		
		return 0;
		*/
	}
}
