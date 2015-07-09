package so.contacts.hub.ui.yellowpage;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import so.contacts.hub.ad.AdCode;
import so.contacts.hub.common.OperatorsCommans;
import so.contacts.hub.remind.BaseRemindActivity;
import so.contacts.hub.remind.RemindManager;
import so.contacts.hub.ui.yellowpage.bean.QueryCommandInfo;
import so.contacts.hub.util.DualCardUtil;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.OperatorsUtils;
import so.contacts.hub.util.SIMCardUtil;
import so.contacts.hub.util.Utils;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yulong.android.contacts.discover.R;
import com.yulong.android.contacts.ui.yellowpage.DoubleCardInterface;

public class YellowPageNewQueryChargeActivity extends BaseRemindActivity implements OnClickListener {

    private static final String TAG = "YellowPageNewQueryChargeActivity";

    // Simcard1 布局
    private RelativeLayout mSimcardLayout1 = null;

    private TextView mSimcardOperatorTView1 = null;

    private TextView mSimcardQueryStateTView1 = null;

    private TextView mSimcardMoneyTView1 = null;

    // Simcard2 布局
    private RelativeLayout mSimcardLayout2 = null;

    private TextView mSimcardOperatorTView2 = null;

    private TextView mSimcardQueryStateTView2 = null;

    private TextView mSimcardMoneyTView2 = null;

    // 查询余额 布局
    private RelativeLayout mConfirmLayout = null;

    private TextView mConfirmContentTView = null;

    private TextView mConfirmWaitTView = null;

    // 查询余额的状态
    private int mQueryState = QUERY_STATE_WELCOME;

    private static final int QUERY_STATE_WELCOME = 1;

    private static final int QUERY_STATE_QUERYING = 2;

    private static final int QUERY_STATE_FAILED = 3;

    private static final int QUERY_STATE_SHOWDATA = 4;

    private static final int QUERY_STATE_TIMEOUT = 5;

    private static final int QUERY_STATE_PARSE_ERROR = 6;

    private static final int QUERY_NO_SIMCARD = 7;

    private SIMCardUtil mSimUtil = null;

    // “查询余额” 按钮打点 计数
    private int mQueryingComputeNum = 0;

    // “查询中” 字符串
    private String mChargingBtnStr = "";

    // 卡槽1的 号码
    private String mSimcardName1 = "";

    // 卡槽2的 号码
    private String mSimcardName2 = "";

    // SIM卡 的数量
    private int mSimcardNum = 0;

    // SIM卡1 是否可用
    private boolean mAvaliableSimcard1 = false;

    // SIM卡2 是否可用
    private boolean mAvaliableSimcard2 = false;

    // SIM卡1 接收到查余额短信
    private boolean mReceiveSimCard1 = false;

    // SIM卡2 接收到查余额短信
    private boolean mReceiveSimCard2 = false;

    // 是否可以接收“查余额”短信
    private boolean mNeedReceiveMsg = true;

    // 发送短信检测最长时间1min
    private static final int SEND_MESSAGE_TIME_GAP = 60000;

    private static final int MSG_QUERYING_ACTION = 0x2001;

    // Sim卡 检查短信发送1min后是否接收到信息
    private static final int MSG_CHECK_MESSAGE_RECEIVE_TIMEOUT_ACTION = 0x2002;

    // Sim卡1 短信解析成功
    private static final int MSG_CHECK_MESSAGE_PARSE_SUCCESS_ACTION1 = 0x2003;

    // Sim卡2 短信解析成功
    private static final int MSG_CHECK_MESSAGE_PARSE_SUCCESS_ACTION2 = 0x2004;

    // Sim卡1 短信解析失败
    private static final int MSG_CHECK_MESSAGE_PARSE_FAILED_ACTION1 = 0x2005;

    // Sim卡2 短信解析失败
    private static final int MSG_CHECK_MESSAGE_PARSE_FAILED_ACTION2 = 0x2006;

    // Sim卡1 短信发送失败
    private static final int MSG_CHECK_MESSAGE_SEND_FAILED_ACTION1 = 0x2007;

    // Sim卡2 短信发送失败
    private static final int MSG_CHECK_MESSAGE_SEND_FAILED_ACTION2 = 0x2008;

    // 查余额 按钮可用
    private static final int MSG_CLICKABLE_ACTION = 0x2009;

    // 酷派手机设为true
    private boolean mPlatformInCoolpad = true;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.putao_querycharge_layout);
        mSimUtil = new SIMCardUtil(this);

        initBindService();
        initView();
        initLayout();
        initReceiver();

        // 增加个日志调试蛋
        RemindManager.getInstance().dumps("=====================");
    }

    private DoubleCardInterface mService = null;

    // client与coolpad service建立连接
    private ServiceConnection servconn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtil.i(TAG, "onServiceConnected ComponentName=" + name + " service=" + service);
            mService = DoubleCardInterface.Stub.asInterface(service);            
            if(mService == null) {
                LogUtil.e(TAG, "onServiceConnected failed");
            }
            initLayout();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtil.i(TAG, "onServiceDisconnected ComponentName=" + name);
        }
    };
    
    public DoubleCardInterface getDoubleCardService() {
        return mService;
    }
    
    private void initBindService() {
        // 绑定coolpad 读取slot和sim的 service
        this.bindService(new Intent("com.yulong.android.contacts.yellowpage.service"), servconn, BIND_AUTO_CREATE);
        LogUtil.d(TAG, "initBindService");
    }
    
    private void unbindService() {
        this.unbindService(servconn);
        LogUtil.d(TAG, "unbindService");
    }
    
    private void initView() {
        if( TextUtils.isEmpty(mTitleContent) ){
        	mTitleContent = getResources().getString(R.string.putao_query_telcharge_hint_head);
        }
        ((TextView)findViewById(R.id.title)).setText(mTitleContent);
        findViewById(R.id.back_layout).setOnClickListener(this);

        // Simcard1 布局
        mSimcardLayout1 = (RelativeLayout)findViewById(R.id.simcard_layout1);
        mSimcardOperatorTView1 = (TextView)findViewById(R.id.simcard_operator1);
        mSimcardQueryStateTView1 = (TextView)findViewById(R.id.simcard_querydate1);
        mSimcardMoneyTView1 = (TextView)findViewById(R.id.simcard_money1);

        // Simcard2 布局
        mSimcardLayout2 = (RelativeLayout)findViewById(R.id.simcard_layout2);
        mSimcardOperatorTView2 = (TextView)findViewById(R.id.simcard_operator2);
        mSimcardQueryStateTView2 = (TextView)findViewById(R.id.simcard_querydate2);
        mSimcardMoneyTView2 = (TextView)findViewById(R.id.simcard_money2);

        // 查询余额 布局
        mConfirmLayout = (RelativeLayout)findViewById(R.id.query_confirm);
        mConfirmContentTView = (TextView)findViewById(R.id.query_confirm_content);
        mConfirmWaitTView = (TextView)findViewById(R.id.query_confirm_wait);

        mConfirmLayout.setOnClickListener(this);
        mChargingBtnStr = getResources().getString(R.string.putao_querytel_querying);
    }

    private void initReceiver() {
        boolean isContainCoolpadSms = isContainCoolpadSms();
        // 注册短信接收
        IntentFilter filter = new IntentFilter();
        filter.addAction(OperatorsCommans.SENT_SMS_ACTION);
        filter.addAction(OperatorsCommans.COOLPAD_SENT_SMS_ACTION);        
        if (isContainCoolpadSms) {
            filter.addAction(OperatorsCommans.COOLPAD_RECV_SMS_ACTION);
        }
        filter.setPriority(Integer.MAX_VALUE);
        this.registerReceiver(mReceiver, filter);

        if (!isContainCoolpadSms) {
            // 注册短信数据库 监听
            registerContentObserver();
        }
    }

    /**
     * 初始化界面布局
     */
    private void initLayout() {
        checkSimcard();
        LogUtil.i(TAG, "simcardNUm: " + mSimcardNum);
        if (mSimcardNum == 0) {
            // 没有Sim卡
            mSimcardLayout1.setVisibility(View.VISIBLE);
            findViewById(R.id.simcard_info1).setVisibility(View.GONE);
            showQueryChargeHint(mSimcardQueryStateTView1, QUERY_NO_SIMCARD, "");
            setMoneyText(mSimcardMoneyTView1, "?", false);
            setChargeLayoutDisable(true);
            return;
        } else if (mSimcardNum == 1) {
            setChargeLayoutDisable(false);
            if (mAvaliableSimcard1) {
                mSimcardLayout1.setVisibility(View.VISIBLE);
                showOperatorText(mSimcardOperatorTView1, mSimcardName1);
            } else if (mAvaliableSimcard2) {
                mSimcardLayout2.setVisibility(View.VISIBLE);
                showOperatorText(mSimcardOperatorTView2, mSimcardName2);
            } else {
                // getValidSlotId() 获取当前sim卡所在卡槽 异常，无法识别。
                mSimcardLayout1.setVisibility(View.VISIBLE);
                showQueryChargeHint(mSimcardQueryStateTView1, QUERY_NO_SIMCARD, "");
                setMoneyText(mSimcardMoneyTView1, "?", false);
                setChargeLayoutDisable(true);
            }
        } else if (mSimcardNum == 2) {
            setChargeLayoutDisable(false);
            mSimcardLayout1.setVisibility(View.VISIBLE);
            mSimcardLayout2.setVisibility(View.VISIBLE);

            showOperatorText(mSimcardOperatorTView1, mSimcardName1);
            showOperatorText(mSimcardOperatorTView2, mSimcardName2);
        }

        if (mAvaliableSimcard1) {
            // 读取卡1 上次查询的信息
            BalanceSaveInfo saveInfo1 = SIMCardUtil.loadBalance(this, SIMCardUtil.SIM_CARD_TAG1);
            if (saveInfo1 != null) {
                showQueryChargeHint(mSimcardQueryStateTView1, QUERY_STATE_SHOWDATA,
                        saveInfo1.queryDate);
                setMoneyText(mSimcardMoneyTView1, saveInfo1.queryMoney, true);
            } else {
                showQueryChargeHint(mSimcardQueryStateTView1, QUERY_STATE_WELCOME, "");
                setMoneyText(mSimcardMoneyTView1, "?", true);
            }
        }

        if (mAvaliableSimcard2) {
            // 读取卡2 上次查询的信息
            BalanceSaveInfo saveInfo2 = SIMCardUtil.loadBalance(this, SIMCardUtil.SIM_CARD_TAG2);
            if (saveInfo2 != null) {
                showQueryChargeHint(mSimcardQueryStateTView2, QUERY_STATE_SHOWDATA,
                        saveInfo2.queryDate);
                setMoneyText(mSimcardMoneyTView2, saveInfo2.queryMoney, true);
            } else {
                showQueryChargeHint(mSimcardQueryStateTView2, QUERY_STATE_WELCOME, "");
                setMoneyText(mSimcardMoneyTView2, "?", true);
            }
        }

    }

    /**
     * 检测Sim卡
     */
    private void checkSimcard() {
        if (mPlatformInCoolpad) {
            // 检测酷派手机 可用卡槽
            checkCoolpadSimcard();
        } else {
            // 检测MTK平台手机 可用卡槽
            checkMtkSimCard();
        }
    }

    /**
     * 显示卡槽 对应的运营商
     */
    private void showOperatorText(TextView operatorTView, String simCardName) {
        operatorTView.setText(simCardName);
    }

    /**
     * 设置是否可用
     */
    private void setChargeLayoutDisable(boolean isDisable) {
        if (isDisable) {
            mConfirmLayout.getBackground().setAlpha(80);
            mConfirmLayout.setClickable(false);
        } else {
            // 清除"..."动态加载
            mHandler.removeMessages(MSG_QUERYING_ACTION);
            mConfirmWaitTView.setVisibility(View.GONE);
            mConfirmContentTView.setText(getResources().getString(R.string.putao_querytel_queryhint));

            mConfirmLayout.getBackground().setAlpha(255);
            mConfirmLayout.setClickable(true);
        }
    }

    /**
     * 设置显示价格的颜色状态
     */
    private void setMoneyText(TextView mSimcardMoneyTView, String showText, boolean isNormal) {
        mSimcardMoneyTView.setText(showText);
        if (isNormal) {
            // 绿色
            mSimcardMoneyTView.setTextColor(getResources().getColor(R.color.putao_text_color_importance));
        } else {
            // 灰色
            mSimcardMoneyTView.setTextColor(getResources().getColor(
                    R.color.putao_express_result_no_data_info));
        }
    }

    /**
     * 设置查询提示
     */
    private void showQueryChargeHint(TextView showTView, int state, String queryDate) {
        mQueryState = state;
        switch (mQueryState) {
            case QUERY_STATE_WELCOME:
                // 您尚未进行查询
                showTView.setText(getResources().getString(R.string.putao_querytel_state_welcome));
                break;
            case QUERY_STATE_QUERYING:
                // 正在查询余额，请稍等…
                showTView.setText(getResources().getString(R.string.putao_querytel_state_querying));
                break;
            case QUERY_STATE_FAILED:
                // 短信发送失败，网络异常或欠费
                showTView.setText(getResources().getString(R.string.putao_querytel_state_failed));
                break;
            case QUERY_STATE_SHOWDATA:
                // 最近查询日期：%s
                showTView.setText(String.format(
                        getResources().getString(R.string.putao_querytel_state_showdate), queryDate));
                break;
            case QUERY_STATE_TIMEOUT:
                // 未收到余额短信，请重试
                showTView.setText(getResources().getString(R.string.putao_querytel_state_timeout));
                break;
            case QUERY_STATE_PARSE_ERROR:
                // 短信不能解析，请人工查阅
                showTView.setText(getResources().getString(R.string.putao_querytel_state_parse_error));
                break;
            case QUERY_NO_SIMCARD:
                // 未检测到SIM卡，不能查询余额
                showTView.setText(getResources().getString(R.string.putao_querytel_state_no_simcard));
                break;
            default:
                showTView.setText("");
                break;
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            BalanceSaveInfo balanceSaveInfo = null;
            switch (what) {
                case MSG_QUERYING_ACTION:
                    // 更新“查余额”button状态
                    int computeNum = ++mQueryingComputeNum % 4;
                    String str = "";
                    if (computeNum == 0) {
                        str = "";
                    } else if (computeNum == 1) {
                        str = ".";
                    } else if (computeNum == 2) {
                        str = "..";
                    } else if (computeNum == 3) {
                        str = "...";
                    }
                    mConfirmContentTView.setText(mChargingBtnStr);
                    mConfirmWaitTView.setVisibility(View.VISIBLE);
                    mConfirmWaitTView.setText(str);
                    mHandler.sendEmptyMessageDelayed(MSG_QUERYING_ACTION, 500);
                    break;
                case MSG_CHECK_MESSAGE_RECEIVE_TIMEOUT_ACTION:
                    // 短信超时：从发送短信开始1min后，还未收到查询余额短信
                    mNeedReceiveMsg = false;
                    mHandler.removeMessages(MSG_CHECK_MESSAGE_RECEIVE_TIMEOUT_ACTION);
                    if (!mReceiveSimCard1) {
                        // SIM1 1min内 没收到短信
                        showQueryChargeHint(mSimcardQueryStateTView1, QUERY_STATE_TIMEOUT, "");
                    }
                    if (!mReceiveSimCard2) {
                        // SIM2 1min内 没收到短信
                        showQueryChargeHint(mSimcardQueryStateTView2, QUERY_STATE_TIMEOUT, "");
                    }
                    // "查余额" 可以点击
                    mHandler.sendEmptyMessage(MSG_CLICKABLE_ACTION);
                    break;
                case MSG_CHECK_MESSAGE_PARSE_SUCCESS_ACTION1:
                    // SIM卡1：解析成功
                    balanceSaveInfo = (BalanceSaveInfo)msg.obj;
                    setSimcardRecevieMsg(SIMCardUtil.SIM_CARD_TAG1, true, balanceSaveInfo);
                    break;
                case MSG_CHECK_MESSAGE_PARSE_FAILED_ACTION1:
                    // SIM卡1：解析失败
                    setSimcardRecevieMsg(SIMCardUtil.SIM_CARD_TAG1, false, null);
                    break;
                case MSG_CHECK_MESSAGE_PARSE_SUCCESS_ACTION2:
                    // SIM卡2：解析成功
                    balanceSaveInfo = (BalanceSaveInfo)msg.obj;
                    setSimcardRecevieMsg(SIMCardUtil.SIM_CARD_TAG2, true, balanceSaveInfo);
                    break;
                case MSG_CHECK_MESSAGE_PARSE_FAILED_ACTION2:
                    // SIM卡2：解析失败
                    setSimcardRecevieMsg(SIMCardUtil.SIM_CARD_TAG2, false, null);
                    break;
                case MSG_CHECK_MESSAGE_SEND_FAILED_ACTION1:
                    // SIM卡1：发送失败
                    showQueryChargeHint(mSimcardQueryStateTView1, QUERY_STATE_FAILED, "");
                    break;
                case MSG_CHECK_MESSAGE_SEND_FAILED_ACTION2:
                    // SIM卡2：发送失败
                    showQueryChargeHint(mSimcardQueryStateTView2, QUERY_STATE_FAILED, "");
                    break;
                case MSG_CLICKABLE_ACTION:
                    // "查余额" 按钮可用
                    setChargeLayoutDisable(false);
                    break;
                default:
                    break;
            }
        };
    };

    private void setSimcardRecevieMsg(int simCardTag, boolean isParseSuccess,
            BalanceSaveInfo balanceSaveInfo) {
        if (simCardTag == SIMCardUtil.SIM_CARD_TAG1) {
            // SIM卡1
            if (isParseSuccess) {
                // 解析成功
                showQueryChargeHint(mSimcardQueryStateTView1, QUERY_STATE_SHOWDATA,
                        balanceSaveInfo.queryDate);
                setMoneyText(mSimcardMoneyTView1, balanceSaveInfo.queryMoney, true);
            } else {
                // 解析失败
                showQueryChargeHint(mSimcardQueryStateTView1, QUERY_STATE_PARSE_ERROR, "");
            }
        } else if (simCardTag == SIMCardUtil.SIM_CARD_TAG2) {
            // SIM卡2
            if (isParseSuccess) {
                // 解析成功
                showQueryChargeHint(mSimcardQueryStateTView2, QUERY_STATE_SHOWDATA,
                        balanceSaveInfo.queryDate);
                setMoneyText(mSimcardMoneyTView2, balanceSaveInfo.queryMoney, true);
            } else {
                // 解析失败
                showQueryChargeHint(mSimcardQueryStateTView2, QUERY_STATE_PARSE_ERROR, "");
            }
        }
    }

    @Override
    public void onClick(View view) {
        // TODO Auto-generated method stub
        int viewId = view.getId();
        if (viewId == R.id.query_confirm) {
			mReceiveSimCard1 = false;
			mReceiveSimCard2 = false;
			mNeedReceiveMsg = true;
			mHandler.sendEmptyMessage(MSG_QUERYING_ACTION);
			setChargeLayoutDisable(true);
			if (mAvaliableSimcard1) {
			    // Sim1
			    showQueryChargeHint(mSimcardQueryStateTView1, QUERY_STATE_QUERYING, "");
			}
			if (mAvaliableSimcard2) {
			    // Sim2
			    showQueryChargeHint(mSimcardQueryStateTView2, QUERY_STATE_QUERYING, "");
			}
			// 1min 延时检测
			mHandler.sendEmptyMessageDelayed(MSG_CHECK_MESSAGE_RECEIVE_TIMEOUT_ACTION,
			        SEND_MESSAGE_TIME_GAP);
			// sendQuerySms();
			sendSmsMessage();
		} else if (viewId == R.id.back_layout) {
			this.finish();
		} else {
		}
    }

    /**
     * 发送短信
     */
    private void sendSmsMessage() {
        if (mPlatformInCoolpad) {
            // 酷派手机发送短信
            sendSmsMessageToCoolpad();
        } else {
            // MTK平台手机发送短信
            sendSmsMessageToMtk();
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
    protected void onDestroy() {
        // TODO Auto-generated method stub
        this.unregisterReceiver(mReceiver);
        unregisterContentObserver();
        unbindService();
        super.onDestroy();
    }

    /**
     * 酷派手机发送短信
     */
    private void sendSmsMessageToCoolpad() {
        Intent intent = new Intent("com.yulong.android.contacts.send.message");
        intent.putExtra("requester", "discover_plug");
        sendBroadcast(intent);

    }

    private void sendQuerySms() {
        String phoneNum = mSimUtil.getNativePhoneNumber();
        String providerName = mSimUtil.getProvidersName();
        LogUtil.i(TAG, "phoneNum: " + phoneNum + " ,providerName: " + providerName);

        QueryCommandInfo commandInfo = OperatorsUtils.getInstance().getCommandInfo(providerName);
        boolean isValid = true;
        if (commandInfo == null) {
            isValid = false;
        } else {
            SmsManager smsMgr = SmsManager.getDefault();
            String operatorNum = commandInfo.getNum();
            String smsText = commandInfo.getText();
            if (!TextUtils.isEmpty(operatorNum) && !TextUtils.isEmpty(smsText)) {
                smsMgr.sendTextMessage(operatorNum, null, smsText, PendingIntent.getBroadcast(this,
                        0, new Intent(OperatorsCommans.SENT_SMS_ACTION), 0), null);
            } else {
                isValid = false;
            }
        }
        if (!isValid) {
            Utils.showToast(this, R.string.putao_query_telecharge_failed_hint, false);
        }
    }

    /**
     * 非酷派手机的MTK平台手机发送短信
     */
    private void sendSmsMessageToMtk() {
        if (mAvaliableSimcard1) {
            // 卡1 可用
            LogUtil.i(TAG, "sendToMtk SimCard1 isAvaliable...");
            sendSmsMessageToMtk(1);
        }

        if (mAvaliableSimcard2) {
            // 卡2 可用
            LogUtil.i(TAG, "sendToMtk SimCard2 isAvaliable...");
            sendSmsMessageToMtk(2);
        }
    }

    private void sendSmsMessageToMtk(int simCardId) {
        String imsiSimcard = DualCardUtil.getSimCardImsi(this, simCardId);
        QueryCommandInfo cmdInfo = OperatorsUtils.getInstance().getCommandInfo(imsiSimcard);
        if (cmdInfo != null) {
            LogUtil.i(TAG, "sendToMtk sendMsg simcardId = " + simCardId);
            Intent intent = new Intent(OperatorsCommans.SENT_SMS_ACTION);
            intent.putExtra("SIMCRRD_TAG", simCardId);
            sendMessageInMtkPlayform(cmdInfo.getNum(), null, cmdInfo.getText(), simCardId,
                    PendingIntent.getBroadcast(this, 0, intent, 0), null);
        }
    }

    /**
     * 检测MTK平台手机 可用卡槽
     */
    private void checkMtkSimCard() {
        if (DualCardUtil.isAvaliableWithSimCard1(this)) {
            // 卡1 可用
            mAvaliableSimcard1 = true;
            mSimcardNum++;
            mSimcardName1 = getResources().getString(R.string.putao_querytel_card_name1);
        }

        if (DualCardUtil.isAvaliableWithSimCard2(this)) {
            // 卡2 可用
            mAvaliableSimcard2 = true;
            mSimcardNum++;
            mSimcardName2 = getResources().getString(R.string.putao_querytel_card_name2);
        }
    }

    /**
     * 检测酷派手机 可用卡槽
     */
    private void checkCoolpadSimcard() {
        // 可用卡的数目
        mSimcardNum = getValidCardNumber();

        if (mSimcardNum == 1) {
            // 1 张卡
            int validSlotId = getValidSlotId();
            if (validSlotId == SIMCardUtil.SIM_CARD_TAG1) {
                mAvaliableSimcard1 = true;
            } else if (validSlotId == SIMCardUtil.SIM_CARD_TAG2) {
                mAvaliableSimcard2 = true;
            }
        } else if (mSimcardNum == 2) {
            // 2 张卡
            mAvaliableSimcard1 = true;
            mAvaliableSimcard2 = true;
        }

        mSimcardName1 = getCardDisplayNameFromSlotId(SIMCardUtil.SIM_CARD_TAG1);
        if (TextUtils.isEmpty(mSimcardName1)) {
            mSimcardName1 = getResources().getString(R.string.putao_querytel_card_name1);
        }
        mSimcardName2 = getCardDisplayNameFromSlotId(SIMCardUtil.SIM_CARD_TAG2);
        if (TextUtils.isEmpty(mSimcardName2)) {
            mSimcardName2 = getResources().getString(R.string.putao_querytel_card_name2);
        }

        LogUtil.i(TAG, "checkCoolpadSimcard SimcardNum=" + mSimcardNum + " AvaliableSimcard1="
                + mAvaliableSimcard1 + " SimcardName1=" + mSimcardName1 + " AvaliableSimcard2="
                + mAvaliableSimcard2 + " SimcardName2=" + mSimcardName2);
    }

    /**
     * 本地保存的查询余额的信息
     */
    public static class BalanceSaveInfo {
        public static final int MEMBER_SIZE = 3; // 元素个数

        public String simTag = ""; // sim卡标识

        public String queryDate = ""; // 查询时间

        public String queryMoney = ""; // 查询余额
    }

    private SimpleDateFormat mDateFormatter = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 余额查询短信广播接收器，拦截并解析短信上的余额/金额数 拦截后组织短信传播
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // 接收短信
            String action = intent.getAction();
            LogUtil.d(TAG, "onReceive action=" + action);
            if (OperatorsCommans.SENT_SMS_ACTION.equals(action)) {
                // 短信发送状态 监听
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        //Utils.showToast(context, R.string.putao_query_telecharge_success_hint, false);
                        // 短信发送成功
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        // 发送失败，则该卡本次发送短信已结束
                        //Utils.showToast(context, R.string.putao_query_telecharge_failed_hint, false);
                        int simCardTag = intent.getIntExtra("SIMCRRD_TAG", -1);
                        if (simCardTag == SIMCardUtil.SIM_CARD_TAG1
                                || simCardTag == SIMCardUtil.SIM_CARD_TAG2) {
                            int what = 0;
                            if (simCardTag == SIMCardUtil.SIM_CARD_TAG1) {
                                what = MSG_CHECK_MESSAGE_SEND_FAILED_ACTION1;
                                mReceiveSimCard1 = true;
                            } else {
                                what = MSG_CHECK_MESSAGE_SEND_FAILED_ACTION2;
                                mReceiveSimCard2 = true;
                            }
                            mHandler.sendEmptyMessage(what);
                            checkSimcardTimeoutState();
                        }
                        break;
                    default:
                        break;
                }
            } else if (OperatorsCommans.COOLPAD_SENT_SMS_ACTION.equals(action)) {
                /** 酷派短信发送状态监听，反馈发送结果
                 * resultCode取值：-1：没有发送（无卡时） 0发送失败  1已发送 
                 */
                int resultCodeCard1 = intent.getIntExtra("card1", -1);
                if(resultCodeCard1 == 0) {
                    mReceiveSimCard1 = true;
                    mHandler.sendEmptyMessage(MSG_CHECK_MESSAGE_SEND_FAILED_ACTION1);                    
                }
                int resultCodeCard2 = intent.getIntExtra("card2", -1);
                if(resultCodeCard2 == 0) {
                    mReceiveSimCard2 = true;
                    mHandler.sendEmptyMessage(MSG_CHECK_MESSAGE_SEND_FAILED_ACTION2);                    
                }
                checkSimcardTimeoutState();

            } else if (OperatorsCommans.COOLPAD_RECV_SMS_ACTION.equals(action)) {
                // 酷派 短信接收广播
                if (!mNeedReceiveMsg) {
                    return;
                }
                int phoneId = intent.getIntExtra("phoneid", -1); // phone
                                                                 // id用于标识哪个网络
                String messageUri = intent.getStringExtra("uri");
                String address = intent.getStringExtra("address");
                String smsBody = intent.getStringExtra("body");

                LogUtil.d(TAG, "phoneId=" + phoneId + " uri=" + messageUri + " address=" + address);
                if (!TextUtils.isEmpty(smsBody) && smsBody.length() > 0) {
                    int simCardTag = getSlotIdByPhoneId(phoneId);
                    LogUtil.d(
                            TAG,
                            "simCardTag=" + simCardTag + " smsBody="+smsBody);

                    String receiveTime = mDateFormatter
                            .format(new Date(System.currentTimeMillis()));
                    /**
                     * doReceivedMsg的 smsBody和receiveTime参数顺序错误 cj modified at
                     * 2014/09/05 start
                     */
                    boolean isParseSuccess = doReceivedMsg(simCardTag, smsBody, receiveTime);
                    // cj modified at 2014/09/05 end
                    if (isParseSuccess) {
                        this.abortBroadcast();
                    }
                }
            }
        }

    };

    private boolean doReceivedMsg(int simCardTag, String msgBody, String receiveTime) {
        boolean isParseSuccess = false;
        String moneyStr = "";

        try {
            moneyStr = SIMCardUtil.parseMoneyofSms(0, msgBody);
            isParseSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, "doReceivedMsg parse sms error: " + e.getMessage());
            isParseSuccess = false;
            return isParseSuccess; // 数据库版本解析出错忽略
        }

        if (TextUtils.isEmpty(moneyStr)) {
            isParseSuccess = false;
        }

        LogUtil.i(TAG, "doReceivedMsg  parse moneyStr=" + moneyStr + " isParseSuccess="
                + isParseSuccess);

        BalanceSaveInfo balanceSaveInfo = null;
        balanceSaveInfo = new BalanceSaveInfo();
        balanceSaveInfo.queryDate = receiveTime;
        balanceSaveInfo.queryMoney = moneyStr;
        if (simCardTag != SIMCardUtil.SIM_CARD_TAG_NONE) {
            balanceSaveInfo.simTag = String.valueOf(simCardTag);
        }
        if (simCardTag == SIMCardUtil.SIM_CARD_TAG1 || simCardTag == SIMCardUtil.SIM_CARD_TAG2) {
            // 是SIM卡1 或 SIM卡2

            int what = 0;
            if (simCardTag == SIMCardUtil.SIM_CARD_TAG1) {
                what = MSG_CHECK_MESSAGE_PARSE_SUCCESS_ACTION1;
                mReceiveSimCard1 = true;
            } else {
                what = MSG_CHECK_MESSAGE_PARSE_SUCCESS_ACTION2;
                mReceiveSimCard2 = true;
            }
            // 保存查询的信息
            SIMCardUtil.saveBalance(YellowPageNewQueryChargeActivity.this, balanceSaveInfo,
                    simCardTag);

            if (isParseSuccess) {
                // 解析成功
                Message msg = mHandler.obtainMessage();
                msg.what = what;
                msg.obj = balanceSaveInfo;
                mHandler.sendMessage(msg);
            } else {
                // 解析失败
                mHandler.sendEmptyMessage(simCardTag == SIMCardUtil.SIM_CARD_TAG1 ? MSG_CHECK_MESSAGE_PARSE_FAILED_ACTION1
                        : MSG_CHECK_MESSAGE_PARSE_FAILED_ACTION2);
            }
            checkSimcardTimeoutState();
        }

        return isParseSuccess;
    }

    /**
     * 检测 Sim卡 发送短信后 在1分钟内 是否检测到结果 1、发送失败，则该卡本次发送短信已结束 2、成功且收到短信，则该卡本次发送短信已结束
     */
    private void checkSimcardTimeoutState() {
        if ((mSimcardNum == 1 && (mReceiveSimCard1 || mReceiveSimCard2))
                || (mSimcardNum == 2 && mReceiveSimCard1 && mReceiveSimCard2)) {
            // 一张卡（卡1 或 卡2）、两张卡
            mNeedReceiveMsg = false;
            // 1min 之内收到了短信，则清空超时检测
            mHandler.removeMessages(MSG_CHECK_MESSAGE_RECEIVE_TIMEOUT_ACTION);
            // "查余额" 可以点击
            mHandler.sendEmptyMessage(MSG_CLICKABLE_ACTION);
        }
    }

    /** 获取当前有效的sim卡数目 */
    private int getValidCardNumber() {
        if (getDoubleCardService() != null) {
            try {
                return getDoubleCardService().getValidCardNumber();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    /**
     * 根据接收到短信的广播的intent判断是哪张sim卡接收到的。 返回值的对应关系如下： -1:获取失败 1:卡1 2:卡2
     */
    private int getCardSlot(Intent intent) {
        if (getDoubleCardService() != null) {
            try {
                return getDoubleCardService().getCardSlot(intent);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    /**
     * 根据卡槽id(卡1为1、卡2为2)获取sim卡的显示名称
     */
    private String getCardDisplayNameFromSlotId(int slotId) {
        if (getDoubleCardService() != null) {
            try {
                return getDoubleCardService()
                        .getCardDisplayNameFromSlotId(slotId);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * 根据phone id获取对应的卡槽id
     */
    private int getSlotIdByPhoneId(int phoneId) {
        if (getDoubleCardService() != null) {
            try {
                return getDoubleCardService().getSlotIdByPhoneId(phoneId);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    /**
     * 获取当前sim卡所在卡槽的id（在getValidCardNumber方法返回1时调用才正确，否则返回-1）
     */
    private int getValidSlotId() {
        if (getDoubleCardService() != null) {
            try {
                return getDoubleCardService().getValidSlotId();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    private void registerContentObserver() {
        // 在这里启动
        mResolver = getContentResolver();
        mObserver = new SmsObserver(mHandler);
        mResolver.registerContentObserver(Uri.parse("content://sms"), true, mObserver);
    }

    private void unregisterContentObserver() {
        if (mObserver != null) {
            this.getContentResolver().unregisterContentObserver(mObserver);
        }
    }

    private ContentResolver mResolver = null;

    private SmsObserver mObserver = null;

    public class SmsObserver extends ContentObserver {

        public SmsObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            LogUtil.i(TAG, "SmsObserver onChange selfChange=" + selfChange + " mNeedReceiveMsg="
                    + mNeedReceiveMsg);
            if (!mNeedReceiveMsg) {
                return;
            }
            try {
                refreshDatabase();
            } catch (Exception e) {
                LogUtil.e(TAG, e.getMessage());
                e.printStackTrace();
            }
        }

        /*
         * 返回卡槽 -1 错误 1 - 卡1 2 - 卡2
         */
        private int querySlotByItemInfo(long item_id) {
            String[] projection = {
                " network_type from itemInfo where _id=" + item_id + "; --"
            };
            Cursor cursor = null;
            try {
                cursor = getContentResolver().query(Uri.parse("content://mms/"), projection, null,
                        null, null);
                if (cursor != null) {
                    if (cursor.moveToNext()) {
                        int network_type = cursor.getInt(cursor.getColumnIndex("network_type"));
                        LogUtil.d(TAG, "querySlotByItemInfo item_id=" + item_id + " slot="
                                + network_type);
                        return network_type;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null)
                    cursor.close();
            }

            return -1;
        }

        /**
         * 查询sms数据库，找出最新未读信息，逐个解析
         * 
         * @throws Exception
         */
        private void refreshDatabase() throws Exception {
            String[] projection = {
                    "_id", "body", "address", "date", "itemInfoid"
            };
            String where = " read = 0 and address in ('10010','10086','10001')";
            Cursor cursor = null;
            try {
                cursor = getContentResolver().query(Uri.parse("content://sms"), projection, where,
                        null, "_id desc limit 3");
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        long _id = cursor.getLong(0);
                        String body = cursor.getString(1);
                        String address = cursor.getString(2);
                        long date = cursor.getLong(3);
                        long itemInfoid = cursor.getLong(4);

                        int simCardSlot = querySlotByItemInfo(itemInfoid);
                        String receiveTime = mDateFormatter.format(new Date(date));

                        LogUtil.i(TAG, "querySms sms_id=" + _id + " ,itemInfoid=" + itemInfoid
                                + " ,simCardSlot=" + simCardSlot + " ,address=" + address
                                + " ,date=" + receiveTime + " ,body=" + body);

                        if (simCardSlot != SIMCardUtil.SIM_CARD_TAG_NONE) {
                            boolean isParseSuccess = doReceivedMsg(simCardSlot, body, receiveTime);
                            if (isParseSuccess) {
                                setReadAndSeenBySmsMsgId(_id);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e(TAG, e.getMessage());
            } finally {
                if (cursor != null)
                    cursor.close();
            }
        }

        /**
         * 根据短信id将该短信修改为已读
         * 
         * @param _id
         */
        public void setReadAndSeenBySmsMsgId(long _id) {
            String whereStr = "( read = 0 ) and _id = " + _id;
            ContentValues values = new ContentValues();
            values.put("read", 1);
            getContentResolver().update(Uri.parse("content://sms"), values, whereStr, null);
        }
    };

    /**
     * 检测短信数据库的sms表 是否有itemInfoid字段 [true]: 集成了酷派内置短信 [false]: 没有集成了酷派内置短信
     */
    private boolean isContainCoolpadSms() {
        boolean isCoolpadSms = false;
        // 查找数据库sms表是否含有itemInfoid字段
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(Uri.parse("content://sms"), null, null, null, null);
            if (cursor != null && cursor.getColumnIndex("itemInfoid") != -1) {
                isCoolpadSms = true;
            }
        } catch (Exception e) {
            isCoolpadSms = false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return isCoolpadSms;
    }

    /**
     * MTK平台发送短信
     * 
     * @param destAddr
     * @param scAddr
     * @param text
     * @param slotId
     * @param sentIntent
     * @param deliveryIntent
     */
    public void sendMessageInMtkPlayform(String destAddr, String scAddr, String text, int slotId,
            PendingIntent sentIntent, PendingIntent deliveryIntent) {
        try {
            String isms = getSmsServiceName(slotId);
            Class<?> serviceManagerCls = Class.forName("android.os.ServiceManager");
            Method getServiceMethod = serviceManagerCls.getMethod("getService", String.class);
            IBinder serverceIBinder = (IBinder)getServiceMethod.invoke(serviceManagerCls, isms);

            // 获得ISms.Stub类
            Class<?> cStub = Class.forName("com.android.internal.telephony.ISms$Stub");
            // 获得asInterface方法
            Method asInterface = cStub.getMethod("asInterface", android.os.IBinder.class);
            // 获取ISms 对象
            Object iccIsms = asInterface.invoke(cStub, serverceIBinder);

            Method sendTextMethod = cStub.getMethod("sendText", String.class, String.class,
                    String.class, String.class, PendingIntent.class, PendingIntent.class);
            sendTextMethod.invoke(iccIsms, "com.yulong.android.contacts.discover", destAddr,
                    scAddr, text, sentIntent, deliveryIntent);

        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static String getSmsServiceName(int slotId) {
        if (slotId == 1) {
            // PhoneConstants.GEMINI_SIM_1
            return "isms";
        } else if (slotId == 2) {
            // PhoneConstants.GEMINI_SIM_2
            return "isms2";
        } else if (slotId == 3) {
            // PhoneConstants.GEMINI_SIM_3
            return "isms3";
        } else if (slotId == 4) {
            // PhoneConstants.GEMINI_SIM_4
            return "isms4";
        } else {
            return null;
        }
    }

    @Override
    public Integer remindCode() {
        return mRemindCode;
    }
    
    @Override
    public String getServiceNameByUrl() {
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
	public Integer getAdId() {
	    return AdCode.ADCODE_YellowPageNewQueryChargeActivity;
	}
}
