package so.contacts.hub.thirdparty.elong.bean;

public enum EnumSortType {

	/**
	 * Default艺龙默认排序 StarRankDesc推荐星级降序 RateAsc价格升序 RateDesc价格降序 DistanceAsc距离升序
	 */

	Default("Default"), StarRankDesc("StarRankDesc"), RateAsc("RateAsc"), DistanceAsc(
			"DistanceAsc"), RateDesc("RateDesc");
	private final String value;

	EnumSortType(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static EnumSortType fromValue(String v) {
		for (EnumSortType c : EnumSortType.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
