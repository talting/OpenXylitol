package cc.xylitol.value.impl;


import cc.xylitol.value.Value;

public class ModeValue extends Value<String> {
    private final String[] modes;

    public ModeValue(String name, String[] modes, String value) {
        super(name);
        this.modes = modes;
        this.setValue(value);
    }

    public ModeValue(String name, String[] modes, String value, Dependency dependenc) {
        super(name, dependenc);
        this.modes = modes;
        this.setValue(value);
    }

    public boolean is(String sb) {
        return this.getValue().equalsIgnoreCase(sb);
    }

    public String[] getModes() {
        return this.modes;
    }

    public String getModeAsString() {
        return this.getValue();
    }

    public void setMode(String mode) {
        String[] arrV = this.modes;
        int n = arrV.length;
        int n2 = 0;
        while (n2 < n) {
            String e = arrV[n2];
            if (e == null)
                return;
            if (e.equalsIgnoreCase(mode)) {
                this.setValue(e);
            }
            ++n2;
        }
    }

    @Override
    public String getConfigValue() {
        return value;
    }
}

