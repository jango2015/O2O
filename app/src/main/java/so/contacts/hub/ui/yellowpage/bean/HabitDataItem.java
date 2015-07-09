package so.contacts.hub.ui.yellowpage.bean;

import java.io.Serializable;

public class HabitDataItem implements Serializable{
    
	private static final long serialVersionUID = 1L;
	
	public static final String LOCAL = "0";
    public static final String UPlOAD = "1";
    public static final String NOT_UPlOAD = "2";
    
    private long id = 0;//[able null][服务器ID]
    
    private String source_type;//[not null][业务模块]
    
    private String content_type;//[not null][数据类型：MOBILE,EMAIL,SFZ,ADDR]
    
    private String content_data;//[not null][数据内容]
    
    private int isupload = Integer.parseInt(LOCAL);//是否上传

    public String getSource_type() {
        return source_type;
    }

    public void setSource_type(String source_type) {
        this.source_type = source_type;
    }

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

    public String getContent_data() {
        return content_data;
    }

    public void setContent_data(String content_data) {
        this.content_data = content_data;
    }

    public int getIsupload() {
        return isupload;
    }

    public void setIsupload(int isupload) {
        this.isupload = isupload;
    }
    
    public long getServiceId() {
        return id;
    }

    public void setServiceId(long service_id) {
        this.id = service_id;
    }

    public HabitDataItem(String source_type, String content_type, String content) {
        super();
        this.source_type = source_type;
        this.content_type = content_type;
        this.content_data = content;
    }

    public HabitDataItem() {
        super();
    }

    @Override
    public String toString() {
        return "HabitDataItem [source_type=" + source_type + ", content_type=" + content_type
                + ", content_data=" + content_data + ", isupload=" + isupload + "]";
    }

    
    
    
    
}
