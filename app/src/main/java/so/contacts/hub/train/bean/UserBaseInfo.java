package so.contacts.hub.train.bean;

public class UserBaseInfo implements java.io.Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 3705803676594035500L;
    private String Name;
    private String Nickname;
    private String Sex;
    private String HeadImg;
    private String Mobile;
    private String Email;
    private String Ctime;
    private String Utime;
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getNickname() {
		return Nickname;
	}
	public void setNickname(String nickname) {
		Nickname = nickname;
	}
	public String getSex() {
		return Sex;
	}
	public void setSex(String sex) {
		Sex = sex;
	}
	public String getHeadImg() {
		return HeadImg;
	}
	public void setHeadImg(String headImg) {
		HeadImg = headImg;
	}
	public String getMobile() {
		return Mobile;
	}
	public void setMobile(String mobile) {
		Mobile = mobile;
	}
	public String getEmail() {
		return Email;
	}
	public void setEmail(String email) {
		Email = email;
	}
	public String getCtime() {
		return Ctime;
	}
	public void setCtime(String ctime) {
		Ctime = ctime;
	}
	public String getUtime() {
		return Utime;
	}
	public void setUtime(String utime) {
		Utime = utime;
	}
    
}
