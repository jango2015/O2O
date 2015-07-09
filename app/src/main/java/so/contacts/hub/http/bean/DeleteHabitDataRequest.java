package so.contacts.hub.http.bean;

import so.contacts.hub.core.Config;

public class DeleteHabitDataRequest extends BaseRequestData<DeleteHabitDataResponse> {
    
    public DeleteHabitDataRequest(long id) {
        super("140003");
        // TODO Auto-generated constructor stub
        this.id = id;
    }

    private long id ;//[long][ not null][要删除的用户习惯数据的id]

    @Override
    protected DeleteHabitDataResponse fromJson(String json) throws Throwable {
        return Config.mGson.fromJson(json, DeleteHabitDataResponse.class);
    }

    @Override
    protected DeleteHabitDataResponse getNewInstance() {
        return new DeleteHabitDataResponse();
    }
    
}
