package cc.xylitol.manager;

import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventPacket;
import net.minecraft.network.play.server.S02PacketChat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BanManager {
    public int bans = 0;

    @EventTarget
    public void onPacket(EventPacket event) {
        if (event.getPacket() instanceof S02PacketChat) {
            S02PacketChat packet = (S02PacketChat) event.getPacket();
            String text = packet.getChatComponent().getUnformattedText();

            if (text.contains("有一名玩家因为作弊已被踢出，祝您游戏愉快!!请大家遵守游戏规则，善待自己的游戏帐号。")) {
                bans++;
            }
            Pattern pattern = Pattern.compile("玩家(.*?)在本局游戏中行为异常");
            Matcher matcher = pattern.matcher(text);

            if (matcher.find()) {
                bans++;
            }
        }
    }
}
