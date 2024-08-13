package cc.xylitol.ui.gui.clickgui;

import cc.xylitol.module.Module;
import cc.xylitol.ui.font.FontManager;
import cc.xylitol.utils.TimerUtil;
import cc.xylitol.utils.render.ColorUtil;
import cc.xylitol.utils.render.HSBData;
import cc.xylitol.utils.render.RenderUtil;
import cc.xylitol.utils.render.RoundedUtil;
import cc.xylitol.utils.render.animation.AnimationUtils;
import cc.xylitol.utils.render.animation.Direction;
import cc.xylitol.utils.render.animation.impl.ContinualAnimation;
import cc.xylitol.utils.render.animation.impl.EaseBackIn;
import cc.xylitol.value.Value;
import cc.xylitol.value.impl.BoolValue;
import cc.xylitol.value.impl.ColorValue;
import cc.xylitol.value.impl.ModeValue;
import cc.xylitol.value.impl.NumberValue;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjglx.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Arrays;

import static cc.xylitol.utils.render.RenderUtil.isHovering;
import static cc.xylitol.utils.render.RenderUtil.reAlpha;
import static net.minecraft.client.gui.Gui.drawRect3;

/**
 * @author xiatian233 && paimonqwq
 */
public class ModuleRender {
    public final Module m;
    final ContinualAnimation settinganimation = new ContinualAnimation();
    private final EaseBackIn hoveranimation = new EaseBackIn(200, 0.3F, 2.0F);
    public int index;
    int color;
    TimerUtil valuetimer = new TimerUtil();
    boolean cansetvalue = false;
    private int height;
    private boolean hover;
    private boolean canbind;
    private int targetAlpha = 0;
    private int nowAlpha = 0;
    private boolean openedcp;

    public ModuleRender(Module module) {
        this.m = module;
    }

    public static double round(final double value, final double inc) {
        if (inc == 0.0) return value;
        else if (inc == 1.0) return Math.round(value);
        else {
            final double halfOfInc = inc / 2.0;
            final double floored = Math.floor(value / inc) * inc;

            if (value >= floored + halfOfInc)
                return new BigDecimal(Math.ceil(value / inc) * inc)
                        .doubleValue();
            else return new BigDecimal(floored)
                    .doubleValue();
        }
    }

    public void draw(int mouseX, int mouseY, int x, int y, int moduley) {
        height = 20;
        color = m.getState() ? Color.PINK.getRGB() : -1;
        hover = isHovering(x + 5, y + height / 2 - FontManager.font18.getHeight() / 2 + 20 + moduley, FontManager.font18.getStringWidth(m.name), FontManager.font18.getHeight(), mouseX, mouseY);
        if (hover) {
            hoveranimation.setDirection(Direction.FORWARDS);
            canbind = true;
            targetAlpha = 255;
        } else {
            hoveranimation.setDirection(Direction.BACKWARDS);
            canbind = false;
            targetAlpha = 100;
        }
        if (cansetvalue) targetAlpha = 255;
        /*RenderUtil.scaleStart((float) ((x + 5 + FontManager.font18.getStringWidth(m.name) / 2) * (1 - hoveranimation.getOutput())), y + height / 2 - FontManager.font18.getHeight() / 2 + 20 + moduley + FontManager.font18.getHeight() / 2, 1F*//* + (float) hoveranimation.getOutput()*//*);
         */
        FontManager.font18.drawString(m.name, (float) (x + (5 * (1 + hoveranimation.getOutput()))), y + height / 2 - FontManager.font18.getHeight() / 2 + 20 + moduley, color);
        /*        RenderUtil.scaleEnd();*/
        //设置value

        nowAlpha = (int) AnimationUtils.animate(targetAlpha, nowAlpha, 0.1);
        RoundedUtil.drawRound(x + 90, y + height / 2 - FontManager.font18.getHeight() / 2 + 20 + moduley, 5, 5, 4f, new Color(255, 255, 255, nowAlpha));

        if (cansetvalue) {
            index = 0;
            int numberindex = 0;
            int addition = 0;
            RoundedUtil.drawRound(x, y + height / 2 - FontManager.font18.getHeight() / 2 + 20 + moduley + FontManager.font18.getHeight(), 100, index, 0f, new Color(0, 0, 0, 100));
            for (Value value : m.getValues()) {
                if (value instanceof NumberValue) {
                    DecimalFormat df = new DecimalFormat("#.#");
                    double d = Double.parseDouble(df.format(((((Number) value.getValue()).doubleValue() - ((NumberValue) value).getMin().doubleValue()) / (((NumberValue) value).getMax().doubleValue() - ((NumberValue) value).getMin().doubleValue()))));
                    double present = 80 * d;
                    value.numberAnim.animate(present, 0.1f);

                    //进度条
                    FontManager.font14.drawString(value.getName() + " " + value.getValue(), x + 10, y + height / 2 - FontManager.font14.getHeight() / 2 + 20 + moduley + FontManager.font14.getHeight() + 8 + numberindex * 15 + addition - FontManager.font14.getHeight() + 8, -1);
                    RoundedUtil.drawRound(x + 10, y + height / 2 - FontManager.font18.getHeight() / 2 + 20 + moduley + FontManager.font18.getHeight() + 8 + numberindex * 15 + addition + 10, 80, 3F, 1F, new Color(0, 0, 0, 130));
                    RoundedUtil.drawRound(x + 10, y + height / 2 - FontManager.font18.getHeight() / 2 + 20 + moduley + FontManager.font18.getHeight() + 8 + numberindex * 15 + addition + 10, (int) value.numberAnim.getOutput(), 3F, 1F, Color.PINK);
                    if (isHovering(x + 10, y + height / 2 - FontManager.font18.getHeight() / 2 + 20 + moduley + FontManager.font18.getHeight() + 8 + numberindex * 15 + addition + 5, 80, 9F, mouseX, mouseY) && Mouse.isButtonDown(0)) {

                        double num = Math.max(((NumberValue) value).getMin(), Math.min(((NumberValue) value).getMax(), round((mouseX - (x + 10)) * (((NumberValue) value).getMax() - ((NumberValue) value).getMin()) / 80 + ((NumberValue) value).getMin(), ((NumberValue) value).getInc())));
                        num = (double) Math.round(num * (1.0D / ((NumberValue) value).getInc())) / (1.0D / ((NumberValue) value).getInc());

                        value.setValue(num);
                    }
                    numberindex++;
                    addition += 10;
                }
                index += 16;
                if (value instanceof BoolValue) {
                    boolean hover = isHovering(x + 5, y + height / 2 - FontManager.font14.getHeight() / 2 + 20 + moduley + FontManager.font14.getHeight() + 8 + numberindex * 15 + addition - FontManager.font14.getHeight() + 8, 100, 5F, mouseX, mouseY);
                    FontManager.font14.drawString(value.getName(), x + 10, y + height / 2 - FontManager.font14.getHeight() / 2 + 20 + moduley + FontManager.font14.getHeight() + 8 + numberindex * 15 + addition - FontManager.font14.getHeight() + 8, -1);
                    if (hover && Mouse.isButtonDown(0)) {
                        if (valuetimer.hasTimeElapsed(300)) {
                            value.setValue(!(Boolean) value.getValue());
                            valuetimer.reset();
                        }
                    }
                    if (hover) {
                        value.easeBackIn.setDirection(Direction.FORWARDS);
                    } else {
                        value.easeBackIn.setDirection(Direction.BACKWARDS);
                    }
                    RoundedUtil.drawRound((float) (x + FontManager.font14.getStringWidth(value.getName()) + ((15) * (1 + value.easeBackIn.getOutput()))), y + height / 2 - FontManager.font14.getHeight() / 2 + 20 + moduley + FontManager.font14.getHeight() + 8 + numberindex * 15 + addition - FontManager.font14.getHeight() + 8, 5, 5F, 1F, new Color(255, 170, 178, 130));
                    if ((Boolean) value.getValue()) {
                        value.decelerateAnimation.setDirection(Direction.FORWARDS);
                    } else {
                        value.decelerateAnimation.setDirection(Direction.BACKWARDS);
                    }
                    if (((Boolean) value.getValue()).booleanValue())
                        FontManager.other14.drawString("v", (float) (x + FontManager.font14.getStringWidth(value.getName()) + ((14) * (1 + value.easeBackIn.getOutput()))), y + height / 2 - FontManager.other14.getHeight() / 2 + 20 + moduley + FontManager.other14.getHeight() + 8 + numberindex * 15 + addition - FontManager.other14.getHeight() + 8, ColorUtil.applyOpacity(new Color(-1), (float) value.decelerateAnimation.getOutput()).getRGB());

                    numberindex++;
                }

                if (value instanceof ModeValue) {
                    ModeValue modeValue = (ModeValue) value;
                    FontManager.font14.drawString(value.getName(), x + 10, y + height / 2 - FontManager.font14.getHeight() / 2 + 24 + moduley + FontManager.font14.getHeight() + 8 + numberindex * 15 + addition - FontManager.font14.getHeight(), -1);
                    RoundedUtil.drawRound(x + 10, y + height / 2 - FontManager.font14.getHeight() / 2 + 20 + moduley + FontManager.font14.getHeight() + 14 + numberindex * 15 + addition, 80, 8F, 3F, new Color(255, 170, 178, 130));
                    GlStateManager.resetColor();
                    if (isHovering(x + 10, y + height / 2 - FontManager.font14.getHeight() / 2 + 20 + moduley + FontManager.font14.getHeight() + 14 + numberindex * 15 + addition, 80, 8F, mouseX, mouseY) && Mouse.isButtonDown(0) && valuetimer.hasTimeElapsed(200)) {
                        if (Arrays.asList(modeValue.getModes()).indexOf(modeValue.getValue()) < modeValue.getModes().length - 1) {
                            value.setValue(modeValue.getModes()[Arrays.asList(modeValue.getModes()).indexOf(value.getValue()) + 1]);
                        } else {
                            value.setValue(modeValue.getModes()[0]);
                        }
                        valuetimer.reset();
                    }
                    FontManager.font14.drawString(((ModeValue) value).getModeAsString(), x + 10 + 40 - FontManager.font16.getStringWidth(((ModeValue) value).getModeAsString()) / 2, y + height / 2 - FontManager.font18.getHeight() / 2 + 20 + moduley + FontManager.font18.getHeight() + 12 + numberindex * 15 + addition + 6F - FontManager.font16.getHeight() / 2 + 2f, -1);
                    numberindex++;
                    addition += 6;
                }
                if (value instanceof ColorValue) {
                    boolean hover = isHovering(x + 10, y + height / 2 - FontManager.font14.getHeight() / 2 + 20 + moduley + FontManager.font14.getHeight() + 14 + numberindex * 15 + addition, 11, 11, mouseX, mouseY);

                    ColorValue setting = (ColorValue) value;
                    final Color valColor = setting.getColorC();

                    HSBData data = new HSBData(valColor);

                    final float[] hsba = {
                            data.getHue(),
                            data.getSaturation(),
                            data.getBrightness(),
                            setting.getColorC().getAlpha(),
                    };
                    RoundedUtil.drawRound(x + 10, y + height / 2 - FontManager.font14.getHeight() / 2 + 20 + moduley + FontManager.font14.getHeight() + 14 + numberindex * 15 + addition, 11, 11, 3f, setting.getColorC());
                    FontManager.font14.drawString(setting.getName(), x + 10, y + height / 2 - FontManager.font14.getHeight() / 2 + 24 + moduley + FontManager.font14.getHeight() + 8 + numberindex * 15 + addition - FontManager.font14.getHeight(), 0xffffffff, false);
                    if (hover && Mouse.isButtonDown(0)) {
                        if (valuetimer.hasTimeElapsed(300)) {

                            openedcp = !openedcp;
                            valuetimer.reset();
                        }
                    }
                    if (openedcp) {
                        GlStateManager.pushMatrix();
                        GlStateManager.translate(6, height / 2 - FontManager.font14.getHeight() / 2 + 20 + moduley + FontManager.font14.getHeight() + 14 + numberindex * 15 + addition, 6);

                        drawRect3(x + 10 + 3, y + 8.5f + 3, 61, 61, new Color(0, 0, 0).getRGB());
                        drawRect3(x + 10, y + 8.5f + 3.5, 60, 60, getColor(Color.getHSBColor(hsba[0], 1, 1)).getRGB());
                        RenderUtil.drawHorizontalGradientSideways(x + 10, y + 8.5f + 3.5, 60, 60, getColor(Color.getHSBColor(hsba[0], 0, 1)).getRGB(), 0x00F);
                        RenderUtil.drawVerticalGradientSideways(x + 10, y + 8.5f + 3.5, 60, 60, 0x00F, getColor(Color.getHSBColor(hsba[0], 1, 0)).getRGB());

                        drawRect3(x + 10 + hsba[1] * 60 - .5, y + 8.5f + 3.5 + ((1 - hsba[2]) * 60) - .5, 1.5, 1.5, new Color(0, 0, 0).getRGB());
                        drawRect3(x + 10 + hsba[1] * 60, y + 8.5f + 3.5 + ((1 - hsba[2]) * 60), .5, .5, getColor(valColor).getRGB());

                        final boolean onSB = isHovering(x + 13, y + 8.5f + 3, 61, 61, mouseX, mouseY - (height / 2 - FontManager.font14.getHeight() / 2 + 20 + moduley + FontManager.font14.getHeight() + 14 + numberindex * 15 + addition));

                        if (onSB && Mouse.isButtonDown(0)) {
                            data.setSaturation(Math.min(Math.max((mouseX - (x + 13) - 3) / 60F, 0), 1));
                            data.setBrightness(1 - Math.min(Math.max((mouseY - (height / 2 - FontManager.font14.getHeight() / 2 + 20 + moduley + FontManager.font14.getHeight() + 14 + numberindex * 15 + addition) - y + 8.5f - 3 - 16) / 60F, 0), 1));
                            setting.setColor(data.getAsColor().getRGB());

                        }

                        drawRect3(x + 10 + 67, y + 8.5f + 3, 10, 61, new Color(0, 0, 0).getRGB());

                        for (float f = 0F; f < 5F; f += 1F) {
                            final Color lasCol = Color.getHSBColor(f / 5F, 1F, 1F);
                            final Color tarCol = Color.getHSBColor(Math.min(f + 1F, 5F) / 5F, 1F, 1F);
                            RenderUtil.drawVerticalGradientSideways(x + 10 + 67.5, y + 8.5f + 3.5 + f * 12, 9, 12, getColor(lasCol).getRGB(), getColor(tarCol).getRGB());
                        }

                        drawRect3(x + 10 + 67.5, y + 8.5f + 2 + hsba[0] * 60, 9, 2, new Color(0, 0, 0).getRGB());
                        drawRect3(x + 10 + 67.5, y + 8.5f + 2.5 + hsba[0] * 60, 9, 1, new Color(204, 198, 255).getRGB());

                        final boolean onHue = RenderUtil.isHovering(x + 10 + 67, y + 8.5f + 3, 10, 61, mouseX - 6, mouseY - (height / 2 - FontManager.font14.getHeight() / 2 + 20 + moduley + FontManager.font14.getHeight() + 14 + numberindex * 15 + addition));

                        if (onHue && Mouse.isButtonDown(0)) {
                            data.setHue(Math.min(Math.max((mouseY - (height / 2 - FontManager.font14.getHeight() / 2 + 20 + moduley + FontManager.font14.getHeight() + 14 + numberindex * 15 + addition) - y + 8.5f - 3 - 16) / 60F, 0), 1));
                            setting.setColor(data.getAsColor().getRGB());
                        }
                        GlStateManager.popMatrix();

                    }
                    numberindex++;
                    addition += 12;
                }
            }
            index += addition;
        } else {
            for (Value<?> value : m.getValues()) {
                if (value instanceof NumberValue) {
                    value.numberAnim.setNow(0);
                }
            }
        }
    }

    private Color getColor(Color color) {
        return reAlpha(color, (int) (1f * color.getAlpha()));
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        //按下并且鼠标为左键
        if (hover && mouseButton == 0) {
            m.toggle();
        }
        if (hover && mouseButton == 1 && m.getValues().size() > 0) {
            cansetvalue = !cansetvalue;
        }

    }

    //设置按键
    public void keyTyped(int keyCode) {

    }
}
