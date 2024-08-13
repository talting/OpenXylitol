package cc.xylitol.module.impl.render;

import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventStrafe;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import cc.xylitol.ui.gui.clickgui.MainGui;
import cc.xylitol.ui.gui.clickgui.NeverLoseClickGui;
import org.lwjglx.input.Keyboard;

public class ClickGUI extends Module {
    public ClickGUI() {
        super("ClickGUI", Category.Render);
        setKey(Keyboard.KEY_RSHIFT);
    }

    @Override
    public void onEnable() {
        mc.displayGuiScreen(NeverLoseClickGui.INSTANCE);
        this.toggle();
    }
}
