package so.contacts.hub.active;

import so.contacts.hub.active.bean.ActiveEggBean;

public interface ActiveInterface {
    
    /**
     * 返回当前节点服务名称，通过URl来判断
     * @return URL
     */
    public String getServiceNameByUrl();
    
    /**
     * 判断是否有活动彩蛋存在，返回有效的彩蛋
     * @param trigger_url  当前节点的服务名称 值为 {@link ActiveInterface#getServiceNameByUrl()} 返回值，如果为空则为<br>
     * {@link ActiveInterface#getServiceName()} 返回值.
     * @return 
     */
    public ActiveEggBean getValidEgg(String trigger_url);
    
    // 判断是否有活动彩蛋存在，返回有效的彩蛋, 默认mUrl
    public ActiveEggBean getValidEgg();
    
    /**
     * 返回当前服务节点名称，通过类名
     * @return
     */
    public String getServiceName();
    
    /**
     * 是否需要匹配扩展参数
     * @return
     */
    public boolean needMatchExpandParam();
}
