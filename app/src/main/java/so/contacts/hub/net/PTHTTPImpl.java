package so.contacts.hub.net;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.VolleyQueue;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.Response.Listener;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;

public class PTHTTPImpl implements IPTHTTP {

    private static final String TAG = "PTRequest";
    private HashMap<String, String> defaultHeaders = new HashMap<String, String>();
    
    @Override
    public void setDefaultHeader(String key, String value) {
        defaultHeaders.put(key, value);
    }
    
    public PTHTTPImpl() {
        // TODO Auto-generated constructor stub
    }
    @Override
    public String post(String url, String queryString) {
        return sync(Request.Method.POST, url, queryString);
    }

    @Override
    public String post(String url, BaseRequestData reqData) {
        return sync(Request.Method.POST, url, reqData);
    }

    @Override
    public void asynPost(String url, String queryString, IResponse cb) {
        asyn(Request.Method.POST, url, queryString, cb);

    }

    @Override
    public void asynPost(String url, BaseRequestData data, final IResponse cb) {
        asyn(Request.Method.POST, url, data, cb);
    }
    
    @Override
    public String get(String url, String queryString) {
        url = convertUrl(url, queryString);
        return sync(Request.Method.GET, url, queryString);
    }

    private String convertUrl(String url, String queryString) {
        if (TextUtils.isEmpty(queryString)) {
            return url;
        }
        if (url.endsWith("?")) {
            url += queryString;
        } else {
            url += "?" + queryString;
        }
        return url;
    }

    @Override
    public String get(String url, BaseRequestData reqData) {
        url = convertUrl(url, encodeParameters(reqData.getParams()));
        return sync(Request.Method.GET, url, reqData);
    }

    @Override
    public void asynGet(String url, String queryString, final IResponse cb) {
        url = convertUrl(url, queryString);
        asyn(Request.Method.GET, url, queryString, cb);
    }

    @Override
    public void asynGet(String url, BaseRequestData data, final IResponse cb) {
        url = convertUrl(url, encodeParameters(data.getParams()));
        asyn(Request.Method.GET, url, data, cb);
    }

    private String sync(int method, String url, String data) { 
        RequestFuture<String> future = RequestFuture.newFuture();
        PTRequest request = new PTRequest(method, url, data, future, future);
        setDefaultHeader(request);
        VolleyQueue.getQueue().add(request);
        try {
            String content = future.get();
            LogUtil.d(TAG, "get result: " + content);
            return content;
        } catch (InterruptedException e) {
            LogUtil.d(TAG, "get InterruptedException: " + e);
            e.printStackTrace();
        } catch (ExecutionException e) {
            LogUtil.d(TAG, "get ExecutionException: " + e);
            e.printStackTrace();
        }
        return null;
    }
    
    private String sync(int method, String url, BaseRequestData data) { 
        RequestFuture<String> future = RequestFuture.newFuture();
        PTRequest request = new PTRequest(method, url, data, future, future);
        setDefaultHeader(request);
        VolleyQueue.getQueue().add(request);
        try {
            String content = future.get();
            LogUtil.d(TAG, "get result: " + content);
            return content;
        } catch (InterruptedException e) {
            LogUtil.d(TAG, "get InterruptedException: " + e);
            e.printStackTrace();
        } catch (ExecutionException e) {
            LogUtil.d(TAG, "get ExecutionException: " + e);
            e.printStackTrace();
        }
        return null;
    }
    
    private void asyn(int method, String url, BaseRequestData data, final IResponse cb) {
        PTRequest request = new PTRequest(method, url, data, new Listener<String>() {

            @Override
            public void onResponse(String content) {
                if (cb != null) {
                    cb.onSuccess(content);
                }
                
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtil.d(TAG, "asyn error: " + error.getMessage() + " error: " + error);
               if (cb != null) {
                   cb.onFail(0);
               }
            }
        });
        setDefaultHeader(request);
        VolleyQueue.getQueue().add(request);
    }
    
    private void asyn(int method, String url, String queryString, final IResponse cb) {
        PTRequest request = new PTRequest(method, url, queryString, new Listener<String>() {

            @Override
            public void onResponse(String content) {
                if (cb != null) {
                    cb.onSuccess(content);
                }
                
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtil.d(TAG, "asyn error: " + error.getMessage() + " error: " + error);
               if (cb != null) {
                   cb.onFail(0);
               }
            }
        });
        setDefaultHeader(request);
        VolleyQueue.getQueue().add(request);
    }
    
    private void setDefaultHeader(PTRequest request) {
        for(String key : defaultHeaders.keySet()) {
            request.setDefaultHeader(key, defaultHeaders.get(key));
        }
    }
    
    private String encodeParameters(Map<String, String> params) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                encodedParams.append('&');
            }
            return encodedParams.toString();
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + "UTF-8", uee);
        }
    }
}
