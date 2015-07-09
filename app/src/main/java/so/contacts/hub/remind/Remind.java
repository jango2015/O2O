package so.contacts.hub.remind;

import java.io.Serializable;

/**
 * @author change
 *
 */
public class Remind implements Serializable{
    private int remind_code;  // [int][not null][提醒码：1:游戏中心]
    private int type;         // [int][not null][提醒类型,1:查看消除打点,2:点击消除打点,3:查看消除数字,4:点击消除数字]
    private int style;
    private String logo;      // [String][null able][提醒logo标示]
    private String text;      // 打点显示文字
    private long endTime;     // 时间消除类型的截止时间
    
    private String expand_param; // [String][null able][提醒额外参数]
    
    public int getRemind_code() {
        return remind_code;
    }
    public void setRemind_code(int remind_code) {
        this.remind_code = remind_code;
    }
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public String getLogo() {
        return logo;
    }
    public void setLogo(String logo) {
        this.logo = logo;
    }
    public String getExpand_param() {
        return expand_param;
    }
    public void setExpand_param(String expand_param) {
        this.expand_param = expand_param;
    }
    
    public int getStyle() {
        return style;
    }
    public void setStyle(int style) {
        this.style = style;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public long getEndTime() {
        return endTime;
    }
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(" remind_code=").append(remind_code)
          .append(" type=").append(type)
          .append(" logo=").append(logo)
          .append(" expand_param=").append(expand_param);
        
        return sb.toString();
    }
}
