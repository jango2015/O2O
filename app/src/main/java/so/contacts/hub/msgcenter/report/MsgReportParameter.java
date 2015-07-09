package so.contacts.hub.msgcenter.report;

import so.contacts.hub.core.Config;


public class MsgReportParameter {
	
	public static long MSG_NOTIFY_DELAY = (1 * 60 * 60 * 1000) / 2;
	public static long MSG_NOTIFY_PECCANCY_DELAY = 20000;//(12 * 60 * 60 * 1000) / 2;
	public static long MSG_MUL_DEL_DELAY = 20000;//(12 * 60 * 60 * 1000) / 2;
	public static long MSG_NOTIFY_TRAIN_DELAY = (1 * 60 * 60 * 1000) / 2;
	
//	public static final int TRAIN_TYPE = 1;
//	public static final int PECCANCY_TYPE = 2;
	
	public static final String VEHICLE = "vehicle";        // 车辆
	public static final String VEHICLE_IDS = "vehicleIds";
	public static final String TRAIN = "train";
	public static final String PECCANCY = "peccancy";	// 违章
	public static final String TYPE = "type";
	public static final String HOTEL = "hotel";  // 酒店
	
	public static final String TRAIN_REPORT_URL = Config.MSG_REPORT.TRAIN_REPORT_URL;
//	public static final String PECCANCY_REPORT_URL = "http://42.121.98.207:9280/msgremind/insert_illeagl_car";
	public static final String PECCANCY_REPORT_URL = Config.MSG_REPORT.PECCANCY_REPORT_URL;
	public static final String QUERY_CARINFO_URL = Config.MSG_REPORT.QUERY_CARINFO_URL;
	public static final String DEL_CARINFO_URL = Config.MSG_REPORT.DEL_CARINFO_URL;
	public static final String DEL_MUL_CARINFO_URL = Config.MSG_REPORT.DEL_MUL_CARINFO_URL;
	public static final String UPDATE_CARINFO_URL = Config.MSG_REPORT.UPDATE_CARINFO_URL;
	public static final String REPORT_URL = "";
	
	public static final String URL = "url";
	
	public static final String REPORT_CONTENT = "report_content";
	
	public static final String ACTION_REPORT = "so.contacts.hub.msgreport.action_report";
	public static final String ACTION_MUL_DEL = "so.contacts.hub.msgreport.action_mul_del";
	public static final String ACTION_REPORT_TRAIN = "so.contacts.hub.msgreport.action_report_train";
	public static final String ACTION_REPORT_PECCANCY = "so.contacts.hub.msgreport.action_report_peccancy";
}
