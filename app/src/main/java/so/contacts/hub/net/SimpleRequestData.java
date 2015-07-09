package so.contacts.hub.net;

import java.util.Map;

/**
 * 部分请求可能直接设置参数，现提供该类<br>
 * 使用样例：<br>
 * <p>{@code 
 *         SimpleRequestData reqData = new SimpleRequestData();
            reqData.setParam("product_type", String.valueOf(OrderEntity.Product.cinema.getProductType()));
            String newContent = PTHTTP.getInstance().post("http://pay.putao.so/pay/order/list", reqData); 
    }
 * </p>
 * @author putao_lhq
 *
 */
public class SimpleRequestData extends BaseRequestData {
	
    @Override
    protected void setParams(Map<String, String> params) {
    }

}
