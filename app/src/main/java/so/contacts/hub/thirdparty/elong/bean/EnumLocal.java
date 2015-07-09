package so.contacts.hub.thirdparty.elong.bean;

public enum EnumLocal {

	zh_CN("zh_CN"), en_US("en_US");
	
	private final String value;

	EnumLocal(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static EnumLocal fromValue(String v) {
		for (EnumLocal c : EnumLocal.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
