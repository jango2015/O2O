package so.contacts.hub.train.bean;

public class OffenAddress implements java.io.Serializable {
	
    private static final long serialVersionUID = 227853318759434654L;

    private String Name;
    private String Mobil;
    private String Pro;
    private String City;
    private String Reg;
    private String Straddr;
    private String Zcode;
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getMobil() {
		return Mobil;
	}
	public void setMobil(String mobile) {
		Mobil = mobile;
	}
	public String getPro() {
		return Pro;
	}
	public void setPro(String pro) {
		Pro = pro;
	}
	public String getCity() {
		return City;
	}
	public void setCity(String city) {
		City = city;
	}
	public String getReg() {
		return Reg;
	}
	public void setReg(String reg) {
		Reg = reg;
	}
	public String getStraddr() {
		return Straddr;
	}
	public void setStraddr(String straddr) {
		Straddr = straddr;
	}
	public String getZcode() {
		return Zcode;
	}
	public void setZcode(String zcode) {
		Zcode = zcode;
	}
	@Override
	public String toString() {
		return "OffenAddress [Name=" + Name + ", Mobil=" + Mobil + ", Pro="
				+ Pro + ", City=" + City + ", Reg=" + Reg + ", Straddr="
				+ Straddr + ", Zcode=" + Zcode + "]";
	}
	
	

}
