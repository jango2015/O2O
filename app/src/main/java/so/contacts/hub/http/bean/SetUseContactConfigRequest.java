package so.contacts.hub.http.bean;

public class SetUseContactConfigRequest extends
		BaseRequestData<SetUseContactConfigResponse> {

	public int is_use_contacts;// [int][not null][1:使用，2:不使用]

	public SetUseContactConfigRequest(int is_use_contacts) {
		super("00003");
		this.is_use_contacts = is_use_contacts;
	}

	@Override
	protected SetUseContactConfigResponse getNewInstance() {
		return null;

	}

	@Override
	protected SetUseContactConfigResponse fromJson(String json)
			throws Throwable {
		return null;

	}

}
