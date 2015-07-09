package so.contacts.hub.http.bean;

import so.contacts.hub.core.Config;

/**
 * 设备号注册接口
 * @author hyl 2014-9-19
 */
public class DeviceUserRegisterRequest extends BaseRequestData<DeviceUserRegisterResponse> {

    public DeviceUserRegisterRequest() {
        super("130001");
    }

    @Override
    protected DeviceUserRegisterResponse getNewInstance() {
        return new DeviceUserRegisterResponse();
    }
    
    @Override
    protected DeviceUserRegisterResponse fromJson(String json) throws Throwable {
        return Config.mGson.fromJson(json, DeviceUserRegisterResponse.class);
    }

    
}
