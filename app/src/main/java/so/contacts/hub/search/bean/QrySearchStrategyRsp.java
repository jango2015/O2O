package so.contacts.hub.search.bean;

import java.util.List;

import so.contacts.hub.http.bean.BaseResponseData;

public class QrySearchStrategyRsp extends BaseResponseData{
	
	private static final long serialVersionUID = 1L;

	private int hits;
	
	private List<SearchStrategyBean> result;

	public int getHits() {
		return hits;
	}

	public void setHits(int hits) {
		this.hits = hits;
	}

	public List<SearchStrategyBean> getResult() {
		return result;
	}

	public void setResult(List<SearchStrategyBean> result) {
		this.result = result;
	}
}
