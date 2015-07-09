package so.contacts.hub.thirdparty.cinema.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class OpenPlayItem implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 4035627958447580449L;
    // 场次ID
    private Long mpid;
    // 影片ID
    private Long movieid;
    // 影院ID
    private Long cinemaid;
    // 影院名称
    private String cinemaname;
    // 影片名称
    private String moviename;
    // 影片语言（中文/英文等）
    private String language;
    // 场次版本（2D,3D,IMAX等）
    private String edition;
    // 影厅ID
    private Long roomid;
    // 影厅名称
    private String roomname;
    // 影厅类型（大厅,中厅等）
    private String roomtype;
    // 放映时间
    private Date playtime;
    // 场次关闭售票时间
    private Date closetime;
    // 场次更新时间
    private Date updatetime;
    // 影院价格
    private Integer price;
    // 格瓦拉卖价（该价格已含服务费）
    private Integer gewaprice;
    // 该场次提供锁座的时间
    private Integer lockminute;
    // 该场次最大购票座位数
    private Integer maxseat;
    // 格瓦拉服务费
    private Integer servicefee;
    // 备注
    private String remark;
    
    // 场次座位信息,场次座位接口用
    private List<CinemaRoomRowSeat> seatList;
    // 已锁的位置，场次锁定座位信息接口用
    private String lockedseat;
    
    public Long getMpid() {
        return mpid;
    }

    public void setMpid(Long mpid) {
        this.mpid = mpid;
    }

    public Long getMovieid() {
        return movieid;
    }

    public void setMovieid(Long movieid) {
        this.movieid = movieid;
    }

    public Long getCinemaid() {
        return cinemaid;
    }

    public void setCinemaid(Long cinemaid) {
        this.cinemaid = cinemaid;
    }

    public String getCinemaname() {
        return cinemaname;
    }

    public void setCinemaname(String cinemaname) {
        this.cinemaname = cinemaname;
    }

    public String getMoviename() {
        return moviename;
    }

    public void setMoviename(String moviename) {
        this.moviename = moviename;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public Long getRoomid() {
        return roomid;
    }

    public void setRoomid(Long roomid) {
        this.roomid = roomid;
    }

    public String getRoomname() {
        return roomname;
    }

    public void setRoomname(String roomname) {
        this.roomname = roomname;
    }

    public Date getPlaytime() {
        return playtime;
    }

    public void setPlaytime(Date playtime) {
        this.playtime = playtime;
    }

    public Date getClosetime() {
        return closetime;
    }

    public void setClosetime(Date closetime) {
        this.closetime = closetime;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getGewaprice() {
        return gewaprice;
    }

    public void setGewaprice(Integer gewaprice) {
        this.gewaprice = gewaprice;
    }

    public Integer getLockminute() {
        return lockminute;
    }

    public void setLockminute(Integer lockminute) {
        this.lockminute = lockminute;
    }

    public Integer getMaxseat() {
        return maxseat;
    }

    public void setMaxseat(Integer maxseat) {
        this.maxseat = maxseat;
    }

    public Integer getServicefee() {
        return servicefee;
    }

    public void setServicefee(Integer servicefee) {
        this.servicefee = servicefee;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRoomtype() {
        return roomtype;
    }

    public void setRoomtype(String roomtype) {
        this.roomtype = roomtype;
    }

    public List<CinemaRoomRowSeat> getSeatList() {
        return seatList;
    }

    public void setSeatList(List<CinemaRoomRowSeat> seatList) {
        this.seatList = seatList;
    }

    public String getLockedseat() {
        return lockedseat;
    }

    public void setLockedseat(String lockedseat) {
        this.lockedseat = lockedseat;
    }

    @Override
    public String toString() {
        return "OpenPlayItem [mpid=" + mpid + ", movieid=" + movieid + ", cinemaid=" + cinemaid
                + ", cinemaname=" + cinemaname + ", moviename=" + moviename + ", language="
                + language + ", edition=" + edition + ", roomid=" + roomid + ", roomname="
                + roomname + ", roomtype=" + roomtype + ", playtime=" + playtime + ", closetime="
                + closetime + ", updatetime=" + updatetime + ", price=" + price + ", gewaprice="
                + gewaprice + ", lockminute=" + lockminute + ", maxseat=" + maxseat
                + ", servicefee=" + servicefee + ", remark=" + remark + ", seatList=" + seatList
                + ", lockedseat=" + lockedseat + "]";
    }
    
}
