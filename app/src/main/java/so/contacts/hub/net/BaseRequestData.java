
package so.contacts.hub.net;

import java.util.HashMap;
import java.util.Map;

import so.contacts.hub.ContactsApp;

import com.mdroid.core.util.SystemUtil;

/**
 * 请求基本类，所有需要向后台请求的业务继承该类<br>
 * 重写{@link BaseRequestData#setParams()}},在该方法中调用<br>
 * {@link BaseRequestData#setParam(String, String)}设置请求参数。可以参考{@link PTDemoRequest}<br>
 * 如果请求数据不需要该类提供的参数，可以重写{@link BaseRequestData#getParams()}方法，<br>
 * 将需要的参数字段通过{@link BaseRequestData#setParam(String, String)}方法设置进去即可。<br>
 * 需要签名的，怎需要重写{@link BaseRequestData#getLocalSign()}方法进行签名处理
 * 
 * @author putao_lhq
 */
public abstract class BaseRequestData {

    /**
     * 设备号
     */
    private String dev_no;

    /**
     * 时间戳
     */
    private long timestamp;

    /**
     * 渠道号
     */
    private String channel_no;

    /**
     * 签名
     */
    private String localSign;

    /**
     * HTTP请求参数
     */
    private Map<String, String> params;

    public BaseRequestData() {
        dev_no = SystemUtil.getDeviceId(ContactsApp.getInstance());
        timestamp = System.currentTimeMillis();
        channel_no = SystemUtil.getChannelNo(ContactsApp.getContext());
        params = new HashMap<String, String>();
    }

    public Map<String, String> getParams() {
        if (getDev_no() != null) {
            params.put("dev_no", getDev_no());
        }
        if (getTimestamp() > 0) {
            params.put("timestamp", String.valueOf(getTimestamp()));
        }
        if (getLocalSign() != null) {
            params.put("local_sign", getLocalSign());
        }
        if (getChannelNo() != null) {
            params.put("channel_no", getChannelNo());
        }
        setParams(params);
        return params;
    }

    /**
     * 设置请求参数，
     */
    protected abstract void setParams(Map<String, String> params);

    public void setParam(String param, String value) {
        params.put(param, value);
    }

    public String getDev_no() {
        return dev_no;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public String getLocalSign() {
        return localSign;
    }

    public String getChannelNo() {
        return channel_no;
    }

}
