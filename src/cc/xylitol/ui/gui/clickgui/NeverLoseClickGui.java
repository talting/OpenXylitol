package cc.xylitol.ui.gui.clickgui;

import cc.xylitol.Client;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import cc.xylitol.module.impl.render.HUD;
import cc.xylitol.ui.font.FontManager;
import cc.xylitol.ui.font.RapeMasterFontManager;
import cc.xylitol.ui.hud.notification.NotificationManager;
import cc.xylitol.ui.hud.notification.NotificationType;
import cc.xylitol.utils.render.*;
import cc.xylitol.utils.render.animation.AnimationUtils;
import cc.xylitol.utils.render.shader.ShaderElement;
import cc.xylitol.value.Value;
import cc.xylitol.value.impl.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL11;
import org.lwjglx.input.Keyboard;
import org.lwjglx.input.Mouse;
import org.lwjglx.opengl.Display;
import top.fl0wowp4rty.phantomshield.api.User;

import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class NeverLoseClickGui extends GuiScreen {
    public static NeverLoseClickGui INSTANCE = new NeverLoseClickGui();
    private short[] date;
    public float x = 4, y = 20;
    public float width = 520;
    public final float height = 420;
    public float search = 300;
    public float visibleAnimation;
    private boolean quitting = false;

    private boolean dragging;
    private float dragX, dragY;
    public int wheel = Mouse.hasWheel() ? Mouse.getDWheel() * 2 : 0;

    private List<Module> leftModules = new CopyOnWriteArrayList<>();
    private List<Module> rightModules = new CopyOnWriteArrayList<>();

    private List[] lists = new List[]{};

    public NumberFormat nf = new DecimalFormat("0000");
    private Category.Pages current = Category.Pages.COMBAT;
    private NumberValue currentSliding = null;

    private Value<?> dropdownItem;
    private TextValue currentEditing;
    private cc.xylitol.utils.render.Rectangle protectArea;

    private final InputField searchTextField = new InputField(FontManager.font16);
    private boolean searching = false;

    private final float[] moduleWheel = {0f, 0f};

    private float alphaAnimate = 10;

    private String tooltip = null;
    private float offsetY = 0;
    private boolean mouseDown = false;

    private final ArrayList<ItemStack> itemStacks = new ArrayList<>();

    public NeverLoseClickGui() {
        INSTANCE = this;
        dropdownItem = null;
        protectArea = null;

        init();
    }

    public void init() {
        font = FontManager.museo18;
        for (Item item : Item.itemRegistry) {
            itemStacks.add(new ItemStack(item));
        }
    }

    private float scrollAni;
    private float CscrollAni;

    private static RapeMasterFontManager font;


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        tooltip = null;


        visibleAnimation = AnimationUtils.animateSmooth(visibleAnimation, quitting ? 0 : 100, .2F);
        if (quitting) {
            currentEditing = null;
            if (Display.isActive() && !mc.inGameHasFocus) {
                mc.inGameHasFocus = true;
                mc.mouseHelper.grabMouseCursor();
            }

            if (Math.round(visibleAnimation) <= 2) mc.displayGuiScreen(null);
        }

        if (!Mouse.isButtonDown(0) && dragging) dragging = false;
        if (dragging) {
            x = mouseX - dragX;
            y = mouseY - dragY;
        }
        if (!quitting) {
            ShaderElement.addBlurTask(() -> {
                RoundedUtil.drawRound(x, y, width, height, 6, true, new Color(255, 255, 255));
                /*if (current == Category.Pages.ENTITY) {
                    RoundedUtil.drawRound(x + width + 8, y + (height / 2) - (height * 0.7f) / 2, 200, height * 0.7f, 6, true, getColor(217, 217, 217));
                }*/
            });
            RoundedUtil.drawRound(x, y, width, height, 6, false, new Color(255, 255, 255, 200));

        }
        float width = Math.max(FontManager.bold38.getStringWidth(Client.name.toUpperCase()), FontManager.bold38.getStringWidth("NOVOLINE"));
        if (width > FontManager.bold38.getStringWidth("NOVOLINE")) {
            this.width = AnimationUtils.animateSmooth(this.width, 520 + width - FontManager.bold38.getStringWidth("NOVOLINE"), 0.5f);
        } else {
            this.width = AnimationUtils.animateSmooth(this.width, 520, 0.5f);
        }
        FontManager.bold38.drawCenteredString(Client.name.toUpperCase(), (float) (x + 7.7 + width / 2), y + 12, getColor(new Color(51, 51, 51)).getRGB());
        float pageY = 44;
        MaskUtil.defineMask();
        RenderUtil.drawRectWH(x, y + pageY - 4, width + 10, 344, -1);
        MaskUtil.finishDefineMask();
        MaskUtil.drawOnMask();
        float sb = 0;
        for (Category pageManager : Category.values()) {
            sb += 12;
            for (Category.Pages ignored : pageManager.getSubPages()) {
                sb += 26;
            }
            sb += 4;
        }
        CscrollY = Math.max(CscrollY, -sb + 344);
        if (RenderUtil.isHovering(x, y + pageY, width + 10, 340, mouseX, mouseY)) {
            CscrollAni = AnimationUtils.animateSmooth(CscrollAni, CscrollY, 0.3f);
        } else {
            CscrollY = CscrollAni;
        }
        pageY += CscrollAni;

        pageY += 12;
        for (Category.Pages cate : Category.Pages.values()) {
            String head = StringUtils.left(cate.name(), 1);
            String display = cate.name().substring(1);

            if (cate == current) {
                RoundedUtil.drawRound(x + 8, y + pageY, width, 16, 4, true, getColor(221, 220, 220));
            }

            float finalPageY = pageY;
            cate.animation.draw(() -> RoundedUtil.drawRound(x + 8, y + finalPageY, width, 16, 4, true, getColor(7, 50, 74, 230)));
            try {
                RenderUtil.drawImage(new ResourceLocation("xylitol/images/" + cate.name().toLowerCase() + ".png"), x + 10, y + pageY + 2, 12, 12, getColor(3, 168, 245).getRGB());
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
            String text = head + display.toLowerCase();
            text = text.replaceAll("_", " ");
            if (cate.gradient) {
                float finalPageY1 = pageY;
                String finalText = text;
                GlStateManager.pushMatrix();
                GradientUtil.applyGradientHorizontal(x + 26, y + finalPageY1 + 4.5f, font.getStringWidth(text), font.getHeight(), Math.round((visibleAnimation / 100F)), getColor(HUD.color(1)), getColor(HUD.color(89)), () -> {
                    GlStateManager.enableAlpha();
                    GlStateManager.alphaFunc(GL11.GL_GREATER, 0);
                    font.drawString(finalText, x + 26, y + finalPageY1 + 4.5f, getColor(255, 255, 255).getRGB());

                });
                GlStateManager.popMatrix();

            } else {
                font.drawString(text, x + 26, y + pageY + 5f, getColor(38, 38, 38).getRGB());

            }
            pageY += 26;
        }


        MaskUtil.resetMask();


        StencilUtil.initStencilToWrite();
        RoundedUtil.drawRound(x, y, this.width, height, 6, true, getColor(255, 255, 255));
        StencilUtil.readStencilBuffer(1);
        ShaderElement.addBlurTask(()->RenderUtil.drawRectWH(x + width + 8 + 8, y, this.width - width-16, height, getColor(255,255,255).getRGB()));
        RenderUtil.drawRectWH(x + width + 8 + 8, y, this.width - width, height, getColor(240, 239, 238,200).getRGB());
        Gui.drawRect(x + width + 8 + 8, y + 40, this.x + this.width, y + 41, getColor(217, 217, 217).getRGB());
        StencilUtil.endStencilBuffer();
        if (current == Category.Pages.CONFIGS) {

            boolean hovered = RenderUtil.isHovering(x + width + 16 + 4, y + 10, 89, 20, mouseX, mouseY);
            RoundedUtil.drawRoundOutline(x + width + 16 + 4, y + 10, 89, 20, 4, 0.1f, getColor(217, 217, 217), hovered ? getColor(new Color(0xFF00A7F2)) : getColor(217, 217, 217));
            font.drawCenteredString("Create Config", x + width + 16 + 4 + (89 / 2f), y + 17, getColor(0, 0, 0).getRGB());
            boolean hovered2 = RenderUtil.isHovering(x + width + 16 + 96, y + 10, 89, 20, mouseX, mouseY);
            RoundedUtil.drawRoundOutline(x + width + 16 + 96, y + 10, 89, 20, 4, 0.1f, getColor(217, 217, 217), hovered2 ? getColor(new Color(0xFF00A7F2)) : getColor(217, 217, 217));
            font.drawCenteredString("Open directory", x + width + 16 + 96 + (89 / 2f), y + 17, getColor(0, 0, 0).getRGB());
        } else {
            RoundedUtil.drawRoundOutline(x + width + 16 + 10, y + 10, search, 20, 4, 0.1f, getColor(242, 241, 240), getColor(217, 217, 217));

            if (!searching)
                RenderUtil.drawImage(new ResourceLocation("xylitol/images/search.png"), x + width + 16 + 10 + 3, y + 13, 14, 14, getColor(150, 150, 150, 100).getRGB());

            searchTextField.setBackgroundText("Press CTRL+F to search with in gui..");
            searchTextField.setDrawingLine(false);
            searchTextField.setxPosition(x + width + 16 + 10 + 3 + (searching ? 0 : 16));
            searchTextField.setyPosition(y + 13);
            searchTextField.setWidth(search);
            searchTextField.setHeight(20);
            searchTextField.setDrawingBackground(false);
            searchTextField.drawTextBox(mouseX, mouseY);
        }

        StencilUtil.initStencilToWrite();
        RenderUtil.drawRectWH(x, y + 45, this.width, height - 46, getColor(201, 201, 201).getRGB());

        StencilUtil.readStencilBuffer(1);
        wheel = Mouse.hasWheel() ? Mouse.getDWheel() * 12 : 0;

        if (current.module) {
            float left = render(leftModules, x + width + 24, mouseX, mouseY);
            float right = render(rightModules, x + width + 24 + 198, mouseX, mouseY);

            final float[] nextWheel = RenderUtil.getNextWheelPosition(wheel, moduleWheel, y + 10, y + 290, Math.max(left, right), 0, RenderUtil.isHovering(x + 120, y + 40, this.width - 120, this.height - 40, mouseX, mouseY));
            moduleWheel[0] = nextWheel[0];
            moduleWheel[1] = Math.max(left, right) > this.height ? Math.max(nextWheel[1], -Math.max(left, right) + this.height - 60) : nextWheel[1];

        } else {

            if (current == Category.Pages.CONFIGS) {
                Client styles = Client.instance;
                float configY = y + 40 + 8 + 14 + scrollAni;
                FontManager.font16.drawString("- My Items", x + width + 16 + 6, configY - 14, getColor(77, 77, 77).getRGB());

                float naotan = styles.getConfigManager().getConfigs().size() * 42;

                if (naotan > this.height - 60) {
                    scrollY = Math.max(scrollY, -naotan + this.height - 60);
                    if (RenderUtil.isHovering(x + width + 16, 0, 370, 420, mouseX, mouseY)) {
                        scrollAni = AnimationUtils.animateSmooth(scrollAni, scrollY*2, 0.2f);
                    } else {
                        scrollY = scrollAni;
                    }
                }

                for (String config : styles.getConfigManager().getConfigs()) {
                    RoundedUtil.drawRoundOutline(x + width + 16 + 6, configY, 370, 36, 4, 0.1f, getColor(230, 230, 230), getColor(217, 217, 217));
                    FontManager.font18.drawString(config, x + width + 16 + 12, configY + 6, getColor(0, 0, 0).getRGB());
                    FontManager.font18.drawString(EnumChatFormatting.GRAY + "Modified: " + EnumChatFormatting.RESET + "2077/7/7" + " " + EnumChatFormatting.GRAY + "Author: " + EnumChatFormatting.RESET + "null", x + width + 16 + 12, configY + 8 + 12, getColor(3, 146, 214).getRGB());
                    boolean fuck = RenderUtil.isHovering(x + width + 16 + 6 + 290 - 6, configY + 8, 76, 20, mouseX, mouseY);
                    if ("modules.json".equals(config)) {
                        RoundedUtil.drawRoundOutline(x + width + 16 + 6 + 290 - 6, configY + 8, 76, 20, 4, 0.1f, getColor(217, 217, 217), fuck ? getColor(new Color(0xFF00A7F2)) : getColor(217, 217, 217));
                        RenderUtil.drawImage(new ResourceLocation("xylitol/images/save.png"), x + width + 16 + 6 + 290 + 10, configY + 8 + 2, 16, 16, getColor(201, 201, 201).getRGB());
                        FontManager.font16.drawString("Save", x + width + 16 + 6 + 290 + 30, configY + 8 + 8, getColor(201, 201, 201).getRGB());
                    } else {
                        RoundedUtil.drawRound(x + width + 16 + 6 + 290 - 6, configY + 8, 76, 20, 4, false, fuck ? getColor(new Color(0xFF00A7F2)) : getColor(new Color(0x1D82AF)));
                        RenderUtil.drawImage(new ResourceLocation("xylitol/images/read.png"), x + width + 16 + 6 + 290 + 10, configY + 8 + 2, 16, 16, fuck ? -1 : getColor(201, 201, 201).getRGB());
                        FontManager.font16.drawString("Load", x + width + 16 + 6 + 290 + 30, configY + 8 + 8, fuck ? -1 : getColor(201, 201, 201).getRGB());
                    }
                    configY += 42;
                }
            }
        }


        StencilUtil.endStencilBuffer();

        RenderUtil.drawRectWH(x, y + height - 30, width + 16, 0.5f, getColor(217, 217, 217).getRGB());
        RenderUtil.drawCircleCGUI(x + 16, y + height - 15, 26, getColor(20, 20, 20).getRGB());


        GlStateManager.disableTexture2D();
        GlStateManager.color(1f, 1f, 1f);

        GlStateManager.enableTexture2D();

        GlStateManager.disableTexture2D();
        GlStateManager.color(1f, 1f, 1f);
        RenderUtil.drawCircleCGUI(x + 16, y + height - 15, 26, 0xFFFFFFFF);


        GlStateManager.disableTexture2D();
        font.drawString(Client.instance.user, x + 34, y + height - 30 + 6, getColor(0, 0, 0).getRGB());
        font.drawString(EnumChatFormatting.GRAY+"Till: ", x + 34, y + height - 30 + 18, getColor(3, 168, 245).getRGB());
        font.drawString("idk", x + 34 +font.getStringWidth("Till: "), y + height - 30 + 18, getColor(3, 168, 245).getRGB());

        alphaAnimate = AnimationUtils.animateSmooth(alphaAnimate, 180, 0.4f);
        /*if (alphaAnimate > 20) {

         *//*ShaderElement.addBlurTask(()->RenderUtil.drawRectWH(0, 0, new ScaledResolution(mc).getScaledWidth(), new ScaledResolution(mc).getScaledHeight(), new Color(0, 0, 0, 255).getRGB()));*//*
            RenderUtil.drawRectWH(0, 0, new ScaledResolution(mc).getScaledWidth(), new ScaledResolution(mc).getScaledHeight(), new Color(0, 0, 0, ((int) alphaAnimate)).getRGB());
        }*/

        if (dropdownItem != null && protectArea != null) {
            if (dropdownItem instanceof ModeValue) {
                final ModeValue property = (ModeValue) dropdownItem;
                property.animation = AnimationUtils.animateSmooth(property.animation, 255, 0.5f);
                RoundedUtil.drawRound(protectArea.getX(), protectArea.getY(), protectArea.getWidth(), protectArea.getHeight() + 1, 4, getColor(RenderUtil.reAlpha(new Color(240, 240, 240), (int) property.animation)));
                int buttonY = 0;
                for (String s : property.getModes()) {
                    font.drawString(s, protectArea.getX() + 3, protectArea.getY() + buttonY + 6, !property.is(s) ? getColor(RenderUtil.reAlpha(new Color(77, 77, 77), (int) property.animation)).getRGB() : getColor(RenderUtil.reAlpha(new Color(0, 0, 0), (int) property.animation)).getRGB());

                    buttonY += 14;
                }
            }
            if (dropdownItem instanceof ColorValue) {
                ColorValue cp = (ColorValue) dropdownItem;

                final Color valColor = cp.getColorC();

                HSBData hsbData = new HSBData(valColor);

                final float[] hsba = {
                        hsbData.getHue(),
                        hsbData.getSaturation(),
                        hsbData.getBrightness(),
                        hsbData.getAlpha(),
                };

                RoundedUtil.drawRoundOutline(protectArea.getX(), protectArea.getY(), protectArea.getWidth(), protectArea.getHeight() + 1, 2, 0.1F, getColor(5, 16, 26), getColor(217, 217, 217));
                RenderUtil.drawRectWH(protectArea.getX() + 3, protectArea.getY() + 3, 61, 61, getColor(0, 0, 0).getRGB());
                RenderUtil.drawRectWH(protectArea.getX() + 3.5, protectArea.getY() + 3.5, 60, 60, getColor(Color.getHSBColor(hsba[0], 1, 1)).getRGB());
                RenderUtil.drawHGradientRect(protectArea.getX() + 3.5, protectArea.getY() + 3.5, 60, 60, getColor(Color.getHSBColor(hsba[0], 0, 1)).getRGB(), 0x00F);
                RenderUtil.drawVGradientRect(protectArea.getX() + 3.5, protectArea.getY() + 3.5, 60, 60, 0x00F, getColor(Color.getHSBColor(hsba[0], 1, 0)).getRGB());

                RenderUtil.drawRectWH(protectArea.getX() + 3.5 + hsba[1] * 60 - .5, protectArea.getY() + 3.5 + ((1 - hsba[2]) * 60) - .5, 1.5, 1.5, getColor(0, 0, 0).getRGB());
                RenderUtil.drawRectWH(protectArea.getX() + 3.5 + hsba[1] * 60, protectArea.getY() + 3.5 + ((1 - hsba[2]) * 60), .5, .5, getColor(valColor).getRGB());

                final boolean onSB = RenderUtil.isHovering(protectArea.getX() + 3, protectArea.getY() + 3, 61, 61, mouseX, mouseY);

                if (onSB && Mouse.isButtonDown(0)) {
                    hsbData.setSaturation(Math.min(Math.max((mouseX - protectArea.getX() - 3) / 60F, 0), 1));
                    hsbData.setBrightness(1 - Math.min(Math.max((mouseY - protectArea.getY() - 3) / 60F, 0), 1));
                    cp.setColor(hsbData.getAsColor().getRGB());

                }

                RenderUtil.drawRectWH(protectArea.getX() + 67, protectArea.getY() + 3, 10, 61, getColor(0, 0, 0).getRGB());

                for (float f = 0F; f < 5F; f += 1F) {
                    final Color lasCol = Color.getHSBColor(f / 5F, 1F, 1F);
                    final Color tarCol = Color.getHSBColor(Math.min(f + 1F, 5F) / 5F, 1F, 1F);
                    RenderUtil.drawVGradientRect(protectArea.getX() + 67.5, protectArea.getY() + 3.5 + f * 12, 9, 12, getColor(lasCol).getRGB(), getColor(tarCol).getRGB());
                }

                RenderUtil.drawRectWH(protectArea.getX() + 67.5, protectArea.getY() + 2 + hsba[0] * 60, 9, 2, getColor(0, 0, 0).getRGB());
                RenderUtil.drawRectWH(protectArea.getX() + 67.5, protectArea.getY() + 2.5 + hsba[0] * 60, 9, 1, getColor(204, 198, 255).getRGB());

                final boolean onHue = RenderUtil.isHovering(protectArea.getX() + 67, protectArea.getY() + 3, 10, 61, mouseX, mouseY);

                if (onHue && Mouse.isButtonDown(0)) {
                    hsbData.setHue(Math.min(Math.max((mouseY - protectArea.getY() - 3) / 60F, 0), 1));
                    cp.setColor(hsbData.getAsColor().getRGB());
                    cp.setRainbowEnabled(false);
                }

                if (cp.isAlphaChangeable()) {

                    RenderUtil.drawRectWH(protectArea.getX() + 3, protectArea.getY() + 67, 61, 9, getColor(0, 0, 0).getRGB());

                    for (int xPosition = 0; xPosition < 30; xPosition++)
                        for (int yPosition = 0; yPosition < 4; yPosition++)
                            RenderUtil.drawRectWH(protectArea.getX() + 3.5 + (xPosition * 2), protectArea.getY() + 67.5 + (yPosition * 2), 2, 2, ((yPosition % 2 == 0) == (xPosition % 2 == 0)) ? getColor(255, 255, 255).getRGB() : getColor(190, 190, 190).getRGB());

                    RenderUtil.drawHGradientRect(protectArea.getX() + 3.5, protectArea.getY() + 67.5, 60, 8, 0x00F, getColor(Color.getHSBColor(hsba[0], 1, 1)).getRGB());

                    RenderUtil.drawRectWH(protectArea.getX() + 2.5 + hsba[3] * 60, protectArea.getY() + 67.5, 2, 8, getColor(0, 0, 0).getRGB());
                    RenderUtil.drawRectWH(protectArea.getX() + 3 + hsba[3] * 60, protectArea.getY() + 67.5, 1, 8, getColor(204, 198, 255).getRGB());

                    final boolean onAlpha = RenderUtil.isHovering(protectArea.getX() + 3, protectArea.getY() + 67, 61, 9, mouseX, mouseY);

                    if (onAlpha && Mouse.isButtonDown(0)) {
                        hsbData.setAlpha(Math.min(Math.max((mouseX - protectArea.getX() - 3) / 60F, 0), 1));
                    }
                }
            }
        }


        if (tooltip != null && !tooltip.isEmpty()) {

            ShaderElement.addBlurTask(() -> RoundedUtil.drawRound(mouseX + 6, mouseY + 6, font.getStringWidth(findLongestString(tooltip.split("\n"))) + 10, tooltip.split("\n").length * 14 + (tooltip.split("\n").length == 1 ? 0 : 4), 2, true, new Color(10, 19, 30, 255)));
            RoundedUtil.drawRound(mouseX + 6, mouseY + 6, font.getStringWidth(findLongestString(tooltip.split("\n"))) + 10, tooltip.split("\n").length * 14 + (tooltip.split("\n").length == 1 ? 0 : 4), 2, false, new Color(255, 255, 255, 30));

            RoundedUtil.drawRound(mouseX + 6, mouseY + 6, font.getStringWidth(findLongestString(tooltip.split("\n"))) + 10, tooltip.split("\n").length * 14 + (tooltip.split("\n").length == 1 ? 0 : 4), 2, true, new Color(10, 19, 30, 100));
            float y = 5;
            for (String s : tooltip.split("\n")) {
                font.drawString(s, mouseX + 6 + 4, mouseY + 6 + y, getColor(255, 255, 255).getRGB());
                if (tooltip.split("\n").length != 1)
                    y += 14;
            }
        }
    }

    protected boolean check(double x, double y, double x2, double y2, double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x2 && mouseY >= y && mouseY <= y2;
    }

    private boolean checkClick() {
        if (!mouseDown && Mouse.isButtonDown(0)) {
            mouseDown = true;
            return true;
        }

        return false;
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

    private float render(List<Module> modules, float offset, int mouseX, int mouseY) {
        float moduleY = 0 + moduleWheel[1];


        for (Module module : modules) {
            FontManager.font16.drawString(module.getName().toUpperCase(), offset + 4, y + 50 + moduleY, new Color(134, 134, 133).getRGB());

            int predictionHeight = 16;
            for (Value<?> property : module.getValues()) {
                if (property.isAvailable()) predictionHeight += property.getHeight();
            }
            RoundedUtil.drawRound(offset, y + 46 + FontManager.font16.getHeight() + 4 + moduleY, 190, predictionHeight, 4, false, getColor(128, 128, 128,50));

            moduleY += 10;
            font.drawString("Enabled", offset + 4, y + 50 + moduleY + 6, module.getState() ? getColor(0, 0, 0).getRGB() : getColor(77, 77, 77).getRGB());
            RoundedUtil.drawRoundOutline(offset + 162, 2f+y + 48 + moduleY + 5, 23 , 12, 6, 0.1f, module.getState() ? getColor(3, 168, 245) : getColor(230, 230, 230), getColor(220, 220, 220));

            if (RenderUtil.isHovering(offset + 168, y + 50 + moduleY + 5, 16, 8, mouseX, mouseY))
                tooltip = "";

            module.cGUIAnimation = AnimationUtils.animateSmooth(module.cGUIAnimation, module.getState() ? 10 : 0, 0.5f);
            RenderUtil.drawImage(new ResourceLocation("xylitol/images/shadow.png"),offset + 160 + module.cGUIAnimation, 2f+y + 44 + moduleY + 9,16,16);
            RenderUtil.drawCircleCGUI(offset + 168 + module.cGUIAnimation, 2f+y + 50 + moduleY + 9, 10, getColor(255, 255, 255).getRGB());

            if (module.getValues().size() > 0)
                RenderUtil.drawRectWH(offset + 4, y + 50 + moduleY + 18, 190 - 8, .5, getColor(217, 217, 217).getRGB());
            moduleY += 18;

            for (Value<?> property : module.getValues()) {
                if (!property.isAvailable()) continue;

                if (property instanceof BoolValue) {
                    final BoolValue bp = (BoolValue) property;
                    font.drawString(property.getName(), offset + 4, y + 50 + moduleY + 6 + 2f, bp.get() ? getColor(0, 0, 0).getRGB() : getColor(77, 77, 77).getRGB());
                    RoundedUtil.drawRoundOutline(offset + 162, 2f+y + 48 + moduleY + 5, 23 , 12, 6, 0.1f, bp.get() ? getColor(3, 168, 245) : getColor(230, 230, 230), getColor(220, 220, 220));
                    property.animation = AnimationUtils.animateSmooth(property.animation, bp.get() ? 10 : 0, 0.5f);
                    RenderUtil.drawImage(new ResourceLocation("xylitol/images/shadow.png"),offset + 160 + property.animation, 2f+y + 44 + moduleY + 9,16,16);
                    RenderUtil.drawCircleCGUI(offset + 168 + property.animation, 2f+y + 50 + moduleY + 9, 10, getColor(255, 255, 255).getRGB());
                }

                /*if (property instanceof LabelProperty) {
                    font.drawCenteredString(translateManager.trans("module." + module.getName().toLowerCase() + "." + property.getName().toLowerCase(), property.getName().toUpperCase()), offset + 94, y + 50 + moduleY + 6, getColor(255, 255, 255).getRGB());
                }*/

                if (property instanceof ColorValue) {
                    ColorValue cp = (ColorValue) property;
                    font.drawString(property.getName(), offset + 4, y + 50 + moduleY + 6 + 2f, getColor(77, 77, 77).getRGB());
                    RenderUtil.drawCircleCGUI(offset + 175, 2f+y + 50 + moduleY + 9, 11, getColor(new Color(cp.getColor())).getRGB());

                    if (dropdownItem == cp) {
                        protectArea = new cc.xylitol.utils.render.Rectangle(offset + 100, 2f+y + moduleY + 50 + 24, 80, cp.isAlphaChangeable() ? 80 : 67);
                    }

                }

                if (property instanceof NumberValue) {
                    DecimalFormat df = new DecimalFormat("#.#");

                    final NumberValue dp = (NumberValue) property;
                    String display = String.valueOf(dp.getValue());
                    if (display.endsWith(".0")) display = display.substring(0, display.length() - 2);
                    else if (display.startsWith("0.")) display = "." + display.substring(2);
                    else if (display.startsWith("-0.")) display = "-" + display.substring(2);
                    font.drawString(property.getName(), offset + 4, y + 50 + moduleY + 6 + 2f, dp.sliding ? getColor(0, 0, 0).getRGB() : getColor(77, 77, 77).getRGB());
                    FontManager.font14.drawCenteredString(display, offset + 190 - 13, 2f+y + 50 + moduleY + 6, getColor(145, 166, 179).getRGB());
                    RoundedUtil.drawRound(offset + 96, 2f+y + 50 + moduleY + 8, 70, 2, 2, true, getColor(255, 255, 255));
                    final double ratio = (dp.getValue() - dp.getMin()) / (dp.getMax() - dp.getMin());
                    int displayLength = (int) (ratio * 70);
                    displayLength = Math.min(displayLength, 70);
                    dp.animatedPercentage = AnimationUtils.animateSmooth(dp.animatedPercentage, displayLength, 0.2F);
                    RoundedUtil.drawRound(offset + 92, 2f+y + 50 + moduleY + 8, dp.animatedPercentage, 2, 2, true, getColor(3, 168, 245));
                    dp.animation = AnimationUtils.animateSmooth(dp.animation, dp.sliding ? 10 : 8, 0.2F);
                    RenderUtil.drawImage(new ResourceLocation("xylitol/images/shadow.png"),84 + offset + dp.animatedPercentage, 2f+y + 42 + moduleY + 9,16,16);

                    RenderUtil.drawCircleCGUI(92 + offset + dp.animatedPercentage, 2f+y + 50 + moduleY + 9, dp.animation, getColor(3, 168, 245).getRGB());

                    if (dp.sliding) {

                        double num = Math.max(dp.getMin(), Math.min(dp.getMax(), round((mouseX - (offset + 92)) * (dp.getMax() - dp.getMin()) / 70 + dp.getMin(), dp.getInc())));
                        num = (double) Math.round(num * (1.0D / dp.getInc())) / (1.0D / dp.getInc());
                        dp.setValue(num);
                    }
                }

                if (property instanceof ModeValue) {
                    final ModeValue sp = (ModeValue) property;
                    sp.height = 24f;
                    font.drawString(property.getName(), offset + 4, y + 50 + moduleY + 9 + 1f, getColor(77, 77, 77).getRGB());
                    RoundedUtil.drawRoundOutline(offset + 100, y + 50 + moduleY + 3.5F, 80, 16, 4, 0.1f, getColor(242, 242, 242), getColor(217, 217, 217));
                    MaskUtil.defineMask();
                    RoundedUtil.drawRoundOutline(offset + 100, y + 50 + moduleY + 3.5F, 80, 16, 4, 0.1f, getColor(242, 242, 242), getColor(217, 217, 217));
                    MaskUtil.finishDefineMask();
                    MaskUtil.drawOnMask();
                    try {
                        font.drawString(sp.get(), offset + 104, y + 50 + moduleY + 9, getColor(77, 77, 77).getRGB());
                    } catch (Exception e) {
                    }
                    MaskUtil.resetMask();
                    //
                    float spmaxWidth = 0;
                    for (String s : sp.getModes()) {
                        float f = font.getStringWidth(s) + 12;
                        if (f > spmaxWidth) {
                            spmaxWidth = f;
                        }
                    }
                    //
                    if (dropdownItem == sp) {
                        protectArea = new cc.xylitol.utils.render.Rectangle(offset + 100, y + moduleY + 50 + 24, spmaxWidth > 80 ? spmaxWidth : 80, 14 * sp.getModes().length);
                    }

                }
                if (property instanceof TextValue) {
                    final TextValue tp = (TextValue) property;
                    boolean isMe = currentEditing == tp;
                    font.drawString(property.getName(), offset + 4, y + 50 + moduleY + 9 + 2f, getColor(255, 255, 255).getRGB());
                    RoundedUtil.drawRoundOutline(offset + 100, y + 50 + moduleY + 3.5F, 80, 18, 2, 0.1f, getColor(230, 230, 230), isMe ? getColor(201, 201, 201).brighter() : getColor(217, 217, 217));
                    MaskUtil.defineMask();
                    RoundedUtil.drawRoundOutline(offset + 100, y + 50 + moduleY + 3.5F, 80, 18, 2, 0.1f, getColor(230, 230, 230), isMe ? getColor(201, 201, 201).brighter() : getColor(217, 217, 217));
                    MaskUtil.finishDefineMask();
                    MaskUtil.drawOnMask();
                    font.drawString(tp.get() + (isMe ? "_" : ""), offset + 104, y + 50 + moduleY + 9, getColor(77, 77, 77).getRGB());

                    RenderUtil.drawRectWH(offset + 104, y + 50 + moduleY + 9, font.getStringWidth(tp.getSelectedString()), font.getHeight(), getColor(255, 255, 255, 100).getRGB());
                    MaskUtil.resetMask();
                }

                moduleY += property.getHeight();

                final List<Value<?>> visible = new ArrayList<>(module.getValues());
                visible.removeIf(Value::isHidden);

                if (visible.indexOf(property) != visible.size() - 1) {
                    RenderUtil.drawRectWH(offset + 4, y + 50 + moduleY, 190 - 8, .5, getColor(217, 217, 217).getRGB());
                }
            }
            moduleY += 4;
        }


        return moduleY - moduleWheel[1];
    }

    public void setQuitting(boolean quitting) {
        this.quitting = quitting;
    }

    public boolean isOpened() {
        return !quitting;
    }

    public void onGuiClosed() {
        super.onGuiClosed();
        try {
            Client.instance.configManager.saveAllConfig();
        } catch (Exception e) {
            e.printStackTrace();
        }
        dropdownItem = null;
        searching = false;
        quitting = false;
    }


    private float scrollY;
    private float CscrollY;

    @Override
    public void handleMouseInput() throws IOException {
        this.scrollY += (float) Mouse.getEventDWheel();
        if (this.scrollY >= 0.0f) {
            this.scrollY = 0.0f;
        }


        this.CscrollY += (float) Mouse.getEventDWheel();
        if (this.CscrollY >= 0.0f) {
            this.CscrollY = 0.0f;
        }

        int i = Mouse.getEventX() * new ScaledResolution(mc).getScaledWidth() / this.mc.displayWidth;
        int j = new ScaledResolution(mc).getScaledHeight() - Mouse.getEventY() * new ScaledResolution(mc).getScaledHeight() / this.mc.displayHeight - 1;
        super.handleMouseInput();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {

        if (keyCode == Keyboard.KEY_RETURN && searching)
            return;

        if (keyCode == Keyboard.KEY_ESCAPE)
            mc.displayGuiScreen(null);

        if (GuiScreen.isKeyComboCtrlF(keyCode)) {
            searching = !searching;
            if (searching) {
                lists = new List[]{leftModules, rightModules};
                searchTextField.setText("");
            } else {
                leftModules = lists[0];
                rightModules = lists[1];
                resetModuleList();
            }
            return;
        }

        if (currentEditing != null) {
            try {
                if (keyCode == Keyboard.KEY_BACK && !currentEditing.get().isEmpty()) {
                    currentEditing.setValue(!currentEditing.getSelectedString().isEmpty() ? "" : currentEditing.get().substring(0, currentEditing.get().length() - 1));
                    currentEditing.setSelectedString("");
                    return;
                }

                if (GuiScreen.isKeyComboCtrlA(keyCode)) {
                    currentEditing.setSelectedString(currentEditing.get());
                    return;
                }

                if (GuiScreen.isKeyComboCtrlC(keyCode)) {
                    GuiScreen.setClipboardString(currentEditing.getSelectedString());
                    return;
                }

                if (GuiScreen.isKeyComboCtrlV(keyCode)) {
                    if (currentEditing.getSelectedString().isEmpty() && (currentEditing.get() + GuiScreen.getClipboardString()).length() > 22) {
                        currentEditing.setSelectedString("");
                        return;
                    }
                    currentEditing.setValue(!currentEditing.getSelectedString().isEmpty() ? GuiScreen.getClipboardString() : currentEditing.get() + GuiScreen.getClipboardString());
                    currentEditing.setSelectedString("");
                    return;
                }

                if (GuiScreen.isCtrlKeyDown()) return;
                if (keyCode == Keyboard.KEY_ESCAPE) {
                    currentEditing.setSelectedString("");
                    currentEditing = null;
                    return;
                }
                if (currentEditing.get().length() > 22) return;

                currentEditing.setValue(!currentEditing.getSelectedString().isEmpty() ? ChatAllowedCharacters.filterAllowedCharacters(String.valueOf(typedChar)) : currentEditing.get() + ChatAllowedCharacters.filterAllowedCharacters(String.valueOf(typedChar)));
                currentEditing.setSelectedString("");
                return;
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        if (searching) {
            searchTextField.setFocused(true);
            searchTextField.keyTyped(typedChar, keyCode);
            resetModuleList();
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }


    private Color getColor(int r, int g, int b) {
        return RenderUtil.reAlpha(new Color(r, g, b), Math.round((visibleAnimation / 100F) * 255F));
    }

    private Color getColor(int r, int g, int b, int a) {
        return RenderUtil.reAlpha(new Color(r, g, b), Math.round((visibleAnimation / 100F) * a));
    }

    private Color getColor(Color color) {
        return RenderUtil.reAlpha(color, Math.round((visibleAnimation / 100F) * color.getAlpha()));
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        if (currentSliding != null) {
            currentSliding.sliding = false;
            currentSliding = null;
        }
    }

    public String findLongestString(String[] strArray) {
        String longestString = "";
        for (String str : strArray) {
            if (font.getStringWidth(str) > font.getStringWidth(longestString)) {
                longestString = str;
            }
        }
        return longestString;
    }

    @Override
    public void initGui() {
        date = User.INSTANCE.getExpiredDate();
        super.initGui();
        resetModuleList();
    }

    private void click(List<Module> modules, float offset, int mouseX, int mouseY, int mouseButton) {
        float moduleY = 0 + moduleWheel[1];


        for (Module module : modules) {

            moduleY += 10;

            if (RenderUtil.isHovering(offset + 4, y + 50 + moduleY + 4, 184, 12, mouseX, mouseY)) {
                if (mouseButton == 0) {
                    module.toggle();
                }
                return;
            }

            moduleY += 18;

            for (Value<?> property : module.getValues()) {
                if (property.isHidden())
                    continue;

                if (property instanceof BoolValue) {
                    final BoolValue bp = (BoolValue) property;
                    if (RenderUtil.isHovering(offset + 4, y + 50 + moduleY + 5, 184, 4f+8, mouseX, mouseY) && mouseButton == 0) {
                        bp.set(!bp.get());
                        return;
                    }
                }

                if (property instanceof NumberValue) {
                    final NumberValue dp = (NumberValue) property;
                    if (RenderUtil.isHovering(offset + 88, y + 50 + moduleY + 2, 78, 16, mouseX, mouseY) && mouseButton == 0) {
                        dp.sliding = true;
                        currentSliding = dp;
                    }
                }

                if (property instanceof ModeValue) {
                    final ModeValue sp = (ModeValue) property;
                    if (RenderUtil.isHovering(offset + 4, y + 50 + moduleY + 6, 184, 18, mouseX, mouseY)) {
                        if (mouseButton == 0) {
                            if (dropdownItem != property) {
                                dropdownItem = sp;
                                sp.animation = 100;
                                protectArea = new cc.xylitol.utils.render.Rectangle(offset + 100, y + moduleY + 50 + 24, 80, 14 * sp.getModes().length);
                            } else {
                                dropdownItem = null;
                                protectArea = null;
                            }
                        }
                    }
                }

                if (property instanceof TextValue) {
                    final TextValue tp = (TextValue) property;
                    if (RenderUtil.isHovering(offset + 4, y + 50 + moduleY + 6, 184, 18, mouseX, mouseY) && mouseButton == 0) {
                        currentEditing = tp;
                    } else if (currentEditing == tp) {
                        currentEditing = null;
                    }
                }
                if (property instanceof ColorValue) {
                    final ColorValue cp = (ColorValue) property;
                    if (RenderUtil.isHovering((float) (offset + 175 - 5.5), (float) (y + 50 + moduleY + 9 - 5.5), 11, 11, mouseX, mouseY)) {
                        if (mouseButton == 0) {
                            dropdownItem = cp;
                            protectArea = new cc.xylitol.utils.render.Rectangle(offset + 100, y + moduleY + 50 + 24, 80, cp.isAlphaChangeable() ? 80 : 67);
                        } else {
                            cp.setRainbowEnabled(!cp.isEnabledRainbow());
                        }
                    }
                }
                moduleY += property.getHeight();
            }
            moduleY += 4;
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (dropdownItem != null && protectArea != null) {
            if (dropdownItem instanceof ModeValue) {
                final ModeValue property = (ModeValue) dropdownItem;
                int buttonY = 0;
                for (String s : property.getModes()) {
                    final boolean isHovering = RenderUtil.isHovering(protectArea.getX() + .5F, protectArea.getY() + buttonY + .5F, protectArea.getWidth() - 1, 14, mouseX, mouseY);
                    if (isHovering && mouseButton == 0) {
                        property.set(s);
                        dropdownItem = null;
                        protectArea = null;
                        return;
                    }
                    buttonY += 14;
                }
            }

            if (dropdownItem instanceof ColorValue) {
                if (!RenderUtil.isHovering(protectArea.getX(), protectArea.getY(), protectArea.getWidth(), protectArea.getHeight() + 1, mouseX, mouseY)) {
                    dropdownItem = null;
                    protectArea = null;
                }
                return;
            }

        }

        if (!RenderUtil.isHovering(x, y, width, height, mouseX, mouseY)) return;
        float width = Math.max(FontManager.bold38.getStringWidth(Client.name.toUpperCase()), FontManager.bold38.getStringWidth("NOVOLINE"));

        if (RenderUtil.isHovering(x + width + 16 + 10, y + 10, 200, 20, mouseX, mouseY) && current != Category.Pages.CONFIGS) {
            searching = !searching;
            if (searching) {
                lists = new List[]{leftModules, rightModules};
                searchTextField.setText("");
            } else {
                leftModules = lists[0];
                rightModules = lists[1];
                resetModuleList();
            }
            searchTextField.setText("");
        }


        float pageY = 44 + CscrollAni;


        if (current == Category.Pages.CONFIGS) {
            boolean hovered = RenderUtil.isHovering(x + width + 16 + 4, y + 10, 89, 20, mouseX, mouseY);
            if (hovered) {
                mc.displayGuiScreen(new SavePresetScreen(this));
            }
            boolean hovered2 = RenderUtil.isHovering(x + width + 16 + 96, y + 10, 89, 20, mouseX, mouseY);
            if (hovered2) {
                Desktop.getDesktop().open(Client.instance.configManager.dir);
            }
        }
        if (current == Category.Pages.CONFIGS) {
            Client styles = Client.instance;
            float configY = y + 40 + 8 + 14 + scrollAni;
            for (String config : styles.getConfigManager().getConfigs()) {
                boolean fuck = RenderUtil.isHovering(x + width + 16 + 6 + 290 - 6, configY + 8, 76, 20, mouseX, mouseY);
                if (fuck) {
                    if (mouseButton == 0) {
                        if ("modules.json".equals(config)) {
                            Client.instance.configManager.saveUserConfig(config);
                            NotificationManager.post(NotificationType.SUCCESS, "Config", "Config successfully saved.");
                        } else {
                            Client.instance.configManager.loadUserConfig(config);
                            NotificationManager.post(NotificationType.SUCCESS, "Config", "Config successfully loaded.");

                        }
                    }
                }
                configY += 42;
            }
        }
        if (RenderUtil.isHovering(x, y + 44, width + 10, 240, mouseX, mouseY)) {
            for (Category page : Category.values()) {
                pageY += 12;
                for (Category.Pages cate : page.getSubPages()) {
                    if (RenderUtil.isHovering(x + 8, y + pageY, width, 16, mouseX, mouseY) && mouseButton == 0) {
                        if (cate != current) cate.animation.mouseClicked(mouseX, mouseY);
                        scrollAni = 0;
                        scrollY = 0;
                        current = cate;
                        dropdownItem = null;
                        protectArea = null;
                        currentEditing = null;
                        moduleWheel[0] = 0;
                        moduleWheel[1] = 0;
                        resetModuleList();
                        return;
                    }
                    pageY += 26;
                }
                pageY += 4;
            }
        }


        if (current.module && RenderUtil.isHovering(x + width + 16 + 8, y + 40, 400, 400, mouseX, mouseY)) {
            click(leftModules, x + width + 24, mouseX, mouseY, mouseButton);
            click(rightModules, x + width + 24 + 198, mouseX, mouseY, mouseButton);
        }
        if (mouseButton == 0 && RenderUtil.isHovering(x, y, 520, 43, mouseX, mouseY)) {
            dragX = mouseX - x;
            dragY = mouseY - y;
            dragging = true;
        }
    }

    public void setCurrent(Category.Pages current) {
        this.current = current;
    }

    public void resetModuleList() {
        leftModules.clear();
        rightModules.clear();

        final List<Module> allList = new ArrayList<>();

        if (searching) {
           for (Module module : Client.instance.getModuleManager().getModuleMap().values()) {
                if (module.getName().toLowerCase().replace(" ", "").contains(searchTextField.getText().toLowerCase().replace(" ", "")))
                    allList.add(module);
            }
        } else {
            allList.addAll(Client.instance.getModuleManager().getModsByPage(current));
        }

        allList.sort((o1, o2) -> o2.getValues().size() - o1.getValues().size());

        int updateIndex = 0;
        while (updateIndex <= allList.size() - 1) {
            leftModules.add(allList.get(updateIndex));
            updateIndex += 2;
        }

        updateIndex = 1;
        while (updateIndex <= allList.size() - 1) {
            rightModules.add(allList.get(updateIndex));
            updateIndex += 2;
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }


    public static class BindScreen extends GuiScreen {
        private final Module target;
        private final GuiScreen parent;

        public BindScreen(Module module, GuiScreen parent) {
            this.target = module;
            this.parent = parent;
        }

        @Override
        protected void keyTyped(char typedChar, int keyCode) throws IOException {
            super.keyTyped(typedChar, keyCode);

            if (keyCode == 1) {
                this.mc.displayGuiScreen(parent);
            }


            if (keyCode != 1 && keyCode != Keyboard.KEY_DELETE) {
                this.target.setKey(keyCode);
                this.mc.displayGuiScreen(parent);
            }

            if (keyCode == Keyboard.KEY_DELETE) {
                this.target.setKey(Keyboard.KEY_NONE);
                this.mc.displayGuiScreen(parent);
            }
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            this.drawDefaultBackground();
            this.drawCenteredString(this.fontRendererObj, "Press any key to bind " + EnumChatFormatting.YELLOW + target.getName(), this.width / 2, 150, 0xFFFFFF);
            this.drawCenteredString(this.fontRendererObj, "Press Delete key to remove the bind.", this.width / 2, 170, 0xFFFFFF);

            super.drawScreen(mouseX, mouseY, partialTicks);
        }
    }

    public static class SavePresetScreen extends GuiScreen {
        private final GuiScreen parent;
        private GuiTextField nameField;

        public SavePresetScreen(GuiScreen parent) {
            this.parent = parent;
        }

        @Override
        protected void keyTyped(char typedChar, int keyCode) throws IOException {
            super.keyTyped(typedChar, keyCode);

            this.nameField.textboxKeyTyped(typedChar, keyCode);

            if (keyCode == 1) {
                this.mc.displayGuiScreen(parent);
            }

            this.nameField.setText(this.nameField.getText().replace(" ", "").replace("#", "").replace("_NONE", ""));
        }

        public void initGui() {
            this.nameField = new GuiTextField(0, Minecraft.getMinecraft().fontRendererObj, this.width / 2 - 100, this.height / 6 + 20, 200, 20);
            this.buttonList.add(new GuiButton(3, this.width / 2 - 100, this.height / 6 + 40 + 22 * 5, "Add"));
            this.buttonList.add(new GuiButton(4, this.width / 2 - 100, this.height / 6 + 40 + 22 * 6, "Cancel"));
        }

        protected void actionPerformed(GuiButton button) throws IOException {

            if (button.id == 3) {
                Client.instance.configManager.saveConfig(this.nameField.getText());
                mc.displayGuiScreen(this.parent);
            }

            if (button.id == 4) {
                mc.displayGuiScreen(this.parent);
            }
        }


        @Override
        protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
            this.nameField.mouseClicked(mouseX, mouseY, mouseButton);
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }

        @Override
        public void updateScreen() {
            this.nameField.updateCursorCounter();
            super.updateScreen();
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            this.drawDefaultBackground();
            this.drawCenteredString(this.fontRendererObj, "Name", this.width / 2 - 89, this.height / 6 + 10, 0xFFFFFF);
            this.nameField.drawTextBox();

            this.drawCenteredString(this.fontRendererObj, "Adding Preset", this.width / 2, 30, 0xFFFFFF);

            super.drawScreen(mouseX, mouseY, partialTicks);
        }
    }
}
