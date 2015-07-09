package so.contacts.hub.http.bean;

public class NewVersionResponseData extends BaseResponseData {
	public String version;// [String][null able][最新版本号]
	public String down_url;// [String][null able][最新版本下载地址]
	public String remark;// [String][null able][最新版本描述]
	public int size; // size:[int][null able][新版版本大小，单位m]
	// public int enforce;//[int][null able][强制升级标志位:0:不强制升级,1:强制升级]
}
