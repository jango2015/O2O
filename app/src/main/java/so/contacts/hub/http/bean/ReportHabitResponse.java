package so.contacts.hub.http.bean;
import java.util.List;

import so.contacts.hub.ui.yellowpage.bean.HabitDataItem;

public class ReportHabitResponse extends BaseResponseData {
    public int habit_version;//[not null][当前习惯版本]
    
    public List<HabitDataItem> habit_data_list;//[null able][用户当前习惯列表]

    public ReportHabitResponse(int habit_version, List<HabitDataItem> habit_data_list) {
        this.habit_version = habit_version;
        this.habit_data_list = habit_data_list;
    }

    public ReportHabitResponse() {
        super();
    }
    
    
    
    
}
