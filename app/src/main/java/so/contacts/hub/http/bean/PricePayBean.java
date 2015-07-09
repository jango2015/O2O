package so.contacts.hub.http.bean;

import java.io.Serializable;

/**
 * 话费售价
 * @author hyl 2014-10-14
 */
public class PricePayBean implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public int type;        //价格类型
    public float pay_price;//售价
}
