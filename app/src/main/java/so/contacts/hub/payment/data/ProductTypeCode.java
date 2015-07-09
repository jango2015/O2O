
package so.contacts.hub.payment.data;

/**
 * 与后台约定好的产品标识参数
 * <table>
 * <tr>
 * <td>product id</td>
 * <td>product name</td>
 * <td>product type</td>
 * </tr>
 * <tr>
 * <td>1</td>
 * <td>水电煤</td>
 * <td>2</td>
 * </tr>
 * <tr>
 * <td>2</td>
 * <td>充话费</td>
 * <td>3</td>
 * </tr>
 * <tr>
 * <td>3</td>
 * <td>充流量</td>
 * <td>4</td>
 * </tr>
 * <tr>
 * <td>4</td>
 * <td>彩票</td>
 * <td>5</td>
 * </tr>
 * <tr>
 * <td>5</td>
 * <td>电影票</td>
 * <td>6</td>
 * </tr>
 * <tr>
 * <td>6</td>
 * <td>团购</td>
 * <td>7</td>
 * </tr>
 * <tr>
 * <td>7</td>
 * <td>机票</td>
 * <td>8</td>
 * </tr>
 * </table>
 * 
 * @author Steve Xu 徐远同
 */
public class ProductTypeCode {
    /**
     * 水电煤
     * 
     * @author Steve Xu 徐远同
     */
    public static class WaterElectricityGas {
        public static final int ProductId = 1;

        public static final int ProductType = 2;
    }

    /**
     * 充话费
     * 
     * @author Steve Xu 徐远同
     */
    public static class Telephone {
        public static final int ProductId = 2;

        public static final int ProductType = 3;
    }

    /**
     * 充流量
     * 
     * @author Steve Xu 徐远同
     */
    public static class Flow {
        public static final int ProductId = 3;

        public static final int ProductType = 4;
    }

    /**
     * 买彩票
     * 
     * @author Steve Xu 徐远同
     */
    public static class Lottery {
        public static final int ProductId = 4;

        public static final int ProductType = 5;
    }

    /**
     * 电影票
     * 
     * @author Steve Xu 徐远同
     */
    public static class Movie {
        public static final int ProductId = 5;

        public static final int ProductType = 6;
    }

    /**
     * 团购
     * 
     * @author Steve Xu 徐远同
     */
    public static class GroupBuy {
        public static final int ProductId = 6;

        public static final int ProductType = 7;
    }

    /**
     * 买机票
     * 
     * @author Steve Xu 徐远同
     */
    public static class Flight {
        public static final int ProductId = 7;

        public static final int ProductType = 8;
    }

    /**
     * 火车票
     * 
     * @author Steve Xu 徐远同
     */
    public static class Train {
        public static final int ProductId = 8;

        public static final int ProductType = 9;
    }
}
