
package so.contacts.hub.thirdparty.city58;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.UMengEventIds;
import so.contacts.hub.util.VolleyQueue;
import android.text.TextUtils;

import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;

/**
 * @author putao_lhq
 * @version 2014年9月24日
 */
public class City58ApiTool {
    private static final String TAG = "City58ApiTool";

    private static final String CHANNEL = "huangye";// 渠道号

    private static final String CHARGE = "b-2-ms-s-anzhuo"; // 计费名

    public static final String URL_GET_CATEGORY_INFO = "http://luna.58.com/xmladsvr/lbsads?";

    public static String requestApi(String apiUrl, Map<String, String> paramMap) {
        if (paramMap == null || paramMap.size() <= 0 || TextUtils.isEmpty(apiUrl)) {
            return null;
        }
        String queryString = getEncodedQueryString(apiUrl, paramMap);
        LogUtil.v(TAG, "queryString = " + queryString);

        String fetchUrl = apiUrl + "?" + queryString;

        RequestFuture<String> future = RequestFuture.newFuture();
        StringRequest request = new StringRequest(fetchUrl, future, future);
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

    @SuppressWarnings("deprecation")
    private static String getEncodedQueryString(String apiUrl, Map<String, String> paramMap) {
        StringBuilder queryBuilder = new StringBuilder();
        for (Entry<String, String> entry : paramMap.entrySet()) {
            queryBuilder.append("&");
            queryBuilder.append(entry.getKey());
            queryBuilder.append("=");
            try {
                queryBuilder.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                queryBuilder.append(URLEncoder.encode(entry.getValue()));
            }
        }
        queryBuilder.append("&aid=");
        queryBuilder.append(CHARGE);
        queryBuilder.append("&ch=");
        queryBuilder.append(CHANNEL);
        return queryBuilder.toString();
    }

    public static String getCityAlice(String city) {
        InputStream in = null;
        BufferedReader br = null;
        String encoding = "utf-8";

        if (TextUtils.isEmpty(city))
            return "";

        String cityAlice = "";
        try {
            in = ContactsApp.getInstance().getAssets().open("putao_city58_cities.txt");
            br = new BufferedReader(new InputStreamReader(in, encoding));
            String line = null;
            while ((line = br.readLine()) != null) {
                if (TextUtils.isEmpty(line)) {
                    continue;
                }
                String elements[] = line.split("\t");
                if (elements == null || elements.length < 2)
                    continue;

                if (city.equals(elements[0])) {
                    cityAlice = elements[1];
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
                if (in != null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return cityAlice;
    }
}
