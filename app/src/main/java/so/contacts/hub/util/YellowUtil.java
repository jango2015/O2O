package so.contacts.hub.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import so.contacts.hub.ContactsApp;
import so.contacts.hub.city.CityBean;
import so.contacts.hub.core.ConstantsParameter;
import so.contacts.hub.db.MovieDB;
import so.contacts.hub.db.YellowPageDB;
import so.contacts.hub.db.MovieDB.MovieCityTable;
import so.contacts.hub.thirdparty.cinema.bean.MovieCity;
import so.contacts.hub.thirdparty.cinema.tool.CinemaApiUtil;
import so.contacts.hub.train.YellowPageTrainDB;
import so.contacts.hub.train.bean.TongChengCity;
import so.contacts.hub.ui.yellowpage.bean.CategoryBean;
import so.contacts.hub.ui.yellowpage.bean.Express;
import so.contacts.hub.ui.yellowpage.bean.ItemBean;
import so.putao.findplug.LBSServiceGaode;
import so.putao.findplug.LBSServiceGaode.LBSServiceListener;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;

public class YellowUtil {
	private static final String TAG = "YellowUtil";

	public static final String TargetActivity = "TargetActivity";
	public static final String TargetParams = "TargetParams";

	/**
	 *start modified by zjh at 2014/09/12
	 *黄页静态数据 部分常量标识
	 */
	// 黄页静态数据 parentId = 0 
	public static final int YELLOW_PAGE_PARENTID_ALL = 0;
	
	// 黄页静态数据 "全部"的 categoryId
	public static final int YELLOW_PAGE_CATEGORY_ID_ALL = 1;

	// 黄页静态数据 "常用"的 categoryId
	public static final int YELLOW_PAGE_CATEGORY_ID_OFFEN = 2;
	
	// 黄页静态数据 默认lastSort = -1;
	public static final int YELLOW_CATEGORY_DEFAULT_LASTSORT = -1;
	
	// 黄页静态数据 默认remindCode = -1;
	public static final int YELLOW_CATEGORY_DEFAULT_REMIND_CODE = -1;
	
	/**
	 * 编辑类型，通过该字段可扩展更多功能
	 * editType = 0：默认值
	 * editType = 1：不可删除
	 * editType = 2：用户添加类型
	 */
	public static final int YELLOW_CATEGORY_EDITTYPE_DEFAULT = 0;
	public static final int YELLOW_CATEGORY_EDITTYPE_NOT_DEL = 1;
	public static final int YELLOW_CATEGORY_EDITTYPE_USER_ADD = 2;
	
    /**
     * 被改变类型
     * change_type = 0：默认值
     * change_type = 1：由用户改变
     * change_type = 2：由服务器改变
     */
    public static final int YELLOW_CATEGORY_CHANGE_TYPE_DEFAULT = 0;
    public static final int YELLOW_CATEGORY_CHANGE_TYPE_USER_MODITY = 1;
    public static final int YELLOW_CATEGORY_CHANGE_TYPE_SERVICE_MODITY = 2;
	
	/**
	 * end modified by zjh at 2014/09/12
	 */

	public static final String TargetIntentParams = "TargetIntentParams"; // intent传输target_params参数

	public static final String DefCategoryActivity = so.contacts.hub.ui.yellowpage.YellowPageCategoryActivity.class
			.getName(); // 显示类别和列表activity
	public static final String DefWubaCityActivity = so.contacts.hub.ui.yellowpage.YellowPageWubaCityActivity.class
			.getName(); // 显示58同城activity

	public static String selectedCity(Context context) {
		SharedPreferences pref = context.getSharedPreferences(
				ConstantsParameter.SHARED_PREFS_YELLOW_PAGE,
				Context.MODE_MULTI_PROCESS);
		return pref.getString(ConstantsParameter.YELLOW_PAGE_SELECTED_CITY, "");
	}

	public static void saveCity(Context context, String name) {
		SharedPreferences pref = context.getSharedPreferences(
				ConstantsParameter.SHARED_PREFS_YELLOW_PAGE,
				Context.MODE_MULTI_PROCESS);
		Editor e = pref.edit();
		e.putString(ConstantsParameter.YELLOW_PAGE_SELECTED_CITY, name);
		e.commit();
	}
	
	//判断是否为电话号码或者座机号码包括区号以及分机号码，返回布尔值  
    public static boolean isNumeric(String input){
        if("360".equals(input) || "361".equals(input)){
            return false;
        }
        return PhoneNumberUtils.isGlobalPhoneNumber(input);
//        String regex="1([\\d]{10})|((\\+[0-9]{2,4})?\\(?[0-9]+\\)?-?)?[0-9]{7,8}";  
//        Pattern p = Pattern.compile(regex);  
//        return p.matcher(input).matches();  
    }

	/**
	 * 检查是否有黄页静态数据
	 * 
	 * @return
	 */
	public static boolean hasStaticYellowData() {
		YellowPageDB db = ContactsAppUtils.getInstance().getDatabaseHelper().getYellowPageDBHelper();
		return db.getCategoryCount(0) > 0 ? true : false;
	}

	/**
	 * 加载默认黄页类别静态数据
	 */
	public static void loadDefaultCategoryDB() {
		YellowPageDB db = ContactsAppUtils.getInstance().getDatabaseHelper().getYellowPageDBHelper();
		InputStream in = null;
		BufferedReader br = null;
		
		String encoding = "utf-8";
		try {
			in = ContactsApp.getInstance().getAssets().open("putao_category.txt");
			br = new BufferedReader(new java.io.InputStreamReader(in, encoding));

			int count = 0;
			String line = null;
			while ((line = br.readLine()) != null) {
				if (count++ == 0 || TextUtils.isEmpty(line)){
					continue;
				}

				String elements[] = line.split("\t");
				if(elements == null || elements.length < 12){
					continue;
				}
				
				CategoryBean cb = new CategoryBean();
				cb.setCategory_id(Long.parseLong(elements[0]));
				cb.setName(elements[1]);
				
				if (!TextUtils.isEmpty(elements[2])){
					cb.setShow_name(elements[2]);
				}
				if (!TextUtils.isEmpty(elements[3])){
					cb.setSort(Integer.parseInt(elements[3]));
				}
				if (!TextUtils.isEmpty(elements[4])){
					cb.setLastSort(Integer.parseInt(elements[4]));
				}
				
				if (!TextUtils.isEmpty(elements[5])){
					// 大图
					cb.setIcon(elements[5]);
				}
				if (!TextUtils.isEmpty(elements[6])){
					// 大图按下图标
					cb.setPressIcon(elements[6]);
				}
				if (!TextUtils.isEmpty(elements[7])){
					// 前缀小图标
					cb.setIconLogo(elements[7]);
				}
				
				if (!TextUtils.isEmpty(elements[8])){
					cb.setTarget_activity(elements[8]);
				}
				if (!TextUtils.isEmpty(elements[9])){
					cb.setTarget_params(elements[9]);
				}
				if (!TextUtils.isEmpty(elements[10])){
					cb.setParent_id(Long.parseLong(elements[10]));
				}
                if (!TextUtils.isEmpty(elements[11])){
                    cb.setRemind_code(Integer.parseInt(elements[11]));
                }
                if (elements.length >= 13 && !TextUtils.isEmpty(elements[12])){
                    cb.setKey_tag(elements[12]);
                }
                if (elements.length >= 14 && !TextUtils.isEmpty(elements[13])){
                    cb.setSearch_sort(Integer.parseInt(elements[13]));
                }
				
				db.insert(cb);
			}
			LogUtil.d(TAG, "loadDefaultCategoryDB total: " + count);
		} catch (IOException e) {
			e.printStackTrace();
			LogUtil.i(TAG, e.getMessage());
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
	}

	/**
	 * 加载默认黄页ITEM静态数据
	 */
	public static void loadDefaultItemDB() {
		YellowPageDB db = ContactsAppUtils.getInstance().getDatabaseHelper()
				.getYellowPageDBHelper();
		InputStream in = null;
		BufferedReader br = null;

		String encoding = "utf-8";
		int count = 0;		
		try {
			in = ContactsApp.getInstance().getAssets()
					.open("putao_category_item.txt");
			br = new BufferedReader(new java.io.InputStreamReader(in, encoding));

			String line = null;
			while ((line = br.readLine()) != null) {
				if (count++ == 0 || TextUtils.isEmpty(line)){
					continue;
				}
				String elements[] = line.split("\t");
				if (elements == null || elements.length < 9){
					continue;
				}

				ItemBean ib = new ItemBean();
				ib.setProvider(0); // 提供方，默认0葡萄
				ib.setItem_id(Long.parseLong(elements[0]));
				ib.setCategory_id(Long.parseLong(elements[1]));
				ib.setName(elements[2]); // 名字

				if (!TextUtils.isEmpty(elements[3])){
					ib.setIcon(elements[3]);
				}
				if (!TextUtils.isEmpty(elements[4])){
					ib.setSort(Integer.parseInt(elements[4]));
				}
				if (!TextUtils.isEmpty(elements[5])){
					ib.setTarget_activity(elements[5]);
				}
				if (!TextUtils.isEmpty(elements[6])){
					ib.setTarget_params(elements[6]);
				}
				if (!TextUtils.isEmpty(elements[7])){
					ib.setContent(elements[7]);
				}
				if (!TextUtils.isEmpty(elements[8])){
					ib.setRemind_code(Integer.parseInt(elements[8]));
				}
				if (elements.length >= 10 && !TextUtils.isEmpty(elements[9])){
					// 关键字标签
					ib.setKey_tag(elements[9]);
				}
                if (elements.length >= 11 && !TextUtils.isEmpty(elements[10])){
                    ib.setSearch_sort(Integer.parseInt(elements[10]));
                }
				
				db.insert(ib);
			}
			LogUtil.d(TAG, "loadDefaultItemDB total: " + count);
		} catch (IOException e) {
			e.printStackTrace();
			LogUtil.i(TAG, e.getMessage()+" count="+count);
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
	}

	public static void loadDefaultExpressDB() {
		YellowPageDB db = ContactsAppUtils.getInstance().getDatabaseHelper()
				.getYellowPageDBHelper();
		InputStream in = null;
		BufferedReader br = null;
		String encoding = "utf-8";

		try {
			in = ContactsApp.getInstance().getAssets()
					.open("putao_express.txt");
			br = new BufferedReader(new InputStreamReader(in, encoding));
			String line = null;
			while ((line = br.readLine()) != null) {
				if (TextUtils.isEmpty(line)) {
					continue;
				}
				String elements[] = line.split("\t");
				if (elements == null || elements.length < 4){
					continue;
				}
				Express express = new Express();
				express.setName(elements[0]);
				express.setPinyin(elements[1]);
				express.setLogo(elements[2]);
				express.setPhone(elements[3]);
				db.insertExpress(express);
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
	}
	
	/**
	 * 加载同城火车票火车站的数据
	 * add by lisheng start 2014-11-24 19:09:25
	 */
	public static void loadTrainTicketDB() {
		YellowPageTrainDB db = ContactsAppUtils.getInstance().getDatabaseHelper()
				.getTrainDBHelper();
		InputStream in = null;
		BufferedReader br = null;
		String encoding = "utf-8";
		List<TongChengCity> cityList = new ArrayList<TongChengCity>();
		try {
			in = ContactsApp.getInstance().getAssets()
					.open("putao_train_ticket.txt");
			br = new BufferedReader(new InputStreamReader(in, encoding));
			String line = null;
			while ((line = br.readLine()) != null) {
				if (TextUtils.isEmpty(line)) {
					continue;
				}
				String elements[] = line.split("\t");
				if(elements == null || elements.length == 0){
					continue;
				}
				TongChengCity city = new TongChengCity();
				city.setStationCode(elements[0]);
				city.setStationName(elements[1]);
				city.setQuanPin(elements[2]);
				city.setJianPin(elements[3]);
        		city.setStationPY(PinyinHelper.getInstance().getFullPinyin(elements[1]));
        		cityList.add(city);
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
		db.insertTongChengCityList(cityList);
	}
	
	/**
	 * 加载电影片数据
	 * add by hyl 2015-1-7 
	 */
	public static void loadMovieCityList(){
		InputStream in = null;
		BufferedReader br = null;
		String encoding = "utf-8";
		ArrayList<MovieCity> cityList = new ArrayList<MovieCity>();
		try {
			in = ContactsApp.getInstance().getAssets().open("putao_movie_city.txt");
			br = new BufferedReader(new InputStreamReader(in, encoding));
			String line = null;
			
			while ((line = br.readLine()) != null) {
				if (line == null || "".equals(line)) {
					continue;
				}
				String elements[] = line.split("\\|");
				if (elements == null || elements.length < 2){
					continue;
				}
				
				MovieCity movieCity = new MovieCity();
				movieCity.setCityname(elements[0]);
				movieCity.setCitycode(elements[1]);
				cityList.add(movieCity);
			}
			if(cityList.size() > 0){
				MovieDB db = ContactsAppUtils.getInstance().getDatabaseHelper().getMovieDB();
				db.clearTable(MovieCityTable.TABLE_NAME);
				db.insertMovieCity(cityList);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				if (br != null){
					br.close();
				}
				if (in != null){
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 读取所有 城市数据
	 * add zjh 2014-12-13
	 */
	public static void loadAllCityList() {
		InputStream in = null;
		BufferedReader br = null;
		String encoding = "utf-8";
		ArrayList<CityBean> cityList = new ArrayList<CityBean>();
		try {
			in = ContactsApp.getInstance().getAssets().open("putao_citylist.txt");
			br = new BufferedReader(new InputStreamReader(in, encoding));
			String line = null;
			while ((line = br.readLine()) != null) {
				if (line == null || "".equals(line)) {
					continue;
				}
				String elements[] = line.split("\t");
				if (elements == null || elements.length < 17){
					continue;
				}

				CityBean cityBean = new CityBean();
				cityBean.setCityName(elements[0]);
				cityBean.setCityPy(elements[1]);
				cityBean.setSelfId(Integer.valueOf(elements[2]));
				cityBean.setParentId(Integer.valueOf(elements[3]));
				cityBean.setCityType(Integer.valueOf(elements[4]));
				cityBean.setDistrictCode(elements[5]);
				cityBean.setCityHot(Integer.valueOf(elements[6]));
				
				cityBean.setWubaState(Integer.valueOf(elements[7]));
				if( !TextUtils.isEmpty(elements[8]) ){
					cityBean.setWubaCode(elements[8]);
				}
				
				cityBean.setElongState(Integer.valueOf(elements[9]));
				if( !TextUtils.isEmpty(elements[10]) ){
					cityBean.setElongCode(elements[10]);
				}
				
				cityBean.setTongchengState(Integer.valueOf(elements[11]));
				if( !TextUtils.isEmpty(elements[12]) ){
					cityBean.setTongchengCode(elements[12]);
				}
				
				cityBean.setGewaraState(Integer.valueOf(elements[13]));
				if( !TextUtils.isEmpty(elements[14]) ){
					cityBean.setGewaraCode(elements[14]);
				}
				
				cityBean.setGaodeState(Integer.valueOf(elements[15]));
				if( !TextUtils.isEmpty(elements[16]) ){
					cityBean.setGaodeCode(elements[16]);
				}
				
				cityList.add(cityBean);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null){
					br.close();
				}
				if (in != null){
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if( cityList.size() > 0 ){
			ContactsAppUtils.getInstance().getDatabaseHelper().getCityListDB().insertCityList(cityList);
		}
	}
	
    public static void loadGpsLocation(final Context context){
        if (NetUtil.isNetworkAvailable(context)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    LBSServiceGaode.process_activate(context, new LBSServiceListener() {

                        @Override
                        public void onLocationFailed() {
                            // TODO Auto-generated method stub
                            LBSServiceGaode.deactivate();

                        }

                        @Override
                        public void onLocationChanged(String city, double latitude,
                                double longitude, long time) {
                            if (!TextUtils.isEmpty(city)) {
                                LBSServiceGaode.deactivate();
                                if (city.endsWith(CommonValueUtil.getInstance().getCityData())) {
                                    city = city.substring(0, city.length() - 1);
                                }

                                SharedPreferences pref = context.getSharedPreferences(
                                        ConstantsParameter.YELLOW_PAGE_GPSLOCATION,
                                        Context.MODE_MULTI_PROCESS);
                                Editor editor = pref.edit();
                                editor.putString(
                                        ConstantsParameter.YELLOW_PAGE_GPSLOCATION_LATITUDE,
                                        latitude + "");
                                editor.putString(
                                        ConstantsParameter.YELLOW_PAGE_GPSLOCATION_LONGTITUDE,
                                        longitude + "");
                                editor.putString(ConstantsParameter.YELLOW_PAGE_GPSLOCATION_CITY,
                                        city + "");
                                editor.commit();

                            } else {
                                onLocationFailed();
                            }
                        }
                    });
                }
            }).start();
        }
    }
	
    
    public static String loadLocalTextFileString(String path){
        File f = new File(path);
        if(!f.exists()){
            return null;
        }
        InputStreamReader inputReader = null;
        BufferedReader bufferReader = null;
        StringBuffer strBuffer = new StringBuffer();
        try
        {
            InputStream inputStream = new FileInputStream(f);
            inputReader = new InputStreamReader(inputStream);
            bufferReader = new BufferedReader(inputReader);
            String line = null;
            while ((line = bufferReader.readLine()) != null)
            {
                strBuffer.append(line);
            } 
        }catch (IOException e)
        {
            e.printStackTrace();
        }finally
        {
            try {
                inputReader.close();
                bufferReader.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return strBuffer.toString();
    }
    
    
    public static boolean isNeedUpdateRechargeName() {
        SharedPreferences pref = ContactsApp.getInstance().getSharedPreferences(
                ConstantsParameter.CONTACTS_SETTING, Context.MODE_MULTI_PROCESS);

        return pref.getBoolean(ConstantsParameter.IS_NEED_UPDATE_RECHARGE_NAME_FLAG, true);
    }
    
    public static void setNeedUpdateRechargeName(boolean flag) {
        SharedPreferences pref = ContactsApp.getInstance().getSharedPreferences(
                ConstantsParameter.CONTACTS_SETTING, Context.MODE_MULTI_PROCESS);

        pref.edit().putBoolean(ConstantsParameter.IS_NEED_UPDATE_RECHARGE_NAME_FLAG, flag).commit();
    }
}
