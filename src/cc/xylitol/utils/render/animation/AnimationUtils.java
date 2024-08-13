package cc.xylitol.utils.render.animation;

import lombok.Getter;
import lombok.Setter;

public class AnimationUtils {
    @Getter
    @Setter
    private static int delta;

    public static float clamp(float number, float min, float max) {
        return number < min ? min : Math.min(number, max);
    }


    public static float animateIDK(double target, double current, double speed) {
        boolean larger = (target > current);
        if (speed < 0.0F) speed = 0.0F;
        else if (speed > 1.0F) speed = 1.0F;
        double dif = Math.abs(current - target);
        double factor = dif * speed;
//        if (factor < 0.1f) factor = 0.1F;
        if (larger) current += factor;
        else current -= factor;
        return (float) current;
    }
    public static double animate(double target, double current, double speed) {
        if (current == target) return current;

        boolean larger = target > current;
        if (speed < 0.0D) {
            speed = 0.0D;
        } else if (speed > 1.0D) {
            speed = 1.0D;
        }

        double dif = Math.max(target, current) - Math.min(target, current);
        double factor = dif * speed;
        if (factor < 0.1D) {
            factor = 0.1D;
        }

        if (larger) {
            current += factor;
            if (current >= target) current = target;
        } else {
            current -= factor;
            if (current <= target) current = target;
        }

        return current;
    }

    public static float animateSmooth(float current, float target, float speed) {
        return purse(target, current, getDelta(), Math.abs(target - current) * speed);
    }

    public static float purse(float target, float current, long delta, float speed) {

        if (delta < 1L) delta = 1L;

        final float difference = current - target;

        final float smoothing = Math.max(speed * (delta / 16F), .15F);

        if (difference > speed)
            current = Math.max(current - smoothing, target);
        else if (difference < -speed)
            current = Math.min(current + smoothing, target);
        else current = target;

        return current;
    }

    public static float animate(float target, float current, float speed) {
        if (current == target) return current;

        boolean larger = target > current;
        if (speed < 0.0f) {
            speed = 0.0f;
        } else if (speed > 1.0f) {
            speed = 1.0f;
        }

        double dif = Math.max(target, current) - Math.min(target, current);
        double factor = dif * speed;
        if (factor < 0.1D) {
            factor = 0.1D;
        }

        if (larger) {
            current += factor;
            if (current >= target) current = target;
        } else {
            current -= factor;
            if (current <= target) current = target;
        }

        return current;
    }
}
