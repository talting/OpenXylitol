package cc.xylitol.module.impl.world;

import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import top.fl0wowp4rty.phantomshield.annotations.Native;

@Native
public class Disabler extends Module {
    @Override
    public void onEnable() {
        //noSlow = getModule(NoSlow.class);
    }

    public Disabler() {
        super("Disabler", Category.Misc);
    }


}