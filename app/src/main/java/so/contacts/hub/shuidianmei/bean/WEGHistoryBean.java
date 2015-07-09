
package so.contacts.hub.shuidianmei.bean;

/**
 * 水电煤历史Bean
 * @author ljq
 *
 */
public class WEGHistoryBean {

    public String id;

    /**
     * 订单号
     */
    public String order_no;

    /**
     * 状态码
     */
    public int status_code;
    /**
     * 状态描述（服务器端）
     */
    public String status_des;
    /**
     * 创建时间
     */
    public String c_time;
    /**
     * 充值类型
     */
    public String pay_type;
    /**
     * 产品ID
     */
    public String pro_id;
    /**
     * 账户号
     */
    public String account;
    /**
     * 查询年月 现在暂时为""
     */
    public String yearmonth;
    /**
     * 标价
     */
    public String mark_price;
    /**
     * 售价
     */
    public String sale_price;
    /**
     * 公司
     */
    public String company;
    /**
     * 水电煤类型 1.水2.电3.煤
     */
    public int weg_type;

//  public int charge_state;
    
//  public String product_id;

//  public String worker_status;
    
//  public String bills;

//  public String username;
    
//  public String usernumber;
    
}
