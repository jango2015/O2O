package so.contacts.hub.ui.yellowpage;

import so.contacts.hub.thirdparty.baidu.BaiduUriApiUtil;
import so.contacts.hub.ad.AdCode;
import so.contacts.hub.core.Config;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.UMengEventIds;
import so.contacts.hub.util.YellowUtil;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import so.contacts.hub.util.MobclickAgentUtil;

import com.yulong.android.contacts.discover.R;

/**
 * 公交换乘
 * 
 * @author putao_lhq
 * 
 */
public class BusChangeFragment extends BusBaseFragment implements
		OnClickListener, OnTouchListener {
	private static final String TAG = "BusChangeFragment";
	
	private static String loc_ok_text;

	private EditText mFrom;
	private EditText mTo;
	private Button mQueryBtn;
	private ImageView mClearFrom;
	private ImageView mClearTo;
	
	private String mCity;
	private double mLatitude;
	private double mLongitude;
	private static final int LOCATION_SUCCESS_ACTION = 100;
	private static final int LOCATION_FAILED_ACTION = 101;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {


		@Override
		public void handleMessage(Message msg) {
			if (mFrom == null || getActivity() == null) {
				return;
			}
			switch (msg.what) {
			case LOCATION_SUCCESS_ACTION:
				loc_ok_text = getActivity().getResources()
						.getString(R.string.putao_bus_form_location_ok_text);
				mFrom.setText(loc_ok_text);
				mFrom.setSelection(loc_ok_text.length());
				// putao_lhq modify for BUG #1358 end
				break;
			case LOCATION_FAILED_ACTION:
				// 定位失败
				mFrom.setHint(getActivity().getResources().getString(
						R.string.putao_bus_form_location_failed_text));
				break;
			}
		}

	};

	private BusTextWatcher mWatcherFrom;
	private BusTextWatcher mWatcherTo;
	private InputMethodManager mInputManager;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.putao_bus_line_fragment, container,
				false);
		mQueryBtn = (Button) view.findViewById(R.id.bus_line_query_btn);
		mListView = (ListView) view.findViewById(R.id.expand);
		mFrom = (EditText) view.findViewById(R.id.from);
		mTo = (EditText) view.findViewById(R.id.to);
		mClearFrom = (ImageView) view.findViewById(R.id.clear);
		mClearTo = (ImageView) view.findViewById(R.id.clear_to);
		mWatcherFrom = new BusTextWatcher(WHICH_FROM);
		mWatcherTo = new BusTextWatcher(WHICH_TO);
		mFrom.addTextChangedListener(mWatcherFrom);
		mTo.addTextChangedListener(mWatcherTo);
	
		mFrom.setOnTouchListener(this);
		mTo.setOnTouchListener(this);
		mClearFrom.setOnClickListener(this);
		mClearTo.setOnClickListener(this);
		mQueryBtn.setOnClickListener(this);
		mListView.setOnItemClickListener(this);
		view.findViewById(R.id.swap).setOnClickListener(this);
		return view;
	}

	public void onLocationChanged(String city, double latitude,
			double longitude, long time) {
		mCity = city;
		mCurCity = city;
		mLatitude = latitude;
		mLongitude = longitude;

		Config.execute(new Runnable() {
			@Override
			public void run() {
				Message m = mHandler.obtainMessage(LOCATION_SUCCESS_ACTION);
				m.sendToTarget();
			}
		});
	}

	public void onLocationFailed() {
		mHandler.obtainMessage(LOCATION_FAILED_ACTION).sendToTarget();
	}

	/**
	 * 按起始点搜索
	 */
	private void doSearch() {
		MobclickAgentUtil.onEvent(getActivity(), UMengEventIds.DISCOVER_YELLOWPAGE_BUS_QUERY_PATH);
		StringBuffer reqUrl = new StringBuffer(
				BaiduUriApiUtil.HOST_DIRECTION_URL);
		reqUrl.append("?origin=");
		String currPosStr = getResources().getString(
				R.string.putao_bus_current_loction);
		String to = mTo.getText().toString();
		String from = mFrom.getText().toString();
		if (currPosStr.equals(to)) {
			reqUrl.append(mFrom.getText().toString());
			reqUrl.append("&destination=");
			reqUrl.append(mLatitude);
			reqUrl.append(",");
			reqUrl.append(mLongitude);
		} else if (currPosStr.equals(from) && mLatitude != 0) {
			reqUrl.append(mLatitude);
			reqUrl.append(",");
			reqUrl.append(mLongitude);
			reqUrl.append("&destination=");
			reqUrl.append(to);
		} else {
			reqUrl.append(mFrom.getText().toString());
			reqUrl.append("&destination=");
			reqUrl.append("&destination=");
			reqUrl.append(to);
		}
		reqUrl.append("&mode=transit");
		reqUrl.append("&region=");
		reqUrl.append(mCity);
		reqUrl.append("&output=html");
		reqUrl.append("&coord_type=gcj02");
		reqUrl.append("&src=putao|yellowpage");

		startWebRequest(reqUrl.toString());
	}

	private void startWebRequest(String reqUrl) {
		LogUtil.d(TAG, "startWebRequest=" + reqUrl);

		mYellowParams.setUrl(reqUrl);
		mYellowParams.setTitle(getResources()
				.getString(R.string.putao_bus_query_hint));

		Intent intent = new Intent(getActivity(),
				so.contacts.hub.ui.web.YellowPageBusActivity.class);
		intent.putExtra(YellowUtil.TargetIntentParams, mYellowParams);

		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.bus_line_query_btn) {
			if (TextUtils.isEmpty(mFrom.getText().toString())
					|| TextUtils.isEmpty(mTo.getText().toString())) {
				Toast.makeText(getActivity(),
						getActivity().getString(R.string.putao_bus_query_line_empty),
						Toast.LENGTH_SHORT).show();
				return;
			}
			doSearch();
		} else if (id == R.id.swap) {
			mFrom.removeTextChangedListener(mWatcherFrom);
			mTo.removeTextChangedListener(mWatcherTo);
			String from = mFrom.getText().toString();
			String to = mTo.getText().toString();
			mFrom.setText(to);
			mTo.setText(from);
			mFrom.addTextChangedListener(mWatcherFrom);
			mTo.addTextChangedListener(mWatcherTo);
		} else if (id == R.id.clear) {
			mFrom.setText("");
		} else if (id == R.id.clear_to) {
			mTo.setText("");
		} else {
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		EditText edit = getEdit();
		String key = mData.get(position).getContent();
		if (edit != null && !TextUtils.isEmpty(key)) {
			edit.removeTextChangedListener(getWatcher());
			edit.setText(key);
			mListView.setVisibility(View.GONE);
			edit.addTextChangedListener(getWatcher());
		}
	}

	private TextWatcher getWatcher() {
		switch (mCurWhich) {
		case WHICH_FROM:
			return mWatcherFrom;
		case WHICH_TO:
			return mWatcherTo;
		default:
			break;
		}
		return null;
	}

	private EditText getEdit() {
		switch (mCurWhich) {
		case WHICH_FROM:
			return mFrom;
		case WHICH_TO:
			return mTo;
		default:
			break;
		}
		return null;
	}

	private void showInputManager(EditText edit, boolean isNeedShow) {
		if (mInputManager == null) {
			mInputManager = (InputMethodManager) getActivity()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
		}
		if (isNeedShow) {
			mInputManager
					.showSoftInput(edit, InputMethodManager.SHOW_IMPLICIT);
		} else {
			if (mInputManager.isActive()) {
				mInputManager.hideSoftInputFromWindow(edit.getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(TextUtils.isEmpty(loc_ok_text)){
			return false;
		}
		EditText edit = null;
		if (v.getId() == R.id.from) {
			edit = mFrom;
		} else if (v.getId() == R.id.to) {
			edit = mTo;
		}
		if (null != edit && loc_ok_text.equals(edit.getText().toString())) {
			requestSelectAll(edit);
			return true;
		}
		return false;
	}
	
	private void requestSelectAll(EditText edit) {
		edit.requestFocus();
		edit.selectAll();
		showInputManager(edit, true);
	}

	@Override
	protected void doTextChanged(CharSequence s, int which) {
		//delete by putao_lhq
		/*
		if (TextUtils.isEmpty(s)) {
			if (which == WHICH_FROM) {
				mClearFrom.setVisibility(View.GONE);
			} else if (which == WHICH_TO) {
				mClearTo.setVisibility(View.GONE);
			}
			return;
		}
		if (which == WHICH_FROM) {
			mClearFrom.setVisibility(View.VISIBLE);
		} else if (which == WHICH_TO) {
			mClearTo.setVisibility(View.VISIBLE);
		}
		*/
	}

	@Override
	public Integer getAdId() {
	    return AdCode.ADCODE_BusChangeFragment;
	}
	
}
