package so.putao.findplug;

import java.util.ArrayList;

public class DianPingBusiness extends SourceItemObject{
	
    private static final long serialVersionUID = 1L;
    
    public int business_id;//商户ID
    public String branch_name;//分店名
    public String address;//地址
    public String telephone;//带区号的电话 
    public String city;//所在城市
    public ArrayList<String> regions;//所在区域信息列表，如[徐汇区，徐家汇] 
    public ArrayList<String> categories;//所属分类信息列表，如[宁波菜，婚宴酒店] 
    public float latitude;//    纬度坐标  
    public float longitude;//  经度坐标  
    public float avg_rating;//星级评分，5.0代表五星，4.5代表四星半，依此类推  
    public String rating_img_url;//星级图片链接  
    public String rating_s_img_url;//小尺寸星级图片链接  
    public int product_grade;//产品/食品口味评价，1:一般，2:尚可，3:好，4:很好，5:非常好  
    public int decoration_grade;//环境评价，1:一般，2:尚可，3:好，4:很好，5:非常好  
    public int service_grade;//服务评价，1:一般，2:尚可，3:好，4:很好，5:非常好  
    public float product_score;//产品/食品口味评价单项分，精确到小数点后一位（十分制）  
    public float decoration_score;//环境评价单项分，精确到小数点后一位（十分制）  
    public float service_score;//服务评价单项分，精确到小数点后一位（十分制）  
    public int avg_price;//人均价格，单位:元，若没有人均，返回-1  
    public int review_count;//点评数量  
    public int distance;//商户与参数坐标的距离，单位为米，如不传入经纬度坐标，结果为-1  
    public String business_url;//  商户页面链接  
    public String s_photo_url;//  小尺寸照片链接，照片最大尺寸278×200  
    public int has_coupon;//  是否有优惠券，0:没有，1:有  
    public int coupon_id;//  优惠券ID  
    public String coupon_description;//优惠券描述  
    public String coupon_url;//  优惠券页面链接  
    public int has_deal;//是否有团购，0:没有，1:有  
    public int deal_count;//商户当前在线团购数量
    public ArrayList<Deal> deals;//团购列表  
    public int has_online_reservation;//是否有在线预订，0:没有，1:有  
    public String online_reservation_url;//  在线预订页面链接，目前仅返回HTML5站点链接  
    
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "{" + "name:" + getName() + ",branch_name:" + branch_name + ",address:" + address
                 + ",telephone:" + telephone + ",s_photo_url:" + s_photo_url + ",online_reservation_url:" + online_reservation_url
                + "}";
    }

	@Override
	public double getLatitude() {
		// TODO Auto-generated method stub
		return latitude;
	}

	@Override
	public double getLongitude() {
		// TODO Auto-generated method stub
		return longitude;
	}
}
