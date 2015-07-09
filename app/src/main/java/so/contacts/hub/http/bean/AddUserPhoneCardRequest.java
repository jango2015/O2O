package so.contacts.hub.http.bean;

import so.contacts.hub.core.Config;

public class AddUserPhoneCardRequest extends
		BaseRequestData<AddUserPhoneCardResponse> {

	public UserPhoneCardDomain upcd;// [UserPhoneCardDomain][not null][新增号码]

	public AddUserPhoneCardRequest(String phone) {
		super("10012");
		upcd = new UserPhoneCardDomain();
		upcd.phone = phone;
	}

	@Override
	protected AddUserPhoneCardResponse getNewInstance() {
		return new AddUserPhoneCardResponse();
	}

	@Override
	protected AddUserPhoneCardResponse fromJson(String json) throws Throwable {
		return Config.mGson.fromJson(json, AddUserPhoneCardResponse.class);
	}

}
