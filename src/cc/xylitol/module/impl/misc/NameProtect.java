package cc.xylitol.module.impl.misc;

import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventText;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import cc.xylitol.value.impl.BoolValue;
import net.minecraft.client.network.NetworkPlayerInfo;

import java.util.ArrayList;
import java.util.List;

public class NameProtect extends Module {
    public NameProtect() {
        super("NameProtect", Category.Misc);
    }

    public static BoolValue hideNameValue = new BoolValue("Hide name", true);
    public static BoolValue allPlayersValue = new BoolValue("All players", true);


    @EventTarget
    public void onText(EventText event) {
        if (mc.thePlayer == null || mc.theWorld == null || mc.getNetHandler() == null) return;
        if (hideNameValue.get()) {
            event.string = event.string.replace(mc.thePlayer.getName(), "critical_2");
        }
        if (allPlayersValue.getValue()) {
            List<NetworkPlayerInfo> playerInfoList = new ArrayList<>(mc.getNetHandler().getPlayerInfoMap());

            for (int i = 0; i < playerInfoList.size(); i++) {
                NetworkPlayerInfo entity = playerInfoList.get(i);
                event.string = event.string.replace(entity.getGameProfile().getName(), "张铁楠#" + (1337 + i));
            }
        }
    }

}
