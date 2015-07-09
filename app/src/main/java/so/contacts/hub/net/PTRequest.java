package so.contacts.hub.net;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

import org.apache.http.protocol.HTTP;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.account.PutaoAccount;
import so.contacts.hub.core.PTUser;
import so.contacts.hub.util.LogUtil;
import so.putao.findplug.LBSServiceGaode;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.mdroid.core.util.SystemUtil;

/**
 * 
 * @author putao_lhq
 *
 */
public class PTRequest extends Request<String> {

    private Listener<String> mListener;
    private static final String TAG = "PTRequest";
    private HashMap<String, String> defaultHeaders = new HashMap<String, String>();
    /** Charset for request. */
    private static final String PROTOCOL_CHARSET = "utf-8";

    /** Content type for request. */
    public static final String JSON_PROTOCOL_CONTENT_TYPE =
        String.format("application/json; charset=%s", PROTOCOL_CHARSET);
    public static final String URL_PROTOCOL_CONTENT_TYPE =
            String.format("application/x-www-form-urlencoded; charset=%s", PROTOCOL_CHARSET);
    private BaseRequestData mRequestData;
    private String mQueryString;
    
    public PTRequest(int method, String url, BaseRequestData data, Listener<String> listen, ErrorListener listener) {
        super(method, url, listener);
        mListener = listen;
        mRequestData = data;
        setDefaultHeader("Accept-Encoding", "gzip");
        setDefaultHeader("Content-Type", URL_PROTOCOL_CONTENT_TYPE);
    }

    public PTRequest(int method, String url, String queryString, Listener<String> listen, ErrorListener listener) {
        super(method, url, listener);
        mListener = listen;
        mQueryString = queryString;
        setDefaultHeader("Accept-Encoding", "gzip");
        setDefaultHeader("Content-Type", URL_PROTOCOL_CONTENT_TYPE);
    }
    
    @Override
    protected void deliverResponse(String arg0) {
       mListener.onResponse(arg0);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        /*
        String parsed;
        try {
            LogUtil.d(TAG,"parseNetworkResponse: " + response.headers);
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            LogUtil.d(TAG,"parseNetworkResponse,UnsupportedEncodingException: " + response);
            parsed = new String(response.data);
        }*/
        return Response.success(getRealString(response.data), HttpHeaderParser.parseCacheHeaders(response));
    }

    private String getRealString(byte[] data) {
        byte[] h = new byte[2];
        h[0] = (data)[0];
        h[1] = (data)[1];
        int head = getShort(h);
        boolean t = head == 0x1f8b;
        InputStream in;
        StringBuilder sb = new StringBuilder();
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            if (t) {
                in = new GZIPInputStream(bis);
            } else {
                in = bis;
            }
            BufferedReader r = new BufferedReader(new InputStreamReader(in), 1000);
            for (String line = r.readLine(); line != null; line = r.readLine()) {
                sb.append(line);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
    
    private int getShort(byte[] data) {
        return (int)((data[0] << 8) | data[1] & 0xFF);
    }
    
    /**
     * 设置请求头文信息
     * @param key
     * @param value
     */
    public void setDefaultHeader(String key, String value) {
        defaultHeaders.put(key, value);
    }
  
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        initDefaultHeaderToken();
        return defaultHeaders;
    }
    
    private void initDefaultHeaderToken(){
        PTUser ptUsr = PutaoAccount.getInstance().getPtUser();
        
        /**
         * 增加每次请求上报的数据,版本,appid,渠道,open_token,机型,城市,经纬度
         * cookie中有城市中文,需要编码
         */
        // version, appid, channel, open_token
        StringBuffer cookieBuf = new StringBuffer();
        String encodeCity = "";
        try {
            encodeCity = URLEncoder.encode(LBSServiceGaode.getLocCity(), getParamsEncoding());
        } catch (UnsupportedEncodingException e) {
            LogUtil.d(TAG, "not support encoding: " + e);
            e.printStackTrace();
        }
        cookieBuf.append("app_id=").append(SystemUtil.getAppid(ContactsApp.getContext()))
                 .append(";channel=").append(SystemUtil.getChannelNo(ContactsApp.getContext()))
                 .append(";version=").append(SystemUtil.getAppVersion(ContactsApp.getContext()))
                 .append(";dev_no=").append(SystemUtil.getDeviceId(ContactsApp.getContext()))
                 .append(";band=").append(SystemUtil.getMachine())
                 .append(";city=").append(encodeCity)
                 .append(";loc=").append(String.valueOf(LBSServiceGaode.getLocLatitude()))
                                 .append(",")
                                 .append(String.valueOf(LBSServiceGaode.getLocLongitude()));
        
        if(ptUsr != null) {
            cookieBuf.append(";pt_token=").append(ptUsr.getPt_token());
        }
        
        setDefaultHeader("Cookie", cookieBuf.toString());
    }
    
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        if (mRequestData == null) {
            return super.getParams();
        }
        Map<String, String> param = mRequestData.getParams();
        if (param == null) {
            return null;
        }
        
        Map<String, String> newParam=new TreeMap<String, String>();
        for (Entry<String, String> entry : param.entrySet()) {
            if (!TextUtils.isEmpty(entry.getValue())) {
                newParam.put(entry.getKey(), entry.getValue());
            }
        }
        return newParam;
    }
    
    @Override
    public byte[] getBody() throws AuthFailureError {
        String contentType = defaultHeaders.get(HTTP.CONTENT_TYPE);
        LogUtil.d(TAG, "contentType: " + contentType);
        if (contentType.equals(URL_PROTOCOL_CONTENT_TYPE)) {
            return super.getBody();
        } else {
            try {
                return mQueryString.getBytes(JSON_PROTOCOL_CONTENT_TYPE);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return mQueryString.getBytes();
            }
        }
    }
}
