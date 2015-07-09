package so.contacts.hub.thirdparty.elong.bean;

public enum EnumCustomerType {

	All("All"), Chinese("Chinese"), OtherForeign("OtherForeign"), HongKong("HongKong"), Japanese("Japanese");
	
	private final String value;

	EnumCustomerType(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static EnumCustomerType fromValue(String v) {
		for (EnumCustomerType c : EnumCustomerType.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}
	
}
