package so.contacts.hub.thirdparty.cinema.tool;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import so.contacts.hub.thirdparty.cinema.utils.GewaApiReqMethod;
import so.contacts.hub.thirdparty.cinema.xmlparse.CinemaXMLParser;

public class CinemaApiUtilHelper {
    
    public static Object doHttpGetObjFromUrl(String url,GewaApiReqMethod method){
        HttpURLConnection conn = null;
        InputStream in = null;
        OutputStream out = null;
        Object obj = null;
        try {
            URL u = new URL(url);
            System.out.println("http url = " + url);
            conn = (HttpURLConnection) u.openConnection();
//            conn.setRequestProperty("Accept-Encoding", "gzip");
            conn.setRequestProperty("Content-Type", "text/xml");
            conn.setRequestProperty("Connection", "keep-alive");
            
//            conn.setDoInput(true);
//            conn.setDoOutput(true);
            conn.setConnectTimeout(20000);
            conn.setReadTimeout(30000);
            int status = conn.getResponseCode();
            System.out.println("http code = " + status);
            if (status == 200) {
                in = conn.getInputStream();
                obj = CinemaXMLParser.parseXML(in, method);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return obj;
    }
}
