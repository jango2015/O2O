package so.contacts.hub.push.bean;

import java.io.Serializable;

/**
 * Push消息-打点数据
 * @author change
 *
 */
public class PushRemindBean implements Serializable{
    private int type;
    private int code;
    private int style;
    private String text;

    private String subCode;
    private long time;
    private String img_url;
    
    public long getTime() {
        return time;
    }
    public void setTime(long time) {
        this.time = time;
    }
    public String getImg_url() {
        return img_url;
    }
    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }
    public String getSubCode() {
        return subCode;
    }
    public void setSubCode(String subCode) {
        this.subCode = subCode;
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

}
