package so.contacts.hub.thirdparty.elong.tool;

import java.util.Calendar;
import java.util.Date;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import java.net.Socket;

import javax.net.ssl.X509TrustManager;
import javax.net.ssl.TrustManager;

import java.security.KeyManagementException;
import java.security.UnrecoverableKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.conn.ssl.SSLSocketFactory;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.protocol.HTTP;
import org.apache.http.HttpVersion;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpStatus;

import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.http.HttpResponse;
import org.apache.http.params.HttpProtocolParams;

import java.security.KeyStore;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.HttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.UMengEventIds;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ELongApiUtilHelper {

	public static String doHttpGet(String url) {
		HttpURLConnection conn = null;
		InputStream in = null;
		InputStreamReader isr = null;
		OutputStream out = null;
		StringBuffer result = null;
		try {
			URL u = new URL(url);
			conn = (HttpURLConnection) u.openConnection();
			conn.setRequestProperty("Accept-Encoding", "gzip");
			// conn.setRequestProperty("Content-Type", "");
			conn.setRequestProperty("Connection", "keep-alive");
			
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setConnectTimeout(60000);
			conn.setReadTimeout(60000);

			int status = conn.getResponseCode();
			if (status == 200) {
				String enc = conn.getContentEncoding();
				result = new StringBuffer();
				in = conn.getInputStream();
				enc = conn.getContentEncoding();

				if (enc != null && enc.equals("gzip")) {
					java.util.zip.GZIPInputStream gzin = new java.util.zip.GZIPInputStream(
							in);
					isr = new InputStreamReader(gzin, "UTF-8");

				} else {
					isr = new InputStreamReader(in, "UTF-8");
				}

				char[] c = new char[1024];
				int a = isr.read(c);
				while (a != -1) {
					result.append(new String(c, 0, a));
					a = isr.read(c);
				}
			} else {
				LogUtil.e("elong", "http code = " + status);
				// add by putao_lhq 2014年10月11日 for 接入商错误上报 start
	            UMengEventIds.reportSearchError("elong", new Exception("http code = " + status));
	            // add by putao_lhq 2014年10月11日 for 接入商错误上报 end
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
			// add by putao_lhq 2014年10月11日 for 接入商错误上报 start
            UMengEventIds.reportSearchError("elong", e);
            // add by putao_lhq 2014年10月11日 for 接入商错误上报 end
		} catch (IOException e) {
			e.printStackTrace();
			// add by putao_lhq 2014年10月11日 for 接入商错误上报 start
            UMengEventIds.reportSearchError("elong", e);
            // add by putao_lhq 2014年10月11日 for 接入商错误上报 end
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
			try {
				if (in != null) {
					in.close();
				}
				if (isr != null) {
					isr.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return result == null ? null : result + "";
	}
	
	private static final int CONNECTION_TIMEOUT = 10000;
	
	public static String doHttpsGet(String serverURL) throws Exception {
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpParameters, CONNECTION_TIMEOUT);
        HttpClient hc = initHttpClient(httpParameters);
        HttpGet get = new HttpGet(serverURL);
        get.addHeader("Content-Type", "text/xml");
        get.setParams(httpParameters);
        HttpResponse response = null;
        try {
            response = hc.execute(get);
        } catch (UnknownHostException e) {
            throw new Exception("Unable to access " + e.getLocalizedMessage());
        } catch (SocketException e) {
            throw new Exception(e.getLocalizedMessage());
        }
        int sCode = response.getStatusLine().getStatusCode();
        if (sCode == HttpStatus.SC_OK) {
            return EntityUtils.toString(response.getEntity());
        } else
            throw new Exception("StatusCode is " + sCode);
    }
	
	private static HttpClient initHttpClient(HttpParams params) {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new SSLSocketFactoryImp(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient(params);
        }
    }
	
	private static class SSLSocketFactoryImp extends SSLSocketFactory {
        final SSLContext sslContext = SSLContext.getInstance("TLS");

        public SSLSocketFactoryImp(KeyStore truststore) throws NoSuchAlgorithmException,
                KeyManagementException, KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain,
                        String authType) throws java.security.cert.CertificateException {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain,
                        String authType) throws java.security.cert.CertificateException {
                }
            };
            sslContext.init(null, new TrustManager[] {
                tm
            }, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose)
                throws IOException, UnknownHostException {
            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }
	
	public static Date parseDate(String dateStr, String format) {
		java.util.Date date = null;
		try {
			java.text.DateFormat df = new java.text.SimpleDateFormat(format);
			String dt = dateStr.replaceAll("-", "/");
			if ((!dt.equals("")) && (dt.length() < format.length())) {
				dt += format.substring(dt.length()).replaceAll("[YyMmDdHhSs]",
						"0");
			}
			date = (java.util.Date) df.parse(dt);
		} catch (Exception e) {
		}
		return date;
	}

	public static Date parseDate(String dateStr) {
		return parseDate(dateStr, "yyyy-MM-dd");
	}

	public static long getMillis(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.getTimeInMillis();
	}

	public static Date addDate(Date date, int day) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(getMillis(date) + ((long) day) * 24 * 3600 * 1000);
		return c.getTime();
	}

	public static String md5(String str) {
		MessageDigest messageDigest = null;

		try {
			messageDigest = MessageDigest.getInstance("MD5");

			messageDigest.reset();

			messageDigest.update(str.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			System.out.println("NoSuchAlgorithmException caught!");
			System.exit(-1);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		byte[] byteArray = messageDigest.digest();

		StringBuffer md5StrBuff = new StringBuffer();

		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				md5StrBuff.append("0").append(
						Integer.toHexString(0xFF & byteArray[i]));
			else
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
		}

		return md5StrBuff.toString();
	}

	public static String encodeUri(String url) {

		try {
			return java.net.URLEncoder.encode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return url;
	}

	public static String encryptDES(String message, String key) throws Exception {
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");

		DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));

		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
		IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

		return toHexString(cipher.doFinal(message.getBytes("UTF-8")));
	}

	public static String decryptDES(String message, String key) throws Exception {

		byte[] bytesrc = convertHexString(message);
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
		IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));

		cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

		byte[] retByte = cipher.doFinal(bytesrc);
		return new String(retByte);
	}

	private static byte[] convertHexString(String ss) {
		byte digest[] = new byte[ss.length() / 2];
		for (int i = 0; i < digest.length; i++) {
			String byteString = ss.substring(2 * i, 2 * i + 2);
			int byteValue = Integer.parseInt(byteString, 16);
			digest[i] = (byte) byteValue;
		}

		return digest;
	}

	private static String toHexString(byte b[]) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			String plainText = Integer.toHexString(0xff & b[i]);
			if (plainText.length() < 2)
				plainText = "0" + plainText;
			hexString.append(plainText);
		}

		return hexString.toString();
	}
	
}
