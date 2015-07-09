package so.contacts.hub.train.bean;

import java.io.Serializable;

public class TrainTricketOrderHistoryBean implements Serializable{
	/**   
		    
		    返回格式
		{
		  "code" : 0,
		  "msg" : "",
		  "data" : {
		    "id" : 2,
		    "puid" : "pt1411349351278-10013",
		    "order_id" : "tvDMxzkK1M0=1",
		    "order_status" : "F",
		    "order_price" : "19.5",
		    "create_time" : "2014-11-04 14:53:10",
		    "depart_station_name" : "上海虹桥",
		    "arrive_station_name" : "昆山南",
		    "depart_time" : "2014-11-20 06:42:00",
		    "train_num" : "D3072",
		    "state" : "100",
		    "error" : "请求成功"
		    
		    {
    "OrderId": "tvDMxzkK1M0=",				//订单ID
    "OrderStatus": "F-已出票",				//订单状态
    "OrderPrice": "19.5",					//订单价格（票价+保险+邮寄费）
    "CreateTime": "2014-11-04 14:53:10",	//创单时间
    "DepartStationName": "上海虹桥",		//出发站
    "ArriveStationName": "昆山南",			//到达站
    "DepartTime": "2014-11-20 06:42:00",		//发车时间
    "TrainNum": "D3072",					//车次号
    "State": "100",							//状态码
    "Error": "请求成功"					//错误信息
}

		    
		  }
		}
	*/
	
	private String order_id; //订单ID
	private String order_status;//订单状态
	private String order_price;//订单价格（票价+保险+邮寄费）
	private String create_time;//创单时间
	private String depart_station_name;//出发站
	private String arrive_station_name;//到达站
	private String depart_time;//发车时间
	private String train_num;//车次号
	
	
	
	private String arrive_time;//到达时间
	private String seat_num;//车厢座位号
	private String name;//旅客姓名
	
	
	public String getOrder_id() {
		return order_id;
	}
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}
	public String getOrder_status() {
		return order_status;
	}
	public void setOrder_status(String order_status) {
		this.order_status = order_status;
	}
	public String getOrder_price() {
		return order_price;
	}
	public void setOrder_price(String order_price) {
		this.order_price = order_price;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public String getDepart_station_name() {
		return depart_station_name;
	}
	public void setDepart_station_name(String depart_station_name) {
		this.depart_station_name = depart_station_name;
	}
	public String getArrive_station_name() {
		return arrive_station_name;
	}
	public void setArrive_station_name(String arrive_station_name) {
		this.arrive_station_name = arrive_station_name;
	}
	public String getDepart_time() {
		return depart_time;
	}
	public void setDepart_time(String depart_time) {
		this.depart_time = depart_time;
	}
	public String getTrain_num() {
		return train_num;
	}
	public void setTrain_num(String train_num) {
		this.train_num = train_num;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getArrive_time() {
		return arrive_time;
	}
	public void setArrive_time(String arrive_time) {
		this.arrive_time = arrive_time;
	}
	public String getSeat_num() {
		return seat_num;
	}
	public void setSeat_num(String seat_num) {
		this.seat_num = seat_num;
	}
	@Override
	public String toString() {
		return "TrainTricketOrderHistoryBean [order_id=" + order_id
				+ ", order_status=" + order_status + ", order_price="
				+ order_price + ", create_time=" + create_time
				+ ", depart_station_name=" + depart_station_name
				+ ", arrive_station_name=" + arrive_station_name
				+ ", depart_time=" + depart_time + ", train_num=" + train_num
				+ ", arrive_time=" + arrive_time + ", seat_num=" + seat_num
				+ ", name=" + name + "]";
	}
}
