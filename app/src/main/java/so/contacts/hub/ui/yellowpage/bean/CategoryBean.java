package so.contacts.hub.ui.yellowpage.bean;

public class CategoryBean implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private long category_id;
	private String name;
	private long parent_id;
	private String show_name;
	private String icon;
	private String iconLogo;
	private String pressIcon; // 按下效果
	private int sort;
	private int lastSort; // 所有的项在 "全部"里的顺序
	private String target_activity;
	private String target_params;
	private String tagIcon; // 打点(hot)
    private int remind_code; // 对应打点服务节点
    private String expand_param;// add by lisheng  增加category表扩展字段;
    private String key_tag; // 关键字标签
    private int search_sort;// 关键字标签排序

	/**
	 * 编辑类型，通过该字段可扩展更多功能
	 * editType = 0：默认值
	 * editType = 1：不可删除
	 * editType = 2：用户添加类型
	 */
	private int editType = 0;

	private int action;// 0-update 1-insert 2-delete
	
	/**
     * 被改变类型
     * change_type = 0：默认值
     * change_type = 1：由用户改变
     * change_type = 2：由服务器改变
     */
	private int change_type = 0;
	

	public int getChange_type() {
        return change_type;
    }

    public void setChange_type(int change_type) {
        this.change_type = change_type;
    }

    public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

	public long getCategory_id() {
		return category_id;
	}

	public void setCategory_id(long category_id) {
		this.category_id = category_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getParent_id() {
		return parent_id;
	}

	public void setParent_id(long parent_id) {
		this.parent_id = parent_id;
	}

	public String getShow_name() {
		return show_name;
	}

	public void setShow_name(String show_name) {
		this.show_name = show_name;
	}
	
	public String getIconLogo(){
		return iconLogo;
	}
	
	public void setIconLogo(String iconLogo){
		this.iconLogo = iconLogo;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public int getLastSort() {
		return lastSort;
	}

	public void setLastSort(int lastSort) {
		this.lastSort = lastSort;
	}

	public String getTarget_activity() {
		return target_activity;
	}

	public void setTarget_activity(String target_activity) {
		this.target_activity = target_activity;
	}

	public String getTarget_params() {
		return target_params;
	}

	public void setTarget_params(String target_params) {
		this.target_params = target_params;
	}

	public String getTagIcon() {
		return tagIcon;
	}

	public void setTagIcon(String tagIcon) {
		this.tagIcon = tagIcon;
	}

	public String getPressIcon() {
		return pressIcon;
	}

	public void setPressIcon(String pressIcon) {
		this.pressIcon = pressIcon;
	}

	public void setEditType(int editType){
		this.editType = editType;
	}
	
	public int getEditType(){
		return editType;
	}

    public int getRemind_code() {
        return remind_code;
    }

    public void setRemind_code(int remind_code) {
        this.remind_code = remind_code;
    }

    
    public String getExpand_param() {
		return expand_param;
	}

	public void setExpand_param(String expand_param) {
		this.expand_param = expand_param;
	}

	public String getKey_tag() {
		return key_tag;
	}

	public void setKey_tag(String key_tag) {
		this.key_tag = key_tag;
	}

	public int getSearch_sort() {
        return search_sort;
    }

    public void setSearch_sort(int search_sort) {
        this.search_sort = search_sort;
    }

    @Override
	public String toString() {
		return "CategoryBean [category_id=" + category_id + ", name=" + name
				+ ", parent_id=" + parent_id + ", show_name=" + show_name
				+ ", icon=" + icon + ", iconLogo=" + iconLogo + ", pressIcon="
				+ pressIcon + ", sort=" + sort + ", lastSort=" + lastSort
				+ ", target_activity=" + target_activity + ", target_params="
				+ target_params + ", remind_code=" + remind_code 
				+ ", expand_param=" + expand_param + " ,key_tag=" + key_tag
				+ ", editType=" + editType + ", action=" + action
				+ ", change_type=" + change_type + "]";
	}

	
	
}
