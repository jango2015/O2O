package so.contacts.hub.http.bean;

import java.util.ArrayList;
import java.util.List;

import so.contacts.hub.core.Config;
import so.contacts.hub.core.ConstantsParameter;

public class ReportContactsRequestData extends
		BaseRequestData<ReportContactsResponseData> {
	public List<Contact> contacts;// [List<Contact>][null able][联系人列表]
	public int has_more;// [int][not null][0:没有更多联系人上报,1:还有联系人需要上报]

	public ReportContactsRequestData(List<Contact> contacts, int has_more) {
		super(ConstantsParameter.ReportContactsRequestCode);
		this.contacts = contacts;
		this.has_more = has_more;
	}

	@Override
	protected ReportContactsResponseData fromJson(String json) {
		return Config.mGson.fromJson(json, ReportContactsResponseData.class);
	}

	public static class Contact {
		public String name;// [string][not null][联系人名字]
		public List<String> mobile_summary_list;// [List<String>][not
												// null][通信号码摘要列表]

		public void addMobile(String mobile) {
			if (mobile_summary_list == null) {
				mobile_summary_list = new ArrayList<String>();
			}
			mobile_summary_list.add(mobile);
		}
	}

	@Override
	protected ReportContactsResponseData getNewInstance() {
		return new ReportContactsResponseData();
	}
}
