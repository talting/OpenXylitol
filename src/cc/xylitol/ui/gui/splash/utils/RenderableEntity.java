package cc.xylitol.ui.gui.splash.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;

/**
 * @author ImXianyu
 * @since 4/15/2023 8:53 PM
 */
@AllArgsConstructor
public class RenderableEntity {

    public final Minecraft mc = Minecraft.getMinecraft();
    @Getter
    @Setter
    private double x;
    @Getter
    @Setter
    private double y;
    @Getter
    @Setter
    private double width;
    @Getter
    @Setter
    private double height;

    public void setPosition(double x, double y) {
        this.setX(x);
        this.setY(y);
    }

    public void setBounds(double width, double height) {
        this.setWidth(width);
        this.setHeight(height);
    }

}
