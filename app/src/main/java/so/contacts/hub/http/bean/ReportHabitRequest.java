package so.contacts.hub.http.bean;

import java.util.List;

import so.contacts.hub.core.Config;
import so.contacts.hub.ui.yellowpage.bean.HabitDataItem;

public class ReportHabitRequest extends BaseRequestData<ReportHabitResponse>{

    public List<HabitDataItem> item_list;//[null able][新增用户习惯数据列表]

    public ReportHabitRequest(List<HabitDataItem> item_list) {//String pt_token, 
        super("140001");
//        this.pt_token = pt_token;
        this.item_list = item_list;
    }

    public ReportHabitRequest() {
        super("140001");
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
