package cc.xylitol.ui.gui.clickgui;

import cc.xylitol.Client;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import cc.xylitol.ui.font.FontManager;
import cc.xylitol.utils.render.RenderUtil;
import cc.xylitol.utils.render.RoundedUtil;
import cc.xylitol.utils.render.animation.AnimationUtils;
import cc.xylitol.utils.render.animation.impl.EaseBackIn;
import cc.xylitol.utils.render.animation.impl.OutputAnimation;
import org.lwjglx.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * @author xiatian233, ChengFeng
 */
public class CategoryPanel {
    private final Category category;
    private final OutputAnimation animation = new OutputAnimation(0);
    ArrayList<ModuleRender> moduleRenders;
    int width;
    int height;
    int moduley;
    int roundindexy;
    int xPosition;
    int yPosition;
    int animateTarget = 0;
    int alphaTarget = 0;
    int nowAlpha = 0;
    int scroll = 0;
    int targetScroll = 0;
    private boolean extended = false;

    public CategoryPanel(Category category) {
        this.category = category;
        moduleRenders = new ArrayList();
        for (Module m : Client.instance.moduleManager.getModsByCategory(category).stream().sorted(Comparator.comparing(Module::getName)).collect(Collectors.toList())) {
            moduleRenders.add(new ModuleRender(m));
        }
    }

    public void setExtended(boolean value) {
        extended = value;

        if (extended) {
            animateTarget = 200;
            alphaTarget = 255;
        } else {
            animateTarget = 0;
            alphaTarget = 0;
        }
    }

    public void drawscreen(int mousex, int mousey, int x, int y, int alpha, EaseBackIn easeBackIn) {
        width = 100;
        height = 20;
        moduley = 0;
        roundindexy = 0;
        xPosition = x;
        yPosition = y;
        for (ModuleRender moduleRender : moduleRenders) {
            roundindexy += FontManager.font18.getHeight() + 7;
            if (moduleRender.cansetvalue) {
                roundindexy += moduleRender.index;
            }
        }
        animation.animate(animateTarget, 0.2f);

        RoundedUtil.drawRound(x, y, width, height + (int) animation.getOutput(), 3f, new Color(0, 0, 0, alpha));
        FontManager.font20.drawString(category.name(), x + width / 2 - FontManager.font20.getStringWidth(category.name()) / 2, y + height / 2 - FontManager.font20.getHeight() / 2, -1);

        nowAlpha = (int) AnimationUtils.animate(alphaTarget, nowAlpha, 0.1);
        RenderUtil.drawRect(x + 5, y + 19, x + width - 5, y + 20, new Color(255, 255, 255, nowAlpha).getRGB());

        if (mousex >= x && mousex <= x + width && mousey >= y && mousey <= y + height + animation.getOutput()) {
            int wheel = Mouse.getDWheel();
            if (wheel != 0) {
                if (wheel > 0) {
                    targetScroll += 15;
                } else targetScroll -= 15;

                if (targetScroll > 0) targetScroll = 0;
            }
        }

        scroll = (int) AnimationUtils.animate(targetScroll, scroll, 0.9);

        RenderUtil.startGlScissor(x, y + 20, width, (int) (height + animation.getOutput()) - 20);
        for (ModuleRender moduleRender : moduleRenders) {
            moduleRender.settinganimation.animate(moduley, 20);
            moduleRender.draw(mousex, mousey, x, y + scroll, (int) moduleRender.settinganimation.getOutput());
            moduley += FontManager.font18.getHeight() + 7;
            if (moduleRender.cansetvalue) {
                moduley += moduleRender.index;
            }
        }
        RenderUtil.stopGlScissor();
    }

    //draw shader
    public void drawshader(int x, int y) {
        RoundedUtil.drawRound(x, y, width, height + (int) animation.getOutput(), 3f, new Color(0, 0, 0, 255));
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseX >= xPosition && mouseX <= xPosition + width && mouseY >= yPosition + 20 && mouseY <= yPosition + height + animation.getOutput() - 3) {
            for (ModuleRender moduleRender : moduleRenders) {
                moduleRender.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }

        if (mouseX >= xPosition && mouseX <= xPosition + width && mouseY >= yPosition && mouseY <= yPosition + 20 && mouseButton == 1) {
            setExtended(!extended);
        }
    }

    public void keyTyped(int keyCode) {
        for (ModuleRender moduleRender : moduleRenders) {
            moduleRender.keyTyped(keyCode);
        }
    }
}
