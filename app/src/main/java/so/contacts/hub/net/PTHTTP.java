package so.contacts.hub.net;

import org.apache.http.protocol.HTTP;

/**
 * HTTP请求类, 业务与后台需要进行交互时使用
 * 所有需要与后台请求的业务调用通过此类来获取请求接口。
 * @author putao_lhq
 *
 */
public class PTHTTP {

    private static IPTHTTP mHttp;
    private static IPTHTTP mOldHttp;
    /**
     * 返回HTTP请求接口，新请求协议调用此接口来获取。
     * @return
     */
    public static IPTHTTP getInstance() {
        if (mHttp == null) {
            mHttp = new PTHTTPImpl();
        }
        return mHttp;
    }
    
    /**
     * 返回HTTP请求接口，旧协议json请求通过该接口进行处理
     * @return
     */
    public static IPTHTTP getOldHttp() {
        if (mOldHttp == null) {
            mOldHttp = new PTHTTPImpl();
            mOldHttp.setDefaultHeader(HTTP.CONTENT_TYPE, PTRequest.JSON_PROTOCOL_CONTENT_TYPE);
        }
        return mOldHttp;
    }
}