package cc.xylitol.ui.gui.alt;

import cc.xylitol.ui.font.FontManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.EnumChatFormatting;

import java.io.IOException;

public abstract class GuiAltLogin extends GuiScreen {
    private final GuiScreen previousScreen;
    private GuiTextField username;
    protected volatile String status = EnumChatFormatting.YELLOW + "Pending...";

    public GuiAltLogin(GuiScreen previousScreen) {
        this.previousScreen = previousScreen;
    }

    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0:
                if (username.getText().length() != 0) {
                    this.onLogin(username.getText(), "");
                } else {
                    status = EnumChatFormatting.RED + "Login failed!";
                }
                break;
            case 1:
                this.mc.displayGuiScreen(this.previousScreen);
                break;
            case 1145:
                this.onLogin(StringUtils.randomString(StringUtils.ALPHA_POOL,10), "");
        }
    }

    public abstract void onLogin(String account,String password);

    public void drawScreen(int x, int y, float z) {
        this.drawDefaultBackground();
        drawBackground(0);
        this.username.drawTextBox();
        FontManager.font20.drawCenteredStringWithShadow("Directly Login", (float)(this.width / 2), 20.0F, -1);
        FontManager.font20.drawCenteredStringWithShadow(status, (float) (this.width / 2), (this.height / 4 + 24) + 38, -1);
        if (this.username.getText().isEmpty() && !this.username.isFocused()) {
            FontManager.font20.drawStringWithShadow("Username", (float)(this.width / 2 - 96), (this.height / 4 + 24) + 72 - 4, -7829368);
        }

        super.drawScreen(x, y, z);
    }

    @Override
    public void initGui() {
        final int var3 = this.height / 4 + 24;
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, var3 + 72 + 12, "Login"));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, var3 + 72 + 12 + 24, "Back"));
        this.buttonList.add(new GuiButton(1145, this.width / 2 - 100, var3 + 72 + 12 + 48, "Random User Name"));
        this.username = new GuiTextField(1, this.mc.fontRendererObj, this.width / 2 - 100, var3 + 72 - 12, 200, 20);
        this.username.setFocused(true);
        this.username.setMaxStringLength(200);
    }

    @Override
    protected void keyTyped(char character, int key) throws IOException {
        super.keyTyped(character, key);

        if (character == '\t' && this.username.isFocused()) {
            this.username.setFocused(!this.username.isFocused());
        }

        if (character == '\r') {
            this.actionPerformed(this.buttonList.get(0));
        }

        this.username.textboxKeyTyped(character, key);
    }

    @Override
    protected void mouseClicked(int x, int y, int button) throws IOException {
        super.mouseClicked(x, y, button);

        this.username.mouseClicked(x, y, button);
    }

    @Override
    public void updateScreen() {
        this.username.updateCursorCounter();
    }
}
