package so.contacts.hub.ui.yellowpage.bean;

import so.putao.findplug.SourceItemObject;

import android.graphics.Bitmap;

public class SougouHmtItem extends SourceItemObject {

	private static final long serialVersionUID = 1L;

	String disString;
	double distance;
	String[] hitWordArray;
	String localLogoUrl;
	String merchantDetailUrl;
	String merchantTag;
	int nextStart;
	String number;
	int resultType;
	Bitmap itemlogo;
	boolean isHasLogo;
	boolean hasMore;

	public Bitmap LoadItemLogo() {
		return itemlogo;
	}

	public void setItemlogo(Bitmap itemlogo) {
		this.itemlogo = itemlogo;
	}

	public boolean isHasLogo() {
		return isHasLogo;
	}

	public void setHasLogo(boolean isHasLogo) {
		this.isHasLogo = isHasLogo;
	}

	public boolean beHasMore() {
		return hasMore;
	}

	public void setHasMore(boolean hasMore) {
		this.hasMore = hasMore;
	}

	public String getDisString() {
		return disString;
	}

	public void setDisString(String disString) {
		this.disString = disString;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public String[] getHitWordArray() {
		return hitWordArray;
	}

	public void setHitWordArray(String[] hitWordArray) {
		this.hitWordArray = hitWordArray;
	}

	public String getMerchantDetailUrl() {
		return merchantDetailUrl;
	}

	public void setMerchantDetailUrl(String merchantDetailUrl) {
		this.merchantDetailUrl = merchantDetailUrl;
	}

	public String getMerchantTag() {
		return merchantTag;
	}

	public void setMerchantTag(String merchantTag) {
		this.merchantTag = merchantTag;
	}

	public int getNextStart() {
		return nextStart;
	}

	public void setNextStart(int nextStart) {
		this.nextStart = nextStart;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public int getResultType() {
		return resultType;
	}

	public void setResultType(int resultType) {
		this.resultType = resultType;
	}

	public String getLocalLogoUrl() {
		return localLogoUrl;
	}

	public void setLocalLogoUrl(String localLogoUrl) {
		this.localLogoUrl = localLogoUrl;
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
