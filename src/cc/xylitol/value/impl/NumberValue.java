package cc.xylitol.value.impl;

import cc.xylitol.value.Value;

public class NumberValue extends Value<Double> {
    public float animatedPercentage;
    public boolean sliding;
    double max, min, inc;

    public NumberValue(String name, double val, double min, double max, double inc) {
        super(name);
        this.setValue(val);
        this.max = max;
        this.min = min;
        this.inc = inc;
    }

    public NumberValue(String name, double val, double min, double max, double inc, Dependency dependenc) {
        super(name, dependenc);
        this.setValue(val);
        this.max = max;
        this.min = min;
        this.inc = inc;
    }

    public Double getMax() {
        return this.max;
    }

    public Double getMin() {
        return this.min;
    }

    public Double getInc() {
        return this.inc;
    }

    @Override
    public Double getConfigValue() {
        return getValue();
    }
}
