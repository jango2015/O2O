package so.contacts.hub.msgcenter.report;

/**
 * 上报服务器信息
 */
public class MsgReport implements java.io.Serializable {


    private static final long serialVersionUID = 1L;
    
    private int id;
    
    private int type;
        
    private String reportContent;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReportContent() {
        return reportContent;
    }

    public void setReportContent(String reportContent) {
        this.reportContent = reportContent;
    }  
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("MsgReport id=").append(id).append(" type=").append(type).append(" reportContent=").append(reportContent);
        return sb.toString();
    }
}
