package so.contacts.hub.http.bean;

public class LocationInfo {
	public String mcc;
	public String mnc;
	public String lac; // [String][not null][区域码]
	public String cell_id; // [String][not null][基站编码]
    
    /**
     * MCC，Mobile Country Code，移动国家代码（中国的为460）；
     * MNC，Mobile Network Code，移动网络号码（中国移动为00，中国联通为01）；
     * LAC，Location Area Code，位置区域码；       
     * CID，Cell Identity，基站编号，是个16位的数据（范围是0到65535）。
     */
}