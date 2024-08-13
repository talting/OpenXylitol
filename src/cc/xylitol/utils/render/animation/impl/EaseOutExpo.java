package cc.xylitol.utils.render.animation.impl;

import cc.xylitol.utils.render.animation.Animation;
import cc.xylitol.utils.render.animation.Direction;
import net.minecraft.util.MathHelper;

public class EaseOutExpo extends Animation {
    public EaseOutExpo(int ms, double endPoint) {
        super(ms, endPoint);
    }

    public EaseOutExpo(int ms, double endPoint, Direction direction) {
        super(ms, endPoint, direction);
    }

    protected double getEquation(double x) {
        return MathHelper.epsilonEquals((float) x, 1.0F) ? 1 : 1 - Math.pow(2, -10 * x);
    }
}
