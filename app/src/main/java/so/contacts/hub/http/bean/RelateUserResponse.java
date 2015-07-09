package so.contacts.hub.http.bean;

import java.io.Serializable;
import java.util.List;

import so.contacts.hub.account.AccountInfo;

public class RelateUserResponse implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    //- 0 表使用设备创建, 1 表使用手机创建, 2 表使用酷派创建
    public static final int SOURCE_DEVICE = 0; 
    public static final int SOURCE_PHONE =  1;
    public static final int SOURCE_FACTORY = 2;
    // 0 表临时用户, 1 表普通绑定用户, 2 表平台绑定用户
    public static final int TYPE_DEVICE = 0;
    public static final int TYPE_PHONE =  1;
    public static final int TYPE_FACTORY = 2;
    
    public String accName;//[String][not null][帐户名]
    public int accSource; //[int][not null][帐户来源] - 0 表使用设备创建, 1 表使用手机创建, 2 表使用酷派创建
    public int accType;   //[int][not null][帐户类型] - 0 表临时用户, 1 表普通绑定用户, 2 表平台绑定用户
    public String accMsg;    //[String][账号信息] //add by putao_lhq
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("RelateUser accName=").append(accName).
        append(" accSource=").append(accSource).
        append(" accType=").append(accType).append(" accMsg=" + accMsg);
        return sb.toString();
    }
}
