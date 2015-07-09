package so.contacts.hub.http.bean;

import so.contacts.hub.core.Config;

public class GetOrderResultRequest extends BaseRequestData<GetOrderResultResponse> {

	public GetOrderResultRequest() {
		super("110002");
	}

	private String orderId;
	
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getOrderId() {
		return orderId;
	}	
	@Override
	protected GetOrderResultResponse fromJson(String json) throws Throwable {
		return Config.mGson.fromJson(json, GetOrderResultResponse.class);
	}

	@Override
	protected GetOrderResultResponse getNewInstance() {
		return new GetOrderResultResponse();
	}
}
