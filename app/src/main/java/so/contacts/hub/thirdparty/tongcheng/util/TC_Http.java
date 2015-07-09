package so.contacts.hub.thirdparty.tongcheng.util;

import java.net.*;
import java.io.*;
import so.contacts.hub.thirdparty.tongcheng.xmlparse.TC_XMLParser;

public class TC_Http {
	
	private static final int TC_CONNECT_TIMEOUT = 60000;

	private static final int TC_READ_TIMEOUT = 60000;
	
	public static Object getDataByPost(String url, String data, Class<?> clz){
		return getData("POST", url, data, clz);
	}
	
	public static Object getData(String method, String url, String data, Class<?> clz){
		Object object = null;
		HttpURLConnection conn = null;
		InputStream inputStream = null;
		OutputStream out = null;
		try {
			URL u = new URL(url);
			conn = (HttpURLConnection) u.openConnection();
			conn.setRequestProperty("Accept-Encoding", "gzip");
			conn.setRequestProperty("Connection", "keep-alive");

			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setConnectTimeout(TC_CONNECT_TIMEOUT);
			conn.setReadTimeout(TC_READ_TIMEOUT);

			if (method.equals("POST")) {
				byte[] sendbyte = data.getBytes("UTF-8");
				out = conn.getOutputStream();
				out.write(sendbyte);
			}

			int status = conn.getResponseCode();
			if (status == 200) {
				inputStream = conn.getInputStream();
			}
			if( inputStream == null ){
				return null;
			}
			
			object = TC_XMLParser.parseXML(inputStream, clz);
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			try {
				if (conn != null) {
					conn.disconnect();
				}
				if( inputStream != null ){
					inputStream.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return object;
	}

	public static String getData(String method, String url, String data) {
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
			conn.setConnectTimeout(TC_CONNECT_TIMEOUT);
			conn.setReadTimeout(TC_READ_TIMEOUT);

			if (method.equals("POST")) {
				byte[] sendbyte = data.getBytes("UTF-8");
				out = conn.getOutputStream();
				out.write(sendbyte);
			}

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
				System.out.println("http code = " + status);
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
}