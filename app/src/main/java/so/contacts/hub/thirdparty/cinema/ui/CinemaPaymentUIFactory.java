
package so.contacts.hub.thirdparty.cinema.ui;

import so.contacts.hub.payment.core.PaymentDesc;
import so.contacts.hub.payment.data.ResultCode;
import so.contacts.hub.payment.ui.PaymentResultHintUI;
import so.contacts.hub.payment.ui.PaymentResultUI;
import so.contacts.hub.payment.ui.PaymentResultUIFactory;
import android.util.SparseArray;

import com.yulong.android.contacts.discover.R;

public class CinemaPaymentUIFactory implements PaymentResultUIFactory {

    public CinemaPaymentUIFactory() {
    }

    protected SparseArray<PaymentResultHintUI> createWeChatHint() {
        int successIcon = R.drawable.putao_icon_exp_checkbox_p;
        int failIcon = R.drawable.putao_icon_logo_failed;
        SparseArray<PaymentResultHintUI> result = new SparseArray<PaymentResultHintUI>();
        PaymentResultHintUI desc = new PaymentResultHintUI();
        desc.infoTxt.valueResId = R.string.putao_movie_deal_failed;
        desc.hintTxt.valueResId = R.string.putao_movie_deal_failed_hint_weixin;
        desc.hintTxt.paramKey = "total_fee";
        desc.iconId = failIcon;
        result.put(ResultCode.OrderStatus.Failed, desc);

        desc = new PaymentResultHintUI();
        desc.infoTxt.valueResId = R.string.putao_movie_deal_success;
        desc.hintTxt.valueResId = R.string.putao_movie_deal_success_hint;
        desc.hintTxt.paramKey = "movie_ticket";
        desc.iconId = successIcon;
        result.put(ResultCode.OrderStatus.Success, desc);

        desc = new PaymentResultHintUI();
        desc.infoTxt.valueResId = R.string.putao_movie_deal_success;
        desc.hintTxt.valueResId = R.string.putao_movie_deal_failed_tips_for_pending;
        desc.iconId = successIcon;
        result.put(ResultCode.OrderStatus.Pending, desc);

        desc = new PaymentResultHintUI();
        desc.iconId = failIcon;
        desc.infoTxt.valueResId = R.string.putao_movie_deal_failed;
        desc.hintTxt.valueResId = R.string.putao_movie_deal_failed_cancel;
        result.put(ResultCode.OrderStatus.Cancel, desc);
        result.put(ResultCode.OrderStatus.WaitForPayment, desc);
        return result;
    }

    protected SparseArray<PaymentResultHintUI> createAliPayHint() {
        int successIcon = R.drawable.putao_icon_exp_checkbox_p;
        int failIcon = R.drawable.putao_icon_logo_failed;
        SparseArray<PaymentResultHintUI> result = new SparseArray<PaymentResultHintUI>();
        PaymentResultHintUI desc = new PaymentResultHintUI();
        desc.infoTxt.valueResId = R.string.putao_movie_deal_failed;
        desc.hintTxt.valueResId = R.string.putao_movie_deal_failed_hint;
        desc.hintTxt.paramKey = "total_fee";
        desc.iconId = failIcon;
        result.put(ResultCode.OrderStatus.Failed, desc);

        desc = new PaymentResultHintUI();
        desc.infoTxt.valueResId = R.string.putao_movie_deal_success;
        desc.hintTxt.valueResId = R.string.putao_movie_deal_success_hint;
        desc.hintTxt.paramKey = "movie_ticket";
        desc.iconId = successIcon;
        result.put(ResultCode.OrderStatus.Success, desc);

        desc = new PaymentResultHintUI();
        desc.infoTxt.valueResId = R.string.putao_movie_deal_success;
        desc.hintTxt.valueResId = R.string.putao_movie_deal_failed_tips_for_pending;
        desc.iconId = successIcon;
        result.put(ResultCode.OrderStatus.Pending, desc);

        desc = new PaymentResultHintUI();
        desc.iconId = failIcon;
        desc.infoTxt.valueResId = R.string.putao_movie_deal_failed;
        desc.hintTxt.valueResId = R.string.putao_movie_deal_failed_cancel;
        result.put(ResultCode.OrderStatus.Cancel, desc);
        result.put(ResultCode.OrderStatus.WaitForPayment, desc);

        return result;
    }

    @Override
    public PaymentResultUI createUI() {
        PaymentResultUI ui = new PaymentResultUI();
        ui.setTitle(R.string.putao_film_ticket, null);
        ui.addDynamicRow(R.string.putao_charge_serialnum_hint, -1, "out_trade_no");
//        ui.addDynamicRow(R.string.putao_movie_deal_result_unit_hint, -1, "company");
//        ui.addDynamicRow(R.string.putao_movie_deal_result_usercode_hint, -1, "account");
//        ui.addDynamicRow(R.string.putao_movie_deal_result_money_hint,
//                R.string.putao_yellow_page_detail_customsprice, "total_fee");
        
        //add by hyl 2015-1-8
        ui.addDynamicRow(R.string.putao_movie_pay_info, -1, "movie_name");
        
        ui.putHintByPaymentAction(PaymentDesc.ID_ALIPAY, createAliPayHint());
        ui.putHintByPaymentAction(PaymentDesc.ID_WE_CHAT, createWeChatHint());
        return ui;
    }
}
