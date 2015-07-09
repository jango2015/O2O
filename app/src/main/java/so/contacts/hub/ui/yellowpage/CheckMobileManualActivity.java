package so.contacts.hub.ui.yellowpage;

import so.contacts.hub.account.IAccCallback;
import so.contacts.hub.account.IPutaoAccount;
import so.contacts.hub.account.PutaoAccount;
import so.contacts.hub.remind.BaseRemindActivity;
import so.contacts.hub.util.ContactsAppUtils;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.MobclickAgentUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yulong.android.contacts.discover.R;

public class CheckMobileManualActivity extends BaseRemindActivity implements View.OnClickListener{

    private Button checkMobileBtn = null;

    private EditText editText = null;
    private TextView checkCodeAlertText = null;
    private String mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.putao_manaul_check_mobile);
        
        mobile = getIntent().getStringExtra("mobile");
        initView();
        
    }

    private void initView() {
        findViewById(R.id.back_layout).setOnClickListener(this);
        ((TextView) findViewById(R.id.title)).setText(R.string.putao_bind_mobile);
        
        checkMobileBtn = (Button)findViewById(R.id.check_mobile_btn);
        editText = (EditText)findViewById(R.id.code_edit);
        checkCodeAlertText = (TextView)findViewById(R.id.code_alert_text);
        
        
        String check_code_alert = getResources().getString(R.string.putao_check_code_alert, "+86 "+mobile);
        
        checkCodeAlertText.setText(check_code_alert);
        
        checkMobileBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String code = editText.getText().toString();
                if(!TextUtils.isEmpty(code)){
                    checkCode(code);
                }
            }
        });
    }

    protected void checkCode(String code) {
        if (code.equals(ContactsAppUtils.getInstance().getManual_check_code())) {
            // 绑定号码
//            Toast.makeText(this, "验证成功", Toast.LENGTH_LONG).show();
            String progressMsg = getResources().getString(R.string.putao_bind_process_msg);
            /*YellowPageDataUtils.loginYellowAsyn(this, progressMsg, mobile, 1, 1,
                    new CallBack() {
                        @Override
                        public void onSuccess(String o) {
                            //免密登录成功
                            setResult(RESULT_OK);
                            finish();
                        }

                        @Override
                        public void onFinish(Object obj) {

                        }

                        @Override
                        public void onFail(String msg) {
                        	Message message = mHandler.obtainMessage();
                        	message.what = MSG_SHOW_LOGIN_FAIL_ACTION;
                        	message.obj = msg;
                        	mHandler.sendMessageDelayed(message, 300);
                        }
                    }, null);*/
            PutaoAccount.getInstance().login(this, mobile, IPutaoAccount.LOGIN_TYPE_BY_PHONE, new IAccCallback() {
				
				@Override
				public void onSuccess() {
					setResult(RESULT_OK);
                    finish();
				}
				
				@Override
				public void onFail(int msg) {
				    LogUtil.e(PutaoAccount.TAG, "onFail msg="+msg);
				    /**
				     * 该错误不能抛到主线程中执行,因为是在独立线程回调,并且回调完成后会重置错误码
				     * modify by cj 2015-01-04
				     */
	                Toast.makeText(CheckMobileManualActivity.this, 
	                        PutaoAccount.getInstance().getToastTextError(CheckMobileManualActivity.this, msg), Toast.LENGTH_LONG).show();
				}

                @Override
                public void onCancel() {
                    LogUtil.e(PutaoAccount.TAG, "onCancel");
                }
			});
        }else{
        	mHandler.sendEmptyMessageDelayed(MSG_SHOW_AUTH_ERROR_ACTION, 300);
        }
    }
    
    private static final int MSG_SHOW_AUTH_ERROR_ACTION = 0x2002;
    
    private Handler mHandler = new Handler(){
    	@Override
        public void handleMessage(Message msg) {
    		switch(msg.what){
    		case MSG_SHOW_AUTH_ERROR_ACTION:
    			mHandler.removeMessages(MSG_SHOW_AUTH_ERROR_ACTION);
            	Toast.makeText(CheckMobileManualActivity.this, R.string.putao_checking_authcode_error, Toast.LENGTH_LONG).show();
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
