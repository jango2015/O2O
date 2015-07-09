package so.contacts.hub.util;

import android.text.TextUtils;

/**
 * 对外API铺助工具
 * @author putao_lhq
 * @version 2014年9月18日
 */
public class SmsAnalysisUtil {
	
	public static String getExpressNum(String body,int startPos){
		if(body == null || body.equals("") || startPos >= body.length()){
			return "";
		}
		String expressNum = "";
		int endPos = body.length();
		int tempPos = startPos;
		int index = tempPos;
		while(tempPos < endPos){
			char s = body.charAt(tempPos);
			if(Character.isDigit(s) || ((int)s > (int)'A') && ((int)s < (int)'Z')){
				index = tempPos;
				break;
			}
			tempPos++;
		}
		
		while(tempPos < endPos){
			char s = body.charAt(tempPos);
			if (Character.isDigit(s) || ((int)s > (int)'A') && ((int)s < (int)'Z')) {
				tempPos++;
			} else {
				break;
			}
		}
		int end = tempPos;
		if(end > index && end < endPos){
			expressNum = body.substring(index, end);
		}
		if(expressNum.equals("") || expressNum.length() < 8 || expressNum.length() > 20){
			return getExpressNum(body, end);
		}
		return expressNum;
	}
	
	/**
	 * add by zjh 2014-12-20 start
		 * BUG #2212 查快递，粘贴板号码快速查询提示只能粘贴数字字符，应该支持英文字符
	 * @param body
	 * @return
	 */
	public static String getExpressNumWithDigitAndChar(String body){
		if( TextUtils.isEmpty(body) ){
			return "";
		}
		StringBuffer strBuffer = new StringBuffer();
		int len = body.length();
		for(int i = 0; i < len; i++){
			char charStr = body.charAt(i);
			if(Character.isDigit(charStr) 
					|| (charStr >= 'A') && ( charStr <= 'Z') || (charStr >= 'a') && ( charStr <= 'z')){
				strBuffer.append(charStr);
			}
		}
		return strBuffer.toString();
	}
	
}
