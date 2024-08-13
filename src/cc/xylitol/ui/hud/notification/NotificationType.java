package cc.xylitol.ui.hud.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.optifine.util.FontUtils;

import java.awt.*;

@Getter @AllArgsConstructor
public enum NotificationType {
    SUCCESS(new Color(20, 250, 90), FontUtils.CHECKMARK),
    DISABLE(new Color(255, 30, 30), FontUtils.XMARK),
    INFO(Color.DARK_GRAY, FontUtils.INFO),
    WARNING(Color.YELLOW, FontUtils.WARNING);
    private final Color color;
    private final String icon;
}