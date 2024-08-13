package cc.xylitol.ui.hud.impl;

import cc.xylitol.Client;
import cc.xylitol.ui.font.FontManager;
import cc.xylitol.ui.hud.HUD;
import cc.xylitol.utils.render.ColorUtil;
import cc.xylitol.utils.render.RenderUtil;
import cc.xylitol.utils.render.shader.ShaderElement;
import net.minecraft.client.Minecraft;

import java.awt.*;

import static cc.xylitol.module.impl.render.HUD.*;

public class Watermark extends HUD {
    public Watermark() {
        super(50, 20, "Watermark");
    }

    @Override
    public void drawShader() {

    }


    @Override
    public void onTick() {
        String clientName = markTextValue.get();

        if (charIndex > clientName.length()) {
            if (clientName.isEmpty()) {
                charIndex = 0;
            } else {
                charIndex = clientName.length() - 1;
            }
        }

        if (clientName.isEmpty()) {
            return;
        }

        updateTick++;

        if (updateTick > 5) {
//            backward = charIndex > (clientName.length() - 1) && charIndex > 0;
            if (charIndex > clientName.length() - 1) {
                backward = true;
            } else if (charIndex <= 0) {
                backward = false;
            }

//            charIndex = MathHelper.clamp_int(charIndex, 0, clientName.length() - 1);

            if (backward) {
                charIndex--;
            } else {
                charIndex++;
            }

            markStr = clientName.substring(0, charIndex);

            updateTick = 0;
        }
    }

    @Override
    public void predrawhud() {

//        if (updateTick == 10) {
//            if (charIndex > clientName.length() - 1 && !backward) {
//                backward = true;
//                charIndex = clientName.length() - 1;
//            } else if (charIndex < 0 && backward) {
//                backward = false;
//                charIndex = 0;
//            }
//
//            if (backward && charIndex > 0) {
//                markStrBuilder.deleteCharAt(charIndex);
//                charIndex--;
//            } else if (charIndex <= clientName.length() - 1) {
//                markStrBuilder.append(clientName.charAt(charIndex));
//                charIndex++;
//            }
//
//            updateTick = 0;
//        }
    }

    int updateTick;
    int charIndex;
    boolean backward;
    String markStr;

    @Override
    public void drawHUD(int xPos, int yPos, float partialTicks) {
        String clientName = Client.name;
        String mark = markStr;

        String title = String.format(" |  %s  |  fps:%s  |  %s", Client.instance.user, Minecraft.getDebugFPS(), Client.version);
        float width = FontManager.font18.getStringWidth(title) + FontManager.font18.getStringWidth(clientName) + 8 + 3;
        RenderUtil.drawRectWH(4, 4, width + 6, 1, color(1).getRGB());
        RenderUtil.drawRect(4, 5, width + 11, FontManager.font20.getHeight() + 8, new Color(0, 0, 0, 100).getRGB());

        //blur
        ShaderElement.addBlurTask(() -> RenderUtil.drawRect(4, 5, width + 11, FontManager.font18.getHeight() + 9, -1));
        ShaderElement.addBloomTask(() -> RenderUtil.drawRect(4, 5, width + 11, FontManager.font18.getHeight() + 9, new Color(0, 0, 0, 255).getRGB()));

        FontManager.font20.drawStringDynamic(clientName, 9, 9 + 1, 1, 6);
        FontManager.font18.drawString(title, 9 + FontManager.font20.getStringWidth(clientName) + 2, 9.5f + 1, -1);
    }

}
