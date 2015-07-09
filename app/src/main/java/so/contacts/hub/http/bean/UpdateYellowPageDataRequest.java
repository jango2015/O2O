package so.contacts.hub.http.bean;

import so.contacts.hub.core.Config;

public class UpdateYellowPageDataRequest extends BaseRequestData<UpdateYellowPageDataResponse> {

    public int data_version;//本地数据版本
    
    public UpdateYellowPageDataRequest(int data_version) {
        super("110001");
        this.data_version = data_version;
    }

    @Override
    protected UpdateYellowPageDataResponse getNewInstance() {
        return new UpdateYellowPageDataResponse();
    }

    @Override
    protected UpdateYellowPageDataResponse fromJson(String json) throws Throwable {
        return Config.mGson.fromJson(json, UpdateYellowPageDataResponse.class);
    }

}
