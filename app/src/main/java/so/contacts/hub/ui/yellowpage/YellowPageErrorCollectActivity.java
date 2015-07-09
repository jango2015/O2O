package so.contacts.hub.ui.yellowpage;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import so.contacts.hub.core.Config;
import so.contacts.hub.core.Config.CallBack;
import so.contacts.hub.core.ConstantsParameter;
import so.contacts.hub.remind.BaseRemindActivity;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.NetUtil;
import so.contacts.hub.util.UMengEventIds;
import so.contacts.hub.util.Utils;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.yulong.android.contacts.discover.R;

public class YellowPageErrorCollectActivity extends BaseRemindActivity implements OnClickListener {

	private static final String TAG = "YellowPageErrorCollectActivity";
	
	private RadioGroup mNumberInfoGroup = null;
	
	private EditText mOthersInfoEText = null;

	private EditText mContactsInfoEText = null;
	
	private Button mCommitBtn = null;
	
	private String mOthersInfo = null;
	
	private String mContactsInfo = null;
	
	private boolean mPrivateTelNumTemp = true;

	private boolean mPrivateTelNum = mPrivateTelNumTemp;
	
	private String mName = "";
	
	private int mSourceId = 0;
	
	private long mItemId = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.putao_yellow_page_error_collect);
//		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		
		parseIntent();
		initView();
	}
	
	private void parseIntent(){
		Intent intent = getIntent();
		mName = intent.getStringExtra("name");
		mSourceId = intent.getIntExtra("source_id", -1);
		mItemId = intent.getLongExtra("item_id", -1);
	}

	private void initView() {
		((TextView) findViewById(R.id.title)).setText(getResources().getString(R.string.putao_yellow_page_error_collect));
		findViewById(R.id.back_layout).setOnClickListener(this);
		mCommitBtn = (Button) findViewById(R.id.commit_btn);
		mCommitBtn.setOnClickListener(this);

		mOthersInfoEText = (EditText) findViewById(R.id.others_info);
		mContactsInfoEText = (EditText) findViewById(R.id.contacts_info);
		mContactsInfoEText.setInputType(InputType.TYPE_CLASS_NUMBER);
		
		mNumberInfoGroup = (RadioGroup) findViewById(R.id.number_choosegroup);
		mNumberInfoGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
						if (checkedId == R.id.number_choose1) {
							mPrivateTelNumTemp = true;
						} else if (checkedId == R.id.number_choose2) {
							mPrivateTelNumTemp = false;
						} else {
						}
					}
				});
	}
	
	private void doCommitInfo(){
		if (!NetUtil.isNetworkAvailable(this)) {
			Utils.showToast(this, R.string.putao_no_net, false);
			return;
		} 
		if( mSourceId == -1 ){
			// 数据来源不明...
			Utils.showToast(this, R.string.putao_yellow_page_error_collect_commit_hint, false);
			return;
		}
		String othersInfo = mOthersInfoEText.getText().toString();
		String contactsInfo = mContactsInfoEText.getText().toString();
		if( TextUtils.isEmpty(othersInfo) || TextUtils.isEmpty(contactsInfo) ){
			Utils.showToast(this, R.string.putao_yellow_page_error_collect_commit_hint, false);
			return;
		}
		String othersInfoTemp = othersInfo.trim();
		String contactsInfoTemp = contactsInfo.trim();
		if("".equals(othersInfoTemp) || "".equals(contactsInfoTemp)){
			Utils.showToast(this, R.string.putao_yellow_page_error_collect_commit_hint, false);
			return;
		}
		if( mPrivateTelNumTemp == mPrivateTelNum && othersInfoTemp.equals(mOthersInfo) 
				&& contactsInfoTemp.equals(mContactsInfo) ){
			Utils.showToast(this, R.string.putao_yellow_page_error_collect_commit_repeat, false);
			return;
		}
		
		makeCommitInfo(mPrivateTelNumTemp, othersInfoTemp, contactsInfoTemp);
	}
	
	private void makeCommitInfo(final boolean privateTelNum, final String oThersInfo, final String contactsInfo){
		// source_id=1&item_id=10&name=sha&type=1&content=122234&contact_info=18682191623
		/*
		    source_id=[1:葡萄,2:大众点评,3:搜狗]
			item_id=[资源ID]
			name=[名字]
			type=[1:号码纠错，2:私人号码]
			content=[描述]
			contact_info=[联系方式]
		 */
		//此处对是内容中包含中文字符的均做utf-8编码
		String othersInfoTemp = oThersInfo;
		String nameTemp = mName;
		try {
			nameTemp = URLEncoder.encode(mName, "UTF-8");
			othersInfoTemp = URLEncoder.encode(oThersInfo, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			LogUtil.i(TAG, "makeCommitInfo encode name or othersInfoTemp exception...");
		}
		int priType = privateTelNum ? 2 : 1;
		StringBuffer strBuffer = new StringBuffer(ConstantsParameter.YELLOWPAGE_ERROR_RECOVERY_URL);
		strBuffer.append("source_id=" + mSourceId);
		strBuffer.append("&item_id=" + mItemId);
		strBuffer.append("&name=" + nameTemp);
		strBuffer.append("&type=" + priType);
		strBuffer.append("&content=" + othersInfoTemp);
		strBuffer.append("&contact_info=" + contactsInfo);

		Utils.showToast(YellowPageErrorCollectActivity.this, 
				R.string.putao_yellow_page_error_collect_committing, false);
		
		String requestUrl = strBuffer.toString();
		if( TextUtils.isEmpty(requestUrl) ){
			return;
		}
		LogUtil.i(TAG, "requestUrl: " + requestUrl);
		Config.asynGet(requestUrl, false, new CallBack() {
			
			@Override
			public void onSuccess(String msg) {
				// TODO Auto-generated method stub
				if( "OK".equals(msg) ){
					mHandler.sendEmptyMessage(MSG_REQUEST_SUCCESS_ACTION);
					MobclickAgentUtil.onEvent(YellowPageErrorCollectActivity.this,
							UMengEventIds.DISCOVER_YELLOWPAGE_AGGREGATED_ERROR_COLLECT_COMMIT_SUCCESS);
				}else if( "Fail".equals(msg) ){
					mHandler.sendEmptyMessage(MSG_REQUEST_FAIL_ACTION);
					MobclickAgentUtil.onEvent(YellowPageErrorCollectActivity.this,
							UMengEventIds.DISCOVER_YELLOWPAGE_AGGREGATED_ERROR_COLLECT_COMMIT_FAIL);
				}
			}
			
			@Override
			public void onFinish(Object obj) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onFail(String msg) {
				// TODO Auto-generated method stub
				mHandler.sendEmptyMessage(MSG_REQUEST_FAIL_ACTION);
				
			}
		});

		mPrivateTelNum = privateTelNum;
		mOthersInfo = oThersInfo;
		mContactsInfo = contactsInfo;
	}
	
	private static final int MSG_REQUEST_SUCCESS_ACTION = 0x2001;
	
	private static final int MSG_REQUEST_FAIL_ACTION = 0x2002;
	
	private Handler mHandler = new Handler(){
		
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			switch(what){
			case MSG_REQUEST_SUCCESS_ACTION:
				Utils.showToast(YellowPageErrorCollectActivity.this, 
						R.string.putao_yellow_page_error_collect_commit_success, false);
				break;
			case MSG_REQUEST_FAIL_ACTION:
				Utils.showToast(YellowPageErrorCollectActivity.this, 
						R.string.putao_yellow_page_error_collect_commit_failed, false);
				break;
			default:
				break;
			}
		};
	};

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.back_layout) {
			finish();
		} else if (id == R.id.commit_btn) {
			doCommitInfo();
		} else {
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
