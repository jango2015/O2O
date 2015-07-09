package so.contacts.hub.net;

/**
 * 请求响应接口
 * @author putao_lhq
 *
 */
public interface IResponse {
    
    /**
     * 请求成功
     * @param content 
     */
    public void onSuccess(String content);

    /**
     * 请求失败
     * @param errorCode
     */
    public void onFail(int errorCode);
}
