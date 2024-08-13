package net.minecraft.network.play.client;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.EnumHand;
import net.vialoadingbase.ViaLoadingBase;

import java.io.IOException;

public class C0APacketAnimation implements Packet<INetHandlerPlayServer> {
    private EnumHand hand;

    public C0APacketAnimation() {
    }

    public C0APacketAnimation(EnumHand hand) {
        this.hand = hand;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException {
        if (ViaLoadingBase.getInstance().getTargetVersion().isNewerThanOrEqualTo(ProtocolVersion.v1_14)) {
            this.hand = buf.readEnumValue(EnumHand.class);
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException {
        if (ViaLoadingBase.getInstance().getTargetVersion().isNewerThanOrEqualTo(ProtocolVersion.v1_14)) {
            buf.writeEnumValue(hand);
        }
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayServer handler) {
        handler.handleAnimation(this);
    }
}
