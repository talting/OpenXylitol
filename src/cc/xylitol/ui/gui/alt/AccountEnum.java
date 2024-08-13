package cc.xylitol.ui.gui.alt;

public enum AccountEnum {
    OFFLINE("OFFLINE"),
    MICROSOFT("MICROSOFT");

    private final String writeName;

    AccountEnum(String name) {
        this.writeName = name;
    }

    public static AccountEnum parse(String str) {
        for (AccountEnum value : values()) {
            if (value.writeName.equals(str)) {
                return value;
            }
        }

        return null;
    }
}
