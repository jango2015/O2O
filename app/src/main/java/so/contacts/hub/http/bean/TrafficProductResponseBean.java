package so.contacts.hub.http.bean;

import java.io.Serializable;
import java.util.List;

public class TrafficProductResponseBean implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private List<TrafficProductInfo> list;//[List< TrafficProductInfo >][null able][套餐列表]
    private String acc_type;//归属地+运营商
    public List<TrafficProductInfo> getList() {
        return list;
    }
    public void setList(List<TrafficProductInfo> list) {
        this.list = list;
    }
    public String getAcc_type() {
        return acc_type;
    }
    public void setAcc_type(String acc_type) {
        this.acc_type = acc_type;
    }
}
