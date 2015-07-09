package so.contacts.hub.businessbean;

import java.util.ArrayList;
import java.util.List;

public class BackContactInfo {
    /** MUST exist */
    private String name; // 姓名

    /** 联系人电话信息 */

    private List<PhoneInfo> phoneList = new ArrayList<PhoneInfo>(); // 联系号码

    private List<EmailInfo> email = new ArrayList<EmailInfo>(); // Email

    /**
     * 构造联系人信息
     * 
     * @param name 联系人姓名
     */
    public BackContactInfo(String name) {
        this.name = name;
    }

    /** 姓名 */
    public String getName() {
        return name;
    }

    /** 姓名 */
    public BackContactInfo setName(String name) {
        this.name = name;
        return this;
    }

    /** 联系电话信息 */
    public List<PhoneInfo> getPhoneList() {
        return phoneList;
    }

    /** 联系电话信息 */
    public BackContactInfo setPhoneList(List<PhoneInfo> phoneList) {
        this.phoneList = phoneList;
        return this;
    }

    /** 邮箱信息 */
    public List<EmailInfo> getEmail() {
        return email;
    }

    /** 邮箱信息 */
    public BackContactInfo setEmail(List<EmailInfo> email) {
        this.email = email;
        return this;
    }

    @Override
    public String toString() {
        return "{name: " + name + ", number: " + phoneList + ", email: " + email + "}";
    }
}
