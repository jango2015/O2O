package so.putao.findplug;

/**
 * 
 * @author putao_lhq
 * @version 2014年9月24日
 */
public class City58Item extends SourceItemObject {

	private static final long serialVersionUID = 2573147111815599521L;

	public String adid;//广告id
	public String title;//主题
	public String describe; //描述
	public String posttime; //发布时间
	public String price; //价格
	public String targeturl;//网址
	
	@Override
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(" ,adid = " + adid);
		strBuilder.append(" ,description = " + describe);
		strBuilder.append(" ,price = " + price);
		strBuilder.append(" ,posttime = " + posttime);
		strBuilder.append(" ,targetUrl = " + targeturl);
		return strBuilder.toString();
	}

	@Override
	public double getLatitude() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getLongitude() {
		// TODO Auto-generated method stub
		return 0;
	}
}
