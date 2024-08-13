package cc.xylitol.value.impl;

import cc.xylitol.value.Value;

import java.awt.*;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class ColorValue extends Value<Integer> {
    private int color;

    protected boolean alphaChangeable = false;
    protected boolean enabledRainbow = false;
    protected boolean rainbowChangeable = false;

    public ColorValue(String name, int color, Dependency dependenc) {
        super(name, dependenc);
        this.setValue(color);
        this.color = color;
    }

    public ColorValue(String name, int color) {
        super(name);
        this.setValue(color);
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public Color getColorC() {
        return new Color(color);
    }

    public void setColor(int color) {
        this.setValue(color);
        this.color = color;
    }


    public boolean isAlphaChangeable() {
        return this.alphaChangeable;
    }

    public void setAlphaChangeable(boolean alphaChangeable) {
        this.alphaChangeable = alphaChangeable;
    }


    public void setRainbowEnabled(boolean enabledRainbow) {
        this.enabledRainbow = !this.rainbowChangeable ? false : enabledRainbow;
    }

    public boolean isEnabledRainbow() {
        return this.enabledRainbow;
    }

    public float[] getHSB() {
        if (value == null) return new float[]{0.0F, 0.0F, 0.0F};
        float[] hsbValues = new float[3];

        float saturation, brightness;
        float hue;

        int cMax = max(value >>> 16 & 0xFF, value >>> 8 & 0xFF);
        if ((value & 0xFF) > cMax) cMax = value & 0xFF;

        int cMin = min(value >>> 16 & 0xFF, value >>> 8 & 0xFF);
        if ((value & 0xFF) < cMin) cMin = value & 0xFF;

        brightness = (float) cMax / 255.0F;
        saturation = cMax != 0 ? (float) (cMax - cMin) / (float) cMax : 0;

        if (saturation == 0) {
            hue = 0;
        } else {
            float redC = (float) (cMax - (value >>> 16 & 0xFF)) / (float) (cMax - cMin), // @off
                    greenC = (float) (cMax - (value >>> 8 & 0xFF)) / (float) (cMax - cMin),
                    blueC = (float) (cMax - (value & 0xFF)) / (float) (cMax - cMin); // @on

            hue = ((value >>> 16 & 0xFF) == cMax ?
                    blueC - greenC :
                    (value >>> 8 & 0xFF) == cMax ? 2.0F + redC - blueC : 4.0F + greenC - redC) / 6.0F;

            if (hue < 0) hue += 1.0F;
        }

        hsbValues[0] = hue;
        hsbValues[1] = saturation;
        hsbValues[2] = brightness;

        return hsbValues;
    }

    @Override
    public Integer getConfigValue() {
        return color;
    }
}
