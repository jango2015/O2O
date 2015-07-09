package so.putao.findplug;

import java.io.Serializable;

/**
 * 所有来源数据(点评、搜狗、高德等)的自有数据类型的基类
 */
public abstract class SourceItemObject implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String name; //商户名称
	
	private String photoUrl;  //图片
	
	private String defaultPhotoUrl; //默认图片

	private String dataSource; // 数据来源显示
	
	private  String localPhotoUrl; // 本地图片

	private boolean isSelected; //列表记录是否选中

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public String getDefaultPhotoUrl() {
		return defaultPhotoUrl;
	}

	public void setDefaultPhotoUrl(String defaultPhotoUrl) {
		this.defaultPhotoUrl = defaultPhotoUrl;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public String getLocalPhotoUrl() {
		return localPhotoUrl;
	}

	public void setLocalPhotoUrl(String localPhotoUrl) {
		this.localPhotoUrl = localPhotoUrl;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	abstract public double getLatitude();

	abstract public double getLongitude();
	
}
