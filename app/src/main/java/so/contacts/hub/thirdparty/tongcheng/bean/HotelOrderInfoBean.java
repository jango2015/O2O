package so.contacts.hub.thirdparty.tongcheng.bean;

/**
 * 黄页-订单列表中 酒店项 信息
 * 
 * @author Michael
 * 
 */
public class HotelOrderInfoBean extends TC_BaseData {

	private static final long serialVersionUID = 1L;

	private long id; // 主键
	private String pt_u_id; // 葡萄用户id
	private String order_no; // 葡萄订单号
	private String hotel_order_no; // 订单流水号
	private int order_status; // 订单状态, 6:暂存单 5:酒店确认未住 4:已成功订单 3:同程已确认(已通知酒店)
								// 2:已取消 1:新建
	private int platform_id; // 平台id
	private String checkin_time; // 入住时间
	private String checkout_time; // 离店时间
	private String arrive_time; // 到店时间
	private int hotel_id; // 酒店id
	private String hotel_name; // 酒店名称
	private String pic_url; //酒店图片
	private String hotel_address; // 酒店地址
	private String hotel_tel; // 酒店电话
	private String room_name; // 房型名称
	private int room_quantity; // 房间数量
	private String booking_name; // 预定人姓名
	private String booking_mobile; // 预定人手机
	private String guest_name; // 入住人姓名
	private String guest_mobile; // 入住人手机
	private int order_type; // 预定类型
	private double order_amount; // 订单金额
	private double pay_amount; // 订单金额
	private double guarantee_amount; // 担保金额
	private double prize_amount; // 奖金总额
	private double hone_voucher_re_deposit; // 现金卷返回奖金
	private int enable_cancel; // 是否可取消, 0不可取消 1可以取消

	// 详情字段
	private String ctip_order_id; // 携程订单号
	private String confirm_time; // 核房时间
	private String create_time; // 创建时间
	private int is_regret; // 2是否是不确定单, 0是正常 1是不确定
	private String uncertain_reason; // 不确定的原因
	private int cash_back_status; // 是否可提现, 0不可 1全额 2部分
	private String cash_back_reason; // 不可提现原因
	private double cash_back_price; // 可提现金额
	private int cash_back_apply_status; // 是否可以申请提现

	private int notify_status; // 通知状态
	private String c_time;
	private String m_time;
    private long receiveMsgTime; //订单消息更新时间
    
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPt_u_id() {
		return pt_u_id;
	}

	public void setPt_u_id(String pt_u_id) {
		this.pt_u_id = pt_u_id;
	}

	public String getPt_order_no() {
		return order_no;
	}

	public void setPt_order_no(String order_no) {
		this.order_no = order_no;
	}
	
	public String getHotel_order_no() {
		return hotel_order_no;
	}

	public void setHotel_order_no(String hotel_order_no) {
		this.hotel_order_no = hotel_order_no;
	}

	public int getOrder_status() {
		return order_status;
	}

	public void setOrder_status(int order_status) {
		this.order_status = order_status;
	}

	public int getPlatform_id() {
		return platform_id;
	}

	public void setPlatform_id(int platform_id) {
		this.platform_id = platform_id;
	}

	public String getCheckin_time() {
		return checkin_time;
	}

	public void setCheckin_time(String checkin_time) {
		this.checkin_time = checkin_time;
	}

	public String getCheckout_time() {
		return checkout_time;
	}

	public void setCheckout_time(String checkout_time) {
		this.checkout_time = checkout_time;
	}

	public String getArrive_time() {
		return arrive_time;
	}

	public void setArrive_time(String arrive_time) {
		this.arrive_time = arrive_time;
	}

	public int getHotel_id() {
		return hotel_id;
	}

	public void setHotel_id(int hotel_id) {
		this.hotel_id = hotel_id;
	}

	public String getHotel_name() {
		return hotel_name;
	}

	public void setHotel_name(String hotel_name) {
		this.hotel_name = hotel_name;
	}

	public String getPic_url() {
		return pic_url;
	}

	public void setPic_url(String pic_url) {
		this.pic_url = pic_url;
	}

	public String getHotel_address() {
		return hotel_address;
	}

	public void setHotel_address(String hotel_address) {
		this.hotel_address = hotel_address;
	}

	public String getHotel_tel() {
		return hotel_tel;
	}

	public void setHotel_tel(String hotel_tel) {
		this.hotel_tel = hotel_tel;
	}

	public String getRoom_name() {
		return room_name;
	}

	public void setRoom_name(String room_name) {
		this.room_name = room_name;
	}

	public int getRoom_quantity() {
		return room_quantity;
	}

	public void setRoom_quantity(int room_quantity) {
		this.room_quantity = room_quantity;
	}

	public String getBooking_name() {
		return booking_name;
	}

	public void setBooking_name(String booking_name) {
		this.booking_name = booking_name;
	}

	public String getBooking_mobile() {
		return booking_mobile;
	}

	public void setBooking_mobile(String booking_mobile) {
		this.booking_mobile = booking_mobile;
	}

	public String getGuest_name() {
		return guest_name;
	}

	public void setGuest_name(String guest_name) {
		this.guest_name = guest_name;
	}

	public String getGuest_mobile() {
		return guest_mobile;
	}

	public void setGuest_mobile(String guest_mobile) {
		this.guest_mobile = guest_mobile;
	}

	public int getOrder_type() {
		return order_type;
	}

	public void setOrder_type(int order_type) {
		this.order_type = order_type;
	}

	public double getOrder_amount() {
		return order_amount;
	}

	public void setOrder_amount(double order_amount) {
		this.order_amount = order_amount;
	}

	public double getPay_amount() {
		return pay_amount;
	}

	public void setPay_amount(double pay_amount) {
		this.pay_amount = pay_amount;
	}

	public double getGuarantee_amount() {
		return guarantee_amount;
	}

	public void setGuarantee_amount(double guarantee_amount) {
		this.guarantee_amount = guarantee_amount;
	}

	public double getPrize_amount() {
		return prize_amount;
	}

	public void setPrize_amount(double prize_amount) {
		this.prize_amount = prize_amount;
	}

	public double getHone_voucher_re_deposit() {
		return hone_voucher_re_deposit;
	}

	public void setHone_voucher_re_deposit(double hone_voucher_re_deposit) {
		this.hone_voucher_re_deposit = hone_voucher_re_deposit;
	}

	public int getEnable_cancel() {
		return enable_cancel;
	}

	public void setEnable_cancel(int enable_cancel) {
		this.enable_cancel = enable_cancel;
	}

	public String getCtip_order_id() {
		return ctip_order_id;
	}

	public void setCtip_order_id(String ctip_order_id) {
		this.ctip_order_id = ctip_order_id;
	}

	public String getConfirm_time() {
		return confirm_time;
	}

	public void setConfirm_time(String confirm_time) {
		this.confirm_time = confirm_time;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public int getIs_regret() {
		return is_regret;
	}

	public void setIs_regret(int is_regret) {
		this.is_regret = is_regret;
	}

	public String getUncertain_reason() {
		return uncertain_reason;
	}

	public void setUncertain_reason(String uncertain_reason) {
		this.uncertain_reason = uncertain_reason;
	}

	public int getCash_back_status() {
		return cash_back_status;
	}

	public void setCash_back_status(int cash_back_status) {
		this.cash_back_status = cash_back_status;
	}

	public String getCash_back_reason() {
		return cash_back_reason;
	}

	public void setCash_back_reason(String cash_back_reason) {
		this.cash_back_reason = cash_back_reason;
	}

	public double getCash_back_price() {
		return cash_back_price;
	}

	public void setCash_back_price(double cash_back_price) {
		this.cash_back_price = cash_back_price;
	}

	public int getCash_back_apply_status() {
		return cash_back_apply_status;
	}

	public void setCash_back_apply_status(int cash_back_apply_status) {
		this.cash_back_apply_status = cash_back_apply_status;
	}

	public int getNotify_status() {
		return notify_status;
	}

	public void setNotify_status(int notify_status) {
		this.notify_status = notify_status;
	}

	public String getC_time() {
		return c_time;
	}

	public void setC_time(String c_time) {
		this.c_time = c_time;
	}

	public String getM_time() {
		return m_time;
	}

	public void setM_time(String m_time) {
		this.m_time = m_time;
	}
    
    public long getReceiveMsgTime() {
        return receiveMsgTime;
    }

    public void setReceiveMsgTime(long receiveMsgTime) {
        this.receiveMsgTime = receiveMsgTime;
    }

	@Override
	public String toString() {
		return "HotelOrder [id=" + id + ", pt_u_id=" + pt_u_id
				+ ", order_no=" + order_no + ", hotel_order_no=" + hotel_order_no
				+ ", order_status=" + order_status + ", platform_id="
				+ platform_id + ", checkin_time=" + checkin_time
				+ ", checkout_time=" + checkout_time + ", arrive_time="
				+ arrive_time + ", hotel_id=" + hotel_id + ", hotel_name="
				+ hotel_name + ", hotel_address=" + hotel_address
				+ ", hotel_tel=" + hotel_tel + ", room_name=" + room_name
				+ ", room_quantity=" + room_quantity + ", booking_name="
				+ booking_name + ", booking_mobile=" + booking_mobile
				+ ", guest_name=" + guest_name + ", guest_mobile="
				+ guest_mobile + ", order_type=" + order_type
				+ ", order_amount=" + order_amount + ", pay_amount="
				+ pay_amount + ", guarantee_amount=" + guarantee_amount
				+ ", prize_amount=" + prize_amount
				+ ", hone_voucher_re_deposit=" + hone_voucher_re_deposit
				+ ", enable_cancel=" + enable_cancel + ", ctip_order_id="
				+ ctip_order_id + ", confirm_time=" + confirm_time
				+ ", create_time=" + create_time + ", is_regret=" + is_regret
				+ ", uncertain_reason=" + uncertain_reason
				+ ", cash_back_status=" + cash_back_status
				+ ", cash_back_reason=" + cash_back_reason
				+ ", cash_back_price =" + cash_back_price
				+ ", cash_back_apply_status=" + cash_back_apply_status
				+ ", notify_status=" + notify_status + ", c_time=" + c_time
				+ ", m_time=" + m_time + ", receiveMsgTime=" + receiveMsgTime + "]";
	}

}
