
package so.contacts.hub.payment.ui;

import java.util.ArrayList;
import java.util.List;

import android.util.SparseArray;

/**
 * 支付结果描述文件
 * 
 * @author Steve Xu 徐远同
 */
public class PaymentResultUI {
    private PaymentResultTextUI title;

    private List<PaymentDynamicRowUI> dynamicList = new ArrayList<PaymentDynamicRowUI>();

    private int questionRes;

    private Class<?> questionActivity;

    private SparseArray<SparseArray<PaymentResultHintUI>> hints = new SparseArray<SparseArray<PaymentResultHintUI>>();

    public PaymentResultTextUI getTitle() {
        return title;
    }

    /**
     * @param resId
     * @param paramKey
     * @return
     */
    public PaymentResultUI setTitle(int resId, String paramKey) {
        title = new PaymentResultTextUI(resId, paramKey);
        return this;
    }

    public List<PaymentDynamicRowUI> getDynamicRowList() {
        return dynamicList;
    }

    public PaymentResultUI setDynamicRowList(List<PaymentDynamicRowUI> dynamicList) {
        this.dynamicList = dynamicList;
        return this;
    }

    public PaymentResultUI addDynamicRow(int keyResId, int valueResId, String paramKey) {
        PaymentDynamicRowUI desc = new PaymentDynamicRowUI(keyResId, valueResId, paramKey);
        if (!this.dynamicList.contains(desc)) {
            this.dynamicList.add(desc);
        }
        return this;
    }

    public int getQuestionRes() {
        return questionRes;
    }

    public PaymentResultUI setQuestionRes(int questionRes) {
        this.questionRes = questionRes;
        return this;
    }

    public Class<?> getQuestionActivityClass() {
        return questionActivity;
    }

    public PaymentResultUI setQuestionActivityClass(Class<?> questionActivity) {
        this.questionActivity = questionActivity;
        return this;
    }

    public SparseArray<PaymentResultHintUI> getHintByPaymentAction(int action) {
        return hints.get(action);
    }

    public PaymentResultUI putHintByPaymentAction(int action,
            SparseArray<PaymentResultHintUI> header) {
        hints.put(action, header);
        return this;
    }
}
