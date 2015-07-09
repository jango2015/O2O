package so.contacts.hub.util;

import java.util.ArrayList;

import so.contacts.hub.common.OperatorsCommans;
import so.contacts.hub.ui.yellowpage.bean.OperatorsInfo;
import so.contacts.hub.ui.yellowpage.bean.QueryCommandInfo;

import android.text.TextUtils;
import android.util.SparseArray;

/**
 * 话费查询 
 * 运营商工具类
 *
 */
public class OperatorsUtils {
	
	private static final String START_YIDONG1 = "46000";
	private static final String START_YIDONG2 = "46002";
	private static final String START_LIANTONG = "46001";
	private static final String START_DIANXIN = "46003";
	
	private SparseArray<QueryCommandInfo> mOperatorsList = new SparseArray<QueryCommandInfo>();
	
	private static OperatorsUtils mInstance = null;
	
	public static OperatorsUtils getInstance(){
		if( mInstance == null ){
			synchronized (OperatorsUtils.class) {
				mInstance = new OperatorsUtils();
			}
		}
		return mInstance;
	}
	
	private OperatorsUtils(){
		initOperatorInfos();
	}
	
	/**
	 * 获取所有运营商信息 查询话费信息
	 */
	private void initOperatorInfos(){
		mOperatorsList.clear();
		
		// 移动
		addOperatorInfo(new OperatorsInfo(OperatorsCommans.TAG_YIDONG, OperatorsCommans.NUM_YIDONG, OperatorsCommans.TEXT_YIDONG));
		// 联通
		addOperatorInfo(new OperatorsInfo(OperatorsCommans.TAG_LIANTONG, OperatorsCommans.NUM_LIANTONG, OperatorsCommans.TEXT_LIANTONG));
		// 电信
		addOperatorInfo(new OperatorsInfo(OperatorsCommans.TAG_DIANXIN, OperatorsCommans.NUM_DIANXIN, OperatorsCommans.TEXT_DIANXIN));
	}
	
	private void addOperatorInfo(OperatorsInfo operatorInfo){
		mOperatorsList.put(operatorInfo.getTag(), operatorInfo.getCommandinfo());
	}
	
	public void addOperatorInfos(ArrayList<OperatorsInfo> infos){
		if( infos == null ){
			return;
		}
		if( infos.size() <= 0 ){
			return;
		}
		for(OperatorsInfo info : infos){
			addOperatorInfo(info);
		}
	}
	
	public QueryCommandInfo getCommandInfo(String imsiInfo){
		int operatorsTag = getOperatorsTag(imsiInfo);
		if( operatorsTag == -1 ){
			return null;
		}
		return  mOperatorsList.get(operatorsTag);
	}
	
	public QueryCommandInfo getCommandInfo(int operatorTag){
		return mOperatorsList.get(operatorTag);
	}
	
	public int getOperatorsTag(String imsiInfo){
		int operatorsTag = -1;
		if( TextUtils.isEmpty(imsiInfo) ){
			return operatorsTag;
		}
		// IMSI号前面3位460是国家，紧接着后面2位00、02是中国移动，01是中国联通，03是中国电信。
		if (imsiInfo.startsWith(START_YIDONG1) || imsiInfo.startsWith(START_YIDONG2)) {
			// 中国移动
			operatorsTag = OperatorsCommans.TAG_YIDONG;
		} else if (imsiInfo.startsWith(START_LIANTONG)) {
			// 中国联通
			operatorsTag = OperatorsCommans.TAG_LIANTONG;
		} else if (imsiInfo.startsWith(START_DIANXIN)) {
			// 中国电信
			operatorsTag = OperatorsCommans.TAG_DIANXIN;
		}
		return operatorsTag;
	}
}