/**
 * 
 */
package so.contacts.hub.businessbean;

/**
 * @author Acher
 *
 */
public class BaseMatchConfig {
    
    public long app_id;// [long][not null][APPID,用于标识利用哪个厂商的原始匹配接口]
    public int sns_id;//[int][not null][社交域ID,1:新浪,2:腾讯,3:人人]
    
    public BaseMatchConfig() {
        
    }

}
