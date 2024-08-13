package cc.xylitol.module.impl.render;

import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventAttack;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import cc.xylitol.value.impl.BoolValue;
import cc.xylitol.value.impl.NumberValue;
import net.minecraft.entity.Entity;


public class Particles extends Module {

    private final BoolValue normal = new BoolValue("Normal Particles", true);
    private final BoolValue enchantment = new BoolValue("Enchantment Particles", true);
    private final NumberValue amount = new NumberValue("Amount", 1, 1, 30, 1);

    public Particles() {
        super("Particles", Category.Render);
    }

    @EventTarget
    public void onAttack(EventAttack event) {
        final Entity entity = event.getTarget();

        for (int i = 0; i < amount.getValue(); i++) {
            if (normal.getValue()) {
                mc.thePlayer.onCriticalHit(entity);
            }
            if (enchantment.getValue()) {
                mc.thePlayer.onEnchantmentCritical(entity);
            }
        }
    }

}