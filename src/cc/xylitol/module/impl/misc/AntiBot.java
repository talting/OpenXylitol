package cc.xylitol.module.impl.misc;

import cc.xylitol.Client;
import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventPacket;
import cc.xylitol.event.impl.events.EventWorldLoad;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import cc.xylitol.utils.DebugUtil;
import cc.xylitol.value.impl.BoolValue;
import cc.xylitol.value.impl.ModeValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S14PacketEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AntiBot extends Module {
    private static final BoolValue entityID = new BoolValue("EntityID", false);
    private static final BoolValue sleep = new BoolValue("Sleep", false);
    private static final BoolValue noArmor = new BoolValue("NoArmor", false);
    private static final BoolValue height = new BoolValue("Height", false);
    private static final BoolValue ground = new BoolValue("Ground", false);
    private static final BoolValue dead = new BoolValue("Dead", false);
    private static final BoolValue health = new BoolValue("Health", false);
    private static final BoolValue hytGetNames = new BoolValue("HytGetName", false);
    private final BoolValue tips = new BoolValue("Quick Macro GetNameTips", false);
    private static final ModeValue hytGetNameModes =
            new ModeValue("Quick Macro GetNameMode", new String[]{"4v4/1v1", "32", "16"}, "4v4");
    private static final List<Integer> groundBotList = new ArrayList<>();
    private static final List<String> playerName = new ArrayList<>();

    public AntiBot() {
        super("AntiBot", Category.Misc);
    }


    @EventTarget
    public void onWorld(EventWorldLoad event) {
        clearAll();
    }

    private void clearAll() {
        playerName.clear();
    }

    @EventTarget
    public void onPacketReceive(EventPacket event) {
        if (mc.thePlayer == null || mc.theWorld == null) return;
        Packet<?> packet = event.getPacket();
        if (event.getPacket() instanceof S14PacketEntity && ground.getValue()) {
            Entity entity = ((S14PacketEntity) event.getPacket()).getEntity(mc.theWorld);

            if (entity instanceof EntityPlayer) {
                if (((S14PacketEntity) event.getPacket()).onGround
                        && !groundBotList.contains(entity.getEntityId())) {
                    groundBotList.add(entity.getEntityId());
                }
            }
        }
        if (hytGetNames.getValue() && packet instanceof S02PacketChat) {
            S02PacketChat s02PacketChat = (S02PacketChat) packet;
            if (s02PacketChat.getChatComponent().getUnformattedText().contains("获得胜利!") || s02PacketChat.getChatComponent().getUnformattedText().contains("游戏开始 ...")) {
                clearAll();
            }
            switch (hytGetNameModes.getValue()) {
                case "4v4/1v1":
                case "32": {
                    Matcher matcher = Pattern.compile("杀死了 (.*?)\\(").matcher(s02PacketChat.getChatComponent().getUnformattedText());
                    Matcher matcher2 = Pattern.compile("起床战争>> (.*?) (\\((((.*?) 死了!)))").matcher(s02PacketChat.getChatComponent().getUnformattedText());
                    if (matcher.find() && !s02PacketChat.getChatComponent().getUnformattedText().contains(": 起床战争>>") || !s02PacketChat.getChatComponent().getUnformattedText().contains(": 杀死了")) {
                        String name = matcher.group(1).trim();
                        if (!name.isEmpty()) {
                            playerName.add(name);
                            if (tips.getValue())
                                DebugUtil.log("§8[§c§l" + Client.name + "Tips§8]§c§dAddBot：" + name);
                            new Thread(() -> {
                                try {
                                    Thread.sleep(6000);
                                    playerName.remove(name);
                                    if (tips.getValue())
                                        DebugUtil.log("§8[§c§l" + Client.name + "Tips§8]§c§dRemovedBot：" + name);
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                            }).start();
                        }
                    }
                    if (matcher2.find() && !s02PacketChat.getChatComponent().getUnformattedText().contains(": 起床战争>>") || !s02PacketChat.getChatComponent().getUnformattedText().contains(": 杀死了")) {
                        String name = matcher2.group(1).trim();
                        if (!name.isEmpty()) {
                            playerName.add(name);
                            if (tips.getValue())
                                DebugUtil.log("§8[§c§l" + Client.name + "Tips§8]§c§dAddBot：" + name);
                            new Thread(() -> {
                                try {
                                    Thread.sleep(6000);
                                    playerName.remove(name);
                                    if (tips.getValue())
                                        DebugUtil.log("§8[§c§l" + Client.name + "Tips§8]§c§dRemovedBot：" + name);
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                            }).start();
                        }
                    }
                    break;
                }
                case "16": {
                    Matcher matcher = Pattern.compile("击败了 (.*?)!").matcher(s02PacketChat.getChatComponent().getUnformattedText());
                    Matcher matcher2 = Pattern.compile("玩家 (.*?)死了！").matcher(s02PacketChat.getChatComponent().getUnformattedText());
                    if (matcher.find() && !s02PacketChat.getChatComponent().getUnformattedText().contains(": 击败了") || !s02PacketChat.getChatComponent().getUnformattedText().contains(": 玩家 ")) {
                        String name = matcher.group(1).trim();
                        if (!name.isEmpty()) {
                            playerName.add(name);
                            if (tips.getValue())
                                DebugUtil.log("§8[§c§l" + Client.name + "Tips§8]§c§dAddBot：" + name);
                            new Thread(() -> {
                                try {
                                    Thread.sleep(10000);
                                    playerName.remove(name);
                                    if (tips.getValue())
                                        DebugUtil.log("§8[§c§l" + Client.name + "Tips§8]§c§dRemovedBot：" + name);
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                            }).start();
                        }
                    }
                    if (matcher2.find() && !s02PacketChat.getChatComponent().getUnformattedText().contains(": 击败了") || !s02PacketChat.getChatComponent().getUnformattedText().contains(": 玩家 ")) {
                        String name = matcher2.group(1).trim();
                        if (!name.isEmpty()) {
                            playerName.add(name);
                            DebugUtil.log("§8[§c§l" + Client.name + "Tips§8]§c§dAddBot：" + name);
                            new Thread(() -> {
                                try {
                                    Thread.sleep(10000);
                                    playerName.remove(name);
                                    DebugUtil.log("§8[§c§l" + Client.name + "Tips§8]§c§dRemovedBot：" + name);
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                            }).start();
                        }
                    }
                    break;

                }
            }
        }
    }

    public static boolean isServerBot(Entity entity) {
        if (Objects.requireNonNull(Client.instance.moduleManager.getModule(AntiBot.class)).getState()) {
            if (entity instanceof EntityPlayer) {
                if (hytGetNames.getValue() && playerName.contains(entity.getName())) {
                    return true;
                }
                if (height.getValue() && (entity.height <= 0.5 || ((EntityPlayer) entity).isPlayerSleeping() || entity.ticksExisted < 80)) {
                    return true;
                }
                if (dead.getValue() && entity.isDead) {
                    return true;
                }
                if (health.getValue() && ((EntityPlayer) entity).getHealth() == 0.0F) {
                    return true;
                }
                if (sleep.getValue() && ((EntityPlayer) entity).isPlayerSleeping()) {
                    return true;
                }
                if (entityID.getValue() && (entity.getEntityId() >= 1000000000 || entity.getEntityId() <= -1)) {
                    return true;
                }
                if (ground.getValue() && !groundBotList.contains(entity.getEntityId())) {
                    return true;
                }
                return noArmor.getValue() && (((EntityPlayer) entity).inventory.armorInventory[0] == null
                        && ((EntityPlayer) entity).inventory.armorInventory[1] == null
                        && ((EntityPlayer) entity).inventory.armorInventory[2] == null
                        && ((EntityPlayer) entity).inventory.armorInventory[3] == null);
            }
        }
        return false;
    }
}