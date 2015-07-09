/**
 * 
 */

package so.contacts.hub.http.bean;

import java.util.List;

/**
 * @author Acher
 */
public class ContactCardInfo {
    public String name;
    
    public String mobile_summary; // [String][not null][名片对应号码摘要]

    public int is_reg; // [int][not null][0:不是葡萄用户,1:是葡萄用户]

    public String addr; // [String][null able][地址 原社交画像]

    public String school; // [String][null able][学校 原社交画像]

    public String company; // [String][null able][公司 原社交画像]
    
    public String job_title;//[String][null able][职位信息]
    
    public String website;//[String][null able][站点信息]

    public String birthday; // [String][null able][生日 原社交画像]

    public long birthday_l; // [long][null able][生日 原社交画像]

    public String email; // [String][null able][邮件 原社交画像]

    public String remark; // [String][null able][描述 原社交画像]

    public List<String> tags; // [List<String>][null able][标签 原社交画像]

    public String approve; // [String][null able][认证信息 原社交画像]

}
