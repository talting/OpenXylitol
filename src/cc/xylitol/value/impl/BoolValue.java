package cc.xylitol.value.impl;


import cc.xylitol.value.Value;

public class BoolValue extends Value<Boolean> {

    public float alpha = 255f;
    public BoolValue(String name, Boolean value){
        super(name);
        this.setValue(value);
    }
    public BoolValue(String name, Boolean value, Dependency dependenc){
        super(name,dependenc);
        this.setValue(value);
    }
    @Override
    public Boolean getConfigValue() {
        return value;
    }
}
