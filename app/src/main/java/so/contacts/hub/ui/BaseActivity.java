package so.contacts.hub.ui;

import so.contacts.hub.ad.AdViewCreator;
import so.contacts.hub.core.Config;
import so.contacts.hub.ui.yellowpage.BaseUIActivity;
import so.contacts.hub.ui.yellowpage.bean.YellowParams;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.UMengEventIds;
import so.contacts.hub.util.YellowUtil;
import so.contacts.hub.widget.CommonDialog;
import so.contacts.hub.widget.CommonDialogFactory;
import so.contacts.hub.widget.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.yulong.android.contacts.discover.R;

public class BaseActivity extends BaseUIActivity{
    
    private ProgressDialog mProgressDialog = null;
    
	private static final String TAG = "BaseActivity";
	
	CommonDialog commonDialog = null;
	
	/**
	 * title栏 内容
	 */
	protected String mTitleContent = null;

	protected int mRemindCode = -1;
	
	protected YellowParams mYellowParams = null;

	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		Intent intent = getIntent();
        if (intent != null) {
        	mYellowParams = (YellowParams)intent.getSerializableExtra(
                    YellowUtil.TargetIntentParams);
            if (mYellowParams != null) {
            	mTitleContent = mYellowParams.getTitle();
            	mRemindCode = mYellowParams.getRemindCode();
            }else{
            	mTitleContent = intent.getStringExtra("title");
            	mRemindCode = intent.getIntExtra("RemindCode", -1);
            }
        }
        
        /** add by zjh 2014-12-04 增加友盟统计 start */
        if (mYellowParams != null) {
        	int entryType = mYellowParams.getEntry_type();
        	if( entryType == YellowParams.ENTRY_TYPE_HOME_PAGE ){
        		MobclickAgentUtil.onEvent(this, UMengEventIds.DISCOVER_YELLOWPAGE_HEADER + 
        				mYellowParams.getCategory_id());
        	}else if( entryType == YellowParams.ENTRY_TYPE_SEARCH_PAGE ){
        		MobclickAgentUtil.onEvent(this, UMengEventIds.DISCOVER_SEARCH_SERVER_ACCESS + 
        				mYellowParams.getCategory_id());
        	}
        }
        /** add by zjh 2014-12-04 增加友盟统计 end */
        
        
        /*
         * 移除信鸽功能 注释该逻辑
         * modified by hyl 2014-12-24 start
         */
        /**
         * 获取信鸽传输的title参数
         */
//        XGPushClickedResult clickResult = XGPushManager.onActivityStarted(this);
//		if( clickResult != null ){
//			LogUtil.i(TAG, " get XG intent key-value.");
//			String customContent = clickResult.getCustomContent();
//			if( !TextUtils.isEmpty(customContent) ){
//				String xgValue = null;
//				try{
//					JSONObject json = new JSONObject(customContent);
//					xgValue = json.getString("title");
//				}catch(JSONException e){
//					xgValue = null;
//				}
//				if( !TextUtils.isEmpty(xgValue) ){
//					mTitleContent = xgValue;
//				}
//			}
//		}
//        LogUtil.i(TAG, "mTitleContent: " + mTitleContent);
        //modified by hyl 2014-12-24 end
	}
	
	protected void onMyStart() {
	}

	protected void onMyRestart() {
	}

	protected void onMyResume() {
	}

	protected void onMyPause() {
	}

	protected void onMyStop() {
	}

	protected synchronized void onMyDestroy() {
		// if (this != null) {
		// onDestroy();
		// }
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	protected void onDestroy(){
	    if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
	    super.onDestroy();
	    AdViewCreator.getCreator().onDestory(this);
	}
	
	@Override
	protected void onResume() {
//		if (ContactsApp.isForceQuit) {
//			ContactsApp.isForceQuit = false;
//			ContactsHubUtils.forceQuit(this);
//		}
		super.onResume();
		AdViewCreator.getCreator().handleAdView(this);
	}

	protected void forceQuit(final Context context) {
		if (commonDialog != null) {
			return;
		}
		commonDialog = CommonDialogFactory.getOkCancelCommonDialog(context);
		commonDialog.setTitle(R.string.putao_account_alert);
		TextView msgTv = commonDialog.getMessageTextView();
		msgTv.setText(R.string.putao_force_account_logout_alert);
		commonDialog.getCancelButton().setText(R.string.putao_confirm);
		commonDialog.getOkButton().setVisibility(View.GONE);
		commonDialog.setCancelButtonClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				commonDialog.dismiss();
				commonDialog = null;
				new Thread(new Runnable() {
					@Override
					public void run() {
						Config.logout();
					}
				}).start();
//				startActivity(new Intent(context, AccountSettingActivity.class));
			}
		});
		commonDialog.show();
	}

	
	public Integer getAdId(){
	    return null;
	}
	
    protected void showLoadingDialog() {
//        if (isFinishing()) {
//            return;
//        }
//        if (mProgressDialog == null) {
//            mProgressDialog = new ProgressDialog(this);
//            mProgressDialog.setMessage(getString(R.string.putao_yellow_page_loading));
//            mProgressDialog.setCanceledOnTouchOutside(false);
//        }
//        if(!mProgressDialog.isShowing()){
//        	mProgressDialog.show();
//        }
        showLoadingDialog(true);
    }

    protected void showLoadingDialog(boolean isHaveContent) {
        if (isFinishing()) {
            return;
        }
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this,isHaveContent);
            mProgressDialog.setMessage(getString(R.string.putao_yellow_page_loading));
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        if(!mProgressDialog.isShowing()){
        	mProgressDialog.show();
        }
    } 
    
    protected void dismissLoadingDialog() {
        if (isFinishing()) {
            return;
        }
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }
	
}
