package so.contacts.hub.thirdparty.cinema.bean;

import java.sql.Timestamp;
import java.util.Date;

/**
 * 订单view的been
 * @author peku
 *
 */
public class SimpleMovieOrder {
	
	private String order_title; //订单标题
	
	private String ticket_code; //取票码
	
	private Timestamp play_time; //播放时间
	
	private String seat; //座位
	
	private int status; //订单状态
	
	private String paid_amount; //支付金额
	
	private String order_no; //订单号

	public String getOrderTitle() {
		return order_title;
	}

	public void setOrderTitle(String orderTitle) {
		this.order_title = orderTitle;
	}

	public String getTicketCode() {
		return ticket_code;
	}

	public void setTicketCode(String ticketCode) {
		this.ticket_code = ticketCode;
	}

	public Timestamp getPlayTime() {
		return play_time;
	}

	public void setPlayTime(Timestamp playTime) {
		this.play_time = playTime;
	}

	public String getSeat() {
		return seat;
	}

	public void setSeat(String seat) {
		this.seat = seat;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getPaidAmount() {
		return paid_amount;
	}

	public void setPaidAmount(String paidAmount) {
		this.paid_amount = paidAmount;
	}

	public String getTradeno() {
		return order_no;
	}

	public void setTradeno(String tradeno) {
		this.order_no = tradeno;
	}

	
}
