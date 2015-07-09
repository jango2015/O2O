package so.contacts.hub.remind;

/**
 * @date	: 
 * @author	: change
 * @descrip	:
 */
public class RemindConfig {
    // 打点配置文件
    public static final String REMIND_CONFIG = "remind_data";
    
    // 游戏中心打点标识码定义
    public static final int GameCenterRemindCode = 3501;
    // 动态社交atme打点标识码定义
    public static final int SnsDynmaicRemindCode = 3401;
    
    // 动态打点更新标志
    public static final String SnsDynmaicRemindUpdateFlag = "sys_dynmaic_remind_update_flag";
    // 游戏中心打点更新标志
    public static final String GameCeterRemindUpdateFlag = "game_center_remind_update_flag";

    // 1级打点根节点： 发现页
    // 2级打点根节点： 动态、推荐联系人、名片更新、黄页、游戏中心    
    // 3级打点：游戏详情页
    public static final int DiscoverPageNode = 0;
    public static final int GameCenterNode = 52;
    public static final int MaxCategoryID = 10000;               // 类别id分解线

    /**
     * 常用但未保存的类别ID/类别itemid，不存入数据库，只保存在remindmanager中
     * 该category_id/item_id与remindcode保持一致
     */
    public static final int AddService = 9998;                  // 首页添加服务按钮
    public static final int MyService = 60;                     // 首页我的 
    
    public static final int MyOrder = 900001;                   // 首页我的->我的订单
    public static final int MyOrderTuan = 900002;               // 首页我的->团购定券
    public static final int MyOrderHotel = 900003;              // 首页我的->酒店订房
    public static final int MyOrderChargeHistory = 900004;      // 首页我的->充值历史
    public static final int MyActivies = 900005;                // 首页我的->我的活动
    public static final int MyTongchengTrain = 900006;          // 首页我的->火车票
    
    //add start xcx 新增提醒中心code 2014-12-27
    public static final int MyMsgCenter = 900007;               // 首页我的->提醒中心
    //add end xcx 新增提醒中心code 2014-12-27
    
    public static final int MaxMyServiceRemindCode = 900199;   

    public static final int MyFavorite = 910001;                // 首页我的->我的收藏
    public static final int MyHistory = 920001;                 // 首页我的->我的历史

    public static final int GameBaseRemindCodeOffset = 990000;  // 游戏没有remindcode，采用GameBaseRemindCode+gameId的方法保存

    public static final int REMIND_TYPE_NONE = -1;             // -1没有打点
    public static final int REMIND_TYPE_VIEW_CLEAN = 0x0;      // 小弱：查看消除
    public static final int REMIND_TYPE_VIEW_NUMBER = 0x1;     // 大弱：点击消除数字
    public static final int REMIND_TYPE_CLICK_CLEAN = 0x2;     // 小强：点击消除打点
    public static final int REMIND_TYPE_CLICK_NUMBER = 0x3;    // 大强：查看消除数字
    public static final int REMIND_TYPE_TIME_CLEAN = 0x4;      // 时间消除
    
    // 打点、气泡样式定义
    public static final int REMIND_STYLE_NONE = 0;
    public static final int REMIND_STYLE_HOT = 1;
    public static final int REMIND_STYLE_HUI = 2;
    public static final int REMIND_STYLE_TUAN = 3;
    public static final int REMIND_STYLE_RECOMMENT = 4;   
    
    // 远程更新打点命令字
    public static final int REMIND_NONE = 0;
    public static final int REMIND_ADD = 1;
    public static final int REMIND_UPDATE = 2;
    public static final int REMIND_DELETE = 3;    
    
    /**
     * 虚拟节点，用于消息中心等内部无节点的地方，
     * 数量统计使用
     * @author xcx
     */
    public static final int REMIND_VIRTUAL_NODE_CODE=-100;
}
