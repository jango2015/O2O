
package so.contacts.hub.payment.core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import so.contacts.hub.active.ActiveUtils;
import so.contacts.hub.active.bean.ActiveEggBean;
import so.contacts.hub.charge.ChargeConst;
import so.contacts.hub.charge.ChargeUtils;
import so.contacts.hub.core.Config;
import so.contacts.hub.payment.PaymentResultActivity;
import so.contacts.hub.payment.ResultUIStaticFactory;
import so.contacts.hub.payment.data.ProductTypeCode.Telephone;
import so.contacts.hub.payment.data.ResultCode;
import so.contacts.hub.payment.data.ResultCode.OrderStatus;
import so.contacts.hub.payment.data.ResultCode.PutaoServerResponse;
import so.contacts.hub.payment.ui.PaymentDynamicRowUI;
import so.contacts.hub.payment.ui.PaymentResult;
import so.contacts.hub.payment.ui.PaymentResultHintUI;
import so.contacts.hub.payment.ui.PaymentResultTextUI;
import so.contacts.hub.payment.ui.PaymentResultUI;
import so.contacts.hub.remind.BaseRemindActivity;
import so.contacts.hub.ui.yellowpage.bean.YellowParams;
import so.contacts.hub.util.LogUtil;
import so.contacts.hub.util.WebViewDialogUtils;
import so.contacts.hub.util.YellowUtil;
import so.contacts.hub.widget.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.mdroid.core.http.IgnitedHttpResponse;
import com.yulong.android.contacts.discover.R;

/**
 * 结果支付页面构造器. 在{@link PaymentResultActivity}中调用, 将{@link PaymentResultUI}
 * 转换成Activity中的视图文件.
 * 
 * @see PaymentResultActivity
 * @see PaymentResultUI
 * @author Steve Xu 徐远同
 */
public class PaymentResultUIBuilder implements OnClickListener {
    private static final String TAG = PaymentResultUIBuilder.class.getSimpleName();

    /**
     * 
     */
    private BaseRemindActivity activity;

    /**
     * 参数类型
     */
    private int productType;

    /**
     * 视图是否已经构建
     */
    private volatile boolean isViewBuilded = false;

    /**
     * 查询订单状态任务
     */
    private QueryStatusTask task;

    /**
     * 查询订单时的对话框
     */
    private ProgressDialog progressDialog = null;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 
     */
    private LinearLayout chargeResultLayout = null;

    /**
     * 充值状态图标
     */
    private ImageView resultImgView = null;

    /**
     * 充值结果文本
     */
    private TextView resultTextView = null;

    /**
     * 充值提示文本
     */
    private TextView resultHintTextView = null;

    /**
     * 付款金额文本
     */
    private TextView totalFeeTextView = null;

    private LinearLayout labelLayout = null;

    /**
     * "需知"文本
     */
    private TextView questionView = null;

    private Map<String, String> orderInfoMap = new HashMap<String, String>();

    /**
     * UI描述文件
     */
    private PaymentResultUI uiDesc;

    /**
     * 从服务器获取到的订单状态
     */
    private int serverStatus = -1;

    /**
     * 支付方式
     */
    private int actionType;

    /**
     * 本地的订单状态
     */
    private int localStatus = -1;

    public PaymentResultUIBuilder(BaseRemindActivity act, PaymentResult result) {
        this.activity = act;
        this.productType = result.getProductType();
        this.localStatus = result.getUserResultCode();
        this.orderInfoMap = result.getQueryParams();
        this.actionType = result.getActionType();
        this.orderNo = orderInfoMap.get("out_trade_no");
        uiDesc = ResultUIStaticFactory.createUI(act, result.getProductType());
    }

    public int getActionType() {
        return actionType;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public int getProductType() {
        return productType;
    }

    public void setProductType(int businessType) {
        this.productType = businessType;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Map<String, String> getOrderInfoMap() {
        return orderInfoMap;
    }

    public void setOrderInfoMap(Map<String, String> orderInfoMap) {
        this.orderInfoMap = orderInfoMap;
    }

    public void startQueryStatus() {
        if (-1 != serverStatus) {
            return;
        }
        if (null != task && task.getStatus() != Status.FINISHED) {
            return;
        }
        task = new QueryStatusTask(this);
        buildView();
        showProgressDialog();
        task.execute();
    }

    public void buildView() {
        if (!isViewBuilded) {
            isViewBuilded = true;
            initView();
        }
    }

    public void hideProgressDialog() {
        if (null != progressDialog && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void dispose() {
    }

    public void showProgressDialog() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
            progressDialog.setMessage(activity.getString(R.string.putao_charge_qry_status_ing));
        }
    }

    private void initView() {
        activity.setContentView(R.layout.putao_payment_result_activity);
        chargeResultLayout = (LinearLayout)activity.findViewById(R.id.charge_result_layout);
        labelLayout = (LinearLayout)activity.findViewById(R.id.business_label_layout);
        resultHintTextView = (TextView)activity.findViewById(R.id.charge_result_hint);

        totalFeeTextView = (TextView)activity.findViewById(R.id.charge_result_success_money);

        resultImgView = (ImageView)activity.findViewById(R.id.charge_result_img);
        resultTextView = (TextView)activity.findViewById(R.id.charge_result_text);

        questionView = (TextView)activity.findViewById(R.id.question);

        questionView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        int quesId = uiDesc.getQuestionRes();
        if (quesId > 0) {
            questionView.setOnClickListener(this);
            questionView.setText(quesId);
        } else {
            questionView.setText("");
        }

        progressDialog = new ProgressDialog(activity, R.style.putao_ChargeProgressDialog);
        progressDialog.setCancelable(false);

        activity.findViewById(R.id.back_layout).setOnClickListener(this);
        TextView titleTxt = (TextView)activity.findViewById(R.id.title);
        if (null != uiDesc.getTitle()) {
            setDynamicText(titleTxt, uiDesc.getTitle());
        } else {
            titleTxt.setText(R.string.putao_charge_tag_title_charge);
        }
        addResultView();
    }

    protected void addResultView() {
        int containerPaddingLeft = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f,
                activity.getResources().getDisplayMetrics());
        int containerPaddingTop = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f,
                activity.getResources().getDisplayMetrics());
        int keyTxtMargLeft = (int)activity.getResources().getDimension(
                R.dimen.putao_chargetel_result_paddingleft);
        int valueTxtMargLeft = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 26f,
                activity.getResources().getDisplayMetrics());
        int keyColor = activity.getResources().getColor(R.color.putao_pt_deep_gray);
        int valueColor = activity.getResources().getColor(R.color.putao_contents_text);
        int keyMinWidth = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70f, activity
                .getResources().getDisplayMetrics());
        float keyTxtSize = activity.getResources().getDimension(
                R.dimen.putao_chargetel_result_small_textsize);
        float valueTxtSize = activity.getResources().getDimension(
                R.dimen.putao_chargetel_result_big_textsize);

        for (PaymentDynamicRowUI item : uiDesc.getDynamicRowList()) {
            LinearLayout container = new LinearLayout(activity);
            LayoutParams containerLayout = new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT);
            containerLayout.gravity = Gravity.CENTER_VERTICAL;
            container.setLayoutParams(containerLayout);
            container.setPadding(containerPaddingLeft, containerPaddingTop, 0, 0);

            TextView keyTxtView = new TextView(activity);
            LayoutParams keyTxtLayout = new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
            keyTxtLayout.gravity = Gravity.CENTER_VERTICAL;
            keyTxtLayout.setMargins(keyTxtMargLeft, 0, 0, 0);
            keyTxtView.setTextColor(keyColor);
            keyTxtView.setMinWidth(keyMinWidth);
            keyTxtView.setTextSize(TypedValue.COMPLEX_UNIT_PX, keyTxtSize);
            keyTxtView.setText(item.keyResId);
            container.addView(keyTxtView, keyTxtLayout);

            TextView valueTxtView = new TextView(activity);
            LayoutParams valueTxtLayout = new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
            valueTxtLayout.gravity = Gravity.CENTER_VERTICAL;
            valueTxtLayout.setMargins(valueTxtMargLeft, 0, 0, 0);
            valueTxtView.setTextColor(valueColor);
            valueTxtView.setTextSize(TypedValue.COMPLEX_UNIT_PX, valueTxtSize);
            setDynamicText(valueTxtView, item.value);
            container.addView(valueTxtView, valueTxtLayout);
            labelLayout.addView(container);
        }
    }

    protected void updateView(int resultStatus) {
        buildView();
        if (-1 != resultStatus) {
            serverStatus = resultStatus;
        }
        labelLayout.setVisibility(View.VISIBLE);
        chargeResultLayout.setVisibility(View.VISIBLE);
        String totalFee = orderInfoMap.get("total_fee");
        totalFeeTextView.setText(String.format(
                activity.getResources().getString(R.string.putao_charge_chy_data), totalFee));
        updateHint(resultStatus);
        dealEggsIfHasAny(resultStatus);
    }

    private void dealEggsIfHasAny(int status) {
        /*
         * modify by putao_lhq at 2015年1月14日 @start
         * old code:
        ActiveEggBean egg = activity.getValidEgg();
        if (egg != null) {
            String expand_param = egg.expand_param;
            if (TextUtils.isEmpty(expand_param) || String.valueOf(status).equals(expand_param)) {
                LogUtil.i(TAG, "oh yeah, find one egg: " + egg.toString());
                WebViewDialogUtils.startWebDialog(activity, ActiveUtils.getRequrlOfSign(egg));
            }
        }*/
        ActiveEggBean egg = queryAgg(status, productType);
        if (egg == null) {
            return;
        }
        String url = ActiveUtils.getRequrlOfSign(egg);
        if (TextUtils.isEmpty(url)) {
            return;
        }
        WebViewDialogUtils.startWebDialog(activity, url);/* end by putao_lhq */
    }

    private void updateHint(int resultStatus) {
        PaymentResultHintUI desc = uiDesc.getHintByPaymentAction(actionType).get(
                Integer.valueOf(resultStatus));
        if (null != desc) {
            setDynamicText(resultTextView, desc.infoTxt);
            setDynamicText(resultHintTextView, desc.hintTxt);
            int iconId = desc.iconId;
            if (iconId <= 0) {
                switch (resultStatus) {
                    case ResultCode.OrderStatus.Pending:
                    case ResultCode.OrderStatus.Success:
                        iconId = R.drawable.putao_icon_transaction_success;
                        break;

                    default:
                        iconId = R.drawable.putao_icon_transaction_error;
                }
            }
            resultImgView.setImageResource(iconId);
        }
    }

    private void setDynamicText(TextView txt, PaymentResultTextUI desc) {
        String valueText = null;
        if (desc.valueResId > 0) {
            valueText = activity.getString(desc.valueResId);
        }

        if (null != desc.paramKey) {
            String paraValue = orderInfoMap.get(desc.paramKey);
            if (null != paraValue) {
                try {
                    paraValue = URLDecoder.decode(paraValue, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (valueText == null) {
                    valueText = paraValue;
                } else {
                    valueText = String.format(valueText, paraValue);
                }
            }
        }
        txt.setText(valueText);
    }

    static class QueryStatusTask extends AsyncTask<String, Void, Integer> {
        private WeakReference<PaymentResultUIBuilder> outterRef;

        private static final String TAG = QueryStatusTask.class.getName();

        public QueryStatusTask(PaymentResultUIBuilder act) {
            outterRef = new WeakReference<PaymentResultUIBuilder>(act);
        }

        @Override
        protected Integer doInBackground(String... params) {
            PaymentResultUIBuilder builder = outterRef.get();
            if (null == builder) {
                return -1;
            }

            String orderNum = builder.orderNo;
            int localStatus = builder.localStatus;
            int retryTime = 3;
            int count = 0;
            while (count < retryTime) {
                try {
                    // TODO FIXME 临时采取的方法，因为后台还没有做充话费
//                    if (Telephone.ProductType == builder.productType) {
//                        return oldStatusToNew(ChargeUtils
//                                .qryChargeStatus(orderNum, true, 500, 1000));
//                    } else {
                        // modify by xcx 2015-01-12 start BUG#2725 结果码为ResultCode.OrderStatus.TimeOut时要使用支付结果码处理
                        int resultInt = queryOrderStatus(orderNum, builder.productType);
                        if (ResultCode.OrderStatus.TimeOut != resultInt) {
                            return resultInt;
                        }else{
                            break;
                        }
                        // modify by xcx 2015-01-12 end BUG#2725 结果码为ResultCode.OrderStatus.TimeOut时要使用支付结果码处理
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    count++;
                    LogUtil.e(TAG, e.getMessage(), e);
                }
            }

            switch (localStatus) {
                case OrderStatus.Success:
                    return OrderStatus.Pending;

                default:
                    return localStatus;
            }
        }

        private int oldStatusToNew(int status) {
            switch (status) {
                case ChargeConst.ChargeRtnStatus_Pending:
                    return ResultCode.OrderStatus.Pending;
                case ChargeConst.ChargeRtnStatus_Ok:
                    return ResultCode.OrderStatus.Success;

                default:
                    return ResultCode.OrderStatus.Failed;
            }
        }

        private int queryOrderStatus(String orderNo, int productType) throws IOException,
                JSONException {
            IgnitedHttpResponse httpResponse = Config.getApiHttp()
                    .get(ChargeUtils.QUERY_ORDER_URL + "?order_no=" + orderNo + "&product_type=" + productType)
                    .send();
            String content = httpResponse.getResponseBodyAsString();
            JSONObject json = new JSONObject(content);
            String resultCode = json.getString("ret_code");
            if (PutaoServerResponse.ResultCodeSuccess.equals(resultCode)) {
                JSONObject data = json.getJSONObject("data");
                return data.getInt("pt_status");
            } else {
                return ResultCode.OrderStatus.TimeOut;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            PaymentResultUIBuilder builder = outterRef.get();
            if (null == builder) {
                return;
            }
            builder.hideProgressDialog();
            int resultInt = result.intValue();
            builder.updateView(resultInt);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.back_layout) {
            activity.onBackPressed();
        } else if (id == R.id.question) {
            Class<?> clazz = uiDesc.getQuestionActivityClass();
            if (null != clazz) {
                Intent intent = new Intent(activity, clazz);
                YellowParams params = new YellowParams();
                params.setTitle(activity.getResources().getString(R.string.putao_charge_question));
                params.setUrl(activity.getResources().getString(R.string.putao_charge_question_url));
                intent.putExtra(YellowUtil.TargetIntentParams, params);
                activity.startActivity(intent);
            }
        }
    }
    
    /**
     * 充值结果界面彩蛋弹出规则：
     * 当充值成功后才弹出彩蛋，同状态以及产品id匹配。
     * @param status
     * @param productId
     * @return
     * @author putao_lhq
     */
    private ActiveEggBean queryAgg(int status, int productId) {
        /*if (status == ResultCode.OrderStatus.Pending || 
                status == ResultCode.OrderStatus.Success) {
            LogUtil.d(TAG, "queryAgg: status: " + status + " ,productid: " + productId);
            return ActiveUtils.getValidEgg("PaymentResult", String.valueOf(productId));
        } else {
            return null;
        }*/
        return ActiveUtils.getValidEgg("PaymentResult", String.valueOf(productId)+ String.valueOf(status));
    }
}
