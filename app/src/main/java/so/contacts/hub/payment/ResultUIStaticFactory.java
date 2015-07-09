
package so.contacts.hub.payment;

import com.amap.api.services.poisearch.Cinema;

import so.contacts.hub.payment.data.ProductTypeCode;
import so.contacts.hub.payment.ui.PaymentResultUI;
import so.contacts.hub.payment.ui.PaymentResultUIFactory;
import so.contacts.hub.shuidianmei.WEGPaymentUIFactory;
import so.contacts.hub.thirdparty.cinema.ui.CinemaPaymentUIFactory;
import so.contacts.hub.ui.yellowpage.payment.uifac.TelephonePaymentUIFactory;
import so.contacts.hub.ui.yellowpage.payment.uifac.TrafficPaymentUIFactory;
import android.content.Context;
import android.util.SparseArray;

/**
 * 生成结果UI工厂{@link PaymentResultUIFactory}的静态工厂. 提供给各个支付业务在这里修改.
 * <p>
 * 静态工厂 --> UI工厂 --> UI
 * </p>
 * 三层处理的原因是为了隔离各个业务的代码. 这样各个业务可以现在自己的业务里声明<code>PaymentResultUIFactory</code>,
 * 然后在静态工厂里返回即可.
 * 
 * @author Steve Xu 徐远同
 */
public class ResultUIStaticFactory {
    private ResultUIStaticFactory() {
    }

    // 结果UI缓存
    private static final SparseArray<PaymentResultUI> cache = new SparseArray<PaymentResultUI>();

    /**
     * 创建product type对应的UI描述文件
     * @param ctx
     * @param productType 商品类型, 定义在{@link ProductTypeCode}中
     * @return
     */
    public static PaymentResultUI createUI(Context ctx, int productType) {
        PaymentResultUI ui = cache.get(productType);
        if (null == ui) {
            PaymentResultUIFactory factory = createFactory(ctx, productType);
            if (null != factory) {
                ui = factory.createUI();
                cache.put(productType, ui);
            }
        }

        return ui;
    }

    /**
     * 创建product type对应的<code>PaymentResultUIFactory</code>工厂, 由各个业务处理
     * @param ctx
     * @param productType
     * @return
     */
    protected static PaymentResultUIFactory createFactory(Context ctx, int productType) {
        switch (productType) {
            case ProductTypeCode.Telephone.ProductType:
                return new TelephonePaymentUIFactory();
            case ProductTypeCode.WaterElectricityGas.ProductType:
                return new WEGPaymentUIFactory();
            case  ProductTypeCode.Movie.ProductType:
//                return new WEGPaymentUIFactory();
            	
            	return new CinemaPaymentUIFactory();
            	
            case ProductTypeCode.Flow.ProductType:
                return new TrafficPaymentUIFactory();
        }
        return null;
    }
}
