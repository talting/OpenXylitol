package cc.xylitol.ui.gui.alt;

import cc.xylitol.Client;
import cc.xylitol.module.impl.render.HUD;
import cc.xylitol.ui.font.FontManager;
import cc.xylitol.ui.hud.notification.NotificationManager;
import cc.xylitol.ui.hud.notification.NotificationType;
import cc.xylitol.utils.WebUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.apache.commons.lang3.StringEscapeUtils;
import top.fl0wowp4rty.phantomshield.annotations.Native;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.zip.InflaterInputStream;

public class GuiCookieGen extends GuiScreen {
    private final GuiScreen previousScreen;
    private GuiTextFieldNoLimit cookieField;

    public GuiCookieGen(GuiScreen previousScreen) {
        this.previousScreen = previousScreen;
    }

    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 1:
                this.mc.displayGuiScreen(this.previousScreen);
                break;
            case 1145:
                getCookie();
        }
    }


    private static String fixJsonSyntax(String corruptedJson) {
        return corruptedJson.replaceAll("[^\\x00-\\x7F]", "");
    }
    @Native
    private void getCookie() {
        String result = WebUtils.get(new String(Base64.getDecoder().decode("aHR0cHM6Ly9jbG91ZC5qcy5tY2Rkcy5jbi9hcGkvQUNDT1VOVC91c2VyL0xpbWVyZW5jZS9nZXQucGhwP3Rva2VuPWRiNTIwM2Q4YWYzZjMyZmYyYzkyNWY1MTQ1YTgxYjJlMzNiMTBhYjcxZjJiY2YyZWJhZjgwODg2N2E1ZjNkZDM0NTYwODBjNzQ5N2Y5NWM0MTQ4OTZlZjNlYmFiMzk4OWQ5ZGViYTg0NGIwNDNkMGNjZWFmZDdmZDBiNjBlZTMzJnR5cGU9c2F1dGg=")));

        if (result != null) {
            try {
                JsonObject jsonObject = JsonParser.parseString(result).getAsJsonObject();

                String msg = jsonObject.getAsJsonPrimitive("msg").getAsString();
                JsonObject dataObject = jsonObject.getAsJsonObject("data");
                String corruptedSauthJson = dataObject.getAsJsonPrimitive("account").getAsString();
                String decodedMsg = StringEscapeUtils.unescapeJava(msg);

                // 真该死了
                String sauthJson = fixJsonSyntax(corruptedSauthJson);


                NotificationManager.post(NotificationType.INFO, "Result", decodedMsg + ", 已自动复制");

                cookieField.setText(sauthJson);
                setClipboardString(sauthJson);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void drawScreen(int x, int y, float z) {
        this.drawDefaultBackground();
        drawBackground(0);
        this.cookieField.drawTextBox();
        FontManager.font20.drawCenteredStringWithShadow("Cookie generator", (float) (this.width / 2), 20.0F, -1);
        Client.instance.getModuleManager().getModule(HUD.class).drawNotifications();

        super.drawScreen(x, y, z);
    }

    @Override
    public void initGui() {
        final int var3 = this.height / 4 + 24;
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, var3 + 72 + 12 + 24, "Back"));
        this.buttonList.add(new GuiButton(1145, this.width / 2 - 100, var3 + 72 + 12 + 48, "Generate Cookie"));
        this.cookieField = new GuiTextFieldNoLimit(1, this.mc.fontRendererObj, this.width / 2 - 100, var3 + 72 - 12, 200, 20);
        this.cookieField.setFocused(true);
        this.cookieField.setMaxStringLength(200);
    }

    @Override
    protected void keyTyped(char character, int key) throws IOException {
        super.keyTyped(character, key);

        if (character == '\t' && this.cookieField.isFocused()) {
            this.cookieField.setFocused(!this.cookieField.isFocused());
        }

        if (character == '\r') {
            this.actionPerformed(this.buttonList.get(0));
        }

        this.cookieField.textboxKeyTyped(character, key);
    }

    @Override
    protected void mouseClicked(int x, int y, int button) throws IOException {
        super.mouseClicked(x, y, button);

        this.cookieField.mouseClicked(x, y, button);
    }

    @Override
    public void updateScreen() {
        this.cookieField.updateCursorCounter();
    }
}
