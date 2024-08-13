package cc.xylitol.utils.player;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.RandomUtils;
import org.lwjglx.util.vector.Vector2f;

import java.util.concurrent.ThreadLocalRandom;

import static cc.xylitol.utils.player.RotationUtil.mc;

@Setter
@Getter
public class Rotation {
    float yaw, pitch;
    public double distanceSq;


    public Rotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Vector2f toVec2f() {
        return new Vector2f(this.yaw, this.pitch);
    }

    public void toPlayer(EntityPlayer player) {
        if (Float.isNaN(yaw) || Float.isNaN(pitch))
            return;

        fixedSensitivity(mc.gameSettings.mouseSensitivity);

        player.rotationYaw = yaw;
        player.rotationPitch = pitch;
    }

    public void fixedSensitivity(Float sensitivity) {
        float f = sensitivity * 0.6F + 0.2F;
        float gcd = f * f * f * 1.2F;

        yaw -= yaw % gcd;
        pitch -= pitch % gcd;
    }

    public static float updateRotation(final float current, final float calc, final float maxDelta) {
        float f = MathHelper.wrapAngleTo180_float(calc - current);
        if (f > maxDelta) {
            f = maxDelta;
        }
        if (f < -maxDelta) {
            f = -maxDelta;
        }
        return current + f;
    }

    public float rotateToYaw(final float yawSpeed, final float currentYaw, final float calcYaw) {
        float yaw = updateRotation(currentYaw, calcYaw, yawSpeed + RandomUtils.nextFloat(0.0f, 15.0f));
        final double diffYaw = MathHelper.wrapAngleTo180_float(calcYaw - currentYaw);
        if (-yawSpeed > diffYaw || diffYaw > yawSpeed) {
            yaw += (float) (RandomUtils.nextFloat(1.0f, 2.0f) * Math.sin(mc.thePlayer.rotationPitch * 3.141592653589793));
        }
        if (yaw == currentYaw) {
            return currentYaw;
        }
        if (mc.gameSettings.mouseSensitivity == 0.5) {
            mc.gameSettings.mouseSensitivity = 0.47887325f;
        }
        final float f1 = mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
        final float f2 = f1 * f1 * f1 * 8.0f;
        final int deltaX = (int) ((6.667 * yaw - 6.666666666666667 * currentYaw) / f2);
        final float f3 = deltaX * f2;
        yaw = (float) (currentYaw + f3 * 0.15);
        return yaw;
    }

    public float rotateToYaw(final float yawSpeed, final float[] currentRots, final float calcYaw) {
        float yaw = updateRotation(currentRots[0], calcYaw, yawSpeed + RandomUtils.nextFloat(0.0f, 15.0f));
        if (yaw != calcYaw) {
            yaw += (float) (RandomUtils.nextFloat(1.0f, 2.0f) * Math.sin(currentRots[1] * 3.141592653589793));
        }
        if (yaw == currentRots[0]) {
            return currentRots[0];
        }
        yaw += (float) (ThreadLocalRandom.current().nextGaussian() * 0.2);
        if (mc.gameSettings.mouseSensitivity == 0.5) {
            mc.gameSettings.mouseSensitivity = 0.47887325f;
        }
        final float f1 = mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
        final float f2 = f1 * f1 * f1 * 8.0f;
        final int deltaX = (int) ((6.667 * yaw - 6.6666667 * currentRots[0]) / f2);
        final float f3 = deltaX * f2;
        yaw = (float) (currentRots[0] + f3 * 0.15);
        return yaw;
    }

    public float rotateToPitch(final float pitchSpeed, final float currentPitch, final float calcPitch) {
        float pitch = updateRotation(currentPitch, calcPitch, pitchSpeed + RandomUtils.nextFloat(0.0f, 15.0f));
        if (pitch != calcPitch) {
            pitch += (float) (RandomUtils.nextFloat(1.0f, 2.0f) * Math.sin(mc.thePlayer.rotationYaw * 3.141592653589793));
        }
        if (mc.gameSettings.mouseSensitivity == 0.5) {
            mc.gameSettings.mouseSensitivity = 0.47887325f;
        }
        final float f1 = mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
        final float f2 = f1 * f1 * f1 * 8.0f;
        final int deltaY = (int) ((6.667 * pitch - 6.666667 * currentPitch) / f2) * -1;
        final float f3 = deltaY * f2;
        final float f4 = (float) (currentPitch - f3 * 0.15);
        pitch = MathHelper.clamp_float(f4, -90.0f, 90.0f);
        return pitch;
    }

    public float rotateToPitch(final float pitchSpeed, final float[] currentRots, final float calcPitch) {
        float pitch = updateRotation(currentRots[1], calcPitch, pitchSpeed + RandomUtils.nextFloat(0.0f, 15.0f));
        if (pitch != calcPitch) {
            pitch += (float) (RandomUtils.nextFloat(1.0f, 2.0f) * Math.sin(currentRots[0] * 3.141592653589793));
        }
        if (mc.gameSettings.mouseSensitivity == 0.5) {
            mc.gameSettings.mouseSensitivity = 0.47887325f;
        }
        final float f1 = mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
        final float f2 = f1 * f1 * f1 * 8.0f;
        final int deltaY = (int) ((6.667 * pitch - 6.666667 * currentRots[1]) / f2) * -1;
        final float f3 = deltaY * f2;
        final float f4 = (float) (currentRots[1] - f3 * 0.15);
        pitch = MathHelper.clamp_float(f4, -90.0f, 90.0f);
        return pitch;
    }
}
