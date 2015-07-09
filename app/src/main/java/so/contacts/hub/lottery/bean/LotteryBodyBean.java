package so.contacts.hub.lottery.bean;

import java.io.Serializable;

public class LotteryBodyBean implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String period;
    private String type;
    private String open_bonus_time;
    private String bonus;
    private String open_bonus_result;
    private String url;
    
    public String getPeriod() {
        return period;
    }
    public void setPeriod(String period) {
        this.period = period;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getOpen_bonus_time() {
        return open_bonus_time;
    }
    public void setOpen_bonus_time(String open_bonus_time) {
        this.open_bonus_time = open_bonus_time;
    }
    public String getBonus() {
        return bonus;
    }
    public void setBonus(String bonus) {
        this.bonus = bonus;
    }
    public String getOpen_bonus_result() {
        return open_bonus_result;
    }
    public void setOpen_bonus_result(String open_bonus_result) {
        this.open_bonus_result = open_bonus_result;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("period=").append(period)
        .append("type=").append(type)
        .append("open_bonus_time=").append(open_bonus_time)
        .append("bonus=").append(bonus)
        .append("open_bonus_result=").append(open_bonus_result)
        .append("url=").append(url);
        
        return sb.toString();
    }
}
