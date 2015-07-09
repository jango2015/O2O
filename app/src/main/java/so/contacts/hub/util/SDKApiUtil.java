package so.contacts.hub.util;

import android.annotation.SuppressLint;
import so.contacts.hub.ui.yellowpage.bean.GaoDePoiItem;
import so.contacts.hub.yellow.data.ParseResult;
import so.contacts.hub.yellow.data.SearchTelResult;
import so.putao.findplug.YelloPageItem;
import so.putao.findplug.YellowPageItemGaoDe;

/**
 * 对外API铺助工具
 * @author putao_lhq
 * @version 2014年9月18日
 */
public class SDKApiUtil {
	
	private static final int MAX_EXP_LENGTH = 20;//快递单号最大的长度
	private static final int MIN_EXP_LENGTH = 5;//快递单号最小长度

 
	public static String parse(String body) {
		if (body == null || "".equals(body)) {
			return null;
		}
		ParseResult result = new ParseResult();
		if (body.contains(CommonValueUtil.getInstance().getCommonHuafei())){
			result.setType(ParseResult.PARSE_TYPE_CHR);
			result.setParsed_str(doParseChr(0,body));
			return ConvUtil.convertObjToBase64String(result);
		} 
		if (body.contains(CommonValueUtil.getInstance().getCommonKuaidi())){
			result.setType(ParseResult.PARSE_TYPE_EXP);
			result.setParsed_str(doParseExp(body));
			return ConvUtil.convertObjToBase64String(result);
		}
		if (body.contains(CommonValueUtil.getInstance().getCommonLiuliang())) {
			result.setType(ParseResult.PARSE_TYPE_TRA);
			result.setParsed_str(doParseTra(body));
		}
		return null;
	}

	private static String doParseTra(String body) {
		return null;
	}

	@SuppressLint("DefaultLocale") 
	private static String doParseChr(int startPos, String body) {
		
		float money = 0.0f;
        String moneyStr = "";
        
		if (startPos >= body.length())
            return "";

        // 从第startPos位置开始查找
        int endPos = body.indexOf("元", startPos);
        if (endPos < 0) {
            return "";
        }

        // 找到‘元’后向前找money
        int numPos = endPos - 1;
        while (numPos >= startPos) {
            char s = body.charAt(numPos);
            if (Character.isDigit(s) || s == '.') {
                numPos--;
            } else {
                break;
            }
        }

        // 再次向前查找汉字‘金额’或‘余额’
        int hzPos = numPos;
        boolean hzFlag = false;
        while (hzPos >= startPos) {
            hzPos--;
            if (body.substring(hzPos, numPos + 1).indexOf("金额") >= 0
                    || body.substring(hzPos, numPos + 1).indexOf("余额") >= 0) {
                hzFlag = true;
                break;
            }
        }

        if (!hzFlag) {
            // 找不到汉字‘金额’或‘余额’, 向后递归
            return doParseChr(endPos + 1, body);
        }

        // 找到money数字并解析
        boolean parseFlag = false;
        try {
            moneyStr = body.substring(numPos + 1, endPos);
            money = Float.parseFloat(moneyStr);
            parseFlag = true;
        } catch (Exception e) {
        }

        if (parseFlag) {
            return String.format("%.1f", money);
        } else {
            // 递归找下一个元
            return doParseChr(endPos + 1, body);
        }
	}

	private static String doParseExp(String body) {
		String danhao = CommonValueUtil.getInstance().getCommonDanhao();
		int index = body.indexOf(danhao);
		if (index == -1) {
			//do other match.
			return null;
		}
		index += danhao.length();
		String result = doParseExp(body, index);
		if(result == null) {
			index++;
			result = doParseExp(body, index);
		}
		return result;
	}

	private static String doParseExp(String body, int start) {
		int startPos = start;
		int endPos = startPos;;
		while (endPos < body.length()) {
			char s = body.charAt(endPos);
			if (Character.isDigit(s)) {
				endPos++;
			} else if (s == ':') {
				startPos++;
				endPos++;
			} else if (((int)s > (int)'A') && ((int)s < (int)'Z')) {
				endPos++;
			}else {
				break;
			}
		}
		String exp_num = body.substring(startPos, endPos);
		if (exp_num == null ||
				exp_num.equals("") ||
				exp_num.length() < MIN_EXP_LENGTH || 
				exp_num.length() > MAX_EXP_LENGTH) {
			return null;
		}
		return exp_num;
	}
	
	public static YelloPageItem getYellowPageItem(String base64) {
		SearchTelResult param = (SearchTelResult) ConvUtil.convertBase64StringToObj(base64);
    	GaoDePoiItem item = new GaoDePoiItem();
    	item.setAddress(param.getAddress());
    	item.setDistance(param.getDistance());
    	item.setLatitude(param.getLatitude());
    	item.setLongitude(param.getLongitude());
    	item.setName(param.getName());
    	item.setPoiId(param.getPoiId());
    	item.setTelephone(param.getTelephone());
    	item.setWebsite(param.getWebsite());
    	YelloPageItem itemGaoDe = new YellowPageItemGaoDe(item);
		return itemGaoDe;
	}
}
