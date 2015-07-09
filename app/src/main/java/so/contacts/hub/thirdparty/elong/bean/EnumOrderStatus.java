package so.contacts.hub.thirdparty.elong.bean;

public enum EnumOrderStatus {

	/**
	 * A-已确认 B-NO SHOW B1-有预定未查到 B2-待查 B3-暂不确定 C-已结帐 D-删除 E-取消 F-已入住 G-变价 H-变更
	 * I-大单 N-新单 O-满房 P-暂无价格 S-特殊 T-计划中 U-特殊满房 V-已审 W-虚拟 Z-删除,另换酒店
	 */

	A("A"), B("B"), B1("B1"), B2("B2"), B3("B3"), C("C"), D("D"), E("E"), F("F"), G(
			"G"), H("H"), I("I"), J("J"), M("M"), N("N"), O("O"), P("P"), R("R"), S(
			"S"), T("T"), U("U"), V("V"), W("W"), Z("Z");
	private final String value;

	EnumOrderStatus(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static EnumOrderStatus fromValue(String v) {
		for (EnumOrderStatus c : EnumOrderStatus.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
