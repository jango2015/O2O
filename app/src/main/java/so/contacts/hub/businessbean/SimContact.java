package so.contacts.hub.businessbean;

import java.util.ArrayList;

public class SimContact implements Cloneable {

	private int id;
	private String name;
	private String phone;
	private String email;
	private String sort_key;
	private String name_pinyin;
	// �¼��ֶ�
	private boolean isItemTop = false;
	ArrayList<Integer> matchIndexList;// ��¼ƥ����±�

	public SimContact(boolean isItemTop, String sort_key) {
		this.isItemTop = isItemTop;
		this.sort_key = sort_key;
		this.name_pinyin = "";
	}

	@Override
	public SimContact clone() {
		try {
			return (SimContact) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ArrayList<Integer> getMatchIndexList() {
		return matchIndexList;
	}

	public void setMatchIndexList(ArrayList<Integer> matchIndexList) {
		this.matchIndexList = matchIndexList;
	}

	public SimContact(String name, String phone, String email,
			ArrayList<Integer> arrayList) {
		super();
		this.name = name;
		this.phone = phone;
		this.email = email;
		this.matchIndexList = arrayList;
	}

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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSort_key() {
		return sort_key;
	}

	public void setSort_key(String sort_key) {
		this.sort_key = sort_key;
	}

	public String getName_pinyin() {
		return name_pinyin;
	}

	public void setName_pinyin(String name_pinyin) {
		this.name_pinyin = name_pinyin;
	}

	public boolean isItemTop() {
		return isItemTop;
	}

	public void setItemTop(boolean isItemTop) {
		this.isItemTop = isItemTop;
	}
}
