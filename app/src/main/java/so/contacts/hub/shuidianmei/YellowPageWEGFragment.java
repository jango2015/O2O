package so.contacts.hub.shuidianmei;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.account.IAccCallback;
import so.contacts.hub.account.PutaoAccount;
import so.contacts.hub.ad.AdCode;
import so.contacts.hub.charge.ChargeUtils;
import so.contacts.hub.http.bean.ProductDescBean;
import so.contacts.hub.payment.GetOrderParam;
import so.contacts.hub.payment.PaymentCallback;
import so.contacts.hub.payment.PaymentViewGroup;
import so.contacts.hub.payment.PaymentViewGroup.OnPaymentActionSelectedListener;
import so.contacts.hub.payment.core.PaymentDesc;
import so.contacts.hub.payment.data.ProductTypeCode;
import so.contacts.hub.payment.data.ResultCode;
import so.contacts.hub.payment.ui.PaymentActionView;
import so.contacts.hub.remind.BaseRemindFragment;
import so.contacts.hub.shuidianmei.WEGAccountAdapter.onDeleteButtonClickListener;
import so.contacts.hub.shuidianmei.bean.WEGOrderInfo;
import so.contacts.hub.shuidianmei.bean.WEGUserBean;
import so.contacts.hub.shuidianmei.bean.WaterElectricityGasBean;
import so.contacts.hub.ui.yellowpage.YellowPageChargeResultActivity;
import so.contacts.hub.ui.yellowpage.YellowPageCitySelectActivity;
import so.contacts.hub.ui.yellowpage.bean.HabitDataItem;
import so.contacts.hub.ui.yellowpage.bean.YellowParams;
import so.contacts.hub.util.ContactsAppUtils;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.NetUtil;
import so.contacts.hub.util.UMengEventIds;
import so.contacts.hub.util.UserInfoUtil;
import so.contacts.hub.util.Utils;
import so.contacts.hub.util.YellowUtil;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yulong.android.contacts.discover.R;

@SuppressLint("ValidFragment")
public class YellowPageWEGFragment extends BaseRemindFragment implements OnClickListener ,IAccCallback, PaymentCallback, OnPaymentActionSelectedListener {

    
    private static final String TAG = YellowPageWEGFragment.class.getSimpleName();
    public static final int WEG_PAGE_TYPE_WATER=1;
    public static final int WEG_PAGE_TYPE_ELECTRICITY=2;
    public static final int WEG_PAGE_TYPE_GAS=3;
    //城市列表 参数 
    private static int REQUEST_CITY = 1;
    private static int REQUEST_COMPANY = 2;
    
    private static int DELAY_TIME = 500;
    
    //用户列表显示数
    private static final int ACCOUNT_HISTORY_NUM = 3 ;
    
    // result code which get contacts phone num
    public static final int REQUEST_CONTACT_INFO = 0x1001;

    public static final int MSG_SHOW_SELECT_CONTACT_PHONE_ACTION = 0x2001;

    public static final int MSG_SHOW_INPUT_NUM_HINT_ACTION = 0x2002;

    public static final int MSG_NO_SERVER_DATA_ACTION = 0x2003;
    
    public static final int MSG_ASKMOBILEPRICE_EXCEPTION_ACTION = 0x2004;

    public static final int MSG_CHARGEING_ACTION = 0x2005;
    
    public static final int MSG_QUERYING_ACTION = 0x2006;

    public static final int MSG_LOGIN_SUCCESS_HINT = 0x2007;

    public static final int MSG_LOGIN_FAIL_HINT = 0x2008;
    
    public static final int MSG_SEND_CHEAK_PRICE = 0x2009;
    
    private int mRemindCode = -1;
    
    private View mContentView = null;
    
    private TextView mCityTextView = null;
    
    private TextView mCompanyTextView = null;
    
    private TextView mBillTextView = null;
    
    private TextView mUserCodeStateTip = null;
    
    private TextView mFeeTextView = null;
    
    private ImageView mClearImageView = null;
    
    private Context mContext = null;

    private YellowPageWaterEGActivity mActivity = null;
    
    private RelativeLayout mCityLayout = null;
    
    private RelativeLayout mCompanyLayout = null;
    
    private RelativeLayout mChargeLayout = null;
    
    private RelativeLayout mEditLayout = null;
    
    private EditText mUserCodeEditText = null;
    
    private TextView mChargeContentTView = null;

    private TextView mChargeWaitTView = null;

    private AsyncTask<WEGUserBean, Void, WEGUserBean> mAskTask = null;

    private Float mBillNum  = Float.valueOf(0);
    
    private Float mFeeNum  =  Float.valueOf(0);
    
    private String mAskAccount = "";
    
//    private LinearLayout mHistoryLayout = null;

    private ListView mHistoryListView = null;
    
    private WEGAccountAdapter mHistoryAdapter;
    
    private ArrayList<WEGAccountHistory> mHistoryList = new ArrayList<WEGAccountHistory>();
    
    // 网络状态变化action
    private static final String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    
    // 是否正在充值状态中
    private boolean mTelephoneCharging = false;
    
    ProductDescBean mPayBean = null;
    
    private int pay_type= PAY_ALIPAY;
    public static final int PAY_ALIPAY = 1;//支付宝
    public static final int PAY_WECHAT = 2;//微信支付（财付通）
    
    // “立即充值” 按钮打点 计数
    private int mQueryingComputeNum = 0;
    
    // “充值中” 字符串
    private String mChargingBtnStr = "";
    
    // “充值中” 字符串
    private String mQueryingBtnStr = "";
    
    //充值的类型 1水 2电 3煤气
    private int weg_type = -1;
    //产品Id
    private String mProduct_id = "";
    
    WaterElectricityGasDB db = null;
    
    InputMethodManager mInputManager = null;
    
    private String hasAskedUserCode = ""; // 上次被询价的号码
    
    private SharedPreferences mLocalHistorySPref = null;
    
    private static final String LOCAL_WEG_HISTORY = "local_weg_history";
    private static final String LAST_CITY = "LAST_CITY";
    
    private volatile long mUpdatePriceTime ;
    private ExecutorService executorService = null;
    
    private PaymentViewGroup paymentView;
    
    private PopupWindow popupWindow_history;
    
    /**
     * add code by putao_lhq
     * @start
     */
    private RadioButton alipayRadio;
    private RadioButton wechatRadio;
    /*@end by putao_lhq*/
    
    /**
     * 初始化时必须指定 水电煤类型
     * @param type 水电煤类型 1.水 2.电 3.煤气
     */
    public YellowPageWEGFragment(int type){
        super();
        weg_type = type;
    }
    
    public YellowPageWEGFragment(){
        super();
    }
    /**
     * @param type 水电煤类型 1.水 2.电 3.煤气
     */
    public void setWEGType(int type){
        weg_type = type;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.putao_weg_yellow_page_w_e_g, null);
        return mContentView;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        db = ContactsAppUtils.getInstance().getDatabaseHelper().getWaterElectricityGasDB();
        
        executorService = Executors.newCachedThreadPool();
        
        mContext = this.getActivity().getBaseContext();
        mActivity = (YellowPageWaterEGActivity)this.getActivity();
        
        mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        
        mLocalHistorySPref = mActivity.getSharedPreferences(LOCAL_WEG_HISTORY,
                Activity.MODE_MULTI_PROCESS);

        Intent intent = mActivity.getIntent();
        if (intent != null) {
            YellowParams params = (YellowParams)intent.getSerializableExtra(
                    YellowUtil.TargetIntentParams);
            if (params != null) {
                mRemindCode = params.getRemindCode();
                MobclickAgentUtil.onEvent(getActivity(), UMengEventIds.DISCOVER_YELLOWPAGE_HEADER + params.getCategory_id() + "_"  + weg_type);
            }else{
                mRemindCode = intent.getIntExtra("RemindCode", -1);
            }
        }
        
        mHistoryList = getAccountNumHistoryList();
        
        initViews();
        
        //将最新的信息填充
        if(mHistoryList != null && mHistoryList.size()>0){
            autoWriteUserInfo(mHistoryList.get(0));
        }else{
            String city = mLocalHistorySPref.getString(LAST_CITY+weg_type, "深圳");
            setCityInfo(city);
        }
        
        mActivity.registerReceiver(mNetworkReceiver, new IntentFilter(CONNECTIVITY_CHANGE_ACTION));

    }

    private void autoWriteUserInfo(WEGAccountHistory accountHistory) {
        mCityTextView.setText(accountHistory.waterElectricityGasBean.getCity());
        mCompanyTextView.setText(accountHistory.waterElectricityGasBean.getCompany());
        mProduct_id = accountHistory.waterElectricityGasBean.getProduct_id();
        mUserCodeEditText.setText(accountHistory.accountNum);
        mUserCodeEditText.setSelection(accountHistory.accountNum.length());
    }
    
    @Override
    public void onResume() {
        LogUtil.i(TAG, "onResume  ");
        if(!TextUtils.isEmpty(hasAskedUserCode)&&mUserCodeEditText.getText().toString().equals(hasAskedUserCode)){
            clear();
            setChargeLayoutDisable(true);
            if(isReadyAsk()){
                WEGUserBean bean = new WEGUserBean();
                bean.setProid(mProduct_id);
                bean.setAccount((String)mUserCodeEditText.getText().toString());
                checkAndRequestPriceByHandle(bean);
            }
        }else{
            setChargeLayoutDisable(true);
        }
        clearChargeBtnMessage();
        super.onResume();
    }
    
    
    private void clear(){
        clearBillInfo();
        clearChargeBtnMessage();
        clearPayPriceMessage();
    }
    
    private void clearBillInfo(){
        mBillTextView.setText("");
        mBillNum = Float.valueOf(0);
        mFeeTextView.setText("");
        mFeeNum = Float.valueOf(0);
        mUserCodeStateTip.setVisibility(View.GONE);
        mUserCodeStateTip.setVisibility(View.GONE);
    }
    
    
    /**
     * 清除“立即充值”按钮 状态变化
     */
    private void clearChargeBtnMessage(){
        mQueryingComputeNum = 0;
        if( mHandler.hasMessages(MSG_CHARGEING_ACTION) ){
            mHandler.removeMessages(MSG_CHARGEING_ACTION);
            mChargeWaitTView.setVisibility(View.GONE);
        }
        if( mHandler.hasMessages(MSG_QUERYING_ACTION) ){
            mHandler.removeMessages(MSG_QUERYING_ACTION);
            mChargeWaitTView.setVisibility(View.GONE);
        }
        mChargeContentTView.setText(R.string.putao_charge_immediately);
    }
    
    /**
     * 清除“支付方式”按钮 信息
     */
    private void clearPayPriceMessage(){
        for (int i = 0, max = PaymentDesc.ALL_PAY_ACTS.length; i < max; i++) {
            paymentView.setAmountText(PaymentDesc.ALL_PAY_ACTS[i], "");
        }
    }
    
    
    /**
     * 选择不同的支付渠道
     */
    private void selectPayChannel(){
        if(!isReadyPay()){
            return;
        }

        float payPrice = mBillNum + mFeeNum;
        for (int i = 0, max = PaymentDesc.ALL_PAY_ACTS.length; i < max; i++) {
            paymentView.setAmountText(
                    PaymentDesc.ALL_PAY_ACTS[i],
                    String.format(getResources().getString(R.string.putao_pay_charge_price),
                            String.format("%.2f", payPrice)));
        }

        setChargeLayoutDisable(false);
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
     * 是否有条件去询价
     * @return
     */
    private boolean isReadyAsk(){
        String  city = (String)mCityTextView.getText();
        String  company =  (String)mCompanyTextView.getText();
        String  usercode = (String)mUserCodeEditText.getText().toString();
        if(!TextUtils.isEmpty(city)&&!TextUtils.isEmpty(company)&&!TextUtils.isEmpty(usercode)&&!TextUtils.isEmpty(mProduct_id)){
            return true;
        }else{
            return false;
        }
    }
    
    /**
     * 是否有条件去支付
     * @return
     */
    private boolean isReadyPay(){
        if(isReadyAsk()){
            return mBillNum >0 ;
        }else{
            return false;
        }
    }
    

    private void initViews() {
        // TODO Auto-generated method stub
        mCityLayout = (RelativeLayout)mContentView.findViewById(R.id.city_relativelayout);
        mCompanyLayout = (RelativeLayout)mContentView.findViewById(R.id.unit_relativelayout);  
        mEditLayout = (RelativeLayout)mContentView.findViewById(R.id.edit_layout_two);
        
        mCityTextView = (TextView)mContentView.findViewById(R.id.city_name);
        mCompanyTextView = (TextView)mContentView.findViewById(R.id.unit_name);
        mBillTextView = (TextView)mContentView.findViewById(R.id.bill_num);
        mFeeTextView = (TextView)mContentView.findViewById(R.id.fee_num);
        
        mClearImageView = (ImageView)mContentView.findViewById(R.id.clear_img);
        
        mUserCodeEditText = (EditText)mContentView.findViewById(R.id.charge_edit);
        mUserCodeStateTip = (TextView)mContentView.findViewById(R.id.user_code_state_tip);
        
        paymentView = (PaymentViewGroup)mContentView.findViewById(R.id.charge_payment_view);
        /**
         * add code by putao_lhq
         * @start
         */
        alipayRadio = (RadioButton)mContentView.findViewById(R.id.ailipayRadio);
        wechatRadio = (RadioButton)mContentView.findViewById(R.id.wechatRadio);
        /*@end by putao_lhq*/
        paymentView.setOnPaymentActionSelectedListener(this);
        paymentView.setPaymentCallback(this);
        paymentView.selectPayAction(PaymentDesc.ID_WE_CHAT);
//      modity by ljq start 2015/01/07 更换选择号码历史UI old code
//        mHistoryLayout = (LinearLayout)mContentView.findViewById(R.id.chargehistory_layout);
//        mHistoryListView = (ListView)mContentView.findViewById(R.id.chargehistory_list);
//        mHistoryListView.setOnTouchListener(new OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                showInputManager(false);
//                return false;
//            }
//        });
//        mHistoryAdapter = new WEGAccountAdapter(mContext, getAccountStrList(mHistoryList));
//        mHistoryAdapter.setOnDeleteButtonClickListener(new onDeleteButtonClickListener() {
//            
//            @Override
//            public void onDeleteButtonClicked(int position, String words) {
//                // TODO Auto-generated method stub
//                clearAccountNumHistory(position, true);
//            }
//        });
//        mHistoryListView.setAdapter(mHistoryAdapter);
//        mHistoryListView.setOnItemClickListener(new OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
//                WEGAccountHistory accountHistory = mHistoryList.get(position);
//                if (accountHistory == null) {
//                    return;
//                }
//                String editText = mUserCodeEditText.getText().toString();
//                mHistoryLayout.setVisibility(View.GONE);
//                if (accountHistory.accountNum.equals(editText)) {
//                    return;
//                }
//                autoWriteUserInfo(accountHistory);
//            }
//        });
//      modity by ljq end 2015/01/07 更换选择号码历史UI old code        
        mChargeLayout = (RelativeLayout)mContentView.findViewById(R.id.charge_confirm);
        mChargeContentTView = (TextView)mContentView.findViewById(R.id.charge_confirm_content);
        mChargeWaitTView = (TextView)mContentView.findViewById(R.id.charge_confirm_wait);
        
        mUserCodeEditText.setOnClickListener(this);
        mUserCodeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                clear();
                setChargeLayoutDisable(true);
                if (TextUtils.isEmpty(arg0)) {
                    mClearImageView.setVisibility(View.INVISIBLE);
                    clear();
                    refreshChargeHistoryLayout();
                } else {
                    if (popupWindow_history != null && popupWindow_history.isShowing()) {
                        popupWindow_history.dismiss();
                    }
                    mClearImageView.setVisibility(View.VISIBLE);
                        if(isReadyAsk()){
                            WEGUserBean bean = new WEGUserBean();
                            bean.setProid(mProduct_id);
                            bean.setAccount((String)mUserCodeEditText.getText().toString());
                            checkAndRequestPriceByHandle(bean);
                        }else{
                            clearBillInfo();
                            clearPayPriceMessage();
                        }
                }
            }
        });
        
        mCityLayout.setOnClickListener(this);
        mCompanyLayout.setOnClickListener(this);
        mClearImageView.setOnClickListener(this);
        mChargeLayout.setOnClickListener(this);
        
        mChargingBtnStr = getResources().getString(R.string.putao_charge_charging);
        mQueryingBtnStr = getResources().getString(R.string.putao_charge_querying);
        
    }
    
    /**
     * 检测账号并请求价格
     */
    private void checkAndRequestPriceByHandle(WEGUserBean bean){
        Log.d("WEGUtil", "checkAndRequestPrice account " + bean.getAccount());
//        if (mAskTask == null || (mAskTask != null && mAskTask.getStatus() == AsyncTask.Status.RUNNING)){
//        }
        
        Message msg = new Message();
        msg.obj = bean;
        msg.what = MSG_SEND_CHEAK_PRICE;
        mUpdatePriceTime = System.currentTimeMillis();
        mHandler.sendMessageDelayed(msg,DELAY_TIME);
        
        

    }
    
    /**
     * 检测账号并请求价格
     */
    private void checkAndRequestPrice(WEGUserBean bean){
        mAskTask = new AskUserBillTask();
        mAskTask.executeOnExecutor(executorService, bean);
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
    public void onClick(View v) {
       int id = v.getId();
       if(id == R.id.city_relativelayout){
           Intent intent = new Intent(mContext, YellowPageCitySelectActivity.class);
           intent.putExtra(YellowPageCitySelectActivity.FROM_ACTIVITY_KEY,YellowPageWaterEGActivity.class.getSimpleName()+"_city");
           intent.putExtra(YellowPageCitySelectActivity.FROM_ACTIVITY_TYPE, weg_type);
           intent.putExtra(YellowPageCitySelectActivity.SHOW_MODE_KEY, YellowPageCitySelectActivity.SHOW_MODE_NOHOT);
           startActivityForResult(intent,REQUEST_CITY);
       }else if(id == R.id.unit_relativelayout){
           if(mCityTextView != null && !TextUtils.isEmpty(mCityTextView.getText())){
                if (mCompanyTextView.getText().equals(
                        getString(R.string.putao_water_eg_tag_city_no_data))) {
                    return;
                }
                Intent intent = new Intent(mContext, YellowPageCitySelectActivity.class);
                intent.putExtra("city", mCityTextView.getText());
                intent.putExtra(YellowPageCitySelectActivity.FROM_ACTIVITY_KEY, YellowPageWaterEGActivity.class.getSimpleName()
                        + "_company");
                intent.putExtra(YellowPageCitySelectActivity.FROM_ACTIVITY_TYPE, weg_type);
                intent.putExtra("title", getString(R.string.putao_water_eg_tag_company_list));
                intent.putExtra(YellowPageCitySelectActivity.SHOW_MODE_KEY,
                        YellowPageCitySelectActivity.SHOW_MODE_PURE);
                startActivityForResult(intent, REQUEST_COMPANY);
           }else{
               Toast.makeText(mContext, R.string.putao_water_eg_tag_no_city,
                       Toast.LENGTH_SHORT).show();
           }
       }else if(id == R.id.clear_img){
           mUserCodeEditText.setText("");
           showInputManager(true);
           setChargeLayoutDisable(true);
       }else if (id == R.id.charge_confirm) {
           if (!isReadyPay()) {
               mHandler.removeMessages(MSG_SHOW_INPUT_NUM_HINT_ACTION);
               mHandler.sendEmptyMessageDelayed(MSG_SHOW_INPUT_NUM_HINT_ACTION, 1000);
               return;
           }
           if (!NetUtil.isNetworkAvailable(mContext)) {
               Utils.showToast(mContext, R.string.putao_no_net, false);
               return;
           }
//           mHistoryLayout.setVisibility(View.GONE);
           showInputManager(false);
//           addPhoneNum(phoneNum);
           setChargeLayoutDisable(true);
           doChargeWEG(pay_type);
       }else if(id == R.id.charge_edit){
           mUserCodeEditText.requestFocus();
           String editText = mUserCodeEditText.getText().toString();
           if (!TextUtils.isEmpty(editText)) {
//               mUserCodeEditText.setSelection(editText.length());
           } else {
               refreshChargeHistoryLayout();
           }
//           showInputManager(true);
       }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if( data == null ){
            return;
        }
        if( resultCode != Activity.RESULT_OK ){
            return;
        }
        if(requestCode == REQUEST_CITY){
            String cityname = data.getStringExtra("cityName");
            mLocalHistorySPref.edit().putString(LAST_CITY+weg_type, cityname).commit();
            setCityInfo(cityname);
        }else if(requestCode == REQUEST_COMPANY){
            mCompanyTextView.setText(data.getStringExtra("cityName"));
            String id = data.getStringExtra("id");
            if(!TextUtils.isEmpty(id)){
                mProduct_id = id; 
            }
        }
        clear();
        if(isReadyAsk()){
            WEGUserBean bean = new WEGUserBean();
            bean.setProid(mProduct_id);
            bean.setAccount((String)mUserCodeEditText.getText().toString());
            checkAndRequestPriceByHandle(bean);
        }
    }

    private void setCityInfo(String cityname) {
        mCityTextView.setText(cityname);
        List<WaterElectricityGasBean> beanlist = db.queryWegDataByTypeAndCity(weg_type,mCityTextView.getText().toString());
        if(beanlist != null && beanlist.size()>0){
            mCompanyTextView.setText(beanlist.get(0).getCompany());
            mProduct_id = beanlist.get(0).getProduct_id(); 
        }else{
            mCompanyTextView.setText(getString(R.string.putao_water_eg_tag_city_no_data));
            mProduct_id = "";
        }
    }
    
    
    private BroadcastReceiver mNetworkReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if( CONNECTIVITY_CHANGE_ACTION.equals(action) ){
                LogUtil.i(TAG, "network is changed!");
                //如果是从无网络到有网络变化 且 之前没有询价的结果
                if( NetUtil.isNetworkAvailable(mContext)){
                    LogUtil.i(TAG, "network is changed and need request phone price...");
                    //去查价
                    if (isReadyAsk()) {
                        WEGUserBean bean = new WEGUserBean();
                        bean.setProid(mProduct_id);
                        bean.setAccount((String)mUserCodeEditText.getText().toString());
                        checkAndRequestPriceByHandle(bean);
                    }
                }
            }
        }
        
    };

    private void doChargeWEG(int pay_type) {
        if ( !PutaoAccount.getInstance().isLogin() ) {
            Toast.makeText(mContext, R.string.putao_yellow_page_try_login, Toast.LENGTH_SHORT).show();
            PutaoAccount.getInstance().silentLogin(this);
        } else {
            LogUtil.i(TAG, "doChargeTelephone start.");

            mTelephoneCharging = true; // 充值中...

            mHandler.sendEmptyMessageDelayed(MSG_CHARGEING_ACTION, 500); // 正在充值中...

            // 获取订单信息和支付
            String pro_id = mProduct_id;
            String account = (String)mUserCodeEditText.getText().toString();
            float price = mBillNum + mFeeNum;
            String company = mCompanyTextView.getText().toString();

            GetOrderParam param = new GetOrderParam();
            param.setProductId(ProductTypeCode.WaterElectricityGas.ProductId);
            param.setProductType(ProductTypeCode.WaterElectricityGas.ProductType);

            String wegStr =  WEGUtil.getRechargeStringByType(mContext, weg_type);
            param.putSubObj("pro_id", pro_id);
            param.putSubObj("account", account);
            param.putSubObj("yearmonth", "");
            param.putSubObj("subject", String.format(mContext.getResources().getString(R.string.putao_water_eg_tag_subject_for_alipay), String.valueOf(price)));
            param.putSubObj("pt_token", PutaoAccount.getInstance().getPtUser().getPt_token());
            
            param.putUIPair("weg_str", wegStr);
            param.putUIPair("company", company);
            param.putUIPair("total_fee", String.valueOf(price));
            param.putUIPair("weg_type", String.valueOf(weg_type));
          
            param.setPriceInCents((int)(price * 100));
            addUmengEvent(param,paymentView.getCurrentSelectPay().actionType,weg_type);
            paymentView.startPayment(param);
            saveAccountNumHistory(mContext, account, pro_id);
            MobclickAgentUtil.onEvent(mContext, WEGUtil.DISCOVER_YELLOWPAGE_WEG_RECHARGE);
        }
    }
    
    /**
     * 有盟统计 
     * @author xcx
     * @param orderParam
     */
    private void addUmengEvent(GetOrderParam orderParam,int payType,int weg_type){
        StringBuffer uMengSuccessIds=new StringBuffer();
        StringBuffer uMengFailIds=new StringBuffer();
        if(weg_type==1){//水
            if (PaymentDesc.ID_ALIPAY==payType) {
                uMengSuccessIds.append(UMengEventIds.DISCOVER_YELLOWPAGE_SHUIDIANMEI_WATER_ALIPAY_SUCCESS);
                uMengFailIds.append(UMengEventIds.DISCOVER_YELLOWPAGE_SHUIDIANMEI_WATER_ALIPAY_FAIL);
            }else{
                uMengSuccessIds.append(UMengEventIds.DISCOVER_YELLOWPAGE_SHUIDIANMEI_WATER_WECHAT_SUCCESS);
                uMengFailIds.append(UMengEventIds.DISCOVER_YELLOWPAGE_SHUIDIANMEI_WATER_WECHAT_FAIL);
            }
           
        }else if(weg_type==2){//电
            if (PaymentDesc.ID_ALIPAY==payType) {
                uMengSuccessIds.append(UMengEventIds.DISCOVER_YELLOWPAGE_SHUIDIANMEI_ELECTRICITY_ALIPAY_SUCCESS);
                uMengFailIds.append(UMengEventIds.DISCOVER_YELLOWPAGE_SHUIDIANMEI_ELECTRICITY_ALIPAY_FAIL);
            }else{
                uMengSuccessIds.append(UMengEventIds.DISCOVER_YELLOWPAGE_SHUIDIANMEI_ELECTRICITY_WECHAT_SUCCESS);
                uMengFailIds.append(UMengEventIds.DISCOVER_YELLOWPAGE_SHUIDIANMEI_ELECTRICITY_WECHAT_FAIL);
            }
           
        }else {//煤 
            if (PaymentDesc.ID_ALIPAY==payType) {
                uMengSuccessIds.append(UMengEventIds.DISCOVER_YELLOWPAGE_SHUIDIANMEI_GAS_ALIPAY_SUCCESS);
                uMengFailIds.append(UMengEventIds.DISCOVER_YELLOWPAGE_SHUIDIANMEI_GAS_ALIPAY_FAIL);
            }else{
                uMengSuccessIds.append(UMengEventIds.DISCOVER_YELLOWPAGE_SHUIDIANMEI_GAS_WECHAT_SUCCESS);
                uMengFailIds.append(UMengEventIds.DISCOVER_YELLOWPAGE_SHUIDIANMEI_GAS_WECHAT_FAIL);
            }
           
        }
        
        uMengSuccessIds.append(",");
        uMengSuccessIds.append(UMengEventIds.DISCOVER_YELLOWPAGE_SHUIDIANMEI_ALL_SUCCESS);
        
        uMengFailIds.append(",");
        uMengFailIds.append(UMengEventIds.DISCOVER_YELLOWPAGE_SHUIDIANMEI_ALL_FAIL);
        orderParam.putUIPair(UMengEventIds.EXTRA_UMENG_EVENT_IDS_SUCCESS,uMengSuccessIds.toString());
        orderParam.putUIPair(UMengEventIds.EXTRA_UMENG_EVENT_IDS_FAIL,uMengFailIds.toString());
    }
    /**
     * charge telephone result
     */
    private void chargeResult(WEGOrderInfo orderInfo, boolean needCallback) {
        if (null == getActivity()) {
            return;
        }
        if (orderInfo == null) {
            return;
        }
        LogUtil.d(TAG, "chargeResult needCallback:" + needCallback + " ,orderInfo: " + orderInfo.toString());
        if( needCallback ){
            Intent intent = new Intent(mActivity, YellowPageChargeResultActivity.class);
            intent.putExtra("OrderInfo", orderInfo);
            intent.putExtra(YellowPageChargeResultActivity.CONTENT_KEY,YellowPageChargeResultActivity.CONTENT_WEG );
            String title = WEGUtil.getRechargeStringByType(mContext, weg_type);
            intent.putExtra("title",title);
            startActivity(intent);
        }
        // modify by xcx 2015-01-19 start 修改埋点方式
//        addUMengEvent(orderInfo);
        // modify by xcx 2015-01-19 end 修改埋点方式
    }
    
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            Object obj = null;
            int what = msg.what;
            switch (what) {
                case MSG_SHOW_INPUT_NUM_HINT_ACTION:
                    mHandler.removeMessages(MSG_SHOW_INPUT_NUM_HINT_ACTION);
                    Utils.showToast(mContext,
                            R.string.putao_water_eg_tag_please_input_info, false);
                    break;
                case MSG_NO_SERVER_DATA_ACTION:
                    if( !TextUtils.isEmpty(mUserCodeEditText.getText().toString()) ){
//                        showPriceData(PRICE_STATE_NODATA);
                        clear();
                        mUserCodeStateTip.setText(R.string.putao_water_eg_tag_no_data);
                        mUserCodeStateTip.setVisibility(View.VISIBLE);
                    }
                    break;
                case MSG_ASKMOBILEPRICE_EXCEPTION_ACTION:
//                    mBillTextView.setText(R.string.putao_water_eg_tag_please_check_network);
                    if(isAdded()){
                        mActivity.showToast(getString(R.string.putao_water_eg_tag_please_check_network));
                    }
                    break;
                case MSG_CHARGEING_ACTION:
                    //更新“立即充值”button状态
                    int computeNum = ++mQueryingComputeNum % 4;
                    String str = "";
                    if( computeNum == 0 ){
                        str = "";
                    }else if( computeNum == 1 ){
                        str = ".";
                    }else if( computeNum == 2 ){
                        str = "..";
                    }else if( computeNum == 3 ){
                        str = "...";
                    }
                    mChargeContentTView.setText(mChargingBtnStr);
                    mChargeWaitTView.setVisibility(View.VISIBLE);
                    mChargeWaitTView.setText(str);
                    mHandler.sendEmptyMessageDelayed(MSG_CHARGEING_ACTION, 500);
                    break;
                case MSG_QUERYING_ACTION:
                    //更新“查询中”button状态
                    computeNum = ++mQueryingComputeNum % 4;
                    str = "";
                    if( computeNum == 0 ){
                        str = "";
                    }else if( computeNum == 1 ){
                        str = ".";
                    }else if( computeNum == 2 ){
                        str = "..";
                    }else if( computeNum == 3 ){
                        str = "...";
                    }
                    mChargeContentTView.setText(mQueryingBtnStr);
                    mChargeWaitTView.setVisibility(View.VISIBLE);
                    mChargeWaitTView.setText(str);
                    mHandler.sendEmptyMessageDelayed(MSG_QUERYING_ACTION, 500);
                    break;    
                case MSG_LOGIN_SUCCESS_HINT:
                    // 登录成功，可以继续充值
                    Toast.makeText(mContext, R.string.putao_yellow_page_try_login_success, Toast.LENGTH_SHORT).show();
                    setChargeLayoutDisable(false);
                    break;
                case MSG_LOGIN_FAIL_HINT:
                    // 登录失败，则不能进行充值
                    Toast.makeText(mContext, R.string.putao_yellow_page_try_login_fail, Toast.LENGTH_SHORT).show();
                    setChargeLayoutDisable(true);
                    clearChargeBtnMessage();
                    break;
                case MSG_SEND_CHEAK_PRICE:
                    //查价
                    if(mUpdatePriceTime > System.currentTimeMillis()-DELAY_TIME){
                        
                    }else{
//                        Log.d("ljq ", "MSG_SEND_CHEAK_PRICE : " + ((WEGUserBean)msg.obj).getAccount());
                        checkAndRequestPrice((WEGUserBean)msg.obj);
                    }
                    break;    
                case ChargeUtils.MSG_SHOW_CHARGE_EXCEPTION_ACTION:
                    // 获取订单- 网络请求异常
                    setChargeLayoutDisable(false);
                    clearChargeBtnMessage();
                    break;
                case ChargeUtils.MSG_SHOW_CHARGE_ALI_EXCEPTION_ACTION:
                    // 获取订单- 阿里返回异常
                    setChargeLayoutDisable(false);
                    clearChargeBtnMessage();
                    break;
                case ChargeUtils.MSG_SHOW_CHARGE_RESULT_ACTION:
                    // 获取订单- 结果
                    obj = msg.obj;
                    // 1: 需要回调到结果页面；2：不需要回调到结果页面
                    int arg1 = msg.arg1;
                    if( obj != null ){
                        WEGOrderInfo orderInfo = (WEGOrderInfo)obj;
                        chargeResult(orderInfo, arg1 == 1 ? true : false);
                    }
                    break;
                default:
                    break;
            }
        };
    };
    
    private void showInputManager(boolean isNeedShow) {
        if (mInputManager == null) {
            mInputManager = (InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        if (isNeedShow) {
            mInputManager.showSoftInput(mUserCodeEditText, InputMethodManager.SHOW_IMPLICIT);
        } else {
            if( mInputManager.isActive() ){
                mInputManager.hideSoftInputFromWindow(mUserCodeEditText.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
    
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mAskTask != null){
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
    
    /**
     * 水电煤询价任务
     */
    private class AskUserBillTask extends AsyncTask<WEGUserBean, Void, WEGUserBean> {

        WEGUserBean bean = null;

        WEGUserBean billBean = null;
        
        long startTime = mUpdatePriceTime;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(mUpdatePriceTime > startTime){
                return;
            }
        }

        @Override
        protected WEGUserBean doInBackground(WEGUserBean... args) {
            LogUtil.d(TAG, "doInBackground");
            mHandler.sendEmptyMessage(MSG_QUERYING_ACTION);
            bean = args[0];
            try {
                billBean = WEGUtil.qryUserBill(bean);
            } catch (Exception e) {
                LogUtil.d(TAG, "AskUserBillTask query bill exception... "+e.getMessage());
                e.printStackTrace();
                mHandler.sendEmptyMessage(MSG_ASKMOBILEPRICE_EXCEPTION_ACTION);
                /*
                 * 当查询出现异常时，返回errorBillBean，解决查询异常时显示单号不存在
                 * modified by hyl 2014-12-23 start
                 * old code:
                 * return null;
                 */
                WEGUserBean errorBillBean = new WEGUserBean();
                errorBillBean.setResult("fail");
                return errorBillBean;
                //modified by hyl 2014-12-23 end
            }
            return billBean;
        }

        @Override
        protected void onPostExecute(WEGUserBean resultBean) {
 //           Log.d("WEGUtil", "updatePriceTime = " + mUpdatePriceTime + " startTime = " + startTime);
            if( TextUtils.isEmpty(mUserCodeEditText.getText().toString()) || (mUpdatePriceTime > startTime)){
                return;
            }
            if (resultBean == null ) {
                LogUtil.d(TAG, "AskUserBillTask query is null");
                mHandler.sendEmptyMessage(MSG_NO_SERVER_DATA_ACTION);
                return;
            }
//            Log.d("WEGUtil", "onPostExecute E ");
            if (resultBean != null && !resultBean.getResult().equals("fail")) {
                if(resultBean.getAccount().equals(mUserCodeEditText.getText().toString())){
                    String billNum = resultBean.getBills();
                    String feeNum = resultBean.getFee();
                    Float billFloat = Float.valueOf(Float.valueOf(billNum) / 100);
                    Float feeFloat = Float.valueOf(Float.valueOf(feeNum) / 100);
                    mBillNum = billFloat;
                    mFeeNum = feeFloat;
                    billNum = billFloat.toString();
                    feeNum = feeFloat.toString();
                    String subjectBill = "";
                    String subjectFee = "";
                    try {
                        subjectBill = String.format(mContext.getResources().getString(R.string.putao_water_eg_tag_RMB), billNum);
                        subjectFee = String.format(mContext.getResources().getString(R.string.putao_water_eg_tag_RMB), feeNum);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                    mBillTextView.setText(subjectBill);
                    mFeeTextView.setText(subjectFee);
                    hasAskedUserCode = resultBean.getAccount();
                    if(mBillNum == 0){
//                        Toast.makeText(mContext, R.string.putao_water_eg_tag_no_bill,
//                                Toast.LENGTH_SHORT).show();
                        mUserCodeStateTip.setText(R.string.putao_water_eg_tag_no_bill_text);
                        mUserCodeStateTip.setVisibility(View.VISIBLE);
                        setChargeLayoutDisable(true);
                    }else{
                        selectPayChannel();
                    }
                }
            } else {
                // 没有询价结果或网络繁忙
                setChargeLayoutDisable(true);
            }
            clearChargeBtnMessage();
            LogUtil.d(TAG, "onPostExecute ");
            super.onPostExecute(resultBean);

        }
    };
    
    private void clearAccountNumHistory(int position, boolean b) {
        // TODO Auto-generated method stub
        if(mHistoryList != null && position<mHistoryList.size()){
            WEGAccountHistory history = mHistoryList.get(position);
            if(history != null){
                String account = history.accountNum;
                String proid = history.waterElectricityGasBean.getProduct_id();
                if(!TextUtils.isEmpty(account)&&!TextUtils.isEmpty(proid)){
                    HabitDataItem item = new HabitDataItem(WEGUtil.MY_NODE_WEG_RECHARGE, WEGUtil.HIBAT_CONTENT_TYPE_WEG_ACCOUNT_CODE+weg_type, account+"&"+proid);
                    UserInfoUtil.getInstace().delHabitDataAsyn(mContext, item);
                    refreshChargeHistoryLayout();
                }
            }
        }
    }
    
    
    /**
     * 获取充值账户 历史列表
     */
    private ArrayList<WEGAccountHistory> getAccountNumHistoryList() {
        ArrayList<WEGAccountHistory> list = new ArrayList<WEGAccountHistory>();
        //加上weg_type来分别是哪个类型的充值
        List<HabitDataItem> items = UserInfoUtil.getInstace().getHabitDataByContentType(WEGUtil.MY_NODE_WEG_RECHARGE, WEGUtil.HIBAT_CONTENT_TYPE_WEG_ACCOUNT_CODE+weg_type, false);
        if(items != null && items.size()>0){
            int num = items.size()<ACCOUNT_HISTORY_NUM?items.size():ACCOUNT_HISTORY_NUM;
            for (int i = 0; i < num; i++) {
                String historyStr = items.get(i).getContent_data();
                if(historyStr.contains("&")){
                    String[] nameAndProid = historyStr.split("&");
                    String account = nameAndProid[0];
                    List<WaterElectricityGasBean> beans = db.queryWegDataByProid(nameAndProid[1]);
                    WaterElectricityGasBean bean = null;
                    if(beans != null && beans.size()>0){
                        bean = beans.get(0);
                    }
                    if(!TextUtils.isEmpty(account)&& bean!=null){
                        WEGAccountHistory history = new WEGAccountHistory();
                        history.accountNum = account;
                        history.waterElectricityGasBean = bean;
                        list.add(history);
                    }
                }
            }
        }
        return list;
    }
    
    private void saveAccountNumHistory(Context context,String account,String proid) {
        if(!TextUtils.isEmpty(account)){
            HabitDataItem item = new HabitDataItem();
            item.setSource_type(WEGUtil.MY_NODE_WEG_RECHARGE);
            //加上weg_type来分别是哪个类型的充值
            item.setContent_type(WEGUtil.HIBAT_CONTENT_TYPE_WEG_ACCOUNT_CODE+weg_type);
            item.setContent_data(account+"&"+proid);
            UserInfoUtil.getInstace().saveHabitData(context, item);
        }
    }
    
    /**
     * 刷新显示充值历史列表
     */
    private void refreshChargeHistoryLayout() {
        mHistoryList = getAccountNumHistoryList();
        if (mHistoryList == null) {
//            mHistoryLayout.setVisibility(View.GONE); old code modity by ljq 2015/01/07
            if (popupWindow_history != null && popupWindow_history.isShowing()) {
                popupWindow_history.dismiss();
            }
            return;
        }
        int size = mHistoryList.size();
        if (size == 0) {
//            mHistoryLayout.setVisibility(View.GONE); old code modity by ljq 2015/01/07
            if (popupWindow_history != null && popupWindow_history.isShowing()) {
                popupWindow_history.dismiss();
            }
            return;
        }
//        mHistoryLayout.setVisibility(View.VISIBLE);   old code modity by ljq 2015/01/07
//       mHistoryAdapter.setData(getAccountStrList(mHistoryList)); old code
        if(popupWindow_history == null || !popupWindow_history.isShowing()){
            showHistoryPopWindow(mUserCodeEditText);
        }
        if(mHistoryAdapter != null){
            mHistoryAdapter.setData(getAccountStrList(mHistoryList));
        }
    }
    
    /**
     * 历史信息类 包含一个 WaterElectricityGasBean 和一个 用户ID字段
     * @author Administrator
     *
     */
    class WEGAccountHistory{
        WaterElectricityGasBean waterElectricityGasBean ;
        String accountNum ;
    }
    
    private ArrayList<String> getAccountStrList(List<WEGAccountHistory> list){
        ArrayList<String> accountList = new ArrayList<String>();
        if(list != null && list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                accountList.add(list.get(i).accountNum);
            }
        }
        return accountList;
    }
    
    @Override
    public void onActionSelected(PaymentDesc desc, PaymentActionView view) {
        pay_type = desc.actionType;
        if( !mTelephoneCharging ){
            selectPayChannel();
        }
    }

    @Override
    public void onPaymentFeedback(int actionType, Throwable t, int resultCode,
            Map<String, String> extras) {
        setChargeLayoutDisable(false);
        clearChargeBtnMessage();
    }

    @Override
    public void onCancel() {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public Integer getAdId() {
        if (weg_type == WEG_PAGE_TYPE_ELECTRICITY) {
            return AdCode.ADCODE_YellowPageWEGFragment_ELECTRICITY;
        } else if (weg_type == WEG_PAGE_TYPE_WATER) {
            return AdCode.ADCODE_YellowPageWEGFragment_WATER;
        } else if (weg_type == WEG_PAGE_TYPE_GAS) {
            return AdCode.ADCODE_YellowPageWEGFragment_GAS;
        }
        return null;
    }

//  add by ljq start 2015/01/07 更换选择号码历史UI 
    private void showHistoryPopWindow(View parent) {
        View view = View.inflate(mContext, R.layout.putao_common_popwindow, null);
        mHistoryListView = (ListView)view.findViewById(R.id.history_list);
//        mHistoryListView.setOnTouchListener(new OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                showInputManager(false);
//                return false;
//            }
//        });
        mHistoryAdapter = new WEGAccountAdapter(mContext, getAccountStrList(mHistoryList));
        mHistoryAdapter.setOnDeleteButtonClickListener(new onDeleteButtonClickListener() {

            @Override
            public void onDeleteButtonClicked(int position, String words) {
                // TODO Auto-generated method stub
                clearAccountNumHistory(position, true);
            }
        });
        if (mHistoryList != null && mHistoryList.size() > 0) {
            mHistoryListView.setAdapter(mHistoryAdapter);
            mHistoryListView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
                    WEGAccountHistory accountHistory = mHistoryList.get(position);
                    if (accountHistory == null) {
                        return;
                    }
                    String editText = mUserCodeEditText.getText().toString();
                    if (popupWindow_history != null && popupWindow_history.isShowing()) {
                        popupWindow_history.dismiss();
                        showInputManager(false);
                    }
                    if (accountHistory.accountNum.equals(editText)) {
                        return;
                    }
                    autoWriteUserInfo(accountHistory);
                }
            });
            mHistoryListView.setOnKeyListener(new OnKeyListener() {

                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    // TODO Auto-generated method stub
                    popupWindow_history.dismiss();
                    return false;
                }

            });

        }
        popupWindow_history = new PopupWindow(view, mEditLayout.getWidth()+Utils.dip2px(mContext, 10f), LayoutParams.WRAP_CONTENT,
                true);
        
        popupWindow_history.setInputMethodMode(InputMethodManager.HIDE_NOT_ALWAYS);
        popupWindow_history.showAsDropDown(parent, 0, 10);
        popupWindow_history.setOutsideTouchable(true);
        popupWindow_history.getContentView().setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if(v.getId() != R.id.charge_edit){
                    popupWindow_history.dismiss();
                    popupWindow_history.setFocusable(false);
                }
                return true;
            }
        });
        popupWindow_history.getContentView().setFilterTouchesWhenObscured(true);
    }
//  add by ljq end 2015/01/07 更换选择号码历史UI 
}
