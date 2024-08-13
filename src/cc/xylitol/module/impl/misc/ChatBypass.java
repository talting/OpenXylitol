package cc.xylitol.module.impl.misc;

import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventHigherPacketSend;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import cc.xylitol.utils.DebugUtil;
import cc.xylitol.utils.TextUtil;
import cc.xylitol.utils.TimerUtil;
import cc.xylitol.value.impl.ModeValue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C01PacketChatMessage;

import java.util.Arrays;
import java.util.LinkedList;

public class ChatBypass extends Module {

    public ModeValue mode = new ModeValue("Mode", new String[]{"Unicode", "ASCII", "Love"}, "Unicode");

    private TimerUtil timer = new TimerUtil();

    public ChatBypass() {
        super("ChatBypass", Category.Misc);
    }

    public String getTag() {
        return mode.getValue();
    }

    @Override
    public void onEnable() {
        if (mode.is("ASCII")) DebugUtil.log("请在需要绕过的消息前面加上#");
    }

    @EventTarget
    private void onPacket(EventHigherPacketSend event) {
        final Packet<?> packet = event.getPacket();

        if (packet instanceof C01PacketChatMessage) {
            final C01PacketChatMessage c01PacketChatMessage = (C01PacketChatMessage) packet;
            final String message = c01PacketChatMessage.getMessage();

            if (message.startsWith("/") || message.startsWith("⠀⠀⠀")) return;

            if (mode.getValue().equalsIgnoreCase("ascii") && message.startsWith("#")) {
                event.setCancelled(true);

                String[] result = TextUtil.getGlyph(message.replaceFirst("#", ""));

                if (result != null) {
                    LinkedList<String> list = new LinkedList<>(Arrays.asList(result));
                    new Thread(() -> {

                        while (!list.isEmpty()) {

                            if (timer.hasTimeElapsed(2100, true)) {
                                mc.thePlayer.sendChatMessageNoEvent(list.getFirst());
                                list.removeFirst();
                                timer.reset();
                            }
                        }
                    }).start();
                }

            } else if (mode.getValue().equalsIgnoreCase("love")) {
                event.setCancelled(true);

                StringBuilder stringBuilder = new StringBuilder();

                for (char c : message.toCharArray()) {
                    stringBuilder.append(c).append("♥");
                }
                mc.thePlayer.sendChatMessageNoEvent(stringBuilder.toString());
            } else {
                StringBuilder stringBuilder = new StringBuilder();

                for (char c : message.toCharArray()) {
                    if (c >= 33 && c <= 128)
                        stringBuilder.append(Character.toChars(c + 65248));
                    else
                        stringBuilder.append(c);
                }
                ((C01PacketChatMessage) packet).setMessage(stringBuilder.toString());
            }
        }
    }
}
