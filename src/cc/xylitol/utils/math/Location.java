package cc.xylitol.utils.math;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;

public class Location {
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public Location(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Location(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        yaw = 0.0F;
        pitch = 0.0F;
    }

    public Location(BlockPos pos) {
        x = pos.getX();
        y = pos.getY();
        z = pos.getZ();
        yaw = 0.0F;
        pitch = 0.0F;
    }

    public Location(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        yaw = 0.0F;
        pitch = 0.0F;
    }

    public Location(EntityLivingBase entity) {
        x = entity.posX;
        y = entity.posY;
        z = entity.posZ;
        yaw = 0.0f;
        pitch = 0.0f;
    }

    public Location add(int x, int y, int z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public Location add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public Location subtract(int x, int y, int z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    public Location subtract(double x, double y, double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    public Block getBlock() {
        return Minecraft.getMinecraft().theWorld.getBlockState(toBlockPos()).getBlock();
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public float getYaw() {
        return yaw;
    }

    public Location setYaw(float yaw) {
        this.yaw = yaw;
        return this;
    }

    public float getPitch() {
        return pitch;
    }

    public Location setPitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

    public BlockPos toBlockPos() {
        return new BlockPos(x, y, z);
    }

    public double distanceTo(Location loc) {
        double dx = loc.x - x;
        double dz = loc.z - z;
        double dy = loc.y - y;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}