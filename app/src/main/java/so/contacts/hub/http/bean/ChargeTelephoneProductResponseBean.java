package so.contacts.hub.http.bean;

import java.io.Serializable;
import java.util.List;

public class ChargeTelephoneProductResponseBean implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private List<PhoneFeeProductInfo> phone_feee_list;//[List< TrafficProductInfo >][null able][套餐列表]
    private String acc_type;//归属地+运营商
    public List<PhoneFeeProductInfo> getList() {
        return phone_feee_list;
    }
    public void setList(List<PhoneFeeProductInfo> list) {
        this.phone_feee_list = list;
    }
    public String getAcc_type() {
        return acc_type;
    }
    public void setAcc_type(String acc_type) {
        this.acc_type = acc_type;
    }
}
