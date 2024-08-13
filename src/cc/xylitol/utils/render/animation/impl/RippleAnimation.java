package cc.xylitol.utils.render.animation.impl;

import cc.xylitol.utils.render.RenderUtil;
import cc.xylitol.utils.render.StencilUtil;
import cc.xylitol.utils.render.animation.AnimationUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


/**
 * 涟漪动画
 *
 * @author cubk
 */
public class RippleAnimation {
    public final List<Ripple> ripples = new ArrayList<>();

    public void addRipple(float x, float y, float radius, float speed) {
        ripples.add(new Ripple(x, y, radius, speed));
    }

    public void mouseClicked(float mouseX, float mouseY) {
        ripples.add(new Ripple(mouseX, mouseY, 100, 1));
    }

    public void draw(float x, float y, float width, float height) {
        if (!ripples.isEmpty()) {
            StencilUtil.initStencilToWrite();
            RenderUtil.drawRect(x, y, width, height, -1);
            StencilUtil.readStencilBuffer(528);
            for (Ripple c : ripples) {
                c.progress = AnimationUtils.animateSmooth(c.progress, c.topRadius, c.speed / 10F);
                RenderUtil.drawCircleCGUI(c.x, c.y, c.progress, new Color(1f, 1f, 1f, (1 - Math.min(1f, Math.max(0f, c.progress / c.topRadius))) / 2).getRGB());
            }
            StencilUtil.endStencilBuffer();
        }
    }

    public void draw(Runnable context) {
        if (!ripples.isEmpty()) {
            StencilUtil.initStencilToWrite();
            context.run();
            StencilUtil.readStencilBuffer(528);
            for (Ripple c : ripples) {
                c.progress = AnimationUtils.animateSmooth(c.progress, c.topRadius, c.speed / 10F);
                RenderUtil.drawCircleCGUI(c.x, c.y, c.progress, new Color(1f, 1f, 1f, (1 - Math.min(1f, Math.max(0f, c.progress / c.topRadius))) / 2).getRGB());
            }
            StencilUtil.endStencilBuffer();
        }
    }

    public static class Ripple {
        public float x;
        public float y;
        public float topRadius;
        public float speed;
        public float alpha;
        public float progress;
        public boolean complete;

        public Ripple(float x, float y, float rad, float speed) {
            this.x = x;
            this.y = y;
            this.alpha = 200;
            this.topRadius = rad;
            this.speed = speed;
        }

    }
}

