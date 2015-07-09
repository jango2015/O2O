package so.contacts.hub.ui.yellowpage.bean;

/**
 * 地铁城市bean
 * @author putao_lhq
 *
 */
public class SubwayCity extends City{

	private String code;
	
	public SubwayCity(String city, String code, String pinyin) {
		this.code = code;
		setCityName(city);
		setCityPY(pinyin);
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
}
