package so.contacts.hub.ui.yellowpage.bean;

import java.io.Serializable;

public class DianhuaBangShopItem implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String address;
    private String[] catIds ;
    private int coupon  ;
    private int dialNum  ;
    private int dist ;
    private String id ;
    private String largeImage ;
    private String logo ;
    private String name  ;
    private int offset  ;
    private DianhuabangTelephoneNum[] tels ;
    private int tuan ;
    private String website  ;
    private String weibo  ;
    private int cityId;
    
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String[] getCatIds() {
        return catIds;
    }
    public void setCatIds(String[] catIds) {
        this.catIds = catIds;
    }
    public int getCoupon() {
        return coupon;
    }
    public void setCoupon(int coupon) {
        this.coupon = coupon;
    }
    public int getDialNum() {
        return dialNum;
    }
    public void setDialNum(int dialNum) {
        this.dialNum = dialNum;
    }
    public int getDist() {
        return dist;
    }
    public void setDist(int dist) {
        this.dist = dist;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getLargeImage() {
        return largeImage;
    }
    public void setLargeImage(String largeImage) {
        this.largeImage = largeImage;
    }
    public String getLogo() {
        return logo;
    }
    public void setLogo(String logo) {
        this.logo = logo;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getOffset() {
        return offset;
    }
    public void setOffset(int offset) {
        this.offset = offset;
    }
    public DianhuabangTelephoneNum[] getTels() {
        return tels;
    }
    public void setTels(DianhuabangTelephoneNum[] tels) {
        this.tels = tels;
    }
    public int getTuan() {
        return tuan;
    }
    public void setTuan(int tuan) {
        this.tuan = tuan;
    }
    public String getWebsite() {
        return website;
    }
    public void setWebsite(String website) {
        this.website = website;
    }
    public String getWeibo() {
        return weibo;
    }
    public void setWeibo(String weibo) {
        this.weibo = weibo;
    }
	public int getCityId() {
		return cityId;
	}
	public void setCityId(int cityId) {
		this.cityId = cityId;
	}
}
