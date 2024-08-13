package cc.xylitol.utils.player;

import cc.xylitol.event.impl.events.EventMove;
import cc.xylitol.event.impl.events.EventMoveInput;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;

import static cc.xylitol.Client.mc;

public class MoveUtil {

    public static boolean isMoving() {
        return mc.thePlayer != null && (mc.thePlayer.movementInput.moveForward != 0F || mc.thePlayer.movementInput.moveStrafe != 0F);
    }
    public static double speed() {
        return Math.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ);
    }

    public static boolean isOnGround(double height) {
        return !mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0D, -height, 0.0D)).isEmpty();
    }



    public static void setMotion2(double d, float f) {
        mc.thePlayer.motionX = -Math.sin(Math.toRadians(f)) * d;
        mc.thePlayer.motionZ = Math.cos(Math.toRadians(f)) * d;
    }
    public static void setMotion(double speed) {
        setMotion(speed, mc.thePlayer.rotationYaw);
    }

    public static void setMotion(double speed, float yaw) {
        double forward = mc.thePlayer.movementInput.moveForward;
        double strafe = mc.thePlayer.movementInput.moveStrafe;
        if ((forward == 0.0D) && (strafe == 0.0D)) {
            mc.thePlayer.motionX = 0;
            mc.thePlayer.motionZ = 0;
        } else {
            if (forward != 0.0D) {
                if (strafe > 0.0D) {
                    yaw += (forward > 0.0D ? -45 : 45);
                } else if (strafe < 0.0D) {
                    yaw += (forward > 0.0D ? 45 : -45);
                }
                strafe = 0.0D;
                if (forward > 0.0D) {
                    forward = 1;
                } else if (forward < 0.0D) {
                    forward = -1;
                }
            }
            mc.thePlayer.motionX = forward * speed * Math.cos(Math.toRadians(yaw + 90.0F)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0F));
            mc.thePlayer.motionZ = forward * speed * Math.sin(Math.toRadians(yaw + 90.0F)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0F));
        }
    }

    public static float getDirection(final float yaw) {
        return getDirection(yaw, mc.thePlayer.movementInput.moveForward, mc.thePlayer.movementInput.moveStrafe);
    }

    public static float getDirection(float yaw, final float forward, final float strafe) {
        if (forward != 0) {
            if (strafe < 0) {
                yaw += forward < 0 ? 135 : 45;
            } else if (strafe > 0) {
                yaw -= forward < 0 ? 135 : 45;
            } else if (strafe == 0 && forward < 0) {
                yaw -= 180;
            }
        } else {
            if (strafe < 0) {
                yaw += 90;
            } else if (strafe > 0) {
                yaw -= 90;
            }
        }

        return yaw;
    }


    public static void strafe(final EventMove moveEvent, final double speed, final double direction) {
        if (!isMoving()) return;
        moveEvent.setX(mc.thePlayer.motionX = -Math.sin(direction) * speed);
        moveEvent.setZ(mc.thePlayer.motionZ = Math.cos(direction) * speed);
    }

    public static double strafe(final double d) {
        if (!isMoving())
            return mc.thePlayer.rotationYaw;

        final double yaw = getDirection1();
        mc.thePlayer.motionX = -MathHelper.sin((float) yaw) * d;
        mc.thePlayer.motionZ = MathHelper.cos((float) yaw) * d;

        return yaw;
    }
    public static void strafe(double speed, float yaw) {
        if (!isMoving()) {
            return;
        }
        yaw = (float)Math.toRadians(yaw);
        mc.thePlayer.motionX = (double)(-MathHelper.sin(yaw)) * speed;
        mc.thePlayer.motionZ = (double)MathHelper.cos(yaw) * speed;
    }

    public static void jump() {
        double jumpY = (double) mc.thePlayer.getJumpUpwardsMotion();

        if (mc.thePlayer.isPotionActive(Potion.jump)) {
            jumpY += (double) ((float) (mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F);
        }

        mc.thePlayer.motionY = (mc.thePlayer.motionY = jumpY);
    }

    /**
     * Fixes the players movement
     */
    public static void fixMovement(final EventMoveInput event, final float yaw) {
        final float forward = event.getForward();
        final float strafe = event.getStrafe();

        final double angle = MathHelper.wrapAngleTo180_double(Math.toDegrees(direction(mc.thePlayer.rotationYaw, forward, strafe)));

        if (forward == 0 && strafe == 0) {
            return;
        }

        float closestForward = 0, closestStrafe = 0, closestDifference = Float.MAX_VALUE;

        for (float predictedForward = -1F; predictedForward <= 1F; predictedForward += 1F) {
            for (float predictedStrafe = -1F; predictedStrafe <= 1F; predictedStrafe += 1F) {
                if (predictedStrafe == 0 && predictedForward == 0) continue;

                final double predictedAngle = MathHelper.wrapAngleTo180_double(Math.toDegrees(direction(yaw, predictedForward, predictedStrafe)));
                final double difference = Math.abs(angle - predictedAngle);

                if (difference < closestDifference) {
                    closestDifference = (float) difference;
                    closestForward = predictedForward;
                    closestStrafe = predictedStrafe;
                }
            }
        }

        event.setForward(closestForward);
        event.setStrafe(closestStrafe);
    }

    public static double direction(float rotationYaw, final double moveForward, final double moveStrafing) {
        if (moveForward < 0F) rotationYaw += 180F;

        float forward = 1F;

        if (moveForward < 0F) forward = -0.5F;
        else if (moveForward > 0F) forward = 0.5F;

        if (moveStrafing > 0F) rotationYaw -= 90F * forward;
        if (moveStrafing < 0F) rotationYaw += 90F * forward;

        return Math.toRadians(rotationYaw);
    }


    public static double getDirection1() {
        float yaw = mc.thePlayer.rotationYaw;

        if (mc.thePlayer.moveForward < 0.0F) {
            yaw += 180F;
        }

        float forward = 1.0F;
        if (mc.thePlayer.moveForward < 0.0F) {
            forward = -0.5F;
        } else if (mc.thePlayer.moveForward > 0.0F) {
            forward = 0.5F;
        }

        if (mc.thePlayer.moveStrafing > 0.0F) {
            yaw -= 90F * forward;
        } else if (mc.thePlayer.moveStrafing < 0.0F) {
            yaw += 90F * forward;
        }

        return Math.toRadians(yaw);
    }

    public static double direction() {
        float rotationYaw = mc.thePlayer.movementYaw;

        if (mc.thePlayer.moveForward < 0) {
            rotationYaw += 180;
        }

        float forward = 1;

        if (mc.thePlayer.moveForward < 0) {
            forward = -0.5F;
        } else if (mc.thePlayer.moveForward > 0) {
            forward = 0.5F;
        }

        if (mc.thePlayer.moveStrafing > 0) {
            rotationYaw -= 70 * forward;
        }

        if (mc.thePlayer.moveStrafing < 0) {
            rotationYaw += 70 * forward;
        }

        return Math.toRadians(rotationYaw);
    }

    public static void stop() {
        mc.thePlayer.motionX = 0;
        mc.thePlayer.motionZ = 0;
    }

}
