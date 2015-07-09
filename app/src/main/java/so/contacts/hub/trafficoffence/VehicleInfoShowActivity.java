package so.contacts.hub.trafficoffence;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.account.PutaoAccount;
import so.contacts.hub.core.Config;
import so.contacts.hub.msgcenter.PTMessageCenterSettings;
import so.contacts.hub.msgcenter.report.MsgReportParameter;
import so.contacts.hub.net.IResponse;
import so.contacts.hub.net.PTHTTP;
import so.contacts.hub.trafficoffence.bean.Vehicle;
import so.contacts.hub.ui.BaseActivity;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.NetUtil;
import so.contacts.hub.util.Utils;
import so.contacts.hub.widget.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.yulong.android.contacts.discover.R;

public class VehicleInfoShowActivity extends BaseActivity implements
		OnClickListener, OnItemClickListener, OnItemLongClickListener {
	private static final String TAG = VehicleInfoShowActivity.class
			.getSimpleName();

	private static final int MSG_SHOW_DIALOG_ACTION = 0x2001;

	private static final int MSG_DISMISS_DIALOG_ACTION = 0x2002;

	private static final int MSG_REFRESH_DATA = 0x2003;

	public static final String REFRESH_DATA_ACTION = "so.contacts.hub.trafficoffence.VehicleInfoShowActivity.REFRESH_DATA_ACTION";

	/**
	 * modify by putao_lhq
	 * 
	 * @start old code private MyListView listView = null;
	 */
	private ListView listView;/* @end by putao_lhq */

	private ArrayList<Vehicle> list = null;
	/**
	 * modify by putao_lhq old code private LinearLayout linearLayout =null;
	 */
	private LinearLayout mEmptyView;
	private TextView mEmptyTip;
	private LinearLayout mDeleteView;
	private ImageButton mDeleteImg;
	private boolean mDeleteMode = false;
	private Set<String> mSelected = new HashSet<String>();
	/* @end by putao_lhq */

	private MyAdapter adapter = null;
	private ImageView next_step_img;
	private TextView title = null;
	private int pos;// 保存item的点击的位置;

	private ProgressDialog mProgressDialog = null;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.putao_traffic_offence_vehicle_show_layout);
		initViews();
		initData();
	}

	private void initViews() {
		/**
		 * modify code by putao_lhq old code: linearLayout =(LinearLayout)
		 * findViewById(R.id.novehicles); listView = (MyListView)
		 * findViewById(R.id.vehilces);
		 */
		listView = (ListView) findViewById(R.id.vehilces);
		mEmptyView = (LinearLayout) findViewById(R.id.empty_layout);
		mEmptyTip = (TextView) findViewById(R.id.empty_tip_one);
		mDeleteView = (LinearLayout) findViewById(R.id.delete_container);
		mDeleteImg = (ImageButton) findViewById(R.id.delete_button);
		mDeleteImg.setOnClickListener(this);
		listView.setOnItemLongClickListener(this);
		listView.setOnItemClickListener(this);
		mEmptyView.setOnClickListener(this);
		/* @end by putao_lhq */
		next_step_img = (ImageView) findViewById(R.id.next_step_img);
		/**
		 * modify by putao_lhq
		 * 
		 * @start old code
		 *        next_step_img.setBackgroundResource(R.drawable.putao_icon_title_add
		 *        );
		 */
		next_step_img.setImageResource(R.drawable.putao_icon_title_add);/*
																		 * @end
																		 * by
																		 * putao_lhq
																		 */
		next_step_img.setVisibility(View.VISIBLE);
		title = (TextView) findViewById(R.id.title);
		title.setText(mTitleContent);
		findViewById(R.id.next_setp_layout).setOnClickListener(this);
		findViewById(R.id.back_layout).setOnClickListener(this);

		mProgressDialog = new ProgressDialog(this);
		if (!mProgressDialog.isShowing()) {
			mProgressDialog
					.setMessage(getString(R.string.putao_yellow_page_loading));
		}
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case MSG_SHOW_DIALOG_ACTION:
				if (mProgressDialog != null) {
					mProgressDialog.show();
				}
				break;
			case MSG_DISMISS_DIALOG_ACTION:
				if (mProgressDialog != null && mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
				if (list != null && list.size() > 0) {
					mEmptyView.setVisibility(View.GONE);
					listView.setVisibility(View.VISIBLE);
				} else {
					mEmptyTip.setText(R.string.putao_traffic_empty_no_car_info);
					mEmptyView.setVisibility(View.VISIBLE);
					listView.setVisibility(View.GONE);
				}
				break;
			case MSG_REFRESH_DATA:
				LogUtil.d(TAG, "MSG_REFRESH_DATA");

				/**
				 * add by ls 2015-01-22;
				 */
				if (mEmptyView != null
						&& mEmptyView.getVisibility() == View.VISIBLE) {
					mEmptyView.setVisibility(View.GONE);
				}
				/** end */

				// list =getVehicleList();
				// adapter.notifyDataSetChanged();
				if (NetUtil.isNetworkAvailable(VehicleInfoShowActivity.this)
						&& PutaoAccount.getInstance().isLogin()) {
					queryCarList();
				} else if (!NetUtil
						.isNetworkAvailable(VehicleInfoShowActivity.this)) {
					mEmptyTip.setText(R.string.putao_netexception_hint);
					mEmptyView.setVisibility(View.VISIBLE);
					listView.setVisibility(View.GONE);
				}
				break;
			default:
				break;
			}
		}
	};

	/**
	 * delete by ls 2015-01-22
	 */
	// private BroadcastReceiver carListRefreshReceiver = new
	// BroadcastReceiver(){
	//
	// @Override
	// public void onReceive(Context context, Intent intent) {
	// if(mHandler != null){
	// mHandler.sendEmptyMessage(MSG_REFRESH_DATA);
	// }
	// }
	//
	// };

	@Override
	protected void onResume() {
		super.onResume();
		mHandler.sendEmptyMessage(MSG_REFRESH_DATA);// add by ls 2015-01-22;
	}

	private void initData() {
		adapter = new MyAdapter();
		listView.setAdapter(adapter);
		// mHandler.sendEmptyMessage(MSG_REFRESH_DATA); //delete by ls
		// 2015-01-22
	}

	public class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (list != null) {
				return list.size();
			}
			return 0;

		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(VehicleInfoShowActivity.this,
						R.layout.putao_traffic_offence_vehicle_item, null);
			}
			Vehicle v = list.get(position);
			LogUtil.i(TAG, "getView:" + v);
			TextView province_code = (TextView) convertView
					.findViewById(R.id.province_code);
			province_code.setText(v.getCar_province());// 省份代码, 为完整车牌号的第一个字
			TextView city = (TextView) convertView.findViewById(R.id.location);
			city.setText(v.getProvince_name() + v.getCity_name());
			TextView car_num = (TextView) convertView
					.findViewById(R.id.car_num);
			car_num.setText(v.getCar_no());
			/**
			 * add code by putao_lhq 添加删除功能
			 * 
			 * @start
			 */
			CheckBox multi = (CheckBox) convertView
					.findViewById(R.id.multi_select);
			if (mDeleteMode) {
				multi.setVisibility(View.VISIBLE);
				if (mSelected.contains(String.valueOf(v.getId()))) {
					multi.setChecked(true);
				} else {
					multi.setChecked(false);
				}
			} else {
				multi.setVisibility(View.GONE);
			}
			convertView.setTag(v);
			return convertView;
		}

	}

	@Override
	public void onClick(View v) {
		LogUtil.d(TAG, "onClick");
		int id = v.getId();
		if (id == R.id.next_setp_layout) {
			Intent intent = new Intent(this, VehicleInfoSettingActivity.class);
			intent.putExtra("title", "车辆信息");
			startActivityForResult(intent, 100);
		} else if (id == R.id.back_layout) {
			finish();
		}
		/**
		 * modify by putao_lhq 添加删除功能 add code
		 * 
		 * @start
		 */
		else if (id == R.id.delete_button) {
			delete();
		} else if (id == R.id.empty_layout) {
			if (mHandler != null) {
				mHandler.sendEmptyMessage(MSG_REFRESH_DATA);
			}
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO 开启编辑页面
		/**
		 * modify by putao_lhq 添加删除功能
		 * 
		 * @start old code: Vehicle v = (Vehicle) view.getTag(); Intent intent =
		 *        new Intent(this,VehicleInfoSettingActivity.class);
		 *        intent.putExtra(Vehicle.class.getSimpleName(), v);
		 *        intent.putExtra("position", position); pos=position;
		 *        startActivityForResult(intent, 101);
		 */
		LogUtil.d(TAG, "onItemClick");
		Vehicle v = (Vehicle) view.getTag();
		if (v == null) {
			return;
		}
		if (mDeleteMode) {
			CheckBox multi = (CheckBox) view.findViewById(R.id.multi_select);
			boolean checked = multi.isChecked();
			if (checked) {
				mSelected.remove(String.valueOf(v.getId()));
			} else {
				mSelected.add(String.valueOf(v.getId()));
			}
			adapter.notifyDataSetChanged();
		} else {
			Intent intent = new Intent(this, VehicleInfoSettingActivity.class);
			intent.putExtra(Vehicle.class.getSimpleName(), v);
			intent.putExtra("position", position);
			intent.putExtra("title",
					this.getResources()
							.getString(R.string.vehicle_info_setting));
			pos = position;
			startActivityForResult(intent, 101);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent result) {
		super.onActivityResult(requestCode, resultCode, result);
		LogUtil.i(TAG, "requestCode=" + requestCode + " resultCode="
				+ resultCode + " result=" + result);
	}

	@Override
	public void onPause() {
		super.onPause();
		LogUtil.v(TAG, "onPause");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LogUtil.v(TAG, "onDestroy");
		// unregisterReceiver(carListRefreshReceiver); //delete by ls 2015-01-22
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
			int position, long arg3) {
		LogUtil.d(TAG, "onItemLongClick");
		if (mDeleteMode) {
			return true;
		}
		mDeleteMode = true;
		// mSelected.clear();
		mDeleteView.setVisibility(View.VISIBLE);
		// ((CheckBox)arg1.findViewById(R.id.multi_select)).setChecked(true);
		// Vehicle vehicle= list.get(position);
		// if (vehicle != null && vehicle.getId() != -1) {
		// mSelected.add(String.valueOf(vehicle.getId()));
		// }
		adapter.notifyDataSetChanged();
		return false;
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
					mHandler.sendEmptyMessage(MSG_REFRESH_DATA);
					// Utils.showToast(VehicleInfoShowActivity.this,
					// R.string.vehicle_del_ok, false);
				} else {
					LogUtil.e(TAG, "doDeleteCar vehicleIds=" + vehicleIds
							+ " Failed");
					// Utils.showToast(VehicleInfoShowActivity.this,
					// R.string.vehicle_del_fail, false);
				}
			}

		});
	}

	private void delete() {

		StringBuffer content = new StringBuffer();
		if (mSelected == null || mSelected.size() == 0) {
			LogUtil.d(TAG, "mSelected = 0");
			return;
		}

		for (Iterator iterator = mSelected.iterator(); iterator.hasNext();) {
			String vehicleId = (String) iterator.next();
			if (!vehicleId.equals("-1")) {
				content.append("ids=" + vehicleId + "&");
			}
		}
		LogUtil.d(TAG, "mSelected.ids: " + content.toString());

		doDeleteCar(content.toString());

		// Intent intent = new Intent();
		// intent.setAction(MsgReportParameter.ACTION_MUL_DEL);
		// intent.putExtra(MsgReportParameter.TYPE,
		// MsgReportParameter.PECCANCY);
		// intent.putExtra(MsgReportParameter.VEHICLE_IDS, content.toString());
		// sendBroadcast(intent);
		mDeleteMode = false;
		mDeleteView.setVisibility(View.GONE);
		// initData();
	}

	/**
	 * 去服务器拉取车辆数据 成功后对控件做初始化操作
	 */
	public void queryCarList() {
		mHandler.sendEmptyMessage(MSG_SHOW_DIALOG_ACTION);
		PTHTTP.getInstance().asynPost(MsgReportParameter.QUERY_CARINFO_URL, "",
				new IResponse() {
					@Override
					public void onSuccess(String content) {
						LogUtil.d(TAG, "content : " + content);
						ArrayList<Vehicle> vehicleList = null;
						try {
							JSONObject obj = new JSONObject(content);
							LogUtil.d(TAG, "ret_code : " + obj.get("ret_code"));
							JSONArray ja = obj.getJSONArray("data");
							vehicleList = new ArrayList<Vehicle>();
							for (int i = 0; i < ja.length(); i++) {
								JSONObject json_obj = ja.getJSONObject(i);
								String result = json_obj.toString();
								Vehicle vehicle = Config.mGson.fromJson(result,
										Vehicle.class);
								LogUtil.d(TAG,
										"vehicle count : " + vehicle.toString());
								vehicleList.add(vehicle);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (vehicleList != null) {
							// 刷新本地列表数据 避免多次请求网络数据
							SharedPreferences sp = ContactsApp
									.getInstance()
									.getSharedPreferences(
											PTMessageCenterSettings.SHARED_NAME,
											Context.MODE_MULTI_PROCESS);
							String jsonStr = Config.mGson.toJson(vehicleList,
									new TypeToken<ArrayList<Vehicle>>() {
									}.getType());
							sp.edit()
									.putString(
											TrafficOffenceMsgBusiness.SAVED_CARS,
											jsonStr).commit();
							
							/**delete by ls 2015年1月23日 */
							
//							sp.edit()
//									.putBoolean("can_traffic_offence_show",
//											true).commit();
							/**end*/
							
							LogUtil.d(TAG,
									"vehicle count : " + vehicleList.size());
							list = vehicleList;
							adapter.notifyDataSetChanged();
							mHandler.sendEmptyMessage(MSG_DISMISS_DIALOG_ACTION);
						}
					}

					@Override
					public void onFail(int errorCode) {
						// TODO Auto-generated method stub
						LogUtil.d(TAG, "onFail errorCode : " + errorCode);
						mHandler.sendEmptyMessage(MSG_DISMISS_DIALOG_ACTION);
					}
				});
	}
}
