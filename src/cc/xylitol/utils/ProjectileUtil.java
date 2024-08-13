package cc.xylitol.utils;

import cc.xylitol.utils.math.MathUtils;
import cc.xylitol.utils.render.RenderUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.*;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Vector2f;
import java.awt.*;
import java.util.List;
import java.util.Objects;

import static cc.xylitol.Client.mc;


public class ProjectileUtil {
    public static ProjectileHit predict(double posX, double posY, double posZ, double motionX, double motionY, double motionZ, double motionSlowdown, double size, double gravity, boolean draw) {
        MovingObjectPosition landingPosition = null;
        boolean hasLanded = false;
        boolean hitEntity = false;

        if (draw) {
            RenderUtil.enableRender3D(true);
            RenderUtil.color((new Color(230, 230, 230)).getRGB());
            GL11.glLineWidth(2.0F);
            GL11.glBegin(3);
        }

        while (!hasLanded && posY > -60.0D) {
            Vec3 posBefore = new Vec3(posX, posY, posZ);
            Vec3 posAfter = new Vec3(posX + motionX, posY + motionY, posZ + motionZ);
            landingPosition = mc.theWorld.rayTraceBlocks(posBefore, posAfter, false, true, false);
            posBefore = new Vec3(posX, posY, posZ);
            posAfter = new Vec3(posX + motionX, posY + motionY, posZ + motionZ);
            if (landingPosition != null) {
                hasLanded = true;
                posAfter = new Vec3(landingPosition.hitVec.xCoord, landingPosition.hitVec.yCoord, landingPosition.hitVec.zCoord);
            }


            AxisAlignedBB arrowBox = new AxisAlignedBB(posX - size, posY - size, posZ - size, posX + size, posY + size, posZ + size);


            List<Entity> entityList = mc.theWorld.getEntitiesWithinAABB(Entity.class, arrowBox.addCoord(motionX, motionY, motionZ).expand(1.0D, 1.0D, 1.0D));

            for (int i = 0; i < entityList.size(); i++) {
                Entity var18 = entityList.get(i);
                if (var18.canBeCollidedWith() && var18 != mc.thePlayer) {
                    AxisAlignedBB var2 = var18.getEntityBoundingBox().expand(size, size, size);

                    MovingObjectPosition possibleEntityLanding = var2.calculateIntercept(posBefore, posAfter);
                    if (possibleEntityLanding != null) {
                        hitEntity = true;
                        hasLanded = true;
                        landingPosition = possibleEntityLanding;
                    }
                }
            }

            posX += motionX;
            posY += motionY;
            posZ += motionZ;
            BlockPos var35 = new BlockPos(posX, posY, posZ);
            Block var36 = mc.theWorld.getBlockState(var35).getBlock();
            if (var36.getBlockState().getBlock().getMaterial() == Material.water) {
                motionX *= 0.6D;
                motionY *= 0.6D;
                motionZ *= 0.6D;
            } else {
                motionX *= motionSlowdown;
                motionY *= motionSlowdown;
                motionZ *= motionSlowdown;
            }

            motionY -= gravity;
            if (draw) {
                GL11.glVertex3d(posX - mc.getRenderManager().getRenderPosX(), posY - mc
                        .getRenderManager().getRenderPosY(), posZ - mc
                        .getRenderManager().getRenderPosZ());
            }
        }

        return new ProjectileHit(posX, posY, posZ, hitEntity, hasLanded, landingPosition);
    }


    public static class EnderPearlPredictor {
        public double predictX, predictY, predictZ, minMotionY, maxMotionY;

        public EnderPearlPredictor(double predictX, double predictY, double predictZ, double minMotionY, double maxMotionY) {
            this.predictX = predictX;
            this.predictY = predictY;
            this.predictZ = predictZ;
            this.minMotionY = minMotionY;
            this.maxMotionY = maxMotionY;
        }

        public double assessRotation(Vector2f rotation) {
            double mul = 1;
            int cnt = 0;
            for (double rate = 0; rate <= 1; rate += 0.3333) {
                for (int yaw = -1; yaw <= 1; yaw += 1) {
                    for (int pitch = -1; pitch <= 1; pitch += 1) {
                        mul *= assessSingleRotation(new Vector2f(rotation.getX() + yaw * 0.5F, rotation.getY() + pitch * 0.5F), MathUtils.interpolate(minMotionY, maxMotionY, rate));
                        cnt++;
                    }
                }
                if (minMotionY == maxMotionY) {
                    break;
                }
            }
            return Math.pow(mul, 1D / cnt);
        }

        private double assessSingleRotation(Vector2f rotation, double motionYOffset) {

            if (rotation.y > 90F) rotation.y = 90F;
            if (rotation.y < -90F) rotation.y = -90F;
            final float motionFactor = 1.5F;
            final float gravity = 0.03F;
            final float size = 0.25F;
            final float motionSlowdown = 0.99F;

            double posX = predictX - (MathHelper.cos(rotation.x / 180.0F * 3.1415927F) * 0.16F);
            double posY = predictY + mc.thePlayer.getEyeHeight() - 0.10000000149011612D;
            double posZ = predictZ - (MathHelper.sin(rotation.y / 180.0F * 3.1415927F) * 0.16F);

            double motionX = (-MathHelper.sin(rotation.x / 180.0F * 3.1415927F) * MathHelper.cos(rotation.y / 180.0F * 3.1415927F)) * 0.4D;

            double motionY = -MathHelper.sin((rotation.y) / 180.0F * 3.1415927F) * 0.4D;


            double motionZ = (MathHelper.cos(rotation.x / 180.0F * 3.1415927F) * MathHelper.cos(rotation.y / 180.0F * 3.1415927F)) * 0.4D;
            float distance = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
            motionX /= distance;
            motionY /= distance;
            motionZ /= distance;
            motionX *= motionFactor;
            motionY *= motionFactor;
            motionZ *= motionFactor;

            motionY += motionYOffset;

            ProjectileHit projectileHit = ProjectileUtil.predict(posX, posY, posZ, motionX, motionY, motionZ, motionSlowdown, size, gravity, false);

            if (!projectileHit.hasLanded) return 0.05D;

            EnumFacing facing = projectileHit.landingPosition.sideHit;

            BlockPos landPos = projectileHit.landingPosition.getBlockPos().add(facing.getDirectionVec());

            return ((facing == EnumFacing.UP || facing == EnumFacing.DOWN) ? assessPlainBlockPos(landPos) : assessSideBlockPos(landPos, facing)) * distanceFunction(new Vec3(predictX, predictY, predictZ).distanceTo(new Vec3(projectileHit.posX, projectileHit.posY, projectileHit.posZ)));
        }

        private double assessPlainBlockPos(BlockPos pos) {
            double mul = 1;
            mul *= Math.pow(assessSingleBlockPos(pos.add(0, 0, 0)), 2);
            mul *= assessSingleBlockPos(pos.add(1, 0, 0));
            mul *= assessSingleBlockPos(pos.add(-1, 0, 0));
            mul *= assessSingleBlockPos(pos.add(0, 0, 1));
            mul *= assessSingleBlockPos(pos.add(0, 0, -1));
            return Math.pow(mul, 1 / 6D);
        }

        private double assessSideBlockPos(BlockPos pos, EnumFacing facing) {
            double mul = 1;
            mul *= Math.pow(assessSingleBlockPos(pos.add(0, 0, 0)), 2);
            mul *= assessSingleBlockPos(pos.add(1, 0, 0));
            mul *= assessSingleBlockPos(pos.add(facing.getDirectionVec()));
            return Math.pow(mul, 1 / 3D);
        }

        private double assessSingleBlockPos(BlockPos pos) {
            for (int y = 0; y >= -5; y--) {
                IBlockState blockState = mc.theWorld.getBlockState(pos.add(0, y, 0));
                if (y == 0 && blockState.getBlock().isFullBlock()) return 0.4D;
                if (blockState.getBlock().isFullBlock()) return 1D;
            }
            return 0.05D;
        }

        private double distanceFunction(double d) {
            d /= 1000;
            return (d + 3) / (d + 2) / (3 / 2D);
        }
    }

    public static class ProjectileHit {
        private final double posX;
        private final double posY;
        private final double posZ;
        private final boolean hitEntity;
        private final boolean hasLanded;
        private final MovingObjectPosition landingPosition;

        public ProjectileHit(double posX, double posY, double posZ, boolean hitEntity,
                             boolean hasLanded, MovingObjectPosition landingPosition) {
            this.posX = posX;
            this.posY = posY;
            this.posZ = posZ;
            this.hitEntity = hitEntity;
            this.hasLanded = hasLanded;
            this.landingPosition = landingPosition;
        }

        public double getPosX() {
            return posX;
        }

        public double getPosY() {
            return posY;
        }

        public double getPosZ() {
            return posZ;
        }

        public boolean isHitEntity() {
            return hitEntity;
        }

        public boolean isHasLanded() {
            return hasLanded;
        }

        public MovingObjectPosition getLandingPosition() {
            return landingPosition;
        }

        // Implement hashCode(), equals(), and toString() if needed

        // Sample implementation of hashCode(), equals(), and toString() methods
        // Remember to override hashCode(), equals(), and toString() methods based on your requirements
        // This is just a basic example
        @Override
        public int hashCode() {
            return Objects.hash(posX, posY, posZ, hitEntity, hasLanded, landingPosition);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            ProjectileHit that = (ProjectileHit) obj;
            return Double.compare(that.posX, posX) == 0 &&
                    Double.compare(that.posY, posY) == 0 &&
                    Double.compare(that.posZ, posZ) == 0 &&
                    hitEntity == that.hitEntity &&
                    hasLanded == that.hasLanded &&
                    Objects.equals(landingPosition, that.landingPosition);
        }

        @Override
        public String toString() {
            return "ProjectileHit{" +
                    "posX=" + posX +
                    ", posY=" + posY +
                    ", posZ=" + posZ +
                    ", hitEntity=" + hitEntity +
                    ", hasLanded=" + hasLanded +
                    ", landingPosition=" + landingPosition +
                    '}';
        }
    }

}
