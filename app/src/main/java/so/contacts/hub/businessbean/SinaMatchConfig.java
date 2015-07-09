/**
 * 
 */

package so.contacts.hub.businessbean;

/**
 * @author Acher
 */
public class SinaMatchConfig extends BaseMatchConfig {
    
    public String dev_model;// [String][not null][设备型号,用于拼凑UA信息]

    public String app_name;// [String][not null][应用名字,用于拼凑UA信息]

    public String app_version;// [String][not null][应用版本好,用于拼凑UA信息]

    public String match_face_status;// [String][not null][匹配标示，用于生成加密签名]

    public String source;// [String][not null][ 匹配源时间标示，用于生成加密签名]

    public int max_match_phone;// [int][not null][单次调用接口最大匹配号码数]
    
    public SinaMatchConfig() {
        
    }

}
