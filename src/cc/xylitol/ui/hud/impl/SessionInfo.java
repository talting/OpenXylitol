package cc.xylitol.ui.hud.impl;

import cc.xylitol.Client;
import cc.xylitol.ui.font.FontManager;
import cc.xylitol.ui.hud.HUD;
import cc.xylitol.utils.render.RenderUtil;
import cc.xylitol.utils.render.shader.ShaderElement;
import top.fl0wowp4rty.phantomshield.api.User;

import java.awt.*;

public class SessionInfo extends HUD {

    public static int startTime;

    public SessionInfo() {
        super(150, 20, "SessionInfo");
    }

    @Override
    public void drawShader() {

    }


    @Override
    public void onTick() {

    }

    @Override
    public void predrawhud() {

    }

    @Override
    public void drawHUD(int xPos, int yPos, float partialTicks) {
        setWidth(150);
        setHeight(60);

        float y = yPos + 6f;
        float infoWidth = xPos + getWidth() - 6f;
        RenderUtil.drawRectWH(xPos, yPos, getWidth(), 1f, cc.xylitol.module.impl.render.HUD.color(1).getRGB());

        RenderUtil.drawRectWH(xPos, yPos + 1f, getWidth(), getHeight() + 10, new Color(0, 0, 0, 100).getRGB());

        FontManager.tenacitybold.drawStringDynamic("Info", xPos + 6f, y, 1, 6);
        y += FontManager.font22.getHeight() + 1f;

        //blur
        ShaderElement.addBlurTask(() -> RenderUtil.drawRectWH(xPos, yPos + 1f, getWidth(), getHeight() + 10, new Color(0, 0, 0, 255).getRGB()));
        ShaderElement.addBloomTask(() -> RenderUtil.drawRectWH(xPos, yPos + 1f, getWidth(), getHeight() + 10, new Color(0, 0, 0, 255).getRGB()));

        //username
        FontManager.font16.drawString("User", xPos + 6f, y - 3, -1);
        FontManager.font16.drawString(User.INSTANCE.getUsername("Insane1337"), infoWidth - FontManager.font16.getStringWidth(User.INSTANCE.getUsername("Insane1337")), y - 3, -1);

        // user
        FontManager.font16.drawString("Name", xPos + 6f, y + 10, -1);
        String name = mc.thePlayer.getNameClear();
        FontManager.font16.drawString(name, infoWidth - FontManager.font16.getStringWidth(name), y + 10, -1);
        y += FontManager.font16.getHeight() + 2f;


        // sessiontime
        FontManager.font16.drawString("Elapsed", xPos + 6f, y + 10, -1);
        String timeString = sessionTime();
        FontManager.font16.drawString(timeString, infoWidth - FontManager.font16.getStringWidth(timeString) + 3, y + 10, -1);
        y += FontManager.font16.getHeight() + 2f;


        // bans
        FontManager.font16.drawString("Bans", xPos + 6f, y + 10, -1);
        String bans = String.valueOf(Client.instance.banManager.bans);
        FontManager.font16.drawString(bans, infoWidth - FontManager.font16.getStringWidth(bans), y + 10, -1);
        y += FontManager.font16.getHeight() + 2f;
    }

    public static String sessionTime() {
        int elapsedTime = ((int) System.currentTimeMillis() - startTime) / 1000;
        String days = elapsedTime > 86400 ? elapsedTime / 86400 + "d " : "";
        elapsedTime = !days.equals("") ? elapsedTime % 86400 : elapsedTime;
        String hours = elapsedTime > 3600 ? elapsedTime / 3600 + "h " : "";
        elapsedTime = !hours.equals("") ? elapsedTime % 3600 : elapsedTime;
        String minutes = elapsedTime > 60 ? elapsedTime / 60 + "m " : "";
        elapsedTime = !minutes.equals("") ? elapsedTime % 60 : elapsedTime;
        String seconds = elapsedTime > 0 ? elapsedTime + "s " : "";
        return days + hours + minutes + seconds;
    }

}
