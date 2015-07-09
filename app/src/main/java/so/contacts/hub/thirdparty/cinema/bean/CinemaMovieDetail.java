package so.contacts.hub.thirdparty.cinema.bean;

import java.io.Serializable;
import java.util.Date;

/**
 *@author ffh
 *@since 2014/12/17
 *影片详情
 */
public class CinemaMovieDetail implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private long movieid;//影片id
    private String moviename;//影片名称
    private String englishname;//影片英文名称
    private String language;//语言 ext:中文
    private String type;//影片类型
    private String state;//出产地区
    private String director;// 导演
    private String actors;//主演
    private String length;//片长
    private String highlight;//一句话影评
    private Date releasedate;//电影的首映日期 ext:2013-12-01
    private String logo;//影片logo
    private String content;//电影详情介绍
    private String imdbid;//imdbid
    private String minprice;//最低票价
    private long collectedtimes;//关注数
    private long clickedtimes;//感兴趣数
    private String generalmark;//影片评分
    private String gcedition;//影片版本 ext:IMAX3D
    
    
    public long getMovieid() {
        return movieid;
    }
    public void setMovieid(long movieid) {
        this.movieid = movieid;
    }
    public String getMoviename() {
        return moviename;
    }
    public void setMoviename(String moviename) {
        this.moviename = moviename;
    }
    public String getEnglishname() {
        return englishname;
    }
    public void setEnglishname(String englishname) {
        this.englishname = englishname;
    }
    public String getLanguage() {
        return language;
    }
    public void setLanguage(String language) {
        this.language = language;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public String getDirector() {
        return director;
    }
    public void setDirector(String director) {
        this.director = director;
    }
    public String getActors() {
        return actors;
    }
    public void setActors(String actors) {
        this.actors = actors;
    }
    public String getLength() {
        return length;
    }
    public void setLength(String length) {
        this.length = length;
    }
    public String getHighlight() {
        return highlight;
    }
    public void setHighlight(String highlight) {
        this.highlight = highlight;
    }
    public Date getReleasedate() {
        return releasedate;
    }
    public void setReleasedate(Date releasedate) {
        this.releasedate = releasedate;
    }
    public String getLogo() {
        return logo;
    }
    public void setLogo(String logo) {
        this.logo = logo;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getImdbid() {
        return imdbid;
    }
    public void setImdbid(String imdbid) {
        this.imdbid = imdbid;
    }
    public String getMinprice() {
        return minprice;
    }
    public void setMinprice(String minprice) {
        this.minprice = minprice;
    }
    public long getCollectedtimes() {
        return collectedtimes;
    }
    public void setCollectedtimes(long collectedtimes) {
        this.collectedtimes = collectedtimes;
    }
    public long getClickedtimes() {
        return clickedtimes;
    }
    public void setClickedtimes(long clickedtimes) {
        this.clickedtimes = clickedtimes;
    }
    public String getGeneralmark() {
        return generalmark;
    }
    public void setGeneralmark(String generalmark) {
        this.generalmark = generalmark;
    }
    public String getGcedition() {
        return gcedition;
    }
    public void setGcedition(String gcedition) {
        this.gcedition = gcedition;
    }
    public static long getSerialversionuid() {
        return serialVersionUID;
    }
    @Override
    public String toString() {
        return "CinemaMovieDetail [movieid=" + movieid + ", moviename=" + moviename
                + ", englishname=" + englishname + ", language=" + language + ", type=" + type
                + ", state=" + state + ", director=" + director + ", actors=" + actors
                + ", length=" + length + ", highlight=" + highlight + ", releasedate="
                + releasedate + ", logo=" + logo + ", content=" + content + ", imdbid=" + imdbid
                + ", minprice=" + minprice + ", collectedtimes=" + collectedtimes
                + ", clickedtimes=" + clickedtimes + ", generalmark=" + generalmark
                + ", gcedition=" + gcedition + "]";
    }
    
}
