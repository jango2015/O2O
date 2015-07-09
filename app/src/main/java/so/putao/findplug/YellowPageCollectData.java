package so.putao.findplug;

import java.io.Serializable;

@SuppressWarnings("serial")
public class YellowPageCollectData implements Serializable {

	private int itemId;

	/**
	 * 收藏: 1
     * 历史: 2
	 */
	private int dataType;

	private String name;

	/**
	 * 大众点评、葡萄、搜狗等数据来源
	 * 葡萄: 1
	 * 点评: 2
	 * 搜狗: 3
	 * 高德: 4
	 * 58：   5
	 * 艺龙：6
	 */
	private int type;

	private String content;

	private long time;

	public YellowPageCollectData() {

	}

	public long getItemId() {
		return itemId;
	}
	
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public int getDataType() {
		return dataType;
	}

	public void setDataType(int dataType) {
		this.dataType = dataType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

}
