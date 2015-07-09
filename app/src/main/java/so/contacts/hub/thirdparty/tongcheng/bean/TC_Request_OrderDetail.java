package so.contacts.hub.thirdparty.tongcheng.bean;

public class TC_Request_OrderDetail extends TC_BaseData {

	private static final long serialVersionUID = 1L;
	
	private String serialIds;
	
	private int isCtripOrderId;  		//是否返回携程订单(0:不返回 1：返回)
	
	private int isReturnCash;			//是否返回返现信息(0:不返回 1：返回)
	
	private int writeDB;				//读/写库(0:读库（默认）;1:写库)

	public String getSerialIds() {
		return serialIds;
	}

	public void setSerialIds(String serialIds) {
		this.serialIds = serialIds;
	}

	public int getIsCtripOrderId() {
		return isCtripOrderId;
	}

	public void setIsCtripOrderId(int isCtripOrderId) {
		this.isCtripOrderId = isCtripOrderId;
	}

	public int getIsReturnCash() {
		return isReturnCash;
	}

	public void setIsReturnCash(int isReturnCash) {
		this.isReturnCash = isReturnCash;
	}

	public int getWriteDB() {
		return writeDB;
	}

	public void setWriteDB(int writeDB) {
		this.writeDB = writeDB;
	}

	public String getBody(){
		StringBuffer bodyBuffer = new StringBuffer();
		bodyBuffer.append("<body>");
		
		bodyBuffer.append("<serialIds>");
		bodyBuffer.append(serialIds);
		bodyBuffer.append("</serialIds>");
		
		bodyBuffer.append("<isCtripOrderId>");
		bodyBuffer.append(isCtripOrderId);
		bodyBuffer.append("</isCtripOrderId>");

		bodyBuffer.append("<isReturnCash>");
		bodyBuffer.append(isReturnCash);
		bodyBuffer.append("</isReturnCash>");
		
		bodyBuffer.append("<writeDB>");
		bodyBuffer.append(writeDB);
		bodyBuffer.append("</writeDB>");
		
		bodyBuffer.append("</body>");
		return bodyBuffer.toString();
	}
	
}
