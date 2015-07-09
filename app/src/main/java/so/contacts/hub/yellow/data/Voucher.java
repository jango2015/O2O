package so.contacts.hub.yellow.data;

import java.io.Serializable;

/**
 * 优惠券信息
 */
public class Voucher implements Serializable{
	private static final long serialVersionUID = 1L;
	
	public long id;//:[long][not null][代金卷ID]
    public String money;//:[String][not null][价格]
    public String scope;//:[String][not null][适用范围，用#区分,目前只取HF(话费)]
    public String start_time;//:[String][not null][开始时间yyyy-mm-dd hh:mm:ss]
    public String end_time;//:[String][not null][过期时间yyyy-mm-dd hh:mm:ss]
    public String get_time;//:[String][not null][取得时间yyyy-mm-dd hh:mm:ss]
    public int resource_consume;//:[int][not null][0:未消费,1:已消费]
    public String consume_time;//:[String][null able][消费时间 yyyy-mm-dd hh:mm:ss]
    public long activity_id;//:[long][not null][活动ID]
    public int status;//:[int][not null][0:正常,1:过期]
    
    @Override
    public String toString() {
    	// TODO Auto-generated method stub
    	return "[Voucher]id: " + id + " ,money: " + money + " ,resource_consume: " + resource_consume 
    			 + " ,status: " + status;
    }
    /**
     * 优惠券应用场景的枚举类
     * @author lixiaohui
     *
     */
    public static enum VoucherScope {
        Huafei {
            @Override
            public   String value() {
                return "HF";
            }
        },Movie {
            @Override
            public String value() {
                return "MOVIE";
            }
        };
       public  abstract String value();
    }
    
}
