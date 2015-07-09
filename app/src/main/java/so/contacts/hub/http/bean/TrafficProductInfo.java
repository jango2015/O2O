
package so.contacts.hub.http.bean;

import java.io.Serializable;

public class TrafficProductInfo implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private int prod_id;// [int][not null]手机流量充值产品id 20000

    private String prod_content;// [String][not null]产品面额 20

    private String putao_price;// [String][not null]产品支付价格 19.6

    private String prod_delaytimes;// [String][null able]充值处理时间(5分钟，24小时，48小时)

    private String acc_type;// [String][null able]手机号信息 [格式:四川移动]

    private String traffic_value;// [String][null able]流量面额 50M

    private String user_scope;// [String][null able]使用范围 [全国 省内]

    private String valid_time;// [String][null able]生效时间 [即时生效 次日生效]

    private String charge_count;// [String][null able]充值限次数 [无限次 10]

    private String support_user;// [String][null able] 支持用户
                                // [支持2G、3G、4G用户充值/支持支持2、3G用户充值]

    private String traffic_effective_period;// [String][null able] 有限时间
                                            // [90天/当月有效/4个月有效(含充值当月)]

    public int getProd_id() {
        return prod_id;
    }

    public void setProd_id(int prod_id) {
        this.prod_id = prod_id;
    }

    public String getProd_content() {
        return prod_content;
    }

    public void setProd_content(String prod_content) {
        this.prod_content = prod_content;
    }

    public String getPutao_price() {
        return putao_price;
    }

    public void setPutao_price(String putao_price) {
        this.putao_price = putao_price;
    }

    public String getProd_delaytimes() {
        return prod_delaytimes;
    }

    public void setProd_delaytimes(String prod_delaytimes) {
        this.prod_delaytimes = prod_delaytimes;
    }

    public String getAcc_type() {
        return acc_type;
    }

    public void setAcc_type(String acc_type) {
        this.acc_type = acc_type;
    }

    public String getTraffic_value() {
        return traffic_value;
    }

    public void setTraffic_value(String traffic_value) {
        this.traffic_value = traffic_value;
    }

    public String getUser_scope() {
        return user_scope;
    }

    public void setUser_scope(String user_scope) {
        this.user_scope = user_scope;
    }

    public String getValid_time() {
        return valid_time;
    }

    public void setValid_time(String valid_time) {
        this.valid_time = valid_time;
    }

    public String getCharge_count() {
        return charge_count;
    }

    public void setCharge_count(String charge_count) {
        this.charge_count = charge_count;
    }

    public String getSupport_user() {
        return support_user;
    }

    public void setSupport_user(String support_user) {
        this.support_user = support_user;
    }

    public String getTraffic_effective_period() {
        return traffic_effective_period;
    }

    public void setTraffic_effective_period(String traffic_effective_period) {
        this.traffic_effective_period = traffic_effective_period;
    }

}
