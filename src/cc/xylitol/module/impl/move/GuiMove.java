package cc.xylitol.module.impl.move;

import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventUpdate;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import cc.xylitol.ui.gui.clickgui.MainGui;
import cc.xylitol.ui.gui.clickgui.NeverLoseClickGui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import org.lwjglx.input.Keyboard;

import java.util.Arrays;
import java.util.List;

public class GuiMove extends Module {

    private static final List<KeyBinding> keys = Arrays.asList(mc.gameSettings.keyBindForward, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindJump);

    public GuiMove() {
        super("GuiMove", Category.Movement);
    }




    public static void updateStates() {
        if (mc.currentScreen != null) {
            for (KeyBinding k : keys) {
                k.setPressed(GameSettings.isKeyDown(k));
                if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
                    if (mc.thePlayer.rotationPitch > -90) {
                        mc.thePlayer.rotationPitch -= 5;
                    }
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
                    if (mc.thePlayer.rotationPitch < 90) {
                        mc.thePlayer.rotationPitch += 5;
                    }
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
                    mc.thePlayer.rotationYaw -= 5;
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
                    mc.thePlayer.rotationYaw += 5;
                }
            }
        }
    }

    @EventTarget
    public void onMotion(final EventUpdate event) {
        if (mc.currentScreen instanceof GuiContainer || (mc.currentScreen instanceof NeverLoseClickGui)) {
            updateStates();
        }
    }
}
