package cc.xylitol.manager;

import cc.xylitol.event.annotations.EventPriority;
import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventPacket;
import cc.xylitol.event.impl.events.EventWorldLoad;
import cc.xylitol.utils.PacketUtil;
import cc.xylitol.utils.TimerUtil;
import lombok.Getter;
import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

import static cc.xylitol.Client.mc;

@Getter
public class BlinkManager {

    public final ConcurrentLinkedQueue<Packet<?>> packets = new ConcurrentLinkedQueue<>();
    public boolean blinking, dispatch;
    public ArrayList<Class<?>> exemptedPackets = new ArrayList<>();
    public TimerUtil exemptionWatch = new TimerUtil();

    public void setExempt(Class<?>... packets) {
        exemptedPackets = new ArrayList<>(Arrays.asList(packets));
        exemptionWatch.reset();
    }

    @EventTarget
    @EventPriority(8888)
    public void onPacket(EventPacket event) {
        if (event.getPacket() instanceof S01PacketJoinGame) {
            packets.clear();
            blinking = false;
        }
        if (event.getEventType() == EventPacket.EventState.SEND) {

            if (mc.thePlayer == null) {
                packets.clear();
                exemptedPackets.clear();
                return;
            }

            if (mc.thePlayer.isDead || mc.isSingleplayer() || !mc.getNetHandler().doneLoadingTerrain) {
                packets.forEach(PacketUtil::sendPacketNoEvent);
                packets.clear();
                blinking = false;
                exemptedPackets.clear();
                return;
            }

            final Packet<?> packet = event.getPacket();

            if (packet instanceof C00Handshake || packet instanceof C00PacketLoginStart ||
                    packet instanceof C00PacketServerQuery || packet instanceof C01PacketPing ||
                    packet instanceof C01PacketEncryptionResponse) {
                return;
            }

            if (blinking && !dispatch) {
                if (exemptionWatch.delay(100)) {
                    exemptionWatch.reset();
                    exemptedPackets.clear();
                }

                if (!event.isCancelled() && exemptedPackets.stream().noneMatch(packetClass ->
                        packetClass == packet.getClass())) {
                    packets.add(packet);
                    event.setCancelled(true);
                }
            } else if (packet instanceof C03PacketPlayer) {
                packets.forEach(PacketUtil::sendPacketNoEvent);
                packets.clear();
                dispatch = false;
            }
        }
    }

    ;

    public void dispatch() {
        dispatch = true;
    }

    @EventTarget
    @EventPriority(8888)
    public void onWorld(EventWorldLoad e) {
        packets.clear();
        blinking = false;
    }

    ;


}