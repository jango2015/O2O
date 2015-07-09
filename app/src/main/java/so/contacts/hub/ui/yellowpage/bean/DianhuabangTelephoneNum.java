package so.contacts.hub.ui.yellowpage.bean;

import java.io.Serializable;

public class DianhuabangTelephoneNum implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String telDesc ;
    private String telFlag ;
    private String telNum ;
    private int telRanking ;//int
    private String telSource ;
    private int telType ;//int
    
    
    public String getTelDesc() {
        return telDesc;
    }
    public void setTelDesc(String telDesc) {
        this.telDesc = telDesc;
    }
    public String getTelFlag() {
        return telFlag;
    }
    public void setTelFlag(String telFlag) {
        this.telFlag = telFlag;
    }
    public String getTelNum() {
        return telNum;
    }
    public void setTelNum(String telNum) {
        this.telNum = telNum;
    }
    public int getTelRanking() {
        return telRanking;
    }
    public void setTelRanking(int telRanking) {
        this.telRanking = telRanking;
    }
    public String getTelSource() {
        return telSource;
    }
    public void setTelSource(String telSource) {
        this.telSource = telSource;
    }
    public int getTelType() {
        return telType;
    }
    public void setTelType(int telType) {
        this.telType = telType;
    }
    
}
