package so.contacts.hub.thirdparty.cinema.bean;
/**
 * 电影开放城市
 */
public class MovieCity implements java.io.Serializable {


    private static final long serialVersionUID = 2144106545869831484L;
    
    private String citycode ;
    
    private String cityname;

    public String getCitycode() {
        return citycode;
    }

    public void setCitycode(String citycode) {
        this.citycode = citycode;
    }

    public String getCityname() {
        return cityname;
    }

    public void setCityname(String cityname) {
        this.cityname = cityname;
    }
    
    
}
