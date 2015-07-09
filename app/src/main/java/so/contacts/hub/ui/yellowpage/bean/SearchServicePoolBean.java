package so.contacts.hub.ui.yellowpage.bean;

public class SearchServicePoolBean implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
    private int configId;
    private int pid;
    private int sort;
    private String searchInfo;
    // add by putao_lhq 2014年10月10日 for 远程配置搜索顺序 start
    private int action;
    
    public int getAction() {
    	return action;
    }
    
    public void setAction(int action) {
    	this.action = action;
    }
    
    @Override
    public String toString() {
    	StringBuffer sb = new StringBuffer();
    	sb.append("id: " + id);
    	sb.append(",configId: " + configId);
    	sb.append(",pid: " + pid);
    	sb.append(",sort: " + sort);
    	sb.append(",searchInfo: " + searchInfo);
    	sb.append(",action: " + action);
    	return sb.toString();
    }
    // add by putao_lhq 2014年10月10日 for 远程配置搜索顺序 end
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getConfigId() {
        return configId;
    }
    public void setConfigId(int configId) {
        this.configId = configId;
    }
    public int getPid() {
        return pid;
    }
    public void setPid(int pid) {
        this.pid = pid;
    }
    public int getSort() {
        return sort;
    }
    public void setSort(int sort) {
        this.sort = sort;
    }
    public String getSearchInfo() {
        return searchInfo;
    }
    public void setSearchInfo(String searchInfo) {
        this.searchInfo = searchInfo;
    }
}
