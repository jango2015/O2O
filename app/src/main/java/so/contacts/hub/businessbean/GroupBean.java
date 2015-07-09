package so.contacts.hub.businessbean;

import java.io.Serializable;

public class GroupBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String group_name;
	private long group_id;

	public GroupBean(String group_name, long group_id) {
		this.group_name = group_name;
		this.group_id = group_id;
	}

	public String getGroup_name() {
		return group_name;
	}

	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}

	public long getGroup_id() {
		return group_id;
	}

	public void setGroup_id(long group_id) {
		this.group_id = group_id;
	}
}
