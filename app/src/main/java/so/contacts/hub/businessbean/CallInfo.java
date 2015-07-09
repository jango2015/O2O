package so.contacts.hub.businessbean;

import java.io.Serializable;

import android.text.TextUtils;

public class CallInfo extends ContactRecord implements Serializable {
	private String _id;// 通话记录id
	private String number;// 通话号码
	private String type;// 通话类型
	private String time;// 通话时长
	private String address;// 归属地
	private String date;// 通话日期
	private String phoneId;// 通话拨号SIM卡
	private int vtcall;;// 视频拨号

	public String getId() {
		return _id;
	}

	public void setId(String _id) {
		this._id = _id;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@Override
    public String getDate() {
		if (TextUtils.isEmpty(date) || "null".equals(date)) {
			date = "0";
		}
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddress() {
		return address;
	}
	
	/**
	 * @param phoneId sim卡
	 */
	public void setPhoneId(String phoneId) {
		this.phoneId = phoneId;
	}
	
	public String getPhoneId() {
		return phoneId;
	}

    public int getVtcall() {
        return vtcall;
    }

    public void setVtcall(int vtcall) {
        this.vtcall = vtcall;
    }
}
