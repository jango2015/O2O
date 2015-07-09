
package so.contacts.hub.msgcenter;

import so.contacts.hub.core.Config;

public class MsgCenterConfig {

    public static final String EXPRESS_REPORT = Config.MSG_REPORT.EXPRESS_REPORT;// 快递查询成功上报

    public static final String ORDER_LIST = Config.MSG_REPORT.ORDER_LIST;// 获取订单列表

    public static final String NOT_ORDER_LIST = Config.MSG_REPORT.NOT_ORDER_LIST;// 获取订单列表

    public static final String ORDER_TIMESTAMP = "order_timestamp";// 获取订单列表的Url参数名

    public static final String PRODUCT_TYPE = "product_type";// 获取订单列表的Url参数名

    public static final String PAGE_SIZE = "page_size";// 获取订单列表的Url参数名

    public static final String PAGE_NO = "page_no";// 获取订单列表的Url参数名

    public static final String SHARED_NAME = "message_center";
    public static final String UPDATE_POINT_NUMBER = "update_point_number";

	public static final int ORDER_CANCEL = 0;      // 订单取消
	public static final int WAIT_BUYER_PAY = 1;    // 订单创建 未支付
	public static final int PAY_FAIL = 2;          // 支付失败
	public static final int TRADE_PROCESS = 3;     // 处理中
	public static final int TRADE_SUCCESS = 4;     // 交易成功
	public static final int REFUND_PROCESS = 5;    // 退款中
	public static final int REFUND_SUCCESS = 6;    // 退款成功

    public static enum Product {
        shuidianmei(1, 2,1) {
            @Override
            public String getCnName() {
                return "水电煤";
            }
        },
        charge(2, 3,1) {
            @Override
            public String getCnName() {
                return "充话费";
            }
        },
        traffic(3, 4,1) {
            @Override
            public String getCnName() {
                return "充流量";
            }
        },
        lottery(4, 5,1) {
            @Override
            public String getCnName() {
                return "彩票";
            }
        },
        cinema(5, 6,1) {
            @Override
            public String getCnName() {
                return "电影";
            }
        },
        group(6, 7,1) {
            @Override
            public String getCnName() {
                return "团购";
            }
        },
        flight(7, 8,1) {
            @Override
            public String getCnName() {
                return "机票";
            }
        },
        train(8, 9,1) {
            @Override
            public String getCnName() {
                return "火车票";
            }
        },
        hotel(9, 10,1) {
            @Override
            public String getCnName() {
                return "酒店";
            }
        },
        express(10, 11,2) {
            @Override
            public String getCnName() {
                return "快递";
            }
        },
        traffic_offence(11, 12,2) {
            @Override
            public String getCnName() {
                return "违章";
            }
        },
        birth(12, 13,2) {
            @Override
            public String getCnName() {
                return "生日";
            }
        },
        back(13, 14,2) {
            @Override
            public String getCnName() {
                return "返程";
            }
        },
        near(14, 15,1) {//附近仅在notification的时候被当作订单型
            @Override
            public String getCnName() {
                return "附近";
            }
        },
        weather(15, 16,2) {
            @Override
            public String getCnName() {
                return "天气";
            }
        };

        private int productId;

        private int productType;
        
        private int businessType;//服务类别,订单型为1,非订单型为2

        Product(int productId, int productType,int businessType) {
            this.productId = productId;
            this.productType = productType;
            this.businessType = businessType;
        }

        public int getProductId() {
            return productId;
        }

        public int getProductType() {
            return productType;
        }

        public int getBusinessType() {
            return businessType;
        }

        public abstract String getCnName();

        public static Product getProduct(int productType) {
            switch (productType) {
                case 2:
                    return shuidianmei;
                case 3:
                    return charge;
                case 4:
                    return traffic;
                case 5:
                    return lottery;
                case 6:
                    return cinema;
                case 7:
                    return group;
                case 8:
                    return flight;
                case 9:
                    return train;
                case 10:
                    return hotel;
                case 11:
                    return express;
                case 12:
                    return traffic_offence;
                case 13:
                    return birth;
                case 14:
                    return back;
                case 15:
                    return near;
                case 16:
                    return weather;
                default:
                    return shuidianmei;
            }
        }
        
        @Override
        public String toString() {
            // TODO Auto-generated method stub
            return super.toString();
        }

    };
}
