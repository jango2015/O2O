package so.contacts.hub.thirdparty.tongcheng.bean;

public class TC_Request_HotelInfo extends TC_BaseData{

	private static final long serialVersionUID = 1L;

	private String hotelId;
	
	private int cs;

	public String getHotelId() {
		return hotelId;
	}

	public void setHotelId(String hotelId) {
		this.hotelId = hotelId;
	}

	public int getCs() {
		return cs;
	}

	public void setCs(int cs) {
		this.cs = cs;
	}
	
	public String getBody(){
		StringBuffer bodyBuffer = new StringBuffer();
		bodyBuffer.append("<body>");
		
		bodyBuffer.append("<hotelId>");
		bodyBuffer.append(hotelId);
		bodyBuffer.append("</hotelId>");
		
		if( cs <= 0 ){
			cs = 2; // 默认坐标系统为百度
		}
		bodyBuffer.append("<cs>");
		bodyBuffer.append(cs);
		bodyBuffer.append("</cs>");
		
		bodyBuffer.append("</body>");
		return bodyBuffer.toString();
	}
	
	
}
