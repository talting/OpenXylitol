package cc.xylitol.module.impl.misc;

import cc.xylitol.Client;
import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventTick;
import cc.xylitol.manager.FriendManager;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import cc.xylitol.module.impl.combat.KillAura;
import cc.xylitol.utils.TimerUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StringUtils;
import org.lwjglx.input.Mouse;

public class MCF extends Module {
    private TimerUtil timer = new TimerUtil();

    public MCF() {
        super("MCF", Category.Misc);
    }

    @EventTarget
    public void onTick(EventTick event) {
        if (mc.inGameHasFocus) {
            boolean down = Mouse.isButtonDown(2);
            if (down) {
                if (timer.delay(200)) {
                    if ((!getModule(KillAura.class).getState() || KillAura.target == null) && mc.objectMouseOver != null && mc.objectMouseOver.entityHit instanceof EntityPlayer) {
                        EntityPlayer player = (EntityPlayer) mc.objectMouseOver.entityHit;
                        String name = StringUtils.stripControlCodes(player.getName());
                        FriendManager friendManager = Client.instance.getFriendManager();
                        if (friendManager.isFriend(name)) {
                            friendManager.remove(name);
                        } else {
                            friendManager.add(name);
                        }
                    }
                    timer.reset();
                }
            }
        }
    }

}
