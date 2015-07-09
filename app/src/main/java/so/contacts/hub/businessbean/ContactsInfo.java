package so.contacts.hub.businessbean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.text.TextUtils;

public class ContactsInfo implements Serializable, Cloneable{
	
	private static final long serialVersionUID = -2126795994134058556L;
	private AccountInfo accountInfo;
	private int index_in_sim = -1;
	private int indicate_phone_or_sim_contact = -1;

	private String id="0";  //ID 
	private int type;  //联系人类型 0私有1商家
	private int shopStatus=0;    //0 状态保持不变 1商家---->私有联系人   2私有联系人-->商家
	private ContactsItem nameItem=new ContactsItem();		//姓名
	private ContactsItem photoItem=new ContactsItem();   //Bitmap photo; //头像
	private ContactsItem sexItem=new ContactsItem();     //性别
	private ContactsItem ringItem=new ContactsItem();	   //铃声 ring ringUrl
	private ContactsItem birthDay=new ContactsItem();    //生日
	private List<ContactsItem> phone_list=new ArrayList<ContactsItem>();  //电话
	private List<ContactsItem> email_list=new ArrayList<ContactsItem>();  //email
	private List<ContactsItem> organization_list=new ArrayList<ContactsItem>();   //组织
	private List<ContactsItem> website_list=new ArrayList<ContactsItem>();    //网站
	private List<ContactsItem> internet_list=new ArrayList<ContactsItem>();   //网络账号
	private List<ContactsItem> address_list=new ArrayList<ContactsItem>();    //地址
	private List<ContactsItem> nickname_list=new ArrayList<ContactsItem>();   //nickname
	private List<ContactsItem> jiguan_list=new ArrayList<ContactsItem>();     //籍贯
	private List<ContactsItem> jinianri_list=new ArrayList<ContactsItem>();   //纪念日
	private List<ContactsItem> description_list=new ArrayList<ContactsItem>(); //备注
	private List<ContactsItem> contact_list=new ArrayList<ContactsItem>();     //相关联系人
//	private List<ContactsItem> business_list=new ArrayList<ContactsItem>();    //商家信息
	private Map<String,ContactsItem> group_map=new HashMap<String,ContactsItem>();      //分组信息
	private boolean groupUpdate;
	private int starred;    //0 未收藏 1收藏

	public int getIndex_in_sim() {
        return index_in_sim;
    }

    public void setIndex_in_sim(int index_in_sim) {
        this.index_in_sim = index_in_sim;
    }

    public int getIndicate_phone_or_sim_contact() {
        return indicate_phone_or_sim_contact;
    }

    public void setIndicate_phone_or_sim_contact(int indicate_phone_or_sim_contact) {
        this.indicate_phone_or_sim_contact = indicate_phone_or_sim_contact;
    }

    public AccountInfo getAccountInfo() {
        return accountInfo;
    }

    public void setAccountInfo(AccountInfo accountInfo) {
        this.accountInfo = accountInfo;
    }
	public String getId() {
		return id;
	}
	public ContactsItem getNameItem() {
		return nameItem;
	}
	public void setNameItem(ContactsItem nameItem) {
		this.nameItem = nameItem;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getShopStatus() {
		return shopStatus;
	}
	public void setShopStatus(int shopStatus) {
		this.shopStatus = shopStatus;
	}
	public ContactsItem getPhotoItem() {
		return photoItem;
	}
	public void setPhotoItem(ContactsItem photoItem) {
		this.photoItem = photoItem;
	}
	public ContactsItem getRingItem() {
		return ringItem;
	}
	public void setRingItem(ContactsItem ringItem) {
		this.ringItem = ringItem;
	}
	public ContactsItem getSexItem() {
		return sexItem;
	}
	public void setSexItem(ContactsItem sexItem) {
		this.sexItem = sexItem;
	}
	public boolean isGroupUpdate() {
		return groupUpdate;
	}
	public void setGroupUpdate(boolean groupUpdate) {
		this.groupUpdate = groupUpdate;
	}
	public List<ContactsItem> getPhone_list() {
		return phone_list;
	}
	public void setPhone_list(List<ContactsItem> phone_list) {
		this.phone_list = phone_list;
	}
	public List<ContactsItem> getContact_list() {
		return contact_list;
	}
	public void setContact_list(List<ContactsItem> contact_list) {
		this.contact_list = contact_list;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getStarred() {
		return starred;
	}
	public void setStarred(int starred) {
		this.starred = starred;
	}
	public ContactsItem getBirthDay() {
		return birthDay;
	}
	public void setBirthDay(ContactsItem birthDay) {
		this.birthDay = birthDay;
	}
	public List<ContactsItem> getEmail_list() {
		return email_list;
	}
	public void setEmail_list(List<ContactsItem> email_list) {
		this.email_list = email_list;
	}
	public List<ContactsItem> getOrganization_list() {
		return organization_list;
	}
	public void setOrganization_list(List<ContactsItem> organization_list) {
		this.organization_list = organization_list;
	}
	public List<ContactsItem> getWebsite_list() {
		return website_list;
	}
	public void setWebsite_list(List<ContactsItem> website_list) {
		this.website_list = website_list;
	}
	public List<ContactsItem> getInternet_list() {
		return internet_list;
	}
	public void setInternet_list(List<ContactsItem> internet_list) {
		this.internet_list = internet_list;
	}
	public List<ContactsItem> getAddress_list() {
		return address_list;
	}
	public void setAddress_list(List<ContactsItem> address_list) {
		this.address_list = address_list;
	}
	public List<ContactsItem> getNickname_list() {
		return nickname_list;
	}
	public void setNickname_list(List<ContactsItem> nickname_list) {
		this.nickname_list = nickname_list;
	}
	public List<ContactsItem> getJiguan_list() {
		return jiguan_list;
	}
	public void setJiguan_list(List<ContactsItem> jiguan_list) {
		this.jiguan_list = jiguan_list;
	}
	public List<ContactsItem> getJinianri_list() {
		return jinianri_list;
	}
	public void setJinianri_list(List<ContactsItem> jinianri_list) {
		this.jinianri_list = jinianri_list;
	}
	public List<ContactsItem> getDescription_list() {
		return description_list;
	}
	public void setDescription_list(List<ContactsItem> description_list) {
		this.description_list = description_list;
	}
	public Map<String, ContactsItem> getGroup_map() {
		return group_map;
	}
	public void setGroup_map(Map<String, ContactsItem> group_map) {
		this.group_map = group_map;
	}
	@Override
	public String toString() {
		StringBuffer str=new StringBuffer();
		str.append("姓名:").append(this.getNameItem().getData1()).append(";");
		if(!TextUtils.isEmpty(sexItem.getData1())){
			str.append("性别:").append(sexItem.getData1());
			str.append(";");
		}
		for(ContactsItem item:phone_list){
			str.append(item.getTag()).append(":");
			str.append(item.getData1());
			str.append(";");
		}
		for(ContactsItem item:email_list){
			str.append(item.getTag()).append(":");
			str.append(item.getData1());
			str.append(";");
		}
		for(ContactsItem item:website_list){
			str.append("网址:");
			str.append(item.getData1());
			str.append(";");
		}
		for(ContactsItem item:internet_list){
			str.append(item.getTag()).append(":");
			str.append(item.getData1());
			str.append(";");
		}
		for(ContactsItem item:address_list){
			str.append(item.getTag()).append(":");
			str.append(item.getData1());
			str.append(";");
		}
		for(ContactsItem item:nickname_list){
			str.append("昵称:");
			str.append(item.getData1());
		}
		for(ContactsItem item:jiguan_list){
			str.append("机关:");
			str.append(item.getData1());
			str.append(";");
		}
		if(!TextUtils.isEmpty(birthDay.getData1())){
			str.append("生日:");
			str.append(birthDay.getData1());
			str.append(";");
		}
		for(ContactsItem item:jinianri_list){
			str.append("纪念日:");
			str.append(item.getData1());
			str.append(";");
		}
		for(ContactsItem item:organization_list){
			if("0".equals(item.getData2())){
				str.append("公司:");
			}else if("1".equals(item.getData2())){
				str.append("组织:");
			}else if("0".equals(item.getData2())){
				str.append(item.getTag()+":");
			}
			str.append(item.getData1());
			str.append(";");
		}
		for(ContactsItem item:description_list){
			str.append("备注:");
			str.append(item.getData1());
			str.append(";");
		}
		return str.toString();
	}
	
	public static Object depthClone(Object srcObj){   
	       Object cloneObj = null;   
	       try {   
	            ByteArrayOutputStream out = new ByteArrayOutputStream();   
	           ObjectOutputStream oo = new ObjectOutputStream(out);   
	          oo.writeObject(srcObj);   
	             
	           ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());   
	           ObjectInputStream oi = new ObjectInputStream(in);   
	           cloneObj = oi.readObject();            
	       } catch (IOException e) {   
	           e.printStackTrace();   
	       } catch (ClassNotFoundException e) {   
	          e.printStackTrace();   
	        }   
	       return cloneObj;   
	 }  
}
