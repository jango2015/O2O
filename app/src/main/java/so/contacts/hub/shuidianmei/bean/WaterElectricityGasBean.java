package so.contacts.hub.shuidianmei.bean;

public class WaterElectricityGasBean {
    private String product_id ;//产品编号
    
    private String province ;//省份
    
    private String city ;//城市
    
    private String company ;//缴费单位
    
    private int weg_type;//充值类型

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public int getWeg_type() {
        return weg_type;
    }

    public void setWeg_type(int weg_type) {
        this.weg_type = weg_type;
    }

    @Override
    public String toString() {
        return "WaterElectricityGasBean [product_id=" + product_id + ", province=" + province
                + ", city=" + city + ", company=" + company + ", recharge_type=" + weg_type
                + "]";
    }
    
    
    
    
}
