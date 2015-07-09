package so.putao.findplug;

import so.contacts.hub.thirdparty.dianping.DianPingApiTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import so.contacts.hub.util.UMengEventIds;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import so.contacts.hub.util.MobclickAgentUtil;

public class YelloPageDianpingFactory extends YelloPageFactory{
	private String categoryStr = "美食,江浙菜,上海菜,淮扬菜,浙江菜,杭帮菜,南京菜,苏帮菜,宁波菜,绍兴菜,无锡菜,舟山菜,衢州菜,温州菜,苏北土菜,粤菜,潮汕菜,茶餐厅,客家菜,湛江菜,川菜,自贡盐帮菜,江湖菜,酸菜鱼,香锅,川味小吃,湘菜,北京菜,北京家常菜,官府菜,烤鸭,东北菜,云贵菜,云南菜,贵州菜,湖北菜,江西菜,山西菜,蒙古菜,徽菜,闽菜,鲁菜,豫菜,贵州菜,广西菜,台湾菜,冀菜,陕西菜,天津菜,青海菜,西北菜,西藏菜,新疆菜,东南亚菜,泰国菜,越南菜,印度菜,新加坡菜,西餐,法国菜,意大利菜,西班牙菜,俄罗斯菜,中东菜,西式正餐,西式简餐,巴西烧烤,无国界料理,牛排,比萨,日本,韩国,火锅,清真菜,小吃快餐,农家菜,创意菜,海鲜,素菜,烧烤,自助餐,面包甜点,酒吧,休闲娱乐,咖啡,茶馆,KTV,电影院,图书馆,博物馆,美术展览,演出票务,公园,景点郊游,足疗按摩,洗浴,游乐游艺,桌面游戏,DIY手工坊,购物,综合商场,食品茶酒,服饰鞋包,珠宝饰品,花店,化妆品,运动户外,儿童服饰,玩具,亲子购物,品牌折扣店,家具,家居建材,书店,眼镜店,办公,超市,便利店,药店,丽人,美发,美容,SPA,化妆品,瘦身纤体,美甲,瑜伽,舞蹈,写真,整形,齿科,结婚,婚纱摄影,婚宴,婚戒首饰,婚纱礼服,婚庆公司,彩妆造型,司仪主持,婚礼跟拍,婚车租赁,婚礼小商品,婚房装修,亲子,早教中心,幼儿园,小学,亲子摄影,亲子游乐,亲子购物,孕产护理,运动健身,游泳馆,羽毛球馆,健身中心,瑜伽,篮球场,足球场,高尔夫场,保龄球馆,乒乓球馆,武术场馆,壁球馆,攀岩馆,射箭馆,骑马场,溜冰场,酒店,五星级酒店,四星级酒店,三星级酒店,经济型酒店,公寓式酒店,精品酒店,青年旅舍,度假村,农家院,汽车服务,4S店,汽车保险,维修保养,配件车饰,驾校,汽车租赁,停车场,加油站,生活服务,医院,干洗店,家政,银行,学校,小区,商务楼,旅行社,培训,宠物医院,齿科,快照冲印";
	
	private List<YelloPageItem> mAllDianPingItemList = new ArrayList<YelloPageItem>();
	
	private Context context;
	private boolean mHasMore = true;
	private int mPage = 1;
	private String mWords;
	private String mCity;
	private double mLongitude;
	private double mLatitude;
	private String mCategory;
	
	private static YelloPageDianpingFactory mInstance;
	
	public static YelloPageDianpingFactory getInstance(Context c) {
		if(mInstance == null){
			mInstance = new YelloPageDianpingFactory(c);
		}
		return mInstance;
	}
	
	private YelloPageDianpingFactory(Context c){
		this.context = c;
	}
	
	@Override
	public ArrayList<YelloPageItem> search(String keyword, String city, double longitude,double latitude,String category,int source) {
		if(TextUtils.isEmpty(category) && !TextUtils.isEmpty(keyword)){
			String[] categories = categoryStr.split(",");
			if(categories != null && categories.length > 0){
				for(String tcategory : categories){
					if(keyword.equals(tcategory)){
						category = keyword;
						break;
					}
				}
			}
		}
		return searchData(keyword, city, longitude, latitude, category, 1);
	}
	
	private ArrayList<YelloPageItem> searchData(String keyword, String city, double longitude,double latitude,String category,int page){
		try {
			ArrayList<YelloPageItem> resultList = new ArrayList<YelloPageItem>();
			if(TextUtils.isEmpty(category) && TextUtils.isEmpty(keyword)){
				mHasMore = false;
				return resultList;
			}
			resultList = searchRaw(keyword, city, longitude,latitude,category,page);
			return resultList;
		} catch (Exception e) {
			mHasMore = false;
			return new ArrayList<YelloPageItem>();	
		}
	}
	
	public ArrayList<YelloPageItem> searchRaw(String keyword, String city, double longitude,double latitude,String category,int page) {
		this.mCategory = category;
		this.mCity = city;
		this.mLatitude = latitude;
		this.mLongitude = longitude;
		this.mWords = keyword;
		this.mPage = page;
        
		ArrayList<YelloPageItem> list = new ArrayList<YelloPageItem>();
        Map<String, String> paramMap = new HashMap<String, String>();
        if(latitude != 0.0 || longitude != 0.0){ //modify by ffh 修改切换城市后大众点评搜不出来的问题
        	paramMap.put("latitude", ""+latitude);
            paramMap.put("longitude", ""+longitude);
            paramMap.put("offset_type", "1");//暂时是0//
            paramMap.put("sort", "7");
            paramMap.put("radius", "5000");
        }
        paramMap.put("city", city);
        if(category != null && !category.equals("")) {
            paramMap.put("category", category);
        }
        if(keyword != null && !keyword.equals("")) {
            paramMap.put("keyword", keyword);
        }
        paramMap.put("out_offset_type", "1");//默认是高德数据//
        paramMap.put("platform", "2");
        /*
         * 将 40 改为 20 
         * update by hyl 2014-8-18 start
         * old code : paramMap.put("limit", "40");
         */
        paramMap.put("limit", "20");
        //update by hyl 2014-8-18 end
        
        paramMap.put("page", String.valueOf(page));
        paramMap.put("format", "json");

        String requestResult = DianPingApiTool.requestApi(DianPingApiTool.URL_GET_FIND_BUSINESS_INFO, paramMap);
        Log.e("error", "requestResult == " + requestResult);
        BusinessesResponse businessesResponse = new Gson().fromJson(requestResult, BusinessesResponse.class);
        Log.e("error", "businessesResponse == " + businessesResponse);
        if(!businessesResponse.status.equals("OK")) {
			if (null != context) {
				MobclickAgentUtil.onEvent(context,
						UMengEventIds.DISCOVER_YELLOWPAGE_SEARCH_DIANPING_FAIL);
			}
			mHasMore = false;
        	return list;
        } else {
        	if (null != context) {
				MobclickAgentUtil.onEvent(context,
						UMengEventIds.DISCOVER_YELLOWPAGE_SEARCH_DIANPING_SUCCESS);
			}
        }
        if(page == 1){
        	mAllDianPingItemList.clear();
        }
        if(businessesResponse.total_count > businessesResponse.businesses.size() + mAllDianPingItemList.size()){
        	mHasMore = true;
        }else{
        	mHasMore = false;
        }
        for(DianPingBusiness business:businessesResponse.businesses) {
        	if(business.getName().contains("(")) {
        		business.setName(business.getName().substring(0, business.getName().indexOf("(")));
        	}
        	YellowPageItemDianping yellowPageItemDianping = new YellowPageItemDianping(business);
//	        	Log.e("error", "yellowPageItemDianping.getLogoBitmap() == " + yellowPageItemDianping.getLogoBitmap());
        	list.add(yellowPageItemDianping);
        }
        
		if (list.size() == 0) {
			if (null != context) {
				MobclickAgentUtil
						.onEvent(
								context,
								UMengEventIds.DISCOVER_YELLOWPAGE_SEARCH_DIANPING_NO_DATA);
			}
		}
        
        mAllDianPingItemList.addAll(list);
        return list;
	}

	@Override
	public ArrayList<YelloPageItem> searchMore() {
		return searchData(mWords, mCity, mLongitude, mLatitude, mCategory, mPage + 1);
	}

	@Override
	public boolean hasMore() {
		return mHasMore;
	}
	
	public String getCategory(){
		return mCategory;
	}
	
}
