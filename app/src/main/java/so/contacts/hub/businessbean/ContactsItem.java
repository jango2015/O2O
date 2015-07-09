package so.contacts.hub.businessbean;

import java.io.Serializable;

import android.graphics.Bitmap;
import android.text.TextUtils;

public class ContactsItem implements Serializable {

	private static final long serialVersionUID = -4082605385070933975L;

	private int id;
	private String tag; // 当前标签
	private String data1;// 值
	private String data2;// 类型
	private String data3; // 保存地址
	private String data4; // 保存职务
	private Bitmap photo; // 头像
	private String mimetype; // 类型
	private boolean isInsert; // 是否插入
	private boolean isDelete; // 是否删除
	private boolean isUpdate = false; // 是否更新
	private int flag; // 该项表示1 有0 无
	private boolean isprimary = false; // 是否设置默认号码

	public ContactsItem() {
	}

	public ContactsItem(String data1) {
		this.data1 = data1;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getData1() {
		return data1;
	}

	public void setData1(String data1) {
		this.data1 = data1;
	}

	public String getData2() {
		return data2;
	}

	public void setData2(String data2) {
		this.data2 = data2;
	}

	public String getData3() {
		return data3;
	}

	public void setData3(String data3) {
		this.data3 = data3;
	}

	public String getData4() {
		return data4;
	}

	public void setData4(String data4) {
		this.data4 = data4;
	}

	public String getMimetype() {
		if (!TextUtils.isEmpty(mimetype)) {
			if (mimetype.equals("phone")) {
				mimetype = "vnd.android.cursor.item/phone_v2";
			} else if (mimetype.equals("email")) {
				mimetype = "vnd.android.cursor.item/email_v2";
			} else if (mimetype.equals("internet")) {
				mimetype = "vnd.android.cursor.item/im";
			} else if (mimetype.equals("address")) {
				mimetype = "vnd.android.cursor.item/postal-address_v2";
			} else if (mimetype.equals("nickname")) {
				mimetype = "vnd.android.cursor.item/nickname";
			} else if (mimetype.equals("jiguan")) {
				mimetype = "vnd.nine.cursor.item/native";
			} else if (mimetype.equals("decription")) {
				mimetype = "vnd.android.cursor.item/note";
			} else if (mimetype.equals("website")) {
				mimetype = "vnd.android.cursor.item/website";
			} else if (mimetype.equals("group")) {
				mimetype = "vnd.android.cursor.item/group_membership";
			} else if (mimetype.equals("birthday")) {
				mimetype = "vnd.android.cursor.item/contact_event";
			} else if (mimetype.equals("jinianri")) {
				mimetype = "vnd.android.cursor.item/contact_event";
			} else if (mimetype.equals("about_contact")) {
				mimetype = "vnd.nine.cursor.item/about_contact";
			}
		}
		return mimetype;
	}

	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}

	public Bitmap getPhoto() {
		return photo;
	}

	public void setPhoto(Bitmap photo) {
		this.photo = photo;
	}

	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	public boolean isUpdate() {
		return isUpdate;
	}

	public void setUpdate(boolean isUpdate) {
		this.isUpdate = isUpdate;
	}
	
	public boolean isInsert() {
		return isInsert;
	}

	public void setInsert(boolean isInsert) {
		this.isInsert = isInsert;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public boolean isIsprimary() {
		return isprimary;
	}

	public void setIsprimary(boolean isprimary) {
		this.isprimary = isprimary;
	}
}
