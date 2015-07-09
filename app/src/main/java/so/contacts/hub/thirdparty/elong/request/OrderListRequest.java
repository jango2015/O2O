package so.contacts.hub.thirdparty.elong.request;

import so.contacts.hub.thirdparty.elong.bean.OrderListBean;

/**
 * 获取订单历史请求
 */
public class OrderListRequest extends BaseRequestAndResult<OrderListBean> {

	private OrderListBean mRequestData = null;
	
	@Override
	public OrderListBean getRequestData() {
		// TODO Auto-generated method stub
		return mRequestData;
	}

	@Override
	public String getRequestMethod() {
		// TODO Auto-generated method stub
		return "hotel.order.list";
	}

	@Override
	public void setRequestData(OrderListBean t) {
		// TODO Auto-generated method stub
		mRequestData = t;
	}

}
