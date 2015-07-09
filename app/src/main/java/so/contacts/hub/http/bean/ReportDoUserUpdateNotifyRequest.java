package so.contacts.hub.http.bean;

import java.util.List;

import so.contacts.hub.core.Config;

public class ReportDoUserUpdateNotifyRequest extends
		BaseRequestData<ReportDoUserUpdateNotifyResponse> {

	public List<Long> report_do_notify_ids;// [List<Long>][not
											// null][已经处理的联系人更新的ID]

	public ReportDoUserUpdateNotifyRequest(List<Long> report_do_notify_ids) {
		super("20011");
		this.report_do_notify_ids = report_do_notify_ids;
	}

	@Override
	protected ReportDoUserUpdateNotifyResponse getNewInstance() {
		return new ReportDoUserUpdateNotifyResponse();
	}

	@Override
	protected ReportDoUserUpdateNotifyResponse fromJson(String json)
			throws Throwable {
		return Config.mGson.fromJson(json, ReportDoUserUpdateNotifyResponse.class);
	}

}
