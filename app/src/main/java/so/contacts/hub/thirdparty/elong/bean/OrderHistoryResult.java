package so.contacts.hub.thirdparty.elong.bean;

import java.util.List;

public class OrderHistoryResult {

	private Long Count;
	
	private List<OrderHistory> Orders;

	public Long getCount() {
		return Count;
	}

	public void setCount(Long count) {
		Count = count;
	}

	public List<OrderHistory> getOrders() {
		return Orders;
	}

	public void setOrders(List<OrderHistory> orders) {
		Orders = orders;
	}
	
	
}
