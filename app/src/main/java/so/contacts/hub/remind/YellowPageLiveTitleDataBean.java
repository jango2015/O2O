package so.contacts.hub.remind;


public class YellowPageLiveTitleDataBean {
	private int code;//categroy 表里面的id
	private String imgUrl; // 如果有背景图片,图片的url
	private String text; // 消息的内容  
	
	private int color = 0xff000000; // 普通字体的颜色,如果color=0，则普通字体的颜色使用默认设置
	private float textSize=0;// 普通字体的大小，如果text_size=0，则显示默认大小
	private int keyWordColor;// 关键词的颜色,如果为0,则关键字的颜色使用默认值;则代表以下内容都为空
	private int keyWordStart;// 关键词开始位置，例如“现在充值95折”，需加亮“95”，则keyword_start=4，keyword_end=6
	private int keyWordEnd;// 关键词结束位置，例如“现在充值95折”，需加亮“95”，则keyword_start=4，keyword_end=6
	private float keyWordSize;// 键词的大小，取值为普通字体大小的倍数，如果keyword_size=0，则和其他字体一样的大小
	private long dismissTime; // 消息过期的时间
	private boolean hasShowed = false;// 表示是否展示过,保证LiveTitle不重复显示,目前不用..
	private int delay;// 表示消息显示的延迟,json中不需要用.
	
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	

	public String getImgUrl() {
		return imgUrl;
	}

	public YellowPageLiveTitleDataBean setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
		return this;
	}

	public String getBubbleText() {
		return text;
	}

	public YellowPageLiveTitleDataBean setText(String text) {
		this.text = text;
		return this;
	}

	public int getColor() {
		return color;
	}

	public YellowPageLiveTitleDataBean setColor(int color) {
		this.color = color;
		return this;
	}

	public float getTextSize() {
		return textSize;
	}

	public YellowPageLiveTitleDataBean setTextSize(float textSize) {
		this.textSize = textSize;
		return this;
	}

	public int getKeyWordColor() {
		return keyWordColor;
	}

	public YellowPageLiveTitleDataBean setKeyWordColor(int keyWordColor) {
		this.keyWordColor = keyWordColor;
		return this;
	}

	public int getKeyWordStart() {
		return keyWordStart;
	}

	public YellowPageLiveTitleDataBean setKeyWordStart(int keyWordStart) {
		this.keyWordStart = keyWordStart;
		return this;
	}

	public int getKeyWordEnd() {
		return keyWordEnd;
	}

	public YellowPageLiveTitleDataBean setKeyWordEnd(int keyWordEnd) {
		this.keyWordEnd = keyWordEnd;
		return this;
	}

	public float getKeyWordSize() {
		return keyWordSize;
	}

	public YellowPageLiveTitleDataBean setKeyWordSize(float keyWordSize) {
		this.keyWordSize = keyWordSize;
		return this;
	}

	public long getDismissTime() {
		return dismissTime;
	}

	public YellowPageLiveTitleDataBean setDismissTime(long dismissTime) {
		this.dismissTime = dismissTime;
		return this;
	}

	public boolean isHasShowed() {
		return hasShowed;
	}

	public YellowPageLiveTitleDataBean setHasShowed(boolean hasShowed) {
		this.hasShowed = hasShowed;
		return this;
	}

	public int getDelay() {
		return delay;
	}

	public YellowPageLiveTitleDataBean setDelay(int delay) {
		this.delay = delay;
		return this;
	}

	@Override
	public String toString() {
		return "YellowPageLiveTitleData [imgUrl=" + imgUrl + ", text=" + text
				+ ", color=" + color + ", textSize=" + textSize
				+ ", keyWordColor=" + keyWordColor + ", keyWordStart="
				+ keyWordStart + ", keyWordEnd=" + keyWordEnd
				+ ", keyWordSize=" + keyWordSize + ", dismissTime="
				+ dismissTime + ", hasShowed=" + hasShowed + ", delay=" + delay
				+ "]";
	}

}
