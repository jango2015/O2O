
package so.contacts.hub.payment.ui;

import so.contacts.hub.payment.action.AbstractPaymentAction;
import so.contacts.hub.payment.action.DefaultPaymentActionFactory;
import so.contacts.hub.payment.core.PaymentDesc;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yulong.android.contacts.discover.R;

/**
 * 支付动作界面. 由三部分组成: 1. 支付方式对应的图标, 如微信的图标, 2. 支付方式对应的名称, 如"微信支付", 3. 待支付的价格文本
 * 
 * @author Steven Xu
 */
public class PaymentActionView extends RelativeLayout {
    private TextView iconText;

    private TextView amountText;

    private PaymentDesc item;

    private RadioButton checkRadio;

    private static final int RADIO_ID = 0x1000;

    public PaymentActionView(Context context) {
        super(context);
    }

    public PaymentActionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public PaymentActionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs);
    }

    public PaymentActionView(Context context, PaymentDesc item) {
        super(context);
        initView(context, item);
        this.item = item;
    }

    public PaymentDesc getPaymentDesc() {
        return item;
    }

    public void setPaymentDesc(PaymentDesc item) {
        this.item = item;
    }

    private void initView(Context ctx, AttributeSet attrs) {
        TypedArray typeArray = ctx.obtainStyledAttributes(attrs, R.styleable.putao_PaymentView);
        PaymentDesc pi = new PaymentDesc();
        try {
            if (null != typeArray && typeArray.length() > 0) {
                int iconId = typeArray.getResourceId(0, -1);
                int sdkResId = typeArray.getResourceId(1, -1);
                int selector = typeArray.getResourceId(2, -1);
                int emptyResId = typeArray.getResourceId(3, -1);
                int id = typeArray.getInt(4, -1);

                pi.iconId = iconId;
                pi.sdkLabel = sdkResId;
                pi.selector = selector;
                pi.emptyString = emptyResId;
                pi.actionType = id;

                AbstractPaymentAction action = DefaultPaymentActionFactory.createAction(pi.actionType,
                        (Activity)ctx);
                if (null == action) {
                    throw new IllegalArgumentException("unregonizable payment id:" + pi.actionType);
                }

                pi.action = action;
                initView(ctx, pi);
            }
        } finally {
            typeArray.recycle();
        }
    }

    private void initView(Context ctx, PaymentDesc item) {
        int padding = (int)getResources().getDimension(R.dimen.putao_charge_pay_way_padding);
        setPadding(padding, padding, padding, padding);
        setClickable(true);
        setId(item.actionType);

        if (item.selector > 0) {
            setBackgroundResource(item.selector);
        }

        float margin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, ctx
                .getResources().getDisplayMetrics());
        checkRadio = new RadioButton(ctx);
        checkRadio.setClickable(false);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        checkRadio.setId(RADIO_ID);
        checkRadio.setText("");
        addView(checkRadio, params);

        iconText = new TextView(ctx);
        iconText.setCompoundDrawablesWithIntrinsicBounds(item.iconId, 0, 0, 0);
        iconText.setGravity(Gravity.CENTER);
        iconText.setText(item.sdkLabel);

        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.addRule(RelativeLayout.RIGHT_OF, checkRadio.getId());
        params.setMargins((int)margin, 0, 0, 0);
        addView(iconText, params);

        amountText = new TextView(ctx);
        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        amountText.setGravity(Gravity.CENTER);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.setMargins((int)margin, 0, (int)margin, 0);
        //modity start by ljq 2015/01/22 暂不需要初始化描述信息
        if(item.emptyString == -1){
            amountText.setText("");
        }else{
            amountText.setText(item.emptyString);
        }
        //modity start by ljq 2015/01/22 暂不需要初始化描述信息
        amountText.setTextColor(getResources().getColorStateList(
                R.color.putao_payment_amount_selector));
        addView(amountText, params);
    }

    public void setSelected(boolean select) {
        checkRadio.setChecked(select);
        super.setSelected(select);
    }

    /**
     * 设置价格文本
     * 
     * @param text
     */
    public void setAmountText(String text) {
        amountText.setText(text);
    }

    public void enable(boolean enable) {
        setEnabled(enable);
        iconText.setEnabled(enable);
        amountText.setEnabled(enable);
    }
}
