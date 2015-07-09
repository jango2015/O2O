package so.contacts.hub.http.bean;

import java.util.List;

import so.contacts.hub.core.Config;

public class DeleteUserUpdateNotifyRequest extends
		BaseRequestData<DeleteUserUpdateNotifyResponse> {

	public List<Long> user_update_notify_ids;// [List<Long>][not
												// null][要清除的联系人更新的ID]

	public DeleteUserUpdateNotifyRequest(List<Long> user_update_notify_ids) {
		super("20010");
		this.user_update_notify_ids = user_update_notify_ids;
	}

	@Override
	protected DeleteUserUpdateNotifyResponse getNewInstance() {
		return new DeleteUserUpdateNotifyResponse();
	}

	@Override
	protected DeleteUserUpdateNotifyResponse fromJson(String json)
			throws Throwable {
		return Config.mGson.fromJson(json, DeleteUserUpdateNotifyResponse.class);
	}

}
