/**
 * 
 */
package so.contacts.hub.http.bean;

import so.contacts.hub.core.Config;

/**
 * @author Acher
 *
 */
public class ReportAppRecommendStatusRequest extends
		BaseRequestData<ReportAppRecommendStatusResponse> {

	public long id;//[long][not null][推荐应用ID]
	public int status;//[int][not null][1:点击查看,2:点击下载,3:完成安装]
	
	/**
	 * @param actionCode
	 */
	public ReportAppRecommendStatusRequest(long id, int status) {
		super("80002");
		this.id = id;
		this.status = status;
	}

	/* (non-Javadoc)
	 * @see so.contacts.hub.http.bean.BaseRequestData#getNewInstance()
	 */
	@Override
	protected ReportAppRecommendStatusResponse getNewInstance() {
		return new ReportAppRecommendStatusResponse();
	}

	/* (non-Javadoc)
	 * @see so.contacts.hub.http.bean.BaseRequestData#fromJson(java.lang.String)
	 */
	@Override
	protected ReportAppRecommendStatusResponse fromJson(String json)
			throws Throwable {
		return Config.mGson.fromJson(json, ReportAppRecommendStatusResponse.class);
	}

}
