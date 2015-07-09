
package so.contacts.hub.http;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import so.contacts.hub.util.LogUtil;

public class WebServiceUtils {

    public static String BuildReqStr(String token, String express_company, String express_no) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                + "<s:Body s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" "
                + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">"
                + "<q1:exec xmlns:q1=\"urn:kuaidihelp_dts\">" + "<request xsi:type=\"xsd:string\">"
                + "&lt;request&gt;&lt;header&gt;&lt;"
                + "service_name&gt;express.get&lt;/service_name&gt;&lt;"
                + "partner_name&gt;putao&lt;/partner_name&gt;&lt;" + "time_stamp&gt;"
                + sdf.format(new Date()) + "&lt;/time_stamp&gt;&lt;"
                + "version&gt;v1&lt;/version&gt;&lt;" + "format&gt;json&lt;/format&gt;&lt;"
                + "token&gt;" + token + "&lt;/token&gt;&lt;" + "/header&gt;&lt;" + "body&gt;&lt;"
                + "express_company&gt;" + express_company + "&lt;/express_company&gt;&lt;"
                + "express_no&gt;" + express_no + "&lt;/express_no&gt;&lt;" + "/body&gt;&lt;"
                + "/request&gt;" + "</request></q1:exec></s:Body></s:Envelope>";
    }

    public static String queryExpress(String requestStr) {
        LogUtil.d("ExpressSmartMatchUtil", "queryExpress->" + requestStr);
        HttpURLConnection conn = null;
        ByteArrayOutputStream out = null;
        String result = "";
        try {
            byte[] xmlbyte = requestStr.toString().getBytes("UTF-8");
            URL url = new URL("http://dts.kuaidihelp.com/webService/dts.php");

            conn = (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(60 * 1000);
            conn.setDoOutput(true);// 允许输出
            conn.setDoInput(true);
            conn.setUseCaches(false);// 不使用缓存
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Length", String.valueOf(xmlbyte.length));
            conn.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");

            conn.getOutputStream().write(xmlbyte);
            conn.getOutputStream().flush();
            conn.getOutputStream().close();

            conn.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;

            while ((line = in.readLine()) != null) {
                result += line;
            }
            int firstIndex = result.indexOf("{");
            int endIndex = result.lastIndexOf("}");
            result = result.substring(firstIndex, endIndex + 1);
        } catch (Exception e) {
            LogUtil.e("ExpressSmartMatchUtil", "connect exception: " + e);
            e.printStackTrace();
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (Exception ex) {
            }
        }
        return result;
    }

    public static String getMd5Token(String plainText) {
        String token = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();

            int i;

            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            token = buf.toString();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return token;
    }

    public static String getOutTradeNo(String mobile,double pay,String dev_no) {
        String result = "";

        String uri = "http://android1.putao.so/PT_SERVER/create_order_no.s?";
        uri += "mobile="+mobile; 
        uri += "&pay="+pay; 
        uri += "&dev_no="+dev_no; 
        uri += "&timestemp="+System.currentTimeMillis(); 
        
        HttpGet httpGet = new HttpGet(uri);
//        HttpParams httpParams = httpGet.getParams();
//        httpParams.setParameter("mobile", mobile);
//        httpParams.setDoubleParameter("pay", pay);
//        httpParams.setParameter("dev_no", dev_no);
//        httpParams.setLongParameter("timestemp", System.currentTimeMillis());
        HttpClient client = new DefaultHttpClient();
        try {
            HttpResponse response = client.execute(httpGet);
            InputStream inputStream = response.getEntity().getContent();
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        try {
//            URL url = new URL(
//                    "http://android1.putao.so/PT_SERVER/create_order_no.s?mobile=13582311857&pay=100&dev_no=1234567&timestemp=155555");
//            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
//            conn.setConnectTimeout(60 * 1000);
//            conn.setDoOutput(true);
//            conn.setDoInput(true);
//            conn.setUseCaches(false);
//            conn.setRequestMethod("GET");
//            conn.setRequestProperty("Connection", "Keep-Alive");
//            conn.setRequestProperty("Charset", "UTF-8");
//
//            if (conn.getResponseCode() == 200) {
//                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                String line;
//                while ((line = in.readLine()) != null) {
//                    result += line;
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return result;
    }

}
