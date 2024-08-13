package net.minecraft.network.play.client;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.EnumHand;
import net.vialoadingbase.ViaLoadingBase;

import java.io.IOException;

public class CPacketPlayerTryUseItem implements Packet<INetHandlerPlayServer> {
    private EnumHand hand;
    private int sequence;

    public CPacketPlayerTryUseItem() {
    }

    public CPacketPlayerTryUseItem(EnumHand p_i46857_1_) {
        this.hand = p_i46857_1_;
    }

    public CPacketPlayerTryUseItem(EnumHand p_i46857_1_, int sequence) {
        this.hand = p_i46857_1_;
        this.sequence = sequence;
    }

    public void readPacketData(PacketBuffer p_readPacketData_1_) throws IOException {
        this.hand = p_readPacketData_1_.readEnumValue(EnumHand.class);
        if (ViaLoadingBase.getInstance().getTargetVersion().isNewerThanOrEqualTo(ProtocolVersion.v1_19)) {
            this.sequence = p_readPacketData_1_.readInt();
        }
    }

    public void writePacketData(PacketBuffer p_writePacketData_1_) throws IOException {
        p_writePacketData_1_.writeEnumValue(this.hand);
        if (ViaLoadingBase.getInstance().getTargetVersion().isNewerThanOrEqualTo(ProtocolVersion.v1_19)) {
            p_writePacketData_1_.writeInt(this.sequence);
        }
    }

    public void processPacket(INetHandlerPlayServer p_processPacket_1_) {
        p_processPacket_1_.processTryUseItem(this);
    }

    public EnumHand getHand() {
        return this.hand;
    }

    public int getSequence() {
        return sequence;
    }

    public int getViaVersionPacketID(ProtocolVersion protocolVersion) {
        switch (protocolVersion.getVersion()) {
            //V1_9
            case 107:
            case 108:
            case 109:
            case 110:
                //V1_10
            case 210:
                //V1_11_*
            case 315:
            case 316: {
                return 29;
            }
            //V1_12_*
            case 335:
            case 338:
            case 340: {
                return 32;
            }
            //V1_13_*
            case 393:
            case 401:
            case 404: {
                return 42;
            }
            //V1_14_*
            case 447:
            case 480:
            case 485:
            case 490:
            case 498:
                //V1_15_*
            case 573:
            case 575:
            case 578: {
                return 45;
            }
            //V1_16_(0/1)
            case 735:
            case 736: {
                return 46;
            }
            //V1_16_(2/3/4/5)
            case 751:
            case 753:
            case 754:
                //V1_17_*
            case 755:
            case 756:
                //V1_18_*
            case 757:
            case 758: {
                return 47;
            }
            //V1_19
            case 759: {
                return 49;
            }
            //V1_19_(1/2/3/4)
            case 760:
            case 761:
            case 762:
                //V1_20_*
            case 763:
            case 764:
            case 765: {
                return 50;
            }
        }
        return -9999;
    }
}
