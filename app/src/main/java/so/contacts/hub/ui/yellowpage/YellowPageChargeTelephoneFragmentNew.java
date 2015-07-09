
package so.contacts.hub.ui.yellowpage;

import so.contacts.hub.charge.ChargeConst;

import so.contacts.hub.http.bean.ChargeTelephoneProductResponseBean;

import so.contacts.hub.http.bean.PhoneFeeProductInfo;

import so.contacts.hub.payment.data.ResultCode;

import so.contacts.hub.payment.GetOrderParam;
import so.contacts.hub.payment.data.ProductTypeCode;

import so.contacts.hub.payment.ui.PaymentActionView;

import so.contacts.hub.payment.PaymentCallback;
import so.contacts.hub.payment.PaymentViewGroup.OnPaymentActionSelectedListener;

import so.contacts.hub.payment.PaymentViewGroup;

import so.contacts.hub.payment.core.PaymentDesc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import so.contacts.hub.account.IAccCallback;
import so.contacts.hub.account.PutaoAccount;
import so.contacts.hub.ad.AdCode;
import so.contacts.hub.adapter.ChargeTelAdapter;
import so.contacts.hub.adapter.ChargeTelAdapter.onDeleteButtonClickListener;
import so.contacts.hub.charge.ChargeUtils;
import so.contacts.hub.core.ConstantsParameter;
import so.contacts.hub.remind.BaseRemindFragment;
import so.contacts.hub.ui.yellowpage.bean.ChargeHistoryItem;
import so.contacts.hub.ui.yellowpage.bean.HabitDataItem;
import so.contacts.hub.ui.yellowpage.bean.YellowParams;
import so.contacts.hub.util.ContactsDBImpl;
import so.contacts.hub.util.ContactsHubUtils;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.MyCenterConstant;
import so.contacts.hub.util.NetUtil;
import so.contacts.hub.util.TelAreaUtil;
import so.contacts.hub.util.UMengEventIds;
import so.contacts.hub.util.UserInfoUtil;
import so.contacts.hub.util.Utils;
import so.contacts.hub.util.YellowUtil;
import so.contacts.hub.widget.CommonDialog;
import so.contacts.hub.widget.CommonDialogFactory;
import so.contacts.hub.yellow.data.Voucher;
import so.contacts.hub.yellow.data.Voucher.VoucherScope;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Contacts.People.Phones;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yulong.android.contacts.discover.R;

public class YellowPageChargeTelephoneFragmentNew extends BaseRemindFragment implements
        OnClickListener, IAccCallback, PaymentCallback, OnPaymentActionSelectedListener {

    private static final String TAG = "YellowPageChargeTelephoneFragment";

    private EditText mNumberEditText = null;

    private TextView mNameTView = null;

    private ImageView mClearInput = null;

    private GridView mMoneyGridView = null;

    private TextView mOperatorTView = null;

    private RelativeLayout mChargeLayout = null;

    private TextView mChargeContentTView = null;

    private TextView mChargeWaitTView = null;

    private LinearLayout mHistoryLayout = null;

    private ListView mHistoryListView = null;

    private ArrayList<ChargeHistoryItem> mHistoryList = new ArrayList<ChargeHistoryItem>();

    private ChargeTelAdapter mHistoryAdapter = null;

    private ChargeAdapter mChargeAdapter = null;

    private String hasAskedPhone = ""; // 上次被询价的号码

    // 第一次初始化
    private boolean isFirstInit = true;

    // 话费价格列表中默认价格（第二个位置：50元）
    private static final int DEFAULT_SELECT_POS = 1;

    /**
     * select pos in charge list
     */
    private int mSelectPos = DEFAULT_SELECT_POS;

    private List<PhoneFeeProductInfo> chargeItems = new ArrayList<PhoneFeeProductInfo>();

    private List<PhoneFeeProductInfo> availableChargeItems = new ArrayList<PhoneFeeProductInfo>();

    private SharedPreferences mChargHistorySPref = null;

    private InputMethodManager mInputManager = null;

    // result code which get contacts phone num
    public static final int REQUEST_CONTACT_INFO = 0x1001;

    public static final int MSG_SHOW_SELECT_CONTACT_PHONE_ACTION = 0x2001;

    public static final int MSG_SHOW_INPUT_NUM_HINT_ACTION = 0x2002;

    public static final int MSG_NO_SERVER_DATA_ACTION = 0x2003;

    public static final int MSG_ASKMOBILEPRICE_EXCEPTION_ACTION = 0x2004;

    public static final int MSG_QUERYING_ACTION = 0x2005;

    public static final int MSG_LOGIN_SUCCESS_HINT = 0x2006;

    public static final int MSG_LOGIN_FAIL_HINT = 0x2007;

    private static final String CHARGE_TELEPHONE_HISTORY = "charge_tel_history";

    private static final String CHARGE_TELEPHONE_KEY = "charge_tel_history_key";

    private static final int SAVE_CHARGE_TEL_NUM = 3;

    private static final int VALID_PHONENUM_SIZE = 11;

    private AsyncTask<String, Void, Boolean> mAskTask = null;

    // 电话 - 联系人 缓存
    private HashMap<String, String> mContactPhoneMap = new HashMap<String, String>();

    // 更新显示价格状态标识：显示价格范围
    private static final int PRICE_STATE_PRICERANGE = 1;

    // 更新显示价格状态标识：显示实际价格
    private static final int PRICE_STATE_PRICE = 2;

    // 更新显示价格状态标识：暂不提供对该号码进行充值
    private static final int PRICE_STATE_NODATA = 3;

    // 更新号码状态标识：显示归属地
    private static final int PHONE_STATE_OPERATOR = 1;

    // 更新号码状态标识：手机号码有误
    private static final int PHONE_STATE_ERROR = 2;

    // 更新号码状态标识：空
    private static final int PHONE_STATE_SPACE = 3;

    // 友盟统计：进入时间
    private long startTime = 0L;

    // 网络状态变化action
    private static final String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

    // “立即充值” 按钮打点 计数
    private int mQueryingComputeNum = 0;

    // “充值中” 字符串
    private String mChargingBtnStr = "";

    private Context mContext = null;

    private Activity mActivity = null;

    private View mContentView = null;

    private int mRemindCode = -1;

    private int payActionType = PaymentDesc.ID_WE_CHAT;

    public static final int PAY_ALIPAY = 1;// 支付宝

    public static final int PAY_WECHAT = 2;// 微信支付（财付通）

    private List<Voucher> couponList = null; // 用户优惠券信息

    private TextView choiceDiscountCouponText = null;// add by hyl 2014-10-19
                                                     // 优惠券显示控件

    private float couponPrice = 0;// 优惠券的价格


    private Voucher choice_voucher = null;// 用户选择适用的优惠券

    // //add by hyl 2014-10-19 end

    /**
     * 是否需要初始化优惠券
     */
    private boolean mNeedInitCouponData = true;

    // 设置字体颜色值
    private int mNormalColor = 0;


    private int mGreenColor = 0;

    // 是否正在充值状态中
    private boolean mTelephoneCharging = false;

    private PaymentViewGroup mPaymentLayout;

    private String operatorInfo = "";// 上次充流量的号码归属地

    private String localPhoneNum="";//本机号码
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.putao_yellow_page_charge_new, null);
        return mContentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = this.getActivity().getBaseContext();
        mActivity = this.getActivity();

        mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mChargHistorySPref = mActivity.getSharedPreferences(CHARGE_TELEPHONE_HISTORY,
                Activity.MODE_MULTI_PROCESS);

        Intent intent = mActivity.getIntent();
        if (intent != null) {
            YellowParams params = (YellowParams)intent
                    .getSerializableExtra(YellowUtil.TargetIntentParams);
            if (params != null) {
                mRemindCode = params.getRemindCode();
            } else {
                mRemindCode = intent.getIntExtra("RemindCode", -1);
            }
        }
        initViews();
        initData();
        initShowPhoneNum();

        mActivity.registerReceiver(mNetworkReceiver, new IntentFilter(CONNECTIVITY_CHANGE_ACTION));
    }

    @Override
    public void onResume() {
        LogUtil.i(TAG, "onResume test... ");
        if (TextUtils.isEmpty(hasAskedPhone)) {
            setChargeLayoutDisable(true);
        } else {
            setChargeLayoutDisable(false);
        }

        startTime = System.currentTimeMillis();

        clearChargeBtnMessage();
        if (mNeedInitCouponData) {
            mNeedInitCouponData = false;
            initCouponData();
        }
        super.onResume();
    }

    private void initViews() {
        mPaymentLayout = (PaymentViewGroup)mContentView.findViewById(R.id.charge_payment_layout);
        mPaymentLayout.selectPayAction(payActionType);
        mPaymentLayout.setOnPaymentActionSelectedListener(this);
        mPaymentLayout.setPaymentCallback(this);
        mNumberEditText = (EditText)mContentView.findViewById(R.id.charge_edit);
        mNameTView = (TextView)mContentView.findViewById(R.id.charge_name);
        mMoneyGridView = (GridView)mContentView.findViewById(R.id.charge_gridview);

        mChargeLayout = (RelativeLayout)mContentView.findViewById(R.id.charge_confirm);
        mChargeLayout.setOnClickListener(this);
        mChargeContentTView = (TextView)mContentView.findViewById(R.id.charge_confirm_content);
        mChargeWaitTView = (TextView)mContentView.findViewById(R.id.charge_confirm_wait);
        choiceDiscountCouponText = (TextView)mContentView
                .findViewById(R.id.choice_discount_coupon_text);
        choiceDiscountCouponText.setOnClickListener(this);
        mClearInput = (ImageView)mContentView.findViewById(R.id.clear_search);
        mOperatorTView = (TextView)mContentView.findViewById(R.id.operator_text);
        mHistoryLayout = (LinearLayout)mContentView.findViewById(R.id.chargehistory_layout);
        mHistoryListView = (ListView)mContentView.findViewById(R.id.chargehistory_list);
        mHistoryListView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showInputManager(false);
                return false;
            }
        });
        mHistoryAdapter = new ChargeTelAdapter(mContext, mHistoryList);
        mHistoryAdapter.setOnDeleteButtonClickListener(new onDeleteButtonClickListener() {
            @Override
            public void onDeleteButtonClicked(int position, String words) {
                clearPhoneNumHistory(words, true);
            }
        });
        mHistoryListView.setAdapter(mHistoryAdapter);
        mHistoryListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
                ChargeHistoryItem historyItem = (ChargeHistoryItem)view.getTag();
                String phoneNum = historyItem.getPhoneNum();
                if (TextUtils.isEmpty(phoneNum)) {
                    return;
                }
                String editText = mNumberEditText.getText().toString();
                editText=editText.replace(" ", "");
                mHistoryLayout.setVisibility(View.GONE);
                if (phoneNum.equals(editText)) {
                    return;
                }
                showNameData("");
                setPhoneNum(phoneNum);
                updateContactName(phoneNum);
                MobclickAgentUtil.onEvent(mContext,
                        UMengEventIds.DISCOVER_YELLOWPAGE_CHARGE_SELECT_HISTORY);
            }
        });

        mNumberEditText.setOnClickListener(this);
        // mChargeLayout.setOnClickListener(this);
        mNameTView.setOnClickListener(this);
        mClearInput.setOnClickListener(this);

        mNumberEditText.setOnClickListener(this);
        mNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                LogUtil.d(TAG, "onTextChanged");
                String contents = arg0.toString();
                Utils.setEditPhoneNumFormat(contents, mNumberEditText);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                LogUtil.d(TAG, "onTextChanged");
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                if (TextUtils.isEmpty(arg0.toString())) {
                    mClearInput.setImageResource(R.drawable.putao_icon_contacts);
                    showInputManager(true);
                    showNameData("");
                    showPhoneNumData(PHONE_STATE_SPACE, "");
                    refreshChargeHistoryLayout();
                    showPriceData(PRICE_STATE_PRICERANGE);
                    setChargeLayoutDisable(true);
                    initChargePriceData();
                } else {
                    mClearInput.setImageResource(R.drawable.putao_icon_clear_s);
                    mHistoryLayout.setVisibility(View.GONE);
                    checkAndRequestPrice(arg0.toString());
                }
            }
        });
        mMoneyGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                if (mTelephoneCharging) {
                    return;
                }
                mSelectPos = arg2;
                mHistoryLayout.setVisibility(View.GONE);
                showInputManager(false);
                mChargeAdapter.notifyDataSetChanged();
                checkAndRequestPrice(mNumberEditText.getText().toString());
            }
        });

        TextView question = (TextView)mContentView.findViewById(R.id.question);
        question.setOnClickListener(this);

        mChargingBtnStr = mContext.getResources().getString(R.string.putao_charge_charging);
    }
    private void setPhoneNum(String phoneNum){
        String newPhone=Utils.formatPhoneNum(phoneNum);
        mNumberEditText.setText(newPhone);
        mNumberEditText.setSelection(newPhone.length());
        setLocalPhoneNumName();
    }
    private void initData() {
        isFirstInit = true;
        mSelectPos = DEFAULT_SELECT_POS;

        initChargePriceData();
        mNumberEditText.setText("");

        mNormalColor = mContext.getResources().getColor(
                R.color.putao_contents_text);
        mGreenColor = mContext.getResources().getColor(
                R.color.putao_light_green);
        LoadVoucherData task=new LoadVoucherData();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
    }

    /**
     * 主动异步拉取优惠券
     * @author xcx
     *
     */
    private class LoadVoucherData extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            UserInfoUtil.getInstace().updateUserCouponData();
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            if(isDetached()){
                return;
            }
            initCouponData();
            super.onPostExecute(result);
        }
    }
    private void initShowPhoneNum() {
        // putao_lhq modify for API start
        // String phoneNum = ContactsHubUtils.getPhoneNumber(mContext);
        String phoneNum = ((YellowPageReChargeActivity)mActivity).phoneNumber;
        if (TextUtils.isEmpty(phoneNum)) {
            phoneNum = PutaoAccount.getInstance().getBindMobile();
        }
        if (TextUtils.isEmpty(phoneNum)) {
            phoneNum = ContactsHubUtils.getPhoneNumber(mContext);
        }
        // putao_lhq modify for API end
        if ((!TextUtils.isEmpty(phoneNum) && phoneNum.length() < VALID_PHONENUM_SIZE)
                || TextUtils.isEmpty(phoneNum)) {
            phoneNum = "";
            mHistoryList = getPhoneNumHistoryList();
            if (mHistoryList == null) {
                return;
            }
            int len = mHistoryList.size();
            if (len > 0) {
                phoneNum = mHistoryList.get(len - 1).getPhoneNum();
            }
        }
        if (!TextUtils.isEmpty(phoneNum)) {
            setPhoneNum(phoneNum);
            checkAndRequestPrice(phoneNum);
        }
    }

    /**
     * 初始化优惠券信息
     */
    private void initCouponData() {
        couponList = UserInfoUtil.getInstace().getAvailableVouchers(VoucherScope.Huafei);
        if (couponList != null && couponList.size() > 0) {
            LogUtil.i(TAG, "initCouponData coupon size: " + couponList.size());
            // 注: 默认显示第一张代金券
            choice_voucher = couponList.get(0);
            couponPrice = Float.valueOf(choice_voucher.money);
            showCouponText(true, couponPrice);
        } else {
            LogUtil.i(TAG, "initCouponData coupon is null or 0.");
            couponList = null;
            couponPrice = 0;
            choice_voucher = null;
            showCouponText(false, couponPrice);
        }
        showPriceData(PRICE_STATE_PRICE);
    }

    private void initChargePriceData() {
        if(chargeItems.size()<5){
            chargeItems.clear();
            PhoneFeeProductInfo item1=new PhoneFeeProductInfo();
            item1.setProd_id(0);
            item1.setProd_content("30");
            item1.setAl_price("29.5 ~ 30");
            item1.setWx_price("29.5 ~ 30");
            chargeItems.add(item1);
            
            PhoneFeeProductInfo item2=new PhoneFeeProductInfo();
            item2.setProd_id(0);
            item2.setProd_content("50");
            item2.setAl_price("49.5 ~ 50");
            item2.setWx_price("49.5 ~ 50");
            chargeItems.add(item2);
            
            PhoneFeeProductInfo item3=new PhoneFeeProductInfo();
            item3.setProd_id(0);
            item3.setProd_content("100");
            item3.setAl_price("97.5 ~ 100");
            item3.setWx_price("97.5 ~ 100");
            chargeItems.add(item3);
            
            PhoneFeeProductInfo item4=new PhoneFeeProductInfo();
            item4.setProd_id(0);
            item4.setProd_content("200");
            item4.setAl_price("195 ~ 200");
            item4.setWx_price("195 ~ 200");
            chargeItems.add(item4);
            
            PhoneFeeProductInfo item5=new PhoneFeeProductInfo();
            item5.setProd_id(0);
            item5.setProd_content("300");
            item5.setAl_price("295 ~ 300");
            item5.setWx_price("295 ~ 300");
            chargeItems.add(item5);
        }
        availableChargeItems.clear();
        availableChargeItems.addAll(chargeItems);
        

        if (mChargeAdapter == null) {
            mChargeAdapter = new ChargeAdapter();
            mMoneyGridView.setAdapter(mChargeAdapter);
        }
        mChargeAdapter.notifyDataSetChanged();
        hasAskedPhone = "";
        showPriceData(PRICE_STATE_PRICERANGE);
    }

    private BroadcastReceiver mNetworkReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (CONNECTIVITY_CHANGE_ACTION.equals(action)) {
                LogUtil.i(TAG, "network is changed!");
                // 如果是从无网络到有网络变化 且 之前没有询价的结果
                if (NetUtil.isNetworkAvailable(mContext) && TextUtils.isEmpty(hasAskedPhone)) {
                    LogUtil.i(TAG, "network is changed and need request phone price...");
                    checkAndRequestPrice(mNumberEditText.getText().toString());
                }
            }
        }

    };

    /**
     * 确认号码是否要询价 并更新 价格状态、号码归属地显示 ，并做网络检测
     * 
     * @param phoneNum
     * @return
     */
    private String checkAskedPhone(String phoneNum) {
        // 如果号码为空或号码长度小于11（为不完整号码） 则显示价格范围，不用询价
        if (TextUtils.isEmpty(phoneNum) || phoneNum.length() < VALID_PHONENUM_SIZE) {
            if (!TextUtils.isEmpty(hasAskedPhone) && hasAskedPhone.length() == VALID_PHONENUM_SIZE
                    && (hasAskedPhone.substring(0, VALID_PHONENUM_SIZE - 1).equals(phoneNum))) {
                // 上一次查询到价格后，删除号码到第10位时，则清空价格列表
                initChargePriceData();
            } else {
                showPriceData(PRICE_STATE_PRICERANGE);
            }
            setChargeLayoutDisable(true);
            showNameData("");
            showPhoneNumData(PHONE_STATE_SPACE, "");
            return "";
        }

        // 检测是否是合法的号码
        if (!TelAreaUtil.getInstance().isValidMobile(phoneNum)) {
            // 号码不合法
            showNameData("");
            showPhoneNumData(PHONE_STATE_ERROR, "");
            return "";
        }

        // 过滤号码头
        // String phoneNumTemp = ContactsHubUtils.formatIPNumber(phoneNum,
        // this);

        // 输入合法的号码后隐藏输入法
        showInputManager(false);

        // 显示号码归属地信息
        String operatorInfo = getOperatorInfo(phoneNum);
        if (!TextUtils.isEmpty(operatorInfo)) {
            showPhoneNumData(PHONE_STATE_OPERATOR, operatorInfo);
        }

        // 检测并显示联系人信息
        updateContactName(phoneNum);

        // 检测网络
        if (!NetUtil.isNetworkAvailable(mContext)) {
            // 没有网络则初始化价格列表
            initChargePriceData();
            setChargeLayoutDisable(true);
            Utils.showToast(mContext, R.string.putao_no_net, false);
            return "";
        }

        if (notNeedQueryPrice(phoneNum)) {
            // （非第一次查询 且该次查询的号码与上一次的号码相同）则 不用询价
            showPriceData(PRICE_STATE_PRICE);
            return "";
        } else {
            // 需要询价
            showPriceData(PRICE_STATE_PRICERANGE);
            setChargeLayoutDisable(true);
            return phoneNum;
        }
    }

    /**
     * 检测号码并请求价格
     */
    private void checkAndRequestPrice(String phoneNum) {
        phoneNum=phoneNum.replace(" ", ""); 
        String validPhoneNum = checkAskedPhone(phoneNum);
        if (TextUtils.isEmpty(validPhoneNum)) {
            return;
        }
        operatorInfo = getOperatorInfo(phoneNum);
        if (!TextUtils.isEmpty(operatorInfo)) {
            showPhoneNumData(PHONE_STATE_OPERATOR, operatorInfo);
        }
        // 询价任务
        if (mAskTask == null || mAskTask != null
                && mAskTask.getStatus() != AsyncTask.Status.RUNNING) {
            mAskTask = new AskMobilePriceTask();
//            mAskTask.execute(validPhoneNum,operatorInfo);
            mAskTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, validPhoneNum,operatorInfo);
        }
    }

    private boolean notNeedQueryPrice(String phoneNum) {
        boolean isEmpty = TextUtils.isEmpty(hasAskedPhone);

        if (!isEmpty
                && phoneNum.substring(0, VALID_PHONENUM_SIZE).equals(
                        hasAskedPhone.substring(0, VALID_PHONENUM_SIZE))) {
            // 上次查询的价格不为空 并且与这次号码相同
            return true;
        }
        return false;
    }

    /**
     * 显示联系人信息
     */
    private void updateContactName(String phoneNum) {
        String name = mNameTView.getText().toString();
        if (TextUtils.isEmpty(name)) {
            name = mContactPhoneMap.get(phoneNum);
            if (TextUtils.isEmpty(name)) {
                new QueryContactName(phoneNum).execute();
            } else {
                showNameData(name);
            }
        }
    }

    /**
     * 异步查询联系人姓名
     */
    private class QueryContactName extends AsyncTask<String, Void, String> {

        private String phoneNum = "";

        public QueryContactName(String phone) {
            phoneNum = phone;
        }

        @Override
        protected String doInBackground(String... arg0) {
            if (TextUtils.isEmpty(phoneNum)) {
                return null;
            }
            return ContactsDBImpl.getInstance().lookUpNumber(mContext, phoneNum);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // 将查询结果缓存起来
            if (!TextUtils.isEmpty(result)) {
                mContactPhoneMap.put(phoneNum, result);
            }
            // 判断查询的号码 是否与 输入框中的号码匹配，若不匹配直接返回
            String num=mNumberEditText.getText().toString();
            num=num.replace(" ", "");
            if (!phoneNum.equals(mNumberEditText.getText().toString())) {
                return;
            }

            showNameData(result);
        }

    }

    /**
     * 电话费询价任务
     */
    private class AskMobilePriceTask extends AsyncTask<String, Void, Boolean> {

        String mobile = "";

        List<PhoneFeeProductInfo> beanList = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... args) {
            LogUtil.d(TAG, "doInBackground");
            mobile = args[0];
            operatorInfo = args[1];
            try {
                ChargeTelephoneProductResponseBean product = ChargeUtils.qryChargeTelephonePrice(
                        mobile, operatorInfo);
                if(null==product){
                    return false;
                }
                operatorInfo = product.getAcc_type();
                beanList = product.getList();
            } catch (Exception e) {
                LogUtil.d(TAG, "AskMobilePriceTask query mobile price exception...");
                e.printStackTrace();
                mHandler.sendEmptyMessage(MSG_ASKMOBILEPRICE_EXCEPTION_ACTION);
                return false;
            }

            if (beanList == null || beanList.size() <= 0) {
                LogUtil.d(TAG, "AskMobilePriceTask qryMobilePrice size is null or 0.");
                mHandler.sendEmptyMessage(MSG_NO_SERVER_DATA_ACTION);
                return false;
            }
            // 把最新数据按从小到大排序
            Collections.sort(beanList, new Comparator<PhoneFeeProductInfo>() {
                @Override
                public int compare(PhoneFeeProductInfo arg0, PhoneFeeProductInfo arg1) {
                    if((Float.parseFloat(arg0.getAl_price()) - Float.parseFloat(arg1.getAl_price()))<0){
                        return -1;
                    }else{
                        return 1;
                    }
                }
            });

            LogUtil.d(TAG, "AskMobilePriceTask qryMobilePrice size=" + beanList.size());
            return true;
        }

        @Override
        protected void onPostExecute(Boolean founded) {
            if(isDetached()){
                return;
            }
            if (TextUtils.isEmpty(mNumberEditText.getText().toString())) {
                return;
            }
            if (founded) {
                hasAskedPhone = ContactsHubUtils.formatIPNumber(mobile, mContext);

                // 在最新价格列表里找到已选择的价格所在位置
                boolean notFindSelectPos = false;
                PhoneFeeProductInfo choiceProduct = availableChargeItems.get(mSelectPos);
                availableChargeItems.clear();
                for (int i = 0; i < chargeItems.size(); i++) {
                    PhoneFeeProductInfo oldProduct = chargeItems.get(i);
                    PhoneFeeProductInfo newProduct = null;

                    if ((newProduct = findMarkPrice(beanList, oldProduct.getProd_content())) != null) {
                        availableChargeItems.add(newProduct);
                    }
                }

                for (PhoneFeeProductInfo info : availableChargeItems) {
                    if (choiceProduct.getProd_content().equals(info.getProd_content())) {
                        notFindSelectPos = true;
                    }
                }

                if (!notFindSelectPos) {

                    for (int i = 0; i < availableChargeItems.size(); i++) {

                        PhoneFeeProductInfo info = availableChargeItems.get(i);
                        if (info.getProd_content().compareTo(choiceProduct.getProd_content()) < 0) {
                            mSelectPos = i;
                        } else {
                            mSelectPos = i;
                            break;
                        }
                    }
                }
                showPriceData(PRICE_STATE_PRICE);
            } else {
                // 没有询价结果或网络繁忙
                setChargeLayoutDisable(true);
            }
            LogUtil.d(TAG, "onPostExecute ");
            mChargeAdapter.notifyDataSetChanged();

            super.onPostExecute(founded);
        }

        PhoneFeeProductInfo findMarkPrice(final List<PhoneFeeProductInfo> list, String prod_content) {
            if (TextUtils.isEmpty(prod_content))
                return null;

            for (int i = 0; i < list.size(); i++) {
                PhoneFeeProductInfo product = list.get(i);
                if (prod_content.equals(product.getProd_content())) {
                    return product;
                }
            }

            return null;
        }
    };

    /**
     * 显示充值实际价格值
     */
    private void showPriceData(int priceState) {
        switch (priceState) {
            case PRICE_STATE_PRICERANGE:
                selectPayChannel(getFitPriceRange());
                break;
            case PRICE_STATE_PRICE:
                // 显示实际价
                selectPayChannel();
                break;
            case PRICE_STATE_NODATA:
                clearPayPriceMessage();
                setChargeLayoutDisable(true);
                break;
            default:
                break;
        }
    }

    private String getFitPriceRange(){
        String content=availableChargeItems.get(mSelectPos).getProd_content();
        for(PhoneFeeProductInfo info:chargeItems){
            if(info.getProd_content().equals(content)){
                return info.getAl_price();
            }
        }
        return "";
    }
    /**
     * 清除“支付方式”按钮 信息
     */
    private void clearPayPriceMessage() {
        for (int i = 0, max = PaymentDesc.ALL_PAY_ACTS.length; i < max; i++) {
            mPaymentLayout.setAmountText(PaymentDesc.ALL_PAY_ACTS[i],
                    getString(R.string.putao_charge_getchargeinfo_failed));
        }
    }

    /**
     * 选择不同的支付渠道
     */
    private void selectPayChannel(String price) {
        for (int i = 0, max = PaymentDesc.ALL_PAY_ACTS.length; i < max; i++) {
            mPaymentLayout
                    .setAmountText(PaymentDesc.ALL_PAY_ACTS[i], String.format(mContext.getResources()
                            .getString(R.string.putao_pay_charge_price), price));
        }
    }

    /**
     * 选择不同的支付渠道
     */
    private void selectPayChannel() {
        if (TextUtils.isEmpty(hasAskedPhone)) {
            return;
        }

        boolean canPaySelectChannel = false;
        boolean canPayAli = false;
        boolean canPayWechat = false;
        PhoneFeeProductInfo info = availableChargeItems.get(mSelectPos);

        String aliPriceStr = info.getAl_price();
        if (!TextUtils.isEmpty(aliPriceStr)) {
            float aliPriceNum = Float.valueOf(aliPriceStr);
            if (aliPriceNum > 0) {
                canPayAli = true;
                mPaymentLayout.setAmountText(PaymentDesc.ID_ALIPAY, String.format(mContext.getResources()
                        .getString(R.string.putao_pay_charge_price), String.format("%.2f",
                        aliPriceNum)));
            }
        }

        String wxPriceStr = info.getWx_price();
        if (!TextUtils.isEmpty(wxPriceStr)) {
            float wxPriceNum = Float.valueOf(wxPriceStr);
            if (wxPriceNum > 0) {
                canPayWechat = true;
                mPaymentLayout.setAmountText(PaymentDesc.ID_WE_CHAT, String.format(mContext.getResources()
                        .getString(R.string.putao_pay_charge_price), String.format("%.2f",
                        wxPriceNum)));
            }
        }
        if (!canPayAli) {
            mPaymentLayout.enablePayActoin(PaymentDesc.ID_ALIPAY, false);
        } else if (!canPayWechat) {
            mPaymentLayout.enablePayActoin(PaymentDesc.ID_WE_CHAT, false);
        }

        if( PaymentDesc.ID_ALIPAY == payActionType && canPayAli){
            canPaySelectChannel = true;
        }else if( PaymentDesc.ID_WE_CHAT == payActionType && canPayWechat){
            canPaySelectChannel = true;
        }
        setChargeLayoutDisable(!canPaySelectChannel);
    }

    /**
     * 显示优惠券信息
     */
    private void showCouponText(boolean needShow, float couponMoney) {
        if (needShow) {
            if (couponMoney == 0) {
                choiceDiscountCouponText.setText(R.string.putao_tel_charge_coupon_not_use);
            } else {
                choiceDiscountCouponText.setText(String.format(
                        mContext.getResources().getString(R.string.putao_user_tel_charge_coupon),
                        couponMoney));
            }
            choiceDiscountCouponText.setVisibility(View.VISIBLE);
        } else {
            choiceDiscountCouponText.setVisibility(View.GONE);
        }
    }

    /**
     * 更新号码状态标识
     */
    private void showPhoneNumData(int phoneState, String operatorInfo) {
        switch (phoneState) {
            case PHONE_STATE_OPERATOR:
                // 更新号码状态标识：显示归属地
                mOperatorTView.setText(operatorInfo);
                mOperatorTView.setTextColor(mContext.getResources().getColor(
                        R.color.putao_text_color_primary));
                break;
            case PHONE_STATE_ERROR:
                // 更新号码状态标识：手机号码有误
                mOperatorTView.setText(mContext.getResources().getString(
                        R.string.putao_charge_phonenum_error));
                mOperatorTView.setTextColor(mContext.getResources().getColor(
                        R.color.putao_text_color_importance));
                break;
            case PHONE_STATE_SPACE:
                // 更新号码状态标识：空
                mOperatorTView.setText("");
                break;
            default:
                break;
        }
    }

    private boolean setLocalPhoneNumName(){
        if(TextUtils.isEmpty(localPhoneNum)){
            localPhoneNum=ContactsHubUtils.getPhoneNumber(mContext);
        }
        if (!TextUtils.isEmpty(localPhoneNum)) {
            String phoneNum = mNumberEditText.getEditableText().toString();
            phoneNum=phoneNum.replace(" ", "");
            if (!TextUtils.isEmpty(phoneNum)&&phoneNum.equals(localPhoneNum)) {
                mNameTView.setVisibility(View.VISIBLE);
                mNameTView.setText(mContext.getResources().getString(R.string.putao_local_phone_num));
                return true;
            }
        }
        return false;
    }
    private void showNameData(String name) {
        if(setLocalPhoneNumName()){
            return;
        }
        if (TextUtils.isEmpty(name)) {
            mNameTView.setVisibility(View.GONE);
            mNameTView.setText("");
        } else {
            mNameTView.setVisibility(View.VISIBLE);
            mNameTView.setText(name);
        }
    }

    /**
     * 选择不同的支付渠道
     */
    // private void selectPayChannel(int payType){
    // if( payType == PAY_WECHAT ){
    // pay_type = PAY_WECHAT;
    // weChatLayout.setSelected(true);
    // alipayLayout.setSelected(false);
    // }else if( payType == PAY_ALIPAY ){
    // pay_type = PAY_ALIPAY;
    // alipayLayout.setSelected(true);
    // weChatLayout.setSelected(false);
    // }else{
    // return;
    // }
    // if( TextUtils.isEmpty(hasAskedPhone) ){
    // return;
    // }
    //
    // boolean canPaySelectChannel = false;
    // boolean canPayAli = false;
    // boolean canPayWechat = false;
    // ProductDescBean productDescBean = chargeItems.get(mSelectPos);
    // List<PricePayBean> pay_list = productDescBean.pay_list;
    // if( pay_list != null ){
    // for (PricePayBean pricePayBean : pay_list) {
    // float payPrice = pricePayBean.pay_price;
    // if( payPrice <= 0 ){
    // // 说明有该支付渠道的价格，但是价格异常，不处理.
    // continue;
    // }
    // originalCost = payPrice;//记录当前用户选择的商品价格
    // closingCost = originalCost - couponPrice;
    // if(pricePayBean.type == PAY_ALIPAY){
    // canPayAli = true;
    // priceByAlipayTextView.setText(String.format(mContext.getResources().getString(R.string.putao_pay_charge_price),
    // String.format("%.2f", closingCost)));
    // if(pricePayBean.type == pay_type){
    // priceByAlipayTextView.setTextColor(mGreenColor);
    // alipayRadio.setChecked(true);//add by putao_lhq
    // }else{
    // priceByAlipayTextView.setTextColor(mNormalColor);
    // alipayRadio.setChecked(false);//add by putao_lhq
    // }
    // }else if(pricePayBean.type == PAY_WECHAT){
    // canPayWechat = true;
    // priceByWeChatTextView.setText(String.format(mContext.getResources().getString(R.string.putao_pay_charge_price),
    // String.format("%.2f", closingCost)));
    // if(pricePayBean.type == pay_type){
    // priceByWeChatTextView.setTextColor(mGreenColor);
    // wechatRadio.setChecked(true);//add by putao_lhq
    // }else{
    // priceByWeChatTextView.setTextColor(mNormalColor);
    // wechatRadio.setChecked(false);//add by putao_lhq
    // }
    // }
    //
    // if( pricePayBean.type == pay_type ){
    // // 当前选择的支付渠道可以支付
    // canPaySelectChannel = true;
    // }
    // }
    // }
    //
    // if( !canPayAli ){
    // priceByAlipayTextView.setText(R.string.putao_charge_getchargeinfo_failed);
    // priceByAlipayTextView.setTextColor(mGrayColor);
    // }else if( !canPayWechat ){
    // priceByWeChatTextView.setText(R.string.putao_charge_getchargeinfo_failed);
    // priceByWeChatTextView.setTextColor(mGrayColor);
    // }
    //
    // setChargeLayoutDisable(!canPaySelectChannel);
    // }

    /**
     * 根据号码获取归属地等信息
     */
    private String getOperatorInfo(String phoneNum) {
        if (TextUtils.isEmpty(phoneNum)) {
            return "";
        }
        if (mHistoryList != null && mHistoryList.size() > 0) {
            for (int i = 0; i < mHistoryList.size(); i++) {
                if (phoneNum.equals(mHistoryList.get(i).getPhoneNum())) {
                    return mHistoryList.get(i).getProvinceAndOperator();
                }
            }
        }
        ChargeHistoryItem historyItem = new ChargeHistoryItem(mContext, phoneNum);
        return historyItem.getProvinceAndOperator();
    }

    /**
     * 充值按钮是否可用
     * 
     * @param isDisable
     */
    private void setChargeLayoutDisable(boolean isDisable) {
        if (isDisable) {
            // 不可用
            mChargeLayout.getBackground().setAlpha(80);
            mChargeLayout.setClickable(false);
        } else {
            // 可用
            mChargeLayout.getBackground().setAlpha(255);
            mChargeLayout.setClickable(true);

            mTelephoneCharging = false;
        }
    }

    /**
     * charge telephone
     */
    private void doChargeTelephone(String phoneNum, PhoneFeeProductInfo productInfo, int pay_type,
            Voucher choice_voucher) {
        if (!PutaoAccount.getInstance().isLogin()) {
            Toast.makeText(mContext, R.string.putao_yellow_page_try_login, Toast.LENGTH_SHORT)
                    .show();
            PutaoAccount.getInstance().silentLogin(this);
        } else {
            LogUtil.i(TAG, "doChargeTelephone start.");

            mTelephoneCharging = true; // 充值中...

            mHandler.sendEmptyMessageDelayed(MSG_QUERYING_ACTION, 500); // 正在查询中...

            String pro_id = String.valueOf(productInfo.getProd_id());
            String price;
            if (PaymentDesc.ID_ALIPAY == pay_type) {
                price = productInfo.getAl_price();
            } else if (PaymentDesc.ID_WE_CHAT == pay_type) {
                price = productInfo.getWx_price();
            } else {
                return;
            }

            GetOrderParam param = new GetOrderParam();
            Float fPrice;

            try {
                fPrice = Float.parseFloat(price);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            int originalPriceInCent = (int)((fPrice * 100));
            int finalPriceInCent = 0;
            if (choice_voucher != null) {
                float choiceVoucherMoney = 0;
                try {
                    choiceVoucherMoney = Float.parseFloat(choice_voucher.money);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (choiceVoucherMoney > 0) {
                    finalPriceInCent = (int)((fPrice - choiceVoucherMoney) * 100);
                    if (finalPriceInCent <= 0) {
                        finalPriceInCent = 1;//最低付费金额1分
                    }
                    param.addCounponId(choice_voucher.id);
                } else {
                    finalPriceInCent = originalPriceInCent;
                }
            } else {
                finalPriceInCent = originalPriceInCent;
            }
            param.setProductId(ProductTypeCode.Telephone.ProductId);
            param.setProductType(ProductTypeCode.Telephone.ProductType);
            param.putSubObj("prodid", pro_id);
            param.putSubObj("prod_price", String.valueOf(finalPriceInCent));
            param.putSubObj("mobilenum", phoneNum);
            param.putSubObj("face_value", productInfo.getProd_content());
            param.putSubObj("attribution", operatorInfo);
            param.putUIPair("mobile_ui", phoneNum + "  " + mOperatorTView.getText().toString());
            param.putUIPair("traffic_value", productInfo.getProd_content());
            param.setPriceInCents(finalPriceInCent);
            addUmengEvent(param);
            mPaymentLayout.startPayment(param);

            addPhoneNumHabit(phoneNum);
            MobclickAgentUtil.onEvent(mContext, UMengEventIds.DISCOVER_YELLOWPAGE_CHARGE_RECHARGE);
        }
    }

    /**
     * 有盟统计 
     * @author xcx
     * @param orderParam
     */
    private void addUmengEvent(GetOrderParam orderParam){
        StringBuffer uMengSuccessIds=new StringBuffer();
        StringBuffer uMengFailIds=new StringBuffer();
        if (PaymentDesc.ID_ALIPAY==mPaymentLayout.getCurrentSelectPay().actionType) {
            uMengSuccessIds.append(UMengEventIds.DISCOVER_YELLOWPAGE_PHONE_RECHARGE_CHARGE_ALIPAY_SUCEESS);
            uMengFailIds.append(UMengEventIds.DISCOVER_YELLOWPAGE_PHONE_RECHARGE_CHARGE_ALIPAY_FAIL);
        }else{
            uMengSuccessIds.append(UMengEventIds.DISCOVER_YELLOWPAGE_PHONE_RECHARGE_CHARGE_WECHAT_SUCEESS);
            uMengFailIds.append(UMengEventIds.DISCOVER_YELLOWPAGE_PHONE_RECHARGE_CHARGE_WECHAT_FAIL);
        }
        orderParam.putUIPair(UMengEventIds.EXTRA_UMENG_EVENT_IDS_SUCCESS,uMengSuccessIds.toString());
        orderParam.putUIPair(UMengEventIds.EXTRA_UMENG_EVENT_IDS_FAIL,uMengFailIds.toString());
    }
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            int what = msg.what;
            switch (what) {
                case MSG_SHOW_SELECT_CONTACT_PHONE_ACTION:
                    Bundle bundle = msg.getData();
                    if (bundle == null) {
                        return;
                    }
                    String phoneNumTemp = bundle.getString("PhoneNum");
                    String contactName = bundle.getString("ContactName");
                    if (TextUtils.isEmpty(phoneNumTemp)) {
                        return;
                    }
                    showNameData(contactName);
                    String phoneNum = ContactsHubUtils.formatIPNumber(phoneNumTemp, mContext);
                    LogUtil.i(TAG, "select contact phonenum: " + phoneNum);
                    int selection = phoneNum.length();
                    if (selection > VALID_PHONENUM_SIZE) {
                        selection = VALID_PHONENUM_SIZE;
                        phoneNum = phoneNum.substring(0, VALID_PHONENUM_SIZE);
                    }
                    setPhoneNum(phoneNum);
                    if (TextUtils.isEmpty(phoneNum) || phoneNum.length() < VALID_PHONENUM_SIZE
                            || !TelAreaUtil.getInstance().isValidMobile(phoneNum)) {
                        // 如果号码为空或号码长度小于11（为不完整号码） 或者 检测是否是合法的号码
                        showPriceData(PRICE_STATE_PRICERANGE);
                        setChargeLayoutDisable(true);
                        showNameData("");
                        showPhoneNumData(PHONE_STATE_ERROR, "");
                    }
                    break;
                case MSG_SHOW_INPUT_NUM_HINT_ACTION:
                    mHandler.removeMessages(MSG_SHOW_INPUT_NUM_HINT_ACTION);
                    Utils.showToast(mContext, R.string.putao_charge_charge_hint, false);
                    break;
                case MSG_NO_SERVER_DATA_ACTION:
                    if (!TextUtils.isEmpty(mNumberEditText.getText().toString())) {
                        showPriceData(PRICE_STATE_NODATA);
                    }
                    break;
                case MSG_ASKMOBILEPRICE_EXCEPTION_ACTION:// add by hyl 2014-8-16
                    Toast.makeText(mContext, R.string.putao_charge_query_price_fail,
                            Toast.LENGTH_SHORT).show();
                    break;
                case MSG_QUERYING_ACTION:
                    // 更新“立即充值”button状态
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
                    mChargeContentTView.setText(mChargingBtnStr);
                    mChargeWaitTView.setVisibility(View.VISIBLE);
                    mChargeWaitTView.setText(str);
                    mHandler.sendEmptyMessageDelayed(MSG_QUERYING_ACTION, 500);
                    break;
                case MSG_LOGIN_SUCCESS_HINT:
                    // 登录成功，可以继续充值
                    Toast.makeText(mContext, R.string.putao_yellow_page_try_login_success,
                            Toast.LENGTH_SHORT).show();
                    setChargeLayoutDisable(false);
                    break;
                case MSG_LOGIN_FAIL_HINT:
                    // 登录失败，则不能进行充值
                    Toast.makeText(mContext, R.string.putao_yellow_page_try_login_fail,
                            Toast.LENGTH_SHORT).show();
                    setChargeLayoutDisable(true);
                    clearChargeBtnMessage();
                    break;
                default:
                    break;
            }
        };
    };

    /**
     * 清除“立即充值”按钮 状态变化
     */
    private void clearChargeBtnMessage() {
        mQueryingComputeNum = 0;
        if (mHandler.hasMessages(MSG_QUERYING_ACTION)) {
            mHandler.removeMessages(MSG_QUERYING_ACTION);
            mChargeWaitTView.setVisibility(View.GONE);
        }
        mChargeContentTView.setText(R.string.putao_charge_immediately);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // TODO Auto-generated method stub
        if (intent == null) {
            return;
        }
        if (REQUEST_CONTACT_INFO == requestCode) {
            Uri uri = intent.getData();
            ContentResolver contentResolver = mActivity.getContentResolver();
            Cursor cursor = contentResolver.query(uri, null, null, null, null);
            if (cursor == null) {
                return;
            }
            String phoneNum = "";
            String contactName = "";
            boolean isFailed = false;
            try {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(Phones.NUMBER);
                if (columnIndex == -1) {
                    // 小米 联系人读取方式
                    columnIndex = cursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                }
                phoneNum = cursor.getString(columnIndex);

                int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                if (nameIndex != -1) {
                    contactName = cursor.getString(nameIndex);
                }
            } catch (Exception e) {
                phoneNum = "";
                isFailed = true;
                LogUtil.d(TAG, "onActivityResult Exception...");
            } finally {
                try {
                    cursor.close();
                } catch (Exception e) {
                }
            }
            if (TextUtils.isEmpty(phoneNum)) {
                if (isFailed) {
                    Utils.showToast(mContext, R.string.putao_charge_getcontact_hint_error, false);
                } else {
                    Utils.showToast(mContext, R.string.putao_charge_getcontact_hint_empty, false);
                }
            } else {
                Message msg = mHandler.obtainMessage();
                msg.what = MSG_SHOW_SELECT_CONTACT_PHONE_ACTION;
                Bundle bundle = new Bundle();
                bundle.putString("PhoneNum", phoneNum);
                bundle.putString("ContactName", contactName);
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        int id = view.getId();
        if (id == R.id.clear_search) {
            if (TextUtils.isEmpty(mNumberEditText.getText().toString())) {
                showInputManager(false);
                try {
                    MobclickAgentUtil.onEvent(mContext,
                            UMengEventIds.DISCOVER_YELLOWPAGE_CHARGE_SELECT_CONTACT);
                    intent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
                    intent.setType("vnd.android.cursor.dir/phone");
                    intent.setType("vnd.android.cursor.dir/phone_v2");
                    startActivityForResult(intent, REQUEST_CONTACT_INFO);
                } catch (Exception e) {

                }
            } else {
                mNumberEditText.setText("");
            }
        } else if (id == R.id.charge_confirm) {
            
            String phoneNum = mNumberEditText.getText().toString();
            phoneNum=phoneNum.replace(" ", "");
            if (TextUtils.isEmpty(phoneNum)) {
                mHandler.removeMessages(MSG_SHOW_INPUT_NUM_HINT_ACTION);
                mHandler.sendEmptyMessageDelayed(MSG_SHOW_INPUT_NUM_HINT_ACTION, 1000);
                return;
            }
            if (!NetUtil.isNetworkAvailable(mContext)) {
                Utils.showToast(mContext, R.string.putao_no_net, false);
                return;
            }
            mHistoryLayout.setVisibility(View.GONE);
            showInputManager(false);
            addPhoneNum(phoneNum);
            PhoneFeeProductInfo product = availableChargeItems.get(mSelectPos);
            if (product != null) {
                setChargeLayoutDisable(true);
                doChargeTelephone(phoneNum, product, payActionType, choice_voucher);
            }
        } else if (id == R.id.charge_name) {
            mNumberEditText.requestFocus();
            String editText = mNumberEditText.getText().toString();
            if (!TextUtils.isEmpty(editText)) {
                mNumberEditText.setSelection(editText.length());
            } else {
                refreshChargeHistoryLayout();
            }
            showInputManager(true);
        } else if (id == R.id.charge_edit) {
            refreshChargeHistoryLayout();
        } else if (id == R.id.question) {
            intent = new Intent(mContext, ChargeQuestionActivity.class);
            YellowParams params = new YellowParams();
            params.setTitle(this.mContext.getResources().getString(R.string.putao_charge_question));
            params.setUrl(this.mContext.getResources().getString(R.string.putao_charge_question_url));
            intent.putExtra(YellowUtil.TargetIntentParams, params);
            startActivity(intent);
        } else if (id == R.id.choice_discount_coupon_text) {
            if (!mTelephoneCharging) {
                final CommonDialog dialog = CommonDialogFactory.getListCommonDialog(this
                        .getActivity());
                final ArrayList<String> datas = new ArrayList<String>();
                if (couponList != null) {
                    datas.add(mContext.getResources().getString(R.string.putao_tel_charge_coupon_not_use)); // 第一个显示：不使用优惠券
                    for (Voucher voucher : couponList) {
                        datas.add(String.format(
                                mContext.getResources()
                                        .getString(R.string.putao_tel_charge_coupon_use_money),
                                Float.parseFloat(voucher.money)));
                    }
                }
                dialog.setTitle(R.string.putao_tel_charge_coupon_use);
                dialog.setListViewDatas(datas);
                dialog.setListViewItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                        dialog.dismiss();

                        if (position == 0) {
                            couponPrice = 0;
                            choice_voucher = null;
                        } else {
                            choice_voucher = couponList.get(position - 1);
                            couponPrice = Float.parseFloat(choice_voucher.money);
                        }
                        showCouponText(true, couponPrice);
                        if (!TextUtils.isEmpty(hasAskedPhone)) {
                            showPriceData(PRICE_STATE_PRICE);
                        }
                    }
                });
                dialog.show();
            }
        } else {
        }
    }

    private void showInputManager(boolean isNeedShow) {
        if (mInputManager == null) {
            mInputManager = (InputMethodManager)mActivity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        if (isNeedShow) {
            mInputManager.showSoftInput(mNumberEditText, InputMethodManager.SHOW_IMPLICIT);
        } else {
            if (mInputManager.isActive()) {
                mInputManager.hideSoftInputFromWindow(mNumberEditText.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * 刷新显示充值电话历史列表
     */
    private void refreshChargeHistoryLayout() {
        if (isFirstInit) {
            isFirstInit = false;
            return;
        }
        mHistoryList = getPhoneNumHistoryList();
        if (mHistoryList == null) {
            mHistoryLayout.setVisibility(View.GONE);
            return;
        }
        int size = mHistoryList.size();
        if (size == 0) {
            mHistoryLayout.setVisibility(View.GONE);
            return;
        }
        mHistoryLayout.setVisibility(View.VISIBLE);
        mHistoryAdapter.setData(mHistoryList);
    }

    /**
     * 增加电话号码到充值电话历史中
     */
    private void addPhoneNum(String phoneNum) {
        if (TextUtils.isEmpty(phoneNum)) {
            return;
        }
        ChargeHistoryItem historyItem = new ChargeHistoryItem(mContext, phoneNum);
        if (!historyItem.isNeedStoreAsHistory()) {
            return;
        }
        mHistoryList = getPhoneNumHistoryList();
        if (mHistoryList == null) {
            mHistoryList = new ArrayList<ChargeHistoryItem>();
        }
        if (isExistInHistory(phoneNum)) {
            return;
        }
        int size = mHistoryList.size();
        if (size >= SAVE_CHARGE_TEL_NUM) {
            mHistoryList.remove(0);
        }
        mHistoryList.add(historyItem);
        StringBuffer strBuffer = new StringBuffer();
        size = mHistoryList.size();
        for (int i = 0; i < size; i++) {
            String historyBean = mHistoryList.get(i).toHistoryBean();
            if (TextUtils.isEmpty(historyBean)) {
                continue;
            }
            strBuffer.append(historyBean);
            if (i == size - 1) {
                continue;
            }
            strBuffer.append(ConstantsParameter.SHAREDPREFERENCES_DATA_DELIMITER);
        }
        mChargHistorySPref.edit().putString(CHARGE_TELEPHONE_KEY, strBuffer.toString()).commit();
    }

    /**
     * 增加电话号码等信息到用户习惯中
     */
    private void addPhoneNumHabit(String phoneNum) {
        if (TextUtils.isEmpty(phoneNum)) {
            return;
        }
        ChargeHistoryItem historyItem = new ChargeHistoryItem(mContext, phoneNum);
        if (!historyItem.isNeedStoreAsHistory()) {
            return;
        }
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append(historyItem.getPhoneNum());
        strBuffer.append(ConstantsParameter.SHAREDPREFERENCES_DATA_DELIMITER_SECOND);
        strBuffer.append(historyItem.getPhoneAddr());
        strBuffer.append(ConstantsParameter.SHAREDPREFERENCES_DATA_DELIMITER_SECOND);
        strBuffer.append(historyItem.getPhoneOperator());
        HabitDataItem item = new HabitDataItem(MyCenterConstant.MY_NODE_CHARGE_TELE,
                MyCenterConstant.HIBAT_CONTENT_TYPE_CHARGE_TELE_AND_ADD_AND_OPERATORS,
                strBuffer.toString());
        UserInfoUtil.getInstace().saveHabitData(mContext, item);
    }

    /**
     * 保存充值电话列表到本地
     */
    private void savePhoneListToFile() {
        if (mHistoryList == null) {
            mHistoryList = new ArrayList<ChargeHistoryItem>();
        }
        int size = mHistoryList.size();
        if (size == 0) {
            mChargHistorySPref.edit().putString(CHARGE_TELEPHONE_KEY, null).commit();
            return;
        }
        StringBuffer strBuffer = new StringBuffer();
        for (int i = 0; i < size; i++) {
            String historyBean = mHistoryList.get(i).toHistoryBean();
            if (TextUtils.isEmpty(historyBean)) {
                continue;
            }
            strBuffer.append(historyBean);
            if (i == size - 1) {
                continue;
            }
            strBuffer.append(ConstantsParameter.SHAREDPREFERENCES_DATA_DELIMITER);
        }
        mChargHistorySPref.edit().putString(CHARGE_TELEPHONE_KEY, strBuffer.toString()).commit();
    }

    /**
     * 在充值电话列表中清除电话号码clearPhone
     */
    private void clearPhoneNumHistory(String clearPhone, boolean needRefreshAdapter) {
        String histyryStr = mChargHistorySPref.getString(CHARGE_TELEPHONE_KEY, null);
        if (TextUtils.isEmpty(histyryStr)) {
            // 修复BUG #1623 充值，最近一条充值号码记录无法删除。
            // modify by lisheng 2014-10-18 22:18:54
            mHistoryList.clear();
            mHistoryAdapter.setData(mHistoryList);
            // modify by lisheng 2014-10-18 22:18:54 end
            return;
        }
        String[] historyBeans = histyryStr
                .split(ConstantsParameter.SHAREDPREFERENCES_DATA_DELIMITER);
        if (historyBeans == null || historyBeans.length == 0) {
            return;
        }
        if (mHistoryList == null) {
            mHistoryList = new ArrayList<ChargeHistoryItem>();
        } else {
            mHistoryList.clear();
        }
        int size = historyBeans.length;
        ChargeHistoryItem historyItem = null;
        for (int i = 0; i < size; i++) {
            String historyBean = historyBeans[i];
            if (TextUtils.isEmpty(historyBean)) {
                continue;
            }
            historyItem = getHistoryItemByString(historyBean);
            if (historyItem == null) {
                continue;
            }
            if (historyItem.getPhoneNum().equals(clearPhone)) {
                continue;
            }
            mHistoryList.add(historyItem);
        }
        savePhoneListToFile();
        if (needRefreshAdapter) {
            mHistoryAdapter.setData(mHistoryList);
        }
    }

    /**
     * 获取充值电话 历史列表
     */
    private ArrayList<ChargeHistoryItem> getPhoneNumHistoryList() {
        String histyryStr = mChargHistorySPref.getString(CHARGE_TELEPHONE_KEY, null);
        // 修复BUG #1623 充值，最近一条充值号码记录无法删除。 modify by lisheng 2014-10-18 22:20:35
        /**
         * if (TextUtils.isEmpty(histyryStr)) { // 检查当本地历史记录为空时，使用应用历史的最后一条记录 //
         * Modified by change at 2014/09/24 start List<HabitDataItem> list =
         * HabitUtil.getInstace().getHabitDataByContentType(MyCenterConstant.
         * MY_NODE_CHARGE_TELE,MyCenterConstant.
         * HIBAT_CONTENT_TYPE_CHARGE_TELE_AND_ADD_AND_OPERATORS,true); if(list
         * == null || list.size() == 0) { return null; } else { HabitDataItem
         * item = list.get(list.size()-1); // 取最后一条应用数据 if(item != null &&
         * !TextUtils.isEmpty(item.getContent_data())) { histyryStr =
         * item.getContent_data(); } if(TextUtils.isEmpty(histyryStr)) return
         * null; } // Modified by change at 2014/09/24 end }
         */
        String[] historyBeans = null;
        if (null != histyryStr) {
            historyBeans = histyryStr.split(ConstantsParameter.SHAREDPREFERENCES_DATA_DELIMITER);
        }
        // 修复BUG #1623 充值，最近一条充值号码记录无法删除。 modify by lisheng 2014-10-18 22:22:28
        // end;

        if (historyBeans == null || historyBeans.length == 0) {
            return null;
        }
        ArrayList<ChargeHistoryItem> phoneList = new ArrayList<ChargeHistoryItem>();
        int size = historyBeans.length;
        ChargeHistoryItem historyItem = null;
        for (int i = 0; i < size; i++) {
            String historyBean = historyBeans[i];
            if (TextUtils.isEmpty(historyBean)) {
                continue;
            }
            historyItem = getHistoryItemByString(historyBean);
            if (historyItem == null) {
                continue;
            }
            phoneList.add(historyItem);
        }
        return phoneList;
    }

    /**
     * 将historyBean解析为ChargeHistoryItem对象
     */
    private ChargeHistoryItem getHistoryItemByString(String historyBean) {
        if (TextUtils.isEmpty(historyBean)) {
            return null;
        }
        String[] chargeHistory = historyBean
                .split(ConstantsParameter.SHAREDPREFERENCES_DATA_DELIMITER_SECOND);
        if (chargeHistory == null || chargeHistory.length == 0) {
            return null;
        }
        if (chargeHistory.length != ChargeHistoryItem.ELEMENT_NUM) {
            return null;
        }
        ChargeHistoryItem historyItem = new ChargeHistoryItem();
        historyItem.setPhoneNum(chargeHistory[0]);
        historyItem.setPhoneAddr(chargeHistory[1]);
        historyItem.setPhoneOperator(chargeHistory[2]);
        return historyItem;
    }

    /**
     * 历史列表中是否已存在该号码
     */
    private boolean isExistInHistory(String phoneNum) {
        if (mHistoryList == null || mHistoryList.size() == 0 || TextUtils.isEmpty(phoneNum)) {
            return false;
        }
        for (int i = 0; i < mHistoryList.size(); i++) {
            if (phoneNum.equals(mHistoryList.get(i).getPhoneNum())) {
                return true;
            }
        }
        return false;
    }

    private class ChargeAdapter extends BaseAdapter {

        public ChargeAdapter() {
        }

        @Override
        public int getCount() {
            return availableChargeItems.size();
        }

        @Override
        public Object getItem(int arg0) {
            return availableChargeItems.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int arg0, View view, ViewGroup arg2) {
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(
                        R.layout.putao_yellow_page_charge_item, null);
            }
            PhoneFeeProductInfo chagreItem = availableChargeItems.get(arg0);
            TextView tv = (TextView)view.findViewById(R.id.face_price_text);
            tv.setText(String.format(
                    mContext.getResources().getString(R.string.putao_yellow_page_detail_customsprice),
                    chagreItem.getProd_content()));
            if (mSelectPos == arg0) {
                view.setBackgroundResource(R.drawable.putao_bg_white_select);
            } else {
                view.setBackgroundResource(R.drawable.putao_bg_white);
            }

            if (mSelectPos == arg0) {
                tv.setTextColor(mGreenColor);
            } else {
                tv.setTextColor(mNormalColor);
            }
            view.getBackground().setAlpha(255);
            return view;
        }

    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    @Override
    public void onPause() {
        try {
            int time = ((int)((System.currentTimeMillis() - startTime) / 1000));
            Map<String, String> map_value = new HashMap<String, String>();
            map_value.put("type", this.getClass().getName());
            // com.putao.analytics.MobclickAgentUtil.onEventValue(mContext,
            // UMengEventIds.DISCOVER_YELLOWPAGE_RESUME_TIME_61,
            // map_value, time);
            MobclickAgentUtil.onEventValue(mContext,
                    UMengEventIds.DISCOVER_YELLOWPAGE_RESUME_TIME_61, map_value, time);
        } catch (Exception e) {
        }
        super.onPause();
        showInputManager(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAskTask != null) {
            mAskTask.cancel(true);
            mAskTask = null;
        }
        mActivity.unregisterReceiver(mNetworkReceiver);
    }

    @Override
    public void onSuccess() {
        mHandler.sendEmptyMessage(MSG_LOGIN_SUCCESS_HINT);
    }

    @Override
    public void onFail(int msg) {
        mHandler.sendEmptyMessage(MSG_LOGIN_FAIL_HINT);
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
        return this.getClass().getName();
    }

    @Override
    public boolean needMatchExpandParam() {
        return false;
    }

    @Override
    public void onCancel() {
        // TODO Auto-generated method stub

    }

    @Override
    public Integer getAdId() {
        return AdCode.ADCODE_YellowPageChargeTelephoneFragment;
    }

    @Override
    public void onActionSelected(PaymentDesc desc, PaymentActionView view) {
        // TODO Auto-generated method stub
        payActionType = desc.actionType;
    }

    @Override
    public void onPaymentFeedback(int actionType, Throwable t, int resultCode,
            Map<String, String> extras) {
        setChargeLayoutDisable(false);
        clearChargeBtnMessage();
        //add by xcx 2015-01-12 start 对使用优惠券下单的情况做处理
        if (choice_voucher != null) {
            if (resultCode == ResultCode.OrderStatus.Success) {
                UserInfoUtil.getInstace().delUserVoucherList(choice_voucher.id);
                initCouponData();
            } else {
                try {
                    int originalResultCode = Integer.valueOf(extras.get("originalResultCode"));
                    if (originalResultCode == ChargeConst.ServRtnStatus_Coupon_Error) {
                        UserInfoUtil.getInstace().delUserVoucherList(choice_voucher.id);
                        initCouponData();
                    }else{
                        String isOrderSuccess = extras.get("isOrderSuccess");
                        if(!TextUtils.isEmpty(isOrderSuccess)&&isOrderSuccess.equals("true")){
                            UserInfoUtil.getInstace().delUserVoucherList(choice_voucher.id);
                            initCouponData();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        //add by xcx 2015-01-12 end 对使用优惠券下单的情况做处理
    }

}
