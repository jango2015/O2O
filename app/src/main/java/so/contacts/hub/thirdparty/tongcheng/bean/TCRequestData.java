package so.contacts.hub.thirdparty.tongcheng.bean;

public class TCRequestData {
	
	private String mRequestHead;
	
	private String mRequestBody;
	
	public TCRequestData(String head, String body){
		mRequestHead = head;
		mRequestBody = body;
	}

	public String getmRequestHead() {
		return mRequestHead;
	}

	public void setmRequestHead(String mRequestHead) {
		this.mRequestHead = mRequestHead;
	}

	public String getmRequestBody() {
		return mRequestBody;
	}

	public void setmRequestBody(String mRequestBody) {
		this.mRequestBody = mRequestBody;
	}
	
	public String getReqeustData(){
		StringBuffer requestData = new StringBuffer();
		requestData.append("<?xml version='1.0' encoding='utf-8' ?>");
		requestData.append("<request>");
		requestData.append(mRequestHead);
		requestData.append(mRequestBody);
		requestData.append("</request>");
		return requestData.toString();
	}

}
