package cc.xylitol.utils.render.animation.impl;

import cc.xylitol.utils.render.animation.AnimationUtils;

/**
 * @author ChengFeng
 * @since 2023/3/18
 */
public class OutputAnimation {
    private double now;

    public OutputAnimation(int now) {
        this.now = now;
    }

    public void animate(double target, float speed) {
        now = AnimationUtils.animate(target, now, speed);
    }

    public double getOutput() {
        return now;
    }

    public void setNow(int now) {
        this.now = now;
    }
}
