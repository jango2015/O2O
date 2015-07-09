
package so.contacts.hub.payment;

import so.contacts.hub.payment.data.ProductTypeCode;
import so.contacts.hub.thirdparty.cinema.ui.CinemaPaymentResultDelegator;

/**
 * 支付结果页代理静态工厂
 * 
 * @author Steve Xu 徐远同
 */
public class ResultActivityDelegatorFactory {
    private ResultActivityDelegatorFactory() {
    }

    public static ResultActivityDelegator createDelegator(int productType) {
        switch (productType) {
            case ProductTypeCode.Movie.ProductType:
                return new CinemaPaymentResultDelegator();

            default:
                break;
        }
        return null;
    }
}
