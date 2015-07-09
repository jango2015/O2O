package so.contacts.hub.ui.yellowpage;

import java.util.ArrayList;
import java.util.List;

import so.contacts.hub.account.IAccCallback;
import so.contacts.hub.account.IPutaoAccount;
import so.contacts.hub.account.PutaoAccount;
import so.contacts.hub.core.PTUser;
import so.contacts.hub.http.bean.RelateUserResponse;
import so.contacts.hub.remind.BaseRemindActivity;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.UMengEventIds;
import so.contacts.hub.util.YellowPageDataUtils;
import so.contacts.hub.widget.CommonDialog;
import so.contacts.hub.widget.CommonDialogFactory;
import so.contacts.hub.widget.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.yulong.android.contacts.discover.R;

/**
 * 账户登录界面
 * @author putao_lhq
 * @version 2014年10月8日
 */
public class YellowPageLoginActivity extends BaseRemindActivity implements OnClickListener {

	private static final String TAG = "YellowPageDataUtils";

	private TextView statusKuyun;
	private TextView statusPhone;
	private TextView tipView;
	
	private RelateUserResponse phoneRelateUser = null; // 手机鉴权账户信息
	private RelateUserResponse kuyunRelateUser = null; // 酷云鉴权账户信息
	
	private ProgressDialog mProgressDialog = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.putao_yellow_page_login_activity);
		initView();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		LogUtil.i(TAG, "onResume...");
		MobclickAgentUtil.onResume(this);
//		if (CoolCloudManager.getInstance().isLoginRunning()) {
//			LogUtil.i(PutaoAccount.TAG, "login is running");
//			return;
//		}
		silentLogin();
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgentUtil.onPause(this);
	}
	
	/**
	 *  静默登录处理
	 */
	private void silentLogin() {
		boolean login = PutaoAccount.getInstance().isLogin();
		if (!login) {
			/*YellowPageDataUtils.silentLogin(this, new CallBack() {

				@Override
				public void onSuccess(String o) {
					refreshAccountInfo();
				}

				@Override
				public void onFinish(Object obj) {
				}

				@Override
				public void onFail(String msg) {
					refreshAccountInfo();
				}
			});*/
			PutaoAccount.getInstance().silentLogin(new IAccCallback() {
				
				@Override
				public void onSuccess() {
					refreshAccountInfo();
					
				}
				
				@Override
				public void onFail(int msg) {
					refreshAccountInfo();
					
				}

                @Override
                public void onCancel() {
                    // TODO Auto-generated method stub
                    
                }
			});
		} else {
			refreshAccountInfo();
		}
	}
	
	private void initView() {
		((TextView)findViewById(R.id.title)).setText(R.string.putao_account_login);
		statusKuyun = (TextView) findViewById(R.id.login_cool_cloud_status);
		statusPhone = (TextView) findViewById(R.id.login_phone_status);
		tipView = (TextView) findViewById(R.id.tip_account);
		
		findViewById(R.id.back_layout).setOnClickListener(this);
		
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        
        
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.setMessage(getString(R.string.putao_yellow_page_loading));
        }
	}

	/**
	 * 刷新账户信息
	 */
	private void refreshAccountInfo() {
		loadRelateUser();
		showAccountInfo();
	}

	/**
	 * 获取鉴权账户信息
	 */
	private void loadRelateUser() {
		phoneRelateUser = null;
	    kuyunRelateUser = null;
		PTUser ptUser = PutaoAccount.getInstance().getPtUser();
		if (ptUser != null) {
			List<RelateUserResponse> relateUsers = ptUser.getRelateUsers();
			if (relateUsers != null) {
				kuyunRelateUser = PutaoAccount.getInstance().getRelateUserResponse(RelateUserResponse.TYPE_FACTORY);
				phoneRelateUser = PutaoAccount.getInstance().getRelateUserResponse(RelateUserResponse.TYPE_PHONE);
				
				if(kuyunRelateUser != null) 
				    LogUtil.d(YellowPageDataUtils.TAG, "loadRelateUser kuyun==> "+kuyunRelateUser.toString());
				if(phoneRelateUser != null) 
                    LogUtil.d(YellowPageDataUtils.TAG, "loadRelateUser putao==> "+phoneRelateUser.toString());
			} else {
			    LogUtil.i(YellowPageDataUtils.TAG, "loadRelateUser is null");
			}
			// add by putao_lhq 2014年10月13日 for BUG #1600 start
			/*if(phoneRelateUser == null && kuyunRelateUser == null) {
			    LogUtil.i(YellowPageDataUtils.TAG, "Putao & CoolCloud user is empty, logout user");
				YellowPageDataUtils.loginOutPtUser(YellowPageLoginActivity.this);
				// 退出酷云授权
				CoolCloudManager.getInstance().logout(null);
			}*/
			// add by putao_lhq 2014年10月13日 for BUG #1600 end
		} 
	}
	
	private void showAccountInfo() {
		boolean showLogout = false;
		if (kuyunRelateUser != null) {
			String displayName = PutaoAccount.getInstance().getDisplayName(kuyunRelateUser);
			if (TextUtils.isEmpty(displayName)) {
				displayName = kuyunRelateUser.accName;
			}
			statusKuyun.setText(displayName);
			showLogout = true;
		} else {
			statusKuyun.setText(getString(R.string.putao_text_status_unlogin));
		}
		if (phoneRelateUser != null) {
			statusPhone.setText(phoneRelateUser.accName);
			showLogout = true;
		} else {
			statusPhone.setText(getString(R.string.putao_text_status_unlogin));
		}
		showLogoutBtn(showLogout);
		updateTip(showLogout);
	}

	
	public void doClick(View view) {
		int id = view.getId();
		if (id == R.id.login_out_btn) {
			logOut();
		} else if (id == R.id.login_phone) {
			if (!PutaoAccount.getInstance().isLogin() || phoneRelateUser == null) {
				bindPhone();// 跳转到绑定手机界面
			} else {
				//adapter to remove coolyun by sml 20150108 
				logOut();
				//showOptionDialog(phoneRelateUser);
			}
		} else if (id == R.id.login_cool_cloud) {
			if (kuyunRelateUser != null) {
				showOptionDialog(kuyunRelateUser);
			} else {
				showLoginKuyunDialog();
			}
		} else {
		}
	}

	private void showOptionDialog(final RelateUserResponse userResponse) {
		final CommonDialog commonDialog = CommonDialogFactory
				.getListCommonDialog(YellowPageLoginActivity.this);
		commonDialog.setTitle(getString(R.string.putao_to) + userResponse.accName);
		ArrayList<String> data = new ArrayList<String>();
		if (userResponse.accSource == RelateUserResponse.SOURCE_PHONE) {
			data.add(getString(R.string.putao_disable_phone_number));
		} else {
			String displayName = PutaoAccount.getInstance().getDisplayName(kuyunRelateUser);
			if (TextUtils.isEmpty(displayName)) {
				displayName = kuyunRelateUser.accName;
			}
			commonDialog.setTitle(getString(R.string.putao_to) + displayName);
			data.add(getString(R.string.putao_disable_kuyun));
		}
		//data.add(getString(R.string.putao_logout));//delete by putao_lhq for BUG #1598 
		commonDialog.setListViewDatas(data);
		commonDialog
				.setListViewItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0,
							View arg1, int arg2, long arg3) {
						commonDialog.dismiss();
						switch (arg2) {
						case 0:// 解绑
							// add by putao_lhq 2014年10月13日 for BUG #1517 start
							if (phoneRelateUser == null || kuyunRelateUser == null) {
								Toast.makeText(YellowPageLoginActivity.this, getString(R.string.putao_text_need_one_toast), 
										Toast.LENGTH_SHORT).show();
								return;
							}
							// add by putao_lhq 2014年10月13日 for BUG #1517 end
							unBindYellowAccount(userResponse);
							break;
						case 1:// 退出登录
							logOut();
							break;
						default:
							break;
						}
					}
				});
		commonDialog.show();
	}

	private void bindPhone() {
		Intent intent = new Intent(YellowPageLoginActivity.this,
				CheckMobileActivity.class);
		startActivity(intent);
	}

	private void login() {
	    LogUtil.d(YellowPageDataUtils.TAG, "CoolCloud login");
		/*CoolCloudManager.getInstance().login(new CallBack() {
			
			@Override
			public void onSuccess(String o) {
			    LogUtil.d(YellowPageDataUtils.TAG, "CoolCloud login ok");
			    mProgressDialog.dismiss();
				refreshAccountInfo();
			}
			
			@Override
			public void onFinish(Object obj) {
			    mProgressDialog.dismiss();
                LogUtil.d(YellowPageDataUtils.TAG, "CoolCloud login finish");
			}
			
			@Override
			public void onFail(String msg) {
			    mProgressDialog.dismiss();
				final String message = msg;
				LogUtil.d(YellowPageDataUtils.TAG, "CoolCloud login failed "+msg);
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(YellowPageLoginActivity.this, message, Toast.LENGTH_SHORT).show();
						// add by putao_lhq 2014年10月14日 for 登录失败后退出授权
						if (CoolCloudManager.getInstance().isLogin()) {
						    LogUtil.d(YellowPageDataUtils.TAG, "CoolCloud logout");
							CoolCloudManager.getInstance().logout(null);
						}
					}
				});
			}
		},this);*/
	    PutaoAccount.getInstance().login(this, null, IPutaoAccount.LOGIN_TYPE_BY_THIRD, new IAccCallback() {
			
			@Override
			public void onSuccess() {
				LogUtil.d(PutaoAccount.TAG, "third account login ok");
			    mProgressDialog.dismiss();
				refreshAccountInfo();
				
			}
			
			@Override
			public void onFail(int msg) {
				mProgressDialog.dismiss();
				LogUtil.d(PutaoAccount.TAG, "third account login failed " + msg);
				String message = PutaoAccount.getInstance().getToastTextError(YellowPageLoginActivity.this, msg);
				if (TextUtils.isEmpty(message)) {
					return;
				}
				Toast.makeText(YellowPageLoginActivity.this, message,
								Toast.LENGTH_SHORT).show();
			}

            @Override
            public void onCancel() {
                LogUtil.d(PutaoAccount.TAG, "third account canceled");
                mProgressDialog.dismiss();
            }
		});
	}

	private void showLogoutBtn(boolean show) {
		if (show) {
			findViewById(R.id.login_out_btn).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.login_out_btn).setVisibility(View.GONE);
		}
	}

	/**
	 * 退出登录 add by hyl 2014-9-22
	 */
	protected void logOut() {
		MobclickAgentUtil.onEvent(this, UMengEventIds.DISCOVER_YELLOWPAGE_EXIT_LOGIN);
		final CommonDialog commonDialog = CommonDialogFactory
				.getOkCancelCommonDialog(this);
		commonDialog.setTitle(R.string.putao_logout);
		commonDialog.getMessageTextView().setText(R.string.putao_msg_logout_dialog);
		commonDialog.setOkButtonClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
			    LogUtil.d(YellowPageDataUtils.TAG, "User confirm logout");
				commonDialog.dismiss();
                android.app.ProgressDialog exit_dialog = new android.app.ProgressDialog(YellowPageLoginActivity.this); 
                exit_dialog.setMessage(getString(R.string.putao_exit_clean_tip));
//                exit_dialog.setCancelable(false); 暂时不加
                exit_dialog.show();
				PutaoAccount.getInstance().logout(YellowPageLoginActivity.this);
				// 重新初始化账户信息
				refreshAccountInfo();
				exit_dialog.dismiss();
				exit_dialog = null;
				
			}
		});
		commonDialog.setCancelButtonClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				commonDialog.dismiss();
			}
		});
		commonDialog.show();
	}
	
	/**
	 * 解绑鉴权信息 add by hyl 2014-9-22
	 * 
	 * @param relateUser
	 *            鉴权凭证
	 */
	private void unBindYellowAccount(RelateUserResponse relateUser) {
		MobclickAgentUtil.onEvent(this, UMengEventIds.DISCOVER_YELLOWPAGE_MY_UNBIND_MOBILE);
		/*String progressMsg = getResources()
				.getString(R.string.putao_bind_process_msg);*/
		String accName = relateUser.accName;
		int accSource = relateUser.accSource;
		int accType = relateUser.accType;
		// 处理解绑
		/*YellowPageDataUtils.unbindAccount(YellowPageLoginActivity.this,
				progressMsg, accName, accSource, accType, new CallBack() {
					@Override
					public void onSuccess(String o) {
						// 解绑成功，重新初始化账户信息
						refreshAccountInfo();
						CoolCloudManager.getInstance().logout(null);
					}

					@Override
					public void onFinish(Object obj) {

					}

					@Override
					public void onFail(String msg) {
						Toast.makeText(YellowPageLoginActivity.this, msg,
								Toast.LENGTH_SHORT).show();
					}
				});*/
		PutaoAccount.getInstance().unbind(accName, accSource, accType, new IAccCallback() {
			
			@Override
			public void onSuccess() {
				refreshAccountInfo();
			}
			
			@Override
			public void onFail(int error_code) {
				Toast.makeText(YellowPageLoginActivity.this, PutaoAccount.getInstance().getToastTextError(YellowPageLoginActivity.this, error_code),
						Toast.LENGTH_SHORT).show();
				
			}

            @Override
            public void onCancel() {
                // TODO Auto-generated method stub
                
            }
		});
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.back_layout) {
			finish();
		}
		
	}
	/**
	 * 更新提示信息
	 * @param show 为true表示已绑定账户提示更换账户提示信息，false则提示绑定账户信息
	 */
	private void updateTip(boolean show) {
		if (show) {
			tipView.setText(getString(R.string.putao_text_tip_account_change));
		} else {
			tipView.setText(getString(R.string.putao_text_tip_account));
		}
	}
	
	/**
	 * 确认酷云登录dialog
	 */
	protected void showLoginKuyunDialog() {
		final CommonDialog commonDialog = CommonDialogFactory
				.getOkCancelCommonDialog(this);
		commonDialog.setTitle(R.string.putao_title_show_login_kuyun_dialog);
		commonDialog.getMessageTextView().setText(R.string.putao_msg_show_login_kuyun_dialog);
		commonDialog.setOkButtonClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
			    LogUtil.i(YellowPageDataUtils.TAG, "User confirm login kuyun");
				commonDialog.dismiss();
				mProgressDialog.show();
				login();
			}
		});
		commonDialog.setCancelButtonClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				commonDialog.dismiss();
			}
		});
		commonDialog.show();
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
