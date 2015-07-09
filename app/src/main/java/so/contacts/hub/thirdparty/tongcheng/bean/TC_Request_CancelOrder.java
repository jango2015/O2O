package so.contacts.hub.thirdparty.tongcheng.bean;

public class TC_Request_CancelOrder extends TC_BaseData {

	private static final long serialVersionUID = 1L;
	
	private String serialId;
	
	private int cancelReasonCode;

	public String getSerialId() {
		return serialId;
	}

	public void setSerialId(String serialId) {
		this.serialId = serialId;
	}

	public int getCancelReasonCode() {
		return cancelReasonCode;
	}

	public void setCancelReasonCode(int cancelReasonCode) {
		this.cancelReasonCode = cancelReasonCode;
	}
	
	public String getBody(){
		StringBuffer bodyBuffer = new StringBuffer();
		bodyBuffer.append("<body>");
		
		bodyBuffer.append("<serialId>");
		bodyBuffer.append(serialId);
		bodyBuffer.append("</serialId>");
		
		bodyBuffer.append("<cancelReasonCode>");
		bodyBuffer.append(cancelReasonCode);
		bodyBuffer.append("</cancelReasonCode>");
		
		bodyBuffer.append("</body>");
		return bodyBuffer.toString();
	}

}
