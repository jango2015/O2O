package so.contacts.hub.push.bean;

import java.io.Serializable;

/**
 * Push消息-广告数据
 * 
 * @author zjh
 * 
 */
public class PushAdBean implements Serializable{

	private static final long serialVersionUID = 1L;

	private int ad_code;// 【广告所在页面】

	private int ad_page_index;// 【广告所在页面位置】1:顶部;2:中间；3：底部

	private String ad_img_url;// 【广告图片Url】长度 = 1:一张图；2：两张图；3：三张图
	
	private String ad_click_type;// 【广告跳转类型】1:打开特定服务页面；2:打开特定链接H5页面
	
	private String ad_click_activity; // 【广告跳转】打开的页面activity名称

	private String ad_click_link;// 【广告跳转连接】notify_type=1：为类名；notify_type=2：为H5 Url;
	
	private String ad_text; //【文本描述】
	
	private long ad_start_time;  // 【广告开始时间】
	
	private long ad_end_time;  // 【广告结束时间】
	
	private boolean mNeedRefresh = true; // 是否需要刷新
	
	private String ad_params_str ;//【运营位参数】

    public String getAd_params_str() {
        return ad_params_str;
    }

    public void setAd_params_str(String ad_params_str) {
        this.ad_params_str = ad_params_str;
    }

    public String getAd_img_url() {
		return ad_img_url;
	}

	public void setAd_img_url(String ad_img_url) {
		this.ad_img_url = ad_img_url;
	}

	public int getAd_code() {
		return ad_code;
	}

	public void setAd_code(int ad_code) {
		this.ad_code = ad_code;
	}

	public int getAd_page_index() {
		return ad_page_index;
	}

	public void setAd_page_index(int ad_page_index) {
		this.ad_page_index = ad_page_index;
	}

	public String getAd_click_type() {
		return ad_click_type;
	}

	public void setAd_click_type(String ad_click_type) {
		this.ad_click_type = ad_click_type;
	}

	public String getAd_click_link() {
		return ad_click_link;
	}

	public void setAd_click_link(String ad_click_link) {
		this.ad_click_link = ad_click_link;
	}
	
	public String getAd_click_activity() {
		return ad_click_activity;
	}

	public void setAd_click_activity(String ad_click_activity) {
		this.ad_click_activity = ad_click_activity;
	}

	public String getAd_text() {
		return ad_text;
	}

	public void setAd_text(String ad_text) {
		this.ad_text = ad_text;
	}
	
	public long getAd_start_time() {
		return ad_start_time;
	}

	public void setAd_start_time(long ad_start_time) {
		this.ad_start_time = ad_start_time;
	}

	public long getAd_end_time() {
		return ad_end_time;
	}

	public void setAd_end_time(long ad_end_time) {
		this.ad_end_time = ad_end_time;
	}
	
    public boolean needRefresh() {
		return mNeedRefresh;
	}

	public void setNeedRefresh(boolean mNeedRefresh) {
		this.mNeedRefresh = mNeedRefresh;
	}

    @Override
    public String toString() {
        return "PushAdBean [ad_code=" + ad_code + ", ad_page_index=" + ad_page_index
                + ", ad_img_url=" + ad_img_url + ", ad_click_type=" + ad_click_type
                + ", ad_click_activity=" + ad_click_activity + ", ad_click_link=" + ad_click_link
                + ", ad_text=" + ad_text + ", ad_start_time=" + ad_start_time + ", ad_end_time="
                + ad_end_time + ", mNeedRefresh=" + mNeedRefresh + ", ad_params_str="
                + ad_params_str + "]";
    }

	
}
