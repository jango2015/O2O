package so.contacts.hub.ui.yellowpage.bean;

public class UserFeedbackBean {
	/**
	 * id 字段为数据库自增
	 * 
	 * c_time 字段为数据更新插入时的当前时间
	 * 
	 * */
	private String pt_token;
	private String dev_no;
	private String channel_no;
	private String version;
	private String band;//格式:设备厂商#设备型号
	private int net_status;
	private String phone_no;
	private String content;
	public String getPt_token() {
		return pt_token;
	}
	public void setPt_token(String pt_token) {
		this.pt_token = pt_token;
	}
	public String getDev_no() {
		return dev_no;
	}
	public void setDev_no(String dev_no) {
		this.dev_no = dev_no;
	}
	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getBand() {
		return band;
	}
	public void setBand(String band) {
		this.band = band;
	}
	public int getNet_status() {
		return net_status;
	}
	public void setNet_status(int net_status) {
		this.net_status = net_status;
	}
	public String getPhone_no() {
		return phone_no;
	}
	public void setPhone_no(String phone_no) {
		this.phone_no = phone_no;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getChannel_no() {
		return channel_no;
	}
	public void setChannel_no(String channel_no) {
		this.channel_no = channel_no;
	}
	@Override
	public String toString() {
		return "UserFeedbackBean [pt_token=" + pt_token + ", dev_no=" + dev_no
				+ ", channel_no=" + channel_no + ", version=" + version
				+ ", band=" + band + ", net_status=" + net_status
				+ ", phone_no=" + phone_no + ", content=" + content + "]";
	}
}
