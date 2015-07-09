
package so.contacts.hub.shuidianmei;

import so.contacts.hub.payment.core.PaymentDesc;
import so.contacts.hub.payment.data.ResultCode;
import so.contacts.hub.payment.ui.PaymentResultHintUI;
import so.contacts.hub.payment.ui.PaymentResultUI;
import so.contacts.hub.payment.ui.PaymentResultUIFactory;
import android.util.SparseArray;

import com.yulong.android.contacts.discover.R;

public class WEGPaymentUIFactory implements PaymentResultUIFactory {

    public WEGPaymentUIFactory() {
    }

    protected SparseArray<PaymentResultHintUI> createWeChatHint() {
        int successIcon = R.drawable.putao_icon_transaction_success;
        int failIcon = R.drawable.putao_icon_transaction_error;
        SparseArray<PaymentResultHintUI> result = new SparseArray<PaymentResultHintUI>();
        PaymentResultHintUI desc = new PaymentResultHintUI();
        desc.infoTxt.valueResId = R.string.putao_water_eg_tag_deal_failed;
        desc.hintTxt.valueResId = R.string.putao_water_eg_tag_deal_failed_hint_weixin;
        desc.hintTxt.paramKey = "total_fee";
        desc.iconId = failIcon;
        result.put(ResultCode.OrderStatus.Failed, desc);

        desc = new PaymentResultHintUI();
        desc.infoTxt.valueResId = R.string.putao_water_eg_tag_deal_success;
        desc.hintTxt.valueResId = R.string.putao_water_eg_tag_deal_success_hint;
        desc.hintTxt.paramKey = "weg_str";
        desc.iconId = successIcon;
        result.put(ResultCode.OrderStatus.Success, desc);

        desc = new PaymentResultHintUI();
        desc.infoTxt.valueResId = R.string.putao_water_eg_tag_deal_success;
        desc.hintTxt.valueResId = R.string.putao_water_eg_tag_deal_failed_tips_for_pending;
        desc.iconId = successIcon;
        result.put(ResultCode.OrderStatus.Pending, desc);

        desc = new PaymentResultHintUI();
        desc.iconId = failIcon;
        desc.infoTxt.valueResId = R.string.putao_water_eg_tag_deal_failed;
        desc.hintTxt.valueResId = R.string.putao_water_eg_tag_deal_failed_cancel;
        result.put(ResultCode.OrderStatus.Cancel, desc);
        result.put(ResultCode.OrderStatus.WaitForPayment, desc);
        return result;
    }

    protected SparseArray<PaymentResultHintUI> createAliPayHint() {
        int successIcon = R.drawable.putao_icon_transaction_success;
        int failIcon = R.drawable.putao_icon_transaction_error;
        SparseArray<PaymentResultHintUI> result = new SparseArray<PaymentResultHintUI>();
        PaymentResultHintUI desc = new PaymentResultHintUI();
        desc.infoTxt.valueResId = R.string.putao_water_eg_tag_deal_failed;
        desc.hintTxt.valueResId = R.string.putao_water_eg_tag_deal_failed_hint;
        desc.hintTxt.paramKey = "total_fee";
        desc.iconId = failIcon;
        result.put(ResultCode.OrderStatus.Failed, desc);

        desc = new PaymentResultHintUI();
        desc.infoTxt.valueResId = R.string.putao_water_eg_tag_deal_success;
        desc.hintTxt.valueResId = R.string.putao_water_eg_tag_deal_success_hint;
        desc.hintTxt.paramKey = "weg_str";
        desc.iconId = successIcon;
        result.put(ResultCode.OrderStatus.Success, desc);

        desc = new PaymentResultHintUI();
        desc.infoTxt.valueResId = R.string.putao_water_eg_tag_deal_success;
        desc.hintTxt.valueResId = R.string.putao_water_eg_tag_deal_failed_tips_for_pending;
        desc.iconId = successIcon;
        result.put(ResultCode.OrderStatus.Pending, desc);

        desc = new PaymentResultHintUI();
        desc.iconId = failIcon;
        desc.infoTxt.valueResId = R.string.putao_water_eg_tag_deal_failed;
        desc.hintTxt.valueResId = R.string.putao_water_eg_tag_deal_failed_cancel;
        result.put(ResultCode.OrderStatus.Cancel, desc);
        result.put(ResultCode.OrderStatus.WaitForPayment, desc);

        return result;
    }

    @Override
    public PaymentResultUI createUI() {
        PaymentResultUI ui = new PaymentResultUI();
        ui.setTitle(-1, "weg_str");
        ui.addDynamicRow(R.string.putao_charge_serialnum_hint, -1, "out_trade_no");
        ui.addDynamicRow(R.string.putao_water_eg_tag_deal_result_unit_hint, -1, "company");
        ui.addDynamicRow(R.string.putao_water_eg_tag_deal_result_usercode_hint, -1, "account");
        ui.addDynamicRow(R.string.putao_water_eg_tag_deal_result_money_hint,
                R.string.putao_yellow_page_detail_customsprice, "total_fee");
        ui.putHintByPaymentAction(PaymentDesc.ID_ALIPAY, createAliPayHint());
        ui.putHintByPaymentAction(PaymentDesc.ID_WE_CHAT, createWeChatHint());
        return ui;
    }
}
