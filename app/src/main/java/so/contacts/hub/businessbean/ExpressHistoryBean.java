package so.contacts.hub.businessbean;

public class ExpressHistoryBean {

	// 已签收
	public static final int STATUS_SIGNOFF = 1;
	// 运送途中
	public static final int STATUS_IN_TRANSIT = 2;
	// 未知状态
	public static final int STATUS_UNKNOW = 3;
	//查询无结果
	public static final int STATUS_NODTA = 4;
	
	public String comId;
	public String comName;
	public String num;
	public int status;
	public String date;
	public boolean isCheck;

}
