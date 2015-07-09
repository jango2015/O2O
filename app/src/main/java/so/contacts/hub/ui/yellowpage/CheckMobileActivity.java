package so.contacts.hub.ui.yellowpage;

import so.contacts.hub.account.IAccCallback;
import so.contacts.hub.account.IPutaoAccount;
import so.contacts.hub.account.PutaoAccount;
import so.contacts.hub.remind.BaseRemindActivity;
import so.contacts.hub.util.ContactsAppUtils;
import so.contacts.hub.util.ContactsHubUtils;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.UMengEventIds;
import so.contacts.hub.util.YellowPageDataUtils;
import so.contacts.hub.widget.CommonDialog;
import so.contacts.hub.widget.CommonDialogFactory;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.yulong.android.contacts.discover.R;

public class CheckMobileActivity extends BaseRemindActivity implements View.OnClickListener{
    private static final String TAG = "CheckMobileActivity";
    
    private static final int CHECK_NUMBER = 0x1;
    private static final int CHECK_MOBILE_REQUEST_CODE = 0x11;
    
    
    Button checkMobileBtn = null;
    EditText editText = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.putao_check_mobile);
        
        initView();
    }
    
    private void initView() {
        findViewById(R.id.back_layout).setOnClickListener(this);
        ((TextView) findViewById(R.id.title)).setText(R.string.putao_bind_mobile);
        
        checkMobileBtn = (Button)findViewById(R.id.check_mobile_btn);
        editText = (EditText)findViewById(R.id.phone_edit);
        
        checkMobileBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String mobile = editText.getText().toString();
                
                //判断输入号码是否正确
                if (TextUtils.isEmpty(mobile) || !TextUtils.isDigitsOnly(mobile)
                        || !ContactsHubUtils.isTelephoneNumber(mobile)) {
                    Toast.makeText(CheckMobileActivity.this,
                            R.string.putao_input_phone_number_error,
                            Toast.LENGTH_LONG).show();
                    return ;
                }
                
                //开始自助验证
                // add by putao_lhq 2014年10月14日 start
                /**
                 * 添加处理逻辑，读取本机号码与输入号码相同则不需要发送验证码，直接绑定手机！
                 */
                String localPhone = ContactsHubUtils.getPhoneNumber(getBaseContext());
                LogUtil.d(YellowPageDataUtils.TAG, "localPhone: " + localPhone+" equals checkMobile: "+mobile);
				if (mobile.equals(localPhone)) {
                	bindPhone();
                	return;
                }
				
                // add by putao_lhq 2014年10月14日 end
                sendManualCheckCode(mobile);
                showWaitingCheckingDialog();
            }
        });
    }

    /**
     * 生成验证码给自己发送短信，自助验证
     */
    private void sendManualCheckCode(String mobile) {
        String  manual_check_code = ((int) (Math.random() * 900) + 100) + "";
        LogUtil.d(TAG, "sendManualCheckCode code="+manual_check_code);
        ContactsAppUtils.getInstance().setCheck_code(null);
        ContactsAppUtils.getInstance().setManual_check_code(manual_check_code);
        ContactsAppUtils.getInstance().setCurrent_mobile(mobile);
        ContactsHubUtils.sendMessage(
                this,
                mobile,
                getResources().getString(R.string.putao_verification_code)
                        + manual_check_code
                        + getResources().getString(R.string.putao_verification_code_from,
                                getResources().getString(R.string.putao_app_name)));
    }

    ProgressBar progressBar;
    CommonDialog mDialog;

    protected void showWaitingCheckingDialog() {
        View v = getLayoutInflater().inflate(R.layout.putao_wait_check_mobile_dialog, null);
        progressBar = (ProgressBar) v.findViewById(R.id.progress);
        progressBar.setMax(30);
        progressBar.setProgress(1);

        mDialog = CommonDialogFactory.getOkCancelCommonDialog(this);
        mDialog.setTitle(R.string.putao_checking_mobile);
        FrameLayout fl = mDialog.getExpandFrameLayout();
        fl.setVisibility(View.VISIBLE);
        fl.addView(v);
        mDialog.getMessageTextView().setVisibility(View.GONE);
        mDialog.hideBottom();
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        mDialog.show();
        Message message = new Message();
        message.what = CHECK_NUMBER;
        Bundle data = message.getData();
        if (data == null) {
            data = new Bundle();
            message.setData(data);
        }
        data.putInt("progress", 1);
        mhandler.sendMessageDelayed(message, 1000);
    }
    
    /**
     * 处理自助验证进度框
     * @param msg
     * @param code
     * @param handlerCallBack
     */
    private void handleCheckCodeProgress(Message msg, String code, int what) {
//        code = "test";
        if (!TextUtils.isEmpty(code)) {
            if (mDialog != null && mDialog.isShowing() && mDialog.getWindow() != null){
                mDialog.dismiss();
            }
            if(code.equals(ContactsAppUtils.getInstance().getManual_check_code())){//验证成功
//                Toast.makeText(this, "验证成功", Toast.LENGTH_LONG).show();
                //绑定号码
                bindPhone();
            } else {
            	LogUtil.d(TAG, "auto check fail,enter into manual check.");
                enterManualCheck();//add by putao_lhq 短信读取失败或解析错误能够进入手动验证
            }
        } else {
            int progress = msg.getData().getInt("progress", what);
            if (progress == 30) {
                if (mDialog != null && mDialog.isShowing() && mDialog.getWindow() != null){
                	mDialog.dismiss();
                }
                // 自动验证失败 ，进入手动验证
                LogUtil.d(TAG, "check fail,enter into manual check.");
                enterManualCheck();
            } else {
                progressBar.setProgress(progress);
                Message message = new Message();
                message.what = what;
                message.getData().putInt("progress", ++progress);
                mhandler.sendMessageDelayed(message, 1000);
            }
        }
    }

    /**
     * 进入手动验证
     * add by putao_lhq 
     * 
     */
	private void enterManualCheck() {
		String mobile = editText.getText().toString();
		Intent intent = new Intent(this, CheckMobileManualActivity.class);
		intent.putExtra("mobile", mobile);
		startActivityForResult(intent,CHECK_MOBILE_REQUEST_CODE);
	}

    /**
     * 绑定手机号码
     * add by putao_lhq
     */
	private void bindPhone() {
		String mobile = editText.getText().toString();
		String progressMsg = getResources().getString(R.string.putao_bind_process_msg);
		
		LogUtil.d(YellowPageDataUtils.TAG, "bindPhone mobile="+mobile);
		MobclickAgentUtil.onEvent(this, UMengEventIds.DISCOVER_YELLOWPAGE_MY_BIND_MOBILE);//add by putao_lhq
		/*YellowPageDataUtils.loginYellowAsyn(this, progressMsg, mobile, 1, 1, new CallBack() {
		    @Override
		    public void onSuccess(String o) {
		        //免密登录成功
		        LogUtil.d(YellowPageDataUtils.TAG, "bindPhone ok");
		        finish();
		    }
		    
		    @Override
		    public void onFinish(Object obj) {
		        
		    }
		    
		    @Override
		    public void onFail(String msg) {
		        LogUtil.d(YellowPageDataUtils.TAG, "bindPhone failed");
		        Toast.makeText(CheckMobileActivity.this, msg, Toast.LENGTH_LONG).show();
		    }
		}, null);*/
		
		PutaoAccount.getInstance().login(this, mobile, IPutaoAccount.LOGIN_TYPE_BY_PHONE, new IAccCallback() {
			
			@Override
			public void onSuccess() {
				//免密登录成功
		        LogUtil.d(PutaoAccount.TAG, "bindPhone ok");
		        finish();
				
			}
			
			@Override
			public void onFail(int msg) {
				LogUtil.d(PutaoAccount.TAG, "bindPhone failed");
		        Toast.makeText(CheckMobileActivity.this, 
		        		PutaoAccount.getInstance().getToastTextError(CheckMobileActivity.this, msg), Toast.LENGTH_LONG).show();
				
			}

            @Override
            public void onCancel() {
                // TODO Auto-generated method stub
                
            }
		});
	}
    
    @SuppressLint("HandlerLeak") 
    private Handler mhandler = new  Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CHECK_NUMBER:
                    String code = ContactsAppUtils.getInstance().getCheck_code();
                    handleCheckCodeProgress(msg, code, msg.what);
                    break;
                default:
                    break;
            }
        }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CHECK_MOBILE_REQUEST_CODE:
                if(resultCode == RESULT_OK){
                    finish();
                }
                break;
            default:
                break;
        }
    };
    
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgentUtil.onResume(this);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgentUtil.onPause(this);
    }
    
    // add by putao_lhq 2014年10月30日 for BUG #1833 start
    @Override
    protected void onDestroy() {
    	if (mDialog != null) {
    		mDialog.dismiss();
    		mDialog = null;
    	}
    	mhandler.removeCallbacksAndMessages(null);
    	super.onDestroy();
    }
    // add by putao_lhq 2014年10月30日 for BUG #1833 end

	@Override
	public String getServiceNameByUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServiceName() {
		// TODO Auto-generated method stub
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
		return null;
	}
}
