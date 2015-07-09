package so.contacts.hub.lottery.bean;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import so.contacts.hub.util.CalendarUtil;

public class LotteryResultBean implements Serializable {
	private long id;
	private String server_order_no;
	private int action;
	private String subject;//主题
	private String body;
	private LotteryBodyBean bodyBean =null;//彩票订单的详情
	private String order_price;
	private int status;
	private long ctime;//订单创建时间
	private long utime;//订单更新时间
	private int deal_status;
	private String s_deal_status;
    static HashMap<Integer,String> deal_statusMap;
    private int order_status;//彩票状态;
	private String expand_status;//彩票状态;
	private String order_refund_url;
	private String order_no;
	private String pt_u_id;
	private Date c_time;
	private Date m_time;
	
	static{
	    deal_statusMap = new HashMap<Integer,String>();
	    deal_statusMap.put(0, "订单创建");
	    deal_statusMap.put(1, "订单付款");
	    deal_statusMap.put(2, "付款成功");
	    deal_statusMap.put(3, "请求退款");
	    deal_statusMap.put(4, "退款成功");
	    deal_statusMap.put(5, "退款失败");
	    deal_statusMap.put(6, "订单完成");
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getServer_order_no() {
		return server_order_no;
	}
	public void setServer_order_no(String server_order_no) {
		this.server_order_no = server_order_no;
	}
	public int getAction() {
		return action;
	}
	public void setAction(int action) {
		this.action = action;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getBody() {
        return body;
    }
    public void setBody(String body) {
        this.body = body;
    }
    public LotteryBodyBean getBodyBean() {
        if(null == bodyBean && this.getBody() != null){
            try {
				bodyBean = new Gson().fromJson(this.getBody(), LotteryBodyBean.class);
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
			}
        }
        return bodyBean;
    }
    public void setBodyBean(LotteryBodyBean bodyBean) {
        this.bodyBean = bodyBean;
    }
    public String getOrder_price() {
		return order_price;
	}
	public void setOrder_price(String order_price) {
		this.order_price = order_price;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public long getCtime() {
		return ctime;
	}
	public void setCtime(long ctime) {
		this.ctime = ctime;
	}
	public long getUtime() {
		return utime;
	}
	public void setUtime(long utime) {
		this.utime = utime;
	}
	public int getDeal_status() {
		return deal_status;
	}
	public void setDeal_status(int deal_status) {
		this.deal_status = deal_status;
	}
	public String getS_deal_status() {
	    return deal_statusMap.get(deal_status);
    }
    public String getExpand_status() {
		return expand_status;
	}
	public void setExpand_status(String expand_status) {
		this.expand_status = expand_status;
	}
	public String getOrder_refund_url() {
		return order_refund_url;
	}
	public void setOrder_refund_url(String order_refund_url) {
		this.order_refund_url = order_refund_url;
	}
	public String getOrder_no() {
		return order_no;
	}
	public void setOrder_no(String order_no) {
		this.order_no = order_no;
	}
	public String getPt_u_id() {
		return pt_u_id;
	}
	public void setPt_u_id(String pt_u_id) {
		this.pt_u_id = pt_u_id;
	}
	public Date getC_time() {
	    if(null == c_time){
	        c_time = new Date(ctime);
	    }
		return c_time;
	}
	public void setC_time(Date c_time) {
		this.c_time = c_time;
	}
	public Date getM_time() {
		return m_time;
	}
	public void setM_time(Date m_time) {
		this.m_time = m_time;
	}
	
	
	public int getOrder_status() {
        return order_status;
    }
    public void setOrder_status(int order_status) {
        this.order_status = order_status;
    }
    @Override
	public String toString() {
		return "LotteryResultBean [id=" + id + ", server_order_no="
				+ server_order_no + ", action=" + action + ", subject="
				+ subject + ", body=[" + body.toString() + "], order_price=" + order_price
				+ ", status=" + status + ", ctime=" + ctime + ", utime="
				+ utime + ", deal_status=" + deal_status + ", expand_status="
				+ expand_status + ", order_refund_url=" + order_refund_url
				+ ", order_no=" + order_no + ", pt_u_id=" + pt_u_id
				+ ", c_time=" + c_time + ", m_time=" + m_time + "]";
	}
	
	
	
}
