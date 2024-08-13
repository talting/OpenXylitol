package cc.xylitol.utils;

import cc.xylitol.manager.PacketManager;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.server.*;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumHand;
import net.vialoadingbase.ViaLoadingBase;
import top.fl0wowp4rty.phantomshield.annotations.Native;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import static cc.xylitol.Client.mc;

public class PacketUtil {

    public static void send(Packet<?> packet) {
        if (mc.thePlayer != null) {
            mc.getNetHandler().addToSendQueue(packet);
        }
    }

    public static void sendTryUseItemPacket() {
        boolean isSword = mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;

        if (isSword) {
            mc.getNetHandler().addToSendQueue(createTryUseItemPacket(mc.thePlayer.getHeldItem(), EnumHand.MAIN_HAND));
            if (ViaLoadingBase.getInstance().getTargetVersion().isNewerThan(ProtocolVersion.v1_8))
                mc.getNetHandler().addToSendQueue(createTryUseItemPacket(mc.thePlayer.getHeldItem(), EnumHand.OFF_HAND));
        }

    }

    public static Packet<INetHandlerPlayServer> createTryUseItemPacket(ItemStack heldItem, EnumHand hand) {
        if (ViaLoadingBase.getInstance().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8)) {
            return new C08PacketPlayerBlockPlacement(heldItem);
        } else if (ViaLoadingBase.getInstance().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_18_2)) {
            return new CPacketPlayerTryUseItem(hand);
        } else {
            CPacketPlayerTryUseItem wrapper = new CPacketPlayerTryUseItem(hand, PacketManager.getSequence());
            PacketManager.setSequence(PacketManager.getSequence() + 1);
            return wrapper;
        }
    }

    @Native
    public static void sharkBee() {
        byte[] modList = "\u0002\u001c\tminecraft\u00061.12.2\tdepartmod\u00031.0\rscreenshotmod\u00031.0\u0003ess\u00051.0.2\u0007vexview\u00062.6.10\u0012basemodneteasecore\u00051.9.4\nsidebarmod\u00031.0\u000bskincoremod\u00061.12.2\u000ffullscreenpopup\f1.12.2.38000\bstoremod\u00031.0\u0003mcp\u00049.42\u0007skinmod\u00031.0\rplayermanager\u00031.0\rdepartcoremod\u00061.12.2\tmcbasemod\u00031.0\u0011mercurius_updater\u00031.0\u0003FML\t8.0.99.99\u000bneteasecore\u00061.12.2\u0007antimod\u00032.0\u000bfoamfixcore\u00057.7.4\nnetworkmod\u00061.11.2\u0007foamfix\t@VERSION@\u0005forge\f14.23.5.2768\rfriendplaymod\u00031.0\u0004libs\u00051.0.2\tfiltermod\u00031.0\u0007germmod\u00053.4.2\tpromotion\u000e1.0.0-SNAPSHOT\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000".getBytes();
        byte[] channels = "FML|HS\u0000FML\u0000FML|MP\u0000FML\u0000antimod\u0000ChatVexView\u0000Base64VexView\u0000HudBase64VexView\u0000FORGE\u0000germplugin-netease\u0000VexView\u0000hyt0\u0000armourers\u0000promotion".getBytes();
        byte[] vexView = encode("{\"packet_sub_type\":\"814:469\",\"packet_data\":\"2.6.10\",\"packet_type\":\"ver\"}");

        mc.getNetHandler().getNetworkManager().sendPacket(new C17PacketCustomPayload("REGISTER", new PacketBuffer(Unpooled.buffer().writeBytes(channels))));
        mc.getNetHandler().getNetworkManager().sendPacket(new C17PacketCustomPayload("FML|HS", new PacketBuffer(Unpooled.buffer().writeBytes(modList))));
        mc.getNetHandler().getNetworkManager().sendPacket(new C17PacketCustomPayload("VexView", new PacketBuffer(Unpooled.buffer().writeBytes(vexView))));
    }

    @Native
    private static byte[] encode(String data) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
            gzipOutputStream.write(data.getBytes());
            gzipOutputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isUseItemPacket(Packet<?> packet) {
        boolean isSword = mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
        if (ViaLoadingBase.getInstance().getTargetVersion().isNewerThan(ProtocolVersion.v1_8)) {
            if (packet instanceof CPacketPlayerTryUseItem) {
                CPacketPlayerTryUseItem wrapper = (CPacketPlayerTryUseItem) packet;
                if (isSword) {
                    return wrapper.getHand().equals(EnumHand.OFF_HAND);
                }
                return true;
            }
        } else {
            if (packet instanceof C08PacketPlayerBlockPlacement) {
                C08PacketPlayerBlockPlacement wrapper = (C08PacketPlayerBlockPlacement) packet;
                BlockPos blockPos = wrapper.getPosition();
                return wrapper.getPlacedBlockDirection() == 255 && (wrapper.facingX == 0 && wrapper.facingY == 0 && wrapper.facingZ == 0) && (blockPos.getX() == -1 && blockPos.getY() == -1 && blockPos.getZ() == -1);
            }
        }
        return false;
    }

    public static boolean isCPacket(Packet<?> packet) {
        return packet.getClass().getSimpleName().startsWith("C");
    }

    public static void handlePacket(Packet<?> packet) {
        INetHandlerPlayClient netHandler = mc.getNetHandler();

        if (packet instanceof S00PacketKeepAlive) {
            netHandler.handleKeepAlive((S00PacketKeepAlive) packet);
        } else if (packet instanceof S01PacketJoinGame) {
            netHandler.handleJoinGame((S01PacketJoinGame) packet);
        } else if (packet instanceof S02PacketChat) {
            netHandler.handleChat((S02PacketChat) packet);
        } else if (packet instanceof S03PacketTimeUpdate) {
            netHandler.handleTimeUpdate((S03PacketTimeUpdate) packet);
        } else if (packet instanceof S04PacketEntityEquipment) {
            netHandler.handleEntityEquipment((S04PacketEntityEquipment) packet);
        } else if (packet instanceof S05PacketSpawnPosition) {
            netHandler.handleSpawnPosition((S05PacketSpawnPosition) packet);
        } else if (packet instanceof S06PacketUpdateHealth) {
            netHandler.handleUpdateHealth((S06PacketUpdateHealth) packet);
        } else if (packet instanceof S07PacketRespawn) {
            netHandler.handleRespawn((S07PacketRespawn) packet);
        } else if (packet instanceof S08PacketPlayerPosLook) {
            netHandler.handlePlayerPosLook((S08PacketPlayerPosLook) packet);
        } else if (packet instanceof S09PacketHeldItemChange) {
            netHandler.handleHeldItemChange((S09PacketHeldItemChange) packet);
        } else if (packet instanceof S10PacketSpawnPainting) {
            netHandler.handleSpawnPainting((S10PacketSpawnPainting) packet);
        } else if (packet instanceof S0APacketUseBed) {
            netHandler.handleUseBed((S0APacketUseBed) packet);
        } else if (packet instanceof S0BPacketAnimation) {
            netHandler.handleAnimation((S0BPacketAnimation) packet);
        } else if (packet instanceof S0CPacketSpawnPlayer) {
            netHandler.handleSpawnPlayer((S0CPacketSpawnPlayer) packet);
        } else if (packet instanceof S0DPacketCollectItem) {
            netHandler.handleCollectItem((S0DPacketCollectItem) packet);
        } else if (packet instanceof S0EPacketSpawnObject) {
            netHandler.handleSpawnObject((S0EPacketSpawnObject) packet);
        } else if (packet instanceof S0FPacketSpawnMob) {
            netHandler.handleSpawnMob((S0FPacketSpawnMob) packet);
        } else if (packet instanceof S11PacketSpawnExperienceOrb) {
            netHandler.handleSpawnExperienceOrb((S11PacketSpawnExperienceOrb) packet);
        } else if (packet instanceof S12PacketEntityVelocity) {
            netHandler.handleEntityVelocity((S12PacketEntityVelocity) packet);
        } else if (packet instanceof S13PacketDestroyEntities) {
            netHandler.handleDestroyEntities((S13PacketDestroyEntities) packet);
        } else if (packet instanceof S14PacketEntity) {
            netHandler.handleEntityMovement((S14PacketEntity) packet);
        } else if (packet instanceof S18PacketEntityTeleport) {
            netHandler.handleEntityTeleport((S18PacketEntityTeleport) packet);
        } else if (packet instanceof S19PacketEntityStatus) {
            netHandler.handleEntityStatus((S19PacketEntityStatus) packet);
        } else if (packet instanceof S19PacketEntityHeadLook) {
            netHandler.handleEntityHeadLook((S19PacketEntityHeadLook) packet);
        } else if (packet instanceof S1BPacketEntityAttach) {
            netHandler.handleEntityAttach((S1BPacketEntityAttach) packet);
        } else if (packet instanceof S1CPacketEntityMetadata) {
            netHandler.handleEntityMetadata((S1CPacketEntityMetadata) packet);
        } else if (packet instanceof S1DPacketEntityEffect) {
            netHandler.handleEntityEffect((S1DPacketEntityEffect) packet);
        } else if (packet instanceof S1EPacketRemoveEntityEffect) {
            netHandler.handleRemoveEntityEffect((S1EPacketRemoveEntityEffect) packet);
        } else if (packet instanceof S1FPacketSetExperience) {
            netHandler.handleSetExperience((S1FPacketSetExperience) packet);
        } else if (packet instanceof S20PacketEntityProperties) {
            netHandler.handleEntityProperties((S20PacketEntityProperties) packet);
        } else if (packet instanceof S21PacketChunkData) {
            netHandler.handleChunkData((S21PacketChunkData) packet);
        } else if (packet instanceof S22PacketMultiBlockChange) {
            netHandler.handleMultiBlockChange((S22PacketMultiBlockChange) packet);
        } else if (packet instanceof S23PacketBlockChange) {
            netHandler.handleBlockChange((S23PacketBlockChange) packet);
        } else if (packet instanceof S24PacketBlockAction) {
            netHandler.handleBlockAction((S24PacketBlockAction) packet);
        } else if (packet instanceof S25PacketBlockBreakAnim) {
            netHandler.handleBlockBreakAnim((S25PacketBlockBreakAnim) packet);
        } else if (packet instanceof S26PacketMapChunkBulk) {
            netHandler.handleMapChunkBulk((S26PacketMapChunkBulk) packet);
        } else if (packet instanceof S27PacketExplosion) {
            netHandler.handleExplosion((S27PacketExplosion) packet);
        } else if (packet instanceof S28PacketEffect) {
            netHandler.handleEffect((S28PacketEffect) packet);
        } else if (packet instanceof S29PacketSoundEffect) {
            netHandler.handleSoundEffect((S29PacketSoundEffect) packet);
        } else if (packet instanceof S2APacketParticles) {
            netHandler.handleParticles((S2APacketParticles) packet);
        } else if (packet instanceof S2BPacketChangeGameState) {
            netHandler.handleChangeGameState((S2BPacketChangeGameState) packet);
        } else if (packet instanceof S2CPacketSpawnGlobalEntity) {
            netHandler.handleSpawnGlobalEntity((S2CPacketSpawnGlobalEntity) packet);
        } else if (packet instanceof S2DPacketOpenWindow) {
            netHandler.handleOpenWindow((S2DPacketOpenWindow) packet);
        } else if (packet instanceof S2EPacketCloseWindow) {
            netHandler.handleCloseWindow((S2EPacketCloseWindow) packet);
        } else if (packet instanceof S2FPacketSetSlot) {
            netHandler.handleSetSlot((S2FPacketSetSlot) packet);
        } else if (packet instanceof S30PacketWindowItems) {
            netHandler.handleWindowItems((S30PacketWindowItems) packet);
        } else if (packet instanceof S31PacketWindowProperty) {
            netHandler.handleWindowProperty((S31PacketWindowProperty) packet);
        } else if (packet instanceof S32PacketConfirmTransaction) {
            netHandler.handleConfirmTransaction((S32PacketConfirmTransaction) packet);
        } else if (packet instanceof S33PacketUpdateSign) {
            netHandler.handleUpdateSign((S33PacketUpdateSign) packet);
        } else if (packet instanceof S34PacketMaps) {
            netHandler.handleMaps((S34PacketMaps) packet);
        } else if (packet instanceof S35PacketUpdateTileEntity) {
            netHandler.handleUpdateTileEntity((S35PacketUpdateTileEntity) packet);
        } else if (packet instanceof S36PacketSignEditorOpen) {
            netHandler.handleSignEditorOpen((S36PacketSignEditorOpen) packet);
        } else if (packet instanceof S37PacketStatistics) {
            netHandler.handleStatistics((S37PacketStatistics) packet);
        } else if (packet instanceof S38PacketPlayerListItem) {
            netHandler.handlePlayerListItem((S38PacketPlayerListItem) packet);
        } else if (packet instanceof S39PacketPlayerAbilities) {
            netHandler.handlePlayerAbilities((S39PacketPlayerAbilities) packet);
        } else if (packet instanceof S3APacketTabComplete) {
            netHandler.handleTabComplete((S3APacketTabComplete) packet);
        } else if (packet instanceof S3BPacketScoreboardObjective) {
            netHandler.handleScoreboardObjective((S3BPacketScoreboardObjective) packet);
        } else if (packet instanceof S3CPacketUpdateScore) {
            netHandler.handleUpdateScore((S3CPacketUpdateScore) packet);
        } else if (packet instanceof S3DPacketDisplayScoreboard) {
            netHandler.handleDisplayScoreboard((S3DPacketDisplayScoreboard) packet);
        } else if (packet instanceof S3EPacketTeams) {
            netHandler.handleTeams((S3EPacketTeams) packet);
        } else if (packet instanceof S3FPacketCustomPayload) {
            netHandler.handleCustomPayload((S3FPacketCustomPayload) packet);
        } else if (packet instanceof S40PacketDisconnect) {
            netHandler.handleDisconnect((S40PacketDisconnect) packet);
        } else if (packet instanceof S41PacketServerDifficulty) {
            netHandler.handleServerDifficulty((S41PacketServerDifficulty) packet);
        } else if (packet instanceof S42PacketCombatEvent) {
            netHandler.handleCombatEvent((S42PacketCombatEvent) packet);
        } else if (packet instanceof S43PacketCamera) {
            netHandler.handleCamera((S43PacketCamera) packet);
        } else if (packet instanceof S44PacketWorldBorder) {
            netHandler.handleWorldBorder((S44PacketWorldBorder) packet);
        } else if (packet instanceof S45PacketTitle) {
            netHandler.handleTitle((S45PacketTitle) packet);
        } else if (packet instanceof S46PacketSetCompressionLevel) {
            netHandler.handleSetCompressionLevel((S46PacketSetCompressionLevel) packet);
        } else if (packet instanceof S47PacketPlayerListHeaderFooter) {
            netHandler.handlePlayerListHeaderFooter((S47PacketPlayerListHeaderFooter) packet);
        } else if (packet instanceof S48PacketResourcePackSend) {
            netHandler.handleResourcePack((S48PacketResourcePackSend) packet);
        } else if (packet instanceof S49PacketUpdateEntityNBT) {
            netHandler.handleEntityNBT((S49PacketUpdateEntityNBT) packet);
        } else {
            throw new IllegalArgumentException("Unable to match packet type to handle: " + packet.getClass());
        }
    }

    public static void sendPacketNoEvent(Packet<?> packet) {
        mc.getNetHandler().addToSendQueueUnregistered(packet);
    }

}
