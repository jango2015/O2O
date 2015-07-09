package so.contacts.hub.trafficoffence.bean;

import java.io.Serializable;

public class Vehicle implements Serializable{
	
	/**
	 * 
	 */
	private long id = -1; //服务器ID
	private String province_name; // 省份名称
	private String province_pinyin; // 省份拼音
	private String city_name; // 城市名称
	private String city_pinyin; // 城市拼音
	private String car_province; // 车牌省份
	private String car_no; // 车牌号
	private String engine_no; // 发动机号
	private String vin_no; // 车架号
	
	public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getProvince_name() {
		return province_name;
	}
	public void setProvince_name(String province_name) {
		this.province_name = province_name;
	}
	public String getProvince_pinyin() {
		return province_pinyin;
	}
	public void setProvince_pinyin(String province_pinyin) {
		this.province_pinyin = province_pinyin;
	}
	public String getCity_name() {
		return city_name;
	}
	public void setCity_name(String city_name) {
		this.city_name = city_name;
	}
	public String getCity_pinyin() {
		return city_pinyin;
	}
	public void setCity_pinyin(String city_pinyin) {
		this.city_pinyin = city_pinyin;
	}
	public String getCar_province() {
		return car_province;
	}
	public void setCar_province(String car_province) {
		this.car_province = car_province;
	}
	public String getCar_no() {
		return car_no;
	}
	public void setCar_no(String car_no) {
		this.car_no = car_no;
	}
	public String getEngine_no() {
		return engine_no;
	}
	public void setEngine_no(String engine_no) {
		this.engine_no = engine_no;
	}
	public String getVin_no() {
		return vin_no;
	}
	public void setVin_no(String vin_no) {
		this.vin_no = vin_no;
	}
    @Override
    public String toString() {
        return "Vehicle [id=" + id + ", province_name=" + province_name + ", province_pinyin="
                + province_pinyin + ", city_name=" + city_name + ", city_pinyin=" + city_pinyin
                + ", car_province=" + car_province + ", car_no=" + car_no + ", engine_no="
                + engine_no + ", vin_no=" + vin_no + "]";
    }
	
}
