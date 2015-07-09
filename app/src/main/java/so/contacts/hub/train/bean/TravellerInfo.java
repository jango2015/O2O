package so.contacts.hub.train.bean;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class TravellerInfo implements Serializable {
	public String name;
	public String phone;
	public int idType;//证件类型
	public String id;//身份证号码
	public String birthday;
	public String address;
	public boolean isSelected =false;
	
	
	
	public TravellerInfo() {
		super();
	}
	public TravellerInfo(String name, String phone, int idType, String id,
			String birthday, String address) {
		this.name = name;
		this.phone = phone;
		this.idType = idType;
		this.id = id;
		this.birthday = birthday;
		this.address = address;
	}



	@Override
	public String toString() {
		return "TravellerInfo [name=" + name + ", phone=" + phone + ", idType="
				+ idType + ", id=" + id + ", birthday=" + birthday
				+ ", address=" + address + "]";
	}
	
	

}
