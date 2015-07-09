package so.contacts.hub.ui.yellowpage.bean;

public class ItemBean extends CategoryBean{

	private static final long serialVersionUID = 1L;
	
	private long item_id;
	private int provider;
	private String description;
	private String content;
	
	public long getItem_id() {
		return item_id;
	}
	public void setItem_id(long item_id) {
		this.item_id = item_id;
	}
	public int getProvider() {
		return provider;
	}
	public void setProvider(int provider) {
		this.provider = provider;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
    @Override
    public String toString() {
        return "ItemBean [item_id=" + item_id + ", provider=" + provider + ", description="
                + description + ", content=" + content + "]" + super.toString() ;
    }
	
	
	
}
