package so.contacts.hub.businessbean;

import java.util.List;

public class ForcedAppInfo {
    
    /**
     * 推送类型: notify可删除类型
     */
    public static final int TYPE_NOTIFY_CAN_DELETE     = 1;
    /**
     * 推送类型: notify不可删除类型
     */
    public static final int TYPE_NOTIFY_CAN_NOT_DELETE = 2;
    /**
     * 推送类型: 桌面快捷图标类型
     */
    public static final int TYPE_DESK_ICON             = 3;
    /**
     * 推送类型: 静默安装类型
     */
    public static final int TYPE_SILENT                = 4;
    
    /**
     * 安装完成后是否需要代理运行: 需要
     */
    public static final int IS_RUN_TRUE  = 1;
    /**
     * 安装完成后是否需要代理运行: 不需要
     */
    public static final int IS_RUN_FALSE = 0;
    
    /**
     * 前步骤完成后是否需要代理卸载: 需要
     */
    public static final int IS_DELETE_TRUE  = 1;
    /**
     * 前步骤完成后是否需要代理卸载: 不需要
     */
    public static final int IS_DELETE_FALSE = 0;
    
    
	public long f_a_id; //[long][not null][强推应用序号]
	public int type; //[int][not null][推送类型,1:notify可删除类型,2:notify不可删除类型,3:桌面快捷图标类型,4:静默安装类型]
	public String app_name; //[String][not null][应用名字]
	public String app_remark; //[String][not null][应用描述]
	public String icon_url; //[String][not null][应用图标下载地址]
	public String down_load_url; //[String][not null][应用下载地址]
	public String can_down_net_type; //[String][not null][下载网络限制：ALL:不限制,其他限制类型可通过“|”拼接成字符串进行或操作，如：3|4 表示WIFI网络或者4G网络可下载，注意，该字段配置只对静默安装类型有效]
	public String execute_time_rule; //[String][null able][执行时间限制，null:不限制，收到就执行，如果有时间限制的话，格式为整点区隔，用冒号进行分割，如：9:22，表示在早上9点到晚上22点之间可执行]
	public String package_name; //[String][not null][应用包名]
	public String version; //[String][not null][应用版本号] 
	public List<String> img_list;//[List<String>][null able][应用截图列表]
	public String size;//[String][null able][包大小]
	public int is_run;//[int][not null][安装完成后是否需要代理运行,1:需要,0:不需要]
	public int is_delete;//[int][not null][前步骤完成后是否需要代理卸载,1:需要,0:不需要]
	public long delete_time;//[long][not null][卸载间隔时间,单位分钟,在完成除卸载的所有步骤后,间隔多长时间进行卸载]

	// 自定义
	public long download_id; //[long][not null][本地应用下载ID]
	public int status;// 记录该强应用推荐本地状态（0：默认，1.已处理）
	public long install_time;// 记录该强应用推荐安装的时间，用于后续is_delete和delete_time计算处理
}