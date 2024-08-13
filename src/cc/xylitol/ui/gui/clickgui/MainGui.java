package cc.xylitol.ui.gui.clickgui;

import cc.xylitol.Client;
import cc.xylitol.module.Category;
import cc.xylitol.ui.font.FontManager;
import cc.xylitol.utils.TimerUtil;
import cc.xylitol.utils.render.RenderUtil;
import cc.xylitol.utils.render.animation.Direction;
import cc.xylitol.utils.render.animation.impl.EaseBackIn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjglx.input.Keyboard;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author xiatian233 && ChengFeng
 */
public class MainGui extends GuiScreen {
    private final EaseBackIn easeBackIn = new EaseBackIn(500, 1F, 1.5F);
    private final TimerUtil timer = new TimerUtil();
    private final TimerUtil startTimer = new TimerUtil();
    private final boolean got = false;
    public int indexx;
    public int indexy;
    ArrayList<CategoryPanel> categoryPanels;
    private int openIndex = 0;
    private boolean shouldDo = true;
    private boolean shouldClose = false;

    public MainGui() {
        categoryPanels = new ArrayList();
        for (Category category : Category.values()) {
            categoryPanels.add(new CategoryPanel(category));
        }
        easeBackIn.setDirection(Direction.BACKWARDS);
    }

    @Override
    public void initGui() {
        easeBackIn.setDirection(Direction.FORWARDS);

        timer.reset();
        startTimer.reset();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (easeBackIn.finished(Direction.BACKWARDS)) {
            Minecraft.getMinecraft().displayGuiScreen(null);
            return;
        }
        indexy = 40;
        indexx = 110;
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());

        if (shouldClose && startTimer.hasTimeElapsed(categoryPanels.size() * 50L)) {
            easeBackIn.setDirection(Direction.BACKWARDS);
        }

        if (!shouldClose || startTimer.hasTimeElapsed(categoryPanels.size() * 50L)) {
            RenderUtil.scaleStart(sr.getScaledWidth() / 2f, sr.getScaledHeight() / 2f, (float) easeBackIn.getOutput());
        }
        for (CategoryPanel categoryPanel : categoryPanels) {
            categoryPanel.drawscreen(mouseX, mouseY, indexx, indexy, (int) (120 * easeBackIn.getOutput()), easeBackIn);
            indexx += 110;
        }
        if (!shouldClose || startTimer.hasTimeElapsed(categoryPanels.size() * 50L)) {
            RenderUtil.scaleEnd();
        }

        if (startTimer.hasTimeElapsed(500)) {
            if (shouldDo && timer.hasTimeElapsed(100)) {
                categoryPanels.get(openIndex).setExtended(true);
                openIndex++;
                if (openIndex == categoryPanels.size()) {
                    openIndex = categoryPanels.size() - 1;
                    shouldDo = false;
                }
                timer.reset();
            }
        }

        if (shouldClose && timer.hasTimeElapsed(50)) {
            categoryPanels.get(openIndex).setExtended(false);
            openIndex--;
            if (openIndex == -1) {
                openIndex = 0;
                shouldClose = false;
            }
            timer.reset();
        }

        String text = "按下Ctrl + 想要绑定的键位来绑定按键,按下Delete来删除键位";
        FontManager.font20.drawString(text, sr.getScaledWidth() / 2 - FontManager.font20.getStringWidth(text) / 2, sr.getScaledHeight() - FontManager.font20.getHeight() - 40, -1);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (CategoryPanel categoryPanel : categoryPanels) {
            categoryPanel.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            Client.instance.configManager.saveAllConfig();
            timer.reset();
            shouldClose = true;
            startTimer.reset();
        }
        for (CategoryPanel categoryPanel : categoryPanels) {
            categoryPanel.keyTyped(keyCode);
        }
    }

    @Override
    public void onGuiClosed() {
        Client.instance.configManager.saveAllConfig();
    }
}
