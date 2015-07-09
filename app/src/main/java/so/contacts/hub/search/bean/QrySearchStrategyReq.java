package so.contacts.hub.search.bean;

import so.contacts.hub.http.bean.BaseRequestData;

public class QrySearchStrategyReq extends BaseRequestData<QrySearchStrategyRsp> {
	
	private static final String ACTION_CODE = "00000";
	
	public QrySearchStrategyReq() {
		super(ACTION_CODE);
	}

	private String word;


	public String getWord() {
		return word;
	}


	public void setWord(String word) {
		this.word = word;
	}
	
	@Override
	protected QrySearchStrategyRsp getNewInstance() {
		return new QrySearchStrategyRsp();
	}

}
