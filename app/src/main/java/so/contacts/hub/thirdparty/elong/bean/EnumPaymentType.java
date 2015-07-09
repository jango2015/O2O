package so.contacts.hub.thirdparty.elong.bean;

public enum EnumPaymentType {

    All("All"),
    SelfPay("SelfPay"),
    Prepay("Prepay");
    private final String value;

    EnumPaymentType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EnumPaymentType fromValue(String v) {
        for (EnumPaymentType c: EnumPaymentType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
