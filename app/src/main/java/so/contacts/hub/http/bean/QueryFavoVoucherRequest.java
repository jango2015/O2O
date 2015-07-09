package so.contacts.hub.http.bean;

import so.contacts.hub.core.Config;

/**
 * 优惠券
 */
public class QueryFavoVoucherRequest extends BaseRequestData<QueryFavoVoucherResponse> {

	public static final String ACTION_CODE = "170001";
	
    public QueryFavoVoucherRequest() {
        super(ACTION_CODE);
    }

    @Override
    protected QueryFavoVoucherResponse getNewInstance() {
        return new QueryFavoVoucherResponse();
    }

    @Override
    protected QueryFavoVoucherResponse fromJson(String json) throws Throwable {
        return Config.mGson.fromJson(json, QueryFavoVoucherResponse.class);
    }
}
