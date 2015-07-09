package so.contacts.hub.http.bean;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class ProductDescBean implements Serializable{
    
    /*
     * 添加价格pay_list（用于支持多种支付手段，每种支付手段对应不同的价格）
     * add by hyl 2014-10-10 start
     */
    public List<PricePayBean> pay_list;
    //add by hyl 2014-10-10 end
    
	// 号码段，截取号码前11位
	public String mobile_area;
	//产品ID
	public long product_id;
	//subject
	public String subject;
	//body
	public String body;
	//扣款价格
	public float price;
	//标价
	public String mark_price;
	//号码描述,eg. 广东电信
	public String mobile_remark;
	
	// 描叙该产品是否被禁止销售
	public boolean disabled;
	
	public String priceRange;
	
	public ProductDescBean(){
		
	}
	
	public ProductDescBean(long product_id, String mark_price, float price, String priceRange) {
		this.product_id = product_id;
		this.mark_price = mark_price;
		this.price = price;
		this.mobile_area = "";
		this.subject = "";
		this.body = "";
		this.mobile_remark = "";
		this.disabled = false; 
		this.priceRange = priceRange;
	}
	
	public void copyFrom(ProductDescBean bean) {
		if(bean != null) {
			this.product_id = bean.product_id;
			this.subject = bean.subject;
			this.body = bean.body;
			this.price = bean.price;
			this.mark_price = bean.mark_price;
			this.mobile_remark = bean.mobile_remark;
			this.pay_list = bean.pay_list;
		}
	}
}
