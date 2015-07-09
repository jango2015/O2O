package so.contacts.hub.trafficoffence.bean;

import java.util.Map;

import so.contacts.hub.net.BaseRequestData;

public class RequestData extends BaseRequestData{

	@Override
	protected void setParams(Map<String, String> params) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, String> getParams() {
		// TODO Auto-generated method stub
		return params;
	}
	
	private Map<String, String> params;
	
	public void setParam(Map<String, String> params) {
		this.params = params;
	}
	
}
