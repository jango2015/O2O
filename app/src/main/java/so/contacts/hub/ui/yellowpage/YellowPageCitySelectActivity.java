package so.contacts.hub.ui.yellowpage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import so.contacts.hub.city.CityListDB;
import so.contacts.hub.db.MovieDB;
import so.contacts.hub.remind.BaseRemindActivity;
import so.contacts.hub.shuidianmei.WaterElectricityGasDB;
import so.contacts.hub.thirdparty.cinema.bean.MovieCity;
import so.contacts.hub.thirdparty.cinema.ui.YellowPageMovieListActivity;
import so.contacts.hub.trafficoffence.VehicleInfoSettingActivity;
import so.contacts.hub.train.YellowPageBookTrainTicketActivity;
import so.contacts.hub.shuidianmei.YellowPageWaterEGActivity;
import so.contacts.hub.shuidianmei.bean.WaterElectricityGasBean;
import so.contacts.hub.train.YellowPageTrainDB;
import so.contacts.hub.ui.yellowpage.bean.City;
import so.contacts.hub.util.CommonValueUtil;
import so.contacts.hub.util.ContactsAppUtils;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.NetUtil;
import so.contacts.hub.util.PinyinHelper;
import so.contacts.hub.util.UiHelper;
import so.contacts.hub.util.YellowUtil;
import so.contacts.hub.widget.IndexBarContacts;
import so.contacts.hub.widget.IndexBarContacts.OnIndexChangeListener;
import so.contacts.hub.widget.ProgressDialog;
import so.putao.findplug.LBSServiceGaode;
import so.putao.findplug.LBSServiceGaode.LBSServiceListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yulong.android.contacts.discover.R;

public class YellowPageCitySelectActivity extends BaseRemindActivity implements
		OnClickListener, OnItemClickListener, LBSServiceListener{

	public static final String TAG = YellowPageCitySelectActivity.class.getSimpleName();
	
	/**
	 * 列表显示方式
	 * 1.正常 带热门城市
	 * 2.不带热门城市
	 * 3.不带本地城市和热门城市
	 */
    public static int SHOW_MODE_NORMAL = 1 ;
    public static int SHOW_MODE_NOHOT = 2 ;
    public static int SHOW_MODE_PURE = 3 ;
    
    public static String SHOW_MODE_KEY = "show_mode_type" ;
    
    public static String FROM_ACTIVITY_KEY = "from" ;
    
    public static String FROM_ACTIVITY_TYPE = "type" ;
    
    public static String ISCHANGE_SEARCHNUMCITY_KEY = "isChangeSearchNumCity";
    
	private ListView mListView;
	private IndexBarContacts mIndexBar;
	private TextView mPopTips;
	private ProgressDialog mProgressDialog;
	
	private YellowPageAdapter mAdapter;
	private HashMap<String, Integer> mAlphaIndexer;
	
	private List<City> mCityList;
	private List<City> mDbCityList = null;
	
	private String mfromActivity;
	
	/**
	 * 是否改变YellowPageSearchNumberActivity搜索里的城市  add ljq 2015-1-9
	 */
	private boolean isChangeSearchNumCity = false; 
	
	//设置城市列表显示方式
	private int mShowMode;
	private boolean isNeedStartSelf =false;
	private ArrayList<String> districtListFromDB;//从数据库查询到的新政区名字的列表
	
	private String selectedPro ="";
	private String selectedCity ="";
	
	private int selectedProvinceSelfId;
	private int selectedCitySelfId;
	
	private int mSourceType = -1; // 【内容源类型】 
	
	private String mTitleContent = null; // title 头
	private int mRechargeType = -1;
	private String mSelectCity = "";
	
	private YellowPageTrainDB trainDB;
	private CityListDB mCityListDB = null;
	private WaterElectricityGasDB mWegDB = null;
	private MovieDB mMovieDB  = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.putao_yellow_page_city_list);
		
		
		parseIntent();
		initViews();
		
		trainDB = ContactsAppUtils.getInstance().getDatabaseHelper().getTrainDBHelper();
		mWegDB = ContactsAppUtils.getInstance().getDatabaseHelper().getWaterElectricityGasDB();
		mCityListDB = ContactsAppUtils.getInstance().getDatabaseHelper().getCityListDB();
		mMovieDB = ContactsAppUtils.getInstance().getDatabaseHelper().getMovieDB();
		
		if( !TextUtils.isEmpty(mfromActivity) ){
			if( mfromActivity.equals(YellowPageWaterEGActivity.class.getSimpleName()+"_company") ){ 		//水电煤 选择 公司
				if(mRechargeType != -1){
	        		if( !TextUtils.isEmpty(mSelectCity) ){
	        			List<WaterElectricityGasBean> beanList = mWegDB.queryWegDataByTypeAndCity(mRechargeType, mSelectCity);
	        			if(beanList != null && beanList.size()>0){
	        				mDbCityList = getCityBeanByWaterElectricityGasBean(beanList);
	        			}
	        		}
	        	}
			}else if( mfromActivity.equals(YellowPageWaterEGActivity.class.getSimpleName()+"_city") ){		//水电煤 选择 城市
				if(mRechargeType != -1){
					if (mProgressDialog != null && !mProgressDialog.isShowing()) {
						mProgressDialog.setMessage(getString(R.string.putao_yellow_page_get_city));
						mProgressDialog.show();
					}
					List<String> cityStrList = mWegDB.queryWegCityListByType(mRechargeType);
					if(cityStrList != null && cityStrList.size()>0){
						mDbCityList = getCityBeanByCityStr(cityStrList);
						if (mDbCityList == null || mDbCityList.size() == 0) {
							Toast.makeText(this, R.string.putao_get_city_list_failed,
									Toast.LENGTH_SHORT).show();
						} else {
							mProgressDialog.dismiss();
						}
					}
				}
			}else if( mfromActivity.equals(YellowPageMovieListActivity.class.getSimpleName()+"_city") ){       //电影票 选择 城市
                if (mProgressDialog != null && !mProgressDialog.isShowing()) {
                    mProgressDialog.setMessage(getString(R.string.putao_yellow_page_get_city));
                    mProgressDialog.show();
                }
                List<MovieCity> cityList = mMovieDB.queryMovieCityAll();
                if(cityList != null && cityList.size()>0){
                    mDbCityList = getCityBeanByMovieCityBean(cityList);
                    if (mDbCityList == null || mDbCityList.size() == 0) {
                        Toast.makeText(this, R.string.putao_get_city_list_failed,
                                Toast.LENGTH_SHORT).show();
                    } else {
                        mProgressDialog.dismiss();
                    }
                }
			}else if (mfromActivity.equals(YellowPageBookTrainTicketActivity.class.getSimpleName())) { // 火车票 选择城市
                mDbCityList = trainDB.getTongChengCityList();
                LogUtil.i("YellowPageCitySelectActivity", "mfromActivity= from火车站");
                if (mDbCityList == null || mDbCityList.size() == 0) {
                    if (mProgressDialog != null && !mProgressDialog.isShowing()) {
                        mProgressDialog
                                .setMessage(getString(R.string.putao_yellow_page_get_city));
                        mProgressDialog.show();
                    }
                    YellowUtil.loadTrainTicketDB();
                    mDbCityList = trainDB.getTongChengCityList();
                    if (mDbCityList == null || mDbCityList.size() == 0) {
                        Toast.makeText(this, R.string.putao_get_city_list_failed,
                                Toast.LENGTH_SHORT).show();
                    } else {
                        mProgressDialog.dismiss();
                    }
                }
            }
			else if(mfromActivity.equals(VehicleInfoSettingActivity.class.getSimpleName())){//违章  选择城市
           	 // 通用的城市选择
   			mDbCityList = mCityListDB.getTrafficOffenceList(); 
   			if (mDbCityList == null || mDbCityList.size() == 0) {
   				if (mProgressDialog != null && !mProgressDialog.isShowing()) {
   					mProgressDialog.setMessage(getString(R.string.putao_yellow_page_get_city));
   					mProgressDialog.show();
   				}
   				YellowUtil.loadAllCityList();
   				mDbCityList = mCityListDB.getTrafficOffenceList();  
   				if (mDbCityList == null || mDbCityList.size() == 0) {
   					Toast.makeText(this, R.string.putao_get_city_list_failed,
   							Toast.LENGTH_SHORT).show();
   				} else {
   					mProgressDialog.dismiss();
   				}
   			}
           }
			//add by ls end
			else if( mfromActivity.equals("fromPost") ){		// 从最开始的postActivity传过来												
				isNeedStartSelf = true;
	        	if (mDbCityList != null) {
	        		mDbCityList.clear();
	        	} else {
	        		mDbCityList = new ArrayList<City>();
	        	}
	        	districtListFromDB = mCityListDB.getDistrictNameByParentId(0);
	        	LogUtil.i("YellowPageCitySelectActivity", "省份的总数是"
	        			+ districtListFromDB.size());
	        	if (districtListFromDB != null && districtListFromDB.size() > 0) {
	        		for (String pro : districtListFromDB) {
	        			City c = new City();
	        			c.setCityName(pro);
	        			c.setCityPY(PinyinHelper.getInstance().getFullPinyin(
	        					pro));
	        			mDbCityList.add(c);
	        		}
	        	}
			} else if( mfromActivity.equals("fromPro") ){	// 从显示省的activity传过来
				if (mDbCityList != null) {
	        		mDbCityList.clear();
	        	} else {
	        		mDbCityList = new ArrayList<City>();
	        	}
	        	isNeedStartSelf = true;
	        	districtListFromDB = mCityListDB.getDistrictNameByParentId(selectedProvinceSelfId);
	        	if (mDbCityList != null) {
	        		mDbCityList.clear();
	        	} else {
	        		mDbCityList = new ArrayList<City>();
	        	}
	        	if (districtListFromDB != null && districtListFromDB.size() > 0) {
	        		for (String pro : districtListFromDB) {
	        			City c = new City();
	        			c.setCityName(pro);
	        			c.setCityPY(PinyinHelper.getInstance().getFullPinyin(
	        					pro));
	        			mDbCityList.add(c);
	        		}
	        	}
			}else if( mfromActivity.equals("fromCity") ){	// 从显示市的actitivy传过来
				isNeedStartSelf = true;
	        	if (mDbCityList != null) {
	        		mDbCityList.clear();
	        	} else {
	        		mDbCityList = new ArrayList<City>();
	        	}
	        	districtListFromDB = mCityListDB.getDistrictNameByParentId(selectedCitySelfId);
	        	if (districtListFromDB != null && districtListFromDB.size() > 0) {
	        		for (String pro : districtListFromDB) {
	        			City c = new City();
	        			c.setCityName(pro);
	        			c.setCityPY(PinyinHelper.getInstance().getFullPinyin(pro));
	        			mDbCityList.add(c);
	        		}
	        	}
			}
		}
		
		if( mSourceType != -1 && (mDbCityList == null || mDbCityList.size() == 0)){
			mDbCityList = mCityListDB.getCityListByType(mSourceType);
		}
		
		if( mDbCityList == null || mDbCityList.size() == 0 ){ // 通用的城市选择
			mDbCityList = mCityListDB.getSimpleCityListByTag(CityListDB.CityDBTable.CITY_TYPE, String.valueOf(CityListDB.CITY_TYPE_AREA)); 
			if (mDbCityList == null || mDbCityList.size() == 0) {
				if (mProgressDialog != null && !mProgressDialog.isShowing()) {
					mProgressDialog.setMessage(getString(R.string.putao_yellow_page_get_city));
					mProgressDialog.show();
				}
				YellowUtil.loadAllCityList();
				mDbCityList = mCityListDB.getSimpleCityListByTag(CityListDB.CityDBTable.CITY_TYPE, String.valueOf(CityListDB.CITY_TYPE_AREA));  
				if (mDbCityList == null || mDbCityList.size() == 0) {
					Toast.makeText(this, R.string.putao_get_city_list_failed,
							Toast.LENGTH_SHORT).show();
				} else {
					mProgressDialog.dismiss();
				}
			}
		}
        
        if( mDbCityList == null || mDbCityList.size() == 0 ){
        	// 无数据
        }else{
        	initData();
        }
	}

	private void initData() {
		List<City> cityList = mDbCityList;
		if(cityList == null){
			cityList = new ArrayList<City>();
		}
		Collections.sort(cityList, new Comparator<City>() {
			@Override
			public int compare(City lhs, City rhs) {
				String lStr = lhs.getCityPY();
				String rStr = rhs.getCityPY();
				return lStr.compareTo(rStr);
			}
		});

		mCityList = new ArrayList<City>();
		if(mShowMode != SHOW_MODE_PURE){
	        City city = new City();
	        city.setCityName(getString(R.string.putao_yellow_page_your_position));
	        mCityList.add(city);
	        city = new City();
	        if(!NetUtil.isGpsAvailable(this) && !NetUtil.isNetworkAvailable(this)){
	        	city.setCityName(getString(R.string.putao_yellow_page_location_failed));
	        }else{
	            if (!LBSServiceGaode.hasPreInfo()) {
	            	city.setCityName(getString(R.string.putao_yellow_page_locating));
	            } else {
	            	city.setCityName(LBSServiceGaode.getCity());
	            }
	        }
	        
	        mCityList.add(city);
        }
		String[] specialCities = getResources().getStringArray(
				R.array.putao_special_cities);
		
		if(mShowMode == SHOW_MODE_NORMAL){
            for (int i = 0; i < specialCities.length; i++) {
                City city = new City();
                city.setCityName(specialCities[i]);
                city.setCityId("-1"); //热门城市的ID为-1，需要从mCityList中去获取
                mCityList.add(city);
            }
		}else if(mShowMode == SHOW_MODE_NOHOT){
            City city = new City();
            city.setCityName(specialCities[specialCities.length - 1]);
            mCityList.add(city);
		}else if(mShowMode == SHOW_MODE_PURE){
		    //do nothing
		}
		mCityList.addAll(cityList);

		initAlphaIndex(mCityList);

		mAdapter.notifyDataSetChanged();
	}
	
	private void parseIntent(){
		Intent intent = getIntent();
		if( intent == null ){
			return;
		}
		isChangeSearchNumCity = intent.getBooleanExtra(ISCHANGE_SEARCHNUMCITY_KEY,false);
		mShowMode = intent.getIntExtra(SHOW_MODE_KEY, SHOW_MODE_NORMAL);
		mTitleContent = intent.getStringExtra("title");
		mfromActivity = intent.getStringExtra(FROM_ACTIVITY_KEY);
		LogUtil.i("YellowPageCitySelectActivity", "mfromActivity="+mfromActivity);
		mRechargeType = intent.getIntExtra(FROM_ACTIVITY_TYPE, -1);
		mSelectCity = intent.getStringExtra("city");
		selectedCitySelfId = intent.getIntExtra("selectedCitySelfId", 1001);
		selectedProvinceSelfId = intent.getIntExtra("selectedProvinceSelfId", 1);
		
		// 接收需要展示的（哪种类型的）城市列表
		mSourceType = intent.getIntExtra("source_type", -1);
		
		if(mShowMode != SHOW_MODE_PURE){
		    new Thread(new Runnable() {
		        @Override
		        public void run() {
		            LBSServiceGaode.activate(YellowPageCitySelectActivity.this,
		                    YellowPageCitySelectActivity.this);
		        }
		    }).start();
		}
	}

	private void initViews() {
		findViewById(R.id.back_layout).setOnClickListener(this);
		TextView title = (TextView) findViewById(R.id.title);
		title.setText(R.string.putao_yellow_page_city_list);
		mListView = (ListView) findViewById(R.id.city_list);
		mListView.setOnItemClickListener(this);
		mPopTips = (TextView) findViewById(R.id.tips);
		
        if (TextUtils.isEmpty(mTitleContent)) {
            mTitleContent = getResources().getString(R.string.putao_yellow_page_city_list);
        }
        ((TextView)findViewById(R.id.title)).setText(mTitleContent);

		mIndexBar = (IndexBarContacts) findViewById(R.id.sideBar);
		mIndexBar.setIndexes(UiHelper.SECTION_ADD_CONTACTS, null);
		mAdapter = new YellowPageAdapter(this);
		mListView.setAdapter(mAdapter);
		mProgressDialog = new ProgressDialog(this);
		
		mIndexBar.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mPopTips.setVisibility(View.VISIBLE);
					break;
				case MotionEvent.ACTION_CANCEL:
				case MotionEvent.ACTION_UP:
					mPopTips.setVisibility(View.GONE);
					break;
				}
				return false;
			}
		});

		mIndexBar.setOnIndexChangeListener(new OnIndexChangeListener() {

			@Override
			public void onChange(int index, String indexChar) {
				if (mAdapter != null) {
					int pos = -1;

					if (mAlphaIndexer.containsKey(indexChar)) {
						pos = mAlphaIndexer.get(indexChar);
					}
					if (pos != -1) {
						mListView.setSelection(pos);
						mPopTips.setText(indexChar);
					}
				}

			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LBSServiceGaode.deactivate();
	}

	private void initAlphaIndex(List<City> specialCityList) {
		mAlphaIndexer = new HashMap<String, Integer>();
		for (int i = 0; i < specialCityList.size(); i++) {
			String name = getAlpha(specialCityList.get(i).getCityPY());
			if (!mAlphaIndexer.containsKey(name)) {
				mAlphaIndexer.put(name, i);
			}
		}
	}

	/**
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		if (str == null) {
			return "#";
		}
		if (str.trim().length() == 0) {
			return "#";
		}
		char c = str.trim().substring(0, 1).charAt(0);

		Pattern pattern = Pattern.compile("^[A-Za-z]+$");
		if (pattern.matcher(c + "").matches()) {
			return (c + "").toUpperCase(Locale.US);
		} else {
			return "#";
		}
	}

	class YellowPageAdapter extends BaseAdapter {

		private Context mContext;
		private LayoutInflater mInflater;

		public YellowPageAdapter(Context context) {
			this.mContext = context;
			this.mInflater = LayoutInflater.from(mContext);
		}

		@Override
		public int getCount() {
			if (mCityList == null) {
				return 0;
			} else {
				return mCityList.size();
			}

		}

		@Override
		public Object getItem(int position) {
			if (mCityList == null) {
				return null;
			} else {
				return mCityList.get(position);
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			String name = mCityList.get(position).getCityName();
			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.putao_yellow_page_city_list_item, null);
			}
			TextView title = (TextView) convertView
					.findViewById(R.id.city_name);
			TextView category = (TextView) convertView
					.findViewById(R.id.category_name);
			//add by putao_lhq for coolui6.0 start
			View view = (View)convertView.findViewById(R.id.category_tag);
			View divider = (View)convertView.findViewById(R.id.divider);
			View content = (View)convertView.findViewById(R.id.content);
			//add by putao_lhq for coolui6.0 end
			boolean isDisclick = false;
			if (mShowMode == SHOW_MODE_NORMAL) {
			    if (position == 0 || position == 2 || position == 7) {
	                isDisclick = true;
	            } else {
	                isDisclick = false;
	            }
			    //add by putao_lhq for coolui6.0 start
	            if (position == 0 || position == 1 || position == 2 || position == 6 || position == 7) {
	                divider.setVisibility(View.GONE);
	            } else {
	                divider.setVisibility(View.VISIBLE);
	            }
	            //add by putao_lhq for coolui6.0 end
			} else if (mShowMode == SHOW_MODE_NOHOT) {
                if (position == 0 || position == 2) {
                    isDisclick = true;
                } else {
                    isDisclick = false;
                }
              //add by putao_lhq for coolui6.0 start
                if (position == 0 || position == 1) {
                    divider.setVisibility(View.GONE);
                } else {
                    divider.setVisibility(View.VISIBLE);
                }
                //add by putao_lhq for coolui6.0 end
            } else if (mShowMode == SHOW_MODE_PURE) {

            }
			if (isDisclick) {
				category.setVisibility(View.VISIBLE);
				view.setVisibility(View.VISIBLE);//add by putao_lhq for coolui6.0
				title.setVisibility(View.GONE);
				category.setText(name);
				content.setVisibility(View.GONE);
			} else {
				category.setVisibility(View.GONE);
				view.setVisibility(View.GONE);//add by putao_lhq for coolui6.0
				title.setVisibility(View.VISIBLE);
				title.setText(name);
				content.setVisibility(View.VISIBLE);
			}
			return convertView;
		}

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.back_layout) {
			finish();
		} else {
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
        boolean isBack = true;
        if (mShowMode == SHOW_MODE_NORMAL) {
            if (position == 0 || position == 2 || position == 7) {
                isBack = false;
            }
        } else if (mShowMode == SHOW_MODE_NOHOT) {
            if (position == 0 || position == 2) {
                isBack = false;
            }
        } else if (mShowMode == SHOW_MODE_PURE) {

        }
        
		// add by lisheng 2014-12-01 start
		if (isNeedStartSelf) {
			if ("fromPost".equals(mfromActivity)) {// 从火车票postaddress传过来
				Intent intent = new Intent(this, YellowPageCitySelectActivity.class);
				intent.putExtra(YellowPageCitySelectActivity.FROM_ACTIVITY_KEY, "fromPro");
				intent.putExtra(YellowPageCitySelectActivity.SHOW_MODE_KEY,
						YellowPageCitySelectActivity.SHOW_MODE_PURE);
				for (int i = 0; i < districtListFromDB.size(); i++) {
					if (districtListFromDB.get(i).equals(
							mCityList.get(position).getCityName())) {
						selectedProvinceSelfId = i + 1;
						LogUtil.i(TAG, "selectedProvinceSelfId="+selectedProvinceSelfId);
						intent.putExtra("title", getString(R.string.putao_train_select_hint));
						intent.putExtra("selectedProvinceSelfId",
								selectedProvinceSelfId);
						selectedPro = districtListFromDB.get(i);
						LogUtil.i(TAG, "selectedPro="+selectedPro);
						startActivityForResult(intent, 2);
					}
				}
			} else if ("fromPro".equals(mfromActivity)) {// 从选择省的activity传过来;
				Intent intent = new Intent(this, YellowPageCitySelectActivity.class);
				intent.putExtra(YellowPageCitySelectActivity.FROM_ACTIVITY_KEY, "fromCity");
				intent.putExtra(YellowPageCitySelectActivity.SHOW_MODE_KEY,
						YellowPageCitySelectActivity.SHOW_MODE_PURE);
				for (String s : districtListFromDB) {
					if (s.equals(mCityList.get(position).getCityName())) {
						selectedCitySelfId = mCityListDB.getSelf_Id(s, selectedProvinceSelfId);
						intent.putExtra("selectedCitySelfId",
								selectedCitySelfId);
						LogUtil.i(TAG, "selectedCitySelfId="+selectedCitySelfId);
						selectedCity = s;
						intent.putExtra("title", getString(R.string.putao_train_select_hint));
						startActivityForResult(intent, 3);
					}
				}
			} else if ("fromCity".equals(mfromActivity)) {// 从选择市的地方传过来
				for (String s : districtListFromDB) {
					if (s.equals(mCityList.get(position).getCityName())) {
						Intent result = new Intent();
						result.putExtra("select", s);
						setResult(RESULT_OK, result);
						finish();
					}
				}
			}
			return;
		}
		// add by lisheng end
        
        if (isBack) {
            //modity by ljq start 2014-12-25 首页搜索mfromActivity为null 需要保存数据 其他业务不能把自己的数据覆盖首页搜索数据
            if(isChangeSearchNumCity){
                YellowUtil.saveCity(this, mCityList.get(position).getCityName());
            }
            //modity by ljq end 2014-12-25 
            City city = mCityList.get(position);
            String cityName = city.getCityName();
            String cityId = city.getCityId();
            if( cityId == null || "-1".equals(cityId) ){ // 热门城市，需要获取其对应的城市ID
            	for(int i = 0; i < mCityList.size(); i++ ){
            		String tempCityName = mCityList.get(i).getCityName();
            		String tempCityId = mCityList.get(i).getCityId();
            		if( tempCityName.equals(cityName) && !TextUtils.isEmpty(tempCityId) && !"-1".equals(tempCityId) ){
            			cityId = tempCityId;
            			break;
            		}
            	}
            }
            Intent intent = new Intent();
            intent.putExtra("cityName", cityName);
            intent.putExtra("cityId", cityId);
            setResult(RESULT_OK, intent);
            finish();
        }
	}

	@Override
	public void onLocationChanged(String city, double latitude,
			double longitude, long time) {
		LogUtil.i("YellowPageCitySelectActivity", "city="+city);
		
		//add by lisheng 增加判断, 仅当不为纯净模式时,才将定位信息加到列表中;
		mShowMode = getIntent().getIntExtra(SHOW_MODE_KEY, SHOW_MODE_NORMAL);
		//add by lisheng end;
		
		if(mShowMode!=SHOW_MODE_PURE){
			if (!TextUtils.isEmpty(city)) {
//				if (city.endsWith(CommonValueUtil.getInstance().getCityData())) {
//					city = city.substring(0, city.length() - 1);
//				}
				if (mCityList != null && mCityList.size() > 1) {
					City cityBean = mCityList.get(1);
					cityBean.setCityName(city);
					mAdapter.notifyDataSetChanged();
				}
				LBSServiceGaode.deactivate();
			}else if(latitude != 0 && longitude != 0){
				if (mCityList != null && mCityList.size() > 1) {
					City cityBean = mCityList.get(1);
					cityBean.setCityName(getString(R.string.putao_yellow_page_location_failed));
					mAdapter.notifyDataSetChanged();
				}
				LBSServiceGaode.deactivate();
			}
		}
		
		
	}

	@Override
	protected void onResume() {
		MobclickAgentUtil.onResume(this);
		super.onResume();
	}

	@Override
	protected void onPause() {
        MobclickAgentUtil.onPause(this);
		super.onPause();
	}

    @Override
    public void onLocationFailed() {
        // TODO Auto-generated method stub
        
    }
    
    //add ljq 2014-11-28 start
    private List<City> getCityBeanByCityStr(List<String> strList){
        PinyinHelper helper = PinyinHelper.getInstance();
        List<City> citybeanList = new ArrayList<City>();
        if(strList != null && strList.size()>0){
            for (int i = 0; i < strList.size(); i++) {
                String cityname = strList.get(i);
                City city = new City();
                city.setCityName(cityname);
                city.setCityPY(helper.getFullPinyin(cityname));
                citybeanList.add(city);
            }
        }
        return citybeanList ;
    }
  //add ljq 2014-11-28 start
    
    //add ljq 2014-11-28 start
    private List<City> getCityBeanByWaterElectricityGasBean(List<WaterElectricityGasBean> beanList){
        PinyinHelper helper = PinyinHelper.getInstance();
        List<City> citybeanList = new ArrayList<City>();
        if(beanList != null && beanList.size()>0){
            for (int i = 0; i < beanList.size(); i++) {
                WaterElectricityGasBean bean = beanList.get(i);
                City city = new City();
                city.setCityId(bean.getProduct_id());
                city.setCityName(bean.getCompany());
                city.setCityPY(helper.getFullPinyin(bean.getCompany()));
                citybeanList.add(city);
            }
        }
        return citybeanList ;
    }
  //add ljq 2014-11-28 start
    
    
    //add ljq 2014-12-24 start
    private List<City> getCityBeanByMovieCityBean(List<MovieCity> beanList){
        PinyinHelper helper = PinyinHelper.getInstance();
        List<City> citybeanList = new ArrayList<City>();
        if(beanList != null && beanList.size()>0){
            for (int i = 0; i < beanList.size(); i++) {
                MovieCity bean = beanList.get(i);
                City city = new City();
                city.setCityId(bean.getCitycode());
                city.setCityName(CommonValueUtil.getInstance().filterDistrict(bean.getCityname()));
                city.setCityPY(helper.getFullPinyin(CommonValueUtil.getInstance().filterDistrict(bean.getCityname())));
                citybeanList.add(city);
            }
        }
        return citybeanList ;
    }
  //add ljq 2014-12-24 start
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	// TODO Auto-generated method stub
    	super.onActivityResult(requestCode, resultCode, data);
    	if(requestCode==2&&data!=null){
    		String selected =data.getStringExtra("select")+"_"+selectedPro;
    		LogUtil.i("YellowPageCitySelectActivity", "第二级页面返回的seltected="+selected);
    		Intent intent = new Intent();
    		intent.putExtra("select", selected);
    		setResult(RESULT_OK, intent);
    		finish();
    	}else if(requestCode==3&&data!=null){
    		String selected =data.getStringExtra("select")+"_"+selectedCity;
    		LogUtil.i("YellowPageCitySelectActivity", "第三级页面返回的seltected="+selected);
    		Intent intent = new Intent();
    		intent.putExtra("select", selected);
    		setResult(RESULT_OK, intent);
    		finish();
    	}
    }

    @Override
    public String getServiceNameByUrl() {
        return null;
    }

    @Override
    public String getServiceName() {
        return this.getClass().getName();
    }

    @Override
    public boolean needMatchExpandParam() {
        return false;
    }

    @Override
    public Integer remindCode() {
        return null;
    }
}
