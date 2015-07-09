package so.contacts.hub.thirdparty.tongcheng.bean;

public class TC_Response_BaseData extends TC_BaseData {

	private static final long serialVersionUID = 1L;

	/*
	rspType		rspCode		rspDesc
	 成功：0		0000		查询成功，有结果
				0001		查询成功，无结果
	 */
	private String rspType; //应答/错误类型
	
	private String rspCode; //应答/错误代码
	
	private String rspDesc; //应答/错误描述
	
	public String getRspType() {
		return rspType;
	}
	public void setRspType(String rspType) {
		this.rspType = rspType;
	}
	public String getRspCode() {
		return rspCode;
	}
	public void setRspCode(String rspCode) {
		this.rspCode = rspCode;
	}
	public String getRspDesc() {
		return rspDesc;
	}
	public void setRspDesc(String rspDesc) {
		this.rspDesc = rspDesc;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	
}
