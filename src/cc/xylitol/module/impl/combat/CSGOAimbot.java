package cc.xylitol.module.impl.combat;

import cc.xylitol.Client;
import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventStrafe;
import cc.xylitol.event.impl.events.EventUpdate;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import cc.xylitol.module.impl.misc.Teams;
import cc.xylitol.utils.PacketUtil;
import cc.xylitol.utils.player.InventoryUtil;
import cc.xylitol.utils.player.Rotation;
import cc.xylitol.utils.player.RotationUtil;
import cc.xylitol.value.impl.BoolValue;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.apache.commons.lang3.RandomUtils;
import org.lwjglx.util.vector.Vector2f;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static cc.xylitol.utils.PacketUtil.sendPacketNoEvent;

public class CSGOAimbot extends Module {

    public CSGOAimbot() {
        super("CSGOAimbot", Category.Combat);
    }

    private final BoolValue silent = new BoolValue("Silent",false);
    private final BoolValue strafefix = new BoolValue("Silent-StrafeFix",false);
    private final BoolValue autofire = new BoolValue("AutoFire",false);
    private final BoolValue autoGun = new BoolValue("AutoGun", false);
    private final BoolValue predict = new BoolValue("Predict", false);

    private float pitch = 0F;
    private float yaw = 0F;
    private boolean a = false;
    private double X = 0.0;
    private double Z = 0.0;
    Rotation result = null;

    @EventTarget
    public void onUpdate(EventUpdate event) {
        Entity entity = getTarget();
        if(entity != null && !entity.isDead){
            if (autoGun.get()) {
                int slot = InventoryUtil.findItem2(9,45, Items.golden_hoe);
                ItemStack stack = mc.thePlayer.inventory.getStackInSlot(slot);
                int slot3 = InventoryUtil.findItem2(9,45, Items.iron_hoe);
                ItemStack stack3 = mc.thePlayer.inventory.getStackInSlot(slot3);
                if (mc.thePlayer.inventory.getStackInSlot(slot3).getDisplayName().contains("\u25AA")) {
                    PacketUtil.sendPacketNoEvent(new C0EPacketClickWindow(0,slot3,mc.thePlayer.inventory.currentItem,2,stack3, (short)3));
                }
                if (mc.thePlayer.inventory.getStackInSlot(slot).getDisplayName().contains("\u25AA")) {
                    PacketUtil.sendPacketNoEvent(new C0EPacketClickWindow(0,slot,mc.thePlayer.inventory.currentItem,2,stack, (short) 1));
                }
            }
        }

        if(mc.thePlayer.getHeldItem().getItem() instanceof ItemHoe) {

            if(entity != null && !entity.isDead) {
                if(predict.get()) {
                    X = (entity.posX - entity.lastTickPosX);
                    Z = (entity.posZ - entity.lastTickPosZ);
                }else{
                    X = 0.0;
                    Z = 0.0;
                }

                Vector2f rotation = RotationUtil.toRotation(RotationUtil.getCenter(entity.getEntityBoundingBox().expand(0, 0.7, 0).offset(2.55 * X, 0, 2.55 * Z)), predict.get());
                if (silent.get()) {
                    Client.instance.rotationManager.setRotation(rotation,180,true);
                } else {
                    mc.thePlayer.rotationPitch = rotation.getY();
                    mc.thePlayer.rotationYaw = rotation.getX();
                }


                //自动开火
                if(autofire.get()){
                    sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                }

            }
        }
    }
    public void onStrafe(EventStrafe event){
        if(strafefix.get()) {
            if (RotationUtil.targetRotation != null) {
                float yaw = RotationUtil.targetRotation.getYaw();
                float strafe = event.getStrafe();
                float forward = event.getForward();
                float friction = event.getFriction();

                float f = strafe * strafe + forward * forward;

                if (f >= 1.0E-4F) {
                    f = (float) Math.sqrt(f);

                    if (f < 1.0F)
                        f = 1.0F;

                    f = friction / f;
                    strafe *= f;
                    forward *= f;

                    float yawSin = (float) Math.sin((yaw * Math.PI / 180F));
                    float yawCos = (float) Math.cos((yaw * Math.PI / 180F));

                    EntityPlayerSP player = mc.thePlayer;

                    player.motionX += strafe * yawCos - forward * yawSin;
                    player.motionZ += forward * yawCos + strafe * yawSin;
                }
                event.setCancelled(true);
            }
        }
    }


    private Entity getTarget() {
        List<Entity> targets = mc.theWorld.loadedEntityList.stream()
                .filter(entity -> entity instanceof EntityPlayer &&
                        entity.getEntityId() != mc.thePlayer.getEntityId() &&
                        !Teams.isSameTeam(entity) &&
                        mc.thePlayer.canEntityBeSeen(entity)).collect(Collectors.toList());

        return(targets.stream().min(Comparator.comparingDouble(entity -> mc.thePlayer.getDistanceToEntity(entity))).orElse(null));
    }

}
