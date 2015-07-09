package so.contacts.hub.ui.yellowpage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;
import com.mdroid.core.http.IgnitedHttpResponse;
import com.mdroid.core.util.SystemUtil;
import com.yulong.android.contacts.discover.R;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import so.contacts.hub.account.IAccCallback;
import so.contacts.hub.account.PutaoAccount;
import so.contacts.hub.core.Config;
import so.contacts.hub.core.PTUser;
import so.contacts.hub.train.TongChengConfig;
import so.contacts.hub.ui.BaseActivity;
import so.contacts.hub.ui.yellowpage.bean.UserFeedbackBean;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.NetUtil;

/** 用户反馈信息 */
public class YellowPageUserFeedbackActivity extends BaseActivity implements
		OnClickListener, IAccCallback {
	private TextView title;
	private EditText feedback_content;
	private EditText et_phone;
	private static final int REPORT_SUCCESS = 1;
	private static final int REPORT_FAILED = -1;
	private static final int REPORT_FAILED_NETEXCEPTION = -2;

	UserFeedbackBean user = null;
	private static final String TAG = YellowPageUserFeedbackActivity.class
			.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.putao_user_feedback_layout);
		initViews();
		initData();
	}

	private void initData() {
		title.setText(mTitleContent);

	}

	/** 获取用户反馈的bean */
	private UserFeedbackBean getUserFeedBack() {
		PTUser ptUser = PutaoAccount.getInstance().getPtUser();
		if (ptUser == null) {
			return null;
		}
		String pt_token = ptUser.getPt_token();
		String dev_no = SystemUtil.getDeviceId(this);
		String channel = SystemUtil.getChannelNo(this);
		int net_status = NetUtil.getNetStatus(this);
		String band = android.os.Build.MANUFACTURER + "#"
				+ android.os.Build.MODEL;
		String version = Integer.toString(SystemUtil.getAppVersionCode(this));
		String phone_no = et_phone.getText().toString().trim();
		String content = feedback_content.getText().toString().trim();
		UserFeedbackBean bean = new UserFeedbackBean();
		bean.setPt_token(pt_token);
		bean.setDev_no(dev_no);
		bean.setChannel_no(channel);
		bean.setVersion(version);
		bean.setBand(band);
		bean.setNet_status(net_status);
		bean.setPhone_no(phone_no);
		bean.setContent(content);
		return bean;
	}

	private void initViews() {
		title = (TextView) findViewById(R.id.title);
		feedback_content = (EditText) findViewById(R.id.feedback_content);
		et_phone = (EditText) findViewById(R.id.et_phone);
		findViewById(R.id.bt_commit).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.bt_commit) {
			commitFeedback();
		}
	}

	private void commitFeedback() {
		String content = feedback_content.getText().toString().trim();
		String phone = et_phone.getText().toString().trim();
		if (TextUtils.isEmpty(content)) {
			Toast.makeText(this, R.string.putao_yellow_page_error_collect_feedback_empty, Toast.LENGTH_SHORT).show();
			return;
		}
		if (TextUtils.isEmpty(phone)) {
			Toast.makeText(this, R.string.putao_yellow_page_error_collect_phone_notnull, Toast.LENGTH_SHORT).show();
			return;
		}
		if (!NetUtil.isNetworkAvailable(this)) {
			Toast.makeText(
					this,
					getResources().getString(
							R.string.putao_no_net), 0).show();
			return;
		}
		user = getUserFeedBack();
		if (user == null) {
			PutaoAccount.getInstance().silentLogin(this);
		} else {
			commit(user);
		}

	}

	/** 在系统登录之后,提交数据 */
	private void commit(final UserFeedbackBean user) {
		Config.execute(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				LogUtil.i(TAG, "user=" + user.toString());
				String url =TongChengConfig.YELLOW_PAGE_FEEDBACK_URL;
				IgnitedHttpResponse httpResponse = null;
				try {
					httpResponse = Config.getApiHttp().post(url, getData(user))
							.send();
					String content = httpResponse.getResponseBodyAsString();
					LogUtil.i(TAG, "content=" + content);
					if (content != null) {
						JSONObject obj = new JSONObject(content);
						String ret_code = obj.getString("ret_code");
						if ("0000".equals(ret_code)) {
							handler.sendEmptyMessageDelayed(REPORT_SUCCESS, 300);
						} else  {
							handler.sendEmptyMessage(REPORT_FAILED);
						}
					}
				} catch (ConnectException e) {
					handler.sendEmptyMessage(REPORT_FAILED_NETEXCEPTION);
					e.printStackTrace();
				} catch (IOException e) {
					handler.sendEmptyMessage(REPORT_FAILED);
					e.printStackTrace();
				} catch (JSONException e) {
					handler.sendEmptyMessage(REPORT_FAILED);
					e.printStackTrace();
				}
			}

		});

	}

	@Override
	public void onSuccess() {
		user = getUserFeedBack();
		commit(user);
	}

	@Override
	public void onFail(int failed_code) {
		Toast.makeText(this,
				getResources().getString(R.string.putao_text_status_unlogin), 0)
				.show();
		return;

	}

	public HttpEntity getData(UserFeedbackBean user) {
		String cotent = Config.mGson.toJson(user);
		LogUtil.i(TAG, "HttpEntity=" + cotent);
		StringEntity entity = null;
		try {
			entity = new StringEntity(cotent, HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return entity;
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REPORT_SUCCESS:
				Toast.makeText(
						YellowPageUserFeedbackActivity.this,
						YellowPageUserFeedbackActivity.this
								.getResources()
								.getString(
										R.string.putao_yellow_page_error_collect_commit_success),
						0).show();
				finish();
				break;
			case REPORT_FAILED:
				Toast.makeText(
						YellowPageUserFeedbackActivity.this,
						YellowPageUserFeedbackActivity.this.getResources()
								.getString(R.string.putao_server_busy), 0)
						.show();
				finish();
				break;
			case REPORT_FAILED_NETEXCEPTION:
				Toast.makeText(
						YellowPageUserFeedbackActivity.this,
						YellowPageUserFeedbackActivity.this.getResources()
								.getString(R.string.putao_no_net), 0).show();
				finish();
				break;
			default:
				break;
			}
		};
	};

    @Override
    public void onCancel() {
        // TODO Auto-generated method stub
        
    }

}
