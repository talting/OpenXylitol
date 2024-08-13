package cc.xylitol.module;

import cc.xylitol.Client;
import cc.xylitol.ui.hud.notification.NotificationManager;
import cc.xylitol.ui.hud.notification.NotificationType;
import cc.xylitol.utils.render.animation.Animation;
import cc.xylitol.utils.render.animation.Direction;
import cc.xylitol.utils.render.animation.impl.DecelerateAnimation;
import cc.xylitol.value.Value;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.List;

public class Module {
    protected static final Minecraft mc = Minecraft.getMinecraft();


    @Getter
    public float cGUIAnimation = 0f;
    @Getter
    private final Animation animation = new DecelerateAnimation(250, 1).setDirection(Direction.BACKWARDS);
    @Getter
    private final List<Value<?>> values = new ArrayList<>();
    /*
     * Information
     */
    @Getter
    public String name;
    @Getter
    public String suffix;
    @Getter
    public Category category;
    public boolean state = false;
    @Getter
    public boolean defaultOn = false;
    @Setter
    @Getter
    public int key = -1;
    @Setter
    @Getter
    public int mouseKey = -1;
    /*
     * Values
     */
    @Setter
    @Getter
    public double progress;

    public Module(String name, Category category) {
        this.name = name;
        this.category = category;
        this.suffix = "";
    }

    public void toggle() {
        this.setState(!this.state);
    }

    public void onEnable() {

    }

    public void onDisable() {

    }

    public static <T extends Module> T getModule(Class<T> clazz) {
        return Client.instance.moduleManager.getModule(clazz);
    }

    public boolean getState() {
        return this.state;
    }

    public void setState(boolean state) {
        if (this.state == state) return;

        this.state = state;

        if (mc.theWorld != null)
            mc.theWorld.playSound(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, "random.click", 0.5F, state ? 0.6F : 0.5F, false);


        if (state) {
            Client.instance.eventManager.register(this);
            NotificationManager.post(NotificationType.SUCCESS, "Module", "Enabled " + this.name);

            onEnable();
        } else {
            Client.instance.eventManager.unregister(this);
            NotificationManager.post(NotificationType.DISABLE, "Module", "Disabled " + this.name);


            onDisable();
        }
    }

    public void setStateSilent(boolean state) {
        if (this.state == state) return;

        this.state = state;

        if (mc.theWorld != null)
            mc.theWorld.playSound(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, "random.click", 0.5F, state ? 0.6F : 0.5F, false);
        if (state) {
            Client.instance.eventManager.register(this);
        } else {
            Client.instance.eventManager.unregister(this);
        }
    }

    public void setSuffix(Object obj) {
        String suffix = obj.toString();
        if (suffix.isEmpty()) {
            this.suffix = suffix;
        } else {
            this.suffix = String.format("§f%s§7", EnumChatFormatting.GRAY + suffix);
        }
    }

}
