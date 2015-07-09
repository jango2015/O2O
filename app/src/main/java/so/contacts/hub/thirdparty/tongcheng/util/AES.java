package so.contacts.hub.thirdparty.tongcheng.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

public class AES {

	/**
	 * 此处使用AES-128-ECB加密模式，key需要为16位。
	 * 注：密钥就是： 数字签名
	 */
	private static final String TEST_HOTEL_PAY_KEY = "2aae5d23442f8b9df53670fc315d8ff6";
	
	public static String Encrypt(String sSrc){
		return Encrypt(sSrc, TEST_HOTEL_PAY_KEY);
	}
	
	// 加密
	public static String Encrypt(String sSrc, String sKey){
		if (sKey == null) {
			System.out.print("Key is null.");
			return null;
		}
		// 判断Key是否为32位
		if (sKey.length() != 32) {
			System.out.print("Key length is not 16.");
			return null;
		}
		String encrypt = null;
		try{
			byte[] raw = sKey.getBytes("utf-8");
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");// "算法/模式/补码方式"
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
			// 此处使用BASE64做转码功能，同时能起到2次加密的作用。
			encrypt = Base64.encodeToString(encrypted, Base64.DEFAULT);
		}catch(Exception e){
			encrypt = null;
		}
		return encrypt;
	}

	public static String Decrypt(String sSrc) {
		return Decrypt(sSrc, TEST_HOTEL_PAY_KEY);
	}
	
	// 解密
	private static String Decrypt(String sSrc, String sKey) {
		try {
			// 判断Key是否正确
			if (sKey == null) {
				System.out.print("Key is null.");
				return null;
			}
			// 判断Key是否为16位
			if (sKey.length() != 16) {
				System.out.print("Key length is not 16.");
				return null;
			}
			byte[] raw = sKey.getBytes("utf-8");
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			// 先用base64解密
			byte[] encrypted1 = Base64.decode(sSrc, Base64.DEFAULT);
			try {
				byte[] original = cipher.doFinal(encrypted1);
				String originalString = new String(original, "utf-8");
				return originalString;
			} catch (Exception e) {
				System.out.println(e.toString());
				return null;
			}
		} catch (Exception ex) {
			System.out.println(ex.toString());
			return null;
		}
	}

	public static void main(String[] args) throws Exception {
		// 需要加密的字串
		String cSrc = "test";
		System.out.println(cSrc);
		// 加密
		String enString = AES.Encrypt(cSrc, TEST_HOTEL_PAY_KEY);
		System.out.println("the Encrypt string is：" + enString);

		// 解密
		String DeString = AES.Decrypt(enString, TEST_HOTEL_PAY_KEY);
		System.out.println("the decrypt string is：" + DeString);
	}
}
