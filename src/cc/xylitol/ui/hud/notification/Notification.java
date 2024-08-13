package cc.xylitol.ui.hud.notification;

import cc.xylitol.Client;
import cc.xylitol.module.impl.render.HUD;
import cc.xylitol.ui.font.FontManager;
import cc.xylitol.utils.TimerUtil;
import cc.xylitol.utils.render.ColorUtil;
import cc.xylitol.utils.render.RenderUtil;
import cc.xylitol.utils.render.animation.Animation;
import cc.xylitol.utils.render.animation.impl.DecelerateAnimation;
import cc.xylitol.utils.render.shader.ShaderElement;
import lombok.Getter;
import net.minecraft.client.gui.Gui;

import java.awt.*;

@Getter
public class Notification {
    private final NotificationType notificationType;
    private final String title, description;
    private final float time;
    private final TimerUtil timerUtil;
    private final Animation animation;
    public String icon;

    public Notification(NotificationType type, String title, String description) {
        this(type, title, description, NotificationManager.getToggleTime());
    }

    public Notification(NotificationType type, String title, String description, float time) {
        this.title = title;
        this.description = description;
        this.time = (long) (time * 1000);
        timerUtil = new TimerUtil();
        this.notificationType = type;
        animation = new DecelerateAnimation(300, 1);

        switch (type) {
            case DISABLE:
                this.icon = "B";
                break;
            case SUCCESS:
                this.icon = "A";
                break;
            case INFO:
                this.icon = "C";
                break;
            case WARNING:
                this.icon = "D";
                break;
        }

    }

    public void drawLettuce(float x, float y, float width, float height) {
        Color color = ColorUtil.applyOpacity(ColorUtil.interpolateColorC(Color.BLACK, getNotificationType().getColor(), .65f), .7f * 100);


        float percentage = Math.min((timerUtil.getTime() / getTime()), 1);
        Gui.drawRect(x, y, x + width, y + height, new Color(0, 0, 0, 70).getRGB());
        Gui.drawRect(x, y, x + (width * percentage), y + height, color.getRGB());
        Color notificationColor = ColorUtil.applyOpacity(getNotificationType().getColor(), 70);
        Color textColor = ColorUtil.applyOpacity(Color.WHITE, 80);

        //Icon
        FontManager.icontestFont35.drawString(getNotificationType().getIcon(), x + 3, (y + FontManager.icontestFont35.getMiddleOfBox(height)), notificationColor.getRGB());

        FontManager.font20.drawString(getDescription(), x + 2.8f + FontManager.icontestFont35.getStringWidth(getNotificationType().getIcon()) + 2f, y + 8f, textColor.getRGB());
    }

    public void drawXylitol(float x, float y, float width, float height) {
        Color notificationColor = ColorUtil.applyOpacity(getNotificationType().getColor(), 70);
        float finalx;

        if (getNotificationType() == NotificationType.INFO) {
            finalx = x + 3;
        } else {
            finalx = x;
        }
        float percentage = Math.min((timerUtil.getTime() / getTime()), 1);


        RenderUtil.drawRectWH(x, y + height - 5, width, height - 8, new Color(0, 0, 0, 100).getRGB());
        RenderUtil.drawRectWH(x+width * percentage, y + height + 18, x, 1, cc.xylitol.module.impl.render.HUD.color(1).getRGB());

        FontManager.icontestFont40.drawString(this.icon, finalx + 3.5f, y + height - 2, notificationColor.getRGB());

        FontManager.font20.drawString(getTitle(), x + 20, y + 30, HUD.color(1).getRGB());
        FontManager.font16.drawString(getDescription(), x + 20, y + 40, -1);
    }

    public void drawClear(float x, float y, float width, float height) {
        Color color = ColorUtil.applyOpacity(ColorUtil.interpolateColorC(Color.BLACK, getNotificationType().getColor(), .65f), .7f * 100);


        float percentage = Math.min((timerUtil.getTime() / getTime()), 1);

        Gui.drawRect(x, y, x + width, y + height, new Color(0, 0, 0, 70).getRGB());
        Gui.drawRect(x, y, x + 1f, y + height, color.brighter().getRGB());
        Gui.drawRect(x, y, x + (width * percentage), y + height, RenderUtil.reAlpha(color.brighter(), 50).getRGB());
        Color textColor = ColorUtil.applyOpacity(Color.WHITE, 80);

        //Icon

        FontManager.font20.drawString(getDescription(), x + 6f, y + 8f, textColor.getRGB());
    }

    public void drawSimple(float x, float y, float width, float height) {
        HUD hud = Client.instance.moduleManager.getModule(HUD.class);

        ShaderElement.addBlurTask(() -> RenderUtil.drawRectWH(x, y, width, height, -1));
        ShaderElement.addBloomTask(() -> RenderUtil.drawRectWH(x, y, width, height, ColorUtil.getColor(0, 0, 0, 200)));

        String icon;
        switch (title) {
            case "Friend Manager":
                // material-supervisor_account
                icon = "\uEC2D";
                break;
            case "Config":
                // material-insert_drive_file
                icon = "\uEA21";
                break;
            case "Module":
                // material-layers
                icon = "\uEB13";
                break;
            case "IRC":
                // material-insert_comment
                icon = "\uEA20";
                break;
            default:
                // material-info
                icon = "\uEBF8";
                break;
        }

        RenderUtil.drawRectWH(x, y, width, height, ColorUtil.getColor(0, 0, 0, 76));

        float textX = x + 2 + 5;
        float textY = y + 6 - 1;

        RenderUtil.drawRectWH(x, textY, 1, 8, HUD.color(1).getRGB());

        FontManager.material18.drawStringDynamic(icon, textX - 1, textY + 2, 1, 2);
        FontManager.axBold20.drawStringDynamic(title, textX + FontManager.material18.getStringWidth(icon) + 2, y + 6, 2, 6);
        FontManager.font18.drawString(description, textX, textY + (FontManager.axBold20.getHeight() - 2), -1);
    }

    public void blurXylitol(float x, float y, float width, float height, boolean glow) {
        Color color = ColorUtil.applyOpacity(ColorUtil.interpolateColorC(Color.BLACK, getNotificationType().getColor(), glow ? 0.65f : 0), 70);
        float percentage = Math.min((timerUtil.getTime() / getTime()), 1);
        Gui.drawRect(x, y, x + width, y + height, Color.BLACK.getRGB());
        Gui.drawRect(x, y, x + 1f, y + height, HUD.color(1).getRGB());
        Gui.drawRect(x, y, x + (width * percentage), y + height, color.getRGB());
        RenderUtil.resetColor();
    }
}