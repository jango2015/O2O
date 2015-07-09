package so.contacts.hub.ui.yellowpage;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;

import so.contacts.hub.yellow.data.RemindBean;

import java.util.List;

import so.contacts.hub.account.AccountInfo;
import so.contacts.hub.account.IAccCallback;
import so.contacts.hub.account.PutaoAccount;
import so.contacts.hub.core.Config;
import so.contacts.hub.core.ConstantsParameter;
import so.contacts.hub.core.PTUser;
import so.contacts.hub.db.YellowPageDB;
import so.contacts.hub.http.bean.RelateUserResponse;
import so.contacts.hub.lottery.LotteryMessageBusiness;
import so.contacts.hub.msgcenter.PTMessageCenterFactory;
import so.contacts.hub.msgcenter.bean.PTMessageBean;
import so.contacts.hub.msgcenter.ui.YellowPageMessageCenterActivity;
import so.contacts.hub.msgcenter.ui.YellowPageMyOrderActivity;
import so.contacts.hub.remind.BaseRemindFragmentActivity;
import so.contacts.hub.remind.RemindConfig;
import so.contacts.hub.remind.RemindManager;
import so.contacts.hub.remind.simple.SimpleRemindView;
import so.contacts.hub.remind.utils.RemindUtils;
import so.contacts.hub.thirdparty.dianping.DianPingApiTool;
import so.contacts.hub.train.TongChengConfig;
import so.contacts.hub.train.YellowPageTrainTicketOrderHistoryH5Activity;
import so.contacts.hub.ui.yellowpage.bean.CategoryBean;
import so.contacts.hub.ui.yellowpage.bean.YellowParams;
import so.contacts.hub.util.ContactsAppUtils;
import so.contacts.hub.util.ContactsHubUtils;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.MyCenterConstant;
import so.contacts.hub.util.UMengEventIds;
import so.contacts.hub.util.YellowUtil;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loader.DataLoader;
import com.loader.DataLoaderListener;
import com.loader.image.ImageLoaderFactory;
import com.yulong.android.contacts.discover.R;

public class YellowPageMyActivity extends BaseRemindFragmentActivity implements
		OnClickListener {

	private static final String TAG = "YellowPageMyActivity";

	// private ArrayList<SimpleRemindView> mRemindList = null;

	private DataLoader mImageLoader = null;

	/*
	 * 账户相关声明
	 */
	// private LinearLayout mAccountLayout = null; // 账户layout
	private ImageView mAccountImg = null; // 账户头像
	private TextView mAccountNameTextView = null;// 账户名称
	private TextView mAccountSubTextView = null; // 账户描述
	// private TextView mAccountSourceView = null; // 账户来源(如：酷云账户)
	// private Button mLoginOutBtn = null;// 退出登录按钮
	private RelateUserResponse phoneRelateUser = null; // 手机鉴权账户信息
	private RelateUserResponse kuyunRelateUser = null; // 酷云鉴权账户信息 add by
														// putao_lhq

	// private RelativeLayout mOrderLayout = null; // 订单layout
	private RelativeLayout mFavoriteLayout = null; // 收藏layout
	private RelativeLayout mHistoryLayout = null; // 历史layout
	private RelativeLayout mTuanOrderLayout = null; // 团购订券layout

	/**
	 * delete code
	 * modify by putao_lhq
	 * 添加我的订单，合并功能到我的订单
	 * 
	private RelativeLayout mHotelOrderLayout = null; // 酒店订房layout
	private RelativeLayout mChargeHistoryLayout = null; // 充值历史
	private RelativeLayout mTongchengTrain = null; // 同城火车票
	 */
	private RelativeLayout mActivitiesHistoryLayout = null; // 活动历史

	private SimpleRemindView mActiveRemindView = null; // 活动 打点view
	private SimpleRemindView mOrderRemindView = null; // 订单 打点view
	/**
	 * delete code by putao_lhq
	 * 添加我的订单，合并功能到我的订单
	 *
	// private SimpleRemindView mLotteryRemindView = null; // 彩票 打点view
	private SimpleRemindView mHotelRemindView = null; // 酒店 打点view
	private SimpleRemindView mChargeRemindView = null; // 充值 打点view
	private SimpleRemindView mTongchengTrainRemindView = null; // 同城火车票打点view
	 */
	
	// modify by putao_lhq 2014年12月17日 start
	/**
	 * 添加提醒中心
	 */
	private RelativeLayout mNotifyCenterLayout = null; // 提醒中心
	private SimpleRemindView mNotifyRemindView = null; // 提醒中心打点
//	private TextView mNotifyView = null; // 最新提醒消息
	// modify by putao_lhq 2014年12月17日  end

    private RelativeLayout mOrderLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.putao_yellow_page_my);

		mImageLoader = new ImageLoaderFactory(this)
				.getDefaultYellowPageLoader();
		initView();
		registerRemindUpdateReceiver();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgentUtil.onResume(this);
		LogUtil.d(TAG, "onResume...");

		mActiveRemindView = (SimpleRemindView) findViewById(R.id.my_activities_item_remindview);
		mActiveRemindView.setRemind(RemindUtils
		        .getRemind(RemindConfig.MyActivies));
		mOrderRemindView = (SimpleRemindView) findViewById(R.id.my_tuan_order_item_remindview);
		mOrderRemindView.setRemind(RemindUtils
		        .getRemind(RemindConfig.MyOrderTuan));
		
		/**
		 * delete code
		 * modify by putao_lhq 添加我的订单，合并这部分功能到我的订单里
		 */ 
		 /*


		// mLotteryRemindView = (SimpleRemindView)
		// findViewById(R.id.my_lottery_order_item_remindview);
		// mLotteryRemindView.setRemind(RemindUtils.getRemind(RemindConfig.MyLottery));

		mHotelRemindView = (SimpleRemindView) findViewById(R.id.my_hotel_order_item_remindview);
		mHotelRemindView.setRemind(RemindUtils
				.getRemind(RemindConfig.MyOrderHotel));

		mChargeRemindView = (SimpleRemindView) findViewById(R.id.my_charge_order_item_remindview);
		mChargeRemindView.setRemind(RemindUtils
				.getRemind(RemindConfig.MyOrderChargeHistory));

		mTongchengTrainRemindView = (SimpleRemindView) findViewById(R.id.my_tongcheng_train_item_remindview);
		mTongchengTrainRemindView.setRemind(RemindUtils
				.getRemind(RemindConfig.MyTongchengTrain));
		*/

		PTUser ptUser = PutaoAccount.getInstance().getPtUser();
		if (ptUser == null) {
			showAccountInfo(false, null);// add by putao_lhq
			/*
			 * YellowPageDataUtils.silentLogin(this, new CallBack() {
			 * 
			 * @Override public void onSuccess(String o) { initAccoutInfo(); }
			 * 
			 * @Override public void onFinish(Object obj) {
			 * 
			 * }
			 * 
			 * @Override public void onFail(String msg) { showAccountInfo(false,
			 * null); } });
			 */
			PutaoAccount.getInstance().silentLogin(new IAccCallback() {

				@Override
				public void onSuccess() {
					initAccoutInfo();
				}

				@Override
				public void onFail(int msg) {
					showAccountInfo(false, null);

				}

                @Override
                public void onCancel() {
                    // TODO Auto-generated method stub
                    
                }
			});
		} else {
			// 读取黄页账户信息 add by hyl 2014-9-20 start
			initAccoutInfo();
			// add by hyl 2014-9-20 end
		}

		// add by putao_lhq 2014年12月17日 for 提醒中心 start
		initNotifyInfo();
		// add by putao_lhq 2014年12月17日 for 提醒中心 end
	}

	private void initView() {
		// delete by putao_lhq for 酷云账号
		/*
		 * if (getIntent() != null) { YellowParams params = (YellowParams)
		 * getIntent() .getSerializableExtra(YellowUtil.TargetIntentParams); if
		 * (params != null) { ((TextView)
		 * findViewById(R.id.title)).setText(params .getCategory_name()); } }
		 */

	    mOrderLayout = (RelativeLayout) findViewById(R.id.my_order_item);
		mFavoriteLayout = (RelativeLayout) findViewById(R.id.my_favorite_item);
		mHistoryLayout = (RelativeLayout) findViewById(R.id.my_history_item);

		// mAccountLayout = (LinearLayout)
		// findViewById(R.id.unlogin_layout);//delete by putao_lhq
		mAccountImg = (ImageView) findViewById(R.id.mobile_account_head_img);
		mAccountNameTextView = (TextView) findViewById(R.id.mobile_account_name_text);
		mAccountSubTextView = (TextView) findViewById(R.id.mobile_account_sub_text);
		// delete by putao_lhq 2014年10月8日 for cool-cloud start
		// mAccountSourceView = (TextView)
		// findViewById(R.id.mobile_account_source);
		// mLoginOutBtn = (Button) findViewById(R.id.login_out_btn);

		// mAccountLayout.setOnClickListener(this);
		// mLoginOutBtn.setOnClickListener(this);
		// delete by putao_lhq 2014年10月8日 for cool-cloud end
		mAccountImg.setOnClickListener(this);// add by putao_lhq 2014年10月8日 for
												// 酷云
		mOrderLayout.setOnClickListener(this);
		mFavoriteLayout.setOnClickListener(this);
		mHistoryLayout.setOnClickListener(this);
		findViewById(R.id.back_layout).setOnClickListener(this);

		mTuanOrderLayout = (RelativeLayout) findViewById(R.id.my_tuan_order_item);
		mTuanOrderLayout.setOnClickListener(this);
		/**
		 * delete code
		 * modify by putao_lhq
		 * 添加我的订单，合并功能到我的订单
		mHotelOrderLayout = (RelativeLayout) findViewById(R.id.my_hotel_order_item);
		mChargeHistoryLayout = (RelativeLayout) findViewById(R.id.my_charge_order_item);
		mTongchengTrain = (RelativeLayout) findViewById(R.id.my_tongcheng_train);
		mHotelOrderLayout.setOnClickListener(this);
		mChargeHistoryLayout.setOnClickListener(this);
		mTongchengTrain.setOnClickListener(this);
		*/

		mActivitiesHistoryLayout = (RelativeLayout) findViewById(R.id.my_activities_item);

		mActivitiesHistoryLayout.setOnClickListener(this);
		findViewById(R.id.my_user_feedback_item).setOnClickListener(this);
		
		// add by putao_lhq 2014年12月17日 for 提醒中心 start
		mNotifyCenterLayout = (RelativeLayout)findViewById(R.id.my_notify_item);
		mNotifyCenterLayout.setOnClickListener(this);
		// add by putao_lhq 2014年12月17日 for 提醒中心 end
	}

	/**
	 * 初始化黄页账户信息 add by hyl 2014-9-19
	 */
	private void initAccoutInfo() {

		phoneRelateUser = null;// add by hyl 2014-9-28 每次初始化账户信息时，将手机鉴权信息置为空

		PTUser ptUser = PutaoAccount.getInstance().getPtUser();
		if (ptUser != null) {
			List<RelateUserResponse> relateUsers = ptUser.getRelateUsers();
			LogUtil.v(PutaoAccount.TAG, "relateUser: " + relateUsers);
			if (relateUsers != null) {
				kuyunRelateUser = PutaoAccount.getInstance()
						.getRelateUserResponse(RelateUserResponse.TYPE_FACTORY);
				if (kuyunRelateUser != null) {
					showAccountInfo(true, kuyunRelateUser);
					return;
				}
				phoneRelateUser = PutaoAccount.getInstance()
						.getRelateUserResponse(RelateUserResponse.TYPE_PHONE);
				if (phoneRelateUser != null) {
					showAccountInfo(true, phoneRelateUser);
				} else {
					showAccountInfo(false, null);
				}
			}
		} else {
			showAccountInfo(false, null);
		}
	}

	/**
	 * 显示账户信息
	 */
	private void showAccountInfo(boolean isLogin, RelateUserResponse relateUser) {
		// modify by putao_lhq 2014年10月9日 for 酷云账号
		/*
		 * if( isLogin ){ // 有账户登录 mAccountNameTextView.setText(accountName);
		 * mAccountSubTextView.setVisibility(View.GONE);
		 * mAccountSourceView.setVisibility(View.GONE);
		 * mLoginOutBtn.setVisibility(View.VISIBLE);
		 * mAccountImg.setImageResource(R.drawable.icon_my_user_login); }else{
		 * mAccountNameTextView.setText(R.string.putao_bind_mobile);
		 * mAccountSubTextView.setVisibility(View.VISIBLE);
		 * mAccountSourceView.setVisibility(View.INVISIBLE);
		 * mLoginOutBtn.setVisibility(View.GONE);
		 * mAccountImg.setImageResource(R.drawable.icon_my_user); }
		 */
		if (isLogin) {
			// 有账户登录
			// 显示名称顺序：1.昵称，2.账户名，3.该账户下的openId.
			String displayName = PutaoAccount.getInstance().getDisplayName(
					relateUser);
			if (TextUtils.isEmpty(displayName)) {
				displayName = relateUser.accName;
			}
			mAccountNameTextView.setText(displayName);
			mAccountSubTextView.setVisibility(View.GONE);
			if (relateUser.accSource == RelateUserResponse.TYPE_FACTORY) {
				mAccountImg.setImageResource(R.drawable.putao_icon_kuyun_login);
				setAvatarImage(mAccountImg, this, relateUser);
			} else {
				mAccountImg
						.setImageResource(R.drawable.putao_icon_my_user_login);
			}

		} else {
			mAccountNameTextView.setText(R.string.putao_bind_mobile);
			mAccountSubTextView.setVisibility(View.VISIBLE);
			mAccountImg.setImageResource(R.drawable.putao_icon_my_user);
		}
	}

	/**
	 * 进入我的订单
	 */
	/*
	 * private void startSendMyOrderActivity() { MobclickAgentUtil.onEvent(this,
	 * UMengEventIds.DISCOVER_YELLOWPAGE_MY_ORDER); Intent intent = null; try{
	 * intent = new Intent(YellowPageMyActivity.this,
	 * YellowPageMyOrderActivity.class); intent.putExtra("RemindCode",
	 * RemindConfig.MyOrder); startActivity(intent); }catch(Exception e){ } }
	 */

	/**
	 * 进入历史收藏页面
	 */
	private void startFavoriteAndHistoryActivity(boolean isFavorite) {
		int type = -1;
		if (isFavorite) {
			type = ConstantsParameter.YELLOWPAGE_DATA_TYPE_FAVORITE;
			MobclickAgentUtil.onEvent(this,
					UMengEventIds.DISCOVER_YELLOWPAGE_MY_FAVORITE);
		} else {
			type = ConstantsParameter.YELLOWPAGE_DATA_TYPE_HISTORY;
			MobclickAgentUtil.onEvent(this,
					UMengEventIds.DISCOVER_YELLOWPAGE_MY_HISTORY);
		}
		Intent intent = null;
		try {
			intent = new Intent(YellowPageMyActivity.this,
					YellowPageMyFavoriteHistoryActivity.class);
			intent.putExtra("type", type);
			startActivity(intent);
		} catch (Exception e) {
		}
	}

	/**
	 * 解绑鉴权信息 add by hyl 2014-9-22
	 * 
	 * @param relateUser
	 *            鉴权凭证
	 */
	/*
	 * private void unBindYellowAccount(RelateUserResponse relateUser) {
	 * MobclickAgentUtil.onEvent(this,
	 * UMengEventIds.DISCOVER_YELLOWPAGE_MY_UNBIND_MOBILE); String progressMsg =
	 * getResources() .getString(R.string.putao_bind_process_msg); String
	 * accName = relateUser.accName; int accSource = relateUser.accSource; int
	 * accType = relateUser.accType; // 处理解绑
	 * YellowPageDataUtils.unbindAccount(YellowPageMyActivity.this, progressMsg,
	 * accName, accSource, accType, new CallBack() {
	 * 
	 * @Override public void onSuccess(String o) { // 解绑成功，重新初始化账户信息
	 * initAccoutInfo(); }
	 * 
	 * @Override public void onFinish(Object obj) {
	 * 
	 * }
	 * 
	 * @Override public void onFail(String msg) {
	 * Toast.makeText(YellowPageMyActivity.this, msg,
	 * Toast.LENGTH_SHORT).show(); } }); } move to YellowPageLoginActivity.java.
	 */

	@Override
	public void onClick(View arg0) {
		int id = arg0.getId();
		Intent intent = null;
		String targetActivity = null;
		YellowParams params = null;
		if (id == R.id.my_favorite_item) {
			startFavoriteAndHistoryActivity(true);
		} else if (id == R.id.my_history_item) {
			startFavoriteAndHistoryActivity(false);
		} else if (id == R.id.back_layout) {
			finish();
		} else if (id == R.id.mobile_account_head_img) {
			intent = new Intent(YellowPageMyActivity.this,
					YellowPageLoginActivity.class);
			startActivity(intent);
		/**
			 * delete code
			 * modify by putao_lhq
			 * 添加我的订单，合并功能到我的订单
			 * 
		} else if (id == R.id.my_hotel_order_item) {
			// 酒店
			targetActivity = MyCenterConstant.MY_NODE_HOTEL_ORDER;
			if (params == null)
				params = new YellowParams();
			try {
				RemindManager.onRemindClick(RemindConfig.MyOrderHotel);
				intent = new Intent(YellowPageMyActivity.this,
						Class.forName(targetActivity));
				params.setTitle(getString(R.string.putao_order_hotel));
				intent.putExtra(YellowUtil.TargetIntentParams, params);
				startActivity(intent);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		    } else if (id == R.id.my_charge_order_item) {
			targetActivity = MyCenterConstant.MY_NODE_CHAGER_HISTROY_ORDER;
			params = null;
			if (params == null)
				params = new YellowParams();
			try {
				RemindManager.onRemindClick(RemindConfig.MyOrderChargeHistory);
				intent = new Intent(YellowPageMyActivity.this,
						Class.forName(targetActivity));
				params.setTitle(getString(R.string.putao_order_charge_history));
				intent.putExtra(YellowUtil.TargetIntentParams, params);
				startActivity(intent);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}*/
		} else if (id == R.id.my_activities_item) {
			targetActivity = MyCenterConstant.MY_NODE_ACTIVE_HISTROY;
			params = null;
			if (params == null)
				params = new YellowParams();
			try {
				RemindManager.onRemindClick(RemindConfig.MyActivies);
				intent = new Intent(YellowPageMyActivity.this,
						Class.forName(targetActivity));
				params.setTitle(getString(R.string.putao_yellow_page_my_activities));
				intent.putExtra(YellowUtil.TargetIntentParams, params);
				startActivity(intent);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		/**
		 * delete code
		 * modify by putao_lhq
		 * 添加我的订单，合并功能到我的订单
		 * 
		    } else if (id == R.id.my_tongcheng_train) {

			// add by lisheng http://10.1.204.41/pub
			MobclickAgentUtil.onEvent(this, UMengEventIds.DISCOVER_YELLOWPAGE_TONGCHENG_MYORDERHISTORY);
			String url = TongChengConfig.YELLOW_PAGE_TONGCHENG_ORDERQUERY;
			intent = new Intent(this, YellowPageJumpH5Activity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			intent.putExtra("targetActivityName",
					YellowPageTrainTicketOrderHistoryH5Activity.class.getName());
			YellowParams yellowParams = new YellowParams();
			yellowParams
					.setTitle(getString(R.string.putao_traintriket_orderhistory));
			String open_token = PutaoAccount.getInstance().getOpenToken();
			if (!TextUtils.isEmpty(open_token)) {
				url = url + "&open_token=" + open_token + "&"
						+ TongChengConfig.PUTAO_TONGCHENG_REFID;
			}
			yellowParams.setUrl(url);
			// yellowParams.setUrl("http://121.41.60.51:7899/_plugin/head/train.html");
			intent.putExtra(YellowUtil.TargetIntentParams, yellowParams);
			startActivity(intent);
			// add by lisheng
*/
		}else if(id == R.id.my_user_feedback_item){
			targetActivity = MyCenterConstant.MY_NODE_USER_FEEDBACK;
			params = null;
			if (params == null)
				params = new YellowParams();
			try {
//				RemindManager.onRemindClick(RemindConfig.MyActivies);//TODO 需要定义
				intent = new Intent(YellowPageMyActivity.this,
						Class.forName(targetActivity));
				params.setTitle(getResources().getString(R.string.putao_yellow_page_my_user_feedback));
				intent.putExtra(YellowUtil.TargetIntentParams, params);
				startActivity(intent);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		/**
		 * add code
		 * modify by putao_lhq
		 * 添加提醒中心 我的订单功能
		 */
		else if (id == R.id.my_notify_item) {
		    //启动提醒中心
		    startActivity(new Intent(this, YellowPageMessageCenterActivity.class));
		    RemindUtils.clearMsgNode();
            MobclickAgentUtil.onEvent(YellowPageMyActivity.this,
                    UMengEventIds.DISCOVER_YELLOWPAGE_MY_MSG_CENTER_IN);
		} else if (id == R.id.my_order_item) {
		    //启动我的订单
		    startActivity(new Intent(this, YellowPageMyOrderActivity.class));
            MobclickAgentUtil.onEvent(YellowPageMyActivity.this,
                    UMengEventIds.DISCOVER_YELLOWPAGE_MY_ORDER_CENTER_IN);
		}/*add code end, by putao_lhq*/
		 else if (id == R.id.my_tuan_order_item) {
	            targetActivity = MyCenterConstant.MY_NODE_TUAN_ORDER;
	            params = null;
	            if (params == null)
	                params = new YellowParams();
	            try {
	                RemindManager.onRemindClick(RemindConfig.MyOrderTuan);
	                intent = new Intent(YellowPageMyActivity.this,
	                        Class.forName(targetActivity));
	                params.setTitle(getString(R.string.putao_order_group));

	                String url = DianPingApiTool.MY_DIANPING_QUAN;//modified by hyl 2014-11-28 start

	                
//	                 * 添加团购用户标识uid add by hyl 2014-9-23 start
	                 
	                PTUser ptUser = PutaoAccount.getInstance().getPtUser();
	                if (ptUser != null) {
	                    String ptUid = ptUser.getPt_uid();
	                    if (!TextUtils.isEmpty(ptUid)) {
	                        url = url + "?uid=" + ptUid;
	                    }
	                }
	                // add by hyl 2014-9-23 end
	                params.setUrl(url);
	                intent.putExtra(YellowUtil.TargetIntentParams, params);
	                startActivity(intent);
	            } catch (ClassNotFoundException e) {
	                e.printStackTrace();
	            }
	        }
		
	}

    /**
	 * 调用服务接口，退出登录 (暂未启用该方法) add by hyl 2014-9-23
	 */
	/*
	 * private void logOutForServer() { //
	 * if(Config.getPTUser().relateUsers.size() == 0){//当前账户已解绑所有的鉴权凭证 //
	 * 当前账户已解绑所有的鉴权凭证，提示用户是否确认退出，如果退出将再也找不回改账户了 final CommonDialog commonDialog
	 * = CommonDialogFactory .getOkCancelCommonDialog(this);
	 * commonDialog.setTitle(R.string.putao_alert);
	 * commonDialog.getMessageTextView().setText("");
	 * commonDialog.setOkButtonClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View arg0) { commonDialog.dismiss();
	 * 
	 * YellowPageDataUtils.ptUserDelete(YellowPageMyActivity.this,
	 * "progressMsg", Config.getPTUser().pt_token, new CallBack() {
	 * 
	 * @Override public void onSuccess(String o) { // 退出登录 YellowPageDataUtils
	 * .loginOutPtUser(YellowPageMyActivity.this); // 重新初始化账户信息
	 * initAccoutInfo(); }
	 * 
	 * @Override public void onFinish(Object obj) {
	 * 
	 * }
	 * 
	 * @Override public void onFail(String msg) {
	 * 
	 * } });
	 * 
	 * } }); commonDialog.setCancelButtonClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View arg0) { commonDialog.dismiss();
	 * 
	 * YellowPageDataUtils.ptUserDelete(YellowPageMyActivity.this,
	 * "progressMsg", Config.getPTUser().pt_token, new CallBack() {
	 * 
	 * @Override public void onSuccess(String o) { // 退出登录 YellowPageDataUtils
	 * .loginOutPtUser(YellowPageMyActivity.this); // 重新初始化账户信息
	 * initAccoutInfo(); }
	 * 
	 * @Override public void onFinish(Object obj) {
	 * 
	 * }
	 * 
	 * @Override public void onFail(String msg) {
	 * 
	 * } });
	 * 
	 * } }); commonDialog.show(); }
	 */

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgentUtil.onPause(this);
	}

	@Override
	protected void onDestroy() {
		if (mImageLoader != null) {
			mImageLoader.clearCache();
		}
		unregisterRemindUpdateReceiver();
		super.onDestroy();
	}

	@Override
	public Integer remindCode() {
		YellowPageDB db = ContactsAppUtils.getInstance().getDatabaseHelper()
				.getYellowPageDBHelper();

		CategoryBean myBean = db
				.queryCategoryByCategoryId(RemindConfig.MyService);
		if (myBean == null)
			return -1;

		return myBean.getRemind_code();
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

	/**
	 * 设置账号头像
	 * 
	 * @param view
	 *            需要设置的imageView
	 * @param context
	 *            上下文
	 * @param relateUser
	 *            鉴权用户，暂时只有酷云有用户信息，即只有酷云账号可以用户自定义头像。
	 */
	private void setAvatarImage(final ImageView view, final Context context,
			RelateUserResponse relateUser) {
		if (relateUser == null || TextUtils.isEmpty(relateUser.accMsg)) {
			LogUtil.v(TAG, "setAvatarImage fail.");
			return;
		}
		AccountInfo info = Config.mGson.fromJson(relateUser.accMsg,
				AccountInfo.class);
		if (null == info) {
			LogUtil.v(TAG, "setAvatarImage fail.");
			return;
		}
		String avatar_url_hd = info.getAvatar_hd_url();
		String avatar_url = info.getAvatar_url();
		// 如果使用了DataLoader 则没有按下效果了 注意
		DataLoader dataLoader = new ImageLoaderFactory(context)
				.getCoolCloudAvatarLoader(context);

		if (avatar_url_hd != null && avatar_url_hd.length() > 0) {
			dataLoader.loadData(avatar_url_hd, view, new DataLoaderListener() {

				@Override
				public void fillDataInView(Object result, View view) {
					if (result != null && view != null) {
						ImageView iv = (ImageView) view;
						Bitmap bitmap = (Bitmap) result;
						bitmap = ContactsHubUtils.makeRoundCorner(bitmap);
						iv.setImageBitmap(bitmap);
					}

				}
			});
		} else if (avatar_url != null && avatar_url.length() > 0) {
			dataLoader.loadData(avatar_url, view, new DataLoaderListener() {
				@Override
				public void fillDataInView(Object result, View view) {
					if (result != null && view != null) {
						ImageView iv = (ImageView) view;
						Bitmap bitmap = (Bitmap) result;
						bitmap = ContactsHubUtils.makeRoundCorner(bitmap);
						iv.setImageBitmap(bitmap);
					}
				}
			});
		}
	}
	
	/**
	 * 更新最新提醒信息
	 * add by putao_lhq for 提醒中心
	 */
	private void initNotifyInfo() {
//	    if (mNotifyView == null) {
//	        mNotifyView = (TextView)findViewById(R.id.my_notify_item_digest);
//	    }
	    if (mNotifyRemindView == null) {
	        mNotifyRemindView = (SimpleRemindView)findViewById(R.id.my_notify_item_remindview);
	    }
	   
	    
	    
	    PTMessageBean bean = PTMessageCenterFactory.getPTMessageCenter().getNewestMsg();
//	    if (bean == null) {
//	        mNotifyView.setVisibility(View.GONE);
//	    } else {
//	        mNotifyView.setText(bean.getDigest());
//	        mNotifyView.setVisibility(View.VISIBLE);
//	    }
	    
	    //TODO 添加提醒中心打点逻辑
	    
	    // add xcx 2014-12-27 start 添加打点信息显示
	    RemindBean remindBean=RemindUtils.getRemind(RemindConfig.MyMsgCenter);
        if(null!=remindBean && remindBean.getRemindType()>=RemindConfig.REMIND_TYPE_VIEW_CLEAN){
            mNotifyRemindView.setVisibility(View.VISIBLE);
            mNotifyRemindView.setRemind(remindBean);
        }else{
            mNotifyRemindView.setVisibility(View.GONE);
        }
        // add xcx 2014-12-27 end 添加打点信息显示
	}
	
    RemindUpdateReceiver remindUpdateReceiver = null;

    private void registerRemindUpdateReceiver() {
        String action = ConstantsParameter.ACTION_REMIND_UPDATE_PLUG;
        IntentFilter filter = new IntentFilter(action);

        remindUpdateReceiver = new RemindUpdateReceiver();
        registerReceiver(remindUpdateReceiver, filter);
    }

    private void unregisterRemindUpdateReceiver() {
        if (remindUpdateReceiver != null) {
            unregisterReceiver(remindUpdateReceiver);
        }
    }

    private class RemindUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            LogUtil.d(TAG, "RemindUpdateReceiver onReceive" + intent.getAction());
            initNotifyInfo();
        }
    }
	
}
