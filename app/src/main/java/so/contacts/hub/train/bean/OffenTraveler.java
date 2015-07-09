package so.contacts.hub.train.bean;

public class OffenTraveler implements java.io.Serializable {

    private static final long serialVersionUID = 714018766567212708L;
    
    private String Lname;
    private String Sex;
    private String Ltype;
    private String Mobile;
    private String Birthday;
    private String Cno;
    private String Ctype;
    private String Cname;
    private String Ctime;
    
    public OffenTraveler() {
    	Lname = "";
    	Sex = "";
    	Ltype = "";
    	Mobile = "";
    	Birthday = "";
    	Cno = "";
    	Ctype = "";
    	Cname = "";
    	Ctime = "";
    }
    
	public String getLname() {
		return Lname;
	}
	public void setLname(String lname) {
		Lname = lname;
	}
	public String getSex() {
		return Sex;
	}
	public void setSex(String sex) {
		Sex = sex;
	}
	public String getLtype() {
		return Ltype;
	}
	public void setLtype(String ltype) {
		Ltype = ltype;
	}
	public String getMobile() {
		return Mobile;
	}
	public void setMobil(String mobile) {
		Mobile = mobile;
	}
	public String getBirthday() {
		return Birthday;
	}
	public void setBirthday(String birthday) {
		Birthday = birthday;
	}
	public String getCno() {
		return Cno;
	}
	public void setCno(String cno) {
		Cno = cno;
	}
	public String getCtype() {
		return Ctype;
	}
	public void setCtype(String ctype) {
		Ctype = ctype;
	}
	public String getCname() {
		return Cname;
	}
	public void setCname(String cname) {
		Cname = cname;
	}
	public String getCtime() {
		return Ctime;
	}
	public void setCtime(String ctime) {
		Ctime = ctime;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}


    
}
