package cc.xylitol.module.impl.render;

import cc.xylitol.Client;
import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventPreRender;
import cc.xylitol.event.impl.events.EventRender2D;
import cc.xylitol.event.impl.events.EventTick;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import cc.xylitol.module.impl.world.Scaffold;
import cc.xylitol.ui.hud.notification.Notification;
import cc.xylitol.ui.hud.notification.NotificationManager;
import cc.xylitol.utils.render.ColorUtil;
import cc.xylitol.utils.render.RenderUtil;
import cc.xylitol.utils.render.animation.Animation;
import cc.xylitol.utils.render.animation.Direction;
import cc.xylitol.utils.render.shader.KawaseBloom;
import cc.xylitol.utils.render.shader.KawaseBlur;
import cc.xylitol.utils.render.shader.ShaderElement;
import cc.xylitol.value.impl.*;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.boss.BossStatus;

import java.awt.*;

import static cc.xylitol.ui.font.FontManager.*;
import static cc.xylitol.utils.render.shader.ShaderElement.createFrameBuffer;

public class HUD extends Module {
    public HUD() {
        super("HUD", Category.Render);
    }

    public ModeValue notiMode = new ModeValue("Notification Mode", new String[]{"Lettuce", "Xylitol", "Clear", "Simple"}, "Xylitol");
    public static ModeValue colorMode = new ModeValue("Color Mode", new String[]{"Fade", "Static", "Double"}, "Fade");

    public static ColorValue mainColor = new ColorValue("Main Color", new Color(255, 175, 63).getRGB());
    public static ColorValue secondColor = new ColorValue("Second Color", new Color(255, 175, 63).getRGB(), () -> colorMode.is("Double"));

    public BoolValue blur = new BoolValue("Blur", false);

    public final NumberValue iterations = new NumberValue("Blur Iterations", 2, 1, 8, 1, () -> blur.getValue());
    public final NumberValue offset = new NumberValue("Blur Offset", 3, 1, 10, 1, () -> blur.getValue());
    public BoolValue bloom = new BoolValue("Bloom (Shadow)", false);

    public final NumberValue shadowRadius = new NumberValue("Bloom Iterations", 3, 1, 8, 1, () -> bloom.getValue());
    public final NumberValue shadowOffset = new NumberValue("Bloom Offset", 1, 1, 10, 1, () -> bloom.getValue());
    public int offsetValue = 0;
    private Framebuffer bloomFramebuffer = new Framebuffer(1, 1, false);
    private Framebuffer stencilFramebuffer = new Framebuffer(1, 1, false);

    private Scaffold scaffold;
    ScaledResolution sr;

    public static TextValue markTextValue = new TextValue("Watermark Text", "new SilenceFix()");

    @Override
    public void onEnable() {
        scaffold = getModule(Scaffold.class);
    }

    public static Color color(int tick) {
        Color textColor = new Color(-1);
        switch (colorMode.get()) {
            case "Fade":
                textColor = ColorUtil.fade(5, tick * 20, new Color(mainColor.getColor()), 1);
                break;
            case "Static":
                textColor = mainColor.getColorC();
                break;
            case "Double":
                tick *= 200;
                textColor = new Color(RenderUtil.colorSwitch(mainColor.getColorC(), secondColor.getColorC(), 2000, -tick / 40, 75, 2));
                break;
        }
        return textColor;
    }

    public void drawWaterMark() {
        sr = new ScaledResolution(mc);
        mc.fontRendererObj.drawStringWithShadow("WatchDog Inactive", sr.getScaledWidth() / 2F - mc.fontRendererObj.getStringWidth("WatchDog Inactive") / 2F, (((BossStatus.bossName != null && BossStatus.statusBarTime > 0) ? 47 : 30) - mc.fontRendererObj.FONT_HEIGHT - 2f), -1);
    }

    public void drawBlur() {
        stencilFramebuffer = createFrameBuffer(stencilFramebuffer);

        stencilFramebuffer.framebufferClear();
        stencilFramebuffer.bindFramebuffer(false);

        for (Runnable runnable : ShaderElement.getTasks()) {
            runnable.run();
        }
        ShaderElement.getTasks().clear();

        stencilFramebuffer.unbindFramebuffer();

        KawaseBlur.renderBlur(stencilFramebuffer.framebufferTexture, iterations.getValue().intValue(), offset.getValue().intValue());
    }

    public void drawBloom() {

        bloomFramebuffer = createFrameBuffer(bloomFramebuffer);
        bloomFramebuffer.framebufferClear();
        bloomFramebuffer.bindFramebuffer(false);

        for (Runnable runnable : ShaderElement.getBloomTasks()) {
            runnable.run();
        }
        ShaderElement.getBloomTasks().clear();

        bloomFramebuffer.unbindFramebuffer();

        KawaseBloom.renderBlur(bloomFramebuffer.framebufferTexture, shadowRadius.getValue().intValue(), shadowOffset.getValue().intValue());
    }

    public void drawNotifications() {
        ScaledResolution sr = new ScaledResolution(mc);
        float yOffset = 0;
        int notificationHeight = 0, notificationWidth = 0, actualOffset;

        NotificationManager.setToggleTime(2f);

        for (Notification notification : NotificationManager.getNotifications()) {
            Animation animation = notification.getAnimation();
            animation.setDirection(notification.getTimerUtil().hasTimeElapsed((long) notification.getTime()) ? Direction.BACKWARDS : Direction.FORWARDS);

            if (animation.finished(Direction.BACKWARDS)) {
                NotificationManager.getNotifications().remove(notification);
                continue;
            }

            float x, y;
            animation.setDuration(200);
            actualOffset = notiMode.is("Xylitol") ? -7 : 3;
            switch (notiMode.getValue()) {
                case "Xylitol":
                    notificationHeight = 32;
                    notificationWidth = Math.max(font20.getStringWidth(notification.getDescription()), font20.getStringWidth(notification.getTitle())) + 15;
                    break;
                case "Lettuce":
                    notificationHeight = 23;
                    notificationWidth = font20.getStringWidth(notification.getDescription()) + 25;
                    break;
                case "Clear":
                    notificationHeight = 23;
                    notificationWidth = font20.getStringWidth(notification.getDescription()) + 15;
                    break;
                case "Simple":
                    notificationHeight = 6 + (axBold20.getHeight() - 2) + 2 + (font18.getHeight() - 2);
                    notificationWidth = font18.getStringWidth(notification.getDescription()) + 20;
                    break;
            }

            x = (float) (sr.getScaledWidth() - (notificationWidth) * animation.getOutput());
            y = sr.getScaledHeight() - (yOffset + 18 + offsetValue + notificationHeight + 15);
            switch (notiMode.getValue()) {
                case "Lettuce":
                    notification.drawLettuce(x, y, notificationWidth, notificationHeight);
                    break;
                case "Xylitol":
                    notification.drawXylitol(x, y, notificationWidth, notificationHeight);
                    break;
                case "Clear":
                    notification.drawClear(x, y, notificationWidth, notificationHeight);
                    break;
                case "Simple":
                    notification.drawSimple(x, y, notificationWidth, notificationHeight);
                    break;
            }
            yOffset += (notificationHeight + actualOffset) * animation.getOutput();
        }
    }

    @EventTarget
    public void onPreRender(EventPreRender e) {
        for (cc.xylitol.ui.hud.HUD hud : Client.instance.hudManager.hudObjects.values()) {
            if (hud.m.getState())
                hud.predraw();
        }
    }

    @EventTarget
    public void onTick(EventTick e) {
        for (cc.xylitol.ui.hud.HUD hud : Client.instance.hudManager.hudObjects.values()) {
            if (hud.m.getState())
                hud.onTick();
        }
    }

    @EventTarget
    public void onRender2D(EventRender2D e) {
        for (cc.xylitol.ui.hud.HUD hud : Client.instance.hudManager.hudObjects.values()) {
            if (hud.m.getState())
                hud.draw(e.getPartialTicks());
        }
        drawNotifications();
        if (mc.thePlayer != null && mc.theWorld != null) {
            scaffold.renderCounter();
        }

    }

    public static class CounterBar {


    }
}
