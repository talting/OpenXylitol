package cc.xylitol.ui.gui.splash.utils;

import cc.xylitol.utils.render.RenderUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * @author ImXianyu
 * @since 4/15/2023 8:54 PM
 */

public class Rect extends RenderableEntity {

    @Getter
    @Setter
    private int color;

    @Getter
    @Setter
    private RectType rectType;

    public Rect(double x, double y, double width, double height, int color, RectType type) {
        super(x, y, width, height);

        this.setColor(color);
        this.setRectType(type);
    }


    public static void draw(double x, double y, double x2, double y2, int color, RectType type) {
        if (type == RectType.EXPAND) {
            RenderUtil.drawRect(x, y, x + x2, y + y2, color);
        } else if (type == RectType.ABSOLUTE_POSITION) {
            RenderUtil.drawRect(x, y, x2, y2, color);
        }
    }

    public void draw() {

        if (this.getRectType() == RectType.EXPAND) {
            RenderUtil.drawRect(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), this.getColor());
        } else if (this.getRectType() == RectType.ABSOLUTE_POSITION) {
            RenderUtil.drawRect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), this.getColor());
        }
    }

    public enum RectType {
        EXPAND,
        ABSOLUTE_POSITION
    }

}
