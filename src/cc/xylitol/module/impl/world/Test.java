package cc.xylitol.module.impl.world;

import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventPacket;
import cc.xylitol.event.impl.events.EventTick;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import cc.xylitol.utils.DelayData;
import cc.xylitol.utils.PacketUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Test extends Module {
    public static Test INSTANCE;
    private int ticks;

    public Test() {
        super("SpectatorAbuse", Category.World);
        INSTANCE = this;
    }

    private final Set<DelayData> packetQueue = new LinkedHashSet<DelayData>();
    private final List<Packet<INetHandlerPlayClient>> packetBuf = new LinkedList<>();

    private boolean delay = false;

    @Override
    public void onEnable() {

        ticks = 0;
    }

    @Override
    public void onDisable() {
        if (mc.thePlayer != null) {
            packetQueue.forEach(it -> PacketUtil.handlePacket(it.getPacket()));
        }

        packetBuf.clear();
        packetQueue.clear();
        delay = false;
        ticks = 0;

    }

    @EventTarget
    public void onUpdate(EventTick event) {
        if (delay) {
            ticks++;
        }

    }

    @EventTarget
    public void onPacket(EventPacket event) {
        Packet<?> packet = event.getPacket();
        if (mc.thePlayer.ticksExisted < 20) {
            packetQueue.clear();
            return;
        }
        if (packet instanceof S08PacketPlayerPosLook && mc.thePlayer.capabilities.isFlying) {
            if (!delay) {
                delay = true;
            }
        }
        if (delay) {
            if (packet instanceof C03PacketPlayer) {
                C03PacketPlayer c03 = (C03PacketPlayer) packet;
                c03.onGround = false;
            }
            if (event.getEventType() == EventPacket.EventState.RECEIVE) {
                if (packet instanceof S32PacketConfirmTransaction/* && getModule(Fly.class).getState()*/) {
                    packetQueue.add(new DelayData(packet, System.currentTimeMillis()));
                    event.setCancelled(true);

                    mc.getNetHandler().getNetworkManager().sendUnregisteredPacketWithoutEvent(new C0FPacketConfirmTransaction(0, (short) 0, true));

                }

            }
        }
    }
}
