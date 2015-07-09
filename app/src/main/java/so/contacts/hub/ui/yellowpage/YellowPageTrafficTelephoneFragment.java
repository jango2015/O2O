
package so.contacts.hub.ui.yellowpage;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import so.contacts.hub.ContactsApp;
import so.contacts.hub.account.IAccCallback;
import so.contacts.hub.account.PutaoAccount;
import so.contacts.hub.ad.AdCode;
import so.contacts.hub.adapter.ChargeTelAdapter;
import so.contacts.hub.adapter.ChargeTelAdapter.onDeleteButtonClickListener;
import so.contacts.hub.charge.ChargeUtils;
import so.contacts.hub.core.ConstantsParameter;
import so.contacts.hub.http.bean.TrafficProductInfo;
import so.contacts.hub.http.bean.TrafficProductResponseBean;
import so.contacts.hub.payment.GetOrderParam;
import so.contacts.hub.payment.PaymentCallback;
import so.contacts.hub.payment.PaymentViewGroup;
import so.contacts.hub.payment.PaymentViewGroup.OnPaymentActionSelectedListener;
import so.contacts.hub.payment.core.PaymentDesc;
import so.contacts.hub.payment.data.ProductTypeCode;
import so.contacts.hub.payment.data.ResultCode;
import so.contacts.hub.payment.ui.PaymentActionView;
import so.contacts.hub.ui.BaseFragment;
import so.contacts.hub.ui.yellowpage.bean.ChargeHistoryItem;
import so.contacts.hub.util.ContactsDBImpl;
import so.contacts.hub.util.ContactsHubUtils;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.MobclickAgentUtil;
import so.contacts.hub.util.NetUtil;
import so.contacts.hub.util.TelAreaUtil;
import so.contacts.hub.util.UMengEventIds;
import so.contacts.hub.util.Utils;
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

/**
 * 
 * @author ffh
 * @since 2014/12/23
 */
public class YellowPageTrafficTelephoneFragment extends BaseFragment implements OnClickListener,IAccCallback,PaymentCallback, OnPaymentActionSelectedListener{

    private static final String TAG = YellowPageTrafficTelephoneFragment.class.getSimpleName();

    private EditText mNumberEditText = null;
    private TextView mNameTView = null;
    private ImageView mClearInput = null;
    private TextView mOperatorTView = null;

    private RelativeLayout mChargeLayout = null;
    private TextView mChargeContentTView = null;
    private TextView mChargeWaitTView = null;
    
//    private LinearLayout mAttentionLayout;
//    private TextView mAttentionFirstText,mAttentionFourthText,mAttentionFifthText,mAttentionSixthText;
    
    private PaymentViewGroup mPaymentLayout;
    
    private GridView mTrafficGridView = null;

    private LinearLayout mHistoryLayout = null;
    private ListView mHistoryListView = null;
    private ChargeTelAdapter mHistoryAdapter = null;
    private ArrayList<ChargeHistoryItem> mHistoryList = new ArrayList<ChargeHistoryItem>();

    private TrafficProductAdapter mTrifficTypeAdapter = null;

    // 默认显示第一个
    private static final int DEFAULT_SELECT_POS = 0;
    
    // 第一次初始化
    private boolean isFirstInit = true;
    /**
     * select pos in charge list
     */
    private int mSelectPos = DEFAULT_SELECT_POS;
    private SharedPreferences mChargHistorySPref = null;
    private InputMethodManager mInputManager = null;

    // result code which get contacts phone num
    private static final int REQUEST_CONTACT_INFO = 0x1001;
    private static final int MSG_SHOW_SELECT_CONTACT_PHONE_ACTION = 0x2001;
    private static final int MSG_SHOW_INPUT_NUM_HINT_ACTION = 0x2002;
    private static final int MSG_NO_SERVER_DATA_ACTION = 0x2003;
    private static final int MSG_ASKMOBILEPRICE_EXCEPTION_ACTION = 0x2004;
    private static final int MSG_QUERYING_ACTION = 0x2005;
    private static final int MSG_LOGIN_SUCCESS_HINT = 0x2006;
    private static final int MSG_LOGIN_FAIL_HINT = 0x2007;

    private static final String CHARGE_TELEPHONE_HISTORY = "charge_tel_history";
    private static final String CHARGE_TELEPHONE_KEY = "charge_tel_history_key";

    private static final int SAVE_CHARGE_TEL_NUM = 3;
    private static final int VALID_PHONENUM_SIZE = 11;

    private AsyncTask<String, Void, Boolean> mAskTask = null;

    // 电话 - 联系人 缓存
    private HashMap<String, String> mContactPhoneMap = new HashMap<String, String>();

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

    private Context mContext = null;

    private Activity mActivity = null;

    private View contentView = null;
    
    private List<TrafficProductInfo> trafficProductList = new ArrayList<TrafficProductInfo>();
    
    private boolean mCharging; //是否正在充值
    
    private String hasAskedPhone = ""; // 上次充流量的号码
    
    private String operatorInfo = "";//上次充流量的号码归属地
    
    private int payActionType = PaymentDesc.ID_WE_CHAT;
    
    private String localPhoneNum="";//本机号码
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = this.getActivity().getBaseContext();
        mActivity = this.getActivity();
        
        
        mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mChargHistorySPref = mActivity.getSharedPreferences(CHARGE_TELEPHONE_HISTORY,
                Activity.MODE_MULTI_PROCESS);
        initViews();
        initData();
        initShowPhoneNum();

        mActivity.registerReceiver(mNetworkReceiver, new IntentFilter(CONNECTIVITY_CHANGE_ACTION));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.putao_yellow_page_traffic, null);
        return contentView;
    }

    private void initViews() {
        
        mPaymentLayout = (PaymentViewGroup)contentView.findViewById(R.id.charge_payment_layout);
        mPaymentLayout.selectPayAction(payActionType);
        mPaymentLayout.setOnPaymentActionSelectedListener(this);
        mPaymentLayout.setPaymentCallback(this);
        
        mTrafficGridView = (GridView)contentView.findViewById(R.id.traffic_gridview);
        
//        mAttentionLayout = (LinearLayout)contentView.findViewById(R.id.charge_attention);
//        mAttentionFirstText = (TextView)contentView.findViewById(R.id.attention_content_first);
//        mAttentionFourthText = (TextView)contentView.findViewById(R.id.attention_content_fourth);
//        mAttentionFifthText = (TextView)contentView.findViewById(R.id.attention_content_five);
//        mAttentionSixthText = (TextView)contentView.findViewById(R.id.attention_content_six);
        
        mNumberEditText = (EditText)contentView.findViewById(R.id.charge_edit);
        mNameTView = (TextView)contentView.findViewById(R.id.charge_name);

        mChargeLayout = (RelativeLayout)contentView.findViewById(R.id.charge_confirm);
        mChargeContentTView = (TextView)contentView.findViewById(R.id.charge_confirm_content);
        mChargeWaitTView = (TextView)contentView.findViewById(R.id.charge_confirm_wait);
        setChargeLayoutDisable(true);

        mClearInput = (ImageView)contentView.findViewById(R.id.clear_search);
        mOperatorTView = (TextView)contentView.findViewById(R.id.operator_text);
        
        mTrifficTypeAdapter = new TrafficProductAdapter(trafficProductList);
        mTrafficGridView.setAdapter(mTrifficTypeAdapter);
        mTrafficGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
             // update by hyl 2014-8-9 start
                TrafficProductInfo productInfo = trafficProductList.get(position);
                if (mCharging){
                    return;
                }
                // update by hyl 2014-8-9 end

                mSelectPos = position;
                mHistoryLayout.setVisibility(View.GONE);
                showInputManager(false);
                mTrifficTypeAdapter.notifyDataSetChanged();
                
                setProductInfo(productInfo);
                /*
                 * bug:当网络不稳定时，上一次没有查询到价格列表，用户再次点击价格时 应该重新去获取价格列表
                 * updaye by hyl 2014-8-16 start
                 * old code: checkAskedPhone(mNumberEditText.getText().toString());
                 */
//                checkAndRequestPrice(mNumberEditText.getText().toString());
                //updaye by hyl 2014-8-16 end
                
            }
        });
//        mTypeChooseListView.setOnItemClickListener(new OnItemClickListener(){
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                mSelectPos = position;
//                TrafficProductInfo productInfo = (TrafficProductInfo)mTrifficTypeAdapter.getItem(position);
//                setProductInfo(productInfo);
//            }
//        });
        
        mHistoryLayout = (LinearLayout)contentView.findViewById(R.id.chargehistory_layout);
        mHistoryListView = (ListView)contentView.findViewById(R.id.chargehistory_list);
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
                //modify 2015-01-06 xcx start 号码格式化
//                mNumberEditText.setText(phoneNum);
//                mNumberEditText.setSelection(phoneNum.length());
                setPhoneNum(phoneNum);
                //modify 2015-01-06 xcx end 号码格式化
                updateContactName(phoneNum);
                MobclickAgentUtil.onEvent(mContext, 
                        UMengEventIds.DISCOVER_YELLOWPAGE_CHARGE_SELECT_HISTORY);
            }
        });
        
        mNumberEditText.setOnClickListener(this);
        mChargeLayout.setOnClickListener(this);
        mNameTView.setOnClickListener(this);
        mClearInput.setOnClickListener(this);
        
        mNumberEditText.setOnClickListener(this);
        mNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence str, int arg1, int arg2, int arg3) {
                LogUtil.d(TAG, "onTextChanged");
                String contents = str.toString();
                Utils.setEditPhoneNumFormat(contents, mNumberEditText);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                if (TextUtils.isEmpty(arg0.toString())) {
                    hasAskedPhone="";
                    mClearInput.setImageResource(R.drawable.putao_icon_contacts);
                    showInputManager(true);
                    refreshChargeHistoryLayout();
                    disableCharge();
                    
                } else {
                    mClearInput.setImageResource(R.drawable.putao_icon_clear_s);
                    mHistoryLayout.setVisibility(View.GONE);
                    checkAndRequestPrice(arg0.toString());
                }
            }
        });
        
        TextView question = (TextView)contentView.findViewById(R.id.question);
        question.setOnClickListener(this);
    }
    
    private void disableCharge(){
        showNameData("");
        showPhoneNumData(PHONE_STATE_SPACE, "");
        setDefaultStrafficList();
        mTrifficTypeAdapter.setProductList(trafficProductList);
        mTrifficTypeAdapter.notifyDataSetChanged();
        //清除价格 
        clearPayPriceMessage();
    }
    
    private void initData() {
        mSelectPos = DEFAULT_SELECT_POS;
        hasAskedPhone = "";
        operatorInfo = "";
        mNumberEditText.setText("");
        isFirstInit = true;
    }
    
    private void initShowPhoneNum() {
        String phoneNum = ((YellowPageReChargeActivity)mActivity).phoneNumber;
        if (TextUtils.isEmpty(phoneNum)) {
            phoneNum = PutaoAccount.getInstance().getBindMobile();
        }
        if (TextUtils.isEmpty(phoneNum)) {
            phoneNum = ContactsHubUtils.getPhoneNumber(mContext);
        }
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
            //modify 2015-01-06 xcx start 号码格式化
//            mNumberEditText.setText(phoneNum);
//            mNumberEditText.setSelection(phoneNum.length());
            setPhoneNum(phoneNum);
            checkAndRequestPrice(phoneNum);
            //modify 2015-01-06 xcx end 号码格式化
        }
    }
    
    
    private void setPhoneNum(String phoneNum){
        String newPhone=Utils.formatPhoneNum(phoneNum);
        mNumberEditText.setText(newPhone);
        mNumberEditText.setSelection(newPhone.length());
        setLocalPhoneNumName();
    }
    private BroadcastReceiver mNetworkReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (CONNECTIVITY_CHANGE_ACTION.equals(action)) {
                LogUtil.i(TAG, "network is changed!");
                // 如果是从无网络到有网络变化 且 之前没有查询过
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
            disableCharge();
            return "";
        }
        // 检测是否是合法的号码
        if (!TelAreaUtil.getInstance().isValidMobile(phoneNum)) {
            // 号码不合法
            disableCharge();
            showPhoneNumData(PHONE_STATE_ERROR, "");
            return "";
        }
        // 输入合法的号码后隐藏输入法
        showInputManager(false);
        // 检测并显示联系人信息
        updateContactName(phoneNum);
        // 检测网络
        if (!NetUtil.isNetworkAvailable(mContext)) {
            // 没有网络则初始化价格列表
            disableCharge();
            Utils.showToast(mContext, R.string.putao_no_net, false);
            return "";
        }
       return phoneNum;
    }
    
//    private void showAttentionLayout(String operatorInfo){
//      mAttentionLayout.setVisibility(View.VISIBLE);
//      mAttentionFirstText.setText(getString(R.string.putao_traffic_tips_first,operatorInfo));
//      if(operatorInfo.contains("联通")){
//          mAttentionFifthText.setVisibility(View.VISIBLE);
//          mAttentionSixthText.setVisibility(View.VISIBLE);
//      }else if(operatorInfo.contains("电信")){
//          mAttentionFourthText.setText(R.string.putao_traffic_tips_fourth_telecom);
//      }else if(operatorInfo.contains("北京")){
//          mAttentionFifthText.setVisibility(View.VISIBLE);
//          mAttentionFifthText.setText(R.string.putao_traffic_tips_five_mobile);
//      }else if(operatorInfo.contains("广东")){
//          
//      }
//    }
    
    private void setProductInfo(TrafficProductInfo productInfo){
        if(productInfo != null){
            //刷新 价格 
            if(!TextUtils.isEmpty(productInfo.getPutao_price())){
                selectPayChannel(productInfo.getPutao_price());
                setChargeLayoutDisable(false);
            }else{
                clearPayPriceMessage();
                setChargeLayoutDisable(true);
            }
        }
    }
    
    private void doChargeTraffic(String phone,TrafficProductInfo productInfo) {
        if ( !PutaoAccount.getInstance().isLogin() ) {
            Toast.makeText(mContext, R.string.putao_yellow_page_try_login, Toast.LENGTH_SHORT).show();
            PutaoAccount.getInstance().silentLogin(this);
        } else {
            mCharging = true; // 充值中...

            mHandler.sendEmptyMessageDelayed(MSG_QUERYING_ACTION, 500); // 正在查询中...

            // 获取订单信息和支付
            String pro_id = String.valueOf(productInfo.getProd_id());
            String price = productInfo.getPutao_price();
            int priceInCent=0;;
            try{
                priceInCent = (int)((Float.parseFloat(price)*100));
            }catch(Exception e){
                e.printStackTrace();
                return ;
            }

            GetOrderParam param = new GetOrderParam();
            param.setProductId(ProductTypeCode.Flow.ProductId);
            param.setProductType(ProductTypeCode.Flow.ProductType);

            param.putSubObj("prodid", pro_id);
            param.putSubObj("prod_price", String.valueOf(priceInCent));
            param.putSubObj("mobilenum", phone);
            param.putSubObj("order_title", productInfo.getTraffic_value());
            param.putSubObj("content",operatorInfo);
            
            param.putUIPair("mobile_ui", phone + "  " + mOperatorTView.getText().toString());
            param.putUIPair("traffic_value", productInfo.getTraffic_value());
            
            param.setPriceInCents(priceInCent);
            addUmengEvent(param);
            mPaymentLayout.startPayment(param);
        }
    }
    
    /**
     * 有盟统计 
     * @author xcx
     * @param orderParam
     */
    private void addUmengEvent(GetOrderParam orderParam){
        StringBuffer uMengSuccessIds = new StringBuffer();
        StringBuffer uMengFailIds = new StringBuffer();
        uMengSuccessIds.append(UMengEventIds.DISCOVER_YELLOWPAGE_PHONE_RECHARGE_FLOW_SUCCESS);
        uMengFailIds.append(UMengEventIds.DISCOVER_YELLOWPAGE_PHONE_RECHARGE_FLOW_FAIL);
        orderParam.putUIPair(UMengEventIds.EXTRA_UMENG_EVENT_IDS_SUCCESS, uMengSuccessIds.toString());
        orderParam.putUIPair(UMengEventIds.EXTRA_UMENG_EVENT_IDS_FAIL, uMengFailIds.toString());
    }
    /**
     * 清除“支付方式”按钮 信息
     */
    private void clearPayPriceMessage(){
        for (int i = 0, max = PaymentDesc.ALL_PAY_ACTS.length; i < max; i++) {
            mPaymentLayout.setAmountText(PaymentDesc.ALL_PAY_ACTS[i], "");
        }
    }
    
    /**
     * 选择不同的支付渠道
     */
    private void selectPayChannel(String price){
        for (int i = 0, max = PaymentDesc.ALL_PAY_ACTS.length; i < max; i++) {
            mPaymentLayout.setAmountText(
                    PaymentDesc.ALL_PAY_ACTS[i],
                    String.format(mContext.getResources().getString(R.string.putao_pay_charge_price),
                            price));
        }
    }

    /**
     * 检测号码并请求价格
     */
    private void checkAndRequestPrice(String phoneNum) {
        //modify 2015-01-06 xcx start 号码格式化
        phoneNum=phoneNum.replace(" ", ""); 
        //modify 2015-01-06 xcx end 号码格式化
        String validPhoneNum = checkAskedPhone(phoneNum);
        if (TextUtils.isEmpty(validPhoneNum)) {
            return;
        }
        
     // 显示号码归属地信息
        operatorInfo = getOperatorInfo(phoneNum);
        if (!TextUtils.isEmpty(operatorInfo)) {
            showPhoneNumData(PHONE_STATE_OPERATOR, operatorInfo);
//            showAttentionLayout(operatorInfo);
        }
        // 询价任务
        if (mAskTask == null || mAskTask != null
                && mAskTask.getStatus() != AsyncTask.Status.RUNNING) {
            mAskTask = new AskTrafficPriceTask();
//            mAskTask.execute(validPhoneNum,operatorInfo);
            mAskTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, validPhoneNum,operatorInfo);
        }
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
            if (!phoneNum.equals(num)) {
                return;
            }

            showNameData(result);
        }

    }
    
    /**
     * 流量查询任务
     */
    private class AskTrafficPriceTask extends AsyncTask<String, Void, Boolean> {

        String mobile = "";
        
        List<TrafficProductInfo> beanList = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... args) {
            LogUtil.d(TAG, "doInBackground");
            mobile = args[0];
            operatorInfo = args[1];
            if(!TextUtils.isEmpty(operatorInfo)){
                operatorInfo = operatorInfo.replace(" ", "");
            }
            try {
                //Test
                TrafficProductResponseBean product = ChargeUtils.qryTrafficPrice(mobile,operatorInfo);
                if(null==product){
                    return false;
                }
                operatorInfo = product.getAcc_type();
                beanList = product.getList();
            } catch (Exception e) {
                LogUtil.d(TAG, "AskMobilePriceTask query mobile price exception...");
                e.printStackTrace();
                // add by hyl 2014-8-16 start
                mHandler.sendEmptyMessage(MSG_ASKMOBILEPRICE_EXCEPTION_ACTION);
                return false;
                // add by hyl 2014-8-16 end
            }

            if (beanList == null || beanList.size() <= 0) {
                LogUtil.d(TAG, "AskMobilePriceTask qryMobilePrice size is null or 0.");
                mHandler.sendEmptyMessage(MSG_NO_SERVER_DATA_ACTION);
                return false;
            }
            // 把最新数据按从小到大排序
            Collections.sort(beanList, new Comparator<TrafficProductInfo>() {
                @Override
                public int compare(TrafficProductInfo arg0, TrafficProductInfo arg1) {
                    if((Float.parseFloat(arg0.getPutao_price()) - Float.parseFloat(arg1.getPutao_price()))<0){
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
            if(!TextUtils.isEmpty(operatorInfo) && operatorInfo.length() >= 2){
                String info = operatorInfo.substring(0, operatorInfo.length() - 2) + " " + operatorInfo.substring(operatorInfo.length() -2);
                mOperatorTView.setText(info);
            }else{
                mOperatorTView.setText("");
            }
            if (founded) {
                hasAskedPhone = ContactsHubUtils.formatIPNumber(mobile, mContext);
                trafficProductList.clear();
                trafficProductList.addAll(beanList);
                mSelectPos=DEFAULT_SELECT_POS;
                setProductInfo(trafficProductList.get(mSelectPos));
            } else {
                // 没有询价结果或网络繁忙
                setDefaultStrafficList();
            }
            if(mTrifficTypeAdapter != null){
                mTrifficTypeAdapter.setProductList(trafficProductList);
                mTrifficTypeAdapter.notifyDataSetChanged();
            }
            super.onPostExecute(founded);
        }
    }
    
    /**
     * 添加默认50M和200M的item，无实际效果，只为界面显示
     * @author xcx
     */
    private void setDefaultStrafficList(){
        trafficProductList.clear();
        TrafficProductInfo info1=new TrafficProductInfo();
        info1.setTraffic_value(""+50+"M");
        info1.setPutao_price("");
        trafficProductList.add(info1);
        
        TrafficProductInfo info2=new TrafficProductInfo();
        info2.setTraffic_value(""+200+"M");
        info2.setPutao_price("");
        trafficProductList.add(info2);
        setChargeLayoutDisable(true);
    }
    
    private class TrafficProductAdapter extends BaseAdapter {

        private List<TrafficProductInfo> productList = null;
        
        public TrafficProductAdapter(List<TrafficProductInfo> prodList){
            productList = prodList;
        }
        
        @Override
        public int getCount() {
            return productList.size();
        }

        @Override
        public Object getItem(int arg0) {
            return productList.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int arg0, View view, ViewGroup arg2) {
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(
                        R.layout.putao_yellow_page_charge_item, null);
            }
            TrafficProductInfo product = productList.get(arg0);
            TextView tv = (TextView)view.findViewById(R.id.face_price_text);
            tv.setText(product.getTraffic_value());
            if (mSelectPos == arg0) {
                view.setBackgroundResource(R.drawable.putao_bg_white_select);
                tv.setTextColor(mContext.getResources().getColor(R.color.putao_light_green));
            } else {
                view.setBackgroundResource(R.drawable.putao_bg_white);
                tv.setTextColor(mContext.getResources().getColor(R.color.putao_pt_deep_gray));
            }
            return view;
        }
        
        public void setProductList(List<TrafficProductInfo> productList) {
            this.productList = productList;
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
                mOperatorTView.setText(mContext.getResources().getString(R.string.putao_charge_phonenum_error));
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
            mChargeLayout.getBackground().setAlpha(80);
            mChargeLayout.setClickable(false);
        } else {
            mChargeLayout.getBackground().setAlpha(255);
            mChargeLayout.setClickable(true);
            mCharging = false;
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
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
//                    mNumberEditText.setText(phoneNum);
//                    mNumberEditText.setSelection(selection);
                    setPhoneNum(phoneNum);
                    if (TextUtils.isEmpty(phoneNum) || phoneNum.length() < VALID_PHONENUM_SIZE
                            || !TelAreaUtil.getInstance().isValidMobile(phoneNum)) {
                        // 如果号码为空或号码长度小于11（为不完整号码） 或者 检测是否是合法的号码
//                        showPriceData(PRICE_STATE_PRICERANGE);
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
                    //TODO 暂不支持
//                    mTypeTextView.setText(R.string.putao_charge_getchargeinfo_failed);
                    if (!TextUtils.isEmpty(mNumberEditText.getText().toString())) {
                        for (int i = 0, max = PaymentDesc.ALL_PAY_ACTS.length; i < max; i++) {
                            mPaymentLayout.setAmountText(PaymentDesc.ALL_PAY_ACTS[i], getString(R.string.putao_charge_getchargeinfo_failed));
                        }
                    }
                    break;
                case MSG_ASKMOBILEPRICE_EXCEPTION_ACTION:// add by hyl 2014-8-16
                    Toast.makeText(mContext, R.string.putao_charge_query_price_fail, Toast.LENGTH_SHORT)
                            .show();
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
                    mChargeContentTView.setText(R.string.putao_charge_charging);
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
        mChargeContentTView.setText(mContext.getResources().getString(R.string.putao_charge_immediately));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
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
        switch (id) {
            case R.id.clear_search:
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
                break;
            case R.id.charge_confirm:
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
                showInputManager(false);
                addPhoneNum(phoneNum);
                TrafficProductInfo product = trafficProductList.get(mSelectPos);
                if (product != null) {
                    setChargeLayoutDisable(true);
                    mHistoryLayout.setVisibility(View.GONE);
                    doChargeTraffic(phoneNum, product);
                }
                break;
            case R.id.charge_name:
                mNumberEditText.requestFocus();
                String editText = mNumberEditText.getText().toString();
                if (!TextUtils.isEmpty(editText)) {
                    mNumberEditText.setSelection(editText.length());
                } else {
                    refreshChargeHistoryLayout();
                }
                showInputManager(true);
                break;
            case R.id.charge_edit:
                if(!mCharging){
                    refreshChargeHistoryLayout();
                }
                break;
            case R.id.question:
                intent = new Intent(mContext,TrafficQuestionActivity.class);
                startActivity(intent);
                break;
            default:
                break;
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
     * 获取充值电话 历史列表
     */
    private ArrayList<ChargeHistoryItem> getPhoneNumHistoryList() {
        String histyryStr = mChargHistorySPref.getString(CHARGE_TELEPHONE_KEY, null);
        if (TextUtils.isEmpty(histyryStr)) {
            return null;
        }
        String[] historyBeans = histyryStr
                .split(ConstantsParameter.SHAREDPREFERENCES_DATA_DELIMITER);
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
    
    /**
     * 刷新显示充值电话历史列表
     */
    private void refreshChargeHistoryLayout() {
//        if (isFirstInit) {
//            isFirstInit = false;
//            return;
//        }
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
     * 在充值电话列表中清除电话号码clearPhone
     */
    private void clearPhoneNumHistory(String clearPhone, boolean needRefreshAdapter) {
        String histyryStr = mChargHistorySPref.getString(CHARGE_TELEPHONE_KEY, null);
        if (TextUtils.isEmpty(histyryStr)) {
            // 修复BUG #1623 充值，最近一条充值号码记录无法删除。
            //modify by lisheng 2014-10-18 22:18:54
            mHistoryList.clear();
            mHistoryAdapter.setData(mHistoryList);
            //  modify by lisheng 2014-10-18 22:18:54 end
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
    
    @Override
    public void onResume() {
        LogUtil.i(TAG, "onResume test... ");
        MobclickAgentUtil.onResume(mContext);
        
        startTime = System.currentTimeMillis();

        if(TextUtils.isEmpty(hasAskedPhone)){
            setChargeLayoutDisable(true);
         }else{
            setChargeLayoutDisable(false);
         }
        clearChargeBtnMessage();
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        MobclickAgentUtil.onPause(mContext);
        try {
            int time = ((int)((System.currentTimeMillis() - startTime) / 1000));
            Map<String, String> map_value = new HashMap<String, String>();
            map_value.put("type", this.getClass().getName());
            MobclickAgentUtil.onEventValue(mContext, UMengEventIds.DISCOVER_YELLOWPAGE_RESUME_TIME_61,
                    map_value, time);
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
    public void onActionSelected(PaymentDesc desc, PaymentActionView view) {
        payActionType = desc.actionType;
    }

    @Override
    public void onPaymentFeedback(int actionType, Throwable t, int resultCode,
            Map<String, String> extras) {
        setChargeLayoutDisable(false);
        clearChargeBtnMessage();
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
    public void onCancel() {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public Integer getAdId() {
        return AdCode.ADCODE_YellowPageTrafficTelephoneFragment;
    }
}
