
package so.contacts.hub.ui.yellowpage.payment.uifac;

import so.contacts.hub.payment.core.PaymentDesc;
import so.contacts.hub.payment.data.ResultCode;
import so.contacts.hub.payment.ui.PaymentResultHintUI;
import so.contacts.hub.payment.ui.PaymentResultUI;
import so.contacts.hub.payment.ui.PaymentResultUIFactory;
import so.contacts.hub.ui.yellowpage.ChargeQuestionActivity;
import android.util.SparseArray;

import com.yulong.android.contacts.discover.R;

public class TelephonePaymentUIFactory implements PaymentResultUIFactory {

    public TelephonePaymentUIFactory() {
    }

    @Override
    public PaymentResultUI createUI() {
        PaymentResultUI result = new PaymentResultUI();
        result.setTitle(R.string.putao_charge_tag_title_charge, null);
//        result.setQuestionRes(R.string.putao_charge_question);
//        result.setQuestionActivityClass(ChargeQuestionActivity.class);
        result.addDynamicRow(R.string.putao_charge_serialnum_hint, -1, "out_trade_no");
        result.addDynamicRow(R.string.putao_charge_phonenum_hint, -1, "mobile_ui");
        result.addDynamicRow(R.string.putao_charge_money_hint,
                R.string.putao_yellow_page_detail_customsprice, "traffic_value");
        result.putHintByPaymentAction(PaymentDesc.ID_ALIPAY, createAliPayHint());
        result.putHintByPaymentAction(PaymentDesc.ID_WE_CHAT, createWeChatHint());
//        result.setQuestionRes(R.string.putao_charge_question);
        return result;
    }

    protected SparseArray<PaymentResultHintUI> createWeChatHint() {
        int successIcon = R.drawable.putao_icon_transaction_success;
        int failIcon = R.drawable.putao_icon_transaction_error;
        SparseArray<PaymentResultHintUI> result = new SparseArray<PaymentResultHintUI>();
        PaymentResultHintUI desc = new PaymentResultHintUI();
        desc.hintTxt.valueResId = R.string.putao_charge_deal_failed_hint_weixin;
        desc.infoTxt.valueResId = R.string.putao_charge_deal_failed;
        desc.iconId = failIcon;
        result.put(ResultCode.OrderStatus.Failed, desc);

        desc = new PaymentResultHintUI();
        desc.infoTxt.valueResId = R.string.putao_charge_deal_success;
        desc.hintTxt.paramKey = "mark_price";
        desc.iconId = successIcon;
        desc.hintTxt.valueResId = R.string.putao_charge_tips_for_success;
        result.put(ResultCode.OrderStatus.Success, desc);

        desc = new PaymentResultHintUI();
        desc.infoTxt.valueResId = R.string.putao_charge_deal_success;
        desc.hintTxt.valueResId = R.string.putao_charge_tips_for_pending;
        desc.iconId = successIcon;
        result.put(ResultCode.OrderStatus.Pending, desc);

        desc = new PaymentResultHintUI();
        desc.iconId = failIcon;
        desc.infoTxt.valueResId = R.string.putao_charge_deal_failed;
        desc.hintTxt.valueResId = R.string.putao_charge_deal_failed_cancel;
        result.put(ResultCode.OrderStatus.Cancel, desc);
        result.put(ResultCode.OrderStatus.WaitForPayment, desc);
        
        desc = new PaymentResultHintUI();
        desc.infoTxt.valueResId = R.string.putao_charge_deal_ask_for_refund;
        desc.hintTxt.valueResId = R.string.putao_charge_traffic_tips_for_ask_for_refund;
        desc.iconId = successIcon;
        result.put(ResultCode.OrderStatus.AskForRefund, desc);
        
        desc = new PaymentResultHintUI();
        desc.infoTxt.valueResId = R.string.putao_charge_deal_refunded;
        desc.hintTxt.valueResId = R.string.putao_charge_traffic_tips_for_refund;
        desc.iconId = successIcon;
        result.put(ResultCode.OrderStatus.Refunded, desc);
        
        desc = new PaymentResultHintUI();
        desc.infoTxt.valueResId = R.string.putao_charge_deal_out_of_date;
        desc.hintTxt.valueResId = R.string.putao_charge_traffic_tips_for_out_of_date;
        desc.iconId = successIcon;
        result.put(ResultCode.OrderStatus.OutOfDate, desc);
        return result;
    }

    protected SparseArray<PaymentResultHintUI> createAliPayHint() {
        int successIcon = R.drawable.putao_icon_transaction_success;
        int failIcon = R.drawable.putao_icon_transaction_error;
        SparseArray<PaymentResultHintUI> result = new SparseArray<PaymentResultHintUI>();
        PaymentResultHintUI desc = new PaymentResultHintUI();
        desc.hintTxt.valueResId = R.string.putao_charge_deal_failed_hint;
        desc.infoTxt.valueResId = R.string.putao_charge_deal_failed;
        desc.iconId = failIcon;
        result.put(ResultCode.OrderStatus.Failed, desc);

        desc = new PaymentResultHintUI();
        desc.infoTxt.valueResId = R.string.putao_charge_deal_success;
        desc.hintTxt.paramKey = "mark_price";
        desc.iconId = successIcon;
        desc.hintTxt.valueResId = R.string.putao_charge_tips_for_success;
        result.put(ResultCode.OrderStatus.Success, desc);

        desc = new PaymentResultHintUI();
        desc.infoTxt.valueResId = R.string.putao_charge_deal_success;
        desc.hintTxt.valueResId = R.string.putao_charge_tips_for_pending;
        desc.iconId = successIcon;
        result.put(ResultCode.OrderStatus.Pending, desc);

        desc = new PaymentResultHintUI();
        desc.iconId = failIcon;
        desc.infoTxt.valueResId = R.string.putao_charge_deal_failed;
        desc.hintTxt.valueResId = R.string.putao_charge_deal_failed_cancel;
        result.put(ResultCode.OrderStatus.Cancel, desc);
        result.put(ResultCode.OrderStatus.WaitForPayment, desc);
        
        desc = new PaymentResultHintUI();
        desc.infoTxt.valueResId = R.string.putao_charge_deal_ask_for_refund;
        desc.hintTxt.valueResId = R.string.putao_charge_traffic_tips_for_ask_for_refund;
        desc.iconId = successIcon;
        result.put(ResultCode.OrderStatus.AskForRefund, desc);
        
        desc = new PaymentResultHintUI();
        desc.infoTxt.valueResId = R.string.putao_charge_deal_refunded;
        desc.hintTxt.valueResId = R.string.putao_charge_traffic_tips_for_refund;
        desc.iconId = successIcon;
        result.put(ResultCode.OrderStatus.Refunded, desc);
        
        desc = new PaymentResultHintUI();
        desc.infoTxt.valueResId = R.string.putao_charge_deal_out_of_date;
        desc.hintTxt.valueResId = R.string.putao_charge_traffic_tips_for_out_of_date;
        desc.iconId = successIcon;
        result.put(ResultCode.OrderStatus.OutOfDate, desc);
        return result;
    }
}
