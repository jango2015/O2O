package so.contacts.hub.ui.yellowpage.bean;

public class SearchProvider implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	private int id;
    private String name;
    private int entryType;
    private int status;
    private String serviceName;
    // add by putao_lhq 2014年10月10日 for 远程配置搜索顺序 start
    private int action;
    public int getAction() {
    	return action;
    }
    public void setAction(int action) {
    	this.action = action;
    }
    // add by putao_lhq 2014年10月10日 for 远程配置搜索顺序 end
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getEntryType() {
        return entryType;
    }
    public void setEntryType(int entryType) {
        this.entryType = entryType;
    }
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public String getServiceName() {
        return serviceName;
    }
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Provider id: ").append(id).append(" name:").append(name)
        .append(" entryType:").append(entryType).append(" status:").append(status)
        .append(" serviceName:").append(serviceName).append("action: " + action);
        
        return sb.toString();
    }    
}
