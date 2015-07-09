
package so.contacts.hub.payment.core;

import java.util.ArrayList;
import java.util.List;

import so.contacts.hub.payment.action.AliPayPaymentAction;
import so.contacts.hub.payment.action.WeChatPaymentAction;
import android.app.Activity;

import com.yulong.android.contacts.discover.R;

/**
 * 支付描述, 用于存储支付UI, Action等相关的数据
 * @author Steve Xu 徐远同
 *
 */
public class PaymentDesc {
    /**
     * 支付宝的支付类型标识
     */
    public static final int ID_ALIPAY = 1;
    /**
     * 微信的支付类型标识
     */
    public static final int ID_WE_CHAT = 2;

    /**
     * 图标ID
     */
    public int iconId;

    /**
     * 支付ID, 对应ID_开头的常量
     */
    public int actionType;

    /**
     * 支付显示字段, 如微信则显示为"微信"
     */
    public int sdkLabel;

    /**
     * 支付操作
     */
    public PaymentAction action;

    public int emptyString;

    public int pricePrefix;

    public int selector;

    /**
     * 获取系统当前支持的全部支付方式ID
     */
    public static final int[] ALL_PAY_ACTS = new int[] {
            ID_WE_CHAT, ID_ALIPAY
    };

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + iconId;
        result = prime * result + actionType;
        result = prime * result + sdkLabel;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PaymentDesc other = (PaymentDesc)obj;
        if (iconId != other.iconId)
            return false;
        if (actionType != other.actionType)
            return false;
        if (sdkLabel != other.sdkLabel)
            return false;
        return true;
    }

    /**
     * 获取默认的支付描述文件列表, 当前为支付宝和微信
     * @param ctx
     * @return
     */
    public static List<PaymentDesc> createDefaultList(Activity ctx) {
        List<PaymentDesc> list = new ArrayList<PaymentDesc>(2);
        PaymentDesc pi = new PaymentDesc();
        pi.action = new WeChatPaymentAction(ctx);
        pi.actionType = PaymentDesc.ID_WE_CHAT;
        //old code : pi.emptyString = R.string.putao_pay_by_wechat;
        //modity start by ljq 2015/01/22 暂不需要初始化描述信息
        pi.emptyString = -1;
        //modity end by ljq 2015/01/22 暂不需要初始化描述信息
        pi.pricePrefix = R.string.putao_pay_by_wechat;
        pi.iconId = R.drawable.putao_icon_pay_wxzf;
        pi.sdkLabel = R.string.putao_pay_by_wechat;
        pi.selector = R.drawable.putao_list_selecter_s;
        list.add(pi);

        pi = new PaymentDesc();
        pi.actionType = PaymentDesc.ID_ALIPAY;
        pi.action = new AliPayPaymentAction(ctx);
        //old code : pi.emptyString = R.string.putao_pay_by_alipay;
        //modity start by ljq 暂不需要初始化描述信息
        pi.emptyString = -1;
        //modity end by ljq 暂不需要初始化描述信息
        pi.iconId = R.drawable.putao_icon_pay_zfbzf;
        pi.sdkLabel = R.string.putao_pay_by_alipay;
        pi.pricePrefix = R.string.putao_pay_by_alipay;
        pi.selector = R.drawable.putao_list_selecter_s;
        list.add(pi);
        return list;
    }
}
