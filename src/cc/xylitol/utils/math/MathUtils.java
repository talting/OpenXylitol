package cc.xylitol.utils.math;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.Random;

public class MathUtils {

    public static final DecimalFormat DF_0 = new DecimalFormat("0");
    public static final DecimalFormat DF_1 = new DecimalFormat("0.0");
    public static final DecimalFormat DF_2 = new DecimalFormat("0.00");
    public static final DecimalFormat DF_1D = new DecimalFormat("0.#");
    public static final DecimalFormat DF_2D = new DecimalFormat("0.##");

    public static final SecureRandom secureRandom = new SecureRandom();

    public static Random random = new Random();


    public static double linearInterpolate(double min, double max, double norm) {
        return (max - min) * norm + min;
    }

    public static double roundToDecimalPlace(final double value, final double inc) {
        final double halfOfInc = inc / 2.0;
        final double floored = StrictMath.floor(value / inc) * inc;
        if (value >= floored + halfOfInc) {
            return new BigDecimal(StrictMath.ceil(value / inc) * inc, MathContext.DECIMAL64).stripTrailingZeros().doubleValue();
        }
        return new BigDecimal(floored, MathContext.DECIMAL64).stripTrailingZeros().doubleValue();
    }

    public static int clamp(int num, int min, int max) {
        if (num < min) {
            return min;
        } else {
            return Math.min(num, max);
        }
    }

    public static double clamp(double num, double min, double max) {
        if (num < min) {
            return min;
        } else {
            return Math.min(num, max);
        }
    }
    public static float[][] getArcVertices(final float radius,
                                           final float angleStart,
                                           final float angleEnd,
                                           final int segments) {
        final float range = Math.max(angleStart, angleEnd) - Math.min(angleStart, angleEnd);
        final int nSegments = Math.max(2, Math.round((360.f / range) * segments));
        final float segDeg = range / nSegments;

        final float[][] vertices = new float[nSegments + 1][2];
        for (int i = 0; i <= nSegments; i++) {
            final float angleOfVert = (angleStart + i * segDeg) / 180.f * (float) Math.PI;
            vertices[i][0] = ((float) Math.sin(angleOfVert)) * radius;
            vertices[i][1] = ((float) -Math.cos(angleOfVert)) * radius;
        }

        return vertices;
    }

    public static int getRandomInRange(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static double[] yawPos(double value) {
        return yawPos(Minecraft.getMinecraft().thePlayer.rotationYaw * MathHelper.deg2Rad, value);
    }

    public static double[] yawPos(float yaw, double value) {
        return new double[]{-MathHelper.sin(yaw) * value, MathHelper.cos(yaw) * value};
    }

    public static float getRandomInRange(float min, float max) {
        SecureRandom random = new SecureRandom();
        return random.nextFloat() * (max - min) + min;
    }

    public static double getRandomInRange(double min, double max) {
        SecureRandom random = new SecureRandom();
        return min == max ? min : random.nextDouble() * (max - min) + min;
    }

    public static int getRandomNumberUsingNextInt(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

    public static double lerp(double old, double newVal, double amount) {
        return (1.0 - amount) * old + amount * newVal;
    }

    public static Double interpolate(double oldValue, double newValue, double interpolationValue) {
        return (oldValue + (newValue - oldValue) * interpolationValue);
    }


    public static double interpolate2(double current, double old, double scale) {
        return old + (current - old) * scale;
    }

    public static float interpolateFloat(float oldValue, float newValue, double interpolationValue) {
        return interpolate(oldValue, newValue, (float) interpolationValue).floatValue();
    }

    public static int interpolateInt(int oldValue, int newValue, double interpolationValue) {
        return interpolate(oldValue, newValue, (float) interpolationValue).intValue();
    }

    public static float calculateGaussianValue(float x, float sigma) {
        double output = 1.0 / Math.sqrt(2.0 * Math.PI * (sigma * sigma));
        return (float) (output * Math.exp(-(x * x) / (2.0 * (sigma * sigma))));
    }

    public static double roundToHalf(double d) {
        return Math.round(d * 2) / 2.0;
    }

    public static double round(double num, double increment) {
        BigDecimal bd = new BigDecimal(num);
        bd = (bd.setScale((int) increment, RoundingMode.HALF_UP));
        return bd.doubleValue();
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static String round(String value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.stripTrailingZeros();
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.toString();
    }

    public static Random getRandom() {
        return random;
    }

    public static float getRandomFloat(float max, float min) {
        SecureRandom random = new SecureRandom();
        return random.nextFloat() * (max - min) + min;
    }

    public static int getRandom(final int min, final int max) {
        if (max < min) {
            return 0;
        }
        return min + random.nextInt((max - min) + 1);
    }

    public static long getRandom(long min, long max) {

        long range = max - min;
        long scaled = random.nextLong() * range;
        if (scaled > max) {
            scaled = max;
        }
        long shifted = scaled + min;

        if (shifted > max) {
            shifted = max;
        }
        return shifted;
    }

    public static double getRandom(double min, double max) {

        double range = max - min;
        double scaled = random.nextDouble() * range;
        if (scaled > max) {
            scaled = max;
        }
        double shifted = scaled + min;

        if (shifted > max) {
            shifted = max;
        }
        return shifted;
    }

    public static int getNumberOfDecimalPlace(double value) {
        final BigDecimal bigDecimal = new BigDecimal(value);
        return Math.max(0, bigDecimal.stripTrailingZeros().scale());
    }

}
