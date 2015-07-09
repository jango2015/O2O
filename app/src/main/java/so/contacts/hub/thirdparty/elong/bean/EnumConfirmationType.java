package so.contacts.hub.thirdparty.elong.bean;

public enum EnumConfirmationType {

    NoNeed("NoNeed"),
    SMS_cn("SMS_cn"),
    SMS_en("SMS_en"),
    Email_cn("Email_cn"),
    Email_en("Email_en"),
    Phone("Phone"),
    Fax("Fax"),
    Fax_big5("Fax_big5"),
    SMS_cn_big5("SMS_cn_big5"),
    Email_cn_big5("Email_cn_big5"),
    NotAllowedConfirm("NotAllowedConfirm");
    private final String value;

    EnumConfirmationType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EnumConfirmationType fromValue(String v) {
        for (EnumConfirmationType c: EnumConfirmationType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
