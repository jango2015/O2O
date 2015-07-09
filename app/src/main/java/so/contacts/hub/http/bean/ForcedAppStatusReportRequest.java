/**
 * 
 */
package so.contacts.hub.http.bean;

import so.contacts.hub.core.Config;

/**
 * @author Acher
 *
 */
public class ForcedAppStatusReportRequest extends
		BaseRequestData<ForcedAppStatusReportResponse> {

	public long f_a_id;//[long][not null][强推应用序号]
	public int act;//[int][not null][状态码：2:已经执行,3:用户点击,5:已经安装]
	public long act_time;//[long][not null][行为发生时间戳，毫秒数]
	
	/**
	 * @param actionCode
	 */
	public ForcedAppStatusReportRequest(long f_a_id, int act) {
		super("60003");
		this.f_a_id = f_a_id;
		this.act = act;
		this.act_time = System.currentTimeMillis();
	}

	/* (non-Javadoc)
	 * @see so.contacts.hub.http.bean.BaseRequestData#getNewInstance()
	 */
	@Override
	protected ForcedAppStatusReportResponse getNewInstance() {
		return new ForcedAppStatusReportResponse();
	}

	/* (non-Javadoc)
	 * @see so.contacts.hub.http.bean.BaseRequestData#fromJson(java.lang.String)
	 */
	@Override
	protected ForcedAppStatusReportResponse fromJson(String json)
			throws Throwable {
		return Config.mGson.fromJson(json, ForcedAppStatusReportResponse.class);
	}

}
