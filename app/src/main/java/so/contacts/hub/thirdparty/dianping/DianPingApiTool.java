
package so.contacts.hub.thirdparty.dianping;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import so.contacts.hub.util.HexUtil;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.UMengEventIds;
import so.contacts.hub.util.VolleyQueue;
import android.annotation.SuppressLint;

import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;

/**
 * Android版本API工具
 * 
 * @version 1.0 2013-1-23
 * @since dianping-java-samples 1.0
 */
@SuppressLint("DefaultLocale")
public class DianPingApiTool {
    private static final String TAG = "DianPingApiTool";

    private static final String APP_KEY = "2949555392";

    private static final String SIGN = "8ff7a44a96244ac48a8b560135aa777a";

    // 点评 - 我的团购券地址
    public static final String MY_DIANPING_QUAN = "http://lite.m.dianping.com/4rkBk4+VGi";// add
                                                                                          // by
                                                                                          // hyl
                                                                                          // 2014-11-28
                                                                                          // start

    /**
     * 获取指定团购信息
     */
    public static String URL_GET_CUSTOMS_INFO = "http://api.dianping.com/v1/deal/get_single_deal";

    /**
     * 获取指定商户最新点评片断
     */
    public static String URL_GET_REVIEWS_INFO = "http://api.dianping.com/v1/review/get_recent_reviews";

    /**
     * 获取支持商户搜索的最新城市列表
     */
    public static String URL_GET_CITY_BUSINESS_INFO = "http://api.dianping.com/v1/metadata/get_cities_with_businesses";

    /**
     * 搜索商户
     */
    public static String URL_GET_FIND_BUSINESS_INFO = "http://api.dianping.com/v1/business/find_businesses";

    /**
     * Search deals
     */
    public static String URL_GET_FIND_DEAL_INFO = "http://api.dianping.com/v1/deal/find_deals";

    /**
     * Get all deal ids list
     */
    public static String URL_GET_ALL_DEAL_ID_LIST = "http://api.dianping.com/v1/deal/get_all_id_list";

    /**
     * Get all deals by id
     */
    public static String URL_GET_BATCH_DEALS_BY_ID = "http://api.dianping.com/v1/deal/get_batch_deals_by_id";

    /**
     * Search Coupons
     */
    public static String URL_GET_FIND_COUPON_INFO = "http://api.dianping.com/v1/coupon/find_coupons";

    /**
     * 获取请求字符串
     * 
     * @param appKey
     * @param secret
     * @param paramMap
     * @return
     */

    public static String getQueryString(String appKey, String secret, Map<String, String> paramMap) {
        String sign = sign(appKey, secret, paramMap);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("appkey=").append(appKey).append("&sign=").append(sign);
        for (Entry<String, String> entry : paramMap.entrySet()) {
            stringBuilder.append('&').append(entry.getKey()).append('=').append(entry.getValue());
        }
        String queryString = stringBuilder.toString();
        return queryString;
    }

    /**
     * 获取请求字符串，参数值进行UTF-8处理
     * 
     * @param appKey
     * @param secret
     * @param paramMap
     * @return
     */
    public static String getUrlEncodedQueryString(String appKey, String secret,
            Map<String, String> paramMap) {
        String sign = sign(appKey, secret, paramMap);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("appkey=").append(appKey).append("&sign=").append(sign);
        for (Entry<String, String> entry : paramMap.entrySet()) {
            try {
                stringBuilder.append('&').append(entry.getKey()).append('=')
                        .append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
            }
        }
        String queryString = stringBuilder.toString();
        return queryString;
    }

    public static String requestApi(String apiUrl, Map<String, String> paramMap) {
        return requestApi(apiUrl, DianPingApiTool.APP_KEY, DianPingApiTool.SIGN, paramMap);
    }

    /**
     * 请求API
     * 
     * @param apiUrl
     * @param appKey
     * @param secret
     * @param paramMap
     * @return
     */
    public static String requestApi(String apiUrl, String appKey, String secret,
            Map<String, String> paramMap) {
        String fetchUrl = apiUrl + "?" + getUrlEncodedQueryString(appKey, secret, paramMap);
        LogUtil.d(TAG, "dian ping fetch = " + fetchUrl);
        RequestFuture<String> future = RequestFuture.newFuture();
        StringRequest request = new StringRequest(fetchUrl, future, future);
        future.setRequest(request);
        VolleyQueue.getQueue().add(request);
        try {
            return future.get();
        } catch (Exception e) {
            if (e.getCause() instanceof Exception) {
                UMengEventIds.reportSearchError("dianping", (Exception)e.getCause());
            } else {
                UMengEventIds.reportSearchError("dianping", e);
            }
            return null;
        }
    }

    /**
     * 签名
     * 
     * @param appKey
     * @param secret
     * @param paramMap
     * @return
     */
    public static String sign(String appKey, String secret, Map<String, String> paramMap) {
        // 参数名排序
        String[] keyArray = paramMap.keySet().toArray(new String[0]);
        Arrays.sort(keyArray);

        // 拼接参数
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(appKey);
        for (String key : keyArray) {
            stringBuilder.append(key).append(paramMap.get(key));
        }

        stringBuilder.append(secret);
        String codes = stringBuilder.toString();

        // SHA-1签名
        // For Android

        MessageDigest sha;
        String sign = "";
        try {
            sha = MessageDigest.getInstance("SHA-1");
            sha.update(codes.getBytes());
            sign = HexUtil.bytes2HexStr(sha.digest()).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            LogUtil.e(TAG, e.getMessage(), e);
        }
        return sign;
    }
}
