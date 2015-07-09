package so.contacts.hub.thirdparty.cinema.bean;

import java.io.Serializable;
/**
 * 
 * @author ffh
 * @since 2014/12/18
 * 影院详情
 */
public class CinemaDetail implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private long cinemaid;//影院id
    private String cinemaname;//影院名称
    private String englishname;//影院英文名称
    private String logo;//影院Logo
    private String citycode;//城市编码
    private String cityname;//城市名称
    private String countycode;//区县代码
    private String countyname;//区县名称 ext:黄浦区
    private String indexarea;//商圈名称  ext:人民广场
    private String contactphone;//影院联系电话
    private String address;//影院详细地址
    private String content;//影院详情     注:该字段仅在影片详情中返回
    private String opentime;//营业时间
    private String pointx;//google经度    
    private String pointy;//google维度
    private String bpointx;//baidu经度
    private String bpointy;//baidu维度
    private String generalmark;//影院评分
    private String feature;//影院特色
    private String transport;//公交信息     
    private String linename;//地铁线路名称
    private String stationname;//地铁站名称
    private String exitnumber;//地铁出口
    private String popcorn;//爆米花套餐   1表示有0表示没有
    private long clickedtimes;//关注数
    private long collectedtimes;//收藏数
    private long diaryid;//取票机位置ID 
    
    private double distance;//存储影院距离
    public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	
	
	public long getCinemaid() {
        return cinemaid;
    }
    public void setCinemaid(long cinemaid) {
        this.cinemaid = cinemaid;
    }
    public String getCinemaname() {
        return cinemaname;
    }
    public void setCinemaname(String cinemaname) {
        this.cinemaname = cinemaname;
    }
    public String getEnglishname() {
        return englishname;
    }
    public void setEnglishname(String englishname) {
        this.englishname = englishname;
    }
    public String getLogo() {
        return logo;
    }
    public void setLogo(String logo) {
        this.logo = logo;
    }
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
    public String getCountycode() {
        return countycode;
    }
    public void setCountycode(String countycode) {
        this.countycode = countycode;
    }
    public String getCountyname() {
        return countyname;
    }
    public void setCountyname(String countyname) {
        this.countyname = countyname;
    }
    public String getIndexarea() {
        return indexarea;
    }
    public void setIndexarea(String indexarea) {
        this.indexarea = indexarea;
    }
    public String getContactphone() {
        return contactphone;
    }
    public void setContactphone(String contactphone) {
        this.contactphone = contactphone;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getOpentime() {
        return opentime;
    }
    public void setOpentime(String opentime) {
        this.opentime = opentime;
    }
    public String getPointx() {
        return pointx;
    }
    public void setPointx(String pointx) {
        this.pointx = pointx;
    }
    public String getPointy() {
        return pointy;
    }
    public void setPointy(String pointy) {
        this.pointy = pointy;
    }
    public String getBpointx() {
        return bpointx;
    }
    public void setBpointx(String bpointx) {
        this.bpointx = bpointx;
    }
    public String getBpointy() {
        return bpointy;
    }
    public void setBpointy(String bpointy) {
        this.bpointy = bpointy;
    }
    public String getGeneralmark() {
        return generalmark;
    }
    public void setGeneralmark(String generalmark) {
        this.generalmark = generalmark;
    }
    public String getFeature() {
        return feature;
    }
    public void setFeature(String feature) {
        this.feature = feature;
    }
    public String getTransport() {
        return transport;
    }
    public void setTransport(String transport) {
        this.transport = transport;
    }
    public String getLinename() {
        return linename;
    }
    public void setLinename(String linename) {
        this.linename = linename;
    }
    public String getStationname() {
        return stationname;
    }
    public void setStationname(String stationname) {
        this.stationname = stationname;
    }
    public String getExitnumber() {
        return exitnumber;
    }
    public void setExitnumber(String exitnumber) {
        this.exitnumber = exitnumber;
    }
    public String getPopcorn() {
        return popcorn;
    }
    public void setPopcorn(String popcorn) {
        this.popcorn = popcorn;
    }
    public long getClickedtimes() {
        return clickedtimes;
    }
    public void setClickedtimes(long clickedtimes) {
        this.clickedtimes = clickedtimes;
    }
    public long getCollectedtimes() {
        return collectedtimes;
    }
    public void setCollectedtimes(long collectedtimes) {
        this.collectedtimes = collectedtimes;
    }
    public long getDiaryid() {
        return diaryid;
    }
    public void setDiaryid(long diaryid) {
        this.diaryid = diaryid;
    }
    @Override
    public String toString() {
        return "CinemaDetail [cinemaid=" + cinemaid + ", cinemaname=" + cinemaname
                + ", englishname=" + englishname + ", logo=" + logo + ", citycode=" + citycode
                + ", cityname=" + cityname + ", countycode=" + countycode + ", countyname="
                + countyname + ", indexarea=" + indexarea + ", contactphone=" + contactphone
                + ", address=" + address + ", content=" + content + ", opentime=" + opentime
                + ", pointx=" + pointx + ", pointy=" + pointy + ", bpointx=" + bpointx
                + ", bpointy=" + bpointy + ", generalmark=" + generalmark + ", feature=" + feature
                + ", transport=" + transport + ", linename=" + linename + ", stationname="
                + stationname + ", exitnumber=" + exitnumber + ", popcorn=" + popcorn
                + ", clickedtimes=" + clickedtimes + ", collectedtimes=" + collectedtimes
                + ", diaryid=" + diaryid + "]";
    }
    
}
