package so.contacts.hub.active.bean;

public class ActiveEggBean implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	public long active_id;               // 活动ID
    public int egg_id;                  // 埋点/彩蛋ID
    public String request_url;          // 请求url
    public String trigger;              // 触发的服务或url，包名或某个h5地址
    public int trigger_type;            // 触发类型 1-弹窗型 2-静默型
    public long start_time;             // 活动开始时间
    public long end_time;               // 活动结束时间
    public int status;                  // 状态 0-未开始 1-进行中 2-已结束
    public String expand_param; 
    public long valid_time;				// 生效时间 //add by putao_lhq
    
    public ActiveEggBean() {
        active_id = -1;
        egg_id =-1;
        status = 0;
    }
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("active_id=").append(active_id)
            .append(" egg_id=").append(egg_id)
            .append(" request_url=").append(request_url)
            .append(" trigger=").append(trigger)
            .append(" trigger_type=").append(trigger_type)
            .append(" start_time=").append(start_time)
            .append(" end_time=").append(end_time)
            .append(" status=").append(status)
            .append(" expand_param=").append(expand_param)
            .append(" valid_time=").append(valid_time);
        
        return sb.toString();
    }
}
