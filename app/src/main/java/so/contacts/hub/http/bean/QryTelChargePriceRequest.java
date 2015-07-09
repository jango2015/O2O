package so.contacts.hub.http.bean;

import so.contacts.hub.core.Config;

public class QryTelChargePriceRequest extends BaseRequestData<QryTelChargePriceResponse>{
	public String mobile;  // [String][not null][充值的手机号]
	
	public QryTelChargePriceRequest(String mobile) {
		super("800100");
		this.mobile = mobile;
	}
	
	@Override
	protected QryTelChargePriceResponse getNewInstance() {
		return new QryTelChargePriceResponse();
	}

	@Override
	protected QryTelChargePriceResponse fromJson(String json) throws Throwable {
		return Config.mGson.fromJson(json, QryTelChargePriceResponse.class);
	}

}
