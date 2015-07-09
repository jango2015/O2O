package so.contacts.hub.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import so.contacts.hub.active.ActiveUtils;
import so.contacts.hub.core.Config;
import so.contacts.hub.core.Config.CallBack;
import so.contacts.hub.db.YellowPageDB;
import so.contacts.hub.http.WebServiceUtils;
import so.contacts.hub.msgcenter.MsgCenterConfig;
import so.contacts.hub.ui.yellowpage.YellowPageExpressSelectReslutPage;
import so.contacts.hub.ui.yellowpage.bean.Express;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.mdroid.core.http.IgnitedHttpResponse;
import com.yulong.android.contacts.discover.R;

/**
 * 快递公司自动匹配 2014-09-11
 * 
 * @author lihq
 * 
 */
public class ExpressSmartMatchUtil implements CallBack {

	private static final String TAG = "ExpressSmartMatchUtil";
	
	public static final String SELECT_KEY = "993abcd3694c6a29e1cc909c2766c5de";
	public static final String PARTNER_NAME = "putao";
	private static final int MAX_THREAD = 8;
	private static final int MSG_MATCH_EXPRESS_SUCCESS = 0;
	private static final int MSG_QUERAY_FAILED = 1;
	private static final int MSG_EXPRESS_NOT_FOUND = 2;

	private static final int QUERY_TYPE_MATCH = 0;
	private static final int QUERY_TYPE_OFFEN = 1;
	private static final int QUERY_TYPE_ALL = 2;

	private int mCurQueryType = 0;
	private boolean success = false;

	private YellowPageExpressSelectReslutPage mTarget;
	private String URL = "http://www.kuaidihelp.com/index/superSearch?searchKey=";
	private List<Express> mAllExp = new ArrayList<Express>();
	private List<Express> mSpecialExp = new ArrayList<Express>();
	private List<Express> mNeedQueryExpress = new ArrayList<Express>();
	private String exp_num;

	private ExecutorService mWorkSation;
	private AtomicInteger finishCount;

	private static final ThreadFactory sThreadFactory = new ThreadFactory() {
		private final AtomicInteger mCount = new AtomicInteger(1);

		@Override
		public Thread newThread(Runnable r) {
			return new Thread(r, "QueryExp #" + mCount.getAndIncrement());
		}
	};

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_MATCH_EXPRESS_SUCCESS:
				LogUtil.d(TAG, "MSG_MATCH_EXPRESS_SUCCESS: arg1 = " + msg.arg1);
				if (success) {
					return;
				}
				doHandleSuccess(msg);
				break;
			case MSG_QUERAY_FAILED:
				if (finishCount.decrementAndGet() == 0) {
					doMatchFailed();
				}
				break;
			case MSG_EXPRESS_NOT_FOUND:
				if (mTarget != null) {
					LogUtil.d(TAG, "not found");
					mTarget.onPostExecuteH(null);
				}
				break;
			default:
				break;
			}
		};
	};

	public ExpressSmartMatchUtil(Activity activity, String number) {
		this.mTarget = (YellowPageExpressSelectReslutPage) activity;
		this.mWorkSation = Executors.newFixedThreadPool(MAX_THREAD,
				sThreadFactory);
		initData(activity);
	}

	protected void doMatchFailed() {
		if (mNeedQueryExpress == null) {
			mNeedQueryExpress = new ArrayList<Express>();
		}
		switch (mCurQueryType) {
		case QUERY_TYPE_MATCH:
			LogUtil.d(TAG, "QUERY_TYPE_MATCH");
			setNextQueryType(QUERY_TYPE_OFFEN);
			mNeedQueryExpress.clear();
			mNeedQueryExpress.addAll(mSpecialExp);
			execAsycQuery();
			break;
		case QUERY_TYPE_OFFEN:
			LogUtil.d(TAG, "QUERY_TYPE_OFFEN");
			setNextQueryType(QUERY_TYPE_ALL);
			mNeedQueryExpress.clear();
			mNeedQueryExpress.addAll(mAllExp);
			execAsycQuery();
			break;
		case QUERY_TYPE_ALL:
			LogUtil.d(TAG, "QUERY_TYPE_ALL");
			mHandler.sendEmptyMessage(MSG_EXPRESS_NOT_FOUND);
			break;
		default:
			break;
		}
	}

	// 加载快递公司信息
	private void initData(Activity activity) {
		YellowPageDB db = ContactsAppUtils.getInstance().getDatabaseHelper()
				.getYellowPageDBHelper();
		List<Express> expressList = db.getExpressList();
		if (expressList == null) {
			expressList = new ArrayList<Express>();
		}
		mAllExp.addAll(expressList);
		String[] specialExpresses = activity.getResources().getStringArray(
				R.array.putao_special_expresses);
		for (int i = 0; i < specialExpresses.length; i++) {
			String[] items = specialExpresses[i].split(",");
			if (items.length == 2) {
				Express exp = getExpressByKey(items[1]);
				if (exp != null) {
					mAllExp.remove(exp);
				}
				mSpecialExp.add(exp);
			}
		}
	}

	/**
	 * 快递直查
	 * 
	 * @param number
	 *            快递单号
	 */
	public void query(String number) {
		if (TextUtils.isEmpty(number)) {
			LogUtil.d(TAG, "number is null");
			return;
		}
		this.exp_num = number;
		this.success = false;
		Config.asynGet(URL + number, false, this);
	}

	private synchronized void setNextQueryType(int type) {
		this.mCurQueryType = type;
	}

	@Override
	public void onSuccess(String o) {
		convertResult(o);
		if (mNeedQueryExpress == null) {
			mNeedQueryExpress = new ArrayList<Express>();
		}
		if (mNeedQueryExpress.size() <= 0) {
			LogUtil.d(TAG, "no match, search from offen");
			setNextQueryType(QUERY_TYPE_OFFEN);
			mNeedQueryExpress.addAll(mSpecialExp);
		} else {
			setNextQueryType(QUERY_TYPE_MATCH);
		}
		execAsycQuery();
	}

	private void execAsycQuery() {
	    LogUtil.d(TAG, "execAsycQuery->" + mNeedQueryExpress.size());
		finishCount = new AtomicInteger(mNeedQueryExpress.size());
		if (mWorkSation == null) {
			LogUtil.d(TAG, "station is null");
			mWorkSation = Executors.newFixedThreadPool(MAX_THREAD,
					sThreadFactory);
		}
		for (int i = 0; i < mNeedQueryExpress.size(); i++) {
			final int index = i;
			mWorkSation.submit(new Runnable() {

				@Override
				public void run() {
					Express express = mNeedQueryExpress.get(index);
					String com_id = express.getPinyin();
					String tokenStr = com_id + "," + exp_num + ","
							+ PARTNER_NAME + "," + SELECT_KEY;

					String requestStr = WebServiceUtils.BuildReqStr(
							WebServiceUtils.getMd5Token(tokenStr), com_id,
							exp_num);
					String resultStr = WebServiceUtils.queryExpress(requestStr);

					try {
						JSONObject jsonObject;
						jsonObject = new JSONObject(resultStr);
						JSONObject jsonObjectResponse = jsonObject
								.getJSONObject("response");

						JSONArray jsonArrayBody = jsonObjectResponse
								.getJSONArray("body");
						if (jsonArrayBody != null) {
							Message msg = mHandler
									.obtainMessage(MSG_MATCH_EXPRESS_SUCCESS);
							msg.obj = resultStr;
							msg.arg1 = index;
							mHandler.sendMessage(msg);
							return;
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					mHandler.obtainMessage(MSG_QUERAY_FAILED);
					mHandler.sendEmptyMessage(MSG_QUERAY_FAILED);
				}
			});
		}
	}

	private void convertResult(String o) {
		if (TextUtils.isEmpty(o)) {
			return;
		}
		try {
			JSONObject result = new JSONObject(o);
			JSONObject response = result.getJSONObject("response")
					.getJSONObject("body").getJSONObject("data")
					.getJSONObject("brand");
			if (response == null) {
				return;
			}
			Iterator<String> keys = response.keys();
			mNeedQueryExpress.clear();
			while (keys.hasNext()) {
				String key = keys.next();
				Express express = getExpressByKey(key);
				if (express != null) {
					mNeedQueryExpress.add(express);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private Express getExpressByKey(String key) {
		for (int i = 0; i < mAllExp.size(); i++) {
			Express express = mAllExp.get(i);
			if (express.getPinyin().equals(key)) {
				return express;
			}
		}
		return null;
	}

	@Override
	public void onFail(String msg) {
		LogUtil.d(TAG, "onFail: " + msg);
		if (mNeedQueryExpress == null) {
			mNeedQueryExpress = new ArrayList<Express>();
		}
		mNeedQueryExpress.clear();
		setNextQueryType(QUERY_TYPE_OFFEN);
		mNeedQueryExpress.addAll(mSpecialExp);
		execAsycQuery();
	}

	@Override
	public void onFinish(Object obj) {
		LogUtil.d(TAG, "onFinish: " + obj);
	}

	public void onDestroy() {
		if (mWorkSation != null) {
			mWorkSation.shutdownNow();
		}
		// add by putao_lhq 2014年11月5日 start
		mHandler.removeCallbacksAndMessages(null);
		mHandler = null;
		// add by putao_lhq 2014年11月5日 end
		mTarget = null;
		mNeedQueryExpress.clear();
		mAllExp.clear();
		mSpecialExp.clear();
	}

	private synchronized void doHandleSuccess(android.os.Message msg) {
		if (msg.obj == null) {
			return;
		}
		if (mWorkSation != null) {
			LogUtil.d(TAG, "should shutdown");
			mWorkSation.shutdownNow();
		}
		String resultStr = (String) msg.obj;
		try {
			JSONObject jsonObject;
			jsonObject = new JSONObject(resultStr);
			JSONObject jsonObjectResponse = jsonObject
					.getJSONObject("response");

			JSONArray jsonArrayBody = jsonObjectResponse.getJSONArray("body");
			if (jsonArrayBody != null) {
				Express express = mNeedQueryExpress.get(msg.arg1);
				mTarget.setExpressTitle(express.getName());
				mTarget.setExpInfo(express.getPinyin(), express.getName());
				mTarget.onPostExecuteH(resultStr);
				success = true;
				final String exp_id = express.getPinyin();
				//add by ls start 添加用户快递单号上报;
				Config.execute(new Runnable() {
					@Override
					public void run() {
					    /*
					     * modify by putao_lhq @start
					     * 上报时同时上报快递公司对应code
					     * old code:
					     * String url = MsgCenterConfig.EXPRESS_REPORT+exp_num
					     */
						String url = MsgCenterConfig.EXPRESS_REPORT+exp_num + "&company_code=" + exp_id;
						//TODO 待完善, 要与后台确定上传阐述
						IgnitedHttpResponse httpResponse = null;
						try {
							httpResponse = Config.getApiHttp().get(url).send();
							String content = httpResponse.getResponseBodyAsString();
							LogUtil.i(TAG, "content=" + content+url);
						} catch (Exception e) {
							e.printStackTrace();
						} 
					}
				});
				//add by ls end;
				
				// 检查查询成功的彩蛋
				// add by cj 2015/01/23 start
				if(this.mTarget != null) {
				    ActiveUtils.findValidEggAndStartWebDialog(this.mTarget, 
				            so.contacts.hub.util.ExpressSmartMatchUtil.class.getName(), null);
				}
				// add by cj 2015/01/23 end
			}
		} catch (JSONException e) {
			LogUtil.d(TAG, "has some exception: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
