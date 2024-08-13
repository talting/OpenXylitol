package cc.xylitol.value.impl;


import cc.xylitol.value.Value;
import lombok.Getter;
import lombok.Setter;

public class TextValue extends Value<String> {
    protected static TextValue self;
    @Getter
    @Setter
    public String selectedString;
    public static String DEFAULT_STRING;

    public TextValue(String name, String description, String value) {
        super(name);
        selectedString = "";
        this.value = value;
    }

    public TextValue(String name,  String value) {
        super(name);
        selectedString = "";
        this.value = value;
    }
    public TextValue(String name, String value, Dependency dependenc) {
        super(name, dependenc);
        selectedString = "";
        this.value = value;
    }

    public static TextValue create(String name) {
        return (self = new TextValue(name, DEFAULT_STRING, DEFAULT_STRING));
    }

    public TextValue withDescription(String description) {
//        self.(description);
        return self;
    }

    public TextValue defaultTo(String value) {
        DEFAULT_STRING = (value);
        return self;
    }

    @Override
    public String getConfigValue() {
        return selectedString;
    }
}
