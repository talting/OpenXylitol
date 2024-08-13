package cc.xylitol.module.impl.world;

import cc.xylitol.Client;
import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.*;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import cc.xylitol.module.impl.render.HUD;
import cc.xylitol.ui.font.FontManager;
import cc.xylitol.utils.BlockUtil;
import cc.xylitol.utils.DebugUtil;
import cc.xylitol.utils.math.MathUtils;
import cc.xylitol.utils.player.*;
import cc.xylitol.utils.render.ColorUtil;
import cc.xylitol.utils.render.RenderUtil;
import cc.xylitol.utils.render.animation.Animation;
import cc.xylitol.utils.render.animation.Direction;
import cc.xylitol.utils.render.animation.impl.DecelerateAnimation;
import cc.xylitol.utils.render.shader.ShaderElement;
import cc.xylitol.value.impl.BoolValue;
import cc.xylitol.value.impl.ModeValue;
import cc.xylitol.value.impl.NumberValue;
import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.*;
import org.lwjglx.input.Keyboard;
import org.lwjglx.util.vector.Vector2f;

import javax.print.DocFlavor;
import java.awt.*;
import java.util.List;
import java.util.*;

import static cc.xylitol.module.impl.move.Eagle.getBlockUnderPlayer;
import static cc.xylitol.utils.player.MoveUtil.isMoving;

public class Scaffold extends Module {
    public static final List<Block> invalidBlocks = Arrays.asList(Blocks.enchanting_table, Blocks.furnace, Blocks.carpet, Blocks.crafting_table, Blocks.trapped_chest, Blocks.chest, Blocks.dispenser, Blocks.air, Blocks.water, Blocks.lava, Blocks.flowing_water, Blocks.flowing_lava/*, Blocks.sand*/, Blocks.snow_layer, Blocks.torch, Blocks.anvil, Blocks.jukebox, Blocks.stone_button, Blocks.wooden_button, Blocks.lever, Blocks.noteblock, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.wooden_pressure_plate, Blocks.heavy_weighted_pressure_plate, Blocks.stone_slab, Blocks.wooden_slab, Blocks.stone_slab2, Blocks.red_mushroom, Blocks.brown_mushroom, Blocks.yellow_flower, Blocks.red_flower, Blocks.anvil, Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.cactus, Blocks.ladder, Blocks.web, Blocks.tnt);
    private static final BoolValue keepYValue = new BoolValue("Keep Y", false);
    public static Scaffold INSTANCE;
    public static double keepYCoord;
    public final ModeValue modeValue = new ModeValue("Mode", new String[]{"Normal", "Legit"}, "Normal");
    public final BoolValue swing = new BoolValue("Swing", true);
    public final BoolValue sprintValue = new BoolValue("Sprint", false);
    public final BoolValue watchdogValue = new BoolValue("Watchdog", false);
    public final BoolValue adStrafe = new BoolValue("ADStrafe", false);
    public final BoolValue tower = new BoolValue("Tower", false);
    public final BoolValue eagle = new BoolValue("Eagle", false);
    public final BoolValue safeValue = new BoolValue("Safe walk", false);
    public final BoolValue telly = new BoolValue("Telly", true);
    public final BoolValue upValue = new BoolValue("Up", false, () -> (telly.getValue() && !keepYValue.getValue()));
    public final BoolValue esp = new BoolValue("ESP", true);
    private final Animation anim = new DecelerateAnimation(250, 1);
    private final NumberValue tellyTicks = new NumberValue("TellyTicks", 2.90, 0.50, 8.00, 0.01);
    public boolean tip = false;
    public int vl;
    protected Random rand = new Random();
    boolean idk = false;
    int idkTick = 0;
    int towerTick = 0;
    private int direction;
    @Getter
    private int slot;
    private BlockPos data;
    private boolean canTellyPlace;
    private int prevItem = 0;

    private EnumFacing enumFacing;
    public Scaffold() {
        super("Scaffold", Category.World);

        vl = 0;
    }

    public static double getYLevel() {
        if (!keepYValue.getValue()) {
            return mc.thePlayer.posY - 1.0;
        }

        return !isMoving() ? mc.thePlayer.posY - 1.0 : keepYCoord;
    }

    public static Vec3 getVec3(BlockPos pos, EnumFacing face) {
        double x = (double) pos.getX() + 0.5;
        double y = (double) pos.getY() + 0.5;
        double z = (double) pos.getZ() + 0.5;
        if (face == EnumFacing.UP || face == EnumFacing.DOWN) {
            x += MathUtils.getRandomInRange(0.3, -0.3);
            z += MathUtils.getRandomInRange(0.3, -0.3);
        } else {
            y += 0.08;
        }
        if (face == EnumFacing.WEST || face == EnumFacing.EAST) {
            z += MathUtils.getRandomInRange(0.3, -0.3);
        }
        if (face == EnumFacing.SOUTH || face == EnumFacing.NORTH) {
            x += MathUtils.getRandomInRange(0.3, -0.3);
        }
        return new Vec3(x, y, z);
    }

    private boolean doHighVLTips() {
        if (!tip) DebugUtil.log("You are in high vl,sprint disabled.");
        tip = true;
        return false;
    }

    @Override
    public void onEnable() {
        idkTick = 5;

        if (mc.thePlayer == null) return;
        prevItem = mc.thePlayer.inventory.currentItem;

        mc.thePlayer.setSprinting(sprintValue.getValue() || !canTellyPlace);
        mc.gameSettings.keyBindSprint.pressed = sprintValue.getValue() || !canTellyPlace;
        canTellyPlace = false;
        tip = false;
        this.data = null;
        this.slot = -1;
    }

    @Override
    public void onDisable() {
        if (mc.thePlayer == null) return;
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);

        if (adStrafe.getValue()) {
            if (mc.gameSettings.keyBindLeft.isKeyDown()) {
                mc.gameSettings.keyBindLeft.setPressed(false);
            } else if (mc.gameSettings.keyBindRight.isKeyDown()) {
                mc.gameSettings.keyBindRight.setPressed(false);
            }
        }

        mc.thePlayer.inventory.currentItem = prevItem;
        Client.instance.slotSpoofManager.stopSpoofing();
    }

    @EventTarget
    public void onPacket(EventPacket e) {

        if (e.getPacket() instanceof C0BPacketEntityAction && watchdogValue.get()) {
            if ((((C0BPacketEntityAction) e.getPacket()).getAction() == C0BPacketEntityAction.Action.START_SPRINTING) || (((C0BPacketEntityAction) e.getPacket()).getAction() == C0BPacketEntityAction.Action.STOP_SPRINTING)) {
                e.setCancelled(true);
            }
        }
    }

    @EventTarget
    public void onRender3D(EventRender3D event) {

        if (data == null) return;
        for (int i = 0; i < 2; i++) {
            final BlockPos blockPos = data;

            final PlaceInfo placeInfo = PlaceInfo.get(blockPos);

            if (BlockUtil.isValidBock(blockPos) && placeInfo != null && esp.get()) {
                RenderUtil.drawBlockBox(blockPos, new Color(255, 0, 0, 70), false);
                break;
            }
        }
    }

    @EventTarget
    public void onUpdate(final EventMotion event) {

        if (this.idkTick > 0) {
            --this.idkTick;
        }
        if (event.isPre() && eagle.getValue()) {
            if (getBlockUnderPlayer(mc.thePlayer) instanceof BlockAir) {
                if (mc.thePlayer.onGround) {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
                }
            } else if (mc.thePlayer.onGround) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
            }
        }

    }

    @EventTarget
    public void onStrafe(EventStrafe event) {
        if ((upValue.getValue() || keepYValue.getValue()) && mc.thePlayer.onGround && isMoving() && !mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.thePlayer.jump();
        }
    }

    @EventTarget
    private void onTick(EventTick event) {
        if (mc.thePlayer == null) return;
        if (this.slot < 0) return;
        if (!telly.getValue()) {
            canTellyPlace = true;
        }
    }

    @EventTarget
    private void onMove(EventMove event) {
        if (mc.thePlayer.onGround && safeValue.getValue()) mc.thePlayer.safeWalk = true;
        if (watchdogValue.getValue() && sprintValue.getValue()) {
            /*if (MoveUtil.isMoving()) {
                if (!Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())) {
                    if (mc.thePlayer.onGround) {
                        MoveUtil.strafe(event, 0.2805, MoveUtil.getDirection1());
                        mc.thePlayer.setSprinting(true);
                    }
                } else {
                    mc.thePlayer.setSprinting(false);
                }
            }
*/
        }
    }

    @EventTarget
    private void onPlace(EventPlace event) {
        this.slot = getBlockSlot();
        if (this.slot < 0) return;

        if (!telly.getValue()) {

            mc.thePlayer.setSprinting(sprintValue.getValue());
            mc.gameSettings.keyBindSprint.pressed = false;
        }
        event.setCancelled(true);
        if (mc.thePlayer == null) return;

        place();
        mc.sendClickBlockToController(mc.currentScreen == null && mc.gameSettings.keyBindAttack.isKeyDown() && mc.inGameHasFocus);

    }


    @EventTarget
    private void onUpdate(EventUpdate event) {
        if (telly.getValue()) {
            if (mc.gameSettings.keyBindJump.pressed) {
                upValue.set(true);
                keepYValue.set(false);
            } else {
                upValue.set(false);
                keepYValue.set(true);
            }
        }
        if (mc.thePlayer.onGround) {
            keepYCoord = Math.floor(mc.thePlayer.posY - 1.0);
        }
        this.slot = getBlockSlot();

        if (this.slot < 0) {
            return;
        }
        mc.thePlayer.inventory.currentItem = this.slot;
        Client.instance.slotSpoofManager.startSpoofing(prevItem);
        this.findBlock();
        //pickBlock();


        if (telly.getValue()) {
            if (canTellyPlace && !mc.thePlayer.onGround && isMoving()) {
                mc.thePlayer.setSprinting(false);
            }
            canTellyPlace = mc.thePlayer.offGroundTicks >= (upValue.getValue() ? (mc.thePlayer.ticksExisted % 16 == 0 ? 2 : 1) : tellyTicks.getValue());
        }
        if (!canTellyPlace) return;
        if (data != null) {

            float yaw = RotationUtil.getRotationBlock(data)[0];
            float pitch = RotationUtil.getRotationBlock(data)[1];

            if (!watchdogValue.getValue() /*&& !sprintValue.getValue()*/) {
                Client.instance.rotationManager.setRotation(new Vector2f(yaw, pitch), 180, !watchdogValue.getValue());
                mc.thePlayer.setSprinting(sprintValue.getValue());

            }
        }


        if (this.idkTick != 0) {
            this.towerTick = 0;
            return;
        }

        if (this.towerTick > 0) {
            ++this.towerTick;
            if (this.towerTick > 6) {
                idk1(MoveUtil.speed() * ((double) (100 - 95) / 100.0));
            }
            if (this.towerTick > 16) {
                this.towerTick = 0;
            }
        }
        if (isTowering()) {
            towerMove();
        } else {
            towerTick = 0;
        }
    }

    public void idk1(double d) {
        float f = MathHelper.wrapAngleTo180_float((float) Math.toDegrees(Math.atan2(mc.thePlayer.motionZ, mc.thePlayer.motionX)) - 90.0f);
        MoveUtil.setMotion2(d, f);
    }

    private boolean isTowering() {
        return tower.getValue() && Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode());
    }

    private void pickBlock() {
        if (getBlockSlot() <= 0) return;
        mc.thePlayer.inventory.currentItem = getBlockSlot();
    }

    public void renderCounter() {
        anim.setDirection(getState() ? Direction.FORWARDS : Direction.BACKWARDS);
        if (!getState() && anim.isDone()) return;
        float output = (float) anim.getOutput();
        int slot = getBlockSlot();
        ItemStack heldItem = slot == -1 ? null : mc.thePlayer.inventory.mainInventory[slot];
        int count = slot == -1 ? 0 : getBlockCount();
        String countStr = String.valueOf(count);
        float blockWH = heldItem != null ? 15 : -2;
        int spacing = 3;
        float x, y;
        ScaledResolution sr = new ScaledResolution(mc);
        String text = "§l" + countStr + "§r Block" + (count != 1 ? "s" : "");
        float textWidth = FontManager.font18.getStringWidth(text);
        float totalWidth = ((textWidth + blockWH + spacing) + 6 + 2) * output;
        x = sr.getScaledWidth() / 2f - (totalWidth / 2f);
        y = sr.getScaledHeight() - (sr.getScaledHeight() / 2f + 30);
        float height = 20;
        RenderUtil.scissorStart(x - 1.5, y - 1.5, totalWidth + 3, height + 3);

        RenderUtil.drawRectWH(x, y, totalWidth, height, RenderUtil.tripleColor(20, .55f).getRGB());
        RenderUtil.drawRectWH(x, y + height / 2 - 4, 1, 8, HUD.color(1).getRGB());
        ShaderElement.addBlurTask(() -> RenderUtil.drawRectWH(x, y, totalWidth, height, -1));
        ShaderElement.addBloomTask(() -> RenderUtil.drawRectWH(x, y, totalWidth, height, ColorUtil.getColor(0, 0, 0, 200)));
        FontManager.font18.drawString(text, x + 2 + blockWH + spacing + 2, y + FontManager.font18.getMiddleOfBox(height) + 3f, -1);

        if (heldItem != null) {
            RenderHelper.enableGUIStandardItemLighting();
            mc.getRenderItem().renderItemAndEffectIntoGUI(heldItem, (int) x + 3 + 1, (int) (y + 10 - (blockWH / 2)));
            RenderHelper.disableStandardItemLighting();
        }

        RenderUtil.scissorEnd();
    }

    @EventTarget
    private void onMotion(EventMotion event) {
        if (event.isPre()) {

            if (this.slot < 0) {
                return;
            }
/*
            if (sprintValue.getValue()) {
                int wrap = RotationUtil.wrapAngleToDirection(mc.thePlayer.rotationYaw, 4);
                if (wrap == 0)
                    direction = 0;
                if (wrap == 1)
                    direction = 90;
                if (wrap == 2)
                    direction = 180;
                if (wrap == 3)
                    direction = 270;
            }
            if (sprintValue.getValue() && mc.thePlayer.moveForward > 0) {
                event.setYaw(direction);
                event.setPitch(100f);
            }*/
            if (this.getBlockCount() <= 0) {
                int spoofSlot = this.getBestSpoofSlot();
                this.getBlock(spoofSlot);
            }
            if (this.slot < 0) return;
            mc.thePlayer.inventoryContainer.getSlot(slot + 36).getStack();
            if (/*data != null && */watchdogValue.getValue()) {
                event.setYaw(MoveUtil.getDirection(mc.thePlayer.rotationYaw) - 180);
                event.setPitch(mc.gameSettings.keyBindJump.isKeyDown() ? 85 : 80);
                RotationUtil.setVisualRotations(MoveUtil.getDirection(mc.thePlayer.rotationYaw) - 180, mc.gameSettings.keyBindJump.isKeyDown() ? 85 : 80);
                if (mc.thePlayer.onGround && MoveUtil.isMoving()) {
                    if (mc.thePlayer.ticksExisted % 2 == 0) {
                        MoveUtil.setMotion(0.25);
                        mc.thePlayer.jump();
                    } else MoveUtil.setMotion(0.003);

                }
            }

        } else {

        }
    }

    private void towerMove() {
        mc.thePlayer.setSprinting(false);
        mc.gameSettings.keyBindSprint.pressed = false;
        if (!MoveUtil.isMoving()) {
            mc.thePlayer.motionX += 0.008;
        }
//                        if (MoveUtils.isMoving()) {
        towerTick++;
        if (mc.thePlayer.onGround) towerTick = 0;

        mc.thePlayer.motionY = 0.41965;
        mc.thePlayer.motionX = Math.min(mc.thePlayer.motionX, 0.265);
        mc.thePlayer.motionZ = Math.min(mc.thePlayer.motionZ, 0.265);

        if (towerTick == 1) mc.thePlayer.motionY = 0.33;
        else if (towerTick == 2) mc.thePlayer.motionY = 1 - mc.thePlayer.posY % 1;
        else if (towerTick >= 3) towerTick = 0;
    }

    private void place() {
        if (!canTellyPlace) return;
        this.slot = getBlockSlot();
        if (this.slot < 0) return;

        //if (PlayerUtil.block(mc.thePlayer.posX, getYLevel(), mc.thePlayer.posZ) instanceof BlockAir) {
        if (this.slot < 0) {
            return;
        }
        if (data != null) {

            EnumFacing enumFacing = keepYValue.getValue() ? this.enumFacing : this.getPlaceSide(this.data);
            if (enumFacing == null) return;
            if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem(), this.data, enumFacing, getVec3(data, enumFacing))) {
                if (swing.getValue()) {
                    mc.thePlayer.swingItem();
                } else {
                    mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
                }
            }

        }
        // }

    }

    private void findBlock() {

        if (MoveUtil.isMoving() && keepYValue.getValue()) {
            boolean shouldGoDown = false;
            final BlockPos blockPosition = new BlockPos(mc.thePlayer.posX, getYLevel(), mc.thePlayer.posZ);

            if ((BlockUtil.isValidBock(blockPosition) || search(blockPosition, !shouldGoDown))) return;


            for (int x = -1; x <= 1; x++)
                for (int z = -1; z <= 1; z++)
                    if (search(blockPosition.add(x, 0, z), !shouldGoDown)) return;
        } else {
            this.data = getBlockPos();
        }

    }

    private double calcStepSize(double range) {
        double accuracy = 6;
        accuracy += accuracy % 2; // If it is set to uneven it changes it to even. Fixes a bug
        return Math.max(range / accuracy, 0.01);
    }

    private boolean search(final BlockPos blockPosition, final boolean checks) {
//        if (BlockUtil.isValidBock(blockPosition))
//            return false;

        final Vec3 eyesPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);

        PlaceRotation placeRotation = null;

        double xzRV = 0.5;
        double yRV = 0.5;
        double xzSSV = calcStepSize(xzRV);
        double ySSV = calcStepSize(xzRV);

        for (final EnumFacing side : EnumFacing.values()) {
            final BlockPos neighbor = blockPosition.offset(side);

            if (!BlockUtil.isValidBock(neighbor)) continue;

            final Vec3 dirVec = new Vec3(side.getDirectionVec());
            for (double xSearch = 0.5 - xzRV / 2; xSearch <= 0.5 + xzRV / 2; xSearch += xzSSV) {
                for (double ySearch = 0.5 - yRV / 2; ySearch <= 0.5 + yRV / 2; ySearch += ySSV) {
                    for (double zSearch = 0.5 - xzRV / 2; zSearch <= 0.5 + xzRV / 2; zSearch += xzSSV) {
                        final Vec3 posVec = new Vec3(blockPosition).addVector(xSearch, ySearch, zSearch);
                        final double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);
                        final Vec3 hitVec = posVec.add(new Vec3(dirVec.xCoord * 0.5, dirVec.yCoord * 0.5, dirVec.zCoord * 0.5));

                        if (checks && (eyesPos.squareDistanceTo(hitVec) > 18.0 || distanceSqPosVec > eyesPos.squareDistanceTo(posVec.add(dirVec)) || mc.theWorld.rayTraceBlocks(eyesPos, hitVec, false, true, false) != null))
                            continue;

                        // face block
                        final double diffX = hitVec.xCoord - eyesPos.xCoord;
                        final double diffY = hitVec.yCoord - eyesPos.yCoord;
                        final double diffZ = hitVec.zCoord - eyesPos.zCoord;

                        final double diffXZ = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);

                        final Rotation rotation = new Rotation(MathHelper.wrapAngleTo180_float((float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F), MathHelper.wrapAngleTo180_float((float) -Math.toDegrees(Math.atan2(diffY, diffXZ))));

                        final Vec3 rotationVector = new Vec3(RotationUtil.getVectorForRotation(rotation).xCoord, RotationUtil.getVectorForRotation(rotation).yCoord, RotationUtil.getVectorForRotation(rotation).zCoord);
                        final Vec3 vector = eyesPos.addVector(rotationVector.xCoord * 4, rotationVector.yCoord * 4, rotationVector.zCoord * 4);
                        final MovingObjectPosition obj = mc.theWorld.rayTraceBlocks(eyesPos, vector, false, false, true);

                        if (!(obj.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && obj.getBlockPos().equals(neighbor)))
                            continue;

                        if (placeRotation == null || Client.instance.rotationManager.getRotationDifference(rotation) < Client.instance.rotationManager.getRotationDifference(placeRotation.getRotation()))
                            placeRotation = new PlaceRotation(new PlaceInfo(neighbor, side.getOpposite(), hitVec), rotation);
                    }
                }
            }
        }

        if (placeRotation == null) return false;

        data = placeRotation.getPlaceInfo().getBlockPos();
        enumFacing = placeRotation.getPlaceInfo().getEnumFacing();

        return true;
    }


    private EnumFacing getPlaceSide(BlockPos blockPos) {
        ArrayList<Vec3> positions = new ArrayList<>();
        HashMap<Vec3, EnumFacing> hashMap = new HashMap<>();
        BlockPos playerPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
        BlockPos bp;
        Vec3 vec3;
        if (BlockUtil.isAirBlock(blockPos.add(0, 1, 0)) && !blockPos.add(0, 1, 0).equals(playerPos) && !mc.thePlayer.onGround) {
            bp = blockPos.add(0, 1, 0);
            vec3 = this.getBestHitFeet(bp);
            positions.add(vec3);
            hashMap.put(vec3, EnumFacing.UP);
        }

        if (BlockUtil.isAirBlock(blockPos.add(1, 0, 0)) && !blockPos.add(1, 0, 0).equals(playerPos)) {
            bp = blockPos.add(1, 0, 0);
            vec3 = this.getBestHitFeet(bp);
            positions.add(vec3);
            hashMap.put(vec3, EnumFacing.EAST);
        }

        if (BlockUtil.isAirBlock(blockPos.add(-1, 0, 0)) && !blockPos.add(-1, 0, 0).equals(playerPos)) {
            bp = blockPos.add(-1, 0, 0);
            vec3 = this.getBestHitFeet(bp);
            positions.add(vec3);
            hashMap.put(vec3, EnumFacing.WEST);
        }

        if (BlockUtil.isAirBlock(blockPos.add(0, 0, 1)) && !blockPos.add(0, 0, 1).equals(playerPos)) {
            bp = blockPos.add(0, 0, 1);
            vec3 = this.getBestHitFeet(bp);
            positions.add(vec3);
            hashMap.put(vec3, EnumFacing.SOUTH);
        }

        if (BlockUtil.isAirBlock(blockPos.add(0, 0, -1)) && !blockPos.add(0, 0, -1).equals(playerPos)) {
            bp = blockPos.add(0, 0, -1);
            vec3 = this.getBestHitFeet(bp);
            positions.add(vec3);
            hashMap.put(vec3, EnumFacing.NORTH);
        }

        positions.sort(Comparator.comparingDouble((vec3x) -> mc.thePlayer.getDistance(vec3x.xCoord, vec3x.yCoord, vec3x.zCoord)));
        if (!positions.isEmpty()) {
            vec3 = this.getBestHitFeet(this.data);
            if (mc.thePlayer.getDistance(vec3.xCoord, vec3.yCoord, vec3.zCoord) >= mc.thePlayer.getDistance(positions.get(0).xCoord, positions.get(0).yCoord, positions.get(0).zCoord)) {
                return hashMap.get(positions.get(0));
            }
        }

        return null;
    }

    private Vec3 getBestHitFeet(BlockPos blockPos) {
        Block block = mc.theWorld.getBlockState(blockPos).getBlock();
        double ex = MathHelper.clamp_double(mc.thePlayer.posX, blockPos.getX(), (double) blockPos.getX() + block.getBlockBoundsMaxX());
        double ey = MathHelper.clamp_double(keepYValue.getValue() ? getYLevel() : mc.thePlayer.posY, blockPos.getY(), (double) blockPos.getY() + block.getBlockBoundsMaxY());
        double ez = MathHelper.clamp_double(mc.thePlayer.posZ, blockPos.getZ(), (double) blockPos.getZ() + block.getBlockBoundsMaxZ());
        return new Vec3(ex, ey, ez);
    }

    private BlockPos getBlockPos() {
        BlockPos playerPos = new BlockPos(mc.thePlayer.posX, getYLevel(), mc.thePlayer.posZ);
        ArrayList<Vec3> positions = new ArrayList<>();
        HashMap<Vec3, BlockPos> hashMap = new HashMap<>();

        for (int x = playerPos.getX() - 5; x <= playerPos.getX() + 5; ++x) {
            for (int y = playerPos.getY() - 1; y <= playerPos.getY(); ++y) {
                for (int z = playerPos.getZ() - 5; z <= playerPos.getZ() + 5; ++z) {
                    if (BlockUtil.isValidBock(new BlockPos(x, y, z))) {
                        BlockPos blockPos = new BlockPos(x, y, z);
                        Block block = mc.theWorld.getBlockState(blockPos).getBlock();
                        double ex = MathHelper.clamp_double(mc.thePlayer.posX, blockPos.getX(), (double) blockPos.getX() + block.getBlockBoundsMaxX());
                        double ey = MathHelper.clamp_double(keepYValue.getValue() ? getYLevel() : mc.thePlayer.posY, blockPos.getY(), (double) blockPos.getY() + block.getBlockBoundsMaxY());
                        double ez = MathHelper.clamp_double(mc.thePlayer.posZ, blockPos.getZ(), (double) blockPos.getZ() + block.getBlockBoundsMaxZ());
                        Vec3 vec3 = new Vec3(ex, ey, ez);
                        positions.add(vec3);
                        hashMap.put(vec3, blockPos);
                    }
                }
            }
        }

        if (!positions.isEmpty()) {
            positions.sort(Comparator.comparingDouble(this::getBestBlock));
            return hashMap.get(positions.get(0));
        } else {
            return null;
        }
    }

    private double getBestBlock(Vec3 vec3) {
        return mc.thePlayer.getDistanceSq(vec3.xCoord, vec3.yCoord, vec3.zCoord);
    }

    public int getBlockSlot() {
        for (int i = 0; i < 9; ++i) {
            if (!mc.thePlayer.inventoryContainer.getSlot(i + 36).getHasStack() || !(mc.thePlayer.inventoryContainer.getSlot(i + 36).getStack().getItem() instanceof ItemBlock))
                continue;
            return i;
        }
        return -1;
    }

    public int getBlockCount() {
        int n = 0;
        int i = 36;
        while (i < 45) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                final Item item = stack.getItem();
                if (stack.getItem() instanceof ItemBlock && this.isValid(item)) {
                    n += stack.stackSize;
                }
            }
            ++i;
        }
        return n;
    }

    private boolean isValid(final Item item) {
        return item instanceof ItemBlock && !invalidBlocks.contains(((ItemBlock) (item)).getBlock());
    }

    private float getYaw() {
        if (mc.gameSettings.keyBindBack.isKeyDown()) return mc.thePlayer.rotationYaw;
        if (mc.gameSettings.keyBindLeft.isKeyDown()) return mc.thePlayer.rotationYaw + 90;
        if (mc.gameSettings.keyBindRight.isKeyDown()) return mc.thePlayer.rotationYaw - 90;
        return mc.thePlayer.rotationYaw - 180;
    }


    private void getBlock(int switchSlot) {
        for (int i = 9; i < 45; ++i) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && (mc.currentScreen == null || mc.currentScreen instanceof GuiInventory)) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemBlock) {
                    ItemBlock block = (ItemBlock) is.getItem();
                    if (isValid(block)) {
                        if (36 + switchSlot != i) {
                            InventoryUtil.swap(i, switchSlot);
                        }
                        break;
                    }
                }
            }
        }

    }

    int getBestSpoofSlot() {
        int spoofSlot = 5;

        for (int i = 36; i < 45; ++i) {
            if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                spoofSlot = i - 36;
                break;
            }
        }

        return spoofSlot;
    }
}
