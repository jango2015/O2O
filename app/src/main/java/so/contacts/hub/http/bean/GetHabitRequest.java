package so.contacts.hub.http.bean;

import so.contacts.hub.core.Config;
import so.contacts.hub.gamecenter.config.GameConfigConstants;

public class GetHabitRequest extends BaseRequestData<ReportHabitResponse>{

    public GetHabitRequest() {//String pt_token
        super(GameConfigConstants.HabitDataRequestCode);
//        this.pt_token = pt_token;
    }

    @Override
    protected ReportHabitResponse getNewInstance() {
        // TODO Auto-generated method stub
        return new ReportHabitResponse();
    }
    
    @Override
    protected ReportHabitResponse fromJson(String json) {
        return Config.mGson.fromJson(json, ReportHabitResponse.class);
    }
    
    
    
}
