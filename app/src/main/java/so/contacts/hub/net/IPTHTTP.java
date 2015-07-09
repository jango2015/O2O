package so.contacts.hub.net;

/**
 * 葡萄服务请求接口<br>
 * 提供了两类八种方式的请求：<br>
 * 1. post<br>
 * 2. get<br>
 * @author putao_lhq
 *
 */
public interface IPTHTTP {

    /**
     * 同步post请求
     * @param url
     * @param queryString<br> 
     *          请求参数，该方式下 参数需要各业务进行拼装为&name1=value1&name2=value2的形式
     * @return
     */
    public String post(String url, String queryString);
    
    /**
     * 同步post请求
     * @param url
     * @param reqData<br> 
     *          该请求下传递继承{@link BaseRequestData}的对象
     * @return
     */
    public String post(String url, BaseRequestData reqData);
    
    public void asynPost(String url, String queryString, final IResponse cb);
    
    public void asynPost(String url, BaseRequestData data, final IResponse cb);

    public String get(String url, String queryString);
    
    public String get(String url, BaseRequestData reqData);
    
    public void asynGet(String url, String queryString, final IResponse cb);
    
    public void asynGet(String url, BaseRequestData data, final IResponse cb);
    
    public void setDefaultHeader(String key, String value);
}
