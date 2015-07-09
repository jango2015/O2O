package so.contacts.hub.thirdparty.tongcheng.bean;

import java.util.List;

public class TC_Response_OrderDetail extends TC_Response_BaseData {

	private static final long serialVersionUID = 1L;
	
	private List<TC_OrderDetailBean> orderList;

	public List<TC_OrderDetailBean> getOrderList() {
		return orderList;
	}

	public void setOrderList(List<TC_OrderDetailBean> orderList) {
		this.orderList = orderList;
	}

}
