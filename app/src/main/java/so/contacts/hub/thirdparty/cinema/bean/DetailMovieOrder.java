package so.contacts.hub.thirdparty.cinema.bean;

import java.io.Serializable;
import java.sql.Timestamp;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.msgcenter.bean.PTOrderStatus;
import so.contacts.hub.thirdparty.cinema.utils.CinemaUtils;

/**
 * 订单详情页的been
 * @author peku
 *
 */
public class DetailMovieOrder implements Serializable{
    private static final long serialVersionUID = 1L;

    public long mp_id; // 场次ID

    public long movie_id; // 电影ID

    public long cinema_id; // 影院ID

    public String movie_name; // 影片名称

    public String cinema_name; // 影院名称

    public String city_code; // 城市编码

    public String city_name; // 城市名称

    public String trade_no; // 订单号

    public String mobile; // 手机号

    public String valid_time; // 订单有效期

    public int discount; // 折扣金额

    public String dis_reason; // 折扣原因

    public int amount; // 需支付的金额,单位分

    public int unit_price; // 单价

    public int quantity; // 座位数量

    private String  movie_photo_url;//电影的图片

    public String add_time; // 下单时间

    public String room_name; // 影厅名称

    public String play_time; // 放映时间

    public String seat; // 座位信息

    public String status; // 订单状态
    public int pt_status; // 订单状态

    public String order_title; // 订单标题

    public String paid_time; // 支付时间

    public int paid_amount; // 支付金额

    public String payseq_no; // 合作商订单号

    public String ukey; // 合作用户标识,终端机

    public String order_no; // 我们的订单号

    public String pt_u_id; // 我们的用户名

    public String error;//订单生成错误信息

    private String coupon_ids;//优惠券列表 add by hyl 2015-1-23
    public String getCoupon_ids() {
        return coupon_ids;
    }

    public void setCoupon_ids(String coupon_ids) {
        this.coupon_ids = coupon_ids;
    }

    public long getMp_id() {
        return mp_id;
    }

    public void setMp_id(long mp_id) {
        this.mp_id = mp_id;
    }

    public long getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(long movie_id) {
        this.movie_id = movie_id;
    }

    public long getCinema_id() {
        return cinema_id;
    }

    public void setCinema_id(long cinema_id) {
        this.cinema_id = cinema_id;
    }

    public String getMovie_name() {
        return movie_name;
    }

    public void setMovie_name(String movie_name) {
        this.movie_name = movie_name;
    }

    public String getCinema_name() {
        return cinema_name;
    }

    public void setCinema_name(String cinema_name) {
        this.cinema_name = cinema_name;
    }

    public String getCity_code() {
        return city_code;
    }

    public void setCity_code(String city_code) {
        this.city_code = city_code;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getTrade_no() {
        return trade_no;
    }

    public void setTrade_no(String trade_no) {
        this.trade_no = trade_no;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getValid_time() {
        return valid_time;
    }

    public void setValid_time(String valid_time) {
        this.valid_time = valid_time;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public String getDis_reason() {
        return dis_reason;
    }

    public void setDis_reason(String dis_reason) {
        this.dis_reason = dis_reason;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(int unit_price) {
        this.unit_price = unit_price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public String getPlay_time() {
        return play_time;
    }

    public void setPlay_time(String play_time) {
        this.play_time = play_time;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPt_status() {
        return pt_status;
    }

    public void setPt_status(int pt_status) {
        this.pt_status = pt_status;
    }

    public String getOrder_title() {
        return order_title;
    }

    public void setOrder_title(String order_title) {
        this.order_title = order_title;
    }

    public String getPaid_time() {
        return paid_time;
    }

    public void setPaid_time(String paid_time) {
        this.paid_time = paid_time;
    }

    public int getPaid_amount() {
        return paid_amount;
    }

    public void setPaid_amount(int paid_amount) {
        this.paid_amount = paid_amount;
    }

    public String getPayseq_no() {
        return payseq_no;
    }

    public void setPayseq_no(String payseq_no) {
        this.payseq_no = payseq_no;
    }

    public String getUkey() {
        return ukey;
    }

    public void setUkey(String ukey) {
        this.ukey = ukey;
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

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public String getMovie_photo_url() {
        return movie_photo_url;
    }

    public void setMovie_photo_url(String movie_photo_url) {
        this.movie_photo_url = movie_photo_url;
    }

    @Override
    public String toString() {
        return "DetailMovieOrder [mp_id=" + mp_id + ", movie_id=" + movie_id + ", cinema_id="
                + cinema_id + ", movie_name=" + movie_name + ", cinema_name=" + cinema_name
                + ", city_code=" + city_code + ", city_name=" + city_name + ", trade_no="
                + trade_no + ", mobile=" + mobile + ", valid_time=" + valid_time + ", discount="
                + discount + ", dis_reason=" + dis_reason + ", amount=" + amount + ", unit_price="
                + unit_price + ", quantity=" + quantity + ", add_time=" + add_time + ", room_name="
                + room_name + ", play_time=" + play_time + ", seat=" + seat + ", status=" + status
                + ", pt_status=" + pt_status + ", order_title=" + order_title + ", paid_time="
                + paid_time + ", paid_amount=" + paid_amount + ", payseq_no=" + payseq_no
                + ", ukey=" + ukey + ", order_no=" + order_no + ", pt_u_id=" + pt_u_id + ", error="
                + error + "]";
    }

    /**
     * 判断订单是否过期
     * add by hyl 2015-1-6
     * @return ture-已过期 false-未过期
     */
    public boolean isTimeOut(){
        return CinemaUtils.timeStr2Long(valid_time) < System.currentTimeMillis();
    }

    /**
     * 获取订单状态的显示值
     * add by hyl 2015-1-6
     * @return
     */
    public String showStatus(){
        int statusResId = -1;
        PTOrderStatus pTOrderStatus = PTOrderStatus.getStatusBeen(pt_status);
        if( pTOrderStatus != null ){
            statusResId = PTOrderStatus.getStatusBeen(pt_status).getStatusStr();
        }
        if(isTimeOut() && pt_status == PTOrderStatus.WAIT_BUYER_PAY.getStatusInt()){
            statusResId = PTOrderStatus.ORDER_CLOSED.getStatusStr();
        }
        if( statusResId != -1 ){
            return ContactsApp.getInstance().getString(statusResId);
        }
        return "";
    }
}

