package so.contacts.hub.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yulong.android.contacts.discover.R;

import so.contacts.hub.core.ConstantsParameter;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

public class TelAreaUtil {
	
	/*****
	  bugId:[BUG #338 通话记录列表向下翻页滑动卡顿]
	  remark:[增加num_area_map用于对已经查询到的号码进行缓存]
	  author_time:[haole_2014-02-15 19:39]
	*/
	private static Map<String,String> num_area_map = new HashMap<String,String>();

	private static final String INDEXDATAFILE = "putao_telArea.sf";
	private static Hearder cacheHearder = null;
	private static AreaCode cacheAreaCode = null;
	private static String cacheIndexFilePath = null;

	// 运营商 0-移动 1-联通 2-电信
	static Map<String, String> netWorkMap = new HashMap<String, String>();

	static {
		netWorkMap.put("134", "0");
		netWorkMap.put("135", "0");
		netWorkMap.put("136", "0");
		netWorkMap.put("137", "0");
		netWorkMap.put("138", "0");
		netWorkMap.put("139", "0");
		netWorkMap.put("147", "0");
		netWorkMap.put("150", "0");
		netWorkMap.put("151", "0");
		netWorkMap.put("152", "0");
		netWorkMap.put("157", "0");
		netWorkMap.put("158", "0");
		netWorkMap.put("159", "0");
		netWorkMap.put("182", "0");
		netWorkMap.put("183", "0");
		netWorkMap.put("184", "0");
		netWorkMap.put("187", "0");
		netWorkMap.put("188", "0");
		
		netWorkMap.put("130", "1");
		netWorkMap.put("131", "1");
		netWorkMap.put("132", "1");
		netWorkMap.put("155", "1");
		netWorkMap.put("156", "1");
		netWorkMap.put("185", "1");
		netWorkMap.put("186", "1");
		
		netWorkMap.put("133", "2");
		netWorkMap.put("153", "2");
		netWorkMap.put("180", "2");
		netWorkMap.put("181", "2");
		netWorkMap.put("189", "2");
	}
	
	public String getNetwork(String number, Context context) {
		number  = ContactsHubUtils.formatIPNumber(number, context);
		String network = "";
		//Context context = Library.Instance().getApplication();
		for (String key : netWorkMap.keySet()) {
			if (number.startsWith(key)) {
				String value = netWorkMap.get(key);
				if ("0".equals(value)) {
					network = context.getResources().getString(R.string.putao_move);//"移动";
				} else if ("1".equals(value)) {
					network =context.getResources().getString(R.string.putao_unicom); //"联通";
				} else {
					network =context.getResources().getString(R.string.putao_telecom);// "电信";
				}
				break;
			}
		}
		return network;
	}

	private static TelAreaUtil telAreaUtil = null;

	private TelAreaUtil() {

	}

	public static TelAreaUtil getInstance() {
		if (telAreaUtil == null) {
			telAreaUtil = new TelAreaUtil();
		}
		return telAreaUtil;
	}

	// Context c;
	//
	// private TelAreaUtil(Context c){
	// this.c = c;
	// }
	
	
	/**
	 * 检查是否是合法的电话号码
	 */
	public boolean isValidMobile(String mobile) {
		/*
	      中国移动号段：134、135、136、137、138、139、150、151、152、157、158、159、182、183、184、187、188、178(4G)、147(上网卡)
	      中国电信号段：133、153、180、181、189 、177(4G)
	      中国联通号段：130、131、132、155、156、185、186、176(4G)、145(上网卡)
	      卫星通信号段：1349
	      虚拟运营商号段：170
	      */
		// 过滤总号段： 13[0-9] , 14[5 7] , 15[0-9] , 17[0 6 7 8] 18[0-9]
		String MOBILE = "^1(3[0-9]|4[57]|5[0-9]|7[0678]|8[0-9])\\d{8}$";
		Pattern pattern = Pattern.compile(MOBILE);
		Matcher matcher = pattern.matcher(mobile);
		return matcher.matches();
	}

	public void copyFile(Context c) throws Exception {
//		InputStream inStream = this.getClass().getClassLoader()
//				.getResourceAsStream(INDEXDATAFILE);
		InputStream inStream = c.getResources().getAssets().open(INDEXDATAFILE);
		File dbFile = new File(c.getFilesDir(), INDEXDATAFILE);
		FileOutputStream outStream = new FileOutputStream(dbFile);
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		outStream.close();
		inStream.close();
		
		updateTelAreaVersionCode(c);
	}

	private void updateTelAreaVersionCode(Context c) {
		SharedPreferences preferences = c.getSharedPreferences(
				ConstantsParameter.CONTACTS_SETTING, Context.MODE_MULTI_PROCESS);
		// 获取当前系统版本号
		String currentCode = ContactsHubUtils.getVersionCode(c);
		// 更新归属地的版本号
		preferences.edit().putString("versionCode", currentCode).commit();
	}

	public boolean isExsit(Context c) {
		File dbFile = new File(c.getFilesDir(), INDEXDATAFILE);
		if (dbFile.exists()) {
			String versionCode = getLastSavedVersionCode(c);
			// 获取当前系统版本号
			String curreneCode = ContactsHubUtils.getVersionCode(c);
			if (!curreneCode.equals(versionCode)) {// 版本不一致
				// 清除本地的归属地文件
				dbFile.delete();
			}
		}
		File predbFile = new File(c.getFilesDir(), "tel_test.sf");
		if (predbFile.exists()) {
			predbFile.delete();
		}
		predbFile = new File(c.getFilesDir(), "tel_area.sf");
		if (predbFile.exists()) {
			predbFile.delete();
		}
		return dbFile.exists();
	}

	private String getLastSavedVersionCode(Context c) {
		SharedPreferences preferences = c.getSharedPreferences(
				ConstantsParameter.CONTACTS_SETTING, Context.MODE_MULTI_PROCESS);
		// 获取上次保存的系统版本号
		String versionCode = preferences.getString("versionCode", "1");
		return versionCode;
	}

	public String searchTel(String telNum, Context c) {
	    if(c==null){
	        return "";
	    }
		if (!isExsit(c)) {
			try {
				copyFile(c);
			} catch (Exception e) {
				e.printStackTrace();
				return "";
			}
		}
		if (TextUtils.isEmpty(telNum))
			return null;
		if (!telNum.matches("[+]?[0-9]+"))
			return null;
		String indexFilePath = c.getFilesDir().getAbsolutePath();
		String address = "";
		try {
			address = searchTel(indexFilePath, telNum, false);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (TextUtils.isEmpty(address)) {
				if (telNum.equals("10086")) {
					address = c.getResources().getString(R.string.putao_mobile_service);//"中国移动客服";
				} else if (telNum.equals("10010")) {
					address = c.getResources().getString(R.string.putao_unicom_service);//"中国联通客服";
				} else if (telNum.equals("10000")) {
					address = c.getResources().getString(R.string.putao_telecom_service);//"中国电信客服";
				} else {
					address = telNum;
				}
			} else {
				if (address == null || "null".equalsIgnoreCase(address))
					address = "("+c.getResources().getString(R.string.putao_unknow_name)+")";//"(未知)";
				if (address.contains("_")) {
					String ads[] = address.split("_");
					if (ads[0].equals(ads[1])) {
						address = ads[0];
					} else {
						address = address.replaceAll("_", " ");
					}
				}
			}
		}
		if (telNum.equals(address))
			return "";
		return address;
	}

	/** * search * */
	private String searchTel(String indexFilePath, String telNum,
			boolean forecast) {
		StringBuffer sb = new StringBuffer(telNum);
		// +
		if (sb.charAt(0) == '+') {
			sb.deleteCharAt(0);
		}
		// 86
		if (sb.charAt(0) == '8' && sb.charAt(1) == '6') {
			sb.delete(0, 2);
		}
		// 以0开头，是区号
		if (sb.charAt(0) == '0') {
			sb.deleteCharAt(0);
			// 首先按照4位区号查询，若查询为空，再按3位区号查询
			if (sb.length() >= 3) {
				sb.delete(3, sb.length());
			}
			String dial = searchTel(indexFilePath, Long.valueOf(sb.toString()),
					false);
			if (dial != null) {
				return dial;
			}
			if (sb.length() >= 2) {
				sb.delete(2, sb.length());
			}
		}
		// 以1开头，是手机号或者服务行业号码
		else if (sb.charAt(0) == '1') {
			// 首先按照手机号码查询，若查询为空，再按特殊号码查询
			/*****
			  bugId:[BUG #338 通话记录列表向下翻页滑动卡顿]
			  remark:[只对手机号码进行搜索，对1开头的特服号码不做处理,减少调用searchTel调用的开销]
			  author_time:[haole_2014-02-15 19:44]
			*/
			if (sb.length() > 7) {
//				String dial = searchTel(indexFilePath,
//						Long.valueOf(sb.substring(0, 8)), false);
//				if (dial != null) {
//					return dial;
//				}
				// 只需要保留7位号码就ok了，多余的删掉
//				if (sb.length() > 7) {
					sb.delete(7, sb.length());
//				}
			} else {
				// 小于7位，最有可能是服务号码
				// do nothing.
			}
		}
		// 以其他数字开头，这也不知道是啥号码了
		else {
			return null;
		}
		return searchTel(indexFilePath, Long.parseLong(sb.toString()), forecast);
	}

	private String searchTel(String indexFilePath, long telNum, boolean forecast) {
		/*****
		  bugId:[BUG #338 通话记录列表向下翻页滑动卡顿]
		  remark:[先判断缓存map中是否有该号段的信息，如果有，不进行查询，直接返回缓存值]
		  author_time:[haole_2014-02-15 19:47]
		*/
		if(num_area_map.containsKey(String.valueOf(telNum))){
			return num_area_map.get(String.valueOf(telNum));
		}
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(indexFilePath + "/" + INDEXDATAFILE, "r");
			if (cacheIndexFilePath == null
					|| !cacheIndexFilePath.equals(indexFilePath)) {
				cacheIndexFilePath = indexFilePath;
				cacheHearder = new Hearder();
				cacheHearder.read(raf);
				cacheHearder.print();
				cacheAreaCode = new AreaCode();
				cacheAreaCode.read(raf);
				cacheAreaCode.print();
			}
			int index = lookUP(raf, cacheHearder.firstRecordOffset,
					cacheHearder.lastRecordOffset, telNum, forecast);
			/*****
			  bugId:[BUG #338 通话记录列表向下翻页滑动卡顿]
			  remark:[缓存中不存在该值，则将查询结果缓存到缓存map中,以便后期使用]
			  author_time:[haole_2014-02-15 19:47]
			*/
			String addr = cacheAreaCode.getCodeByIndex(index);
			if(null!=addr&&!"".equals(addr)){
				num_area_map.put(String.valueOf(telNum), addr);
			}
			return addr;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (raf != null) {
				try {
					raf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return null;
	}

	private int lookUP(RandomAccessFile raf, long startpos, long endpos,
			long looknum, boolean forecast) throws IOException {
		Record record = new Record();
		long seekpos = 0;

		do {
			seekpos = startpos + (endpos - startpos) / record.Size() / 2
					* record.Size();
			raf.seek(seekpos);
			record.read(raf);
			int whichNum = record.inWhich(looknum);
			if (whichNum > 0) {
				startpos = seekpos + record.Size();
			} else if (whichNum < 0) {
				endpos = seekpos - record.Size();
			} else {
				return record.areacodeIndex;
			}
		} while (startpos <= endpos);

		if (forecast) {
			return record.areacodeIndex;
		} else {
			return -1;
		}
	}

	class Hearder {
		int firstRecordOffset;
		int lastRecordOffset;

		public int Size() {
			return (Integer.SIZE + Integer.SIZE) / Byte.SIZE;
		}

		public void write(RandomAccessFile raf) throws IOException {
			raf.writeInt(this.firstRecordOffset);
			raf.writeInt(this.lastRecordOffset);
		}

		public void read(RandomAccessFile raf) throws IOException {
			this.firstRecordOffset = raf.readInt();
			this.lastRecordOffset = raf.readInt();
		}

		public void print() {
			// System.out.println("===== Hearder ===== ");
			// System.out.println("[" + firstRecordOffset + " , " +
			// lastRecordOffset + "]");
		}
	}

	class AreaCode {
		private String areacode;
		private String[] codes;

		public AreaCode() {
			this("");
		}

		public AreaCode(String areacode) {
			this.areacode = areacode;
			this.codes = null;
		}

		public int Size() {
			return areacode.getBytes().length + (Integer.SIZE / Byte.SIZE);
		}

		public void print() {
			// System.out.println("===== AreaCode ===== ");
			// System.out.println("[" + areacode.getBytes().length + "]" +
			// areacode);
		}

		public void write(RandomAccessFile raf) throws IOException {
			raf.writeInt(areacode.getBytes().length);
			raf.write(this.areacode.getBytes());
		}

		public void read(RandomAccessFile raf) throws IOException {
			byte[] bytes = new byte[raf.readInt()];
			raf.read(bytes);
			this.areacode = new String(bytes);
		}

		public String getCodeByIndex(int index) {
			if (this.codes == null) {
				this.codes = this.areacode.split(",");
			}
			return (index < 0 || this.codes == null || index >= this.codes.length) ? null
					: this.codes[index];
		}
	}

	class Record {
		long baseTelNum;
		int numCnt;
		int areacodeIndex;

		public Record() {
			this(0, 0, 0);
		}

		public Record(long baseTelNum, int numCnt, int areacodeIndex) {
			this.baseTelNum = baseTelNum;
			this.numCnt = numCnt;
			this.areacodeIndex = areacodeIndex;
		}

		public void print() {
			// System.out.println("===== Record ===== ");
			// System.out.println("<" + baseTelNum + "> <" + numCnt + "> <" +
			// areacodeIndex + ">");
		}

		public int Size() {
			return (Long.SIZE + Integer.SIZE) / Byte.SIZE;
		}

		public void write(RandomAccessFile raf) throws IOException {
			raf.writeLong(this.baseTelNum);
			int tmp = this.numCnt << 16;
			tmp += 0xFFFF & this.areacodeIndex;
			raf.writeInt(tmp);
		}

		public void read(RandomAccessFile raf) throws IOException {
			this.baseTelNum = raf.readLong();
			int tmp = raf.readInt();
			this.numCnt = tmp >> 16;
			this.areacodeIndex = 0xFFFF & tmp;
		}

		public int inWhich(long telNum) {
			if (telNum < this.baseTelNum) {
				return -1;
			} else if (telNum >= this.baseTelNum + this.numCnt) {
				return 1;
			} else {
				return 0;
			}
		}
	}
}
