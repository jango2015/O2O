package so.contacts.hub.http.bean;

import so.contacts.hub.core.Config;

public class QuitCircleRequestData extends
		BaseRequestData<QuitCircleResponseData> {

	public long room_local_id;

	public QuitCircleRequestData(long room_local_id) {
		super("30010");
		this.room_local_id = room_local_id;
	}

	@Override
	protected QuitCircleResponseData getNewInstance() {
		return new QuitCircleResponseData();
	}

	@Override
	protected QuitCircleResponseData fromJson(String json) throws Throwable {
		return Config.mGson.fromJson(json, QuitCircleResponseData.class);
	}

}
