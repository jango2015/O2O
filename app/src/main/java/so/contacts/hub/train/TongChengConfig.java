package so.contacts.hub.train;

public class TongChengConfig {
	 //add by lisheng 同城火车票订票查询,和订单查询http://61.155.159.109:9523/pub/
	//add by lisheng 测试地址 http://61.155.159.109:9523/pub/
    
    public static final String HOST_TRAIN = "http://m.ly.com/pub/train"; 
    //"http://m.ly.com/pub/train"; 
    //"http://train1.nat123.net/pub/train";// 同城测试用
    // "http://train.nat123.net/pub/train/";
    // "http://61.155.159.109:9523/pub/train";
	
	public static final String PUTAO_TONGCHENG_TRAIN_REFID = "refid"; // 同程为葡萄分配的渠道号
	public static final String PUTAO_TONGCHENG_TRAIN_REFVAL = "47664101"; // 同程为葡萄分配的渠道号
	
	//http://m.ly.com/pub/train/
	// http://train.nat123.net/pub/train/trainsearch-shenzhen-guangzhou.html?Time=2014-11-29&filter=GD|D&ShowAvailableTrain=0&refid=47664101&open_token=97ced04399b046fcb7ccfc21934113cf
    public static final String YELLOW_PAGE_TONGCHENG_TICKETQUERY = HOST_TRAIN+"/trainsearch";
//    public static final String YELLOW_PAGE_TONGCHENG_TICKETQUERY = "http://121.41.60.51:7899/_plugin/head/train.html";
    public static final String YELLOW_PAGE_TONGCHENG_ORDERQUERY = HOST_TRAIN+"/orderlist";
//    public static final String YELLOW_PAGE_TONGCHENG_ORDER_REPORT ="http://121.41.59.121:8080/tongcheng/report_train_ticket";
//    public static final String YELLOW_PAGE_TONGCHENG_REQUEST_PARAM ="&ShowAvailableTrain=0&refid=47664101&open_token=97ced04399b046fcb7ccfc21934113cf";
    public static final String YELLOW_PAGE_TONGCHENG_REQUEST_PARAM ="&ShowAvailableTrain=0";
    
    public static final String YELLOW_PAGE_FEEDBACK_URL = "http://feedback.putao.so/feedback/suggest";//用户反馈url
//    public static final String YELLOW_PAGE_FEEDBACK_URL = "http://121.41.59.121:8080/feedback/suggest";//用户反馈url
}
