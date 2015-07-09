package so.contacts.hub.ui.yellowpage;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import so.contacts.hub.adapter.ExpressHistoryListAdapter;
import so.contacts.hub.businessbean.ExpressHistoryBean;
import so.contacts.hub.core.ConstantsParameter;
import so.contacts.hub.http.WebServiceUtils;
import so.contacts.hub.remind.BaseRemindActivity;
import so.contacts.hub.util.CommonValueUtil;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.NetUtil;
import so.contacts.hub.util.ThreadPoolUtil;
import so.contacts.hub.widget.CommonDialog;
import so.contacts.hub.widget.CommonDialogFactory;
import so.contacts.hub.widget.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import so.contacts.hub.util.MobclickAgentUtil;

import com.yulong.android.contacts.discover.R;

public class YellowPageExpressSelectHistoryPage extends BaseRemindActivity implements
		OnClickListener, OnItemClickListener, OnItemLongClickListener,
		OnCheckedChangeListener {

	public static final String TAG = "YellowPageExpressSelectHistoryPage";

	private SharedPreferences spHistory;
	public static final String EXPRESS_HISTORY = "express_history";
	public static final String EXPRESS_HISTORY_KEY = "express_history_key";
	public static final String EXPRESS_HISTORY_EXPRESS_COMPANY_INFO_KEY = "express_history_express_company_info_key";

	private ListView historyListView;
	private ArrayList<ExpressHistoryBean> historyList = new ArrayList<ExpressHistoryBean>();
	private ExpressHistoryListAdapter histotyListAdapter;
	private ArrayList<ExpressHistoryBean> deleteList = new ArrayList<ExpressHistoryBean>();

	private LinearLayout deleteParentLayout;
	private CheckBox selectAllCheckBox;
	private TextView selectAllTextView;
	private RelativeLayout noHistoryLayout;

	private boolean isDeleteMode = false;

	private CommonDialog deleteDialog;

	private ScreenBroadcastReceiver mReceiver;
	
	private ProgressDialog mProgressDialog = null;
	
	public static final int MSG_UPDATE_EXPRESS_STATUS = 0x001;
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case MSG_UPDATE_EXPRESS_STATUS:
				ExpressHistoryBean bean = (ExpressHistoryBean) msg.obj;
				if( historyList == null || bean == null){
					return;
				}
				for(int i = 0; i < historyList.size(); i++){
					ExpressHistoryBean historyBean = historyList.get(i);
					if( historyBean.num.equals(bean.num) && historyBean.num.equals(bean.num) ){
						historyBean.status = bean.status;
						histotyListAdapter.setData(historyList);
						break;
					}
				}
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		spHistory = getSharedPreferences(EXPRESS_HISTORY, MODE_MULTI_PROCESS);

		setContentView(R.layout.putao_express_select_history_page);

		initView();

		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_USER_PRESENT);
		mReceiver = new ScreenBroadcastReceiver();
		registerReceiver(mReceiver, filter);
	}
	
	private void showDialog() {
		if( mProgressDialog == null ){
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setMessage(getString(R.string.putao_yellow_page_loading));
		}
        mProgressDialog.show();
    }
	
	 private void disMissDialog() {
		 if (mProgressDialog != null && mProgressDialog.isShowing()) {
			 mProgressDialog.dismiss();
		 }
	 }

	@Override
	protected void onResume() {
		super.onResume();
		if (mReceiver.wasScreenOff) {

		} else {
			showDialog();
			loadHistory();
		}
		MobclickAgentUtil.onResume(this);
	}

	@Override
	protected void onPause() {
		MobclickAgentUtil.onPause(this);
		super.onPause();
	}

	private class ScreenBroadcastReceiver extends BroadcastReceiver {
		private String action = null;

		public boolean wasScreenOff = false;

		@Override
		public void onReceive(Context context, Intent intent) {
			action = intent.getAction();
			if (Intent.ACTION_SCREEN_ON.equals(action)) {
				// 开屏
			} else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
				// 锁屏
				wasScreenOff = true;
			} else if (Intent.ACTION_USER_PRESENT.equals(action)) {
				// 解锁
				wasScreenOff = false;
			}
		}
	}

	private void initView() {
		((TextView) findViewById(R.id.title)).setText(getResources().getString(
				R.string.putao_express_select_history_title));
		findViewById(R.id.back_layout).setOnClickListener(this);

		historyListView = (ListView) findViewById(R.id.express_history_listview);
		histotyListAdapter = new ExpressHistoryListAdapter(this, historyList);
		historyListView.setAdapter(histotyListAdapter);
		historyListView.setOnItemLongClickListener(this);
		historyListView.setOnItemClickListener(this);

		deleteParentLayout = (LinearLayout) findViewById(R.id.delete_parent_layout);
		selectAllCheckBox = (CheckBox) findViewById(R.id.express_history_select_all_check_box);
		selectAllCheckBox.setOnCheckedChangeListener(this);

		selectAllTextView = (TextView) findViewById(R.id.select_all_tv);

		findViewById(R.id.delete_button).setOnClickListener(this);

		noHistoryLayout = (RelativeLayout) findViewById(R.id.no_history_layout);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.back_layout) {
			this.finish();
		} else if (id == R.id.delete_button) {
			if (null == deleteList || deleteList.size() == 0) {
				showToast(R.string.putao_express_history_no_select_history);
				return;
			}
			deleteDialog = CommonDialogFactory.getOkCancelCommonDialog(this);
			deleteDialog.setTitle(R.string.putao_express_history_delete_dialog_title);
			TextView msgTv = deleteDialog.getMessageTextView();
			msgTv.setText(R.string.putao_express_history_delete_confirm);
			deleteDialog.setOkButtonClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (selectAllCheckBox.isChecked()) {
						deleteAllHistory();
					} else {
						deleteAllHistory();
						for (int i = historyList.size() - 1; i >= 0; i--) {
							ExpressHistoryBean oldbean = historyList.get(i);
							if (!deleteList.contains(oldbean)) {
								saveHistory(oldbean);
							}
						}
					}
					histotyListAdapter.notifyDataSetChanged();
					loadHistory();
					deleteDialog.dismiss();
				}
			});
			deleteDialog.show();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		ExpressHistoryBean hb = historyList.get(position);
		if (isDeleteMode) {
			hb.isCheck = !hb.isCheck;

			histotyListAdapter.notifyDataSetChanged();

			if (hb.isCheck) {
				if (!deleteList.contains(hb)) {
					deleteList.add(hb);
				}
			} else {
				if (deleteList.contains(hb)) {
					deleteList.remove(hb);
				}
			}
			checkSelectAllCheckBox();
		} else {
			//putao_lhq modify for BUG #1343 start
			if (hb.status == ExpressHistoryBean.STATUS_NODTA) {
				startSelectActivity(hb.comId, "", hb.num);
			} else {
				startSelectActivity(hb.comId, hb.comName, hb.num);
			}
			//putao_lhq modify for BUG #1343 end
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		ExpressHistoryBean hb = historyList.get(position);
		if (!isDeleteMode) {
			isDeleteMode = true;
			hb.isCheck = true;
			deleteList.add(hb);
			histotyListAdapter.setMode(isDeleteMode);
			deleteParentLayout.setVisibility(View.VISIBLE);
			checkSelectAllCheckBox();
			return true;
		}
		return false;
	}

	private void checkSelectAllCheckBox() {
		if (deleteList.size() == historyList.size()) {
			selectAllCheckBox.setOnCheckedChangeListener(null);
			selectAllCheckBox.setChecked(true);
			selectAllTextView.setText(R.string.putao_cancle_all_choose);
			selectAllCheckBox
					.setOnCheckedChangeListener(YellowPageExpressSelectHistoryPage.this);
		} else {
			selectAllCheckBox.setOnCheckedChangeListener(null);
			selectAllCheckBox.setChecked(false);
			selectAllTextView.setText(R.string.putao_all_choose);
			selectAllCheckBox
					.setOnCheckedChangeListener(YellowPageExpressSelectHistoryPage.this);
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		deleteList.clear();

		for (ExpressHistoryBean hb : historyList) {
			hb.isCheck = isChecked;
		}

		if (isChecked) {
			deleteList.addAll(historyList);
			selectAllTextView.setText(R.string.putao_cancle_all_choose);
		} else {
			deleteList.clear();
			selectAllTextView.setText(R.string.putao_all_choose);
		}
		histotyListAdapter.setData(historyList);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (isDeleteMode) {
				isDeleteMode = false;
				deleteList.clear();
				for (ExpressHistoryBean ehb : historyList) {
					ehb.isCheck = false;
				}
				histotyListAdapter.setMode(isDeleteMode);
				histotyListAdapter.notifyDataSetChanged();
				deleteParentLayout.setVisibility(View.GONE);
				return true;
			}
		}
		return super.dispatchKeyEvent(event);
	}

	LoadHistoryTask loadHistoryTask;
	private void loadHistory() {
	    loadHistoryTask = new LoadHistoryTask();
	    loadHistoryTask.execute();
	}

	class LoadHistoryTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			historyList.clear();
			deleteList.clear();
			isDeleteMode = false;
			histotyListAdapter.setMode(isDeleteMode);
			histotyListAdapter.notifyDataSetChanged();
			deleteParentLayout.setVisibility(View.GONE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			String histyryStr = spHistory.getString(EXPRESS_HISTORY_KEY, null);

			if (null == histyryStr || "".equals(histyryStr.trim())) {
				// showListView(false);
				return null;
			}

			String[] historyBeans = histyryStr
					.split(ConstantsParameter.SHAREDPREFERENCES_DATA_DELIMITER);

			if (null == historyBeans || historyBeans.length == 0) {
				// showListView(false);
				return null;
			}

			for (String historyBean : historyBeans) {
				String[] value = historyBean
						.split(ConstantsParameter.SHAREDPREFERENCES_DATA_DELIMITER_SECOND);
				if (null == value) {
					continue;
				}
				ExpressHistoryBean hb = new ExpressHistoryBean();
				hb.comId = value[0];
				hb.comName = value[1];
				hb.num = value[2];
				try {
					hb.status = Integer.parseInt(value[3]);
				} catch (Exception e) {
				}
				hb.date = value[4];

				historyList.add(hb);
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			disMissDialog();
			histotyListAdapter.notifyDataSetChanged();
			boolean needShow = false;
			if (null != historyList && historyList.size() > 0) {
				needShow = true;
			}
			showListView(needShow);
			if( needShow ){
				doCheckHsitorydataStatus();
			}
		}
	}
	
	/**
	 * 检测快递历史中所有数据的状态
	 */
	private void doCheckHsitorydataStatus(){
		if ( !NetUtil.isNetworkAvailable(YellowPageExpressSelectHistoryPage.this) ) {
			return;
		}
		for (int i = 0; i < historyList.size(); i++) {
			ExpressHistoryBean hb = historyList.get(i);
			if (null != hb && hb.status != ExpressHistoryBean.STATUS_SIGNOFF) {
				String tokenStr = hb.comId + "," + hb.num + ","
						+ YellowPageExpressSelectReslutPage.PARTNER_NAME + ","
						+ YellowPageExpressSelectReslutPage.SELECT_KEY;
				final String requestUrl = WebServiceUtils.BuildReqStr(
						WebServiceUtils.getMd5Token(tokenStr), hb.comId, hb.num);
				final int index = i;
				ThreadPoolUtil.getInstance().execute(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						loadAndUpdateHistorydataStatus(requestUrl, index);
					}
				});
			}
		}
	}
	
	/**
	 * 更新快递历史中数据的状态
	 * (注：只对 "运送途中" 或者 "已签收" 两种状态进行更新 )
	 */
	private void loadAndUpdateHistorydataStatus(String requestUrl, int index){
		if( TextUtils.isEmpty(requestUrl) ){
			LogUtil.i(TAG, "loadAndUpdateHistorydataStatus url is null.");
			return;
		}
		String responseData = WebServiceUtils.queryExpress(requestUrl);
		if( TextUtils.isEmpty(responseData) ){
			LogUtil.i(TAG, "loadAndUpdateHistorydataStatus result is null or size is 0.");
			return;
		}
		LogUtil.i(TAG, "loadAndUpdateHistorydataStatus requestUrl is ok.");
		String exp_date = "";
		String exp_com_id = "";
		String exp_com_name = "";
		String exp_num = "";
		
		int signStatus = ExpressHistoryBean.STATUS_UNKNOW;
		
		try {
			JSONObject jsonObject = new JSONObject(responseData);
			JSONObject jsonObjectResponse = jsonObject
					.getJSONObject("response");

			JSONArray jsonArrayBody = jsonObjectResponse
					.getJSONArray("body");
			ArrayList<String> readInfos = new ArrayList<String>();
			for (int i = 0; i < jsonArrayBody.length(); i++) {
				String itemStr = (String) jsonArrayBody.opt(i);
				readInfos.add(0,itemStr);
			}
			
			for (int i = 0; i < readInfos.size(); i++) {
				String info = readInfos.get(i);
				if (null == info || "".equals(info)) {
					continue;
				}
				
				info = info.replaceAll("　", " ");
				
				String[] data = info.split(" ");
				StringBuffer date = new StringBuffer();
				StringBuffer content = new StringBuffer();
				for (int j = 0; j < data.length; j++) {

					String item = data[j];
					
					if (null == item || "".equals(item)) {
						continue;
					}
					
					if (j == 0) {
						date.append(item);
					} else if (j == 1) {
						date.append(" ").append(item);
					} else {
						content.append(item).append(" ");
					}
				}
				
				exp_date = dateStrOperating(date.toString().trim());
				
				if(i == 0){
					signStatus = checkExpressStatus(content.toString());
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if( TextUtils.isEmpty(exp_com_name) || TextUtils.isEmpty(exp_num) ){
			//快递公司名称为空 或者 快递号 为空则不需要更新
			return;
		}
		
		String dateStr = null == exp_date ? "" : exp_date;

		ExpressHistoryBean hb = new ExpressHistoryBean();
		hb.comId = exp_com_id;
		hb.comName = exp_com_name;
		hb.num = exp_num;
		hb.status = signStatus;
		if( index % 4 == 0 ){
			hb.status = ExpressHistoryBean.STATUS_SIGNOFF;
		}
		hb.date = dateStr;
		
		
		if( signStatus == ExpressHistoryBean.STATUS_SIGNOFF || 
				signStatus == ExpressHistoryBean.STATUS_IN_TRANSIT){
			// 运送途中 或者 已签收两种状态进行更新
			Message msg = mHandler.obtainMessage();
			msg.what = MSG_UPDATE_EXPRESS_STATUS;
			mHandler.obtainMessage(MSG_UPDATE_EXPRESS_STATUS, hb).sendToTarget();
			saveHistory(hb);
			
		}
	}

	private String dateStrOperating(String oldString){
		if(null == oldString){
			return "";
		}

		// String rex = "^[0-9]([3][0-9]{1}|59|58|88|89)[0-9]{8}$";
		oldString = oldString.trim();
		
		// 去掉尾部的“.”
		if (oldString.endsWith(".")) {
			oldString = oldString.substring(0, oldString.lastIndexOf("."));
		}
		// 处理日期与时间之间没有空格的问题,例如“2014-01-0112:12:12”
		// 一般格式为 “2014-01-01 12:12:12”
		try {
			if(oldString.length() == 18){
				oldString = oldString.substring(0, 10) + " "
						+ oldString.substring(10, oldString.length());
			}
			return oldString;
		} catch (Exception e) {
		}
		
		return oldString;
	}

	protected int checkExpressStatus(String lastContent) {

		if (null == lastContent || "".equals(lastContent.trim())) {
			return ExpressHistoryBean.STATUS_UNKNOW;
		}

		String[] expressSignoffArrays = getResources().getStringArray(
				R.array.putao_express_Signoff_string_array);

		for (String s : expressSignoffArrays) {
			if (lastContent.contains(s)) {
				return ExpressHistoryBean.STATUS_SIGNOFF;
			}
		}

		return ExpressHistoryBean.STATUS_IN_TRANSIT;
	}
	
	private void showListView(boolean haveData) {
		if (haveData) {
			historyListView.setVisibility(View.VISIBLE);
			noHistoryLayout.setVisibility(View.GONE);
		} else {
			historyListView.setVisibility(View.GONE);
			noHistoryLayout.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onDestroy() {
		if (null != mReceiver) {
			try {
				unregisterReceiver(mReceiver);
			} catch (Exception e) {
			}
		}
		ThreadPoolUtil.getInstance().exitThreadPool();
		if(loadHistoryTask != null){
		    loadHistoryTask.cancel(true);
		    loadHistoryTask = null;
		}
		super.onDestroy();
	}

	private void saveHistory(ExpressHistoryBean hisbean) {
		// comId,comName,num,status,date,time;comId,comName,num,status,date,time
		if (null == hisbean) {
			return;
		}

		try {
			String exp_com_id = hisbean.comId;
			String exp_com_name = hisbean.comName;
			String exp_num = hisbean.num;
			int status = hisbean.status;
			String dateStr = hisbean.date;

			if (null == exp_com_id || null == exp_com_name || null == exp_num
					|| null == dateStr || "".equals(exp_com_id)
					|| "".equals(exp_com_name) || "".equals(exp_num)
					|| "".equals(status) || "".equals(dateStr)) {
				return;
			}

			String newLine = exp_com_id
					+ ConstantsParameter.SHAREDPREFERENCES_DATA_DELIMITER_SECOND
					+ exp_com_name
					+ ConstantsParameter.SHAREDPREFERENCES_DATA_DELIMITER_SECOND
					+ exp_num
					+ ConstantsParameter.SHAREDPREFERENCES_DATA_DELIMITER_SECOND
					+ status
					+ ConstantsParameter.SHAREDPREFERENCES_DATA_DELIMITER_SECOND
					+ dateStr;

			String historyStr = spHistory.getString(
					YellowPageExpressSelectHome.EXPRESS_HISTORY_KEY, "");

			String newHistory = null;

			if (null == historyStr || "".equals(historyStr)) {
				newHistory = newLine;
			} else {
				// 判断是否和上次查询的单号和快递公司id一样
				// 如果是一样则只是更新数据
				String[] historys = historyStr
						.split(ConstantsParameter.SHAREDPREFERENCES_DATA_DELIMITER);
				if (null == historys || historys.length <= 0) {
					newHistory = newLine;
				} else {
					int index = -1;
					boolean isUpdate = false;

					ArrayList<String> historyList = new ArrayList<String>();
					for (String s : historys) {
						historyList.add(s);
					}

					for (int i = 0; i < historyList.size(); i++) {
						String historyStrs = historyList.get(i);
						if (null == historyStrs || "".equals(historyStrs)) {
							continue;
						}
						String[] second = historyStrs
								.split(ConstantsParameter.SHAREDPREFERENCES_DATA_DELIMITER_SECOND);

						if (null == second) {
							continue;
						}
						// 判断快递公司id和快递号是否一样
						// modify by putao_lhq 2014年10月11日 for BUG #1551 start
						/*if (exp_com_id.equals(second[0])
								&& exp_num.equals(second[2])) {*/
						if ((exp_com_id.equals(second[0]) || second[0].equals("unknown"))
								&& exp_num.equals(second[2])) {
						// modify by putao_lhq 2014年10月11日 for BUG #1551 end
							isUpdate = true;
							index = i;
							break;
						}
					}
					//putao_lhq modify for 对于已查快递历史记录显示固定，不再更新到顶部 start
					StringBuffer sb = new StringBuffer();
					if (isUpdate && index != -1) {
						historyList.remove(index);
						historyList.add(index, newLine);
					} else {
						sb.append(newLine);
						if (historyList.size() > 0) {
							sb.append(ConstantsParameter.SHAREDPREFERENCES_DATA_DELIMITER);
						}
					}

					//putao_lhq modify for 对于已查快递历史记录显示固定，不再更新到顶部 end
					for (int i = 0; i < historyList.size(); i++) {
						sb.append(historyList.get(i));
						if (i == historyList.size() - 1) {
							break;
						}
						sb.append(ConstantsParameter.SHAREDPREFERENCES_DATA_DELIMITER);

					}
					newHistory = sb.toString();
				}
			}

			spHistory
					.edit()
					.putString(YellowPageExpressSelectHome.EXPRESS_HISTORY_KEY,
							newHistory).commit();
		} catch (Exception e) {
		}
	}

	private void deleteAllHistory() {
		spHistory
				.edit()
				.putString(YellowPageExpressSelectHome.EXPRESS_HISTORY_KEY,
						null).commit();
	}

	private void startSelectActivity(String expComIdStr, String expComNameStr,
			String expNumStr) {
		Intent i = new Intent(YellowPageExpressSelectHistoryPage.this,
				YellowPageExpressSelectReslutPage.class);
		i.putExtra(YellowPageExpressSelectReslutPage.EXTRA_EXP_COM_ID,
				expComIdStr);
		i.putExtra(YellowPageExpressSelectReslutPage.EXTRA_EXP_COM_NAME,
				expComNameStr);
		i.putExtra(YellowPageExpressSelectReslutPage.EXTRA_EXP_NUM, expNumStr);

		startActivity(i);
	}

	private Toast mToast;

	private void showToast(int stringId) {
		if (null == mToast) {
			mToast = Toast.makeText(this, stringId, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(stringId);
			mToast.setDuration(Toast.LENGTH_SHORT);
		}
		mToast.show();
	}

	@Override
	public String getServiceNameByUrl() {
		// TODO Auto-generated method stub
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
		return mRemindCode;
	}

}
