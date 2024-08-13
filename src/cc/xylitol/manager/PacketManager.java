package cc.xylitol.manager;

import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventPacket;
import cc.xylitol.event.impl.events.EventWorldLoad;
import cc.xylitol.utils.PacketUtil;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PacketManager {
    private static final Minecraft mc = Minecraft.getMinecraft();

    private BlinkFlag blinkFlag = BlinkFlag.NONE;

    public final Queue<Packet<?>> blinkPackets = new ConcurrentLinkedQueue<>();

    @Getter
    @Setter
    public static int sequence = 0;

    public boolean handle(Packet<?> packet) {
        if (blinkFlag == BlinkFlag.NONE) {
            return false;
        }

        blinkPackets.offer(packet);
        return true;
    }

    public boolean blink(boolean state, BlinkFlag flag) {
        if (flag == BlinkFlag.NONE) {
            return false;
        }

        if (blinkFlag == BlinkFlag.NONE || blinkFlag == flag) {
            if (state) {
                blinkFlag = flag;
                return true;
            }

            if (mc.getNetHandler() != null) {
                blinkFlag = BlinkFlag.NONE;
                while (!blinkPackets.isEmpty()) {
                    Packet<?> packet = blinkPackets.poll();
                    PacketUtil.sendPacketNoEvent(packet);
                }
            }
            clear(flag);

            return true;
        }

        return false;
    }

    public void clear(BlinkFlag flag) {
        if (flag == BlinkFlag.NONE) {
            return;
        }

        if (blinkFlag == flag) {
            blinkPackets.clear();
            blinkFlag = BlinkFlag.NONE;
        }
    }

    public BlinkFlag flag() {
        return blinkFlag;
    }

    @SuppressWarnings("unused")
    @EventTarget
    public void onPacket(EventPacket event) {
        if (event.getPacket() instanceof S08PacketPlayerPosLook) {
            blink(false, blinkFlag);
        } else if (
                event.getPacket() instanceof C00Handshake ||
                        event.getPacket() instanceof C00PacketLoginStart ||
                        event.getPacket() instanceof C00PacketServerQuery ||
                        event.getPacket() instanceof C01PacketPing ||
                        event.getPacket() instanceof C01PacketEncryptionResponse
        ) {
            blink(false, blinkFlag);
        } else {
            if (mc.thePlayer.isDead || !(mc.getNetHandler()).doneLoadingTerrain) {
                blink(false, blinkFlag);
            }
        }

    }

    @SuppressWarnings("unused")
    @EventTarget
    public void onWorldLoad(EventWorldLoad event) {
        sequence = 0;
        clear(blinkFlag);
    }

    public enum BlinkFlag {
        NONE, ANTI_VOID, AUTO_BLOCK, BLINK, NO_FALL, NO_SLOW
    }
}
