package cc.xylitol.ui.gui.splash.impl;

import cc.xylitol.ui.gui.splash.LoadingScreenRenderer;
import cc.xylitol.ui.gui.splash.utils.Image;
import cc.xylitol.ui.gui.splash.utils.Rect;
import cc.xylitol.utils.TimerUtil;
import cc.xylitol.utils.render.RenderUtil;
import lombok.Getter;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GenshinImpactLoadingScreen extends LoadingScreenRenderer {


    FadeInOutImage gs1;

    TimerUtil startTimer = new TimerUtil();
    boolean firstFrame = false;

    @Override
    public void init() {
        super.init();
        gs1 = new FadeInOutImage(new ResourceLocation("xylitol/gs1.png"));
    }

    @Override
    public void render(int width, int height) {
        Rect.draw(0, 0, width, height, RenderUtil.hexColor(255, 255, 255), Rect.RectType.ABSOLUTE_POSITION);

        if (!firstFrame) {
            firstFrame = true;
            startTimer.reset();
        }

        if (!startTimer.hasTimeElapsed(1000))
            return;

        if (!gs1.isFinished())
            gs1.render(width, height);
    }

    @Override
    public boolean isLoadingScreenFinished() {
        return gs1.isFinished();
    }

    private static class FadeInOutImage {

        @Getter
        private final ResourceLocation img;

        float screeMaskAlpha = 0;
        boolean increasing = true;

        boolean finished = false;

        boolean firstFrame = false;

        TimerUtil timer = new TimerUtil();

        public FadeInOutImage(ResourceLocation loc) {
            img = loc;
        }

        public void render(int width, int height) {

            if (!firstFrame) {
                firstFrame = true;
                timer.reset();
            }

            if (increasing || timer.hasTimeElapsed(2000)) {
                screeMaskAlpha += increasing ? 1 * 0.003921568627451F : -1 * 0.003921568627451F;
            }

            if ((!increasing && screeMaskAlpha < 0.01))
                finished = true;

            if (increasing && screeMaskAlpha > 0.99) {
                increasing = false;
                timer.reset();
            }

            GL11.glColor4f(1, 1, 1, screeMaskAlpha);
            Image.draw(img, 0, 0, width, height, Image.Type.NoColor);

        }

        public boolean isFinished() {
            return finished;
        }
    }
}
