package so.contacts.hub.ui.yellowpage.bean;


public class SearchConfigBean implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	private int id;
    private String keyword;
    private int entry;
    private int schdule;
    
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
    	sb.append(",keyword: " + keyword);
    	sb.append(",entry: " + entry);
    	sb.append(",schdule: " + schdule);
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
    public String getKeyword() {
        return keyword;
    }
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    public int getEntry() {
        return entry;
    }
    public void setEntry(int entry) {
        this.entry = entry;
    }
    public int getSchdule() {
        return schdule;
    }
    public void setSchdule(int schdule) {
        this.schdule = schdule;
    }

    
}
