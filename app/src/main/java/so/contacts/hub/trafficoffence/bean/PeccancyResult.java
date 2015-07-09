package so.contacts.hub.trafficoffence.bean;

import java.io.Serializable;
import java.util.ArrayList;


public class PeccancyResult implements Serializable{
	/**这个类需要负责给服务器回报数据*/
	private byte status;
	private String msg;
	private String api_url;
	private String pt_u_id;
	private String params;
	private long car_id;
	
	
	//以下5项为某一车牌号下违章信息的总数据;
	private int total_illegal;  // 违章次数
	private int total_point;   // 共扣分数
	private double total_fine; // 共罚款(元)
	private String update_time_text; // 更新时间
	private String car_no;//车牌号
	private  String detail_list ;//所有违章信息的json
	
	/**等于1时, 为向服务器上报信息; 为2时, 显示通知栏;*/
	private String sub_msg_type;

	
	
	public String getCar_no() {
		return car_no;
	}
	public void setCar_no(String car_no) {
		this.car_no = car_no;
	}
	public byte getStatus() {
		return status;
	}
	public void setStatus(byte status) {
		this.status = status;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getApi_url() {
		return api_url;
	}
	public void setApi_url(String api_url) {
		this.api_url = api_url;
	}
	public String getPt_u_id() {
		return pt_u_id;
	}
	public void setPt_u_id(String pt_u_id) {
		this.pt_u_id = pt_u_id;
	}
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
	public long getCar_id() {
		return car_id;
	}
	public void setCar_id(long car_id) {
		this.car_id = car_id;
	}
	public int getTotal_illegal() {
		return total_illegal;
	}
	public void setTotal_illegal(int total_illegal) {
		this.total_illegal = total_illegal;
	}
	public int getTotal_point() {
		return total_point;
	}
	public void setTotal_point(int total_point) {
		this.total_point = total_point;
	}
	public double getTotal_fine() {
		return total_fine;
	}
	public void setTotal_fine(double total_fine) {
		this.total_fine = total_fine;
	}
	public String getUpdate_time_text() {
		return update_time_text;
	}
	public void setUpdate_time_text(String update_time_text) {
		this.update_time_text = update_time_text;
	}
	public String getSub_msg_type() {
		return sub_msg_type;
	}
	public void setSub_msg_type(String sub_msg_type) {
		this.sub_msg_type = sub_msg_type;
	}
	public String getDetail_list() {
		return detail_list;
	}
	public void setDetail_list(String detail_list) {
		this.detail_list = detail_list;
	}
	@Override
	public String toString() {
		return "PeccancyResult [status=" + status + ", msg=" + msg
				+ ", api_url=" + api_url + ", pt_u_id=" + pt_u_id + ", params="
				+ params + ", car_id=" + car_id + ", total_illegal="
				+ total_illegal + ", total_point=" + total_point
				+ ", total_fine=" + total_fine + ", update_time_text="
				+ update_time_text + ", car_no=" + car_no + ", detail_list="
				+ detail_list + ", sub_msg_type=" + sub_msg_type + "]";
	}

}
