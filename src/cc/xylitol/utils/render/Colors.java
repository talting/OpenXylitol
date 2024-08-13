package cc.xylitol.utils.render;

import cc.xylitol.utils.math.MathUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

import java.awt.*;
import java.text.NumberFormat;

public class Colors {
    public static final int BLACK = Color.BLACK.getRGB();
    public static final int WHITE = Color.WHITE.getRGB();
    public static final int RED = new Color(0xf44336).getRGB();
    public static final int PINK = new Color(0xff80ab).getRGB();
    public static final int PURPLE = new Color(0xba68c8).getRGB();
    public static final int DEEP_PURPLE = new Color(0x7E5EB5).getRGB();
    public static final int INDIGO = new Color(0x7986cb).getRGB();
    public static final int GREY = new Color(-9868951).getRGB();
    public static final int BLUE = new Color(0x1976d2).getRGB();
    public static final int LIGHT_BLUE = new Color(0x74C3FF).getRGB();
    public static final int CYAN = new Color(0x00ACC1).getRGB();
    public static final int TEAL = new Color(0xA7FFEB).getRGB();
    public static final int GREEN = new Color(0x00FF46).getRGB();

    public static int getColor(final int red, final int green, final int blue) {
        return getColor(red, green, blue, 255);
    }

    public static int getColor(final Color color) {
        return getColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public static int getColor(final int brightness) {
        return getColor(brightness, brightness, brightness, 255);
    }

    public static int getColor(final int brightness, final int alpha) {
        return getColor(brightness, brightness, brightness, alpha);
    }

    public static int getColor(final int red, final int green, final int blue, final int alpha) {
        int color = MathHelper.clamp_int(alpha, 0, 255) << 24;
        color |= MathHelper.clamp_int(red, 0, 255) << 16;
        color |= MathHelper.clamp_int(green, 0, 255) << 8;
        color |= MathHelper.clamp_int(blue, 0, 255);
        return color;
    }

    public static int rainbow(long delay) {
        double rainbowState = (System.currentTimeMillis() + delay * 1.2) / 8.0;
        return Color.getHSBColor((float)(rainbowState % 360.0 / 360.0), 0.5F, 0.9F).getRGB();
    }

    public static Color blendColors(final float[] fractions, final Color[] colors, final float progress) {
        if (fractions == null) {
            throw new IllegalArgumentException("Fractions can't be null");
        }
        if (colors == null) {
            throw new IllegalArgumentException("Colours can't be null");
        }
        if (fractions.length == colors.length) {
            final int[] indicies = getFractionIndicies(fractions, progress);
            final float[] range = { fractions[indicies[0]], fractions[indicies[1]] };
            final Color[] colorRange = { colors[indicies[0]], colors[indicies[1]] };
            final float max = range[1] - range[0];
            final float value = progress - range[0];
            final float weight = value / max;
            final Color color = blend(colorRange[0], colorRange[1], 1.0f - weight);
            return color;
        }
        throw new IllegalArgumentException("Fractions and colours must have equal number of elements");
    }

    public static int removeAlphaComponent(final int colour) {
        final int red = colour >> 16 & 0xFF;
        final int green = colour >> 8 & 0xFF;
        final int blue = colour & 0xFF;

        return ((red & 0xFF) << 16) |
                ((green & 0xFF) << 8) |
                (blue & 0xFF);
    }

    public static int[] getFractionIndicies(final float[] fractions, final float progress) {
        final int[] range = new int[2];
        int startPoint;
        for (startPoint = 0; startPoint < fractions.length && fractions[startPoint] <= progress; ++startPoint) {}
        if (startPoint >= fractions.length) {
            startPoint = fractions.length - 1;
        }
        range[0] = startPoint - 1;
        range[1] = startPoint;
        return range;
    }

    public static Color blend(final Color color1, final Color color2, final double ratio) {
        final float r = (float)ratio;
        final float ir = 1.0f - r;
        final float[] rgb1 = new float[3];
        final float[] rgb2 = new float[3];
        color1.getColorComponents(rgb1);
        color2.getColorComponents(rgb2);
        float red = rgb1[0] * r + rgb2[0] * ir;
        float green = rgb1[1] * r + rgb2[1] * ir;
        float blue = rgb1[2] * r + rgb2[2] * ir;
        if (red < 0.0f) {
            red = 0.0f;
        }
        else if (red > 255.0f) {
            red = 255.0f;
        }
        if (green < 0.0f) {
            green = 0.0f;
        }
        else if (green > 255.0f) {
            green = 255.0f;
        }
        if (blue < 0.0f) {
            blue = 0.0f;
        }
        else if (blue > 255.0f) {
            blue = 255.0f;
        }
        Color color3 = null;
        try {
            color3 = new Color(red, green, blue);
        }
        catch (IllegalArgumentException exp) {
            final NumberFormat nf = NumberFormat.getNumberInstance();
            System.out.println(nf.format(red) + "; " + nf.format(green) + "; " + nf.format(blue));
            exp.printStackTrace();
        }
        return color3;
    }

    public static Color interpolateColors(Color color1, Color color2, float point) {
        if (point > 1)
            point = 1;
        return new Color((int) ((color2.getRed() - color1.getRed()) * point + color1.getRed()),
                (int) ((color2.getGreen() - color1.getGreen()) * point + color1.getGreen()),
                (int) ((color2.getBlue() - color1.getBlue()) * point + color1.getBlue()));
    }

    public static Color interpolateColorsDynamic(double speed, int index, Color start, Color end) {
        int angle = (int) (((System.currentTimeMillis()) / speed + index) % 360);
        angle = (angle >= 180 ? 360 - angle : angle) * 2;
        return interpolateColors(start, end, angle / 360f);
    }

    public static Color getHealthColor(EntityLivingBase e, int alpha){
        final double health = e.getHealth();
        final double maxHealth = e.getMaxHealth();
        final double healthPercentage = health / maxHealth;
        final Color c1 = Color.RED;
        final Color c2 = Color.GREEN;
        final Color healthColor = new Color(
                (int) (MathUtils.linearInterpolate(c1.getRed(), c2.getRed(), healthPercentage)),
                (int) (MathUtils.linearInterpolate(c1.getGreen(), c2.getGreen(), healthPercentage)),
                (int) (MathUtils.linearInterpolate(c1.getBlue(), c2.getBlue(), healthPercentage)), alpha
        );
        return healthColor.darker();
    }

    //Opacity value ranges from 0-1
    public static Color applyOpacity(Color color, float opacity) {
        opacity = Math.min(1, Math.max(0, opacity));
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (color.getAlpha() * opacity));
    }

    public static int astolfoRainbow(final int offset, final float saturation, final float brightness) {
        double currentColor = Math.ceil((double)(System.currentTimeMillis() + offset * 130L)) / 6.0;
        return Color.getHSBColor(((float)((currentColor %= 360.0) / 360.0) < 0.5) ? (-(float)(currentColor / 360.0)) : ((float)(currentColor / 360.0)), saturation, brightness).getRGB();
    }
}