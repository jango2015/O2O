package so.contacts.hub.ui.yellowpage;

import so.contacts.hub.util.UMengEventIds;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.active.ActiveUtils;
import so.contacts.hub.businessbean.ExpressHistoryBean;
import so.contacts.hub.core.ConstantsParameter;
import so.contacts.hub.remind.BaseRemindActivity;
import so.contacts.hub.ui.yellowpage.bean.Express;
import so.contacts.hub.util.ContactsAppUtils;
import so.contacts.hub.util.ContactsHubUtils;
import so.contacts.hub.util.ExpressSmartMatchUtil;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.NetUtil;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import so.contacts.hub.util.MobclickAgentUtil;

import com.yulong.android.contacts.discover.R;

public class YellowPageExpressSelectReslutPage extends BaseRemindActivity implements
		OnClickListener {

	public static final String TAG = "YellowPageExpressSelectReslutPage";

	public static final String PARTNER_NAME = "putao";
	public static final String VERSION = "v1";
	public static final String SELECT_KEY = "993abcd3694c6a29e1cc909c2766c5de";
	
	public static final String EXTRA_EXP_COM_ID = "extra_exp_com_id";
	public static final String EXTRA_EXP_COM_NAME = "extra_exp_com_name";
	public static final String EXTRA_EXP_NUM = "extra_exp_num";
	
	public static final String STATUS_SUCCESS = "Success ";
	public static final String STATUS_FAIL = "fail ";

	public static final String EXPRESS_SELECT_URL_HEADER = "http://www.kuaidihelp.com/api?";

	private RelativeLayout titleBackLayout;
	private TextView titleTextView;
	private TextView statusTextView;
	private ScrollView resultScrollView;
	private LinearLayout resultLayout;
	private RelativeLayout waitLayout;
	private RelativeLayout errorLayout;
	private TextView infoTextView;
	private Button reTryBtn;
	private TextView expNumTextView;
	
	private View footerView;
	
	// 快递公司信息 start
	private RelativeLayout mExpressCompanyLayout = null;
	private ImageView mExpressCompanyLogoImgView = null;
	private TextView mExpressCompanyNameTView = null;
	private TextView mExpressCompanyPhoneTView = null;
	private Button mExpressCompanyCallBtn = null;
	// 快递公司信息 end
	
	private String mExpressCompanyPhone = null;

	private String exp_com_id = null;
	private String exp_com_name;
	private String exp_num;
	private String exp_date;

	private SharedPreferences spHistory;
	
	public static final int EXPRESS_HISTORY_MAX_SIZE = 20;
	
	public static final int MSG_SET_INFO_TEXT = 0x2001;

	private static final int MSG_SHOW_NO_DATA_SAVE_ACTION = 0x2002;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.putao_express_select_result_page);

		spHistory = getSharedPreferences(YellowPageExpressSelectHome.EXPRESS_HISTORY, Context.MODE_MULTI_PROCESS);
		
		parseIntent();
		initView();
		mExpmatch = new ExpressSmartMatchUtil(this, exp_num);
		queryExpress();
			
		//lihq modify for plug-1.3 end
	}

	private void queryExpress() {
		if (NetUtil.isNetworkAvailable(YellowPageExpressSelectReslutPage.this)) {
			onPreExecuteH();
			mExpmatch.query(exp_num);
		} else {
			mHandler.obtainMessage(MSG_SET_INFO_TEXT, R.string.putao_no_net, 0).sendToTarget();
		}
	}

	private void initView() {
		titleBackLayout = (RelativeLayout) findViewById(R.id.back_layout);
		titleBackLayout.setOnClickListener(this);
		titleTextView = (TextView) findViewById(R.id.title);
		//lihq modify for plug-1.3 start
		if (null != exp_com_name && !"".equals(exp_com_name)) {
			titleTextView.setText(exp_com_name);
		} else {
			titleTextView.setText(R.string.putao_express_check_title);
		}
		//lihq modify for plug-1.3 end
		
		footerView = View.inflate(this, R.layout.putao_express_footer_view, null);
		
		statusTextView = (TextView) findViewById(R.id.express_status);
		statusTextView.setVisibility(View.GONE);
		resultScrollView = (ScrollView) findViewById(R.id.reault_scrollview);
		resultLayout = (LinearLayout) findViewById(R.id.select_result_layout);
		waitLayout = (RelativeLayout) findViewById(R.id.wait_layout);
		errorLayout = (RelativeLayout) findViewById(R.id.select_error_result_layout);
		infoTextView = (TextView) findViewById(R.id.favorite_info_textview);
		expNumTextView = (TextView) findViewById(R.id.express_num_value);
		if (null != exp_num) {
			expNumTextView.setText(exp_num);
		}

		reTryBtn = (Button) findViewById(R.id.express_select_retry_btn);
		reTryBtn.setOnClickListener(this);
		
		mExpressCompanyLayout = (RelativeLayout) findViewById(R.id.express_company_show_layout);
		mExpressCompanyLogoImgView = (ImageView) findViewById(R.id.express_company_logo);
		mExpressCompanyNameTView = (TextView) findViewById(R.id.express_company_name);
		mExpressCompanyPhoneTView = (TextView) findViewById(R.id.express_company_phone);
		mExpressCompanyCallBtn = (Button) findViewById(R.id.express_company_call);
		mExpressCompanyCallBtn.setOnClickListener(this);
		
	}

	public void setExpressTitle(String title) {
		if (TextUtils.isEmpty(title)) {
			return;
		}
		titleTextView.setText(title);
	}
	
	private void parseIntent() {
		Intent i = getIntent();
		exp_com_id = i.getStringExtra(EXTRA_EXP_COM_ID);
		exp_com_name = i.getStringExtra(EXTRA_EXP_COM_NAME);
		exp_num = i.getStringExtra(EXTRA_EXP_NUM);
	}

	public void setExpInfo(String exp_id, String exp_name) {
		this.exp_com_id = exp_id;
		this.exp_com_name = exp_name;
	}
	
	public void onPreExecuteH() {
		waitLayout.setVisibility(View.VISIBLE);
		errorLayout.setVisibility(View.GONE);
		resultScrollView.setVisibility(View.GONE);
		statusTextView.setVisibility(View.GONE);
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
	
	private View getItemView(String content, String date, boolean isLlightCircle, boolean hideTop, boolean hideBottom) {
		View view = View.inflate(this,
				R.layout.putao_yellow_page_express_result_item, null);

		TextView contentTextView = (TextView) view
				.findViewById(R.id.info_content);
		TextView dateTextView = (TextView) view.findViewById(R.id.info_date);
		ImageView circleImageView = (ImageView) view.findViewById(R.id.circle_imageview);
		
		circleImageView
				.setImageResource(isLlightCircle ? R.drawable.putao_icon_mileage_p
						: R.drawable.putao_icon_mileage);
		
//		LinearLayout infoLayout = (LinearLayout) view
//				.findViewById(R.id.info_layout);
//		RelativeLayout.LayoutParams llp = new RelativeLayout.LayoutParams(
//				LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
//		llp.addRule(RelativeLayout.RIGHT_OF, R.id.left_layout);
//		llp.setMargins(5, 10, 0, 10);
//		infoLayout.setLayoutParams(llp);
		
		View hideTopView = view.findViewById(R.id.hide_top_view);
		View hideBottomView = view.findViewById(R.id.hide_bottom_view);

		hideTopView.setVisibility(hideTop ? View.VISIBLE : View.GONE);
		hideBottomView.setVisibility(hideBottom ? View.VISIBLE : View.GONE);
		
//		if (null != content) {
//			SpannableStringBuilder style=new SpannableStringBuilder(content);
//	        style.setSpan(new ForegroundColorSpan(Color.RED),3,8,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//	        contentTextView.setText(style);
//		}
		
		if (null != content) {
			contentTextView.setText(content.trim());
		}

		if (null != date) {
			dateTextView.setText(date);
		}

		return view;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.back_layout) {
			this.finish();
		} else if (id == R.id.express_select_retry_btn) {
			queryExpress();
		}else if( id == R.id.express_company_call ){
			if( !TextUtils.isEmpty(mExpressCompanyPhone) ){
				ContactsHubUtils.call(YellowPageExpressSelectReslutPage.this, mExpressCompanyPhone);
			}
		}
	}
	
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch(what){
			case MSG_SET_INFO_TEXT:
				int resid = msg.arg1;
				infoTextView.setText(resid);
				break;
			case MSG_SHOW_NO_DATA_SAVE_ACTION:
				showLayoutWithNoData();
				break;
			default:
				break;
			}
		};
	};

	private ExpressSmartMatchUtil mExpmatch = null;
	
	public static String getFormatTime() {
	     //2014-01-01 12:12:12
		String formatData = "";
		SimpleDateFormat formatter = null;
		try{
			formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");     
			Date curDate = new Date(System.currentTimeMillis());//获取当前时间     
			formatData = formatter.format(curDate);
		}catch(Exception e){
		}
		return formatData;
	}
	
	private void showLayoutWithNoData(){
		waitLayout.setVisibility(View.GONE);
		errorLayout.setVisibility(View.VISIBLE);
		resultScrollView.setVisibility(View.GONE);
		statusTextView.setVisibility(View.GONE);
		saveExceptionHistory();
	}

	/**
	 * 显示快递公司相关信息
	 */
	private void showExpressCompanyInfo(){
		if( TextUtils.isEmpty(exp_com_id) ){
			return;
		}
		Express express = ContactsAppUtils.getInstance().getDatabaseHelper().getYellowPageDBHelper().queryExpress(exp_com_id);
		if( express == null ){
			LogUtil.i(TAG, "showExpressCompanyInfo query express is null.");
			return;
		}
		mExpressCompanyPhone = express.getPhone();
		
		int defaultResId = getResources().getIdentifier(express.getLogo(), "drawable", getPackageName());
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), defaultResId);
        if(bitmap == null){
        	bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.putao_a0521);
        }
        
        if( bitmap != null ){
        	int corner = getResources().getDimensionPixelSize(R.dimen.putao_image_round_corner);
        	mExpressCompanyLogoImgView.setImageBitmap(ContactsHubUtils.corner(bitmap, corner, 0));
        }
        if( TextUtils.isEmpty(exp_com_name) ){
        	exp_com_name = express.getName();
        }
        mExpressCompanyNameTView.setText(exp_com_name);
        mExpressCompanyPhoneTView.setText(mExpressCompanyPhone);
		mExpressCompanyLayout.setVisibility(View.VISIBLE);
	}
	
	public void onPostExecuteH(Object result) {
		String reusltStr = "";
		if (result == null) {
			mHandler.sendEmptyMessage(MSG_SHOW_NO_DATA_SAVE_ACTION);
			return;
		}
		showExpressCompanyInfo();
		
		
		reusltStr = (String) result;
		LogUtil.d(TAG, "onPostExecuteH result = " + reusltStr);
		int signStatus = ExpressHistoryBean.STATUS_UNKNOW;
		int statusId = R.string.putao_express_result_status_unknow;
		
		try {
			JSONObject jsonObject = new JSONObject(reusltStr);
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
				
				boolean isLight = i == 0 ? true : false;
				boolean hideTop = i == 0 ? true : false;
				boolean hideBottom = i == readInfos.size() - 1 ? true : false;
				
				View itemView = getItemView(content.toString().trim(),
						exp_date, isLight, hideTop, hideBottom);
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
						LayoutParams.MATCH_PARENT,
						getResources().getDimensionPixelSize(
								R.dimen.putao_express_result_item_height));
				
				if(content.toString().trim().length() > 30){
					lp.height = getResources().getDimensionPixelSize(
							R.dimen.putao_express_result_item_height_x);
				}
				
				if(i == 0){
					signStatus = checkExpressStatus(content.toString());
				}
				
				resultLayout.addView(itemView, lp);
				
				if (i == readInfos.size() - 1) {
					LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
							LayoutParams.MATCH_PARENT,
							getResources().getDimensionPixelSize(
									R.dimen.putao_express_result_footer_view_height));
					lp1.topMargin = 50;
					resultLayout.addView(footerView, lp1);
				}
			}
			
			waitLayout.setVisibility(View.GONE);
			errorLayout.setVisibility(View.GONE);                                                                  
			resultScrollView.setVisibility(View.VISIBLE);

			String dateStr = null == exp_date ? "" : exp_date;
			switch (signStatus) {
			case ExpressHistoryBean.STATUS_SIGNOFF:
				statusId = R.string.putao_express_result_status_complete;
				break;
			case ExpressHistoryBean.STATUS_IN_TRANSIT:
				statusId = R.string.putao_express_result_status_in_transit;
				break;
			case ExpressHistoryBean.STATUS_UNKNOW:
				statusId = R.string.putao_express_result_status_unknow;
				break;
			}
			
			statusTextView.setVisibility(View.VISIBLE);
			statusTextView.setText(statusId);

			ExpressHistoryBean hb = new ExpressHistoryBean();
			hb.comId = exp_com_id;
			hb.comName = exp_com_name;
			hb.num = exp_num;
			hb.status = signStatus;
			hb.date = dateStr;
			
			saveHistory(hb);
		} catch (JSONException e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(MSG_SHOW_NO_DATA_SAVE_ACTION);
		}
	}
	
	/**
	 * 保存 未查询到结果的单号
	 */
	private void saveExceptionHistory(){
		ExpressHistoryBean hb = new ExpressHistoryBean();
		//putao_lhq modify for BUG #1343 start
		hb.comId = "unknown";
		hb.comName = getString(R.string.putao_text_failed_express);
		hb.num = exp_num;
		hb.status = ExpressHistoryBean.STATUS_NODTA;
		hb.date = getFormatTime();
		//putao_lhq modify for BUG #1343 end
		saveHistory(hb);
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
						
						if(i == EXPRESS_HISTORY_MAX_SIZE - 2){
							break;
						}
						
						sb.append(ConstantsParameter.SHAREDPREFERENCES_DATA_DELIMITER);
					}
					newHistory = sb.toString();
					LogUtil.d("putao_lhq", "newHistory: " + newHistory);
				}
			}

			spHistory
					.edit()
					.putString(YellowPageExpressSelectHome.EXPRESS_HISTORY_KEY,
							newHistory).commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	protected void onDestroy() {
		if (mExpmatch != null) {
			mExpmatch.onDestroy();
		}
		super.onDestroy();
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Integer remindCode() {
		// TODO Auto-generated method stub
		return mRemindCode;
	}
	
	
}
