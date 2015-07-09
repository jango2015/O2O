package so.contacts.hub.trafficoffence;

import so.contacts.hub.util.UMengEventIds;
import so.contacts.hub.util.MobclickAgentUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.reflect.TypeToken;
import com.yulong.android.contacts.discover.R;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import so.contacts.hub.ContactsApp;
import so.contacts.hub.adapter.TrafficOffenceInfoAdapter;
import so.contacts.hub.city.CityListDB;
import so.contacts.hub.core.Config;
import so.contacts.hub.msgcenter.MsgCenterConfig;
import so.contacts.hub.msgcenter.PTMessageCenterSettings;
import so.contacts.hub.msgcenter.report.MsgReport;
import so.contacts.hub.msgcenter.report.MsgReportUtils;
import so.contacts.hub.trafficoffence.bean.Vehicle;
import so.contacts.hub.ui.BaseActivity;
import so.contacts.hub.ui.yellowpage.YellowPageCitySelectActivity;
import so.contacts.hub.ui.yellowpage.bean.HabitDataItem;
import so.contacts.hub.util.ContactsAppUtils;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.MyCenterConstant;
import so.contacts.hub.util.NetUtil;
import so.contacts.hub.util.UserInfoUtil;
import so.contacts.hub.widget.CommonDialog;
import so.contacts.hub.widget.CommonDialogFactory;
import so.contacts.hub.widget.ProgressDialog;
import so.contacts.hub.adapter.TrafficOffenceInfoAdapter.onDeleteButtonClickListener;

public class VehicleInfoSettingActivity extends BaseActivity implements
		OnClickListener {
	private TextView province_code;// 车牌号中省份的简称
	private TextView vehicle_region;// 城市列表
	private EditText car_num;// 车牌号
	private EditText engine_num;// 发动机号,后6位
	private EditText vin_num;// 车架号,后6位
	private Button vehicleinfo_commit;
	private static final int REQUEST_CODE = 100;
	private Map<Integer, String> maps = new HashMap<Integer, String>();
	private TextView title = null;
	private ImageView image1 = null;
	private ImageView image2 = null;
	private PopupWindow popupWindow;
	private PopupWindow popupWindow_history;
	private LinearLayout engine_layout = null;
	private String province;// 省份名字
	private String city;// 城市名字;
	private LinearLayout vehicle_delete_layout = null;
	private ImageButton delete_button;
	private static final int FAILED = -1;
	private int position = -1;
	private static final String TAG = VehicleInfoSettingActivity.class
			.getSimpleName();
	private Vehicle vc = null;// 从设置界面传过来的车辆信息；
	private ArrayList<Vehicle> mLastSetVc = null;// 最后一次设置的车辆信息 来自用户习惯数据；
	private InputMethodManager mInputManager = null;
	private boolean isFirst = true;
	private String[] spc;// 不需要输入发动机号的省份,蒙,云,粤,豫

	private static final int REPORT_SUCCESS = 0x100;
	private static final int REPORT_FAILED = 0x101;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.putao_traffic_offence_add_vehicle_layout);
		spc = getResources().getStringArray(
				R.array.putao_traffic_offence_spcical_pro);
		initView();
		parseIntent();
		initData();
	}

	private void parseIntent() {
		Intent intent = null;
		intent = getIntent();
		if (intent != null) {
			vc = (Vehicle) intent.getSerializableExtra(Vehicle.class
					.getSimpleName());
			position = intent.getIntExtra("position", -1);
			if (vc != null) {
				province = vc.getProvince_name();
				city = vc.getCity_name();
				vehicle_region.setText(province + city);
				province_code.setText(vc.getCar_province());
				car_num.setText(vc.getCar_no());
				engine_num.setText(vc.getEngine_no());
				vin_num.setText(vc.getVin_no());
				vehicle_delete_layout.setVisibility(View.VISIBLE);
				String pro_code = province_code.getText().toString();
				if (engine_layout != null && spc != null) {
					if (spc[0].contains(pro_code) || spc[1].contains(pro_code)
							|| spc[2].contains(pro_code)
							|| spc[3].contains(pro_code)) {
						engine_layout.setVisibility(View.VISIBLE);
					} else {
						engine_layout.setVisibility(View.GONE);
					}
				}

			}
		}

	}

	private void initData() {
		String[] city = getResources().getStringArray(
				R.array.putao_provinces_simple);
		for (int i = 1; i <= city.length; i++) {
			maps.put(i, city[i - 1]);
		}
	}

	private void initView() {
		// add ljq start 2015/01/05 增加用户习惯信息处理
		List<HabitDataItem> items = UserInfoUtil
				.getInstace()
				.getHabitDataByContentType(
						MyCenterConstant.TRAFFIC_OFFENCE_VEHICLE_INFO,
						MyCenterConstant.HIBAT_CONTENT_TYPE_OFFEN_TRAFFIC_VEHICLE_INFO,
						false);
		if (items != null && items.size() > 0) {
			HabitDataItem item = items.get(0);
			mLastSetVc = Config.mGson.fromJson(item.getContent_data(),
					new TypeToken<ArrayList<Vehicle>>() {
					}.getType());
		}
		// add ljq end 2015/01/05 增加用户习惯信息处理

		engine_layout = (LinearLayout) findViewById(R.id.engine_layout);

		province_code = (TextView) findViewById(R.id.province_code);
		vehicle_region = (TextView) findViewById(R.id.vehicle_region);
		car_num = (EditText) findViewById(R.id.car_num);
		engine_num = (EditText) findViewById(R.id.engine_num);
		vin_num = (EditText) findViewById(R.id.vin_num);
		vehicleinfo_commit = (Button) findViewById(R.id.vehicle_info_commit);
		title = (TextView) findViewById(R.id.title);
		title.setText(mTitleContent);
		vehicle_delete_layout = (LinearLayout) findViewById(R.id.vehicle_delete_layout);
		// vehicle_delete_layout.setOnClickListener(this);
		delete_button = (ImageButton) findViewById(R.id.delete_button);
		delete_button.setOnClickListener(this);
		vehicle_region.setOnClickListener(this);
		vehicleinfo_commit.setOnClickListener(this);
		findViewById(R.id.back_layout).setOnClickListener(this);

		image1 = (ImageView) findViewById(R.id.vehicle_hint1);
		image1.setOnClickListener(this);
		image2 = (ImageView) findViewById(R.id.vehicle_hint2);
		image2.setOnClickListener(this);
		car_num.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (TextUtils.isEmpty(s)
						&& mLastSetVc != null
						&& (popupWindow_history == null || !popupWindow_history
								.isShowing())) {
					showHistoryPopWindow(car_num);
				} else {
					if (popupWindow_history != null
							&& popupWindow_history.isShowing()) {
						popupWindow_history.dismiss();
					}
				}
			}
		});

		car_num.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (TextUtils.isEmpty(car_num.getEditableText())
						&& mLastSetVc != null
						&& (popupWindow_history == null || !popupWindow_history
								.isShowing())) {
					showHistoryPopWindow(car_num);
				} else {

				}
				return false;
			}
		});

		car_num.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					if (TextUtils.isEmpty(car_num.getEditableText())
							&& mLastSetVc != null
							&& !isFirst
							&& (popupWindow_history == null || !popupWindow_history
									.isShowing())) {
						showHistoryPopWindow(car_num);
						isFirst = false;
					}
				} else {
					if (popupWindow_history != null
							&& popupWindow_history.isShowing()) {
						popupWindow_history.dismiss();
					}
				}
			}
		});

	}

	private void showInputManager(boolean isNeedShow) {
		if (mInputManager == null) {
			mInputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		}
		if (isNeedShow) {
			mInputManager.showSoftInput(car_num,
					InputMethodManager.SHOW_IMPLICIT);
		} else {
			if (mInputManager.isActive()) {
				mInputManager.hideSoftInputFromWindow(car_num.getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.vehicle_info_commit) {
			commit();
			MobclickAgentUtil
					.onEvent(
							ContactsApp.getInstance().getApplicationContext(),
							UMengEventIds.DISCOVER_YELLOWPAGE_MY_MSG_CENTER_SETTING_CAR_INFO_SAVE);
		} else if (id == R.id.vehicle_region) {
			selectCity();
		} else if (id == R.id.back_layout) {
			finish();
		} else if (id == R.id.vehicle_hint1 || id == R.id.vehicle_hint2) {
			showPopWindow(image1);
		} else if (id == R.id.delete_button) {
			delete();
		}

	}

	/** 删除已有的数据 */
	private void delete() {
		LogUtil.i(TAG, "position=" + position);
		final CommonDialog dialog = CommonDialogFactory
				.getOkCancelCommonLinearLayoutDialog(this);
		dialog.getTitleTextView().setText("删除");
		dialog.getMessageTextView().setText(
				"确定要删除车牌" + vc.getCar_province() + vc.getCar_no() + "?");
		dialog.show();
		dialog.setOkButtonClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (position != -1) {
					deletecar(position);
					dialog.dismiss();
					Intent intent = new Intent();
					setResult(RESULT_OK);
					finish();
				}
			}
		});
		dialog.setCancelButtonClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}

	@SuppressWarnings("deprecation")
	private void showPopWindow(View parent) {
		// TODO 待优化
		View view = View.inflate(this, R.layout.putao_show_vehicle_popwindow,
				null);
		int x = getWindowManager().getDefaultDisplay().getWidth();
		int y = getWindowManager().getDefaultDisplay().getHeight();
		// LogUtil.i(TAG , "X="+x+"y="+y);
		popupWindow = new PopupWindow(view, x, y);
		popupWindow
				.setAnimationStyle(R.style.putao_Animations_PopUpMenu_Center);
		popupWindow.showAtLocation(parent, Gravity.CENTER, 0,
				(int) parent.getY());
		popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(false);
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (popupWindow != null && popupWindow.isShowing()) {
					popupWindow.dismiss();
				}
			}
		});
	}

	private void showHistoryPopWindow(View parent) {
		View view = View.inflate(this, R.layout.putao_common_popwindow, null);
		ListView historyListView = (ListView) view
				.findViewById(R.id.history_list);
		historyListView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				showInputManager(false);
				Vehicle historyItem = mLastSetVc.get(0);
				String carNum = historyItem.getCar_no();
				String engineNum = historyItem.getEngine_no();
				String vinNum = historyItem.getVin_no();
				if (TextUtils.isEmpty(carNum)) {
					return false;
				}
				String editText = car_num.getText().toString();
				popupWindow_history.dismiss();
				if (carNum.equals(editText)) {
					return false;
				}
				car_num.setText(carNum);
				car_num.setSelection(carNum.length());
				// engine_num.setText(engineNum);
				// vin_num.setText(vinNum);
				return false;
			}
		});
		if (mLastSetVc != null && mLastSetVc.size() > 0) {
			TrafficOffenceInfoAdapter mHistoryAdapter = new TrafficOffenceInfoAdapter(
					this, mLastSetVc);
			mHistoryAdapter
					.setOnDeleteButtonClickListener(new onDeleteButtonClickListener() {
						@Override
						public void onDeleteButtonClicked(int position,
								String words) {
							// 预留代码 用于删除逻辑
						}
					});
			historyListView.setAdapter(mHistoryAdapter);
			historyListView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View view,
						int position, long arg3) {
					//
				}
			});
			popupWindow_history = new PopupWindow(view, parent.getWidth(),
					LayoutParams.WRAP_CONTENT);
			popupWindow_history.showAsDropDown(parent);
			popupWindow_history.setOutsideTouchable(true);
		}
	}

	private void selectCity() {
		Intent intent = new Intent(this, YellowPageCitySelectActivity.class);
		intent.putExtra(YellowPageCitySelectActivity.SHOW_MODE_KEY,
				YellowPageCitySelectActivity.SHOW_MODE_NOHOT);
		intent.putExtra(YellowPageCitySelectActivity.FROM_ACTIVITY_KEY,
				VehicleInfoSettingActivity.class.getSimpleName());
		intent.putExtra("title", "地理位置");
		// intent.putExtra(name, value);
		startActivityForResult(intent, REQUEST_CODE);
	}

	private void commit() {
		// String region = vehicle_region.getText().toString().trim();
		String numhint = province_code.getText().toString().trim();
		String num = car_num.getText().toString().trim().toUpperCase();
		String engine = engine_num.getText().toString().trim().toUpperCase();
		String vim = vin_num.getText().toString().trim().toUpperCase();
		if (TextUtils.isEmpty(city)) {
			Toast.makeText(this, "请选择地区", Toast.LENGTH_SHORT).show();
			return;
		}
		if (TextUtils.isEmpty(num) || num.length() != 6) {
			Toast.makeText(this, "请填写6位车牌号码", Toast.LENGTH_SHORT).show();
			return;
		}
		// if (TextUtils.isEmpty(engine) || engine.length() != 6) {
		// Toast.makeText(this, "请填写发动机号的后6位", Toast.LENGTH_SHORT).show();
		// return;
		// }
		if (TextUtils.isEmpty(vim) || vim.length() != 6) {
			Toast.makeText(this, "请填写发车架号的后6位", Toast.LENGTH_SHORT).show();
			return;
		}
		if (!NetUtil.isNetworkAvailable(this)) {
			Toast.makeText(this, getString(R.string.putao_network_exception),
					Toast.LENGTH_SHORT).show();
			return;
		}
		final Vehicle vehicle;
		if (vc != null) {
			vehicle = vc;
		} else {
			vehicle = new Vehicle();
		}
		vehicle.setCar_province(numhint);
		vehicle.setCar_no(num);
		vehicle.setEngine_no(engine);
		vehicle.setVin_no(vim);
		vehicle.setProvince_name(province);
		vehicle.setCity_name(city);

		savecar(vehicle);

		doReport(vehicle);

		/**
		 * old code: Intent intent = new Intent();
		 * intent.setAction(MsgReportParameter.ACTION_REPORT);
		 * intent.putExtra(MsgReportParameter.TYPE,
		 * MsgReportParameter.PECCANCY);
		 * intent.putExtra(MsgReportParameter.VEHICLE, vehicle);
		 * sendBroadcast(intent);
		 */

		setResult(RESULT_OK, new Intent());
		finish();
	}

	private void doReport(final Vehicle vehicle) {
		showLoadingDialog();
		Config.execute(new Runnable() {
			@Override
			public void run() {
				MsgReport report = new MsgReport();
				report.setType(MsgCenterConfig.Product.traffic_offence
						.getProductType());
				report.setReportContent(Config.mGson.toJson(vehicle));

				int status = MsgReportUtils.doReportVehicle(report);
				if (MsgReportUtils.REPORT_STATUS_OK == status) {
					/**
					 * 上报成功 add by ls
					 */

					LogUtil.d(TAG, "status=" + status);
					handler.sendEmptyMessage(REPORT_SUCCESS);
				} else {
					// 上报异常
					handler.sendEmptyMessage(REPORT_FAILED);

					/** end */
				}
			}

		});
	}

	/** 保存车辆信息到本地 */
	private void savecar(Vehicle vehicle) {
		// TODO Auto-generated method stub
		ArrayList<Vehicle> list = null;
		SharedPreferences sp = ContactsApp.getInstance()
				.getSharedPreferences(PTMessageCenterSettings.SHARED_NAME,
						Context.MODE_MULTI_PROCESS);
		String result = sp
				.getString(TrafficOffenceMsgBusiness.SAVED_CARS, null);
		if (result == null) {
			list = new ArrayList<Vehicle>();
		} else {
			list = Config.mGson.fromJson(result,
					new TypeToken<ArrayList<Vehicle>>() {
					}.getType());
		}
		// 如果ID不是-1 说明是已经存在的数据 这时候做更新操作
		if (vehicle.getId() != -1) {
			if (list != null && list.size() != 0) {
				for (int i = 0; i < list.size(); i++) {
					Vehicle v = list.get(i);
					if (v.getId() == vehicle.getId()) {
						v.setCar_no(vehicle.getCar_no());
						v.setCar_province(vehicle.getCar_province());
						v.setCity_name(vehicle.getCity_name());
						v.setCity_pinyin(vehicle.getCity_pinyin());
						v.setEngine_no(vehicle.getEngine_no());
						v.setProvince_name(vehicle.getProvince_name());
						v.setProvince_pinyin(vehicle.getProvince_pinyin());
						v.setVin_no(vehicle.getVin_no());
					}
				}
			}
		} else {
			list.add(vehicle);
		}
		sp.edit()
				.putString(TrafficOffenceMsgBusiness.SAVED_CARS,
						Config.mGson.toJson(list)).commit();

		sp.edit().putBoolean("can_traffic_offence_show", true).commit();

		// add ljq start 2015/01/05 增加用户习惯信息处理
		HabitDataItem item = new HabitDataItem();
		item.setSource_type(MyCenterConstant.TRAFFIC_OFFENCE_VEHICLE_INFO);
		item.setContent_type(MyCenterConstant.HIBAT_CONTENT_TYPE_OFFEN_TRAFFIC_VEHICLE_INFO);
		list = new ArrayList<Vehicle>();
		list.add(vehicle);
		String dataContent = Config.mGson.toJson(list,
				new TypeToken<ArrayList<Vehicle>>() {
				}.getType());
		item.setContent_data(dataContent);
		UserInfoUtil.getInstace().saveHabitDataNow(item);
		// add ljq end 2015/01/05 增加用户习惯信息处理

	}

	/** 删除车辆信息 */
	private void deletecar(int position) {
		ArrayList<Vehicle> list = null;
		SharedPreferences sp = ContactsApp.getInstance()
				.getSharedPreferences(PTMessageCenterSettings.SHARED_NAME,
						Context.MODE_MULTI_PROCESS);
		String result = sp
				.getString(TrafficOffenceMsgBusiness.SAVED_CARS, null);
		if (result != null) {
			list = Config.mGson.fromJson(result,
					new TypeToken<ArrayList<Vehicle>>() {
					}.getType());
		}
		Vehicle vehicle = list.remove(position);

		sp.edit()
				.putString(TrafficOffenceMsgBusiness.SAVED_CARS,
						Config.mGson.toJson(list)).commit();

		delCarOnServer(vehicle);

		// add ljq start 2015/01/05 增加用户习惯信息处理 暂不删除
		// HabitDataItem item = new HabitDataItem();
		// item.setSource_type(MyCenterConstant.TRAFFIC_OFFENCE_VEHICLE_INFO);
		// item.setContent_type(MyCenterConstant.HIBAT_CONTENT_TYPE_OFFEN_TRAFFIC_VEHICLE_INFO);
		// list = new ArrayList<Vehicle>();
		// list.add(vehicle);
		// String dataContent =Config.mGson.toJson(list,
		// new TypeToken<ArrayList<Vehicle>>() {
		// }.getType());
		// item.setContent_data(dataContent);
		// UserInfoUtil.getInstace().delHabitDataAsyn(this, item);
		// add ljq start 2015/01/05 增加用户习惯信息处理
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent result) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, result);
		if (resultCode != RESULT_OK || result == null
				|| requestCode != REQUEST_CODE) {
			return;
		}
		CityListDB db = ContactsAppUtils.getInstance().getDatabaseHelper()
				.getCityListDB();
		String region = result.getStringExtra("cityName");
		String id = result.getStringExtra("cityId");

		LogUtil.d(TAG, "region =" + region + "id=" + id);

		if (id == null) { // 从定位的城市返回时, 无cityId;
			id = db.getSelfId(region);
			region = db.getSelfName(Integer.valueOf(id));// 加上城市完整名称
		}
		city = region;

		int p_id = db.getParentId(Integer.valueOf(id));// 返回-1,错误,0标识是
		LogUtil.d(TAG, "parent id=" + p_id);
		String p_name = "";
		if (p_id != -1 && p_id != 0) {
			p_name = db.getSelfName(p_id);
			// LogUtil.d(TAG, "p_name ="+p_name+"province_code"+maps.get(p_id));
			province_code.setText(maps.get(p_id));
		} else if (p_id == 0) {// 直辖市 直接从id读;
		// LogUtil.d(TAG,
		// "id="+id+"___map.get(id)="+maps.get(Integer.valueOf(id)));
			province_code.setText(maps.get(Integer.valueOf(id)));
		}
		province = p_name;
		vehicle_region.setText(p_name + region);
		province_code.setVisibility(View.VISIBLE);
		String pro_code = province_code.getText().toString();

		if (engine_layout != null && spc != null) {
			if (spc[0].contains(pro_code) || spc[1].contains(pro_code)
					|| spc[2].contains(pro_code) || spc[3].contains(pro_code)) {
				engine_layout.setVisibility(View.VISIBLE);
			} else {
				engine_layout.setVisibility(View.GONE);
			}
		}
	}

	/** 删除车牌号 */
	private void doDeleteCar(final String vehicleIds) {

		Config.execute(new Runnable() {
			@Override
			public void run() {
				boolean succ = VehicleUtils.doDeleteCar(vehicleIds);
				if (succ) {
					LogUtil.i(TAG, "doDeleteCar vehicleIds=" + vehicleIds
							+ " OK");
					;
					// Utils.showToast(VehicleInfoSettingActivity.this,
					// R.string.vehicle_del_ok, false);
				} else {
					LogUtil.i(TAG, "doDeleteCar vehicleIds=" + vehicleIds
							+ " Failed");
					;
					// Utils.showToast(VehicleInfoSettingActivity.this,
					// R.string.vehicle_del_fail, false);
				}
			}

		});
	}

	/**
	 * 去服务器删除车辆数据
	 */
	public void delCarOnServer(final Vehicle vehicle) {
		if (vehicle == null || vehicle.getId() == -1) {
			return;
		}
		LogUtil.d(TAG, "delCarOnServer vehicle: " + vehicle.toString());
		doDeleteCar("ids=" + String.valueOf(vehicle.getId()));

		// modify by xn 使用广播到MsgReportReceiver删除
		// SimpleRequestData request = new SimpleRequestData();
		// request.setParam("id", String.valueOf(vehicle.getId()));
		// PTHTTP.getInstance().asynPost(MsgReportParameter.DEL_CARINFO_URL,
		// request, new IResponse() {
		// @Override
		// public void onSuccess(String content) {
		// LogUtil.d(TAG, "onSuccess content : " + content);
		// }
		//
		// @Override
		// public void onFail(int errorCode) {
		// // TODO Auto-generated method stub
		// LogUtil.d(TAG, "onFail errorCode : " + errorCode);
		// }
		// });
		// Intent intent = new Intent();
		// intent.setAction(MsgReportParameter.ACTION_MUL_DEL);
		// intent.putExtra(MsgReportParameter.TYPE,
		// MsgReportParameter.PECCANCY);
		// intent.putExtra(MsgReportParameter.VEHICLE_IDS,
		// "ids="+vehicle.getId());
		// sendBroadcast(intent);
	}

	/**
	 * add by ls 2015-01-22 ;
	 */

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REPORT_SUCCESS:
				dismissLoadingDialog();
				finish();
				break;
			case REPORT_FAILED:
				Toast.makeText(
						VehicleInfoSettingActivity.this,
						getResources().getString(
								R.string.traffic_offence_report_fail),
						Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}

		};
	};

	/** end */

}
