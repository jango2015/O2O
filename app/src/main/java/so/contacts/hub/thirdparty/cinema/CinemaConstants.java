
package so.contacts.hub.thirdparty.cinema;

public class CinemaConstants {
    /** 场次id */
    public static final String MPID = "mpid";
    public static final String CINEMA_NAME = "cinema_name";
    
    public static final String MOVIE_PHOTO_URL = "movie_photo_url";

    public static final String CINEMA_ADDRESS = "cinema_address";
    public static final String CINEMA_CITY = "cinema_city";
    
    /** 订单详情页入口参数 */
    public static final String ENTRY_TYPE = "entry_type";

    public static final int GENERATE_ORDER = 1;

    public static final int ORDER_DETAIL = 2;

    public static final String MOVIE_ORDER_DETAIL = "movie_order_detail";
    public static final String MOVIE_ORDER_SEAT_INFO = "movie_order_seat_info";
    public static final String MOVIE_ORDER_SEAT = "movie_order_seat";
    public static final String MOVIE_ORDER_LANGUAGE = "movie_order_language";
    public static final String MOVIE_ORDER_EDITION = "movie_order_edition";
    
    public static final String MOVIE_ORDER_SERVICEFEE = "movie_order_servicefee";//add by hyl 2015-1-7 电影票服务费

    /**
     * logo大小width=0 height=0
     */
    public static final int[] PIC_SIZE_DEFAULT = new int[]{0,0};
    /**
     * logo大小width=72 height=96
     */
    public static final int[] PIC_SIZE_1 = new int[]{72,96};
    /**
     * logo大小width=96 height=128
     */
    public static final int[] PIC_SIZE_2 = new int[]{96,128};
    /**
     * logo大小width=120 height=160
     */
    public static final int[] PIC_SIZE_3 = new int[]{120,160};
    /**
     * logo大小width=150 height=200
     */
    public static final int[] PIC_SIZE_4 = new int[]{150,200};
    /**
     * logo大小width=210 height=280
     */
    public static final int[] PIC_SIZE_5 = new int[]{210,280};
    
}
