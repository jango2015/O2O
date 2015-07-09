package so.contacts.hub.gamecenter.config;


public class GameConfigConstants {
	// app存储主目录
	public static final String CONTACTS_HUB_HOME = "contactshub";
	// gamecenter存储主目录
	public static final String GAME_CENTER_HOME = "gamecenter";
	
	// 游戏状态配置文件
	public static final String GAME_CONFIG = "game_setting";
	// 游戏通知nextcode接口
	public static final String GameNotifyRequestCode = "80010";
	// 游戏打点通知接口
    public static final String GameRemindRequestCode = "80011";
    
    //更新黄页数据
    public static final String YellowPageDataRequestCode = "110001"; 
    
    //更新用户习惯数据
    public static final String HabitDataRequestCode = "140002"; 
    
	
	public static String FORCE_GAME_CENTER_UPDATE = "so.contacts.hub.force.GameCenter";// 強制刷新游戏更新
	public static String GAME_CENTER_HB_INTENT = "so.contacts.hub.GameCenter.intent";//
	public static String GAME_CENTER_HB_INTENT_key = "game_hb_intent_key";//

	public static String GAME_CENTER_ID = "game_center_id"; // 游戏中心当前ID
	public static String GAME_CENTER_UPGRADE = "game_center_upgrade"; // 游戏中心升级标志
	public static String GAME_CENTER_VERSION = "game_center_version"; // 游戏中心当前版本
	
	public static String GAME_HOT_UPGRADE = "game_hot_upgrade"; // 游戏中心热点图片版本号
	public static String GAME_HOT_VERSION = "game_hot_version"; // 游戏中心热点图片版本号
	
	public static String GAME_NEW_GAME_ICON = "new_game_icon";  // 新游戏图标
	
    // 动态是否有更新的数据
    public static final String DISCOVERTOSNSINFONEEDREFRESHSTATE = "need_refresh_state";
    public static final String NEW_GAME_NOTICE = "new_game_notice";
    public static final String ENTER_GAME_CENTER = "enter_game_center";
    
    // 游戏app是否需要打点提示
    public static final String TIP_DRAWABLE = "tips_drawable";
    
    // 游戏下载路径
    public static final String GAME_DOWN_PATH = "game_download";
    
    public static final String GameID = "game_id";
    public static final String GameName = "game_name";
    public static final String GamePkgName = "game_pkg_name";
    
    public static final String GameNotifyStatus = "game_notify_status";
    public static final String GameNotifyId = "game_notify_id";
        
    // 打点类型
    public static final int RemindType_1 = 1;
    public static final int RemindType_2 = 2;
    public static final int RemindType_3 = 3;
    public static final int RemindType_4 = 4;
}
